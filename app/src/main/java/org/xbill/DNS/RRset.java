package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/RRset.class */
public class RRset implements Serializable {
    private static final long serialVersionUID = -3270249290171239695L;
    private short nsigs;
    private short position;
    private List rrs;

    public RRset() {
        this.rrs = new ArrayList(1);
        this.nsigs = (short) 0;
        this.position = (short) 0;
    }

    public RRset(RRset rRset) {
        synchronized (rRset) {
            this.rrs = (List) ((ArrayList) rRset.rrs).clone();
            this.nsigs = rRset.nsigs;
            this.position = rRset.position;
        }
    }

    public RRset(Record record) {
        this();
        safeAddRR(record);
    }

    private Iterator iterator(boolean z7, boolean z8) {
        int i7;
        List subList;
        synchronized (this) {
            int size = this.rrs.size();
            int i8 = z7 ? size - this.nsigs : this.nsigs;
            if (i8 == 0) {
                return Collections.EMPTY_LIST.iterator();
            }
            if (!z7) {
                i7 = size - this.nsigs;
            } else if (z8) {
                if (this.position >= i8) {
                    this.position = (short) 0;
                }
                i7 = this.position;
                this.position = (short) (i7 + 1);
            } else {
                i7 = 0;
            }
            ArrayList arrayList = new ArrayList(i8);
            if (z7) {
                arrayList.addAll(this.rrs.subList(i7, i8));
                if (i7 != 0) {
                    subList = this.rrs.subList(0, i7);
                }
                return arrayList.iterator();
            }
            subList = this.rrs.subList(i7, size);
            arrayList.addAll(subList);
            return arrayList.iterator();
        }
    }

    private String iteratorToString(Iterator it) {
        StringBuffer stringBuffer = new StringBuffer();
        while (it.hasNext()) {
            Record record = (Record) it.next();
            stringBuffer.append("[");
            stringBuffer.append(record.rdataToString());
            stringBuffer.append("]");
            if (it.hasNext()) {
                stringBuffer.append(" ");
            }
        }
        return stringBuffer.toString();
    }

    private void safeAddRR(Record record) {
        if (record instanceof RRSIGRecord) {
            this.rrs.add(record);
            this.nsigs = (short) (this.nsigs + 1);
        } else if (this.nsigs == 0) {
            this.rrs.add(record);
        } else {
            List list = this.rrs;
            list.add(list.size() - this.nsigs, record);
        }
    }

    public void addRR(Record record) {
        synchronized (this) {
            if (this.rrs.size() == 0) {
                safeAddRR(record);
                return;
            }
            Record first = first();
            if (!record.sameRRset(first)) {
                throw new IllegalArgumentException("record does not match rrset");
            }
            Record record2 = record;
            if (record.getTTL() != first.getTTL()) {
                if (record.getTTL() <= first.getTTL()) {
                    int i7 = 0;
                    while (true) {
                        record2 = record;
                        if (i7 >= this.rrs.size()) {
                            break;
                        }
                        Record cloneRecord = ((Record) this.rrs.get(i7)).cloneRecord();
                        cloneRecord.setTTL(record.getTTL());
                        this.rrs.set(i7, cloneRecord);
                        i7++;
                    }
                } else {
                    record2 = record.cloneRecord();
                    record2.setTTL(first.getTTL());
                }
            }
            if (!this.rrs.contains(record2)) {
                safeAddRR(record2);
            }
        }
    }

    public void clear() {
        synchronized (this) {
            this.rrs.clear();
            this.position = (short) 0;
            this.nsigs = (short) 0;
        }
    }

    public void deleteRR(Record record) {
        synchronized (this) {
            if (this.rrs.remove(record) && (record instanceof RRSIGRecord)) {
                this.nsigs = (short) (this.nsigs - 1);
            }
        }
    }

    public Record first() {
        Record record;
        synchronized (this) {
            if (this.rrs.size() == 0) {
                throw new IllegalStateException("rrset is empty");
            }
            record = (Record) this.rrs.get(0);
        }
        return record;
    }

    public int getDClass() {
        return first().getDClass();
    }

    public Name getName() {
        return first().getName();
    }

    public long getTTL() {
        long ttl;
        synchronized (this) {
            ttl = first().getTTL();
        }
        return ttl;
    }

    public int getType() {
        return first().getRRsetType();
    }

    public Iterator rrs() {
        Iterator it;
        synchronized (this) {
            it = iterator(true, true);
        }
        return it;
    }

    public Iterator rrs(boolean z7) {
        Iterator it;
        synchronized (this) {
            it = iterator(true, z7);
        }
        return it;
    }

    public Iterator sigs() {
        Iterator it;
        synchronized (this) {
            it = iterator(false, false);
        }
        return it;
    }

    public int size() {
        int size;
        short s7;
        synchronized (this) {
            size = this.rrs.size();
            s7 = this.nsigs;
        }
        return size - s7;
    }

    public String toString() {
        if (this.rrs.size() == 0) {
            return "{empty}";
        }
        StringBuffer p = a.p("{ ");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getName());
        stringBuffer.append(" ");
        p.append(stringBuffer.toString());
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(getTTL());
        stringBuffer2.append(" ");
        p.append(stringBuffer2.toString());
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append(DClass.string(getDClass()));
        stringBuffer3.append(" ");
        p.append(stringBuffer3.toString());
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append(Type.string(getType()));
        stringBuffer4.append(" ");
        p.append(stringBuffer4.toString());
        p.append(iteratorToString(iterator(true, false)));
        if (this.nsigs > 0) {
            p.append(" sigs: ");
            p.append(iteratorToString(iterator(false, false)));
        }
        p.append(" }");
        return p.toString();
    }
}
