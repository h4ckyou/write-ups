package com.google.android.material.color.utilities;

import java.util.function.Function;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DynamicColor$$ExternalSyntheticLambda18 implements Function {
    public final /* synthetic */ Function f$0;

    public /* synthetic */ DynamicColor$$ExternalSyntheticLambda18(Function function) {
        this.f$0 = function;
    }

    public final Object apply(Object obj) {
        return Double.valueOf(((TonalPalette) this.f$0.apply((DynamicScheme) obj)).getChroma());
    }
}
