package org.xbill.DNS;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/AAAARecord.class */
public class AAAARecord extends Record {
    private static final long serialVersionUID = -4588601512069748050L;
    private byte[] address;

    public AAAARecord() {
    }

    public AAAARecord(Name name, int i7, long j7, InetAddress inetAddress) {
        super(name, 28, i7, j7);
        if (Address.familyOf(inetAddress) != 2) {
            throw new IllegalArgumentException("invalid IPv6 address");
        }
        this.address = inetAddress.getAddress();
    }

    public InetAddress getAddress() {
        try {
            Name name = this.name;
            return name == null ? InetAddress.getByAddress(this.address) : InetAddress.getByAddress(name.toString(), this.address);
        } catch (UnknownHostException e8) {
            return null;
        }
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new AAAARecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.address = tokenizer.getAddressBytes(2);
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.address = dNSInput.readByteArray(16);
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        InetAddress byAddress;
        String str = null;
        try {
            byAddress = InetAddress.getByAddress(null, this.address);
        } catch (UnknownHostException e8) {
        }
        if (byAddress.getAddress().length != 4) {
            str = byAddress.getHostAddress();
            return str;
        }
        StringBuffer stringBuffer = new StringBuffer("0:0:0:0:0:ffff:");
        byte[] bArr = this.address;
        byte b8 = bArr[12];
        byte b9 = bArr[13];
        byte b10 = bArr[14];
        byte b11 = bArr[15];
        stringBuffer.append(Integer.toHexString(((b8 & 255) << 8) + (b9 & 255)));
        stringBuffer.append(':');
        stringBuffer.append(Integer.toHexString(((b10 & 255) << 8) + (b11 & 255)));
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        dNSOutput.writeByteArray(this.address);
    }
}
