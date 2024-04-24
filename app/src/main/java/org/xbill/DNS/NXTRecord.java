package org.xbill.DNS;

import androidx.activity.result.a;
import java.util.BitSet;
import org.xbill.DNS.Tokenizer;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/NXTRecord.class */
public class NXTRecord extends Record {
    private static final long serialVersionUID = -8851454400765507520L;
    private BitSet bitmap;
    private Name next;

    public NXTRecord() {
    }

    public NXTRecord(Name name, int i7, long j7, Name name2, BitSet bitSet) {
        super(name, 30, i7, j7);
        this.next = Record.checkName("next", name2);
        this.bitmap = bitSet;
    }

    public BitSet getBitmap() {
        return this.bitmap;
    }

    public Name getNext() {
        return this.next;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new NXTRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        Tokenizer.Token token;
        this.next = tokenizer.getName(name);
        this.bitmap = new BitSet();
        while (true) {
            token = tokenizer.get();
            if (!token.isString()) {
                tokenizer.unget();
                return;
            }
            int value = Type.value(token.value, true);
            if (value <= 0 || value > 128) {
                break;
            } else {
                this.bitmap.set(value);
            }
        }
        StringBuffer p = a.p("Invalid type: ");
        p.append(token.value);
        throw tokenizer.exception(p.toString());
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.next = new Name(dNSInput);
        this.bitmap = new BitSet();
        int remaining = dNSInput.remaining();
        for (int i7 = 0; i7 < remaining; i7++) {
            int readU8 = dNSInput.readU8();
            for (int i8 = 0; i8 < 8; i8++) {
                if (((1 << (7 - i8)) & readU8) != 0) {
                    this.bitmap.set((i7 * 8) + i8);
                }
            }
        }
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.next);
        int length = this.bitmap.length();
        short s7 = 0;
        while (true) {
            short s8 = s7;
            if (s8 >= length) {
                return stringBuffer.toString();
            }
            if (this.bitmap.get(s8)) {
                stringBuffer.append(" ");
                stringBuffer.append(Type.string(s8));
            }
            s7 = (short) (s8 + 1);
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        int i7;
        this.next.toWire(dNSOutput, null, z7);
        int length = this.bitmap.length();
        int i8 = 0;
        for (0; i7 < length; i7 + 1) {
            int i9 = i8 | (this.bitmap.get(i7) ? 1 << (7 - (i7 % 8)) : 0);
            if (i7 % 8 != 7) {
                i8 = i9;
                i7 = i7 != length - 1 ? i7 + 1 : 0;
            }
            dNSOutput.writeU8(i9);
            i8 = 0;
        }
    }
}
