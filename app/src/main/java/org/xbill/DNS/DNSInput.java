package org.xbill.DNS;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSInput.class */
public class DNSInput {
    private byte[] array;
    private int end;
    private int pos = 0;
    private int saved_pos = -1;
    private int saved_end = -1;

    public DNSInput(byte[] bArr) {
        this.array = bArr;
        this.end = bArr.length;
    }

    private void require(int i7) {
        if (i7 > remaining()) {
            throw new WireParseException("end of input");
        }
    }

    public void clearActive() {
        this.end = this.array.length;
    }

    public int current() {
        return this.pos;
    }

    public void jump(int i7) {
        byte[] bArr = this.array;
        if (i7 >= bArr.length) {
            throw new IllegalArgumentException("cannot jump past end of input");
        }
        this.pos = i7;
        this.end = bArr.length;
    }

    public void readByteArray(byte[] bArr, int i7, int i8) {
        require(i8);
        System.arraycopy(this.array, this.pos, bArr, i7, i8);
        this.pos += i8;
    }

    public byte[] readByteArray() {
        int remaining = remaining();
        byte[] bArr = new byte[remaining];
        System.arraycopy(this.array, this.pos, bArr, 0, remaining);
        this.pos += remaining;
        return bArr;
    }

    public byte[] readByteArray(int i7) {
        require(i7);
        byte[] bArr = new byte[i7];
        System.arraycopy(this.array, this.pos, bArr, 0, i7);
        this.pos += i7;
        return bArr;
    }

    public byte[] readCountedString() {
        require(1);
        byte[] bArr = this.array;
        int i7 = this.pos;
        this.pos = i7 + 1;
        return readByteArray(bArr[i7] & 255);
    }

    public int readU16() {
        require(2);
        byte[] bArr = this.array;
        int i7 = this.pos;
        int i8 = i7 + 1;
        byte b8 = bArr[i7];
        this.pos = i8 + 1;
        return ((b8 & 255) << 8) + (bArr[i8] & 255);
    }

    public long readU32() {
        require(4);
        byte[] bArr = this.array;
        int i7 = this.pos;
        int i8 = i7 + 1;
        byte b8 = bArr[i7];
        int i9 = i8 + 1;
        byte b9 = bArr[i8];
        int i10 = i9 + 1;
        byte b10 = bArr[i9];
        this.pos = i10 + 1;
        return ((b8 & 255) << 24) + ((b9 & 255) << 16) + ((b10 & 255) << 8) + (bArr[i10] & 255);
    }

    public int readU8() {
        require(1);
        byte[] bArr = this.array;
        int i7 = this.pos;
        this.pos = i7 + 1;
        return bArr[i7] & 255;
    }

    public int remaining() {
        return this.end - this.pos;
    }

    public void restore() {
        int i7 = this.saved_pos;
        if (i7 < 0) {
            throw new IllegalStateException("no previous state");
        }
        this.pos = i7;
        this.end = this.saved_end;
        this.saved_pos = -1;
        this.saved_end = -1;
    }

    public void restoreActive(int i7) {
        if (i7 > this.array.length) {
            throw new IllegalArgumentException("cannot set active region past end of input");
        }
        this.end = i7;
    }

    public void save() {
        this.saved_pos = this.pos;
        this.saved_end = this.end;
    }

    public int saveActive() {
        return this.end;
    }

    public void setActive(int i7) {
        int length = this.array.length;
        int i8 = this.pos;
        if (i7 > length - i8) {
            throw new IllegalArgumentException("cannot set active region past end of input");
        }
        this.end = i8 + i7;
    }
}
