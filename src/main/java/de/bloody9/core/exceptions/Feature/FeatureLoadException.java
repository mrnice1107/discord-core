package de.bloody9.core.exceptions.Feature;

import org.jetbrains.annotations.NotNull;

public class FeatureLoadException extends FeatureException {
    public FeatureLoadException(@NotNull String featureType) {
        super("Failed to load feature: " + featureType);
    }
    public FeatureLoadException() {
        super("Failed to load feature!");
    }
}
