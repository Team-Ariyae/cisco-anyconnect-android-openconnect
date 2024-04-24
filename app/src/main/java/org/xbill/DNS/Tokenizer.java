package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.xbill.DNS.utils.base16;
import org.xbill.DNS.utils.base32;
import org.xbill.DNS.utils.base64;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Tokenizer.class */
public class Tokenizer {
    public static final int COMMENT = 5;
    public static final int EOF = 0;
    public static final int EOL = 1;
    public static final int IDENTIFIER = 3;
    public static final int QUOTED_STRING = 4;
    public static final int WHITESPACE = 2;
    private static String delim = " \t\n;()\"";
    private static String quotes = "\"";
    private Token current;
    private String delimiters;
    private String filename;
    private PushbackInputStream is;
    private int line;
    private int multiline;
    private boolean quoting;
    private StringBuffer sb;
    private boolean ungottenToken;
    private boolean wantClose;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Tokenizer$Token.class */
    public static class Token {
        public int type;
        public String value;

        private Token() {
            this.type = -1;
            this.value = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Token set(int i7, StringBuffer stringBuffer) {
            if (i7 < 0) {
                throw new IllegalArgumentException();
            }
            this.type = i7;
            this.value = stringBuffer == null ? null : stringBuffer.toString();
            return this;
        }

        public boolean isEOL() {
            int i7 = this.type;
            boolean z7 = true;
            if (i7 != 1) {
                z7 = i7 == 0;
            }
            return z7;
        }

        public boolean isString() {
            int i7 = this.type;
            return i7 == 3 || i7 == 4;
        }

        public String toString() {
            StringBuffer stringBuffer;
            String str;
            int i7 = this.type;
            if (i7 == 0) {
                return "<eof>";
            }
            if (i7 == 1) {
                return "<eol>";
            }
            if (i7 == 2) {
                return "<whitespace>";
            }
            if (i7 == 3) {
                stringBuffer = new StringBuffer();
                str = "<identifier: ";
            } else if (i7 == 4) {
                stringBuffer = new StringBuffer();
                str = "<quoted_string: ";
            } else {
                if (i7 != 5) {
                    return "<unknown>";
                }
                stringBuffer = new StringBuffer();
                str = "<comment: ";
            }
            stringBuffer.append(str);
            stringBuffer.append(this.value);
            stringBuffer.append(">");
            return stringBuffer.toString();
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Tokenizer$TokenizerException.class */
    public static class TokenizerException extends TextParseException {
        public String message;

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public TokenizerException(java.lang.String r4, int r5, java.lang.String r6) {
            /*
                r3 = this;
                java.lang.StringBuffer r0 = new java.lang.StringBuffer
                r1 = r0
                r1.<init>()
                r7 = r0
                r0 = r7
                r1 = r4
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r7
                java.lang.String r1 = ":"
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r7
                r1 = r5
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r7
                java.lang.String r1 = ": "
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r7
                r1 = r6
                java.lang.StringBuffer r0 = r0.append(r1)
                r0 = r3
                r1 = r7
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                r0 = r3
                r1 = r6
                r0.message = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.Tokenizer.TokenizerException.<init>(java.lang.String, int, java.lang.String):void");
        }

        public String getBaseMessage() {
            return this.message;
        }
    }

    public Tokenizer(File file) {
        this(new FileInputStream(file));
        this.wantClose = true;
        this.filename = file.getName();
    }

    public Tokenizer(InputStream inputStream) {
        this.is = new PushbackInputStream(inputStream instanceof BufferedInputStream ? inputStream : new BufferedInputStream(inputStream), 2);
        this.ungottenToken = false;
        this.multiline = 0;
        this.quoting = false;
        this.delimiters = delim;
        this.current = new Token();
        this.sb = new StringBuffer();
        this.filename = "<none>";
        this.line = 1;
    }

    public Tokenizer(String str) {
        this(new ByteArrayInputStream(str.getBytes()));
    }

    private String _getIdentifier(String str) {
        Token token = get();
        if (token.type == 3) {
            return token.value;
        }
        throw a.u("expected ", str, this);
    }

    private void checkUnbalancedParens() {
        if (this.multiline > 0) {
            throw exception("unbalanced parentheses");
        }
    }

    private int getChar() {
        int read = this.is.read();
        int i7 = read;
        if (read == 13) {
            int read2 = this.is.read();
            if (read2 != 10) {
                this.is.unread(read2);
            }
            i7 = 10;
        }
        if (i7 == 10) {
            this.line++;
        }
        return i7;
    }

    private String remainingStrings() {
        StringBuffer stringBuffer;
        StringBuffer stringBuffer2 = null;
        while (true) {
            stringBuffer = stringBuffer2;
            Token token = get();
            if (!token.isString()) {
                break;
            }
            StringBuffer stringBuffer3 = stringBuffer;
            if (stringBuffer == null) {
                stringBuffer3 = new StringBuffer();
            }
            stringBuffer3.append(token.value);
            stringBuffer2 = stringBuffer3;
        }
        unget();
        if (stringBuffer == null) {
            return null;
        }
        return stringBuffer.toString();
    }

    private int skipWhitespace() {
        int i7;
        int i8 = 0;
        while (true) {
            i7 = getChar();
            if (i7 == 32 || i7 == 9 || (i7 == 10 && this.multiline > 0)) {
                i8++;
            }
        }
        ungetChar(i7);
        return i8;
    }

    private void ungetChar(int i7) {
        if (i7 == -1) {
            return;
        }
        this.is.unread(i7);
        if (i7 == 10) {
            this.line--;
        }
    }

    public void close() {
        if (this.wantClose) {
            try {
                this.is.close();
            } catch (IOException e8) {
            }
        }
    }

    public TextParseException exception(String str) {
        return new TokenizerException(this.filename, this.line, str);
    }

    public void finalize() {
        close();
    }

    public Token get() {
        return get(false, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:104:0x0215, code lost:
    
        ungetChar(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x0222, code lost:
    
        if (r4.sb.length() != 0) goto L117;
     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x0227, code lost:
    
        if (r7 == 4) goto L117;
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x022a, code lost:
    
        checkUnbalancedParens();
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x0237, code lost:
    
        return r4.current.set(0, null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x0244, code lost:
    
        return r4.current.set(r7, r4.sb);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.xbill.DNS.Tokenizer.Token get(boolean r5, boolean r6) {
        /*
            Method dump skipped, instructions count: 581
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.Tokenizer.get(boolean, boolean):org.xbill.DNS.Tokenizer$Token");
    }

    public InetAddress getAddress(int i7) {
        try {
            return Address.getByAddress(_getIdentifier("an address"), i7);
        } catch (UnknownHostException e8) {
            throw exception(e8.getMessage());
        }
    }

    public byte[] getAddressBytes(int i7) {
        String _getIdentifier = _getIdentifier("an address");
        byte[] byteArray = Address.toByteArray(_getIdentifier, i7);
        if (byteArray != null) {
            return byteArray;
        }
        throw a.u("Invalid address: ", _getIdentifier, this);
    }

    public byte[] getBase32String(base32 base32Var) {
        byte[] fromString = base32Var.fromString(_getIdentifier("a base32 string"));
        if (fromString != null) {
            return fromString;
        }
        throw exception("invalid base32 encoding");
    }

    public byte[] getBase64() {
        return getBase64(false);
    }

    public byte[] getBase64(boolean z7) {
        String remainingStrings = remainingStrings();
        if (remainingStrings == null) {
            if (z7) {
                throw exception("expected base64 encoded string");
            }
            return null;
        }
        byte[] fromString = base64.fromString(remainingStrings);
        if (fromString != null) {
            return fromString;
        }
        throw exception("invalid base64 encoding");
    }

    public void getEOL() {
        int i7 = get().type;
        if (i7 != 1 && i7 != 0) {
            throw exception("expected EOL or EOF");
        }
    }

    public byte[] getHex() {
        return getHex(false);
    }

    public byte[] getHex(boolean z7) {
        String remainingStrings = remainingStrings();
        if (remainingStrings == null) {
            if (z7) {
                throw exception("expected hex encoded string");
            }
            return null;
        }
        byte[] fromString = base16.fromString(remainingStrings);
        if (fromString != null) {
            return fromString;
        }
        throw exception("invalid hex encoding");
    }

    public byte[] getHexString() {
        byte[] fromString = base16.fromString(_getIdentifier("a hex string"));
        if (fromString != null) {
            return fromString;
        }
        throw exception("invalid hex encoding");
    }

    public String getIdentifier() {
        return _getIdentifier("an identifier");
    }

    public long getLong() {
        String _getIdentifier = _getIdentifier("an integer");
        if (!Character.isDigit(_getIdentifier.charAt(0))) {
            throw exception("expected an integer");
        }
        try {
            return Long.parseLong(_getIdentifier);
        } catch (NumberFormatException e8) {
            throw exception("expected an integer");
        }
    }

    public Name getName(Name name) {
        try {
            Name fromString = Name.fromString(_getIdentifier("a name"), name);
            if (fromString.isAbsolute()) {
                return fromString;
            }
            throw new RelativeNameException(fromString);
        } catch (TextParseException e8) {
            throw exception(e8.getMessage());
        }
    }

    public String getString() {
        Token token = get();
        if (token.isString()) {
            return token.value;
        }
        throw exception("expected a string");
    }

    public long getTTL() {
        try {
            return TTL.parseTTL(_getIdentifier("a TTL value"));
        } catch (NumberFormatException e8) {
            throw exception("expected a TTL value");
        }
    }

    public long getTTLLike() {
        try {
            return TTL.parse(_getIdentifier("a TTL-like value"), false);
        } catch (NumberFormatException e8) {
            throw exception("expected a TTL-like value");
        }
    }

    public int getUInt16() {
        long j7 = getLong();
        if (j7 < 0 || j7 > 65535) {
            throw exception("expected an 16 bit unsigned integer");
        }
        return (int) j7;
    }

    public long getUInt32() {
        long j7 = getLong();
        if (j7 < 0 || j7 > 4294967295L) {
            throw exception("expected an 32 bit unsigned integer");
        }
        return j7;
    }

    public int getUInt8() {
        long j7 = getLong();
        if (j7 < 0 || j7 > 255) {
            throw exception("expected an 8 bit unsigned integer");
        }
        return (int) j7;
    }

    public void unget() {
        if (this.ungottenToken) {
            throw new IllegalStateException("Cannot unget multiple tokens");
        }
        if (this.current.type == 1) {
            this.line--;
        }
        this.ungottenToken = true;
    }
}
