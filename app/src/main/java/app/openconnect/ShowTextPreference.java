package app.openconnect;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import io.github.inflationx.calligraphy3.BuildConfig;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/ShowTextPreference.class */
public class ShowTextPreference extends DialogPreference {
    public ShowTextPreference(Context context) {
        this(context, null);
    }

    public ShowTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ShowTextPreference(Context context, AttributeSet attributeSet, int i7) {
        super(context, attributeSet, i7);
    }

    @Override // android.preference.DialogPreference, android.preference.Preference
    public void onClick() {
    }

    public void setText(String str) {
        String str2 = str;
        if (str == null) {
            str2 = BuildConfig.FLAVOR;
        }
        persistString(str2);
        notifyDependencyChange(shouldDisableDependents());
    }
}
