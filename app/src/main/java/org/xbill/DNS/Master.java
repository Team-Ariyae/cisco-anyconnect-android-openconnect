package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xbill.DNS.Tokenizer;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Master.class */
public class Master {
    private int currentDClass;
    private long currentTTL;
    private int currentType;
    private long defaultTTL;
    private File file;
    private Generator generator;
    private List generators;
    private Master included;
    private Record last;
    private boolean needSOATTL;
    private boolean noExpandGenerate;
    private Name origin;
    private Tokenizer st;

    public Master(File file, Name name, long j7) {
        this.last = null;
        this.included = null;
        if (name != null && !name.isAbsolute()) {
            throw new RelativeNameException(name);
        }
        this.file = file;
        this.st = new Tokenizer(file);
        this.origin = name;
        this.defaultTTL = j7;
    }

    public Master(InputStream inputStream) {
        this(inputStream, (Name) null, -1L);
    }

    public Master(InputStream inputStream, Name name) {
        this(inputStream, name, -1L);
    }

    public Master(InputStream inputStream, Name name, long j7) {
        this.last = null;
        this.included = null;
        if (name != null && !name.isAbsolute()) {
            throw new RelativeNameException(name);
        }
        this.st = new Tokenizer(inputStream);
        this.origin = name;
        this.defaultTTL = j7;
    }

    public Master(String str) {
        this(new File(str), (Name) null, -1L);
    }

    public Master(String str, Name name) {
        this(new File(str), name, -1L);
    }

    public Master(String str, Name name, long j7) {
        this(new File(str), name, j7);
    }

    private void endGenerate() {
        this.st.getEOL();
        this.generator = null;
    }

    private Record nextGenerated() {
        try {
            return this.generator.nextRecord();
        } catch (Tokenizer.TokenizerException e8) {
            Tokenizer tokenizer = this.st;
            StringBuffer p = a.p("Parsing $GENERATE: ");
            p.append(e8.getBaseMessage());
            throw tokenizer.exception(p.toString());
        } catch (TextParseException e9) {
            Tokenizer tokenizer2 = this.st;
            StringBuffer p7 = a.p("Parsing $GENERATE: ");
            p7.append(e9.getMessage());
            throw tokenizer2.exception(p7.toString());
        }
    }

    private Name parseName(String str, Name name) {
        try {
            return Name.fromString(str, name);
        } catch (TextParseException e8) {
            throw this.st.exception(e8.getMessage());
        }
    }

    private void parseTTLClassAndType() {
        boolean z7;
        String str;
        String string = this.st.getString();
        int value = DClass.value(string);
        this.currentDClass = value;
        if (value >= 0) {
            string = this.st.getString();
            z7 = true;
        } else {
            z7 = false;
        }
        this.currentTTL = -1L;
        try {
            this.currentTTL = TTL.parseTTL(string);
            str = this.st.getString();
        } catch (NumberFormatException e8) {
            long j7 = this.defaultTTL;
            if (j7 < 0) {
                Record record = this.last;
                str = string;
                if (record != null) {
                    j7 = record.getTTL();
                }
            }
            this.currentTTL = j7;
            str = string;
        }
        String str2 = str;
        if (!z7) {
            int value2 = DClass.value(str);
            this.currentDClass = value2;
            if (value2 >= 0) {
                str2 = this.st.getString();
            } else {
                this.currentDClass = 1;
                str2 = str;
            }
        }
        int value3 = Type.value(str2);
        this.currentType = value3;
        if (value3 < 0) {
            Tokenizer tokenizer = this.st;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Invalid type '");
            stringBuffer.append(str2);
            stringBuffer.append("'");
            throw tokenizer.exception(stringBuffer.toString());
        }
        if (this.currentTTL < 0) {
            if (value3 != 6) {
                throw this.st.exception("missing TTL");
            }
            this.needSOATTL = true;
            this.currentTTL = 0L;
        }
    }

    private long parseUInt32(String str) {
        if (!Character.isDigit(str.charAt(0))) {
            return -1L;
        }
        try {
            long parseLong = Long.parseLong(str);
            if (parseLong < 0 || parseLong > 4294967295L) {
                return -1L;
            }
            return parseLong;
        } catch (NumberFormatException e8) {
            return -1L;
        }
    }

    private void startGenerate() {
        String identifier = this.st.getIdentifier();
        int indexOf = identifier.indexOf("-");
        if (indexOf < 0) {
            throw a.u("Invalid $GENERATE range specifier: ", identifier, this.st);
        }
        String substring = identifier.substring(0, indexOf);
        String substring2 = identifier.substring(indexOf + 1);
        String str = null;
        int indexOf2 = substring2.indexOf("/");
        String str2 = substring2;
        if (indexOf2 >= 0) {
            str = substring2.substring(indexOf2 + 1);
            str2 = substring2.substring(0, indexOf2);
        }
        long parseUInt32 = parseUInt32(substring);
        long parseUInt322 = parseUInt32(str2);
        long parseUInt323 = str != null ? parseUInt32(str) : 1L;
        if (parseUInt32 < 0 || parseUInt322 < 0 || parseUInt32 > parseUInt322 || parseUInt323 <= 0) {
            throw a.u("Invalid $GENERATE range specifier: ", identifier, this.st);
        }
        String identifier2 = this.st.getIdentifier();
        parseTTLClassAndType();
        if (!Generator.supportedType(this.currentType)) {
            Tokenizer tokenizer = this.st;
            StringBuffer p = a.p("$GENERATE does not support ");
            p.append(Type.string(this.currentType));
            p.append(" records");
            throw tokenizer.exception(p.toString());
        }
        String identifier3 = this.st.getIdentifier();
        this.st.getEOL();
        this.st.unget();
        this.generator = new Generator(parseUInt32, parseUInt322, parseUInt323, identifier2, this.currentType, this.currentDClass, this.currentTTL, identifier3, this.origin);
        if (this.generators == null) {
            this.generators = new ArrayList(1);
        }
        this.generators.add(this.generator);
    }

    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0091 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0054 A[SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:66:0x019e -> B:16:0x01a1). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.xbill.DNS.Record _nextRecord() {
        /*
            Method dump skipped, instructions count: 605
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.Master._nextRecord():org.xbill.DNS.Record");
    }

    public void expandGenerate(boolean z7) {
        this.noExpandGenerate = !z7;
    }

    public void finalize() {
        Tokenizer tokenizer = this.st;
        if (tokenizer != null) {
            tokenizer.close();
        }
    }

    public Iterator generators() {
        List list = this.generators;
        return (list != null ? Collections.unmodifiableList(list) : Collections.EMPTY_LIST).iterator();
    }

    public Record nextRecord() {
        try {
            Record _nextRecord = _nextRecord();
            if (_nextRecord == null) {
            }
            return _nextRecord;
        } finally {
            this.st.close();
        }
    }
}
