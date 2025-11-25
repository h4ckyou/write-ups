package okio.internal;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.Base64;
import okio.Buffer;
import okio.ByteString;
import okio.Platform;
import okio.Util;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000P\n\u0000\n\u0002\u0010\u0019\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0018\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u0002\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0007H\b\u001a\u0010\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eH\u0002\u001a\r\u0010\u000f\u001a\u00020\u0010*\u00020\nH\b\u001a\r\u0010\u0011\u001a\u00020\u0010*\u00020\nH\b\u001a\u0015\u0010\u0012\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u0013\u001a\u00020\nH\b\u001a\u000f\u0010\u0014\u001a\u0004\u0018\u00010\n*\u00020\u0010H\b\u001a\r\u0010\u0015\u001a\u00020\n*\u00020\u0010H\b\u001a\r\u0010\u0016\u001a\u00020\n*\u00020\u0010H\b\u001a\u0015\u0010\u0017\u001a\u00020\u0018*\u00020\n2\u0006\u0010\u0019\u001a\u00020\u0007H\b\u001a\u0015\u0010\u0017\u001a\u00020\u0018*\u00020\n2\u0006\u0010\u0019\u001a\u00020\nH\b\u001a\u0017\u0010\u001a\u001a\u00020\u0018*\u00020\n2\b\u0010\u0013\u001a\u0004\u0018\u00010\u001bH\b\u001a\u0015\u0010\u001c\u001a\u00020\u001d*\u00020\n2\u0006\u0010\u001e\u001a\u00020\u0005H\b\u001a\r\u0010\u001f\u001a\u00020\u0005*\u00020\nH\b\u001a\r\u0010 \u001a\u00020\u0005*\u00020\nH\b\u001a\r\u0010!\u001a\u00020\u0010*\u00020\nH\b\u001a\u001d\u0010\"\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u0005H\b\u001a\r\u0010$\u001a\u00020\u0007*\u00020\nH\b\u001a\u001d\u0010%\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u0005H\b\u001a\u001d\u0010%\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010#\u001a\u00020\u0005H\b\u001a-\u0010&\u001a\u00020\u0018*\u00020\n2\u0006\u0010'\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010(\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H\b\u001a-\u0010&\u001a\u00020\u0018*\u00020\n2\u0006\u0010'\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010(\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H\b\u001a\u0015\u0010*\u001a\u00020\u0018*\u00020\n2\u0006\u0010+\u001a\u00020\u0007H\b\u001a\u0015\u0010*\u001a\u00020\u0018*\u00020\n2\u0006\u0010+\u001a\u00020\nH\b\u001a\u001d\u0010,\u001a\u00020\n*\u00020\n2\u0006\u0010-\u001a\u00020\u00052\u0006\u0010.\u001a\u00020\u0005H\b\u001a\r\u0010/\u001a\u00020\n*\u00020\nH\b\u001a\r\u00100\u001a\u00020\n*\u00020\nH\b\u001a\r\u00101\u001a\u00020\u0007*\u00020\nH\b\u001a\u001d\u00102\u001a\u00020\n*\u00020\u00072\u0006\u0010'\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H\b\u001a\r\u00103\u001a\u00020\u0010*\u00020\nH\b\u001a\r\u00104\u001a\u00020\u0010*\u00020\nH\b\u001a$\u00105\u001a\u000206*\u00020\n2\u0006\u00107\u001a\u0002082\u0006\u0010'\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H\u0000\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003¨\u00069"}, d2 = {"HEX_DIGIT_CHARS", "", "getHEX_DIGIT_CHARS", "()[C", "codePointIndexToCharIndex", "", "s", "", "codePointCount", "commonOf", "Lokio/ByteString;", "data", "decodeHexDigit", "c", "", "commonBase64", "", "commonBase64Url", "commonCompareTo", "other", "commonDecodeBase64", "commonDecodeHex", "commonEncodeUtf8", "commonEndsWith", "", "suffix", "commonEquals", "", "commonGetByte", "", "pos", "commonGetSize", "commonHashCode", "commonHex", "commonIndexOf", "fromIndex", "commonInternalArray", "commonLastIndexOf", "commonRangeEquals", "offset", "otherOffset", "byteCount", "commonStartsWith", "prefix", "commonSubstring", "beginIndex", "endIndex", "commonToAsciiLowercase", "commonToAsciiUppercase", "commonToByteArray", "commonToByteString", "commonToString", "commonUtf8", "commonWrite", "", "buffer", "Lokio/Buffer;", "okio"}, k = 2, mv = {1, 4, 0})
/* compiled from: ByteString.kt */
public final class ByteStringKt {
    private static final char[] HEX_DIGIT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static final String commonUtf8(ByteString $this$commonUtf8) {
        Intrinsics.checkNotNullParameter($this$commonUtf8, "$this$commonUtf8");
        String result = $this$commonUtf8.getUtf8$okio();
        if (result != null) {
            return result;
        }
        String result2 = Platform.toUtf8String($this$commonUtf8.internalArray$okio());
        $this$commonUtf8.setUtf8$okio(result2);
        return result2;
    }

    public static final String commonBase64(ByteString $this$commonBase64) {
        Intrinsics.checkNotNullParameter($this$commonBase64, "$this$commonBase64");
        return Base64.encodeBase64$default($this$commonBase64.getData$okio(), (byte[]) null, 1, (Object) null);
    }

    public static final String commonBase64Url(ByteString $this$commonBase64Url) {
        Intrinsics.checkNotNullParameter($this$commonBase64Url, "$this$commonBase64Url");
        return Base64.encodeBase64($this$commonBase64Url.getData$okio(), Base64.getBASE64_URL_SAFE());
    }

    public static final char[] getHEX_DIGIT_CHARS() {
        return HEX_DIGIT_CHARS;
    }

    public static final String commonHex(ByteString $this$commonHex) {
        Intrinsics.checkNotNullParameter($this$commonHex, "$this$commonHex");
        char[] result = new char[($this$commonHex.getData$okio().length * 2)];
        int c = 0;
        for (int $this$shr$iv : $this$commonHex.getData$okio()) {
            int c2 = c + 1;
            result[c] = getHEX_DIGIT_CHARS()[($this$shr$iv >> 4) & 15];
            c = c2 + 1;
            result[c2] = getHEX_DIGIT_CHARS()[15 & $this$shr$iv];
        }
        return new String(result);
    }

    public static final ByteString commonToAsciiLowercase(ByteString $this$commonToAsciiLowercase) {
        byte b;
        Intrinsics.checkNotNullParameter($this$commonToAsciiLowercase, "$this$commonToAsciiLowercase");
        int i = 0;
        while (i < $this$commonToAsciiLowercase.getData$okio().length) {
            byte c = $this$commonToAsciiLowercase.getData$okio()[i];
            byte b2 = (byte) 65;
            if (c < b2 || c > (b = (byte) 90)) {
                i++;
            } else {
                byte[] data$okio = $this$commonToAsciiLowercase.getData$okio();
                byte[] lowercase = Arrays.copyOf(data$okio, data$okio.length);
                Intrinsics.checkNotNullExpressionValue(lowercase, "java.util.Arrays.copyOf(this, size)");
                int i2 = i + 1;
                lowercase[i] = (byte) (c + 32);
                while (i2 < lowercase.length) {
                    byte c2 = lowercase[i2];
                    if (c2 < b2 || c2 > b) {
                        i2++;
                    } else {
                        lowercase[i2] = (byte) (c2 + 32);
                        i2++;
                    }
                }
                return new ByteString(lowercase);
            }
        }
        return $this$commonToAsciiLowercase;
    }

    public static final ByteString commonToAsciiUppercase(ByteString $this$commonToAsciiUppercase) {
        byte b;
        Intrinsics.checkNotNullParameter($this$commonToAsciiUppercase, "$this$commonToAsciiUppercase");
        int i = 0;
        while (i < $this$commonToAsciiUppercase.getData$okio().length) {
            byte c = $this$commonToAsciiUppercase.getData$okio()[i];
            byte b2 = (byte) 97;
            if (c < b2 || c > (b = (byte) 122)) {
                i++;
            } else {
                byte[] data$okio = $this$commonToAsciiUppercase.getData$okio();
                byte[] lowercase = Arrays.copyOf(data$okio, data$okio.length);
                Intrinsics.checkNotNullExpressionValue(lowercase, "java.util.Arrays.copyOf(this, size)");
                int i2 = i + 1;
                lowercase[i] = (byte) (c - 32);
                while (i2 < lowercase.length) {
                    byte c2 = lowercase[i2];
                    if (c2 < b2 || c2 > b) {
                        i2++;
                    } else {
                        lowercase[i2] = (byte) (c2 - 32);
                        i2++;
                    }
                }
                return new ByteString(lowercase);
            }
        }
        return $this$commonToAsciiUppercase;
    }

    public static final ByteString commonSubstring(ByteString $this$commonSubstring, int beginIndex, int endIndex) {
        Intrinsics.checkNotNullParameter($this$commonSubstring, "$this$commonSubstring");
        boolean z = true;
        if (beginIndex >= 0) {
            if (endIndex <= $this$commonSubstring.getData$okio().length) {
                if (endIndex - beginIndex < 0) {
                    z = false;
                }
                if (!z) {
                    throw new IllegalArgumentException("endIndex < beginIndex".toString());
                } else if (beginIndex == 0 && endIndex == $this$commonSubstring.getData$okio().length) {
                    return $this$commonSubstring;
                } else {
                    return new ByteString(ArraysKt.copyOfRange($this$commonSubstring.getData$okio(), beginIndex, endIndex));
                }
            } else {
                throw new IllegalArgumentException(("endIndex > length(" + $this$commonSubstring.getData$okio().length + ')').toString());
            }
        } else {
            throw new IllegalArgumentException("beginIndex < 0".toString());
        }
    }

    public static final byte commonGetByte(ByteString $this$commonGetByte, int pos) {
        Intrinsics.checkNotNullParameter($this$commonGetByte, "$this$commonGetByte");
        return $this$commonGetByte.getData$okio()[pos];
    }

    public static final int commonGetSize(ByteString $this$commonGetSize) {
        Intrinsics.checkNotNullParameter($this$commonGetSize, "$this$commonGetSize");
        return $this$commonGetSize.getData$okio().length;
    }

    public static final byte[] commonToByteArray(ByteString $this$commonToByteArray) {
        Intrinsics.checkNotNullParameter($this$commonToByteArray, "$this$commonToByteArray");
        byte[] data$okio = $this$commonToByteArray.getData$okio();
        byte[] copyOf = Arrays.copyOf(data$okio, data$okio.length);
        Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    public static final byte[] commonInternalArray(ByteString $this$commonInternalArray) {
        Intrinsics.checkNotNullParameter($this$commonInternalArray, "$this$commonInternalArray");
        return $this$commonInternalArray.getData$okio();
    }

    public static final boolean commonRangeEquals(ByteString $this$commonRangeEquals, int offset, ByteString other, int otherOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "$this$commonRangeEquals");
        Intrinsics.checkNotNullParameter(other, "other");
        return other.rangeEquals(otherOffset, $this$commonRangeEquals.getData$okio(), offset, byteCount);
    }

    public static final boolean commonRangeEquals(ByteString $this$commonRangeEquals, int offset, byte[] other, int otherOffset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonRangeEquals, "$this$commonRangeEquals");
        Intrinsics.checkNotNullParameter(other, "other");
        return offset >= 0 && offset <= $this$commonRangeEquals.getData$okio().length - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount && Util.arrayRangeEquals($this$commonRangeEquals.getData$okio(), offset, other, otherOffset, byteCount);
    }

    public static final boolean commonStartsWith(ByteString $this$commonStartsWith, ByteString prefix) {
        Intrinsics.checkNotNullParameter($this$commonStartsWith, "$this$commonStartsWith");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return $this$commonStartsWith.rangeEquals(0, prefix, 0, prefix.size());
    }

    public static final boolean commonStartsWith(ByteString $this$commonStartsWith, byte[] prefix) {
        Intrinsics.checkNotNullParameter($this$commonStartsWith, "$this$commonStartsWith");
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return $this$commonStartsWith.rangeEquals(0, prefix, 0, prefix.length);
    }

    public static final boolean commonEndsWith(ByteString $this$commonEndsWith, ByteString suffix) {
        Intrinsics.checkNotNullParameter($this$commonEndsWith, "$this$commonEndsWith");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.size(), suffix, 0, suffix.size());
    }

    public static final boolean commonEndsWith(ByteString $this$commonEndsWith, byte[] suffix) {
        Intrinsics.checkNotNullParameter($this$commonEndsWith, "$this$commonEndsWith");
        Intrinsics.checkNotNullParameter(suffix, "suffix");
        return $this$commonEndsWith.rangeEquals($this$commonEndsWith.size() - suffix.length, suffix, 0, suffix.length);
    }

    public static final int commonIndexOf(ByteString $this$commonIndexOf, byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonIndexOf, "$this$commonIndexOf");
        Intrinsics.checkNotNullParameter(other, "other");
        int limit = $this$commonIndexOf.getData$okio().length - other.length;
        int i = Math.max(fromIndex, 0);
        if (i > limit) {
            return -1;
        }
        while (!Util.arrayRangeEquals($this$commonIndexOf.getData$okio(), i, other, 0, other.length)) {
            if (i == limit) {
                return -1;
            }
            i++;
        }
        return i;
    }

    public static final int commonLastIndexOf(ByteString $this$commonLastIndexOf, ByteString other, int fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "$this$commonLastIndexOf");
        Intrinsics.checkNotNullParameter(other, "other");
        return $this$commonLastIndexOf.lastIndexOf(other.internalArray$okio(), fromIndex);
    }

    public static final int commonLastIndexOf(ByteString $this$commonLastIndexOf, byte[] other, int fromIndex) {
        Intrinsics.checkNotNullParameter($this$commonLastIndexOf, "$this$commonLastIndexOf");
        Intrinsics.checkNotNullParameter(other, "other");
        for (int i = Math.min(fromIndex, $this$commonLastIndexOf.getData$okio().length - other.length); i >= 0; i--) {
            if (Util.arrayRangeEquals($this$commonLastIndexOf.getData$okio(), i, other, 0, other.length)) {
                return i;
            }
        }
        return -1;
    }

    public static final boolean commonEquals(ByteString $this$commonEquals, Object other) {
        Intrinsics.checkNotNullParameter($this$commonEquals, "$this$commonEquals");
        if (other == $this$commonEquals) {
            return true;
        }
        if (!(other instanceof ByteString)) {
            return false;
        }
        if (((ByteString) other).size() != $this$commonEquals.getData$okio().length || !((ByteString) other).rangeEquals(0, $this$commonEquals.getData$okio(), 0, $this$commonEquals.getData$okio().length)) {
            return false;
        }
        return true;
    }

    public static final int commonHashCode(ByteString $this$commonHashCode) {
        Intrinsics.checkNotNullParameter($this$commonHashCode, "$this$commonHashCode");
        int result = $this$commonHashCode.getHashCode$okio();
        if (result != 0) {
            return result;
        }
        int it = Arrays.hashCode($this$commonHashCode.getData$okio());
        $this$commonHashCode.setHashCode$okio(it);
        return it;
    }

    public static final int commonCompareTo(ByteString $this$commonCompareTo, ByteString other) {
        Intrinsics.checkNotNullParameter($this$commonCompareTo, "$this$commonCompareTo");
        Intrinsics.checkNotNullParameter(other, "other");
        int sizeA = $this$commonCompareTo.size();
        int sizeB = other.size();
        int i = 0;
        int size = Math.min(sizeA, sizeB);
        while (i < size) {
            byte byteA = $this$commonCompareTo.getByte(i) & UByte.MAX_VALUE;
            byte $this$and$iv = other.getByte(i) & UByte.MAX_VALUE;
            if (byteA == $this$and$iv) {
                i++;
            } else if (byteA < $this$and$iv) {
                return -1;
            } else {
                return 1;
            }
        }
        if (sizeA == sizeB) {
            return 0;
        }
        if (sizeA < sizeB) {
            return -1;
        }
        return 1;
    }

    public static final ByteString commonOf(byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        byte[] copyOf = Arrays.copyOf(data, data.length);
        Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, size)");
        return new ByteString(copyOf);
    }

    public static final ByteString commonToByteString(byte[] $this$commonToByteString, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonToByteString, "$this$commonToByteString");
        Util.checkOffsetAndCount((long) $this$commonToByteString.length, (long) offset, (long) byteCount);
        return new ByteString(ArraysKt.copyOfRange($this$commonToByteString, offset, offset + byteCount));
    }

    public static final ByteString commonEncodeUtf8(String $this$commonEncodeUtf8) {
        Intrinsics.checkNotNullParameter($this$commonEncodeUtf8, "$this$commonEncodeUtf8");
        ByteString byteString = new ByteString(Platform.asUtf8ToByteArray($this$commonEncodeUtf8));
        byteString.setUtf8$okio($this$commonEncodeUtf8);
        return byteString;
    }

    public static final ByteString commonDecodeBase64(String $this$commonDecodeBase64) {
        Intrinsics.checkNotNullParameter($this$commonDecodeBase64, "$this$commonDecodeBase64");
        byte[] decoded = Base64.decodeBase64ToArray($this$commonDecodeBase64);
        if (decoded != null) {
            return new ByteString(decoded);
        }
        return null;
    }

    public static final ByteString commonDecodeHex(String $this$commonDecodeHex) {
        Intrinsics.checkNotNullParameter($this$commonDecodeHex, "$this$commonDecodeHex");
        if ($this$commonDecodeHex.length() % 2 == 0) {
            byte[] result = new byte[($this$commonDecodeHex.length() / 2)];
            int length = result.length;
            for (int i = 0; i < length; i++) {
                result[i] = (byte) ((decodeHexDigit($this$commonDecodeHex.charAt(i * 2)) << 4) + decodeHexDigit($this$commonDecodeHex.charAt((i * 2) + 1)));
            }
            return new ByteString(result);
        }
        throw new IllegalArgumentException(("Unexpected hex string: " + $this$commonDecodeHex).toString());
    }

    public static final void commonWrite(ByteString $this$commonWrite, Buffer buffer, int offset, int byteCount) {
        Intrinsics.checkNotNullParameter($this$commonWrite, "$this$commonWrite");
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        buffer.write($this$commonWrite.getData$okio(), offset, byteCount);
    }

    /* access modifiers changed from: private */
    public static final int decodeHexDigit(char c) {
        if ('0' <= c && '9' >= c) {
            return c - '0';
        }
        if ('a' <= c && 'f' >= c) {
            return (c - 'a') + 10;
        }
        if ('A' <= c && 'F' >= c) {
            return (c - 'A') + 10;
        }
        throw new IllegalArgumentException("Unexpected hex digit: " + c);
    }

    public static final String commonToString(ByteString $this$commonToString) {
        Intrinsics.checkNotNullParameter($this$commonToString, "$this$commonToString");
        boolean z = true;
        if ($this$commonToString.getData$okio().length == 0) {
            return "[size=0]";
        }
        int i = codePointIndexToCharIndex($this$commonToString.getData$okio(), 64);
        if (i != -1) {
            String text = $this$commonToString.utf8();
            if (text != null) {
                String substring = text.substring(0, i);
                Intrinsics.checkNotNullExpressionValue(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
                String safeText = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(substring, "\\", "\\\\", false, 4, (Object) null), "\n", "\\n", false, 4, (Object) null), "\r", "\\r", false, 4, (Object) null);
                if (i < text.length()) {
                    return "[size=" + $this$commonToString.getData$okio().length + " text=" + safeText + "…]";
                }
                return "[text=" + safeText + ']';
            }
            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
        } else if ($this$commonToString.getData$okio().length <= 64) {
            return "[hex=" + $this$commonToString.hex() + ']';
        } else {
            StringBuilder append = new StringBuilder().append("[size=").append($this$commonToString.getData$okio().length).append(" hex=");
            ByteString $this$commonSubstring$iv = $this$commonToString;
            if (64 <= $this$commonSubstring$iv.getData$okio().length) {
                if (64 - 0 < 0) {
                    z = false;
                }
                if (z) {
                    if (64 != $this$commonSubstring$iv.getData$okio().length) {
                        $this$commonSubstring$iv = new ByteString(ArraysKt.copyOfRange($this$commonSubstring$iv.getData$okio(), 0, 64));
                    }
                    return append.append($this$commonSubstring$iv.hex()).append("…]").toString();
                }
                throw new IllegalArgumentException("endIndex < beginIndex".toString());
            }
            throw new IllegalArgumentException(("endIndex > length(" + $this$commonSubstring$iv.getData$okio().length + ')').toString());
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x0185, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x0191, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x0195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0193, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x01c8, code lost:
        if (31 < r14) goto L_0x01cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x01d4, code lost:
        if (159 < r14) goto L_0x01d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x01d6, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x025b, code lost:
        if (r16 == false) goto L_0x0260;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x029b, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x02a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x02a7, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x02ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x02a9, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x02fb, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x0300;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0307, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x030b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0309, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0358, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x035d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0364, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x0368;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0366, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x03a8, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x03ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x03b4, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x03b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x03b6, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x03c3, code lost:
        if (65533(0xfffd, float:9.1831E-41) < 65536(0x10000, float:9.18355E-41)) goto L_0x03fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x03e1, code lost:
        if (31 < r15) goto L_0x03e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x03ed, code lost:
        if (159 < r15) goto L_0x03f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x03ef, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x03fc, code lost:
        if (r15 < 65536) goto L_0x03fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x04cd, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x04d2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x04d9, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x04dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x04db, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x052b, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x0530;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x0537, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x053b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0539, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x058c, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x0591;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0598, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x059c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x059a, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x05ee, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x05f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x05fa, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x05fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x05fc, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x0640, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x0645;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x064c, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x0650;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x064e, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x065b, code lost:
        if (65533(0xfffd, float:9.1831E-41) < 65536(0x10000, float:9.18355E-41)) goto L_0x065d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0684, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x0689;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0690, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x0694;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0692, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x069f, code lost:
        if (65533(0xfffd, float:9.1831E-41) < 65536(0x10000, float:9.18355E-41)) goto L_0x065d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x06bd, code lost:
        if (31 < r15) goto L_0x06c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x06c9, code lost:
        if (159 < r15) goto L_0x06cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x06cb, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x06d8, code lost:
        if (r15 < 65536) goto L_0x065d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x012d, code lost:
        if (31 < 65533(0xfffd, float:9.1831E-41)) goto L_0x0132;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x0139, code lost:
        if (159(0x9f, float:2.23E-43) < 65533(0xfffd, float:9.1831E-41)) goto L_0x013d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x013b, code lost:
        r16 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final int codePointIndexToCharIndex(byte[] r29, int r30) {
        /*
            r0 = r30
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = r29
            int r5 = r4.length
            r6 = r29
            r7 = 0
            r8 = r3
        L_0x000c:
            if (r8 >= r5) goto L_0x0723
            byte r9 = r6[r8]
            r10 = 127(0x7f, float:1.78E-43)
            r11 = 159(0x9f, float:2.23E-43)
            r12 = 31
            r14 = 13
            r13 = 10
            r15 = 65536(0x10000, float:9.18355E-41)
            r16 = 0
            r17 = 2
            r18 = 1
            if (r9 < 0) goto L_0x00a0
            r19 = r9
            r20 = 0
            int r21 = r2 + 1
            if (r2 != r0) goto L_0x002e
            return r1
        L_0x002e:
            r2 = r19
            if (r2 == r13) goto L_0x0048
            if (r2 == r14) goto L_0x0048
            r19 = 0
            if (r2 < 0) goto L_0x003c
            if (r12 >= r2) goto L_0x0041
        L_0x003c:
            if (r10 <= r2) goto L_0x003f
            goto L_0x0044
        L_0x003f:
            if (r11 < r2) goto L_0x0044
        L_0x0041:
            r19 = r18
            goto L_0x0046
        L_0x0044:
            r19 = r16
        L_0x0046:
            if (r19 != 0) goto L_0x004d
        L_0x0048:
            r11 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r11) goto L_0x004f
        L_0x004d:
            r10 = -1
            return r10
        L_0x004f:
            if (r2 >= r15) goto L_0x0054
            r11 = r18
            goto L_0x0056
        L_0x0054:
            r11 = r17
        L_0x0056:
            int r1 = r1 + r11
            int r8 = r8 + 1
            r2 = r21
        L_0x005c:
            if (r8 >= r5) goto L_0x009c
            byte r11 = r6[r8]
            if (r11 < 0) goto L_0x009c
            int r11 = r8 + 1
            byte r8 = r6[r8]
            r20 = 0
            int r21 = r2 + 1
            if (r2 != r0) goto L_0x006d
            return r1
        L_0x006d:
            if (r8 == r13) goto L_0x0086
            if (r8 == r14) goto L_0x0086
            r2 = 0
            if (r8 < 0) goto L_0x0078
            if (r12 >= r8) goto L_0x007f
        L_0x0078:
            if (r10 <= r8) goto L_0x007b
            goto L_0x0082
        L_0x007b:
            r10 = 159(0x9f, float:2.23E-43)
            if (r10 < r8) goto L_0x0082
        L_0x007f:
            r2 = r18
            goto L_0x0084
        L_0x0082:
            r2 = r16
        L_0x0084:
            if (r2 != 0) goto L_0x008b
        L_0x0086:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r8 != r2) goto L_0x008d
        L_0x008b:
            r2 = -1
            return r2
        L_0x008d:
            if (r8 >= r15) goto L_0x0092
            r2 = r18
            goto L_0x0094
        L_0x0092:
            r2 = r17
        L_0x0094:
            int r1 = r1 + r2
            r8 = r11
            r2 = r21
            r10 = 127(0x7f, float:1.78E-43)
            goto L_0x005c
        L_0x009c:
            r26 = r3
            goto L_0x071c
        L_0x00a0:
            r10 = 5
            r11 = r9
            r20 = 0
            int r10 = r11 >> r10
            r11 = -2
            if (r10 != r11) goto L_0x01f7
            r10 = r6
            r11 = 0
            int r15 = r8 + 1
            if (r5 > r15) goto L_0x00ef
            r15 = 65533(0xfffd, float:9.1831E-41)
            r21 = 0
            r22 = r15
            r23 = 0
            int r24 = r2 + 1
            if (r2 != r0) goto L_0x00bd
            return r1
        L_0x00bd:
            r2 = r22
            if (r2 == r13) goto L_0x00d7
            if (r2 == r14) goto L_0x00d7
            r13 = 0
            if (r2 < 0) goto L_0x00ca
            if (r12 >= r2) goto L_0x00d3
        L_0x00ca:
            r12 = 127(0x7f, float:1.78E-43)
            if (r12 <= r2) goto L_0x00cf
            goto L_0x00d5
        L_0x00cf:
            r12 = 159(0x9f, float:2.23E-43)
            if (r12 < r2) goto L_0x00d5
        L_0x00d3:
            r16 = r18
        L_0x00d5:
            if (r16 != 0) goto L_0x00dc
        L_0x00d7:
            r12 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r12) goto L_0x00de
        L_0x00dc:
            r12 = -1
            return r12
        L_0x00de:
            r12 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r12) goto L_0x00e4
            r17 = r18
        L_0x00e4:
            int r1 = r1 + r17
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r26 = r3
            r17 = r18
            goto L_0x01f1
        L_0x00ef:
            byte r15 = r10[r8]
            int r22 = r8 + 1
            byte r12 = r10[r22]
            r22 = 0
            r24 = 192(0xc0, float:2.69E-43)
            r25 = r12
            r26 = 0
            r14 = r25 & r24
            r13 = 128(0x80, float:1.794E-43)
            if (r14 != r13) goto L_0x0106
            r13 = r18
            goto L_0x0108
        L_0x0106:
            r13 = r16
        L_0x0108:
            if (r13 != 0) goto L_0x0157
            r13 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r21 = r13
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x0118
            return r1
        L_0x0118:
            r26 = r3
            r2 = r21
            r3 = 10
            if (r2 == r3) goto L_0x013f
            r3 = 13
            if (r2 == r3) goto L_0x013f
            r3 = 0
            if (r2 < 0) goto L_0x0130
            r21 = r3
            r3 = 31
            if (r3 >= r2) goto L_0x013b
            goto L_0x0132
        L_0x0130:
            r21 = r3
        L_0x0132:
            r3 = 127(0x7f, float:1.78E-43)
            if (r3 <= r2) goto L_0x0137
            goto L_0x013d
        L_0x0137:
            r3 = 159(0x9f, float:2.23E-43)
            if (r3 < r2) goto L_0x013d
        L_0x013b:
            r16 = r18
        L_0x013d:
            if (r16 != 0) goto L_0x0144
        L_0x013f:
            r3 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r3) goto L_0x0146
        L_0x0144:
            r3 = -1
            return r3
        L_0x0146:
            r3 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r3) goto L_0x014c
            r17 = r18
        L_0x014c:
            int r1 = r1 + r17
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r17 = r18
            r24 = r25
            goto L_0x01f1
        L_0x0157:
            r26 = r3
            r3 = r12 ^ 3968(0xf80, float:5.56E-42)
            int r13 = r15 << 6
            r3 = r3 ^ r13
            r13 = 128(0x80, float:1.794E-43)
            if (r3 >= r13) goto L_0x01ad
            r13 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r21 = r13
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x0172
            return r1
        L_0x0172:
            r2 = r21
            r4 = 10
            if (r2 == r4) goto L_0x0197
            r4 = 13
            if (r2 == r4) goto L_0x0197
            r4 = 0
            if (r2 < 0) goto L_0x0188
            r21 = r4
            r4 = 31
            if (r4 >= r2) goto L_0x0193
            goto L_0x018a
        L_0x0188:
            r21 = r4
        L_0x018a:
            r4 = 127(0x7f, float:1.78E-43)
            if (r4 <= r2) goto L_0x018f
            goto L_0x0195
        L_0x018f:
            r4 = 159(0x9f, float:2.23E-43)
            if (r4 < r2) goto L_0x0195
        L_0x0193:
            r16 = r18
        L_0x0195:
            if (r16 != 0) goto L_0x019c
        L_0x0197:
            r4 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r4) goto L_0x019e
        L_0x019c:
            r4 = -1
            return r4
        L_0x019e:
            r4 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r4) goto L_0x01a3
            goto L_0x01a5
        L_0x01a3:
            r18 = r17
        L_0x01a5:
            int r1 = r1 + r18
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r24 = r25
            goto L_0x01ef
        L_0x01ad:
            r4 = r3
            r13 = 0
            r14 = r4
            r21 = 0
            int r22 = r2 + 1
            if (r2 != r0) goto L_0x01b7
            return r1
        L_0x01b7:
            r2 = 10
            if (r14 == r2) goto L_0x01da
            r2 = 13
            if (r14 == r2) goto L_0x01da
            r2 = 0
            if (r14 < 0) goto L_0x01cb
            r24 = r2
            r2 = 31
            if (r2 >= r14) goto L_0x01d6
            goto L_0x01cd
        L_0x01cb:
            r24 = r2
        L_0x01cd:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r14) goto L_0x01d2
            goto L_0x01d8
        L_0x01d2:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r14) goto L_0x01d8
        L_0x01d6:
            r16 = r18
        L_0x01d8:
            if (r16 != 0) goto L_0x01df
        L_0x01da:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r14 != r2) goto L_0x01e1
        L_0x01df:
            r2 = -1
            return r2
        L_0x01e1:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r14 >= r2) goto L_0x01e6
            goto L_0x01e8
        L_0x01e6:
            r18 = r17
        L_0x01e8:
            int r1 = r1 + r18
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r24 = r22
        L_0x01ef:
        L_0x01f1:
            int r8 = r8 + r17
            r2 = r24
            goto L_0x071c
        L_0x01f7:
            r26 = r3
            r3 = 4
            r4 = r9
            r10 = 0
            int r3 = r4 >> r3
            if (r3 != r11) goto L_0x040f
            r3 = r6
            r11 = 0
            int r13 = r8 + 2
            if (r5 > r13) goto L_0x0264
            r4 = 65533(0xfffd, float:9.1831E-41)
            r10 = 0
            r12 = r4
            r13 = 0
            int r14 = r2 + 1
            if (r2 != r0) goto L_0x0211
            return r1
        L_0x0211:
            r2 = 10
            if (r12 == r2) goto L_0x0232
            r2 = 13
            if (r12 == r2) goto L_0x0232
            r2 = 0
            if (r12 < 0) goto L_0x0222
            r15 = 31
            if (r15 >= r12) goto L_0x022b
        L_0x0222:
            r15 = 127(0x7f, float:1.78E-43)
            if (r15 <= r12) goto L_0x0227
            goto L_0x022e
        L_0x0227:
            r15 = 159(0x9f, float:2.23E-43)
            if (r15 < r12) goto L_0x022e
        L_0x022b:
            r2 = r18
            goto L_0x0230
        L_0x022e:
            r2 = r16
        L_0x0230:
            if (r2 != 0) goto L_0x0237
        L_0x0232:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r12 != r2) goto L_0x0239
        L_0x0237:
            r2 = -1
            return r2
        L_0x0239:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r12 >= r2) goto L_0x0240
            r2 = r18
            goto L_0x0242
        L_0x0240:
            r2 = r17
        L_0x0242:
            int r1 = r1 + r2
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            int r2 = r8 + 1
            if (r5 <= r2) goto L_0x0260
            int r2 = r8 + 1
            byte r2 = r3[r2]
            r4 = 0
            r10 = 192(0xc0, float:2.69E-43)
            r12 = r2
            r13 = 0
            r10 = r10 & r12
            r12 = 128(0x80, float:1.794E-43)
            if (r10 != r12) goto L_0x025a
            r16 = r18
        L_0x025a:
            if (r16 != 0) goto L_0x025e
            goto L_0x0260
        L_0x025e:
            goto L_0x040a
        L_0x0260:
            r17 = r18
            goto L_0x040a
        L_0x0264:
            byte r13 = r3[r8]
            int r14 = r8 + 1
            byte r14 = r3[r14]
            r15 = 0
            r22 = 192(0xc0, float:2.69E-43)
            r25 = r14
            r27 = 0
            r12 = r25 & r22
            r10 = 128(0x80, float:1.794E-43)
            if (r12 != r10) goto L_0x027a
            r10 = r18
            goto L_0x027c
        L_0x027a:
            r10 = r16
        L_0x027c:
            if (r10 != 0) goto L_0x02c5
            r4 = 65533(0xfffd, float:9.1831E-41)
            r10 = 0
            r12 = r4
            r15 = 0
            int r21 = r2 + 1
            if (r2 != r0) goto L_0x028a
            return r1
        L_0x028a:
            r2 = 10
            if (r12 == r2) goto L_0x02ad
            r2 = 13
            if (r12 == r2) goto L_0x02ad
            r2 = 0
            if (r12 < 0) goto L_0x029e
            r22 = r2
            r2 = 31
            if (r2 >= r12) goto L_0x02a9
            goto L_0x02a0
        L_0x029e:
            r22 = r2
        L_0x02a0:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r12) goto L_0x02a5
            goto L_0x02ab
        L_0x02a5:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r12) goto L_0x02ab
        L_0x02a9:
            r16 = r18
        L_0x02ab:
            if (r16 != 0) goto L_0x02b2
        L_0x02ad:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r12 != r2) goto L_0x02b4
        L_0x02b2:
            r2 = -1
            return r2
        L_0x02b4:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r12 >= r2) goto L_0x02ba
            r17 = r18
        L_0x02ba:
            int r1 = r1 + r17
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r17 = r18
            r14 = r21
            goto L_0x040a
        L_0x02c5:
            int r10 = r8 + 2
            byte r10 = r3[r10]
            r12 = 0
            r15 = 192(0xc0, float:2.69E-43)
            r25 = r10
            r27 = 0
            r15 = r25 & r15
            r4 = 128(0x80, float:1.794E-43)
            if (r15 != r4) goto L_0x02d9
            r4 = r18
            goto L_0x02db
        L_0x02d9:
            r4 = r16
        L_0x02db:
            if (r4 != 0) goto L_0x0324
            r4 = 65533(0xfffd, float:9.1831E-41)
            r12 = 0
            r15 = r4
            r21 = 0
            int r22 = r2 + 1
            if (r2 != r0) goto L_0x02ea
            return r1
        L_0x02ea:
            r2 = 10
            if (r15 == r2) goto L_0x030d
            r2 = 13
            if (r15 == r2) goto L_0x030d
            r2 = 0
            if (r15 < 0) goto L_0x02fe
            r24 = r2
            r2 = 31
            if (r2 >= r15) goto L_0x0309
            goto L_0x0300
        L_0x02fe:
            r24 = r2
        L_0x0300:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r15) goto L_0x0305
            goto L_0x030b
        L_0x0305:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r15) goto L_0x030b
        L_0x0309:
            r16 = r18
        L_0x030b:
            if (r16 != 0) goto L_0x0312
        L_0x030d:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x0314
        L_0x0312:
            r2 = -1
            return r2
        L_0x0314:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x0319
            goto L_0x031b
        L_0x0319:
            r18 = r17
        L_0x031b:
            int r1 = r1 + r18
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r14 = r22
            goto L_0x040a
        L_0x0324:
            r4 = -123008(0xfffffffffffe1f80, float:NaN)
            r4 = r4 ^ r10
            int r12 = r14 << 6
            r4 = r4 ^ r12
            int r12 = r13 << 12
            r4 = r4 ^ r12
            r12 = 2048(0x800, float:2.87E-42)
            if (r4 >= r12) goto L_0x037e
            r12 = 65533(0xfffd, float:9.1831E-41)
            r15 = 0
            r21 = r12
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x0343
            return r1
        L_0x0343:
            r2 = r21
            r21 = r3
            r3 = 10
            if (r2 == r3) goto L_0x036a
            r3 = 13
            if (r2 == r3) goto L_0x036a
            r3 = 0
            if (r2 < 0) goto L_0x035b
            r24 = r3
            r3 = 31
            if (r3 >= r2) goto L_0x0366
            goto L_0x035d
        L_0x035b:
            r24 = r3
        L_0x035d:
            r3 = 127(0x7f, float:1.78E-43)
            if (r3 <= r2) goto L_0x0362
            goto L_0x0368
        L_0x0362:
            r3 = 159(0x9f, float:2.23E-43)
            if (r3 < r2) goto L_0x0368
        L_0x0366:
            r16 = r18
        L_0x0368:
            if (r16 != 0) goto L_0x036f
        L_0x036a:
            r3 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r3) goto L_0x0371
        L_0x036f:
            r3 = -1
            return r3
        L_0x0371:
            r3 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r3) goto L_0x0377
            r17 = r18
        L_0x0377:
            int r1 = r1 + r17
        L_0x037a:
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            goto L_0x0405
        L_0x037e:
            r21 = r3
            r3 = 55296(0xd800, float:7.7486E-41)
            if (r3 <= r4) goto L_0x0386
            goto L_0x03c6
        L_0x0386:
            r3 = 57343(0xdfff, float:8.0355E-41)
            if (r3 < r4) goto L_0x03c6
            r3 = 65533(0xfffd, float:9.1831E-41)
            r12 = 0
            r15 = r3
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x0397
            return r1
        L_0x0397:
            r2 = 10
            if (r15 == r2) goto L_0x03ba
            r2 = 13
            if (r15 == r2) goto L_0x03ba
            r2 = 0
            if (r15 < 0) goto L_0x03ab
            r24 = r2
            r2 = 31
            if (r2 >= r15) goto L_0x03b6
            goto L_0x03ad
        L_0x03ab:
            r24 = r2
        L_0x03ad:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r15) goto L_0x03b2
            goto L_0x03b8
        L_0x03b2:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r15) goto L_0x03b8
        L_0x03b6:
            r16 = r18
        L_0x03b8:
            if (r16 != 0) goto L_0x03bf
        L_0x03ba:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x03c1
        L_0x03bf:
            r2 = -1
            return r2
        L_0x03c1:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x0400
            goto L_0x03fe
        L_0x03c6:
            r3 = r4
            r12 = 0
            r15 = r3
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x03d0
            return r1
        L_0x03d0:
            r2 = 10
            if (r15 == r2) goto L_0x03f3
            r2 = 13
            if (r15 == r2) goto L_0x03f3
            r2 = 0
            if (r15 < 0) goto L_0x03e4
            r24 = r2
            r2 = 31
            if (r2 >= r15) goto L_0x03ef
            goto L_0x03e6
        L_0x03e4:
            r24 = r2
        L_0x03e6:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r15) goto L_0x03eb
            goto L_0x03f1
        L_0x03eb:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r15) goto L_0x03f1
        L_0x03ef:
            r16 = r18
        L_0x03f1:
            if (r16 != 0) goto L_0x03f8
        L_0x03f3:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x03fa
        L_0x03f8:
            r2 = -1
            return r2
        L_0x03fa:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x0400
        L_0x03fe:
            r17 = r18
        L_0x0400:
            int r1 = r1 + r17
            goto L_0x037a
        L_0x0405:
            r14 = r25
            r17 = 3
        L_0x040a:
            int r8 = r8 + r17
            r2 = r14
            goto L_0x071c
        L_0x040f:
            r3 = 3
            r4 = r9
            r10 = 0
            int r3 = r4 >> r3
            if (r3 != r11) goto L_0x06e2
            r3 = r6
            r4 = 0
            int r10 = r8 + 3
            if (r5 > r10) goto L_0x0499
            r10 = 65533(0xfffd, float:9.1831E-41)
            r11 = 0
            r12 = r10
            r13 = 0
            int r14 = r2 + 1
            if (r2 != r0) goto L_0x0427
            return r1
        L_0x0427:
            r2 = 10
            if (r12 == r2) goto L_0x0448
            r2 = 13
            if (r12 == r2) goto L_0x0448
            r2 = 0
            if (r12 < 0) goto L_0x0438
            r15 = 31
            if (r15 >= r12) goto L_0x0441
        L_0x0438:
            r15 = 127(0x7f, float:1.78E-43)
            if (r15 <= r12) goto L_0x043d
            goto L_0x0444
        L_0x043d:
            r15 = 159(0x9f, float:2.23E-43)
            if (r15 < r12) goto L_0x0444
        L_0x0441:
            r2 = r18
            goto L_0x0446
        L_0x0444:
            r2 = r16
        L_0x0446:
            if (r2 != 0) goto L_0x044d
        L_0x0448:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r12 != r2) goto L_0x044f
        L_0x044d:
            r2 = -1
            return r2
        L_0x044f:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r12 >= r2) goto L_0x0456
            r2 = r18
            goto L_0x0458
        L_0x0456:
            r2 = r17
        L_0x0458:
            int r1 = r1 + r2
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            int r2 = r8 + 1
            if (r5 <= r2) goto L_0x0495
            int r2 = r8 + 1
            byte r2 = r3[r2]
            r10 = 0
            r11 = 192(0xc0, float:2.69E-43)
            r12 = r2
            r13 = 0
            r11 = r11 & r12
            r12 = 128(0x80, float:1.794E-43)
            if (r11 != r12) goto L_0x0471
            r11 = r18
            goto L_0x0473
        L_0x0471:
            r11 = r16
        L_0x0473:
            if (r11 != 0) goto L_0x0477
            goto L_0x0495
        L_0x0477:
            int r2 = r8 + 2
            if (r5 <= r2) goto L_0x0493
            int r2 = r8 + 2
            byte r2 = r3[r2]
            r10 = 0
            r11 = 192(0xc0, float:2.69E-43)
            r12 = r2
            r13 = 0
            r11 = r11 & r12
            r12 = 128(0x80, float:1.794E-43)
            if (r11 != r12) goto L_0x048b
            r16 = r18
        L_0x048b:
            if (r16 != 0) goto L_0x048f
            goto L_0x0493
        L_0x048f:
            r17 = 3
            goto L_0x06de
        L_0x0493:
            goto L_0x06de
        L_0x0495:
            r17 = r18
            goto L_0x06de
        L_0x0499:
            byte r10 = r3[r8]
            int r11 = r8 + 1
            byte r11 = r3[r11]
            r12 = 0
            r13 = 192(0xc0, float:2.69E-43)
            r14 = r11
            r15 = 0
            r13 = r13 & r14
            r14 = 128(0x80, float:1.794E-43)
            if (r13 != r14) goto L_0x04ac
            r13 = r18
            goto L_0x04ae
        L_0x04ac:
            r13 = r16
        L_0x04ae:
            if (r13 != 0) goto L_0x04f7
            r12 = 65533(0xfffd, float:9.1831E-41)
            r13 = 0
            r14 = r12
            r15 = 0
            int r21 = r2 + 1
            if (r2 != r0) goto L_0x04bc
            return r1
        L_0x04bc:
            r2 = 10
            if (r14 == r2) goto L_0x04df
            r2 = 13
            if (r14 == r2) goto L_0x04df
            r2 = 0
            if (r14 < 0) goto L_0x04d0
            r22 = r2
            r2 = 31
            if (r2 >= r14) goto L_0x04db
            goto L_0x04d2
        L_0x04d0:
            r22 = r2
        L_0x04d2:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r14) goto L_0x04d7
            goto L_0x04dd
        L_0x04d7:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r14) goto L_0x04dd
        L_0x04db:
            r16 = r18
        L_0x04dd:
            if (r16 != 0) goto L_0x04e4
        L_0x04df:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r14 != r2) goto L_0x04e6
        L_0x04e4:
            r2 = -1
            return r2
        L_0x04e6:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r14 >= r2) goto L_0x04ec
            r17 = r18
        L_0x04ec:
            int r1 = r1 + r17
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r17 = r18
            r14 = r21
            goto L_0x06de
        L_0x04f7:
            int r12 = r8 + 2
            byte r12 = r3[r12]
            r13 = 0
            r14 = 192(0xc0, float:2.69E-43)
            r15 = r12
            r27 = 0
            r14 = r14 & r15
            r15 = 128(0x80, float:1.794E-43)
            if (r14 != r15) goto L_0x0509
            r14 = r18
            goto L_0x050b
        L_0x0509:
            r14 = r16
        L_0x050b:
            if (r14 != 0) goto L_0x0554
            r13 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r15 = r13
            r21 = 0
            int r22 = r2 + 1
            if (r2 != r0) goto L_0x051a
            return r1
        L_0x051a:
            r2 = 10
            if (r15 == r2) goto L_0x053d
            r2 = 13
            if (r15 == r2) goto L_0x053d
            r2 = 0
            if (r15 < 0) goto L_0x052e
            r24 = r2
            r2 = 31
            if (r2 >= r15) goto L_0x0539
            goto L_0x0530
        L_0x052e:
            r24 = r2
        L_0x0530:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r15) goto L_0x0535
            goto L_0x053b
        L_0x0535:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r15) goto L_0x053b
        L_0x0539:
            r16 = r18
        L_0x053b:
            if (r16 != 0) goto L_0x0542
        L_0x053d:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x0544
        L_0x0542:
            r2 = -1
            return r2
        L_0x0544:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x0549
            goto L_0x054b
        L_0x0549:
            r18 = r17
        L_0x054b:
            int r1 = r1 + r18
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r14 = r22
            goto L_0x06de
        L_0x0554:
            int r13 = r8 + 3
            byte r13 = r3[r13]
            r14 = 0
            r15 = 192(0xc0, float:2.69E-43)
            r27 = r13
            r28 = 0
            r15 = r27 & r15
            r27 = r3
            r3 = 128(0x80, float:1.794E-43)
            if (r15 != r3) goto L_0x056a
            r3 = r18
            goto L_0x056c
        L_0x056a:
            r3 = r16
        L_0x056c:
            if (r3 != 0) goto L_0x05b6
            r3 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r15 = r3
            r21 = 0
            int r22 = r2 + 1
            if (r2 != r0) goto L_0x057b
            return r1
        L_0x057b:
            r2 = 10
            if (r15 == r2) goto L_0x059e
            r2 = 13
            if (r15 == r2) goto L_0x059e
            r2 = 0
            if (r15 < 0) goto L_0x058f
            r24 = r2
            r2 = 31
            if (r2 >= r15) goto L_0x059a
            goto L_0x0591
        L_0x058f:
            r24 = r2
        L_0x0591:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r15) goto L_0x0596
            goto L_0x059c
        L_0x0596:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r15) goto L_0x059c
        L_0x059a:
            r16 = r18
        L_0x059c:
            if (r16 != 0) goto L_0x05a3
        L_0x059e:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x05a5
        L_0x05a3:
            r2 = -1
            return r2
        L_0x05a5:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x05ab
            r17 = r18
        L_0x05ab:
            int r1 = r1 + r17
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r14 = r22
            r17 = 3
            goto L_0x06de
        L_0x05b6:
            r3 = 3678080(0x381f80, float:5.154088E-39)
            r3 = r3 ^ r13
            int r14 = r12 << 6
            r3 = r3 ^ r14
            int r14 = r11 << 12
            r3 = r3 ^ r14
            int r14 = r10 << 18
            r3 = r3 ^ r14
            r14 = 1114111(0x10ffff, float:1.561202E-39)
            if (r3 <= r14) goto L_0x0616
            r14 = 65533(0xfffd, float:9.1831E-41)
            r15 = 0
            r21 = r14
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x05d9
            return r1
        L_0x05d9:
            r2 = r21
            r21 = r4
            r4 = 10
            if (r2 == r4) goto L_0x0600
            r4 = 13
            if (r2 == r4) goto L_0x0600
            r4 = 0
            if (r2 < 0) goto L_0x05f1
            r24 = r4
            r4 = 31
            if (r4 >= r2) goto L_0x05fc
            goto L_0x05f3
        L_0x05f1:
            r24 = r4
        L_0x05f3:
            r4 = 127(0x7f, float:1.78E-43)
            if (r4 <= r2) goto L_0x05f8
            goto L_0x05fe
        L_0x05f8:
            r4 = 159(0x9f, float:2.23E-43)
            if (r4 < r2) goto L_0x05fe
        L_0x05fc:
            r16 = r18
        L_0x05fe:
            if (r16 != 0) goto L_0x0605
        L_0x0600:
            r4 = 65533(0xfffd, float:9.1831E-41)
            if (r2 != r4) goto L_0x0607
        L_0x0605:
            r4 = -1
            return r4
        L_0x0607:
            r4 = 65536(0x10000, float:9.18355E-41)
            if (r2 >= r4) goto L_0x060d
            r17 = r18
        L_0x060d:
            int r1 = r1 + r17
        L_0x0610:
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            r14 = r25
            goto L_0x06db
        L_0x0616:
            r21 = r4
            r4 = 55296(0xd800, float:7.7486E-41)
            if (r4 <= r3) goto L_0x061e
            goto L_0x0663
        L_0x061e:
            r4 = 57343(0xdfff, float:8.0355E-41)
            if (r4 < r3) goto L_0x0663
            r4 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r15 = r4
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x062f
            return r1
        L_0x062f:
            r2 = 10
            if (r15 == r2) goto L_0x0652
            r2 = 13
            if (r15 == r2) goto L_0x0652
            r2 = 0
            if (r15 < 0) goto L_0x0643
            r24 = r2
            r2 = 31
            if (r2 >= r15) goto L_0x064e
            goto L_0x0645
        L_0x0643:
            r24 = r2
        L_0x0645:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r15) goto L_0x064a
            goto L_0x0650
        L_0x064a:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r15) goto L_0x0650
        L_0x064e:
            r16 = r18
        L_0x0650:
            if (r16 != 0) goto L_0x0657
        L_0x0652:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x0659
        L_0x0657:
            r2 = -1
            return r2
        L_0x0659:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x065f
        L_0x065d:
            r17 = r18
        L_0x065f:
            int r1 = r1 + r17
            goto L_0x0610
        L_0x0663:
            r4 = 65536(0x10000, float:9.18355E-41)
            if (r3 >= r4) goto L_0x06a2
            r4 = 65533(0xfffd, float:9.1831E-41)
            r14 = 0
            r15 = r4
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x0673
            return r1
        L_0x0673:
            r2 = 10
            if (r15 == r2) goto L_0x0696
            r2 = 13
            if (r15 == r2) goto L_0x0696
            r2 = 0
            if (r15 < 0) goto L_0x0687
            r24 = r2
            r2 = 31
            if (r2 >= r15) goto L_0x0692
            goto L_0x0689
        L_0x0687:
            r24 = r2
        L_0x0689:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r15) goto L_0x068e
            goto L_0x0694
        L_0x068e:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r15) goto L_0x0694
        L_0x0692:
            r16 = r18
        L_0x0694:
            if (r16 != 0) goto L_0x069b
        L_0x0696:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x069d
        L_0x069b:
            r2 = -1
            return r2
        L_0x069d:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x065f
            goto L_0x065d
        L_0x06a2:
            r4 = r3
            r14 = 0
            r15 = r4
            r22 = 0
            int r25 = r2 + 1
            if (r2 != r0) goto L_0x06ac
            return r1
        L_0x06ac:
            r2 = 10
            if (r15 == r2) goto L_0x06cf
            r2 = 13
            if (r15 == r2) goto L_0x06cf
            r2 = 0
            if (r15 < 0) goto L_0x06c0
            r24 = r2
            r2 = 31
            if (r2 >= r15) goto L_0x06cb
            goto L_0x06c2
        L_0x06c0:
            r24 = r2
        L_0x06c2:
            r2 = 127(0x7f, float:1.78E-43)
            if (r2 <= r15) goto L_0x06c7
            goto L_0x06cd
        L_0x06c7:
            r2 = 159(0x9f, float:2.23E-43)
            if (r2 < r15) goto L_0x06cd
        L_0x06cb:
            r16 = r18
        L_0x06cd:
            if (r16 != 0) goto L_0x06d4
        L_0x06cf:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r15 != r2) goto L_0x06d6
        L_0x06d4:
            r2 = -1
            return r2
        L_0x06d6:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r15 >= r2) goto L_0x065f
            goto L_0x065d
        L_0x06db:
            r17 = 4
        L_0x06de:
            int r8 = r8 + r17
            r2 = r14
            goto L_0x071c
        L_0x06e2:
            r3 = 65533(0xfffd, float:9.1831E-41)
            r4 = 0
            int r10 = r2 + 1
            if (r2 != r0) goto L_0x06eb
            return r1
        L_0x06eb:
            r2 = 10
            if (r3 == r2) goto L_0x0709
            r2 = 13
            if (r3 == r2) goto L_0x0709
            r2 = 0
            if (r3 < 0) goto L_0x06fc
            r11 = 31
            if (r11 >= r3) goto L_0x0705
        L_0x06fc:
            r11 = 127(0x7f, float:1.78E-43)
            if (r11 <= r3) goto L_0x0701
            goto L_0x0707
        L_0x0701:
            r11 = 159(0x9f, float:2.23E-43)
            if (r11 < r3) goto L_0x0707
        L_0x0705:
            r16 = r18
        L_0x0707:
            if (r16 != 0) goto L_0x070e
        L_0x0709:
            r2 = 65533(0xfffd, float:9.1831E-41)
            if (r3 != r2) goto L_0x0710
        L_0x070e:
            r2 = -1
            return r2
        L_0x0710:
            r2 = 65536(0x10000, float:9.18355E-41)
            if (r3 >= r2) goto L_0x0716
            r17 = r18
        L_0x0716:
            int r1 = r1 + r17
            int r8 = r8 + 1
            r2 = r10
        L_0x071c:
            r4 = r29
            r3 = r26
            goto L_0x000c
        L_0x0723:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal.ByteStringKt.codePointIndexToCharIndex(byte[], int):int");
    }
}
