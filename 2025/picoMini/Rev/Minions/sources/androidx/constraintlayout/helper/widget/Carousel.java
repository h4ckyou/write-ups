package androidx.constraintlayout.helper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.constraintlayout.motion.widget.MotionHelper;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.ArrayList;
import java.util.Iterator;

public class Carousel extends MotionHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "Carousel";
    public static final int TOUCH_UP_CARRY_ON = 2;
    public static final int TOUCH_UP_IMMEDIATE_STOP = 1;
    private int backwardTransition = -1;
    /* access modifiers changed from: private */
    public float dampening = 0.9f;
    private int emptyViewBehavior = 4;
    private int firstViewReference = -1;
    private int forwardTransition = -1;
    private boolean infiniteCarousel = false;
    /* access modifiers changed from: private */
    public Adapter mAdapter = null;
    private int mAnimateTargetDelay = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    /* access modifiers changed from: private */
    public int mIndex = 0;
    int mLastStartId = -1;
    private final ArrayList<View> mList = new ArrayList<>();
    /* access modifiers changed from: private */
    public MotionLayout mMotionLayout;
    /* access modifiers changed from: private */
    public int mPreviousIndex = 0;
    private int mTargetIndex = -1;
    Runnable mUpdateRunnable = new Runnable() {
        public void run() {
            Carousel.this.mMotionLayout.setProgress(0.0f);
            Carousel.this.updateItems();
            Carousel.this.mAdapter.onNewItem(Carousel.this.mIndex);
            float velocity = Carousel.this.mMotionLayout.getVelocity();
            if (Carousel.this.touchUpMode == 2 && velocity > Carousel.this.velocityThreshold && Carousel.this.mIndex < Carousel.this.mAdapter.count() - 1) {
                final float v = Carousel.this.dampening * velocity;
                if (Carousel.this.mIndex == 0 && Carousel.this.mPreviousIndex > Carousel.this.mIndex) {
                    return;
                }
                if (Carousel.this.mIndex != Carousel.this.mAdapter.count() - 1 || Carousel.this.mPreviousIndex >= Carousel.this.mIndex) {
                    Carousel.this.mMotionLayout.post(new Runnable() {
                        public void run() {
                            Carousel.this.mMotionLayout.touchAnimateTo(5, 1.0f, v);
                        }
                    });
                }
            }
        }
    };
    private int nextState = -1;
    private int previousState = -1;
    private int startIndex = 0;
    /* access modifiers changed from: private */
    public int touchUpMode = 1;
    /* access modifiers changed from: private */
    public float velocityThreshold = 2.0f;

    public interface Adapter {
        int count();

        void onNewItem(int i);

        void populate(View view, int i);
    }

    public Carousel(Context context) {
        super(context);
    }

    public Carousel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Carousel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Carousel);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.Carousel_carousel_firstView) {
                    this.firstViewReference = a.getResourceId(attr, this.firstViewReference);
                } else if (attr == R.styleable.Carousel_carousel_backwardTransition) {
                    this.backwardTransition = a.getResourceId(attr, this.backwardTransition);
                } else if (attr == R.styleable.Carousel_carousel_forwardTransition) {
                    this.forwardTransition = a.getResourceId(attr, this.forwardTransition);
                } else if (attr == R.styleable.Carousel_carousel_emptyViewsBehavior) {
                    this.emptyViewBehavior = a.getInt(attr, this.emptyViewBehavior);
                } else if (attr == R.styleable.Carousel_carousel_previousState) {
                    this.previousState = a.getResourceId(attr, this.previousState);
                } else if (attr == R.styleable.Carousel_carousel_nextState) {
                    this.nextState = a.getResourceId(attr, this.nextState);
                } else if (attr == R.styleable.Carousel_carousel_touchUp_dampeningFactor) {
                    this.dampening = a.getFloat(attr, this.dampening);
                } else if (attr == R.styleable.Carousel_carousel_touchUpMode) {
                    this.touchUpMode = a.getInt(attr, this.touchUpMode);
                } else if (attr == R.styleable.Carousel_carousel_touchUp_velocityThreshold) {
                    this.velocityThreshold = a.getFloat(attr, this.velocityThreshold);
                } else if (attr == R.styleable.Carousel_carousel_infinite) {
                    this.infiniteCarousel = a.getBoolean(attr, this.infiniteCarousel);
                }
            }
            a.recycle();
        }
    }

    public void setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
    }

    public int getCount() {
        if (this.mAdapter != null) {
            return this.mAdapter.count();
        }
        return 0;
    }

    public int getCurrentIndex() {
        return this.mIndex;
    }

    public void transitionToIndex(int index, int delay) {
        this.mTargetIndex = Math.max(0, Math.min(getCount() - 1, index));
        this.mAnimateTargetDelay = Math.max(0, delay);
        this.mMotionLayout.setTransitionDuration(this.mAnimateTargetDelay);
        if (index < this.mIndex) {
            this.mMotionLayout.transitionToState(this.previousState, this.mAnimateTargetDelay);
        } else {
            this.mMotionLayout.transitionToState(this.nextState, this.mAnimateTargetDelay);
        }
    }

    public void jumpToIndex(int index) {
        this.mIndex = Math.max(0, Math.min(getCount() - 1, index));
        refresh();
    }

    public void refresh() {
        int count = this.mList.size();
        for (int i = 0; i < count; i++) {
            View view = this.mList.get(i);
            if (this.mAdapter.count() == 0) {
                updateViewVisibility(view, this.emptyViewBehavior);
            } else {
                updateViewVisibility(view, 0);
            }
        }
        this.mMotionLayout.rebuildScene();
        updateItems();
    }

    public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
        this.mLastStartId = startId;
    }

    public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
        this.mPreviousIndex = this.mIndex;
        if (currentId == this.nextState) {
            this.mIndex++;
        } else if (currentId == this.previousState) {
            this.mIndex--;
        }
        if (this.infiniteCarousel) {
            if (this.mIndex >= this.mAdapter.count()) {
                this.mIndex = 0;
            }
            if (this.mIndex < 0) {
                this.mIndex = this.mAdapter.count() - 1;
            }
        } else {
            if (this.mIndex >= this.mAdapter.count()) {
                this.mIndex = this.mAdapter.count() - 1;
            }
            if (this.mIndex < 0) {
                this.mIndex = 0;
            }
        }
        if (this.mPreviousIndex != this.mIndex) {
            this.mMotionLayout.post(this.mUpdateRunnable);
        }
    }

    private void enableAllTransitions(boolean enable) {
        Iterator<MotionScene.Transition> it = this.mMotionLayout.getDefinedTransitions().iterator();
        while (it.hasNext()) {
            it.next().setEnabled(enable);
        }
    }

    private boolean enableTransition(int transitionID, boolean enable) {
        MotionScene.Transition transition;
        if (transitionID == -1 || this.mMotionLayout == null || (transition = this.mMotionLayout.getTransition(transitionID)) == null || enable == transition.isEnabled()) {
            return false;
        }
        transition.setEnabled(enable);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() instanceof MotionLayout) {
            MotionLayout container = (MotionLayout) getParent();
            for (int i = 0; i < this.mCount; i++) {
                int id = this.mIds[i];
                View view = container.getViewById(id);
                if (this.firstViewReference == id) {
                    this.startIndex = i;
                }
                this.mList.add(view);
            }
            this.mMotionLayout = container;
            if (this.touchUpMode == 2) {
                MotionScene.Transition forward = this.mMotionLayout.getTransition(this.forwardTransition);
                if (forward != null) {
                    forward.setOnTouchUp(5);
                }
                MotionScene.Transition backward = this.mMotionLayout.getTransition(this.backwardTransition);
                if (backward != null) {
                    backward.setOnTouchUp(5);
                }
            }
            updateItems();
        }
    }

    private boolean updateViewVisibility(View view, int visibility) {
        if (this.mMotionLayout == null) {
            return false;
        }
        boolean needsMotionSceneRebuild = false;
        int[] constraintSets = this.mMotionLayout.getConstraintSetIds();
        for (int updateViewVisibility : constraintSets) {
            needsMotionSceneRebuild |= updateViewVisibility(updateViewVisibility, view, visibility);
        }
        return needsMotionSceneRebuild;
    }

    private boolean updateViewVisibility(int constraintSetId, View view, int visibility) {
        ConstraintSet.Constraint constraint;
        ConstraintSet constraintSet = this.mMotionLayout.getConstraintSet(constraintSetId);
        if (constraintSet == null || (constraint = constraintSet.getConstraint(view.getId())) == null) {
            return false;
        }
        constraint.propertySet.mVisibilityMode = 1;
        view.setVisibility(visibility);
        return true;
    }

    /* access modifiers changed from: private */
    public void updateItems() {
        if (this.mAdapter != null && this.mMotionLayout != null && this.mAdapter.count() != 0) {
            int viewCount = this.mList.size();
            for (int i = 0; i < viewCount; i++) {
                View view = this.mList.get(i);
                int index = (this.mIndex + i) - this.startIndex;
                if (this.infiniteCarousel) {
                    if (index < 0) {
                        if (this.emptyViewBehavior != 4) {
                            updateViewVisibility(view, this.emptyViewBehavior);
                        } else {
                            updateViewVisibility(view, 0);
                        }
                        if (index % this.mAdapter.count() == 0) {
                            this.mAdapter.populate(view, 0);
                        } else {
                            this.mAdapter.populate(view, this.mAdapter.count() + (index % this.mAdapter.count()));
                        }
                    } else if (index >= this.mAdapter.count()) {
                        if (index == this.mAdapter.count()) {
                            index = 0;
                        } else if (index > this.mAdapter.count()) {
                            index %= this.mAdapter.count();
                        }
                        if (this.emptyViewBehavior != 4) {
                            updateViewVisibility(view, this.emptyViewBehavior);
                        } else {
                            updateViewVisibility(view, 0);
                        }
                        this.mAdapter.populate(view, index);
                    } else {
                        updateViewVisibility(view, 0);
                        this.mAdapter.populate(view, index);
                    }
                } else if (index < 0) {
                    updateViewVisibility(view, this.emptyViewBehavior);
                } else if (index >= this.mAdapter.count()) {
                    updateViewVisibility(view, this.emptyViewBehavior);
                } else {
                    updateViewVisibility(view, 0);
                    this.mAdapter.populate(view, index);
                }
            }
            if (this.mTargetIndex != -1 && this.mTargetIndex != this.mIndex) {
                this.mMotionLayout.post(new Carousel$$ExternalSyntheticLambda0(this));
            } else if (this.mTargetIndex == this.mIndex) {
                this.mTargetIndex = -1;
            }
            if (this.backwardTransition == -1 || this.forwardTransition == -1) {
                Log.w(TAG, "No backward or forward transitions defined for Carousel!");
            } else if (!this.infiniteCarousel) {
                int count = this.mAdapter.count();
                if (this.mIndex == 0) {
                    enableTransition(this.backwardTransition, false);
                } else {
                    enableTransition(this.backwardTransition, true);
                    this.mMotionLayout.setTransition(this.backwardTransition);
                }
                if (this.mIndex == count - 1) {
                    enableTransition(this.forwardTransition, false);
                    return;
                }
                enableTransition(this.forwardTransition, true);
                this.mMotionLayout.setTransition(this.forwardTransition);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$updateItems$0$androidx-constraintlayout-helper-widget-Carousel  reason: not valid java name */
    public /* synthetic */ void m7lambda$updateItems$0$androidxconstraintlayouthelperwidgetCarousel() {
        this.mMotionLayout.setTransitionDuration(this.mAnimateTargetDelay);
        if (this.mTargetIndex < this.mIndex) {
            this.mMotionLayout.transitionToState(this.previousState, this.mAnimateTargetDelay);
        } else {
            this.mMotionLayout.transitionToState(this.nextState, this.mAnimateTargetDelay);
        }
    }
}
