package org.xbill.DNS;

import androidx.activity.result.a;
import java.security.PublicKey;
import java.util.StringTokenizer;
import org.xbill.DNS.DNSSEC;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/KEYRecord.class */
public class KEYRecord extends KEYBase {
    public static final int FLAG_NOAUTH = 32768;
    public static final int FLAG_NOCONF = 16384;
    public static final int FLAG_NOKEY = 49152;
    public static final int OWNER_HOST = 512;
    public static final int OWNER_USER = 0;
    public static final int OWNER_ZONE = 256;
    public static final int PROTOCOL_ANY = 255;
    public static final int PROTOCOL_DNSSEC = 3;
    public static final int PROTOCOL_EMAIL = 2;
    public static final int PROTOCOL_IPSEC = 4;
    public static final int PROTOCOL_TLS = 1;
    private static final long serialVersionUID = 6385613447571488906L;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/KEYRecord$Flags.class */
    public static class Flags {
        public static final int EXTEND = 4096;
        public static final int FLAG10 = 32;
        public static final int FLAG11 = 16;
        public static final int FLAG2 = 8192;
        public static final int FLAG4 = 2048;
        public static final int FLAG5 = 1024;
        public static final int FLAG8 = 128;
        public static final int FLAG9 = 64;
        public static final int HOST = 512;
        public static final int NOAUTH = 32768;
        public static final int NOCONF = 16384;
        public static final int NOKEY = 49152;
        public static final int NTYP3 = 768;
        public static final int OWNER_MASK = 768;
        public static final int SIG0 = 0;
        public static final int SIG1 = 1;
        public static final int SIG10 = 10;
        public static final int SIG11 = 11;
        public static final int SIG12 = 12;
        public static final int SIG13 = 13;
        public static final int SIG14 = 14;
        public static final int SIG15 = 15;
        public static final int SIG2 = 2;
        public static final int SIG3 = 3;
        public static final int SIG4 = 4;
        public static final int SIG5 = 5;
        public static final int SIG6 = 6;
        public static final int SIG7 = 7;
        public static final int SIG8 = 8;
        public static final int SIG9 = 9;
        public static final int USER = 0;
        public static final int USE_MASK = 49152;
        public static final int ZONE = 256;
        private static Mnemonic flags;

        static {
            Mnemonic mnemonic = new Mnemonic("KEY flags", 2);
            flags = mnemonic;
            mnemonic.setMaximum(Message.MAXLENGTH);
            flags.setNumericAllowed(false);
            flags.add(16384, "NOCONF");
            flags.add(32768, "NOAUTH");
            flags.add(49152, "NOKEY");
            flags.add(FLAG2, "FLAG2");
            flags.add(EXTEND, "EXTEND");
            flags.add(FLAG4, "FLAG4");
            flags.add(FLAG5, "FLAG5");
            flags.add(0, "USER");
            flags.add(256, "ZONE");
            flags.add(512, "HOST");
            flags.add(768, "NTYP3");
            flags.add(128, "FLAG8");
            flags.add(64, "FLAG9");
            flags.add(32, "FLAG10");
            flags.add(16, "FLAG11");
            flags.add(0, "SIG0");
            flags.add(1, "SIG1");
            flags.add(2, "SIG2");
            flags.add(3, "SIG3");
            flags.add(4, "SIG4");
            flags.add(5, "SIG5");
            flags.add(6, "SIG6");
            flags.add(7, "SIG7");
            flags.add(8, "SIG8");
            flags.add(9, "SIG9");
            flags.add(10, "SIG10");
            flags.add(11, "SIG11");
            flags.add(12, "SIG12");
            flags.add(13, "SIG13");
            flags.add(14, "SIG14");
            flags.add(15, "SIG15");
        }

        private Flags() {
        }

        public static int value(String str) {
            try {
                int parseInt = Integer.parseInt(str);
                if (parseInt < 0 || parseInt > 65535) {
                    return -1;
                }
                return parseInt;
            } catch (NumberFormatException e8) {
                StringTokenizer stringTokenizer = new StringTokenizer(str, "|");
                int i7 = 0;
                while (true) {
                    int i8 = i7;
                    if (!stringTokenizer.hasMoreTokens()) {
                        return i8;
                    }
                    int value = flags.getValue(stringTokenizer.nextToken());
                    if (value < 0) {
                        return -1;
                    }
                    i7 = i8 | value;
                }
            }
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/KEYRecord$Protocol.class */
    public static class Protocol {
        public static final int ANY = 255;
        public static final int DNSSEC = 3;
        public static final int EMAIL = 2;
        public static final int IPSEC = 4;
        public static final int NONE = 0;
        public static final int TLS = 1;
        private static Mnemonic protocols;

        static {
            Mnemonic mnemonic = new Mnemonic("KEY protocol", 2);
            protocols = mnemonic;
            mnemonic.setMaximum(255);
            protocols.setNumericAllowed(true);
            protocols.add(0, "NONE");
            protocols.add(1, "TLS");
            protocols.add(2, "EMAIL");
            protocols.add(3, "DNSSEC");
            protocols.add(4, "IPSEC");
            protocols.add(255, "ANY");
        }

        private Protocol() {
        }

        public static String string(int i7) {
            return protocols.getText(i7);
        }

        public static int value(String str) {
            return protocols.getValue(str);
        }
    }

    public KEYRecord() {
    }

    public KEYRecord(Name name, int i7, long j7, int i8, int i9, int i10, PublicKey publicKey) {
        super(name, 25, i7, j7, i8, i9, i10, DNSSEC.fromPublicKey(publicKey, i10));
        this.publicKey = publicKey;
    }

    public KEYRecord(Name name, int i7, long j7, int i8, int i9, int i10, byte[] bArr) {
        super(name, 25, i7, j7, i8, i9, i10, bArr);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new KEYRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        String identifier = tokenizer.getIdentifier();
        int value = Flags.value(identifier);
        this.flags = value;
        if (value < 0) {
            throw a.u("Invalid flags: ", identifier, tokenizer);
        }
        String identifier2 = tokenizer.getIdentifier();
        int value2 = Protocol.value(identifier2);
        this.proto = value2;
        if (value2 < 0) {
            throw a.u("Invalid protocol: ", identifier2, tokenizer);
        }
        String identifier3 = tokenizer.getIdentifier();
        int value3 = DNSSEC.Algorithm.value(identifier3);
        this.alg = value3;
        if (value3 < 0) {
            throw a.u("Invalid algorithm: ", identifier3, tokenizer);
        }
        if ((this.flags & 49152) == 49152) {
            this.key = null;
        } else {
            this.key = tokenizer.getBase64();
        }
    }
}
