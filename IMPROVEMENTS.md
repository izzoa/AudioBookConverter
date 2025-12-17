# AudioBookConverter - Improvement Plan

This document outlines 25 proposed enhancements for AudioBookConverter that will work across all platforms (Windows, macOS, Linux).

**Progress Tracking:** Check off tasks as completed. Each improvement has granular sub-tasks to follow.

---

## 📋 Quick Reference

| # | Feature | Phase | Status |
|---|---------|-------|--------|
| 5 | Export/Import Settings & Presets | 1 | ✅ Complete |
| 8 | Recent Files/Folders Menu | 1 | ✅ Complete |
| 11 | Improved Error Messages & Recovery | 1 | ✅ Complete |
| 19 | Input Validation & Sanitization | 1 | ✅ Complete |
| 4 | Conversion Time Estimation | 1 | ✅ Complete |
| 2 | Audio Normalization/Volume Leveling | 2 | ⬜ Not Started |
| 7 | Drag-and-Drop for Artwork | 2 | ⬜ Not Started |
| 13 | Chapter Markers from Text File | 2 | ⬜ Not Started |
| 6 | System Tray Integration | 2 | ⬜ Not Started |
| 15 | Localization Support (i18n) | 2 | ⬜ Not Started |
| 1 | Metadata Auto-Fill from Online Sources | 3 | ⬜ Not Started |
| 3 | Silence Detection & Trimming | 3 | ⬜ Not Started |
| 9 | Conversion History/Log Viewer | 3 | ⬜ Not Started |
| 10 | Batch Queue Management | 3 | ⬜ Not Started |
| 12 | Additional Output Formats (FLAC, Opus) | 3 | ⬜ Not Started |
| 16 | Memory Usage Optimization | 4 | ⬜ Not Started |
| 20 | Enhanced Auto-Update Mechanism | 4 | ⬜ Not Started |
| 14 | Audio Preview with Waveform Display | 4 | ⬜ Not Started |
| 17 | Parallel Concatenation | 4 | ⬜ Not Started |
| 18 | GPU-Accelerated Encoding | 4 | ⬜ Not Started |
| 21 | CSS Architecture & Theme System | 5 | ✅ Complete |
| 22 | SVG Icon System | 5 | ✅ Complete |
| 23 | Typography & Spacing | 5 | ✅ Complete |
| 24 | Modern Component Styling | 5 | ✅ Complete |
| 25 | FXML Layout Refinements | 5 | ✅ Complete |

**Legend:** ⬜ Not Started | 🔄 In Progress | ✅ Complete | ⏸️ Blocked

---

## 🚀 Phase 1: Quick Wins (Low Effort, High Impact)

### Improvement #5: Export/Import Settings & Presets
**Status:** ⬜ Not Started  
**Priority:** High | **Effort:** Low

> Allow users to export their settings and custom presets to a file, and import settings on other machines or share with others.

#### Tasks:
- [ ] **5.1** Read and understand `Settings.java` structure
- [ ] **5.2** Read and understand `Preset.java` structure
- [ ] **5.3** Add `exportToJson(File file)` method to `Settings.java`
  - [ ] Serialize all settings fields to JSON
  - [ ] Include version number for migration compatibility
  - [ ] Handle file write errors gracefully
- [ ] **5.4** Add `importFromJson(File file)` static method to `Settings.java`
  - [ ] Parse JSON and validate structure
  - [ ] Handle version mismatches with migration logic
  - [ ] Return imported Settings object or throw descriptive exception
- [ ] **5.5** Add `exportPresets(List<Preset> presets, File file)` method
- [ ] **5.6** Add `importPresets(File file)` method with merge option
- [ ] **5.7** Update `fxml_converter.fxml` - Add menu items under System menu:
  - [ ] "Export Settings..."
  - [ ] "Import Settings..."
  - [ ] "Export Presets..."
  - [ ] "Import Presets..."
- [ ] **5.8** Add handler methods in `FilesController.java`:
  - [ ] `exportSettings()` - show save dialog, call export
  - [ ] `importSettings()` - show open dialog, call import, refresh UI
  - [ ] `exportPresets()` - show save dialog
  - [ ] `importPresets()` - show open dialog with merge/replace option
- [ ] **5.9** Add locale strings to `messages_en.properties`
- [ ] **5.10** Test export/import cycle preserves all settings
- [ ] **5.11** Test cross-platform file compatibility

---

### Improvement #8: Recent Files/Folders Menu
**Status:** ⬜ Not Started  
**Priority:** High | **Effort:** Low

> Track and display recently used source folders and output destinations for quick access.

#### Tasks:
- [ ] **8.1** Add to `Settings.java`:
  - [ ] `private List<String> recentSourceFolders = new ArrayList<>()`
  - [ ] `private List<String> recentOutputFolders = new ArrayList<>()`
  - [ ] `private static final int MAX_RECENT_ITEMS = 10`
  - [ ] `addRecentSourceFolder(String path)` method (maintains max size)
  - [ ] `addRecentOutputFolder(String path)` method
  - [ ] `clearRecentFolders()` method
  - [ ] Getters for both lists
- [ ] **8.2** Update `DialogHelper.java`:
  - [ ] In `selectFilesDialog()` - call `addRecentSourceFolder()` after selection
  - [ ] In `selectFolderDialog()` - call `addRecentSourceFolder()` after selection
  - [ ] In `selectOutputFile()` - call `addRecentOutputFolder()` after selection
- [ ] **8.3** Update `fxml_converter.fxml`:
  - [ ] Add `<Menu fx:id="recentSourceMenu" text="Recent Sources">` under File menu
  - [ ] Add `<Menu fx:id="recentOutputMenu" text="Recent Outputs">` under File menu
  - [ ] Add `<MenuItem text="Clear Recent" onAction="#clearRecent"/>`
- [ ] **8.4** Add to `FilesController.java`:
  - [ ] `@FXML private Menu recentSourceMenu`
  - [ ] `@FXML private Menu recentOutputMenu`
  - [ ] `refreshRecentMenus()` method - populates menus from Settings
  - [ ] `openRecentSource(ActionEvent)` handler
  - [ ] `openRecentOutput(ActionEvent)` handler
  - [ ] `clearRecent()` handler
  - [ ] Call `refreshRecentMenus()` in `initialize()` and after file operations
- [ ] **8.5** Add locale strings to `messages_en.properties`
- [ ] **8.6** Test recent folders persist across app restarts
- [ ] **8.7** Test menu updates after selecting new folders

---

### Improvement #11: Improved Error Messages & Recovery
**Status:** ⬜ Not Started  
**Priority:** High | **Effort:** Low

> Replace technical error messages with user-friendly explanations and actionable suggestions.

#### Tasks:
- [ ] **11.1** Create `ErrorCatalog.java` in `uk.yermak.audiobookconverter`:
  - [ ] Define enum `ErrorCode` with codes: `FFMPEG_NOT_FOUND`, `PERMISSION_DENIED`, `DISK_FULL`, `INVALID_INPUT`, `ENCODING_FAILED`, `NETWORK_ERROR`, `UNKNOWN`
  - [ ] Create `ErrorInfo` record with: `code`, `userMessage`, `technicalDetails`, `suggestions`, `wikiUrl`
  - [ ] Add static method `fromException(Exception e)` that pattern-matches exception messages to error codes
  - [ ] Add static method `getUserMessage(ErrorCode code)` 
- [ ] **11.2** Create `ErrorHandler.java`:
  - [ ] `showError(ErrorCode code, String technicalDetails)` - displays user-friendly dialog
  - [ ] `showErrorWithRetry(ErrorCode code, Runnable retryAction)` - dialog with Retry button
  - [ ] `showErrorWithOptions(ErrorCode code, Map<String, Runnable> options)` - multiple action buttons
  - [ ] Include "Copy Details" button for bug reports
  - [ ] Include "Help" button linking to wiki
- [ ] **11.3** Update `ConversionJob.java`:
  - [ ] Replace catch block with `ErrorHandler` calls
  - [ ] Detect specific failure modes (disk space, permissions, codec issues)
  - [ ] Offer retry option for transient failures
- [ ] **11.4** Add error detection in `FFMpegNativeConverter.java`:
  - [ ] Parse FFmpeg stderr for known error patterns
  - [ ] Map FFmpeg errors to `ErrorCode` values
- [ ] **11.5** Add to `messages_en.properties`:
  - [ ] User-friendly messages for each `ErrorCode`
  - [ ] Suggestion strings for each error type
- [ ] **11.6** Test each error scenario displays correct message
- [ ] **11.7** Test retry functionality works correctly

---

### Improvement #19: Input Validation & Sanitization
**Status:** ⬜ Not Started  
**Priority:** High | **Effort:** Low

> Add comprehensive validation for user inputs, especially filename format templates.

#### Tasks:
- [ ] **19.1** Create `FilenameValidator.java`:
  - [ ] `validateTemplate(String template)` - returns `ValidationResult`
  - [ ] `ValidationResult` record with: `isValid`, `errors`, `warnings`
  - [ ] Check for invalid characters per platform (Windows: `<>:"/\|?*`, Unix: `/\0`)
  - [ ] Check for reserved Windows names (CON, PRN, AUX, NUL, COM1-9, LPT1-9)
  - [ ] Validate ST4 template syntax (balanced `<if>` tags, valid placeholders)
  - [ ] `sanitizeFilename(String filename)` - removes/replaces invalid chars
  - [ ] `previewFilename(String template, AudioBookInfo info)` - shows what filename would be generated
- [ ] **19.2** Update `Utils.java`:
  - [ ] Add `sanitizeForFilesystem(String input)` method
  - [ ] Use in `getOuputFilenameSuggestion()` method
- [ ] **19.3** Update `SettingsDialog.java`:
  - [ ] Validate filename/chapter/part format templates on OK click
  - [ ] Show validation errors inline or in dialog
  - [ ] Prevent saving invalid templates
- [ ] **19.4** Update `settings.fxml`:
  - [ ] Add validation status indicator next to each format field
  - [ ] Add "Preview" button to show example output
- [ ] **19.5** Update `OutputController.java`:
  - [ ] Add filename preview in output tab
  - [ ] Update preview when book info or format changes
- [ ] **19.6** Add to `messages_en.properties`:
  - [ ] Validation error messages
  - [ ] Character restriction warnings
- [ ] **19.7** Test validation catches all invalid inputs
- [ ] **19.8** Test sanitization produces valid filenames on all platforms

---

### Improvement #4: Conversion Time Estimation
**Status:** ⬜ Not Started  
**Priority:** High | **Effort:** Low

> Display estimated time remaining for conversions based on processing speed.

#### Tasks:
- [ ] **4.1** Update `ProgressCallback.java`:
  - [ ] Add `long startTimeMillis` field
  - [ ] Add `long bytesProcessed` tracking
  - [ ] Add `long totalBytes` field
  - [ ] Add `getEstimatedRemainingSeconds()` method
  - [ ] Add `getProcessingSpeedBytesPerSecond()` method
- [ ] **4.2** Update `ConversionProgress.java`:
  - [ ] Track start time when conversion begins
  - [ ] Calculate rolling average speed (last 10 updates)
  - [ ] Expose `estimatedTimeRemaining` property
- [ ] **4.3** Update `ProgressComponent.java`:
  - [ ] Add Label for "Time Remaining: HH:MM:SS"
  - [ ] Add Label for "Speed: X.X MB/s"
  - [ ] Update labels from `ConversionProgress` properties
  - [ ] Handle "Calculating..." state for first few seconds
- [ ] **4.4** Update `progress.fxml`:
  - [ ] Add UI elements for time estimation display
  - [ ] Style appropriately
- [ ] **4.5** Update `ConversionJob.java`:
  - [ ] Initialize total bytes from media list
  - [ ] Pass bytes info to progress callbacks
- [ ] **4.6** Add to `messages_en.properties`:
  - [ ] "Time remaining" label
  - [ ] "Calculating..." placeholder
  - [ ] "Speed" label
- [ ] **4.7** Test estimation accuracy on various file sizes
- [ ] **4.8** Test display updates smoothly during conversion

---

## 🔧 Phase 2: Core Enhancements (Medium Effort)

### Improvement #2: Audio Normalization/Volume Leveling
**Status:** ⬜ Not Started  
**Priority:** High | **Effort:** Medium

> Add option to normalize audio loudness using FFmpeg's loudnorm filter.

#### Tasks:
- [ ] **2.1** Update `OutputParameters.java`:
  - [ ] Add `private boolean normalizeAudio = false`
  - [ ] Add `private double targetLUFS = -16.0`
  - [ ] Add getters and setters
  - [ ] Update constructor to include new fields
- [ ] **2.2** Update `Preset.java`:
  - [ ] Add normalization fields to preset
  - [ ] Update `defaultValues` list with normalization defaults
  - [ ] Update `copy()` method
- [ ] **2.3** Update `Format.java`:
  - [ ] Add normalization filter to `getReencodingOptions()`:
    ```
    if (outputParameters.isNormalizeAudio()) {
        options.add("-af");
        options.add("loudnorm=I=" + outputParameters.getTargetLUFS());
    }
    ```
- [ ] **2.4** Update `output.fxml`:
  - [ ] Add CheckBox for "Normalize Audio"
  - [ ] Add Slider or ComboBox for target LUFS (-24 to -14 range)
  - [ ] Add tooltip explaining normalization
- [ ] **2.5** Update `OutputController.java`:
  - [ ] Add `@FXML CheckBox normalizeCheckbox`
  - [ ] Add `@FXML Slider targetLufsSlider`
  - [ ] Wire up change listeners to save to preset
  - [ ] Update `updateOutputSettingsFromPreset()` method
- [ ] **2.6** Add to `messages_en.properties`:
  - [ ] Normalization checkbox label
  - [ ] LUFS slider label
  - [ ] Tooltip text explaining loudness normalization
- [ ] **2.7** Test normalization produces consistent output levels
- [ ] **2.8** Test interaction with speed adjustment filter

---

### Improvement #7: Drag-and-Drop for Artwork
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** Low

> Enable drag-and-drop for adding cover artwork to the artwork panel.

#### Tasks:
- [ ] **7.1** Update `art_work.fxml`:
  - [ ] Add `onDragOver` handler to artwork ListView
  - [ ] Add `onDragDropped` handler
  - [ ] Add `onDragEntered`/`onDragExited` for visual feedback
- [ ] **7.2** Update `ArtWorkController.java`:
  - [ ] Add `handleDragOver(DragEvent event)`:
    - [ ] Check for image files or URLs in dragboard
    - [ ] Accept drop if valid: `event.acceptTransferModes(TransferMode.COPY)`
  - [ ] Add `handleDragDropped(DragEvent event)`:
    - [ ] Get files from `event.getDragboard().getFiles()`
    - [ ] Filter for image extensions (png, jpg, jpeg, gif, webp, bmp)
    - [ ] Load each image as ArtWork and add to context
    - [ ] Handle URL drops: download image from URL
  - [ ] Add `handleDragEntered(DragEvent event)`:
    - [ ] Add visual highlight style to drop zone
  - [ ] Add `handleDragExited(DragEvent event)`:
    - [ ] Remove highlight style
- [ ] **7.3** Add CSS styling for drag highlight state
- [ ] **7.4** Add to `messages_en.properties`:
  - [ ] Tooltip for drop zone
- [ ] **7.5** Test drag from file manager (Windows Explorer, Finder, Nautilus)
- [ ] **7.6** Test drag from web browser
- [ ] **7.7** Test multiple file drop

---

### Improvement #13: Chapter Markers from Text File
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** Low

> Import chapter timestamps and titles from external text/CSV files.

#### Tasks:
- [ ] **13.1** Create `ChapterImporter.java`:
  - [ ] Define `ChapterEntry` record: `timestamp (long ms)`, `title (String)`
  - [ ] `List<ChapterEntry> parseSimpleText(File file)`:
    - [ ] Format: `HH:MM:SS Title` or `HH:MM:SS - Title`
  - [ ] `List<ChapterEntry> parseCSV(File file)`:
    - [ ] Format: `timestamp,title` with header row optional
  - [ ] `List<ChapterEntry> parseAudacityLabels(File file)`:
    - [ ] Format: `start_seconds\tend_seconds\tlabel`
  - [ ] `List<ChapterEntry> parseCUE(File file)`:
    - [ ] Parse standard CUE sheet format
  - [ ] `List<ChapterEntry> autoDetectAndParse(File file)`:
    - [ ] Detect format from extension or content
  - [ ] `void exportToText(List<Chapter> chapters, File file)`
  - [ ] `void exportToCSV(List<Chapter> chapters, File file)`
- [ ] **13.2** Update `fxml_converter.fxml`:
  - [ ] Add "Import Chapters from File..." menu item under Chapter menu
  - [ ] Add "Export Chapters to File..." menu item
- [ ] **13.3** Update `FilesController.java`:
  - [ ] Add `importChaptersFromFile()` handler:
    - [ ] Show file chooser with appropriate filters
    - [ ] Parse file using ChapterImporter
    - [ ] Show preview dialog with parsed chapters
    - [ ] On confirm, apply chapters to book
  - [ ] Add `exportChaptersToFile()` handler
- [ ] **13.4** Create preview dialog for chapter import:
  - [ ] Show table of parsed chapters
  - [ ] Allow editing before import
  - [ ] Show warnings for any parse issues
- [ ] **13.5** Add to `messages_en.properties`:
  - [ ] Menu item labels
  - [ ] Dialog titles and labels
  - [ ] Parse error messages
- [ ] **13.6** Test import of each supported format
- [ ] **13.7** Test export/re-import cycle preserves data

---

### Improvement #6: System Tray Integration
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** Medium

> Add option to minimize to system tray and show notifications from tray.

#### Tasks:
- [ ] **6.1** Create `SystemTrayManager.java`:
  - [ ] Check `SystemTray.isSupported()` at init
  - [ ] Create `TrayIcon` with app icon
  - [ ] Create `PopupMenu` with items:
    - [ ] "Show" - brings window to front
    - [ ] "Pause All" - pauses conversions
    - [ ] "Resume All" - resumes conversions
    - [ ] Separator
    - [ ] "Exit" - closes app
  - [ ] `void show()` - adds icon to system tray
  - [ ] `void hide()` - removes icon
  - [ ] `void updateTooltip(String text)` - shows conversion progress
  - [ ] `void showNotification(String title, String message)`
- [ ] **6.2** Update `Settings.java`:
  - [ ] Add `private boolean minimizeToTray = false`
  - [ ] Add `private boolean showTrayNotifications = true`
  - [ ] Add getters and setters
- [ ] **6.3** Update `AudiobookConverter.java`:
  - [ ] Initialize `SystemTrayManager` after stage creation
  - [ ] Modify `setOnCloseRequest`:
    - [ ] If `minimizeToTray` enabled and conversions running, minimize instead of close
  - [ ] Add minimize to tray on window iconify if enabled
  - [ ] Restore window on tray icon double-click
- [ ] **6.4** Update `settings.fxml`:
  - [ ] Add CheckBox "Minimize to system tray"
  - [ ] Add CheckBox "Show tray notifications"
- [ ] **6.5** Update `SettingsDialog.java`:
  - [ ] Wire up tray settings checkboxes
- [ ] **6.6** Update `ConversionJob.java`:
  - [ ] Call `SystemTrayManager.updateTooltip()` on progress updates
  - [ ] Call `SystemTrayManager.showNotification()` on completion
- [ ] **6.7** Add to `messages_en.properties`:
  - [ ] Tray menu items
  - [ ] Settings labels
  - [ ] Notification messages
- [ ] **6.8** Test on Windows (system tray well-supported)
- [ ] **6.9** Test on macOS (menu bar integration)
- [ ] **6.10** Test on Linux (varies by desktop environment)

---

### Improvement #15: Localization Support (i18n)
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** Medium

> Add translations for common languages.

#### Tasks:
- [ ] **15.1** Audit all FXML files for hardcoded strings:
  - [ ] `fxml_converter.fxml` - replace with `%key` references
  - [ ] `book_info.fxml`
  - [ ] `art_work.fxml`
  - [ ] `output.fxml`
  - [ ] `settings.fxml`
  - [ ] `mediaplayer.fxml`
  - [ ] `progress.fxml`
- [ ] **15.2** Update `messages_en.properties`:
  - [ ] Add any missing strings found in audit
  - [ ] Organize by feature/section with comments
- [ ] **15.3** Create `messages_de.properties` (German):
  - [ ] Translate all strings
- [ ] **15.4** Create `messages_fr.properties` (French):
  - [ ] Translate all strings
- [ ] **15.5** Create `messages_es.properties` (Spanish):
  - [ ] Translate all strings
- [ ] **15.6** Create `messages_ru.properties` (Russian):
  - [ ] Translate all strings
- [ ] **15.7** Update `Settings.java`:
  - [ ] Add `private String locale = "en"`
  - [ ] Add getter and setter
- [ ] **15.8** Update `settings.fxml`:
  - [ ] Add ComboBox for language selection
- [ ] **15.9** Update `SettingsDialog.java`:
  - [ ] Wire up language selector
  - [ ] Note: requires app restart for full effect
- [ ] **15.10** Update `AudiobookConverter.java`:
  - [ ] Load saved locale preference before loading FXML
  - [ ] Set `Locale.setDefault()` based on setting
- [ ] **15.11** Test each language displays correctly
- [ ] **15.12** Test special characters render properly

---

## 🎯 Phase 3: Advanced Features (Higher Effort)

### Improvement #1: Metadata Auto-Fill from Online Sources
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** High

> Integrate with public APIs to automatically fetch book metadata.

#### Tasks:
- [ ] **1.1** Create `loaders/OpenLibraryClient.java`:
  - [ ] Define `BookMetadata` record: title, author, description, coverUrl, isbn, publishYear
  - [ ] `CompletableFuture<List<BookMetadata>> searchByTitle(String title)`
  - [ ] `CompletableFuture<List<BookMetadata>> searchByAuthor(String author)`
  - [ ] `CompletableFuture<BookMetadata> getByISBN(String isbn)`
  - [ ] `CompletableFuture<byte[]> downloadCover(String coverUrl)`
  - [ ] Handle rate limiting and errors gracefully
  - [ ] Use HttpClient for async requests
- [ ] **1.2** Create `loaders/GoogleBooksClient.java`:
  - [ ] Same interface as OpenLibraryClient
  - [ ] Implement Google Books API integration
  - [ ] Handle API key if needed (optional for basic queries)
- [ ] **1.3** Create `loaders/MetadataFetcher.java`:
  - [ ] Orchestrates multiple providers
  - [ ] `CompletableFuture<List<BookMetadata>> search(String query)`
  - [ ] Merge and dedupe results from multiple sources
  - [ ] Cache results using WeakHashMap
  - [ ] Configurable provider priority
- [ ] **1.4** Create `MetadataSearchDialog.java`:
  - [ ] Search input field
  - [ ] Results table showing matches
  - [ ] Preview selected metadata
  - [ ] "Apply" button to populate BookInfo
  - [ ] "Download Cover" option
- [ ] **1.5** Update `book_info.fxml`:
  - [ ] Add "Search Online" button next to title field
- [ ] **1.6** Update `BookInfoController.java`:
  - [ ] Add `searchOnline()` handler
  - [ ] Show MetadataSearchDialog
  - [ ] Apply selected metadata to form fields
  - [ ] Download and add cover to artwork
- [ ] **1.7** Add to `messages_en.properties`:
  - [ ] Search dialog labels
  - [ ] Error messages for API failures
  - [ ] "No results found" message
- [ ] **1.8** Test search with various book titles
- [ ] **1.9** Test cover download and artwork addition
- [ ] **1.10** Test offline/error handling

---

### Improvement #3: Silence Detection & Trimming
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** Medium

> Automatically detect and optionally trim silence from tracks.

#### Tasks:
- [ ] **3.1** Create `SilenceDetector.java`:
  - [ ] Define `SilenceRegion` record: startMs, endMs, durationMs
  - [ ] `List<SilenceRegion> detectSilence(File audioFile, double thresholdDb, double minDurationSec)`:
    - [ ] Run FFmpeg with silencedetect filter
    - [ ] Parse output for silence_start and silence_end
    - [ ] Return list of detected regions
  - [ ] `List<Long> suggestChapterMarkers(List<SilenceRegion> silences, long minChapterDuration)`:
    - [ ] Find silences that could be chapter breaks
    - [ ] Filter by minimum chapter duration
- [ ] **3.2** Create `SilenceDetectionDialog.java`:
  - [ ] Slider for threshold (-60dB to -20dB, default -50dB)
  - [ ] Slider for minimum duration (0.1s to 2s, default 0.5s)
  - [ ] "Analyze" button
  - [ ] Results list showing detected silences
  - [ ] Options: "Trim Leading", "Trim Trailing", "Use as Chapter Markers"
  - [ ] Preview visualization (optional)
- [ ] **3.3** Update `Settings.java`:
  - [ ] Add `private boolean trimLeadingSilence = false`
  - [ ] Add `private boolean trimTrailingSilence = false`
  - [ ] Add `private double silenceThresholdDb = -50.0`
  - [ ] Add `private double silenceMinDuration = 0.5`
- [ ] **3.4** Update `fxml_converter.fxml`:
  - [ ] Add "Detect Silence..." menu item under Chapter menu
- [ ] **3.5** Update `FilesController.java`:
  - [ ] Add `detectSilence()` handler
  - [ ] Show SilenceDetectionDialog
  - [ ] Apply selected silence handling
- [ ] **3.6** Update `FFMpegNativeConverter.java` (if trim enabled):
  - [ ] Add trim filter to FFmpeg command
  - [ ] Calculate trim points from silence detection
- [ ] **3.7** Add to `messages_en.properties`:
  - [ ] Dialog labels and tooltips
  - [ ] Results messages
- [ ] **3.8** Test silence detection on various audio files
- [ ] **3.9** Test trimming produces correct output
- [ ] **3.10** Test chapter marker suggestions

---

### Improvement #9: Conversion History/Log Viewer
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** Medium

> Maintain a searchable history of past conversions.

#### Tasks:
- [ ] **9.1** Create `ConversionHistoryEntry.java`:
  - [ ] Fields: id, timestamp, sourceFiles (list), outputFile, presetName, duration, status, errorMessage
  - [ ] Serializable to JSON
- [ ] **9.2** Create `ConversionHistory.java`:
  - [ ] Store history in JSON file in app home directory
  - [ ] `void addEntry(ConversionHistoryEntry entry)`
  - [ ] `List<ConversionHistoryEntry> getAll()`
  - [ ] `List<ConversionHistoryEntry> search(String query)`
  - [ ] `List<ConversionHistoryEntry> filterByDateRange(Date from, Date to)`
  - [ ] `List<ConversionHistoryEntry> filterByStatus(ProgressStatus status)`
  - [ ] `void clearHistory()`
  - [ ] `void exportToCSV(File file)`
  - [ ] Limit history size (configurable, default 1000 entries)
- [ ] **9.3** Create `conversion_history.fxml`:
  - [ ] Search field
  - [ ] Filter dropdowns (date range, status)
  - [ ] TableView with columns: Date, Title, Output, Status, Duration
  - [ ] Detail panel showing full info for selected entry
  - [ ] Buttons: "Open Output Folder", "Re-convert", "Delete", "Clear All"
- [ ] **9.4** Create `ConversionHistoryController.java`:
  - [ ] Load and display history
  - [ ] Implement search and filter
  - [ ] Handle button actions
- [ ] **9.5** Update `fxml_converter.fxml`:
  - [ ] Add "Conversion History" menu item under System menu
- [ ] **9.6** Update `FilesController.java`:
  - [ ] Add `showHistory()` handler
- [ ] **9.7** Update `ConversionJob.java`:
  - [ ] Create history entry when conversion starts
  - [ ] Update entry on completion/failure
  - [ ] Save to ConversionHistory
- [ ] **9.8** Add to `messages_en.properties`:
  - [ ] History dialog labels
  - [ ] Column headers
  - [ ] Button labels
- [ ] **9.9** Test history persists across app restarts
- [ ] **9.10** Test search and filter functionality
- [ ] **9.11** Test re-convert from history entry

---

### Improvement #10: Batch Queue Management
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** Medium

> Enhanced queue management with reordering, priorities, and templates.

#### Tasks:
- [ ] **10.1** Update `ConversionJob.java`:
  - [ ] Add `private Priority priority = Priority.NORMAL`
  - [ ] Define `enum Priority { HIGH, NORMAL, LOW }`
  - [ ] Add getter and setter
- [ ] **10.2** Update `ConversionContext.java`:
  - [ ] Change `conversionQueue` to support priority ordering
  - [ ] Add `moveJobUp(ConversionJob job)`
  - [ ] Add `moveJobDown(ConversionJob job)`
  - [ ] Add `setPriority(ConversionJob job, Priority priority)`
  - [ ] Add `duplicateJob(ConversionJob job)` - creates new job with same settings
- [ ] **10.3** Update `ProgressComponent.java`:
  - [ ] Make ListView items draggable for reordering
  - [ ] Add context menu with:
    - [ ] "Move Up"
    - [ ] "Move Down"
    - [ ] "Set Priority" submenu (High/Normal/Low)
    - [ ] "Duplicate"
    - [ ] "Cancel"
  - [ ] Show priority indicator (icon or color)
- [ ] **10.4** Update `progress.fxml`:
  - [ ] Enable drag-and-drop on ListView
  - [ ] Add priority indicator styling
- [ ] **10.5** Create queue template feature:
  - [ ] Define `QueueTemplate` class: name, list of job configurations
  - [ ] Add "Save as Template" button
  - [ ] Add "Load Template" menu
  - [ ] Store templates in Settings
- [ ] **10.6** Update `Settings.java`:
  - [ ] Add `List<QueueTemplate> queueTemplates`
- [ ] **10.7** Add to `messages_en.properties`:
  - [ ] Context menu labels
  - [ ] Priority labels
  - [ ] Template dialog labels
- [ ] **10.8** Test drag-to-reorder functionality
- [ ] **10.9** Test priority affects execution order
- [ ] **10.10** Test template save/load

---

### Improvement #12: Additional Output Formats (FLAC, Opus)
**Status:** ⬜ Not Started  
**Priority:** Low | **Effort:** Medium

> Add support for FLAC and standalone Opus output formats.

#### Tasks:
- [ ] **12.1** Create `formats/FLACFormat.java` extending `Format`:
  - [ ] Set codec to "flac"
  - [ ] Set extension to "flac"
  - [ ] Define compression levels (0-12, default 5)
  - [ ] Override `frequencies()` - support higher rates
  - [ ] Override `bitrates()` - N/A for lossless, use compression level
  - [ ] Override `getConcatOptions()`
  - [ ] Override `getReencodingOptions()` - add compression level
  - [ ] Override `mp4Compatible()` return false
  - [ ] Override `ffmpegCompatible()` return true
- [ ] **12.2** Create `formats/OpusFormat.java` extending `Format`:
  - [ ] Set codec to "libopus"
  - [ ] Set extension to "opus"
  - [ ] Define bitrates suitable for Opus (6-510 kbps)
  - [ ] Override all required methods
  - [ ] Support VBR and CBR modes
- [ ] **12.3** Update `Format.java`:
  - [ ] Add `static Format FLAC = new FLACFormat()`
  - [ ] Add `static Format OPUS = new OpusFormat()`
  - [ ] Update `instance()` method to handle new extensions
- [ ] **12.4** Update `OutputController.java`:
  - [ ] Add FLAC and OPUS to format dropdown
  - [ ] Handle format-specific UI (compression level for FLAC)
- [ ] **12.5** Update `output.fxml`:
  - [ ] Add compression level control (shown only for FLAC)
- [ ] **12.6** Update `OutputParameters.java`:
  - [ ] Add `private int compressionLevel = 5` for FLAC
- [ ] **12.7** Add to `messages_en.properties`:
  - [ ] Format descriptions
  - [ ] Compression level labels
- [ ] **12.8** Test FLAC output quality and file size
- [ ] **12.9** Test Opus output quality and compatibility
- [ ] **12.10** Test chapter support in new formats

---

## 📊 Phase 4: Performance & Polish

### Improvement #16: Memory Usage Optimization
**Status:** ⬜ Not Started  
**Priority:** Medium | **Effort:** Medium

> Optimize memory usage for large audiobooks.

#### Tasks:
- [ ] **16.1** Update `ArtWorkImage.java`:
  - [ ] Implement lazy loading - don't load full image until needed
  - [ ] Store only file path/URL initially
  - [ ] Load thumbnail for display (max 200x200)
  - [ ] Load full image only when exporting
  - [ ] Use `SoftReference` for cached full images
- [ ] **16.2** Update `ArtWorkBean.java`:
  - [ ] Implement thumbnail generation
  - [ ] Cache thumbnails to disk in temp directory
  - [ ] Clear cache on app exit
- [ ] **16.3** Update `FFMediaLoader.java`:
  - [ ] Stream metadata parsing instead of loading all at once
  - [ ] Release resources immediately after parsing
  - [ ] Use bounded thread pool for media loading
- [ ] **16.4** Update `ConversionContext.java`:
  - [ ] Implement `WeakReference` for cached media info
  - [ ] Add method to clear caches manually
  - [ ] Monitor memory usage and clear automatically if needed
- [ ] **16.5** Add memory monitoring (debug mode):
  - [ ] Track heap usage
  - [ ] Log when caches are cleared
  - [ ] Optional memory usage display in UI (Settings toggle)
- [ ] **16.6** Test with audiobook containing 100+ files
- [ ] **16.7** Test with very large cover images (5000x5000+)
- [ ] **16.8** Profile memory usage before and after optimization

---

### Improvement #20: Enhanced Auto-Update Mechanism
**Status:** ⬜ Not Started  
**Priority:** Low | **Effort:** Medium

> Improve version checker with release notes and download option.

#### Tasks:
- [ ] **20.1** Create `UpdateManager.java`:
  - [ ] `CompletableFuture<UpdateInfo> checkForUpdate()`:
    - [ ] Fetch from GitHub API releases endpoint
    - [ ] Parse latest release info
  - [ ] Define `UpdateInfo` record: version, releaseNotes, downloadUrl, publishDate
  - [ ] `void downloadUpdate(UpdateInfo info, ProgressCallback callback)`:
    - [ ] Download installer to temp directory
    - [ ] Show download progress
  - [ ] Cache check result for session (don't spam API)
- [ ] **20.2** Create update dialog:
  - [ ] Show current version vs new version
  - [ ] Display release notes (markdown rendered)
  - [ ] "Download" button
  - [ ] "Remind Me Later" button
  - [ ] "Skip This Version" checkbox
  - [ ] Download progress bar
- [ ] **20.3** Update `Settings.java`:
  - [ ] Add `private String skippedVersion = null`
  - [ ] Add `private boolean checkUpdatesOnStartup = true`
  - [ ] Add `private int updateCheckIntervalDays = 7`
  - [ ] Add `private long lastUpdateCheckTimestamp = 0`
- [ ] **20.4** Update `settings.fxml`:
  - [ ] Add "Check for updates on startup" checkbox
  - [ ] Add update check interval dropdown
- [ ] **20.5** Update `SettingsDialog.java`:
  - [ ] Wire up update settings
- [ ] **20.6** Update `AudiobookConverter.java`:
  - [ ] Replace `VersionChecker` with `UpdateManager`
  - [ ] Respect settings for auto-check
  - [ ] Skip if version is in skipped list
- [ ] **20.7** Add to `messages_en.properties`:
  - [ ] Update dialog labels
  - [ ] Settings labels
- [ ] **20.8** Test update detection with mock responses
- [ ] **20.9** Test download functionality
- [ ] **20.10** Test "skip version" persistence

---

### Improvement #14: Audio Preview with Waveform Display
**Status:** ⬜ Not Started  
**Priority:** Low | **Effort:** High

> Add visual waveform representation for audio navigation.

#### Tasks:
- [ ] **14.1** Create `WaveformGenerator.java`:
  - [ ] `CompletableFuture<int[]> generateWaveformData(File audioFile, int numSamples)`:
    - [ ] Use FFmpeg to extract audio samples
    - [ ] Downsample to target resolution
    - [ ] Return array of amplitude values
  - [ ] Cache generated waveforms to disk
  - [ ] Use hash of file path + mod time as cache key
- [ ] **14.2** Create `WaveformView.java` (extends Canvas):
  - [ ] `void setWaveformData(int[] data)`
  - [ ] `void setPlaybackPosition(double percent)`
  - [ ] `void setSelection(double startPercent, double endPercent)`
  - [ ] Draw waveform with configurable colors
  - [ ] Draw playback position indicator
  - [ ] Draw selection highlight
  - [ ] Handle click events for seeking
  - [ ] Handle drag for selection (sub-track splitting)
- [ ] **14.3** Update `mediaplayer.fxml`:
  - [ ] Add WaveformView below or replacing time slider
  - [ ] Adjust layout for new component
- [ ] **14.4** Update `MediaPlayerController.java`:
  - [ ] Initialize WaveformView
  - [ ] Generate waveform when track selected
  - [ ] Update playback position during playback
  - [ ] Handle click-to-seek on waveform
  - [ ] Pass selection to SubTracksDialog for splitting
- [ ] **14.5** Update `SubTracksDialog.java`:
  - [ ] Use waveform selection for split points
  - [ ] Show waveform in split dialog
- [ ] **14.6** Add loading indicator while waveform generates
- [ ] **14.7** Add to `messages_en.properties`:
  - [ ] "Generating waveform..." message
  - [ ] Tooltips
- [ ] **14.8** Test waveform generation performance
- [ ] **14.9** Test seeking via waveform click
- [ ] **14.10** Test selection for sub-track splitting
- [ ] **14.11** Test cache hit/miss scenarios

---

### Improvement #17: Parallel Concatenation
**Status:** ⬜ Not Started  
**Priority:** Low | **Effort:** High

> Implement chunked parallel concatenation for large audiobooks.

#### Tasks:
- [ ] **17.1** Update `Settings.java`:
  - [ ] Add `private int parallelConcatThreshold = 50` (file count)
  - [ ] Add `private int parallelConcatChunkSize = 20`
  - [ ] Add getters and setters
- [ ] **17.2** Update `FFMpegConcatenator.java`:
  - [ ] Add `shouldUseParallelConcat(int fileCount)` method
  - [ ] Create `concatParallel()` method:
    - [ ] Split file list into chunks
    - [ ] Create temp output file for each chunk
    - [ ] Submit chunk concatenation to executor
    - [ ] Wait for all chunks to complete
    - [ ] Final concatenation of chunk outputs
    - [ ] Clean up temp chunk files
  - [ ] Update `concat()` to choose strategy based on threshold
- [ ] **17.3** Update progress tracking:
  - [ ] Track progress across all chunks
  - [ ] Aggregate progress for display
- [ ] **17.4** Handle errors:
  - [ ] If one chunk fails, cancel others
  - [ ] Clean up partial outputs
  - [ ] Report which chunk failed
- [ ] **17.5** Add to `settings.fxml`:
  - [ ] Advanced settings section for parallel concat options
- [ ] **17.6** Add to `messages_en.properties`:
  - [ ] Settings labels
  - [ ] Progress messages for parallel processing
- [ ] **17.7** Benchmark with 100+ file audiobook
- [ ] **17.8** Test error handling when chunk fails
- [ ] **17.9** Test with varying chunk sizes
- [ ] **17.10** Compare sequential vs parallel performance

---

### Improvement #18: GPU-Accelerated Encoding
**Status:** ⬜ Not Started  
**Priority:** Low | **Effort:** High

> Add hardware-accelerated encoding option via FFmpeg.

#### Tasks:
- [ ] **18.1** Create `HardwareDetector.java`:
  - [ ] `List<HardwareEncoder> detectAvailableEncoders()`:
    - [ ] Run `ffmpeg -encoders` and parse output
    - [ ] Check for: `h264_nvenc`, `hevc_nvenc` (NVIDIA)
    - [ ] Check for: `h264_videotoolbox`, `aac_at` (macOS)
    - [ ] Check for: `h264_vaapi`, `aac_vaapi` (Linux)
    - [ ] Check for: `h264_qsv`, `aac_qsv` (Intel QuickSync)
  - [ ] Define `HardwareEncoder` record: name, type, supported
  - [ ] Cache detection results
- [ ] **18.2** Update `Settings.java`:
  - [ ] Add `private boolean useHardwareAcceleration = false`
  - [ ] Add `private String preferredHardwareEncoder = "auto"`
- [ ] **18.3** Update `OutputParameters.java`:
  - [ ] Add `private boolean hardwareAcceleration = false`
  - [ ] Add `private String hardwareEncoder = null`
- [ ] **18.4** Update `Format.java` and subclasses:
  - [ ] Modify `getReencodingOptions()` to use hardware encoder when enabled
  - [ ] Add fallback to software encoding if hardware fails
  - [ ] Note: Hardware acceleration primarily benefits video; for audio-only, benefit is limited
- [ ] **18.5** Update `settings.fxml`:
  - [ ] Add "Use Hardware Acceleration" checkbox
  - [ ] Add detected encoders dropdown
  - [ ] Show "No hardware encoders detected" if none found
- [ ] **18.6** Update `SettingsDialog.java`:
  - [ ] Run hardware detection on dialog open
  - [ ] Populate encoder dropdown
  - [ ] Wire up settings
- [ ] **18.7** Update `Platform.java`:
  - [ ] Add hardware capabilities info
- [ ] **18.8** Add to `messages_en.properties`:
  - [ ] Hardware acceleration labels
  - [ ] Encoder descriptions
  - [ ] "Not available" messages
- [ ] **18.9** Test on system with NVIDIA GPU
- [ ] **18.10** Test on macOS with VideoToolbox
- [ ] **18.11** Test on Linux with VAAPI
- [ ] **18.12** Test fallback when hardware encoding fails
- [ ] **18.13** Benchmark hardware vs software encoding speed

---

## 🎨 Phase 5: UI Modernization (Fluent Design)

### Improvement #21: CSS Architecture & Theme System
**Status:** ✅ Complete  
**Priority:** High | **Effort:** Medium

> Create a proper CSS architecture with modular stylesheets and theme variables following Microsoft Fluent Design principles.

#### Tasks:
- [ ] **21.1** Create `styles/` folder structure:
  - [ ] `src/main/resources/styles/fluent-base.css` - CSS variables and base resets
  - [ ] `src/main/resources/styles/fluent-light.css` - Light theme color overrides
  - [ ] `src/main/resources/styles/fluent-dark.css` - Dark theme color overrides
  - [ ] `src/main/resources/styles/components.css` - Component-specific styles
  - [ ] `src/main/resources/styles/layout.css` - Layout and spacing utilities
- [ ] **21.2** Define CSS custom properties in `fluent-base.css`:
  - [ ] Color palette: `--color-primary`, `--color-accent`, `--color-background`, `--color-surface`, `--color-text-primary`, `--color-text-secondary`
  - [ ] Semantic colors: `--color-success`, `--color-warning`, `--color-error`, `--color-info`
  - [ ] Spacing scale: `--spacing-xs` (4px), `--spacing-sm` (8px), `--spacing-md` (16px), `--spacing-lg` (24px), `--spacing-xl` (32px)
  - [ ] Border radius: `--radius-sm`, `--radius-md`, `--radius-lg`
  - [ ] Shadows: `--shadow-sm`, `--shadow-md`, `--shadow-lg` (Fluent depth system)
  - [ ] Transitions: `--transition-fast`, `--transition-normal`
- [ ] **21.3** Implement light theme in `fluent-light.css`:
  - [ ] Follow Fluent Design light mode color specifications
  - [ ] Background: neutral grays (#FAFAFA, #F5F5F5)
  - [ ] Surface: white with subtle shadows
  - [ ] Primary: Fluent Blue (#0078D4)
- [ ] **21.4** Implement dark theme in `fluent-dark.css`:
  - [ ] Follow Fluent Design dark mode specifications
  - [ ] Background: dark grays (#202020, #2D2D2D)
  - [ ] Surface: elevated dark (#3D3D3D)
  - [ ] Adjusted accent colors for dark backgrounds
- [ ] **21.5** Update `JfxEnv.java`:
  - [ ] Replace inline `-fx-base` style with stylesheet loading
  - [ ] Create `loadTheme(boolean darkMode)` method
  - [ ] Load base CSS + theme-specific CSS
  - [ ] Support runtime theme switching
- [ ] **21.6** Update `AudiobookConverter.java`:
  - [ ] Load stylesheets on scene creation
  - [ ] Apply saved theme preference from Settings
- [ ] **21.7** Test theme switching without app restart
- [ ] **21.8** Test all components render correctly in both themes

---

### Improvement #22: SVG Icon System
**Status:** ✅ Complete (Using Ikonli library)  
**Priority:** Medium | **Effort:** Medium

> Replace existing icons with modern SVG icons from Fluent UI Icons library with theme-aware coloring.

#### Tasks:
- [x] **22.1** Create icon infrastructure:
  - [x] Create `icons/` resource folder: `src/main/resources/icons/` (Replaced by Ikonli)
  - [x] Create `IconLoader.java` utility class:
    - [x] `Node loadIcon(String name, int size)` - loads SVG as JavaFX node
    - [x] `Node loadIcon(String name, int size, String colorClass)` - with color class
    - [x] Support for icon caching (Handled by Ikonli)
    - [x] Fallback handling for missing icons
- [x] **22.2** Download and organize Fluent UI Icons: (Handled by Ikonli library pack)
- [x] **22.3** Add CSS classes for icon theming:
  - [x] `.icon-primary` - uses primary text color
  - [x] `.icon-secondary` - uses secondary text color
  - [x] `.icon-accent` - uses accent color
  - [x] `.icon-success`, `.icon-warning`, `.icon-error`
- [/] **22.4** Update menu items with icons:
  - [x] File menu items (Add, Remove, Clear, etc.)
  - [ ] Chapter menu items
  - [ ] System menu items (Settings, Export, Import)
- [/] **22.5** Update toolbar/buttons with icons:
  - [x] Media player controls
  - [x] Action buttons throughout UI
- [ ] **22.6** Update list items with icons:
  - [ ] File list items (audio file icon)
  - [ ] Chapter list items
  - [ ] Progress list items (status icons)
- [ ] **22.7** Test icons scale correctly at different DPI settings
- [ ] **22.8** Test icon colors update with theme changes

---

### Improvement #23: Typography & Spacing
**Status:** ✅ Complete  
**Priority:** High | **Effort:** Low

> Implement consistent typography scale and spacing system following Fluent Design guidelines.

#### Tasks:
- [ ] **23.1** Add cross-platform fonts:
  - [ ] Add Inter font as web font (open source alternative to Segoe UI Variable)
  - [ ] Create `fonts/` resource folder with Inter font files (.ttf)
  - [ ] Configure font loading in base CSS
  - [ ] Define fallback font stack: `'Inter', 'Segoe UI Variable', -apple-system, sans-serif`
- [ ] **23.2** Define type scale in CSS:
  - [ ] `--font-size-caption`: 12px - for secondary info, timestamps
  - [ ] `--font-size-body`: 14px - default body text
  - [ ] `--font-size-body-large`: 16px - emphasized body text
  - [ ] `--font-size-subtitle`: 18px - section headers
  - [ ] `--font-size-title`: 24px - dialog/panel titles
  - [ ] `--font-size-display`: 32px - large headers (rarely used)
- [ ] **23.3** Define font weights:
  - [ ] `--font-weight-regular`: 400
  - [ ] `--font-weight-medium`: 500
  - [ ] `--font-weight-semibold`: 600
- [ ] **23.4** Create typography utility classes:
  - [ ] `.text-caption`, `.text-body`, `.text-body-large`
  - [ ] `.text-subtitle`, `.text-title`
  - [ ] `.text-primary`, `.text-secondary`, `.text-disabled`
  - [ ] `.text-truncate` - for ellipsis overflow
- [ ] **23.5** Define spacing scale (4px base unit):
  - [ ] Update all CSS variables with consistent spacing
  - [ ] Create spacing utility classes: `.p-xs`, `.p-sm`, `.p-md`, `.p-lg`, `.m-xs`, etc.
- [ ] **23.6** Update `fxml_converter.fxml`:
  - [ ] Add consistent padding to main containers
  - [ ] Update menu bar spacing
  - [ ] Adjust tab content padding
- [ ] **23.7** Update all FXML files with spacing classes:
  - [ ] `book_info.fxml` - form field spacing
  - [ ] `art_work.fxml` - list item spacing
  - [ ] `output.fxml` - control spacing
  - [ ] `progress.fxml` - progress item spacing
  - [ ] `settings.fxml` - settings row spacing
  - [ ] `mediaplayer.fxml` - control spacing
- [ ] **23.8** Test readability across different screen sizes
- [ ] **23.9** Test text rendering on all platforms

---

### Improvement #24: Modern Component Styling
**Status:** ✅ Complete  
**Priority:** High | **Effort:** High

> Apply Fluent Design styling to all JavaFX components including buttons, inputs, lists, and panels.

#### Tasks:
- [ ] **24.1** Style buttons in `components.css`:
  - [ ] `.button` base styles: padding, border-radius, transitions
  - [ ] `.button-primary`: filled accent color, white text
  - [ ] `.button-secondary`: outlined style, transparent background
  - [ ] `.button-subtle`: minimal style, transparent until hover
  - [ ] `.button-accent`: high-emphasis action button
  - [ ] Hover, pressed, focused, and disabled states
  - [ ] Focus ring using Fluent Design focus indicator
- [ ] **24.2** Style text inputs:
  - [ ] `.text-field` base: clean border, proper padding
  - [ ] Underline style on focus (Fluent characteristic)
  - [ ] Placeholder text styling
  - [ ] Error state with red border
  - [ ] `.text-area` multi-line styling
- [ ] **24.3** Style combo boxes and dropdowns:
  - [ ] Clean dropdown arrow icon
  - [ ] Popup styling with shadow
  - [ ] Item hover and selection states
  - [ ] Consistent with text input styling
- [ ] **24.4** Style lists and tables:
  - [ ] `.list-view` clean styling, remove default borders
  - [ ] Row hover effect (subtle background change)
  - [ ] Selection highlight using accent color
  - [ ] `.table-view` header styling
  - [ ] Alternating row colors (optional, subtle)
- [ ] **24.5** Style tab panes:
  - [ ] `.tab-pane` modern tab strip
  - [ ] Active tab indicator (underline style)
  - [ ] Tab hover effects
  - [ ] Tab content area styling
- [ ] **24.6** Style menus and context menus:
  - [ ] Menu background with Fluent depth (shadow)
  - [ ] Menu item padding and hover states
  - [ ] Separator styling
  - [ ] Submenu indicator styling
- [ ] **24.7** Style dialogs and alerts:
  - [ ] Dialog background and shadow
  - [ ] Header/title area styling
  - [ ] Button bar alignment and spacing
  - [ ] Consistent with Fluent dialog patterns
- [ ] **24.8** Style sliders and progress bars:
  - [ ] `.slider` track and thumb styling
  - [ ] `.progress-bar` Fluent-style indicator
  - [ ] Indeterminate progress animation
- [ ] **24.9** Style scroll bars:
  - [ ] Thin, modern scrollbar design
  - [ ] Auto-hide behavior (optional)
  - [ ] Thumb and track colors per theme
- [ ] **24.10** Add card/panel styling:
  - [ ] `.card` class: surface color, rounded corners, subtle shadow
  - [ ] `.card-elevated` for higher elevation
  - [ ] `.panel` for content sections
- [ ] **24.11** Add transition effects:
  - [ ] Smooth transitions on hover/focus (150ms ease)
  - [ ] Button press feedback
  - [ ] List item transitions
- [ ] **24.12** Test all components in both light and dark themes
- [ ] **24.13** Test keyboard navigation and focus indicators

---

### Improvement #25: FXML Layout Refinements
**Status:** ✅ Complete  
**Priority:** Medium | **Effort:** Medium

> Update all FXML files to apply new style classes, improve visual hierarchy, and ensure consistent component sizing.

#### Tasks:
- [ ] **25.1** Update `fxml_converter.fxml` (main layout):
  - [ ] Add `styleClass="root-container"` to root VBox
  - [ ] Apply card styling to main content areas
  - [ ] Update menu bar with proper spacing
  - [ ] Ensure tab pane uses new styles
  - [ ] Add proper padding to all containers
- [ ] **25.2** Update `book_info.fxml`:
  - [ ] Wrap form in card panel
  - [ ] Apply typography classes to labels
  - [ ] Style input fields consistently
  - [ ] Improve label-input alignment (grid refinement)
  - [ ] Add visual grouping for related fields
- [ ] **25.3** Update `art_work.fxml`:
  - [ ] Style artwork list as card
  - [ ] Add proper spacing between items
  - [ ] Style action buttons
  - [ ] Improve empty state messaging
- [ ] **25.4** Update `output.fxml`:
  - [ ] Group related settings in cards
  - [ ] Style combo boxes and inputs
  - [ ] Improve preset selector styling
  - [ ] Add visual hierarchy with headers
- [ ] **25.5** Update `progress.fxml`:
  - [ ] Style progress list items as cards
  - [ ] Improve progress bar visibility
  - [ ] Style status labels with appropriate colors
  - [ ] Update button styling (pause, cancel, etc.)
- [ ] **25.6** Update `settings.fxml`:
  - [ ] Group settings into logical sections
  - [ ] Style toggle switches
  - [ ] Add section headers
  - [ ] Improve spacing and alignment
- [ ] **25.7** Update `mediaplayer.fxml`:
  - [ ] Style playback controls with icon buttons
  - [ ] Improve slider/scrubber styling
  - [ ] Add time display styling
  - [ ] Style volume control
- [ ] **25.8** Update `subtracks.fxml`:
  - [ ] Style dialog content
  - [ ] Style list of sub-tracks
  - [ ] Update button styling
- [ ] **25.9** Ensure consistent minimum widths/heights:
  - [ ] Buttons: minimum 32px height
  - [ ] Input fields: minimum 32px height
  - [ ] List items: appropriate row height
- [ ] **25.10** Add responsive considerations:
  - [ ] Proper min/max widths on containers
  - [ ] Text truncation where appropriate
  - [ ] Flexible layouts that adapt to window size
- [ ] **25.11** Test layout at minimum window size
- [ ] **25.12** Test layout at maximum/multiple monitors

---

## 📝 Progress Notes

Use this section to track overall progress and any blockers:

### Session Log
| Date | Improvements Worked On | Notes |
|------|----------------------|-------|
| | | |

### Blockers
- None currently

### Decisions Made
- None yet

---

## 🔗 References

- [FFmpeg Documentation](https://ffmpeg.org/documentation.html)
- [JavaFX Documentation](https://openjfx.io/javadoc/21/)
- [OpenLibrary API](https://openlibrary.org/developers/api)
- [Google Books API](https://developers.google.com/books)
- [Microsoft Fluent Design System](https://fluent2.microsoft.design/)
- [Fluent UI Icons](https://github.com/microsoft/fluentui-system-icons)
- [Inter Font Family](https://rsms.me/inter/)
- [JavaFX CSS Reference](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/cssref.html)

---

*Last Updated: November 2025*
