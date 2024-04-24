package org.strongswan.android.logic.imc.attributes;

import android.util.Pair;
import java.util.Iterator;
import java.util.LinkedList;
import org.strongswan.android.utils.BufferedByteWriter;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/attributes/SettingsAttribute.class */
public class SettingsAttribute implements Attribute {
    private final LinkedList<Pair<String, String>> mSettings = new LinkedList<>();

    public void addSetting(String str, String str2) {
        this.mSettings.add(new Pair<>(str, str2));
    }

    @Override // org.strongswan.android.logic.imc.attributes.Attribute
    public byte[] getEncoding() {
        BufferedByteWriter bufferedByteWriter = new BufferedByteWriter();
        bufferedByteWriter.put32(this.mSettings.size());
        Iterator<Pair<String, String>> it = this.mSettings.iterator();
        while (it.hasNext()) {
            Pair<String, String> next = it.next();
            bufferedByteWriter.putLen16(((String) next.first).getBytes());
            bufferedByteWriter.putLen16(((String) next.second).getBytes());
        }
        return bufferedByteWriter.toByteArray();
    }
}
