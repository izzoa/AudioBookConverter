# AudioBookConverter - AI Assistant Context

This document provides context and design specifications for AI assistants working on the AudioBookConverter project.

## Project Overview

AudioBookConverter is a cross-platform JavaFX application that converts audio files into audiobook formats (M4B, MP3, etc.) with chapter support, metadata editing, and batch processing capabilities.

### Tech Stack
- **Language:** Java 21+ (with preview features enabled)
- **UI Framework:** JavaFX 21+
- **Build Tool:** Maven
- **Audio Processing:** FFmpeg (external dependency)
- **JSON Serialization:** Gson
- **Additional UI Components:** ControlsFX

### Project Structure
```
src/main/java/uk/yermak/audiobookconverter/
├── fx/                     # JavaFX controllers and UI components
│   ├── FilesController.java      # Main controller
│   ├── OutputController.java     # Output settings
│   ├── BookInfoController.java   # Metadata editing
│   ├── ArtWorkController.java    # Cover art management
│   ├── ProgressComponent.java    # Conversion progress
│   ├── DialogHelper.java         # File dialogs
│   ├── ErrorHandler.java         # Error display
│   ├── ErrorCatalog.java         # Error definitions
│   └── FilenameValidator.java    # Input validation
├── loaders/                # Media file loaders
├── formats/                # Output format definitions
├── Settings.java           # User preferences
├── Preset.java             # Output presets
├── ConversionJob.java      # Conversion task
├── ConversionContext.java  # Application state
└── Utils.java              # Utility methods

src/main/resources/
├── uk/yermak/audiobookconverter/fx/
│   ├── fxml_converter.fxml       # Main layout
│   ├── book_info.fxml            # Book info panel
│   ├── art_work.fxml             # Artwork panel
│   ├── output.fxml               # Output settings panel
│   ├── progress.fxml             # Progress display
│   ├── settings.fxml             # Settings dialog
│   ├── mediaplayer.fxml          # Audio preview
│   └── subtracks.fxml            # Sub-track splitting
├── locales/
│   └── messages_en.properties    # English strings
└── styles/                       # CSS stylesheets (Phase 5)
```

---

## Design System: Fluent Design

The UI modernization follows **Microsoft Fluent Design System** principles for a clean, modern, and accessible interface.

### Color Palette

#### Light Theme
| Token | Value | Usage |
|-------|-------|-------|
| `--color-background` | `#FAFAFA` | App background |
| `--color-background-secondary` | `#F5F5F5` | Secondary backgrounds |
| `--color-surface` | `#FFFFFF` | Cards, panels |
| `--color-surface-secondary` | `#F9F9F9` | Elevated surfaces |
| `--color-primary` | `#0078D4` | Primary actions, links |
| `--color-primary-hover` | `#106EBE` | Primary hover state |
| `--color-accent` | `#0078D4` | Accent elements |
| `--color-text-primary` | `#1A1A1A` | Primary text |
| `--color-text-secondary` | `#616161` | Secondary text |
| `--color-text-disabled` | `#A0A0A0` | Disabled text |
| `--color-border` | `#E0E0E0` | Borders, dividers |
| `--color-border-strong` | `#C4C4C4` | Emphasized borders |

#### Dark Theme
| Token | Value | Usage |
|-------|-------|-------|
| `--color-background` | `#202020` | App background |
| `--color-background-secondary` | `#2D2D2D` | Secondary backgrounds |
| `--color-surface` | `#2D2D2D` | Cards, panels |
| `--color-surface-secondary` | `#3D3D3D` | Elevated surfaces |
| `--color-primary` | `#4CC2FF` | Primary actions (adjusted for dark) |
| `--color-primary-hover` | `#62CDFF` | Primary hover state |
| `--color-accent` | `#4CC2FF` | Accent elements |
| `--color-text-primary` | `#FFFFFF` | Primary text |
| `--color-text-secondary` | `#B0B0B0` | Secondary text |
| `--color-text-disabled` | `#707070` | Disabled text |
| `--color-border` | `#404040` | Borders, dividers |
| `--color-border-strong` | `#505050` | Emphasized borders |

#### Semantic Colors
| Token | Light | Dark | Usage |
|-------|-------|------|-------|
| `--color-success` | `#107C10` | `#6CCB5F` | Success states |
| `--color-warning` | `#FF8C00` | `#FCE100` | Warnings |
| `--color-error` | `#D13438` | `#FF6B6B` | Errors |
| `--color-info` | `#0078D4` | `#4CC2FF` | Information |

### Typography

#### Font Stack
```css
-fx-font-family: 'Inter', 'Segoe UI Variable', 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
```

#### Type Scale
| Token | Size | Weight | Usage |
|-------|------|--------|-------|
| `--font-size-caption` | 12px | 400 | Timestamps, secondary info |
| `--font-size-body` | 14px | 400 | Default body text |
| `--font-size-body-large` | 16px | 400 | Emphasized body |
| `--font-size-subtitle` | 18px | 600 | Section headers |
| `--font-size-title` | 24px | 600 | Dialog/panel titles |
| `--font-size-display` | 32px | 600 | Large headers |

#### Font Weights
| Token | Value |
|-------|-------|
| `--font-weight-regular` | 400 |
| `--font-weight-medium` | 500 |
| `--font-weight-semibold` | 600 |

### Spacing Scale (4px base unit)

| Token | Value | Usage |
|-------|-------|-------|
| `--spacing-xs` | 4px | Tight spacing |
| `--spacing-sm` | 8px | Small gaps |
| `--spacing-md` | 16px | Standard padding |
| `--spacing-lg` | 24px | Section spacing |
| `--spacing-xl` | 32px | Large sections |
| `--spacing-xxl` | 48px | Major divisions |

### Border Radius

| Token | Value | Usage |
|-------|-------|-------|
| `--radius-sm` | 4px | Small elements (chips, badges) |
| `--radius-md` | 8px | Buttons, inputs |
| `--radius-lg` | 12px | Cards, panels |
| `--radius-xl` | 16px | Dialogs, modals |

### Shadows (Fluent Depth System)

```css
/* Rest/Default */
--shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.06);

/* Hover/Elevated */
--shadow-md: 0 4px 8px rgba(0, 0, 0, 0.08), 0 2px 4px rgba(0, 0, 0, 0.06);

/* Active/Popover */
--shadow-lg: 0 8px 16px rgba(0, 0, 0, 0.12), 0 4px 8px rgba(0, 0, 0, 0.08);

/* Dark theme shadows use rgba(0, 0, 0, 0.4) base */
```

### Transitions

| Token | Value | Usage |
|-------|-------|-------|
| `--transition-fast` | 100ms ease | Micro-interactions |
| `--transition-normal` | 150ms ease | Standard transitions |
| `--transition-slow` | 250ms ease | Complex animations |

---

## Component Specifications

### Buttons

| Variant | Background | Text | Border |
|---------|------------|------|--------|
| Primary | `--color-primary` | White | None |
| Secondary | Transparent | `--color-primary` | `--color-primary` |
| Subtle | Transparent | `--color-text-primary` | None |
| Accent | `--color-accent` | White | None |

**States:**
- **Hover:** Darken 10% or show background
- **Pressed:** Darken 20%
- **Focused:** 2px outline with `--color-primary`, 2px offset
- **Disabled:** 40% opacity

**Sizing:**
- Minimum height: 32px
- Horizontal padding: 16px
- Border radius: `--radius-md`

### Text Inputs

- Height: 32px
- Padding: 8px 12px
- Border: 1px solid `--color-border`
- Border radius: `--radius-md`
- **Focus:** Bottom border becomes 2px `--color-primary` (Fluent underline effect)
- **Error:** Border color `--color-error`

### Lists & Tables

- Row height: 40px minimum
- Hover: `--color-surface-secondary` background
- Selected: `--color-primary` at 10% opacity with left accent border
- Dividers: 1px `--color-border` (optional)

### Cards

- Background: `--color-surface`
- Border radius: `--radius-lg`
- Shadow: `--shadow-sm`
- Padding: `--spacing-md`
- **Hover (interactive):** `--shadow-md`

### Tab Panes

- Tab height: 40px
- Active indicator: 2px bottom border `--color-primary`
- Inactive text: `--color-text-secondary`
- Active text: `--color-text-primary`

---

## Icons

Use **Fluent UI System Icons** (MIT licensed) from:
https://github.com/microsoft/fluentui-system-icons

### Icon Sizing
| Size | Usage |
|------|-------|
| 16px | Inline with text, menu items |
| 20px | Standard buttons, list items |
| 24px | Toolbar buttons |
| 32px | Large actions, empty states |

### Icon Colors
- Primary: `--color-text-primary`
- Secondary: `--color-text-secondary`
- Accent: `--color-primary`
- Match semantic colors for status icons

---

## Coding Conventions

### Java
- Use Java 21 features (records, pattern matching, etc.)
- Follow standard Java naming conventions
- Prefer composition over inheritance
- Use `@FXML` annotations for controller bindings
- Handle all exceptions with `ErrorHandler`

### FXML
- Use `fx:id` for controller bindings
- Use `%key` syntax for localized strings
- Apply `styleClass` attributes for CSS styling
- Prefer `GridPane` and `VBox`/`HBox` for layouts

### CSS (JavaFX)
- Use `-fx-` prefix for all properties
- Define variables in base stylesheet
- Theme-specific overrides in separate files
- Component styles in `components.css`

### Localization
- All user-visible strings in `messages_en.properties`
- Use `ResourceBundle` for loading
- Key format: `category.subcategory.element`
- Example: `settings.export.title`

---

## Error Handling

Use `ErrorCatalog` and `ErrorHandler` for consistent error display:

```java
// Showing an error
ErrorHandler.showError(exception, ErrorCatalog.FILE_NOT_FOUND);

// Showing info/success
ErrorHandler.showInfo(title, message);
```

### Error Categories
- `GENERIC_ERROR` - Unexpected errors
- `FILE_NOT_FOUND` - Missing files
- `PERMISSION_DENIED` - Access issues
- `FFMPEG_NOT_FOUND` - Missing FFmpeg
- `CONVERSION_FAILED` - Encoding errors
- `INVALID_FILENAME_FORMAT` - Bad templates
- `SETTINGS_EXPORT_FAILED` - Export errors
- `SETTINGS_IMPORT_FAILED` - Import errors

---

## Build Commands

```bash
# Compile
mvn clean compile

# Package (skip tests)
mvn package -DskipTests

# Run tests
mvn test

# Run application (development)
./run-dev.sh  # or mvn javafx:run
```

---

## References

- [JavaFX CSS Reference](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/cssref.html)
- [Microsoft Fluent Design](https://fluent2.microsoft.design/)
- [Fluent UI Icons](https://github.com/microsoft/fluentui-system-icons)
- [Inter Font](https://rsms.me/inter/)
- [FFmpeg Documentation](https://ffmpeg.org/documentation.html)

