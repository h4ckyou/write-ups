package okhttp3.internal.http;

import com.android.volley.toolbox.HttpHeaderParser;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.List;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.connection.RouteException;
import okhttp3.internal.http2.ConnectionShutdownException;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u0000 \u001e2\u00020\u0001:\u0001\u001eB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u001a\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u001c\u0010\u000b\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0002J\u0010\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0012H\u0002J(\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u0012H\u0002J\u0018\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u0006H\u0002J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u001cH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u001f"}, d2 = {"Lokhttp3/internal/http/RetryAndFollowUpInterceptor;", "Lokhttp3/Interceptor;", "client", "Lokhttp3/OkHttpClient;", "(Lokhttp3/OkHttpClient;)V", "buildRedirectRequest", "Lokhttp3/Request;", "userResponse", "Lokhttp3/Response;", "method", "", "followUpRequest", "exchange", "Lokhttp3/internal/connection/Exchange;", "intercept", "chain", "Lokhttp3/Interceptor$Chain;", "isRecoverable", "", "e", "Ljava/io/IOException;", "requestSendStarted", "recover", "call", "Lokhttp3/internal/connection/RealCall;", "userRequest", "requestIsOneShot", "retryAfter", "", "defaultDelay", "Companion", "okhttp"}, k = 1, mv = {1, 4, 0})
/* compiled from: RetryAndFollowUpInterceptor.kt */
public final class RetryAndFollowUpInterceptor implements Interceptor {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private static final int MAX_FOLLOW_UPS = 20;
    private final OkHttpClient client;

    public RetryAndFollowUpInterceptor(OkHttpClient client2) {
        Intrinsics.checkNotNullParameter(client2, "client");
        this.client = client2;
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        boolean z;
        Interceptor.Chain chain2 = chain;
        Intrinsics.checkNotNullParameter(chain2, "chain");
        RealInterceptorChain realChain = (RealInterceptorChain) chain2;
        Request request = ((RealInterceptorChain) chain2).getRequest$okhttp();
        RealCall call = realChain.getCall$okhttp();
        List recoveredFailures = CollectionsKt.emptyList();
        boolean newExchangeFinder = true;
        Response priorResponse = null;
        int followUpCount = 0;
        Request request2 = request;
        while (true) {
            call.enterNetworkInterceptorExchange(request2, newExchangeFinder);
            boolean closeActiveExchange = true;
            try {
                if (!call.isCanceled()) {
                    z = false;
                    Response response = realChain.proceed(request2);
                    newExchangeFinder = true;
                    if (priorResponse != null) {
                        response = response.newBuilder().priorResponse(priorResponse.newBuilder().body((ResponseBody) null).build()).build();
                    }
                    Exchange exchange = call.getInterceptorScopedExchange$okhttp();
                    Request followUp = followUpRequest(response, exchange);
                    if (followUp == null) {
                        if (exchange != null && exchange.isDuplex$okhttp()) {
                            call.timeoutEarlyExit();
                        }
                        call.exitNetworkInterceptorExchange$okhttp(false);
                        return response;
                    }
                    RequestBody followUpBody = followUp.body();
                    if (followUpBody == null || !followUpBody.isOneShot()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            Util.closeQuietly((Closeable) body);
                        }
                        followUpCount++;
                        if (followUpCount <= 20) {
                            request2 = followUp;
                            priorResponse = response;
                        } else {
                            Exchange exchange2 = exchange;
                            throw new ProtocolException("Too many follow-up requests: " + followUpCount);
                        }
                    } else {
                        closeActiveExchange = false;
                        return response;
                    }
                } else {
                    throw new IOException("Canceled");
                }
            } catch (RouteException e) {
                RouteException e2 = e;
                if (recover(e2.getLastConnectException(), call, request2, false)) {
                    recoveredFailures = CollectionsKt.plus(recoveredFailures, e2.getFirstConnectException());
                    newExchangeFinder = false;
                } else {
                    throw Util.withSuppressed(e2.getFirstConnectException(), recoveredFailures);
                }
            } catch (IOException e3) {
                IOException e4 = e3;
                if (!(e4 instanceof ConnectionShutdownException)) {
                    z = true;
                }
                if (recover(e4, call, request2, z)) {
                    recoveredFailures = CollectionsKt.plus(recoveredFailures, e4);
                    newExchangeFinder = false;
                } else {
                    throw Util.withSuppressed(e4, recoveredFailures);
                }
            } finally {
                call.exitNetworkInterceptorExchange$okhttp(closeActiveExchange);
            }
        }
    }

    private final boolean recover(IOException e, RealCall call, Request userRequest, boolean requestSendStarted) {
        if (!this.client.retryOnConnectionFailure()) {
            return false;
        }
        if ((!requestSendStarted || !requestIsOneShot(e, userRequest)) && isRecoverable(e, requestSendStarted) && call.retryAfterFailure()) {
            return true;
        }
        return false;
    }

    private final boolean requestIsOneShot(IOException e, Request userRequest) {
        RequestBody requestBody = userRequest.body();
        return (requestBody != null && requestBody.isOneShot()) || (e instanceof FileNotFoundException);
    }

    private final boolean isRecoverable(IOException e, boolean requestSendStarted) {
        if (e instanceof ProtocolException) {
            return false;
        }
        if (e instanceof InterruptedIOException) {
            if (!(e instanceof SocketTimeoutException) || requestSendStarted) {
                return false;
            }
            return true;
        } else if ((!(e instanceof SSLHandshakeException) || !(e.getCause() instanceof CertificateException)) && !(e instanceof SSLPeerUnverifiedException)) {
            return true;
        } else {
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0003, code lost:
        r1 = r10.getConnection$okhttp();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final okhttp3.Request followUpRequest(okhttp3.Response r9, okhttp3.internal.connection.Exchange r10) throws java.io.IOException {
        /*
            r8 = this;
            r0 = 0
            if (r10 == 0) goto L_0x000e
            okhttp3.internal.connection.RealConnection r1 = r10.getConnection$okhttp()
            if (r1 == 0) goto L_0x000e
            okhttp3.Route r1 = r1.route()
            goto L_0x000f
        L_0x000e:
            r1 = r0
        L_0x000f:
            int r2 = r9.code()
            okhttp3.Request r3 = r9.request()
            java.lang.String r3 = r3.method()
            switch(r2) {
                case 300: goto L_0x00c9;
                case 301: goto L_0x00c9;
                case 302: goto L_0x00c9;
                case 303: goto L_0x00c9;
                case 307: goto L_0x00c9;
                case 308: goto L_0x00c9;
                case 401: goto L_0x00be;
                case 407: goto L_0x009a;
                case 408: goto L_0x0064;
                case 421: goto L_0x003d;
                case 503: goto L_0x001f;
                default: goto L_0x001e;
            }
        L_0x001e:
            return r0
        L_0x001f:
            okhttp3.Response r4 = r9.priorResponse()
            if (r4 == 0) goto L_0x002e
            int r5 = r4.code()
            r6 = 503(0x1f7, float:7.05E-43)
            if (r5 != r6) goto L_0x002e
            return r0
        L_0x002e:
            r5 = 2147483647(0x7fffffff, float:NaN)
            int r5 = r8.retryAfter(r9, r5)
            if (r5 != 0) goto L_0x003c
            okhttp3.Request r0 = r9.request()
            return r0
        L_0x003c:
            return r0
        L_0x003d:
            okhttp3.Request r4 = r9.request()
            okhttp3.RequestBody r4 = r4.body()
            if (r4 == 0) goto L_0x004e
            boolean r5 = r4.isOneShot()
            if (r5 == 0) goto L_0x004e
            return r0
        L_0x004e:
            if (r10 == 0) goto L_0x0063
            boolean r5 = r10.isCoalescedConnection$okhttp()
            if (r5 != 0) goto L_0x0057
            goto L_0x0063
        L_0x0057:
            okhttp3.internal.connection.RealConnection r0 = r10.getConnection$okhttp()
            r0.noCoalescedConnections$okhttp()
            okhttp3.Request r0 = r9.request()
            return r0
        L_0x0063:
            return r0
        L_0x0064:
            okhttp3.OkHttpClient r4 = r8.client
            boolean r4 = r4.retryOnConnectionFailure()
            if (r4 != 0) goto L_0x006d
            return r0
        L_0x006d:
            okhttp3.Request r4 = r9.request()
            okhttp3.RequestBody r4 = r4.body()
            if (r4 == 0) goto L_0x007e
            boolean r5 = r4.isOneShot()
            if (r5 == 0) goto L_0x007e
            return r0
        L_0x007e:
            okhttp3.Response r5 = r9.priorResponse()
            if (r5 == 0) goto L_0x008d
            int r6 = r5.code()
            r7 = 408(0x198, float:5.72E-43)
            if (r6 != r7) goto L_0x008d
            return r0
        L_0x008d:
            r6 = 0
            int r6 = r8.retryAfter(r9, r6)
            if (r6 <= 0) goto L_0x0095
            return r0
        L_0x0095:
            okhttp3.Request r0 = r9.request()
            return r0
        L_0x009a:
            kotlin.jvm.internal.Intrinsics.checkNotNull(r1)
            java.net.Proxy r0 = r1.proxy()
            java.net.Proxy$Type r4 = r0.type()
            java.net.Proxy$Type r5 = java.net.Proxy.Type.HTTP
            if (r4 != r5) goto L_0x00b4
            okhttp3.OkHttpClient r4 = r8.client
            okhttp3.Authenticator r4 = r4.proxyAuthenticator()
            okhttp3.Request r4 = r4.authenticate(r1, r9)
            return r4
        L_0x00b4:
            java.net.ProtocolException r4 = new java.net.ProtocolException
            java.lang.String r5 = "Received HTTP_PROXY_AUTH (407) code while not using proxy"
            r4.<init>(r5)
            java.lang.Throwable r4 = (java.lang.Throwable) r4
            throw r4
        L_0x00be:
            okhttp3.OkHttpClient r0 = r8.client
            okhttp3.Authenticator r0 = r0.authenticator()
            okhttp3.Request r0 = r0.authenticate(r1, r9)
            return r0
        L_0x00c9:
            okhttp3.Request r0 = r8.buildRedirectRequest(r9, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http.RetryAndFollowUpInterceptor.followUpRequest(okhttp3.Response, okhttp3.internal.connection.Exchange):okhttp3.Request");
    }

    private final Request buildRedirectRequest(Response userResponse, String method) {
        String location;
        HttpUrl url;
        RequestBody requestBody = null;
        if (!this.client.followRedirects() || (location = Response.header$default(userResponse, "Location", (String) null, 2, (Object) null)) == null || (url = userResponse.request().url().resolve(location)) == null) {
            return null;
        }
        if (!Intrinsics.areEqual((Object) url.scheme(), (Object) userResponse.request().url().scheme()) && !this.client.followSslRedirects()) {
            return null;
        }
        Request.Builder requestBuilder = userResponse.request().newBuilder();
        if (HttpMethod.permitsRequestBody(method)) {
            int responseCode = userResponse.code();
            boolean maintainBody = HttpMethod.INSTANCE.redirectsWithBody(method) || responseCode == 308 || responseCode == 307;
            if (!HttpMethod.INSTANCE.redirectsToGet(method) || responseCode == 308 || responseCode == 307) {
                if (maintainBody) {
                    requestBody = userResponse.request().body();
                }
                requestBuilder.method(method, requestBody);
            } else {
                requestBuilder.method("GET", (RequestBody) null);
            }
            if (!maintainBody) {
                requestBuilder.removeHeader("Transfer-Encoding");
                requestBuilder.removeHeader("Content-Length");
                requestBuilder.removeHeader(HttpHeaderParser.HEADER_CONTENT_TYPE);
            }
        }
        if (!Util.canReuseConnectionFor(userResponse.request().url(), url)) {
            requestBuilder.removeHeader("Authorization");
        }
        return requestBuilder.url(url).build();
    }

    private final int retryAfter(Response userResponse, int defaultDelay) {
        String header = Response.header$default(userResponse, "Retry-After", (String) null, 2, (Object) null);
        if (header == null) {
            return defaultDelay;
        }
        if (!new Regex("\\d+").matches(header)) {
            return Integer.MAX_VALUE;
        }
        Integer valueOf = Integer.valueOf(header);
        Intrinsics.checkNotNullExpressionValue(valueOf, "Integer.valueOf(header)");
        return valueOf.intValue();
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lokhttp3/internal/http/RetryAndFollowUpInterceptor$Companion;", "", "()V", "MAX_FOLLOW_UPS", "", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: RetryAndFollowUpInterceptor.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
