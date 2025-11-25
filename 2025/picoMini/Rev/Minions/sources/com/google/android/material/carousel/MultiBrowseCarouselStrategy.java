package com.google.android.material.carousel;

import android.view.View;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.RecyclerView;

public final class MultiBrowseCarouselStrategy extends CarouselStrategy {
    private static final int[] MEDIUM_COUNTS = {1, 0};
    private static final int[] MEDIUM_COUNTS_COMPACT = {0};
    private static final int[] SMALL_COUNTS = {1};
    private final boolean forceCompactArrangement;

    public MultiBrowseCarouselStrategy() {
        this(false);
    }

    public MultiBrowseCarouselStrategy(boolean forceCompactArrangement2) {
        this.forceCompactArrangement = forceCompactArrangement2;
    }

    /* access modifiers changed from: package-private */
    public KeylineState onFirstChildMeasuredWithMargins(Carousel carousel, View child) {
        float measuredChildSize;
        float childMargins;
        float availableSpace = (float) carousel.getContainerHeight();
        if (carousel.isHorizontal()) {
            availableSpace = (float) carousel.getContainerWidth();
        }
        RecyclerView.LayoutParams childLayoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
        float childMargins2 = (float) (childLayoutParams.topMargin + childLayoutParams.bottomMargin);
        float measuredChildSize2 = (float) child.getMeasuredHeight();
        if (carousel.isHorizontal()) {
            childMargins = (float) (childLayoutParams.leftMargin + childLayoutParams.rightMargin);
            measuredChildSize = (float) child.getMeasuredWidth();
        } else {
            childMargins = childMargins2;
            measuredChildSize = measuredChildSize2;
        }
        float smallChildSizeMin = CarouselStrategyHelper.getSmallSizeMin(child.getContext()) + childMargins;
        float smallChildSizeMax = CarouselStrategyHelper.getSmallSizeMax(child.getContext()) + childMargins;
        float targetLargeChildSize = Math.min(measuredChildSize + childMargins, availableSpace);
        float targetSmallChildSize = MathUtils.clamp((measuredChildSize / 3.0f) + childMargins, CarouselStrategyHelper.getSmallSizeMin(child.getContext()) + childMargins, CarouselStrategyHelper.getSmallSizeMax(child.getContext()) + childMargins);
        float targetMediumChildSize = (targetLargeChildSize + targetSmallChildSize) / 2.0f;
        int[] smallCounts = SMALL_COUNTS;
        int[] mediumCounts = this.forceCompactArrangement ? MEDIUM_COUNTS_COMPACT : MEDIUM_COUNTS;
        int largeCountMin = (int) Math.max(1.0d, Math.floor((double) (((availableSpace - (((float) CarouselStrategyHelper.maxValue(mediumCounts)) * targetMediumChildSize)) - (((float) CarouselStrategyHelper.maxValue(smallCounts)) * smallChildSizeMax)) / targetLargeChildSize)));
        int largeCountMax = (int) Math.ceil((double) (availableSpace / targetLargeChildSize));
        int[] largeCounts = new int[((largeCountMax - largeCountMin) + 1)];
        for (int i = 0; i < largeCounts.length; i++) {
            largeCounts[i] = largeCountMax - i;
        }
        int i2 = largeCountMax;
        int i3 = largeCountMin;
        return CarouselStrategyHelper.createLeftAlignedKeylineState(child.getContext(), childMargins, availableSpace, Arrangement.findLowestCostArrangement(availableSpace, targetSmallChildSize, smallChildSizeMin, smallChildSizeMax, smallCounts, targetMediumChildSize, mediumCounts, targetLargeChildSize, largeCounts));
    }
}
