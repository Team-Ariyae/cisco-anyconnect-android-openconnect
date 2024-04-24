package app.openconnect.api;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import app.openconnect.core.OpenVpnService;
import v6.a;
import v6.b;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/api/GrantPermissionsActivity.class */
public class GrantPermissionsActivity extends Activity {
    public static final String EXTRA_START_ACTIVITY = ".start_activity";
    public static final String EXTRA_UUID = ".UUID";
    private String mStartActivity;
    private String mUUID;

    private void reportBadRom(Exception exc) {
        if (a.f6485a == null) {
            Log.w("a", "Calling ACRA.getConfig() before ACRA.init() gives you an empty configuration instance. You might prefer calling ACRA.getNewDefaultConfig(Application) to get an instance with default values taken from a @ReportsCrashes annotation.");
            a.f6485a = new b(null);
        }
        b bVar = a.f6485a;
        bVar.getClass();
        a.f6485a = bVar;
        throw new IllegalStateException("Cannot access ErrorReporter before ACRA#init");
    }

    @Override // android.app.Activity
    public void onActivityResult(int i7, int i8, Intent intent) {
        super.onActivityResult(i7, i8, intent);
        setResult(i8);
        if (i8 == -1) {
            Intent intent2 = new Intent(getBaseContext(), (Class<?>) OpenVpnService.class);
            intent2.putExtra(OpenVpnService.EXTRA_UUID, this.mUUID);
            startService(intent2);
            if (this.mStartActivity != null) {
                Intent intent3 = new Intent();
                intent3.setClassName(this, this.mStartActivity);
                startActivity(intent3);
            }
        }
        finish();
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(getPackageName() + EXTRA_UUID);
        this.mUUID = stringExtra;
        if (stringExtra == null) {
            finish();
            return;
        }
        this.mStartActivity = intent.getStringExtra(getPackageName() + EXTRA_START_ACTIVITY);
        try {
            Intent prepare = VpnService.prepare(this);
            if (prepare != null) {
                startActivityForResult(prepare, 0);
            } else {
                onActivityResult(0, -1, null);
            }
        } catch (Exception e8) {
            reportBadRom(e8);
            finish();
        }
    }
}
