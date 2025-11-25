package okio.internal;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.ByteString;
import okio.Segment;
import okio.SegmentedByteString;
import okio.Util;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000R\n\u0000\n\u0002\u0010\b\n\u0002\u0010\u0015\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a$\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0000\u001a\u0017\u0010\u0006\u001a\u00020\u0007*\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\b\u001a\r\u0010\u000b\u001a\u00020\u0001*\u00020\bH\b\u001a\r\u0010\f\u001a\u00020\u0001*\u00020\bH\b\u001a\u0015\u0010\r\u001a\u00020\u000e*\u00020\b2\u0006\u0010\u000f\u001a\u00020\u0001H\b\u001a-\u0010\u0010\u001a\u00020\u0007*\u00020\b2\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u0001H\b\u001a-\u0010\u0010\u001a\u00020\u0007*\u00020\b2\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u00152\u0006\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u0001H\b\u001a\u001d\u0010\u0016\u001a\u00020\u0015*\u00020\b2\u0006\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u0001H\b\u001a\r\u0010\u0019\u001a\u00020\u0012*\u00020\bH\b\u001a%\u0010\u001a\u001a\u00020\u001b*\u00020\b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u0001H\b\u001a]\u0010\u001e\u001a\u00020\u001b*\u00020\b2K\u0010\u001f\u001aG\u0012\u0013\u0012\u00110\u0012¢\u0006\f\b!\u0012\b\b\"\u0012\u0004\b\b(#\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b!\u0012\b\b\"\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b!\u0012\b\b\"\u0012\u0004\b\b(\u0014\u0012\u0004\u0012\u00020\u001b0 H\bø\u0001\u0000\u001aj\u0010\u001e\u001a\u00020\u001b*\u00020\b2\u0006\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u00012K\u0010\u001f\u001aG\u0012\u0013\u0012\u00110\u0012¢\u0006\f\b!\u0012\b\b\"\u0012\u0004\b\b(#\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b!\u0012\b\b\"\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b!\u0012\b\b\"\u0012\u0004\b\b(\u0014\u0012\u0004\u0012\u00020\u001b0 H\b\u001a\u0014\u0010$\u001a\u00020\u0001*\u00020\b2\u0006\u0010\u000f\u001a\u00020\u0001H\u0000\u0002\u0007\n\u0005\b20\u0001¨\u0006%"}, d2 = {"binarySearch", "", "", "value", "fromIndex", "toIndex", "commonEquals", "", "Lokio/SegmentedByteString;", "other", "", "commonGetSize", "commonHashCode", "commonInternalGet", "", "pos", "commonRangeEquals", "offset", "", "otherOffset", "byteCount", "Lokio/ByteString;", "commonSubstring", "beginIndex", "endIndex", "commonToByteArray", "commonWrite", "", "buffer", "Lokio/Buffer;", "forEachSegment", "action", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "data", "segment", "okio"}, k = 2, mv = {1, 4, 0})
/* compiled from: SegmentedByteString.kt */
public final class SegmentedByteStringKt {
    public static final int binarySearch(int[] $this$binarySearch, int value, int fromIndex, int toIndex) {
        Intrinsics.checkNotNullParameter($this$binarySearch, "$this$binarySearch");
        int left = fromIndex;
        int right = toIndex - 1;
        while (left <= right) {
            int mid = (left + right) >>> 1;
            int midVal = $this$binarySearch[mid];
            if (midVal < value) {
                left = mid + 1;
            } else if (midVal <= value) {
                return mid;
            } else {
                right = mid - 1;
            }
        }
        return (-left) - 1;
    }

    public static final int segment(SegmentedByteString $this$segment, int pos) {
        Intrinsics.checkNotNullParameter($this$segment, "$this$segment");
        int i = binarySearch($this$segment.getDirectory$okio(), pos + 1, 0, ((Object[]) $this$segment.getSegments$okio()).length);
        return i >= 0 ? i : ~i;
    }

    public static final void forEachSegment(SegmentedByteString $this$forEachSegment, Function3<? super byte[], ? super Integer, ? super Integer, Unit> action) {
        Intrinsics.checkNotNullParameter($this$forEachSegment, "$this$forEachSegment");
        Intrinsics.checkNotNullParameter(action, "action");
        int segmentCount = ((Object[]) $this$forEachSegment.getSegments$okio()).length;
        int pos = 0;
        for (int s = 0; s < segmentCount; s++) {
            int segmentPos = $this$forEachSegment.getDirectory$okio()[segmentCount + s];
            int nextSegmentOffset = $this$forEachSegment.getDirectory$okio()[s];
            action.invoke($this$forEachSegment.getSegments$okio()[s], Integer.valueOf(segmentPos), Integer.valueOf(nextSegmentOffset - pos));
            pos = nextSegmentOffset;
        }
    }

    /* access modifiers changed from: private */
    public static final void forEachSegment(SegmentedByteString $this$forEachSegment, int beginIndex, int endIndex, Function3<? super byte[], ? super Integer, ? super Integer, Unit> action) {
        int s = segment($this$forEachSegment, beginIndex);
        int pos = beginIndex;
        while (pos < endIndex) {
            int segmentOffset = s == 0 ? 0 : $this$forEachSegment.getDirectory$okio()[s - 1];
            int segmentPos = $this$forEachSegment.getDirectory$okio()[((Object[]) $this$forEachSegment.getSegments$okio()).length + s];
            int byteCount = Math.min(endIndex, segmentOffset + ($this$forEachSegment.getDirectory$okio()[s] - segmentOffset)) - pos;
            action.invoke($this$forEachSegment.getSegments$okio()[s], Integer.valueOf((pos - segmentOffset) + segmentPos), Integer.valueOf(byteCount));
            pos += byteCount;
            s++;
        }
    }

    public static final ByteString commonSubstring(SegmentedByteString $this$commonSubstring, int beginIndex, int endIndex) {
        int index;
        Intrinsics.checkNotNullParameter($this$commonSubstring, "$this$commonSubstring");
        int segmentOffset = 0;
        boolean z = true;
        if (beginIndex >= 0) {
            if (endIndex <= $this$commonSubstring.size()) {
                int subLen = endIndex - beginIndex;
                if (subLen < 0) {
                    z = false;
                }
                if (!z) {
                    throw new IllegalArgumentException(("endIndex=" + endIndex + " < beginIndex=" + beginIndex).toString());
                } else if (beginIndex == 0 && endIndex == $this$commonSubstring.size()) {
                    return $this$commonSubstring;
                } else {
                    if (beginIndex == endIndex) {
                        return ByteString.EMPTY;
                    }
                    int beginSegment = segment($this$commonSubstring, beginIndex);
                    int endSegment = segment($this$commonSubstring, endIndex - 1);
                    byte[][] newSegments = (byte[][]) ArraysKt.copyOfRange((T[]) (Object[]) $this$commonSubstring.getSegments$okio(), beginSegment, endSegment + 1);
                    int[] newDirectory = new int[(((Object[]) newSegments).length * 2)];
                    int index2 = 0;
                    if (beginSegment <= endSegment) {
                        int s = beginSegment;
                        while (true) {
                            newDirectory[index2] = Math.min($this$commonSubstring.getDirectory$okio()[s] - beginIndex, subLen);
                            index = index2 + 1;
                            newDirectory[index2 + ((Object[]) newSegments).length] = $this$commonSubstring.getDirectory$okio()[((Object[]) $this$commonSubstring.getSegments$okio()).length + s];
                            if (s == endSegment) {
                                break;
                            }
                            s++;
                            index2 = index;
                        }
                        int i = index;
                    }
                    if (beginSegment != 0) {
                        segmentOffset = $this$commonSubstring.getDirectory$okio()[beginSegment - 1];
                    }
                    int length = ((Object[]) newSegments).length;
                    newDirectory[length] = newDirectory[length] + (beginIndex - segmentOffset);
                    return new SegmentedByteString(newSegments, newDirectory);
                }
            } else {
                throw new IllegalArgumentException(("endIndex=" + endIndex + " > length(" + $this$commonSubstring.size() + ')').toString());
            }
        } else {
            throw new IllegalArgumentException(("beginIndex=" + beginIndex + " < 0").toString());
        }
    }

    public static final byte commonInternalGet(SegmentedByteString $this$commonInternalGet, int pos) {
        Intrinsics.checkNotNullParameter($this$commonInternalGet, "$this$commonInternalGet");
        Util.checkOffsetAndCount((long) $this$commonInternalGet.getDirectory$okio()[((Object[]) $this$commonInternalGet.getSegments$okio()).length - 1], (long) pos, 1);
        int segment = segment($this$commonInternalGet, pos);
        return $this$commonInternalGet.getSegments$okio()[segment][(pos - (segment == 0 ? 0 : $this$commonInternalGet.getDirectory$okio()[segment - 1])) + $this$commonInternalGet.getDirectory$okio()[((Object[]) $this$commonInternalGet.getSegments$okio()).length + segment]];
    }

    public static final int commonGetSize(SegmentedByteString $this$commonGetSize) {
        Intrinsics.checkNotNullParameter($this$commonGetSize, "$this$commonGetSize");
        return $this$commonGetSize.getDirectory$okio()[((Object[]) $this$commonGetSize.getSegments$okio()).length - 1];
    }

    public static final byte[] commonToByteArray(SegmentedByteString $this$commonToByteArray) {
        Intrinsics.checkNotNullParameter($this$commonToByteArray, "$this$commonToByteArray");
        byte[] result = new byte[$this$commonToByteArray.size()];
        int resultPos = 0;
        SegmentedByteString $this$forEachSegment$iv = $this$commonToByteArray;
        int segmentCount$iv = ((Object[]) $this$forEachSegment$iv.getSegments$okio()).length;
        int pos$iv = 0;
        for (int s$iv = 0; s$iv < segmentCount$iv; s$iv++) {
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
            int byteCount = nextSegmentOffset$iv - pos$iv;
            int offset = segmentPos$iv;
            ArraysKt.copyInto($this$forEachSegment$iv.getSegments$okio()[s$iv], result, resultPos, offset, offset + byteCount);
            resultPos += byteCount;
            pos$iv = nextSegmentOffset$iv;
        }
        return result;
    }

    public static final void commonWrite(SegmentedByteString $this$commonWrite, Buffer buffer, int offset, int byteCount) {
        Buffer buffer2 = buffer;
        int i = offset;
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(buffer2, "buffer");
        int endIndex$iv = i + byteCount;
        SegmentedByteString $this$forEachSegment$iv = $this$commonWrite;
        int s$iv = segment($this$forEachSegment$iv, i);
        int pos$iv = offset;
        while (pos$iv < endIndex$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + ($this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv)) - pos$iv;
            int offset2 = (pos$iv - segmentOffset$iv) + segmentPos$iv;
            Segment segment = new Segment($this$forEachSegment$iv.getSegments$okio()[s$iv], offset2, offset2 + byteCount$iv, true, false);
            if (buffer2.head == null) {
                segment.prev = segment;
                segment.next = segment.prev;
                buffer2.head = segment.next;
            } else {
                Segment segment2 = buffer2.head;
                Intrinsics.checkNotNull(segment2);
                Segment segment3 = segment2.prev;
                Intrinsics.checkNotNull(segment3);
                segment3.push(segment);
            }
            pos$iv += byteCount$iv;
            s$iv++;
            int i2 = offset;
        }
        buffer2.setSize$okio(buffer.size() + ((long) $this$commonWrite.size()));
    }

    public static final boolean commonRangeEquals(SegmentedByteString $this$commonRangeEquals, int offset, ByteString other, int otherOffset, int byteCount) {
        int i = offset;
        ByteString byteString = other;
        int $i$f$commonRangeEquals = 0;
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "$this$commonRangeEquals");
        Intrinsics.checkNotNullParameter(byteString, "other");
        if (i < 0) {
            return false;
        }
        if (i > $this$commonRangeEquals.size() - byteCount) {
            return false;
        }
        int otherOffset2 = otherOffset;
        int endIndex$iv = i + byteCount;
        SegmentedByteString $this$forEachSegment$iv = $this$commonRangeEquals;
        int s$iv = segment($this$forEachSegment$iv, i);
        int pos$iv = offset;
        while (pos$iv < endIndex$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + ($this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv)) - pos$iv;
            int byteCount2 = byteCount$iv;
            int offset2 = $i$f$commonRangeEquals;
            if (byteString.rangeEquals(otherOffset2, $this$forEachSegment$iv.getSegments$okio()[s$iv], (pos$iv - segmentOffset$iv) + segmentPos$iv, byteCount2) == 0) {
                return false;
            }
            otherOffset2 += byteCount2;
            pos$iv += byteCount$iv;
            s$iv++;
            int i2 = offset;
            $i$f$commonRangeEquals = offset2;
        }
        return true;
    }

    public static final boolean commonRangeEquals(SegmentedByteString $this$commonRangeEquals, int offset, byte[] other, int otherOffset, int byteCount) {
        int i = offset;
        byte[] bArr = other;
        int i2 = otherOffset;
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "$this$commonRangeEquals");
        Intrinsics.checkNotNullParameter(bArr, "other");
        if (i < 0 || i > $this$commonRangeEquals.size() - byteCount || i2 < 0 || i2 > bArr.length - byteCount) {
            return false;
        }
        int otherOffset2 = otherOffset;
        int endIndex$iv = i + byteCount;
        SegmentedByteString $this$forEachSegment$iv = $this$commonRangeEquals;
        int s$iv = segment($this$forEachSegment$iv, i);
        int pos$iv = offset;
        while (pos$iv < endIndex$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[]) $this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + ($this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv)) - pos$iv;
            int byteCount2 = byteCount$iv;
            if (Util.arrayRangeEquals($this$forEachSegment$iv.getSegments$okio()[s$iv], segmentPos$iv + (pos$iv - segmentOffset$iv), bArr, otherOffset2, byteCount2) == 0) {
                return false;
            }
            otherOffset2 += byteCount2;
            pos$iv += byteCount$iv;
            s$iv++;
            int i3 = offset;
            int i4 = otherOffset;
        }
        return true;
    }

    public static final boolean commonEquals(SegmentedByteString $this$commonEquals, Object other) {
        Intrinsics.checkNotNullParameter($this$commonEquals, "$this$commonEquals");
        if (other == $this$commonEquals) {
            return true;
        }
        if (!(other instanceof ByteString)) {
            return false;
        }
        if (((ByteString) other).size() != $this$commonEquals.size() || !$this$commonEquals.rangeEquals(0, (ByteString) other, 0, $this$commonEquals.size())) {
            return false;
        }
        return true;
    }

    public static final int commonHashCode(SegmentedByteString $this$commonHashCode) {
        SegmentedByteString segmentedByteString = $this$commonHashCode;
        Intrinsics.checkNotNullParameter(segmentedByteString, "$this$commonHashCode");
        int result = $this$commonHashCode.getHashCode$okio();
        if (result != 0) {
            return result;
        }
        int result2 = 1;
        SegmentedByteString $this$forEachSegment$iv = $this$commonHashCode;
        int segmentCount$iv = ((Object[]) $this$forEachSegment$iv.getSegments$okio()).length;
        int pos$iv = 0;
        for (int s$iv = 0; s$iv < segmentCount$iv; s$iv++) {
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            int offset = segmentPos$iv;
            int limit = offset + (nextSegmentOffset$iv - pos$iv);
            for (int i = offset; i < limit; i++) {
                result2 = (result2 * 31) + data[i];
            }
            pos$iv = nextSegmentOffset$iv;
        }
        segmentedByteString.setHashCode$okio(result2);
        return result2;
    }
}
