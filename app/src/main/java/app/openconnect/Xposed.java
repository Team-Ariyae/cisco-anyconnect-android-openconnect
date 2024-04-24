package app.openconnect;

import android.os.Binder;
import android.util.Log;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.a;
import de.robv.android.xposed.b;
import e3.a;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/Xposed.class */
public class Xposed {
    public static final String PKG_NAME = "app.openconnect";

    public void initZygote(a aVar) {
        Class b8 = b.b(null, "android.net.BaseNetworkStateTracker");
        b.a("prepare", String.class, String.class, new de.robv.android.xposed.a(this) { // from class: app.openconnect.Xposed.1
            public final Xposed this$0;

            {
                this.this$0 = this;
            }

            @Override // de.robv.android.xposed.a
            public void beforeHookedMethod(a.C0058a c0058a) {
                throw null;
            }
        });
        b.a("enforceControlPermission", new de.robv.android.xposed.a(this, b8) { // from class: app.openconnect.Xposed.2
            public final Xposed this$0;
            public final Class val$clazz0;

            {
                this.this$0 = this;
                this.val$clazz0 = b8;
            }

            @Override // de.robv.android.xposed.a
            public void beforeHookedMethod(a.C0058a c0058a) {
                Binder.getCallingUid();
                try {
                    this.val$clazz0.getDeclaredField("mContext").setAccessible(true);
                    throw null;
                } catch (Exception e8) {
                    StringBuilder r7 = androidx.activity.result.a.r("OpenConnect: exception checking UIDs: ");
                    r7.append(e8.getLocalizedMessage());
                    String sb = r7.toString();
                    Object[] objArr = XposedBridge.f3060a;
                    synchronized (XposedBridge.class) {
                        try {
                            Log.i("Xposed", sb);
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            }
        });
    }
}
