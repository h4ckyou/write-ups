package androidx.constraintlayout.core.motion;

import androidx.constraintlayout.core.motion.utils.Easing;
import androidx.constraintlayout.core.motion.utils.Rect;
import java.util.HashSet;
import java.util.LinkedHashMap;

class MotionConstrainedPoint implements Comparable<MotionConstrainedPoint> {
    static final int CARTESIAN = 2;
    public static final boolean DEBUG = false;
    static final int PERPENDICULAR = 1;
    public static final String TAG = "MotionPaths";
    static String[] names = {"position", "x", "y", "width", "height", "pathRotate"};
    private float alpha = 1.0f;
    private boolean applyElevation = false;
    private float elevation = 0.0f;
    private float height;
    private int mAnimateRelativeTo = -1;
    LinkedHashMap<String, CustomVariable> mCustomVariable = new LinkedHashMap<>();
    private int mDrawPath = 0;
    private Easing mKeyFrameEasing;
    int mMode = 0;
    private float mPathRotate = Float.NaN;
    private float mPivotX = Float.NaN;
    private float mPivotY = Float.NaN;
    private float mProgress = Float.NaN;
    double[] mTempDelta = new double[18];
    double[] mTempValue = new double[18];
    int mVisibilityMode = 0;
    private float position;
    private float rotation = 0.0f;
    private float rotationX = 0.0f;
    public float rotationY = 0.0f;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float translationX = 0.0f;
    private float translationY = 0.0f;
    private float translationZ = 0.0f;
    int visibility;
    private float width;
    private float x;
    private float y;

    private boolean diff(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) {
            if (Float.isNaN(a) != Float.isNaN(b)) {
                return true;
            }
            return false;
        } else if (Math.abs(a - b) > 1.0E-6f) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void different(MotionConstrainedPoint points, HashSet<String> keySet) {
        if (diff(this.alpha, points.alpha)) {
            keySet.add("alpha");
        }
        if (diff(this.elevation, points.elevation)) {
            keySet.add("translationZ");
        }
        if (this.visibility != points.visibility && this.mVisibilityMode == 0 && (this.visibility == 4 || points.visibility == 4)) {
            keySet.add("alpha");
        }
        if (diff(this.rotation, points.rotation)) {
            keySet.add("rotationZ");
        }
        if (!Float.isNaN(this.mPathRotate) || !Float.isNaN(points.mPathRotate)) {
            keySet.add("pathRotate");
        }
        if (!Float.isNaN(this.mProgress) || !Float.isNaN(points.mProgress)) {
            keySet.add("progress");
        }
        if (diff(this.rotationX, points.rotationX)) {
            keySet.add("rotationX");
        }
        if (diff(this.rotationY, points.rotationY)) {
            keySet.add("rotationY");
        }
        if (diff(this.mPivotX, points.mPivotX)) {
            keySet.add("pivotX");
        }
        if (diff(this.mPivotY, points.mPivotY)) {
            keySet.add("pivotY");
        }
        if (diff(this.scaleX, points.scaleX)) {
            keySet.add("scaleX");
        }
        if (diff(this.scaleY, points.scaleY)) {
            keySet.add("scaleY");
        }
        if (diff(this.translationX, points.translationX)) {
            keySet.add("translationX");
        }
        if (diff(this.translationY, points.translationY)) {
            keySet.add("translationY");
        }
        if (diff(this.translationZ, points.translationZ)) {
            keySet.add("translationZ");
        }
        if (diff(this.elevation, points.elevation)) {
            keySet.add("elevation");
        }
    }

    /* access modifiers changed from: package-private */
    public void different(MotionConstrainedPoint points, boolean[] mask, String[] custom) {
        int c = 0 + 1;
        mask[0] = mask[0] | diff(this.position, points.position);
        int c2 = c + 1;
        mask[c] = mask[c] | diff(this.x, points.x);
        int c3 = c2 + 1;
        mask[c2] = mask[c2] | diff(this.y, points.y);
        int c4 = c3 + 1;
        mask[c3] = mask[c3] | diff(this.width, points.width);
        int i = c4 + 1;
        mask[c4] = mask[c4] | diff(this.height, points.height);
    }

    /* access modifiers changed from: package-private */
    public void fillStandard(double[] data, int[] toUse) {
        int[] iArr = toUse;
        float[] set = {this.position, this.x, this.y, this.width, this.height, this.alpha, this.elevation, this.rotation, this.rotationX, this.rotationY, this.scaleX, this.scaleY, this.mPivotX, this.mPivotY, this.translationX, this.translationY, this.translationZ, this.mPathRotate};
        int c = 0;
        int i = 0;
        while (true) {
            int[] iArr2 = toUse;
            if (i < iArr2.length) {
                if (iArr2[i] < set.length) {
                    data[c] = (double) set[iArr2[i]];
                    c++;
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasCustomData(String name) {
        return this.mCustomVariable.containsKey(name);
    }

    /* access modifiers changed from: package-private */
    public int getCustomDataCount(String name) {
        return this.mCustomVariable.get(name).numberOfInterpolatedValues();
    }

    /* access modifiers changed from: package-private */
    public int getCustomData(String name, double[] value, int offset) {
        CustomVariable a = this.mCustomVariable.get(name);
        if (a.numberOfInterpolatedValues() == 1) {
            value[offset] = (double) a.getValueToInterpolate();
            return 1;
        }
        int N = a.numberOfInterpolatedValues();
        float[] f = new float[N];
        a.getValuesToInterpolate(f);
        int i = 0;
        while (i < N) {
            value[offset] = (double) f[i];
            i++;
            offset++;
        }
        return N;
    }

    /* access modifiers changed from: package-private */
    public void setBounds(float x2, float y2, float w, float h) {
        this.x = x2;
        this.y = y2;
        this.width = w;
        this.height = h;
    }

    public int compareTo(MotionConstrainedPoint o) {
        return Float.compare(this.position, o.position);
    }

    public void applyParameters(MotionWidget view) {
        this.visibility = view.getVisibility();
        this.alpha = view.getVisibility() != 4 ? 0.0f : view.getAlpha();
        this.applyElevation = false;
        this.rotation = view.getRotationZ();
        this.rotationX = view.getRotationX();
        this.rotationY = view.getRotationY();
        this.scaleX = view.getScaleX();
        this.scaleY = view.getScaleY();
        this.mPivotX = view.getPivotX();
        this.mPivotY = view.getPivotY();
        this.translationX = view.getTranslationX();
        this.translationY = view.getTranslationY();
        this.translationZ = view.getTranslationZ();
        for (String s : view.getCustomAttributeNames()) {
            CustomVariable attr = view.getCustomAttribute(s);
            if (attr != null && attr.isContinuous()) {
                this.mCustomVariable.put(s, attr);
            }
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addValues(java.util.HashMap<java.lang.String, androidx.constraintlayout.core.motion.utils.SplineSet> r9, int r10) {
        /*
            r8 = this;
            java.util.Set r0 = r9.keySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0008:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x01f8
            java.lang.Object r1 = r0.next()
            java.lang.String r1 = (java.lang.String) r1
            java.lang.Object r2 = r9.get(r1)
            androidx.constraintlayout.core.motion.utils.SplineSet r2 = (androidx.constraintlayout.core.motion.utils.SplineSet) r2
            int r3 = r1.hashCode()
            r4 = 1
            switch(r3) {
                case -1249320806: goto L_0x00a7;
                case -1249320805: goto L_0x009d;
                case -1249320804: goto L_0x0093;
                case -1225497657: goto L_0x0087;
                case -1225497656: goto L_0x007b;
                case -1225497655: goto L_0x006f;
                case -1001078227: goto L_0x0065;
                case -987906986: goto L_0x005b;
                case -987906985: goto L_0x0051;
                case -908189618: goto L_0x0046;
                case -908189617: goto L_0x003a;
                case 92909918: goto L_0x002f;
                case 803192288: goto L_0x0024;
                default: goto L_0x0022;
            }
        L_0x0022:
            goto L_0x00b1
        L_0x0024:
            java.lang.String r3 = "pathRotate"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 6
            goto L_0x00b2
        L_0x002f:
            java.lang.String r3 = "alpha"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 0
            goto L_0x00b2
        L_0x003a:
            java.lang.String r3 = "scaleY"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 9
            goto L_0x00b2
        L_0x0046:
            java.lang.String r3 = "scaleX"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 8
            goto L_0x00b2
        L_0x0051:
            java.lang.String r3 = "pivotY"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 5
            goto L_0x00b2
        L_0x005b:
            java.lang.String r3 = "pivotX"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 4
            goto L_0x00b2
        L_0x0065:
            java.lang.String r3 = "progress"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 7
            goto L_0x00b2
        L_0x006f:
            java.lang.String r3 = "translationZ"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 12
            goto L_0x00b2
        L_0x007b:
            java.lang.String r3 = "translationY"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 11
            goto L_0x00b2
        L_0x0087:
            java.lang.String r3 = "translationX"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 10
            goto L_0x00b2
        L_0x0093:
            java.lang.String r3 = "rotationZ"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = r4
            goto L_0x00b2
        L_0x009d:
            java.lang.String r3 = "rotationY"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 3
            goto L_0x00b2
        L_0x00a7:
            java.lang.String r3 = "rotationX"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L_0x0022
            r3 = 2
            goto L_0x00b2
        L_0x00b1:
            r3 = -1
        L_0x00b2:
            r5 = 1065353216(0x3f800000, float:1.0)
            r6 = 0
            switch(r3) {
                case 0: goto L_0x01a4;
                case 1: goto L_0x0195;
                case 2: goto L_0x0186;
                case 3: goto L_0x0176;
                case 4: goto L_0x0166;
                case 5: goto L_0x0156;
                case 6: goto L_0x0146;
                case 7: goto L_0x0136;
                case 8: goto L_0x0126;
                case 9: goto L_0x0116;
                case 10: goto L_0x0106;
                case 11: goto L_0x00f6;
                case 12: goto L_0x00e6;
                default: goto L_0x00b8;
            }
        L_0x00b8:
            java.lang.String r3 = "CUSTOM"
            boolean r3 = r1.startsWith(r3)
            java.lang.String r5 = "MotionPaths"
            if (r3 == 0) goto L_0x01e0
            java.lang.String r3 = ","
            java.lang.String[] r3 = r1.split(r3)
            r3 = r3[r4]
            java.util.LinkedHashMap<java.lang.String, androidx.constraintlayout.core.motion.CustomVariable> r4 = r8.mCustomVariable
            boolean r4 = r4.containsKey(r3)
            if (r4 == 0) goto L_0x01df
            java.util.LinkedHashMap<java.lang.String, androidx.constraintlayout.core.motion.CustomVariable> r4 = r8.mCustomVariable
            java.lang.Object r4 = r4.get(r3)
            androidx.constraintlayout.core.motion.CustomVariable r4 = (androidx.constraintlayout.core.motion.CustomVariable) r4
            boolean r6 = r2 instanceof androidx.constraintlayout.core.motion.utils.SplineSet.CustomSpline
            if (r6 == 0) goto L_0x01b3
            r5 = r2
            androidx.constraintlayout.core.motion.utils.SplineSet$CustomSpline r5 = (androidx.constraintlayout.core.motion.utils.SplineSet.CustomSpline) r5
            r5.setPoint((int) r10, (androidx.constraintlayout.core.motion.CustomVariable) r4)
            goto L_0x01df
        L_0x00e6:
            float r3 = r8.translationZ
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x00ef
            goto L_0x00f1
        L_0x00ef:
            float r6 = r8.translationZ
        L_0x00f1:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x00f6:
            float r3 = r8.translationY
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x00ff
            goto L_0x0101
        L_0x00ff:
            float r6 = r8.translationY
        L_0x0101:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x0106:
            float r3 = r8.translationX
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x010f
            goto L_0x0111
        L_0x010f:
            float r6 = r8.translationX
        L_0x0111:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x0116:
            float r3 = r8.scaleY
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x011f
            goto L_0x0121
        L_0x011f:
            float r5 = r8.scaleY
        L_0x0121:
            r2.setPoint(r10, r5)
            goto L_0x01f6
        L_0x0126:
            float r3 = r8.scaleX
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x012f
            goto L_0x0131
        L_0x012f:
            float r5 = r8.scaleX
        L_0x0131:
            r2.setPoint(r10, r5)
            goto L_0x01f6
        L_0x0136:
            float r3 = r8.mProgress
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x013f
            goto L_0x0141
        L_0x013f:
            float r6 = r8.mProgress
        L_0x0141:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x0146:
            float r3 = r8.mPathRotate
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x014f
            goto L_0x0151
        L_0x014f:
            float r6 = r8.mPathRotate
        L_0x0151:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x0156:
            float r3 = r8.mPivotY
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x015f
            goto L_0x0161
        L_0x015f:
            float r6 = r8.mPivotY
        L_0x0161:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x0166:
            float r3 = r8.mPivotX
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x016f
            goto L_0x0171
        L_0x016f:
            float r6 = r8.mPivotX
        L_0x0171:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x0176:
            float r3 = r8.rotationY
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x017f
            goto L_0x0181
        L_0x017f:
            float r6 = r8.rotationY
        L_0x0181:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x0186:
            float r3 = r8.rotationX
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x018f
            goto L_0x0191
        L_0x018f:
            float r6 = r8.rotationX
        L_0x0191:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x0195:
            float r3 = r8.rotation
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x019e
            goto L_0x01a0
        L_0x019e:
            float r6 = r8.rotation
        L_0x01a0:
            r2.setPoint(r10, r6)
            goto L_0x01f6
        L_0x01a4:
            float r3 = r8.alpha
            boolean r3 = java.lang.Float.isNaN(r3)
            if (r3 == 0) goto L_0x01ad
            goto L_0x01af
        L_0x01ad:
            float r5 = r8.alpha
        L_0x01af:
            r2.setPoint(r10, r5)
            goto L_0x01f6
        L_0x01b3:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.StringBuilder r6 = r6.append(r1)
            java.lang.String r7 = " ViewSpline not a CustomSet frame = "
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.StringBuilder r6 = r6.append(r10)
            java.lang.String r7 = ", value"
            java.lang.StringBuilder r6 = r6.append(r7)
            float r7 = r4.getValueToInterpolate()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.StringBuilder r6 = r6.append(r2)
            java.lang.String r6 = r6.toString()
            androidx.constraintlayout.core.motion.utils.Utils.loge(r5, r6)
        L_0x01df:
            goto L_0x01f6
        L_0x01e0:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "UNKNOWN spline "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.StringBuilder r3 = r3.append(r1)
            java.lang.String r3 = r3.toString()
            androidx.constraintlayout.core.motion.utils.Utils.loge(r5, r3)
        L_0x01f6:
            goto L_0x0008
        L_0x01f8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.core.motion.MotionConstrainedPoint.addValues(java.util.HashMap, int):void");
    }

    public void setState(MotionWidget view) {
        setBounds((float) view.getX(), (float) view.getY(), (float) view.getWidth(), (float) view.getHeight());
        applyParameters(view);
    }

    public void setState(Rect rect, MotionWidget view, int rotation2, float prevous) {
        setBounds((float) rect.left, (float) rect.top, (float) rect.width(), (float) rect.height());
        applyParameters(view);
        this.mPivotX = Float.NaN;
        this.mPivotY = Float.NaN;
        switch (rotation2) {
            case 1:
                this.rotation = prevous - 90.0f;
                return;
            case 2:
                this.rotation = 90.0f + prevous;
                return;
            default:
                return;
        }
    }
}
