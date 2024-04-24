package org.xbill.DNS;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ClientSubnetOption.class */
public class ClientSubnetOption extends EDNSOption {
    private static final long serialVersionUID = -3868158449890266347L;
    private InetAddress address;
    private int family;
    private int scopeNetmask;
    private int sourceNetmask;

    public ClientSubnetOption() {
        super(8);
    }

    public ClientSubnetOption(int i7, int i8, InetAddress inetAddress) {
        super(8);
        int familyOf = Address.familyOf(inetAddress);
        this.family = familyOf;
        this.sourceNetmask = checkMaskLength("source netmask", familyOf, i7);
        this.scopeNetmask = checkMaskLength("scope netmask", this.family, i8);
        InetAddress truncate = Address.truncate(inetAddress, i7);
        this.address = truncate;
        if (!inetAddress.equals(truncate)) {
            throw new IllegalArgumentException("source netmask is not valid for address");
        }
    }

    public ClientSubnetOption(int i7, InetAddress inetAddress) {
        this(i7, 0, inetAddress);
    }

    private static int checkMaskLength(String str, int i7, int i8) {
        int addressLength = Address.addressLength(i7) * 8;
        if (i8 >= 0 && i8 <= addressLength) {
            return i8;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\"");
        stringBuffer.append(str);
        stringBuffer.append("\" ");
        stringBuffer.append(i8);
        stringBuffer.append(" must be in the range ");
        stringBuffer.append("[0..");
        stringBuffer.append(addressLength);
        stringBuffer.append("]");
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getFamily() {
        return this.family;
    }

    public int getScopeNetmask() {
        return this.scopeNetmask;
    }

    public int getSourceNetmask() {
        return this.sourceNetmask;
    }

    @Override // org.xbill.DNS.EDNSOption
    public void optionFromWire(DNSInput dNSInput) {
        int readU16 = dNSInput.readU16();
        this.family = readU16;
        if (readU16 != 1 && readU16 != 2) {
            throw new WireParseException("unknown address family");
        }
        int readU8 = dNSInput.readU8();
        this.sourceNetmask = readU8;
        if (readU8 > Address.addressLength(this.family) * 8) {
            throw new WireParseException("invalid source netmask");
        }
        int readU82 = dNSInput.readU8();
        this.scopeNetmask = readU82;
        if (readU82 > Address.addressLength(this.family) * 8) {
            throw new WireParseException("invalid scope netmask");
        }
        byte[] readByteArray = dNSInput.readByteArray();
        if (readByteArray.length != (this.sourceNetmask + 7) / 8) {
            throw new WireParseException("invalid address");
        }
        byte[] bArr = new byte[Address.addressLength(this.family)];
        System.arraycopy(readByteArray, 0, bArr, 0, readByteArray.length);
        try {
            InetAddress byAddress = InetAddress.getByAddress(bArr);
            this.address = byAddress;
            if (!Address.truncate(byAddress, this.sourceNetmask).equals(this.address)) {
                throw new WireParseException("invalid padding");
            }
        } catch (UnknownHostException e8) {
            throw new WireParseException("invalid address", e8);
        }
    }

    @Override // org.xbill.DNS.EDNSOption
    public String optionToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.address.getHostAddress());
        stringBuffer.append("/");
        stringBuffer.append(this.sourceNetmask);
        stringBuffer.append(", scope netmask ");
        stringBuffer.append(this.scopeNetmask);
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.EDNSOption
    public void optionToWire(DNSOutput dNSOutput) {
        dNSOutput.writeU16(this.family);
        dNSOutput.writeU8(this.sourceNetmask);
        dNSOutput.writeU8(this.scopeNetmask);
        dNSOutput.writeByteArray(this.address.getAddress(), 0, (this.sourceNetmask + 7) / 8);
    }
}
