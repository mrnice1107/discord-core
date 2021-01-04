package de.bloody9.core.exceptions.Feature;

import de.bloody9.core.feature.Feature;
import org.jetbrains.annotations.NotNull;

public class FeatureDisabledException extends FeatureException {
    public FeatureDisabledException(@NotNull Feature feature) {
        super(feature, "Feature is disabled!");
    }
}
