package org.strongswan.android.logic.imc.attributes;

import android.util.Pair;
import java.util.Iterator;
import java.util.LinkedList;
import org.strongswan.android.logic.imc.collectors.Protocol;
import org.strongswan.android.utils.BufferedByteWriter;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/attributes/PortFilterAttribute.class */
public class PortFilterAttribute implements Attribute {
    private final LinkedList<Pair<Protocol, Short>> mPorts = new LinkedList<>();

    public void addPort(Protocol protocol, short s7) {
        this.mPorts.add(new Pair<>(protocol, Short.valueOf(s7)));
    }

    @Override // org.strongswan.android.logic.imc.attributes.Attribute
    public byte[] getEncoding() {
        BufferedByteWriter bufferedByteWriter = new BufferedByteWriter();
        Iterator<Pair<Protocol, Short>> it = this.mPorts.iterator();
        while (it.hasNext()) {
            Pair<Protocol, Short> next = it.next();
            bufferedByteWriter.put((byte) 0);
            bufferedByteWriter.put(((Protocol) next.first).getValue());
            bufferedByteWriter.put16(((Short) next.second).shortValue());
        }
        return bufferedByteWriter.toByteArray();
    }
}
