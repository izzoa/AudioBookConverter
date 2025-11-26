package uk.yermak.audiobookconverter.fx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.yermak.audiobookconverter.AudiobookConverter;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

/**
 * Handles displaying user-friendly error messages with recovery options.
 */
public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Shows a user-friendly error dialog.
     */
    public static void showError(ErrorCatalog.ErrorCode code, String technicalDetails) {
        ErrorCatalog.ErrorInfo info = ErrorCatalog.fromCode(code, technicalDetails);
        showErrorDialog(info, null, null);
    }

    /**
     * Shows a user-friendly error dialog for an exception.
     */
    public static void showError(Exception e) {
        logger.error("Error occurred", e);
        ErrorCatalog.ErrorInfo info = ErrorCatalog.fromException(e);
        showErrorDialog(info, null, null);
    }

    /**
     * Shows an error dialog with a retry option.
     */
    public static void showErrorWithRetry(ErrorCatalog.ErrorCode code, String technicalDetails, Runnable retryAction) {
        ErrorCatalog.ErrorInfo info = ErrorCatalog.fromCode(code, technicalDetails);
        showErrorDialog(info, retryAction, null);
    }

    /**
     * Shows an error dialog with multiple action options.
     */
    public static void showErrorWithOptions(ErrorCatalog.ErrorCode code, String technicalDetails, Map<String, Runnable> options) {
        ErrorCatalog.ErrorInfo info = ErrorCatalog.fromCode(code, technicalDetails);
        showErrorDialog(info, null, options);
    }

    /**
     * Shows the error dialog on the JavaFX application thread.
     */
    private static void showErrorDialog(ErrorCatalog.ErrorInfo info, Runnable retryAction, Map<String, Runnable> options) {
        Runnable showDialog = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(info.code().getTitle());
            alert.setHeaderText(info.userMessage());

            // Create expandable content
            VBox content = new VBox(10);
            content.setPadding(new Insets(10));

            // Suggestions section
            if (info.suggestions() != null && !info.suggestions().isEmpty()) {
                Label suggestionsLabel = new Label("Suggestions:");
                suggestionsLabel.setStyle("-fx-font-weight: bold;");
                
                TextArea suggestionsArea = new TextArea(info.suggestions());
                suggestionsArea.setEditable(false);
                suggestionsArea.setWrapText(true);
                suggestionsArea.setPrefRowCount(4);
                suggestionsArea.setMaxHeight(100);
                
                content.getChildren().addAll(suggestionsLabel, suggestionsArea);
            }

            // Technical details section (expandable)
            if (info.technicalDetails() != null && !info.technicalDetails().isEmpty()) {
                TitledPane detailsPane = new TitledPane();
                detailsPane.setText("Technical Details (click to expand)");
                detailsPane.setExpanded(false);
                
                TextArea detailsArea = new TextArea(info.technicalDetails());
                detailsArea.setEditable(false);
                detailsArea.setWrapText(true);
                detailsArea.setPrefRowCount(6);
                VBox.setVgrow(detailsArea, Priority.ALWAYS);
                
                detailsPane.setContent(detailsArea);
                content.getChildren().add(detailsPane);
            }

            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setMinWidth(500);
            alert.getDialogPane().setMinHeight(300);
            
            // Apply Fluent Design styling
            DialogStyleHelper.styleAlert(alert);

            // Configure buttons
            alert.getButtonTypes().clear();
            
            if (retryAction != null) {
                ButtonType retryButton = new ButtonType("Retry", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().add(retryButton);
            }
            
            if (options != null && !options.isEmpty()) {
                for (String optionName : options.keySet()) {
                    ButtonType optionButton = new ButtonType(optionName, ButtonBar.ButtonData.OTHER);
                    alert.getButtonTypes().add(optionButton);
                }
            }
            
            // Add Copy Details button
            ButtonType copyButton = new ButtonType("Copy Details", ButtonBar.ButtonData.LEFT);
            alert.getButtonTypes().add(copyButton);
            
            // Add Help button if URL available
            if (info.hasHelpUrl()) {
                ButtonType helpButton = new ButtonType("Help", ButtonBar.ButtonData.HELP);
                alert.getButtonTypes().add(helpButton);
            }
            
            // Add Close button
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().add(closeButton);

            // Handle button clicks
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                ButtonType clicked = result.get();
                
                if (clicked.getText().equals("Retry") && retryAction != null) {
                    retryAction.run();
                } else if (clicked.getText().equals("Copy Details")) {
                    copyToClipboard(formatErrorReport(info));
                } else if (clicked.getText().equals("Help") && info.hasHelpUrl()) {
                    openHelpUrl(info.helpUrl());
                } else if (options != null && options.containsKey(clicked.getText())) {
                    options.get(clicked.getText()).run();
                }
            }
        };

        if (Platform.isFxApplicationThread()) {
            showDialog.run();
        } else {
            Platform.runLater(showDialog);
        }
    }

    /**
     * Formats error information for copying to clipboard.
     */
    private static String formatErrorReport(ErrorCatalog.ErrorInfo info) {
        StringBuilder report = new StringBuilder();
        report.append("=== AudioBookConverter Error Report ===\n\n");
        report.append("Error Type: ").append(info.code().name()).append("\n");
        report.append("Title: ").append(info.code().getTitle()).append("\n");
        report.append("Message: ").append(info.userMessage()).append("\n\n");
        report.append("Technical Details:\n").append(info.technicalDetails()).append("\n\n");
        report.append("Suggestions:\n").append(info.suggestions()).append("\n");
        if (info.hasHelpUrl()) {
            report.append("\nHelp URL: ").append(info.helpUrl()).append("\n");
        }
        return report.toString();
    }

    /**
     * Copies text to the system clipboard.
     */
    private static void copyToClipboard(String text) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(text), null);
            
            // Show brief confirmation
            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.setTitle("Copied");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Error details copied to clipboard.");
            DialogStyleHelper.styleAlert(confirmation);
            confirmation.show();
            
            // Auto-close after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(confirmation::close);
                } catch (InterruptedException ignored) {}
            }).start();
            
        } catch (Exception e) {
            logger.error("Failed to copy to clipboard", e);
        }
    }

    /**
     * Opens the help URL in the default browser.
     */
    private static void openHelpUrl(String url) {
        try {
            if (AudiobookConverter.getEnv() != null) {
                AudiobookConverter.getEnv().showDocument(url);
            }
        } catch (Exception e) {
            logger.error("Failed to open help URL", e);
        }
    }

    /**
     * Shows a simple error message (for backward compatibility with existing code).
     */
    public static void showSimpleError(String title, String message) {
        Runnable showDialog = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            DialogStyleHelper.styleAlert(alert);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            showDialog.run();
        } else {
            Platform.runLater(showDialog);
        }
    }
}

