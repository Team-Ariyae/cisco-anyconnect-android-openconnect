package app.openconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.util.Iterator;
import java.util.Map;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/ClearPasswordPreference.class */
public class ClearPasswordPreference extends DialogPreference {
    public ClearPasswordPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.preference.DialogPreference
    public void onDialogClosed(boolean z7) {
        if (z7) {
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            Iterator<Map.Entry<String, ?>> it = sharedPreferences.getAll().entrySet().iterator();
            while (it.hasNext()) {
                String key = it.next().getKey();
                if (key.startsWith("FORMDATA-") || key.startsWith("ACCEPTED-CERT-")) {
                    sharedPreferences.edit().putString(key, BuildConfig.FLAVOR).commit();
                }
            }
        }
    }
}
