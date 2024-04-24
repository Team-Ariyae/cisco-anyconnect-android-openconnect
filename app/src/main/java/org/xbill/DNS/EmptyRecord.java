package org.xbill.DNS;

import io.github.inflationx.calligraphy3.BuildConfig;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/EmptyRecord.class */
public class EmptyRecord extends Record {
    private static final long serialVersionUID = 3601852050646429582L;

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new EmptyRecord();
    }

    @Override // org.xbill.DNS.Record
    public void rdataFromString(Tokenizer tokenizer, Name name) {
    }

    @Override // org.xbill.DNS.Record
    public void rrFromWire(DNSInput dNSInput) {
    }

    @Override // org.xbill.DNS.Record
    public String rrToString() {
        return BuildConfig.FLAVOR;
    }

    @Override // org.xbill.DNS.Record
    public void rrToWire(DNSOutput dNSOutput, Compression compression, boolean z7) {
    }
}
