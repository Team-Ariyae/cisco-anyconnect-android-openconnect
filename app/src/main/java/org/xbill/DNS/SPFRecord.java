package org.xbill.DNS;

import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SPFRecord.class */
public class SPFRecord extends TXTBase {
    private static final long serialVersionUID = -2100754352801658722L;

    public SPFRecord() {
    }

    public SPFRecord(Name name, int i7, long j7, String str) {
        super(name, 99, i7, j7, str);
    }

    public SPFRecord(Name name, int i7, long j7, List list) {
        super(name, 99, i7, j7, list);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new SPFRecord();
    }
}
