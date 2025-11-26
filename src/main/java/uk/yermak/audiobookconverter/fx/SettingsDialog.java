package uk.yermak.audiobookconverter.fx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.yermak.audiobookconverter.Settings;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsDialog extends Dialog<Map<String, Object>> {
    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String FILENAME_FORMAT = "filename_format";
    public static final String PART_FORMAT = "part_format";
    public static final String CHAPTER_FORMAT = "chapter_format";
    public static final String DARK_MODE = "dark_mode";
    public static final String SHOW_HINTS = "show_hints";

    @FXML
    private ToggleSwitch darkMode;
    @FXML
    private TextArea filenameFormat;
    @FXML
    private TextArea partFormat;
    @FXML
    private TextArea chapterFormat;
    @FXML
    private ToggleSwitch showHints;


    public SettingsDialog(Window window) {
        setTitle("AudioBookConverter Settings");
        setHeaderText("Customize AudioBookConverter");
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().setContent(new GridPane());

        // Add validation before closing on OK
        final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            List<String> validationErrors = validateTemplates();
            if (!validationErrors.isEmpty()) {
                event.consume(); // Prevent dialog from closing
                showValidationErrors(validationErrors);
            }
        });

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                HashMap<String, Object> results = new HashMap<>();
                results.put(DARK_MODE, darkMode.isSelected());
                results.put(FILENAME_FORMAT, filenameFormat.getText());
                results.put(PART_FORMAT, partFormat.getText());
                results.put(CHAPTER_FORMAT, chapterFormat.getText());
                results.put(SHOW_HINTS, showHints.isSelected());
                return results;
            }
            return null;
        });
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));

        fxmlLoader.setRoot(getDialogPane().getContent());
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        // Apply Fluent Design styling
        DialogStyleHelper.styleDialog(this);
    }

    /**
     * Validates all template fields and returns a list of errors.
     */
    private List<String> validateTemplates() {
        List<String> errors = new ArrayList<>();

        // Validate filename format
        FilenameValidator.ValidationResult filenameResult = 
                FilenameValidator.validateTemplate(filenameFormat.getText());
        if (!filenameResult.isValid()) {
            errors.add("Filename Format:\n" + filenameResult.getErrorsAsString());
        }

        // Validate part format
        FilenameValidator.ValidationResult partResult = 
                FilenameValidator.validateTemplate(partFormat.getText());
        if (!partResult.isValid()) {
            errors.add("Part Format:\n" + partResult.getErrorsAsString());
        }

        // Validate chapter format (more lenient - allow some special chars for display)
        FilenameValidator.ValidationResult chapterResult = 
                FilenameValidator.validateTemplate(chapterFormat.getText());
        if (chapterResult.hasErrors()) {
            // Only report critical errors for chapter format
            for (String error : chapterResult.errors()) {
                if (error.contains("Unbalanced")) {
                    errors.add("Chapter Format:\n" + error);
                }
            }
        }

        return errors;
    }

    /**
     * Shows validation errors in an alert dialog.
     */
    private void showValidationErrors(List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("Please fix the following issues:");
        alert.setContentText(String.join("\n\n", errors));
        DialogStyleHelper.styleAlert(alert);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        Settings settings = Settings.loadSetting();
        darkMode.setSelected(settings.isDarkMode());
        filenameFormat.setText(settings.getFilenameFormat());
        partFormat.setText(settings.getPartFormat());
        chapterFormat.setText(settings.getChapterFormat());
        showHints.setSelected(settings.isShowHints());
    }

}
