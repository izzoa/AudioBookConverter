package uk.yermak.audiobookconverter.fx;

import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.input.Mnemonic;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Objects;

/**
 * JavaFX environment manager for handling scene, styles, and services.
 * Manages theme switching and stylesheet loading.
 */
public class JfxEnv {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    // Stylesheet paths
    private static final String STYLES_BASE = "/styles/fluent-base.css";
    private static final String STYLES_LIGHT = "/styles/fluent-light.css";
    private static final String STYLES_DARK = "/styles/fluent-dark.css";
    private static final String STYLES_COMPONENTS = "/styles/components.css";

    private final Scene scene;
    private final HostServices hostServices;
    private boolean darkMode = false;

    public JfxEnv(Scene scene, HostServices hostServices) {
        this.scene = scene;
        this.hostServices = hostServices;
    }

    /**
     * Loads the initial stylesheets for the application.
     * Should be called once after scene creation.
     * 
     * @param darkMode true to load dark theme, false for light theme
     */
    public void loadStylesheets(boolean darkMode) {
        this.darkMode = darkMode;
        scene.getStylesheets().clear();
        
        // Load base styles (variables, utilities)
        addStylesheet(STYLES_BASE);
        
        // Load theme-specific styles
        if (darkMode) {
            addStylesheet(STYLES_DARK);
        } else {
            addStylesheet(STYLES_LIGHT);
        }
        
        // Load component styles
        addStylesheet(STYLES_COMPONENTS);
        
        logger.info("Loaded {} theme stylesheets", darkMode ? "dark" : "light");
    }

    /**
     * Sets the dark mode and reloads stylesheets.
     * 
     * @param darkMode true for dark theme, false for light theme
     */
    public void setDarkMode(boolean darkMode) {
        if (this.darkMode != darkMode) {
            loadStylesheets(darkMode);
        }
    }
    
    /**
     * Returns whether dark mode is currently active.
     * 
     * @return true if dark mode is active
     */
    public boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Adds a stylesheet to the scene if it exists.
     * 
     * @param path the resource path to the CSS file
     */
    private void addStylesheet(String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
                logger.debug("Loaded stylesheet: {}", path);
            } else {
                logger.warn("Stylesheet not found: {}", path);
            }
        } catch (Exception e) {
            logger.error("Failed to load stylesheet: {}", path, e);
        }
    }

    /**
     * Gets the main application window.
     * 
     * @return the window
     */
    public Window getWindow() {
        return scene.getWindow();
    }

    /**
     * Opens a URL in the default browser.
     * 
     * @param url the URL to open
     */
    public void showDocument(String url) {
        hostServices.showDocument(url);
    }

    /**
     * Adds a keyboard mnemonic to the scene.
     * 
     * @param mnemonic the mnemonic to add
     */
    public void addMnemonic(Mnemonic mnemonic) {
        scene.addMnemonic(mnemonic);
    }
    
    /**
     * Gets the current scene.
     * 
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }
}
