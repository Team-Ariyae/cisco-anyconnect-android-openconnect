package org.xbill.DNS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.strongswan.android.data.VpnProfileDataSource;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/OPTRecord.class */
public class OPTRecord extends Record {
    private static final long serialVersionUID = -6254521894809367938L;
    private List options;

    public OPTRecord() {
    }

    public OPTRecord(int i7, int i8, int i9) {
        this(i7, i8, i9, 0, null);
    }

    public OPTRecord(int i7, int i8, int i9, int i10) {
        this(i7, i8, i9, i10, null);
    }

    public OPTRecord(int i7, int i8, int i9, int i10, List list) {
        super(Name.root, 41, i7, 0L);
        Record.checkU16("payloadSize", i7);
        Record.checkU8("xrcode", i8);
        Record.checkU8("version", i9);
        Record.checkU16(VpnProfileDataSource.KEY_FLAGS, i10);
        this.ttl = (i8 << 24) + (i9 << 16) + i10;
        if (list != null) {
            this.options = new ArrayList(list);
        }
    }

    @Override // org.xbill.DNS.Record
    public boolean equals(Object obj) {
        return super.equals(obj) && this.ttl == ((OPTRecord) obj).ttl;
    }

    public int getExtendedRcode() {
        return (int) (this.ttl >>> 24);
    }

    public int getFlags() {
        return (int) (this.ttl & 65535);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new OPTRecord();
    }

    public List getOptions() {
        List list = this.options;
        return list == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(list);
    }

    public List getOptions(int i7) {
        List<EDNSOption> list = this.options;
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        List list2 = Collections.EMPTY_LIST;
        for (EDNSOption eDNSOption : list) {
            if (eDNSOption.getCode() == i7) {
                List list3 = list2;
                if (list2 == Collections.EMPTY_LIST) {
                    list3 = new ArrayList();
                }
                list3.add(eDNSOption);
                list2 = list3;
            }
        }
        return list2;
    }

    public int getPayloadSize() {
        return this.dclass;
    }

    public int getVersion() {
        return (int) ((this.ttl >>> 16) & 255);
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
        throw tokenizer.exception("no text format defined for OPT");
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
        if (dNSInput.remaining() > 0) {
            this.options = new ArrayList();
        }
        while (dNSInput.remaining() > 0) {
            this.options.add(EDNSOption.fromWire(dNSInput));
        }
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        StringBuffer stringBuffer = new StringBuffer();
        List list = this.options;
        if (list != null) {
            stringBuffer.append(list);
            stringBuffer.append(" ");
        }
        stringBuffer.append(" ; payload ");
        stringBuffer.append(getPayloadSize());
        stringBuffer.append(", xrcode ");
        stringBuffer.append(getExtendedRcode());
        stringBuffer.append(", version ");
        stringBuffer.append(getVersion());
        stringBuffer.append(", flags ");
        stringBuffer.append(getFlags());
        return stringBuffer.toString();
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
        List list = this.options;
        if (list == null) {
            return;
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ((EDNSOption) it.next()).toWire(dNSOutput);
        }
    }
}
