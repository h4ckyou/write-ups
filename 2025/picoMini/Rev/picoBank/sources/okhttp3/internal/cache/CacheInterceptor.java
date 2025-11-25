package okhttp3.internal.cache;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import com.android.volley.toolbox.HttpHeaderParser;
import java.io.Closeable;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.RealResponseBody;
import okio.Okio;
import okio.Sink;
import okio.Source;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u000f\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0004J\u001a\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\bH\u0002J\u0010\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0010"}, d2 = {"Lokhttp3/internal/cache/CacheInterceptor;", "Lokhttp3/Interceptor;", "cache", "Lokhttp3/Cache;", "(Lokhttp3/Cache;)V", "getCache$okhttp", "()Lokhttp3/Cache;", "cacheWritingResponse", "Lokhttp3/Response;", "cacheRequest", "Lokhttp3/internal/cache/CacheRequest;", "response", "intercept", "chain", "Lokhttp3/Interceptor$Chain;", "Companion", "okhttp"}, k = 1, mv = {1, 4, 0})
/* compiled from: CacheInterceptor.kt */
public final class CacheInterceptor implements Interceptor {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private final Cache cache;

    public CacheInterceptor(Cache cache2) {
        this.cache = cache2;
    }

    public final Cache getCache$okhttp() {
        return this.cache;
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        EventListener listener;
        ResponseBody body;
        ResponseBody body2;
        Intrinsics.checkNotNullParameter(chain, "chain");
        Call call = chain.call();
        Cache cache2 = this.cache;
        Response cacheCandidate = cache2 != null ? cache2.get$okhttp(chain.request()) : null;
        CacheStrategy strategy = new CacheStrategy.Factory(System.currentTimeMillis(), chain.request(), cacheCandidate).compute();
        Request networkRequest = strategy.getNetworkRequest();
        Response cacheResponse = strategy.getCacheResponse();
        Cache cache3 = this.cache;
        if (cache3 != null) {
            cache3.trackResponse$okhttp(strategy);
        }
        RealCall realCall = (RealCall) (!(call instanceof RealCall) ? null : call);
        if (realCall == null || (listener = realCall.getEventListener$okhttp()) == null) {
            listener = EventListener.NONE;
        }
        if (!(cacheCandidate == null || cacheResponse != null || (body2 = cacheCandidate.body()) == null)) {
            Util.closeQuietly((Closeable) body2);
        }
        if (networkRequest == null && cacheResponse == null) {
            Response it = new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(TypedValues.PositionType.TYPE_PERCENT_HEIGHT).message("Unsatisfiable Request (only-if-cached)").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1).receivedResponseAtMillis(System.currentTimeMillis()).build();
            listener.satisfactionFailure(call, it);
            return it;
        } else if (networkRequest == null) {
            Intrinsics.checkNotNull(cacheResponse);
            Response it2 = cacheResponse.newBuilder().cacheResponse(Companion.stripBody(cacheResponse)).build();
            listener.cacheHit(call, it2);
            return it2;
        } else {
            if (cacheResponse != null) {
                listener.cacheConditionalHit(call, cacheResponse);
            } else if (this.cache != null) {
                listener.cacheMiss(call);
            }
            Response response = null;
            try {
                Response networkResponse = chain.proceed(networkRequest);
                if (!(networkResponse != null || cacheCandidate == null || body == null)) {
                }
                if (cacheResponse != null) {
                    if (networkResponse == null || networkResponse.code() != 304) {
                        ResponseBody body3 = cacheResponse.body();
                        if (body3 != null) {
                            Util.closeQuietly((Closeable) body3);
                        }
                    } else {
                        Response response2 = cacheResponse.newBuilder().headers(Companion.combine(cacheResponse.headers(), networkResponse.headers())).sentRequestAtMillis(networkResponse.sentRequestAtMillis()).receivedResponseAtMillis(networkResponse.receivedResponseAtMillis()).cacheResponse(Companion.stripBody(cacheResponse)).networkResponse(Companion.stripBody(networkResponse)).build();
                        ResponseBody body4 = networkResponse.body();
                        Intrinsics.checkNotNull(body4);
                        body4.close();
                        Cache cache4 = this.cache;
                        Intrinsics.checkNotNull(cache4);
                        cache4.trackConditionalCacheHit$okhttp();
                        this.cache.update$okhttp(cacheResponse, response2);
                        listener.cacheHit(call, response2);
                        return response2;
                    }
                }
                Intrinsics.checkNotNull(networkResponse);
                Response response3 = networkResponse.newBuilder().cacheResponse(Companion.stripBody(cacheResponse)).networkResponse(Companion.stripBody(networkResponse)).build();
                if (this.cache != null) {
                    if (HttpHeaders.promisesBody(response3) && CacheStrategy.Companion.isCacheable(response3, networkRequest)) {
                        Response cacheWritingResponse = cacheWritingResponse(this.cache.put$okhttp(response3), response3);
                        Response response4 = cacheWritingResponse;
                        if (cacheResponse != null) {
                            listener.cacheMiss(call);
                        }
                        return cacheWritingResponse;
                    } else if (HttpMethod.INSTANCE.invalidatesCache(networkRequest.method())) {
                        try {
                            this.cache.remove$okhttp(networkRequest);
                        } catch (IOException e) {
                        }
                    }
                }
                return response3;
            } finally {
                if (!(cacheCandidate == null || (body = cacheCandidate.body()) == null)) {
                    Util.closeQuietly((Closeable) body);
                }
            }
        }
    }

    private final Response cacheWritingResponse(CacheRequest cacheRequest, Response response) throws IOException {
        if (cacheRequest == null) {
            return response;
        }
        Sink cacheBodyUnbuffered = cacheRequest.body();
        ResponseBody body = response.body();
        Intrinsics.checkNotNull(body);
        CacheInterceptor$cacheWritingResponse$cacheWritingSource$1 cacheWritingSource = new CacheInterceptor$cacheWritingResponse$cacheWritingSource$1(body.source(), cacheRequest, Okio.buffer(cacheBodyUnbuffered));
        return response.newBuilder().body(new RealResponseBody(Response.header$default(response, HttpHeaderParser.HEADER_CONTENT_TYPE, (String) null, 2, (Object) null), response.body().contentLength(), Okio.buffer((Source) cacheWritingSource))).build();
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004H\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u0010\u0010\u000b\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u0014\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0002¨\u0006\u000f"}, d2 = {"Lokhttp3/internal/cache/CacheInterceptor$Companion;", "", "()V", "combine", "Lokhttp3/Headers;", "cachedHeaders", "networkHeaders", "isContentSpecificHeader", "", "fieldName", "", "isEndToEnd", "stripBody", "Lokhttp3/Response;", "response", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: CacheInterceptor.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        /* access modifiers changed from: private */
        public final Response stripBody(Response response) {
            if ((response != null ? response.body() : null) != null) {
                return response.newBuilder().body((ResponseBody) null).build();
            }
            return response;
        }

        /* access modifiers changed from: private */
        public final Headers combine(Headers cachedHeaders, Headers networkHeaders) {
            Headers.Builder result = new Headers.Builder();
            int size = cachedHeaders.size();
            for (int index = 0; index < size; index++) {
                String fieldName = cachedHeaders.name(index);
                String value = cachedHeaders.value(index);
                if ((!StringsKt.equals("Warning", fieldName, true) || !StringsKt.startsWith$default(value, "1", false, 2, (Object) null)) && (isContentSpecificHeader(fieldName) || !isEndToEnd(fieldName) || networkHeaders.get(fieldName) == null)) {
                    result.addLenient$okhttp(fieldName, value);
                }
            }
            int size2 = networkHeaders.size();
            for (int index2 = 0; index2 < size2; index2++) {
                String fieldName2 = networkHeaders.name(index2);
                if (!isContentSpecificHeader(fieldName2) && isEndToEnd(fieldName2)) {
                    result.addLenient$okhttp(fieldName2, networkHeaders.value(index2));
                }
            }
            return result.build();
        }

        private final boolean isEndToEnd(String fieldName) {
            return !StringsKt.equals("Connection", fieldName, true) && !StringsKt.equals("Keep-Alive", fieldName, true) && !StringsKt.equals("Proxy-Authenticate", fieldName, true) && !StringsKt.equals("Proxy-Authorization", fieldName, true) && !StringsKt.equals("TE", fieldName, true) && !StringsKt.equals("Trailers", fieldName, true) && !StringsKt.equals("Transfer-Encoding", fieldName, true) && !StringsKt.equals("Upgrade", fieldName, true);
        }

        private final boolean isContentSpecificHeader(String fieldName) {
            if (StringsKt.equals("Content-Length", fieldName, true) || StringsKt.equals("Content-Encoding", fieldName, true) || StringsKt.equals(HttpHeaderParser.HEADER_CONTENT_TYPE, fieldName, true)) {
                return true;
            }
            return false;
        }
    }
}
