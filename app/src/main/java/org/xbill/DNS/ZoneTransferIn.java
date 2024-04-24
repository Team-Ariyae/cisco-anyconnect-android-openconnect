package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.xbill.DNS.TSIG;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ZoneTransferIn.class */
public class ZoneTransferIn {
    private static final int AXFR = 6;
    private static final int END = 7;
    private static final int FIRSTDATA = 1;
    private static final int INITIALSOA = 0;
    private static final int IXFR_ADD = 5;
    private static final int IXFR_ADDSOA = 4;
    private static final int IXFR_DEL = 3;
    private static final int IXFR_DELSOA = 2;
    private SocketAddress address;
    private TCPClient client;
    private long current_serial;
    private int dclass;
    private long end_serial;
    private ZoneTransferHandler handler;
    private Record initialsoa;
    private long ixfr_serial;
    private SocketAddress localAddress;
    private int qtype;
    private int rtype;
    private int state;
    private long timeout = 900000;
    private TSIG tsig;
    private TSIG.StreamVerifier verifier;
    private boolean want_fallback;
    private Name zname;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ZoneTransferIn$BasicHandler.class */
    public static class BasicHandler implements ZoneTransferHandler {
        private List axfr;
        private List ixfr;

        private BasicHandler() {
        }

        @Override // org.xbill.DNS.ZoneTransferIn.ZoneTransferHandler
        public void handleRecord(Record record) {
            List list;
            List list2 = this.ixfr;
            if (list2 != null) {
                Delta delta = (Delta) list2.get(list2.size() - 1);
                list = delta.adds.size() > 0 ? delta.adds : delta.deletes;
            } else {
                list = this.axfr;
            }
            list.add(record);
        }

        @Override // org.xbill.DNS.ZoneTransferIn.ZoneTransferHandler
        public void startAXFR() {
            this.axfr = new ArrayList();
        }

        @Override // org.xbill.DNS.ZoneTransferIn.ZoneTransferHandler
        public void startIXFR() {
            this.ixfr = new ArrayList();
        }

        @Override // org.xbill.DNS.ZoneTransferIn.ZoneTransferHandler
        public void startIXFRAdds(Record record) {
            List list = this.ixfr;
            Delta delta = (Delta) list.get(list.size() - 1);
            delta.adds.add(record);
            delta.end = ZoneTransferIn.getSOASerial(record);
        }

        @Override // org.xbill.DNS.ZoneTransferIn.ZoneTransferHandler
        public void startIXFRDeletes(Record record) {
            Delta delta = new Delta();
            delta.deletes.add(record);
            delta.start = ZoneTransferIn.getSOASerial(record);
            this.ixfr.add(delta);
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ZoneTransferIn$Delta.class */
    public static class Delta {
        public List adds;
        public List deletes;
        public long end;
        public long start;

        private Delta() {
            this.adds = new ArrayList();
            this.deletes = new ArrayList();
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ZoneTransferIn$ZoneTransferHandler.class */
    public interface ZoneTransferHandler {
        void handleRecord(Record record);

        void startAXFR();

        void startIXFR();

        void startIXFRAdds(Record record);

        void startIXFRDeletes(Record record);
    }

    private ZoneTransferIn() {
    }

    private ZoneTransferIn(Name name, int i7, long j7, boolean z7, SocketAddress socketAddress, TSIG tsig) {
        this.address = socketAddress;
        this.tsig = tsig;
        if (name.isAbsolute()) {
            this.zname = name;
        } else {
            try {
                this.zname = Name.concatenate(name, Name.root);
            } catch (NameTooLongException e8) {
                throw new IllegalArgumentException("ZoneTransferIn: name too long");
            }
        }
        this.qtype = i7;
        this.dclass = 1;
        this.ixfr_serial = j7;
        this.want_fallback = z7;
        this.state = 0;
    }

    private void closeConnection() {
        try {
            TCPClient tCPClient = this.client;
            if (tCPClient != null) {
                tCPClient.cleanup();
            }
        } catch (IOException e8) {
        }
    }

    private void doxfr() {
        sendQuery();
        while (this.state != 7) {
            byte[] recv = this.client.recv();
            Message parseMessage = parseMessage(recv);
            if (parseMessage.getHeader().getRcode() == 0 && this.verifier != null) {
                parseMessage.getTSIG();
                if (this.verifier.verify(parseMessage, recv) != 0) {
                    fail("TSIG failure");
                }
            }
            Record[] sectionArray = parseMessage.getSectionArray(1);
            if (this.state == 0) {
                int rcode = parseMessage.getRcode();
                if (rcode != 0) {
                    if (this.qtype == 251 && rcode == 4) {
                        fallback();
                        doxfr();
                        return;
                    }
                    fail(Rcode.string(rcode));
                }
                Record question = parseMessage.getQuestion();
                if (question != null && question.getType() != this.qtype) {
                    fail("invalid question section");
                }
                if (sectionArray.length == 0 && this.qtype == 251) {
                    fallback();
                    doxfr();
                    return;
                }
            }
            for (Record record : sectionArray) {
                parseRR(record);
            }
            if (this.state == 7 && this.verifier != null && !parseMessage.isVerified()) {
                fail("last message must be signed");
            }
        }
    }

    private void fail(String str) {
        throw new ZoneTransferException(str);
    }

    private void fallback() {
        if (!this.want_fallback) {
            fail("server doesn't support IXFR");
        }
        logxfr("falling back to AXFR");
        this.qtype = 252;
        this.state = 0;
    }

    private BasicHandler getBasicHandler() {
        ZoneTransferHandler zoneTransferHandler = this.handler;
        if (zoneTransferHandler instanceof BasicHandler) {
            return (BasicHandler) zoneTransferHandler;
        }
        throw new IllegalArgumentException("ZoneTransferIn used callback interface");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getSOASerial(Record record) {
        return ((SOARecord) record).getSerial();
    }

    private void logxfr(String str) {
        if (Options.check("verbose")) {
            PrintStream printStream = System.out;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.zname);
            stringBuffer.append(": ");
            stringBuffer.append(str);
            printStream.println(stringBuffer.toString());
        }
    }

    public static ZoneTransferIn newAXFR(Name name, String str, int i7, TSIG tsig) {
        int i8 = i7;
        if (i7 == 0) {
            i8 = 53;
        }
        return newAXFR(name, new InetSocketAddress(str, i8), tsig);
    }

    public static ZoneTransferIn newAXFR(Name name, String str, TSIG tsig) {
        return newAXFR(name, str, 0, tsig);
    }

    public static ZoneTransferIn newAXFR(Name name, SocketAddress socketAddress, TSIG tsig) {
        return new ZoneTransferIn(name, 252, 0L, false, socketAddress, tsig);
    }

    public static ZoneTransferIn newIXFR(Name name, long j7, boolean z7, String str, int i7, TSIG tsig) {
        int i8 = i7;
        if (i7 == 0) {
            i8 = 53;
        }
        return newIXFR(name, j7, z7, new InetSocketAddress(str, i8), tsig);
    }

    public static ZoneTransferIn newIXFR(Name name, long j7, boolean z7, String str, TSIG tsig) {
        return newIXFR(name, j7, z7, str, 0, tsig);
    }

    public static ZoneTransferIn newIXFR(Name name, long j7, boolean z7, SocketAddress socketAddress, TSIG tsig) {
        return new ZoneTransferIn(name, Type.IXFR, j7, z7, socketAddress, tsig);
    }

    private void openConnection() {
        TCPClient tCPClient = new TCPClient(System.currentTimeMillis() + this.timeout);
        this.client = tCPClient;
        SocketAddress socketAddress = this.localAddress;
        if (socketAddress != null) {
            tCPClient.bind(socketAddress);
        }
        this.client.connect(this.address);
    }

    private Message parseMessage(byte[] bArr) {
        try {
            return new Message(bArr);
        } catch (IOException e8) {
            if (e8 instanceof WireParseException) {
                throw ((WireParseException) e8);
            }
            throw new WireParseException("Error parsing message");
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0009. Please report as an issue. */
    private void parseRR(Record record) {
        int i7;
        String str;
        int type = record.getType();
        switch (this.state) {
            case 0:
                if (type != 6) {
                    fail("missing initial SOA");
                }
                this.initialsoa = record;
                long sOASerial = getSOASerial(record);
                this.end_serial = sOASerial;
                if (this.qtype != 251 || Serial.compare(sOASerial, this.ixfr_serial) > 0) {
                    this.state = 1;
                    return;
                } else {
                    logxfr("up to date");
                    this.state = 7;
                    return;
                }
            case 1:
                if (this.qtype == 251 && type == 6 && getSOASerial(record) == this.ixfr_serial) {
                    this.rtype = Type.IXFR;
                    this.handler.startIXFR();
                    logxfr("got incremental response");
                    this.state = 2;
                } else {
                    this.rtype = 252;
                    this.handler.startAXFR();
                    this.handler.handleRecord(this.initialsoa);
                    logxfr("got nonincremental response");
                    this.state = 6;
                }
                parseRR(record);
                return;
            case 2:
                this.handler.startIXFRDeletes(record);
                i7 = 3;
                this.state = i7;
                return;
            case 3:
                if (type == 6) {
                    this.current_serial = getSOASerial(record);
                    this.state = 4;
                    parseRR(record);
                    return;
                }
                this.handler.handleRecord(record);
                return;
            case 4:
                this.handler.startIXFRAdds(record);
                i7 = 5;
                this.state = i7;
                return;
            case 5:
                if (type == 6) {
                    long sOASerial2 = getSOASerial(record);
                    if (sOASerial2 != this.end_serial) {
                        if (sOASerial2 == this.current_serial) {
                            this.state = 2;
                            parseRR(record);
                            return;
                        } else {
                            StringBuffer p = a.p("IXFR out of sync: expected serial ");
                            p.append(this.current_serial);
                            p.append(" , got ");
                            p.append(sOASerial2);
                            fail(p.toString());
                        }
                    }
                    this.state = 7;
                    return;
                }
                this.handler.handleRecord(record);
                return;
            case 6:
                if (type != 1 || record.getDClass() == this.dclass) {
                    this.handler.handleRecord(record);
                    if (type != 6) {
                        return;
                    }
                    this.state = 7;
                    return;
                }
                return;
            case 7:
                str = "extra data";
                fail(str);
                return;
            default:
                str = "invalid state";
                fail(str);
                return;
        }
    }

    private void sendQuery() {
        Record newRecord = Record.newRecord(this.zname, this.qtype, this.dclass);
        Message message = new Message();
        message.getHeader().setOpcode(0);
        message.addRecord(newRecord, 0);
        if (this.qtype == 251) {
            Name name = this.zname;
            int i7 = this.dclass;
            Name name2 = Name.root;
            message.addRecord(new SOARecord(name, i7, 0L, name2, name2, this.ixfr_serial, 0L, 0L, 0L, 0L), 2);
        }
        TSIG tsig = this.tsig;
        if (tsig != null) {
            tsig.apply(message, null);
            this.verifier = new TSIG.StreamVerifier(this.tsig, message.getTSIG());
        }
        this.client.send(message.toWire(Message.MAXLENGTH));
    }

    public List getAXFR() {
        return getBasicHandler().axfr;
    }

    public List getIXFR() {
        return getBasicHandler().ixfr;
    }

    public Name getName() {
        return this.zname;
    }

    public int getType() {
        return this.qtype;
    }

    public boolean isAXFR() {
        return this.rtype == 252;
    }

    public boolean isCurrent() {
        BasicHandler basicHandler = getBasicHandler();
        return basicHandler.axfr == null && basicHandler.ixfr == null;
    }

    public boolean isIXFR() {
        return this.rtype == 251;
    }

    public List run() {
        BasicHandler basicHandler = new BasicHandler();
        run(basicHandler);
        return basicHandler.axfr != null ? basicHandler.axfr : basicHandler.ixfr;
    }

    public void run(ZoneTransferHandler zoneTransferHandler) {
        this.handler = zoneTransferHandler;
        try {
            openConnection();
            doxfr();
        } finally {
            closeConnection();
        }
    }

    public void setDClass(int i7) {
        DClass.check(i7);
        this.dclass = i7;
    }

    public void setLocalAddress(SocketAddress socketAddress) {
        this.localAddress = socketAddress;
    }

    public void setTimeout(int i7) {
        if (i7 < 0) {
            throw new IllegalArgumentException("timeout cannot be negative");
        }
        this.timeout = i7 * 1000;
    }
}
