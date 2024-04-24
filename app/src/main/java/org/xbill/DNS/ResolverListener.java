package org.xbill.DNS;

import java.util.EventListener;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/ResolverListener.class */
public interface ResolverListener extends EventListener {
    void handleException(Object obj, Exception exc);

    void receiveMessage(Object obj, Message message);
}
