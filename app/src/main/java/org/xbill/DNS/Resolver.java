package org.xbill.DNS;

import java.util.List;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/Resolver.class */
public interface Resolver {
    Message send(Message message);

    Object sendAsync(Message message, ResolverListener resolverListener);

    void setEDNS(int i7);

    void setEDNS(int i7, int i8, int i9, List list);

    void setIgnoreTruncation(boolean z7);

    void setPort(int i7);

    void setTCP(boolean z7);

    void setTSIGKey(TSIG tsig);

    void setTimeout(int i7);

    void setTimeout(int i7, int i8);
}
