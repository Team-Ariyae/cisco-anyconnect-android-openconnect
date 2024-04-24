package app.openconnect.core;

import android.content.Context;
import androidx.activity.result.a;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/VPNLogItem.class */
public class VPNLogItem implements Serializable {
    private static final long serialVersionUID = 7341923752956090364L;
    private int mLevel;
    private long mLogtime = System.currentTimeMillis();
    private String mMsg;

    public VPNLogItem(int i7, String str) {
        this.mLevel = i7;
        this.mMsg = str;
    }

    public String format(Context context, String str) {
        String str2;
        if (str.equals("none")) {
            str2 = BuildConfig.FLAVOR;
        } else {
            Date date = new Date(this.mLogtime);
            str2 = (str.equals("long") ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) : new SimpleDateFormat("HH:mm:ss", Locale.getDefault())).format(date) + " ";
        }
        StringBuilder r7 = a.r(str2);
        r7.append(this.mMsg);
        return r7.toString();
    }

    public String toString() {
        return format(null, "long");
    }
}
