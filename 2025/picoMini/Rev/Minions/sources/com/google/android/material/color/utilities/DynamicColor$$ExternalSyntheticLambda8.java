package com.google.android.material.color.utilities;

import java.util.function.BiFunction;
import java.util.function.Function;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DynamicColor$$ExternalSyntheticLambda8 implements BiFunction {
    public final /* synthetic */ Function f$0;
    public final /* synthetic */ DynamicScheme f$1;
    public final /* synthetic */ Function f$2;

    public /* synthetic */ DynamicColor$$ExternalSyntheticLambda8(Function function, DynamicScheme dynamicScheme, Function function2) {
        this.f$0 = function;
        this.f$1 = dynamicScheme;
        this.f$2 = function2;
    }

    public final Object apply(Object obj, Object obj2) {
        return DynamicColor.lambda$toneMinContrastDefault$15(this.f$0, this.f$1, this.f$2, (Double) obj, (Double) obj2);
    }
}
