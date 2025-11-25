package com.google.android.material.color.utilities;

import java.util.function.Function;

public final class MaterialDynamicColors {
    private static final double CONTAINER_ACCENT_TONE_DELTA = 15.0d;

    public DynamicColor highestSurface(DynamicScheme s) {
        return s.isDark ? surfaceBright() : surfaceDim();
    }

    public DynamicColor primaryPaletteKeyColor() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda63(), new MaterialDynamicColors$$ExternalSyntheticLambda65());
    }

    public DynamicColor secondaryPaletteKeyColor() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda70(), new MaterialDynamicColors$$ExternalSyntheticLambda71());
    }

    public DynamicColor tertiaryPaletteKeyColor() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda80(), new MaterialDynamicColors$$ExternalSyntheticLambda81());
    }

    public DynamicColor neutralPaletteKeyColor() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda11(), new MaterialDynamicColors$$ExternalSyntheticLambda22());
    }

    public DynamicColor neutralVariantPaletteKeyColor() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda131(), new MaterialDynamicColors$$ExternalSyntheticLambda132());
    }

    static /* synthetic */ Double lambda$background$11(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 6.0d : 98.0d);
    }

    public DynamicColor background() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda91(), new MaterialDynamicColors$$ExternalSyntheticLambda92());
    }

    public DynamicColor onBackground() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda95(), new MaterialDynamicColors$$ExternalSyntheticLambda96(), new MaterialDynamicColors$$ExternalSyntheticLambda98(this));
    }

    static /* synthetic */ Double lambda$onBackground$13(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 90.0d : 10.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onBackground$14$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m51lambda$onBackground$14$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return background();
    }

    static /* synthetic */ Double lambda$surface$16(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 6.0d : 98.0d);
    }

    public DynamicColor surface() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda0(), new MaterialDynamicColors$$ExternalSyntheticLambda64());
    }

    static /* synthetic */ Double lambda$inverseSurface$18(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 90.0d : 20.0d);
    }

    public DynamicColor inverseSurface() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda35(), new MaterialDynamicColors$$ExternalSyntheticLambda36());
    }

    static /* synthetic */ Double lambda$surfaceBright$20(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 24.0d : 98.0d);
    }

    public DynamicColor surfaceBright() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda101(), new MaterialDynamicColors$$ExternalSyntheticLambda102());
    }

    static /* synthetic */ Double lambda$surfaceDim$22(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 6.0d : 87.0d);
    }

    public DynamicColor surfaceDim() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda33(), new MaterialDynamicColors$$ExternalSyntheticLambda44());
    }

    static /* synthetic */ Double lambda$surfaceContainerLowest$24(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 4.0d : 100.0d);
    }

    public DynamicColor surfaceContainerLowest() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda1(), new MaterialDynamicColors$$ExternalSyntheticLambda2());
    }

    static /* synthetic */ Double lambda$surfaceContainerLow$26(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 10.0d : 96.0d);
    }

    public DynamicColor surfaceContainerLow() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda72(), new MaterialDynamicColors$$ExternalSyntheticLambda73());
    }

    static /* synthetic */ Double lambda$surfaceContainer$28(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 12.0d : 94.0d);
    }

    public DynamicColor surfaceContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda29(), new MaterialDynamicColors$$ExternalSyntheticLambda30());
    }

    static /* synthetic */ Double lambda$surfaceContainerHigh$30(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 17.0d : 92.0d);
    }

    public DynamicColor surfaceContainerHigh() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda93(), new MaterialDynamicColors$$ExternalSyntheticLambda94());
    }

    static /* synthetic */ Double lambda$surfaceContainerHighest$32(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 22.0d : 90.0d);
    }

    public DynamicColor surfaceContainerHighest() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda137(), new MaterialDynamicColors$$ExternalSyntheticLambda138());
    }

    public DynamicColor onSurface() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda130(), new MaterialDynamicColors$$ExternalSyntheticLambda141(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$onSurface$34(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 90.0d : 10.0d);
    }

    public DynamicColor inverseOnSurface() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda10(), new MaterialDynamicColors$$ExternalSyntheticLambda12(), new MaterialDynamicColors$$ExternalSyntheticLambda13(this));
    }

    static /* synthetic */ Double lambda$inverseOnSurface$36(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 20.0d : 95.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$inverseOnSurface$37$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m49lambda$inverseOnSurface$37$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return inverseSurface();
    }

    static /* synthetic */ Double lambda$surfaceVariant$39(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 30.0d : 90.0d);
    }

    public DynamicColor surfaceVariant() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda128(), new MaterialDynamicColors$$ExternalSyntheticLambda129());
    }

    public DynamicColor onSurfaceVariant() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda31(), new MaterialDynamicColors$$ExternalSyntheticLambda32(), new MaterialDynamicColors$$ExternalSyntheticLambda34(this));
    }

    static /* synthetic */ Double lambda$onSurfaceVariant$41(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 80.0d : 30.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSurfaceVariant$42$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m64lambda$onSurfaceVariant$42$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return surfaceVariant();
    }

    public DynamicColor outline() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda3(), new MaterialDynamicColors$$ExternalSyntheticLambda4(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$outline$44(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 60.0d : 50.0d);
    }

    public DynamicColor outlineVariant() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda99(), new MaterialDynamicColors$$ExternalSyntheticLambda100(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$outlineVariant$46(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 30.0d : 80.0d);
    }

    public DynamicColor shadow() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda135(), new MaterialDynamicColors$$ExternalSyntheticLambda136());
    }

    public DynamicColor scrim() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda50(), new MaterialDynamicColors$$ExternalSyntheticLambda51());
    }

    static /* synthetic */ Double lambda$surfaceTint$52(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 80.0d : 40.0d);
    }

    public DynamicColor surfaceTint() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda5(), new MaterialDynamicColors$$ExternalSyntheticLambda6());
    }

    public DynamicColor primaryContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda89(), new MaterialDynamicColors$$ExternalSyntheticLambda90(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$primaryContainer$54(DynamicScheme s) {
        if (isFidelity(s)) {
            return Double.valueOf(performAlbers(s.sourceColorHct, s));
        }
        if (isMonochrome(s)) {
            return Double.valueOf(s.isDark ? 85.0d : 25.0d);
        }
        return Double.valueOf(s.isDark ? 30.0d : 90.0d);
    }

    public DynamicColor onPrimaryContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda123(), new MaterialDynamicColors$$ExternalSyntheticLambda124(this), new MaterialDynamicColors$$ExternalSyntheticLambda125(this), (Function<DynamicScheme, ToneDeltaConstraint>) null);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onPrimaryContainer$56$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ Double m55lambda$onPrimaryContainer$56$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        if (isFidelity(s)) {
            return Double.valueOf(DynamicColor.contrastingTone(primaryContainer().tone.apply(s).doubleValue(), 4.5d));
        }
        if (isMonochrome(s)) {
            return Double.valueOf(s.isDark ? 0.0d : 100.0d);
        }
        return Double.valueOf(s.isDark ? 90.0d : 10.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onPrimaryContainer$57$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m56lambda$onPrimaryContainer$57$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return primaryContainer();
    }

    public DynamicColor primary() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda47(), new MaterialDynamicColors$$ExternalSyntheticLambda48(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this), new MaterialDynamicColors$$ExternalSyntheticLambda49(this));
    }

    static /* synthetic */ Double lambda$primary$59(DynamicScheme s) {
        if (isMonochrome(s)) {
            return Double.valueOf(s.isDark ? 100.0d : 0.0d);
        }
        return Double.valueOf(s.isDark ? 80.0d : 40.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$primary$60$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ ToneDeltaConstraint m70lambda$primary$60$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return new ToneDeltaConstraint(CONTAINER_ACCENT_TONE_DELTA, primaryContainer(), s.isDark ? TonePolarity.DARKER : TonePolarity.LIGHTER);
    }

    public DynamicColor inversePrimary() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda106(), new MaterialDynamicColors$$ExternalSyntheticLambda107(), new MaterialDynamicColors$$ExternalSyntheticLambda109(this));
    }

    static /* synthetic */ Double lambda$inversePrimary$62(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 40.0d : 80.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$inversePrimary$63$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m50lambda$inversePrimary$63$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return inverseSurface();
    }

    public DynamicColor onPrimary() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda103(), new MaterialDynamicColors$$ExternalSyntheticLambda104(), new MaterialDynamicColors$$ExternalSyntheticLambda105(this));
    }

    static /* synthetic */ Double lambda$onPrimary$65(DynamicScheme s) {
        if (isMonochrome(s)) {
            return Double.valueOf(s.isDark ? 10.0d : 90.0d);
        }
        return Double.valueOf(s.isDark ? 20.0d : 100.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onPrimary$66$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m54lambda$onPrimary$66$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return primary();
    }

    public DynamicColor secondaryContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda74(), new MaterialDynamicColors$$ExternalSyntheticLambda76(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$secondaryContainer$68(DynamicScheme s) {
        double d = 30.0d;
        if (isMonochrome(s)) {
            if (!s.isDark) {
                d = 85.0d;
            }
            return Double.valueOf(d);
        }
        if (!s.isDark) {
            d = 90.0d;
        }
        double initialTone = d;
        if (!isFidelity(s)) {
            return Double.valueOf(initialTone);
        }
        return Double.valueOf(performAlbers(s.secondaryPalette.getHct(findDesiredChromaByTone(s.secondaryPalette.getHue(), s.secondaryPalette.getChroma(), initialTone, !s.isDark)), s));
    }

    public DynamicColor onSecondaryContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda17(), new MaterialDynamicColors$$ExternalSyntheticLambda18(this), new MaterialDynamicColors$$ExternalSyntheticLambda19(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSecondaryContainer$70$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ Double m60lambda$onSecondaryContainer$70$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        if (isFidelity(s)) {
            return Double.valueOf(DynamicColor.contrastingTone(secondaryContainer().tone.apply(s).doubleValue(), 4.5d));
        }
        return Double.valueOf(s.isDark ? 90.0d : 10.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSecondaryContainer$71$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m61lambda$onSecondaryContainer$71$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return secondaryContainer();
    }

    public DynamicColor secondary() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda149(), new MaterialDynamicColors$$ExternalSyntheticLambda150(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this), new MaterialDynamicColors$$ExternalSyntheticLambda151(this));
    }

    static /* synthetic */ Double lambda$secondary$73(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 80.0d : 40.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$secondary$74$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ ToneDeltaConstraint m71lambda$secondary$74$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return new ToneDeltaConstraint(CONTAINER_ACCENT_TONE_DELTA, secondaryContainer(), s.isDark ? TonePolarity.DARKER : TonePolarity.LIGHTER);
    }

    public DynamicColor onSecondary() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda146(), new MaterialDynamicColors$$ExternalSyntheticLambda147(), new MaterialDynamicColors$$ExternalSyntheticLambda148(this));
    }

    static /* synthetic */ Double lambda$onSecondary$76(DynamicScheme s) {
        double d = 100.0d;
        if (isMonochrome(s)) {
            if (s.isDark) {
                d = 10.0d;
            }
            return Double.valueOf(d);
        }
        if (s.isDark) {
            d = 20.0d;
        }
        return Double.valueOf(d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSecondary$77$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m59lambda$onSecondary$77$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return secondary();
    }

    public DynamicColor tertiaryContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda144(), new MaterialDynamicColors$$ExternalSyntheticLambda145(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$tertiaryContainer$79(DynamicScheme s) {
        if (isMonochrome(s)) {
            return Double.valueOf(s.isDark ? 60.0d : 49.0d);
        } else if (!isFidelity(s)) {
            return Double.valueOf(s.isDark ? 30.0d : 90.0d);
        } else {
            return Double.valueOf(DislikeAnalyzer.fixIfDisliked(s.tertiaryPalette.getHct(performAlbers(s.tertiaryPalette.getHct(s.sourceColorHct.getTone()), s))).getTone());
        }
    }

    public DynamicColor onTertiaryContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda14(), new MaterialDynamicColors$$ExternalSyntheticLambda15(this), new MaterialDynamicColors$$ExternalSyntheticLambda16(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onTertiaryContainer$81$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ Double m66lambda$onTertiaryContainer$81$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        if (isMonochrome(s)) {
            return Double.valueOf(s.isDark ? 0.0d : 100.0d);
        } else if (isFidelity(s)) {
            return Double.valueOf(DynamicColor.contrastingTone(tertiaryContainer().tone.apply(s).doubleValue(), 4.5d));
        } else {
            return Double.valueOf(s.isDark ? 90.0d : 10.0d);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onTertiaryContainer$82$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m67lambda$onTertiaryContainer$82$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return tertiaryContainer();
    }

    public DynamicColor tertiary() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda56(), new MaterialDynamicColors$$ExternalSyntheticLambda57(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this), new MaterialDynamicColors$$ExternalSyntheticLambda58(this));
    }

    static /* synthetic */ Double lambda$tertiary$84(DynamicScheme s) {
        if (isMonochrome(s)) {
            return Double.valueOf(s.isDark ? 90.0d : 25.0d);
        }
        return Double.valueOf(s.isDark ? 80.0d : 40.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$tertiary$85$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ ToneDeltaConstraint m72lambda$tertiary$85$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return new ToneDeltaConstraint(CONTAINER_ACCENT_TONE_DELTA, tertiaryContainer(), s.isDark ? TonePolarity.DARKER : TonePolarity.LIGHTER);
    }

    public DynamicColor onTertiary() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda97(), new MaterialDynamicColors$$ExternalSyntheticLambda108(), new MaterialDynamicColors$$ExternalSyntheticLambda119(this));
    }

    static /* synthetic */ Double lambda$onTertiary$87(DynamicScheme s) {
        if (isMonochrome(s)) {
            return Double.valueOf(s.isDark ? 10.0d : 90.0d);
        }
        return Double.valueOf(s.isDark ? 20.0d : 100.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onTertiary$88$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m65lambda$onTertiary$88$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return tertiary();
    }

    public DynamicColor errorContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda42(), new MaterialDynamicColors$$ExternalSyntheticLambda43(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$errorContainer$90(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 30.0d : 90.0d);
    }

    public DynamicColor onErrorContainer() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda37(), new MaterialDynamicColors$$ExternalSyntheticLambda38(), new MaterialDynamicColors$$ExternalSyntheticLambda39(this));
    }

    static /* synthetic */ Double lambda$onErrorContainer$92(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 90.0d : 10.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onErrorContainer$93$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m53lambda$onErrorContainer$93$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return errorContainer();
    }

    public DynamicColor error() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda24(), new MaterialDynamicColors$$ExternalSyntheticLambda25(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this), new MaterialDynamicColors$$ExternalSyntheticLambda26(this));
    }

    static /* synthetic */ Double lambda$error$95(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 80.0d : 40.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$error$96$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ ToneDeltaConstraint m48lambda$error$96$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return new ToneDeltaConstraint(CONTAINER_ACCENT_TONE_DELTA, errorContainer(), s.isDark ? TonePolarity.DARKER : TonePolarity.LIGHTER);
    }

    public DynamicColor onError() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda117(), new MaterialDynamicColors$$ExternalSyntheticLambda118(), new MaterialDynamicColors$$ExternalSyntheticLambda120(this));
    }

    static /* synthetic */ Double lambda$onError$98(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 20.0d : 100.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onError$99$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m52lambda$onError$99$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return error();
    }

    public DynamicColor primaryFixed() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda139(), new MaterialDynamicColors$$ExternalSyntheticLambda140(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$primaryFixed$101(DynamicScheme s) {
        if (!isMonochrome(s)) {
            return Double.valueOf(90.0d);
        }
        return Double.valueOf(s.isDark ? 100.0d : 10.0d);
    }

    public DynamicColor primaryFixedDim() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda142(), new MaterialDynamicColors$$ExternalSyntheticLambda143(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$primaryFixedDim$103(DynamicScheme s) {
        if (!isMonochrome(s)) {
            return Double.valueOf(80.0d);
        }
        return Double.valueOf(s.isDark ? 90.0d : 20.0d);
    }

    public DynamicColor onPrimaryFixed() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda20(), new MaterialDynamicColors$$ExternalSyntheticLambda21(), new MaterialDynamicColors$$ExternalSyntheticLambda23(this));
    }

    static /* synthetic */ Double lambda$onPrimaryFixed$105(DynamicScheme s) {
        double d = 10.0d;
        if (!isMonochrome(s)) {
            return Double.valueOf(10.0d);
        }
        if (!s.isDark) {
            d = 90.0d;
        }
        return Double.valueOf(d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onPrimaryFixed$106$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m57lambda$onPrimaryFixed$106$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return primaryFixedDim();
    }

    public DynamicColor onPrimaryFixedVariant() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda112(), new MaterialDynamicColors$$ExternalSyntheticLambda113(), new MaterialDynamicColors$$ExternalSyntheticLambda114(this));
    }

    static /* synthetic */ Double lambda$onPrimaryFixedVariant$108(DynamicScheme s) {
        double d = 30.0d;
        if (!isMonochrome(s)) {
            return Double.valueOf(30.0d);
        }
        if (!s.isDark) {
            d = 70.0d;
        }
        return Double.valueOf(d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onPrimaryFixedVariant$109$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m58lambda$onPrimaryFixedVariant$109$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return primaryFixedDim();
    }

    public DynamicColor secondaryFixed() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda110(), new MaterialDynamicColors$$ExternalSyntheticLambda111(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$secondaryFixed$111(DynamicScheme s) {
        return Double.valueOf(isMonochrome(s) ? 80.0d : 90.0d);
    }

    public DynamicColor secondaryFixedDim() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda126(), new MaterialDynamicColors$$ExternalSyntheticLambda127(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$secondaryFixedDim$113(DynamicScheme s) {
        return Double.valueOf(isMonochrome(s) ? 70.0d : 80.0d);
    }

    public DynamicColor onSecondaryFixed() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda7(), new MaterialDynamicColors$$ExternalSyntheticLambda8(), new MaterialDynamicColors$$ExternalSyntheticLambda9(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSecondaryFixed$116$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m62lambda$onSecondaryFixed$116$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return secondaryFixedDim();
    }

    public DynamicColor onSecondaryFixedVariant() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda52(), new MaterialDynamicColors$$ExternalSyntheticLambda53(), new MaterialDynamicColors$$ExternalSyntheticLambda54(this));
    }

    static /* synthetic */ Double lambda$onSecondaryFixedVariant$118(DynamicScheme s) {
        return Double.valueOf(isMonochrome(s) ? 25.0d : 30.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onSecondaryFixedVariant$119$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m63lambda$onSecondaryFixedVariant$119$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return secondaryFixedDim();
    }

    public DynamicColor tertiaryFixed() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda45(), new MaterialDynamicColors$$ExternalSyntheticLambda46(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$tertiaryFixed$121(DynamicScheme s) {
        return Double.valueOf(isMonochrome(s) ? 40.0d : 90.0d);
    }

    public DynamicColor tertiaryFixedDim() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda115(), new MaterialDynamicColors$$ExternalSyntheticLambda116(), new MaterialDynamicColors$$ExternalSyntheticLambda152(this));
    }

    static /* synthetic */ Double lambda$tertiaryFixedDim$123(DynamicScheme s) {
        return Double.valueOf(isMonochrome(s) ? 30.0d : 80.0d);
    }

    public DynamicColor onTertiaryFixed() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda77(), new MaterialDynamicColors$$ExternalSyntheticLambda78(), new MaterialDynamicColors$$ExternalSyntheticLambda79(this));
    }

    static /* synthetic */ Double lambda$onTertiaryFixed$125(DynamicScheme s) {
        return Double.valueOf(isMonochrome(s) ? 90.0d : 10.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onTertiaryFixed$126$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m68lambda$onTertiaryFixed$126$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return tertiaryFixedDim();
    }

    public DynamicColor onTertiaryFixedVariant() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda67(), new MaterialDynamicColors$$ExternalSyntheticLambda68(), new MaterialDynamicColors$$ExternalSyntheticLambda69(this));
    }

    static /* synthetic */ Double lambda$onTertiaryFixedVariant$128(DynamicScheme s) {
        return Double.valueOf(isMonochrome(s) ? 70.0d : 30.0d);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onTertiaryFixedVariant$129$com-google-android-material-color-utilities-MaterialDynamicColors  reason: not valid java name */
    public /* synthetic */ DynamicColor m69lambda$onTertiaryFixedVariant$129$comgoogleandroidmaterialcolorutilitiesMaterialDynamicColors(DynamicScheme s) {
        return tertiaryFixedDim();
    }

    static /* synthetic */ Double lambda$controlActivated$131(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 30.0d : 90.0d);
    }

    public DynamicColor controlActivated() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda61(), new MaterialDynamicColors$$ExternalSyntheticLambda62(), (Function<DynamicScheme, DynamicColor>) null);
    }

    static /* synthetic */ Double lambda$controlNormal$133(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 80.0d : 30.0d);
    }

    public DynamicColor controlNormal() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda59(), new MaterialDynamicColors$$ExternalSyntheticLambda60());
    }

    public DynamicColor controlHighlight() {
        return new DynamicColor(new MaterialDynamicColors$$ExternalSyntheticLambda82(), new MaterialDynamicColors$$ExternalSyntheticLambda83(), new MaterialDynamicColors$$ExternalSyntheticLambda84(), new MaterialDynamicColors$$ExternalSyntheticLambda85(), (Function<DynamicScheme, DynamicColor>) null, new MaterialDynamicColors$$ExternalSyntheticLambda87(), new MaterialDynamicColors$$ExternalSyntheticLambda88(), (Function<DynamicScheme, ToneDeltaConstraint>) null);
    }

    static /* synthetic */ Double lambda$controlHighlight$136(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 100.0d : 0.0d);
    }

    static /* synthetic */ Double lambda$controlHighlight$137(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 0.2d : 0.12d);
    }

    static /* synthetic */ Double lambda$controlHighlight$138(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 100.0d : 0.0d);
    }

    static /* synthetic */ Double lambda$controlHighlight$140(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 100.0d : 0.0d);
    }

    static /* synthetic */ Double lambda$textPrimaryInverse$143(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 10.0d : 90.0d);
    }

    public DynamicColor textPrimaryInverse() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda27(), new MaterialDynamicColors$$ExternalSyntheticLambda28());
    }

    static /* synthetic */ Double lambda$textSecondaryAndTertiaryInverse$145(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 30.0d : 80.0d);
    }

    public DynamicColor textSecondaryAndTertiaryInverse() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda40(), new MaterialDynamicColors$$ExternalSyntheticLambda41());
    }

    static /* synthetic */ Double lambda$textPrimaryInverseDisableOnly$147(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 10.0d : 90.0d);
    }

    public DynamicColor textPrimaryInverseDisableOnly() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda121(), new MaterialDynamicColors$$ExternalSyntheticLambda122());
    }

    static /* synthetic */ Double lambda$textSecondaryAndTertiaryInverseDisabled$149(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 10.0d : 90.0d);
    }

    public DynamicColor textSecondaryAndTertiaryInverseDisabled() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda75(), new MaterialDynamicColors$$ExternalSyntheticLambda86());
    }

    static /* synthetic */ Double lambda$textHintInverse$151(DynamicScheme s) {
        return Double.valueOf(s.isDark ? 10.0d : 90.0d);
    }

    public DynamicColor textHintInverse() {
        return DynamicColor.fromPalette(new MaterialDynamicColors$$ExternalSyntheticLambda133(), new MaterialDynamicColors$$ExternalSyntheticLambda134());
    }

    private static ViewingConditions viewingConditionsForAlbers(DynamicScheme scheme) {
        return ViewingConditions.defaultWithBackgroundLstar(scheme.isDark ? 30.0d : 80.0d);
    }

    private static boolean isFidelity(DynamicScheme scheme) {
        return scheme.variant == Variant.FIDELITY || scheme.variant == Variant.CONTENT;
    }

    private static boolean isMonochrome(DynamicScheme scheme) {
        return scheme.variant == Variant.MONOCHROME;
    }

    static double findDesiredChromaByTone(double hue, double chroma, double tone, boolean byDecreasingTone) {
        double answer = tone;
        Hct closestToChroma = Hct.from(hue, chroma, tone);
        if (closestToChroma.getChroma() >= chroma) {
            return answer;
        }
        Hct closestToChroma2 = closestToChroma;
        double chromaPeak = closestToChroma.getChroma();
        while (closestToChroma2.getChroma() < chroma) {
            double answer2 = answer + (byDecreasingTone ? -1.0d : 1.0d);
            Hct potentialSolution = Hct.from(hue, chroma, answer2);
            if (chromaPeak <= potentialSolution.getChroma() && Math.abs(potentialSolution.getChroma() - chroma) >= 0.4d) {
                if (Math.abs(potentialSolution.getChroma() - chroma) < Math.abs(closestToChroma2.getChroma() - chroma)) {
                    closestToChroma2 = potentialSolution;
                }
                chromaPeak = Math.max(chromaPeak, potentialSolution.getChroma());
                answer = answer2;
            } else {
                Hct hct = closestToChroma2;
                return answer2;
            }
        }
        Hct hct2 = closestToChroma2;
        return answer;
    }

    static double performAlbers(Hct prealbers, DynamicScheme scheme) {
        Hct albersd = prealbers.inViewingConditions(viewingConditionsForAlbers(scheme));
        if (!DynamicColor.tonePrefersLightForeground(prealbers.getTone()) || DynamicColor.toneAllowsLightForeground(albersd.getTone())) {
            return DynamicColor.enableLightForeground(albersd.getTone());
        }
        return DynamicColor.enableLightForeground(prealbers.getTone());
    }
}
