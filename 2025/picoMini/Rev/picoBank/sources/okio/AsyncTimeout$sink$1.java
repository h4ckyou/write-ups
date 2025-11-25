package okio;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000-\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\b\u0010\u0004\u001a\u00020\u0003H\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\bH\u0016J\u0018\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016¨\u0006\u000e"}, d2 = {"okio/AsyncTimeout$sink$1", "Lokio/Sink;", "close", "", "flush", "timeout", "Lokio/AsyncTimeout;", "toString", "", "write", "source", "Lokio/Buffer;", "byteCount", "", "okio"}, k = 1, mv = {1, 4, 0})
/* compiled from: AsyncTimeout.kt */
public final class AsyncTimeout$sink$1 implements Sink {
    final /* synthetic */ Sink $sink;
    final /* synthetic */ AsyncTimeout this$0;

    AsyncTimeout$sink$1(AsyncTimeout this$02, Sink $captured_local_variable$1) {
        this.this$0 = this$02;
        this.$sink = $captured_local_variable$1;
    }

    public void write(Buffer source, long byteCount) {
        Intrinsics.checkNotNullParameter(source, "source");
        Util.checkOffsetAndCount(source.size(), 0, byteCount);
        long remaining = byteCount;
        while (remaining > 0) {
            long toWrite = 0;
            Segment s = source.head;
            Intrinsics.checkNotNull(s);
            while (true) {
                if (toWrite >= ((long) 65536)) {
                    break;
                }
                toWrite += (long) (s.limit - s.pos);
                if (toWrite >= remaining) {
                    toWrite = remaining;
                    break;
                }
                Segment segment = s.next;
                Intrinsics.checkNotNull(segment);
                s = segment;
            }
            AsyncTimeout this_$iv = this.this$0;
            this_$iv.enter();
            try {
                this.$sink.write(source, toWrite);
                Unit unit = Unit.INSTANCE;
                if (!this_$iv.exit()) {
                    remaining -= toWrite;
                } else {
                    throw this_$iv.access$newTimeoutException((IOException) null);
                }
            } catch (IOException e$iv) {
                throw (!this_$iv.exit() ? e$iv : this_$iv.access$newTimeoutException(e$iv));
            } catch (Throwable th) {
                if (!this_$iv.exit() || 0 == 0) {
                    throw th;
                }
                throw this_$iv.access$newTimeoutException((IOException) null);
            }
        }
    }

    public void flush() {
        AsyncTimeout this_$iv = this.this$0;
        this_$iv.enter();
        try {
            this.$sink.flush();
            Unit unit = Unit.INSTANCE;
            if (this_$iv.exit()) {
                throw this_$iv.access$newTimeoutException((IOException) null);
            }
        } catch (IOException e$iv) {
            throw (!this_$iv.exit() ? e$iv : this_$iv.access$newTimeoutException(e$iv));
        } catch (Throwable th) {
            if (!this_$iv.exit() || 0 == 0) {
                throw th;
            }
            throw this_$iv.access$newTimeoutException((IOException) null);
        }
    }

    public void close() {
        AsyncTimeout this_$iv = this.this$0;
        this_$iv.enter();
        try {
            this.$sink.close();
            Unit unit = Unit.INSTANCE;
            if (this_$iv.exit()) {
                throw this_$iv.access$newTimeoutException((IOException) null);
            }
        } catch (IOException e$iv) {
            throw (!this_$iv.exit() ? e$iv : this_$iv.access$newTimeoutException(e$iv));
        } catch (Throwable th) {
            if (!this_$iv.exit() || 0 == 0) {
                throw th;
            }
            throw this_$iv.access$newTimeoutException((IOException) null);
        }
    }

    public AsyncTimeout timeout() {
        return this.this$0;
    }

    public String toString() {
        return "AsyncTimeout.sink(" + this.$sink + ')';
    }
}
