package com.google.android.material.color.utilities;

import java.util.function.Function;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DynamicColor$$ExternalSyntheticLambda11 implements Function {
    public final /* synthetic */ DynamicScheme f$0;

    public /* synthetic */ DynamicColor$$ExternalSyntheticLambda11(DynamicScheme dynamicScheme) {
        this.f$0 = dynamicScheme;
    }

    public final Object apply(Object obj) {
        return Double.valueOf(((DynamicColor) obj).getTone(this.f$0));
    }
}
