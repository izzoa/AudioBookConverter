package uk.yermak.audiobookconverter.fx;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.yermak.audiobookconverter.book.AudioBookInfo;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates and sanitizes filenames and filename templates.
 */
public class FilenameValidator {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Invalid characters for Windows filenames
    private static final String WINDOWS_INVALID_CHARS = "<>:\"/\\|?*";
    
    // Invalid characters for Unix/Mac filenames
    private static final String UNIX_INVALID_CHARS = "/\0";
    
    // Combined invalid characters for cross-platform safety
    private static final String ALL_INVALID_CHARS = "<>:\"/\\|?*\0";
    
    // Reserved Windows filenames
    private static final Set<String> WINDOWS_RESERVED_NAMES = Set.of(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    );

    // Pattern to match ST4 template placeholders
    private static final Pattern ST4_PLACEHOLDER = Pattern.compile("<([A-Z_]+)(?:;[^>]*)?>|<if\\(([A-Z_]+)\\)>|<endif>");
    
    // Valid ST4 placeholders for audiobook templates
    private static final Set<String> VALID_PLACEHOLDERS = Set.of(
            "WRITER", "TITLE", "NARRATOR", "SERIES", "BOOK_NUMBER", "BOOK_TITLE",
            "GENRE", "YEAR", "COMMENT", "PART", "CHAPTER_NUMBER", "CHAPTER_TEXT",
            "DURATION", "TAG", "CUSTOM_TITLE"
    );

    /**
     * Result of filename validation.
     */
    public record ValidationResult(
            boolean isValid,
            List<String> errors,
            List<String> warnings
    ) {
        public boolean hasWarnings() {
            return warnings != null && !warnings.isEmpty();
        }
        
        public boolean hasErrors() {
            return errors != null && !errors.isEmpty();
        }
        
        public String getErrorsAsString() {
            return errors != null ? String.join("\n", errors) : "";
        }
        
        public String getWarningsAsString() {
            return warnings != null ? String.join("\n", warnings) : "";
        }
    }

    /**
     * Validates a filename format template.
     */
    public static ValidationResult validateTemplate(String template) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (template == null || template.trim().isEmpty()) {
            errors.add("Template cannot be empty");
            return new ValidationResult(false, errors, warnings);
        }

        // Check for balanced if/endif tags
        int ifCount = countOccurrences(template, "<if(");
        int endifCount = countOccurrences(template, "<endif>");
        if (ifCount != endifCount) {
            errors.add("Unbalanced <if>/<endif> tags: " + ifCount + " if(s) but " + endifCount + " endif(s)");
        }

        // Check for valid placeholders
        Matcher matcher = ST4_PLACEHOLDER.matcher(template);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String ifPlaceholder = matcher.group(2);
            String found = placeholder != null ? placeholder : ifPlaceholder;
            
            if (found != null && !VALID_PLACEHOLDERS.contains(found)) {
                warnings.add("Unknown placeholder: <" + found + ">");
            }
        }

        // Check for invalid characters that would make the filename invalid
        String withoutPlaceholders = template.replaceAll("<[^>]+>", "");
        for (char c : ALL_INVALID_CHARS.toCharArray()) {
            if (withoutPlaceholders.indexOf(c) >= 0) {
                errors.add("Invalid character '" + (c == '\0' ? "\\0" : c) + "' in template");
            }
        }

        // Check for patterns that might cause issues
        if (template.startsWith(" ") || template.endsWith(" ")) {
            warnings.add("Template has leading or trailing spaces");
        }
        if (template.startsWith(".")) {
            warnings.add("Filename starting with '.' will be hidden on Unix/Mac");
        }
        if (template.endsWith(".")) {
            warnings.add("Filename ending with '.' may cause issues on Windows");
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Sanitizes a string for use as a filename on all platforms.
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "untitled";
        }

        String sanitized = filename;
        
        // Replace invalid characters with underscore
        for (char c : ALL_INVALID_CHARS.toCharArray()) {
            sanitized = sanitized.replace(c, '_');
        }
        
        // Replace control characters
        sanitized = sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "_");
        
        // Trim spaces and dots from beginning and end
        sanitized = sanitized.trim();
        while (sanitized.startsWith(".")) {
            sanitized = sanitized.substring(1);
        }
        while (sanitized.endsWith(".")) {
            sanitized = sanitized.substring(0, sanitized.length() - 1);
        }
        
        // Check for Windows reserved names
        String baseName = sanitized.contains(".") 
                ? sanitized.substring(0, sanitized.indexOf('.')) 
                : sanitized;
        if (WINDOWS_RESERVED_NAMES.contains(baseName.toUpperCase())) {
            sanitized = "_" + sanitized;
        }
        
        // Limit length (255 is common max, but leave room for extension)
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200);
        }
        
        // Ensure not empty
        if (sanitized.isEmpty()) {
            sanitized = "untitled";
        }

        return sanitized;
    }

    /**
     * Checks if a filename is valid for all platforms.
     */
    public static boolean isValidFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        
        // Check for invalid characters
        for (char c : ALL_INVALID_CHARS.toCharArray()) {
            if (filename.indexOf(c) >= 0) {
                return false;
            }
        }
        
        // Check for reserved names
        String baseName = filename.contains(".") 
                ? filename.substring(0, filename.indexOf('.')) 
                : filename;
        if (WINDOWS_RESERVED_NAMES.contains(baseName.toUpperCase())) {
            return false;
        }
        
        // Check length
        if (filename.length() > 255) {
            return false;
        }
        
        return true;
    }

    /**
     * Previews what a filename template would generate with given book info.
     * This is a simplified preview - actual generation uses ST4 template engine.
     */
    public static String previewFilename(String template, AudioBookInfo info) {
        if (template == null || info == null) {
            return "(preview unavailable)";
        }
        
        String preview = template;
        
        // Simple replacement for preview purposes
        preview = replaceIfBlock(preview, "WRITER", info.writer().get());
        preview = replaceIfBlock(preview, "TITLE", info.title().get());
        preview = replaceIfBlock(preview, "NARRATOR", info.narrator().get());
        preview = replaceIfBlock(preview, "SERIES", info.series().get());
        preview = replaceIfBlock(preview, "BOOK_NUMBER", info.bookNumber().get());
        preview = replaceIfBlock(preview, "YEAR", info.year().get());
        preview = replaceIfBlock(preview, "GENRE", info.genre().get());
        
        // Replace simple placeholders
        preview = preview.replace("<WRITER>", getValueOrEmpty(info.writer().get()));
        preview = preview.replace("<TITLE>", getValueOrEmpty(info.title().get()));
        preview = preview.replace("<NARRATOR>", getValueOrEmpty(info.narrator().get()));
        preview = preview.replace("<SERIES>", getValueOrEmpty(info.series().get()));
        preview = preview.replace("<BOOK_NUMBER>", getValueOrEmpty(info.bookNumber().get()));
        preview = preview.replace("<YEAR>", getValueOrEmpty(info.year().get()));
        preview = preview.replace("<GENRE>", getValueOrEmpty(info.genre().get()));
        
        // Handle formatted placeholders
        preview = preview.replaceAll("<BOOK_NUMBER;[^>]+>", getValueOrEmpty(info.bookNumber().get()));
        
        // Clean up any remaining tags for preview
        preview = preview.replaceAll("<[^>]+>", "");
        
        // Sanitize the result
        preview = sanitizeFilename(preview);
        
        return preview.isEmpty() ? "(empty result)" : preview;
    }

    private static String replaceIfBlock(String template, String placeholder, String value) {
        String ifStart = "<if(" + placeholder + ")>";
        String ifEnd = "<endif>";
        
        if (!template.contains(ifStart)) {
            return template;
        }
        
        // Find the if block
        int startIdx = template.indexOf(ifStart);
        int endIdx = template.indexOf(ifEnd, startIdx);
        
        if (endIdx < 0) {
            return template;
        }
        
        String beforeIf = template.substring(0, startIdx);
        String insideIf = template.substring(startIdx + ifStart.length(), endIdx);
        String afterEndif = template.substring(endIdx + ifEnd.length());
        
        if (StringUtils.isNotBlank(value)) {
            // Include the content inside if block
            return beforeIf + insideIf + afterEndif;
        } else {
            // Remove the if block entirely
            return beforeIf + afterEndif;
        }
    }

    private static String getValueOrEmpty(String value) {
        return StringUtils.isNotBlank(value) ? value : "";
    }

    private static int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}

