package com.google.android.material.carousel;

import android.view.View;

public abstract class CarouselStrategy {
    /* access modifiers changed from: package-private */
    public abstract KeylineState onFirstChildMeasuredWithMargins(Carousel carousel, View view);

    static float getChildMaskPercentage(float maskedSize, float unmaskedSize, float childMargins) {
        return 1.0f - ((maskedSize - childMargins) / (unmaskedSize - childMargins));
    }

    /* access modifiers changed from: package-private */
    public boolean isContained() {
        return true;
    }
}
