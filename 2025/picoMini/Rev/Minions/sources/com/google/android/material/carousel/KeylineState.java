package com.google.android.material.carousel;

import com.google.android.material.animation.AnimationUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class KeylineState {
    private final int firstFocalKeylineIndex;
    private final float itemSize;
    private final List<Keyline> keylines;
    private final int lastFocalKeylineIndex;

    private KeylineState(float itemSize2, List<Keyline> keylines2, int firstFocalKeylineIndex2, int lastFocalKeylineIndex2) {
        this.itemSize = itemSize2;
        this.keylines = Collections.unmodifiableList(keylines2);
        this.firstFocalKeylineIndex = firstFocalKeylineIndex2;
        this.lastFocalKeylineIndex = lastFocalKeylineIndex2;
    }

    /* access modifiers changed from: package-private */
    public float getItemSize() {
        return this.itemSize;
    }

    /* access modifiers changed from: package-private */
    public List<Keyline> getKeylines() {
        return this.keylines;
    }

    /* access modifiers changed from: package-private */
    public Keyline getFirstFocalKeyline() {
        return this.keylines.get(this.firstFocalKeylineIndex);
    }

    /* access modifiers changed from: package-private */
    public int getFirstFocalKeylineIndex() {
        return this.firstFocalKeylineIndex;
    }

    /* access modifiers changed from: package-private */
    public Keyline getLastFocalKeyline() {
        return this.keylines.get(this.lastFocalKeylineIndex);
    }

    /* access modifiers changed from: package-private */
    public int getLastFocalKeylineIndex() {
        return this.lastFocalKeylineIndex;
    }

    /* access modifiers changed from: package-private */
    public Keyline getFirstKeyline() {
        return this.keylines.get(0);
    }

    /* access modifiers changed from: package-private */
    public Keyline getLastKeyline() {
        return this.keylines.get(this.keylines.size() - 1);
    }

    static KeylineState lerp(KeylineState from, KeylineState to, float progress) {
        if (from.getItemSize() == to.getItemSize()) {
            List<Keyline> fromKeylines = from.getKeylines();
            List<Keyline> toKeylines = to.getKeylines();
            if (fromKeylines.size() == toKeylines.size()) {
                List<Keyline> keylines2 = new ArrayList<>();
                for (int i = 0; i < from.getKeylines().size(); i++) {
                    keylines2.add(Keyline.lerp(fromKeylines.get(i), toKeylines.get(i), progress));
                }
                return new KeylineState(from.getItemSize(), keylines2, AnimationUtils.lerp(from.getFirstFocalKeylineIndex(), to.getFirstFocalKeylineIndex(), progress), AnimationUtils.lerp(from.getLastFocalKeylineIndex(), to.getLastFocalKeylineIndex(), progress));
            }
            throw new IllegalArgumentException("Keylines being linearly interpolated must have the same number of keylines.");
        }
        throw new IllegalArgumentException("Keylines being linearly interpolated must have the same item size.");
    }

    static KeylineState reverse(KeylineState keylineState) {
        Builder builder = new Builder(keylineState.getItemSize());
        float start = keylineState.getFirstKeyline().locOffset - (keylineState.getFirstKeyline().maskedItemSize / 2.0f);
        int i = keylineState.getKeylines().size() - 1;
        while (i >= 0) {
            Keyline k = keylineState.getKeylines().get(i);
            builder.addKeyline((k.maskedItemSize / 2.0f) + start, k.mask, k.maskedItemSize, i >= keylineState.getFirstFocalKeylineIndex() && i <= keylineState.getLastFocalKeylineIndex());
            start += k.maskedItemSize;
            i--;
        }
        return builder.build();
    }

    static final class Builder {
        private static final int NO_INDEX = -1;
        private static final float UNKNOWN_LOC = Float.MIN_VALUE;
        private int firstFocalKeylineIndex = -1;
        private final float itemSize;
        private int lastFocalKeylineIndex = -1;
        private float lastKeylineMaskedSize = 0.0f;
        private Keyline tmpFirstFocalKeyline;
        private final List<Keyline> tmpKeylines = new ArrayList();
        private Keyline tmpLastFocalKeyline;

        Builder(float itemSize2) {
            this.itemSize = itemSize2;
        }

        /* access modifiers changed from: package-private */
        public Builder addKeyline(float offsetLoc, float mask, float maskedItemSize) {
            return addKeyline(offsetLoc, mask, maskedItemSize, false);
        }

        /* access modifiers changed from: package-private */
        public Builder addKeyline(float offsetLoc, float mask, float maskedItemSize, boolean isFocal) {
            if (maskedItemSize <= 0.0f) {
                return this;
            }
            Keyline tmpKeyline = new Keyline(Float.MIN_VALUE, offsetLoc, mask, maskedItemSize);
            if (isFocal) {
                if (this.tmpFirstFocalKeyline == null) {
                    this.tmpFirstFocalKeyline = tmpKeyline;
                    this.firstFocalKeylineIndex = this.tmpKeylines.size();
                }
                if (this.lastFocalKeylineIndex != -1 && this.tmpKeylines.size() - this.lastFocalKeylineIndex > 1) {
                    throw new IllegalArgumentException("Keylines marked as focal must be placed next to each other. There cannot be non-focal keylines between focal keylines.");
                } else if (maskedItemSize == this.tmpFirstFocalKeyline.maskedItemSize) {
                    this.tmpLastFocalKeyline = tmpKeyline;
                    this.lastFocalKeylineIndex = this.tmpKeylines.size();
                } else {
                    throw new IllegalArgumentException("Keylines that are marked as focal must all have the same masked item size.");
                }
            } else if (this.tmpFirstFocalKeyline == null && tmpKeyline.maskedItemSize < this.lastKeylineMaskedSize) {
                throw new IllegalArgumentException("Keylines before the first focal keyline must be ordered by incrementing masked item size.");
            } else if (this.tmpLastFocalKeyline != null && tmpKeyline.maskedItemSize > this.lastKeylineMaskedSize) {
                throw new IllegalArgumentException("Keylines after the last focal keyline must be ordered by decreasing masked item size.");
            }
            this.lastKeylineMaskedSize = tmpKeyline.maskedItemSize;
            this.tmpKeylines.add(tmpKeyline);
            return this;
        }

        /* access modifiers changed from: package-private */
        public Builder addKeylineRange(float offsetLoc, float mask, float maskedItemSize, int count) {
            return addKeylineRange(offsetLoc, mask, maskedItemSize, count, false);
        }

        /* access modifiers changed from: package-private */
        public Builder addKeylineRange(float offsetLoc, float mask, float maskedItemSize, int count, boolean isFocal) {
            if (count <= 0 || maskedItemSize <= 0.0f) {
                return this;
            }
            for (int i = 0; i < count; i++) {
                addKeyline((((float) i) * maskedItemSize) + offsetLoc, mask, maskedItemSize, isFocal);
            }
            return this;
        }

        /* access modifiers changed from: package-private */
        public KeylineState build() {
            if (this.tmpFirstFocalKeyline != null) {
                List<Keyline> keylines = new ArrayList<>();
                for (int i = 0; i < this.tmpKeylines.size(); i++) {
                    Keyline tmpKeyline = this.tmpKeylines.get(i);
                    keylines.add(new Keyline(calculateKeylineLocationForItemPosition(this.tmpFirstFocalKeyline.locOffset, this.itemSize, this.firstFocalKeylineIndex, i), tmpKeyline.locOffset, tmpKeyline.mask, tmpKeyline.maskedItemSize));
                }
                return new KeylineState(this.itemSize, keylines, this.firstFocalKeylineIndex, this.lastFocalKeylineIndex);
            }
            throw new IllegalStateException("There must be a keyline marked as focal.");
        }

        private static float calculateKeylineLocationForItemPosition(float firstFocalLoc, float itemSize2, int firstFocalPosition, int itemPosition) {
            return (firstFocalLoc - (((float) firstFocalPosition) * itemSize2)) + (((float) itemPosition) * itemSize2);
        }
    }

    static final class Keyline {
        final float loc;
        final float locOffset;
        final float mask;
        final float maskedItemSize;

        Keyline(float loc2, float locOffset2, float mask2, float maskedItemSize2) {
            this.loc = loc2;
            this.locOffset = locOffset2;
            this.mask = mask2;
            this.maskedItemSize = maskedItemSize2;
        }

        static Keyline lerp(Keyline from, Keyline to, float progress) {
            return new Keyline(AnimationUtils.lerp(from.loc, to.loc, progress), AnimationUtils.lerp(from.locOffset, to.locOffset, progress), AnimationUtils.lerp(from.mask, to.mask, progress), AnimationUtils.lerp(from.maskedItemSize, to.maskedItemSize, progress));
        }
    }
}
