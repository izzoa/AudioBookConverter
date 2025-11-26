package uk.yermak.audiobookconverter;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.yermak.audiobookconverter.formats.Format;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

public class Settings {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EXPORT_VERSION = "1.0";
    private static final Preferences preferences = Preferences.userNodeForPackage(AudiobookConverter.class);
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Format.class, (JsonDeserializer<Format>) (jsonElement, type, context) -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement extension = jsonObject.get("extension");
                String formatType = extension.getAsString();
                return new FormatInstanceCreator(formatType).createInstance(type);
            }).create();

    private boolean darkMode = false;
    private boolean showHints = true;
    private int lastUsedPreset = 0;
    private List<Preset> presets = new ArrayList<>();
    private Set<String> genres = new TreeSet<>();
    private String chapterFormat = "<if(BOOK_NUMBER)><BOOK_NUMBER>. <endif>" +
            "<if(BOOK_TITLE)><BOOK_TITLE>. <endif>" +
            "<if(CHAPTER_TEXT)><CHAPTER_TEXT> <endif>" +
            "<if(CHAPTER_NUMBER)><CHAPTER_NUMBER; format=\"%,03d\"> <endif>" +
            "<if(TAG)><TAG> <endif>" +
            "<if(CUSTOM_TITLE)><CUSTOM_TITLE> <endif>" +
            "<if(DURATION)> - <DURATION; format=\"%02d:%02d:%02d\"><endif>";
    private String filenameFormat = "<WRITER><if(SERIES)> - [<SERIES><if(BOOK_NUMBER)> - <BOOK_NUMBER; format=\"%,02d\"><endif>]<endif> - <TITLE><if(NARRATOR)> (<NARRATOR>)<endif>";
    private String partFormat = "<if(WRITER)><WRITER> <endif>" +
            "<if(SERIES)>- [<SERIES><if(BOOK_NUMBER)> -<BOOK_NUMBER><endif>] - <endif>" +
            "<if(TITLE)><TITLE><endif>" +
            "<if(NARRATOR)> (<NARRATOR>)<endif>" +
            "<if(YEAR)>-<YEAR><endif>" +
            "<if(PART)>, Part <PART; format=\"%,03d\"><endif>";
    private String chapterContext = "CHAPTER_NUMBER:CHAPTER_TEXT:DURATION";
    private String chapterCustomTitle = "";
    private String outputFolder = System.getProperty("user.home");
    private String sourceFolder = System.getProperty("user.home");
    
    // Recent folders tracking
    private static final int MAX_RECENT_ITEMS = 10;
    private List<String> recentSourceFolders = new ArrayList<>();
    private List<String> recentOutputFolders = new ArrayList<>();

    public static void saveSetting(Settings settings) {
        preferences.put(Version.getSettingsVersion(), gson.toJson(settings));
    }

    public static void clear() {
        preferences.remove(Version.getSettingsVersion());
    }

    public void save() {
        saveSetting(this);
    }

    public static Settings loadSetting() {
        String settingsJson = preferences.get(Version.getSettingsVersion(), null);
        if (settingsJson == null) {
            Settings settings = new Settings();
            settings.setPresets(Preset.defaultValues);
            return settings;
        }
        Settings settings = gson.fromJson(settingsJson, Settings.class);
        return settings;
    }

    public static String getRawData() {
        String settingsJson = preferences.get(Version.getSettingsVersion(), null);
        return settingsJson;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public Settings setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        return this;
    }

    public boolean isShowHints() {
        return showHints;
    }

    public Settings setShowHints(boolean showHints) {
        this.showHints = showHints;
        return this;
    }

    public List<Preset> getPresets() {
        return presets;
    }

    public void setPresets(List<Preset> presets) {
        this.presets = presets;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    public String getChapterFormat() {
        return chapterFormat;
    }

    public void setChapterFormat(String chapterFormat) {
        this.chapterFormat = chapterFormat;
    }

    public String getFilenameFormat() {
        return filenameFormat;
    }

    public void setFilenameFormat(String filenameFormat) {
        this.filenameFormat = filenameFormat;
    }

    public String getPartFormat() {

        return partFormat;
    }

    public void setPartFormat(String partFormat) {
        this.partFormat = partFormat;
    }

    public String getChapterContext() {
        return chapterContext;
    }

    public Settings setChapterContext(String chapterContext) {
        this.chapterContext = chapterContext;
        return this;
    }

    public String getChapterCustomTitle() {
        return chapterCustomTitle;
    }

    public Settings setChapterCustomTitle(String chapterCustomTitle) {
        this.chapterCustomTitle = chapterCustomTitle;
        return this;
    }

    public File getOutputFolder() {
        File output = new File(outputFolder);
        if (output.exists()) {
            return output;
        } else if (output.getParentFile().exists()) {
            return output.getParentFile();
        } else {
            return new File(System.getProperty("user.home"));
        }
    }

    public Settings setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
        return this;
    }

    public File getSourceFolder() {
        File source = new File(sourceFolder);
        if (source.exists()) {
            return source;
        } else if (source.getParentFile().exists()) {
            return source.getParentFile();
        } else {
            return new File(System.getProperty("user.home"));
        }
    }

    public Settings setSourceFolder(String sourceFolder) {
        this.sourceFolder = sourceFolder;
        return this;
    }

    public Preset findPreset(String name) {
        for (Preset preset : presets) {
            if (preset.getName().equals(name)) return preset;
        }
        return null;
    }

    public int getLastUsedPreset() {
        return lastUsedPreset;
    }

    public void setLastUsedPreset(int lastUsedPreset) {
        this.lastUsedPreset = lastUsedPreset;
    }

    // ============== Recent Folders Methods ==============

    public List<String> getRecentSourceFolders() {
        return new ArrayList<>(recentSourceFolders);
    }

    public List<String> getRecentOutputFolders() {
        return new ArrayList<>(recentOutputFolders);
    }

    /**
     * Adds a folder to the recent source folders list.
     * Maintains max size and moves duplicates to the front.
     */
    public void addRecentSourceFolder(String path) {
        if (path == null || path.isEmpty()) return;
        // Remove if already exists (will be re-added at front)
        recentSourceFolders.remove(path);
        // Add at front
        recentSourceFolders.add(0, path);
        // Trim to max size
        while (recentSourceFolders.size() > MAX_RECENT_ITEMS) {
            recentSourceFolders.remove(recentSourceFolders.size() - 1);
        }
    }

    /**
     * Adds a folder to the recent output folders list.
     * Maintains max size and moves duplicates to the front.
     */
    public void addRecentOutputFolder(String path) {
        if (path == null || path.isEmpty()) return;
        // Remove if already exists (will be re-added at front)
        recentOutputFolders.remove(path);
        // Add at front
        recentOutputFolders.add(0, path);
        // Trim to max size
        while (recentOutputFolders.size() > MAX_RECENT_ITEMS) {
            recentOutputFolders.remove(recentOutputFolders.size() - 1);
        }
    }

    /**
     * Clears all recent folder history.
     */
    public void clearRecentFolders() {
        recentSourceFolders.clear();
        recentOutputFolders.clear();
    }

    // ============== Export/Import Methods ==============

    /**
     * Exports all settings to a JSON file.
     * @param file The destination file
     * @throws IOException If file cannot be written
     */
    public void exportToFile(File file) throws IOException {
        ExportWrapper wrapper = new ExportWrapper(EXPORT_VERSION, this);
        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        String json = prettyGson.toJson(wrapper);
        Files.writeString(file.toPath(), json, StandardCharsets.UTF_8);
        logger.info("Settings exported to: {}", file.getAbsolutePath());
    }

    /**
     * Imports settings from a JSON file.
     * @param file The source file
     * @return The imported Settings object
     * @throws IOException If file cannot be read or parsed
     */
    public static Settings importFromFile(File file) throws IOException {
        String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        ExportWrapper wrapper = gson.fromJson(json, ExportWrapper.class);
        
        if (wrapper == null || wrapper.settings == null) {
            throw new IOException("Invalid settings file format");
        }
        
        logger.info("Settings imported from: {} (version: {})", file.getAbsolutePath(), wrapper.exportVersion);
        return wrapper.settings;
    }

    /**
     * Exports only presets to a JSON file.
     * @param presetsToExport The list of presets to export
     * @param file The destination file
     * @throws IOException If file cannot be written
     */
    public static void exportPresetsToFile(List<Preset> presetsToExport, File file) throws IOException {
        PresetsExportWrapper wrapper = new PresetsExportWrapper(EXPORT_VERSION, presetsToExport);
        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        String json = prettyGson.toJson(wrapper);
        Files.writeString(file.toPath(), json, StandardCharsets.UTF_8);
        logger.info("Presets exported to: {} ({} presets)", file.getAbsolutePath(), presetsToExport.size());
    }

    /**
     * Imports presets from a JSON file.
     * @param file The source file
     * @return The list of imported presets
     * @throws IOException If file cannot be read or parsed
     */
    public static List<Preset> importPresetsFromFile(File file) throws IOException {
        String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        PresetsExportWrapper wrapper = gson.fromJson(json, PresetsExportWrapper.class);
        
        if (wrapper == null || wrapper.presets == null) {
            throw new IOException("Invalid presets file format");
        }
        
        logger.info("Presets imported from: {} ({} presets, version: {})", 
                file.getAbsolutePath(), wrapper.presets.size(), wrapper.exportVersion);
        return wrapper.presets;
    }

    /**
     * Merges imported presets with existing presets.
     * Presets with the same name will be replaced by imported ones.
     * @param importedPresets The presets to merge
     */
    public void mergePresets(List<Preset> importedPresets) {
        for (Preset imported : importedPresets) {
            // Remove existing preset with same name
            presets.removeIf(existing -> existing.getName().equals(imported.getName()));
            presets.add(imported);
        }
        logger.info("Merged {} presets into settings", importedPresets.size());
    }

    /**
     * Replaces all presets with imported presets.
     * @param importedPresets The new presets
     */
    public void replacePresets(List<Preset> importedPresets) {
        presets.clear();
        presets.addAll(importedPresets);
        lastUsedPreset = 0;
        logger.info("Replaced all presets with {} imported presets", importedPresets.size());
    }

    // Wrapper class for settings export with version info
    private static class ExportWrapper {
        String exportVersion;
        String appVersion;
        Settings settings;

        ExportWrapper(String exportVersion, Settings settings) {
            this.exportVersion = exportVersion;
            this.appVersion = Version.getVersionString();
            this.settings = settings;
        }
    }

    // Wrapper class for presets-only export
    private static class PresetsExportWrapper {
        String exportVersion;
        String appVersion;
        List<Preset> presets;

        PresetsExportWrapper(String exportVersion, List<Preset> presets) {
            this.exportVersion = exportVersion;
            this.appVersion = Version.getVersionString();
            this.presets = presets;
        }
    }

    public static class FormatInstanceCreator implements InstanceCreator<Format> {
        private final String formatType;

        public FormatInstanceCreator(String formatType) {
            this.formatType = formatType;
        }

        @Override
        public Format createInstance(Type type) {
            return Format.instance(formatType);
        }
    }
}

