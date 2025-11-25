package okhttp3.internal.http2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Headers;
import okhttp3.internal.Util;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.BufferedSource;
import okio.Sink;
import okio.Source;
import okio.Timeout;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 _2\u00020\u0001:\u0004_`abB1\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\u0002\u0010\u000bJ\u000e\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020#J\r\u0010C\u001a\u00020AH\u0000¢\u0006\u0002\bDJ\r\u0010E\u001a\u00020AH\u0000¢\u0006\u0002\bFJ\u0018\u0010G\u001a\u00020A2\u0006\u0010H\u001a\u00020\u000f2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015J\u001a\u0010I\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0002J\u000e\u0010J\u001a\u00020A2\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010K\u001a\u00020A2\u0006\u0010L\u001a\u00020\nJ\u0006\u0010M\u001a\u00020NJ\u0006\u0010O\u001a\u00020PJ\u0006\u0010,\u001a\u00020QJ\u0016\u0010R\u001a\u00020A2\u0006\u00104\u001a\u00020S2\u0006\u0010T\u001a\u00020\u0003J\u0016\u0010U\u001a\u00020A2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u0007J\u000e\u0010V\u001a\u00020A2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010W\u001a\u00020\nJ\u0006\u0010L\u001a\u00020\nJ\r\u0010X\u001a\u00020AH\u0000¢\u0006\u0002\bYJ$\u0010Z\u001a\u00020A2\f\u0010[\u001a\b\u0012\u0004\u0012\u00020]0\\2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010^\u001a\u00020\u0007J\u0006\u0010>\u001a\u00020QR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001e\u0010\u000e\u001a\u0004\u0018\u00010\u000f8@X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u000e\u0010\u001a\u001a\u00020\u0007X\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\n0\u001cX\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u001f\u001a\u00020\u00078F¢\u0006\u0006\u001a\u0004\b\u001f\u0010 R\u0011\u0010!\u001a\u00020\u00078F¢\u0006\u0006\u001a\u0004\b!\u0010 R$\u0010$\u001a\u00020#2\u0006\u0010\"\u001a\u00020#@@X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b'\u0010(R$\u0010)\u001a\u00020#2\u0006\u0010\"\u001a\u00020#@@X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b*\u0010&\"\u0004\b+\u0010(R\u0018\u0010,\u001a\u00060-R\u00020\u0000X\u0004¢\u0006\b\n\u0000\u001a\u0004\b.\u0010/R\u0018\u00100\u001a\u000601R\u00020\u0000X\u0004¢\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u0018\u00104\u001a\u000605R\u00020\u0000X\u0004¢\u0006\b\n\u0000\u001a\u0004\b6\u00107R$\u00108\u001a\u00020#2\u0006\u0010\"\u001a\u00020#@@X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b9\u0010&\"\u0004\b:\u0010(R$\u0010;\u001a\u00020#2\u0006\u0010\"\u001a\u00020#@@X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b<\u0010&\"\u0004\b=\u0010(R\u0018\u0010>\u001a\u00060-R\u00020\u0000X\u0004¢\u0006\b\n\u0000\u001a\u0004\b?\u0010/¨\u0006c"}, d2 = {"Lokhttp3/internal/http2/Http2Stream;", "", "id", "", "connection", "Lokhttp3/internal/http2/Http2Connection;", "outFinished", "", "inFinished", "headers", "Lokhttp3/Headers;", "(ILokhttp3/internal/http2/Http2Connection;ZZLokhttp3/Headers;)V", "getConnection", "()Lokhttp3/internal/http2/Http2Connection;", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "getErrorCode$okhttp", "()Lokhttp3/internal/http2/ErrorCode;", "setErrorCode$okhttp", "(Lokhttp3/internal/http2/ErrorCode;)V", "errorException", "Ljava/io/IOException;", "getErrorException$okhttp", "()Ljava/io/IOException;", "setErrorException$okhttp", "(Ljava/io/IOException;)V", "hasResponseHeaders", "headersQueue", "Ljava/util/ArrayDeque;", "getId", "()I", "isLocallyInitiated", "()Z", "isOpen", "<set-?>", "", "readBytesAcknowledged", "getReadBytesAcknowledged", "()J", "setReadBytesAcknowledged$okhttp", "(J)V", "readBytesTotal", "getReadBytesTotal", "setReadBytesTotal$okhttp", "readTimeout", "Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "getReadTimeout$okhttp", "()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "sink", "Lokhttp3/internal/http2/Http2Stream$FramingSink;", "getSink$okhttp", "()Lokhttp3/internal/http2/Http2Stream$FramingSink;", "source", "Lokhttp3/internal/http2/Http2Stream$FramingSource;", "getSource$okhttp", "()Lokhttp3/internal/http2/Http2Stream$FramingSource;", "writeBytesMaximum", "getWriteBytesMaximum", "setWriteBytesMaximum$okhttp", "writeBytesTotal", "getWriteBytesTotal", "setWriteBytesTotal$okhttp", "writeTimeout", "getWriteTimeout$okhttp", "addBytesToWriteWindow", "", "delta", "cancelStreamIfNecessary", "cancelStreamIfNecessary$okhttp", "checkOutNotClosed", "checkOutNotClosed$okhttp", "close", "rstStatusCode", "closeInternal", "closeLater", "enqueueTrailers", "trailers", "getSink", "Lokio/Sink;", "getSource", "Lokio/Source;", "Lokio/Timeout;", "receiveData", "Lokio/BufferedSource;", "length", "receiveHeaders", "receiveRstStream", "takeHeaders", "waitForIo", "waitForIo$okhttp", "writeHeaders", "responseHeaders", "", "Lokhttp3/internal/http2/Header;", "flushHeaders", "Companion", "FramingSink", "FramingSource", "StreamTimeout", "okhttp"}, k = 1, mv = {1, 4, 0})
/* compiled from: Http2Stream.kt */
public final class Http2Stream {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final long EMIT_BUFFER_SIZE = 16384;
    private final Http2Connection connection;
    private ErrorCode errorCode;
    private IOException errorException;
    private boolean hasResponseHeaders;
    private final ArrayDeque<Headers> headersQueue = new ArrayDeque<>();
    private final int id;
    private long readBytesAcknowledged;
    private long readBytesTotal;
    private final StreamTimeout readTimeout;
    private final FramingSink sink;
    private final FramingSource source;
    private long writeBytesMaximum = ((long) this.connection.getPeerSettings().getInitialWindowSize());
    private long writeBytesTotal;
    private final StreamTimeout writeTimeout;

    public Http2Stream(int id2, Http2Connection connection2, boolean outFinished, boolean inFinished, Headers headers) {
        Intrinsics.checkNotNullParameter(connection2, "connection");
        this.id = id2;
        this.connection = connection2;
        this.source = new FramingSource((long) this.connection.getOkHttpSettings().getInitialWindowSize(), inFinished);
        this.sink = new FramingSink(outFinished);
        this.readTimeout = new StreamTimeout();
        this.writeTimeout = new StreamTimeout();
        if (headers != null) {
            if (!isLocallyInitiated()) {
                this.headersQueue.add(headers);
                return;
            }
            throw new IllegalStateException("locally-initiated streams shouldn't have headers yet".toString());
        } else if (!isLocallyInitiated()) {
            throw new IllegalStateException("remotely-initiated streams should have headers".toString());
        }
    }

    public final int getId() {
        return this.id;
    }

    public final Http2Connection getConnection() {
        return this.connection;
    }

    public final long getReadBytesTotal() {
        return this.readBytesTotal;
    }

    public final void setReadBytesTotal$okhttp(long j) {
        this.readBytesTotal = j;
    }

    public final long getReadBytesAcknowledged() {
        return this.readBytesAcknowledged;
    }

    public final void setReadBytesAcknowledged$okhttp(long j) {
        this.readBytesAcknowledged = j;
    }

    public final long getWriteBytesTotal() {
        return this.writeBytesTotal;
    }

    public final void setWriteBytesTotal$okhttp(long j) {
        this.writeBytesTotal = j;
    }

    public final long getWriteBytesMaximum() {
        return this.writeBytesMaximum;
    }

    public final void setWriteBytesMaximum$okhttp(long j) {
        this.writeBytesMaximum = j;
    }

    public final FramingSource getSource$okhttp() {
        return this.source;
    }

    public final FramingSink getSink$okhttp() {
        return this.sink;
    }

    public final StreamTimeout getReadTimeout$okhttp() {
        return this.readTimeout;
    }

    public final StreamTimeout getWriteTimeout$okhttp() {
        return this.writeTimeout;
    }

    public final synchronized ErrorCode getErrorCode$okhttp() {
        return this.errorCode;
    }

    public final void setErrorCode$okhttp(ErrorCode errorCode2) {
        this.errorCode = errorCode2;
    }

    public final IOException getErrorException$okhttp() {
        return this.errorException;
    }

    public final void setErrorException$okhttp(IOException iOException) {
        this.errorException = iOException;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0031, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized boolean isOpen() {
        /*
            r2 = this;
            monitor-enter(r2)
            okhttp3.internal.http2.ErrorCode r0 = r2.errorCode     // Catch:{ all -> 0x0033 }
            r1 = 0
            if (r0 == 0) goto L_0x0008
            monitor-exit(r2)
            return r1
        L_0x0008:
            okhttp3.internal.http2.Http2Stream$FramingSource r0 = r2.source     // Catch:{ all -> 0x0033 }
            boolean r0 = r0.getFinished$okhttp()     // Catch:{ all -> 0x0033 }
            if (r0 != 0) goto L_0x001a
            okhttp3.internal.http2.Http2Stream$FramingSource r0 = r2.source     // Catch:{ all -> 0x0033 }
            boolean r0 = r0.getClosed$okhttp()     // Catch:{ all -> 0x0033 }
            if (r0 == 0) goto L_0x0030
        L_0x001a:
            okhttp3.internal.http2.Http2Stream$FramingSink r0 = r2.sink     // Catch:{ all -> 0x0033 }
            boolean r0 = r0.getFinished()     // Catch:{ all -> 0x0033 }
            if (r0 != 0) goto L_0x002a
            okhttp3.internal.http2.Http2Stream$FramingSink r0 = r2.sink     // Catch:{ all -> 0x0033 }
            boolean r0 = r0.getClosed()     // Catch:{ all -> 0x0033 }
            if (r0 == 0) goto L_0x0030
        L_0x002a:
            boolean r0 = r2.hasResponseHeaders     // Catch:{ all -> 0x0033 }
            if (r0 == 0) goto L_0x0030
            monitor-exit(r2)
            return r1
        L_0x0030:
            monitor-exit(r2)
            r0 = 1
            return r0
        L_0x0033:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.isOpen():boolean");
    }

    public final boolean isLocallyInitiated() {
        if (this.connection.getClient$okhttp() == ((this.id & 1) == 1)) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized okhttp3.Headers takeHeaders() throws java.io.IOException {
        /*
            r2 = this;
            monitor-enter(r2)
            okhttp3.internal.http2.Http2Stream$StreamTimeout r0 = r2.readTimeout     // Catch:{ all -> 0x0054 }
            r0.enter()     // Catch:{ all -> 0x0054 }
        L_0x0007:
            java.util.ArrayDeque<okhttp3.Headers> r0 = r2.headersQueue     // Catch:{ all -> 0x004d }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x004d }
            if (r0 == 0) goto L_0x0019
            okhttp3.internal.http2.ErrorCode r0 = r2.errorCode     // Catch:{ all -> 0x0017 }
            if (r0 != 0) goto L_0x0019
            r2.waitForIo$okhttp()     // Catch:{ all -> 0x0017 }
            goto L_0x0007
        L_0x0017:
            r0 = move-exception
            goto L_0x004e
        L_0x0019:
            okhttp3.internal.http2.Http2Stream$StreamTimeout r0 = r2.readTimeout     // Catch:{ all -> 0x0054 }
            r0.exitAndThrowIfTimedOut()     // Catch:{ all -> 0x0054 }
            java.util.ArrayDeque<okhttp3.Headers> r0 = r2.headersQueue     // Catch:{ all -> 0x0054 }
            java.util.Collection r0 = (java.util.Collection) r0     // Catch:{ all -> 0x0054 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0054 }
            r0 = r0 ^ 1
            if (r0 == 0) goto L_0x003a
            java.util.ArrayDeque<okhttp3.Headers> r0 = r2.headersQueue     // Catch:{ all -> 0x0054 }
            java.lang.Object r0 = r0.removeFirst()     // Catch:{ all -> 0x0054 }
            java.lang.String r1 = "headersQueue.removeFirst()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)     // Catch:{ all -> 0x0054 }
            okhttp3.Headers r0 = (okhttp3.Headers) r0     // Catch:{ all -> 0x0054 }
            monitor-exit(r2)
            return r0
        L_0x003a:
            java.io.IOException r0 = r2.errorException     // Catch:{ all -> 0x0054 }
            if (r0 == 0) goto L_0x0041
        L_0x003e:
            java.lang.Throwable r0 = (java.lang.Throwable) r0     // Catch:{ all -> 0x0054 }
            goto L_0x004c
        L_0x0041:
            okhttp3.internal.http2.StreamResetException r0 = new okhttp3.internal.http2.StreamResetException     // Catch:{ all -> 0x0054 }
            okhttp3.internal.http2.ErrorCode r1 = r2.errorCode     // Catch:{ all -> 0x0054 }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r1)     // Catch:{ all -> 0x0054 }
            r0.<init>(r1)     // Catch:{ all -> 0x0054 }
            goto L_0x003e
        L_0x004c:
            throw r0     // Catch:{ all -> 0x0054 }
        L_0x004d:
            r0 = move-exception
        L_0x004e:
            okhttp3.internal.http2.Http2Stream$StreamTimeout r1 = r2.readTimeout     // Catch:{ all -> 0x0054 }
            r1.exitAndThrowIfTimedOut()     // Catch:{ all -> 0x0054 }
            throw r0     // Catch:{ all -> 0x0054 }
        L_0x0054:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.takeHeaders():okhttp3.Headers");
    }

    public final synchronized Headers trailers() throws IOException {
        Headers trailers;
        Throwable th;
        if (this.errorCode != null) {
            IOException iOException = this.errorException;
            if (iOException != null) {
                th = iOException;
            } else {
                ErrorCode errorCode2 = this.errorCode;
                Intrinsics.checkNotNull(errorCode2);
                th = new StreamResetException(errorCode2);
            }
            throw th;
        }
        if (this.source.getFinished$okhttp() && this.source.getReceiveBuffer().exhausted() && this.source.getReadBuffer().exhausted()) {
            trailers = this.source.getTrailers();
            if (trailers == null) {
                trailers = Util.EMPTY_HEADERS;
            }
        } else {
            throw new IllegalStateException("too early; can't read the trailers yet".toString());
        }
        return trailers;
    }

    public final void writeHeaders(List<Header> responseHeaders, boolean outFinished, boolean flushHeaders) throws IOException {
        boolean z;
        Intrinsics.checkNotNullParameter(responseHeaders, "responseHeaders");
        if (!Util.assertionsEnabled || !Thread.holdsLock(this)) {
            boolean flushHeaders2 = flushHeaders;
            synchronized (this) {
                z = true;
                this.hasResponseHeaders = true;
                if (outFinished) {
                    this.sink.setFinished(true);
                }
                Unit unit = Unit.INSTANCE;
            }
            if (!flushHeaders2) {
                synchronized (this.connection) {
                    if (this.connection.getWriteBytesTotal() < this.connection.getWriteBytesMaximum()) {
                        z = false;
                    }
                    flushHeaders2 = z;
                    Unit unit2 = Unit.INSTANCE;
                }
            }
            this.connection.writeHeaders$okhttp(this.id, outFinished, responseHeaders);
            if (flushHeaders2) {
                this.connection.flush();
                return;
            }
            return;
        }
        StringBuilder append = new StringBuilder().append("Thread ");
        Thread currentThread = Thread.currentThread();
        Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
        throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append(this).toString());
    }

    public final void enqueueTrailers(Headers trailers) {
        Intrinsics.checkNotNullParameter(trailers, "trailers");
        synchronized (this) {
            boolean z = true;
            if (!this.sink.getFinished()) {
                if (trailers.size() == 0) {
                    z = false;
                }
                if (z) {
                    this.sink.setTrailers(trailers);
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new IllegalArgumentException("trailers.size() == 0".toString());
                }
            } else {
                throw new IllegalStateException("already finished".toString());
            }
        }
    }

    public final Timeout readTimeout() {
        return this.readTimeout;
    }

    public final Timeout writeTimeout() {
        return this.writeTimeout;
    }

    public final Source getSource() {
        return this.source;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0012  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x001a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final okio.Sink getSink() {
        /*
            r3 = this;
            monitor-enter(r3)
            r0 = 0
            boolean r1 = r3.hasResponseHeaders     // Catch:{ all -> 0x002a }
            if (r1 != 0) goto L_0x000f
            boolean r1 = r3.isLocallyInitiated()     // Catch:{ all -> 0x002a }
            if (r1 == 0) goto L_0x000d
            goto L_0x000f
        L_0x000d:
            r1 = 0
            goto L_0x0010
        L_0x000f:
            r1 = 1
        L_0x0010:
            if (r1 == 0) goto L_0x001a
            kotlin.Unit r0 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x002a }
            monitor-exit(r3)
            okhttp3.internal.http2.Http2Stream$FramingSink r0 = r3.sink
            okio.Sink r0 = (okio.Sink) r0
            return r0
        L_0x001a:
            r1 = 0
            java.lang.String r2 = "reply before requesting the sink"
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x002a }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x002a }
            r1.<init>(r2)     // Catch:{ all -> 0x002a }
            java.lang.Throwable r1 = (java.lang.Throwable) r1     // Catch:{ all -> 0x002a }
            throw r1     // Catch:{ all -> 0x002a }
        L_0x002a:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.getSink():okio.Sink");
    }

    public final void close(ErrorCode rstStatusCode, IOException errorException2) throws IOException {
        Intrinsics.checkNotNullParameter(rstStatusCode, "rstStatusCode");
        if (closeInternal(rstStatusCode, errorException2)) {
            this.connection.writeSynReset$okhttp(this.id, rstStatusCode);
        }
    }

    public final void closeLater(ErrorCode errorCode2) {
        Intrinsics.checkNotNullParameter(errorCode2, "errorCode");
        if (closeInternal(errorCode2, (IOException) null)) {
            this.connection.writeSynResetLater$okhttp(this.id, errorCode2);
        }
    }

    private final boolean closeInternal(ErrorCode errorCode2, IOException errorException2) {
        if (!Util.assertionsEnabled || !Thread.holdsLock(this)) {
            synchronized (this) {
                if (this.errorCode != null) {
                    return false;
                }
                if (this.source.getFinished$okhttp() && this.sink.getFinished()) {
                    return false;
                }
                this.errorCode = errorCode2;
                this.errorException = errorException2;
                notifyAll();
                Unit unit = Unit.INSTANCE;
                this.connection.removeStream$okhttp(this.id);
                return true;
            }
        }
        StringBuilder append = new StringBuilder().append("Thread ");
        Thread currentThread = Thread.currentThread();
        Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
        throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append(this).toString());
    }

    public final void receiveData(BufferedSource source2, int length) throws IOException {
        Intrinsics.checkNotNullParameter(source2, "source");
        if (!Util.assertionsEnabled || !Thread.holdsLock(this)) {
            this.source.receive$okhttp(source2, (long) length);
            return;
        }
        StringBuilder append = new StringBuilder().append("Thread ");
        Thread currentThread = Thread.currentThread();
        Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
        throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append(this).toString());
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0062  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void receiveHeaders(okhttp3.Headers r7, boolean r8) {
        /*
            r6 = this;
            java.lang.String r0 = "headers"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r7, r0)
            r0 = r6
            r1 = 0
            boolean r2 = okhttp3.internal.Util.assertionsEnabled
            if (r2 == 0) goto L_0x0044
            boolean r2 = java.lang.Thread.holdsLock(r0)
            if (r2 != 0) goto L_0x0012
            goto L_0x0044
        L_0x0012:
            java.lang.AssertionError r2 = new java.lang.AssertionError
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Thread "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.Thread r4 = java.lang.Thread.currentThread()
            java.lang.String r5 = "Thread.currentThread()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r4, r5)
            java.lang.String r4 = r4.getName()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = " MUST NOT hold lock on "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.StringBuilder r3 = r3.append(r0)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            java.lang.Throwable r2 = (java.lang.Throwable) r2
            throw r2
        L_0x0044:
            r0 = 0
            monitor-enter(r6)
            r1 = 0
            boolean r2 = r6.hasResponseHeaders     // Catch:{ all -> 0x0082 }
            r3 = 1
            if (r2 == 0) goto L_0x0056
            if (r8 != 0) goto L_0x0050
            goto L_0x0056
        L_0x0050:
            okhttp3.internal.http2.Http2Stream$FramingSource r2 = r6.source     // Catch:{ all -> 0x0082 }
            r2.setTrailers(r7)     // Catch:{ all -> 0x0082 }
            goto L_0x005f
        L_0x0056:
            r6.hasResponseHeaders = r3     // Catch:{ all -> 0x0082 }
            java.util.ArrayDeque<okhttp3.Headers> r2 = r6.headersQueue     // Catch:{ all -> 0x0082 }
            java.util.Collection r2 = (java.util.Collection) r2     // Catch:{ all -> 0x0082 }
            r2.add(r7)     // Catch:{ all -> 0x0082 }
        L_0x005f:
            if (r8 == 0) goto L_0x0067
            okhttp3.internal.http2.Http2Stream$FramingSource r2 = r6.source     // Catch:{ all -> 0x0082 }
            r2.setFinished$okhttp(r3)     // Catch:{ all -> 0x0082 }
        L_0x0067:
            boolean r2 = r6.isOpen()     // Catch:{ all -> 0x0082 }
            r0 = r2
            r2 = r6
            r3 = 0
            r4 = r2
            java.lang.Object r4 = (java.lang.Object) r4     // Catch:{ all -> 0x0082 }
            r4.notifyAll()     // Catch:{ all -> 0x0082 }
            kotlin.Unit r1 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0082 }
            monitor-exit(r6)
            if (r0 != 0) goto L_0x0081
            okhttp3.internal.http2.Http2Connection r1 = r6.connection
            int r2 = r6.id
            r1.removeStream$okhttp(r2)
        L_0x0081:
            return
        L_0x0082:
            r1 = move-exception
            monitor-exit(r6)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.receiveHeaders(okhttp3.Headers, boolean):void");
    }

    public final synchronized void receiveRstStream(ErrorCode errorCode2) {
        Intrinsics.checkNotNullParameter(errorCode2, "errorCode");
        if (this.errorCode == null) {
            this.errorCode = errorCode2;
            notifyAll();
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0017\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\u001a\u001a\u00020\u001bH\u0016J\u0018\u0010\u001c\u001a\u00020\u00032\u0006\u0010\u001d\u001a\u00020\u000f2\u0006\u0010\u001e\u001a\u00020\u0003H\u0016J\u001d\u0010\u001f\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020!2\u0006\u0010\u001e\u001a\u00020\u0003H\u0000¢\u0006\u0002\b\"J\b\u0010#\u001a\u00020$H\u0016J\u0010\u0010%\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0003H\u0002R\u001a\u0010\u0007\u001a\u00020\u0005X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\u0004\u001a\u00020\u0005X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\t\"\u0004\b\r\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019¨\u0006&"}, d2 = {"Lokhttp3/internal/http2/Http2Stream$FramingSource;", "Lokio/Source;", "maxByteCount", "", "finished", "", "(Lokhttp3/internal/http2/Http2Stream;JZ)V", "closed", "getClosed$okhttp", "()Z", "setClosed$okhttp", "(Z)V", "getFinished$okhttp", "setFinished$okhttp", "readBuffer", "Lokio/Buffer;", "getReadBuffer", "()Lokio/Buffer;", "receiveBuffer", "getReceiveBuffer", "trailers", "Lokhttp3/Headers;", "getTrailers", "()Lokhttp3/Headers;", "setTrailers", "(Lokhttp3/Headers;)V", "close", "", "read", "sink", "byteCount", "receive", "source", "Lokio/BufferedSource;", "receive$okhttp", "timeout", "Lokio/Timeout;", "updateConnectionFlowControl", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: Http2Stream.kt */
    public final class FramingSource implements Source {
        private boolean closed;
        private boolean finished;
        private final long maxByteCount;
        private final Buffer readBuffer = new Buffer();
        private final Buffer receiveBuffer = new Buffer();
        private Headers trailers;

        public FramingSource(long maxByteCount2, boolean finished2) {
            this.maxByteCount = maxByteCount2;
            this.finished = finished2;
        }

        public final boolean getFinished$okhttp() {
            return this.finished;
        }

        public final void setFinished$okhttp(boolean z) {
            this.finished = z;
        }

        public final Buffer getReceiveBuffer() {
            return this.receiveBuffer;
        }

        public final Buffer getReadBuffer() {
            return this.readBuffer;
        }

        public final Headers getTrailers() {
            return this.trailers;
        }

        public final void setTrailers(Headers headers) {
            this.trailers = headers;
        }

        public final boolean getClosed$okhttp() {
            return this.closed;
        }

        public final void setClosed$okhttp(boolean z) {
            this.closed = z;
        }

        /* JADX INFO: finally extract failed */
        public long read(Buffer sink, long byteCount) throws IOException {
            Buffer buffer = sink;
            long j = byteCount;
            Intrinsics.checkNotNullParameter(buffer, "sink");
            long j2 = 0;
            if (j >= 0) {
                while (true) {
                    boolean tryAgain = false;
                    long readBytesDelivered = -1;
                    Object errorExceptionToDeliver = (IOException) null;
                    synchronized (Http2Stream.this) {
                        Http2Stream.this.getReadTimeout$okhttp().enter();
                        try {
                            if (Http2Stream.this.getErrorCode$okhttp() != null) {
                                Object errorException$okhttp = Http2Stream.this.getErrorException$okhttp();
                                if (errorException$okhttp == null) {
                                    ErrorCode errorCode$okhttp = Http2Stream.this.getErrorCode$okhttp();
                                    Intrinsics.checkNotNull(errorCode$okhttp);
                                    errorException$okhttp = (IOException) new StreamResetException(errorCode$okhttp);
                                }
                                errorExceptionToDeliver = errorException$okhttp;
                            }
                            if (!this.closed) {
                                if (this.readBuffer.size() > j2) {
                                    readBytesDelivered = this.readBuffer.read(buffer, Math.min(j, this.readBuffer.size()));
                                    Http2Stream http2Stream = Http2Stream.this;
                                    http2Stream.setReadBytesTotal$okhttp(http2Stream.getReadBytesTotal() + readBytesDelivered);
                                    long unacknowledgedBytesRead = Http2Stream.this.getReadBytesTotal() - Http2Stream.this.getReadBytesAcknowledged();
                                    if (errorExceptionToDeliver == null && unacknowledgedBytesRead >= ((long) (Http2Stream.this.getConnection().getOkHttpSettings().getInitialWindowSize() / 2))) {
                                        Http2Stream.this.getConnection().writeWindowUpdateLater$okhttp(Http2Stream.this.getId(), unacknowledgedBytesRead);
                                        Http2Stream.this.setReadBytesAcknowledged$okhttp(Http2Stream.this.getReadBytesTotal());
                                    }
                                } else if (!this.finished && errorExceptionToDeliver == null) {
                                    Http2Stream.this.waitForIo$okhttp();
                                    tryAgain = true;
                                }
                                Http2Stream.this.getReadTimeout$okhttp().exitAndThrowIfTimedOut();
                                Unit unit = Unit.INSTANCE;
                            } else {
                                throw new IOException("stream closed");
                            }
                        } catch (Throwable th) {
                            Http2Stream.this.getReadTimeout$okhttp().exitAndThrowIfTimedOut();
                            throw th;
                        }
                    }
                    if (tryAgain) {
                        j2 = 0;
                    } else if (readBytesDelivered != -1) {
                        updateConnectionFlowControl(readBytesDelivered);
                        return readBytesDelivered;
                    } else if (errorExceptionToDeliver == null) {
                        return -1;
                    } else {
                        Intrinsics.checkNotNull(errorExceptionToDeliver);
                        throw ((Throwable) errorExceptionToDeliver);
                    }
                }
            } else {
                throw new IllegalArgumentException(("byteCount < 0: " + j).toString());
            }
        }

        private final void updateConnectionFlowControl(long read) {
            Object $this$assertThreadDoesntHoldLock$iv = Http2Stream.this;
            if (!Util.assertionsEnabled || !Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                Http2Stream.this.getConnection().updateConnectionFlowControl$okhttp(read);
                return;
            }
            StringBuilder append = new StringBuilder().append("Thread ");
            Thread currentThread = Thread.currentThread();
            Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
            throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append($this$assertThreadDoesntHoldLock$iv).toString());
        }

        public final void receive$okhttp(BufferedSource source, long byteCount) throws IOException {
            boolean finished2;
            boolean flowControlError;
            BufferedSource bufferedSource = source;
            Intrinsics.checkNotNullParameter(bufferedSource, "source");
            Object $this$assertThreadDoesntHoldLock$iv = Http2Stream.this;
            if (!Util.assertionsEnabled || !Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                long byteCount2 = byteCount;
                while (byteCount2 > 0) {
                    synchronized (Http2Stream.this) {
                        finished2 = this.finished;
                        flowControlError = this.readBuffer.size() + byteCount2 > this.maxByteCount;
                        Unit unit = Unit.INSTANCE;
                    }
                    if (flowControlError) {
                        bufferedSource.skip(byteCount2);
                        Http2Stream.this.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                        return;
                    } else if (finished2) {
                        bufferedSource.skip(byteCount2);
                        return;
                    } else {
                        long read = bufferedSource.read(this.receiveBuffer, byteCount2);
                        if (read != -1) {
                            byteCount2 -= read;
                            long bytesDiscarded = 0;
                            synchronized (Http2Stream.this) {
                                if (this.closed) {
                                    bytesDiscarded = this.receiveBuffer.size();
                                    this.receiveBuffer.clear();
                                } else {
                                    boolean wasEmpty = this.readBuffer.size() == 0;
                                    this.readBuffer.writeAll(this.receiveBuffer);
                                    if (wasEmpty) {
                                        Object $this$notifyAll$iv = Http2Stream.this;
                                        if ($this$notifyAll$iv != null) {
                                            $this$notifyAll$iv.notifyAll();
                                        } else {
                                            throw new NullPointerException("null cannot be cast to non-null type java.lang.Object");
                                        }
                                    }
                                }
                                Unit unit2 = Unit.INSTANCE;
                            }
                            if (bytesDiscarded > 0) {
                                updateConnectionFlowControl(bytesDiscarded);
                            }
                        } else {
                            throw new EOFException();
                        }
                    }
                }
                return;
            }
            StringBuilder append = new StringBuilder().append("Thread ");
            Thread currentThread = Thread.currentThread();
            Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
            throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append($this$assertThreadDoesntHoldLock$iv).toString());
        }

        public Timeout timeout() {
            return Http2Stream.this.getReadTimeout$okhttp();
        }

        public void close() throws IOException {
            long bytesDiscarded;
            synchronized (Http2Stream.this) {
                this.closed = true;
                bytesDiscarded = this.readBuffer.size();
                this.readBuffer.clear();
                Object $this$notifyAll$iv = Http2Stream.this;
                if ($this$notifyAll$iv != null) {
                    $this$notifyAll$iv.notifyAll();
                    Unit unit = Unit.INSTANCE;
                } else {
                    throw new NullPointerException("null cannot be cast to non-null type java.lang.Object");
                }
            }
            if (bytesDiscarded > 0) {
                updateConnectionFlowControl(bytesDiscarded);
            }
            Http2Stream.this.cancelStreamIfNecessary$okhttp();
        }
    }

    public final void cancelStreamIfNecessary$okhttp() throws IOException {
        boolean cancel;
        boolean open;
        if (!Util.assertionsEnabled || !Thread.holdsLock(this)) {
            synchronized (this) {
                cancel = !this.source.getFinished$okhttp() && this.source.getClosed$okhttp() && (this.sink.getFinished() || this.sink.getClosed());
                open = isOpen();
                Unit unit = Unit.INSTANCE;
            }
            if (cancel) {
                close(ErrorCode.CANCEL, (IOException) null);
            } else if (!open) {
                this.connection.removeStream$okhttp(this.id);
            }
        } else {
            StringBuilder append = new StringBuilder().append("Thread ");
            Thread currentThread = Thread.currentThread();
            Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
            throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append(this).toString());
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\u0010\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0003H\u0002J\b\u0010\u0018\u001a\u00020\u0015H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u0018\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016R\u001a\u0010\u0005\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\u0002\u001a\u00020\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\tR\u000e\u0010\f\u001a\u00020\rX\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013¨\u0006\u001f"}, d2 = {"Lokhttp3/internal/http2/Http2Stream$FramingSink;", "Lokio/Sink;", "finished", "", "(Lokhttp3/internal/http2/Http2Stream;Z)V", "closed", "getClosed", "()Z", "setClosed", "(Z)V", "getFinished", "setFinished", "sendBuffer", "Lokio/Buffer;", "trailers", "Lokhttp3/Headers;", "getTrailers", "()Lokhttp3/Headers;", "setTrailers", "(Lokhttp3/Headers;)V", "close", "", "emitFrame", "outFinishedOnLastFrame", "flush", "timeout", "Lokio/Timeout;", "write", "source", "byteCount", "", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: Http2Stream.kt */
    public final class FramingSink implements Sink {
        private boolean closed;
        private boolean finished;
        private final Buffer sendBuffer;
        private Headers trailers;

        public FramingSink(boolean finished2) {
            this.finished = finished2;
            this.sendBuffer = new Buffer();
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public /* synthetic */ FramingSink(Http2Stream http2Stream, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this((i & 1) != 0 ? false : z);
        }

        public final boolean getFinished() {
            return this.finished;
        }

        public final void setFinished(boolean z) {
            this.finished = z;
        }

        public final Headers getTrailers() {
            return this.trailers;
        }

        public final void setTrailers(Headers headers) {
            this.trailers = headers;
        }

        public final boolean getClosed() {
            return this.closed;
        }

        public final void setClosed(boolean z) {
            this.closed = z;
        }

        public void write(Buffer source, long byteCount) throws IOException {
            Intrinsics.checkNotNullParameter(source, "source");
            Object $this$assertThreadDoesntHoldLock$iv = Http2Stream.this;
            if (!Util.assertionsEnabled || !Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                this.sendBuffer.write(source, byteCount);
                while (this.sendBuffer.size() >= Http2Stream.EMIT_BUFFER_SIZE) {
                    emitFrame(false);
                }
                return;
            }
            StringBuilder append = new StringBuilder().append("Thread ");
            Thread currentThread = Thread.currentThread();
            Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
            throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append($this$assertThreadDoesntHoldLock$iv).toString());
        }

        private final void emitFrame(boolean outFinishedOnLastFrame) throws IOException {
            synchronized (Http2Stream.this) {
                try {
                    Http2Stream.this.getWriteTimeout$okhttp().enter();
                    while (Http2Stream.this.getWriteBytesTotal() >= Http2Stream.this.getWriteBytesMaximum() && !this.finished && !this.closed && Http2Stream.this.getErrorCode$okhttp() == null) {
                        Http2Stream.this.waitForIo$okhttp();
                    }
                    Http2Stream.this.getWriteTimeout$okhttp().exitAndThrowIfTimedOut();
                    Http2Stream.this.checkOutNotClosed$okhttp();
                    long toWrite = Math.min(Http2Stream.this.getWriteBytesMaximum() - Http2Stream.this.getWriteBytesTotal(), this.sendBuffer.size());
                    Http2Stream http2Stream = Http2Stream.this;
                    http2Stream.setWriteBytesTotal$okhttp(http2Stream.getWriteBytesTotal() + toWrite);
                    boolean outFinished = outFinishedOnLastFrame && toWrite == this.sendBuffer.size() && Http2Stream.this.getErrorCode$okhttp() == null;
                    try {
                        Unit unit = Unit.INSTANCE;
                        Http2Stream.this.getWriteTimeout$okhttp().enter();
                        try {
                            Http2Stream.this.getConnection().writeData(Http2Stream.this.getId(), outFinished, this.sendBuffer, toWrite);
                        } finally {
                            Http2Stream.this.getWriteTimeout$okhttp().exitAndThrowIfTimedOut();
                        }
                    } catch (Throwable th) {
                        th = th;
                        boolean z = outFinished;
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        }

        public void flush() throws IOException {
            Object $this$assertThreadDoesntHoldLock$iv = Http2Stream.this;
            if (!Util.assertionsEnabled || !Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                synchronized (Http2Stream.this) {
                    Http2Stream.this.checkOutNotClosed$okhttp();
                    Unit unit = Unit.INSTANCE;
                }
                while (this.sendBuffer.size() > 0) {
                    emitFrame(false);
                    Http2Stream.this.getConnection().flush();
                }
                return;
            }
            StringBuilder append = new StringBuilder().append("Thread ");
            Thread currentThread = Thread.currentThread();
            Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
            throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append($this$assertThreadDoesntHoldLock$iv).toString());
        }

        public Timeout timeout() {
            return Http2Stream.this.getWriteTimeout$okhttp();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0065, code lost:
            if (r12.this$0.getSink$okhttp().finished != false) goto L_0x00cc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0071, code lost:
            if (r12.sendBuffer.size() <= 0) goto L_0x0075;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0073, code lost:
            r1 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0075, code lost:
            r1 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0078, code lost:
            if (r12.trailers == null) goto L_0x007c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x007a, code lost:
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x007c, code lost:
            r2 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x007e, code lost:
            if (r2 == false) goto L_0x00a7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0088, code lost:
            if (r12.sendBuffer.size() <= 0) goto L_0x008e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x008a, code lost:
            emitFrame(false);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x008e, code lost:
            r3 = r12.this$0.getConnection();
            r4 = r12.this$0.getId();
            r6 = r12.trailers;
            kotlin.jvm.internal.Intrinsics.checkNotNull(r6);
            r3.writeHeaders$okhttp(r4, r0, okhttp3.internal.Util.toHeaderList(r6));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a7, code lost:
            if (r1 == false) goto L_0x00b7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x00b1, code lost:
            if (r12.sendBuffer.size() <= 0) goto L_0x00cc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x00b3, code lost:
            emitFrame(true);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x00b7, code lost:
            if (r0 == false) goto L_0x00cc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b9, code lost:
            r12.this$0.getConnection().writeData(r12.this$0.getId(), true, (okio.Buffer) null, 0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00cc, code lost:
            r1 = r12.this$0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:43:0x00cf, code lost:
            monitor-enter(r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
            r12.closed = true;
            r2 = kotlin.Unit.INSTANCE;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x00d5, code lost:
            monitor-exit(r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d6, code lost:
            r12.this$0.getConnection().flush();
            r12.this$0.cancelStreamIfNecessary$okhttp();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e4, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void close() throws java.io.IOException {
            /*
                r12 = this;
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                r1 = 0
                boolean r2 = okhttp3.internal.Util.assertionsEnabled
                if (r2 == 0) goto L_0x0040
                boolean r2 = java.lang.Thread.holdsLock(r0)
                if (r2 != 0) goto L_0x000e
                goto L_0x0040
            L_0x000e:
                java.lang.AssertionError r2 = new java.lang.AssertionError
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Thread "
                java.lang.StringBuilder r3 = r3.append(r4)
                java.lang.Thread r4 = java.lang.Thread.currentThread()
                java.lang.String r5 = "Thread.currentThread()"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r4, r5)
                java.lang.String r4 = r4.getName()
                java.lang.StringBuilder r3 = r3.append(r4)
                java.lang.String r4 = " MUST NOT hold lock on "
                java.lang.StringBuilder r3 = r3.append(r4)
                java.lang.StringBuilder r3 = r3.append(r0)
                java.lang.String r3 = r3.toString()
                r2.<init>(r3)
                java.lang.Throwable r2 = (java.lang.Throwable) r2
                throw r2
            L_0x0040:
                r0 = 0
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                monitor-enter(r1)
                r2 = 0
                boolean r3 = r12.closed     // Catch:{ all -> 0x00e8 }
                if (r3 == 0) goto L_0x004c
                monitor-exit(r1)
                return
            L_0x004c:
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00e8 }
                okhttp3.internal.http2.ErrorCode r3 = r3.getErrorCode$okhttp()     // Catch:{ all -> 0x00e8 }
                r4 = 0
                r5 = 1
                if (r3 != 0) goto L_0x0058
                r3 = r5
                goto L_0x0059
            L_0x0058:
                r3 = r4
            L_0x0059:
                r0 = r3
                kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x00e8 }
                monitor-exit(r1)
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Stream$FramingSink r1 = r1.getSink$okhttp()
                boolean r1 = r1.finished
                if (r1 != 0) goto L_0x00cc
                okio.Buffer r1 = r12.sendBuffer
                long r1 = r1.size()
                r6 = 0
                int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
                if (r1 <= 0) goto L_0x0075
                r1 = r5
                goto L_0x0076
            L_0x0075:
                r1 = r4
            L_0x0076:
                okhttp3.Headers r2 = r12.trailers
                if (r2 == 0) goto L_0x007c
                r2 = r5
                goto L_0x007d
            L_0x007c:
                r2 = r4
            L_0x007d:
                if (r2 == 0) goto L_0x00a7
            L_0x0080:
                okio.Buffer r3 = r12.sendBuffer
                long r8 = r3.size()
                int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r3 <= 0) goto L_0x008e
                r12.emitFrame(r4)
                goto L_0x0080
            L_0x008e:
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Connection r3 = r3.getConnection()
                okhttp3.internal.http2.Http2Stream r4 = okhttp3.internal.http2.Http2Stream.this
                int r4 = r4.getId()
                okhttp3.Headers r6 = r12.trailers
                kotlin.jvm.internal.Intrinsics.checkNotNull(r6)
                java.util.List r6 = okhttp3.internal.Util.toHeaderList(r6)
                r3.writeHeaders$okhttp(r4, r0, r6)
                goto L_0x00cc
            L_0x00a7:
                if (r1 == 0) goto L_0x00b7
            L_0x00a9:
                okio.Buffer r3 = r12.sendBuffer
                long r3 = r3.size()
                int r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
                if (r3 <= 0) goto L_0x00cc
                r12.emitFrame(r5)
                goto L_0x00a9
            L_0x00b7:
                if (r0 == 0) goto L_0x00cc
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Connection r6 = r3.getConnection()
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this
                int r7 = r3.getId()
                r9 = 0
                r10 = 0
                r8 = 1
                r6.writeData(r7, r8, r9, r10)
            L_0x00cc:
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                monitor-enter(r1)
                r2 = 0
                r12.closed = r5     // Catch:{ all -> 0x00e5 }
                kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x00e5 }
                monitor-exit(r1)
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Connection r1 = r1.getConnection()
                r1.flush()
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                r1.cancelStreamIfNecessary$okhttp()
                return
            L_0x00e5:
                r2 = move-exception
                monitor-exit(r1)
                throw r2
            L_0x00e8:
                r2 = move-exception
                monitor-exit(r1)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramingSink.close():void");
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lokhttp3/internal/http2/Http2Stream$Companion;", "", "()V", "EMIT_BUFFER_SIZE", "", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: Http2Stream.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    public final void addBytesToWriteWindow(long delta) {
        this.writeBytesMaximum += delta;
        if (delta > 0) {
            notifyAll();
        }
    }

    public final void checkOutNotClosed$okhttp() throws IOException {
        if (this.sink.getClosed()) {
            throw new IOException("stream closed");
        } else if (this.sink.getFinished()) {
            throw new IOException("stream finished");
        } else if (this.errorCode != null) {
            Throwable th = this.errorException;
            if (th == null) {
                ErrorCode errorCode2 = this.errorCode;
                Intrinsics.checkNotNull(errorCode2);
                th = new StreamResetException(errorCode2);
            }
            throw th;
        }
    }

    public final void waitForIo$okhttp() throws InterruptedIOException {
        try {
            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004J\u0012\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u0014J\b\u0010\b\u001a\u00020\u0004H\u0014¨\u0006\t"}, d2 = {"Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "Lokio/AsyncTimeout;", "(Lokhttp3/internal/http2/Http2Stream;)V", "exitAndThrowIfTimedOut", "", "newTimeoutException", "Ljava/io/IOException;", "cause", "timedOut", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: Http2Stream.kt */
    public final class StreamTimeout extends AsyncTimeout {
        public StreamTimeout() {
        }

        /* access modifiers changed from: protected */
        public void timedOut() {
            Http2Stream.this.closeLater(ErrorCode.CANCEL);
            Http2Stream.this.getConnection().sendDegradedPingLater$okhttp();
        }

        /* access modifiers changed from: protected */
        public IOException newTimeoutException(IOException cause) {
            SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");
            SocketTimeoutException $this$apply = socketTimeoutException;
            if (cause != null) {
                $this$apply.initCause(cause);
            }
            return socketTimeoutException;
        }

        public final void exitAndThrowIfTimedOut() throws IOException {
            if (exit()) {
                throw newTimeoutException((IOException) null);
            }
        }
    }
}
