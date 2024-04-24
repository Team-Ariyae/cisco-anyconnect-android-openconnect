package org.xbill.DNS;

import androidx.activity.result.a;
import java.util.Random;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Header.class */
public class Header implements Cloneable {
    public static final int LENGTH = 12;
    private static Random random = new Random();
    private int[] counts;
    private int flags;
    private int id;

    public Header() {
        init();
    }

    public Header(int i7) {
        init();
        setID(i7);
    }

    public Header(DNSInput dNSInput) {
        this(dNSInput.readU16());
        this.flags = dNSInput.readU16();
        int i7 = 0;
        while (true) {
            int[] iArr = this.counts;
            if (i7 >= iArr.length) {
                return;
            }
            iArr[i7] = dNSInput.readU16();
            i7++;
        }
    }

    public Header(byte[] bArr) {
        this(new DNSInput(bArr));
    }

    private static void checkFlag(int i7) {
        if (validFlag(i7)) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("invalid flag bit ");
        stringBuffer.append(i7);
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    private void init() {
        this.counts = new int[4];
        this.flags = 0;
        this.id = -1;
    }

    public static int setFlag(int i7, int i8, boolean z7) {
        checkFlag(i8);
        int i9 = 1 << (15 - i8);
        return z7 ? i7 | i9 : i7 & (i9 ^ (-1));
    }

    private static boolean validFlag(int i7) {
        return i7 >= 0 && i7 <= 15 && Flags.isFlag(i7);
    }

    public Object clone() {
        Header header = new Header();
        header.id = this.id;
        header.flags = this.flags;
        int[] iArr = this.counts;
        System.arraycopy(iArr, 0, header.counts, 0, iArr.length);
        return header;
    }

    public void decCount(int i7) {
        int[] iArr = this.counts;
        int i8 = iArr[i7];
        if (i8 == 0) {
            throw new IllegalStateException("DNS section count cannot be decremented");
        }
        iArr[i7] = i8 - 1;
    }

    public int getCount(int i7) {
        return this.counts[i7];
    }

    public boolean getFlag(int i7) {
        checkFlag(i7);
        boolean z7 = true;
        if (((1 << (15 - i7)) & this.flags) == 0) {
            z7 = false;
        }
        return z7;
    }

    public boolean[] getFlags() {
        boolean[] zArr = new boolean[16];
        for (int i7 = 0; i7 < 16; i7++) {
            if (validFlag(i7)) {
                zArr[i7] = getFlag(i7);
            }
        }
        return zArr;
    }

    public int getFlagsByte() {
        return this.flags;
    }

    public int getID() {
        int i7;
        int i8 = this.id;
        if (i8 >= 0) {
            return i8;
        }
        synchronized (this) {
            if (this.id < 0) {
                this.id = random.nextInt(Message.MAXLENGTH);
            }
            i7 = this.id;
        }
        return i7;
    }

    public int getOpcode() {
        return (this.flags >> 11) & 15;
    }

    public int getRcode() {
        return this.flags & 15;
    }

    public void incCount(int i7) {
        int[] iArr = this.counts;
        int i8 = iArr[i7];
        if (i8 == 65535) {
            throw new IllegalStateException("DNS section count cannot be incremented");
        }
        iArr[i7] = i8 + 1;
    }

    public String printFlags() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i7 = 0; i7 < 16; i7++) {
            if (validFlag(i7) && getFlag(i7)) {
                stringBuffer.append(Flags.string(i7));
                stringBuffer.append(" ");
            }
        }
        return stringBuffer.toString();
    }

    public void setCount(int i7, int i8) {
        if (i8 >= 0 && i8 <= 65535) {
            this.counts[i7] = i8;
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("DNS section count ");
        stringBuffer.append(i8);
        stringBuffer.append(" is out of range");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public void setFlag(int i7) {
        checkFlag(i7);
        this.flags = setFlag(this.flags, i7, true);
    }

    public void setID(int i7) {
        if (i7 >= 0 && i7 <= 65535) {
            this.id = i7;
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("DNS message ID ");
        stringBuffer.append(i7);
        stringBuffer.append(" is out of range");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public void setOpcode(int i7) {
        if (i7 >= 0 && i7 <= 15) {
            this.flags = (i7 << 11) | (this.flags & 34815);
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("DNS Opcode ");
        stringBuffer.append(i7);
        stringBuffer.append("is out of range");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public void setRcode(int i7) {
        if (i7 >= 0 && i7 <= 15) {
            this.flags = i7 | (this.flags & (-16));
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("DNS Rcode ");
        stringBuffer.append(i7);
        stringBuffer.append(" is out of range");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public String toString() {
        return toStringWithRcode(getRcode());
    }

    public String toStringWithRcode(int i7) {
        StringBuffer p = a.p(";; ->>HEADER<<- ");
        StringBuffer p7 = a.p("opcode: ");
        p7.append(Opcode.string(getOpcode()));
        p.append(p7.toString());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(", status: ");
        stringBuffer.append(Rcode.string(i7));
        p.append(stringBuffer.toString());
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(", id: ");
        stringBuffer2.append(getID());
        p.append(stringBuffer2.toString());
        p.append("\n");
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append(";; flags: ");
        stringBuffer3.append(printFlags());
        p.append(stringBuffer3.toString());
        p.append("; ");
        for (int i8 = 0; i8 < 4; i8++) {
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append(Section.string(i8));
            stringBuffer4.append(": ");
            stringBuffer4.append(getCount(i8));
            stringBuffer4.append(" ");
            p.append(stringBuffer4.toString());
        }
        return p.toString();
    }

    public void toWire(DNSOutput dNSOutput) {
        dNSOutput.writeU16(getID());
        dNSOutput.writeU16(this.flags);
        int i7 = 0;
        while (true) {
            int[] iArr = this.counts;
            if (i7 >= iArr.length) {
                return;
            }
            dNSOutput.writeU16(iArr[i7]);
            i7++;
        }
    }

    public byte[] toWire() {
        DNSOutput dNSOutput = new DNSOutput();
        toWire(dNSOutput);
        return dNSOutput.toByteArray();
    }

    public void unsetFlag(int i7) {
        checkFlag(i7);
        this.flags = setFlag(this.flags, i7, false);
    }
}
