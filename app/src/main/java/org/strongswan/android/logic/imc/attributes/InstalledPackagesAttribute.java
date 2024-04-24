package org.strongswan.android.logic.imc.attributes;

import android.util.Pair;
import java.util.Iterator;
import java.util.LinkedList;
import org.strongswan.android.utils.BufferedByteWriter;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/attributes/InstalledPackagesAttribute.class */
public class InstalledPackagesAttribute implements Attribute {
    private final short RESERVED = 0;
    private final LinkedList<Pair<String, String>> mPackages = new LinkedList<>();

    public void addPackage(String str, String str2) {
        this.mPackages.add(new Pair<>(str, str2));
    }

    @Override // org.strongswan.android.logic.imc.attributes.Attribute
    public byte[] getEncoding() {
        BufferedByteWriter bufferedByteWriter = new BufferedByteWriter();
        bufferedByteWriter.put16((short) 0);
        bufferedByteWriter.put16((short) this.mPackages.size());
        Iterator<Pair<String, String>> it = this.mPackages.iterator();
        while (it.hasNext()) {
            Pair<String, String> next = it.next();
            bufferedByteWriter.putLen8(((String) next.first).getBytes());
            bufferedByteWriter.putLen8(((String) next.second).getBytes());
        }
        return bufferedByteWriter.toByteArray();
    }
}
