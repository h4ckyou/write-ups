package com.google.android.material.color.utilities;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class DynamicColor {
    public final Function<DynamicScheme, DynamicColor> background;
    public final Function<DynamicScheme, Double> chroma;
    private final HashMap<DynamicScheme, Hct> hctCache = new HashMap<>();
    public final Function<DynamicScheme, Double> hue;
    public final Function<DynamicScheme, Double> opacity;
    public final Function<DynamicScheme, Double> tone;
    public final Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint;
    public final Function<DynamicScheme, Double> toneMaxContrast;
    public final Function<DynamicScheme, Double> toneMinContrast;

    public DynamicColor(Function<DynamicScheme, Double> hue2, Function<DynamicScheme, Double> chroma2, Function<DynamicScheme, Double> tone2, Function<DynamicScheme, Double> opacity2, Function<DynamicScheme, DynamicColor> background2, Function<DynamicScheme, Double> toneMinContrast2, Function<DynamicScheme, Double> toneMaxContrast2, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint2) {
        this.hue = hue2;
        this.chroma = chroma2;
        this.tone = tone2;
        this.opacity = opacity2;
        this.background = background2;
        this.toneMinContrast = toneMinContrast2;
        this.toneMaxContrast = toneMaxContrast2;
        this.toneDeltaConstraint = toneDeltaConstraint2;
    }

    public static DynamicColor fromArgb(int argb) {
        return fromPalette(new DynamicColor$$ExternalSyntheticLambda3(TonalPalette.fromInt(argb)), new DynamicColor$$ExternalSyntheticLambda4(Hct.fromInt(argb)));
    }

    static /* synthetic */ TonalPalette lambda$fromArgb$0(TonalPalette palette, DynamicScheme s) {
        return palette;
    }

    public static DynamicColor fromArgb(int argb, Function<DynamicScheme, Double> tone2) {
        return fromPalette(new DynamicColor$$ExternalSyntheticLambda10(argb), tone2);
    }

    public static DynamicColor fromArgb(int argb, Function<DynamicScheme, Double> tone2, Function<DynamicScheme, DynamicColor> background2) {
        return fromPalette(new DynamicColor$$ExternalSyntheticLambda0(argb), tone2, background2);
    }

    public static DynamicColor fromArgb(int argb, Function<DynamicScheme, Double> tone2, Function<DynamicScheme, DynamicColor> background2, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint2) {
        return fromPalette(new DynamicColor$$ExternalSyntheticLambda16(argb), tone2, background2, toneDeltaConstraint2);
    }

    public static DynamicColor fromPalette(Function<DynamicScheme, TonalPalette> palette, Function<DynamicScheme, Double> tone2) {
        return fromPalette(palette, tone2, (Function<DynamicScheme, DynamicColor>) null, (Function<DynamicScheme, ToneDeltaConstraint>) null);
    }

    public static DynamicColor fromPalette(Function<DynamicScheme, TonalPalette> palette, Function<DynamicScheme, Double> tone2, Function<DynamicScheme, DynamicColor> background2) {
        return fromPalette(palette, tone2, background2, (Function<DynamicScheme, ToneDeltaConstraint>) null);
    }

    public static DynamicColor fromPalette(Function<DynamicScheme, TonalPalette> palette, Function<DynamicScheme, Double> tone2, Function<DynamicScheme, DynamicColor> background2, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint2) {
        return new DynamicColor(new DynamicColor$$ExternalSyntheticLambda17(palette), new DynamicColor$$ExternalSyntheticLambda18(palette), tone2, (Function<DynamicScheme, Double>) null, background2, new DynamicColor$$ExternalSyntheticLambda1(tone2, background2, toneDeltaConstraint2), new DynamicColor$$ExternalSyntheticLambda2(tone2, background2, toneDeltaConstraint2), toneDeltaConstraint2);
    }

    public int getArgb(DynamicScheme scheme) {
        int argb = getHct(scheme).toInt();
        if (this.opacity == null) {
            return argb;
        }
        return (16777215 & argb) | (MathUtils.clampInt(0, 255, (int) Math.round(255.0d * this.opacity.apply(scheme).doubleValue())) << 24);
    }

    public Hct getHct(DynamicScheme scheme) {
        Hct cachedAnswer = this.hctCache.get(scheme);
        if (cachedAnswer != null) {
            return cachedAnswer;
        }
        Hct answer = Hct.from(this.hue.apply(scheme).doubleValue(), this.chroma.apply(scheme).doubleValue(), getTone(scheme));
        if (this.hctCache.size() > 4) {
            this.hctCache.clear();
        }
        this.hctCache.put(scheme, answer);
        return answer;
    }

    public double getTone(DynamicScheme scheme) {
        double endTone;
        double maxRatio;
        double minRatio;
        DynamicScheme dynamicScheme = scheme;
        double answer = this.tone.apply(dynamicScheme).doubleValue();
        boolean z = true;
        boolean decreasingContrast = dynamicScheme.contrastLevel < 0.0d;
        if (dynamicScheme.contrastLevel != 0.0d) {
            double startTone = this.tone.apply(dynamicScheme).doubleValue();
            endTone = (((decreasingContrast ? this.toneMinContrast : this.toneMaxContrast).apply(dynamicScheme).doubleValue() - startTone) * Math.abs(dynamicScheme.contrastLevel)) + startTone;
        } else {
            endTone = answer;
        }
        DynamicColor bgDynamicColor = this.background == null ? null : this.background.apply(dynamicScheme);
        if (bgDynamicColor != null) {
            if (bgDynamicColor.background == null || bgDynamicColor.background.apply(dynamicScheme) == null) {
                z = false;
            }
            boolean bgHasBg = z;
            double standardRatio = Contrast.ratioOfTones(this.tone.apply(dynamicScheme).doubleValue(), bgDynamicColor.tone.apply(dynamicScheme).doubleValue());
            if (decreasingContrast) {
                minRatio = bgHasBg ? Contrast.ratioOfTones(this.toneMinContrast.apply(dynamicScheme).doubleValue(), bgDynamicColor.toneMinContrast.apply(dynamicScheme).doubleValue()) : 1.0d;
                maxRatio = standardRatio;
            } else {
                double maxContrastRatio = Contrast.ratioOfTones(this.toneMaxContrast.apply(dynamicScheme).doubleValue(), bgDynamicColor.toneMaxContrast.apply(dynamicScheme).doubleValue());
                minRatio = bgHasBg ? Math.min(maxContrastRatio, standardRatio) : 1.0d;
                maxRatio = bgHasBg ? Math.max(maxContrastRatio, standardRatio) : 21.0d;
            }
        } else {
            maxRatio = 21.0d;
            minRatio = 1.0d;
        }
        double finalMinRatio = minRatio;
        double finalMaxRatio = maxRatio;
        double finalAnswer = endTone;
        Function<DynamicScheme, Double> function = this.tone;
        DynamicColor$$ExternalSyntheticLambda11 dynamicColor$$ExternalSyntheticLambda11 = new DynamicColor$$ExternalSyntheticLambda11(dynamicScheme);
        boolean z2 = decreasingContrast;
        double d = endTone;
        double d2 = finalAnswer;
        double d3 = finalMaxRatio;
        double d4 = finalMinRatio;
        return calculateDynamicTone(scheme, function, dynamicColor$$ExternalSyntheticLambda11, new DynamicColor$$ExternalSyntheticLambda12(finalAnswer), new DynamicColor$$ExternalSyntheticLambda13(bgDynamicColor), this.toneDeltaConstraint, new DynamicColor$$ExternalSyntheticLambda14(finalMinRatio), new DynamicColor$$ExternalSyntheticLambda15(finalMaxRatio));
    }

    static /* synthetic */ DynamicColor lambda$getTone$11(DynamicColor bgDynamicColor, DynamicScheme s) {
        return bgDynamicColor;
    }

    public static double toneMinContrastDefault(Function<DynamicScheme, Double> tone2, Function<DynamicScheme, DynamicColor> background2, DynamicScheme scheme, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint2) {
        return calculateDynamicTone(scheme, tone2, new DynamicColor$$ExternalSyntheticLambda7(scheme), new DynamicColor$$ExternalSyntheticLambda8(tone2, scheme, background2), background2, toneDeltaConstraint2, (Function<Double, Double>) null, new DynamicColor$$ExternalSyntheticLambda9());
    }

    static /* synthetic */ Double lambda$toneMinContrastDefault$15(Function tone2, DynamicScheme scheme, Function background2, Double stdRatio, Double bgTone) {
        double answer = ((Double) tone2.apply(scheme)).doubleValue();
        if (stdRatio.doubleValue() >= 7.0d) {
            answer = contrastingTone(bgTone.doubleValue(), 4.5d);
        } else if (stdRatio.doubleValue() >= 3.0d) {
            answer = contrastingTone(bgTone.doubleValue(), 3.0d);
        } else {
            if ((background2 == null || background2.apply(scheme) == null || ((DynamicColor) background2.apply(scheme)).background == null || ((DynamicColor) background2.apply(scheme)).background.apply(scheme) == null) ? false : true) {
                answer = contrastingTone(bgTone.doubleValue(), stdRatio.doubleValue());
            }
        }
        return Double.valueOf(answer);
    }

    static /* synthetic */ Double lambda$toneMinContrastDefault$16(Double standardRatio) {
        return standardRatio;
    }

    public static double toneMaxContrastDefault(Function<DynamicScheme, Double> tone2, Function<DynamicScheme, DynamicColor> background2, DynamicScheme scheme, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint2) {
        return calculateDynamicTone(scheme, tone2, new DynamicColor$$ExternalSyntheticLambda5(scheme), new DynamicColor$$ExternalSyntheticLambda6(background2, scheme), background2, toneDeltaConstraint2, (Function<Double, Double>) null, (Function<Double, Double>) null);
    }

    static /* synthetic */ Double lambda$toneMaxContrastDefault$18(Function background2, DynamicScheme scheme, Double stdRatio, Double bgTone) {
        if ((background2 == null || background2.apply(scheme) == null || ((DynamicColor) background2.apply(scheme)).background == null || ((DynamicColor) background2.apply(scheme)).background.apply(scheme) == null) ? false : true) {
            return Double.valueOf(contrastingTone(bgTone.doubleValue(), 7.0d));
        }
        return Double.valueOf(contrastingTone(bgTone.doubleValue(), Math.max(7.0d, stdRatio.doubleValue())));
    }

    public static double calculateDynamicTone(DynamicScheme scheme, Function<DynamicScheme, Double> toneStandard, Function<DynamicColor, Double> toneToJudge, BiFunction<Double, Double, Double> desiredTone, Function<DynamicScheme, DynamicColor> background2, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint2, Function<Double, Double> minRatio, Function<Double, Double> maxRatio) {
        double answer;
        DynamicScheme dynamicScheme = scheme;
        Function<DynamicScheme, DynamicColor> function = background2;
        Function<Double, Double> function2 = minRatio;
        Function<Double, Double> function3 = maxRatio;
        double toneStd = toneStandard.apply(dynamicScheme).doubleValue();
        double answer2 = toneStd;
        DynamicColor bgDynamic = function == null ? null : function.apply(dynamicScheme);
        if (bgDynamic == null) {
            return answer2;
        }
        double bgToneStd = bgDynamic.tone.apply(dynamicScheme).doubleValue();
        double stdRatio = Contrast.ratioOfTones(toneStd, bgToneStd);
        double bgTone = toneToJudge.apply(bgDynamic).doubleValue();
        double d = answer2;
        double myDesiredTone = desiredTone.apply(Double.valueOf(stdRatio), Double.valueOf(bgTone)).doubleValue();
        double currentRatio = Contrast.ratioOfTones(bgTone, myDesiredTone);
        double minRatioRealized = 1.0d;
        if (!(function2 == null || function2.apply(Double.valueOf(stdRatio)) == null)) {
            minRatioRealized = function2.apply(Double.valueOf(stdRatio)).doubleValue();
        }
        double maxRatioRealized = 21.0d;
        if (!(function3 == null || function3.apply(Double.valueOf(stdRatio)) == null)) {
            maxRatioRealized = function3.apply(Double.valueOf(stdRatio)).doubleValue();
        }
        double desiredRatio = MathUtils.clampDouble(minRatioRealized, maxRatioRealized, currentRatio);
        if (desiredRatio == currentRatio) {
            answer = myDesiredTone;
        } else {
            answer = contrastingTone(bgTone, desiredRatio);
        }
        if (bgDynamic.background == null || bgDynamic.background.apply(dynamicScheme) == null) {
            answer = enableLightForeground(answer);
        }
        double d2 = myDesiredTone;
        double d3 = bgTone;
        double d4 = bgToneStd;
        return ensureToneDelta(answer, toneStd, scheme, toneDeltaConstraint2, toneToJudge);
    }

    static double ensureToneDelta(double tone2, double toneStandard, DynamicScheme scheme, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint2, Function<DynamicColor, Double> toneToDistanceFrom) {
        DynamicScheme dynamicScheme = scheme;
        Function<DynamicScheme, ToneDeltaConstraint> function = toneDeltaConstraint2;
        ToneDeltaConstraint constraint = function == null ? null : function.apply(dynamicScheme);
        if (constraint == null) {
            return tone2;
        }
        double requiredDelta = constraint.delta;
        double keepAwayTone = toneToDistanceFrom.apply(constraint.keepAway).doubleValue();
        double delta = Math.abs(tone2 - keepAwayTone);
        if (delta >= requiredDelta) {
            return tone2;
        }
        switch (constraint.keepAwayPolarity) {
            case DARKER:
                return MathUtils.clampDouble(0.0d, 100.0d, keepAwayTone + requiredDelta);
            case LIGHTER:
                return MathUtils.clampDouble(0.0d, 100.0d, keepAwayTone - requiredDelta);
            case NO_PREFERENCE:
                boolean lighten = true;
                boolean preferLighten = toneStandard > constraint.keepAway.tone.apply(dynamicScheme).doubleValue();
                double alterAmount = Math.abs(delta - requiredDelta);
                if (!preferLighten ? tone2 >= alterAmount : tone2 + alterAmount > 100.0d) {
                    lighten = false;
                }
                return lighten ? tone2 + alterAmount : tone2 - alterAmount;
            default:
                return tone2;
        }
    }

    public static double contrastingTone(double bgTone, double ratio) {
        double d = bgTone;
        double lighterTone = Contrast.lighterUnsafe(bgTone, ratio);
        double darkerTone = Contrast.darkerUnsafe(bgTone, ratio);
        double lighterRatio = Contrast.ratioOfTones(lighterTone, d);
        double darkerRatio = Contrast.ratioOfTones(darkerTone, d);
        if (!tonePrefersLightForeground(bgTone)) {
            return (darkerRatio >= ratio || darkerRatio >= lighterRatio) ? darkerTone : lighterTone;
        }
        boolean negligibleDifference = Math.abs(lighterRatio - darkerRatio) < 0.1d && lighterRatio < ratio && darkerRatio < ratio;
        if (lighterRatio >= ratio || lighterRatio >= darkerRatio || negligibleDifference) {
            return lighterTone;
        }
        return darkerTone;
    }

    public static double enableLightForeground(double tone2) {
        if (!tonePrefersLightForeground(tone2) || toneAllowsLightForeground(tone2)) {
            return tone2;
        }
        return 49.0d;
    }

    public static boolean tonePrefersLightForeground(double tone2) {
        return Math.round(tone2) < 60;
    }

    public static boolean toneAllowsLightForeground(double tone2) {
        return Math.round(tone2) <= 49;
    }
}
