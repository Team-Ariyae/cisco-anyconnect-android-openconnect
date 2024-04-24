package app.openconnect.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/UserDialog.class */
public abstract class UserDialog {
    public static final String TAG = "OpenConnect";
    private static HashMap<String, DeferredPref> mDeferredPrefs = new HashMap<>();
    private boolean mDialogUp;
    public SharedPreferences mPrefs;
    private Object mResult;

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/UserDialog$DeferredBooleanPref.class */
    public class DeferredBooleanPref extends DeferredPref {
        public final UserDialog this$0;
        public boolean value;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DeferredBooleanPref(UserDialog userDialog, SharedPreferences sharedPreferences, String str, boolean z7) {
            super(userDialog, sharedPreferences, str);
            this.this$0 = userDialog;
            this.value = z7;
        }

        @Override // app.openconnect.core.UserDialog.DeferredPref
        public void commit() {
            this.mPrefs.edit().putBoolean(this.mKey, this.value).commit();
        }
    }

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/UserDialog$DeferredPref.class */
    public abstract class DeferredPref {
        public String mKey;
        public SharedPreferences mPrefs;
        public final UserDialog this$0;

        public DeferredPref(UserDialog userDialog, SharedPreferences sharedPreferences, String str) {
            this.this$0 = userDialog;
            this.mPrefs = sharedPreferences;
            this.mKey = str;
        }

        public abstract void commit();
    }

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/UserDialog$DeferredStringPref.class */
    public class DeferredStringPref extends DeferredPref {
        public final UserDialog this$0;
        public String value;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DeferredStringPref(UserDialog userDialog, SharedPreferences sharedPreferences, String str, String str2) {
            super(userDialog, sharedPreferences, str);
            this.this$0 = userDialog;
            this.value = str2;
        }

        @Override // app.openconnect.core.UserDialog.DeferredPref
        public void commit() {
            this.mPrefs.edit().putString(this.mKey, this.value).commit();
        }
    }

    public UserDialog(SharedPreferences sharedPreferences) {
        this.mPrefs = sharedPreferences;
    }

    public static void clearDeferredPrefs() {
        mDeferredPrefs.clear();
    }

    public static void writeDeferredPrefs() {
        Iterator<DeferredPref> it = mDeferredPrefs.values().iterator();
        while (it.hasNext()) {
            it.next().commit();
        }
        mDeferredPrefs.clear();
    }

    public Object earlyReturn() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.mResult == null ? "not skipping" : "skipping");
        sb.append(" user dialog");
        Log.d("OpenConnect", sb.toString());
        return this.mResult;
    }

    public void finish(Object obj) {
        synchronized (this) {
            if (this.mDialogUp) {
                this.mResult = obj;
                notifyAll();
            }
        }
    }

    public boolean getBooleanPref(String str) {
        try {
            return ((DeferredBooleanPref) mDeferredPrefs.get(str)).value;
        } catch (ClassCastException | NullPointerException e8) {
            return this.mPrefs.getBoolean(str, false);
        }
    }

    public String getStringPref(String str) {
        try {
            return ((DeferredStringPref) mDeferredPrefs.get(str)).value;
        } catch (ClassCastException | NullPointerException e8) {
            return this.mPrefs.getString(str, BuildConfig.FLAVOR);
        }
    }

    public void onStart(Context context) {
        this.mDialogUp = true;
        Log.d("OpenConnect", "rendering user dialog");
    }

    public void onStop(Context context) {
        this.mDialogUp = false;
        Log.d("OpenConnect", "tearing down user dialog");
    }

    public void setBooleanPref(String str, boolean z7) {
        mDeferredPrefs.put(str, new DeferredBooleanPref(this, this.mPrefs, str, z7));
    }

    public void setStringPref(String str, String str2) {
        mDeferredPrefs.put(str, new DeferredStringPref(this, this.mPrefs, str, str2));
    }

    public Object waitForResponse() {
        while (true) {
            Object obj = this.mResult;
            if (obj != null) {
                return obj;
            }
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e8) {
                }
            }
        }
    }
}
