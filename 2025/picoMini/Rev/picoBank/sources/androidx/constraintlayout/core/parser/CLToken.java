package androidx.constraintlayout.core.parser;

public class CLToken extends CLElement {
    int index = 0;
    char[] tokenFalse = "false".toCharArray();
    char[] tokenNull = "null".toCharArray();
    char[] tokenTrue = "true".toCharArray();
    Type type = Type.UNKNOWN;

    enum Type {
        UNKNOWN,
        TRUE,
        FALSE,
        NULL
    }

    public boolean getBoolean() throws CLParsingException {
        if (this.type == Type.TRUE) {
            return true;
        }
        if (this.type == Type.FALSE) {
            return false;
        }
        throw new CLParsingException("this token is not a boolean: <" + content() + ">", this);
    }

    public boolean isNull() throws CLParsingException {
        if (this.type == Type.NULL) {
            return true;
        }
        throw new CLParsingException("this token is not a null: <" + content() + ">", this);
    }

    public CLToken(char[] content) {
        super(content);
    }

    public static CLElement allocate(char[] content) {
        return new CLToken(content);
    }

    /* access modifiers changed from: protected */
    public String toJSON() {
        if (CLParser.DEBUG) {
            return "<" + content() + ">";
        }
        return content();
    }

    /* access modifiers changed from: protected */
    public String toFormattedJSON(int indent, int forceIndent) {
        StringBuilder json = new StringBuilder();
        addIndent(json, indent);
        json.append(content());
        return json.toString();
    }

    public Type getType() {
        return this.type;
    }

    public boolean validate(char c, long position) {
        boolean isValid = false;
        boolean z = false;
        switch (this.type) {
            case TRUE:
                if (this.tokenTrue[this.index] == c) {
                    z = true;
                }
                isValid = z;
                if (isValid && this.index + 1 == this.tokenTrue.length) {
                    setEnd(position);
                    break;
                }
            case FALSE:
                if (this.tokenFalse[this.index] == c) {
                    z = true;
                }
                isValid = z;
                if (isValid && this.index + 1 == this.tokenFalse.length) {
                    setEnd(position);
                    break;
                }
            case NULL:
                if (this.tokenNull[this.index] == c) {
                    z = true;
                }
                isValid = z;
                if (isValid && this.index + 1 == this.tokenNull.length) {
                    setEnd(position);
                    break;
                }
            case UNKNOWN:
                if (this.tokenTrue[this.index] != c) {
                    if (this.tokenFalse[this.index] != c) {
                        if (this.tokenNull[this.index] == c) {
                            this.type = Type.NULL;
                            isValid = true;
                            break;
                        }
                    } else {
                        this.type = Type.FALSE;
                        isValid = true;
                        break;
                    }
                } else {
                    this.type = Type.TRUE;
                    isValid = true;
                    break;
                }
                break;
        }
        this.index++;
        return isValid;
    }
}
