package org.xbill.DNS;

import java.util.Date;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SIGRecord.class */
public class SIGRecord extends SIGBase {
    private static final long serialVersionUID = 4963556060953589058L;

    public SIGRecord() {
    }

    public SIGRecord(Name name, int i7, long j7, int i8, int i9, long j8, Date date, Date date2, int i10, Name name2, byte[] bArr) {
        super(name, 24, i7, j7, i8, i9, j8, date, date2, i10, name2, bArr);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new SIGRecord();
    }
}
