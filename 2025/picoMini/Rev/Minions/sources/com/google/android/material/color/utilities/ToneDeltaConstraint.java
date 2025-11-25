package com.google.android.material.color.utilities;

public final class ToneDeltaConstraint {
    public final double delta;
    public final DynamicColor keepAway;
    public final TonePolarity keepAwayPolarity;

    public ToneDeltaConstraint(double delta2, DynamicColor keepAway2, TonePolarity keepAwayPolarity2) {
        this.delta = delta2;
        this.keepAway = keepAway2;
        this.keepAwayPolarity = keepAwayPolarity2;
    }
}
