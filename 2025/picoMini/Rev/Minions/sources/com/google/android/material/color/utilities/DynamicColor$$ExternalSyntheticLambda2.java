package com.google.android.material.color.utilities;

import java.util.function.Function;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DynamicColor$$ExternalSyntheticLambda2 implements Function {
    public final /* synthetic */ Function f$0;
    public final /* synthetic */ Function f$1;
    public final /* synthetic */ Function f$2;

    public /* synthetic */ DynamicColor$$ExternalSyntheticLambda2(Function function, Function function2, Function function3) {
        this.f$0 = function;
        this.f$1 = function2;
        this.f$2 = function3;
    }

    public final Object apply(Object obj) {
        return Double.valueOf(DynamicColor.toneMaxContrastDefault(this.f$0, this.f$1, (DynamicScheme) obj, this.f$2));
    }
}
