package com.android.volley;

import com.android.volley.Request;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

class WaitingRequestManager implements Request.NetworkRequestCompleteListener {
    private final CacheDispatcher mCacheDispatcher;
    private final BlockingQueue<Request<?>> mNetworkQueue;
    private final RequestQueue mRequestQueue;
    private final ResponseDelivery mResponseDelivery;
    private final Map<String, List<Request<?>>> mWaitingRequests;

    WaitingRequestManager(RequestQueue requestQueue) {
        this.mWaitingRequests = new HashMap();
        this.mRequestQueue = requestQueue;
        this.mResponseDelivery = this.mRequestQueue.getResponseDelivery();
        this.mCacheDispatcher = null;
        this.mNetworkQueue = null;
    }

    WaitingRequestManager(CacheDispatcher cacheDispatcher, BlockingQueue<Request<?>> networkQueue, ResponseDelivery responseDelivery) {
        this.mWaitingRequests = new HashMap();
        this.mRequestQueue = null;
        this.mResponseDelivery = responseDelivery;
        this.mCacheDispatcher = cacheDispatcher;
        this.mNetworkQueue = networkQueue;
    }

    public void onResponseReceived(Request<?> request, Response<?> response) {
        List<Request<?>> waitingRequests;
        if (response.cacheEntry == null || response.cacheEntry.isExpired()) {
            onNoUsableResponseReceived(request);
            return;
        }
        String cacheKey = request.getCacheKey();
        synchronized (this) {
            waitingRequests = this.mWaitingRequests.remove(cacheKey);
        }
        if (waitingRequests != null) {
            if (VolleyLog.DEBUG) {
                VolleyLog.v("Releasing %d waiting requests for cacheKey=%s.", Integer.valueOf(waitingRequests.size()), cacheKey);
            }
            for (Request<?> waiting : waitingRequests) {
                this.mResponseDelivery.postResponse(waiting, response);
            }
        }
    }

    public synchronized void onNoUsableResponseReceived(Request<?> request) {
        String cacheKey = request.getCacheKey();
        List<Request<?>> waitingRequests = this.mWaitingRequests.remove(cacheKey);
        if (waitingRequests != null && !waitingRequests.isEmpty()) {
            if (VolleyLog.DEBUG) {
                VolleyLog.v("%d waiting requests for cacheKey=%s; resend to network", Integer.valueOf(waitingRequests.size()), cacheKey);
            }
            Request<?> nextInLine = waitingRequests.remove(0);
            this.mWaitingRequests.put(cacheKey, waitingRequests);
            nextInLine.setNetworkRequestCompleteListener(this);
            if (this.mRequestQueue != null) {
                this.mRequestQueue.sendRequestOverNetwork(nextInLine);
            } else if (!(this.mCacheDispatcher == null || this.mNetworkQueue == null)) {
                try {
                    this.mNetworkQueue.put(nextInLine);
                } catch (InterruptedException iex) {
                    VolleyLog.e("Couldn't add request to queue. %s", iex.toString());
                    Thread.currentThread().interrupt();
                    this.mCacheDispatcher.quit();
                }
            }
        }
        return;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0039, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0052, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean maybeAddToWaitingRequests(com.android.volley.Request<?> r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            java.lang.String r0 = r5.getCacheKey()     // Catch:{ all -> 0x0054 }
            java.util.Map<java.lang.String, java.util.List<com.android.volley.Request<?>>> r1 = r4.mWaitingRequests     // Catch:{ all -> 0x0054 }
            boolean r1 = r1.containsKey(r0)     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x003b
            java.util.Map<java.lang.String, java.util.List<com.android.volley.Request<?>>> r1 = r4.mWaitingRequests     // Catch:{ all -> 0x0054 }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ all -> 0x0054 }
            java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x0054 }
            if (r1 != 0) goto L_0x001d
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x0054 }
            r2.<init>()     // Catch:{ all -> 0x0054 }
            r1 = r2
        L_0x001d:
            java.lang.String r2 = "waiting-for-response"
            r5.addMarker(r2)     // Catch:{ all -> 0x0054 }
            r1.add(r5)     // Catch:{ all -> 0x0054 }
            java.util.Map<java.lang.String, java.util.List<com.android.volley.Request<?>>> r2 = r4.mWaitingRequests     // Catch:{ all -> 0x0054 }
            r2.put(r0, r1)     // Catch:{ all -> 0x0054 }
            boolean r2 = com.android.volley.VolleyLog.DEBUG     // Catch:{ all -> 0x0054 }
            if (r2 == 0) goto L_0x0038
            java.lang.String r2 = "Request for cacheKey=%s is in flight, putting on hold."
            java.lang.Object[] r3 = new java.lang.Object[]{r0}     // Catch:{ all -> 0x0054 }
            com.android.volley.VolleyLog.d(r2, r3)     // Catch:{ all -> 0x0054 }
        L_0x0038:
            monitor-exit(r4)
            r2 = 1
            return r2
        L_0x003b:
            java.util.Map<java.lang.String, java.util.List<com.android.volley.Request<?>>> r1 = r4.mWaitingRequests     // Catch:{ all -> 0x0054 }
            r2 = 0
            r1.put(r0, r2)     // Catch:{ all -> 0x0054 }
            r5.setNetworkRequestCompleteListener(r4)     // Catch:{ all -> 0x0054 }
            boolean r1 = com.android.volley.VolleyLog.DEBUG     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0051
            java.lang.String r1 = "new request, sending to network %s"
            java.lang.Object[] r2 = new java.lang.Object[]{r0}     // Catch:{ all -> 0x0054 }
            com.android.volley.VolleyLog.d(r1, r2)     // Catch:{ all -> 0x0054 }
        L_0x0051:
            monitor-exit(r4)
            r1 = 0
            return r1
        L_0x0054:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.volley.WaitingRequestManager.maybeAddToWaitingRequests(com.android.volley.Request):boolean");
    }
}
