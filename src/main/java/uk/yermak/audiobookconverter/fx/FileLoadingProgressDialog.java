package uk.yermak.audiobookconverter.fx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import uk.yermak.audiobookconverter.Settings;

import java.io.File;

/**
 * A popup dialog that displays progress while files are being loaded into the application.
 * Shows a progress bar and status text indicating how many files have been processed.
 */
public class FileLoadingProgressDialog {
    
    private final Stage stage;
    private final ProgressBar progressBar;
    private final Label statusLabel;
    private final Label fileNameLabel;
    
    private int totalFiles = 0;
    private int processedFiles = 0;

    /**
     * Creates a new file loading progress dialog.
     *
     * @param owner the parent window
     */
    public FileLoadingProgressDialog(Window owner) {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Loading Files");
        stage.setResizable(false);

        // Create UI components
        Label titleLabel = new Label("Loading Files");
        titleLabel.getStyleClass().addAll("text-subtitle", "text-bold");
        
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        
        statusLabel = new Label("Preparing...");
        statusLabel.getStyleClass().add("text-secondary");
        
        fileNameLabel = new Label("");
        fileNameLabel.getStyleClass().add("text-caption");
        fileNameLabel.setWrapText(true);
        fileNameLabel.setMaxWidth(350);
        
        // Layout
        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(24));
        content.getStyleClass().addAll("card", "file-loading-dialog");
        content.getChildren().addAll(titleLabel, progressBar, statusLabel, fileNameLabel);
        
        Scene scene = new Scene(content);
        applyStylesheets(scene);
        
        stage.setScene(scene);
        stage.sizeToScene();
    }
    
    /**
     * Applies the current theme stylesheets to the scene.
     */
    private void applyStylesheets(Scene scene) {
        scene.getStylesheets().clear();
        
        boolean darkMode = isDarkMode();
        
        // Add base styles
        var baseResource = getClass().getResource("/styles/fluent-base.css");
        if (baseResource != null) {
            scene.getStylesheets().add(baseResource.toExternalForm());
        }
        
        // Add theme-specific styles
        String themePath = darkMode ? "/styles/fluent-dark.css" : "/styles/fluent-light.css";
        var themeResource = getClass().getResource(themePath);
        if (themeResource != null) {
            scene.getStylesheets().add(themeResource.toExternalForm());
        }
        
        // Add component styles
        var componentsResource = getClass().getResource("/styles/components.css");
        if (componentsResource != null) {
            scene.getStylesheets().add(componentsResource.toExternalForm());
        }
    }
    
    /**
     * Checks if dark mode is currently active.
     */
    private boolean isDarkMode() {
        try {
            return Settings.loadSetting().isDarkMode();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Shows the progress dialog.
     */
    public void show() {
        Platform.runLater(() -> {
            stage.show();
            stage.centerOnScreen();
        });
    }

    /**
     * Closes the progress dialog.
     */
    public void close() {
        Platform.runLater(stage::close);
    }

    /**
     * Sets the total number of files to be processed.
     *
     * @param total the total number of files
     */
    public void setTotalFiles(int total) {
        this.totalFiles = total;
        this.processedFiles = 0;
        Platform.runLater(() -> {
            progressBar.setProgress(0);
            statusLabel.setText("Processing 0 of " + total + " files...");
        });
    }

    /**
     * Updates progress when a file has been processed.
     *
     * @param fileName the name of the file that was processed
     */
    public void fileProcessed(String fileName) {
        processedFiles++;
        final int current = processedFiles;
        final double progress = totalFiles > 0 ? (double) current / totalFiles : 0;
        
        Platform.runLater(() -> {
            progressBar.setProgress(progress);
            statusLabel.setText("Processing " + current + " of " + totalFiles + " files...");
            
            // Show just the filename, not the full path
            String displayName = new File(fileName).getName();
            if (displayName.length() > 50) {
                displayName = displayName.substring(0, 47) + "...";
            }
            fileNameLabel.setText(displayName);
        });
    }

    /**
     * Updates the status to show completion.
     */
    public void setComplete() {
        Platform.runLater(() -> {
            progressBar.setProgress(1.0);
            statusLabel.setText("Loading complete!");
            fileNameLabel.setText("");
        });
    }

    /**
     * Updates the status to show an error.
     *
     * @param message the error message
     */
    public void setError(String message) {
        Platform.runLater(() -> {
            statusLabel.setText("Error: " + message);
            statusLabel.getStyleClass().add("text-error");
        });
    }

    /**
     * Checks if the dialog is currently showing.
     *
     * @return true if showing
     */
    public boolean isShowing() {
        return stage.isShowing();
    }
}

