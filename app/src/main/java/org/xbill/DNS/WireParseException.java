package org.xbill.DNS;

import java.io.IOException;

/* loaded from: TehShop-dex2jar.jar:org/xbill/DNS/WireParseException.class */
public class WireParseException extends IOException {
    public WireParseException() {
    }

    public WireParseException(String str) {
        super(str);
    }

    public WireParseException(String str, Throwable th) {
        super(str);
        initCause(th);
    }
}
