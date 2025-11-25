package com.google.android.material.color.utilities;

import java.util.function.BiFunction;
import java.util.function.Function;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DynamicColor$$ExternalSyntheticLambda6 implements BiFunction {
    public final /* synthetic */ Function f$0;
    public final /* synthetic */ DynamicScheme f$1;

    public /* synthetic */ DynamicColor$$ExternalSyntheticLambda6(Function function, DynamicScheme dynamicScheme) {
        this.f$0 = function;
        this.f$1 = dynamicScheme;
    }

    public final Object apply(Object obj, Object obj2) {
        return DynamicColor.lambda$toneMaxContrastDefault$18(this.f$0, this.f$1, (Double) obj, (Double) obj2);
    }
}
