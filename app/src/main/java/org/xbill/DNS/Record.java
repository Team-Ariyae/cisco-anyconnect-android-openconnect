package org.xbill.DNS;

import androidx.activity.result.a;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import org.xbill.DNS.Tokenizer;
import org.xbill.DNS.utils.base16;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Record.class */
public abstract class Record implements Cloneable, Comparable, Serializable {
    private static final DecimalFormat byteFormat;
    private static final long serialVersionUID = 2694906050116005466L;
    public int dclass;
    public Name name;
    public long ttl;
    public int type;

    static {
        DecimalFormat decimalFormat = new DecimalFormat();
        byteFormat = decimalFormat;
        decimalFormat.setMinimumIntegerDigits(3);
    }

    public Record() {
    }

    public Record(Name name, int i7, int i8, long j7) {
        if (!name.isAbsolute()) {
            throw new RelativeNameException(name);
        }
        Type.check(i7);
        DClass.check(i8);
        TTL.check(j7);
        this.name = name;
        this.type = i7;
        this.dclass = i8;
        this.ttl = j7;
    }

    public static byte[] byteArrayFromString(String str) {
        boolean z7;
        int i7;
        int i8;
        byte b8;
        byte[] bytes = str.getBytes();
        int i9 = 0;
        while (true) {
            if (i9 >= bytes.length) {
                z7 = false;
                break;
            }
            if (bytes[i9] == 92) {
                z7 = true;
                break;
            }
            i9++;
        }
        if (!z7) {
            if (bytes.length <= 255) {
                return bytes;
            }
            throw new TextParseException("text string too long");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i10 = 0;
        boolean z8 = false;
        int i11 = 0;
        for (byte b9 : bytes) {
            if (z8) {
                if (b9 < 48 || b9 > 57 || i10 >= 3) {
                    i7 = i10;
                    i8 = i11;
                    b8 = b9;
                    if (i10 > 0) {
                        if (i10 < 3) {
                            throw new TextParseException("bad escape");
                        }
                        i7 = i10;
                        i8 = i11;
                        b8 = b9;
                    }
                } else {
                    i10++;
                    i11 = (i11 * 10) + (b9 - 48);
                    if (i11 > 255) {
                        throw new TextParseException("bad escape");
                    }
                    if (i10 >= 3) {
                        b8 = (byte) i11;
                        i7 = i10;
                        i8 = i11;
                    }
                }
                byteArrayOutputStream.write(b8);
                i10 = i7;
                z8 = false;
                i11 = i8;
            } else if (b9 == 92) {
                i10 = 0;
                z8 = true;
                i11 = 0;
            } else {
                byteArrayOutputStream.write(b9);
            }
        }
        if (i10 > 0 && i10 < 3) {
            throw new TextParseException("bad escape");
        }
        if (byteArrayOutputStream.toByteArray().length <= 255) {
            return byteArrayOutputStream.toByteArray();
        }
        throw new TextParseException("text string too long");
    }

    public static String byteArrayToString(byte[] bArr, boolean z7) {
        StringBuffer stringBuffer = new StringBuffer();
        if (z7) {
            stringBuffer.append('\"');
        }
        for (byte b8 : bArr) {
            int i7 = b8 & 255;
            if (i7 < 32 || i7 >= 127) {
                stringBuffer.append('\\');
                stringBuffer.append(byteFormat.format(i7));
            } else {
                if (i7 == 34 || i7 == 92) {
                    stringBuffer.append('\\');
                }
                stringBuffer.append((char) i7);
            }
        }
        if (z7) {
            stringBuffer.append('\"');
        }
        return stringBuffer.toString();
    }

    public static byte[] checkByteArrayLength(String str, byte[] bArr, int i7) {
        if (bArr.length <= 65535) {
            byte[] bArr2 = new byte[bArr.length];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            return bArr2;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\"");
        stringBuffer.append(str);
        stringBuffer.append("\" array ");
        stringBuffer.append("must have no more than ");
        stringBuffer.append(i7);
        stringBuffer.append(" elements");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public static Name checkName(String str, Name name) {
        if (name.isAbsolute()) {
            return name;
        }
        throw new RelativeNameException(name);
    }

    public static int checkU16(String str, int i7) {
        if (i7 >= 0 && i7 <= 65535) {
            return i7;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\"");
        stringBuffer.append(str);
        stringBuffer.append("\" ");
        stringBuffer.append(i7);
        stringBuffer.append(" must be an unsigned 16 ");
        stringBuffer.append("bit value");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public static long checkU32(String str, long j7) {
        if (j7 >= 0 && j7 <= 4294967295L) {
            return j7;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\"");
        stringBuffer.append(str);
        stringBuffer.append("\" ");
        stringBuffer.append(j7);
        stringBuffer.append(" must be an unsigned 32 ");
        stringBuffer.append("bit value");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public static int checkU8(String str, int i7) {
        if (i7 >= 0 && i7 <= 255) {
            return i7;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\"");
        stringBuffer.append(str);
        stringBuffer.append("\" ");
        stringBuffer.append(i7);
        stringBuffer.append(" must be an unsigned 8 ");
        stringBuffer.append("bit value");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public static Record fromString(Name name, int i7, int i8, long j7, String str, Name name2) {
        return fromString(name, i7, i8, j7, new Tokenizer(str), name2);
    }

    public static Record fromString(Name name, int i7, int i8, long j7, Tokenizer tokenizer, Name name2) {
        if (!name.isAbsolute()) {
            throw new RelativeNameException(name);
        }
        Type.check(i7);
        DClass.check(i8);
        TTL.check(j7);
        Tokenizer.Token token = tokenizer.get();
        if (token.type == 3 && token.value.equals("\\#")) {
            int uInt16 = tokenizer.getUInt16();
            byte[] hex = tokenizer.getHex();
            byte[] bArr = hex;
            if (hex == null) {
                bArr = new byte[0];
            }
            if (uInt16 == bArr.length) {
                return newRecord(name, i7, i8, j7, uInt16, new DNSInput(bArr));
            }
            throw tokenizer.exception("invalid unknown RR encoding: length mismatch");
        }
        tokenizer.unget();
        Record emptyRecord = getEmptyRecord(name, i7, i8, j7, true);
        emptyRecord.rdataFromString(tokenizer, name2);
        int i9 = tokenizer.get().type;
        if (i9 == 1 || i9 == 0) {
            return emptyRecord;
        }
        throw tokenizer.exception("unexpected tokens at end of record");
    }

    public static Record fromWire(DNSInput dNSInput, int i7) {
        return fromWire(dNSInput, i7, false);
    }

    public static Record fromWire(DNSInput dNSInput, int i7, boolean z7) {
        Name name = new Name(dNSInput);
        int readU16 = dNSInput.readU16();
        int readU162 = dNSInput.readU16();
        if (i7 == 0) {
            return newRecord(name, readU16, readU162);
        }
        long readU32 = dNSInput.readU32();
        int readU163 = dNSInput.readU16();
        return (readU163 == 0 && z7 && (i7 == 1 || i7 == 2)) ? newRecord(name, readU16, readU162, readU32) : newRecord(name, readU16, readU162, readU32, readU163, dNSInput);
    }

    public static Record fromWire(byte[] bArr, int i7) {
        return fromWire(new DNSInput(bArr), i7, false);
    }

    private static final Record getEmptyRecord(Name name, int i7, int i8, long j7, boolean z7) {
        Record emptyRecord;
        if (z7) {
            Record proto = Type.getProto(i7);
            emptyRecord = proto != null ? proto.getObject() : new UNKRecord();
        } else {
            emptyRecord = new EmptyRecord();
        }
        emptyRecord.name = name;
        emptyRecord.type = i7;
        emptyRecord.dclass = i8;
        emptyRecord.ttl = j7;
        return emptyRecord;
    }

    public static Record newRecord(Name name, int i7, int i8) {
        return newRecord(name, i7, i8, 0L);
    }

    public static Record newRecord(Name name, int i7, int i8, long j7) {
        if (!name.isAbsolute()) {
            throw new RelativeNameException(name);
        }
        Type.check(i7);
        DClass.check(i8);
        TTL.check(j7);
        return getEmptyRecord(name, i7, i8, j7, false);
    }

    private static Record newRecord(Name name, int i7, int i8, long j7, int i9, DNSInput dNSInput) {
        Record emptyRecord = getEmptyRecord(name, i7, i8, j7, dNSInput != null);
        if (dNSInput != null) {
            if (dNSInput.remaining() < i9) {
                throw new WireParseException("truncated record");
            }
            dNSInput.setActive(i9);
            emptyRecord.rrFromWire(dNSInput);
            if (dNSInput.remaining() > 0) {
                throw new WireParseException("invalid record length");
            }
            dNSInput.clearActive();
        }
        return emptyRecord;
    }

    public static Record newRecord(Name name, int i7, int i8, long j7, int i9, byte[] bArr) {
        if (!name.isAbsolute()) {
            throw new RelativeNameException(name);
        }
        Type.check(i7);
        DClass.check(i8);
        TTL.check(j7);
        try {
            return newRecord(name, i7, i8, j7, i9, bArr != null ? new DNSInput(bArr) : null);
        } catch (IOException e8) {
            return null;
        }
    }

    public static Record newRecord(Name name, int i7, int i8, long j7, byte[] bArr) {
        return newRecord(name, i7, i8, j7, bArr.length, bArr);
    }

    private void toWireCanonical(DNSOutput dNSOutput, boolean z7) {
        this.name.toWireCanonical(dNSOutput);
        dNSOutput.writeU16(this.type);
        dNSOutput.writeU16(this.dclass);
        dNSOutput.writeU32(z7 ? 0L : this.ttl);
        int current = dNSOutput.current();
        dNSOutput.writeU16(0);
        rrToWire(dNSOutput, null, true);
        dNSOutput.writeU16At((dNSOutput.current() - current) - 2, current);
    }

    private byte[] toWireCanonical(boolean z7) {
        DNSOutput dNSOutput = new DNSOutput();
        toWireCanonical(dNSOutput, z7);
        return dNSOutput.toByteArray();
    }

    public static String unknownToString(byte[] bArr) {
        StringBuffer p = a.p("\\# ");
        p.append(bArr.length);
        p.append(" ");
        p.append(base16.toString(bArr));
        return p.toString();
    }

    public Record cloneRecord() {
        try {
            return (Record) clone();
        } catch (CloneNotSupportedException e8) {
            throw new IllegalStateException();
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        Record record = (Record) obj;
        if (this == record) {
            return 0;
        }
        int compareTo = this.name.compareTo(record.name);
        if (compareTo != 0) {
            return compareTo;
        }
        int i7 = this.dclass - record.dclass;
        if (i7 != 0) {
            return i7;
        }
        int i8 = this.type - record.type;
        if (i8 != 0) {
            return i8;
        }
        byte[] rdataToWireCanonical = rdataToWireCanonical();
        byte[] rdataToWireCanonical2 = record.rdataToWireCanonical();
        for (int i9 = 0; i9 < rdataToWireCanonical.length && i9 < rdataToWireCanonical2.length; i9++) {
            int i10 = (rdataToWireCanonical[i9] & 255) - (rdataToWireCanonical2[i9] & 255);
            if (i10 != 0) {
                return i10;
            }
        }
        return rdataToWireCanonical.length - rdataToWireCanonical2.length;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Record)) {
            return false;
        }
        Record record = (Record) obj;
        if (this.type == record.type && this.dclass == record.dclass && this.name.equals(record.name)) {
            return Arrays.equals(rdataToWireCanonical(), record.rdataToWireCanonical());
        }
        return false;
    }

    public Name getAdditionalName() {
        return null;
    }

    public int getDClass() {
        return this.dclass;
    }

    public Name getName() {
        return this.name;
    }

    public abstract Record getObject();

    public int getRRsetType() {
        int i7 = this.type;
        int i8 = i7;
        if (i7 == 46) {
            i8 = ((RRSIGRecord) this).getTypeCovered();
        }
        return i8;
    }

    public long getTTL() {
        return this.ttl;
    }

    public int getType() {
        return this.type;
    }

    public int hashCode() {
        int i7 = 0;
        for (byte b8 : toWireCanonical(true)) {
            i7 += (i7 << 3) + (b8 & 255);
        }
        return i7;
    }

    public abstract void rdataFromString(Tokenizer tokenizer, Name name);

    public String rdataToString() {
        return rrToString();
    }

    public byte[] rdataToWireCanonical() {
        DNSOutput dNSOutput = new DNSOutput();
        rrToWire(dNSOutput, null, true);
        return dNSOutput.toByteArray();
    }

    public abstract void rrFromWire(DNSInput dNSInput);

    public abstract String rrToString();

    public abstract void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7);

    public boolean sameRRset(Record record) {
        return getRRsetType() == record.getRRsetType() && this.dclass == record.dclass && this.name.equals(record.name);
    }

    public void setTTL(long j7) {
        this.ttl = j7;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.name);
        if (stringBuffer.length() < 8) {
            stringBuffer.append("\t");
        }
        if (stringBuffer.length() < 16) {
            stringBuffer.append("\t");
        }
        stringBuffer.append("\t");
        boolean check = Options.check("BINDTTL");
        long j7 = this.ttl;
        if (check) {
            stringBuffer.append(TTL.format(j7));
        } else {
            stringBuffer.append(j7);
        }
        stringBuffer.append("\t");
        if (this.dclass != 1 || !Options.check("noPrintIN")) {
            stringBuffer.append(DClass.string(this.dclass));
            stringBuffer.append("\t");
        }
        stringBuffer.append(Type.string(this.type));
        String rrToString = rrToString();
        if (!rrToString.equals(BuildConfig.FLAVOR)) {
            stringBuffer.append("\t");
            stringBuffer.append(rrToString);
        }
        return stringBuffer.toString();
    }

    public void toWire(DNSOutput dNSOutput, int i7, Compression compression) {
        this.name.toWire(dNSOutput, compression);
        dNSOutput.writeU16(this.type);
        dNSOutput.writeU16(this.dclass);
        if (i7 == 0) {
            return;
        }
        dNSOutput.writeU32(this.ttl);
        int current = dNSOutput.current();
        dNSOutput.writeU16(0);
        rrToWire(dNSOutput, compression, false);
        dNSOutput.writeU16At((dNSOutput.current() - current) - 2, current);
    }

    public byte[] toWire(int i7) {
        DNSOutput dNSOutput = new DNSOutput();
        toWire(dNSOutput, i7, null);
        return dNSOutput.toByteArray();
    }

    public byte[] toWireCanonical() {
        return toWireCanonical(false);
    }

    public Record withDClass(int i7, long j7) {
        Record cloneRecord = cloneRecord();
        cloneRecord.dclass = i7;
        cloneRecord.ttl = j7;
        return cloneRecord;
    }

    public Record withName(Name name) {
        if (!name.isAbsolute()) {
            throw new RelativeNameException(name);
        }
        Record cloneRecord = cloneRecord();
        cloneRecord.name = name;
        return cloneRecord;
    }
}
