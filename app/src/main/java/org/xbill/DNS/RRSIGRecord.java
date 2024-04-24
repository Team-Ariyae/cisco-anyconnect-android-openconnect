package org.xbill.DNS;

import java.util.Date;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/RRSIGRecord.class */
public class RRSIGRecord extends SIGBase {
    private static final long serialVersionUID = -2609150673537226317L;

    public RRSIGRecord() {
    }

    public RRSIGRecord(Name name, int i7, long j7, int i8, int i9, long j8, Date date, Date date2, int i10, Name name2, byte[] bArr) {
        super(name, 46, i7, j7, i8, i9, j8, date, date2, i10, name2, bArr);
    }

    @Override // org.xbill.DNS.Record
    public Record getObject() {
        return new RRSIGRecord();
    }
}
