package androidx.activity;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.lifecycle.LifecycleEventObserver;
import java.lang.reflect.Field;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0000\u0018\u0000 \f2\u00020\u0001:\u0004\u000b\f\r\u000eB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Landroidx/activity/ImmLeaksCleaner;", "Landroidx/lifecycle/LifecycleEventObserver;", "activity", "Landroid/app/Activity;", "(Landroid/app/Activity;)V", "onStateChanged", "", "source", "Landroidx/lifecycle/LifecycleOwner;", "event", "Landroidx/lifecycle/Lifecycle$Event;", "Cleaner", "Companion", "FailedInitialization", "ValidCleaner", "activity_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
/* compiled from: ImmLeaksCleaner.kt */
public final class ImmLeaksCleaner implements LifecycleEventObserver {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public static final Lazy<Cleaner> cleaner$delegate = LazyKt.lazy(ImmLeaksCleaner$Companion$cleaner$2.INSTANCE);
    private final Activity activity;

    public ImmLeaksCleaner(Activity activity2) {
        Intrinsics.checkNotNullParameter(activity2, "activity");
        this.activity = activity2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0047, code lost:
        if (r6 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0049, code lost:
        r0.isActive();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onStateChanged(androidx.lifecycle.LifecycleOwner r8, androidx.lifecycle.Lifecycle.Event r9) {
        /*
            r7 = this;
            java.lang.String r0 = "source"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            java.lang.String r0 = "event"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r9, r0)
            androidx.lifecycle.Lifecycle$Event r0 = androidx.lifecycle.Lifecycle.Event.ON_DESTROY
            if (r9 == r0) goto L_0x0010
            return
        L_0x0010:
            android.app.Activity r0 = r7.activity
            java.lang.String r1 = "input_method"
            java.lang.Object r0 = r0.getSystemService(r1)
            java.lang.String r1 = "null cannot be cast to non-null type android.view.inputmethod.InputMethodManager"
            kotlin.jvm.internal.Intrinsics.checkNotNull(r0, r1)
            android.view.inputmethod.InputMethodManager r0 = (android.view.inputmethod.InputMethodManager) r0
            androidx.activity.ImmLeaksCleaner$Companion r1 = Companion
            androidx.activity.ImmLeaksCleaner$Cleaner r1 = r1.getCleaner()
            r2 = 0
            java.lang.Object r3 = r1.getLock(r0)
            if (r3 != 0) goto L_0x002e
            return
        L_0x002e:
            monitor-enter(r3)
            r4 = 0
            android.view.View r5 = r1.getServedView(r0)     // Catch:{ all -> 0x004f }
            if (r5 != 0) goto L_0x0038
            monitor-exit(r3)
            return
        L_0x0038:
            boolean r6 = r5.isAttachedToWindow()     // Catch:{ all -> 0x004f }
            if (r6 == 0) goto L_0x0041
            monitor-exit(r3)
            return
        L_0x0041:
            boolean r6 = r1.clearNextServedView(r0)     // Catch:{ all -> 0x004f }
            monitor-exit(r3)
            r4 = r6
            if (r4 == 0) goto L_0x004c
            r0.isActive()
        L_0x004c:
            return
        L_0x004f:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.activity.ImmLeaksCleaner.onStateChanged(androidx.lifecycle.LifecycleOwner, androidx.lifecycle.Lifecycle$Event):void");
    }

    @Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001B\u0007\b\u0004¢\u0006\u0002\u0010\u0002J\f\u0010\u000b\u001a\u00020\f*\u00020\u0004H&R\u0018\u0010\u0003\u001a\u0004\u0018\u00010\u0001*\u00020\u0004X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b*\u00020\u0004X¦\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\n\u0001\u0002\r\u000e¨\u0006\u000f"}, d2 = {"Landroidx/activity/ImmLeaksCleaner$Cleaner;", "", "()V", "lock", "Landroid/view/inputmethod/InputMethodManager;", "getLock", "(Landroid/view/inputmethod/InputMethodManager;)Ljava/lang/Object;", "servedView", "Landroid/view/View;", "getServedView", "(Landroid/view/inputmethod/InputMethodManager;)Landroid/view/View;", "clearNextServedView", "", "Landroidx/activity/ImmLeaksCleaner$FailedInitialization;", "Landroidx/activity/ImmLeaksCleaner$ValidCleaner;", "activity_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* compiled from: ImmLeaksCleaner.kt */
    public static abstract class Cleaner {
        public /* synthetic */ Cleaner(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public abstract boolean clearNextServedView(InputMethodManager inputMethodManager);

        public abstract Object getLock(InputMethodManager inputMethodManager);

        public abstract View getServedView(InputMethodManager inputMethodManager);

        private Cleaner() {
        }
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\f\u0010\f\u001a\u00020\r*\u00020\u0005H\u0016R\u001a\u0010\u0003\u001a\u0004\u0018\u00010\u0004*\u00020\u00058VX\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\u0004\u0018\u00010\t*\u00020\u00058VX\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006\u000e"}, d2 = {"Landroidx/activity/ImmLeaksCleaner$FailedInitialization;", "Landroidx/activity/ImmLeaksCleaner$Cleaner;", "()V", "lock", "", "Landroid/view/inputmethod/InputMethodManager;", "getLock", "(Landroid/view/inputmethod/InputMethodManager;)Ljava/lang/Object;", "servedView", "Landroid/view/View;", "getServedView", "(Landroid/view/inputmethod/InputMethodManager;)Landroid/view/View;", "clearNextServedView", "", "activity_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* compiled from: ImmLeaksCleaner.kt */
    public static final class FailedInitialization extends Cleaner {
        public static final FailedInitialization INSTANCE = new FailedInitialization();

        private FailedInitialization() {
            super((DefaultConstructorMarker) null);
        }

        public Object getLock(InputMethodManager $this$lock) {
            Intrinsics.checkNotNullParameter($this$lock, "<this>");
            return null;
        }

        public View getServedView(InputMethodManager $this$servedView) {
            Intrinsics.checkNotNullParameter($this$servedView, "<this>");
            return null;
        }

        public boolean clearNextServedView(InputMethodManager $this$clearNextServedView) {
            Intrinsics.checkNotNullParameter($this$clearNextServedView, "<this>");
            return false;
        }
    }

    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0002\u0010\u0006J\f\u0010\u0010\u001a\u00020\u0011*\u00020\tH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u0004\u0018\u00010\b*\u00020\t8VX\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\u0004\u0018\u00010\r*\u00020\t8VX\u0004¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0012"}, d2 = {"Landroidx/activity/ImmLeaksCleaner$ValidCleaner;", "Landroidx/activity/ImmLeaksCleaner$Cleaner;", "hField", "Ljava/lang/reflect/Field;", "servedViewField", "nextServedViewField", "(Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;Ljava/lang/reflect/Field;)V", "lock", "", "Landroid/view/inputmethod/InputMethodManager;", "getLock", "(Landroid/view/inputmethod/InputMethodManager;)Ljava/lang/Object;", "servedView", "Landroid/view/View;", "getServedView", "(Landroid/view/inputmethod/InputMethodManager;)Landroid/view/View;", "clearNextServedView", "", "activity_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* compiled from: ImmLeaksCleaner.kt */
    public static final class ValidCleaner extends Cleaner {
        private final Field hField;
        private final Field nextServedViewField;
        private final Field servedViewField;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public ValidCleaner(Field hField2, Field servedViewField2, Field nextServedViewField2) {
            super((DefaultConstructorMarker) null);
            Intrinsics.checkNotNullParameter(hField2, "hField");
            Intrinsics.checkNotNullParameter(servedViewField2, "servedViewField");
            Intrinsics.checkNotNullParameter(nextServedViewField2, "nextServedViewField");
            this.hField = hField2;
            this.servedViewField = servedViewField2;
            this.nextServedViewField = nextServedViewField2;
        }

        public Object getLock(InputMethodManager $this$lock) {
            Intrinsics.checkNotNullParameter($this$lock, "<this>");
            try {
                return this.hField.get($this$lock);
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        public View getServedView(InputMethodManager $this$servedView) {
            Intrinsics.checkNotNullParameter($this$servedView, "<this>");
            try {
                return (View) this.servedViewField.get($this$servedView);
            } catch (ClassCastException | IllegalAccessException e) {
                return null;
            }
        }

        public boolean clearNextServedView(InputMethodManager $this$clearNextServedView) {
            Intrinsics.checkNotNullParameter($this$clearNextServedView, "<this>");
            try {
                this.nextServedViewField.set($this$clearNextServedView, (Object) null);
                return true;
            } catch (IllegalAccessException e) {
                return false;
            }
        }
    }

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006¨\u0006\t"}, d2 = {"Landroidx/activity/ImmLeaksCleaner$Companion;", "", "()V", "cleaner", "Landroidx/activity/ImmLeaksCleaner$Cleaner;", "getCleaner", "()Landroidx/activity/ImmLeaksCleaner$Cleaner;", "cleaner$delegate", "Lkotlin/Lazy;", "activity_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    /* compiled from: ImmLeaksCleaner.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Cleaner getCleaner() {
            return (Cleaner) ImmLeaksCleaner.cleaner$delegate.getValue();
        }
    }
}
