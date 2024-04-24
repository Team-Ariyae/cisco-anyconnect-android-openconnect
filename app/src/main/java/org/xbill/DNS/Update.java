package org.xbill.DNS;

import java.util.Iterator;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Update.class */
public class Update extends Message {
    private int dclass;
    private Name origin;

    public Update(Name name) {
        this(name, 1);
    }

    public Update(Name name, int i7) {
        if (!name.isAbsolute()) {
            throw new RelativeNameException(name);
        }
        DClass.check(i7);
        getHeader().setOpcode(5);
        addRecord(Record.newRecord(name, 6, 1), 0);
        this.origin = name;
        this.dclass = i7;
    }

    private void newPrereq(Record record) {
        addRecord(record, 1);
    }

    private void newUpdate(Record record) {
        addRecord(record, 2);
    }

    public void absent(Name name) {
        newPrereq(Record.newRecord(name, 255, 254, 0L));
    }

    public void absent(Name name, int i7) {
        newPrereq(Record.newRecord(name, i7, 254, 0L));
    }

    public void add(Name name, int i7, long j7, String str) {
        newUpdate(Record.fromString(name, i7, this.dclass, j7, str, this.origin));
    }

    public void add(Name name, int i7, long j7, Tokenizer tokenizer) {
        newUpdate(Record.fromString(name, i7, this.dclass, j7, tokenizer, this.origin));
    }

    public void add(RRset rRset) {
        Iterator rrs = rRset.rrs();
        while (rrs.hasNext()) {
            add((Record) rrs.next());
        }
    }

    public void add(Record record) {
        newUpdate(record);
    }

    public void add(Record[] recordArr) {
        for (Record record : recordArr) {
            add(record);
        }
    }

    public void delete(Name name) {
        newUpdate(Record.newRecord(name, 255, 255, 0L));
    }

    public void delete(Name name, int i7) {
        newUpdate(Record.newRecord(name, i7, 255, 0L));
    }

    public void delete(Name name, int i7, String str) {
        newUpdate(Record.fromString(name, i7, 254, 0L, str, this.origin));
    }

    public void delete(Name name, int i7, Tokenizer tokenizer) {
        newUpdate(Record.fromString(name, i7, 254, 0L, tokenizer, this.origin));
    }

    public void delete(RRset rRset) {
        Iterator rrs = rRset.rrs();
        while (rrs.hasNext()) {
            delete((Record) rrs.next());
        }
    }

    public void delete(Record record) {
        newUpdate(record.withDClass(254, 0L));
    }

    public void delete(Record[] recordArr) {
        for (Record record : recordArr) {
            delete(record);
        }
    }

    public void present(Name name) {
        newPrereq(Record.newRecord(name, 255, 255, 0L));
    }

    public void present(Name name, int i7) {
        newPrereq(Record.newRecord(name, i7, 255, 0L));
    }

    public void present(Name name, int i7, String str) {
        newPrereq(Record.fromString(name, i7, this.dclass, 0L, str, this.origin));
    }

    public void present(Name name, int i7, Tokenizer tokenizer) {
        newPrereq(Record.fromString(name, i7, this.dclass, 0L, tokenizer, this.origin));
    }

    public void present(Record record) {
        newPrereq(record);
    }

    public void replace(Name name, int i7, long j7, String str) {
        delete(name, i7);
        add(name, i7, j7, str);
    }

    public void replace(Name name, int i7, long j7, Tokenizer tokenizer) {
        delete(name, i7);
        add(name, i7, j7, tokenizer);
    }

    public void replace(RRset rRset) {
        delete(rRset.getName(), rRset.getType());
        Iterator rrs = rRset.rrs();
        while (rrs.hasNext()) {
            add((Record) rrs.next());
        }
    }

    public void replace(Record record) {
        delete(record.getName(), record.getType());
        add(record);
    }

    public void replace(Record[] recordArr) {
        for (Record record : recordArr) {
            replace(record);
        }
    }
}
