package okhttp3.internal.http;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Challenge;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.ByteString;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000R\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0005\n\u0000\u001a\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u001a\u0018\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b*\u00020\n2\u0006\u0010\u000b\u001a\u00020\f\u001a\n\u0010\r\u001a\u00020\u0004*\u00020\u0006\u001a\u001a\u0010\u000e\u001a\u00020\u000f*\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\t0\u0012H\u0002\u001a\u000e\u0010\u0013\u001a\u0004\u0018\u00010\f*\u00020\u0010H\u0002\u001a\u000e\u0010\u0014\u001a\u0004\u0018\u00010\f*\u00020\u0010H\u0002\u001a\u001a\u0010\u0015\u001a\u00020\u000f*\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\n\u001a\f\u0010\u001a\u001a\u00020\u0004*\u00020\u0010H\u0002\u001a\u0014\u0010\u001b\u001a\u00020\u0004*\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u001dH\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"QUOTED_STRING_DELIMITERS", "Lokio/ByteString;", "TOKEN_DELIMITERS", "hasBody", "", "response", "Lokhttp3/Response;", "parseChallenges", "", "Lokhttp3/Challenge;", "Lokhttp3/Headers;", "headerName", "", "promisesBody", "readChallengeHeader", "", "Lokio/Buffer;", "result", "", "readQuotedString", "readToken", "receiveHeaders", "Lokhttp3/CookieJar;", "url", "Lokhttp3/HttpUrl;", "headers", "skipCommasAndWhitespace", "startsWith", "prefix", "", "okhttp"}, k = 2, mv = {1, 4, 0})
/* compiled from: HttpHeaders.kt */
public final class HttpHeaders {
    private static final ByteString QUOTED_STRING_DELIMITERS = ByteString.Companion.encodeUtf8("\"\\");
    private static final ByteString TOKEN_DELIMITERS = ByteString.Companion.encodeUtf8("\t ,=");

    public static final List<Challenge> parseChallenges(Headers $this$parseChallenges, String headerName) {
        Intrinsics.checkNotNullParameter($this$parseChallenges, "$this$parseChallenges");
        Intrinsics.checkNotNullParameter(headerName, "headerName");
        List result = new ArrayList();
        int size = $this$parseChallenges.size();
        for (int h = 0; h < size; h++) {
            if (StringsKt.equals(headerName, $this$parseChallenges.name(h), true)) {
                try {
                    readChallengeHeader(new Buffer().writeUtf8($this$parseChallenges.value(h)), result);
                } catch (EOFException e) {
                    Platform.Companion.get().log("Unable to parse challenge", 5, e);
                }
            }
        }
        return result;
    }

    private static final void readChallengeHeader(Buffer $this$readChallengeHeader, List<Challenge> result) throws EOFException {
        String parameterValue;
        String peek = null;
        while (true) {
            if (peek == null) {
                skipCommasAndWhitespace($this$readChallengeHeader);
                peek = readToken($this$readChallengeHeader);
                if (peek == null) {
                    return;
                }
            }
            String schemeName = peek;
            boolean commaPrefixed = skipCommasAndWhitespace($this$readChallengeHeader);
            peek = readToken($this$readChallengeHeader);
            if (peek != null) {
                byte b = (byte) 61;
                int eqCount = Util.skipAll($this$readChallengeHeader, b);
                boolean commaSuffixed = skipCommasAndWhitespace($this$readChallengeHeader);
                if (commaPrefixed || (!commaSuffixed && !$this$readChallengeHeader.exhausted())) {
                    Map parameters = new LinkedHashMap();
                    int eqCount2 = eqCount + Util.skipAll($this$readChallengeHeader, b);
                    while (true) {
                        if (peek == null) {
                            peek = readToken($this$readChallengeHeader);
                            if (skipCommasAndWhitespace($this$readChallengeHeader)) {
                                break;
                            }
                            eqCount2 = Util.skipAll($this$readChallengeHeader, b);
                        }
                        if (eqCount2 == 0) {
                            break;
                        } else if (eqCount2 <= 1 && !skipCommasAndWhitespace($this$readChallengeHeader)) {
                            if (startsWith($this$readChallengeHeader, (byte) 34)) {
                                parameterValue = readQuotedString($this$readChallengeHeader);
                            } else {
                                parameterValue = readToken($this$readChallengeHeader);
                            }
                            if (parameterValue != null) {
                                String replaced = (String) parameters.put(peek, parameterValue);
                                peek = null;
                                if (replaced == null) {
                                    if (!skipCommasAndWhitespace($this$readChallengeHeader) && !$this$readChallengeHeader.exhausted()) {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                    result.add(new Challenge(schemeName, (Map<String, String>) parameters));
                } else {
                    Map singletonMap = Collections.singletonMap((Object) null, peek + StringsKt.repeat("=", eqCount));
                    Intrinsics.checkNotNullExpressionValue(singletonMap, "Collections.singletonMap…ek + \"=\".repeat(eqCount))");
                    result.add(new Challenge(schemeName, (Map<String, String>) singletonMap));
                    peek = null;
                }
            } else if ($this$readChallengeHeader.exhausted()) {
                result.add(new Challenge(schemeName, (Map<String, String>) MapsKt.emptyMap()));
                return;
            } else {
                return;
            }
        }
    }

    private static final boolean skipCommasAndWhitespace(Buffer $this$skipCommasAndWhitespace) {
        boolean commaFound = false;
        while (!$this$skipCommasAndWhitespace.exhausted()) {
            switch ($this$skipCommasAndWhitespace.getByte(0)) {
                case 9:
                case 32:
                    $this$skipCommasAndWhitespace.readByte();
                    continue;
                case 44:
                    $this$skipCommasAndWhitespace.readByte();
                    commaFound = true;
                    continue;
            }
            return commaFound;
        }
        return commaFound;
    }

    private static final boolean startsWith(Buffer $this$startsWith, byte prefix) {
        return !$this$startsWith.exhausted() && $this$startsWith.getByte(0) == prefix;
    }

    private static final String readQuotedString(Buffer $this$readQuotedString) throws EOFException {
        byte b = (byte) 34;
        if ($this$readQuotedString.readByte() == b) {
            Buffer result = new Buffer();
            while (true) {
                long i = $this$readQuotedString.indexOfElement(QUOTED_STRING_DELIMITERS);
                if (i == -1) {
                    return null;
                }
                if ($this$readQuotedString.getByte(i) == b) {
                    result.write($this$readQuotedString, i);
                    $this$readQuotedString.readByte();
                    return result.readUtf8();
                } else if ($this$readQuotedString.size() == i + 1) {
                    return null;
                } else {
                    result.write($this$readQuotedString, i);
                    $this$readQuotedString.readByte();
                    result.write($this$readQuotedString, 1);
                }
            }
        } else {
            throw new IllegalArgumentException("Failed requirement.".toString());
        }
    }

    private static final String readToken(Buffer $this$readToken) {
        long tokenSize = $this$readToken.indexOfElement(TOKEN_DELIMITERS);
        if (tokenSize == -1) {
            tokenSize = $this$readToken.size();
        }
        if (tokenSize != 0) {
            return $this$readToken.readUtf8(tokenSize);
        }
        return null;
    }

    public static final void receiveHeaders(CookieJar $this$receiveHeaders, HttpUrl url, Headers headers) {
        Intrinsics.checkNotNullParameter($this$receiveHeaders, "$this$receiveHeaders");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(headers, "headers");
        if ($this$receiveHeaders != CookieJar.NO_COOKIES) {
            List cookies = Cookie.Companion.parseAll(url, headers);
            if (!cookies.isEmpty()) {
                $this$receiveHeaders.saveFromResponse(url, cookies);
            }
        }
    }

    public static final boolean promisesBody(Response $this$promisesBody) {
        Intrinsics.checkNotNullParameter($this$promisesBody, "$this$promisesBody");
        if (Intrinsics.areEqual((Object) $this$promisesBody.request().method(), (Object) "HEAD")) {
            return false;
        }
        int responseCode = $this$promisesBody.code();
        if (((responseCode >= 100 && responseCode < 200) || responseCode == 204 || responseCode == 304) && Util.headersContentLength($this$promisesBody) == -1 && !StringsKt.equals("chunked", Response.header$default($this$promisesBody, "Transfer-Encoding", (String) null, 2, (Object) null), true)) {
            return false;
        }
        return true;
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "No longer supported", replaceWith = @ReplaceWith(expression = "response.promisesBody()", imports = {}))
    public static final boolean hasBody(Response response) {
        Intrinsics.checkNotNullParameter(response, "response");
        return promisesBody(response);
    }
}
