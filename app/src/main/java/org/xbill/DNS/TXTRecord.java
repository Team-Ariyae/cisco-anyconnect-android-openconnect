package org.xbill.DNS;

import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/TXTRecord.class */
public class TXTRecord extends TXTBase {
    private static final long serialVersionUID = -5780785764284221342L;

    public TXTRecord() {
    }

    public TXTRecord(Name name, int i7, long j7, String str) {
        super(name, 16, i7, j7, str);
    }

    public TXTRecord(Name name, int i7, long j7, List list) {
        super(name, 16, i7, j7, list);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new TXTRecord();
    }
}
