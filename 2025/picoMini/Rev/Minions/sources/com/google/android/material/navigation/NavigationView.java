package com.google.android.material.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import androidx.activity.BackEventCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.widget.TintTypedArray;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.customview.view.AbsSavedState;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.R;
import com.google.android.material.internal.ContextUtils;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.internal.NavigationMenuPresenter;
import com.google.android.material.internal.ScrimInsetsFrameLayout;
import com.google.android.material.internal.WindowUtils;
import com.google.android.material.motion.MaterialBackHandler;
import com.google.android.material.motion.MaterialBackOrchestrator;
import com.google.android.material.motion.MaterialSideContainerBackHelper;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.ShapeableDelegate;

public class NavigationView extends ScrimInsetsFrameLayout implements MaterialBackHandler {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int DEF_STYLE_RES = R.style.Widget_Design_NavigationView;
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private static final int PRESENTER_NAVIGATION_VIEW_ID = 1;
    private final DrawerLayout.DrawerListener backDrawerListener;
    /* access modifiers changed from: private */
    public final MaterialBackOrchestrator backOrchestrator;
    private boolean bottomInsetScrimEnabled;
    private int drawerLayoutCornerSize;
    OnNavigationItemSelectedListener listener;
    private final int maxWidth;
    private final NavigationMenu menu;
    private MenuInflater menuInflater;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    /* access modifiers changed from: private */
    public final NavigationMenuPresenter presenter;
    private final ShapeableDelegate shapeableDelegate;
    private final MaterialSideContainerBackHelper sideContainerBackHelper;
    /* access modifiers changed from: private */
    public final int[] tmpLocation;
    private boolean topInsetScrimEnabled;

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem menuItem);
    }

    public NavigationView(Context context) {
        this(context, (AttributeSet) null);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.navigationViewStyle);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NavigationView(android.content.Context r17, android.util.AttributeSet r18, int r19) {
        /*
            r16 = this;
            r0 = r16
            r7 = r18
            r8 = r19
            int r1 = DEF_STYLE_RES
            r2 = r17
            android.content.Context r1 = com.google.android.material.theme.overlay.MaterialThemeOverlay.wrap(r2, r7, r8, r1)
            r0.<init>(r1, r7, r8)
            com.google.android.material.internal.NavigationMenuPresenter r1 = new com.google.android.material.internal.NavigationMenuPresenter
            r1.<init>()
            r0.presenter = r1
            r1 = 2
            int[] r1 = new int[r1]
            r0.tmpLocation = r1
            r9 = 1
            r0.topInsetScrimEnabled = r9
            r0.bottomInsetScrimEnabled = r9
            r10 = 0
            r0.drawerLayoutCornerSize = r10
            com.google.android.material.shape.ShapeableDelegate r1 = com.google.android.material.shape.ShapeableDelegate.create(r16)
            r0.shapeableDelegate = r1
            com.google.android.material.motion.MaterialSideContainerBackHelper r1 = new com.google.android.material.motion.MaterialSideContainerBackHelper
            r1.<init>(r0)
            r0.sideContainerBackHelper = r1
            com.google.android.material.motion.MaterialBackOrchestrator r1 = new com.google.android.material.motion.MaterialBackOrchestrator
            r1.<init>(r0)
            r0.backOrchestrator = r1
            com.google.android.material.navigation.NavigationView$1 r1 = new com.google.android.material.navigation.NavigationView$1
            r1.<init>()
            r0.backDrawerListener = r1
            android.content.Context r11 = r16.getContext()
            com.google.android.material.internal.NavigationMenu r1 = new com.google.android.material.internal.NavigationMenu
            r1.<init>(r11)
            r0.menu = r1
            int[] r3 = com.google.android.material.R.styleable.NavigationView
            int r5 = DEF_STYLE_RES
            int[] r6 = new int[r10]
            r1 = r11
            r2 = r18
            r4 = r19
            androidx.appcompat.widget.TintTypedArray r1 = com.google.android.material.internal.ThemeEnforcement.obtainTintedStyledAttributes(r1, r2, r3, r4, r5, r6)
            int r2 = com.google.android.material.R.styleable.NavigationView_android_background
            boolean r2 = r1.hasValue(r2)
            if (r2 == 0) goto L_0x006b
            int r2 = com.google.android.material.R.styleable.NavigationView_android_background
            android.graphics.drawable.Drawable r2 = r1.getDrawable(r2)
            androidx.core.view.ViewCompat.setBackground(r0, r2)
        L_0x006b:
            int r2 = com.google.android.material.R.styleable.NavigationView_drawerLayoutCornerSize
            int r2 = r1.getDimensionPixelSize(r2, r10)
            r0.drawerLayoutCornerSize = r2
            android.graphics.drawable.Drawable r2 = r16.getBackground()
            if (r2 == 0) goto L_0x0081
            android.graphics.drawable.Drawable r2 = r16.getBackground()
            boolean r2 = r2 instanceof android.graphics.drawable.ColorDrawable
            if (r2 == 0) goto L_0x00ac
        L_0x0081:
            int r2 = DEF_STYLE_RES
            com.google.android.material.shape.ShapeAppearanceModel$Builder r2 = com.google.android.material.shape.ShapeAppearanceModel.builder((android.content.Context) r11, (android.util.AttributeSet) r7, (int) r8, (int) r2)
            com.google.android.material.shape.ShapeAppearanceModel r2 = r2.build()
            android.graphics.drawable.Drawable r3 = r16.getBackground()
            com.google.android.material.shape.MaterialShapeDrawable r4 = new com.google.android.material.shape.MaterialShapeDrawable
            r4.<init>((com.google.android.material.shape.ShapeAppearanceModel) r2)
            boolean r5 = r3 instanceof android.graphics.drawable.ColorDrawable
            if (r5 == 0) goto L_0x00a6
            r5 = r3
            android.graphics.drawable.ColorDrawable r5 = (android.graphics.drawable.ColorDrawable) r5
            int r5 = r5.getColor()
            android.content.res.ColorStateList r5 = android.content.res.ColorStateList.valueOf(r5)
            r4.setFillColor(r5)
        L_0x00a6:
            r4.initializeElevationOverlay(r11)
            androidx.core.view.ViewCompat.setBackground(r0, r4)
        L_0x00ac:
            int r2 = com.google.android.material.R.styleable.NavigationView_elevation
            boolean r2 = r1.hasValue(r2)
            if (r2 == 0) goto L_0x00be
            int r2 = com.google.android.material.R.styleable.NavigationView_elevation
            int r2 = r1.getDimensionPixelSize(r2, r10)
            float r2 = (float) r2
            r0.setElevation(r2)
        L_0x00be:
            int r2 = com.google.android.material.R.styleable.NavigationView_android_fitsSystemWindows
            boolean r2 = r1.getBoolean(r2, r10)
            r0.setFitsSystemWindows(r2)
            int r2 = com.google.android.material.R.styleable.NavigationView_android_maxWidth
            int r2 = r1.getDimensionPixelSize(r2, r10)
            r0.maxWidth = r2
            r2 = 0
            int r3 = com.google.android.material.R.styleable.NavigationView_subheaderColor
            boolean r3 = r1.hasValue(r3)
            if (r3 == 0) goto L_0x00de
            int r3 = com.google.android.material.R.styleable.NavigationView_subheaderColor
            android.content.res.ColorStateList r2 = r1.getColorStateList(r3)
        L_0x00de:
            r3 = 0
            int r4 = com.google.android.material.R.styleable.NavigationView_subheaderTextAppearance
            boolean r4 = r1.hasValue(r4)
            if (r4 == 0) goto L_0x00ed
            int r4 = com.google.android.material.R.styleable.NavigationView_subheaderTextAppearance
            int r3 = r1.getResourceId(r4, r10)
        L_0x00ed:
            r4 = 16842808(0x1010038, float:2.3693715E-38)
            if (r3 != 0) goto L_0x00f8
            if (r2 != 0) goto L_0x00f8
            android.content.res.ColorStateList r2 = r0.createDefaultColorStateList(r4)
        L_0x00f8:
            int r5 = com.google.android.material.R.styleable.NavigationView_itemIconTint
            boolean r5 = r1.hasValue(r5)
            if (r5 == 0) goto L_0x0107
            int r4 = com.google.android.material.R.styleable.NavigationView_itemIconTint
            android.content.res.ColorStateList r4 = r1.getColorStateList(r4)
            goto L_0x010b
        L_0x0107:
            android.content.res.ColorStateList r4 = r0.createDefaultColorStateList(r4)
        L_0x010b:
            r5 = 0
            int r6 = com.google.android.material.R.styleable.NavigationView_itemTextAppearance
            boolean r6 = r1.hasValue(r6)
            if (r6 == 0) goto L_0x011a
            int r6 = com.google.android.material.R.styleable.NavigationView_itemTextAppearance
            int r5 = r1.getResourceId(r6, r10)
        L_0x011a:
            int r6 = com.google.android.material.R.styleable.NavigationView_itemTextAppearanceActiveBoldEnabled
            boolean r6 = r1.getBoolean(r6, r9)
            int r12 = com.google.android.material.R.styleable.NavigationView_itemIconSize
            boolean r12 = r1.hasValue(r12)
            if (r12 == 0) goto L_0x0131
            int r12 = com.google.android.material.R.styleable.NavigationView_itemIconSize
            int r12 = r1.getDimensionPixelSize(r12, r10)
            r0.setItemIconSize(r12)
        L_0x0131:
            r12 = 0
            int r13 = com.google.android.material.R.styleable.NavigationView_itemTextColor
            boolean r13 = r1.hasValue(r13)
            if (r13 == 0) goto L_0x0140
            int r13 = com.google.android.material.R.styleable.NavigationView_itemTextColor
            android.content.res.ColorStateList r12 = r1.getColorStateList(r13)
        L_0x0140:
            if (r5 != 0) goto L_0x014b
            if (r12 != 0) goto L_0x014b
            r13 = 16842806(0x1010036, float:2.369371E-38)
            android.content.res.ColorStateList r12 = r0.createDefaultColorStateList(r13)
        L_0x014b:
            int r13 = com.google.android.material.R.styleable.NavigationView_itemBackground
            android.graphics.drawable.Drawable r13 = r1.getDrawable(r13)
            if (r13 != 0) goto L_0x0179
            boolean r14 = r0.hasShapeAppearance(r1)
            if (r14 == 0) goto L_0x0179
            android.graphics.drawable.Drawable r13 = r0.createDefaultItemBackground(r1)
            int r14 = com.google.android.material.R.styleable.NavigationView_itemRippleColor
            android.content.res.ColorStateList r14 = com.google.android.material.resources.MaterialResources.getColorStateList((android.content.Context) r11, (androidx.appcompat.widget.TintTypedArray) r1, (int) r14)
            if (r14 == 0) goto L_0x0179
            r15 = 0
            android.graphics.drawable.Drawable r9 = r0.createDefaultItemDrawable(r1, r15)
            android.graphics.drawable.RippleDrawable r10 = new android.graphics.drawable.RippleDrawable
            android.content.res.ColorStateList r7 = com.google.android.material.ripple.RippleUtils.sanitizeRippleDrawableColor(r14)
            r10.<init>(r7, r15, r9)
            r7 = r10
            com.google.android.material.internal.NavigationMenuPresenter r10 = r0.presenter
            r10.setItemForeground(r7)
        L_0x0179:
            int r7 = com.google.android.material.R.styleable.NavigationView_itemHorizontalPadding
            boolean r7 = r1.hasValue(r7)
            if (r7 == 0) goto L_0x018b
            int r7 = com.google.android.material.R.styleable.NavigationView_itemHorizontalPadding
            r9 = 0
            int r7 = r1.getDimensionPixelSize(r7, r9)
            r0.setItemHorizontalPadding(r7)
        L_0x018b:
            int r7 = com.google.android.material.R.styleable.NavigationView_itemVerticalPadding
            boolean r7 = r1.hasValue(r7)
            if (r7 == 0) goto L_0x019e
            int r7 = com.google.android.material.R.styleable.NavigationView_itemVerticalPadding
            r9 = 0
            int r7 = r1.getDimensionPixelSize(r7, r9)
            r0.setItemVerticalPadding(r7)
            goto L_0x019f
        L_0x019e:
            r9 = 0
        L_0x019f:
            int r7 = com.google.android.material.R.styleable.NavigationView_dividerInsetStart
            int r7 = r1.getDimensionPixelSize(r7, r9)
            r0.setDividerInsetStart(r7)
            int r10 = com.google.android.material.R.styleable.NavigationView_dividerInsetEnd
            int r10 = r1.getDimensionPixelSize(r10, r9)
            r0.setDividerInsetEnd(r10)
            int r14 = com.google.android.material.R.styleable.NavigationView_subheaderInsetStart
            int r14 = r1.getDimensionPixelSize(r14, r9)
            r0.setSubheaderInsetStart(r14)
            int r15 = com.google.android.material.R.styleable.NavigationView_subheaderInsetEnd
            int r15 = r1.getDimensionPixelSize(r15, r9)
            r0.setSubheaderInsetEnd(r15)
            int r9 = com.google.android.material.R.styleable.NavigationView_topInsetScrimEnabled
            r17 = r7
            boolean r7 = r0.topInsetScrimEnabled
            boolean r7 = r1.getBoolean(r9, r7)
            r0.setTopInsetScrimEnabled(r7)
            int r7 = com.google.android.material.R.styleable.NavigationView_bottomInsetScrimEnabled
            boolean r9 = r0.bottomInsetScrimEnabled
            boolean r7 = r1.getBoolean(r7, r9)
            r0.setBottomInsetScrimEnabled(r7)
            int r7 = com.google.android.material.R.styleable.NavigationView_itemIconPadding
            r9 = 0
            int r7 = r1.getDimensionPixelSize(r7, r9)
            int r9 = com.google.android.material.R.styleable.NavigationView_itemMaxLines
            r8 = 1
            int r9 = r1.getInt(r9, r8)
            r0.setItemMaxLines(r9)
            com.google.android.material.internal.NavigationMenu r9 = r0.menu
            com.google.android.material.navigation.NavigationView$2 r8 = new com.google.android.material.navigation.NavigationView$2
            r8.<init>()
            r9.setCallback(r8)
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r9 = 1
            r8.setId(r9)
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            com.google.android.material.internal.NavigationMenu r9 = r0.menu
            r8.initForMenu(r11, r9)
            if (r3 == 0) goto L_0x020a
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r8.setSubheaderTextAppearance(r3)
        L_0x020a:
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r8.setSubheaderColor(r2)
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r8.setItemIconTintList(r4)
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            int r9 = r16.getOverScrollMode()
            r8.setOverScrollMode(r9)
            if (r5 == 0) goto L_0x0224
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r8.setItemTextAppearance(r5)
        L_0x0224:
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r8.setItemTextAppearanceActiveBoldEnabled(r6)
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r8.setItemTextColor(r12)
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r8.setItemBackground(r13)
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            r8.setItemIconPadding(r7)
            com.google.android.material.internal.NavigationMenu r8 = r0.menu
            com.google.android.material.internal.NavigationMenuPresenter r9 = r0.presenter
            r8.addMenuPresenter(r9)
            com.google.android.material.internal.NavigationMenuPresenter r8 = r0.presenter
            androidx.appcompat.view.menu.MenuView r8 = r8.getMenuView(r0)
            android.view.View r8 = (android.view.View) r8
            r0.addView(r8)
            int r8 = com.google.android.material.R.styleable.NavigationView_menu
            boolean r8 = r1.hasValue(r8)
            if (r8 == 0) goto L_0x025d
            int r8 = com.google.android.material.R.styleable.NavigationView_menu
            r9 = 0
            int r8 = r1.getResourceId(r8, r9)
            r0.inflateMenu(r8)
            goto L_0x025e
        L_0x025d:
            r9 = 0
        L_0x025e:
            int r8 = com.google.android.material.R.styleable.NavigationView_headerLayout
            boolean r8 = r1.hasValue(r8)
            if (r8 == 0) goto L_0x026f
            int r8 = com.google.android.material.R.styleable.NavigationView_headerLayout
            int r8 = r1.getResourceId(r8, r9)
            r0.inflateHeaderView(r8)
        L_0x026f:
            r1.recycle()
            r16.setupInsetScrimsListener()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.navigation.NavigationView.<init>(android.content.Context, android.util.AttributeSet, int):void");
    }

    public void setOverScrollMode(int overScrollMode) {
        super.setOverScrollMode(overScrollMode);
        if (this.presenter != null) {
            this.presenter.setOverScrollMode(overScrollMode);
        }
    }

    public void setForceCompatClippingEnabled(boolean enabled) {
        this.shapeableDelegate.setForceCompatClippingEnabled(this, enabled);
    }

    private void maybeUpdateCornerSizeForDrawerLayout(int width, int height) {
        if ((getParent() instanceof DrawerLayout) && (getLayoutParams() instanceof DrawerLayout.LayoutParams) && this.drawerLayoutCornerSize > 0 && (getBackground() instanceof MaterialShapeDrawable)) {
            boolean isAbsGravityLeft = GravityCompat.getAbsoluteGravity(((DrawerLayout.LayoutParams) getLayoutParams()).gravity, ViewCompat.getLayoutDirection(this)) == 3;
            MaterialShapeDrawable background = (MaterialShapeDrawable) getBackground();
            ShapeAppearanceModel.Builder builder = background.getShapeAppearanceModel().toBuilder().setAllCornerSizes((float) this.drawerLayoutCornerSize);
            if (isAbsGravityLeft) {
                builder.setTopLeftCornerSize(0.0f);
                builder.setBottomLeftCornerSize(0.0f);
            } else {
                builder.setTopRightCornerSize(0.0f);
                builder.setBottomRightCornerSize(0.0f);
            }
            ShapeAppearanceModel model = builder.build();
            background.setShapeAppearanceModel(model);
            this.shapeableDelegate.onShapeAppearanceChanged(this, model);
            this.shapeableDelegate.onMaskChanged(this, new RectF(0.0f, 0.0f, (float) width, (float) height));
            this.shapeableDelegate.setOffsetZeroCornerEdgeBoundsEnabled(this, true);
        }
    }

    private boolean hasShapeAppearance(TintTypedArray a) {
        return a.hasValue(R.styleable.NavigationView_itemShapeAppearance) || a.hasValue(R.styleable.NavigationView_itemShapeAppearanceOverlay);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        MaterialShapeUtils.setParentAbsoluteElevation(this);
        ViewParent parent = getParent();
        if ((parent instanceof DrawerLayout) && this.backOrchestrator.shouldListenForBackCallbacks()) {
            DrawerLayout drawerLayout = (DrawerLayout) parent;
            drawerLayout.removeDrawerListener(this.backDrawerListener);
            drawerLayout.addDrawerListener(this.backDrawerListener);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        ViewParent parent = getParent();
        if (parent instanceof DrawerLayout) {
            ((DrawerLayout) parent).removeDrawerListener(this.backDrawerListener);
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maybeUpdateCornerSizeForDrawerLayout(w, h);
    }

    public void setElevation(float elevation) {
        super.setElevation(elevation);
        MaterialShapeUtils.setElevation(this, elevation);
    }

    private Drawable createDefaultItemBackground(TintTypedArray a) {
        return createDefaultItemDrawable(a, MaterialResources.getColorStateList(getContext(), a, R.styleable.NavigationView_itemShapeFillColor));
    }

    private Drawable createDefaultItemDrawable(TintTypedArray a, ColorStateList fillColor) {
        TintTypedArray tintTypedArray = a;
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(ShapeAppearanceModel.builder(getContext(), tintTypedArray.getResourceId(R.styleable.NavigationView_itemShapeAppearance, 0), tintTypedArray.getResourceId(R.styleable.NavigationView_itemShapeAppearanceOverlay, 0)).build());
        materialShapeDrawable.setFillColor(fillColor);
        return new InsetDrawable(materialShapeDrawable, tintTypedArray.getDimensionPixelSize(R.styleable.NavigationView_itemShapeInsetStart, 0), tintTypedArray.getDimensionPixelSize(R.styleable.NavigationView_itemShapeInsetTop, 0), tintTypedArray.getDimensionPixelSize(R.styleable.NavigationView_itemShapeInsetEnd, 0), tintTypedArray.getDimensionPixelSize(R.styleable.NavigationView_itemShapeInsetBottom, 0));
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.menuState = new Bundle();
        this.menu.savePresenterStates(state.menuState);
        return state;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable savedState) {
        if (!(savedState instanceof SavedState)) {
            super.onRestoreInstanceState(savedState);
            return;
        }
        SavedState state = (SavedState) savedState;
        super.onRestoreInstanceState(state.getSuperState());
        this.menu.restorePresenterStates(state.menuState);
    }

    public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthSpec, int heightSpec) {
        switch (View.MeasureSpec.getMode(widthSpec)) {
            case Integer.MIN_VALUE:
                widthSpec = View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(widthSpec), this.maxWidth), BasicMeasure.EXACTLY);
                break;
            case 0:
                widthSpec = View.MeasureSpec.makeMeasureSpec(this.maxWidth, BasicMeasure.EXACTLY);
                break;
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.shapeableDelegate.maybeClip(canvas, new NavigationView$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$dispatchDraw$0$com-google-android-material-navigation-NavigationView  reason: not valid java name */
    public /* synthetic */ void m79lambda$dispatchDraw$0$comgoogleandroidmaterialnavigationNavigationView(Canvas x$0) {
        super.dispatchDraw(x$0);
    }

    /* access modifiers changed from: protected */
    public void onInsetsChanged(WindowInsetsCompat insets) {
        this.presenter.dispatchApplyWindowInsets(insets);
    }

    public void inflateMenu(int resId) {
        this.presenter.setUpdateSuspended(true);
        getMenuInflater().inflate(resId, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(false);
    }

    public Menu getMenu() {
        return this.menu;
    }

    public View inflateHeaderView(int res) {
        return this.presenter.inflateHeaderView(res);
    }

    public void addHeaderView(View view) {
        this.presenter.addHeaderView(view);
    }

    public void removeHeaderView(View view) {
        this.presenter.removeHeaderView(view);
    }

    public int getHeaderCount() {
        return this.presenter.getHeaderCount();
    }

    public View getHeaderView(int index) {
        return this.presenter.getHeaderView(index);
    }

    public ColorStateList getItemIconTintList() {
        return this.presenter.getItemTintList();
    }

    public void setItemIconTintList(ColorStateList tint) {
        this.presenter.setItemIconTintList(tint);
    }

    public ColorStateList getItemTextColor() {
        return this.presenter.getItemTextColor();
    }

    public void setItemTextColor(ColorStateList textColor) {
        this.presenter.setItemTextColor(textColor);
    }

    public Drawable getItemBackground() {
        return this.presenter.getItemBackground();
    }

    public void setItemBackgroundResource(int resId) {
        setItemBackground(ContextCompat.getDrawable(getContext(), resId));
    }

    public void setItemBackground(Drawable itemBackground) {
        this.presenter.setItemBackground(itemBackground);
    }

    public int getItemHorizontalPadding() {
        return this.presenter.getItemHorizontalPadding();
    }

    public void setItemHorizontalPadding(int padding) {
        this.presenter.setItemHorizontalPadding(padding);
    }

    public void setItemHorizontalPaddingResource(int paddingResource) {
        this.presenter.setItemHorizontalPadding(getResources().getDimensionPixelSize(paddingResource));
    }

    public int getItemVerticalPadding() {
        return this.presenter.getItemVerticalPadding();
    }

    public void setItemVerticalPadding(int padding) {
        this.presenter.setItemVerticalPadding(padding);
    }

    public void setItemVerticalPaddingResource(int paddingResource) {
        this.presenter.setItemVerticalPadding(getResources().getDimensionPixelSize(paddingResource));
    }

    public int getItemIconPadding() {
        return this.presenter.getItemIconPadding();
    }

    public void setItemIconPadding(int padding) {
        this.presenter.setItemIconPadding(padding);
    }

    public void setItemIconPaddingResource(int paddingResource) {
        this.presenter.setItemIconPadding(getResources().getDimensionPixelSize(paddingResource));
    }

    public void setCheckedItem(int id) {
        MenuItem item = this.menu.findItem(id);
        if (item != null) {
            this.presenter.setCheckedItem((MenuItemImpl) item);
        }
    }

    public void setCheckedItem(MenuItem checkedItem) {
        MenuItem item = this.menu.findItem(checkedItem.getItemId());
        if (item != null) {
            this.presenter.setCheckedItem((MenuItemImpl) item);
            return;
        }
        throw new IllegalArgumentException("Called setCheckedItem(MenuItem) with an item that is not in the current menu.");
    }

    public MenuItem getCheckedItem() {
        return this.presenter.getCheckedItem();
    }

    public void setItemTextAppearance(int resId) {
        this.presenter.setItemTextAppearance(resId);
    }

    public void setItemTextAppearanceActiveBoldEnabled(boolean isBold) {
        this.presenter.setItemTextAppearanceActiveBoldEnabled(isBold);
    }

    public void setItemIconSize(int iconSize) {
        this.presenter.setItemIconSize(iconSize);
    }

    public void setItemMaxLines(int itemMaxLines) {
        this.presenter.setItemMaxLines(itemMaxLines);
    }

    public int getItemMaxLines() {
        return this.presenter.getItemMaxLines();
    }

    public boolean isTopInsetScrimEnabled() {
        return this.topInsetScrimEnabled;
    }

    public void setTopInsetScrimEnabled(boolean enabled) {
        this.topInsetScrimEnabled = enabled;
    }

    public boolean isBottomInsetScrimEnabled() {
        return this.bottomInsetScrimEnabled;
    }

    public void setBottomInsetScrimEnabled(boolean enabled) {
        this.bottomInsetScrimEnabled = enabled;
    }

    public int getDividerInsetStart() {
        return this.presenter.getDividerInsetStart();
    }

    public void setDividerInsetStart(int dividerInsetStart) {
        this.presenter.setDividerInsetStart(dividerInsetStart);
    }

    public int getDividerInsetEnd() {
        return this.presenter.getDividerInsetEnd();
    }

    public void setDividerInsetEnd(int dividerInsetEnd) {
        this.presenter.setDividerInsetEnd(dividerInsetEnd);
    }

    public int getSubheaderInsetStart() {
        return this.presenter.getSubheaderInsetStart();
    }

    public void setSubheaderInsetStart(int subheaderInsetStart) {
        this.presenter.setSubheaderInsetStart(subheaderInsetStart);
    }

    public int getSubheaderInsetEnd() {
        return this.presenter.getSubheaderInsetEnd();
    }

    public void setSubheaderInsetEnd(int subheaderInsetEnd) {
        this.presenter.setSubheaderInsetEnd(subheaderInsetEnd);
    }

    public void startBackProgress(BackEventCompat backEvent) {
        requireDrawerLayoutParent();
        this.sideContainerBackHelper.startBackProgress(backEvent);
    }

    public void updateBackProgress(BackEventCompat backEvent) {
        this.sideContainerBackHelper.updateBackProgress(backEvent, ((DrawerLayout.LayoutParams) requireDrawerLayoutParent().second).gravity);
    }

    public void handleBackInvoked() {
        Pair<DrawerLayout, DrawerLayout.LayoutParams> drawerLayoutPair = requireDrawerLayoutParent();
        DrawerLayout drawerLayout = (DrawerLayout) drawerLayoutPair.first;
        BackEventCompat backEvent = this.sideContainerBackHelper.onHandleBackInvoked();
        if (backEvent == null || Build.VERSION.SDK_INT < 34) {
            drawerLayout.closeDrawer((View) this);
            return;
        }
        this.sideContainerBackHelper.finishBackProgress(backEvent, ((DrawerLayout.LayoutParams) drawerLayoutPair.second).gravity, DrawerLayoutUtils.getScrimCloseAnimatorListener(drawerLayout, this), DrawerLayoutUtils.getScrimCloseAnimatorUpdateListener(drawerLayout));
    }

    public void cancelBackProgress() {
        requireDrawerLayoutParent();
        this.sideContainerBackHelper.cancelBackProgress();
    }

    private Pair<DrawerLayout, DrawerLayout.LayoutParams> requireDrawerLayoutParent() {
        ViewParent parent = getParent();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if ((parent instanceof DrawerLayout) && (layoutParams instanceof DrawerLayout.LayoutParams)) {
            return new Pair<>((DrawerLayout) parent, (DrawerLayout.LayoutParams) layoutParams);
        }
        throw new IllegalStateException("NavigationView back progress requires the direct parent view to be a DrawerLayout.");
    }

    /* access modifiers changed from: package-private */
    public MaterialSideContainerBackHelper getBackHelper() {
        return this.sideContainerBackHelper;
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new SupportMenuInflater(getContext());
        }
        return this.menuInflater;
    }

    private ColorStateList createDefaultColorStateList(int baseColorThemeAttr) {
        TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(baseColorThemeAttr, value, true)) {
            return null;
        }
        ColorStateList baseColor = AppCompatResources.getColorStateList(getContext(), value.resourceId);
        if (!getContext().getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, value, true)) {
            return null;
        }
        int colorPrimary = value.data;
        int defaultColor = baseColor.getDefaultColor();
        return new ColorStateList(new int[][]{DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET}, new int[]{baseColor.getColorForState(DISABLED_STATE_SET, defaultColor), colorPrimary, defaultColor});
    }

    private void setupInsetScrimsListener() {
        this.onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                NavigationView.this.getLocationOnScreen(NavigationView.this.tmpLocation);
                boolean isOnRightSide = true;
                boolean isBehindStatusBar = NavigationView.this.tmpLocation[1] == 0;
                NavigationView.this.presenter.setBehindStatusBar(isBehindStatusBar);
                NavigationView.this.setDrawTopInsetForeground(isBehindStatusBar && NavigationView.this.isTopInsetScrimEnabled());
                NavigationView.this.setDrawLeftInsetForeground(NavigationView.this.tmpLocation[0] == 0 || NavigationView.this.tmpLocation[0] + NavigationView.this.getWidth() == 0);
                Activity activity = ContextUtils.getActivity(NavigationView.this.getContext());
                if (activity != null) {
                    Rect displayBounds = WindowUtils.getCurrentWindowBounds(activity);
                    NavigationView.this.setDrawBottomInsetForeground((displayBounds.height() - NavigationView.this.getHeight() == NavigationView.this.tmpLocation[1]) && (Color.alpha(activity.getWindow().getNavigationBarColor()) != 0) && NavigationView.this.isBottomInsetScrimEnabled());
                    if (!(displayBounds.width() == NavigationView.this.tmpLocation[0] || displayBounds.width() - NavigationView.this.getWidth() == NavigationView.this.tmpLocation[0])) {
                        isOnRightSide = false;
                    }
                    NavigationView.this.setDrawRightInsetForeground(isOnRightSide);
                }
            }
        };
        getViewTreeObserver().addOnGlobalLayoutListener(this.onGlobalLayoutListener);
    }

    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, (ClassLoader) null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        public Bundle menuState;

        public SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.menuState = in.readBundle(loader);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(this.menuState);
        }
    }
}
