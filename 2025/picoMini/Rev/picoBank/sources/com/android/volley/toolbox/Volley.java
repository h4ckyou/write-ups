package com.android.volley.toolbox;

import android.content.Context;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import java.io.File;

public class Volley {
    private static final String DEFAULT_CACHE_DIR = "volley";

    public static RequestQueue newRequestQueue(Context context, BaseHttpStack stack) {
        BasicNetwork network;
        if (stack == null) {
            network = new BasicNetwork((BaseHttpStack) new HurlStack());
        } else {
            network = new BasicNetwork(stack);
        }
        return newRequestQueue(context, (Network) network);
    }

    @Deprecated
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        if (stack != null) {
            return newRequestQueue(context, (Network) new BasicNetwork(stack));
        }
        BaseHttpStack baseHttpStack = null;
        return newRequestQueue(context, (BaseHttpStack) null);
    }

    private static RequestQueue newRequestQueue(Context context, Network network) {
        final Context appContext = context.getApplicationContext();
        RequestQueue queue = new RequestQueue(new DiskBasedCache(new DiskBasedCache.FileSupplier() {
            private File cacheDir = null;

            public File get() {
                if (this.cacheDir == null) {
                    this.cacheDir = new File(appContext.getCacheDir(), Volley.DEFAULT_CACHE_DIR);
                }
                return this.cacheDir;
            }
        }), network);
        queue.start();
        return queue;
    }

    public static RequestQueue newRequestQueue(Context context) {
        BaseHttpStack baseHttpStack = null;
        return newRequestQueue(context, (BaseHttpStack) null);
    }
}
