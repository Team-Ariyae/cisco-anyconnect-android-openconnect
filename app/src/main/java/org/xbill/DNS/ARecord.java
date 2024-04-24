package org.xbill.DNS;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ARecord.class */
public class ARecord extends Record {
    private static final long serialVersionUID = -2172609200849142323L;
    private int addr;

    public ARecord() {
    }

    public ARecord(Name name, int i7, long j7, InetAddress inetAddress) {
        super(name, 1, i7, j7);
        if (Address.familyOf(inetAddress) != 1) {
            throw new IllegalArgumentException("invalid IPv4 address");
        }
        this.addr = fromArray(inetAddress.getAddress());
    }

    private static final int fromArray(byte[] bArr) {
        return (bArr[3] & 255) | ((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << 8);
    }

    private static final byte[] toArray(int i7) {
        return new byte[]{(byte) ((i7 >>> 24) & 255), (byte) ((i7 >>> 16) & 255), (byte) ((i7 >>> 8) & 255), (byte) (i7 & 255)};
    }

    public InetAddress getAddress() {
        try {
            Name name = this.name;
            return name == null ? InetAddress.getByAddress(toArray(this.addr)) : InetAddress.getByAddress(name.toString(), toArray(this.addr));
        } catch (UnknownHostException e8) {
            return null;
        }
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new ARecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.addr = fromArray(tokenizer.getAddressBytes(1));
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.addr = fromArray(dNSInput.readByteArray(4));
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        return Address.toDottedQuad(toArray(this.addr));
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeU32(this.addr & 4294967295L);
    }
}
