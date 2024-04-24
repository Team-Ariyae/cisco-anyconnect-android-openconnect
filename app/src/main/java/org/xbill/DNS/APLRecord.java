package org.xbill.DNS;

import androidx.activity.result.a;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xbill.DNS.Tokenizer;
import org.xbill.DNS.utils.base16;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/APLRecord.class */
public class APLRecord extends Record {
    private static final long serialVersionUID = -1348173791712935864L;
    private List elements;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/APLRecord$Element.class */
    public static class Element {
        public final Object address;
        public final int family;
        public final boolean negative;
        public final int prefixLength;

        private Element(int i7, boolean z7, Object obj, int i8) {
            this.family = i7;
            this.negative = z7;
            this.address = obj;
            this.prefixLength = i8;
            if (!APLRecord.validatePrefixLength(i7, i8)) {
                throw new IllegalArgumentException("invalid prefix length");
            }
        }

        public Element(boolean z7, InetAddress inetAddress, int i7) {
            this(Address.familyOf(inetAddress), z7, inetAddress, i7);
        }

        public boolean equals(Object obj) {
            boolean z7 = false;
            if (obj != null) {
                if (obj instanceof Element) {
                    Element element = (Element) obj;
                    z7 = false;
                    if (this.family == element.family) {
                        z7 = false;
                        if (this.negative == element.negative) {
                            z7 = false;
                            if (this.prefixLength == element.prefixLength) {
                                z7 = false;
                                if (this.address.equals(element.address)) {
                                    z7 = true;
                                }
                            }
                        }
                    }
                } else {
                    z7 = false;
                }
            }
            return z7;
        }

        public int hashCode() {
            return this.address.hashCode() + this.prefixLength + (this.negative ? 1 : 0);
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            if (this.negative) {
                stringBuffer.append("!");
            }
            stringBuffer.append(this.family);
            stringBuffer.append(":");
            int i7 = this.family;
            stringBuffer.append((i7 == 1 || i7 == 2) ? ((InetAddress) this.address).getHostAddress() : base16.toString((byte[]) this.address));
            stringBuffer.append("/");
            stringBuffer.append(this.prefixLength);
            return stringBuffer.toString();
        }
    }

    public APLRecord() {
    }

    public APLRecord(Name name, int i7, long j7, List list) {
        super(name, 42, i7, j7);
        this.elements = new ArrayList(list.size());
        for (Object obj : list) {
            if (!(obj instanceof Element)) {
                throw new IllegalArgumentException("illegal element");
            }
            Element element = (Element) obj;
            int i8 = element.family;
            if (i8 != 1 && i8 != 2) {
                throw new IllegalArgumentException("unknown family");
            }
            this.elements.add(element);
        }
    }

    private static int addressLength(byte[] bArr) {
        for (int length = bArr.length - 1; length >= 0; length--) {
            if (bArr[length] != 0) {
                return length + 1;
            }
        }
        return 0;
    }

    private static byte[] parseAddress(byte[] bArr, int i7) {
        if (bArr.length > i7) {
            throw new WireParseException("invalid address length");
        }
        if (bArr.length == i7) {
            return bArr;
        }
        byte[] bArr2 = new byte[i7];
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        return bArr2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean validatePrefixLength(int i7, int i8) {
        if (i8 < 0 || i8 >= 256) {
            return false;
        }
        if (i7 != 1 || i8 <= 32) {
            return i7 != 2 || i8 <= 128;
        }
        return false;
    }

    public List getElements() {
        return this.elements;
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new APLRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        this.elements = new ArrayList(1);
        while (true) {
            Tokenizer.Token token = tokenizer.get();
            if (!token.isString()) {
                tokenizer.unget();
                return;
            }
            String str = token.value;
            boolean startsWith = str.startsWith("!");
            int indexOf = str.indexOf(58, startsWith ? 1 : 0);
            if (indexOf < 0) {
                throw tokenizer.exception("invalid address prefix element");
            }
            int indexOf2 = str.indexOf(47, indexOf);
            if (indexOf2 < 0) {
                throw tokenizer.exception("invalid address prefix element");
            }
            String substring = str.substring(startsWith ? 1 : 0, indexOf);
            String substring2 = str.substring(indexOf + 1, indexOf2);
            String substring3 = str.substring(indexOf2 + 1);
            try {
                int parseInt = Integer.parseInt(substring);
                if (parseInt != 1 && parseInt != 2) {
                    throw tokenizer.exception("unknown family");
                }
                try {
                    int parseInt2 = Integer.parseInt(substring3);
                    if (!validatePrefixLength(parseInt, parseInt2)) {
                        throw tokenizer.exception("invalid prefix length");
                    }
                    byte[] byteArray = Address.toByteArray(substring2, parseInt);
                    if (byteArray == null) {
                        throw a.u("invalid IP address ", substring2, tokenizer);
                    }
                    this.elements.add(new Element(startsWith, InetAddress.getByAddress(byteArray), parseInt2));
                } catch (NumberFormatException e8) {
                    throw tokenizer.exception("invalid prefix length");
                }
            } catch (NumberFormatException e9) {
                throw tokenizer.exception("invalid family");
            }
        }
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        this.elements = new ArrayList(1);
        while (dNSInput.remaining() != 0) {
            int readU16 = dNSInput.readU16();
            int readU8 = dNSInput.readU8();
            int readU82 = dNSInput.readU8();
            boolean z7 = (readU82 & 128) != 0;
            byte[] readByteArray = dNSInput.readByteArray(readU82 & (-129));
            if (!validatePrefixLength(readU16, readU8)) {
                throw new WireParseException("invalid prefix length");
            }
            this.elements.add((readU16 == 1 || readU16 == 2) ? new Element(z7, InetAddress.getByAddress(parseAddress(readByteArray, Address.addressLength(readU16))), readU8) : new Element(readU16, z7, readByteArray, readU8));
        }
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = this.elements.iterator();
        while (it.hasNext()) {
            stringBuffer.append((Element) it.next());
            if (it.hasNext()) {
                stringBuffer.append(" ");
            }
        }
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        byte[] address;
        int addressLength;
        for (Element element : this.elements) {
            int i7 = element.family;
            if (i7 == 1 || i7 == 2) {
                address = ((InetAddress) element.address).getAddress();
                addressLength = addressLength(address);
            } else {
                address = (byte[]) element.address;
                addressLength = address.length;
            }
            int i8 = element.negative ? addressLength | 128 : addressLength;
            dNSOutput.writeU16(element.family);
            dNSOutput.writeU8(element.prefixLength);
            dNSOutput.writeU8(i8);
            dNSOutput.writeByteArray(address, 0, addressLength);
        }
    }
}
