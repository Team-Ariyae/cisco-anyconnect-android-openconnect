package org.xbill.DNS;

import androidx.activity.result.a;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Lookup.class */
public final class Lookup {
    public static final int HOST_NOT_FOUND = 3;
    public static final int SUCCESSFUL = 0;
    public static final int TRY_AGAIN = 2;
    public static final int TYPE_NOT_FOUND = 4;
    public static final int UNRECOVERABLE = 1;
    public static Class class$org$xbill$DNS$Lookup;
    private static Map defaultCaches;
    private static int defaultNdots;
    private static Resolver defaultResolver;
    private static Name[] defaultSearchPath;
    private static final Name[] noAliases = new Name[0];
    private List aliases;
    private Record[] answers;
    private boolean badresponse;
    private String badresponse_error;
    private Cache cache;
    private int credibility;
    private int dclass;
    private boolean done;
    private boolean doneCurrent;
    private String error;
    private boolean foundAlias;
    private int iterations;
    private Name name;
    private boolean nametoolong;
    private boolean networkerror;
    private boolean nxdomain;
    private boolean referral;
    private Resolver resolver;
    private int result;
    private Name[] searchPath;
    private boolean temporary_cache;
    private boolean timedout;
    private int type;
    private boolean verbose;

    static {
        refreshDefault();
    }

    public Lookup(String str) {
        this(Name.fromString(str), 1, 1);
    }

    public Lookup(String str, int i7) {
        this(Name.fromString(str), i7, 1);
    }

    public Lookup(String str, int i7, int i8) {
        this(Name.fromString(str), i7, i8);
    }

    public Lookup(Name name) {
        this(name, 1, 1);
    }

    public Lookup(Name name, int i7) {
        this(name, i7, 1);
    }

    public Lookup(Name name, int i7, int i8) {
        Type.check(i7);
        DClass.check(i8);
        if (!Type.isRR(i7) && i7 != 255) {
            throw new IllegalArgumentException("Cannot query for meta-types other than ANY");
        }
        this.name = name;
        this.type = i7;
        this.dclass = i8;
        Class cls = class$org$xbill$DNS$Lookup;
        Class cls2 = cls;
        if (cls == null) {
            cls2 = class$("org.xbill.DNS.Lookup");
            class$org$xbill$DNS$Lookup = cls2;
        }
        synchronized (cls2) {
            try {
                this.resolver = getDefaultResolver();
                this.searchPath = getDefaultSearchPath();
                this.cache = getDefaultCache(i8);
            } catch (Throwable th) {
                Class cls3 = cls2;
                throw th;
            }
        }
        this.credibility = 3;
        this.verbose = Options.check("verbose");
        this.result = -1;
    }

    private void checkDone() {
        if (!this.done || this.result == -1) {
            StringBuffer p = a.p("Lookup of ");
            p.append(this.name);
            p.append(" ");
            StringBuffer stringBuffer = new StringBuffer(p.toString());
            if (this.dclass != 1) {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append(DClass.string(this.dclass));
                stringBuffer2.append(" ");
                stringBuffer.append(stringBuffer2.toString());
            }
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append(Type.string(this.type));
            stringBuffer3.append(" isn't done");
            stringBuffer.append(stringBuffer3.toString());
            throw new IllegalStateException(stringBuffer.toString());
        }
    }

    public static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e8) {
            throw new NoClassDefFoundError().initCause(e8);
        }
    }

    private void follow(Name name, Name name2) {
        this.foundAlias = true;
        this.badresponse = false;
        this.networkerror = false;
        this.timedout = false;
        this.nxdomain = false;
        this.referral = false;
        int i7 = this.iterations + 1;
        this.iterations = i7;
        if (i7 >= 6 || name.equals(name2)) {
            this.result = 1;
            this.error = "CNAME loop";
            this.done = true;
        } else {
            if (this.aliases == null) {
                this.aliases = new ArrayList();
            }
            this.aliases.add(name2);
            lookup(name);
        }
    }

    public static Cache getDefaultCache(int i7) {
        Cache cache;
        synchronized (Lookup.class) {
            try {
                DClass.check(i7);
                Cache cache2 = (Cache) defaultCaches.get(Mnemonic.toInteger(i7));
                cache = cache2;
                if (cache2 == null) {
                    cache = new Cache(i7);
                    defaultCaches.put(Mnemonic.toInteger(i7), cache);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return cache;
    }

    public static Resolver getDefaultResolver() {
        Resolver resolver;
        synchronized (Lookup.class) {
            try {
                resolver = defaultResolver;
            } catch (Throwable th) {
                throw th;
            }
        }
        return resolver;
    }

    public static Name[] getDefaultSearchPath() {
        Name[] nameArr;
        synchronized (Lookup.class) {
            try {
                nameArr = defaultSearchPath;
            } catch (Throwable th) {
                throw th;
            }
        }
        return nameArr;
    }

    private void lookup(Name name) {
        String str;
        SetResponse lookupRecords = this.cache.lookupRecords(name, this.type, this.credibility);
        if (this.verbose) {
            PrintStream printStream = System.err;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("lookup ");
            stringBuffer.append(name);
            stringBuffer.append(" ");
            stringBuffer.append(Type.string(this.type));
            printStream.println(stringBuffer.toString());
            System.err.println(lookupRecords);
        }
        processResponse(name, lookupRecords);
        if (this.done || this.doneCurrent) {
            return;
        }
        Message newQuery = Message.newQuery(Record.newRecord(name, this.type, this.dclass));
        try {
            Message send = this.resolver.send(newQuery);
            int rcode = send.getHeader().getRcode();
            if (rcode != 0 && rcode != 3) {
                this.badresponse = true;
                str = Rcode.string(rcode);
            } else {
                if (newQuery.getQuestion().equals(send.getQuestion())) {
                    SetResponse addMessage = this.cache.addMessage(send);
                    SetResponse setResponse = addMessage;
                    if (addMessage == null) {
                        setResponse = this.cache.lookupRecords(name, this.type, this.credibility);
                    }
                    if (this.verbose) {
                        PrintStream printStream2 = System.err;
                        StringBuffer stringBuffer2 = new StringBuffer();
                        stringBuffer2.append("queried ");
                        stringBuffer2.append(name);
                        stringBuffer2.append(" ");
                        stringBuffer2.append(Type.string(this.type));
                        printStream2.println(stringBuffer2.toString());
                        System.err.println(setResponse);
                    }
                    processResponse(name, setResponse);
                    return;
                }
                this.badresponse = true;
                str = "response does not match query";
            }
            this.badresponse_error = str;
        } catch (IOException e8) {
            if (e8 instanceof InterruptedIOException) {
                this.timedout = true;
            } else {
                this.networkerror = true;
            }
        }
    }

    private void processResponse(Name name, SetResponse setResponse) {
        if (setResponse.isSuccessful()) {
            RRset[] answers = setResponse.answers();
            ArrayList arrayList = new ArrayList();
            for (RRset rRset : answers) {
                Iterator rrs = rRset.rrs();
                while (rrs.hasNext()) {
                    arrayList.add(rrs.next());
                }
            }
            this.result = 0;
            this.answers = (Record[]) arrayList.toArray(new Record[arrayList.size()]);
        } else if (setResponse.isNXDOMAIN()) {
            this.nxdomain = true;
            this.doneCurrent = true;
            if (this.iterations <= 0) {
                return;
            } else {
                this.result = 3;
            }
        } else if (setResponse.isNXRRSET()) {
            this.result = 4;
            this.answers = null;
        } else {
            if (setResponse.isCNAME()) {
                follow(setResponse.getCNAME().getTarget(), name);
                return;
            }
            if (!setResponse.isDNAME()) {
                if (setResponse.isDelegation()) {
                    this.referral = true;
                    return;
                }
                return;
            } else {
                try {
                    follow(name.fromDNAME(setResponse.getDNAME()), name);
                    return;
                } catch (NameTooLongException e8) {
                    this.result = 1;
                    this.error = "Invalid DNAME target";
                }
            }
        }
        this.done = true;
    }

    public static void refreshDefault() {
        synchronized (Lookup.class) {
            try {
                try {
                    defaultResolver = new ExtendedResolver();
                    defaultSearchPath = ResolverConfig.getCurrentConfig().searchPath();
                    defaultCaches = new HashMap();
                    defaultNdots = ResolverConfig.getCurrentConfig().ndots();
                } catch (UnknownHostException e8) {
                    throw new RuntimeException("Failed to initialize resolver");
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private final void reset() {
        this.iterations = 0;
        this.foundAlias = false;
        this.done = false;
        this.doneCurrent = false;
        this.aliases = null;
        this.answers = null;
        this.result = -1;
        this.error = null;
        this.nxdomain = false;
        this.badresponse = false;
        this.badresponse_error = null;
        this.networkerror = false;
        this.timedout = false;
        this.nametoolong = false;
        this.referral = false;
        if (this.temporary_cache) {
            this.cache.clearCache();
        }
    }

    private void resolve(Name name, Name name2) {
        this.doneCurrent = false;
        if (name2 != null) {
            try {
                name = Name.concatenate(name, name2);
            } catch (NameTooLongException e8) {
                this.nametoolong = true;
                return;
            }
        }
        lookup(name);
    }

    public static void setDefaultCache(Cache cache, int i7) {
        synchronized (Lookup.class) {
            try {
                DClass.check(i7);
                defaultCaches.put(Mnemonic.toInteger(i7), cache);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void setDefaultResolver(Resolver resolver) {
        synchronized (Lookup.class) {
            try {
                defaultResolver = resolver;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void setDefaultSearchPath(String[] strArr) {
        synchronized (Lookup.class) {
            try {
                if (strArr == null) {
                    defaultSearchPath = null;
                    return;
                }
                Name[] nameArr = new Name[strArr.length];
                for (int i7 = 0; i7 < strArr.length; i7++) {
                    nameArr[i7] = Name.fromString(strArr[i7], Name.root);
                }
                defaultSearchPath = nameArr;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void setDefaultSearchPath(Name[] nameArr) {
        synchronized (Lookup.class) {
            try {
                defaultSearchPath = nameArr;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void setPacketLogger(PacketLogger packetLogger) {
        synchronized (Lookup.class) {
            try {
                Client.setPacketLogger(packetLogger);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public Name[] getAliases() {
        checkDone();
        List list = this.aliases;
        return list == null ? noAliases : (Name[]) list.toArray(new Name[list.size()]);
    }

    public Record[] getAnswers() {
        checkDone();
        return this.answers;
    }

    public String getErrorString() {
        checkDone();
        String str = this.error;
        if (str != null) {
            return str;
        }
        int i7 = this.result;
        if (i7 == 0) {
            return "successful";
        }
        if (i7 == 1) {
            return "unrecoverable error";
        }
        if (i7 == 2) {
            return "try again";
        }
        if (i7 == 3) {
            return "host not found";
        }
        if (i7 == 4) {
            return "type not found";
        }
        throw new IllegalStateException("unknown result");
    }

    public int getResult() {
        checkDone();
        return this.result;
    }

    public Record[] run() {
        Name name;
        Name name2;
        String str;
        if (this.done) {
            reset();
        }
        if (!this.name.isAbsolute()) {
            if (this.searchPath != null) {
                if (this.name.labels() > defaultNdots) {
                    resolve(this.name, Name.root);
                }
                if (!this.done) {
                    int i7 = 0;
                    while (true) {
                        Name[] nameArr = this.searchPath;
                        if (i7 >= nameArr.length) {
                            break;
                        }
                        resolve(this.name, nameArr[i7]);
                        if (this.done) {
                            return this.answers;
                        }
                        if (this.foundAlias) {
                            break;
                        }
                        i7++;
                    }
                } else {
                    return this.answers;
                }
            } else {
                name = this.name;
                name2 = Name.root;
            }
        } else {
            name = this.name;
            name2 = null;
        }
        resolve(name, name2);
        if (!this.done) {
            if (this.badresponse) {
                this.result = 2;
                str = this.badresponse_error;
            } else if (this.timedout) {
                this.result = 2;
                str = "timed out";
            } else if (this.networkerror) {
                this.result = 2;
                str = "network error";
            } else if (this.nxdomain) {
                this.result = 3;
                this.done = true;
            } else if (this.referral) {
                this.result = 1;
                str = "referral";
            } else if (this.nametoolong) {
                this.result = 1;
                str = "name too long";
            }
            this.error = str;
            this.done = true;
        }
        return this.answers;
    }

    public void setCache(Cache cache) {
        boolean z7;
        if (cache == null) {
            this.cache = new Cache(this.dclass);
            z7 = true;
        } else {
            this.cache = cache;
            z7 = false;
        }
        this.temporary_cache = z7;
    }

    public void setCredibility(int i7) {
        this.credibility = i7;
    }

    public void setNdots(int i7) {
        if (i7 >= 0) {
            defaultNdots = i7;
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Illegal ndots value: ");
        stringBuffer.append(i7);
        throw new IllegalArgumentException(stringBuffer.toString());
    }

    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    public void setSearchPath(String[] strArr) {
        if (strArr == null) {
            this.searchPath = null;
            return;
        }
        Name[] nameArr = new Name[strArr.length];
        for (int i7 = 0; i7 < strArr.length; i7++) {
            nameArr[i7] = Name.fromString(strArr[i7], Name.root);
        }
        this.searchPath = nameArr;
    }

    public void setSearchPath(Name[] nameArr) {
        this.searchPath = nameArr;
    }
}
