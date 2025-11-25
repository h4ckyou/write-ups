package com.google.android.material.carousel;

import android.view.View;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.RecyclerView;

public class HeroCarouselStrategy extends CarouselStrategy {
    private static final int[] MEDIUM_COUNTS = {0, 1};
    private static final int[] SMALL_COUNTS = {1};

    /* access modifiers changed from: package-private */
    public KeylineState onFirstChildMeasuredWithMargins(Carousel carousel, View child) {
        int availableSpace = carousel.getContainerHeight();
        if (carousel.isHorizontal()) {
            availableSpace = carousel.getContainerWidth();
        }
        RecyclerView.LayoutParams childLayoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
        float childMargins = (float) (childLayoutParams.topMargin + childLayoutParams.bottomMargin);
        float measuredChildSize = (float) (child.getMeasuredWidth() * 2);
        if (carousel.isHorizontal()) {
            childMargins = (float) (childLayoutParams.leftMargin + childLayoutParams.rightMargin);
            measuredChildSize = (float) (child.getMeasuredHeight() * 2);
        }
        float smallChildSizeMin = CarouselStrategyHelper.getSmallSizeMin(child.getContext()) + childMargins;
        float smallChildSizeMax = CarouselStrategyHelper.getSmallSizeMax(child.getContext()) + childMargins;
        float targetLargeChildSize = Math.min(measuredChildSize + childMargins, (float) availableSpace);
        float targetSmallChildSize = MathUtils.clamp((measuredChildSize / 3.0f) + childMargins, CarouselStrategyHelper.getSmallSizeMin(child.getContext()) + childMargins, CarouselStrategyHelper.getSmallSizeMax(child.getContext()) + childMargins);
        float targetMediumChildSize = (targetLargeChildSize + targetSmallChildSize) / 2.0f;
        int largeCountMin = (int) Math.max(1.0d, Math.floor((double) ((((float) availableSpace) - (((float) CarouselStrategyHelper.maxValue(SMALL_COUNTS)) * smallChildSizeMax)) / targetLargeChildSize)));
        int largeCountMax = (int) Math.ceil((double) (((float) availableSpace) / targetLargeChildSize));
        int[] largeCounts = new int[((largeCountMax - largeCountMin) + 1)];
        for (int i = 0; i < largeCounts.length; i++) {
            largeCounts[i] = largeCountMin + i;
        }
        int[] largeCounts2 = largeCounts;
        int i2 = largeCountMax;
        int i3 = largeCountMin;
        return CarouselStrategyHelper.createLeftAlignedKeylineState(child.getContext(), childMargins, (float) availableSpace, Arrangement.findLowestCostArrangement((float) availableSpace, targetSmallChildSize, smallChildSizeMin, smallChildSizeMax, SMALL_COUNTS, targetMediumChildSize, MEDIUM_COUNTS, targetLargeChildSize, largeCounts2));
    }
}
