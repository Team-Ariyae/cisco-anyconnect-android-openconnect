package org.xbill.DNS;

import java.net.Inet6Address;
import java.net.InetAddress;
import org.strongswan.android.data.VpnProfileDataSource;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/IPSECKEYRecord.class */
public class IPSECKEYRecord extends Record {
    private static final long serialVersionUID = 3050449702765909687L;
    private int algorithmType;
    private Object gateway;
    private int gatewayType;
    private byte[] key;
    private int precedence;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/IPSECKEYRecord$Algorithm.class */
    public static class Algorithm {
        public static final int DSA = 1;
        public static final int RSA = 2;

        private Algorithm() {
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/IPSECKEYRecord$Gateway.class */
    public static class Gateway {
        public static final int IPv4 = 1;
        public static final int IPv6 = 2;
        public static final int Name = 3;
        public static final int None = 0;

        private Gateway() {
        }
    }

    public IPSECKEYRecord() {
    }

    public IPSECKEYRecord(Name name, int i7, long j7, int i8, int i9, int i10, Object obj, byte[] bArr) {
        super(name, 45, i7, j7);
        Name name2;
        this.precedence = Record.checkU8("precedence", i8);
        this.gatewayType = Record.checkU8("gatewayType", i9);
        this.algorithmType = Record.checkU8("algorithmType", i10);
        if (i9 != 0) {
            if (i9 != 1) {
                if (i9 != 2) {
                    if (i9 != 3) {
                        throw new IllegalArgumentException("\"gatewayType\" must be between 0 and 3");
                    }
                    if (!(obj instanceof Name)) {
                        throw new IllegalArgumentException("\"gateway\" must be a DNS name");
                    }
                    name2 = Record.checkName(VpnProfileDataSource.KEY_GATEWAY, (Name) obj);
                } else if (!(obj instanceof Inet6Address)) {
                    throw new IllegalArgumentException("\"gateway\" must be an IPv6 address");
                }
            } else if (!(obj instanceof InetAddress)) {
                throw new IllegalArgumentException("\"gateway\" must be an IPv4 address");
            }
            this.gateway = obj;
            this.key = bArr;
        }
        name2 = null;
        this.gateway = name2;
        this.key = bArr;
    }

    public int getAlgorithmType() {
        return this.algorithmType;
    }

    public Object getGateway() {
        return this.gateway;
    }

    public int getGatewayType() {
        return this.gatewayType;
    }

    public byte[] getKey() {
        return this.key;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new IPSECKEYRecord();
    }

    public int getPrecedence() {
        return this.precedence;
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        Name name2;
        this.precedence = tokenizer.getUInt8();
        this.gatewayType = tokenizer.getUInt8();
        this.algorithmType = tokenizer.getUInt8();
        int i7 = this.gatewayType;
        if (i7 != 0) {
            int i8 = 1;
            if (i7 != 1) {
                i8 = 2;
                if (i7 != 2) {
                    if (i7 != 3) {
                        throw new WireParseException("invalid gateway type");
                    }
                    name2 = tokenizer.getName(name);
                }
            }
            name2 = tokenizer.getAddress(i8);
        } else {
            if (!tokenizer.getString().equals(".")) {
                throw new TextParseException("invalid gateway format");
            }
            name2 = null;
        }
        this.gateway = name2;
        this.key = tokenizer.getBase64(false);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        Object obj;
        this.precedence = dNSInput.readU8();
        this.gatewayType = dNSInput.readU8();
        this.algorithmType = dNSInput.readU8();
        int i7 = this.gatewayType;
        if (i7 == 0) {
            obj = null;
        } else if (i7 == 1) {
            obj = InetAddress.getByAddress(dNSInput.readByteArray(4));
        } else if (i7 == 2) {
            obj = InetAddress.getByAddress(dNSInput.readByteArray(16));
        } else {
            if (i7 != 3) {
                throw new WireParseException("invalid gateway type");
            }
            obj = new Name(dNSInput);
        }
        this.gateway = obj;
        if (dNSInput.remaining() > 0) {
            this.key = dNSInput.readByteArray();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x007d  */
    @Override // org.xbill.DNS.Record
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String rrToString() {
        /*
            r3 = this;
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r1 = r0
            r1.<init>()
            r6 = r0
            r0 = r6
            r1 = r3
            int r1 = r1.precedence
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r6
            java.lang.String r1 = " "
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r6
            r1 = r3
            int r1 = r1.gatewayType
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r6
            java.lang.String r1 = " "
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r6
            r1 = r3
            int r1 = r1.algorithmType
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r6
            java.lang.String r1 = " "
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r3
            int r0 = r0.gatewayType
            r4 = r0
            r0 = r4
            if (r0 == 0) goto L6d
            r0 = r4
            r1 = 1
            if (r0 == r1) goto L5f
            r0 = r4
            r1 = 2
            if (r0 == r1) goto L5f
            r0 = r4
            r1 = 3
            if (r0 == r1) goto L53
            goto L76
        L53:
            r0 = r6
            r1 = r3
            java.lang.Object r1 = r1.gateway
            java.lang.StringBuffer r0 = r0.append(r1)
            goto L76
        L5f:
            r0 = r3
            java.lang.Object r0 = r0.gateway
            java.net.InetAddress r0 = (java.net.InetAddress) r0
            java.lang.String r0 = r0.getHostAddress()
            r5 = r0
            goto L70
        L6d:
            java.lang.String r0 = "."
            r5 = r0
        L70:
            r0 = r6
            r1 = r5
            java.lang.StringBuffer r0 = r0.append(r1)
        L76:
            r0 = r3
            byte[] r0 = r0.key
            if (r0 == 0) goto L90
            r0 = r6
            java.lang.String r1 = " "
            java.lang.StringBuffer r0 = r0.append(r1)
            r0 = r6
            r1 = r3
            byte[] r1 = r1.key
            java.lang.String r1 = org.xbill.DNS.utils.base64.toString(r1)
            java.lang.StringBuffer r0 = r0.append(r1)
        L90:
            r0 = r6
            java.lang.String r0 = r0.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.IPSECKEYRecord.rrToString():java.lang.String");
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU8(this.precedence);
        dNSOutput.writeU8(this.gatewayType);
        dNSOutput.writeU8(this.algorithmType);
        int i7 = this.gatewayType;
        if (i7 == 1 || i7 == 2) {
            dNSOutput.writeByteArray(((InetAddress) this.gateway).getAddress());
        } else if (i7 == 3) {
            ((Name) this.gateway).toWire(dNSOutput, null, z7);
        }
        byte[] bArr = this.key;
        if (bArr != null) {
            dNSOutput.writeByteArray(bArr);
        }
    }
}
