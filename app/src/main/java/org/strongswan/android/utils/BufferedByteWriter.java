package org.strongswan.android.utils;

import java.nio.ByteBuffer;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/utils/BufferedByteWriter.class */
public class BufferedByteWriter {
    private byte[] mBuffer;
    private ByteBuffer mWriter;

    public BufferedByteWriter() {
        this(0);
    }

    public BufferedByteWriter(int i7) {
        byte[] bArr = new byte[i7 <= 4 ? 32 : i7];
        this.mBuffer = bArr;
        this.mWriter = ByteBuffer.wrap(bArr);
    }

    private void ensureCapacity(int i7) {
        if (this.mWriter.remaining() >= i7) {
            return;
        }
        byte[] bArr = this.mBuffer;
        byte[] bArr2 = new byte[(bArr.length + i7) * 2];
        System.arraycopy(bArr, 0, bArr2, 0, this.mWriter.position());
        this.mBuffer = bArr2;
        ByteBuffer wrap = ByteBuffer.wrap(bArr2);
        wrap.position(this.mWriter.position());
        this.mWriter = wrap;
    }

    public BufferedByteWriter put(byte b8) {
        ensureCapacity(1);
        this.mWriter.put(b8);
        return this;
    }

    public BufferedByteWriter put(byte[] bArr) {
        ensureCapacity(bArr.length);
        this.mWriter.put(bArr);
        return this;
    }

    public BufferedByteWriter put16(byte b8) {
        return put16((short) (b8 & 255));
    }

    public BufferedByteWriter put16(short s7) {
        ensureCapacity(2);
        this.mWriter.putShort(s7);
        return this;
    }

    public BufferedByteWriter put24(byte b8) {
        ensureCapacity(3);
        this.mWriter.putShort((short) 0);
        this.mWriter.put(b8);
        return this;
    }

    public BufferedByteWriter put24(int i7) {
        ensureCapacity(3);
        this.mWriter.put((byte) (i7 >> 16));
        this.mWriter.putShort((short) i7);
        return this;
    }

    public BufferedByteWriter put24(short s7) {
        ensureCapacity(3);
        this.mWriter.put((byte) 0);
        this.mWriter.putShort(s7);
        return this;
    }

    public BufferedByteWriter put32(byte b8) {
        return put32(b8 & 255);
    }

    public BufferedByteWriter put32(int i7) {
        ensureCapacity(4);
        this.mWriter.putInt(i7);
        return this;
    }

    public BufferedByteWriter put32(short s7) {
        return put32(s7 & 65535);
    }

    public BufferedByteWriter put64(byte b8) {
        return put64(b8 & 255);
    }

    public BufferedByteWriter put64(int i7) {
        return put64(i7 & 4294967295L);
    }

    public BufferedByteWriter put64(long j7) {
        ensureCapacity(8);
        this.mWriter.putLong(j7);
        return this;
    }

    public BufferedByteWriter put64(short s7) {
        return put64(s7 & 65535);
    }

    public BufferedByteWriter putLen16(byte[] bArr) {
        ensureCapacity(bArr.length + 2);
        this.mWriter.putShort((short) bArr.length);
        this.mWriter.put(bArr);
        return this;
    }

    public BufferedByteWriter putLen8(byte[] bArr) {
        ensureCapacity(bArr.length + 1);
        this.mWriter.put((byte) bArr.length);
        this.mWriter.put(bArr);
        return this;
    }

    public byte[] toByteArray() {
        int position = this.mWriter.position();
        byte[] bArr = new byte[position];
        System.arraycopy(this.mBuffer, 0, bArr, 0, position);
        return bArr;
    }
}
