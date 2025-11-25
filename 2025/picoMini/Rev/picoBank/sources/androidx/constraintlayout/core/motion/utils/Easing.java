package androidx.constraintlayout.core.motion.utils;

public class Easing {
    private static final String ACCELERATE = "cubic(0.4, 0.05, 0.8, 0.7)";
    private static final String ACCELERATE_NAME = "accelerate";
    private static final String ANTICIPATE = "cubic(0.36, 0, 0.66, -0.56)";
    private static final String ANTICIPATE_NAME = "anticipate";
    private static final String DECELERATE = "cubic(0.0, 0.0, 0.2, 0.95)";
    private static final String DECELERATE_NAME = "decelerate";
    private static final String LINEAR = "cubic(1, 1, 0, 0)";
    private static final String LINEAR_NAME = "linear";
    public static String[] NAMED_EASING = {STANDARD_NAME, ACCELERATE_NAME, DECELERATE_NAME, LINEAR_NAME};
    private static final String OVERSHOOT = "cubic(0.34, 1.56, 0.64, 1)";
    private static final String OVERSHOOT_NAME = "overshoot";
    private static final String STANDARD = "cubic(0.4, 0.0, 0.2, 1)";
    private static final String STANDARD_NAME = "standard";
    static Easing sDefault = new Easing();
    String str = "identity";

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static androidx.constraintlayout.core.motion.utils.Easing getInterpolator(java.lang.String r3) {
        /*
            if (r3 != 0) goto L_0x0004
            r0 = 0
            return r0
        L_0x0004:
            java.lang.String r0 = "cubic"
            boolean r0 = r3.startsWith(r0)
            if (r0 == 0) goto L_0x0012
            androidx.constraintlayout.core.motion.utils.Easing$CubicEasing r0 = new androidx.constraintlayout.core.motion.utils.Easing$CubicEasing
            r0.<init>(r3)
            return r0
        L_0x0012:
            java.lang.String r0 = "spline"
            boolean r0 = r3.startsWith(r0)
            if (r0 == 0) goto L_0x0021
            androidx.constraintlayout.core.motion.utils.StepCurve r0 = new androidx.constraintlayout.core.motion.utils.StepCurve
            r0.<init>(r3)
            return r0
        L_0x0021:
            java.lang.String r0 = "Schlick"
            boolean r0 = r3.startsWith(r0)
            if (r0 == 0) goto L_0x002f
            androidx.constraintlayout.core.motion.utils.Schlick r0 = new androidx.constraintlayout.core.motion.utils.Schlick
            r0.<init>(r3)
            return r0
        L_0x002f:
            int r0 = r3.hashCode()
            switch(r0) {
                case -1354466595: goto L_0x006a;
                case -1263948740: goto L_0x0060;
                case -1197605014: goto L_0x0056;
                case -1102672091: goto L_0x004c;
                case -749065269: goto L_0x0042;
                case 1312628413: goto L_0x0037;
                default: goto L_0x0036;
            }
        L_0x0036:
            goto L_0x0074
        L_0x0037:
            java.lang.String r0 = "standard"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0036
            r0 = 0
            goto L_0x0075
        L_0x0042:
            java.lang.String r0 = "overshoot"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0036
            r0 = 5
            goto L_0x0075
        L_0x004c:
            java.lang.String r0 = "linear"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0036
            r0 = 3
            goto L_0x0075
        L_0x0056:
            java.lang.String r0 = "anticipate"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0036
            r0 = 4
            goto L_0x0075
        L_0x0060:
            java.lang.String r0 = "decelerate"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0036
            r0 = 2
            goto L_0x0075
        L_0x006a:
            java.lang.String r0 = "accelerate"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0036
            r0 = 1
            goto L_0x0075
        L_0x0074:
            r0 = -1
        L_0x0075:
            switch(r0) {
                case 0: goto L_0x00c2;
                case 1: goto L_0x00ba;
                case 2: goto L_0x00b2;
                case 3: goto L_0x00aa;
                case 4: goto L_0x00a2;
                case 5: goto L_0x009a;
                default: goto L_0x0078;
            }
        L_0x0078:
            java.io.PrintStream r0 = java.lang.System.err
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "transitionEasing syntax error syntax:transitionEasing=\"cubic(1.0,0.5,0.0,0.6)\" or "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String[] r2 = NAMED_EASING
            java.lang.String r2 = java.util.Arrays.toString(r2)
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.println(r1)
            androidx.constraintlayout.core.motion.utils.Easing r0 = sDefault
            return r0
        L_0x009a:
            androidx.constraintlayout.core.motion.utils.Easing$CubicEasing r0 = new androidx.constraintlayout.core.motion.utils.Easing$CubicEasing
            java.lang.String r1 = "cubic(0.34, 1.56, 0.64, 1)"
            r0.<init>(r1)
            return r0
        L_0x00a2:
            androidx.constraintlayout.core.motion.utils.Easing$CubicEasing r0 = new androidx.constraintlayout.core.motion.utils.Easing$CubicEasing
            java.lang.String r1 = "cubic(0.36, 0, 0.66, -0.56)"
            r0.<init>(r1)
            return r0
        L_0x00aa:
            androidx.constraintlayout.core.motion.utils.Easing$CubicEasing r0 = new androidx.constraintlayout.core.motion.utils.Easing$CubicEasing
            java.lang.String r1 = "cubic(1, 1, 0, 0)"
            r0.<init>(r1)
            return r0
        L_0x00b2:
            androidx.constraintlayout.core.motion.utils.Easing$CubicEasing r0 = new androidx.constraintlayout.core.motion.utils.Easing$CubicEasing
            java.lang.String r1 = "cubic(0.0, 0.0, 0.2, 0.95)"
            r0.<init>(r1)
            return r0
        L_0x00ba:
            androidx.constraintlayout.core.motion.utils.Easing$CubicEasing r0 = new androidx.constraintlayout.core.motion.utils.Easing$CubicEasing
            java.lang.String r1 = "cubic(0.4, 0.05, 0.8, 0.7)"
            r0.<init>(r1)
            return r0
        L_0x00c2:
            androidx.constraintlayout.core.motion.utils.Easing$CubicEasing r0 = new androidx.constraintlayout.core.motion.utils.Easing$CubicEasing
            java.lang.String r1 = "cubic(0.4, 0.0, 0.2, 1)"
            r0.<init>(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.core.motion.utils.Easing.getInterpolator(java.lang.String):androidx.constraintlayout.core.motion.utils.Easing");
    }

    public double get(double x) {
        return x;
    }

    public String toString() {
        return this.str;
    }

    public double getDiff(double x) {
        return 1.0d;
    }

    static class CubicEasing extends Easing {
        private static double d_error = 1.0E-4d;
        private static double error = 0.01d;
        double x1;
        double x2;
        double y1;
        double y2;

        CubicEasing(String configString) {
            this.str = configString;
            int start = configString.indexOf(40);
            int off1 = configString.indexOf(44, start);
            this.x1 = Double.parseDouble(configString.substring(start + 1, off1).trim());
            int off2 = configString.indexOf(44, off1 + 1);
            this.y1 = Double.parseDouble(configString.substring(off1 + 1, off2).trim());
            int off3 = configString.indexOf(44, off2 + 1);
            this.x2 = Double.parseDouble(configString.substring(off2 + 1, off3).trim());
            this.y2 = Double.parseDouble(configString.substring(off3 + 1, configString.indexOf(41, off3 + 1)).trim());
        }

        public CubicEasing(double x12, double y12, double x22, double y22) {
            setup(x12, y12, x22, y22);
        }

        /* access modifiers changed from: package-private */
        public void setup(double x12, double y12, double x22, double y22) {
            this.x1 = x12;
            this.y1 = y12;
            this.x2 = x22;
            this.y2 = y22;
        }

        private double getX(double t) {
            double t1 = 1.0d - t;
            return (this.x1 * t1 * 3.0d * t1 * t) + (this.x2 * 3.0d * t1 * t * t) + (t * t * t);
        }

        private double getY(double t) {
            double t1 = 1.0d - t;
            return (this.y1 * t1 * 3.0d * t1 * t) + (this.y2 * 3.0d * t1 * t * t) + (t * t * t);
        }

        private double getDiffX(double t) {
            double t1 = 1.0d - t;
            return (t1 * 3.0d * t1 * this.x1) + (6.0d * t1 * t * (this.x2 - this.x1)) + (3.0d * t * t * (1.0d - this.x2));
        }

        private double getDiffY(double t) {
            double t1 = 1.0d - t;
            return (t1 * 3.0d * t1 * this.y1) + (6.0d * t1 * t * (this.y2 - this.y1)) + (3.0d * t * t * (1.0d - this.y2));
        }

        public double getDiff(double x) {
            double t = 0.5d;
            double range = 0.5d;
            while (range > d_error) {
                range *= 0.5d;
                if (getX(t) < x) {
                    t += range;
                } else {
                    t -= range;
                }
            }
            double x12 = getX(t - range);
            double x22 = getX(t + range);
            return (getY(t + range) - getY(t - range)) / (x22 - x12);
        }

        public double get(double x) {
            if (x <= 0.0d) {
                return 0.0d;
            }
            if (x >= 1.0d) {
                return 1.0d;
            }
            double t = 0.5d;
            double range = 0.5d;
            while (range > error) {
                range *= 0.5d;
                if (getX(t) < x) {
                    t += range;
                } else {
                    t -= range;
                }
            }
            double x12 = getX(t - range);
            double x22 = getX(t + range);
            double y12 = getY(t - range);
            return (((getY(t + range) - y12) * (x - x12)) / (x22 - x12)) + y12;
        }
    }
}
