package com.google.android.material.navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuView;
import androidx.customview.view.AbsSavedState;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;
import com.google.android.material.shape.ShapeAppearanceModel;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class NavigationBarView extends FrameLayout {
    public static final int LABEL_VISIBILITY_AUTO = -1;
    public static final int LABEL_VISIBILITY_LABELED = 1;
    public static final int LABEL_VISIBILITY_SELECTED = 0;
    public static final int LABEL_VISIBILITY_UNLABELED = 2;
    private static final int MENU_PRESENTER_ID = 1;
    private final NavigationBarMenu menu;
    private MenuInflater menuInflater;
    private final NavigationBarMenuView menuView;
    private final NavigationBarPresenter presenter = new NavigationBarPresenter();
    /* access modifiers changed from: private */
    public OnItemReselectedListener reselectedListener;
    /* access modifiers changed from: private */
    public OnItemSelectedListener selectedListener;

    @Retention(RetentionPolicy.SOURCE)
    public @interface LabelVisibility {
    }

    public interface OnItemReselectedListener {
        void onNavigationItemReselected(MenuItem menuItem);
    }

    public interface OnItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem menuItem);
    }

    /* access modifiers changed from: protected */
    public abstract NavigationBarMenuView createNavigationBarMenuView(Context context);

    public abstract int getMaxItemCount();

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NavigationBarView(android.content.Context r18, android.util.AttributeSet r19, int r20, int r21) {
        /*
            r17 = this;
            r0 = r17
            r7 = r19
            r8 = r20
            android.content.Context r1 = com.google.android.material.theme.overlay.MaterialThemeOverlay.wrap(r18, r19, r20, r21)
            r0.<init>(r1, r7, r8)
            com.google.android.material.navigation.NavigationBarPresenter r1 = new com.google.android.material.navigation.NavigationBarPresenter
            r1.<init>()
            r0.presenter = r1
            android.content.Context r9 = r17.getContext()
            int[] r3 = com.google.android.material.R.styleable.NavigationBarView
            int r1 = com.google.android.material.R.styleable.NavigationBarView_itemTextAppearanceInactive
            int r2 = com.google.android.material.R.styleable.NavigationBarView_itemTextAppearanceActive
            int[] r6 = new int[]{r1, r2}
            r1 = r9
            r2 = r19
            r4 = r20
            r5 = r21
            androidx.appcompat.widget.TintTypedArray r1 = com.google.android.material.internal.ThemeEnforcement.obtainTintedStyledAttributes(r1, r2, r3, r4, r5, r6)
            com.google.android.material.navigation.NavigationBarMenu r2 = new com.google.android.material.navigation.NavigationBarMenu
            java.lang.Class r3 = r17.getClass()
            int r4 = r17.getMaxItemCount()
            r2.<init>(r9, r3, r4)
            r0.menu = r2
            com.google.android.material.navigation.NavigationBarMenuView r2 = r0.createNavigationBarMenuView(r9)
            r0.menuView = r2
            com.google.android.material.navigation.NavigationBarPresenter r2 = r0.presenter
            com.google.android.material.navigation.NavigationBarMenuView r3 = r0.menuView
            r2.setMenuView(r3)
            com.google.android.material.navigation.NavigationBarPresenter r2 = r0.presenter
            r3 = 1
            r2.setId(r3)
            com.google.android.material.navigation.NavigationBarMenuView r2 = r0.menuView
            com.google.android.material.navigation.NavigationBarPresenter r4 = r0.presenter
            r2.setPresenter(r4)
            com.google.android.material.navigation.NavigationBarMenu r2 = r0.menu
            com.google.android.material.navigation.NavigationBarPresenter r4 = r0.presenter
            r2.addMenuPresenter(r4)
            com.google.android.material.navigation.NavigationBarPresenter r2 = r0.presenter
            android.content.Context r4 = r17.getContext()
            com.google.android.material.navigation.NavigationBarMenu r5 = r0.menu
            r2.initForMenu(r4, r5)
            int r2 = com.google.android.material.R.styleable.NavigationBarView_itemIconTint
            boolean r2 = r1.hasValue(r2)
            if (r2 == 0) goto L_0x007c
            com.google.android.material.navigation.NavigationBarMenuView r2 = r0.menuView
            int r4 = com.google.android.material.R.styleable.NavigationBarView_itemIconTint
            android.content.res.ColorStateList r4 = r1.getColorStateList(r4)
            r2.setIconTintList(r4)
            goto L_0x008a
        L_0x007c:
            com.google.android.material.navigation.NavigationBarMenuView r2 = r0.menuView
            com.google.android.material.navigation.NavigationBarMenuView r4 = r0.menuView
            r5 = 16842808(0x1010038, float:2.3693715E-38)
            android.content.res.ColorStateList r4 = r4.createDefaultColorStateList(r5)
            r2.setIconTintList(r4)
        L_0x008a:
            int r2 = com.google.android.material.R.styleable.NavigationBarView_itemIconSize
            android.content.res.Resources r4 = r17.getResources()
            int r5 = com.google.android.material.R.dimen.mtrl_navigation_bar_item_default_icon_size
            int r4 = r4.getDimensionPixelSize(r5)
            int r2 = r1.getDimensionPixelSize(r2, r4)
            r0.setItemIconSize(r2)
            int r2 = com.google.android.material.R.styleable.NavigationBarView_itemTextAppearanceInactive
            boolean r2 = r1.hasValue(r2)
            r4 = 0
            if (r2 == 0) goto L_0x00af
            int r2 = com.google.android.material.R.styleable.NavigationBarView_itemTextAppearanceInactive
            int r2 = r1.getResourceId(r2, r4)
            r0.setItemTextAppearanceInactive(r2)
        L_0x00af:
            int r2 = com.google.android.material.R.styleable.NavigationBarView_itemTextAppearanceActive
            boolean r2 = r1.hasValue(r2)
            if (r2 == 0) goto L_0x00c0
            int r2 = com.google.android.material.R.styleable.NavigationBarView_itemTextAppearanceActive
            int r2 = r1.getResourceId(r2, r4)
            r0.setItemTextAppearanceActive(r2)
        L_0x00c0:
            int r2 = com.google.android.material.R.styleable.NavigationBarView_itemTextAppearanceActiveBoldEnabled
            boolean r2 = r1.getBoolean(r2, r3)
            r0.setItemTextAppearanceActiveBoldEnabled(r2)
            int r5 = com.google.android.material.R.styleable.NavigationBarView_itemTextColor
            boolean r5 = r1.hasValue(r5)
            if (r5 == 0) goto L_0x00da
            int r5 = com.google.android.material.R.styleable.NavigationBarView_itemTextColor
            android.content.res.ColorStateList r5 = r1.getColorStateList(r5)
            r0.setItemTextColor(r5)
        L_0x00da:
            android.graphics.drawable.Drawable r5 = r17.getBackground()
            if (r5 == 0) goto L_0x00ec
            android.graphics.drawable.Drawable r5 = r17.getBackground()
            boolean r5 = r5 instanceof android.graphics.drawable.ColorDrawable
            if (r5 == 0) goto L_0x00e9
            goto L_0x00ec
        L_0x00e9:
            r5 = r21
            goto L_0x00fe
        L_0x00ec:
            r5 = r21
            com.google.android.material.shape.ShapeAppearanceModel$Builder r6 = com.google.android.material.shape.ShapeAppearanceModel.builder((android.content.Context) r9, (android.util.AttributeSet) r7, (int) r8, (int) r5)
            com.google.android.material.shape.ShapeAppearanceModel r6 = r6.build()
            com.google.android.material.shape.MaterialShapeDrawable r6 = r0.createMaterialShapeDrawableBackground(r9, r6)
            androidx.core.view.ViewCompat.setBackground(r0, r6)
        L_0x00fe:
            int r6 = com.google.android.material.R.styleable.NavigationBarView_itemPaddingTop
            boolean r6 = r1.hasValue(r6)
            if (r6 == 0) goto L_0x010f
            int r6 = com.google.android.material.R.styleable.NavigationBarView_itemPaddingTop
            int r6 = r1.getDimensionPixelSize(r6, r4)
            r0.setItemPaddingTop(r6)
        L_0x010f:
            int r6 = com.google.android.material.R.styleable.NavigationBarView_itemPaddingBottom
            boolean r6 = r1.hasValue(r6)
            if (r6 == 0) goto L_0x0120
            int r6 = com.google.android.material.R.styleable.NavigationBarView_itemPaddingBottom
            int r6 = r1.getDimensionPixelSize(r6, r4)
            r0.setItemPaddingBottom(r6)
        L_0x0120:
            int r6 = com.google.android.material.R.styleable.NavigationBarView_activeIndicatorLabelPadding
            boolean r6 = r1.hasValue(r6)
            if (r6 == 0) goto L_0x0131
            int r6 = com.google.android.material.R.styleable.NavigationBarView_activeIndicatorLabelPadding
            int r6 = r1.getDimensionPixelSize(r6, r4)
            r0.setActiveIndicatorLabelPadding(r6)
        L_0x0131:
            int r6 = com.google.android.material.R.styleable.NavigationBarView_elevation
            boolean r6 = r1.hasValue(r6)
            if (r6 == 0) goto L_0x0143
            int r6 = com.google.android.material.R.styleable.NavigationBarView_elevation
            int r6 = r1.getDimensionPixelSize(r6, r4)
            float r6 = (float) r6
            r0.setElevation(r6)
        L_0x0143:
            int r6 = com.google.android.material.R.styleable.NavigationBarView_backgroundTint
            android.content.res.ColorStateList r6 = com.google.android.material.resources.MaterialResources.getColorStateList((android.content.Context) r9, (androidx.appcompat.widget.TintTypedArray) r1, (int) r6)
            android.graphics.drawable.Drawable r10 = r17.getBackground()
            android.graphics.drawable.Drawable r10 = r10.mutate()
            androidx.core.graphics.drawable.DrawableCompat.setTintList(r10, r6)
            int r10 = com.google.android.material.R.styleable.NavigationBarView_labelVisibilityMode
            r11 = -1
            int r10 = r1.getInteger(r10, r11)
            r0.setLabelVisibilityMode(r10)
            int r10 = com.google.android.material.R.styleable.NavigationBarView_itemBackground
            int r10 = r1.getResourceId(r10, r4)
            if (r10 == 0) goto L_0x016c
            com.google.android.material.navigation.NavigationBarMenuView r11 = r0.menuView
            r11.setItemBackgroundRes(r10)
            goto L_0x0175
        L_0x016c:
            int r11 = com.google.android.material.R.styleable.NavigationBarView_itemRippleColor
            android.content.res.ColorStateList r11 = com.google.android.material.resources.MaterialResources.getColorStateList((android.content.Context) r9, (androidx.appcompat.widget.TintTypedArray) r1, (int) r11)
            r0.setItemRippleColor(r11)
        L_0x0175:
            int r11 = com.google.android.material.R.styleable.NavigationBarView_itemActiveIndicatorStyle
            int r11 = r1.getResourceId(r11, r4)
            if (r11 == 0) goto L_0x01c2
            r0.setItemActiveIndicatorEnabled(r3)
            int[] r3 = com.google.android.material.R.styleable.NavigationBarActiveIndicator
            android.content.res.TypedArray r3 = r9.obtainStyledAttributes(r11, r3)
            int r12 = com.google.android.material.R.styleable.NavigationBarActiveIndicator_android_width
            int r12 = r3.getDimensionPixelSize(r12, r4)
            r0.setItemActiveIndicatorWidth(r12)
            int r13 = com.google.android.material.R.styleable.NavigationBarActiveIndicator_android_height
            int r13 = r3.getDimensionPixelSize(r13, r4)
            r0.setItemActiveIndicatorHeight(r13)
            int r14 = com.google.android.material.R.styleable.NavigationBarActiveIndicator_marginHorizontal
            int r14 = r3.getDimensionPixelOffset(r14, r4)
            r0.setItemActiveIndicatorMarginHorizontal(r14)
            int r15 = com.google.android.material.R.styleable.NavigationBarActiveIndicator_android_color
            android.content.res.ColorStateList r15 = com.google.android.material.resources.MaterialResources.getColorStateList((android.content.Context) r9, (android.content.res.TypedArray) r3, (int) r15)
            r0.setItemActiveIndicatorColor(r15)
            r18 = r2
            int r2 = com.google.android.material.R.styleable.NavigationBarActiveIndicator_shapeAppearance
            int r2 = r3.getResourceId(r2, r4)
            com.google.android.material.shape.ShapeAppearanceModel$Builder r16 = com.google.android.material.shape.ShapeAppearanceModel.builder(r9, r2, r4)
            com.google.android.material.shape.ShapeAppearanceModel r4 = r16.build()
            r0.setItemActiveIndicatorShapeAppearance(r4)
            r3.recycle()
            goto L_0x01c4
        L_0x01c2:
            r18 = r2
        L_0x01c4:
            int r2 = com.google.android.material.R.styleable.NavigationBarView_menu
            boolean r2 = r1.hasValue(r2)
            if (r2 == 0) goto L_0x01d6
            int r2 = com.google.android.material.R.styleable.NavigationBarView_menu
            r3 = 0
            int r2 = r1.getResourceId(r2, r3)
            r0.inflateMenu(r2)
        L_0x01d6:
            r1.recycle()
            com.google.android.material.navigation.NavigationBarMenuView r2 = r0.menuView
            r0.addView(r2)
            com.google.android.material.navigation.NavigationBarMenu r2 = r0.menu
            com.google.android.material.navigation.NavigationBarView$1 r3 = new com.google.android.material.navigation.NavigationBarView$1
            r3.<init>()
            r2.setCallback(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.navigation.NavigationBarView.<init>(android.content.Context, android.util.AttributeSet, int, int):void");
    }

    private MaterialShapeDrawable createMaterialShapeDrawableBackground(Context context, ShapeAppearanceModel shapeAppearanceModel) {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        Drawable originalBackground = getBackground();
        if (originalBackground instanceof ColorDrawable) {
            materialShapeDrawable.setFillColor(ColorStateList.valueOf(((ColorDrawable) originalBackground).getColor()));
        }
        materialShapeDrawable.initializeElevationOverlay(context);
        materialShapeDrawable.setShapeAppearanceModel(shapeAppearanceModel);
        return materialShapeDrawable;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        MaterialShapeUtils.setParentAbsoluteElevation(this);
    }

    public void setElevation(float elevation) {
        super.setElevation(elevation);
        MaterialShapeUtils.setElevation(this, elevation);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.selectedListener = listener;
    }

    public void setOnItemReselectedListener(OnItemReselectedListener listener) {
        this.reselectedListener = listener;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public MenuView getMenuView() {
        return this.menuView;
    }

    public void inflateMenu(int resId) {
        this.presenter.setUpdateSuspended(true);
        getMenuInflater().inflate(resId, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(true);
    }

    public ColorStateList getItemIconTintList() {
        return this.menuView.getIconTintList();
    }

    public void setItemIconTintList(ColorStateList tint) {
        this.menuView.setIconTintList(tint);
    }

    public void setItemIconSize(int iconSize) {
        this.menuView.setItemIconSize(iconSize);
    }

    public void setItemIconSizeRes(int iconSizeRes) {
        setItemIconSize(getResources().getDimensionPixelSize(iconSizeRes));
    }

    public int getItemIconSize() {
        return this.menuView.getItemIconSize();
    }

    public ColorStateList getItemTextColor() {
        return this.menuView.getItemTextColor();
    }

    public void setItemTextColor(ColorStateList textColor) {
        this.menuView.setItemTextColor(textColor);
    }

    @Deprecated
    public int getItemBackgroundResource() {
        return this.menuView.getItemBackgroundRes();
    }

    public void setItemBackgroundResource(int resId) {
        this.menuView.setItemBackgroundRes(resId);
    }

    public Drawable getItemBackground() {
        return this.menuView.getItemBackground();
    }

    public void setItemBackground(Drawable background) {
        this.menuView.setItemBackground(background);
    }

    public ColorStateList getItemRippleColor() {
        return this.menuView.getItemRippleColor();
    }

    public void setItemRippleColor(ColorStateList itemRippleColor) {
        this.menuView.setItemRippleColor(itemRippleColor);
    }

    public int getItemPaddingTop() {
        return this.menuView.getItemPaddingTop();
    }

    public void setItemPaddingTop(int paddingTop) {
        this.menuView.setItemPaddingTop(paddingTop);
    }

    public int getItemPaddingBottom() {
        return this.menuView.getItemPaddingBottom();
    }

    public void setItemPaddingBottom(int paddingBottom) {
        this.menuView.setItemPaddingBottom(paddingBottom);
    }

    public void setActiveIndicatorLabelPadding(int activeIndicatorLabelPadding) {
        this.menuView.setActiveIndicatorLabelPadding(activeIndicatorLabelPadding);
    }

    public int getActiveIndicatorLabelPadding() {
        return this.menuView.getActiveIndicatorLabelPadding();
    }

    public boolean isItemActiveIndicatorEnabled() {
        return this.menuView.getItemActiveIndicatorEnabled();
    }

    public void setItemActiveIndicatorEnabled(boolean enabled) {
        this.menuView.setItemActiveIndicatorEnabled(enabled);
    }

    public int getItemActiveIndicatorWidth() {
        return this.menuView.getItemActiveIndicatorWidth();
    }

    public void setItemActiveIndicatorWidth(int width) {
        this.menuView.setItemActiveIndicatorWidth(width);
    }

    public int getItemActiveIndicatorHeight() {
        return this.menuView.getItemActiveIndicatorHeight();
    }

    public void setItemActiveIndicatorHeight(int height) {
        this.menuView.setItemActiveIndicatorHeight(height);
    }

    public int getItemActiveIndicatorMarginHorizontal() {
        return this.menuView.getItemActiveIndicatorMarginHorizontal();
    }

    public void setItemActiveIndicatorMarginHorizontal(int horizontalMargin) {
        this.menuView.setItemActiveIndicatorMarginHorizontal(horizontalMargin);
    }

    public ShapeAppearanceModel getItemActiveIndicatorShapeAppearance() {
        return this.menuView.getItemActiveIndicatorShapeAppearance();
    }

    public void setItemActiveIndicatorShapeAppearance(ShapeAppearanceModel shapeAppearance) {
        this.menuView.setItemActiveIndicatorShapeAppearance(shapeAppearance);
    }

    public ColorStateList getItemActiveIndicatorColor() {
        return this.menuView.getItemActiveIndicatorColor();
    }

    public void setItemActiveIndicatorColor(ColorStateList csl) {
        this.menuView.setItemActiveIndicatorColor(csl);
    }

    public int getSelectedItemId() {
        return this.menuView.getSelectedItemId();
    }

    public void setSelectedItemId(int itemId) {
        MenuItem item = this.menu.findItem(itemId);
        if (item != null && !this.menu.performItemAction(item, this.presenter, 0)) {
            item.setChecked(true);
        }
    }

    public void setLabelVisibilityMode(int labelVisibilityMode) {
        if (this.menuView.getLabelVisibilityMode() != labelVisibilityMode) {
            this.menuView.setLabelVisibilityMode(labelVisibilityMode);
            this.presenter.updateMenuView(false);
        }
    }

    public int getLabelVisibilityMode() {
        return this.menuView.getLabelVisibilityMode();
    }

    public void setItemTextAppearanceInactive(int textAppearanceRes) {
        this.menuView.setItemTextAppearanceInactive(textAppearanceRes);
    }

    public int getItemTextAppearanceInactive() {
        return this.menuView.getItemTextAppearanceInactive();
    }

    public void setItemTextAppearanceActive(int textAppearanceRes) {
        this.menuView.setItemTextAppearanceActive(textAppearanceRes);
    }

    public void setItemTextAppearanceActiveBoldEnabled(boolean isBold) {
        this.menuView.setItemTextAppearanceActiveBoldEnabled(isBold);
    }

    public int getItemTextAppearanceActive() {
        return this.menuView.getItemTextAppearanceActive();
    }

    public void setItemOnTouchListener(int menuItemId, View.OnTouchListener onTouchListener) {
        this.menuView.setItemOnTouchListener(menuItemId, onTouchListener);
    }

    public BadgeDrawable getBadge(int menuItemId) {
        return this.menuView.getBadge(menuItemId);
    }

    public BadgeDrawable getOrCreateBadge(int menuItemId) {
        return this.menuView.getOrCreateBadge(menuItemId);
    }

    public void removeBadge(int menuItemId) {
        this.menuView.removeBadge(menuItemId);
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new SupportMenuInflater(getContext());
        }
        return this.menuInflater;
    }

    public NavigationBarPresenter getPresenter() {
        return this.presenter;
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.menuPresenterState = new Bundle();
        this.menu.savePresenterStates(savedState.menuPresenterState);
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.menu.restorePresenterStates(savedState.menuPresenterState);
    }

    static class SavedState extends AbsSavedState {
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
        Bundle menuPresenterState;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            readFromParcel(source, loader == null ? getClass().getClassLoader() : loader);
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(this.menuPresenterState);
        }

        private void readFromParcel(Parcel in, ClassLoader loader) {
            this.menuPresenterState = in.readBundle(loader);
        }
    }
}
