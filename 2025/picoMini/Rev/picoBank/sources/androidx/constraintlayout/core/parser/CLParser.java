package androidx.constraintlayout.core.parser;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CLParser {
    static boolean DEBUG = false;
    private boolean hasComment = false;
    private int lineNumber;
    private String mContent;

    enum TYPE {
        UNKNOWN,
        OBJECT,
        ARRAY,
        NUMBER,
        STRING,
        KEY,
        TOKEN
    }

    public static CLObject parse(String string) throws CLParsingException {
        return new CLParser(string).parse();
    }

    public CLParser(String content) {
        this.mContent = content;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0143, code lost:
        if (r6 != ':') goto L_0x016d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.constraintlayout.core.parser.CLObject parse() throws androidx.constraintlayout.core.parser.CLParsingException {
        /*
            r16 = this;
            r0 = r16
            r1 = 0
            java.lang.String r2 = r0.mContent
            char[] r2 = r2.toCharArray()
            r3 = 0
            int r4 = r2.length
            r5 = 1
            r0.lineNumber = r5
            r6 = -1
            r7 = 0
        L_0x0010:
            r8 = 10
            if (r7 >= r4) goto L_0x0026
            char r9 = r2[r7]
            r10 = 123(0x7b, float:1.72E-43)
            if (r9 != r10) goto L_0x001c
            r6 = r7
            goto L_0x0026
        L_0x001c:
            if (r9 != r8) goto L_0x0023
            int r8 = r0.lineNumber
            int r8 = r8 + r5
            r0.lineNumber = r8
        L_0x0023:
            int r7 = r7 + 1
            goto L_0x0010
        L_0x0026:
            r7 = -1
            if (r6 == r7) goto L_0x01d1
            androidx.constraintlayout.core.parser.CLObject r1 = androidx.constraintlayout.core.parser.CLObject.allocate(r2)
            int r7 = r0.lineNumber
            r1.setLine(r7)
            long r9 = (long) r6
            r1.setStart(r9)
            r3 = r1
            int r7 = r6 + 1
        L_0x0039:
            if (r7 >= r4) goto L_0x018d
            char r9 = r2[r7]
            if (r9 != r8) goto L_0x0044
            int r10 = r0.lineNumber
            int r10 = r10 + r5
            r0.lineNumber = r10
        L_0x0044:
            boolean r10 = r0.hasComment
            if (r10 == 0) goto L_0x0052
            if (r9 != r8) goto L_0x004e
            r10 = 0
            r0.hasComment = r10
            goto L_0x0052
        L_0x004e:
            r15 = r6
            r5 = r8
            goto L_0x0186
        L_0x0052:
            if (r3 != 0) goto L_0x0057
            r15 = r6
            goto L_0x018e
        L_0x0057:
            boolean r10 = r3.isDone()
            if (r10 == 0) goto L_0x0066
            androidx.constraintlayout.core.parser.CLElement r3 = r0.getNextJsonElement(r7, r9, r3, r2)
            r15 = r6
            r5 = r8
            r6 = r9
            goto L_0x016d
        L_0x0066:
            boolean r10 = r3 instanceof androidx.constraintlayout.core.parser.CLObject
            r11 = 125(0x7d, float:1.75E-43)
            if (r10 == 0) goto L_0x0082
            if (r9 != r11) goto L_0x0079
            int r10 = r7 + -1
            long r10 = (long) r10
            r3.setEnd(r10)
            r15 = r6
            r5 = r8
            r6 = r9
            goto L_0x016d
        L_0x0079:
            androidx.constraintlayout.core.parser.CLElement r3 = r0.getNextJsonElement(r7, r9, r3, r2)
            r15 = r6
            r5 = r8
            r6 = r9
            goto L_0x016d
        L_0x0082:
            boolean r10 = r3 instanceof androidx.constraintlayout.core.parser.CLArray
            r12 = 93
            if (r10 == 0) goto L_0x009e
            if (r9 != r12) goto L_0x0095
            int r10 = r7 + -1
            long r10 = (long) r10
            r3.setEnd(r10)
            r15 = r6
            r5 = r8
            r6 = r9
            goto L_0x016d
        L_0x0095:
            androidx.constraintlayout.core.parser.CLElement r3 = r0.getNextJsonElement(r7, r9, r3, r2)
            r15 = r6
            r5 = r8
            r6 = r9
            goto L_0x016d
        L_0x009e:
            boolean r10 = r3 instanceof androidx.constraintlayout.core.parser.CLString
            r13 = 1
            if (r10 == 0) goto L_0x00bc
            long r10 = r3.start
            int r10 = (int) r10
            char r10 = r2[r10]
            if (r10 != r9) goto L_0x00b7
            long r11 = r3.start
            long r11 = r11 + r13
            r3.setStart(r11)
            int r11 = r7 + -1
            long r11 = (long) r11
            r3.setEnd(r11)
        L_0x00b7:
            r15 = r6
            r5 = r8
            r6 = r9
            goto L_0x016d
        L_0x00bc:
            boolean r10 = r3 instanceof androidx.constraintlayout.core.parser.CLToken
            if (r10 == 0) goto L_0x00f6
            r10 = r3
            androidx.constraintlayout.core.parser.CLToken r10 = (androidx.constraintlayout.core.parser.CLToken) r10
            r15 = r6
            long r5 = (long) r7
            boolean r5 = r10.validate(r9, r5)
            if (r5 == 0) goto L_0x00cc
            goto L_0x00f7
        L_0x00cc:
            androidx.constraintlayout.core.parser.CLParsingException r5 = new androidx.constraintlayout.core.parser.CLParsingException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "parsing incorrect token "
            java.lang.StringBuilder r6 = r6.append(r8)
            java.lang.String r8 = r10.content()
            java.lang.StringBuilder r6 = r6.append(r8)
            java.lang.String r8 = " at line "
            java.lang.StringBuilder r6 = r6.append(r8)
            int r8 = r0.lineNumber
            java.lang.StringBuilder r6 = r6.append(r8)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6, r10)
            throw r5
        L_0x00f6:
            r15 = r6
        L_0x00f7:
            boolean r5 = r3 instanceof androidx.constraintlayout.core.parser.CLKey
            if (r5 != 0) goto L_0x0102
            boolean r5 = r3 instanceof androidx.constraintlayout.core.parser.CLString
            if (r5 == 0) goto L_0x0100
            goto L_0x0102
        L_0x0100:
            r6 = r9
            goto L_0x0123
        L_0x0102:
            long r5 = r3.start
            int r5 = (int) r5
            char r5 = r2[r5]
            r6 = 39
            if (r5 == r6) goto L_0x0112
            r6 = 34
            if (r5 != r6) goto L_0x0110
            goto L_0x0112
        L_0x0110:
            r6 = r9
            goto L_0x0123
        L_0x0112:
            if (r5 != r9) goto L_0x0122
            r6 = r9
            long r8 = r3.start
            long r8 = r8 + r13
            r3.setStart(r8)
            int r8 = r7 + -1
            long r8 = (long) r8
            r3.setEnd(r8)
            goto L_0x0123
        L_0x0122:
            r6 = r9
        L_0x0123:
            boolean r5 = r3.isDone()
            if (r5 != 0) goto L_0x016b
            if (r6 == r11) goto L_0x0146
            if (r6 == r12) goto L_0x0146
            r5 = 44
            if (r6 == r5) goto L_0x0146
            r5 = 32
            if (r6 == r5) goto L_0x0146
            r5 = 9
            if (r6 == r5) goto L_0x0146
            r5 = 13
            if (r6 == r5) goto L_0x0146
            r5 = 10
            if (r6 == r5) goto L_0x0148
            r8 = 58
            if (r6 != r8) goto L_0x016d
            goto L_0x0148
        L_0x0146:
            r5 = 10
        L_0x0148:
            int r8 = r7 + -1
            long r8 = (long) r8
            r3.setEnd(r8)
            if (r6 == r11) goto L_0x0152
            if (r6 != r12) goto L_0x016d
        L_0x0152:
            androidx.constraintlayout.core.parser.CLElement r3 = r3.getContainer()
            int r8 = r7 + -1
            long r8 = (long) r8
            r3.setEnd(r8)
            boolean r8 = r3 instanceof androidx.constraintlayout.core.parser.CLKey
            if (r8 == 0) goto L_0x016d
            androidx.constraintlayout.core.parser.CLElement r3 = r3.getContainer()
            int r8 = r7 + -1
            long r8 = (long) r8
            r3.setEnd(r8)
            goto L_0x016d
        L_0x016b:
            r5 = 10
        L_0x016d:
            boolean r8 = r3.isDone()
            if (r8 == 0) goto L_0x0186
            boolean r8 = r3 instanceof androidx.constraintlayout.core.parser.CLKey
            if (r8 == 0) goto L_0x0182
            r8 = r3
            androidx.constraintlayout.core.parser.CLKey r8 = (androidx.constraintlayout.core.parser.CLKey) r8
            java.util.ArrayList r8 = r8.mElements
            int r8 = r8.size()
            if (r8 <= 0) goto L_0x0186
        L_0x0182:
            androidx.constraintlayout.core.parser.CLElement r3 = r3.getContainer()
        L_0x0186:
            int r7 = r7 + 1
            r8 = r5
            r6 = r15
            r5 = 1
            goto L_0x0039
        L_0x018d:
            r15 = r6
        L_0x018e:
            if (r3 == 0) goto L_0x01b0
            boolean r5 = r3.isDone()
            if (r5 != 0) goto L_0x01b0
            boolean r5 = r3 instanceof androidx.constraintlayout.core.parser.CLString
            if (r5 == 0) goto L_0x01a4
            long r5 = r3.start
            int r5 = (int) r5
            r6 = 1
            int r5 = r5 + r6
            long r7 = (long) r5
            r3.setStart(r7)
            goto L_0x01a5
        L_0x01a4:
            r6 = 1
        L_0x01a5:
            int r5 = r4 + -1
            long r7 = (long) r5
            r3.setEnd(r7)
            androidx.constraintlayout.core.parser.CLElement r3 = r3.getContainer()
            goto L_0x018e
        L_0x01b0:
            boolean r5 = DEBUG
            if (r5 == 0) goto L_0x01d0
            java.io.PrintStream r5 = java.lang.System.out
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Root: "
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = r1.toJSON()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.println(r6)
        L_0x01d0:
            return r1
        L_0x01d1:
            r15 = r6
            androidx.constraintlayout.core.parser.CLParsingException r5 = new androidx.constraintlayout.core.parser.CLParsingException
            java.lang.String r6 = "invalid json content"
            r7 = 0
            r5.<init>(r6, r7)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.core.parser.CLParser.parse():androidx.constraintlayout.core.parser.CLObject");
    }

    private CLElement getNextJsonElement(int position, char c, CLElement currentElement, char[] content) throws CLParsingException {
        switch (c) {
            case 9:
            case 10:
            case 13:
            case ' ':
            case ',':
            case ':':
                return currentElement;
            case '\"':
            case '\'':
                if (currentElement instanceof CLObject) {
                    return createElement(currentElement, position, TYPE.KEY, true, content);
                }
                return createElement(currentElement, position, TYPE.STRING, true, content);
            case '+':
            case '-':
            case '.':
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE /*48*/:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_EDITOR_ABSOLUTEX /*49*/:
            case '2':
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TAG /*51*/:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_BASELINE_TO_TOP_OF /*52*/:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_BASELINE_TO_BOTTOM_OF /*53*/:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_MARGIN_BASELINE /*54*/:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_GONE_MARGIN_BASELINE /*55*/:
            case '8':
            case '9':
                return createElement(currentElement, position, TYPE.NUMBER, true, content);
            case '/':
                if (position + 1 >= content.length || content[position + 1] != '/') {
                    return currentElement;
                }
                this.hasComment = true;
                return currentElement;
            case '[':
                return createElement(currentElement, position, TYPE.ARRAY, true, content);
            case ']':
            case '}':
                currentElement.setEnd((long) (position - 1));
                CLElement currentElement2 = currentElement.getContainer();
                currentElement2.setEnd((long) position);
                return currentElement2;
            case '{':
                return createElement(currentElement, position, TYPE.OBJECT, true, content);
            default:
                if (!(currentElement instanceof CLContainer) || (currentElement instanceof CLObject)) {
                    return createElement(currentElement, position, TYPE.KEY, true, content);
                }
                CLElement currentElement3 = createElement(currentElement, position, TYPE.TOKEN, true, content);
                CLToken token = (CLToken) currentElement3;
                if (token.validate(c, (long) position)) {
                    return currentElement3;
                }
                throw new CLParsingException("incorrect token <" + c + "> at line " + this.lineNumber, token);
        }
    }

    private CLElement createElement(CLElement currentElement, int position, TYPE type, boolean applyStart, char[] content) {
        CLElement newElement = null;
        if (DEBUG) {
            System.out.println("CREATE " + type + " at " + content[position]);
        }
        switch (type) {
            case OBJECT:
                newElement = CLObject.allocate(content);
                position++;
                break;
            case ARRAY:
                newElement = CLArray.allocate(content);
                position++;
                break;
            case STRING:
                newElement = CLString.allocate(content);
                break;
            case NUMBER:
                newElement = CLNumber.allocate(content);
                break;
            case KEY:
                newElement = CLKey.allocate(content);
                break;
            case TOKEN:
                newElement = CLToken.allocate(content);
                break;
        }
        if (newElement == null) {
            return null;
        }
        newElement.setLine(this.lineNumber);
        if (applyStart) {
            newElement.setStart((long) position);
        }
        if (currentElement instanceof CLContainer) {
            newElement.setContainer((CLContainer) currentElement);
        }
        return newElement;
    }
}
