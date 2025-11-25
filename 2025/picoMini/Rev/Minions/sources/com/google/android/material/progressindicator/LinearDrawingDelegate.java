package com.google.android.material.progressindicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import com.google.android.material.color.MaterialColors;

final class LinearDrawingDelegate extends DrawingDelegate<LinearProgressIndicatorSpec> {
    private float displayedCornerRadius;
    private Path displayedTrackPath;
    private float displayedTrackThickness;
    private float trackLength = 300.0f;

    public LinearDrawingDelegate(LinearProgressIndicatorSpec spec) {
        super(spec);
    }

    public int getPreferredWidth() {
        return -1;
    }

    public int getPreferredHeight() {
        return ((LinearProgressIndicatorSpec) this.spec).trackThickness;
    }

    public void adjustCanvas(Canvas canvas, Rect bounds, float trackThicknessFraction) {
        this.trackLength = (float) bounds.width();
        float trackSize = (float) ((LinearProgressIndicatorSpec) this.spec).trackThickness;
        canvas.translate(((float) bounds.left) + (((float) bounds.width()) / 2.0f), ((float) bounds.top) + (((float) bounds.height()) / 2.0f) + Math.max(0.0f, ((float) (bounds.height() - ((LinearProgressIndicatorSpec) this.spec).trackThickness)) / 2.0f));
        if (((LinearProgressIndicatorSpec) this.spec).drawHorizontallyInverse) {
            canvas.scale(-1.0f, 1.0f);
        }
        if ((this.drawable.isShowing() && ((LinearProgressIndicatorSpec) this.spec).showAnimationBehavior == 1) || (this.drawable.isHiding() && ((LinearProgressIndicatorSpec) this.spec).hideAnimationBehavior == 2)) {
            canvas.scale(1.0f, -1.0f);
        }
        if (this.drawable.isShowing() || this.drawable.isHiding()) {
            canvas.translate(0.0f, (((float) ((LinearProgressIndicatorSpec) this.spec).trackThickness) * (trackThicknessFraction - 1.0f)) / 2.0f);
        }
        canvas.clipRect((-this.trackLength) / 2.0f, (-trackSize) / 2.0f, this.trackLength / 2.0f, trackSize / 2.0f);
        this.displayedTrackThickness = ((float) ((LinearProgressIndicatorSpec) this.spec).trackThickness) * trackThicknessFraction;
        this.displayedCornerRadius = ((float) ((LinearProgressIndicatorSpec) this.spec).trackCornerRadius) * trackThicknessFraction;
    }

    public void fillIndicator(Canvas canvas, Paint paint, float startFraction, float endFraction, int color) {
        if (startFraction != endFraction) {
            float originX = (-this.trackLength) / 2.0f;
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            paint.setColor(color);
            canvas.save();
            canvas.clipPath(this.displayedTrackPath);
            canvas.drawRoundRect(new RectF(((this.trackLength * startFraction) + originX) - (this.displayedCornerRadius * 2.0f), (-this.displayedTrackThickness) / 2.0f, (this.trackLength * endFraction) + originX, this.displayedTrackThickness / 2.0f), this.displayedCornerRadius, this.displayedCornerRadius, paint);
            canvas.restore();
        }
    }

    /* access modifiers changed from: package-private */
    public void fillTrack(Canvas canvas, Paint paint) {
        int trackColor = MaterialColors.compositeARGBWithAlpha(((LinearProgressIndicatorSpec) this.spec).trackColor, this.drawable.getAlpha());
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(trackColor);
        this.displayedTrackPath = new Path();
        this.displayedTrackPath.addRoundRect(new RectF((-this.trackLength) / 2.0f, (-this.displayedTrackThickness) / 2.0f, this.trackLength / 2.0f, this.displayedTrackThickness / 2.0f), this.displayedCornerRadius, this.displayedCornerRadius, Path.Direction.CCW);
        canvas.drawPath(this.displayedTrackPath, paint);
    }
}
