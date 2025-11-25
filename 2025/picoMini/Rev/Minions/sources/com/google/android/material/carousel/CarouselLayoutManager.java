package com.google.android.material.carousel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.R;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.carousel.KeylineState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CarouselLayoutManager extends RecyclerView.LayoutManager implements Carousel, RecyclerView.SmoothScroller.ScrollVectorProvider {
    public static final int HORIZONTAL = 0;
    private static final String TAG = "CarouselLayoutManager";
    public static final int VERTICAL = 1;
    private CarouselStrategy carouselStrategy;
    private int currentFillStartPosition;
    private KeylineState currentKeylineState;
    private final DebugItemDecoration debugItemDecoration;
    private boolean isDebuggingEnabled;
    /* access modifiers changed from: private */
    public KeylineStateList keylineStateList;
    private Map<Integer, KeylineState> keylineStatePositionMap;
    int maxScroll;
    int minScroll;
    private CarouselOrientationHelper orientationHelper;
    int scrollOffset;

    private static final class ChildCalculations {
        final float center;
        final View child;
        final float offsetCenter;
        final KeylineRange range;

        ChildCalculations(View child2, float center2, float offsetCenter2, KeylineRange range2) {
            this.child = child2;
            this.center = center2;
            this.offsetCenter = offsetCenter2;
            this.range = range2;
        }
    }

    public CarouselLayoutManager() {
        this(new MultiBrowseCarouselStrategy());
    }

    public CarouselLayoutManager(CarouselStrategy strategy) {
        this(strategy, 0);
    }

    public CarouselLayoutManager(CarouselStrategy strategy, int orientation) {
        this.isDebuggingEnabled = false;
        this.debugItemDecoration = new DebugItemDecoration();
        this.currentFillStartPosition = 0;
        setCarouselStrategy(strategy);
        setOrientation(orientation);
    }

    public CarouselLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.isDebuggingEnabled = false;
        this.debugItemDecoration = new DebugItemDecoration();
        this.currentFillStartPosition = 0;
        setOrientation(getProperties(context, attrs, defStyleAttr, defStyleRes).orientation);
        setCarouselStrategy(new MultiBrowseCarouselStrategy());
    }

    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    public void setCarouselStrategy(CarouselStrategy carouselStrategy2) {
        this.carouselStrategy = carouselStrategy2;
        refreshKeylineState();
    }

    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() <= 0) {
            removeAndRecycleAllViews(recycler);
            this.currentFillStartPosition = 0;
            return;
        }
        boolean isRtl = isLayoutRtl();
        boolean isInitialLoad = this.keylineStateList == null;
        if (isInitialLoad) {
            View firstChild = recycler.getViewForPosition(0);
            measureChildWithMargins(firstChild, 0, 0);
            KeylineState keylineState = this.carouselStrategy.onFirstChildMeasuredWithMargins(this, firstChild);
            this.keylineStateList = KeylineStateList.from(this, isRtl ? KeylineState.reverse(keylineState) : keylineState);
        }
        int startScroll = calculateStartScroll(this.keylineStateList);
        int endScroll = calculateEndScroll(state, this.keylineStateList);
        this.minScroll = isRtl ? endScroll : startScroll;
        this.maxScroll = isRtl ? startScroll : endScroll;
        if (isInitialLoad) {
            this.scrollOffset = startScroll;
            this.keylineStatePositionMap = this.keylineStateList.getKeylineStateForPositionMap(getItemCount(), this.minScroll, this.maxScroll, isLayoutRtl());
        } else {
            this.scrollOffset += calculateShouldScrollBy(0, this.scrollOffset, this.minScroll, this.maxScroll);
        }
        this.currentFillStartPosition = MathUtils.clamp(this.currentFillStartPosition, 0, state.getItemCount());
        updateCurrentKeylineStateForScrollOffset();
        detachAndScrapAttachedViews(recycler);
        fill(recycler, state);
    }

    private void refreshKeylineState() {
        this.keylineStateList = null;
        requestLayout();
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        removeAndRecycleOutOfBoundsViews(recycler);
        if (getChildCount() == 0) {
            addViewsStart(recycler, this.currentFillStartPosition - 1);
            addViewsEnd(recycler, state, this.currentFillStartPosition);
        } else {
            int firstPosition = getPosition(getChildAt(0));
            int lastPosition = getPosition(getChildAt(getChildCount() - 1));
            addViewsStart(recycler, firstPosition - 1);
            addViewsEnd(recycler, state, lastPosition + 1);
        }
        validateChildOrderIfDebugging();
    }

    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (getChildCount() == 0) {
            this.currentFillStartPosition = 0;
        } else {
            this.currentFillStartPosition = getPosition(getChildAt(0));
        }
        validateChildOrderIfDebugging();
    }

    private void addViewsStart(RecyclerView.Recycler recycler, int startPosition) {
        int start = calculateChildStartForFill(startPosition);
        int i = startPosition;
        while (i >= 0) {
            ChildCalculations calculations = makeChildCalculations(recycler, (float) start, i);
            if (!isLocOffsetOutOfFillBoundsStart(calculations.offsetCenter, calculations.range)) {
                start = addStart(start, (int) this.currentKeylineState.getItemSize());
                if (!isLocOffsetOutOfFillBoundsEnd(calculations.offsetCenter, calculations.range)) {
                    addAndLayoutView(calculations.child, 0, calculations);
                }
                i--;
            } else {
                return;
            }
        }
    }

    private void addViewsEnd(RecyclerView.Recycler recycler, RecyclerView.State state, int startPosition) {
        int start = calculateChildStartForFill(startPosition);
        int i = startPosition;
        while (i < state.getItemCount()) {
            ChildCalculations calculations = makeChildCalculations(recycler, (float) start, i);
            if (!isLocOffsetOutOfFillBoundsEnd(calculations.offsetCenter, calculations.range)) {
                start = addEnd(start, (int) this.currentKeylineState.getItemSize());
                if (!isLocOffsetOutOfFillBoundsStart(calculations.offsetCenter, calculations.range)) {
                    addAndLayoutView(calculations.child, -1, calculations);
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void logChildrenIfDebugging() {
        if (this.isDebuggingEnabled && Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "internal representation of views on the screen");
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                Log.d(TAG, "item position " + getPosition(child) + ", center:" + getDecoratedCenterXWithMargins(child) + ", child index:" + i);
            }
            Log.d(TAG, "==============");
        }
    }

    private void validateChildOrderIfDebugging() {
        if (this.isDebuggingEnabled && getChildCount() >= 1) {
            int i = 0;
            while (i < getChildCount() - 1) {
                int currPos = getPosition(getChildAt(i));
                int nextPos = getPosition(getChildAt(i + 1));
                if (currPos <= nextPos) {
                    i++;
                } else {
                    logChildrenIfDebugging();
                    throw new IllegalStateException("Detected invalid child order. Child at index [" + i + "] had adapter position [" + currPos + "] and child at index [" + (i + 1) + "] had adapter position [" + nextPos + "].");
                }
            }
        }
    }

    private ChildCalculations makeChildCalculations(RecyclerView.Recycler recycler, float start, int position) {
        View child = recycler.getViewForPosition(position);
        measureChildWithMargins(child, 0, 0);
        int center = addEnd((int) start, (int) (this.currentKeylineState.getItemSize() / 2.0f));
        KeylineRange range = getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), (float) center, false);
        return new ChildCalculations(child, (float) center, calculateChildOffsetCenterForLocation(child, (float) center, range), range);
    }

    private void addAndLayoutView(View child, int index, ChildCalculations calculations) {
        float halfItemSize = this.currentKeylineState.getItemSize() / 2.0f;
        addView(child, index);
        this.orientationHelper.layoutDecoratedWithMargins(child, (int) (calculations.offsetCenter - halfItemSize), (int) (calculations.offsetCenter + halfItemSize));
        updateChildMaskForLocation(child, calculations.center, calculations.range);
    }

    private boolean isLocOffsetOutOfFillBoundsStart(float locOffset, KeylineRange range) {
        int maskedEnd = addEnd((int) locOffset, (int) (getMaskedItemSizeForLocOffset(locOffset, range) / 2.0f));
        if (isLayoutRtl()) {
            if (maskedEnd > getContainerSize()) {
                return true;
            }
        } else if (maskedEnd < 0) {
            return true;
        }
        return false;
    }

    public boolean isHorizontal() {
        return this.orientationHelper.orientation == 0;
    }

    private boolean isLocOffsetOutOfFillBoundsEnd(float locOffset, KeylineRange range) {
        int maskedStart = addStart((int) locOffset, (int) (getMaskedItemSizeForLocOffset(locOffset, range) / 2.0f));
        if (isLayoutRtl()) {
            if (maskedStart < 0) {
                return true;
            }
        } else if (maskedStart > getContainerSize()) {
            return true;
        }
        return false;
    }

    public void getDecoratedBoundsWithMargins(View view, Rect outBounds) {
        super.getDecoratedBoundsWithMargins(view, outBounds);
        float centerX = (float) outBounds.centerX();
        float delta = (((float) outBounds.width()) - getMaskedItemSizeForLocOffset(centerX, getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), centerX, true))) / 2.0f;
        outBounds.set((int) (((float) outBounds.left) + delta), outBounds.top, (int) (((float) outBounds.right) - delta), outBounds.bottom);
    }

    private float getDecoratedCenterXWithMargins(View child) {
        Rect bounds = new Rect();
        super.getDecoratedBoundsWithMargins(child, bounds);
        return (float) bounds.centerX();
    }

    private void removeAndRecycleOutOfBoundsViews(RecyclerView.Recycler recycler) {
        while (getChildCount() > 0) {
            View child = getChildAt(0);
            float centerX = getDecoratedCenterXWithMargins(child);
            if (!isLocOffsetOutOfFillBoundsStart(centerX, getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), centerX, true))) {
                break;
            }
            removeAndRecycleView(child, recycler);
        }
        while (getChildCount() - 1 >= 0) {
            View child2 = getChildAt(getChildCount() - 1);
            float centerX2 = getDecoratedCenterXWithMargins(child2);
            if (isLocOffsetOutOfFillBoundsEnd(centerX2, getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), centerX2, true))) {
                removeAndRecycleView(child2, recycler);
            } else {
                return;
            }
        }
    }

    private static KeylineRange getSurroundingKeylineRange(List<KeylineState.Keyline> keylines, float location, boolean isOffset) {
        int leftMinDistanceIndex = -1;
        float leftMinDistance = Float.MAX_VALUE;
        int leftMostIndex = -1;
        float leftMostX = Float.MAX_VALUE;
        int rightMinDistanceIndex = -1;
        float rightMinDistance = Float.MAX_VALUE;
        int rightMostIndex = -1;
        float rightMostX = -3.4028235E38f;
        for (int i = 0; i < keylines.size(); i++) {
            KeylineState.Keyline keyline = keylines.get(i);
            float currentLoc = isOffset ? keyline.locOffset : keyline.loc;
            float delta = Math.abs(currentLoc - location);
            if (currentLoc <= location && delta <= leftMinDistance) {
                leftMinDistance = delta;
                leftMinDistanceIndex = i;
            }
            if (currentLoc > location && delta <= rightMinDistance) {
                rightMinDistance = delta;
                rightMinDistanceIndex = i;
            }
            if (currentLoc <= leftMostX) {
                leftMostIndex = i;
                leftMostX = currentLoc;
            }
            if (currentLoc > rightMostX) {
                rightMostIndex = i;
                rightMostX = currentLoc;
            }
        }
        if (leftMinDistanceIndex == -1) {
            leftMinDistanceIndex = leftMostIndex;
        }
        if (rightMinDistanceIndex == -1) {
            rightMinDistanceIndex = rightMostIndex;
        }
        return new KeylineRange(keylines.get(leftMinDistanceIndex), keylines.get(rightMinDistanceIndex));
    }

    private void updateCurrentKeylineStateForScrollOffset() {
        if (this.maxScroll <= this.minScroll) {
            this.currentKeylineState = isLayoutRtl() ? this.keylineStateList.getEndState() : this.keylineStateList.getStartState();
        } else {
            this.currentKeylineState = this.keylineStateList.getShiftedState((float) this.scrollOffset, (float) this.minScroll, (float) this.maxScroll);
        }
        this.debugItemDecoration.setKeylines(this.currentKeylineState.getKeylines());
    }

    private static int calculateShouldScrollBy(int delta, int currentScroll, int minScroll2, int maxScroll2) {
        int targetScroll = currentScroll + delta;
        if (targetScroll < minScroll2) {
            return minScroll2 - currentScroll;
        }
        if (targetScroll > maxScroll2) {
            return maxScroll2 - currentScroll;
        }
        return delta;
    }

    private int calculateStartScroll(KeylineStateList stateList) {
        boolean isRtl = isLayoutRtl();
        KeylineState startState = isRtl ? stateList.getEndState() : stateList.getStartState();
        return (int) ((((float) getParentStart()) + ((float) (getPaddingStart() * (isRtl ? 1 : -1)))) - ((float) addStart((int) (isRtl ? startState.getLastFocalKeyline() : startState.getFirstFocalKeyline()).loc, (int) (startState.getItemSize() / 2.0f))));
    }

    private int calculateEndScroll(RecyclerView.State state, KeylineStateList stateList) {
        boolean isRtl = isLayoutRtl();
        KeylineState endState = isRtl ? stateList.getStartState() : stateList.getEndState();
        KeylineState.Keyline endFocalKeyline = isRtl ? endState.getFirstFocalKeyline() : endState.getLastFocalKeyline();
        float lastItemDistanceFromFirstItem = ((((float) (state.getItemCount() - 1)) * endState.getItemSize()) + ((float) getPaddingEnd())) * (isRtl ? -1.0f : 1.0f);
        float endFocalLocDistanceFromStart = endFocalKeyline.loc - ((float) getParentStart());
        float endFocalLocDistanceFromEnd = ((float) getParentEnd()) - endFocalKeyline.loc;
        if (Math.abs(endFocalLocDistanceFromStart) > Math.abs(lastItemDistanceFromFirstItem)) {
            return 0;
        }
        return (int) ((lastItemDistanceFromFirstItem - endFocalLocDistanceFromStart) + endFocalLocDistanceFromEnd);
    }

    private int calculateChildStartForFill(int startPosition) {
        return addEnd((int) ((float) (getParentStart() - this.scrollOffset)), (int) (this.currentKeylineState.getItemSize() * ((float) startPosition)));
    }

    private float calculateChildOffsetCenterForLocation(View child, float childCenterLocation, KeylineRange range) {
        float offsetCenter = AnimationUtils.lerp(range.leftOrTop.locOffset, range.rightOrBottom.locOffset, range.leftOrTop.loc, range.rightOrBottom.loc, childCenterLocation);
        if (range.rightOrBottom != this.currentKeylineState.getFirstKeyline() && range.leftOrTop != this.currentKeylineState.getLastKeyline()) {
            return offsetCenter;
        }
        return offsetCenter + ((childCenterLocation - range.rightOrBottom.loc) * ((1.0f - range.rightOrBottom.mask) + (this.orientationHelper.getMaskMargins((RecyclerView.LayoutParams) child.getLayoutParams()) / this.currentKeylineState.getItemSize())));
    }

    private float getMaskedItemSizeForLocOffset(float locOffset, KeylineRange range) {
        return AnimationUtils.lerp(range.leftOrTop.maskedItemSize, range.rightOrBottom.maskedItemSize, range.leftOrTop.locOffset, range.rightOrBottom.locOffset, locOffset);
    }

    private void updateChildMaskForLocation(View child, float childCenterLocation, KeylineRange range) {
        View view = child;
        KeylineRange keylineRange = range;
        if (view instanceof Maskable) {
            float maskProgress = AnimationUtils.lerp(keylineRange.leftOrTop.mask, keylineRange.rightOrBottom.mask, keylineRange.leftOrTop.loc, keylineRange.rightOrBottom.loc, childCenterLocation);
            float childHeight = (float) child.getHeight();
            float childWidth = (float) child.getWidth();
            float maskWidth = AnimationUtils.lerp(0.0f, childWidth / 2.0f, 0.0f, 1.0f, maskProgress);
            RectF maskRect = this.orientationHelper.getMaskRect(childHeight, childWidth, AnimationUtils.lerp(0.0f, childHeight / 2.0f, 0.0f, 1.0f, maskProgress), maskWidth);
            float offsetCenter = calculateChildOffsetCenterForLocation(child, childCenterLocation, range);
            RectF offsetMaskRect = new RectF(offsetCenter - (maskRect.width() / 2.0f), offsetCenter - (maskRect.height() / 2.0f), (maskRect.width() / 2.0f) + offsetCenter, (maskRect.height() / 2.0f) + offsetCenter);
            float f = maskProgress;
            float f2 = childHeight;
            float f3 = childWidth;
            RectF parentBoundsRect = new RectF((float) getParentLeft(), (float) getParentTop(), (float) getParentRight(), (float) getParentBottom());
            if (this.carouselStrategy.isContained()) {
                this.orientationHelper.containMaskWithinBounds(maskRect, offsetMaskRect, parentBoundsRect);
            }
            this.orientationHelper.moveMaskOnEdgeOutsideBounds(maskRect, offsetMaskRect, parentBoundsRect);
            ((Maskable) view).setMaskRectF(maskRect);
        }
    }

    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        float childWidthDimension;
        float childHeightDimension;
        if (child instanceof Maskable) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            Rect insets = new Rect();
            calculateItemDecorationsForChild(child, insets);
            int widthUsed2 = widthUsed + insets.left + insets.right;
            int heightUsed2 = heightUsed + insets.top + insets.bottom;
            if (this.keylineStateList == null || this.orientationHelper.orientation != 0) {
                childWidthDimension = (float) lp.width;
            } else {
                childWidthDimension = this.keylineStateList.getDefaultState().getItemSize();
            }
            if (this.keylineStateList == null || this.orientationHelper.orientation != 1) {
                childHeightDimension = (float) lp.height;
            } else {
                childHeightDimension = this.keylineStateList.getDefaultState().getItemSize();
            }
            child.measure(getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed2, (int) childWidthDimension, canScrollHorizontally()), getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin + heightUsed2, (int) childHeightDimension, canScrollVertically()));
            return;
        }
        throw new IllegalStateException("All children of a RecyclerView using CarouselLayoutManager must use MaskableFrameLayout as their root ViewGroup.");
    }

    /* access modifiers changed from: private */
    public int getParentLeft() {
        return this.orientationHelper.getParentLeft();
    }

    private int getParentStart() {
        return this.orientationHelper.getParentStart();
    }

    /* access modifiers changed from: private */
    public int getParentRight() {
        return this.orientationHelper.getParentRight();
    }

    private int getParentEnd() {
        return this.orientationHelper.getParentEnd();
    }

    /* access modifiers changed from: private */
    public int getParentTop() {
        return this.orientationHelper.getParentTop();
    }

    /* access modifiers changed from: private */
    public int getParentBottom() {
        return this.orientationHelper.getParentBottom();
    }

    public int getContainerWidth() {
        return getWidth();
    }

    public int getContainerHeight() {
        return getHeight();
    }

    private int getContainerSize() {
        if (isHorizontal()) {
            return getContainerWidth();
        }
        return getContainerHeight();
    }

    /* access modifiers changed from: package-private */
    public boolean isLayoutRtl() {
        return isHorizontal() && getLayoutDirection() == 1;
    }

    private int addStart(int value, int amount) {
        return isLayoutRtl() ? value + amount : value - amount;
    }

    private int addEnd(int value, int amount) {
        return isLayoutRtl() ? value - amount : value + amount;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (getChildCount() > 0) {
            event.setFromIndex(getPosition(getChildAt(0)));
            event.setToIndex(getPosition(getChildAt(getChildCount() - 1)));
        }
    }

    private int getScrollOffsetForPosition(int position, KeylineState keylineState) {
        if (isLayoutRtl()) {
            return (int) (((((float) getContainerSize()) - keylineState.getLastFocalKeyline().loc) - (((float) position) * keylineState.getItemSize())) - (keylineState.getItemSize() / 2.0f));
        }
        return (int) (((((float) position) * keylineState.getItemSize()) - keylineState.getFirstFocalKeyline().loc) + (keylineState.getItemSize() / 2.0f));
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (this.keylineStateList == null) {
            return null;
        }
        int offset = getOffsetToScrollToPosition(targetPosition, getKeylineStateForPosition(targetPosition));
        if (isHorizontal()) {
            return new PointF((float) offset, 0.0f);
        }
        return new PointF(0.0f, (float) offset);
    }

    /* access modifiers changed from: package-private */
    public int getOffsetToScrollToPosition(int position, KeylineState keylineState) {
        return getScrollOffsetForPosition(position, keylineState) - this.scrollOffset;
    }

    /* access modifiers changed from: package-private */
    public int getOffsetToScrollToPositionForSnap(int position, boolean partialSnap) {
        int targetSnapOffset = getOffsetToScrollToPosition(position, this.keylineStateList.getShiftedState((float) this.scrollOffset, (float) this.minScroll, (float) this.maxScroll, true));
        int positionOffset = targetSnapOffset;
        if (this.keylineStatePositionMap != null) {
            positionOffset = getOffsetToScrollToPosition(position, getKeylineStateForPosition(position));
        }
        if (!partialSnap) {
            return targetSnapOffset;
        }
        if (Math.abs(positionOffset) < Math.abs(targetSnapOffset)) {
            return positionOffset;
        }
        return targetSnapOffset;
    }

    private KeylineState getKeylineStateForPosition(int position) {
        KeylineState keylineState;
        if (this.keylineStatePositionMap == null || (keylineState = this.keylineStatePositionMap.get(Integer.valueOf(MathUtils.clamp(position, 0, Math.max(0, getItemCount() - 1))))) == null) {
            return this.keylineStateList.getDefaultState();
        }
        return keylineState;
    }

    public void scrollToPosition(int position) {
        if (this.keylineStateList != null) {
            this.scrollOffset = getScrollOffsetForPosition(position, getKeylineStateForPosition(position));
            this.currentFillStartPosition = MathUtils.clamp(position, 0, Math.max(0, getItemCount() - 1));
            updateCurrentKeylineStateForScrollOffset();
            requestLayout();
        }
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return CarouselLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            public int calculateDxToMakeVisible(View view, int snapPreference) {
                if (CarouselLayoutManager.this.keylineStateList == null || !CarouselLayoutManager.this.isHorizontal()) {
                    return 0;
                }
                return CarouselLayoutManager.this.calculateScrollDeltaToMakePositionVisible(CarouselLayoutManager.this.getPosition(view));
            }

            public int calculateDyToMakeVisible(View view, int snapPreference) {
                if (CarouselLayoutManager.this.keylineStateList == null || CarouselLayoutManager.this.isHorizontal()) {
                    return 0;
                }
                return CarouselLayoutManager.this.calculateScrollDeltaToMakePositionVisible(CarouselLayoutManager.this.getPosition(view));
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    /* access modifiers changed from: package-private */
    public int calculateScrollDeltaToMakePositionVisible(int position) {
        return (int) (((float) this.scrollOffset) - ((float) getScrollOffsetForPosition(position, getKeylineStateForPosition(position))));
    }

    public boolean canScrollHorizontally() {
        return isHorizontal();
    }

    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (canScrollHorizontally()) {
            return scrollBy(dx, recycler, state);
        }
        return 0;
    }

    public boolean canScrollVertically() {
        return !isHorizontal();
    }

    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (canScrollVertically()) {
            return scrollBy(dy, recycler, state);
        }
        return 0;
    }

    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        if (this.keylineStateList == null) {
            return false;
        }
        int delta = getOffsetToScrollToPosition(getPosition(child), getKeylineStateForPosition(getPosition(child)));
        if (focusedChildVisible || delta == 0) {
            return false;
        }
        parent.scrollBy(delta, 0);
        return true;
    }

    private int scrollBy(int distance, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || distance == 0) {
            return 0;
        }
        int scrolledBy = calculateShouldScrollBy(distance, this.scrollOffset, this.minScroll, this.maxScroll);
        this.scrollOffset += scrolledBy;
        updateCurrentKeylineStateForScrollOffset();
        float halfItemSize = this.currentKeylineState.getItemSize() / 2.0f;
        int start = calculateChildStartForFill(getPosition(getChildAt(0)));
        Rect boundsRect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            offsetChild(getChildAt(i), (float) start, halfItemSize, boundsRect);
            start = addEnd(start, (int) this.currentKeylineState.getItemSize());
        }
        fill(recycler, state);
        return scrolledBy;
    }

    private void offsetChild(View child, float startOffset, float halfItemSize, Rect boundsRect) {
        int center = addEnd((int) startOffset, (int) halfItemSize);
        KeylineRange range = getSurroundingKeylineRange(this.currentKeylineState.getKeylines(), (float) center, false);
        float offsetCenter = calculateChildOffsetCenterForLocation(child, (float) center, range);
        super.getDecoratedBoundsWithMargins(child, boundsRect);
        updateChildMaskForLocation(child, (float) center, range);
        this.orientationHelper.offsetChild(child, boundsRect, halfItemSize, offsetCenter);
    }

    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        return this.scrollOffset;
    }

    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return (int) this.keylineStateList.getDefaultState().getItemSize();
    }

    public int computeHorizontalScrollRange(RecyclerView.State state) {
        return this.maxScroll - this.minScroll;
    }

    public int computeVerticalScrollOffset(RecyclerView.State state) {
        return this.scrollOffset;
    }

    public int computeVerticalScrollExtent(RecyclerView.State state) {
        return (int) this.keylineStateList.getDefaultState().getItemSize();
    }

    public int computeVerticalScrollRange(RecyclerView.State state) {
        return this.maxScroll - this.minScroll;
    }

    public int getOrientation() {
        return this.orientationHelper.orientation;
    }

    public void setOrientation(int orientation) {
        if (orientation == 0 || orientation == 1) {
            assertNotInLayoutOrScroll((String) null);
            if (this.orientationHelper == null || orientation != this.orientationHelper.orientation) {
                this.orientationHelper = CarouselOrientationHelper.createOrientationHelper(this, orientation);
                refreshKeylineState();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("invalid orientation:" + orientation);
    }

    public void setDebuggingEnabled(RecyclerView recyclerView, boolean enabled) {
        this.isDebuggingEnabled = enabled;
        recyclerView.removeItemDecoration(this.debugItemDecoration);
        if (enabled) {
            recyclerView.addItemDecoration(this.debugItemDecoration);
        }
        recyclerView.invalidateItemDecorations();
    }

    private static class KeylineRange {
        final KeylineState.Keyline leftOrTop;
        final KeylineState.Keyline rightOrBottom;

        KeylineRange(KeylineState.Keyline leftOrTop2, KeylineState.Keyline rightOrBottom2) {
            Preconditions.checkArgument(leftOrTop2.loc <= rightOrBottom2.loc);
            this.leftOrTop = leftOrTop2;
            this.rightOrBottom = rightOrBottom2;
        }
    }

    private static class DebugItemDecoration extends RecyclerView.ItemDecoration {
        private List<KeylineState.Keyline> keylines = Collections.unmodifiableList(new ArrayList());
        private final Paint linePaint = new Paint();

        DebugItemDecoration() {
            this.linePaint.setStrokeWidth(5.0f);
            this.linePaint.setColor(-65281);
        }

        /* access modifiers changed from: package-private */
        public void setKeylines(List<KeylineState.Keyline> keylines2) {
            this.keylines = Collections.unmodifiableList(keylines2);
        }

        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            this.linePaint.setStrokeWidth(parent.getResources().getDimension(R.dimen.m3_carousel_debug_keyline_width));
            for (KeylineState.Keyline keyline : this.keylines) {
                this.linePaint.setColor(ColorUtils.blendARGB(-65281, -16776961, keyline.mask));
                if (((CarouselLayoutManager) parent.getLayoutManager()).isHorizontal()) {
                    c.drawLine(keyline.locOffset, (float) ((CarouselLayoutManager) parent.getLayoutManager()).getParentTop(), keyline.locOffset, (float) ((CarouselLayoutManager) parent.getLayoutManager()).getParentBottom(), this.linePaint);
                } else {
                    c.drawLine((float) ((CarouselLayoutManager) parent.getLayoutManager()).getParentLeft(), keyline.locOffset, (float) ((CarouselLayoutManager) parent.getLayoutManager()).getParentRight(), keyline.locOffset, this.linePaint);
                }
            }
        }
    }
}
