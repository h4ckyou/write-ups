package androidx.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001JG\u0010\u0002\u001a\u00020\u0003\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00050\t2\u0006\u0010\n\u001a\u0002H\u00042\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016¢\u0006\u0002\u0010\r¨\u0006\u000e"}, d2 = {"androidx/activity/ComponentActivity$activityResultRegistry$1", "Landroidx/activity/result/ActivityResultRegistry;", "onLaunch", "", "I", "O", "requestCode", "", "contract", "Landroidx/activity/result/contract/ActivityResultContract;", "input", "options", "Landroidx/core/app/ActivityOptionsCompat;", "(ILandroidx/activity/result/contract/ActivityResultContract;Ljava/lang/Object;Landroidx/core/app/ActivityOptionsCompat;)V", "activity_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
/* compiled from: ComponentActivity.kt */
public final class ComponentActivity$activityResultRegistry$1 extends ActivityResultRegistry {
    final /* synthetic */ ComponentActivity this$0;

    ComponentActivity$activityResultRegistry$1(ComponentActivity $receiver) {
        this.this$0 = $receiver;
    }

    public <I, O> void onLaunch(int requestCode, ActivityResultContract<I, O> contract, I input, ActivityOptionsCompat options) {
        Bundle optionsBundle;
        Bundle optionsBundle2;
        int i = requestCode;
        ActivityResultContract<I, O> activityResultContract = contract;
        I i2 = input;
        Intrinsics.checkNotNullParameter(activityResultContract, "contract");
        ComponentActivity activity = this.this$0;
        ActivityResultContract.SynchronousResult synchronousResult = activityResultContract.getSynchronousResult(activity, i2);
        if (synchronousResult != null) {
            new Handler(Looper.getMainLooper()).post(new ComponentActivity$activityResultRegistry$1$$ExternalSyntheticLambda0(this, i, synchronousResult));
            return;
        }
        Intent intent = activityResultContract.createIntent(activity, i2);
        if (intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            Intrinsics.checkNotNull(extras);
            if (extras.getClassLoader() == null) {
                intent.setExtrasClassLoader(activity.getClassLoader());
            }
        }
        if (intent.hasExtra(ActivityResultContracts.StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE)) {
            Bundle optionsBundle3 = intent.getBundleExtra(ActivityResultContracts.StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE);
            intent.removeExtra(ActivityResultContracts.StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE);
            optionsBundle = optionsBundle3;
        } else if (options != null) {
            optionsBundle = options.toBundle();
        } else {
            optionsBundle = null;
        }
        if (Intrinsics.areEqual((Object) ActivityResultContracts.RequestMultiplePermissions.ACTION_REQUEST_PERMISSIONS, (Object) intent.getAction())) {
            String[] permissions = intent.getStringArrayExtra(ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSIONS);
            if (permissions == null) {
                permissions = new String[0];
            }
            ActivityCompat.requestPermissions(activity, permissions, i);
            Bundle bundle = optionsBundle;
        } else if (Intrinsics.areEqual((Object) ActivityResultContracts.StartIntentSenderForResult.ACTION_INTENT_SENDER_REQUEST, (Object) intent.getAction())) {
            IntentSenderRequest request = (IntentSenderRequest) intent.getParcelableExtra(ActivityResultContracts.StartIntentSenderForResult.EXTRA_INTENT_SENDER_REQUEST);
            try {
                Activity activity2 = activity;
                Intrinsics.checkNotNull(request);
                optionsBundle2 = optionsBundle;
                try {
                    ActivityCompat.startIntentSenderForResult(activity2, request.getIntentSender(), requestCode, request.getFillInIntent(), request.getFlagsMask(), request.getFlagsValues(), 0, optionsBundle2);
                    Bundle bundle2 = optionsBundle2;
                } catch (IntentSender.SendIntentException e) {
                    e = e;
                    new Handler(Looper.getMainLooper()).post(new ComponentActivity$activityResultRegistry$1$$ExternalSyntheticLambda1(this, i, e));
                    Bundle bundle3 = optionsBundle2;
                }
            } catch (IntentSender.SendIntentException e2) {
                e = e2;
                optionsBundle2 = optionsBundle;
                new Handler(Looper.getMainLooper()).post(new ComponentActivity$activityResultRegistry$1$$ExternalSyntheticLambda1(this, i, e));
                Bundle bundle32 = optionsBundle2;
            }
        } else {
            ActivityCompat.startActivityForResult(activity, intent, i, optionsBundle);
        }
    }

    /* access modifiers changed from: private */
    public static final void onLaunch$lambda$0(ComponentActivity$activityResultRegistry$1 this$02, int $requestCode, ActivityResultContract.SynchronousResult $synchronousResult) {
        Intrinsics.checkNotNullParameter(this$02, "this$0");
        this$02.dispatchResult($requestCode, $synchronousResult.getValue());
    }

    /* access modifiers changed from: private */
    public static final void onLaunch$lambda$1(ComponentActivity$activityResultRegistry$1 this$02, int $requestCode, IntentSender.SendIntentException $e) {
        Intrinsics.checkNotNullParameter(this$02, "this$0");
        Intrinsics.checkNotNullParameter($e, "$e");
        this$02.dispatchResult($requestCode, 0, new Intent().setAction(ActivityResultContracts.StartIntentSenderForResult.ACTION_INTENT_SENDER_REQUEST).putExtra(ActivityResultContracts.StartIntentSenderForResult.EXTRA_SEND_INTENT_EXCEPTION, $e));
    }
}
