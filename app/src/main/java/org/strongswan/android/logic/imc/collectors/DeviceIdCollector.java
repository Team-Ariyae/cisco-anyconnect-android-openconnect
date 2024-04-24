package org.strongswan.android.logic.imc.collectors;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.DeviceIdAttribute;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/collectors/DeviceIdCollector.class */
public class DeviceIdCollector implements Collector {
    private final ContentResolver mContentResolver;

    public DeviceIdCollector(Context context) {
        this.mContentResolver = context.getContentResolver();
    }

    @Override // org.strongswan.android.logic.imc.collectors.Collector
    public Attribute getMeasurement() {
        String string = Settings.Secure.getString(this.mContentResolver, "android_id");
        if (string == null) {
            return null;
        }
        DeviceIdAttribute deviceIdAttribute = new DeviceIdAttribute();
        deviceIdAttribute.setDeviceId(string);
        return deviceIdAttribute;
    }
}
