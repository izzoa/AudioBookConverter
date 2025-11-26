package uk.yermak.audiobookconverter.fx;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import uk.yermak.audiobookconverter.AudiobookConverter;
import uk.yermak.audiobookconverter.Settings;

import java.net.URL;

/**
 * Utility class to apply Fluent Design stylesheets to dialogs.
 * Ensures consistent styling across all application dialogs.
 */
public class DialogStyleHelper {
    
    private static final String STYLES_BASE = "/styles/fluent-base.css";
    private static final String STYLES_LIGHT = "/styles/fluent-light.css";
    private static final String STYLES_DARK = "/styles/fluent-dark.css";
    private static final String STYLES_COMPONENTS = "/styles/components.css";

    /**
     * Applies the current theme stylesheets to a Dialog.
     * 
     * @param dialog the dialog to style
     */
    public static void styleDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        applyStylesheets(dialogPane);
    }

    /**
     * Applies the current theme stylesheets to an Alert.
     * 
     * @param alert the alert to style
     */
    public static void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        applyStylesheets(dialogPane);
    }

    /**
     * Applies the current theme stylesheets to a DialogPane.
     * 
     * @param dialogPane the dialog pane to style
     */
    public static void applyStylesheets(DialogPane dialogPane) {
        dialogPane.getStylesheets().clear();
        
        boolean darkMode = isDarkMode();
        
        // Add base styles
        addStylesheet(dialogPane, STYLES_BASE);
        
        // Add theme-specific styles
        if (darkMode) {
            addStylesheet(dialogPane, STYLES_DARK);
        } else {
            addStylesheet(dialogPane, STYLES_LIGHT);
        }
        
        // Add component styles
        addStylesheet(dialogPane, STYLES_COMPONENTS);
    }

    /**
     * Adds a single stylesheet to a DialogPane.
     * 
     * @param dialogPane the dialog pane
     * @param path the resource path to the CSS file
     */
    private static void addStylesheet(DialogPane dialogPane, String path) {
        URL resource = DialogStyleHelper.class.getResource(path);
        if (resource != null) {
            dialogPane.getStylesheets().add(resource.toExternalForm());
        }
    }

    /**
     * Checks if dark mode is currently active.
     * 
     * @return true if dark mode is active
     */
    private static boolean isDarkMode() {
        try {
            JfxEnv env = AudiobookConverter.getEnv();
            if (env != null) {
                return env.isDarkMode();
            }
        } catch (Exception e) {
            // Fall back to settings if env not available
        }
        
        try {
            return Settings.loadSetting().isDarkMode();
        } catch (Exception e) {
            return false;
        }
    }
}

