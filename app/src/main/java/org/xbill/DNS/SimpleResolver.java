package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/SimpleResolver.class */
public class SimpleResolver implements Resolver {
    public static final int DEFAULT_EDNS_PAYLOADSIZE = 1280;
    public static final int DEFAULT_PORT = 53;
    private static final short DEFAULT_UDPSIZE = 512;
    private static String defaultResolver = "localhost";
    private static int uniqueID;
    private InetSocketAddress address;
    private boolean ignoreTruncation;
    private InetSocketAddress localAddress;
    private OPTRecord queryOPT;
    private long timeoutValue;
    private TSIG tsig;
    private boolean useTCP;

    public SimpleResolver() {
        this(null);
    }

    public SimpleResolver(String str) {
        this.timeoutValue = 10000L;
        String str2 = str;
        if (str == null) {
            String server = ResolverConfig.getCurrentConfig().server();
            str2 = server;
            if (server == null) {
                str2 = defaultResolver;
            }
        }
        this.address = new InetSocketAddress(str2.equals("0") ? InetAddress.getLocalHost() : InetAddress.getByName(str2), 53);
    }

    private void applyEDNS(Message message) {
        if (this.queryOPT == null || message.getOPT() != null) {
            return;
        }
        message.addRecord(this.queryOPT, 3);
    }

    private int maxUDPSize(Message message) {
        OPTRecord opt = message.getOPT();
        if (opt == null) {
            return 512;
        }
        return opt.getPayloadSize();
    }

    private Message parseMessage(byte[] bArr) {
        try {
            return new Message(bArr);
        } catch (IOException e8) {
            if (Options.check("verbose")) {
                e8.printStackTrace();
            }
            IOException iOException = e8;
            if (!(e8 instanceof WireParseException)) {
                iOException = new WireParseException("Error parsing message");
            }
            throw ((WireParseException) iOException);
        }
    }

    private Message sendAXFR(Message message) {
        ZoneTransferIn newAXFR = ZoneTransferIn.newAXFR(message.getQuestion().getName(), this.address, this.tsig);
        newAXFR.setTimeout((int) (getTimeout() / 1000));
        newAXFR.setLocalAddress(this.localAddress);
        try {
            newAXFR.run();
            List axfr = newAXFR.getAXFR();
            Message message2 = new Message(message.getHeader().getID());
            message2.getHeader().setFlag(5);
            message2.getHeader().setFlag(0);
            message2.addRecord(message.getQuestion(), 0);
            Iterator it = axfr.iterator();
            while (it.hasNext()) {
                message2.addRecord((Record) it.next(), 1);
            }
            return message2;
        } catch (ZoneTransferException e8) {
            throw new WireParseException(e8.getMessage());
        }
    }

    public static void setDefaultResolver(String str) {
        defaultResolver = str;
    }

    private void verifyTSIG(Message message, Message message2, byte[] bArr, TSIG tsig) {
        if (tsig == null) {
            return;
        }
        int verify = tsig.verify(message2, bArr, message.getTSIG());
        if (Options.check("verbose")) {
            PrintStream printStream = System.err;
            StringBuffer p = a.p("TSIG verify: ");
            p.append(Rcode.TSIGstring(verify));
            printStream.println(p.toString());
        }
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public TSIG getTSIGKey() {
        return this.tsig;
    }

    public long getTimeout() {
        return this.timeoutValue;
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x018f, code lost:
    
        return r0;
     */
    @Override // org.xbill.DNS.Resolver
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.xbill.DNS.Message send(org.xbill.DNS.Message r8) {
        /*
            Method dump skipped, instructions count: 411
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.SimpleResolver.send(org.xbill.DNS.Message):org.xbill.DNS.Message");
    }

    @Override // org.xbill.DNS.Resolver
    public Object sendAsync(Message message, ResolverListener resolverListener) {
        Integer num;
        synchronized (this) {
            int i7 = uniqueID;
            uniqueID = i7 + 1;
            num = new Integer(i7);
        }
        Record question = message.getQuestion();
        String name = question != null ? question.getName().toString() : "(none)";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getClass());
        stringBuffer.append(": ");
        stringBuffer.append(name);
        String stringBuffer2 = stringBuffer.toString();
        ResolveThread resolveThread = new ResolveThread(this, message, num, resolverListener);
        resolveThread.setName(stringBuffer2);
        resolveThread.setDaemon(true);
        resolveThread.start();
        return num;
    }

    public void setAddress(InetAddress inetAddress) {
        this.address = new InetSocketAddress(inetAddress, this.address.getPort());
    }

    public void setAddress(InetSocketAddress inetSocketAddress) {
        this.address = inetSocketAddress;
    }

    @Override // org.xbill.DNS.Resolver
    public void setEDNS(int i7) {
        setEDNS(i7, 0, 0, null);
    }

    @Override // org.xbill.DNS.Resolver
    public void setEDNS(int i7, int i8, int i9, List list) {
        if (i7 != 0 && i7 != -1) {
            throw new IllegalArgumentException("invalid EDNS level - must be 0 or -1");
        }
        if (i8 == 0) {
            i8 = 1280;
        }
        this.queryOPT = new OPTRecord(i8, 0, i7, i9, list);
    }

    @Override // org.xbill.DNS.Resolver
    public void setIgnoreTruncation(boolean z7) {
        this.ignoreTruncation = z7;
    }

    public void setLocalAddress(InetAddress inetAddress) {
        this.localAddress = new InetSocketAddress(inetAddress, 0);
    }

    public void setLocalAddress(InetSocketAddress inetSocketAddress) {
        this.localAddress = inetSocketAddress;
    }

    @Override // org.xbill.DNS.Resolver
    public void setPort(int i7) {
        this.address = new InetSocketAddress(this.address.getAddress(), i7);
    }

    @Override // org.xbill.DNS.Resolver
    public void setTCP(boolean z7) {
        this.useTCP = z7;
    }

    @Override // org.xbill.DNS.Resolver
    public void setTSIGKey(TSIG tsig) {
        this.tsig = tsig;
    }

    @Override // org.xbill.DNS.Resolver
    public void setTimeout(int i7) {
        setTimeout(i7, 0);
    }

    @Override // org.xbill.DNS.Resolver
    public void setTimeout(int i7, int i8) {
        this.timeoutValue = (i7 * 1000) + i8;
    }
}
