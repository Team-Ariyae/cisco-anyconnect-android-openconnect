package org.strongswan.android.logic.imc.collectors;

import android.os.Build;
import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.StringVersionAttribute;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/collectors/StringVersionCollector.class */
public class StringVersionCollector implements Collector {
    @Override // org.strongswan.android.logic.imc.collectors.Collector
    public Attribute getMeasurement() {
        StringVersionAttribute stringVersionAttribute = new StringVersionAttribute();
        stringVersionAttribute.setProductVersionNumber(Build.VERSION.RELEASE);
        stringVersionAttribute.setInternalBuildNumber(Build.DISPLAY);
        return stringVersionAttribute;
    }
}
