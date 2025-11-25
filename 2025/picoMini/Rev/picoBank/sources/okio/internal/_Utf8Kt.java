package okio.internal;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlin.jvm.internal.Intrinsics;
import okio.Utf8;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0012\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\u001e\u0010\u0003\u001a\u00020\u0002*\u00020\u00012\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005¨\u0006\u0007"}, d2 = {"commonAsUtf8ToByteArray", "", "", "commonToUtf8String", "beginIndex", "", "endIndex", "okio"}, k = 2, mv = {1, 4, 0})
/* compiled from: -Utf8.kt */
public final class _Utf8Kt {
    public static /* synthetic */ String commonToUtf8String$default(byte[] bArr, int i, int i2, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            i = 0;
        }
        if ((i3 & 2) != 0) {
            i2 = bArr.length;
        }
        return commonToUtf8String(bArr, i, i2);
    }

    public static final String commonToUtf8String(byte[] $this$commonToUtf8String, int beginIndex, int endIndex) {
        int $i$f$processUtf16Chars;
        int length;
        int i;
        int length2;
        int length3;
        int length4;
        int length5;
        int length6;
        int length7;
        int i2;
        int length8;
        int length9;
        byte b1$iv$iv;
        int length10;
        byte[] bArr = $this$commonToUtf8String;
        int i3 = beginIndex;
        int i4 = endIndex;
        Intrinsics.checkNotNullParameter(bArr, "$this$commonToUtf8String");
        if (i3 < 0 || i4 > bArr.length || i3 > i4) {
            throw new ArrayIndexOutOfBoundsException("size=" + bArr.length + " beginIndex=" + i3 + " endIndex=" + i4);
        }
        char[] chars = new char[(i4 - i3)];
        int length11 = 0;
        byte[] $this$processUtf16Chars$iv = $this$commonToUtf8String;
        int $i$f$processUtf16Chars2 = 0;
        int index$iv = beginIndex;
        while (index$iv < i4) {
            byte b0$iv = $this$processUtf16Chars$iv[index$iv];
            if (b0$iv >= 0) {
                int length12 = length11 + 1;
                chars[length11] = (char) b0$iv;
                index$iv++;
                while (index$iv < i4 && $this$processUtf16Chars$iv[index$iv] >= 0) {
                    chars[length12] = (char) $this$processUtf16Chars$iv[index$iv];
                    index$iv++;
                    length12++;
                }
                $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                length11 = length12;
            } else if ((b0$iv >> 5) == -2) {
                byte[] $this$process2Utf8Bytes$iv$iv = $this$processUtf16Chars$iv;
                if (i4 <= index$iv + 1) {
                    int length13 = length11 + 1;
                    chars[length11] = (char) Utf8.REPLACEMENT_CODE_POINT;
                    Unit unit = Unit.INSTANCE;
                    length9 = length13;
                    b1$iv$iv = 1;
                    $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                } else {
                    byte b0$iv$iv = $this$process2Utf8Bytes$iv$iv[index$iv];
                    byte b1$iv$iv2 = $this$process2Utf8Bytes$iv$iv[index$iv + 1];
                    if (!((b1$iv$iv2 & 192) == 128)) {
                        length9 = length11 + 1;
                        chars[length11] = (char) Utf8.REPLACEMENT_CODE_POINT;
                        Unit unit2 = Unit.INSTANCE;
                        $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                        b1$iv$iv = 1;
                    } else {
                        int codePoint$iv$iv = (b1$iv$iv2 ^ Utf8.MASK_2BYTES) ^ (b0$iv$iv << 6);
                        if (codePoint$iv$iv < 128) {
                            $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                            length10 = length11 + 1;
                            chars[length11] = (char) Utf8.REPLACEMENT_CODE_POINT;
                        } else {
                            $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                            length10 = length11 + 1;
                            chars[length11] = (char) codePoint$iv$iv;
                        }
                        Unit unit3 = Unit.INSTANCE;
                        length9 = length10;
                        b1$iv$iv = 2;
                    }
                }
                index$iv += b1$iv$iv;
                length11 = length9;
            } else {
                $i$f$processUtf16Chars = $i$f$processUtf16Chars2;
                if ((b0$iv >> 4) == -2) {
                    byte[] $this$process3Utf8Bytes$iv$iv = $this$processUtf16Chars$iv;
                    if (i4 <= index$iv + 2) {
                        length7 = length11 + 1;
                        chars[length11] = (char) Utf8.REPLACEMENT_CODE_POINT;
                        Unit unit4 = Unit.INSTANCE;
                        if (i4 > index$iv + 1) {
                            if ((192 & $this$process3Utf8Bytes$iv$iv[index$iv + 1]) == 128) {
                                i2 = 2;
                            }
                        }
                        i2 = 1;
                    } else {
                        byte b0$iv$iv2 = $this$process3Utf8Bytes$iv$iv[index$iv];
                        byte b1$iv$iv3 = $this$process3Utf8Bytes$iv$iv[index$iv + 1];
                        if (!((b1$iv$iv3 & 192) == 128)) {
                            int length14 = length11 + 1;
                            chars[length11] = (char) Utf8.REPLACEMENT_CODE_POINT;
                            Unit unit5 = Unit.INSTANCE;
                            length7 = length14;
                            i2 = 1;
                        } else {
                            byte b2$iv$iv = $this$process3Utf8Bytes$iv$iv[index$iv + 2];
                            if (!((b2$iv$iv & 192) == 128)) {
                                int length15 = length11 + 1;
                                chars[length11] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                Unit unit6 = Unit.INSTANCE;
                                length7 = length15;
                                i2 = 2;
                            } else {
                                int codePoint$iv$iv2 = ((-123008 ^ b2$iv$iv) ^ (b1$iv$iv3 << 6)) ^ (b0$iv$iv2 << 12);
                                if (codePoint$iv$iv2 < 2048) {
                                    length8 = length11 + 1;
                                    chars[length11] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                } else if (55296 <= codePoint$iv$iv2 && 57343 >= codePoint$iv$iv2) {
                                    length8 = length11 + 1;
                                    chars[length11] = (char) Utf8.REPLACEMENT_CODE_POINT;
                                } else {
                                    length8 = length11 + 1;
                                    chars[length11] = (char) codePoint$iv$iv2;
                                }
                                Unit unit7 = Unit.INSTANCE;
                                length7 = length8;
                                i2 = 3;
                            }
                        }
                    }
                    index$iv += i2;
                    length11 = length7;
                } else if ((b0$iv >> 3) == -2) {
                    byte[] $this$process4Utf8Bytes$iv$iv = $this$processUtf16Chars$iv;
                    if (i4 <= index$iv + 3) {
                        if (65533 != 65533) {
                            int length16 = length11 + 1;
                            chars[length11] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                            length = length16 + 1;
                            chars[length16] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                        } else {
                            chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                            length = length11 + 1;
                        }
                        Unit unit8 = Unit.INSTANCE;
                        if (i4 > index$iv + 1) {
                            if ((192 & $this$process4Utf8Bytes$iv$iv[index$iv + 1]) == 128) {
                                if (i4 > index$iv + 2) {
                                    if ((192 & $this$process4Utf8Bytes$iv$iv[index$iv + 2]) == 128) {
                                        i = 3;
                                    }
                                }
                                i = 2;
                            }
                        }
                        i = 1;
                    } else {
                        byte b0$iv$iv3 = $this$process4Utf8Bytes$iv$iv[index$iv];
                        byte b1$iv$iv4 = $this$process4Utf8Bytes$iv$iv[index$iv + 1];
                        if (!((b1$iv$iv4 & 192) == 128)) {
                            if (65533 != 65533) {
                                int length17 = length11 + 1;
                                chars[length11] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                length6 = length17 + 1;
                                chars[length17] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                            } else {
                                chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                                length6 = length11 + 1;
                            }
                            Unit unit9 = Unit.INSTANCE;
                            i = 1;
                        } else {
                            byte b2$iv$iv2 = $this$process4Utf8Bytes$iv$iv[index$iv + 2];
                            if (!((b2$iv$iv2 & 192) == 128)) {
                                if (65533 != 65533) {
                                    int length18 = length11 + 1;
                                    chars[length11] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                    chars[length18] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                    length5 = length18 + 1;
                                } else {
                                    chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                                    length5 = length11 + 1;
                                }
                                Unit unit10 = Unit.INSTANCE;
                                i = 2;
                            } else {
                                byte b3$iv$iv = $this$process4Utf8Bytes$iv$iv[index$iv + 3];
                                if (!((b3$iv$iv & 192) == 128)) {
                                    if (65533 != 65533) {
                                        int length19 = length11 + 1;
                                        chars[length11] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                        length4 = length19 + 1;
                                        chars[length19] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                    } else {
                                        chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                                        length4 = length11 + 1;
                                    }
                                    Unit unit11 = Unit.INSTANCE;
                                    length = length4;
                                    i = 3;
                                } else {
                                    int codePoint$iv$iv3 = (((3678080 ^ b3$iv$iv) ^ (b2$iv$iv2 << 6)) ^ (b1$iv$iv4 << 12)) ^ (b0$iv$iv3 << 18);
                                    if (codePoint$iv$iv3 > 1114111) {
                                        if (65533 != 65533) {
                                            int length20 = length11 + 1;
                                            chars[length11] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                            length3 = length20 + 1;
                                            chars[length20] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                            Unit unit12 = Unit.INSTANCE;
                                            i = 4;
                                            length = length3;
                                        } else {
                                            length2 = length11 + 1;
                                            chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                                        }
                                    } else if (55296 <= codePoint$iv$iv3 && 57343 >= codePoint$iv$iv3) {
                                        if (65533 != 65533) {
                                            int length21 = length11 + 1;
                                            chars[length11] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                            length3 = length21 + 1;
                                            chars[length21] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                            Unit unit122 = Unit.INSTANCE;
                                            i = 4;
                                            length = length3;
                                        } else {
                                            length2 = length11 + 1;
                                            chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                                        }
                                    } else if (codePoint$iv$iv3 >= 65536) {
                                        int codePoint$iv = codePoint$iv$iv3;
                                        if (codePoint$iv != 65533) {
                                            int length22 = length11 + 1;
                                            chars[length11] = (char) ((codePoint$iv >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                            length3 = length22 + 1;
                                            chars[length22] = (char) ((codePoint$iv & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                            Unit unit1222 = Unit.INSTANCE;
                                            i = 4;
                                            length = length3;
                                        } else {
                                            length2 = length11 + 1;
                                            chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                                        }
                                    } else if (65533 != 65533) {
                                        int length23 = length11 + 1;
                                        chars[length11] = (char) ((Utf8.REPLACEMENT_CODE_POINT >>> 10) + Utf8.HIGH_SURROGATE_HEADER);
                                        length3 = length23 + 1;
                                        chars[length23] = (char) ((65533 & 1023) + Utf8.LOG_SURROGATE_HEADER);
                                        Unit unit12222 = Unit.INSTANCE;
                                        i = 4;
                                        length = length3;
                                    } else {
                                        length2 = length11 + 1;
                                        chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                                    }
                                    length3 = length2;
                                    Unit unit122222 = Unit.INSTANCE;
                                    i = 4;
                                    length = length3;
                                }
                            }
                        }
                    }
                    index$iv += i;
                    length11 = length;
                } else {
                    chars[length11] = Utf8.REPLACEMENT_CHARACTER;
                    index$iv++;
                    length11++;
                }
            }
            $i$f$processUtf16Chars2 = $i$f$processUtf16Chars;
        }
        return new String(chars, 0, length11);
    }

    public static final byte[] commonAsUtf8ToByteArray(String $this$commonAsUtf8ToByteArray) {
        char charAt;
        Intrinsics.checkNotNullParameter($this$commonAsUtf8ToByteArray, "$this$commonAsUtf8ToByteArray");
        byte[] bytes = new byte[($this$commonAsUtf8ToByteArray.length() * 4)];
        int length = $this$commonAsUtf8ToByteArray.length();
        for (int index = 0; index < length; index++) {
            char b0 = $this$commonAsUtf8ToByteArray.charAt(index);
            if (Intrinsics.compare((int) b0, 128) >= 0) {
                int index$iv = index;
                int endIndex$iv = $this$commonAsUtf8ToByteArray.length();
                String $this$processUtf8Bytes$iv = $this$commonAsUtf8ToByteArray;
                int index$iv2 = index;
                while (index$iv2 < endIndex$iv) {
                    byte c$iv = $this$processUtf8Bytes$iv.charAt(index$iv2);
                    if (Intrinsics.compare((int) c$iv, 128) < 0) {
                        int size = index$iv + 1;
                        bytes[index$iv] = (byte) c$iv;
                        index$iv2++;
                        while (index$iv2 < endIndex$iv && Intrinsics.compare((int) $this$processUtf8Bytes$iv.charAt(index$iv2), 128) < 0) {
                            bytes[size] = (byte) $this$processUtf8Bytes$iv.charAt(index$iv2);
                            index$iv2++;
                            size++;
                        }
                        index$iv = size;
                    } else if (Intrinsics.compare((int) c$iv, 2048) < 0) {
                        int size2 = index$iv + 1;
                        bytes[index$iv] = (byte) ((c$iv >> 6) | 192);
                        bytes[size2] = (byte) ((c$iv & Utf8.REPLACEMENT_BYTE) | ByteCompanionObject.MIN_VALUE);
                        index$iv2++;
                        index$iv = size2 + 1;
                    } else if (55296 > c$iv || 57343 < c$iv) {
                        int size3 = index$iv + 1;
                        bytes[index$iv] = (byte) ((c$iv >> 12) | 224);
                        int size4 = size3 + 1;
                        bytes[size3] = (byte) (((c$iv >> 6) & 63) | 128);
                        bytes[size4] = (byte) ((c$iv & Utf8.REPLACEMENT_BYTE) | ByteCompanionObject.MIN_VALUE);
                        index$iv2++;
                        index$iv = size4 + 1;
                    } else if (Intrinsics.compare((int) c$iv, 56319) > 0 || endIndex$iv <= index$iv2 + 1 || 56320 > (charAt = $this$processUtf8Bytes$iv.charAt(index$iv2 + 1)) || 57343 < charAt) {
                        bytes[index$iv] = Utf8.REPLACEMENT_BYTE;
                        index$iv2++;
                        index$iv++;
                    } else {
                        int codePoint$iv = ((c$iv << 10) + $this$processUtf8Bytes$iv.charAt(index$iv2 + 1)) - 56613888;
                        int size5 = index$iv + 1;
                        bytes[index$iv] = (byte) ((codePoint$iv >> 18) | 240);
                        int size6 = size5 + 1;
                        bytes[size5] = (byte) (((codePoint$iv >> 12) & 63) | 128);
                        int size7 = size6 + 1;
                        bytes[size6] = (byte) (((codePoint$iv >> 6) & 63) | 128);
                        bytes[size7] = (byte) ((codePoint$iv & Utf8.REPLACEMENT_BYTE) | ByteCompanionObject.MIN_VALUE);
                        index$iv2 += 2;
                        index$iv = size7 + 1;
                    }
                }
                byte[] copyOf = Arrays.copyOf(bytes, index$iv);
                Intrinsics.checkNotNullExpressionValue(copyOf, "java.util.Arrays.copyOf(this, newSize)");
                return copyOf;
            }
            bytes[index] = (byte) b0;
        }
        byte[] copyOf2 = Arrays.copyOf(bytes, $this$commonAsUtf8ToByteArray.length());
        Intrinsics.checkNotNullExpressionValue(copyOf2, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf2;
    }
}
