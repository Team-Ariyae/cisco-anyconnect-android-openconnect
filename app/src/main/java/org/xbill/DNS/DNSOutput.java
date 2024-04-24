package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSOutput.class */
public class DNSOutput {
    private byte[] array;
    private int pos;
    private int saved_pos;

    public DNSOutput() {
        this(32);
    }

    public DNSOutput(int i7) {
        this.array = new byte[i7];
        this.pos = 0;
        this.saved_pos = -1;
    }

    private void check(long j7, int i7) {
        if (j7 < 0 || j7 > (1 << i7)) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(j7);
            stringBuffer.append(" out of range for ");
            stringBuffer.append(i7);
            stringBuffer.append(" bit value");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
    }

    private void need(int i7) {
        byte[] bArr = this.array;
        int length = bArr.length;
        int i8 = this.pos;
        if (length - i8 >= i7) {
            return;
        }
        int length2 = bArr.length * 2;
        int i9 = length2;
        if (length2 < i8 + i7) {
            i9 = i8 + i7;
        }
        byte[] bArr2 = new byte[i9];
        System.arraycopy(bArr, 0, bArr2, 0, i8);
        this.array = bArr2;
    }

    public int current() {
        return this.pos;
    }

    public void jump(int i7) {
        if (i7 > this.pos) {
            throw new IllegalArgumentException("cannot jump past end of data");
        }
        this.pos = i7;
    }

    public void restore() {
        int i7 = this.saved_pos;
        if (i7 < 0) {
            throw new IllegalStateException("no previous state");
        }
        this.pos = i7;
        this.saved_pos = -1;
    }

    public void save() {
        this.saved_pos = this.pos;
    }

    public byte[] toByteArray() {
        int i7 = this.pos;
        byte[] bArr = new byte[i7];
        System.arraycopy(this.array, 0, bArr, 0, i7);
        return bArr;
    }

    public void writeByteArray(byte[] bArr) {
        writeByteArray(bArr, 0, bArr.length);
    }

    public void writeByteArray(byte[] bArr, int i7, int i8) {
        need(i8);
        System.arraycopy(bArr, i7, this.array, this.pos, i8);
        this.pos += i8;
    }

    public void writeCountedString(byte[] bArr) {
        if (bArr.length > 255) {
            throw new IllegalArgumentException("Invalid counted string");
        }
        need(bArr.length + 1);
        byte[] bArr2 = this.array;
        int i7 = this.pos;
        this.pos = i7 + 1;
        bArr2[i7] = (byte) (255 & bArr.length);
        writeByteArray(bArr, 0, bArr.length);
    }

    public void writeU16(int i7) {
        check(i7, 16);
        need(2);
        byte[] bArr = this.array;
        int i8 = this.pos;
        int i9 = i8 + 1;
        bArr[i8] = (byte) ((i7 >>> 8) & 255);
        this.pos = i9 + 1;
        bArr[i9] = (byte) (i7 & 255);
    }

    public void writeU16At(int i7, int i8) {
        check(i7, 16);
        if (i8 > this.pos - 2) {
            throw new IllegalArgumentException("cannot write past end of data");
        }
        byte[] bArr = this.array;
        bArr[i8] = (byte) ((i7 >>> 8) & 255);
        bArr[i8 + 1] = (byte) (i7 & 255);
    }

    public void writeU32(long j7) {
        check(j7, 32);
        need(4);
        byte[] bArr = this.array;
        int i7 = this.pos;
        int i8 = i7 + 1;
        bArr[i7] = (byte) ((j7 >>> 24) & 255);
        int i9 = i8 + 1;
        bArr[i8] = (byte) ((j7 >>> 16) & 255);
        int i10 = i9 + 1;
        bArr[i9] = (byte) ((j7 >>> 8) & 255);
        this.pos = i10 + 1;
        bArr[i10] = (byte) (j7 & 255);
    }

    public void writeU8(int i7) {
        check(i7, 8);
        need(1);
        byte[] bArr = this.array;
        int i8 = this.pos;
        this.pos = i8 + 1;
        bArr[i8] = (byte) (i7 & 255);
    }
}
