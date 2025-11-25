package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.Util;
import okhttp3.internal.concurrent.Task;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.ws.WebSocketReader;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000¶\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u001c\u0018\u0000 `2\u00020\u00012\u00020\u0002:\u0005_`abcB?\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\u0006\u0010\u000f\u001a\u00020\f¢\u0006\u0002\u0010\u0010J\u0016\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\f2\u0006\u00105\u001a\u000206J\b\u00107\u001a\u000203H\u0016J\u001f\u00108\u001a\u0002032\u0006\u00109\u001a\u00020:2\b\u0010;\u001a\u0004\u0018\u00010<H\u0000¢\u0006\u0002\b=J\u001a\u0010>\u001a\u00020\u00122\u0006\u0010?\u001a\u00020%2\b\u0010@\u001a\u0004\u0018\u00010\u0018H\u0016J \u0010>\u001a\u00020\u00122\u0006\u0010?\u001a\u00020%2\b\u0010@\u001a\u0004\u0018\u00010\u00182\u0006\u0010A\u001a\u00020\fJ\u000e\u0010B\u001a\u0002032\u0006\u0010C\u001a\u00020DJ\u001c\u0010E\u001a\u0002032\n\u0010F\u001a\u00060Gj\u0002`H2\b\u00109\u001a\u0004\u0018\u00010:J\u0016\u0010I\u001a\u0002032\u0006\u0010\u001e\u001a\u00020\u00182\u0006\u0010*\u001a\u00020+J\u0006\u0010J\u001a\u000203J\u0018\u0010K\u001a\u0002032\u0006\u0010?\u001a\u00020%2\u0006\u0010@\u001a\u00020\u0018H\u0016J\u0010\u0010L\u001a\u0002032\u0006\u0010M\u001a\u00020\u0018H\u0016J\u0010\u0010L\u001a\u0002032\u0006\u0010N\u001a\u00020 H\u0016J\u0010\u0010O\u001a\u0002032\u0006\u0010P\u001a\u00020 H\u0016J\u0010\u0010Q\u001a\u0002032\u0006\u0010P\u001a\u00020 H\u0016J\u000e\u0010R\u001a\u00020\u00122\u0006\u0010P\u001a\u00020 J\u0006\u0010S\u001a\u00020\u0012J\b\u0010!\u001a\u00020\fH\u0016J\u0006\u0010'\u001a\u00020%J\u0006\u0010(\u001a\u00020%J\b\u0010T\u001a\u00020\u0006H\u0016J\b\u0010U\u001a\u000203H\u0002J\u0010\u0010V\u001a\u00020\u00122\u0006\u0010M\u001a\u00020\u0018H\u0016J\u0010\u0010V\u001a\u00020\u00122\u0006\u0010N\u001a\u00020 H\u0016J\u0018\u0010V\u001a\u00020\u00122\u0006\u0010W\u001a\u00020 2\u0006\u0010X\u001a\u00020%H\u0002J\u0006\u0010)\u001a\u00020%J\u0006\u0010Y\u001a\u000203J\r\u0010Z\u001a\u00020\u0012H\u0000¢\u0006\u0002\b[J\r\u0010\\\u001a\u000203H\u0000¢\u0006\u0002\b]J\f\u0010^\u001a\u00020\u0012*\u00020\u000eH\u0002R\u000e\u0010\u0011\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u0018X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u001cX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\fX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\"\u001a\u0004\u0018\u00010#X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020%X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u0004\u0018\u00010\u0018X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020%X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020%X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020%X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010*\u001a\u0004\u0018\u00010+X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020-X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010.\u001a\u0004\u0018\u00010/X\u000e¢\u0006\u0002\n\u0000R\u0010\u00100\u001a\u0004\u0018\u000101X\u000e¢\u0006\u0002\n\u0000¨\u0006d"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket;", "Lokhttp3/WebSocket;", "Lokhttp3/internal/ws/WebSocketReader$FrameCallback;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "originalRequest", "Lokhttp3/Request;", "listener", "Lokhttp3/WebSocketListener;", "random", "Ljava/util/Random;", "pingIntervalMillis", "", "extensions", "Lokhttp3/internal/ws/WebSocketExtensions;", "minimumDeflateSize", "(Lokhttp3/internal/concurrent/TaskRunner;Lokhttp3/Request;Lokhttp3/WebSocketListener;Ljava/util/Random;JLokhttp3/internal/ws/WebSocketExtensions;J)V", "awaitingPong", "", "call", "Lokhttp3/Call;", "enqueuedClose", "failed", "key", "", "getListener$okhttp", "()Lokhttp3/WebSocketListener;", "messageAndCloseQueue", "Ljava/util/ArrayDeque;", "", "name", "pongQueue", "Lokio/ByteString;", "queueSize", "reader", "Lokhttp3/internal/ws/WebSocketReader;", "receivedCloseCode", "", "receivedCloseReason", "receivedPingCount", "receivedPongCount", "sentPingCount", "streams", "Lokhttp3/internal/ws/RealWebSocket$Streams;", "taskQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "writer", "Lokhttp3/internal/ws/WebSocketWriter;", "writerTask", "Lokhttp3/internal/concurrent/Task;", "awaitTermination", "", "timeout", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "cancel", "checkUpgradeSuccess", "response", "Lokhttp3/Response;", "exchange", "Lokhttp3/internal/connection/Exchange;", "checkUpgradeSuccess$okhttp", "close", "code", "reason", "cancelAfterCloseMillis", "connect", "client", "Lokhttp3/OkHttpClient;", "failWebSocket", "e", "Ljava/lang/Exception;", "Lkotlin/Exception;", "initReaderAndWriter", "loopReader", "onReadClose", "onReadMessage", "text", "bytes", "onReadPing", "payload", "onReadPong", "pong", "processNextFrame", "request", "runWriter", "send", "data", "formatOpcode", "tearDown", "writeOneFrame", "writeOneFrame$okhttp", "writePingFrame", "writePingFrame$okhttp", "isValid", "Close", "Companion", "Message", "Streams", "WriterTask", "okhttp"}, k = 1, mv = {1, 4, 0})
/* compiled from: RealWebSocket.kt */
public final class RealWebSocket implements WebSocket, WebSocketReader.FrameCallback {
    private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000;
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final long DEFAULT_MINIMUM_DEFLATE_SIZE = 1024;
    private static final long MAX_QUEUE_SIZE = 16777216;
    private static final List<Protocol> ONLY_HTTP1 = CollectionsKt.listOf(Protocol.HTTP_1_1);
    private boolean awaitingPong;
    private Call call;
    private boolean enqueuedClose;
    /* access modifiers changed from: private */
    public WebSocketExtensions extensions;
    private boolean failed;
    private final String key;
    private final WebSocketListener listener;
    /* access modifiers changed from: private */
    public final ArrayDeque<Object> messageAndCloseQueue = new ArrayDeque<>();
    private long minimumDeflateSize;
    /* access modifiers changed from: private */
    public String name;
    private final Request originalRequest;
    private final long pingIntervalMillis;
    private final ArrayDeque<ByteString> pongQueue = new ArrayDeque<>();
    private long queueSize;
    private final Random random;
    private WebSocketReader reader;
    private int receivedCloseCode = -1;
    private String receivedCloseReason;
    private int receivedPingCount;
    private int receivedPongCount;
    private int sentPingCount;
    private Streams streams;
    private TaskQueue taskQueue;
    private WebSocketWriter writer;
    private Task writerTask;

    public RealWebSocket(TaskRunner taskRunner, Request originalRequest2, WebSocketListener listener2, Random random2, long pingIntervalMillis2, WebSocketExtensions extensions2, long minimumDeflateSize2) {
        Request request = originalRequest2;
        WebSocketListener webSocketListener = listener2;
        Random random3 = random2;
        Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
        Intrinsics.checkNotNullParameter(request, "originalRequest");
        Intrinsics.checkNotNullParameter(webSocketListener, "listener");
        Intrinsics.checkNotNullParameter(random3, "random");
        this.originalRequest = request;
        this.listener = webSocketListener;
        this.random = random3;
        this.pingIntervalMillis = pingIntervalMillis2;
        this.extensions = extensions2;
        this.minimumDeflateSize = minimumDeflateSize2;
        this.taskQueue = taskRunner.newQueue();
        if (Intrinsics.areEqual((Object) "GET", (Object) this.originalRequest.method())) {
            ByteString.Companion companion = ByteString.Companion;
            byte[] $this$apply = new byte[16];
            this.random.nextBytes($this$apply);
            Unit unit = Unit.INSTANCE;
            this.key = ByteString.Companion.of$default(companion, $this$apply, 0, 0, 3, (Object) null).base64();
            return;
        }
        throw new IllegalArgumentException(("Request must be GET: " + this.originalRequest.method()).toString());
    }

    public final WebSocketListener getListener$okhttp() {
        return this.listener;
    }

    public Request request() {
        return this.originalRequest;
    }

    public synchronized long queueSize() {
        return this.queueSize;
    }

    public void cancel() {
        Call call2 = this.call;
        Intrinsics.checkNotNull(call2);
        call2.cancel();
    }

    public final void connect(OkHttpClient client) {
        Intrinsics.checkNotNullParameter(client, "client");
        if (this.originalRequest.header("Sec-WebSocket-Extensions") != null) {
            failWebSocket(new ProtocolException("Request header not permitted: 'Sec-WebSocket-Extensions'"), (Response) null);
            return;
        }
        OkHttpClient webSocketClient = client.newBuilder().eventListener(EventListener.NONE).protocols(ONLY_HTTP1).build();
        Request request = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").header("Sec-WebSocket-Extensions", "permessage-deflate").build();
        this.call = new RealCall(webSocketClient, request, true);
        Call call2 = this.call;
        Intrinsics.checkNotNull(call2);
        call2.enqueue(new RealWebSocket$connect$1(this, request));
    }

    /* access modifiers changed from: private */
    public final boolean isValid(WebSocketExtensions $this$isValid) {
        if ($this$isValid.unknownValues || $this$isValid.clientMaxWindowBits != null) {
            return false;
        }
        if ($this$isValid.serverMaxWindowBits == null) {
            return true;
        }
        int intValue = $this$isValid.serverMaxWindowBits.intValue();
        if (8 > intValue || 15 < intValue) {
            return false;
        }
        return true;
    }

    public final void checkUpgradeSuccess$okhttp(Response response, Exchange exchange) throws IOException {
        Intrinsics.checkNotNullParameter(response, "response");
        if (response.code() == 101) {
            String headerConnection = Response.header$default(response, "Connection", (String) null, 2, (Object) null);
            if (StringsKt.equals("Upgrade", headerConnection, true)) {
                String headerUpgrade = Response.header$default(response, "Upgrade", (String) null, 2, (Object) null);
                if (StringsKt.equals("websocket", headerUpgrade, true)) {
                    String headerAccept = Response.header$default(response, "Sec-WebSocket-Accept", (String) null, 2, (Object) null);
                    String acceptExpected = ByteString.Companion.encodeUtf8(this.key + WebSocketProtocol.ACCEPT_MAGIC).sha1().base64();
                    if (true ^ Intrinsics.areEqual((Object) acceptExpected, (Object) headerAccept)) {
                        throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '" + acceptExpected + "' but was '" + headerAccept + '\'');
                    } else if (exchange == null) {
                        throw new ProtocolException("Web Socket exchange missing: bad interceptor?");
                    }
                } else {
                    throw new ProtocolException("Expected 'Upgrade' header value 'websocket' but was '" + headerUpgrade + '\'');
                }
            } else {
                throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '" + headerConnection + '\'');
            }
        } else {
            throw new ProtocolException("Expected HTTP 101 response but was '" + response.code() + ' ' + response.message() + '\'');
        }
    }

    public final void initReaderAndWriter(String name2, Streams streams2) throws IOException {
        String str = name2;
        Streams streams3 = streams2;
        Intrinsics.checkNotNullParameter(str, "name");
        Intrinsics.checkNotNullParameter(streams3, "streams");
        WebSocketExtensions webSocketExtensions = this.extensions;
        Intrinsics.checkNotNull(webSocketExtensions);
        WebSocketExtensions extensions2 = webSocketExtensions;
        synchronized (this) {
            this.name = str;
            this.streams = streams3;
            this.writer = new WebSocketWriter(streams2.getClient(), streams2.getSink(), this.random, extensions2.perMessageDeflate, extensions2.noContextTakeover(streams2.getClient()), this.minimumDeflateSize);
            this.writerTask = new WriterTask();
            if (this.pingIntervalMillis != 0) {
                long pingIntervalNanos = TimeUnit.MILLISECONDS.toNanos(this.pingIntervalMillis);
                String name$iv = str + " ping";
                this.taskQueue.schedule(new RealWebSocket$initReaderAndWriter$$inlined$synchronized$lambda$1(name$iv, name$iv, pingIntervalNanos, this, name2, streams2, extensions2), pingIntervalNanos);
            }
            if (!this.messageAndCloseQueue.isEmpty()) {
                runWriter();
            }
            Unit unit = Unit.INSTANCE;
        }
        this.reader = new WebSocketReader(streams2.getClient(), streams2.getSource(), this, extensions2.perMessageDeflate, extensions2.noContextTakeover(!streams2.getClient()));
    }

    public final void loopReader() throws IOException {
        while (this.receivedCloseCode == -1) {
            WebSocketReader webSocketReader = this.reader;
            Intrinsics.checkNotNull(webSocketReader);
            webSocketReader.processNextFrame();
        }
    }

    public final boolean processNextFrame() throws IOException {
        try {
            WebSocketReader webSocketReader = this.reader;
            Intrinsics.checkNotNull(webSocketReader);
            webSocketReader.processNextFrame();
            if (this.receivedCloseCode == -1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            failWebSocket(e, (Response) null);
            return false;
        }
    }

    public final void awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
        Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
        this.taskQueue.idleLatch().await(timeout, timeUnit);
    }

    public final void tearDown() throws InterruptedException {
        this.taskQueue.shutdown();
        this.taskQueue.idleLatch().await(10, TimeUnit.SECONDS);
    }

    public final synchronized int sentPingCount() {
        return this.sentPingCount;
    }

    public final synchronized int receivedPingCount() {
        return this.receivedPingCount;
    }

    public final synchronized int receivedPongCount() {
        return this.receivedPongCount;
    }

    public void onReadMessage(String text) throws IOException {
        Intrinsics.checkNotNullParameter(text, "text");
        this.listener.onMessage((WebSocket) this, text);
    }

    public void onReadMessage(ByteString bytes) throws IOException {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        this.listener.onMessage((WebSocket) this, bytes);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0029, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onReadPing(okio.ByteString r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            java.lang.String r0 = "payload"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)     // Catch:{ all -> 0x002a }
            boolean r0 = r1.failed     // Catch:{ all -> 0x002a }
            if (r0 != 0) goto L_0x0028
            boolean r0 = r1.enqueuedClose     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0018
            java.util.ArrayDeque<java.lang.Object> r0 = r1.messageAndCloseQueue     // Catch:{ all -> 0x002a }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x002a }
            if (r0 == 0) goto L_0x0018
            goto L_0x0028
        L_0x0018:
            java.util.ArrayDeque<okio.ByteString> r0 = r1.pongQueue     // Catch:{ all -> 0x002a }
            r0.add(r2)     // Catch:{ all -> 0x002a }
            r1.runWriter()     // Catch:{ all -> 0x002a }
            int r0 = r1.receivedPingCount     // Catch:{ all -> 0x002a }
            int r0 = r0 + 1
            r1.receivedPingCount = r0     // Catch:{ all -> 0x002a }
            monitor-exit(r1)
            return
        L_0x0028:
            monitor-exit(r1)
            return
        L_0x002a:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.onReadPing(okio.ByteString):void");
    }

    public synchronized void onReadPong(ByteString payload) {
        Intrinsics.checkNotNullParameter(payload, "payload");
        this.receivedPongCount++;
        this.awaitingPong = false;
    }

    public void onReadClose(int code, String reason) {
        Intrinsics.checkNotNullParameter(reason, "reason");
        boolean z = true;
        if (code != -1) {
            Object toClose = (Streams) null;
            Object readerToClose = (WebSocketReader) null;
            Object writerToClose = (WebSocketWriter) null;
            synchronized (this) {
                if (this.receivedCloseCode != -1) {
                    z = false;
                }
                if (z) {
                    this.receivedCloseCode = code;
                    this.receivedCloseReason = reason;
                    if (this.enqueuedClose && this.messageAndCloseQueue.isEmpty()) {
                        toClose = this.streams;
                        Streams streams2 = null;
                        this.streams = null;
                        readerToClose = this.reader;
                        WebSocketReader webSocketReader = null;
                        this.reader = null;
                        writerToClose = this.writer;
                        WebSocketWriter webSocketWriter = null;
                        this.writer = null;
                        this.taskQueue.shutdown();
                    }
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalStateException("already closed".toString());
                }
            }
            try {
                this.listener.onClosing(this, code, reason);
                if (toClose != null) {
                    this.listener.onClosed(this, code, reason);
                }
            } finally {
                if (toClose != null) {
                    Util.closeQuietly((Closeable) toClose);
                }
                if (readerToClose != null) {
                    Util.closeQuietly((Closeable) readerToClose);
                }
                if (writerToClose != null) {
                    Util.closeQuietly((Closeable) writerToClose);
                }
            }
        } else {
            throw new IllegalArgumentException("Failed requirement.".toString());
        }
    }

    public boolean send(String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        return send(ByteString.Companion.encodeUtf8(text), 1);
    }

    public boolean send(ByteString bytes) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        return send(bytes, 2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003d, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final synchronized boolean send(okio.ByteString r7, int r8) {
        /*
            r6 = this;
            monitor-enter(r6)
            boolean r0 = r6.failed     // Catch:{ all -> 0x003e }
            r1 = 0
            if (r0 != 0) goto L_0x003c
            boolean r0 = r6.enqueuedClose     // Catch:{ all -> 0x003e }
            if (r0 == 0) goto L_0x000b
            goto L_0x003c
        L_0x000b:
            long r2 = r6.queueSize     // Catch:{ all -> 0x003e }
            int r0 = r7.size()     // Catch:{ all -> 0x003e }
            long r4 = (long) r0     // Catch:{ all -> 0x003e }
            long r2 = r2 + r4
            r4 = 16777216(0x1000000, double:8.289046E-317)
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0022
            r0 = 1001(0x3e9, float:1.403E-42)
            r2 = 0
            r6.close(r0, r2)     // Catch:{ all -> 0x003e }
            monitor-exit(r6)
            return r1
        L_0x0022:
            long r0 = r6.queueSize     // Catch:{ all -> 0x003e }
            int r2 = r7.size()     // Catch:{ all -> 0x003e }
            long r2 = (long) r2     // Catch:{ all -> 0x003e }
            long r0 = r0 + r2
            r6.queueSize = r0     // Catch:{ all -> 0x003e }
            java.util.ArrayDeque<java.lang.Object> r0 = r6.messageAndCloseQueue     // Catch:{ all -> 0x003e }
            okhttp3.internal.ws.RealWebSocket$Message r1 = new okhttp3.internal.ws.RealWebSocket$Message     // Catch:{ all -> 0x003e }
            r1.<init>(r8, r7)     // Catch:{ all -> 0x003e }
            r0.add(r1)     // Catch:{ all -> 0x003e }
            r6.runWriter()     // Catch:{ all -> 0x003e }
            monitor-exit(r6)
            r0 = 1
            return r0
        L_0x003c:
            monitor-exit(r6)
            return r1
        L_0x003e:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.send(okio.ByteString, int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized boolean pong(okio.ByteString r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            java.lang.String r0 = "payload"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)     // Catch:{ all -> 0x0026 }
            boolean r0 = r1.failed     // Catch:{ all -> 0x0026 }
            if (r0 != 0) goto L_0x0023
            boolean r0 = r1.enqueuedClose     // Catch:{ all -> 0x0026 }
            if (r0 == 0) goto L_0x0018
            java.util.ArrayDeque<java.lang.Object> r0 = r1.messageAndCloseQueue     // Catch:{ all -> 0x0026 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0026 }
            if (r0 == 0) goto L_0x0018
            goto L_0x0023
        L_0x0018:
            java.util.ArrayDeque<okio.ByteString> r0 = r1.pongQueue     // Catch:{ all -> 0x0026 }
            r0.add(r2)     // Catch:{ all -> 0x0026 }
            r1.runWriter()     // Catch:{ all -> 0x0026 }
            monitor-exit(r1)
            r0 = 1
            return r0
        L_0x0023:
            monitor-exit(r1)
            r0 = 0
            return r0
        L_0x0026:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.pong(okio.ByteString):boolean");
    }

    public boolean close(int code, String reason) {
        return close(code, reason, CANCEL_AFTER_CLOSE_MILLIS);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0062, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized boolean close(int r8, java.lang.String r9, long r10) {
        /*
            r7 = this;
            monitor-enter(r7)
            okhttp3.internal.ws.WebSocketProtocol r0 = okhttp3.internal.ws.WebSocketProtocol.INSTANCE     // Catch:{ all -> 0x0063 }
            r0.validateCloseCode(r8)     // Catch:{ all -> 0x0063 }
            r0 = 0
            r1 = r0
            okio.ByteString r1 = (okio.ByteString) r1     // Catch:{ all -> 0x0063 }
            r1 = 0
            r2 = 1
            if (r9 == 0) goto L_0x0047
            okio.ByteString$Companion r3 = okio.ByteString.Companion     // Catch:{ all -> 0x0063 }
            okio.ByteString r3 = r3.encodeUtf8(r9)     // Catch:{ all -> 0x0063 }
            r0 = r3
            int r3 = r0.size()     // Catch:{ all -> 0x0063 }
            long r3 = (long) r3     // Catch:{ all -> 0x0063 }
            r5 = 123(0x7b, double:6.1E-322)
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 > 0) goto L_0x0022
            r3 = r2
            goto L_0x0023
        L_0x0022:
            r3 = r1
        L_0x0023:
            if (r3 == 0) goto L_0x0026
            goto L_0x0047
        L_0x0026:
            r1 = 0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0063 }
            r2.<init>()     // Catch:{ all -> 0x0063 }
            java.lang.String r3 = "reason.size() > 123: "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x0063 }
            java.lang.StringBuilder r2 = r2.append(r9)     // Catch:{ all -> 0x0063 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0063 }
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0063 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0063 }
            r1.<init>(r2)     // Catch:{ all -> 0x0063 }
            java.lang.Throwable r1 = (java.lang.Throwable) r1     // Catch:{ all -> 0x0063 }
            throw r1     // Catch:{ all -> 0x0063 }
        L_0x0047:
            boolean r3 = r7.failed     // Catch:{ all -> 0x0063 }
            if (r3 != 0) goto L_0x0061
            boolean r3 = r7.enqueuedClose     // Catch:{ all -> 0x0063 }
            if (r3 == 0) goto L_0x0050
            goto L_0x0061
        L_0x0050:
            r7.enqueuedClose = r2     // Catch:{ all -> 0x0063 }
            java.util.ArrayDeque<java.lang.Object> r1 = r7.messageAndCloseQueue     // Catch:{ all -> 0x0063 }
            okhttp3.internal.ws.RealWebSocket$Close r3 = new okhttp3.internal.ws.RealWebSocket$Close     // Catch:{ all -> 0x0063 }
            r3.<init>(r8, r0, r10)     // Catch:{ all -> 0x0063 }
            r1.add(r3)     // Catch:{ all -> 0x0063 }
            r7.runWriter()     // Catch:{ all -> 0x0063 }
            monitor-exit(r7)
            return r2
        L_0x0061:
            monitor-exit(r7)
            return r1
        L_0x0063:
            r8 = move-exception
            monitor-exit(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.close(int, java.lang.String, long):boolean");
    }

    private final void runWriter() {
        if (!Util.assertionsEnabled || Thread.holdsLock(this)) {
            Task writerTask2 = this.writerTask;
            if (writerTask2 != null) {
                TaskQueue.schedule$default(this.taskQueue, writerTask2, 0, 2, (Object) null);
                return;
            }
            return;
        }
        StringBuilder append = new StringBuilder().append("Thread ");
        Thread currentThread = Thread.currentThread();
        Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
        throw new AssertionError(append.append(currentThread.getName()).append(" MUST hold lock on ").append(this).toString());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:?, code lost:
        r8 = r30.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:?, code lost:
        r10 = (java.lang.String) r29.element;
        kotlin.jvm.internal.Intrinsics.checkNotNull(r10);
        r2.onClosed(r6, r8, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x027b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x027c, code lost:
        r8 = r26;
        r6 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0282, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x0283, code lost:
        r9 = r29;
        r8 = r26;
        r6 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x028b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x028c, code lost:
        r9 = r29;
        r7 = r30;
        r8 = r26;
        r6 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x0296, code lost:
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x029a, code lost:
        r0 = (okhttp3.internal.ws.RealWebSocket.Streams) r5.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x02a0, code lost:
        if (r0 == null) goto L_0x02a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x02a2, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x02a7, code lost:
        r0 = (okhttp3.internal.ws.WebSocketReader) r27.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x02ad, code lost:
        if (r0 == null) goto L_0x02b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x02af, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x02b4, code lost:
        r0 = (okhttp3.internal.ws.WebSocketWriter) r26.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x02ba, code lost:
        if (r0 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x02bc, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x02c3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x02c4, code lost:
        r8 = r26;
        r6 = r27;
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x02cd, code lost:
        r8 = r26;
        r6 = r27;
        r5 = r28;
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x02de, code lost:
        throw new java.lang.NullPointerException("null cannot be cast to non-null type okhttp3.internal.ws.RealWebSocket.Close");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x02df, code lost:
        r8 = r26;
        r6 = r27;
        r5 = r28;
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x02f0, code lost:
        throw new java.lang.AssertionError();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x02f1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x02f3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x02f4, code lost:
        r8 = r26;
        r6 = r27;
        r5 = r28;
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x02fe, code lost:
        r2 = (okhttp3.internal.ws.RealWebSocket.Streams) r5.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0302, code lost:
        if (r2 != null) goto L_0x0304;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0304, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0309, code lost:
        r2 = (okhttp3.internal.ws.WebSocketReader) r6.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x030d, code lost:
        if (r2 != null) goto L_0x030f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x030f, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0314, code lost:
        r2 = (okhttp3.internal.ws.WebSocketWriter) r8.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0318, code lost:
        if (r2 != null) goto L_0x031a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x031a, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x031f, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x01d0, code lost:
        r1 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x01d3, code lost:
        if (r1 == null) goto L_0x0201;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        kotlin.jvm.internal.Intrinsics.checkNotNull(r25);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        r25.writePong(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01dd, code lost:
        r5 = r28;
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x01e5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x01e6, code lost:
        r8 = r26;
        r6 = r27;
        r5 = r28;
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x01f2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x01f3, code lost:
        r4 = r25;
        r8 = r26;
        r6 = r27;
        r5 = r28;
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0201, code lost:
        r4 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0207, code lost:
        if ((r3.element instanceof okhttp3.internal.ws.RealWebSocket.Message) == false) goto L_0x0243;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:?, code lost:
        r0 = r3.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x020b, code lost:
        if (r0 == null) goto L_0x023b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x020d, code lost:
        r2 = (okhttp3.internal.ws.RealWebSocket.Message) r0;
        kotlin.jvm.internal.Intrinsics.checkNotNull(r4);
        r4.writeMessageFrame(r2.getFormatOpcode(), r2.getData());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x021e, code lost:
        monitor-enter(r32);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:?, code lost:
        r15.queueSize -= (long) r2.getData().size();
        r0 = kotlin.Unit.INSTANCE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
        monitor-exit(r32);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0231, code lost:
        r5 = r28;
        r9 = r29;
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0242, code lost:
        throw new java.lang.NullPointerException("null cannot be cast to non-null type okhttp3.internal.ws.RealWebSocket.Message");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0247, code lost:
        if ((r3.element instanceof okhttp3.internal.ws.RealWebSocket.Close) == false) goto L_0x02df;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0249, code lost:
        r0 = r3.element;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x024b, code lost:
        if (r0 == null) goto L_0x02cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x024d, code lost:
        r0 = (okhttp3.internal.ws.RealWebSocket.Close) r0;
        kotlin.jvm.internal.Intrinsics.checkNotNull(r4);
        r4.writeClose(r0.getCode(), r0.getReason());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x025d, code lost:
        r5 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0263, code lost:
        if (((okhttp3.internal.ws.RealWebSocket.Streams) r5.element) == null) goto L_0x0296;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:?, code lost:
        r2 = r15.listener;
        r6 = r15;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0304  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x031a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean writeOneFrame$okhttp() throws java.io.IOException {
        /*
            r32 = this;
            r15 = r32
            r1 = 0
            r2 = 0
            kotlin.jvm.internal.Ref$ObjectRef r0 = new kotlin.jvm.internal.Ref$ObjectRef
            r0.<init>()
            r3 = 0
            r0.element = r3
            r14 = r0
            kotlin.jvm.internal.Ref$IntRef r0 = new kotlin.jvm.internal.Ref$IntRef
            r0.<init>()
            r4 = -1
            r0.element = r4
            r13 = r0
            kotlin.jvm.internal.Ref$ObjectRef r0 = new kotlin.jvm.internal.Ref$ObjectRef
            r0.<init>()
            r5 = r3
            java.lang.String r5 = (java.lang.String) r5
            r0.element = r3
            r12 = r0
            kotlin.jvm.internal.Ref$ObjectRef r0 = new kotlin.jvm.internal.Ref$ObjectRef
            r0.<init>()
            r5 = r3
            okhttp3.internal.ws.RealWebSocket$Streams r5 = (okhttp3.internal.ws.RealWebSocket.Streams) r5
            r0.element = r3
            r11 = r0
            kotlin.jvm.internal.Ref$ObjectRef r0 = new kotlin.jvm.internal.Ref$ObjectRef
            r0.<init>()
            r5 = r3
            okhttp3.internal.ws.WebSocketReader r5 = (okhttp3.internal.ws.WebSocketReader) r5
            r0.element = r3
            r10 = r0
            kotlin.jvm.internal.Ref$ObjectRef r0 = new kotlin.jvm.internal.Ref$ObjectRef
            r0.<init>()
            r5 = r3
            okhttp3.internal.ws.WebSocketWriter r5 = (okhttp3.internal.ws.WebSocketWriter) r5
            r0.element = r3
            r9 = r0
            monitor-enter(r32)
            r0 = 0
            boolean r5 = r15.failed     // Catch:{ all -> 0x033c }
            r6 = 0
            if (r5 == 0) goto L_0x004c
            monitor-exit(r32)
            return r6
        L_0x004c:
            okhttp3.internal.ws.WebSocketWriter r5 = r15.writer     // Catch:{ all -> 0x033c }
            r8 = r5
            java.util.ArrayDeque<okio.ByteString> r1 = r15.pongQueue     // Catch:{ all -> 0x0332 }
            java.lang.Object r1 = r1.poll()     // Catch:{ all -> 0x0332 }
            okio.ByteString r1 = (okio.ByteString) r1     // Catch:{ all -> 0x0332 }
            r7 = r1
            if (r7 != 0) goto L_0x01ba
            java.util.ArrayDeque<java.lang.Object> r1 = r15.messageAndCloseQueue     // Catch:{ all -> 0x019a }
            java.lang.Object r1 = r1.poll()     // Catch:{ all -> 0x019a }
            r14.element = r1     // Catch:{ all -> 0x019a }
            T r1 = r14.element     // Catch:{ all -> 0x019a }
            boolean r1 = r1 instanceof okhttp3.internal.ws.RealWebSocket.Close     // Catch:{ all -> 0x019a }
            if (r1 == 0) goto L_0x016e
            int r1 = r15.receivedCloseCode     // Catch:{ all -> 0x014b }
            r13.element = r1     // Catch:{ all -> 0x014b }
            java.lang.String r1 = r15.receivedCloseReason     // Catch:{ all -> 0x014b }
            r12.element = r1     // Catch:{ all -> 0x014b }
            int r1 = r13.element     // Catch:{ all -> 0x014b }
            if (r1 == r4) goto L_0x00b2
            okhttp3.internal.ws.RealWebSocket$Streams r1 = r15.streams     // Catch:{ all -> 0x00a7 }
            r11.element = r1     // Catch:{ all -> 0x00a7 }
            r1 = r3
            okhttp3.internal.ws.RealWebSocket$Streams r1 = (okhttp3.internal.ws.RealWebSocket.Streams) r1     // Catch:{ all -> 0x00a7 }
            r15.streams = r3     // Catch:{ all -> 0x00a7 }
            okhttp3.internal.ws.WebSocketReader r1 = r15.reader     // Catch:{ all -> 0x00a7 }
            r10.element = r1     // Catch:{ all -> 0x00a7 }
            r1 = r3
            okhttp3.internal.ws.WebSocketReader r1 = (okhttp3.internal.ws.WebSocketReader) r1     // Catch:{ all -> 0x00a7 }
            r15.reader = r3     // Catch:{ all -> 0x00a7 }
            okhttp3.internal.ws.WebSocketWriter r1 = r15.writer     // Catch:{ all -> 0x00a7 }
            r9.element = r1     // Catch:{ all -> 0x00a7 }
            r1 = r3
            okhttp3.internal.ws.WebSocketWriter r1 = (okhttp3.internal.ws.WebSocketWriter) r1     // Catch:{ all -> 0x00a7 }
            r15.writer = r3     // Catch:{ all -> 0x00a7 }
            okhttp3.internal.concurrent.TaskQueue r1 = r15.taskQueue     // Catch:{ all -> 0x00a7 }
            r1.shutdown()     // Catch:{ all -> 0x00a7 }
            r19 = r0
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r3 = r14
            goto L_0x01cb
        L_0x00a7:
            r0 = move-exception
            r2 = r7
            r1 = r8
            r8 = r9
            r6 = r10
            r5 = r11
            r9 = r12
            r7 = r13
            r3 = r14
            goto L_0x0343
        L_0x00b2:
            T r1 = r14.element     // Catch:{ all -> 0x014b }
            if (r1 == 0) goto L_0x011e
            okhttp3.internal.ws.RealWebSocket$Close r1 = (okhttp3.internal.ws.RealWebSocket.Close) r1     // Catch:{ all -> 0x014b }
            long r1 = r1.getCancelAfterCloseMillis()     // Catch:{ all -> 0x014b }
            r5 = r1
            okhttp3.internal.concurrent.TaskQueue r1 = r15.taskQueue     // Catch:{ all -> 0x014b }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x014b }
            r2.<init>()     // Catch:{ all -> 0x014b }
            java.lang.String r3 = r15.name     // Catch:{ all -> 0x014b }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x014b }
            java.lang.String r3 = " cancel"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x014b }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x014b }
            java.util.concurrent.TimeUnit r3 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ all -> 0x014b }
            long r3 = r3.toNanos(r5)     // Catch:{ all -> 0x014b }
            r16 = 1
            r17 = 0
            okhttp3.internal.ws.RealWebSocket$writeOneFrame$$inlined$synchronized$lambda$1 r18 = new okhttp3.internal.ws.RealWebSocket$writeOneFrame$$inlined$synchronized$lambda$1     // Catch:{ all -> 0x014b }
            r19 = r0
            r0 = r1
            r1 = r18
            r20 = r3
            r3 = r16
            r4 = r2
            r22 = r5
            r5 = r16
            r6 = r32
            r24 = r7
            r7 = r8
            r25 = r8
            r8 = r24
            r26 = r9
            r9 = r14
            r27 = r10
            r10 = r13
            r28 = r11
            r11 = r12
            r29 = r12
            r12 = r28
            r30 = r13
            r13 = r27
            r31 = r14
            r14 = r26
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ all -> 0x0138 }
            r1 = r18
            okhttp3.internal.concurrent.Task r1 = (okhttp3.internal.concurrent.Task) r1     // Catch:{ all -> 0x0138 }
            r3 = r20
            r0.schedule(r1, r3)     // Catch:{ all -> 0x0138 }
            r3 = r31
            goto L_0x01cb
        L_0x011e:
            r19 = r0
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r31 = r14
            java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x0138 }
            java.lang.String r1 = "null cannot be cast to non-null type okhttp3.internal.ws.RealWebSocket.Close"
            r0.<init>(r1)     // Catch:{ all -> 0x0138 }
            throw r0     // Catch:{ all -> 0x0138 }
        L_0x0138:
            r0 = move-exception
            r2 = r24
            r1 = r25
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            r3 = r31
            goto L_0x0343
        L_0x014b:
            r0 = move-exception
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r31 = r14
            r2 = r24
            r1 = r25
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            r3 = r31
            goto L_0x0343
        L_0x016e:
            r19 = r0
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r31 = r14
            r3 = r31
            T r0 = r3.element     // Catch:{ all -> 0x0189 }
            if (r0 != 0) goto L_0x01cb
            monitor-exit(r32)
            return r6
        L_0x0189:
            r0 = move-exception
            r2 = r24
            r1 = r25
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            goto L_0x0343
        L_0x019a:
            r0 = move-exception
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r3 = r14
            r2 = r24
            r1 = r25
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            goto L_0x0343
        L_0x01ba:
            r19 = r0
            r24 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            r28 = r11
            r29 = r12
            r30 = r13
            r3 = r14
        L_0x01cb:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0320 }
            monitor-exit(r32)
            r1 = r24
            if (r1 == 0) goto L_0x0201
            kotlin.jvm.internal.Intrinsics.checkNotNull(r25)     // Catch:{ all -> 0x01f2 }
            r4 = r25
            r4.writePong(r1)     // Catch:{ all -> 0x01e5 }
            r5 = r28
            r9 = r29
            r7 = r30
            goto L_0x029a
        L_0x01e5:
            r0 = move-exception
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            goto L_0x02fe
        L_0x01f2:
            r0 = move-exception
            r4 = r25
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            goto L_0x02fe
        L_0x0201:
            r4 = r25
            T r0 = r3.element     // Catch:{ all -> 0x02f3 }
            boolean r0 = r0 instanceof okhttp3.internal.ws.RealWebSocket.Message     // Catch:{ all -> 0x02f3 }
            if (r0 == 0) goto L_0x0243
            T r0 = r3.element     // Catch:{ all -> 0x01e5 }
            if (r0 == 0) goto L_0x023b
            okhttp3.internal.ws.RealWebSocket$Message r0 = (okhttp3.internal.ws.RealWebSocket.Message) r0     // Catch:{ all -> 0x01e5 }
            r2 = r0
            kotlin.jvm.internal.Intrinsics.checkNotNull(r4)     // Catch:{ all -> 0x01e5 }
            int r0 = r2.getFormatOpcode()     // Catch:{ all -> 0x01e5 }
            okio.ByteString r5 = r2.getData()     // Catch:{ all -> 0x01e5 }
            r4.writeMessageFrame(r0, r5)     // Catch:{ all -> 0x01e5 }
            monitor-enter(r32)     // Catch:{ all -> 0x01e5 }
            r0 = 0
            long r5 = r15.queueSize     // Catch:{ all -> 0x0238 }
            okio.ByteString r7 = r2.getData()     // Catch:{ all -> 0x0238 }
            int r7 = r7.size()     // Catch:{ all -> 0x0238 }
            long r7 = (long) r7     // Catch:{ all -> 0x0238 }
            long r5 = r5 - r7
            r15.queueSize = r5     // Catch:{ all -> 0x0238 }
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0238 }
            monitor-exit(r32)     // Catch:{ all -> 0x01e5 }
            r5 = r28
            r9 = r29
            r7 = r30
            goto L_0x029a
        L_0x0238:
            r0 = move-exception
            monitor-exit(r32)     // Catch:{ all -> 0x01e5 }
            throw r0     // Catch:{ all -> 0x01e5 }
        L_0x023b:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x01e5 }
            java.lang.String r2 = "null cannot be cast to non-null type okhttp3.internal.ws.RealWebSocket.Message"
            r0.<init>(r2)     // Catch:{ all -> 0x01e5 }
            throw r0     // Catch:{ all -> 0x01e5 }
        L_0x0243:
            T r0 = r3.element     // Catch:{ all -> 0x02f3 }
            boolean r0 = r0 instanceof okhttp3.internal.ws.RealWebSocket.Close     // Catch:{ all -> 0x02f3 }
            if (r0 == 0) goto L_0x02df
            T r0 = r3.element     // Catch:{ all -> 0x02f3 }
            if (r0 == 0) goto L_0x02cd
            okhttp3.internal.ws.RealWebSocket$Close r0 = (okhttp3.internal.ws.RealWebSocket.Close) r0     // Catch:{ all -> 0x02f3 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r4)     // Catch:{ all -> 0x02f3 }
            int r2 = r0.getCode()     // Catch:{ all -> 0x02f3 }
            okio.ByteString r5 = r0.getReason()     // Catch:{ all -> 0x02f3 }
            r4.writeClose(r2, r5)     // Catch:{ all -> 0x02f3 }
            r5 = r28
            T r2 = r5.element     // Catch:{ all -> 0x02c3 }
            okhttp3.internal.ws.RealWebSocket$Streams r2 = (okhttp3.internal.ws.RealWebSocket.Streams) r2     // Catch:{ all -> 0x02c3 }
            if (r2 == 0) goto L_0x0296
            okhttp3.WebSocketListener r2 = r15.listener     // Catch:{ all -> 0x028b }
            r6 = r15
            okhttp3.WebSocket r6 = (okhttp3.WebSocket) r6     // Catch:{ all -> 0x028b }
            r7 = r30
            int r8 = r7.element     // Catch:{ all -> 0x0282 }
            r9 = r29
            T r10 = r9.element     // Catch:{ all -> 0x027b }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ all -> 0x027b }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r10)     // Catch:{ all -> 0x027b }
            r2.onClosed(r6, r8, r10)     // Catch:{ all -> 0x027b }
            goto L_0x029a
        L_0x027b:
            r0 = move-exception
            r8 = r26
            r6 = r27
            goto L_0x02fe
        L_0x0282:
            r0 = move-exception
            r9 = r29
            r8 = r26
            r6 = r27
            goto L_0x02fe
        L_0x028b:
            r0 = move-exception
            r9 = r29
            r7 = r30
            r8 = r26
            r6 = r27
            goto L_0x02fe
        L_0x0296:
            r9 = r29
            r7 = r30
        L_0x029a:
            T r0 = r5.element
            okhttp3.internal.ws.RealWebSocket$Streams r0 = (okhttp3.internal.ws.RealWebSocket.Streams) r0
            if (r0 == 0) goto L_0x02a7
            java.io.Closeable r0 = (java.io.Closeable) r0
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r0)
        L_0x02a7:
            r6 = r27
            T r0 = r6.element
            okhttp3.internal.ws.WebSocketReader r0 = (okhttp3.internal.ws.WebSocketReader) r0
            if (r0 == 0) goto L_0x02b4
            java.io.Closeable r0 = (java.io.Closeable) r0
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r0)
        L_0x02b4:
            r8 = r26
            T r0 = r8.element
            okhttp3.internal.ws.WebSocketWriter r0 = (okhttp3.internal.ws.WebSocketWriter) r0
            if (r0 == 0) goto L_0x02c1
            java.io.Closeable r0 = (java.io.Closeable) r0
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r0)
        L_0x02c1:
            r0 = 1
            return r0
        L_0x02c3:
            r0 = move-exception
            r8 = r26
            r6 = r27
            r9 = r29
            r7 = r30
            goto L_0x02fe
        L_0x02cd:
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x02f1 }
            java.lang.String r2 = "null cannot be cast to non-null type okhttp3.internal.ws.RealWebSocket.Close"
            r0.<init>(r2)     // Catch:{ all -> 0x02f1 }
            throw r0     // Catch:{ all -> 0x02f1 }
        L_0x02df:
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            java.lang.AssertionError r0 = new java.lang.AssertionError     // Catch:{ all -> 0x02f1 }
            r0.<init>()     // Catch:{ all -> 0x02f1 }
            java.lang.Throwable r0 = (java.lang.Throwable) r0     // Catch:{ all -> 0x02f1 }
            throw r0     // Catch:{ all -> 0x02f1 }
        L_0x02f1:
            r0 = move-exception
            goto L_0x02fe
        L_0x02f3:
            r0 = move-exception
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
        L_0x02fe:
            T r2 = r5.element
            okhttp3.internal.ws.RealWebSocket$Streams r2 = (okhttp3.internal.ws.RealWebSocket.Streams) r2
            if (r2 == 0) goto L_0x0309
            java.io.Closeable r2 = (java.io.Closeable) r2
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r2)
        L_0x0309:
            T r2 = r6.element
            okhttp3.internal.ws.WebSocketReader r2 = (okhttp3.internal.ws.WebSocketReader) r2
            if (r2 == 0) goto L_0x0314
            java.io.Closeable r2 = (java.io.Closeable) r2
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r2)
        L_0x0314:
            T r2 = r8.element
            okhttp3.internal.ws.WebSocketWriter r2 = (okhttp3.internal.ws.WebSocketWriter) r2
            if (r2 == 0) goto L_0x031f
            java.io.Closeable r2 = (java.io.Closeable) r2
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r2)
        L_0x031f:
            throw r0
        L_0x0320:
            r0 = move-exception
            r1 = r24
            r4 = r25
            r8 = r26
            r6 = r27
            r5 = r28
            r9 = r29
            r7 = r30
            r2 = r1
            r1 = r4
            goto L_0x0343
        L_0x0332:
            r0 = move-exception
            r4 = r8
            r8 = r9
            r6 = r10
            r5 = r11
            r9 = r12
            r7 = r13
            r3 = r14
            r1 = r4
            goto L_0x0343
        L_0x033c:
            r0 = move-exception
            r8 = r9
            r6 = r10
            r5 = r11
            r9 = r12
            r7 = r13
            r3 = r14
        L_0x0343:
            monitor-exit(r32)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.writeOneFrame$okhttp():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0025, code lost:
        if (r1 == -1) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0027, code lost:
        failWebSocket(new java.net.SocketTimeoutException("sent ping but didn't receive pong within " + r7.pingIntervalMillis + "ms (after " + (r1 - 1) + " successful ping/pongs)"), (okhttp3.Response) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r0.writePing(okio.ByteString.EMPTY);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0062, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0063, code lost:
        failWebSocket(r3, (okhttp3.Response) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void writePingFrame$okhttp() {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            monitor-enter(r7)
            r2 = 0
            boolean r3 = r7.failed     // Catch:{ all -> 0x006d }
            if (r3 == 0) goto L_0x000a
            monitor-exit(r7)
            return
        L_0x000a:
            okhttp3.internal.ws.WebSocketWriter r3 = r7.writer     // Catch:{ all -> 0x006d }
            if (r3 == 0) goto L_0x006b
            r0 = r3
            boolean r3 = r7.awaitingPong     // Catch:{ all -> 0x006d }
            r4 = -1
            if (r3 == 0) goto L_0x0017
            int r3 = r7.sentPingCount     // Catch:{ all -> 0x006d }
            goto L_0x0018
        L_0x0017:
            r3 = r4
        L_0x0018:
            r1 = r3
            int r3 = r7.sentPingCount     // Catch:{ all -> 0x006d }
            r5 = 1
            int r3 = r3 + r5
            r7.sentPingCount = r3     // Catch:{ all -> 0x006d }
            r7.awaitingPong = r5     // Catch:{ all -> 0x006d }
            kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x006d }
            monitor-exit(r7)
            r2 = 0
            if (r1 == r4) goto L_0x005b
            java.net.SocketTimeoutException r3 = new java.net.SocketTimeoutException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "sent ping but didn't receive pong within "
            java.lang.StringBuilder r4 = r4.append(r5)
            long r5 = r7.pingIntervalMillis
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = "ms (after "
            java.lang.StringBuilder r4 = r4.append(r5)
            int r5 = r1 + -1
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = " successful ping/pongs)"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            java.lang.Exception r3 = (java.lang.Exception) r3
            r7.failWebSocket(r3, r2)
            return
        L_0x005b:
            okio.ByteString r3 = okio.ByteString.EMPTY     // Catch:{ IOException -> 0x0062 }
            r0.writePing(r3)     // Catch:{ IOException -> 0x0062 }
            goto L_0x0069
        L_0x0062:
            r3 = move-exception
            r4 = r3
            java.lang.Exception r4 = (java.lang.Exception) r4
            r7.failWebSocket(r4, r2)
        L_0x0069:
            return
        L_0x006b:
            monitor-exit(r7)
            return
        L_0x006d:
            r2 = move-exception
            monitor-exit(r7)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.writePingFrame$okhttp():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r6.listener.onFailure(r6, r7, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005b, code lost:
        if (r0 != null) goto L_0x005d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005d, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0063, code lost:
        if (r1 != null) goto L_0x0065;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0065, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006b, code lost:
        if (r2 != null) goto L_0x006d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006d, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0073, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void failWebSocket(java.lang.Exception r7, okhttp3.Response r8) {
        /*
            r6 = this;
            java.lang.String r0 = "e"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r7, r0)
            r0 = 0
            r1 = 0
            r2 = 0
            monitor-enter(r6)
            r3 = 0
            boolean r4 = r6.failed     // Catch:{ all -> 0x0074 }
            if (r4 == 0) goto L_0x0010
            monitor-exit(r6)
            return
        L_0x0010:
            r4 = 1
            r6.failed = r4     // Catch:{ all -> 0x0074 }
            okhttp3.internal.ws.RealWebSocket$Streams r4 = r6.streams     // Catch:{ all -> 0x0074 }
            r0 = r4
            r4 = 0
            r5 = r4
            okhttp3.internal.ws.RealWebSocket$Streams r5 = (okhttp3.internal.ws.RealWebSocket.Streams) r5     // Catch:{ all -> 0x0074 }
            r6.streams = r4     // Catch:{ all -> 0x0074 }
            okhttp3.internal.ws.WebSocketReader r5 = r6.reader     // Catch:{ all -> 0x0074 }
            r1 = r5
            r5 = r4
            okhttp3.internal.ws.WebSocketReader r5 = (okhttp3.internal.ws.WebSocketReader) r5     // Catch:{ all -> 0x0074 }
            r6.reader = r4     // Catch:{ all -> 0x0074 }
            okhttp3.internal.ws.WebSocketWriter r5 = r6.writer     // Catch:{ all -> 0x0074 }
            r2 = r5
            r5 = r4
            okhttp3.internal.ws.WebSocketWriter r5 = (okhttp3.internal.ws.WebSocketWriter) r5     // Catch:{ all -> 0x0074 }
            r6.writer = r4     // Catch:{ all -> 0x0074 }
            okhttp3.internal.concurrent.TaskQueue r4 = r6.taskQueue     // Catch:{ all -> 0x0074 }
            r4.shutdown()     // Catch:{ all -> 0x0074 }
            kotlin.Unit r3 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0074 }
            monitor-exit(r6)
            okhttp3.WebSocketListener r3 = r6.listener     // Catch:{ all -> 0x005a }
            r4 = r6
            okhttp3.WebSocket r4 = (okhttp3.WebSocket) r4     // Catch:{ all -> 0x005a }
            r5 = r7
            java.lang.Throwable r5 = (java.lang.Throwable) r5     // Catch:{ all -> 0x005a }
            r3.onFailure(r4, r5, r8)     // Catch:{ all -> 0x005a }
            if (r0 == 0) goto L_0x0048
            r3 = r0
            java.io.Closeable r3 = (java.io.Closeable) r3
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r3)
        L_0x0048:
            if (r1 == 0) goto L_0x0050
            r3 = r1
            java.io.Closeable r3 = (java.io.Closeable) r3
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r3)
        L_0x0050:
            if (r2 == 0) goto L_0x0058
            r3 = r2
            java.io.Closeable r3 = (java.io.Closeable) r3
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r3)
        L_0x0058:
            return
        L_0x005a:
            r3 = move-exception
            if (r0 == 0) goto L_0x0063
            r4 = r0
            java.io.Closeable r4 = (java.io.Closeable) r4
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4)
        L_0x0063:
            if (r1 == 0) goto L_0x006b
            r4 = r1
            java.io.Closeable r4 = (java.io.Closeable) r4
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4)
        L_0x006b:
            if (r2 == 0) goto L_0x0073
            r4 = r2
            java.io.Closeable r4 = (java.io.Closeable) r4
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4)
        L_0x0073:
            throw r3
        L_0x0074:
            r3 = move-exception
            monitor-exit(r6)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.failWebSocket(java.lang.Exception, okhttp3.Response):void");
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u000b"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$Message;", "", "formatOpcode", "", "data", "Lokio/ByteString;", "(ILokio/ByteString;)V", "getData", "()Lokio/ByteString;", "getFormatOpcode", "()I", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: RealWebSocket.kt */
    public static final class Message {
        private final ByteString data;
        private final int formatOpcode;

        public Message(int formatOpcode2, ByteString data2) {
            Intrinsics.checkNotNullParameter(data2, "data");
            this.formatOpcode = formatOpcode2;
            this.data = data2;
        }

        public final int getFormatOpcode() {
            return this.formatOpcode;
        }

        public final ByteString getData() {
            return this.data;
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\b\b\u0000\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$Close;", "", "code", "", "reason", "Lokio/ByteString;", "cancelAfterCloseMillis", "", "(ILokio/ByteString;J)V", "getCancelAfterCloseMillis", "()J", "getCode", "()I", "getReason", "()Lokio/ByteString;", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: RealWebSocket.kt */
    public static final class Close {
        private final long cancelAfterCloseMillis;
        private final int code;
        private final ByteString reason;

        public Close(int code2, ByteString reason2, long cancelAfterCloseMillis2) {
            this.code = code2;
            this.reason = reason2;
            this.cancelAfterCloseMillis = cancelAfterCloseMillis2;
        }

        public final int getCode() {
            return this.code;
        }

        public final ByteString getReason() {
            return this.reason;
        }

        public final long getCancelAfterCloseMillis() {
            return this.cancelAfterCloseMillis;
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b&\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$Streams;", "Ljava/io/Closeable;", "client", "", "source", "Lokio/BufferedSource;", "sink", "Lokio/BufferedSink;", "(ZLokio/BufferedSource;Lokio/BufferedSink;)V", "getClient", "()Z", "getSink", "()Lokio/BufferedSink;", "getSource", "()Lokio/BufferedSource;", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: RealWebSocket.kt */
    public static abstract class Streams implements Closeable {
        private final boolean client;
        private final BufferedSink sink;
        private final BufferedSource source;

        public Streams(boolean client2, BufferedSource source2, BufferedSink sink2) {
            Intrinsics.checkNotNullParameter(source2, "source");
            Intrinsics.checkNotNullParameter(sink2, "sink");
            this.client = client2;
            this.source = source2;
            this.sink = sink2;
        }

        public final boolean getClient() {
            return this.client;
        }

        public final BufferedSource getSource() {
            return this.source;
        }

        public final BufferedSink getSink() {
            return this.sink;
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$WriterTask;", "Lokhttp3/internal/concurrent/Task;", "(Lokhttp3/internal/ws/RealWebSocket;)V", "runOnce", "", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: RealWebSocket.kt */
    private final class WriterTask extends Task {
        public WriterTask() {
            super(RealWebSocket.this.name + " writer", false, 2, (DefaultConstructorMarker) null);
        }

        public long runOnce() {
            try {
                if (RealWebSocket.this.writeOneFrame$okhttp()) {
                    return 0;
                }
                return -1;
            } catch (IOException e) {
                RealWebSocket.this.failWebSocket(e, (Response) null);
                return -1;
            }
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lokhttp3/internal/ws/RealWebSocket$Companion;", "", "()V", "CANCEL_AFTER_CLOSE_MILLIS", "", "DEFAULT_MINIMUM_DEFLATE_SIZE", "MAX_QUEUE_SIZE", "ONLY_HTTP1", "", "Lokhttp3/Protocol;", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: RealWebSocket.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
