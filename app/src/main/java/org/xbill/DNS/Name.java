package org.xbill.DNS;

import androidx.activity.result.a;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DecimalFormat;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Name.class */
public class Name implements Comparable, Serializable {
    private static final int LABEL_COMPRESSION = 192;
    private static final int LABEL_MASK = 192;
    private static final int LABEL_NORMAL = 0;
    private static final int MAXLABEL = 63;
    private static final int MAXLABELS = 128;
    private static final int MAXNAME = 255;
    private static final int MAXOFFSETS = 7;
    private static final DecimalFormat byteFormat;
    public static final Name empty;
    private static final byte[] lowercase;
    public static final Name root;
    private static final long serialVersionUID = -7257019940971525644L;
    private static final Name wild;
    private int hashcode;
    private byte[] name;
    private long offsets;
    private static final byte[] emptyLabel = {0};
    private static final byte[] wildLabel = {1, 42};

    static {
        DecimalFormat decimalFormat = new DecimalFormat();
        byteFormat = decimalFormat;
        lowercase = new byte[256];
        decimalFormat.setMinimumIntegerDigits(3);
        int i7 = 0;
        while (true) {
            byte[] bArr = lowercase;
            if (i7 >= bArr.length) {
                Name name = new Name();
                root = name;
                name.appendSafe(emptyLabel, 0, 1);
                Name name2 = new Name();
                empty = name2;
                name2.name = new byte[0];
                Name name3 = new Name();
                wild = name3;
                name3.appendSafe(wildLabel, 0, 1);
                return;
            }
            if (i7 < 65 || i7 > 90) {
                bArr[i7] = (byte) i7;
            } else {
                bArr[i7] = (byte) ((i7 - 65) + 97);
            }
            i7++;
        }
    }

    private Name() {
    }

    public Name(String str) {
        this(str, (Name) null);
    }

    public Name(String str, Name name) {
        boolean z7;
        int i7;
        boolean z8;
        int i8;
        int i9;
        byte b8;
        if (str.equals(BuildConfig.FLAVOR)) {
            throw parseException(str, "empty name");
        }
        if (str.equals("@")) {
            if (name == null) {
                copy(empty, this);
                return;
            } else {
                copy(name, this);
                return;
            }
        }
        if (str.equals(".")) {
            copy(root, this);
            return;
        }
        byte[] bArr = new byte[64];
        int i10 = 0;
        boolean z9 = false;
        int i11 = -1;
        int i12 = 1;
        int i13 = 0;
        for (int i14 = 0; i14 < str.length(); i14++) {
            byte charAt = (byte) str.charAt(i14);
            if (z9) {
                if (charAt < 48 || charAt > 57 || i10 >= 3) {
                    i8 = i10;
                    i9 = i13;
                    b8 = charAt;
                    if (i10 > 0) {
                        if (i10 < 3) {
                            throw parseException(str, "bad escape");
                        }
                        i8 = i10;
                        i9 = i13;
                        b8 = charAt;
                    }
                } else {
                    i10++;
                    i13 = (i13 * 10) + (charAt - 48);
                    if (i13 > 255) {
                        throw parseException(str, "bad escape");
                    }
                    if (i10 < 3) {
                        continue;
                    } else {
                        b8 = (byte) i13;
                        i8 = i10;
                        i9 = i13;
                    }
                }
                if (i12 > 63) {
                    throw parseException(str, "label too long");
                }
                i7 = i12 + 1;
                bArr[i12] = b8;
                i13 = i9;
                i11 = i12;
                z8 = false;
                i10 = i8;
                z9 = z8;
                i12 = i7;
            } else {
                if (charAt == 92) {
                    i10 = 0;
                    z9 = true;
                    i13 = 0;
                } else if (charAt != 46) {
                    int i15 = i11 == -1 ? i14 : i11;
                    if (i12 > 63) {
                        throw parseException(str, "label too long");
                    }
                    i7 = i12 + 1;
                    bArr[i12] = charAt;
                    z8 = z9;
                    i11 = i15;
                    z9 = z8;
                    i12 = i7;
                } else {
                    if (i11 == -1) {
                        throw parseException(str, "invalid empty label");
                    }
                    bArr[0] = (byte) (i12 - 1);
                    appendFromString(str, bArr, 0, 1);
                    i11 = -1;
                    i12 = 1;
                }
            }
        }
        if (i10 > 0 && i10 < 3) {
            throw parseException(str, "bad escape");
        }
        if (z9) {
            throw parseException(str, "bad escape");
        }
        if (i11 == -1) {
            z7 = true;
            appendFromString(str, emptyLabel, 0, 1);
        } else {
            bArr[0] = (byte) (i12 - 1);
            appendFromString(str, bArr, 0, 1);
            z7 = false;
        }
        if (name == null || z7) {
            return;
        }
        appendFromString(str, name.name, name.offset(0), name.getlabels());
    }

    public Name(DNSInput dNSInput) {
        byte[] bArr = new byte[64];
        boolean z7 = false;
        boolean z8 = false;
        while (!z7) {
            int readU8 = dNSInput.readU8();
            int i7 = readU8 & 192;
            if (i7 != 0) {
                if (i7 != 192) {
                    throw new WireParseException("bad label type");
                }
                int readU82 = dNSInput.readU8() + ((readU8 & (-193)) << 8);
                if (Options.check("verbosecompression")) {
                    PrintStream printStream = System.err;
                    StringBuffer p = a.p("currently ");
                    p.append(dNSInput.current());
                    p.append(", pointer to ");
                    p.append(readU82);
                    printStream.println(p.toString());
                }
                if (readU82 >= dNSInput.current() - 2) {
                    throw new WireParseException("bad compression");
                }
                boolean z9 = z8;
                if (!z8) {
                    dNSInput.save();
                    z9 = true;
                }
                dNSInput.jump(readU82);
                z8 = z9;
                if (Options.check("verbosecompression")) {
                    PrintStream printStream2 = System.err;
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("current name '");
                    stringBuffer.append(this);
                    stringBuffer.append("', seeking to ");
                    stringBuffer.append(readU82);
                    printStream2.println(stringBuffer.toString());
                    z8 = z9;
                }
            } else {
                if (getlabels() >= 128) {
                    throw new WireParseException("too many labels");
                }
                if (readU8 == 0) {
                    append(emptyLabel, 0, 1);
                    z7 = true;
                } else {
                    bArr[0] = (byte) readU8;
                    dNSInput.readByteArray(bArr, 1, readU8);
                    append(bArr, 0, 1);
                }
            }
        }
        if (z8) {
            dNSInput.restore();
        }
    }

    public Name(Name name, int i7) {
        int labels = name.labels();
        if (i7 > labels) {
            throw new IllegalArgumentException("attempted to remove too many labels");
        }
        this.name = name.name;
        int i8 = labels - i7;
        setlabels(i8);
        for (int i9 = 0; i9 < 7 && i9 < i8; i9++) {
            setoffset(i9, name.offset(i9 + i7));
        }
    }

    public Name(byte[] bArr) {
        this(new DNSInput(bArr));
    }

    private final void append(byte[] bArr, int i7, int i8) {
        byte[] bArr2 = this.name;
        int length = bArr2 == null ? 0 : bArr2.length - offset(0);
        int i9 = i7;
        int i10 = 0;
        for (int i11 = 0; i11 < i8; i11++) {
            byte b8 = bArr[i9];
            if (b8 > 63) {
                throw new IllegalStateException("invalid label");
            }
            int i12 = b8 + 1;
            i9 += i12;
            i10 += i12;
        }
        int i13 = length + i10;
        if (i13 > 255) {
            throw new NameTooLongException();
        }
        int i14 = getlabels();
        int i15 = i14 + i8;
        if (i15 > 128) {
            throw new IllegalStateException("too many labels");
        }
        byte[] bArr3 = new byte[i13];
        if (length != 0) {
            System.arraycopy(this.name, offset(0), bArr3, 0, length);
        }
        System.arraycopy(bArr, i7, bArr3, length, i10);
        this.name = bArr3;
        for (int i16 = 0; i16 < i8; i16++) {
            setoffset(i14 + i16, length);
            length += bArr3[length] + 1;
        }
        setlabels(i15);
    }

    private final void appendFromString(String str, byte[] bArr, int i7, int i8) {
        try {
            append(bArr, i7, i8);
        } catch (NameTooLongException e8) {
            throw parseException(str, "Name too long");
        }
    }

    private final void appendSafe(byte[] bArr, int i7, int i8) {
        try {
            append(bArr, i7, i8);
        } catch (NameTooLongException e8) {
        }
    }

    private String byteString(byte[] bArr, int i7) {
        StringBuffer stringBuffer = new StringBuffer();
        int i8 = i7 + 1;
        byte b8 = bArr[i7];
        for (int i9 = i8; i9 < i8 + b8; i9++) {
            int i10 = bArr[i9] & 255;
            if (i10 <= 32 || i10 >= 127) {
                stringBuffer.append('\\');
                stringBuffer.append(byteFormat.format(i10));
            } else {
                if (i10 == 34 || i10 == 40 || i10 == 41 || i10 == 46 || i10 == 59 || i10 == 92 || i10 == 64 || i10 == 36) {
                    stringBuffer.append('\\');
                }
                stringBuffer.append((char) i10);
            }
        }
        return stringBuffer.toString();
    }

    public static Name concatenate(Name name, Name name2) {
        if (name.isAbsolute()) {
            return name;
        }
        Name name3 = new Name();
        copy(name, name3);
        name3.append(name2.name, name2.offset(0), name2.getlabels());
        return name3;
    }

    private static final void copy(Name name, Name name2) {
        if (name.offset(0) == 0) {
            name2.name = name.name;
            name2.offsets = name.offsets;
            return;
        }
        int offset = name.offset(0);
        int length = name.name.length - offset;
        int labels = name.labels();
        byte[] bArr = new byte[length];
        name2.name = bArr;
        System.arraycopy(name.name, offset, bArr, 0, length);
        for (int i7 = 0; i7 < labels && i7 < 7; i7++) {
            name2.setoffset(i7, name.offset(i7) - offset);
        }
        name2.setlabels(labels);
    }

    private final boolean equals(byte[] bArr, int i7) {
        int labels = labels();
        int offset = offset(0);
        for (int i8 = 0; i8 < labels; i8++) {
            byte b8 = this.name[offset];
            if (b8 != bArr[i7]) {
                return false;
            }
            offset++;
            i7++;
            if (b8 > 63) {
                throw new IllegalStateException("invalid label");
            }
            for (int i9 = 0; i9 < b8; i9++) {
                byte[] bArr2 = lowercase;
                if (bArr2[this.name[offset] & 255] != bArr2[bArr[i7] & 255]) {
                    return false;
                }
                i7++;
                offset++;
            }
        }
        return true;
    }

    public static Name fromConstantString(String str) {
        try {
            return fromString(str, null);
        } catch (TextParseException e8) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Invalid name '");
            stringBuffer.append(str);
            stringBuffer.append("'");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
    }

    public static Name fromString(String str) {
        return fromString(str, null);
    }

    public static Name fromString(String str, Name name) {
        return (!str.equals("@") || name == null) ? str.equals(".") ? root : new Name(str, name) : name;
    }

    private final int getlabels() {
        return (int) (this.offsets & 255);
    }

    private final int offset(int i7) {
        if (i7 == 0 && getlabels() == 0) {
            return 0;
        }
        if (i7 < 0 || i7 >= getlabels()) {
            throw new IllegalArgumentException("label out of range");
        }
        if (i7 < 7) {
            return ((int) (this.offsets >>> ((7 - i7) * 8))) & 255;
        }
        int offset = offset(6);
        for (int i8 = 6; i8 < i7; i8++) {
            offset += this.name[offset] + 1;
        }
        return offset;
    }

    private static TextParseException parseException(String str, String str2) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("'");
        stringBuffer.append(str);
        stringBuffer.append("': ");
        stringBuffer.append(str2);
        return new TextParseException(stringBuffer.toString());
    }

    private final void setlabels(int i7) {
        this.offsets = (this.offsets & (-256)) | i7;
    }

    private final void setoffset(int i7, int i8) {
        if (i7 >= 7) {
            return;
        }
        int i9 = (7 - i7) * 8;
        this.offsets = (i8 << i9) | (this.offsets & ((255 << i9) ^ (-1)));
    }

    public Name canonicalize() {
        boolean z7;
        int i7 = 0;
        while (true) {
            byte[] bArr = this.name;
            if (i7 >= bArr.length) {
                z7 = true;
                break;
            }
            byte[] bArr2 = lowercase;
            byte b8 = bArr[i7];
            if (bArr2[b8 & 255] != b8) {
                z7 = false;
                break;
            }
            i7++;
        }
        if (z7) {
            return this;
        }
        Name name = new Name();
        name.appendSafe(this.name, offset(0), getlabels());
        int i8 = 0;
        while (true) {
            byte[] bArr3 = name.name;
            if (i8 >= bArr3.length) {
                return name;
            }
            bArr3[i8] = lowercase[bArr3[i8] & 255];
            i8++;
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        Name name = (Name) obj;
        if (this == name) {
            return 0;
        }
        int labels = labels();
        int labels2 = name.labels();
        int i7 = labels > labels2 ? labels2 : labels;
        for (int i8 = 1; i8 <= i7; i8++) {
            int offset = offset(labels - i8);
            int offset2 = name.offset(labels2 - i8);
            byte b8 = this.name[offset];
            byte b9 = name.name[offset2];
            for (int i9 = 0; i9 < b8 && i9 < b9; i9++) {
                byte[] bArr = lowercase;
                int i10 = bArr[this.name[(i9 + offset) + 1] & 255] - bArr[name.name[(i9 + offset2) + 1] & 255];
                if (i10 != 0) {
                    return i10;
                }
            }
            if (b8 != b9) {
                return b8 - b9;
            }
        }
        return labels - labels2;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof Name)) {
            return false;
        }
        Name name = (Name) obj;
        if (name.hashcode == 0) {
            name.hashCode();
        }
        if (this.hashcode == 0) {
            hashCode();
        }
        if (name.hashcode == this.hashcode && name.labels() == labels()) {
            return equals(name.name, name.offset(0));
        }
        return false;
    }

    public Name fromDNAME(DNAMERecord dNAMERecord) {
        Name name = dNAMERecord.getName();
        Name target = dNAMERecord.getTarget();
        if (!subdomain(name)) {
            return null;
        }
        int labels = labels();
        int labels2 = name.labels();
        int length = length() - name.length();
        int offset = offset(0);
        int labels3 = target.labels();
        short length2 = target.length();
        int i7 = length + length2;
        if (i7 > 255) {
            throw new NameTooLongException();
        }
        Name name2 = new Name();
        int i8 = (labels - labels2) + labels3;
        name2.setlabels(i8);
        byte[] bArr = new byte[i7];
        name2.name = bArr;
        System.arraycopy(this.name, offset, bArr, 0, length);
        System.arraycopy(target.name, 0, name2.name, length, length2);
        int i9 = 0;
        for (int i10 = 0; i10 < 7 && i10 < i8; i10++) {
            name2.setoffset(i10, i9);
            i9 += name2.name[i9] + 1;
        }
        return name2;
    }

    public byte[] getLabel(int i7) {
        int offset = offset(i7);
        byte[] bArr = this.name;
        int i8 = (byte) (bArr[offset] + 1);
        byte[] bArr2 = new byte[i8];
        System.arraycopy(bArr, offset, bArr2, 0, i8);
        return bArr2;
    }

    public String getLabelString(int i7) {
        return byteString(this.name, offset(i7));
    }

    public int hashCode() {
        int i7 = this.hashcode;
        if (i7 != 0) {
            return i7;
        }
        int i8 = 0;
        int offset = offset(0);
        while (true) {
            byte[] bArr = this.name;
            if (offset >= bArr.length) {
                this.hashcode = i8;
                return i8;
            }
            i8 += (i8 << 3) + lowercase[bArr[offset] & 255];
            offset++;
        }
    }

    public boolean isAbsolute() {
        int labels = labels();
        boolean z7 = false;
        if (labels == 0) {
            return false;
        }
        if (this.name[offset(labels - 1)] == 0) {
            z7 = true;
        }
        return z7;
    }

    public boolean isWild() {
        if (labels() == 0) {
            return false;
        }
        byte[] bArr = this.name;
        boolean z7 = false;
        if (bArr[0] == 1) {
            z7 = false;
            if (bArr[1] == 42) {
                z7 = true;
            }
        }
        return z7;
    }

    public int labels() {
        return getlabels();
    }

    public short length() {
        if (getlabels() == 0) {
            return (short) 0;
        }
        return (short) (this.name.length - offset(0));
    }

    public Name relativize(Name name) {
        if (name == null || !subdomain(name)) {
            return this;
        }
        Name name2 = new Name();
        copy(this, name2);
        int length = length() - name.length();
        name2.setlabels(name2.labels() - name.labels());
        name2.name = new byte[length];
        System.arraycopy(this.name, offset(0), name2.name, 0, length);
        return name2;
    }

    public boolean subdomain(Name name) {
        int labels = labels();
        int labels2 = name.labels();
        if (labels2 > labels) {
            return false;
        }
        return labels2 == labels ? equals(name) : name.equals(this.name, offset(labels - labels2));
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean z7) {
        int labels = labels();
        if (labels == 0) {
            return "@";
        }
        int i7 = 0;
        if (labels == 1 && this.name[offset(0)] == 0) {
            return ".";
        }
        StringBuffer stringBuffer = new StringBuffer();
        int offset = offset(0);
        while (true) {
            if (i7 >= labels) {
                break;
            }
            byte b8 = this.name[offset];
            if (b8 > 63) {
                throw new IllegalStateException("invalid label");
            }
            if (b8 != 0) {
                if (i7 > 0) {
                    stringBuffer.append('.');
                }
                stringBuffer.append(byteString(this.name, offset));
                offset += b8 + 1;
                i7++;
            } else if (!z7) {
                stringBuffer.append('.');
            }
        }
        return stringBuffer.toString();
    }

    public void toWire(DNSOutput dNSOutput, Compression compression) {
        if (!isAbsolute()) {
            throw new IllegalArgumentException("toWire() called on non-absolute name");
        }
        int labels = labels();
        int i7 = 0;
        while (i7 < labels - 1) {
            Name name = i7 == 0 ? this : new Name(this, i7);
            int i8 = -1;
            if (compression != null) {
                i8 = compression.get(name);
            }
            if (i8 >= 0) {
                dNSOutput.writeU16(49152 | i8);
                return;
            }
            if (compression != null) {
                compression.add(dNSOutput.current(), name);
            }
            int offset = offset(i7);
            byte[] bArr = this.name;
            dNSOutput.writeByteArray(bArr, offset, bArr[offset] + 1);
            i7++;
        }
        dNSOutput.writeU8(0);
    }

    public void toWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        if (z7) {
            toWireCanonical(dNSOutput);
        } else {
            toWire(dNSOutput, compression);
        }
    }

    public byte[] toWire() {
        DNSOutput dNSOutput = new DNSOutput();
        toWire(dNSOutput, null);
        return dNSOutput.toByteArray();
    }

    public void toWireCanonical(DNSOutput dNSOutput) {
        dNSOutput.writeByteArray(toWireCanonical());
    }

    public byte[] toWireCanonical() {
        int labels = labels();
        if (labels == 0) {
            return new byte[0];
        }
        byte[] bArr = new byte[this.name.length - offset(0)];
        int offset = offset(0);
        int i7 = 0;
        for (int i8 = 0; i8 < labels; i8++) {
            byte b8 = this.name[offset];
            if (b8 > 63) {
                throw new IllegalStateException("invalid label");
            }
            offset++;
            bArr[i7] = b8;
            i7++;
            for (int i9 = 0; i9 < b8; i9++) {
                bArr[i7] = lowercase[this.name[offset] & 255];
                i7++;
                offset++;
            }
        }
        return bArr;
    }

    public Name wild(int i7) {
        if (i7 < 1) {
            throw new IllegalArgumentException("must replace 1 or more labels");
        }
        try {
            Name name = new Name();
            copy(wild, name);
            name.append(this.name, offset(i7), getlabels() - i7);
            return name;
        } catch (NameTooLongException e8) {
            throw new IllegalStateException("Name.wild: concatenate failed");
        }
    }
}
