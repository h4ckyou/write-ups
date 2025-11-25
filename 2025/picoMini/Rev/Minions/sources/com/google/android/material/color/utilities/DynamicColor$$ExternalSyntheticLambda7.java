package com.google.android.material.color.utilities;

import java.util.function.Function;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DynamicColor$$ExternalSyntheticLambda7 implements Function {
    public final /* synthetic */ DynamicScheme f$0;

    public /* synthetic */ DynamicColor$$ExternalSyntheticLambda7(DynamicScheme dynamicScheme) {
        this.f$0 = dynamicScheme;
    }

    public final Object apply(Object obj) {
        return ((DynamicColor) obj).toneMinContrast.apply(this.f$0);
    }
}
