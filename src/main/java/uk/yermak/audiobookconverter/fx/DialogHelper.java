package uk.yermak.audiobookconverter.fx;

import com.google.common.collect.ImmutableSet;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.yermak.audiobookconverter.*;
import uk.yermak.audiobookconverter.book.AudioBookInfo;
import uk.yermak.audiobookconverter.fx.util.Comparators;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.*;

public class DialogHelper {
    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String M4B = "m4b";
    private static final String M4A = "m4a";
    public static final String MP3 = "mp3";
    public static final String WMA = "wma";
    public static final String FLAC = "flac";
    public static final String AAC = "aac";
    public static final String OGG = "ogg";
    public static final String WAV = "wav";
    public static final String AAX = "aax";
    public static final String AA = "aa";

    private final static String[] FILE_EXTENSIONS = {MP3, M4A, M4B, WMA, FLAC, OGG, AAC, WAV, AAX, AA};


    static String selectOutputFile(AudioBookInfo audioBookInfo) {
        JfxEnv env = AudiobookConverter.getEnv();

        final FileChooser fileChooser = new FileChooser();
        try {
            File outputFolder = Settings.loadSetting().getOutputFolder();
            // Only set initial directory if it exists and is readable (important for macOS sandbox)
            if (outputFolder != null && outputFolder.exists() && outputFolder.isDirectory() && outputFolder.canRead()) {
                fileChooser.setInitialDirectory(outputFolder);
            }
            // If not accessible, let macOS pick a reasonable default by not setting initialDirectory
        } catch (Exception e) {
            logger.error("Failed to load Output Folder and set Initial Directory", e);
            // Continue without setting initial directory - system will use default
        }
        fileChooser.setInitialFileName(Utils.getOuputFilenameSuggestion(audioBookInfo));
        fileChooser.setTitle("Save AudioBook");
        String formatAsString = AudiobookConverter.getContext().getFormat().toString();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(formatAsString, "*." + formatAsString)
        );
        
        File file;
        try {
            file = fileChooser.showSaveDialog(env.getWindow());
        } catch (Exception e) {
            logger.error("Failed to show save dialog", e);
            return null;
        }
        if (file == null) return null;
        File parentFolder = file.getParentFile();
        Settings settings = Settings.loadSetting();
        settings.setOutputFolder(parentFolder.getAbsolutePath());
        settings.addRecentOutputFolder(parentFolder.getAbsolutePath());
        settings.save();
        return file.getPath();
    }

    public static List<String> selectFilesDialog() {
        Window window = AudiobookConverter.getEnv().getWindow();
        final FileChooser fileChooser = new FileChooser();
        try {
            File sourceFolder = Settings.loadSetting().getSourceFolder();
            // Only set initial directory if it exists and is readable (important for macOS sandbox)
            if (sourceFolder != null && sourceFolder.exists() && sourceFolder.isDirectory() && sourceFolder.canRead()) {
                fileChooser.setInitialDirectory(sourceFolder);
            }
            // If not accessible, let macOS pick a reasonable default by not setting initialDirectory
        } catch (Exception e) {
            logger.error("Failed to load Source Folder and set Initial Directory", e);
            // Continue without setting initial directory - system will use default
        }
        StringJoiner filetypes = new StringJoiner("/");

        Arrays.stream(FILE_EXTENSIONS).map(String::toUpperCase).forEach(filetypes::add);

        fileChooser.setTitle("Select " + filetypes + " files for conversion");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", Arrays.asList(toSuffixes("*.", FILE_EXTENSIONS))));

        List<File> files;
        try {
            files = fileChooser.showOpenMultipleDialog(window);
        } catch (Exception e) {
            logger.error("Failed to show file dialog", e);
            return null;
        }
        if (files == null) return null;

        if (!files.isEmpty()) {
            File firstFile = files.get(0);
            File parentFile = firstFile.getParentFile();
            Settings settings = Settings.loadSetting();
            settings.setSourceFolder(parentFile.getAbsolutePath());
            settings.addRecentSourceFolder(parentFile.getAbsolutePath());
            settings.save();
        }
        return collectFiles(files);
    }

    public static List<String> selectFolderDialog() {
        Window window = AudiobookConverter.getEnv().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        try {
            File sourceFolder = Settings.loadSetting().getSourceFolder();
            // Only set initial directory if it exists and is readable (important for macOS sandbox)
            if (sourceFolder != null && sourceFolder.exists() && sourceFolder.isDirectory() && sourceFolder.canRead()) {
                directoryChooser.setInitialDirectory(sourceFolder);
            }
            // If not accessible, let macOS pick a reasonable default by not setting initialDirectory
        } catch (Exception e) {
            logger.error("Failed to load Source Folder and set Initial Directory", e);
            // Continue without setting initial directory - system will use default
        }

        StringJoiner filetypes = new StringJoiner("/");

        Arrays.stream(FILE_EXTENSIONS).map(String::toUpperCase).forEach(filetypes::add);

        directoryChooser.setTitle("Select folder with " + filetypes + " files for conversion");
        
        File selectedDirectory;
        try {
            selectedDirectory = directoryChooser.showDialog(window);
        } catch (Exception e) {
            logger.error("Failed to show folder dialog", e);
            return null;
        }

        if (selectedDirectory == null) return null;
        Settings settings = Settings.loadSetting();
        settings.setSourceFolder(selectedDirectory.getAbsolutePath());
        settings.addRecentSourceFolder(selectedDirectory.getAbsolutePath());
        settings.save();

        return collectFiles(Collections.singleton(selectedDirectory));
    }

    static List<String> collectFiles(Collection<File> files) {
        List<String> fileNames = new ArrayList<>();
        ImmutableSet<String> extensions = ImmutableSet.copyOf(FILE_EXTENSIONS);

        for (File file : files) {
            if (file.isDirectory()) {
                SuffixFileFilter suffixFileFilter = new SuffixFileFilter(toSuffixes(".", FILE_EXTENSIONS), IOCase.INSENSITIVE);
                Collection<File> nestedFiles = FileUtils.listFiles(file, suffixFileFilter, TrueFileFilter.INSTANCE);
                nestedFiles.stream().map(File::getPath).forEach(fileNames::add);
            } else {
                boolean allowedFileExtension = extensions.contains(FilenameUtils.getExtension(file.getName()).toLowerCase());
                if (allowedFileExtension) {
                    fileNames.add(file.getPath());
                }
            }
        }

        Comparator<String> cmp = Comparators.comparingAlphaDecimal(Comparator.comparing(CharSequence::toString, String::compareToIgnoreCase));
        fileNames.sort(cmp);
        return fileNames;
    }

    private static String[] toSuffixes(String prefix, final String[] extensions) {
        final String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            suffixes[i] = prefix + extensions[i];
        }
        return suffixes;
    }


}
