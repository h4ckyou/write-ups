package com.google.android.material.carousel;

import androidx.core.math.MathUtils;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.carousel.KeylineState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class KeylineStateList {
    private static final int NO_INDEX = -1;
    private final KeylineState defaultState;
    private final float endShiftRange;
    private final List<KeylineState> endStateSteps;
    private final float[] endStateStepsInterpolationPoints;
    private final float startShiftRange;
    private final List<KeylineState> startStateSteps;
    private final float[] startStateStepsInterpolationPoints;

    private KeylineStateList(KeylineState defaultState2, List<KeylineState> startStateSteps2, List<KeylineState> endStateSteps2) {
        this.defaultState = defaultState2;
        this.startStateSteps = Collections.unmodifiableList(startStateSteps2);
        this.endStateSteps = Collections.unmodifiableList(endStateSteps2);
        this.startShiftRange = startStateSteps2.get(startStateSteps2.size() - 1).getFirstKeyline().loc - defaultState2.getFirstKeyline().loc;
        this.endShiftRange = defaultState2.getLastKeyline().loc - endStateSteps2.get(endStateSteps2.size() - 1).getLastKeyline().loc;
        this.startStateStepsInterpolationPoints = getStateStepInterpolationPoints(this.startShiftRange, startStateSteps2, true);
        this.endStateStepsInterpolationPoints = getStateStepInterpolationPoints(this.endShiftRange, endStateSteps2, false);
    }

    static KeylineStateList from(Carousel carousel, KeylineState state) {
        return new KeylineStateList(state, getStateStepsStart(state), getStateStepsEnd(carousel, state));
    }

    /* access modifiers changed from: package-private */
    public KeylineState getDefaultState() {
        return this.defaultState;
    }

    /* access modifiers changed from: package-private */
    public KeylineState getStartState() {
        return this.startStateSteps.get(this.startStateSteps.size() - 1);
    }

    /* access modifiers changed from: package-private */
    public KeylineState getEndState() {
        return this.endStateSteps.get(this.endStateSteps.size() - 1);
    }

    public KeylineState getShiftedState(float scrollOffset, float minScrollOffset, float maxScrollOffset) {
        return getShiftedState(scrollOffset, minScrollOffset, maxScrollOffset, false);
    }

    /* access modifiers changed from: package-private */
    public KeylineState getShiftedState(float scrollOffset, float minScrollOffset, float maxScrollOffset, boolean roundToNearestStep) {
        float[] interpolationPoints;
        List<KeylineState> steps;
        float interpolation;
        float startShiftOffset = this.startShiftRange + minScrollOffset;
        float endShiftOffset = maxScrollOffset - this.endShiftRange;
        if (scrollOffset < startShiftOffset) {
            interpolation = AnimationUtils.lerp(1.0f, 0.0f, minScrollOffset, startShiftOffset, scrollOffset);
            steps = this.startStateSteps;
            interpolationPoints = this.startStateStepsInterpolationPoints;
        } else if (scrollOffset <= endShiftOffset) {
            return this.defaultState;
        } else {
            interpolation = AnimationUtils.lerp(0.0f, 1.0f, endShiftOffset, maxScrollOffset, scrollOffset);
            steps = this.endStateSteps;
            interpolationPoints = this.endStateStepsInterpolationPoints;
        }
        if (roundToNearestStep) {
            return closestStateStepFromInterpolation(steps, interpolation, interpolationPoints);
        }
        return lerp(steps, interpolation, interpolationPoints);
    }

    private static KeylineState lerp(List<KeylineState> stateSteps, float interpolation, float[] stateStepsInterpolationPoints) {
        float[] stateStepsRange = getStateStepsRange(stateSteps, interpolation, stateStepsInterpolationPoints);
        return KeylineState.lerp(stateSteps.get((int) stateStepsRange[1]), stateSteps.get((int) stateStepsRange[2]), stateStepsRange[0]);
    }

    private static float[] getStateStepsRange(List<KeylineState> stateSteps, float interpolation, float[] stateStepsInterpolationPoints) {
        int numberOfSteps = stateSteps.size();
        float lowerBounds = stateStepsInterpolationPoints[0];
        for (int i = 1; i < numberOfSteps; i++) {
            float upperBounds = stateStepsInterpolationPoints[i];
            if (interpolation <= upperBounds) {
                return new float[]{AnimationUtils.lerp(0.0f, 1.0f, lowerBounds, upperBounds, interpolation), (float) (i - 1), (float) i};
            }
            lowerBounds = upperBounds;
        }
        return new float[]{0.0f, 0.0f, 0.0f};
    }

    private KeylineState closestStateStepFromInterpolation(List<KeylineState> stateSteps, float interpolation, float[] stateStepsInterpolationPoints) {
        float[] stateStepsRange = getStateStepsRange(stateSteps, interpolation, stateStepsInterpolationPoints);
        if (stateStepsRange[0] > 0.5f) {
            return stateSteps.get((int) stateStepsRange[2]);
        }
        return stateSteps.get((int) stateStepsRange[1]);
    }

    private static float[] getStateStepInterpolationPoints(float shiftRange, List<KeylineState> stateSteps, boolean isShiftingLeft) {
        float distanceShifted;
        int numberOfSteps = stateSteps.size();
        float[] stateStepsInterpolationPoints = new float[numberOfSteps];
        int i = 1;
        while (i < numberOfSteps) {
            KeylineState prevState = stateSteps.get(i - 1);
            KeylineState currState = stateSteps.get(i);
            if (isShiftingLeft) {
                distanceShifted = currState.getFirstKeyline().loc - prevState.getFirstKeyline().loc;
            } else {
                distanceShifted = prevState.getLastKeyline().loc - currState.getLastKeyline().loc;
            }
            stateStepsInterpolationPoints[i] = i == numberOfSteps + -1 ? 1.0f : stateStepsInterpolationPoints[i - 1] + (distanceShifted / shiftRange);
            i++;
        }
        return stateStepsInterpolationPoints;
    }

    private static boolean isFirstFocalItemAtLeftOfContainer(KeylineState state) {
        return state.getFirstFocalKeyline().locOffset - (state.getFirstFocalKeyline().maskedItemSize / 2.0f) <= 0.0f || state.getFirstFocalKeyline() == state.getFirstKeyline();
    }

    private static List<KeylineState> getStateStepsStart(KeylineState defaultState2) {
        List<KeylineState> steps = new ArrayList<>();
        steps.add(defaultState2);
        int firstInBoundsKeylineIndex = findFirstInBoundsKeylineIndex(defaultState2);
        if (isFirstFocalItemAtLeftOfContainer(defaultState2) || firstInBoundsKeylineIndex == -1) {
            return steps;
        }
        int start = firstInBoundsKeylineIndex;
        int numberOfSteps = (defaultState2.getFirstFocalKeylineIndex() - 1) - start;
        float originalStart = defaultState2.getFirstKeyline().locOffset - (defaultState2.getFirstKeyline().maskedItemSize / 2.0f);
        for (int i = 0; i <= numberOfSteps; i++) {
            KeylineState prevStepState = steps.get(steps.size() - 1);
            int itemOrigIndex = start + i;
            int dstIndex = defaultState2.getKeylines().size() - 1;
            if (itemOrigIndex - 1 >= 0) {
                dstIndex = findFirstIndexAfterLastFocalKeylineWithMask(prevStepState, defaultState2.getKeylines().get(itemOrigIndex - 1).mask) - 1;
            }
            steps.add(moveKeylineAndCreateKeylineState(prevStepState, firstInBoundsKeylineIndex, dstIndex, originalStart, (defaultState2.getFirstFocalKeylineIndex() - i) - 1, (defaultState2.getLastFocalKeylineIndex() - i) - 1));
        }
        return steps;
    }

    private static boolean isLastFocalItemAtRightOfContainer(Carousel carousel, KeylineState state) {
        int containerSize = carousel.getContainerHeight();
        if (carousel.isHorizontal()) {
            containerSize = carousel.getContainerWidth();
        }
        return state.getLastFocalKeyline().locOffset + (state.getLastFocalKeyline().maskedItemSize / 2.0f) >= ((float) containerSize) || state.getLastFocalKeyline() == state.getLastKeyline();
    }

    private static List<KeylineState> getStateStepsEnd(Carousel carousel, KeylineState defaultState2) {
        List<KeylineState> steps = new ArrayList<>();
        steps.add(defaultState2);
        int lastInBoundsKeylineIndex = findLastInBoundsKeylineIndex(carousel, defaultState2);
        if (isLastFocalItemAtRightOfContainer(carousel, defaultState2) || lastInBoundsKeylineIndex == -1) {
            return steps;
        }
        int end = lastInBoundsKeylineIndex;
        int numberOfSteps = end - defaultState2.getLastFocalKeylineIndex();
        float originalStart = defaultState2.getFirstKeyline().locOffset - (defaultState2.getFirstKeyline().maskedItemSize / 2.0f);
        for (int i = 0; i < numberOfSteps; i++) {
            KeylineState prevStepState = steps.get(steps.size() - 1);
            int itemOrigIndex = end - i;
            int dstIndex = 0;
            if (itemOrigIndex + 1 < defaultState2.getKeylines().size()) {
                dstIndex = findLastIndexBeforeFirstFocalKeylineWithMask(prevStepState, defaultState2.getKeylines().get(itemOrigIndex + 1).mask) + 1;
            }
            steps.add(moveKeylineAndCreateKeylineState(prevStepState, lastInBoundsKeylineIndex, dstIndex, originalStart, defaultState2.getFirstFocalKeylineIndex() + i + 1, defaultState2.getLastFocalKeylineIndex() + i + 1));
        }
        return steps;
    }

    private static KeylineState moveKeylineAndCreateKeylineState(KeylineState state, int keylineSrcIndex, int keylineDstIndex, float startOffset, int newFirstFocalIndex, int newLastFocalIndex) {
        List<KeylineState.Keyline> tmpKeylines = new ArrayList<>(state.getKeylines());
        tmpKeylines.add(keylineDstIndex, tmpKeylines.remove(keylineSrcIndex));
        KeylineState.Builder builder = new KeylineState.Builder(state.getItemSize());
        int j = 0;
        while (j < tmpKeylines.size()) {
            KeylineState.Keyline k = tmpKeylines.get(j);
            builder.addKeyline((k.maskedItemSize / 2.0f) + startOffset, k.mask, k.maskedItemSize, j >= newFirstFocalIndex && j <= newLastFocalIndex);
            startOffset += k.maskedItemSize;
            j++;
        }
        return builder.build();
    }

    private static int findFirstIndexAfterLastFocalKeylineWithMask(KeylineState state, float mask) {
        for (int i = state.getLastFocalKeylineIndex(); i < state.getKeylines().size(); i++) {
            if (mask == state.getKeylines().get(i).mask) {
                return i;
            }
        }
        return state.getKeylines().size() - 1;
    }

    private static int findLastIndexBeforeFirstFocalKeylineWithMask(KeylineState state, float mask) {
        for (int i = state.getFirstFocalKeylineIndex() - 1; i >= 0; i--) {
            if (mask == state.getKeylines().get(i).mask) {
                return i;
            }
        }
        return 0;
    }

    private static int findFirstInBoundsKeylineIndex(KeylineState state) {
        for (int i = 0; i < state.getKeylines().size(); i++) {
            if (state.getKeylines().get(i).locOffset >= 0.0f) {
                return i;
            }
        }
        return -1;
    }

    private static int findLastInBoundsKeylineIndex(Carousel carousel, KeylineState state) {
        int containerSize = carousel.getContainerHeight();
        if (carousel.isHorizontal()) {
            containerSize = carousel.getContainerWidth();
        }
        for (int i = state.getKeylines().size() - 1; i >= 0; i--) {
            if (state.getKeylines().get(i).locOffset <= ((float) containerSize)) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public Map<Integer, KeylineState> getKeylineStateForPositionMap(int itemCount, int minHorizontalScroll, int maxHorizontalScroll, boolean isRTL) {
        int i = itemCount;
        float itemSize = this.defaultState.getItemSize();
        Map<Integer, KeylineState> keylineStates = new HashMap<>();
        int endStepsIndex = 0;
        int startStepsIndex = 0;
        for (int i2 = 0; i2 < i; i2++) {
            int position = isRTL ? (i - i2) - 1 : i2;
            if (((float) position) * itemSize * ((float) (isRTL ? -1 : 1)) > ((float) maxHorizontalScroll) - this.endShiftRange || i2 >= i - this.endStateSteps.size()) {
                keylineStates.put(Integer.valueOf(position), this.endStateSteps.get(MathUtils.clamp(endStepsIndex, 0, this.endStateSteps.size() - 1)));
                endStepsIndex++;
            }
        }
        int i3 = maxHorizontalScroll;
        for (int i4 = i - 1; i4 >= 0; i4--) {
            int position2 = isRTL ? (i - i4) - 1 : i4;
            if (((float) position2) * itemSize * ((float) (isRTL ? -1 : 1)) < ((float) minHorizontalScroll) + this.startShiftRange || i4 < this.startStateSteps.size()) {
                keylineStates.put(Integer.valueOf(position2), this.startStateSteps.get(MathUtils.clamp(startStepsIndex, 0, this.startStateSteps.size() - 1)));
                startStepsIndex++;
            }
        }
        int i5 = minHorizontalScroll;
        return keylineStates;
    }
}
