package okhttp3.logging;

import com.android.volley.toolbox.HttpHeaderParser;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.StringsKt;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\u0018\u00002\u00020\u0001:\u0002\u001e\u001fB\u0011\b\u0007\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\r\u0010\u000b\u001a\u00020\tH\u0007¢\u0006\u0002\b\u0012J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u000e\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u0007J\u000e\u0010\u001d\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\tR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u000e¢\u0006\u0002\n\u0000R$\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\t@GX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\n\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006 "}, d2 = {"Lokhttp3/logging/HttpLoggingInterceptor;", "Lokhttp3/Interceptor;", "logger", "Lokhttp3/logging/HttpLoggingInterceptor$Logger;", "(Lokhttp3/logging/HttpLoggingInterceptor$Logger;)V", "headersToRedact", "", "", "<set-?>", "Lokhttp3/logging/HttpLoggingInterceptor$Level;", "level", "getLevel", "()Lokhttp3/logging/HttpLoggingInterceptor$Level;", "(Lokhttp3/logging/HttpLoggingInterceptor$Level;)V", "bodyHasUnknownEncoding", "", "headers", "Lokhttp3/Headers;", "-deprecated_level", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "logHeader", "", "i", "", "redactHeader", "name", "setLevel", "Level", "Logger", "okhttp-logging-interceptor"}, k = 1, mv = {1, 4, 0})
/* compiled from: HttpLoggingInterceptor.kt */
public final class HttpLoggingInterceptor implements Interceptor {
    private volatile Set<String> headersToRedact;
    private volatile Level level;
    private final Logger logger;

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\u0007"}, d2 = {"Lokhttp3/logging/HttpLoggingInterceptor$Level;", "", "(Ljava/lang/String;I)V", "NONE", "BASIC", "HEADERS", "BODY", "okhttp-logging-interceptor"}, k = 1, mv = {1, 4, 0})
    /* compiled from: HttpLoggingInterceptor.kt */
    public enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    public HttpLoggingInterceptor() {
        this((Logger) null, 1, (DefaultConstructorMarker) null);
    }

    public HttpLoggingInterceptor(Logger logger2) {
        Intrinsics.checkNotNullParameter(logger2, "logger");
        this.logger = logger2;
        this.headersToRedact = SetsKt.emptySet();
        this.level = Level.NONE;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ HttpLoggingInterceptor(Logger logger2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? Logger.DEFAULT : logger2);
    }

    public final Level getLevel() {
        return this.level;
    }

    public final void level(Level level2) {
        Intrinsics.checkNotNullParameter(level2, "<set-?>");
        this.level = level2;
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bæ\u0001\u0018\u0000 \u00062\u00020\u0001:\u0001\u0006J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0007"}, d2 = {"Lokhttp3/logging/HttpLoggingInterceptor$Logger;", "", "log", "", "message", "", "Companion", "okhttp-logging-interceptor"}, k = 1, mv = {1, 4, 0})
    /* compiled from: HttpLoggingInterceptor.kt */
    public interface Logger {
        public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
        public static final Logger DEFAULT = new Companion.DefaultLogger();

        void log(String str);

        @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\u0005B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\u00020\u00048\u0006X\u0004ø\u0001\u0000¢\u0006\u0002\n\u0000¨\u0006\u0001\u0002\u0007\n\u0005\bF0\u0001¨\u0006\u0006"}, d2 = {"Lokhttp3/logging/HttpLoggingInterceptor$Logger$Companion;", "", "()V", "DEFAULT", "Lokhttp3/logging/HttpLoggingInterceptor$Logger;", "DefaultLogger", "okhttp-logging-interceptor"}, k = 1, mv = {1, 4, 0})
        /* compiled from: HttpLoggingInterceptor.kt */
        public static final class Companion {
            static final /* synthetic */ Companion $$INSTANCE = null;

            private Companion() {
            }

            public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
                this();
            }

            @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007"}, d2 = {"Lokhttp3/logging/HttpLoggingInterceptor$Logger$Companion$DefaultLogger;", "Lokhttp3/logging/HttpLoggingInterceptor$Logger;", "()V", "log", "", "message", "", "okhttp-logging-interceptor"}, k = 1, mv = {1, 4, 0})
            /* compiled from: HttpLoggingInterceptor.kt */
            private static final class DefaultLogger implements Logger {
                public void log(String message) {
                    Intrinsics.checkNotNullParameter(message, "message");
                    Platform.log$default(Platform.Companion.get(), message, 0, (Throwable) null, 6, (Object) null);
                }
            }
        }
    }

    public final void redactHeader(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        TreeSet newHeadersToRedact = new TreeSet(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
        CollectionsKt.addAll(newHeadersToRedact, this.headersToRedact);
        newHeadersToRedact.add(name);
        this.headersToRedact = newHeadersToRedact;
    }

    public final HttpLoggingInterceptor setLevel(Level level2) {
        Intrinsics.checkNotNullParameter(level2, "level");
        this.level = level2;
        return this;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "moved to var", replaceWith = @ReplaceWith(expression = "level", imports = {}))
    /* renamed from: -deprecated_level  reason: not valid java name */
    public final Level m1869deprecated_level() {
        return this.level;
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        String str;
        String requestStartMessage;
        String str2;
        long contentLength;
        char c;
        String str3;
        String str4;
        Charset charset;
        Throwable th;
        Connection connection;
        Charset charset2;
        Interceptor.Chain chain2 = chain;
        Intrinsics.checkNotNullParameter(chain2, "chain");
        Level level2 = this.level;
        Request request = chain.request();
        if (level2 == Level.NONE) {
            return chain2.proceed(request);
        }
        boolean logBody = level2 == Level.BODY;
        boolean logHeaders = logBody || level2 == Level.HEADERS;
        RequestBody requestBody = request.body();
        Connection connection2 = chain.connection();
        String requestStartMessage2 = "--> " + request.method() + ' ' + request.url() + (connection2 != null ? " " + connection2.protocol() : "");
        if (logHeaders || requestBody == null) {
            str = "";
            requestStartMessage = requestStartMessage2;
        } else {
            str = "";
            requestStartMessage = requestStartMessage2 + " (" + requestBody.contentLength() + "-byte body)";
        }
        this.logger.log(requestStartMessage);
        if (logHeaders) {
            Headers headers = request.headers();
            if (requestBody != null) {
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    MediaType it = contentType;
                    if (headers.get(HttpHeaderParser.HEADER_CONTENT_TYPE) == null) {
                        Level level3 = level2;
                        this.logger.log("Content-Type: " + it);
                    } else {
                        MediaType mediaType = it;
                    }
                }
                if (requestBody.contentLength() == -1) {
                    connection = connection2;
                    String str5 = requestStartMessage;
                } else if (headers.get("Content-Length") == null) {
                    connection = connection2;
                    String str6 = requestStartMessage;
                    this.logger.log("Content-Length: " + requestBody.contentLength());
                } else {
                    connection = connection2;
                    String str7 = requestStartMessage;
                }
            } else {
                connection = connection2;
                String str8 = requestStartMessage;
            }
            int size = headers.size();
            for (int i = 0; i < size; i++) {
                logHeader(headers, i);
            }
            if (!logBody) {
                str2 = str;
                Headers headers2 = headers;
            } else if (requestBody == null) {
                Connection connection3 = connection;
                str2 = str;
                Headers headers3 = headers;
            } else if (bodyHasUnknownEncoding(request.headers())) {
                this.logger.log("--> END " + request.method() + " (encoded body omitted)");
                Connection connection4 = connection;
                str2 = str;
            } else if (requestBody.isDuplex()) {
                this.logger.log("--> END " + request.method() + " (duplex request body omitted)");
                Connection connection5 = connection;
                str2 = str;
            } else if (requestBody.isOneShot()) {
                this.logger.log("--> END " + request.method() + " (one-shot body omitted)");
                Connection connection6 = connection;
                str2 = str;
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                MediaType contentType2 = requestBody.contentType();
                if (contentType2 == null || (charset2 = contentType2.charset(StandardCharsets.UTF_8)) == null) {
                    charset2 = StandardCharsets.UTF_8;
                    Intrinsics.checkNotNullExpressionValue(charset2, "UTF_8");
                }
                Connection connection7 = connection;
                MediaType mediaType2 = contentType2;
                String str9 = str;
                this.logger.log(str9);
                if (Utf8Kt.isProbablyUtf8(buffer)) {
                    Headers headers4 = headers;
                    this.logger.log(buffer.readString(charset2));
                    str2 = str9;
                    Charset charset3 = charset2;
                    this.logger.log("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    str2 = str9;
                    Headers headers5 = headers;
                    Charset charset4 = charset2;
                    this.logger.log("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)");
                }
            }
            this.logger.log("--> END " + request.method());
        } else {
            Connection connection8 = connection2;
            String str10 = requestStartMessage;
            str2 = str;
        }
        long startNs = System.nanoTime();
        try {
            Response response = chain2.proceed(request);
            String str11 = "UTF_8";
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            ResponseBody responseBody = response.body();
            Intrinsics.checkNotNull(responseBody);
            Request request2 = request;
            long j = startNs;
            long contentLength2 = responseBody.contentLength();
            String bodySize = contentLength2 != -1 ? contentLength2 + "-byte" : "unknown-length";
            Logger logger2 = this.logger;
            RequestBody requestBody2 = requestBody;
            String str12 = str11;
            StringBuilder append = new StringBuilder().append("<-- ").append(response.code());
            if (response.message().length() == 0) {
                contentLength = contentLength2;
                str3 = "-byte body)";
                str4 = str2;
                c = ' ';
            } else {
                str3 = "-byte body)";
                contentLength = contentLength2;
                c = ' ';
                str4 = String.valueOf(' ') + response.message();
            }
            logger2.log(append.append(str4).append(c).append(response.request().url()).append(" (").append(tookMs).append("ms").append(!logHeaders ? ", " + bodySize + " body" : str2).append(')').toString());
            if (logHeaders) {
                Headers headers6 = response.headers();
                int size2 = headers6.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    logHeader(headers6, i2);
                }
                if (!logBody) {
                    Headers headers7 = headers6;
                } else if (!HttpHeaders.promisesBody(response)) {
                    String str13 = bodySize;
                    Headers headers8 = headers6;
                } else if (bodyHasUnknownEncoding(response.headers())) {
                    this.logger.log("<-- END HTTP (encoded body omitted)");
                    String str14 = bodySize;
                } else {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer2 = source.getBuffer();
                    Long gzippedLength = null;
                    if (StringsKt.equals("gzip", headers6.get("Content-Encoding"), true)) {
                        gzippedLength = Long.valueOf(buffer2.size());
                        Closeable gzipSource = new GzipSource(buffer2.clone());
                        Throwable th2 = null;
                        try {
                            GzipSource gzippedResponseBody = (GzipSource) gzipSource;
                            buffer2 = new Buffer();
                            String str15 = bodySize;
                            try {
                                buffer2.writeAll(gzippedResponseBody);
                                CloseableKt.closeFinally(gzipSource, (Throwable) null);
                            } catch (Throwable th3) {
                                th = th3;
                                try {
                                    throw th;
                                } catch (Throwable th4) {
                                    Throwable th5 = th4;
                                    CloseableKt.closeFinally(gzipSource, th);
                                    throw th5;
                                }
                            }
                        } catch (Throwable th6) {
                            String str16 = bodySize;
                            th = th6;
                            throw th;
                        }
                    }
                    MediaType contentType3 = responseBody.contentType();
                    if (contentType3 == null || (charset = contentType3.charset(StandardCharsets.UTF_8)) == null) {
                        charset = StandardCharsets.UTF_8;
                        Intrinsics.checkNotNullExpressionValue(charset, str12);
                    }
                    if (!Utf8Kt.isProbablyUtf8(buffer2)) {
                        this.logger.log(str2);
                        MediaType mediaType3 = contentType3;
                        Headers headers9 = headers6;
                        BufferedSource bufferedSource = source;
                        this.logger.log("<-- END HTTP (binary " + buffer2.size() + "-byte body omitted)");
                        return response;
                    }
                    Headers headers10 = headers6;
                    BufferedSource bufferedSource2 = source;
                    String str17 = str2;
                    if (contentLength != 0) {
                        this.logger.log(str17);
                        this.logger.log(buffer2.clone().readString(charset));
                    }
                    if (gzippedLength != null) {
                        this.logger.log("<-- END HTTP (" + buffer2.size() + "-byte, " + gzippedLength + "-gzipped-byte body)");
                    } else {
                        this.logger.log("<-- END HTTP (" + buffer2.size() + str3);
                    }
                }
                this.logger.log("<-- END HTTP");
            }
            return response;
        } catch (Exception e) {
            Request request3 = request;
            long j2 = startNs;
            RequestBody requestBody3 = requestBody;
            Exception e2 = e;
            this.logger.log("<-- HTTP FAILED: " + e2);
            throw e2;
        }
    }

    private final void logHeader(Headers headers, int i) {
        this.logger.log(headers.name(i) + ": " + (this.headersToRedact.contains(headers.name(i)) ? "██" : headers.value(i)));
    }

    private final boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        if (contentEncoding == null || StringsKt.equals(contentEncoding, "identity", true) || StringsKt.equals(contentEncoding, "gzip", true)) {
            return false;
        }
        return true;
    }
}
