package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.collection.ArrayMap;
import androidx.core.os.CancellationSignal;
import androidx.core.util.Preconditions;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.FragmentAnim;
import androidx.fragment.app.SpecialEffectsController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DefaultSpecialEffectsController extends SpecialEffectsController {
    DefaultSpecialEffectsController(ViewGroup container) {
        super(container);
    }

    /* access modifiers changed from: package-private */
    public void executeOperations(List<SpecialEffectsController.Operation> operations, boolean isPop) {
        boolean z = isPop;
        SpecialEffectsController.Operation firstOut = null;
        SpecialEffectsController.Operation lastIn = null;
        for (SpecialEffectsController.Operation operation : operations) {
            SpecialEffectsController.Operation.State currentState = SpecialEffectsController.Operation.State.from(operation.getFragment().mView);
            switch (operation.getFinalState()) {
                case GONE:
                case INVISIBLE:
                case REMOVED:
                    if (currentState == SpecialEffectsController.Operation.State.VISIBLE && firstOut == null) {
                        firstOut = operation;
                        break;
                    }
                case VISIBLE:
                    if (currentState == SpecialEffectsController.Operation.State.VISIBLE) {
                        break;
                    } else {
                        lastIn = operation;
                        break;
                    }
            }
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            Log.v(FragmentManager.TAG, "Executing operations from " + firstOut + " to " + lastIn);
        }
        List<AnimationInfo> animations = new ArrayList<>();
        List<TransitionInfo> transitions = new ArrayList<>();
        final List<SpecialEffectsController.Operation> awaitingContainerChanges = new ArrayList<>(operations);
        syncAnimations(operations);
        Iterator<SpecialEffectsController.Operation> it = operations.iterator();
        while (it.hasNext()) {
            final SpecialEffectsController.Operation operation2 = it.next();
            CancellationSignal animCancellationSignal = new CancellationSignal();
            operation2.markStartedSpecialEffect(animCancellationSignal);
            animations.add(new AnimationInfo(operation2, animCancellationSignal, z));
            CancellationSignal transitionCancellationSignal = new CancellationSignal();
            operation2.markStartedSpecialEffect(transitionCancellationSignal);
            transitions.add(new TransitionInfo(operation2, transitionCancellationSignal, z, !z ? operation2 == lastIn : operation2 == firstOut));
            operation2.addCompletionListener(new Runnable() {
                public void run() {
                    if (awaitingContainerChanges.contains(operation2)) {
                        awaitingContainerChanges.remove(operation2);
                        DefaultSpecialEffectsController.this.applyContainerChanges(operation2);
                    }
                }
            });
        }
        List<SpecialEffectsController.Operation> awaitingContainerChanges2 = awaitingContainerChanges;
        Map<SpecialEffectsController.Operation, Boolean> startedTransitions = startTransitions(transitions, awaitingContainerChanges, isPop, firstOut, lastIn);
        startAnimations(animations, awaitingContainerChanges2, startedTransitions.containsValue(true), startedTransitions);
        for (SpecialEffectsController.Operation operation3 : awaitingContainerChanges2) {
            applyContainerChanges(operation3);
        }
        awaitingContainerChanges2.clear();
        if (FragmentManager.isLoggingEnabled(2)) {
            Log.v(FragmentManager.TAG, "Completed executing operations from " + firstOut + " to " + lastIn);
        }
    }

    private void syncAnimations(List<SpecialEffectsController.Operation> operations) {
        Fragment lastOpFragment = operations.get(operations.size() - 1).getFragment();
        for (SpecialEffectsController.Operation operation : operations) {
            operation.getFragment().mAnimationInfo.mEnterAnim = lastOpFragment.mAnimationInfo.mEnterAnim;
            operation.getFragment().mAnimationInfo.mExitAnim = lastOpFragment.mAnimationInfo.mExitAnim;
            operation.getFragment().mAnimationInfo.mPopEnterAnim = lastOpFragment.mAnimationInfo.mPopEnterAnim;
            operation.getFragment().mAnimationInfo.mPopExitAnim = lastOpFragment.mAnimationInfo.mPopExitAnim;
        }
    }

    private void startAnimations(List<AnimationInfo> animationInfos, List<SpecialEffectsController.Operation> awaitingContainerChanges, boolean startedAnyTransition, Map<SpecialEffectsController.Operation, Boolean> startedTransitions) {
        boolean startedAnyAnimator;
        View viewToAnimate;
        final SpecialEffectsController.Operation operation;
        ViewGroup container = getContainer();
        Context context = container.getContext();
        ArrayList arrayList = new ArrayList();
        boolean startedAnyAnimator2 = false;
        for (AnimationInfo animationInfo : animationInfos) {
            if (animationInfo.isVisibilityUnchanged()) {
                animationInfo.completeSpecialEffect();
                Map<SpecialEffectsController.Operation, Boolean> map = startedTransitions;
            } else {
                FragmentAnim.AnimationOrAnimator anim = animationInfo.getAnimation(context);
                if (anim == null) {
                    animationInfo.completeSpecialEffect();
                    Map<SpecialEffectsController.Operation, Boolean> map2 = startedTransitions;
                } else {
                    Animator animator = anim.animator;
                    if (animator == null) {
                        arrayList.add(animationInfo);
                        Map<SpecialEffectsController.Operation, Boolean> map3 = startedTransitions;
                    } else {
                        SpecialEffectsController.Operation operation2 = animationInfo.getOperation();
                        Fragment fragment = operation2.getFragment();
                        if (Boolean.TRUE.equals(startedTransitions.get(operation2))) {
                            if (FragmentManager.isLoggingEnabled(2)) {
                                Log.v(FragmentManager.TAG, "Ignoring Animator set on " + fragment + " as this Fragment was involved in a Transition.");
                            }
                            animationInfo.completeSpecialEffect();
                        } else {
                            boolean isHideOperation = operation2.getFinalState() == SpecialEffectsController.Operation.State.GONE;
                            if (isHideOperation) {
                                awaitingContainerChanges.remove(operation2);
                            } else {
                                List<SpecialEffectsController.Operation> list = awaitingContainerChanges;
                            }
                            View viewToAnimate2 = fragment.mView;
                            container.startViewTransition(viewToAnimate2);
                            AnonymousClass2 r19 = r0;
                            View viewToAnimate3 = viewToAnimate2;
                            final ViewGroup viewGroup = container;
                            Fragment fragment2 = fragment;
                            final View view = viewToAnimate3;
                            SpecialEffectsController.Operation operation3 = operation2;
                            final boolean z = isHideOperation;
                            final Animator animator2 = animator;
                            final SpecialEffectsController.Operation operation4 = operation3;
                            final AnimationInfo animationInfo2 = animationInfo;
                            AnonymousClass2 r0 = new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator anim) {
                                    viewGroup.endViewTransition(view);
                                    if (z) {
                                        operation4.getFinalState().applyState(view);
                                    }
                                    animationInfo2.completeSpecialEffect();
                                    if (FragmentManager.isLoggingEnabled(2)) {
                                        Log.v(FragmentManager.TAG, "Animator from operation " + operation4 + " has ended.");
                                    }
                                }
                            };
                            animator2.addListener(r0);
                            animator2.setTarget(viewToAnimate3);
                            animator2.start();
                            if (FragmentManager.isLoggingEnabled(2)) {
                                operation = operation3;
                                Log.v(FragmentManager.TAG, "Animator from operation " + operation + " has started.");
                            } else {
                                operation = operation3;
                            }
                            animationInfo.getSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() {
                                public void onCancel() {
                                    animator2.end();
                                    if (FragmentManager.isLoggingEnabled(2)) {
                                        Log.v(FragmentManager.TAG, "Animator from operation " + operation + " has been canceled.");
                                    }
                                }
                            });
                            startedAnyAnimator2 = true;
                        }
                    }
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            AnimationInfo animationInfo3 = (AnimationInfo) it.next();
            SpecialEffectsController.Operation operation5 = animationInfo3.getOperation();
            Fragment fragment3 = operation5.getFragment();
            if (startedAnyTransition) {
                if (FragmentManager.isLoggingEnabled(2)) {
                    Log.v(FragmentManager.TAG, "Ignoring Animation set on " + fragment3 + " as Animations cannot run alongside Transitions.");
                }
                animationInfo3.completeSpecialEffect();
            } else if (startedAnyAnimator2) {
                if (FragmentManager.isLoggingEnabled(2)) {
                    Log.v(FragmentManager.TAG, "Ignoring Animation set on " + fragment3 + " as Animations cannot run alongside Animators.");
                }
                animationInfo3.completeSpecialEffect();
            } else {
                final View viewToAnimate4 = fragment3.mView;
                Animation anim2 = (Animation) Preconditions.checkNotNull(((FragmentAnim.AnimationOrAnimator) Preconditions.checkNotNull(animationInfo3.getAnimation(context))).animation);
                SpecialEffectsController.Operation.State finalState = operation5.getFinalState();
                if (finalState != SpecialEffectsController.Operation.State.REMOVED) {
                    viewToAnimate4.startAnimation(anim2);
                    animationInfo3.completeSpecialEffect();
                    SpecialEffectsController.Operation.State state = finalState;
                    Animation animation = anim2;
                    viewToAnimate = viewToAnimate4;
                    startedAnyAnimator = startedAnyAnimator2;
                } else {
                    container.startViewTransition(viewToAnimate4);
                    AnonymousClass4 r16 = r0;
                    startedAnyAnimator = startedAnyAnimator2;
                    Animation animation2 = new FragmentAnim.EndViewTransitionAnimation(anim2, container, viewToAnimate4);
                    SpecialEffectsController.Operation.State state2 = finalState;
                    final SpecialEffectsController.Operation operation6 = operation5;
                    Animation animation3 = anim2;
                    final ViewGroup viewGroup2 = container;
                    final AnimationInfo animationInfo4 = animationInfo3;
                    AnonymousClass4 r02 = new Animation.AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                            if (FragmentManager.isLoggingEnabled(2)) {
                                Log.v(FragmentManager.TAG, "Animation from operation " + operation6 + " has reached onAnimationStart.");
                            }
                        }

                        public void onAnimationEnd(Animation animation) {
                            viewGroup2.post(new Runnable() {
                                public void run() {
                                    viewGroup2.endViewTransition(viewToAnimate4);
                                    animationInfo4.completeSpecialEffect();
                                }
                            });
                            if (FragmentManager.isLoggingEnabled(2)) {
                                Log.v(FragmentManager.TAG, "Animation from operation " + operation6 + " has ended.");
                            }
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }
                    };
                    animation2.setAnimationListener(r02);
                    viewToAnimate = viewToAnimate4;
                    viewToAnimate.startAnimation(animation2);
                    if (FragmentManager.isLoggingEnabled(2)) {
                        Log.v(FragmentManager.TAG, "Animation from operation " + operation5 + " has started.");
                    }
                }
                CancellationSignal signal = animationInfo3.getSignal();
                final View view2 = viewToAnimate;
                final ViewGroup viewGroup3 = container;
                ViewGroup container2 = container;
                AnonymousClass5 r7 = r0;
                final AnimationInfo animationInfo5 = animationInfo3;
                View view3 = viewToAnimate;
                final SpecialEffectsController.Operation operation7 = operation5;
                AnonymousClass5 r03 = new CancellationSignal.OnCancelListener() {
                    public void onCancel() {
                        view2.clearAnimation();
                        viewGroup3.endViewTransition(view2);
                        animationInfo5.completeSpecialEffect();
                        if (FragmentManager.isLoggingEnabled(2)) {
                            Log.v(FragmentManager.TAG, "Animation from operation " + operation7 + " has been cancelled.");
                        }
                    }
                };
                signal.setOnCancelListener(r7);
                startedAnyAnimator2 = startedAnyAnimator;
                container = container2;
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: android.view.View} */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x0597, code lost:
        if (r13 == r42) goto L_0x059c;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x05b7  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x05ee  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Map<androidx.fragment.app.SpecialEffectsController.Operation, java.lang.Boolean> startTransitions(java.util.List<androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo> r38, java.util.List<androidx.fragment.app.SpecialEffectsController.Operation> r39, boolean r40, androidx.fragment.app.SpecialEffectsController.Operation r41, androidx.fragment.app.SpecialEffectsController.Operation r42) {
        /*
            r37 = this;
            r6 = r37
            r7 = r40
            r8 = r41
            r9 = r42
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r10 = r0
            r0 = 0
            java.util.Iterator r1 = r38.iterator()
            r15 = r0
        L_0x0014:
            boolean r0 = r1.hasNext()
            if (r0 == 0) goto L_0x006b
            java.lang.Object r0 = r1.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r0 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r0
            boolean r2 = r0.isVisibilityUnchanged()
            if (r2 == 0) goto L_0x0027
            goto L_0x0014
        L_0x0027:
            androidx.fragment.app.FragmentTransitionImpl r2 = r0.getHandlingImpl()
            if (r15 != 0) goto L_0x0030
            r3 = r2
            r15 = r3
            goto L_0x006a
        L_0x0030:
            if (r2 == 0) goto L_0x006a
            if (r15 != r2) goto L_0x0035
            goto L_0x006a
        L_0x0035:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Mixing framework transitions and AndroidX transitions is not allowed. Fragment "
            java.lang.StringBuilder r3 = r3.append(r4)
            androidx.fragment.app.SpecialEffectsController$Operation r4 = r0.getOperation()
            androidx.fragment.app.Fragment r4 = r4.getFragment()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = " returned Transition "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.Object r4 = r0.getTransition()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = " which uses a different Transition  type than other Fragments."
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.<init>(r3)
            throw r1
        L_0x006a:
            goto L_0x0014
        L_0x006b:
            r14 = 0
            if (r15 != 0) goto L_0x008e
            java.util.Iterator r0 = r38.iterator()
        L_0x0072:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x008d
            java.lang.Object r1 = r0.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r1 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r1
            androidx.fragment.app.SpecialEffectsController$Operation r2 = r1.getOperation()
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r14)
            r10.put(r2, r3)
            r1.completeSpecialEffect()
            goto L_0x0072
        L_0x008d:
            return r10
        L_0x008e:
            android.view.View r0 = new android.view.View
            android.view.ViewGroup r1 = r37.getContainer()
            android.content.Context r1 = r1.getContext()
            r0.<init>(r1)
            r13 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r12 = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r11 = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r5 = r3
            androidx.collection.ArrayMap r3 = new androidx.collection.ArrayMap
            r3.<init>()
            r4 = r3
            java.util.Iterator r19 = r38.iterator()
            r3 = r1
            r20 = r2
        L_0x00be:
            boolean r1 = r19.hasNext()
            java.lang.String r14 = "FragmentManager"
            if (r1 == 0) goto L_0x041b
            java.lang.Object r1 = r19.next()
            r21 = r1
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r21 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r21
            boolean r22 = r21.hasSharedElementTransition()
            if (r22 == 0) goto L_0x03fb
            if (r8 == 0) goto L_0x03fb
            if (r9 == 0) goto L_0x03fb
            java.lang.Object r1 = r21.getSharedElementTransition()
            java.lang.Object r1 = r15.cloneTransition(r1)
            java.lang.Object r1 = r15.wrapTransitionInSet(r1)
            androidx.fragment.app.Fragment r0 = r42.getFragment()
            java.util.ArrayList r0 = r0.getSharedElementSourceNames()
            androidx.fragment.app.Fragment r18 = r41.getFragment()
            java.util.ArrayList r2 = r18.getSharedElementSourceNames()
            androidx.fragment.app.Fragment r18 = r41.getFragment()
            r24 = r10
            java.util.ArrayList r10 = r18.getSharedElementTargetNames()
            r18 = 0
            r25 = r1
            r1 = r18
        L_0x0105:
            r18 = r3
            int r3 = r10.size()
            if (r1 >= r3) goto L_0x012a
            java.lang.Object r3 = r10.get(r1)
            int r3 = r0.indexOf(r3)
            r26 = r10
            r10 = -1
            if (r3 == r10) goto L_0x0123
            java.lang.Object r10 = r2.get(r1)
            java.lang.String r10 = (java.lang.String) r10
            r0.set(r3, r10)
        L_0x0123:
            int r1 = r1 + 1
            r3 = r18
            r10 = r26
            goto L_0x0105
        L_0x012a:
            r26 = r10
            androidx.fragment.app.Fragment r1 = r42.getFragment()
            java.util.ArrayList r10 = r1.getSharedElementTargetNames()
            if (r7 != 0) goto L_0x014c
            androidx.fragment.app.Fragment r1 = r41.getFragment()
            androidx.core.app.SharedElementCallback r1 = r1.getExitTransitionCallback()
            androidx.fragment.app.Fragment r3 = r42.getFragment()
            androidx.core.app.SharedElementCallback r3 = r3.getEnterTransitionCallback()
            r36 = r3
            r3 = r1
            r1 = r36
            goto L_0x0161
        L_0x014c:
            androidx.fragment.app.Fragment r1 = r41.getFragment()
            androidx.core.app.SharedElementCallback r1 = r1.getEnterTransitionCallback()
            androidx.fragment.app.Fragment r3 = r42.getFragment()
            androidx.core.app.SharedElementCallback r3 = r3.getExitTransitionCallback()
            r36 = r3
            r3 = r1
            r1 = r36
        L_0x0161:
            r27 = r13
            int r13 = r0.size()
            r28 = 0
            r29 = r2
            r2 = r28
        L_0x016d:
            if (r2 >= r13) goto L_0x018d
            java.lang.Object r28 = r0.get(r2)
            r30 = r13
            r13 = r28
            java.lang.String r13 = (java.lang.String) r13
            java.lang.Object r28 = r10.get(r2)
            r31 = r12
            r12 = r28
            java.lang.String r12 = (java.lang.String) r12
            r4.put(r13, r12)
            int r2 = r2 + 1
            r13 = r30
            r12 = r31
            goto L_0x016d
        L_0x018d:
            r31 = r12
            r30 = r13
            r2 = 2
            boolean r12 = androidx.fragment.app.FragmentManager.isLoggingEnabled(r2)
            if (r12 == 0) goto L_0x01f6
            java.lang.String r2 = ">>> entering view names <<<"
            android.util.Log.v(r14, r2)
            java.util.Iterator r2 = r10.iterator()
        L_0x01a1:
            boolean r12 = r2.hasNext()
            java.lang.String r13 = "Name: "
            if (r12 == 0) goto L_0x01c8
            java.lang.Object r12 = r2.next()
            java.lang.String r12 = (java.lang.String) r12
            r28 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.StringBuilder r2 = r2.append(r13)
            java.lang.StringBuilder r2 = r2.append(r12)
            java.lang.String r2 = r2.toString()
            android.util.Log.v(r14, r2)
            r2 = r28
            goto L_0x01a1
        L_0x01c8:
            java.lang.String r2 = ">>> exiting view names <<<"
            android.util.Log.v(r14, r2)
            java.util.Iterator r2 = r0.iterator()
        L_0x01d1:
            boolean r12 = r2.hasNext()
            if (r12 == 0) goto L_0x01f6
            java.lang.Object r12 = r2.next()
            java.lang.String r12 = (java.lang.String) r12
            r28 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.StringBuilder r2 = r2.append(r13)
            java.lang.StringBuilder r2 = r2.append(r12)
            java.lang.String r2 = r2.toString()
            android.util.Log.v(r14, r2)
            r2 = r28
            goto L_0x01d1
        L_0x01f6:
            androidx.collection.ArrayMap r2 = new androidx.collection.ArrayMap
            r2.<init>()
            r13 = r2
            androidx.fragment.app.Fragment r2 = r41.getFragment()
            android.view.View r2 = r2.mView
            r6.findNamedViews(r13, r2)
            r13.retainAll(r0)
            if (r3 == 0) goto L_0x0272
            r2 = 2
            boolean r12 = androidx.fragment.app.FragmentManager.isLoggingEnabled(r2)
            if (r12 == 0) goto L_0x0227
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r12 = "Executing exit callback for operation "
            java.lang.StringBuilder r2 = r2.append(r12)
            java.lang.StringBuilder r2 = r2.append(r8)
            java.lang.String r2 = r2.toString()
            android.util.Log.v(r14, r2)
        L_0x0227:
            r3.onMapSharedElements(r0, r13)
            int r2 = r0.size()
            r12 = 1
            int r2 = r2 - r12
        L_0x0230:
            if (r2 < 0) goto L_0x026d
            java.lang.Object r12 = r0.get(r2)
            java.lang.String r12 = (java.lang.String) r12
            java.lang.Object r28 = r13.get(r12)
            android.view.View r28 = (android.view.View) r28
            if (r28 != 0) goto L_0x0248
            r4.remove(r12)
            r32 = r0
            r33 = r3
            goto L_0x0266
        L_0x0248:
            r32 = r0
            java.lang.String r0 = androidx.core.view.ViewCompat.getTransitionName(r28)
            boolean r0 = r12.equals(r0)
            if (r0 != 0) goto L_0x0264
            java.lang.Object r0 = r4.remove(r12)
            java.lang.String r0 = (java.lang.String) r0
            r33 = r3
            java.lang.String r3 = androidx.core.view.ViewCompat.getTransitionName(r28)
            r4.put(r3, r0)
            goto L_0x0266
        L_0x0264:
            r33 = r3
        L_0x0266:
            int r2 = r2 + -1
            r0 = r32
            r3 = r33
            goto L_0x0230
        L_0x026d:
            r32 = r0
            r33 = r3
            goto L_0x027d
        L_0x0272:
            r32 = r0
            r33 = r3
            java.util.Set r0 = r13.keySet()
            r4.retainAll(r0)
        L_0x027d:
            androidx.collection.ArrayMap r0 = new androidx.collection.ArrayMap
            r0.<init>()
            r12 = r0
            androidx.fragment.app.Fragment r0 = r42.getFragment()
            android.view.View r0 = r0.mView
            r6.findNamedViews(r12, r0)
            r12.retainAll(r10)
            java.util.Collection r0 = r4.values()
            r12.retainAll(r0)
            if (r1 == 0) goto L_0x0302
            r0 = 2
            boolean r0 = androidx.fragment.app.FragmentManager.isLoggingEnabled(r0)
            if (r0 == 0) goto L_0x02b5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Executing enter callback for operation "
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.StringBuilder r0 = r0.append(r9)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r14, r0)
        L_0x02b5:
            r1.onMapSharedElements(r10, r12)
            int r0 = r10.size()
            r2 = 1
            int r0 = r0 - r2
        L_0x02be:
            if (r0 < 0) goto L_0x02ff
            java.lang.Object r2 = r10.get(r0)
            java.lang.String r2 = (java.lang.String) r2
            java.lang.Object r3 = r12.get(r2)
            android.view.View r3 = (android.view.View) r3
            if (r3 != 0) goto L_0x02da
            java.lang.String r14 = androidx.fragment.app.FragmentTransition.findKeyForValue(r4, r2)
            if (r14 == 0) goto L_0x02d7
            r4.remove(r14)
        L_0x02d7:
            r23 = r1
            goto L_0x02fa
        L_0x02da:
            java.lang.String r14 = androidx.core.view.ViewCompat.getTransitionName(r3)
            boolean r14 = r2.equals(r14)
            if (r14 != 0) goto L_0x02f8
            java.lang.String r14 = androidx.fragment.app.FragmentTransition.findKeyForValue(r4, r2)
            if (r14 == 0) goto L_0x02f5
            r23 = r1
            java.lang.String r1 = androidx.core.view.ViewCompat.getTransitionName(r3)
            r4.put(r14, r1)
            goto L_0x02fa
        L_0x02f5:
            r23 = r1
            goto L_0x02fa
        L_0x02f8:
            r23 = r1
        L_0x02fa:
            int r0 = r0 + -1
            r1 = r23
            goto L_0x02be
        L_0x02ff:
            r23 = r1
            goto L_0x0307
        L_0x0302:
            r23 = r1
            androidx.fragment.app.FragmentTransition.retainValues(r4, r12)
        L_0x0307:
            java.util.Set r0 = r4.keySet()
            r6.retainMatchingViews(r13, r0)
            java.util.Collection r0 = r4.values()
            r6.retainMatchingViews(r12, r0)
            boolean r0 = r4.isEmpty()
            if (r0 == 0) goto L_0x0335
            r0 = 0
            r11.clear()
            r5.clear()
            r29 = r4
            r13 = r9
            r35 = r11
            r9 = r15
            r3 = r18
            r2 = r27
            r1 = r31
            r7 = 0
            r15 = r5
            r5 = r8
            r8 = r24
            goto L_0x040b
        L_0x0335:
            androidx.fragment.app.Fragment r0 = r42.getFragment()
            androidx.fragment.app.Fragment r1 = r41.getFragment()
            r14 = 1
            androidx.fragment.app.FragmentTransition.callSharedElementStartEnd(r0, r1, r7, r13, r14)
            android.view.ViewGroup r3 = r37.getContainer()
            androidx.fragment.app.DefaultSpecialEffectsController$6 r2 = new androidx.fragment.app.DefaultSpecialEffectsController$6
            r1 = r32
            r0 = r2
            r14 = r25
            r1 = r37
            r7 = r2
            r25 = r29
            r2 = r42
            r9 = r3
            r34 = r18
            r28 = r33
            r3 = r41
            r29 = r4
            r4 = r40
            r8 = r5
            r5 = r12
            r0.<init>(r2, r3, r4, r5)
            androidx.core.view.OneShotPreDrawListener.add(r9, r7)
            java.util.Collection r0 = r13.values()
            r11.addAll(r0)
            boolean r0 = r32.isEmpty()
            if (r0 != 0) goto L_0x0389
            r0 = r32
            r1 = 0
            java.lang.Object r2 = r0.get(r1)
            r1 = r2
            java.lang.String r1 = (java.lang.String) r1
            java.lang.Object r2 = r13.get(r1)
            r3 = r2
            android.view.View r3 = (android.view.View) r3
            r15.setEpicenter((java.lang.Object) r14, (android.view.View) r3)
            goto L_0x038d
        L_0x0389:
            r0 = r32
            r3 = r34
        L_0x038d:
            java.util.Collection r1 = r12.values()
            r8.addAll(r1)
            boolean r1 = r10.isEmpty()
            if (r1 != 0) goto L_0x03be
            r1 = 0
            java.lang.Object r2 = r10.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            java.lang.Object r4 = r12.get(r2)
            android.view.View r4 = (android.view.View) r4
            if (r4 == 0) goto L_0x03bb
            r20 = 1
            r5 = r15
            android.view.ViewGroup r7 = r37.getContainer()
            androidx.fragment.app.DefaultSpecialEffectsController$7 r9 = new androidx.fragment.app.DefaultSpecialEffectsController$7
            r1 = r31
            r9.<init>(r5, r4, r1)
            androidx.core.view.OneShotPreDrawListener.add(r7, r9)
            goto L_0x03c0
        L_0x03bb:
            r1 = r31
            goto L_0x03c0
        L_0x03be:
            r1 = r31
        L_0x03c0:
            r2 = r27
            r15.setSharedElementTargets(r14, r2, r11)
            r4 = 0
            r5 = 0
            r7 = 0
            r9 = 0
            r35 = r11
            r11 = r15
            r27 = r12
            r12 = r14
            r31 = r13
            r13 = r7
            r32 = r14
            r7 = 0
            r33 = 1
            r14 = r9
            r9 = r15
            r15 = r4
            r16 = r5
            r17 = r32
            r18 = r8
            r11.scheduleRemoveTargets(r12, r13, r14, r15, r16, r17, r18)
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r33)
            r5 = r41
            r15 = r8
            r8 = r24
            r8.put(r5, r4)
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r33)
            r13 = r42
            r8.put(r13, r4)
            r0 = r32
            goto L_0x040b
        L_0x03fb:
            r34 = r3
            r29 = r4
            r35 = r11
            r1 = r12
            r2 = r13
            r7 = 0
            r13 = r9
            r9 = r15
            r15 = r5
            r5 = r8
            r8 = r10
            r3 = r34
        L_0x040b:
            r12 = r1
            r14 = r7
            r10 = r8
            r4 = r29
            r11 = r35
            r7 = r40
            r8 = r5
            r5 = r15
            r15 = r9
            r9 = r13
            r13 = r2
            goto L_0x00be
        L_0x041b:
            r34 = r3
            r29 = r4
            r35 = r11
            r1 = r12
            r2 = r13
            r7 = 0
            r33 = 1
            r13 = r9
            r9 = r15
            r15 = r5
            r5 = r8
            r8 = r10
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r4 = 0
            r10 = 0
            java.util.Iterator r19 = r38.iterator()
        L_0x0436:
            boolean r11 = r19.hasNext()
            if (r11 == 0) goto L_0x0563
            java.lang.Object r11 = r19.next()
            r21 = r11
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r21 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r21
            boolean r11 = r21.isVisibilityUnchanged()
            if (r11 == 0) goto L_0x0459
            androidx.fragment.app.SpecialEffectsController$Operation r11 = r21.getOperation()
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r7)
            r8.put(r11, r12)
            r21.completeSpecialEffect()
            goto L_0x0436
        L_0x0459:
            java.lang.Object r11 = r21.getTransition()
            java.lang.Object r12 = r9.cloneTransition(r11)
            androidx.fragment.app.SpecialEffectsController$Operation r11 = r21.getOperation()
            if (r0 == 0) goto L_0x046e
            if (r11 == r5) goto L_0x046b
            if (r11 != r13) goto L_0x046e
        L_0x046b:
            r16 = r33
            goto L_0x0470
        L_0x046e:
            r16 = r7
        L_0x0470:
            r22 = r16
            if (r12 != 0) goto L_0x048a
            if (r22 != 0) goto L_0x0480
            java.lang.Boolean r13 = java.lang.Boolean.valueOf(r7)
            r8.put(r11, r13)
            r21.completeSpecialEffect()
        L_0x0480:
            r31 = r1
            r30 = r2
            r2 = r14
            r7 = r15
            r14 = r34
            goto L_0x0556
        L_0x048a:
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            androidx.fragment.app.Fragment r7 = r11.getFragment()
            android.view.View r7 = r7.mView
            r6.captureTransitioningViews(r13, r7)
            if (r22 == 0) goto L_0x04a9
            if (r11 != r5) goto L_0x04a3
            r7 = r35
            r13.removeAll(r7)
            goto L_0x04ab
        L_0x04a3:
            r7 = r35
            r13.removeAll(r15)
            goto L_0x04ab
        L_0x04a9:
            r7 = r35
        L_0x04ab:
            boolean r16 = r13.isEmpty()
            if (r16 == 0) goto L_0x04bd
            r9.addTarget(r12, r2)
            r30 = r2
            r35 = r7
            r2 = r14
            r7 = r15
            r15 = r12
            goto L_0x0520
        L_0x04bd:
            r9.addTargets(r12, r13)
            r17 = 0
            r18 = 0
            r16 = 0
            r25 = 0
            r26 = r11
            r11 = r9
            r27 = r12
            r28 = r13
            r13 = r27
            r30 = r2
            r2 = r14
            r14 = r28
            r35 = r7
            r7 = r15
            r15 = r16
            r16 = r25
            r11.scheduleRemoveTargets(r12, r13, r14, r15, r16, r17, r18)
            androidx.fragment.app.SpecialEffectsController$Operation$State r11 = r26.getFinalState()
            androidx.fragment.app.SpecialEffectsController$Operation$State r12 = androidx.fragment.app.SpecialEffectsController.Operation.State.GONE
            if (r11 != r12) goto L_0x051a
            r15 = r39
            r11 = r26
            r15.remove(r11)
            java.util.ArrayList r12 = new java.util.ArrayList
            r13 = r28
            r12.<init>(r13)
            androidx.fragment.app.Fragment r14 = r11.getFragment()
            android.view.View r14 = r14.mView
            r12.remove(r14)
            androidx.fragment.app.Fragment r14 = r11.getFragment()
            android.view.View r14 = r14.mView
            r15 = r27
            r9.scheduleHideFragmentView(r15, r14, r12)
            android.view.ViewGroup r14 = r37.getContainer()
            r16 = r12
            androidx.fragment.app.DefaultSpecialEffectsController$8 r12 = new androidx.fragment.app.DefaultSpecialEffectsController$8
            r12.<init>(r13)
            androidx.core.view.OneShotPreDrawListener.add(r14, r12)
            goto L_0x0520
        L_0x051a:
            r11 = r26
            r15 = r27
            r13 = r28
        L_0x0520:
            androidx.fragment.app.SpecialEffectsController$Operation$State r12 = r11.getFinalState()
            androidx.fragment.app.SpecialEffectsController$Operation$State r14 = androidx.fragment.app.SpecialEffectsController.Operation.State.VISIBLE
            if (r12 != r14) goto L_0x0536
            r3.addAll(r13)
            if (r20 == 0) goto L_0x0533
            r9.setEpicenter((java.lang.Object) r15, (android.graphics.Rect) r1)
            r14 = r34
            goto L_0x053b
        L_0x0533:
            r14 = r34
            goto L_0x053b
        L_0x0536:
            r14 = r34
            r9.setEpicenter((java.lang.Object) r15, (android.view.View) r14)
        L_0x053b:
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r33)
            r8.put(r11, r12)
            boolean r12 = r21.isOverlapAllowed()
            r31 = r1
            r1 = 0
            if (r12 == 0) goto L_0x0551
            java.lang.Object r1 = r9.mergeTransitionsTogether(r4, r15, r1)
            r4 = r1
            goto L_0x0556
        L_0x0551:
            java.lang.Object r1 = r9.mergeTransitionsTogether(r10, r15, r1)
            r10 = r1
        L_0x0556:
            r13 = r42
            r15 = r7
            r34 = r14
            r1 = r31
            r7 = 0
            r14 = r2
            r2 = r30
            goto L_0x0436
        L_0x0563:
            r31 = r1
            r30 = r2
            r2 = r14
            r7 = r15
            r14 = r34
            java.lang.Object r1 = r9.mergeTransitionsInSequence(r4, r10, r0)
            if (r1 != 0) goto L_0x0572
            return r8
        L_0x0572:
            java.util.Iterator r4 = r38.iterator()
        L_0x0576:
            boolean r11 = r4.hasNext()
            if (r11 == 0) goto L_0x060e
            java.lang.Object r11 = r4.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r11 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r11
            boolean r12 = r11.isVisibilityUnchanged()
            if (r12 == 0) goto L_0x0589
            goto L_0x0576
        L_0x0589:
            java.lang.Object r12 = r11.getTransition()
            androidx.fragment.app.SpecialEffectsController$Operation r13 = r11.getOperation()
            if (r0 == 0) goto L_0x059f
            if (r13 == r5) goto L_0x059a
            r15 = r42
            if (r13 != r15) goto L_0x05a1
            goto L_0x059c
        L_0x059a:
            r15 = r42
        L_0x059c:
            r16 = r33
            goto L_0x05a3
        L_0x059f:
            r15 = r42
        L_0x05a1:
            r16 = 0
        L_0x05a3:
            if (r12 != 0) goto L_0x05ad
            if (r16 == 0) goto L_0x05a8
            goto L_0x05ad
        L_0x05a8:
            r17 = r4
            r18 = r10
            goto L_0x0606
        L_0x05ad:
            android.view.ViewGroup r17 = r37.getContainer()
            boolean r17 = androidx.core.view.ViewCompat.isLaidOut(r17)
            if (r17 != 0) goto L_0x05ee
            r17 = 2
            boolean r18 = androidx.fragment.app.FragmentManager.isLoggingEnabled(r17)
            if (r18 == 0) goto L_0x05e6
            r17 = r4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "SpecialEffectsController: Container "
            java.lang.StringBuilder r4 = r4.append(r5)
            android.view.ViewGroup r5 = r37.getContainer()
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = " has not been laid out. Completing operation "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r13)
            java.lang.String r4 = r4.toString()
            android.util.Log.v(r2, r4)
            goto L_0x05e8
        L_0x05e6:
            r17 = r4
        L_0x05e8:
            r11.completeSpecialEffect()
            r18 = r10
            goto L_0x0606
        L_0x05ee:
            r17 = r4
            androidx.fragment.app.SpecialEffectsController$Operation r4 = r11.getOperation()
            androidx.fragment.app.Fragment r4 = r4.getFragment()
            androidx.core.os.CancellationSignal r5 = r11.getSignal()
            r18 = r10
            androidx.fragment.app.DefaultSpecialEffectsController$9 r10 = new androidx.fragment.app.DefaultSpecialEffectsController$9
            r10.<init>(r11, r13)
            r9.setListenerForTransitionEnd(r4, r1, r5, r10)
        L_0x0606:
            r5 = r41
            r4 = r17
            r10 = r18
            goto L_0x0576
        L_0x060e:
            r15 = r42
            r18 = r10
            android.view.ViewGroup r4 = r37.getContainer()
            boolean r4 = androidx.core.view.ViewCompat.isLaidOut(r4)
            if (r4 != 0) goto L_0x061d
            return r8
        L_0x061d:
            r4 = 4
            androidx.fragment.app.FragmentTransition.setViewVisibility(r3, r4)
            java.util.ArrayList r4 = r9.prepareSetNameOverridesReordered(r7)
            r5 = 2
            boolean r5 = androidx.fragment.app.FragmentManager.isLoggingEnabled(r5)
            if (r5 == 0) goto L_0x06a6
            java.lang.String r5 = ">>>>> Beginning transition <<<<<"
            android.util.Log.v(r2, r5)
            java.lang.String r5 = ">>>>> SharedElementFirstOutViews <<<<<"
            android.util.Log.v(r2, r5)
            java.util.Iterator r5 = r35.iterator()
        L_0x063b:
            boolean r10 = r5.hasNext()
            java.lang.String r11 = " Name: "
            java.lang.String r12 = "View: "
            if (r10 == 0) goto L_0x066c
            java.lang.Object r10 = r5.next()
            android.view.View r10 = (android.view.View) r10
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.StringBuilder r12 = r13.append(r12)
            java.lang.StringBuilder r12 = r12.append(r10)
            java.lang.StringBuilder r11 = r12.append(r11)
            java.lang.String r12 = androidx.core.view.ViewCompat.getTransitionName(r10)
            java.lang.StringBuilder r11 = r11.append(r12)
            java.lang.String r11 = r11.toString()
            android.util.Log.v(r2, r11)
            goto L_0x063b
        L_0x066c:
            java.lang.String r5 = ">>>>> SharedElementLastInViews <<<<<"
            android.util.Log.v(r2, r5)
            java.util.Iterator r5 = r7.iterator()
        L_0x0675:
            boolean r10 = r5.hasNext()
            if (r10 == 0) goto L_0x06a6
            java.lang.Object r10 = r5.next()
            android.view.View r10 = (android.view.View) r10
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.StringBuilder r13 = r13.append(r12)
            java.lang.StringBuilder r13 = r13.append(r10)
            java.lang.StringBuilder r13 = r13.append(r11)
            r16 = r5
            java.lang.String r5 = androidx.core.view.ViewCompat.getTransitionName(r10)
            java.lang.StringBuilder r5 = r13.append(r5)
            java.lang.String r5 = r5.toString()
            android.util.Log.v(r2, r5)
            r5 = r16
            goto L_0x0675
        L_0x06a6:
            android.view.ViewGroup r2 = r37.getContainer()
            r9.beginDelayedTransition(r2, r1)
            android.view.ViewGroup r12 = r37.getContainer()
            r11 = r9
            r13 = r35
            r2 = r14
            r14 = r7
            r15 = r4
            r16 = r29
            r11.setNameOverridesReordered(r12, r13, r14, r15, r16)
            r5 = 0
            androidx.fragment.app.FragmentTransition.setViewVisibility(r3, r5)
            r5 = r35
            r9.swapSharedElementTargets(r0, r5, r7)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.DefaultSpecialEffectsController.startTransitions(java.util.List, java.util.List, boolean, androidx.fragment.app.SpecialEffectsController$Operation, androidx.fragment.app.SpecialEffectsController$Operation):java.util.Map");
    }

    /* access modifiers changed from: package-private */
    public void retainMatchingViews(ArrayMap<String, View> sharedElementViews, Collection<String> transitionNames) {
        Iterator<Map.Entry<String, View>> iterator = sharedElementViews.entrySet().iterator();
        while (iterator.hasNext()) {
            if (!transitionNames.contains(ViewCompat.getTransitionName(iterator.next().getValue()))) {
                iterator.remove();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void captureTransitioningViews(ArrayList<View> transitioningViews, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (!ViewGroupCompat.isTransitionGroup(viewGroup)) {
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = viewGroup.getChildAt(i);
                    if (child.getVisibility() == 0) {
                        captureTransitioningViews(transitioningViews, child);
                    }
                }
            } else if (!transitioningViews.contains(view)) {
                transitioningViews.add(viewGroup);
            }
        } else if (!transitioningViews.contains(view)) {
            transitioningViews.add(view);
        }
    }

    /* access modifiers changed from: package-private */
    public void findNamedViews(Map<String, View> namedViews, View view) {
        String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            namedViews.put(transitionName, view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                if (child.getVisibility() == 0) {
                    findNamedViews(namedViews, child);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void applyContainerChanges(SpecialEffectsController.Operation operation) {
        operation.getFinalState().applyState(operation.getFragment().mView);
    }

    private static class SpecialEffectsInfo {
        private final SpecialEffectsController.Operation mOperation;
        private final CancellationSignal mSignal;

        SpecialEffectsInfo(SpecialEffectsController.Operation operation, CancellationSignal signal) {
            this.mOperation = operation;
            this.mSignal = signal;
        }

        /* access modifiers changed from: package-private */
        public SpecialEffectsController.Operation getOperation() {
            return this.mOperation;
        }

        /* access modifiers changed from: package-private */
        public CancellationSignal getSignal() {
            return this.mSignal;
        }

        /* access modifiers changed from: package-private */
        public boolean isVisibilityUnchanged() {
            SpecialEffectsController.Operation.State currentState = SpecialEffectsController.Operation.State.from(this.mOperation.getFragment().mView);
            SpecialEffectsController.Operation.State finalState = this.mOperation.getFinalState();
            return currentState == finalState || !(currentState == SpecialEffectsController.Operation.State.VISIBLE || finalState == SpecialEffectsController.Operation.State.VISIBLE);
        }

        /* access modifiers changed from: package-private */
        public void completeSpecialEffect() {
            this.mOperation.completeSpecialEffect(this.mSignal);
        }
    }

    private static class AnimationInfo extends SpecialEffectsInfo {
        private FragmentAnim.AnimationOrAnimator mAnimation;
        private boolean mIsPop;
        private boolean mLoadedAnim = false;

        AnimationInfo(SpecialEffectsController.Operation operation, CancellationSignal signal, boolean isPop) {
            super(operation, signal);
            this.mIsPop = isPop;
        }

        /* access modifiers changed from: package-private */
        public FragmentAnim.AnimationOrAnimator getAnimation(Context context) {
            if (this.mLoadedAnim) {
                return this.mAnimation;
            }
            this.mAnimation = FragmentAnim.loadAnimation(context, getOperation().getFragment(), getOperation().getFinalState() == SpecialEffectsController.Operation.State.VISIBLE, this.mIsPop);
            this.mLoadedAnim = true;
            return this.mAnimation;
        }
    }

    private static class TransitionInfo extends SpecialEffectsInfo {
        private final boolean mOverlapAllowed;
        private final Object mSharedElementTransition;
        private final Object mTransition;

        TransitionInfo(SpecialEffectsController.Operation operation, CancellationSignal signal, boolean isPop, boolean providesSharedElementTransition) {
            super(operation, signal);
            Object obj;
            Object obj2;
            boolean z;
            if (operation.getFinalState() == SpecialEffectsController.Operation.State.VISIBLE) {
                if (isPop) {
                    obj2 = operation.getFragment().getReenterTransition();
                } else {
                    obj2 = operation.getFragment().getEnterTransition();
                }
                this.mTransition = obj2;
                if (isPop) {
                    z = operation.getFragment().getAllowReturnTransitionOverlap();
                } else {
                    z = operation.getFragment().getAllowEnterTransitionOverlap();
                }
                this.mOverlapAllowed = z;
            } else {
                if (isPop) {
                    obj = operation.getFragment().getReturnTransition();
                } else {
                    obj = operation.getFragment().getExitTransition();
                }
                this.mTransition = obj;
                this.mOverlapAllowed = true;
            }
            if (!providesSharedElementTransition) {
                this.mSharedElementTransition = null;
            } else if (isPop) {
                this.mSharedElementTransition = operation.getFragment().getSharedElementReturnTransition();
            } else {
                this.mSharedElementTransition = operation.getFragment().getSharedElementEnterTransition();
            }
        }

        /* access modifiers changed from: package-private */
        public Object getTransition() {
            return this.mTransition;
        }

        /* access modifiers changed from: package-private */
        public boolean isOverlapAllowed() {
            return this.mOverlapAllowed;
        }

        public boolean hasSharedElementTransition() {
            return this.mSharedElementTransition != null;
        }

        public Object getSharedElementTransition() {
            return this.mSharedElementTransition;
        }

        /* access modifiers changed from: package-private */
        public FragmentTransitionImpl getHandlingImpl() {
            FragmentTransitionImpl transitionImpl = getHandlingImpl(this.mTransition);
            FragmentTransitionImpl sharedElementTransitionImpl = getHandlingImpl(this.mSharedElementTransition);
            if (transitionImpl == null || sharedElementTransitionImpl == null || transitionImpl == sharedElementTransitionImpl) {
                return transitionImpl != null ? transitionImpl : sharedElementTransitionImpl;
            }
            throw new IllegalArgumentException("Mixing framework transitions and AndroidX transitions is not allowed. Fragment " + getOperation().getFragment() + " returned Transition " + this.mTransition + " which uses a different Transition  type than its shared element transition " + this.mSharedElementTransition);
        }

        private FragmentTransitionImpl getHandlingImpl(Object transition) {
            if (transition == null) {
                return null;
            }
            if (FragmentTransition.PLATFORM_IMPL != null && FragmentTransition.PLATFORM_IMPL.canHandle(transition)) {
                return FragmentTransition.PLATFORM_IMPL;
            }
            if (FragmentTransition.SUPPORT_IMPL != null && FragmentTransition.SUPPORT_IMPL.canHandle(transition)) {
                return FragmentTransition.SUPPORT_IMPL;
            }
            throw new IllegalArgumentException("Transition " + transition + " for fragment " + getOperation().getFragment() + " is not a valid framework Transition or AndroidX Transition");
        }
    }
}
