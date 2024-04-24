package org.xbill.DNS;

import androidx.activity.result.a;
import java.security.PublicKey;
import org.xbill.DNS.DNSSEC;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSKEYRecord.class */
public class DNSKEYRecord extends KEYBase {
    private static final long serialVersionUID = -8679800040426675002L;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSKEYRecord$Flags.class */
    public static class Flags {
        public static final int REVOKE = 128;
        public static final int SEP_KEY = 1;
        public static final int ZONE_KEY = 256;

        private Flags() {
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/DNSKEYRecord$Protocol.class */
    public static class Protocol {
        public static final int DNSSEC = 3;

        private Protocol() {
        }
    }

    public DNSKEYRecord() {
    }

    public DNSKEYRecord(Name name, int i7, long j7, int i8, int i9, int i10, PublicKey publicKey) {
        super(name, 48, i7, j7, i8, i9, i10, DNSSEC.fromPublicKey(publicKey, i10));
        this.publicKey = publicKey;
    }

    public DNSKEYRecord(Name name, int i7, long j7, int i8, int i9, int i10, byte[] bArr) {
        super(name, 48, i7, j7, i8, i9, i10, bArr);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new DNSKEYRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.flags = tokenizer.getUInt16();
        this.proto = tokenizer.getUInt8();
        String string = tokenizer.getString();
        int value = DNSSEC.Algorithm.value(string);
        this.alg = value;
        if (value < 0) {
            throw a.u("Invalid algorithm: ", string, tokenizer);
        }
        this.key = tokenizer.getBase64();
    }
}
