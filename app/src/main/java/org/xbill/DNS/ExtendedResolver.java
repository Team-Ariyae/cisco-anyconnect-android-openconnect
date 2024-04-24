package org.xbill.DNS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ExtendedResolver.class */
public class ExtendedResolver implements Resolver {
    private static final int quantum = 5;
    private List resolvers;
    private boolean loadBalance = false;
    private int lbStart = 0;
    private int retries = 3;

    /* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ExtendedResolver$Resolution.class */
    public static class Resolution implements ResolverListener {
        public boolean done;
        public Object[] inprogress;
        public ResolverListener listener;
        public int outstanding;
        public Message query;
        public Resolver[] resolvers;
        public Message response;
        public int retries;
        public int[] sent;
        public Throwable thrown;

        public Resolution(ExtendedResolver extendedResolver, Message message) {
            List list = extendedResolver.resolvers;
            this.resolvers = (Resolver[]) list.toArray(new Resolver[list.size()]);
            if (extendedResolver.loadBalance) {
                int length = this.resolvers.length;
                int access$208 = ExtendedResolver.access$208(extendedResolver) % length;
                if (extendedResolver.lbStart > length) {
                    ExtendedResolver.access$244(extendedResolver, length);
                }
                if (access$208 > 0) {
                    Resolver[] resolverArr = new Resolver[length];
                    for (int i7 = 0; i7 < length; i7++) {
                        resolverArr[i7] = this.resolvers[(i7 + access$208) % length];
                    }
                    this.resolvers = resolverArr;
                }
            }
            Resolver[] resolverArr2 = this.resolvers;
            this.sent = new int[resolverArr2.length];
            this.inprogress = new Object[resolverArr2.length];
            this.retries = extendedResolver.retries;
            this.query = message;
        }

        /* JADX WARN: Code restructure failed: missing block: B:41:0x00af, code lost:
        
            if (r5.thrown == null) goto L41;
         */
        /* JADX WARN: Removed duplicated region for block: B:46:0x00db  */
        /* JADX WARN: Removed duplicated region for block: B:49:0x00de  */
        @Override // org.xbill.DNS.ResolverListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void handleException(java.lang.Object r6, java.lang.Exception r7) {
            /*
                Method dump skipped, instructions count: 333
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.xbill.DNS.ExtendedResolver.Resolution.handleException(java.lang.Object, java.lang.Exception):void");
        }

        @Override // org.xbill.DNS.ResolverListener
        public void receiveMessage(Object obj, Message message) {
            if (Options.check("verbose")) {
                System.err.println("ExtendedResolver: received message");
            }
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.response = message;
                this.done = true;
                ResolverListener resolverListener = this.listener;
                if (resolverListener == null) {
                    notifyAll();
                } else {
                    resolverListener.receiveMessage(this, message);
                }
            }
        }

        public void send(int i7) {
            int[] iArr = this.sent;
            iArr[i7] = iArr[i7] + 1;
            this.outstanding++;
            try {
                this.inprogress[i7] = this.resolvers[i7].sendAsync(this.query, this);
            } catch (Throwable th) {
                synchronized (this) {
                    this.thrown = th;
                    this.done = true;
                    if (this.listener == null) {
                        notifyAll();
                    }
                }
            }
        }

        public Message start() {
            try {
                int[] iArr = this.sent;
                iArr[0] = iArr[0] + 1;
                this.outstanding++;
                this.inprogress[0] = new Object();
                return this.resolvers[0].send(this.query);
            } catch (Exception e8) {
                handleException(this.inprogress[0], e8);
                synchronized (this) {
                    while (!this.done) {
                        try {
                            wait();
                        } catch (InterruptedException e9) {
                        }
                    }
                    Message message = this.response;
                    if (message != null) {
                        return message;
                    }
                    Throwable th = this.thrown;
                    if (th instanceof IOException) {
                        throw ((IOException) th);
                    }
                    if (th instanceof RuntimeException) {
                        throw ((RuntimeException) th);
                    }
                    if (th instanceof Error) {
                        throw ((Error) th);
                    }
                    throw new IllegalStateException("ExtendedResolver failure");
                }
            }
        }

        public void startAsync(ResolverListener resolverListener) {
            this.listener = resolverListener;
            send(0);
        }
    }

    public ExtendedResolver() {
        init();
        String[] servers = ResolverConfig.getCurrentConfig().servers();
        if (servers == null) {
            this.resolvers.add(new SimpleResolver());
            return;
        }
        for (String str : servers) {
            SimpleResolver simpleResolver = new SimpleResolver(str);
            simpleResolver.setTimeout(5);
            this.resolvers.add(simpleResolver);
        }
    }

    public ExtendedResolver(String[] strArr) {
        init();
        for (String str : strArr) {
            SimpleResolver simpleResolver = new SimpleResolver(str);
            simpleResolver.setTimeout(5);
            this.resolvers.add(simpleResolver);
        }
    }

    public ExtendedResolver(Resolver[] resolverArr) {
        init();
        for (Resolver resolver : resolverArr) {
            this.resolvers.add(resolver);
        }
    }

    public static /* synthetic */ int access$208(ExtendedResolver extendedResolver) {
        int i7 = extendedResolver.lbStart;
        extendedResolver.lbStart = i7 + 1;
        return i7;
    }

    public static /* synthetic */ int access$244(ExtendedResolver extendedResolver, int i7) {
        int i8 = extendedResolver.lbStart % i7;
        extendedResolver.lbStart = i8;
        return i8;
    }

    private void init() {
        this.resolvers = new ArrayList();
    }

    public void addResolver(Resolver resolver) {
        this.resolvers.add(resolver);
    }

    public void deleteResolver(Resolver resolver) {
        this.resolvers.remove(resolver);
    }

    public Resolver getResolver(int i7) {
        if (i7 < this.resolvers.size()) {
            return (Resolver) this.resolvers.get(i7);
        }
        return null;
    }

    public Resolver[] getResolvers() {
        List list = this.resolvers;
        return (Resolver[]) list.toArray(new Resolver[list.size()]);
    }

    @Override // org.xbill.DNS.Resolver
    public Message send(Message message) {
        return new Resolution(this, message).start();
    }

    @Override // org.xbill.DNS.Resolver
    public Object sendAsync(Message message, ResolverListener resolverListener) {
        Resolution resolution = new Resolution(this, message);
        resolution.startAsync(resolverListener);
        return resolution;
    }

    @Override // org.xbill.DNS.Resolver
    public void setEDNS(int i7) {
        for (int i8 = 0; i8 < this.resolvers.size(); i8++) {
            ((Resolver) this.resolvers.get(i8)).setEDNS(i7);
        }
    }

    @Override // org.xbill.DNS.Resolver
    public void setEDNS(int i7, int i8, int i9, List list) {
        for (int i10 = 0; i10 < this.resolvers.size(); i10++) {
            ((Resolver) this.resolvers.get(i10)).setEDNS(i7, i8, i9, list);
        }
    }

    @Override // org.xbill.DNS.Resolver
    public void setIgnoreTruncation(boolean z7) {
        for (int i7 = 0; i7 < this.resolvers.size(); i7++) {
            ((Resolver) this.resolvers.get(i7)).setIgnoreTruncation(z7);
        }
    }

    public void setLoadBalance(boolean z7) {
        this.loadBalance = z7;
    }

    @Override // org.xbill.DNS.Resolver
    public void setPort(int i7) {
        for (int i8 = 0; i8 < this.resolvers.size(); i8++) {
            ((Resolver) this.resolvers.get(i8)).setPort(i7);
        }
    }

    public void setRetries(int i7) {
        this.retries = i7;
    }

    @Override // org.xbill.DNS.Resolver
    public void setTCP(boolean z7) {
        for (int i7 = 0; i7 < this.resolvers.size(); i7++) {
            ((Resolver) this.resolvers.get(i7)).setTCP(z7);
        }
    }

    @Override // org.xbill.DNS.Resolver
    public void setTSIGKey(TSIG tsig) {
        for (int i7 = 0; i7 < this.resolvers.size(); i7++) {
            ((Resolver) this.resolvers.get(i7)).setTSIGKey(tsig);
        }
    }

    @Override // org.xbill.DNS.Resolver
    public void setTimeout(int i7) {
        setTimeout(i7, 0);
    }

    @Override // org.xbill.DNS.Resolver
    public void setTimeout(int i7, int i8) {
        for (int i9 = 0; i9 < this.resolvers.size(); i9++) {
            ((Resolver) this.resolvers.get(i9)).setTimeout(i7, i8);
        }
    }
}
