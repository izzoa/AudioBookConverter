package uk.yermak.audiobookconverter.fx;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;

/**
 * Catalog of error codes with user-friendly messages and recovery suggestions.
 */
public class ErrorCatalog {

    /**
     * Error codes for common failure scenarios.
     */
    public enum ErrorCode {
        FFMPEG_NOT_FOUND(
                "FFmpeg Not Found",
                "The FFmpeg audio processor could not be found on your system.",
                "1. Ensure FFmpeg is installed\n2. Check that FFmpeg is in your system PATH\n3. Reinstall AudioBookConverter",
                "https://github.com/yermak/AudioBookConverter/wiki/Installation"
        ),
        FFMPEG_EXECUTION_FAILED(
                "Audio Processing Failed",
                "FFmpeg encountered an error while processing audio.",
                "1. Check that input files are valid audio files\n2. Ensure enough disk space is available\n3. Try a different output format",
                "https://github.com/yermak/AudioBookConverter/wiki/FAQ"
        ),
        PERMISSION_DENIED(
                "Permission Denied",
                "Cannot access the file or folder due to insufficient permissions.",
                "1. Check that you have read/write access to the folder\n2. Try running as administrator\n3. Choose a different output location",
                null
        ),
        FILE_NOT_FOUND(
                "File Not Found",
                "One or more source files could not be found.",
                "1. Verify the files still exist\n2. Check if files were moved or renamed\n3. Re-add the files to the conversion",
                null
        ),
        DISK_FULL(
                "Insufficient Disk Space",
                "There is not enough disk space to complete the conversion.",
                "1. Free up disk space on the destination drive\n2. Choose a different output location\n3. Use lower quality settings for smaller file size",
                null
        ),
        INVALID_INPUT(
                "Invalid Input File",
                "One or more input files are corrupted or in an unsupported format.",
                "1. Verify the audio files play correctly in another player\n2. Try converting files individually to identify the problem\n3. Re-download or re-rip the source files",
                null
        ),
        ENCODING_FAILED(
                "Encoding Failed",
                "The audio encoding process failed unexpectedly.",
                "1. Try different encoding settings\n2. Check system resources (CPU, memory)\n3. Restart the application and try again",
                "https://github.com/yermak/AudioBookConverter/wiki/FAQ"
        ),
        NETWORK_ERROR(
                "Network Error",
                "A network operation failed. This may affect downloading cover art or checking for updates.",
                "1. Check your internet connection\n2. Try again later\n3. Proceed without online features",
                null
        ),
        CONCATENATION_FAILED(
                "File Merge Failed",
                "Failed to combine audio files into the final audiobook.",
                "1. Check that all source files are accessible\n2. Ensure enough disk space for temporary files\n3. Try converting fewer files at once",
                null
        ),
        ARTWORK_ERROR(
                "Artwork Processing Failed",
                "Failed to add cover artwork to the audiobook.",
                "1. Check that artwork images are valid (JPG, PNG)\n2. Try removing and re-adding artwork\n3. Proceed without cover art",
                null
        ),
        UNKNOWN(
                "Unexpected Error",
                "An unexpected error occurred during conversion.",
                "1. Check the error details below\n2. Restart the application\n3. Report this issue if it persists",
                "https://github.com/yermak/AudioBookConverter/issues"
        );

        private final String title;
        private final String userMessage;
        private final String suggestions;
        private final String helpUrl;

        ErrorCode(String title, String userMessage, String suggestions, String helpUrl) {
            this.title = title;
            this.userMessage = userMessage;
            this.suggestions = suggestions;
            this.helpUrl = helpUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getUserMessage() {
            return userMessage;
        }

        public String getSuggestions() {
            return suggestions;
        }

        public String getHelpUrl() {
            return helpUrl;
        }
    }

    /**
     * Information about an error for display purposes.
     */
    public record ErrorInfo(
            ErrorCode code,
            String userMessage,
            String technicalDetails,
            String suggestions,
            String helpUrl
    ) {
        public boolean hasHelpUrl() {
            return helpUrl != null && !helpUrl.isEmpty();
        }
    }

    /**
     * Analyzes an exception and returns appropriate error information.
     */
    public static ErrorInfo fromException(Exception e) {
        ErrorCode code = classifyException(e);
        return new ErrorInfo(
                code,
                code.getUserMessage(),
                e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName(),
                code.getSuggestions(),
                code.getHelpUrl()
        );
    }

    /**
     * Creates error info from a known error code with additional details.
     */
    public static ErrorInfo fromCode(ErrorCode code, String technicalDetails) {
        return new ErrorInfo(
                code,
                code.getUserMessage(),
                technicalDetails,
                code.getSuggestions(),
                code.getHelpUrl()
        );
    }

    /**
     * Classifies an exception into an appropriate error code.
     */
    private static ErrorCode classifyException(Exception e) {
        String message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        // Check exception type first
        if (e instanceof AccessDeniedException) {
            return ErrorCode.PERMISSION_DENIED;
        }
        if (e instanceof NoSuchFileException) {
            return ErrorCode.FILE_NOT_FOUND;
        }
        if (e instanceof IOException) {
            if (message.contains("permission") || message.contains("access denied")) {
                return ErrorCode.PERMISSION_DENIED;
            }
            if (message.contains("no space") || message.contains("disk full")) {
                return ErrorCode.DISK_FULL;
            }
            if (message.contains("not found") || message.contains("no such file")) {
                return ErrorCode.FILE_NOT_FOUND;
            }
        }

        // Check message content
        if (message.contains("ffmpeg") || message.contains("ffprobe")) {
            if (message.contains("not found") || message.contains("cannot run")) {
                return ErrorCode.FFMPEG_NOT_FOUND;
            }
            return ErrorCode.FFMPEG_EXECUTION_FAILED;
        }

        if (message.contains("permission") || message.contains("access denied") || message.contains("cannot write")) {
            return ErrorCode.PERMISSION_DENIED;
        }

        if (message.contains("no space") || message.contains("disk full") || message.contains("not enough space")) {
            return ErrorCode.DISK_FULL;
        }

        if (message.contains("corrupt") || message.contains("invalid") || message.contains("unsupported format")) {
            return ErrorCode.INVALID_INPUT;
        }

        if (message.contains("encode") || message.contains("codec")) {
            return ErrorCode.ENCODING_FAILED;
        }

        if (message.contains("concat") || message.contains("merge")) {
            return ErrorCode.CONCATENATION_FAILED;
        }

        if (message.contains("artwork") || message.contains("cover") || message.contains("image")) {
            return ErrorCode.ARTWORK_ERROR;
        }

        if (message.contains("network") || message.contains("connection") || message.contains("timeout")) {
            return ErrorCode.NETWORK_ERROR;
        }

        return ErrorCode.UNKNOWN;
    }

    /**
     * Parses FFmpeg stderr output for known error patterns.
     */
    public static ErrorCode classifyFFmpegError(String stderrOutput) {
        if (stderrOutput == null || stderrOutput.isEmpty()) {
            return ErrorCode.FFMPEG_EXECUTION_FAILED;
        }

        String lower = stderrOutput.toLowerCase();

        if (lower.contains("no such file") || lower.contains("does not exist")) {
            return ErrorCode.FILE_NOT_FOUND;
        }
        if (lower.contains("permission denied") || lower.contains("access is denied")) {
            return ErrorCode.PERMISSION_DENIED;
        }
        if (lower.contains("no space left") || lower.contains("disk full")) {
            return ErrorCode.DISK_FULL;
        }
        if (lower.contains("invalid data") || lower.contains("corrupt") || lower.contains("invalid input")) {
            return ErrorCode.INVALID_INPUT;
        }
        if (lower.contains("encoder") || lower.contains("codec not found")) {
            return ErrorCode.ENCODING_FAILED;
        }

        return ErrorCode.FFMPEG_EXECUTION_FAILED;
    }
}

