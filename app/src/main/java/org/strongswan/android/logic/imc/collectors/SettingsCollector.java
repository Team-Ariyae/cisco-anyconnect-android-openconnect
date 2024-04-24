package org.strongswan.android.logic.imc.collectors;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import java.util.Locale;
import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.SettingsAttribute;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/collectors/SettingsCollector.class */
public class SettingsCollector implements Collector {
    private final ContentResolver mContentResolver;
    private final String[] mSettings;

    public SettingsCollector(Context context, String[] strArr) {
        this.mContentResolver = context.getContentResolver();
        this.mSettings = strArr;
    }

    @Override // org.strongswan.android.logic.imc.collectors.Collector
    public Attribute getMeasurement() {
        String[] strArr = this.mSettings;
        if (strArr == null || strArr.length == 0) {
            return null;
        }
        SettingsAttribute settingsAttribute = new SettingsAttribute();
        for (String str : this.mSettings) {
            ContentResolver contentResolver = this.mContentResolver;
            Locale locale = Locale.US;
            String string = Settings.Secure.getString(contentResolver, str.toLowerCase(locale));
            String str2 = string;
            if (string == null) {
                str2 = Settings.System.getString(this.mContentResolver, str.toLowerCase(locale));
            }
            if (str2 != null) {
                settingsAttribute.addSetting(str, str2);
            }
        }
        return settingsAttribute;
    }
}
