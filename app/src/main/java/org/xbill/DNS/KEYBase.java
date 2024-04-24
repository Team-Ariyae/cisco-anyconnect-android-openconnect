package org.xbill.DNS;

import java.security.PublicKey;
import org.strongswan.android.data.VpnProfileDataSource;
import org.xbill.DNS.utils.base64;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/KEYBase.class */
public abstract class KEYBase extends Record {
    private static final long serialVersionUID = 3469321722693285454L;
    public int alg;
    public int flags;
    public int footprint;
    public byte[] key;
    public int proto;
    public PublicKey publicKey;

    public KEYBase() {
        this.footprint = -1;
        this.publicKey = null;
    }

    public KEYBase(Name name, int i7, int i8, long j7, int i9, int i10, int i11, byte[] bArr) {
        super(name, i7, i8, j7);
        this.footprint = -1;
        this.publicKey = null;
        this.flags = Record.checkU16(VpnProfileDataSource.KEY_FLAGS, i9);
        this.proto = Record.checkU8("proto", i10);
        this.alg = Record.checkU8("alg", i11);
        this.key = bArr;
    }

    public int getAlgorithm() {
        return this.alg;
    }

    public int getFlags() {
        return this.flags;
    }

    public int getFootprint() {
        int i7;
        int i8;
        int i9 = this.footprint;
        if (i9 >= 0) {
            return i9;
        }
        DNSOutput dNSOutput = new DNSOutput();
        int i10 = 0;
        rrToWire(dNSOutput, null, false);
        byte[] byteArray = dNSOutput.toByteArray();
        if (this.alg == 1) {
            byte b8 = byteArray[byteArray.length - 3];
            i8 = byteArray[byteArray.length - 2] & 255;
            i7 = (b8 & 255) << 8;
        } else {
            int i11 = 0;
            while (i10 < byteArray.length - 1) {
                i11 += ((byteArray[i10] & 255) << 8) + (byteArray[i10 + 1] & 255);
                i10 += 2;
            }
            i7 = i11;
            if (i10 < byteArray.length) {
                i7 = i11 + ((byteArray[i10] & 255) << 8);
            }
            i8 = (i7 >> 16) & Message.MAXLENGTH;
        }
        int i12 = (i7 + i8) & Message.MAXLENGTH;
        this.footprint = i12;
        return i12;
    }

    public byte[] getKey() {
        return this.key;
    }

    public int getProtocol() {
        return this.proto;
    }

    public PublicKey getPublicKey() {
        PublicKey publicKey = this.publicKey;
        if (publicKey != null) {
            return publicKey;
        }
        PublicKey publicKey2 = DNSSEC.toPublicKey(this);
        this.publicKey = publicKey2;
        return publicKey2;
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.flags = dNSInput.readU16();
        this.proto = dNSInput.readU8();
        this.alg = dNSInput.readU8();
        if (dNSInput.remaining() > 0) {
            this.key = dNSInput.readByteArray();
        }
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.flags);
        stringBuffer.append(" ");
        stringBuffer.append(this.proto);
        stringBuffer.append(" ");
        stringBuffer.append(this.alg);
        if (this.key != null) {
            if (Options.check("multiline")) {
                stringBuffer.append(" (\n");
                stringBuffer.append(base64.formatString(this.key, 64, "\t", true));
                stringBuffer.append(" ; key_tag = ");
                stringBuffer.append(getFootprint());
            } else {
                stringBuffer.append(" ");
                stringBuffer.append(base64.toString(this.key));
            }
        }
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU16(this.flags);
        dNSOutput.writeU8(this.proto);
        dNSOutput.writeU8(this.alg);
        byte[] bArr = this.key;
        if (bArr != null) {
            dNSOutput.writeByteArray(bArr);
        }
    }
}
