package uk.yermak.audiobookconverter.fx;

import javafx.scene.Node;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;
import org.kordamp.ikonli.javafx.FontIcon;

public class IconLoader {

    public static Node loadIcon(String name, int size) {
        return loadIcon(name, size, null);
    }

    public static Node loadIcon(String name, int size, String colorClass) {
        Ikon ikon = resolveIkon(name);
        if (ikon == null) {
            // Fallback to a warning icon or null
            ikon = FluentUiRegularMZ.WARNING_24;
        }

        FontIcon icon = new FontIcon(ikon);
        icon.setIconSize(size);

        if (colorClass != null && !colorClass.isEmpty()) {
            icon.getStyleClass().add(colorClass);
        }

        return icon;
    }

    private static Ikon resolveIkon(String name) {
        try {
            // Try to find in AL (A-L)
            return FluentUiRegularAL.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            try {
                // Try to find in MZ (M-Z)
                return FluentUiRegularMZ.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e2) {
                System.err.println("Icon not found: " + name);
                return null;
            }
        }
    }
}
