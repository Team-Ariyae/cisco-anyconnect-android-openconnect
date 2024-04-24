package org.xbill.DNS;

import java.net.SocketAddress;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/PacketLogger.class */
public interface PacketLogger {
    void log(String str, SocketAddress socketAddress, SocketAddress socketAddress2, byte[] bArr);
}
