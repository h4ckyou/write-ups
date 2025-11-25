package okio.internal;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.EOFException;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Typography;
import okhttp3.internal.connection.RealConnection;
import okio.Buffer;
import okio.ByteString;
import okio.Options;
import okio.Platform;
import okio.Segment;
import okio.SegmentPool;
import okio.SegmentedByteString;
import okio.Sink;
import okio.Source;
import okio.Utf8;
import okio.Util;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000v\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\n\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a0\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\bH\u0000\u001a\r\u0010\u0011\u001a\u00020\u0012*\u00020\u0013H\b\u001a\r\u0010\u0014\u001a\u00020\u0005*\u00020\u0013H\b\u001a\r\u0010\u0015\u001a\u00020\u0013*\u00020\u0013H\b\u001a%\u0010\u0016\u001a\u00020\u0013*\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a\u0017\u0010\u001a\u001a\u00020\n*\u00020\u00132\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\b\u001a\u0015\u0010\u001d\u001a\u00020\u001e*\u00020\u00132\u0006\u0010\u001f\u001a\u00020\u0005H\b\u001a\r\u0010 \u001a\u00020\b*\u00020\u0013H\b\u001a%\u0010!\u001a\u00020\u0005*\u00020\u00132\u0006\u0010\"\u001a\u00020\u001e2\u0006\u0010#\u001a\u00020\u00052\u0006\u0010$\u001a\u00020\u0005H\b\u001a\u001d\u0010!\u001a\u00020\u0005*\u00020\u00132\u0006\u0010\u000e\u001a\u00020%2\u0006\u0010#\u001a\u00020\u0005H\b\u001a\u001d\u0010&\u001a\u00020\u0005*\u00020\u00132\u0006\u0010'\u001a\u00020%2\u0006\u0010#\u001a\u00020\u0005H\b\u001a-\u0010(\u001a\u00020\n*\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020%2\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\bH\b\u001a\u0015\u0010)\u001a\u00020\b*\u00020\u00132\u0006\u0010*\u001a\u00020\u0001H\b\u001a%\u0010)\u001a\u00020\b*\u00020\u00132\u0006\u0010*\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\bH\b\u001a\u001d\u0010)\u001a\u00020\u0005*\u00020\u00132\u0006\u0010*\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a\u0015\u0010+\u001a\u00020\u0005*\u00020\u00132\u0006\u0010*\u001a\u00020,H\b\u001a\r\u0010-\u001a\u00020\u001e*\u00020\u0013H\b\u001a\r\u0010.\u001a\u00020\u0001*\u00020\u0013H\b\u001a\u0015\u0010.\u001a\u00020\u0001*\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a\r\u0010/\u001a\u00020%*\u00020\u0013H\b\u001a\u0015\u0010/\u001a\u00020%*\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a\r\u00100\u001a\u00020\u0005*\u00020\u0013H\b\u001a\u0015\u00101\u001a\u00020\u0012*\u00020\u00132\u0006\u0010*\u001a\u00020\u0001H\b\u001a\u001d\u00101\u001a\u00020\u0012*\u00020\u00132\u0006\u0010*\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a\r\u00102\u001a\u00020\u0005*\u00020\u0013H\b\u001a\r\u00103\u001a\u00020\b*\u00020\u0013H\b\u001a\r\u00104\u001a\u00020\u0005*\u00020\u0013H\b\u001a\r\u00105\u001a\u000206*\u00020\u0013H\b\u001a\u0015\u00107\u001a\u000208*\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a\r\u00109\u001a\u00020\b*\u00020\u0013H\b\u001a\u000f\u0010:\u001a\u0004\u0018\u000108*\u00020\u0013H\b\u001a\u0015\u0010;\u001a\u000208*\u00020\u00132\u0006\u0010<\u001a\u00020\u0005H\b\u001a\u0015\u0010=\u001a\u00020\b*\u00020\u00132\u0006\u0010>\u001a\u00020?H\b\u001a\u0015\u0010@\u001a\u00020\u0012*\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a\r\u0010A\u001a\u00020%*\u00020\u0013H\b\u001a\u0015\u0010A\u001a\u00020%*\u00020\u00132\u0006\u0010\u0019\u001a\u00020\bH\b\u001a\u0015\u0010B\u001a\u00020\f*\u00020\u00132\u0006\u0010C\u001a\u00020\bH\b\u001a\u0015\u0010D\u001a\u00020\u0013*\u00020\u00132\u0006\u0010E\u001a\u00020\u0001H\b\u001a%\u0010D\u001a\u00020\u0013*\u00020\u00132\u0006\u0010E\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\bH\b\u001a\u001d\u0010D\u001a\u00020\u0012*\u00020\u00132\u0006\u0010E\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a)\u0010D\u001a\u00020\u0013*\u00020\u00132\u0006\u0010F\u001a\u00020%2\b\b\u0002\u0010\u0018\u001a\u00020\b2\b\b\u0002\u0010\u0019\u001a\u00020\bH\b\u001a\u001d\u0010D\u001a\u00020\u0013*\u00020\u00132\u0006\u0010E\u001a\u00020G2\u0006\u0010\u0019\u001a\u00020\u0005H\b\u001a\u0015\u0010H\u001a\u00020\u0005*\u00020\u00132\u0006\u0010E\u001a\u00020GH\b\u001a\u0015\u0010I\u001a\u00020\u0013*\u00020\u00132\u0006\u0010\"\u001a\u00020\bH\b\u001a\u0015\u0010J\u001a\u00020\u0013*\u00020\u00132\u0006\u0010K\u001a\u00020\u0005H\b\u001a\u0015\u0010L\u001a\u00020\u0013*\u00020\u00132\u0006\u0010K\u001a\u00020\u0005H\b\u001a\u0015\u0010M\u001a\u00020\u0013*\u00020\u00132\u0006\u0010N\u001a\u00020\bH\b\u001a\u0015\u0010O\u001a\u00020\u0013*\u00020\u00132\u0006\u0010K\u001a\u00020\u0005H\b\u001a\u0015\u0010P\u001a\u00020\u0013*\u00020\u00132\u0006\u0010Q\u001a\u00020\bH\b\u001a%\u0010R\u001a\u00020\u0013*\u00020\u00132\u0006\u0010S\u001a\u0002082\u0006\u0010T\u001a\u00020\b2\u0006\u0010U\u001a\u00020\bH\b\u001a\u0015\u0010V\u001a\u00020\u0013*\u00020\u00132\u0006\u0010W\u001a\u00020\bH\b\u001a\u0014\u0010X\u001a\u000208*\u00020\u00132\u0006\u0010Y\u001a\u00020\u0005H\u0000\u001a?\u0010Z\u001a\u0002H[\"\u0004\b\u0000\u0010[*\u00020\u00132\u0006\u0010#\u001a\u00020\u00052\u001a\u0010\\\u001a\u0016\u0012\u0006\u0012\u0004\u0018\u00010\f\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u0002H[0]H\bø\u0001\u0000¢\u0006\u0002\u0010^\u001a\u001e\u0010_\u001a\u00020\b*\u00020\u00132\u0006\u0010>\u001a\u00020?2\b\b\u0002\u0010`\u001a\u00020\nH\u0000\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u000e\u0010\u0004\u001a\u00020\u0005XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0005XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\bXT¢\u0006\u0002\n\u0000\u0002\u0007\n\u0005\b20\u0001¨\u0006a"}, d2 = {"HEX_DIGIT_BYTES", "", "getHEX_DIGIT_BYTES", "()[B", "OVERFLOW_DIGIT_START", "", "OVERFLOW_ZONE", "SEGMENTING_THRESHOLD", "", "rangeEquals", "", "segment", "Lokio/Segment;", "segmentPos", "bytes", "bytesOffset", "bytesLimit", "commonClear", "", "Lokio/Buffer;", "commonCompleteSegmentByteCount", "commonCopy", "commonCopyTo", "out", "offset", "byteCount", "commonEquals", "other", "", "commonGet", "", "pos", "commonHashCode", "commonIndexOf", "b", "fromIndex", "toIndex", "Lokio/ByteString;", "commonIndexOfElement", "targetBytes", "commonRangeEquals", "commonRead", "sink", "commonReadAll", "Lokio/Sink;", "commonReadByte", "commonReadByteArray", "commonReadByteString", "commonReadDecimalLong", "commonReadFully", "commonReadHexadecimalUnsignedLong", "commonReadInt", "commonReadLong", "commonReadShort", "", "commonReadUtf8", "", "commonReadUtf8CodePoint", "commonReadUtf8Line", "commonReadUtf8LineStrict", "limit", "commonSelect", "options", "Lokio/Options;", "commonSkip", "commonSnapshot", "commonWritableSegment", "minimumCapacity", "commonWrite", "source", "byteString", "Lokio/Source;", "commonWriteAll", "commonWriteByte", "commonWriteDecimalLong", "v", "commonWriteHexadecimalUnsignedLong", "commonWriteInt", "i", "commonWriteLong", "commonWriteShort", "s", "commonWriteUtf8", "string", "beginIndex", "endIndex", "commonWriteUtf8CodePoint", "codePoint", "readUtf8Line", "newline", "seek", "T", "lambda", "Lkotlin/Function2;", "(Lokio/Buffer;JLkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "selectPrefix", "selectTruncated", "okio"}, k = 2, mv = {1, 4, 0})
/* compiled from: Buffer.kt */
public final class BufferKt {
    private static final byte[] HEX_DIGIT_BYTES = Platform.asUtf8ToByteArray("0123456789abcdef");
    public static final long OVERFLOW_DIGIT_START = -7;
    public static final long OVERFLOW_ZONE = -922337203685477580L;
    public static final int SEGMENTING_THRESHOLD = 4096;

    public static final byte[] getHEX_DIGIT_BYTES() {
        return HEX_DIGIT_BYTES;
    }

    public static final boolean rangeEquals(Segment segment, int segmentPos, byte[] bytes, int bytesOffset, int bytesLimit) {
        Intrinsics.checkNotNullParameter(segment, "segment");
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        Segment segment2 = segment;
        int segmentPos2 = segmentPos;
        int segmentLimit = segment2.limit;
        byte[] data = segment2.data;
        for (int i = bytesOffset; i < bytesLimit; i++) {
            if (segmentPos2 == segmentLimit) {
                Segment segment3 = segment2.next;
                Intrinsics.checkNotNull(segment3);
                segment2 = segment3;
                data = segment2.data;
                segmentPos2 = segment2.pos;
                segmentLimit = segment2.limit;
            }
            if (data[segmentPos2] != bytes[i]) {
                return false;
            }
            segmentPos2++;
        }
        return true;
    }

    public static final String readUtf8Line(Buffer $this$readUtf8Line, long newline) {
        Intrinsics.checkNotNullParameter($this$readUtf8Line, "$this$readUtf8Line");
        if (newline <= 0 || $this$readUtf8Line.getByte(newline - 1) != ((byte) 13)) {
            String result = $this$readUtf8Line.readUtf8(newline);
            $this$readUtf8Line.skip(1);
            return result;
        }
        String result2 = $this$readUtf8Line.readUtf8(newline - 1);
        $this$readUtf8Line.skip(2);
        return result2;
    }

    public static final <T> T seek(Buffer $this$seek, long fromIndex, Function2<? super Segment, ? super Long, ? extends T> lambda) {
        Intrinsics.checkNotNullParameter($this$seek, "$this$seek");
        Intrinsics.checkNotNullParameter(lambda, "lambda");
        Segment s = $this$seek.head;
        if (s == null) {
            return lambda.invoke(null, -1L);
        }
        if ($this$seek.size() - fromIndex < fromIndex) {
            long offset = $this$seek.size();
            while (offset > fromIndex) {
                Segment segment = s.prev;
                Intrinsics.checkNotNull(segment);
                s = segment;
                offset -= (long) (s.limit - s.pos);
            }
            return lambda.invoke(s, Long.valueOf(offset));
        }
        long offset2 = 0;
        while (true) {
            long nextOffset = ((long) (s.limit - s.pos)) + offset2;
            if (nextOffset > fromIndex) {
                return lambda.invoke(s, Long.valueOf(offset2));
            }
            Segment segment2 = s.next;
            Intrinsics.checkNotNull(segment2);
            s = segment2;
            offset2 = nextOffset;
        }
    }

    public static /* synthetic */ int selectPrefix$default(Buffer buffer, Options options, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return selectPrefix(buffer, options, z);
    }

    public static final int selectPrefix(Buffer $this$selectPrefix, Options options, boolean selectTruncated) {
        int nextStep;
        int pos;
        Buffer buffer = $this$selectPrefix;
        Intrinsics.checkNotNullParameter(buffer, "$this$selectPrefix");
        Intrinsics.checkNotNullParameter(options, "options");
        Segment head = buffer.head;
        int i = -1;
        if (head == null) {
            return selectTruncated ? -2 : -1;
        }
        Segment s = head;
        byte[] data = head.data;
        int pos2 = head.pos;
        int limit = head.limit;
        int[] trie = options.getTrie$okio();
        int scanOrSelect = 0;
        int prefixIndex = -1;
        loop0:
        while (true) {
            int triePos = scanOrSelect + 1;
            int scanOrSelect2 = trie[scanOrSelect];
            int triePos2 = triePos + 1;
            int possiblePrefixIndex = trie[triePos];
            if (possiblePrefixIndex != i) {
                prefixIndex = possiblePrefixIndex;
            }
            if (s == null) {
                break;
            }
            if (scanOrSelect2 < 0) {
                int trieLimit = triePos2 + (scanOrSelect2 * -1);
                while (true) {
                    int pos3 = pos2 + 1;
                    int triePos3 = triePos2 + 1;
                    if ((data[pos2] & 255) != trie[triePos2]) {
                        return prefixIndex;
                    }
                    boolean scanComplete = triePos3 == trieLimit;
                    if (pos3 == limit) {
                        Intrinsics.checkNotNull(s);
                        Segment segment = s.next;
                        Intrinsics.checkNotNull(segment);
                        s = segment;
                        pos = s.pos;
                        data = s.data;
                        limit = s.limit;
                        if (s == head) {
                            if (!scanComplete) {
                                int i2 = pos;
                                int i3 = triePos3;
                                break loop0;
                            }
                            s = null;
                        }
                    } else {
                        pos = pos3;
                    }
                    if (scanComplete) {
                        nextStep = trie[triePos3];
                        pos2 = pos;
                        break;
                    }
                    pos2 = pos;
                    triePos2 = triePos3;
                    Buffer buffer2 = $this$selectPrefix;
                }
            } else {
                int selectChoiceCount = scanOrSelect2;
                int pos4 = pos2 + 1;
                int $this$and$iv = data[pos2] & 255;
                int selectLimit = triePos2 + selectChoiceCount;
                while (triePos2 != selectLimit) {
                    if ($this$and$iv == trie[triePos2]) {
                        nextStep = trie[triePos2 + selectChoiceCount];
                        if (pos4 == limit) {
                            Segment segment2 = s.next;
                            Intrinsics.checkNotNull(segment2);
                            s = segment2;
                            int pos5 = s.pos;
                            data = s.data;
                            limit = s.limit;
                            if (s == head) {
                                s = null;
                                pos2 = pos5;
                                int pos6 = triePos2;
                            } else {
                                pos2 = pos5;
                                int pos7 = triePos2;
                            }
                        } else {
                            pos2 = pos4;
                            int pos8 = triePos2;
                        }
                    } else {
                        triePos2++;
                    }
                }
                return prefixIndex;
            }
            if (nextStep >= 0) {
                return nextStep;
            }
            scanOrSelect = -nextStep;
            i = -1;
            Buffer buffer3 = $this$selectPrefix;
        }
        if (selectTruncated) {
            return -2;
        }
        return prefixIndex;
    }

    public static final Buffer commonCopyTo(Buffer $this$commonCopyTo, Buffer out, long offset, long byteCount) {
        Buffer buffer = $this$commonCopyTo;
        Buffer buffer2 = out;
        Intrinsics.checkNotNullParameter($this$commonCopyTo, "$this$commonCopyTo");
        Intrinsics.checkNotNullParameter(out, "out");
        long offset2 = offset;
        long byteCount2 = byteCount;
        Util.checkOffsetAndCount($this$commonCopyTo.size(), offset2, byteCount2);
        if (byteCount2 == 0) {
            return buffer;
        }
        out.setSize$okio(out.size() + byteCount2);
        Segment s = buffer.head;
        while (true) {
            Intrinsics.checkNotNull(s);
            if (offset2 < ((long) (s.limit - s.pos))) {
                break;
            }
            offset2 -= (long) (s.limit - s.pos);
            s = s.next;
        }
        while (byteCount2 > 0) {
            Intrinsics.checkNotNull(s);
            Segment copy = s.sharedCopy();
            copy.pos += (int) offset2;
            copy.limit = Math.min(copy.pos + ((int) byteCount2), copy.limit);
            if (buffer2.head == null) {
                copy.prev = copy;
                copy.next = copy.prev;
                buffer2.head = copy.next;
            } else {
                Segment segment = buffer2.head;
                Intrinsics.checkNotNull(segment);
                Segment segment2 = segment.prev;
                Intrinsics.checkNotNull(segment2);
                segment2.push(copy);
            }
            byteCount2 -= (long) (copy.limit - copy.pos);
            offset2 = 0;
            s = s.next;
        }
        return buffer;
    }

    public static final long commonCompleteSegmentByteCount(Buffer $this$commonCompleteSegmentByteCount) {
        Intrinsics.checkNotNullParameter($this$commonCompleteSegmentByteCount, "$this$commonCompleteSegmentByteCount");
        long result = $this$commonCompleteSegmentByteCount.size();
        if (result == 0) {
            return 0;
        }
        Segment segment = $this$commonCompleteSegmentByteCount.head;
        Intrinsics.checkNotNull(segment);
        Segment tail = segment.prev;
        Intrinsics.checkNotNull(tail);
        if (tail.limit >= 8192 || !tail.owner) {
            return result;
        }
        return result - ((long) (tail.limit - tail.pos));
    }

    public static final byte commonReadByte(Buffer $this$commonReadByte) {
        Intrinsics.checkNotNullParameter($this$commonReadByte, "$this$commonReadByte");
        if ($this$commonReadByte.size() != 0) {
            Segment segment = $this$commonReadByte.head;
            Intrinsics.checkNotNull(segment);
            int pos = segment.pos;
            int limit = segment.limit;
            int pos2 = pos + 1;
            byte pos3 = segment.data[pos];
            $this$commonReadByte.setSize$okio($this$commonReadByte.size() - 1);
            if (pos2 == limit) {
                $this$commonReadByte.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = pos2;
            }
            return pos3;
        }
        throw new EOFException();
    }

    public static final short commonReadShort(Buffer $this$commonReadShort) {
        Intrinsics.checkNotNullParameter($this$commonReadShort, "$this$commonReadShort");
        if ($this$commonReadShort.size() >= 2) {
            Segment segment = $this$commonReadShort.head;
            Intrinsics.checkNotNull(segment);
            int pos = segment.pos;
            int limit = segment.limit;
            if (limit - pos < 2) {
                return (short) ((($this$commonReadShort.readByte() & UByte.MAX_VALUE) << 8) | ($this$commonReadShort.readByte() & 255));
            }
            byte[] data = segment.data;
            int pos2 = pos + 1;
            int pos3 = pos2 + 1;
            int s = ((data[pos] & UByte.MAX_VALUE) << 8) | (data[pos2] & 255);
            $this$commonReadShort.setSize$okio($this$commonReadShort.size() - 2);
            if (pos3 == limit) {
                $this$commonReadShort.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = pos3;
            }
            return (short) s;
        }
        throw new EOFException();
    }

    public static final int commonReadInt(Buffer $this$commonReadInt) {
        Intrinsics.checkNotNullParameter($this$commonReadInt, "$this$commonReadInt");
        if ($this$commonReadInt.size() >= 4) {
            Segment segment = $this$commonReadInt.head;
            Intrinsics.checkNotNull(segment);
            int pos = segment.pos;
            int limit = segment.limit;
            if (((long) (limit - pos)) < 4) {
                return (($this$commonReadInt.readByte() & UByte.MAX_VALUE) << 24) | (($this$commonReadInt.readByte() & UByte.MAX_VALUE) << 16) | (($this$commonReadInt.readByte() & UByte.MAX_VALUE) << 8) | ($this$commonReadInt.readByte() & UByte.MAX_VALUE);
            }
            byte[] data = segment.data;
            int pos2 = pos + 1;
            int pos3 = pos2 + 1;
            int i = ((data[pos] & UByte.MAX_VALUE) << 24) | ((data[pos2] & UByte.MAX_VALUE) << 16);
            int pos4 = pos3 + 1;
            int i2 = i | ((data[pos3] & UByte.MAX_VALUE) << 8);
            int pos5 = pos4 + 1;
            int i3 = i2 | (data[pos4] & 255);
            $this$commonReadInt.setSize$okio($this$commonReadInt.size() - 4);
            if (pos5 == limit) {
                $this$commonReadInt.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = pos5;
            }
            return i3;
        }
        throw new EOFException();
    }

    public static final long commonReadLong(Buffer $this$commonReadLong) {
        Buffer buffer = $this$commonReadLong;
        Intrinsics.checkNotNullParameter($this$commonReadLong, "$this$commonReadLong");
        if ($this$commonReadLong.size() >= 8) {
            Segment segment = buffer.head;
            Intrinsics.checkNotNull(segment);
            int pos = segment.pos;
            int limit = segment.limit;
            if (((long) (limit - pos)) < 8) {
                return ((((long) $this$commonReadLong.readInt()) & 4294967295L) << 32) | (((long) $this$commonReadLong.readInt()) & 4294967295L);
            }
            byte[] data = segment.data;
            int pos2 = pos + 1;
            int pos3 = pos2 + 1;
            long j = (((long) data[pos2]) & 255) << 48;
            int pos4 = pos3 + 1;
            int pos5 = pos4 + 1;
            int pos6 = pos5 + 1;
            int pos7 = pos6 + 1;
            long j2 = j | ((255 & ((long) data[pos])) << 56) | ((255 & ((long) data[pos3])) << 40) | ((((long) data[pos4]) & 255) << 32) | ((255 & ((long) data[pos5])) << 24) | ((((long) data[pos6]) & 255) << 16);
            int pos8 = pos7 + 1;
            int pos9 = pos8 + 1;
            long v = j2 | ((255 & ((long) data[pos7])) << 8) | (((long) data[pos8]) & 255);
            $this$commonReadLong.setSize$okio($this$commonReadLong.size() - 8);
            if (pos9 == limit) {
                buffer.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = pos9;
            }
            return v;
        }
        throw new EOFException();
    }

    public static final byte commonGet(Buffer $this$commonGet, long pos) {
        Intrinsics.checkNotNullParameter($this$commonGet, "$this$commonGet");
        Util.checkOffsetAndCount($this$commonGet.size(), pos, 1);
        Buffer $this$seek$iv = $this$commonGet;
        Segment s$iv = $this$seek$iv.head;
        if (s$iv == null) {
            Segment s = null;
            Intrinsics.checkNotNull(s);
            return s.data[(int) ((((long) s.pos) + pos) - -1)];
        } else if ($this$seek$iv.size() - pos < pos) {
            long offset$iv = $this$seek$iv.size();
            while (offset$iv > pos) {
                Segment segment = s$iv.prev;
                Intrinsics.checkNotNull(segment);
                s$iv = segment;
                offset$iv -= (long) (s$iv.limit - s$iv.pos);
            }
            Segment s2 = s$iv;
            Intrinsics.checkNotNull(s2);
            return s2.data[(int) ((((long) s2.pos) + pos) - offset$iv)];
        } else {
            long offset$iv2 = 0;
            while (true) {
                long nextOffset$iv = ((long) (s$iv.limit - s$iv.pos)) + offset$iv2;
                if (nextOffset$iv > pos) {
                    Segment s3 = s$iv;
                    Intrinsics.checkNotNull(s3);
                    return s3.data[(int) ((((long) s3.pos) + pos) - offset$iv2)];
                }
                Segment segment2 = s$iv.next;
                Intrinsics.checkNotNull(segment2);
                s$iv = segment2;
                offset$iv2 = nextOffset$iv;
            }
        }
    }

    public static final void commonClear(Buffer $this$commonClear) {
        Intrinsics.checkNotNullParameter($this$commonClear, "$this$commonClear");
        $this$commonClear.skip($this$commonClear.size());
    }

    public static final void commonSkip(Buffer $this$commonSkip, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonSkip, "$this$commonSkip");
        long byteCount2 = byteCount;
        while (byteCount2 > 0) {
            Segment head = $this$commonSkip.head;
            if (head != null) {
                int b$iv = (int) Math.min(byteCount2, (long) (head.limit - head.pos));
                $this$commonSkip.setSize$okio($this$commonSkip.size() - ((long) b$iv));
                byteCount2 -= (long) b$iv;
                head.pos += b$iv;
                if (head.pos == head.limit) {
                    $this$commonSkip.head = head.pop();
                    SegmentPool.recycle(head);
                }
            } else {
                throw new EOFException();
            }
        }
    }

    public static /* synthetic */ Buffer commonWrite$default(Buffer $this$commonWrite, ByteString byteString, int offset, int byteCount, int i, Object obj) {
        if ((i & 2) != 0) {
            offset = 0;
        }
        if ((i & 4) != 0) {
            byteCount = byteString.size();
        }
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(byteString, "byteString");
        byteString.write$okio($this$commonWrite, offset, byteCount);
        return $this$commonWrite;
    }

    public static final Buffer commonWrite(Buffer $this$commonWrite, ByteString byteString, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(byteString, "byteString");
        byteString.write$okio($this$commonWrite, offset, byteCount);
        return $this$commonWrite;
    }

    public static final Buffer commonWriteDecimalLong(Buffer $this$commonWriteDecimalLong, long v) {
        int width;
        Buffer buffer = $this$commonWriteDecimalLong;
        Intrinsics.checkNotNullParameter(buffer, "$this$commonWriteDecimalLong");
        long v2 = v;
        if (v2 == 0) {
            return buffer.writeByte(48);
        }
        boolean negative = false;
        if (v2 < 0) {
            v2 = -v2;
            if (v2 < 0) {
                return buffer.writeUtf8("-9223372036854775808");
            }
            negative = true;
        }
        if (v2 < 100000000) {
            width = v2 < 10000 ? v2 < 100 ? v2 < 10 ? 1 : 2 : v2 < 1000 ? 3 : 4 : v2 < 1000000 ? v2 < 100000 ? 5 : 6 : v2 < 10000000 ? 7 : 8;
        } else if (v2 < 1000000000000L) {
            width = v2 < RealConnection.IDLE_CONNECTION_HEALTHY_NS ? v2 < 1000000000 ? 9 : 10 : v2 < 100000000000L ? 11 : 12;
        } else if (v2 < 1000000000000000L) {
            width = v2 < 10000000000000L ? 13 : v2 < 100000000000000L ? 14 : 15;
        } else if (v2 < 100000000000000000L) {
            width = v2 < 10000000000000000L ? 16 : 17;
        } else if (v2 < 1000000000000000000L) {
            width = 18;
        } else {
            width = 19;
        }
        if (negative) {
            width++;
        }
        Segment tail = buffer.writableSegment$okio(width);
        byte[] data = tail.data;
        int pos = tail.limit + width;
        while (v2 != 0) {
            long j = (long) 10;
            pos--;
            data[pos] = getHEX_DIGIT_BYTES()[(int) (v2 % j)];
            v2 /= j;
        }
        if (negative) {
            data[pos - 1] = (byte) 45;
        }
        tail.limit += width;
        buffer.setSize$okio($this$commonWriteDecimalLong.size() + ((long) width));
        return buffer;
    }

    public static final Buffer commonWriteHexadecimalUnsignedLong(Buffer $this$commonWriteHexadecimalUnsignedLong, long v) {
        Buffer buffer = $this$commonWriteHexadecimalUnsignedLong;
        Intrinsics.checkNotNullParameter(buffer, "$this$commonWriteHexadecimalUnsignedLong");
        long v2 = v;
        if (v2 == 0) {
            return buffer.writeByte(48);
        }
        long x = v2;
        long x2 = x | (x >>> 1);
        long x3 = x2 | (x2 >>> 2);
        long x4 = x3 | (x3 >>> 4);
        long x5 = x4 | (x4 >>> 8);
        long x6 = x5 | (x5 >>> 16);
        long x7 = x6 | (x6 >>> 32);
        long x8 = x7 - ((x7 >>> 1) & 6148914691236517205L);
        long x9 = ((x8 >>> 2) & 3689348814741910323L) + (3689348814741910323L & x8);
        long x10 = ((x9 >>> 4) + x9) & 1085102592571150095L;
        long x11 = x10 + (x10 >>> 8);
        long x12 = x11 + (x11 >>> 16);
        int width = (int) ((((long) 3) + ((x12 & 63) + (63 & (x12 >>> 32)))) / ((long) 4));
        Segment tail = buffer.writableSegment$okio(width);
        byte[] data = tail.data;
        int start = tail.limit;
        for (int pos = (tail.limit + width) - 1; pos >= start; pos--) {
            data[pos] = getHEX_DIGIT_BYTES()[(int) (15 & v2)];
            v2 >>>= 4;
        }
        tail.limit += width;
        buffer.setSize$okio($this$commonWriteHexadecimalUnsignedLong.size() + ((long) width));
        return buffer;
    }

    public static final Segment commonWritableSegment(Buffer $this$commonWritableSegment, int minimumCapacity) {
        Intrinsics.checkNotNullParameter($this$commonWritableSegment, "$this$commonWritableSegment");
        boolean z = true;
        if (minimumCapacity < 1 || minimumCapacity > 8192) {
            z = false;
        }
        if (!z) {
            throw new IllegalArgumentException("unexpected capacity".toString());
        } else if ($this$commonWritableSegment.head == null) {
            Segment result = SegmentPool.take();
            $this$commonWritableSegment.head = result;
            result.prev = result;
            result.next = result;
            return result;
        } else {
            Segment segment = $this$commonWritableSegment.head;
            Intrinsics.checkNotNull(segment);
            Segment tail = segment.prev;
            Intrinsics.checkNotNull(tail);
            if (tail.limit + minimumCapacity > 8192 || !tail.owner) {
                return tail.push(SegmentPool.take());
            }
            return tail;
        }
    }

    public static final Buffer commonWrite(Buffer $this$commonWrite, byte[] source) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(source, "source");
        return $this$commonWrite.write(source, 0, source.length);
    }

    public static final Buffer commonWrite(Buffer $this$commonWrite, byte[] source, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(source, "source");
        int offset2 = offset;
        Util.checkOffsetAndCount((long) source.length, (long) offset2, (long) byteCount);
        int limit = offset2 + byteCount;
        while (offset2 < limit) {
            Segment tail = $this$commonWrite.writableSegment$okio(1);
            int toCopy = Math.min(limit - offset2, 8192 - tail.limit);
            ArraysKt.copyInto(source, tail.data, tail.limit, offset2, offset2 + toCopy);
            offset2 += toCopy;
            tail.limit += toCopy;
        }
        $this$commonWrite.setSize$okio($this$commonWrite.size() + ((long) byteCount));
        return $this$commonWrite;
    }

    public static final byte[] commonReadByteArray(Buffer $this$commonReadByteArray) {
        Intrinsics.checkNotNullParameter($this$commonReadByteArray, "$this$commonReadByteArray");
        return $this$commonReadByteArray.readByteArray($this$commonReadByteArray.size());
    }

    public static final byte[] commonReadByteArray(Buffer $this$commonReadByteArray, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonReadByteArray, "$this$commonReadByteArray");
        if (!(byteCount >= 0 && byteCount <= ((long) Integer.MAX_VALUE))) {
            throw new IllegalArgumentException(("byteCount: " + byteCount).toString());
        } else if ($this$commonReadByteArray.size() >= byteCount) {
            byte[] result = new byte[((int) byteCount)];
            $this$commonReadByteArray.readFully(result);
            return result;
        } else {
            throw new EOFException();
        }
    }

    public static final int commonRead(Buffer $this$commonRead, byte[] sink) {
        Intrinsics.checkNotNullParameter($this$commonRead, "$this$commonRead");
        Intrinsics.checkNotNullParameter(sink, "sink");
        return $this$commonRead.read(sink, 0, sink.length);
    }

    public static final void commonReadFully(Buffer $this$commonReadFully, byte[] sink) {
        Intrinsics.checkNotNullParameter($this$commonReadFully, "$this$commonReadFully");
        Intrinsics.checkNotNullParameter(sink, "sink");
        int offset = 0;
        while (offset < sink.length) {
            int read = $this$commonReadFully.read(sink, offset, sink.length - offset);
            if (read != -1) {
                offset += read;
            } else {
                throw new EOFException();
            }
        }
    }

    public static final int commonRead(Buffer $this$commonRead, byte[] sink, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRead, "$this$commonRead");
        Intrinsics.checkNotNullParameter(sink, "sink");
        Util.checkOffsetAndCount((long) sink.length, (long) offset, (long) byteCount);
        Segment s = $this$commonRead.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(byteCount, s.limit - s.pos);
        ArraysKt.copyInto(s.data, sink, offset, s.pos, s.pos + toCopy);
        s.pos += toCopy;
        $this$commonRead.setSize$okio($this$commonRead.size() - ((long) toCopy));
        if (s.pos == s.limit) {
            $this$commonRead.head = s.pop();
            SegmentPool.recycle(s);
        }
        return toCopy;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00d1, code lost:
        if (r11 != r12) goto L_0x00df;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00d3, code lost:
        r1 = r18;
        r1.head = r9.pop();
        okio.SegmentPool.recycle(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00df, code lost:
        r1 = r18;
        r9.pos = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00e4, code lost:
        if (r6 != false) goto L_0x00ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00e8, code lost:
        if (r1.head != null) goto L_0x00eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ef, code lost:
        r1.setSize$okio(r18.size() - ((long) r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00f8, code lost:
        if (r5 == false) goto L_0x00fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        return -r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final long commonReadDecimalLong(okio.Buffer r18) {
        /*
            r0 = r18
            r1 = 0
            java.lang.String r2 = "$this$commonReadDecimalLong"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r2)
            long r2 = r18.size()
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x00fe
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = -7
        L_0x0019:
            okio.Segment r9 = r0.head
            kotlin.jvm.internal.Intrinsics.checkNotNull(r9)
            byte[] r10 = r9.data
            int r11 = r9.pos
            int r12 = r9.limit
        L_0x0025:
            if (r11 >= r12) goto L_0x00cc
            byte r13 = r10[r11]
            r14 = 48
            byte r14 = (byte) r14
            if (r13 < r14) goto L_0x0089
            r15 = 57
            byte r15 = (byte) r15
            if (r13 > r15) goto L_0x0089
            int r14 = r14 - r13
            r15 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            int r17 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r17 < 0) goto L_0x0053
            int r15 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r15 != 0) goto L_0x0048
            r15 = r1
            long r0 = (long) r14
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 >= 0) goto L_0x0049
            goto L_0x0054
        L_0x0048:
            r15 = r1
        L_0x0049:
            r0 = 10
            long r2 = r2 * r0
            long r0 = (long) r14
            long r2 = r2 + r0
            r16 = r6
            r17 = r10
            goto L_0x0099
        L_0x0053:
            r15 = r1
        L_0x0054:
            okio.Buffer r0 = new okio.Buffer
            r0.<init>()
            okio.Buffer r0 = r0.writeDecimalLong((long) r2)
            okio.Buffer r0 = r0.writeByte((int) r13)
            if (r5 != 0) goto L_0x0066
            r0.readByte()
        L_0x0066:
            java.lang.NumberFormatException r1 = new java.lang.NumberFormatException
            r16 = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r17 = r10
            java.lang.String r10 = "Number too large: "
            java.lang.StringBuilder r6 = r6.append(r10)
            java.lang.String r10 = r0.readUtf8()
            java.lang.StringBuilder r6 = r6.append(r10)
            java.lang.String r6 = r6.toString()
            r1.<init>(r6)
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            throw r1
        L_0x0089:
            r15 = r1
            r16 = r6
            r17 = r10
            r0 = 45
            byte r0 = (byte) r0
            if (r13 != r0) goto L_0x00a8
            if (r4 != 0) goto L_0x00a8
            r5 = 1
            r0 = 1
            long r7 = r7 - r0
        L_0x0099:
            int r11 = r11 + 1
            int r4 = r4 + 1
            r0 = r18
            r1 = r15
            r6 = r16
            r10 = r17
            goto L_0x0025
        L_0x00a8:
            if (r4 == 0) goto L_0x00ad
            r0 = 1
            r6 = r0
            goto L_0x00d1
        L_0x00ad:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "Expected leading [0-9] or '-' character but was 0x"
            java.lang.StringBuilder r1 = r1.append(r6)
            java.lang.String r6 = okio.Util.toHexString((byte) r13)
            java.lang.StringBuilder r1 = r1.append(r6)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            throw r0
        L_0x00cc:
            r15 = r1
            r16 = r6
            r17 = r10
        L_0x00d1:
            if (r11 != r12) goto L_0x00df
            okio.Segment r0 = r9.pop()
            r1 = r18
            r1.head = r0
            okio.SegmentPool.recycle(r9)
            goto L_0x00e3
        L_0x00df:
            r1 = r18
            r9.pos = r11
        L_0x00e3:
            if (r6 != 0) goto L_0x00ef
            okio.Segment r0 = r1.head
            if (r0 != 0) goto L_0x00eb
            goto L_0x00ef
        L_0x00eb:
            r0 = r1
            r1 = r15
            goto L_0x0019
        L_0x00ef:
            long r9 = r18.size()
            long r11 = (long) r4
            long r9 = r9 - r11
            r1.setSize$okio(r9)
            if (r5 == 0) goto L_0x00fc
            r9 = r2
            goto L_0x00fd
        L_0x00fc:
            long r9 = -r2
        L_0x00fd:
            return r9
        L_0x00fe:
            r15 = r1
            r1 = r0
            java.io.EOFException r0 = new java.io.EOFException
            r0.<init>()
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal.BufferKt.commonReadDecimalLong(okio.Buffer):long");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00b0, code lost:
        if (r9 != r10) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b2, code lost:
        r15.head = r7.pop();
        okio.SegmentPool.recycle(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00bc, code lost:
        r7.pos = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00bf, code lost:
        if (r6 != false) goto L_0x00c5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0091 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final long commonReadHexadecimalUnsignedLong(okio.Buffer r15) {
        /*
            r0 = 0
            java.lang.String r1 = "$this$commonReadHexadecimalUnsignedLong"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r15, r1)
            long r1 = r15.size()
            r3 = 0
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x00cf
            r1 = 0
            r5 = 0
            r6 = 0
        L_0x0014:
            okio.Segment r7 = r15.head
            kotlin.jvm.internal.Intrinsics.checkNotNull(r7)
            byte[] r8 = r7.data
            int r9 = r7.pos
            int r10 = r7.limit
        L_0x0020:
            if (r9 >= r10) goto L_0x00b0
            r11 = 0
            byte r12 = r8[r9]
            r13 = 48
            byte r13 = (byte) r13
            if (r12 < r13) goto L_0x0032
            r14 = 57
            byte r14 = (byte) r14
            if (r12 > r14) goto L_0x0032
            int r11 = r12 - r13
            goto L_0x004f
        L_0x0032:
            r13 = 97
            byte r13 = (byte) r13
            if (r12 < r13) goto L_0x0041
            r14 = 102(0x66, float:1.43E-43)
            byte r14 = (byte) r14
            if (r12 > r14) goto L_0x0041
            int r13 = r12 - r13
            int r11 = r13 + 10
            goto L_0x004f
        L_0x0041:
            r13 = 65
            byte r13 = (byte) r13
            if (r12 < r13) goto L_0x008d
            r14 = 70
            byte r14 = (byte) r14
            if (r12 > r14) goto L_0x008d
            int r13 = r12 - r13
            int r11 = r13 + 10
        L_0x004f:
            r13 = -1152921504606846976(0xf000000000000000, double:-3.105036184601418E231)
            long r13 = r13 & r1
            int r13 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r13 != 0) goto L_0x0061
            r13 = 4
            long r1 = r1 << r13
            long r13 = (long) r11
            long r1 = r1 | r13
            int r9 = r9 + 1
            int r5 = r5 + 1
            goto L_0x0020
        L_0x0061:
            okio.Buffer r3 = new okio.Buffer
            r3.<init>()
            okio.Buffer r3 = r3.writeHexadecimalUnsignedLong((long) r1)
            okio.Buffer r3 = r3.writeByte((int) r12)
            java.lang.NumberFormatException r4 = new java.lang.NumberFormatException
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "Number too large: "
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r14 = r3.readUtf8()
            java.lang.StringBuilder r13 = r13.append(r14)
            java.lang.String r13 = r13.toString()
            r4.<init>(r13)
            java.lang.Throwable r4 = (java.lang.Throwable) r4
            throw r4
        L_0x008d:
            if (r5 == 0) goto L_0x0091
            r6 = 1
            goto L_0x00b0
        L_0x0091:
            java.lang.NumberFormatException r3 = new java.lang.NumberFormatException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r13 = "Expected leading [0-9a-fA-F] character but was 0x"
            java.lang.StringBuilder r4 = r4.append(r13)
            java.lang.String r13 = okio.Util.toHexString((byte) r12)
            java.lang.StringBuilder r4 = r4.append(r13)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            java.lang.Throwable r3 = (java.lang.Throwable) r3
            throw r3
        L_0x00b0:
            if (r9 != r10) goto L_0x00bc
            okio.Segment r11 = r7.pop()
            r15.head = r11
            okio.SegmentPool.recycle(r7)
            goto L_0x00be
        L_0x00bc:
            r7.pos = r9
        L_0x00be:
            if (r6 != 0) goto L_0x00c5
            okio.Segment r11 = r15.head
            if (r11 != 0) goto L_0x0014
        L_0x00c5:
            long r3 = r15.size()
            long r7 = (long) r5
            long r3 = r3 - r7
            r15.setSize$okio(r3)
            return r1
        L_0x00cf:
            java.io.EOFException r1 = new java.io.EOFException
            r1.<init>()
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal.BufferKt.commonReadHexadecimalUnsignedLong(okio.Buffer):long");
    }

    public static final ByteString commonReadByteString(Buffer $this$commonReadByteString) {
        Intrinsics.checkNotNullParameter($this$commonReadByteString, "$this$commonReadByteString");
        return $this$commonReadByteString.readByteString($this$commonReadByteString.size());
    }

    public static final ByteString commonReadByteString(Buffer $this$commonReadByteString, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonReadByteString, "$this$commonReadByteString");
        if (!(byteCount >= 0 && byteCount <= ((long) Integer.MAX_VALUE))) {
            throw new IllegalArgumentException(("byteCount: " + byteCount).toString());
        } else if ($this$commonReadByteString.size() < byteCount) {
            throw new EOFException();
        } else if (byteCount < ((long) 4096)) {
            return new ByteString($this$commonReadByteString.readByteArray(byteCount));
        } else {
            ByteString snapshot = $this$commonReadByteString.snapshot((int) byteCount);
            ByteString byteString = snapshot;
            $this$commonReadByteString.skip(byteCount);
            return snapshot;
        }
    }

    public static final int commonSelect(Buffer $this$commonSelect, Options options) {
        Intrinsics.checkNotNullParameter($this$commonSelect, "$this$commonSelect");
        Intrinsics.checkNotNullParameter(options, "options");
        int index = selectPrefix$default($this$commonSelect, options, false, 2, (Object) null);
        if (index == -1) {
            return -1;
        }
        $this$commonSelect.skip((long) options.getByteStrings$okio()[index].size());
        return index;
    }

    public static final void commonReadFully(Buffer $this$commonReadFully, Buffer sink, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonReadFully, "$this$commonReadFully");
        Intrinsics.checkNotNullParameter(sink, "sink");
        if ($this$commonReadFully.size() >= byteCount) {
            sink.write($this$commonReadFully, byteCount);
        } else {
            sink.write($this$commonReadFully, $this$commonReadFully.size());
            throw new EOFException();
        }
    }

    public static final long commonReadAll(Buffer $this$commonReadAll, Sink sink) {
        Intrinsics.checkNotNullParameter($this$commonReadAll, "$this$commonReadAll");
        Intrinsics.checkNotNullParameter(sink, "sink");
        long byteCount = $this$commonReadAll.size();
        if (byteCount > 0) {
            sink.write($this$commonReadAll, byteCount);
        }
        return byteCount;
    }

    public static final String commonReadUtf8(Buffer $this$commonReadUtf8, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8, "$this$commonReadUtf8");
        if (!(byteCount >= 0 && byteCount <= ((long) Integer.MAX_VALUE))) {
            throw new IllegalArgumentException(("byteCount: " + byteCount).toString());
        } else if ($this$commonReadUtf8.size() < byteCount) {
            throw new EOFException();
        } else if (byteCount == 0) {
            return "";
        } else {
            Segment s = $this$commonReadUtf8.head;
            Intrinsics.checkNotNull(s);
            if (((long) s.pos) + byteCount > ((long) s.limit)) {
                return _Utf8Kt.commonToUtf8String$default($this$commonReadUtf8.readByteArray(byteCount), 0, 0, 3, (Object) null);
            }
            String result = _Utf8Kt.commonToUtf8String(s.data, s.pos, s.pos + ((int) byteCount));
            s.pos += (int) byteCount;
            $this$commonReadUtf8.setSize$okio($this$commonReadUtf8.size() - byteCount);
            if (s.pos == s.limit) {
                $this$commonReadUtf8.head = s.pop();
                SegmentPool.recycle(s);
            }
            return result;
        }
    }

    public static final String commonReadUtf8Line(Buffer $this$commonReadUtf8Line) {
        Intrinsics.checkNotNullParameter($this$commonReadUtf8Line, "$this$commonReadUtf8Line");
        long newline = $this$commonReadUtf8Line.indexOf((byte) 10);
        if (newline != -1) {
            return readUtf8Line($this$commonReadUtf8Line, newline);
        }
        if ($this$commonReadUtf8Line.size() != 0) {
            return $this$commonReadUtf8Line.readUtf8($this$commonReadUtf8Line.size());
        }
        return null;
    }

    public static final String commonReadUtf8LineStrict(Buffer $this$commonReadUtf8LineStrict, long limit) {
        Buffer buffer = $this$commonReadUtf8LineStrict;
        long j = limit;
        Intrinsics.checkNotNullParameter(buffer, "$this$commonReadUtf8LineStrict");
        if (j >= 0) {
            long j2 = Long.MAX_VALUE;
            if (j != Long.MAX_VALUE) {
                j2 = j + 1;
            }
            long scanLength = j2;
            byte b = (byte) 10;
            long newline = $this$commonReadUtf8LineStrict.indexOf(b, 0, scanLength);
            if (newline != -1) {
                return readUtf8Line(buffer, newline);
            }
            if (scanLength < $this$commonReadUtf8LineStrict.size() && buffer.getByte(scanLength - 1) == ((byte) 13) && buffer.getByte(scanLength) == b) {
                return readUtf8Line(buffer, scanLength);
            }
            Buffer data = new Buffer();
            long j3 = newline;
            $this$commonReadUtf8LineStrict.copyTo(data, 0, Math.min((long) 32, $this$commonReadUtf8LineStrict.size()));
            throw new EOFException("\\n not found: limit=" + Math.min($this$commonReadUtf8LineStrict.size(), j) + " content=" + data.readByteString().hex() + Typography.ellipsis);
        }
        throw new IllegalArgumentException(("limit < 0: " + j).toString());
    }

    public static final int commonReadUtf8CodePoint(Buffer $this$commonReadUtf8CodePoint) {
        int min;
        int byteCount;
        int codePoint;
        Intrinsics.checkNotNullParameter($this$commonReadUtf8CodePoint, "$this$commonReadUtf8CodePoint");
        if ($this$commonReadUtf8CodePoint.size() != 0) {
            byte b0 = $this$commonReadUtf8CodePoint.getByte(0);
            if ((128 & b0) == 0) {
                codePoint = b0 & 127;
                byteCount = 1;
                min = 0;
            } else if ((224 & b0) == 192) {
                codePoint = b0 & 31;
                byteCount = 2;
                min = 128;
            } else if ((240 & b0) == 224) {
                codePoint = b0 & 15;
                byteCount = 3;
                min = 2048;
            } else if ((248 & b0) == 240) {
                codePoint = b0 & 7;
                byteCount = 4;
                min = 65536;
            } else {
                $this$commonReadUtf8CodePoint.skip(1);
                return Utf8.REPLACEMENT_CODE_POINT;
            }
            if ($this$commonReadUtf8CodePoint.size() >= ((long) byteCount)) {
                int i = 1;
                while (i < byteCount) {
                    int b = $this$commonReadUtf8CodePoint.getByte((long) i);
                    if ((192 & b) == 128) {
                        codePoint = (codePoint << 6) | (63 & b);
                        i++;
                    } else {
                        $this$commonReadUtf8CodePoint.skip((long) i);
                        return Utf8.REPLACEMENT_CODE_POINT;
                    }
                }
                $this$commonReadUtf8CodePoint.skip((long) byteCount);
                if (codePoint > 1114111) {
                    return Utf8.REPLACEMENT_CODE_POINT;
                }
                if ((55296 <= codePoint && 57343 >= codePoint) || codePoint < min) {
                    return Utf8.REPLACEMENT_CODE_POINT;
                }
                return codePoint;
            }
            throw new EOFException("size < " + byteCount + ": " + $this$commonReadUtf8CodePoint.size() + " (to read code point prefixed 0x" + Util.toHexString(b0) + ')');
        }
        throw new EOFException();
    }

    public static final Buffer commonWriteUtf8(Buffer $this$commonWriteUtf8, String string, int beginIndex, int endIndex) {
        int runLimit;
        Buffer buffer = $this$commonWriteUtf8;
        String str = string;
        int i = beginIndex;
        int i2 = endIndex;
        Intrinsics.checkNotNullParameter(buffer, "$this$commonWriteUtf8");
        Intrinsics.checkNotNullParameter(str, TypedValues.Custom.S_STRING);
        int i3 = 1;
        if (i >= 0) {
            if (i2 >= i) {
                if (i2 <= string.length()) {
                    int runSize = beginIndex;
                    while (runSize < i2) {
                        int c = str.charAt(runSize);
                        if (c < 128) {
                            Segment tail = buffer.writableSegment$okio(i3);
                            byte[] data = tail.data;
                            int segmentOffset = tail.limit - runSize;
                            int runLimit2 = Math.min(i2, 8192 - segmentOffset);
                            int i4 = runSize + 1;
                            data[runSize + segmentOffset] = (byte) c;
                            while (i4 < runLimit2) {
                                int c2 = str.charAt(i4);
                                if (c2 >= 128) {
                                    break;
                                }
                                data[i4 + segmentOffset] = (byte) c2;
                                i4++;
                            }
                            int runSize2 = (i4 + segmentOffset) - tail.limit;
                            tail.limit += runSize2;
                            buffer.setSize$okio(((long) runSize2) + $this$commonWriteUtf8.size());
                            runSize = i4;
                            runLimit = 1;
                        } else if (c < 2048) {
                            Segment tail2 = buffer.writableSegment$okio(2);
                            tail2.data[tail2.limit] = (byte) ((c >> 6) | 192);
                            tail2.data[tail2.limit + 1] = (byte) (128 | (c & 63));
                            tail2.limit += 2;
                            buffer.setSize$okio($this$commonWriteUtf8.size() + 2);
                            runSize++;
                            runLimit = 1;
                        } else if (c < 55296 || c > 57343) {
                            Segment tail3 = buffer.writableSegment$okio(3);
                            tail3.data[tail3.limit] = (byte) ((c >> 12) | 224);
                            runLimit = 1;
                            tail3.data[tail3.limit + 1] = (byte) ((63 & (c >> 6)) | 128);
                            tail3.data[tail3.limit + 2] = (byte) ((c & 63) | 128);
                            tail3.limit += 3;
                            buffer.setSize$okio($this$commonWriteUtf8.size() + 3);
                            runSize++;
                        } else {
                            int low = runSize + 1 < i2 ? str.charAt(runSize + 1) : 0;
                            if (c > 56319 || 56320 > low || 57343 < low) {
                                buffer.writeByte(63);
                                runSize++;
                                runLimit = 1;
                            } else {
                                int codePoint = (((c & 1023) << 10) | (low & 1023)) + 65536;
                                Segment tail4 = buffer.writableSegment$okio(4);
                                tail4.data[tail4.limit] = (byte) ((codePoint >> 18) | 240);
                                tail4.data[tail4.limit + 1] = (byte) (((codePoint >> 12) & 63) | 128);
                                tail4.data[tail4.limit + 2] = (byte) (((codePoint >> 6) & 63) | 128);
                                tail4.data[tail4.limit + 3] = (byte) (128 | (codePoint & 63));
                                tail4.limit += 4;
                                buffer.setSize$okio($this$commonWriteUtf8.size() + 4);
                                runSize += 2;
                                runLimit = 1;
                            }
                        }
                        i3 = runLimit;
                    }
                    return buffer;
                }
                throw new IllegalArgumentException(("endIndex > string.length: " + i2 + " > " + string.length()).toString());
            }
            throw new IllegalArgumentException(("endIndex < beginIndex: " + i2 + " < " + i).toString());
        }
        throw new IllegalArgumentException(("beginIndex < 0: " + i).toString());
    }

    public static final Buffer commonWriteUtf8CodePoint(Buffer $this$commonWriteUtf8CodePoint, int codePoint) {
        Intrinsics.checkNotNullParameter($this$commonWriteUtf8CodePoint, "$this$commonWriteUtf8CodePoint");
        if (codePoint < 128) {
            $this$commonWriteUtf8CodePoint.writeByte(codePoint);
        } else if (codePoint < 2048) {
            Segment tail = $this$commonWriteUtf8CodePoint.writableSegment$okio(2);
            tail.data[tail.limit] = (byte) ((codePoint >> 6) | 192);
            tail.data[tail.limit + 1] = (byte) (128 | (codePoint & 63));
            tail.limit += 2;
            $this$commonWriteUtf8CodePoint.setSize$okio($this$commonWriteUtf8CodePoint.size() + 2);
        } else if (55296 <= codePoint && 57343 >= codePoint) {
            $this$commonWriteUtf8CodePoint.writeByte(63);
        } else if (codePoint < 65536) {
            Segment tail2 = $this$commonWriteUtf8CodePoint.writableSegment$okio(3);
            tail2.data[tail2.limit] = (byte) ((codePoint >> 12) | 224);
            tail2.data[tail2.limit + 1] = (byte) ((63 & (codePoint >> 6)) | 128);
            tail2.data[tail2.limit + 2] = (byte) (128 | (codePoint & 63));
            tail2.limit += 3;
            $this$commonWriteUtf8CodePoint.setSize$okio($this$commonWriteUtf8CodePoint.size() + 3);
        } else if (codePoint <= 1114111) {
            Segment tail3 = $this$commonWriteUtf8CodePoint.writableSegment$okio(4);
            tail3.data[tail3.limit] = (byte) ((codePoint >> 18) | 240);
            tail3.data[tail3.limit + 1] = (byte) (((codePoint >> 12) & 63) | 128);
            tail3.data[tail3.limit + 2] = (byte) (((codePoint >> 6) & 63) | 128);
            tail3.data[tail3.limit + 3] = (byte) (128 | (codePoint & 63));
            tail3.limit += 4;
            $this$commonWriteUtf8CodePoint.setSize$okio($this$commonWriteUtf8CodePoint.size() + 4);
        } else {
            throw new IllegalArgumentException("Unexpected code point: 0x" + Util.toHexString(codePoint));
        }
        return $this$commonWriteUtf8CodePoint;
    }

    public static final long commonWriteAll(Buffer $this$commonWriteAll, Source source) {
        Intrinsics.checkNotNullParameter($this$commonWriteAll, "$this$commonWriteAll");
        Intrinsics.checkNotNullParameter(source, "source");
        long totalBytesRead = 0;
        while (true) {
            long readCount = source.read($this$commonWriteAll, (long) 8192);
            if (readCount == -1) {
                return totalBytesRead;
            }
            totalBytesRead += readCount;
        }
    }

    public static final Buffer commonWrite(Buffer $this$commonWrite, Source source, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(source, "source");
        long byteCount2 = byteCount;
        while (byteCount2 > 0) {
            long read = source.read($this$commonWrite, byteCount2);
            if (read != -1) {
                byteCount2 -= read;
            } else {
                throw new EOFException();
            }
        }
        return $this$commonWrite;
    }

    public static final Buffer commonWriteByte(Buffer $this$commonWriteByte, int b) {
        Intrinsics.checkNotNullParameter($this$commonWriteByte, "$this$commonWriteByte");
        Segment tail = $this$commonWriteByte.writableSegment$okio(1);
        byte[] bArr = tail.data;
        int i = tail.limit;
        tail.limit = i + 1;
        bArr[i] = (byte) b;
        $this$commonWriteByte.setSize$okio($this$commonWriteByte.size() + 1);
        return $this$commonWriteByte;
    }

    public static final Buffer commonWriteShort(Buffer $this$commonWriteShort, int s) {
        Intrinsics.checkNotNullParameter($this$commonWriteShort, "$this$commonWriteShort");
        Segment tail = $this$commonWriteShort.writableSegment$okio(2);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((s >>> 8) & 255);
        data[limit2] = (byte) (s & 255);
        tail.limit = limit2 + 1;
        $this$commonWriteShort.setSize$okio($this$commonWriteShort.size() + 2);
        return $this$commonWriteShort;
    }

    public static final Buffer commonWriteInt(Buffer $this$commonWriteInt, int i) {
        Intrinsics.checkNotNullParameter($this$commonWriteInt, "$this$commonWriteInt");
        Segment tail = $this$commonWriteInt.writableSegment$okio(4);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((i >>> 24) & 255);
        int limit3 = limit2 + 1;
        data[limit2] = (byte) ((i >>> 16) & 255);
        int limit4 = limit3 + 1;
        data[limit3] = (byte) ((i >>> 8) & 255);
        data[limit4] = (byte) (i & 255);
        tail.limit = limit4 + 1;
        $this$commonWriteInt.setSize$okio($this$commonWriteInt.size() + 4);
        return $this$commonWriteInt;
    }

    public static final Buffer commonWriteLong(Buffer $this$commonWriteLong, long v) {
        Intrinsics.checkNotNullParameter($this$commonWriteLong, "$this$commonWriteLong");
        Segment tail = $this$commonWriteLong.writableSegment$okio(8);
        byte[] data = tail.data;
        int limit = tail.limit;
        int limit2 = limit + 1;
        data[limit] = (byte) ((int) ((v >>> 56) & 255));
        int limit3 = limit2 + 1;
        data[limit2] = (byte) ((int) ((v >>> 48) & 255));
        int limit4 = limit3 + 1;
        data[limit3] = (byte) ((int) ((v >>> 40) & 255));
        int limit5 = limit4 + 1;
        data[limit4] = (byte) ((int) ((v >>> 32) & 255));
        int limit6 = limit5 + 1;
        data[limit5] = (byte) ((int) ((v >>> 24) & 255));
        int limit7 = limit6 + 1;
        data[limit6] = (byte) ((int) ((v >>> 16) & 255));
        int limit8 = limit7 + 1;
        data[limit7] = (byte) ((int) ((v >>> 8) & 255));
        data[limit8] = (byte) ((int) (v & 255));
        tail.limit = limit8 + 1;
        $this$commonWriteLong.setSize$okio($this$commonWriteLong.size() + 8);
        return $this$commonWriteLong;
    }

    public static final void commonWrite(Buffer $this$commonWrite, Buffer source, long byteCount) {
        Segment tail;
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(source, "source");
        long byteCount2 = byteCount;
        if (source != $this$commonWrite) {
            Util.checkOffsetAndCount(source.size(), 0, byteCount2);
            while (byteCount2 > 0) {
                Segment segment = source.head;
                Intrinsics.checkNotNull(segment);
                int i = segment.limit;
                Segment segment2 = source.head;
                Intrinsics.checkNotNull(segment2);
                if (byteCount2 < ((long) (i - segment2.pos))) {
                    if ($this$commonWrite.head != null) {
                        Segment segment3 = $this$commonWrite.head;
                        Intrinsics.checkNotNull(segment3);
                        tail = segment3.prev;
                    } else {
                        tail = null;
                    }
                    if (tail != null && tail.owner) {
                        if ((((long) tail.limit) + byteCount2) - ((long) (tail.shared ? 0 : tail.pos)) <= ((long) 8192)) {
                            Segment segment4 = source.head;
                            Intrinsics.checkNotNull(segment4);
                            segment4.writeTo(tail, (int) byteCount2);
                            source.setSize$okio(source.size() - byteCount2);
                            $this$commonWrite.setSize$okio($this$commonWrite.size() + byteCount2);
                            return;
                        }
                    }
                    Segment segment5 = source.head;
                    Intrinsics.checkNotNull(segment5);
                    source.head = segment5.split((int) byteCount2);
                }
                Segment segmentToMove = source.head;
                Intrinsics.checkNotNull(segmentToMove);
                long movedByteCount = (long) (segmentToMove.limit - segmentToMove.pos);
                source.head = segmentToMove.pop();
                if ($this$commonWrite.head == null) {
                    $this$commonWrite.head = segmentToMove;
                    segmentToMove.prev = segmentToMove;
                    segmentToMove.next = segmentToMove.prev;
                } else {
                    Segment segment6 = $this$commonWrite.head;
                    Intrinsics.checkNotNull(segment6);
                    Segment tail2 = segment6.prev;
                    Intrinsics.checkNotNull(tail2);
                    tail2.push(segmentToMove).compact();
                }
                source.setSize$okio(source.size() - movedByteCount);
                $this$commonWrite.setSize$okio($this$commonWrite.size() + movedByteCount);
                byteCount2 -= movedByteCount;
            }
            return;
        }
        throw new IllegalArgumentException("source == this".toString());
    }

    public static final long commonRead(Buffer $this$commonRead, Buffer sink, long byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRead, "$this$commonRead");
        Intrinsics.checkNotNullParameter(sink, "sink");
        long byteCount2 = byteCount;
        if (!(byteCount2 >= 0)) {
            throw new IllegalArgumentException(("byteCount < 0: " + byteCount2).toString());
        } else if ($this$commonRead.size() == 0) {
            return -1;
        } else {
            if (byteCount2 > $this$commonRead.size()) {
                byteCount2 = $this$commonRead.size();
            }
            sink.write($this$commonRead, byteCount2);
            return byteCount2;
        }
    }

    public static final long commonIndexOf(Buffer $this$commonIndexOf, byte b, long fromIndex, long toIndex) {
        byte b2 = b;
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "$this$commonIndexOf");
        long fromIndex2 = fromIndex;
        long toIndex2 = toIndex;
        if (0 <= fromIndex2 && toIndex2 >= fromIndex2) {
            if (toIndex2 > $this$commonIndexOf.size()) {
                toIndex2 = $this$commonIndexOf.size();
            }
            if (fromIndex2 == toIndex2) {
                return -1;
            }
            long fromIndex$iv = fromIndex2;
            Buffer $this$seek$iv = $this$commonIndexOf;
            int $i$f$seek = 0;
            Segment s$iv = $this$seek$iv.head;
            if (s$iv == null) {
                Segment segment = null;
                return -1;
            } else if ($this$seek$iv.size() - fromIndex$iv < fromIndex$iv) {
                long offset$iv = $this$seek$iv.size();
                while (offset$iv > fromIndex$iv) {
                    Segment segment2 = s$iv.prev;
                    Intrinsics.checkNotNull(segment2);
                    s$iv = segment2;
                    offset$iv -= (long) (s$iv.limit - s$iv.pos);
                }
                Segment s = s$iv;
                long offset = offset$iv;
                int i = 0;
                if (s != null) {
                    long offset2 = offset;
                    Segment s2 = s;
                    while (offset2 < toIndex2) {
                        Buffer $this$seek$iv2 = $this$seek$iv;
                        byte[] data = s2.data;
                        Segment s3 = s;
                        int i2 = i;
                        int $i$f$seek2 = $i$f$seek;
                        Segment s$iv2 = s$iv;
                        int limit = (int) Math.min((long) s2.limit, (((long) s2.pos) + toIndex2) - offset2);
                        for (int pos = (int) ((((long) s2.pos) + fromIndex2) - offset2); pos < limit; pos++) {
                            if (data[pos] == b2) {
                                return ((long) (pos - s2.pos)) + offset2;
                            }
                        }
                        offset2 += (long) (s2.limit - s2.pos);
                        fromIndex2 = offset2;
                        Segment segment3 = s2.next;
                        Intrinsics.checkNotNull(segment3);
                        s2 = segment3;
                        Buffer buffer = $this$commonIndexOf;
                        $this$seek$iv = $this$seek$iv2;
                        s = s3;
                        i = i2;
                        $i$f$seek = $i$f$seek2;
                        s$iv = s$iv2;
                    }
                    return -1;
                }
                return -1;
            } else {
                Buffer buffer2 = $this$seek$iv;
                long offset$iv2 = 0;
                while (true) {
                    long nextOffset$iv = ((long) (s$iv.limit - s$iv.pos)) + offset$iv2;
                    if (nextOffset$iv > fromIndex$iv) {
                        break;
                    }
                    Segment segment4 = s$iv.next;
                    Intrinsics.checkNotNull(segment4);
                    s$iv = segment4;
                    offset$iv2 = nextOffset$iv;
                }
                Segment s4 = s$iv;
                long offset3 = offset$iv2;
                if (s4 != null) {
                    Segment s5 = s4;
                    long offset4 = offset3;
                    while (offset4 < toIndex2) {
                        byte[] data2 = s5.data;
                        long offset$iv3 = offset$iv2;
                        Segment s6 = s4;
                        long offset5 = offset3;
                        int limit2 = (int) Math.min((long) s5.limit, (((long) s5.pos) + toIndex2) - offset4);
                        for (int pos2 = (int) ((((long) s5.pos) + fromIndex2) - offset4); pos2 < limit2; pos2++) {
                            if (data2[pos2] == b2) {
                                return ((long) (pos2 - s5.pos)) + offset4;
                            }
                        }
                        offset4 += (long) (s5.limit - s5.pos);
                        fromIndex2 = offset4;
                        Segment segment5 = s5.next;
                        Intrinsics.checkNotNull(segment5);
                        s5 = segment5;
                        offset$iv2 = offset$iv3;
                        s4 = s6;
                        offset3 = offset5;
                    }
                    return -1;
                }
                return -1;
            }
        } else {
            throw new IllegalArgumentException(("size=" + $this$commonIndexOf.size() + " fromIndex=" + fromIndex2 + " toIndex=" + toIndex2).toString());
        }
    }

    public static final long commonIndexOf(Buffer $this$commonIndexOf, ByteString bytes, long fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "$this$commonIndexOf");
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        long fromIndex2 = fromIndex;
        if (bytes.size() > 0) {
            if (fromIndex2 >= 0) {
                long fromIndex$iv = fromIndex2;
                Buffer $this$seek$iv = $this$commonIndexOf;
                int $i$f$seek = 0;
                Segment s$iv = $this$seek$iv.head;
                if (s$iv == null) {
                    Segment segment = null;
                    return -1;
                } else if ($this$seek$iv.size() - fromIndex$iv < fromIndex$iv) {
                    long offset$iv = $this$seek$iv.size();
                    while (offset$iv > fromIndex$iv) {
                        Segment segment2 = s$iv.prev;
                        Intrinsics.checkNotNull(segment2);
                        s$iv = segment2;
                        offset$iv -= (long) (s$iv.limit - s$iv.pos);
                    }
                    Segment s = s$iv;
                    long offset = offset$iv;
                    if (s != null) {
                        long offset2 = offset;
                        byte[] targetByteArray = bytes.internalArray$okio();
                        byte b0 = targetByteArray[0];
                        int bytesSize = bytes.size();
                        Buffer buffer = $this$seek$iv;
                        long resultLimit = ($this$commonIndexOf.size() - ((long) bytesSize)) + 1;
                        Segment s2 = s;
                        while (offset2 < resultLimit) {
                            byte[] data = s2.data;
                            int $i$f$seek2 = $i$f$seek;
                            Segment s$iv2 = s$iv;
                            long offset3 = offset;
                            Segment s3 = s;
                            int segmentLimit = (int) Math.min((long) s2.limit, (((long) s2.pos) + resultLimit) - offset2);
                            for (int pos = (int) ((((long) s2.pos) + fromIndex2) - offset2); pos < segmentLimit; pos++) {
                                if (data[pos] == b0 && rangeEquals(s2, pos + 1, targetByteArray, 1, bytesSize)) {
                                    return ((long) (pos - s2.pos)) + offset2;
                                }
                            }
                            offset2 += (long) (s2.limit - s2.pos);
                            fromIndex2 = offset2;
                            Segment segment3 = s2.next;
                            Intrinsics.checkNotNull(segment3);
                            s2 = segment3;
                            Buffer buffer2 = $this$commonIndexOf;
                            ByteString byteString = bytes;
                            s = s3;
                            $i$f$seek = $i$f$seek2;
                            s$iv = s$iv2;
                            offset = offset3;
                        }
                        return -1;
                    }
                    return -1;
                } else {
                    Buffer buffer3 = $this$seek$iv;
                    long offset$iv2 = 0;
                    while (true) {
                        long nextOffset$iv = ((long) (s$iv.limit - s$iv.pos)) + offset$iv2;
                        if (nextOffset$iv > fromIndex$iv) {
                            break;
                        }
                        long j = offset$iv2;
                        Segment segment4 = s$iv.next;
                        Intrinsics.checkNotNull(segment4);
                        s$iv = segment4;
                        offset$iv2 = nextOffset$iv;
                        fromIndex$iv = fromIndex$iv;
                    }
                    Segment s4 = s$iv;
                    long offset4 = offset$iv2;
                    int i = 0;
                    if (s4 == null) {
                        return -1;
                    }
                    Segment s5 = s4;
                    long offset5 = offset4;
                    byte[] targetByteArray2 = bytes.internalArray$okio();
                    byte b02 = targetByteArray2[0];
                    long j2 = offset$iv2;
                    int bytesSize2 = bytes.size();
                    Segment segment5 = s4;
                    long resultLimit2 = ($this$commonIndexOf.size() - ((long) bytesSize2)) + 1;
                    while (offset5 < resultLimit2) {
                        byte[] data2 = s5.data;
                        int i2 = i;
                        long fromIndex$iv2 = fromIndex$iv;
                        long offset6 = offset4;
                        int segmentLimit2 = (int) Math.min((long) s5.limit, (((long) s5.pos) + resultLimit2) - offset5);
                        for (int pos2 = (int) ((((long) s5.pos) + fromIndex2) - offset5); pos2 < segmentLimit2; pos2++) {
                            if (data2[pos2] == b02) {
                                if (rangeEquals(s5, pos2 + 1, targetByteArray2, 1, bytesSize2)) {
                                    return ((long) (pos2 - s5.pos)) + offset5;
                                }
                            }
                        }
                        offset5 += (long) (s5.limit - s5.pos);
                        fromIndex2 = offset5;
                        Segment segment6 = s5.next;
                        Intrinsics.checkNotNull(segment6);
                        s5 = segment6;
                        i = i2;
                        fromIndex$iv = fromIndex$iv2;
                        offset4 = offset6;
                    }
                    return -1;
                }
            } else {
                throw new IllegalArgumentException(("fromIndex < 0: " + fromIndex2).toString());
            }
        } else {
            throw new IllegalArgumentException("bytes is empty".toString());
        }
    }

    public static final long commonIndexOfElement(Buffer $this$commonIndexOfElement, ByteString targetBytes, long fromIndex) {
        ByteString byteString = targetBytes;
        int $i$f$commonIndexOfElement = 0;
        Intrinsics.checkNotNullParameter($this$commonIndexOfElement, "$this$commonIndexOfElement");
        Intrinsics.checkNotNullParameter(byteString, "targetBytes");
        long fromIndex2 = fromIndex;
        if (fromIndex2 >= 0) {
            long fromIndex$iv = fromIndex2;
            Buffer $this$seek$iv = $this$commonIndexOfElement;
            Segment s$iv = $this$seek$iv.head;
            if (s$iv == null) {
                Segment segment = null;
                return -1;
            } else if ($this$seek$iv.size() - fromIndex$iv < fromIndex$iv) {
                long offset$iv = $this$seek$iv.size();
                while (offset$iv > fromIndex$iv) {
                    Segment segment2 = s$iv.prev;
                    Intrinsics.checkNotNull(segment2);
                    s$iv = segment2;
                    offset$iv -= (long) (s$iv.limit - s$iv.pos);
                }
                Segment s = s$iv;
                long offset = offset$iv;
                if (s == null) {
                    return -1;
                }
                Segment s2 = s;
                long offset2 = offset;
                if (targetBytes.size() == 2) {
                    byte b0 = byteString.getByte(0);
                    byte b1 = byteString.getByte(1);
                    Segment s3 = s2;
                    while (offset2 < $this$commonIndexOfElement.size()) {
                        int $i$f$commonIndexOfElement2 = $i$f$commonIndexOfElement;
                        byte[] data = s3.data;
                        Buffer $this$seek$iv2 = $this$seek$iv;
                        int pos = (int) ((((long) s3.pos) + fromIndex2) - offset2);
                        int limit = s3.limit;
                        while (pos < limit) {
                            int limit2 = limit;
                            byte limit3 = data[pos];
                            if (limit3 == b0 || limit3 == b1) {
                                byte[] bArr = data;
                                int i = pos;
                                return ((long) (pos - s3.pos)) + offset2;
                            }
                            pos++;
                            limit = limit2;
                        }
                        int i2 = pos;
                        int i3 = limit;
                        offset2 += (long) (s3.limit - s3.pos);
                        fromIndex2 = offset2;
                        Segment segment3 = s3.next;
                        Intrinsics.checkNotNull(segment3);
                        s3 = segment3;
                        Buffer buffer = $this$commonIndexOfElement;
                        $i$f$commonIndexOfElement = $i$f$commonIndexOfElement2;
                        $this$seek$iv = $this$seek$iv2;
                    }
                    Buffer buffer2 = $this$seek$iv;
                    return -1;
                }
                Buffer buffer3 = $this$seek$iv;
                byte[] targetByteArray = targetBytes.internalArray$okio();
                Segment s4 = s2;
                while (offset2 < $this$commonIndexOfElement.size()) {
                    byte[] data2 = s4.data;
                    int pos2 = (int) ((((long) s4.pos) + fromIndex2) - offset2);
                    int limit4 = s4.limit;
                    while (pos2 < limit4) {
                        byte b = data2[pos2];
                        byte[] data3 = data2;
                        long fromIndex3 = fromIndex2;
                        for (byte t : targetByteArray) {
                            if (b == t) {
                                return ((long) (pos2 - s4.pos)) + offset2;
                            }
                        }
                        pos2++;
                        data2 = data3;
                        fromIndex2 = fromIndex3;
                    }
                    long j = fromIndex2;
                    offset2 += (long) (s4.limit - s4.pos);
                    fromIndex2 = offset2;
                    Segment segment4 = s4.next;
                    Intrinsics.checkNotNull(segment4);
                    s4 = segment4;
                }
                long j2 = fromIndex2;
                Segment segment5 = s4;
                return -1;
            } else {
                Buffer buffer4 = $this$seek$iv;
                long offset$iv2 = 0;
                while (true) {
                    long nextOffset$iv = ((long) (s$iv.limit - s$iv.pos)) + offset$iv2;
                    if (nextOffset$iv > fromIndex$iv) {
                        break;
                    }
                    long j3 = offset$iv2;
                    Segment segment6 = s$iv.next;
                    Intrinsics.checkNotNull(segment6);
                    s$iv = segment6;
                    offset$iv2 = nextOffset$iv;
                    byteString = targetBytes;
                }
                Segment s5 = s$iv;
                long offset3 = offset$iv2;
                if (s5 == null) {
                    return -1;
                }
                Segment s6 = s5;
                long offset4 = offset3;
                if (targetBytes.size() == 2) {
                    byte b02 = byteString.getByte(0);
                    byte b12 = byteString.getByte(1);
                    while (offset4 < $this$commonIndexOfElement.size()) {
                        byte[] data4 = s6.data;
                        long offset$iv3 = offset$iv2;
                        int pos3 = (int) ((((long) s6.pos) + fromIndex2) - offset4);
                        int limit5 = s6.limit;
                        while (pos3 < limit5) {
                            int limit6 = limit5;
                            byte limit7 = data4[pos3];
                            if (limit7 == b02 || limit7 == b12) {
                                byte[] bArr2 = data4;
                                int i4 = pos3;
                                return ((long) (pos3 - s6.pos)) + offset4;
                            }
                            pos3++;
                            limit5 = limit6;
                        }
                        int i5 = pos3;
                        int i6 = limit5;
                        offset4 += (long) (s6.limit - s6.pos);
                        fromIndex2 = offset4;
                        Segment segment7 = s6.next;
                        Intrinsics.checkNotNull(segment7);
                        s6 = segment7;
                        ByteString byteString2 = targetBytes;
                        offset$iv2 = offset$iv3;
                    }
                    Segment segment8 = s5;
                    return -1;
                }
                byte[] targetByteArray2 = targetBytes.internalArray$okio();
                while (offset4 < $this$commonIndexOfElement.size()) {
                    byte[] data5 = s6.data;
                    int pos4 = (int) ((((long) s6.pos) + fromIndex2) - offset4);
                    int limit8 = s6.limit;
                    while (pos4 < limit8) {
                        byte b2 = data5[pos4];
                        byte[] data6 = data5;
                        int length = targetByteArray2.length;
                        Segment s7 = s5;
                        int i7 = 0;
                        while (i7 < length) {
                            int i8 = length;
                            byte t2 = targetByteArray2[i7];
                            if (b2 == t2) {
                                byte[] bArr3 = targetByteArray2;
                                byte b3 = t2;
                                return ((long) (pos4 - s6.pos)) + offset4;
                            }
                            byte b4 = t2;
                            i7++;
                            length = i8;
                        }
                        pos4++;
                        data5 = data6;
                        s5 = s7;
                    }
                    byte[] targetByteArray3 = targetByteArray2;
                    byte[] bArr4 = data5;
                    Segment segment9 = s5;
                    offset4 += (long) (s6.limit - s6.pos);
                    fromIndex2 = offset4;
                    Segment segment10 = s6.next;
                    Intrinsics.checkNotNull(segment10);
                    s6 = segment10;
                    targetByteArray2 = targetByteArray3;
                }
                Segment segment11 = s5;
                return -1;
            }
        } else {
            throw new IllegalArgumentException(("fromIndex < 0: " + fromIndex2).toString());
        }
    }

    public static final boolean commonRangeEquals(Buffer $this$commonRangeEquals, long offset, ByteString bytes, int bytesOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "$this$commonRangeEquals");
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        if (offset < 0 || bytesOffset < 0 || byteCount < 0 || $this$commonRangeEquals.size() - offset < ((long) byteCount) || bytes.size() - bytesOffset < byteCount) {
            return false;
        }
        for (int i = 0; i < byteCount; i++) {
            if ($this$commonRangeEquals.getByte(((long) i) + offset) != bytes.getByte(bytesOffset + i)) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: type inference failed for: r22v0, types: [java.lang.Object] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final boolean commonEquals(okio.Buffer r21, java.lang.Object r22) {
        /*
            r0 = r21
            r1 = r22
            r2 = 0
            java.lang.String r3 = "$this$commonEquals"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r3)
            r3 = 1
            if (r0 != r1) goto L_0x000e
            return r3
        L_0x000e:
            boolean r4 = r1 instanceof okio.Buffer
            r5 = 0
            if (r4 != 0) goto L_0x0014
            return r5
        L_0x0014:
            long r6 = r21.size()
            r4 = r1
            okio.Buffer r4 = (okio.Buffer) r4
            long r8 = r4.size()
            int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x0024
            return r5
        L_0x0024:
            long r6 = r21.size()
            r8 = 0
            int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r4 != 0) goto L_0x002f
            return r3
        L_0x002f:
            okio.Segment r4 = r0.head
            kotlin.jvm.internal.Intrinsics.checkNotNull(r4)
            r6 = r1
            okio.Buffer r6 = (okio.Buffer) r6
            okio.Segment r6 = r6.head
            kotlin.jvm.internal.Intrinsics.checkNotNull(r6)
            int r7 = r4.pos
            int r10 = r6.pos
            r11 = 0
            r13 = 0
        L_0x0044:
            long r15 = r21.size()
            int r15 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1))
            if (r15 >= 0) goto L_0x0094
            int r15 = r4.limit
            int r15 = r15 - r7
            int r8 = r6.limit
            int r8 = r8 - r10
            int r8 = java.lang.Math.min(r15, r8)
            long r13 = (long) r8
            r8 = 0
        L_0x0059:
            int r15 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r15 >= 0) goto L_0x0077
            byte[] r15 = r4.data
            int r17 = r7 + 1
            byte r7 = r15[r7]
            byte[] r15 = r6.data
            int r18 = r10 + 1
            byte r10 = r15[r10]
            if (r7 == r10) goto L_0x006c
            return r5
        L_0x006c:
            r19 = 1
            long r19 = r8 + r19
            r7 = r17
            r10 = r18
            r8 = r19
            goto L_0x0059
        L_0x0077:
            int r8 = r4.limit
            if (r7 != r8) goto L_0x0083
            okio.Segment r8 = r4.next
            kotlin.jvm.internal.Intrinsics.checkNotNull(r8)
            r4 = r8
            int r7 = r4.pos
        L_0x0083:
            int r8 = r6.limit
            if (r10 != r8) goto L_0x0090
            okio.Segment r8 = r6.next
            kotlin.jvm.internal.Intrinsics.checkNotNull(r8)
            r6 = r8
            int r8 = r6.pos
            r10 = r8
        L_0x0090:
            long r11 = r11 + r13
            r8 = 0
            goto L_0x0044
        L_0x0094:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal.BufferKt.commonEquals(okio.Buffer, java.lang.Object):boolean");
    }

    public static final int commonHashCode(Buffer $this$commonHashCode) {
        Intrinsics.checkNotNullParameter($this$commonHashCode, "$this$commonHashCode");
        Segment s = $this$commonHashCode.head;
        if (s == null) {
            return 0;
        }
        int result = 1;
        do {
            int limit = s.limit;
            for (int pos = s.pos; pos < limit; pos++) {
                result = (result * 31) + s.data[pos];
            }
            Segment segment = s.next;
            Intrinsics.checkNotNull(segment);
            s = segment;
        } while (s != $this$commonHashCode.head);
        return result;
    }

    public static final Buffer commonCopy(Buffer $this$commonCopy) {
        Intrinsics.checkNotNullParameter($this$commonCopy, "$this$commonCopy");
        Buffer result = new Buffer();
        if ($this$commonCopy.size() == 0) {
            return result;
        }
        Segment head = $this$commonCopy.head;
        Intrinsics.checkNotNull(head);
        Segment headCopy = head.sharedCopy();
        result.head = headCopy;
        headCopy.prev = result.head;
        headCopy.next = headCopy.prev;
        for (Segment s = head.next; s != head; s = s.next) {
            Segment segment = headCopy.prev;
            Intrinsics.checkNotNull(segment);
            Intrinsics.checkNotNull(s);
            segment.push(s.sharedCopy());
        }
        result.setSize$okio($this$commonCopy.size());
        return result;
    }

    public static final ByteString commonSnapshot(Buffer $this$commonSnapshot) {
        Intrinsics.checkNotNullParameter($this$commonSnapshot, "$this$commonSnapshot");
        if ($this$commonSnapshot.size() <= ((long) Integer.MAX_VALUE)) {
            return $this$commonSnapshot.snapshot((int) $this$commonSnapshot.size());
        }
        throw new IllegalStateException(("size > Int.MAX_VALUE: " + $this$commonSnapshot.size()).toString());
    }

    public static final ByteString commonSnapshot(Buffer $this$commonSnapshot, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonSnapshot, "$this$commonSnapshot");
        if (byteCount == 0) {
            return ByteString.EMPTY;
        }
        Util.checkOffsetAndCount($this$commonSnapshot.size(), 0, (long) byteCount);
        int offset = 0;
        int segmentCount = 0;
        Segment s = $this$commonSnapshot.head;
        while (offset < byteCount) {
            Intrinsics.checkNotNull(s);
            if (s.limit != s.pos) {
                offset += s.limit - s.pos;
                segmentCount++;
                s = s.next;
            } else {
                throw new AssertionError("s.limit == s.pos");
            }
        }
        byte[][] segments = new byte[segmentCount][];
        int[] directory = new int[(segmentCount * 2)];
        int offset2 = 0;
        int segmentCount2 = 0;
        Segment s2 = $this$commonSnapshot.head;
        while (offset2 < byteCount) {
            Intrinsics.checkNotNull(s2);
            segments[segmentCount2] = s2.data;
            offset2 += s2.limit - s2.pos;
            directory[segmentCount2] = Math.min(offset2, byteCount);
            directory[((Object[]) segments).length + segmentCount2] = s2.pos;
            s2.shared = true;
            segmentCount2++;
            s2 = s2.next;
        }
        return new SegmentedByteString(segments, directory);
    }
}
