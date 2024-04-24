package org.xbill.DNS;

import androidx.activity.result.a;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Message.class */
public class Message implements Cloneable {
    public static final int MAXLENGTH = 65535;
    public static final int TSIG_FAILED = 4;
    public static final int TSIG_INTERMEDIATE = 2;
    public static final int TSIG_SIGNED = 3;
    public static final int TSIG_UNSIGNED = 0;
    public static final int TSIG_VERIFIED = 1;
    private Header header;
    private TSIGRecord querytsig;
    private List[] sections;
    public int sig0start;
    private int size;
    public int tsigState;
    private int tsigerror;
    private TSIG tsigkey;
    public int tsigstart;
    private static Record[] emptyRecordArray = new Record[0];
    private static RRset[] emptyRRsetArray = new RRset[0];

    public Message() {
        this(new Header());
    }

    public Message(int i7) {
        this(new Header(i7));
    }

    public Message(DNSInput dNSInput) {
        this(new Header(dNSInput));
        boolean z7 = this.header.getOpcode() == 5;
        boolean flag = this.header.getFlag(6);
        for (int i7 = 0; i7 < 4; i7++) {
            try {
                int count = this.header.getCount(i7);
                if (count > 0) {
                    this.sections[i7] = new ArrayList(count);
                }
                for (int i8 = 0; i8 < count; i8++) {
                    int current = dNSInput.current();
                    Record fromWire = Record.fromWire(dNSInput, i7, z7);
                    this.sections[i7].add(fromWire);
                    if (i7 == 3) {
                        if (fromWire.getType() == 250) {
                            this.tsigstart = current;
                        }
                        if (fromWire.getType() == 24 && ((SIGRecord) fromWire).getTypeCovered() == 0) {
                            this.sig0start = current;
                        }
                    }
                }
            } catch (WireParseException e8) {
                if (!flag) {
                    throw e8;
                }
            }
        }
        this.size = dNSInput.current();
    }

    private Message(Header header) {
        this.sections = new List[4];
        this.header = header;
    }

    public Message(byte[] bArr) {
        this(new DNSInput(bArr));
    }

    public static Message newQuery(Record record) {
        Message message = new Message();
        message.header.setOpcode(0);
        message.header.setFlag(7);
        message.addRecord(record, 0);
        return message;
    }

    public static Message newUpdate(Name name) {
        return new Update(name);
    }

    private static boolean sameSet(Record record, Record record2) {
        return record.getRRsetType() == record2.getRRsetType() && record.getDClass() == record2.getDClass() && record.getName().equals(record2.getName());
    }

    private int sectionToWire(DNSOutput dNSOutput, int i7, Compression compression, int i8) {
        int size = this.sections[i7].size();
        int current = dNSOutput.current();
        Record record = null;
        int i9 = 0;
        int i10 = 0;
        for (int i11 = 0; i11 < size; i11++) {
            Record record2 = (Record) this.sections[i7].get(i11);
            if (i7 == 3 && (record2 instanceof OPTRecord)) {
                i9++;
            } else {
                int i12 = current;
                int i13 = i10;
                if (record != null) {
                    i12 = current;
                    i13 = i10;
                    if (!sameSet(record2, record)) {
                        i12 = dNSOutput.current();
                        i13 = i11;
                    }
                }
                record2.toWire(dNSOutput, i7, compression);
                if (dNSOutput.current() > i8) {
                    dNSOutput.jump(i12);
                    return (size - i13) + i9;
                }
                record = record2;
                i10 = i13;
                current = i12;
            }
        }
        return i9;
    }

    private boolean toWire(DNSOutput dNSOutput, int i7) {
        int i8;
        if (i7 < 12) {
            return false;
        }
        TSIG tsig = this.tsigkey;
        int i9 = i7;
        if (tsig != null) {
            i9 = i7 - tsig.recordLength();
        }
        OPTRecord opt = getOPT();
        byte[] bArr = null;
        int i10 = i9;
        if (opt != null) {
            bArr = opt.toWire(3);
            i10 = i9 - bArr.length;
        }
        int current = dNSOutput.current();
        this.header.toWire(dNSOutput);
        Compression compression = new Compression();
        int flagsByte = this.header.getFlagsByte();
        int i11 = 0;
        int i12 = 0;
        while (true) {
            i8 = flagsByte;
            if (i11 >= 4) {
                break;
            }
            if (this.sections[i11] != null) {
                int sectionToWire = sectionToWire(dNSOutput, i11, compression, i10);
                if (sectionToWire != 0 && i11 != 3) {
                    int flag = Header.setFlag(flagsByte, 6, true);
                    int i13 = current + 4;
                    dNSOutput.writeU16At(this.header.getCount(i11) - sectionToWire, (i11 * 2) + i13);
                    while (true) {
                        i11++;
                        i8 = flag;
                        if (i11 >= 3) {
                            break;
                        }
                        dNSOutput.writeU16At(0, (i11 * 2) + i13);
                    }
                } else if (i11 == 3) {
                    i12 = this.header.getCount(i11) - sectionToWire;
                }
            }
            i11++;
        }
        int i14 = i12;
        if (bArr != null) {
            dNSOutput.writeByteArray(bArr);
            i14 = i12 + 1;
        }
        if (i8 != this.header.getFlagsByte()) {
            dNSOutput.writeU16At(i8, current + 2);
        }
        if (i14 != this.header.getCount(3)) {
            dNSOutput.writeU16At(i14, current + 10);
        }
        TSIG tsig2 = this.tsigkey;
        if (tsig2 == null) {
            return true;
        }
        tsig2.generate(this, dNSOutput.toByteArray(), this.tsigerror, this.querytsig).toWire(dNSOutput, 3, compression);
        dNSOutput.writeU16At(i14 + 1, current + 10);
        return true;
    }

    public void addRecord(Record record, int i7) {
        List[] listArr = this.sections;
        if (listArr[i7] == null) {
            listArr[i7] = new LinkedList();
        }
        this.header.incCount(i7);
        this.sections[i7].add(record);
    }

    public Object clone() {
        Message message = new Message();
        int i7 = 0;
        while (true) {
            List[] listArr = this.sections;
            if (i7 >= listArr.length) {
                message.header = (Header) this.header.clone();
                message.size = this.size;
                return message;
            }
            if (listArr[i7] != null) {
                message.sections[i7] = new LinkedList(this.sections[i7]);
            }
            i7++;
        }
    }

    public boolean findRRset(Name name, int i7) {
        boolean z7 = true;
        if (!findRRset(name, i7, 1)) {
            z7 = true;
            if (!findRRset(name, i7, 2)) {
                z7 = findRRset(name, i7, 3);
            }
        }
        return z7;
    }

    public boolean findRRset(Name name, int i7, int i8) {
        if (this.sections[i8] == null) {
            return false;
        }
        for (int i9 = 0; i9 < this.sections[i8].size(); i9++) {
            Record record = (Record) this.sections[i8].get(i9);
            if (record.getType() == i7 && name.equals(record.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean findRecord(Record record) {
        for (int i7 = 1; i7 <= 3; i7++) {
            List list = this.sections[i7];
            if (list != null && list.contains(record)) {
                return true;
            }
        }
        return false;
    }

    public boolean findRecord(Record record, int i7) {
        List list = this.sections[i7];
        return list != null && list.contains(record);
    }

    public Header getHeader() {
        return this.header;
    }

    public OPTRecord getOPT() {
        for (Record record : getSectionArray(3)) {
            if (record instanceof OPTRecord) {
                return (OPTRecord) record;
            }
        }
        return null;
    }

    public Record getQuestion() {
        List list = this.sections[0];
        if (list == null || list.size() == 0) {
            return null;
        }
        return (Record) list.get(0);
    }

    public int getRcode() {
        int rcode = this.header.getRcode();
        OPTRecord opt = getOPT();
        int i7 = rcode;
        if (opt != null) {
            i7 = rcode + (opt.getExtendedRcode() << 4);
        }
        return i7;
    }

    public Record[] getSectionArray(int i7) {
        List list = this.sections[i7];
        return list == null ? emptyRecordArray : (Record[]) list.toArray(new Record[list.size()]);
    }

    public RRset[] getSectionRRsets(int i7) {
        if (this.sections[i7] == null) {
            return emptyRRsetArray;
        }
        LinkedList linkedList = new LinkedList();
        Record[] sectionArray = getSectionArray(i7);
        HashSet hashSet = new HashSet();
        for (int i8 = 0; i8 < sectionArray.length; i8++) {
            Name name = sectionArray[i8].getName();
            boolean z7 = true;
            if (hashSet.contains(name)) {
                int size = linkedList.size() - 1;
                while (true) {
                    z7 = true;
                    if (size < 0) {
                        break;
                    }
                    RRset rRset = (RRset) linkedList.get(size);
                    if (rRset.getType() == sectionArray[i8].getRRsetType() && rRset.getDClass() == sectionArray[i8].getDClass() && rRset.getName().equals(name)) {
                        rRset.addRR(sectionArray[i8]);
                        z7 = false;
                        break;
                    }
                    size--;
                }
            }
            if (z7) {
                linkedList.add(new RRset(sectionArray[i8]));
                hashSet.add(name);
            }
        }
        return (RRset[]) linkedList.toArray(new RRset[linkedList.size()]);
    }

    public TSIGRecord getTSIG() {
        int count = this.header.getCount(3);
        if (count == 0) {
            return null;
        }
        Record record = (Record) this.sections[3].get(count - 1);
        if (record.type != 250) {
            return null;
        }
        return (TSIGRecord) record;
    }

    public boolean isSigned() {
        int i7 = this.tsigState;
        boolean z7 = true;
        if (i7 != 3) {
            z7 = true;
            if (i7 != 1) {
                z7 = i7 == 4;
            }
        }
        return z7;
    }

    public boolean isVerified() {
        boolean z7 = true;
        if (this.tsigState != 1) {
            z7 = false;
        }
        return z7;
    }

    public int numBytes() {
        return this.size;
    }

    public void removeAllRecords(int i7) {
        this.sections[i7] = null;
        this.header.setCount(i7, 0);
    }

    public boolean removeRecord(Record record, int i7) {
        List list = this.sections[i7];
        if (list == null || !list.remove(record)) {
            return false;
        }
        this.header.decCount(i7);
        return true;
    }

    public String sectionToString(int i7) {
        if (i7 > 3) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Record record : getSectionArray(i7)) {
            if (i7 == 0) {
                StringBuffer p = a.p(";;\t");
                p.append(record.name);
                stringBuffer.append(p.toString());
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append(", type = ");
                stringBuffer2.append(Type.string(record.type));
                stringBuffer.append(stringBuffer2.toString());
                StringBuffer stringBuffer3 = new StringBuffer();
                stringBuffer3.append(", class = ");
                stringBuffer3.append(DClass.string(record.dclass));
                stringBuffer.append(stringBuffer3.toString());
            } else {
                stringBuffer.append(record);
            }
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public void setTSIG(TSIG tsig, int i7, TSIGRecord tSIGRecord) {
        this.tsigkey = tsig;
        this.tsigerror = i7;
        this.querytsig = tSIGRecord;
    }

    public String toString() {
        StringBuffer stringBuffer;
        StringBuffer p;
        String updString;
        StringBuffer stringBuffer2 = new StringBuffer();
        if (getOPT() != null) {
            stringBuffer = new StringBuffer();
            stringBuffer.append(this.header.toStringWithRcode(getRcode()));
        } else {
            stringBuffer = new StringBuffer();
            stringBuffer.append(this.header);
        }
        stringBuffer.append("\n");
        stringBuffer2.append(stringBuffer.toString());
        if (isSigned()) {
            stringBuffer2.append(";; TSIG ");
            stringBuffer2.append(isVerified() ? "ok" : "invalid");
            stringBuffer2.append('\n');
        }
        for (int i7 = 0; i7 < 4; i7++) {
            if (this.header.getOpcode() != 5) {
                p = a.p(";; ");
                updString = Section.longString(i7);
            } else {
                p = a.p(";; ");
                updString = Section.updString(i7);
            }
            p.append(updString);
            p.append(":\n");
            stringBuffer2.append(p.toString());
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append(sectionToString(i7));
            stringBuffer3.append("\n");
            stringBuffer2.append(stringBuffer3.toString());
        }
        StringBuffer p7 = a.p(";; Message size: ");
        p7.append(numBytes());
        p7.append(" bytes");
        stringBuffer2.append(p7.toString());
        return stringBuffer2.toString();
    }

    public void toWire(DNSOutput dNSOutput) {
        this.header.toWire(dNSOutput);
        Compression compression = new Compression();
        for (int i7 = 0; i7 < 4; i7++) {
            if (this.sections[i7] != null) {
                for (int i8 = 0; i8 < this.sections[i7].size(); i8++) {
                    ((Record) this.sections[i7].get(i8)).toWire(dNSOutput, i7, compression);
                }
            }
        }
    }

    public byte[] toWire() {
        DNSOutput dNSOutput = new DNSOutput();
        toWire(dNSOutput);
        this.size = dNSOutput.current();
        return dNSOutput.toByteArray();
    }

    public byte[] toWire(int i7) {
        DNSOutput dNSOutput = new DNSOutput();
        toWire(dNSOutput, i7);
        this.size = dNSOutput.current();
        return dNSOutput.toByteArray();
    }
}
