package com.google.android.material.carousel;

import android.content.Context;
import com.google.android.material.R;
import com.google.android.material.carousel.KeylineState;

final class CarouselStrategyHelper {
    private CarouselStrategyHelper() {
    }

    static float getExtraSmallSize(Context context) {
        return context.getResources().getDimension(R.dimen.m3_carousel_gone_size);
    }

    static float getSmallSizeMin(Context context) {
        return context.getResources().getDimension(R.dimen.m3_carousel_small_item_size_min);
    }

    static float getSmallSizeMax(Context context) {
        return context.getResources().getDimension(R.dimen.m3_carousel_small_item_size_max);
    }

    static KeylineState createLeftAlignedKeylineState(Context context, float childHorizontalMargins, float availableSpace, Arrangement arrangement) {
        float f = childHorizontalMargins;
        Arrangement arrangement2 = arrangement;
        float extraSmallChildWidth = getExtraSmallSize(context) + f;
        float extraSmallHeadCenterX = 0.0f - (extraSmallChildWidth / 2.0f);
        float largeStartCenterX = (arrangement2.largeSize / 2.0f) + 0.0f;
        float largeEndCenterX = largeStartCenterX + (((float) Math.max(0, arrangement2.largeCount - 1)) * arrangement2.largeSize);
        float start = (arrangement2.largeSize / 2.0f) + largeEndCenterX;
        float mediumCenterX = arrangement2.mediumCount > 0 ? (arrangement2.mediumSize / 2.0f) + start : largeEndCenterX;
        float smallStartCenterX = arrangement2.smallCount > 0 ? (arrangement2.smallSize / 2.0f) + (arrangement2.mediumCount > 0 ? (arrangement2.mediumSize / 2.0f) + mediumCenterX : start) : mediumCenterX;
        float extraSmallTailCenterX = availableSpace + (extraSmallChildWidth / 2.0f);
        float extraSmallMask = CarouselStrategy.getChildMaskPercentage(extraSmallChildWidth, arrangement2.largeSize, f);
        float smallMask = CarouselStrategy.getChildMaskPercentage(arrangement2.smallSize, arrangement2.largeSize, f);
        float mediumMask = CarouselStrategy.getChildMaskPercentage(arrangement2.mediumSize, arrangement2.largeSize, f);
        float smallMask2 = smallMask;
        float f2 = extraSmallHeadCenterX;
        float extraSmallMask2 = extraSmallMask;
        KeylineState.Builder builder = new KeylineState.Builder(arrangement2.largeSize).addKeyline(extraSmallHeadCenterX, extraSmallMask, extraSmallChildWidth).addKeylineRange(largeStartCenterX, 0.0f, arrangement2.largeSize, arrangement2.largeCount, true);
        if (arrangement2.mediumCount > 0) {
            builder.addKeyline(mediumCenterX, mediumMask, arrangement2.mediumSize);
        }
        if (arrangement2.smallCount > 0) {
            builder.addKeylineRange(smallStartCenterX, smallMask2, arrangement2.smallSize, arrangement2.smallCount);
        }
        builder.addKeyline(extraSmallTailCenterX, extraSmallMask2, extraSmallChildWidth);
        return builder.build();
    }

    static int maxValue(int[] array) {
        int largest = Integer.MIN_VALUE;
        for (int j : array) {
            if (j > largest) {
                largest = j;
            }
        }
        return largest;
    }
}
