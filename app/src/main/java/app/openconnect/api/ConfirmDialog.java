package app.openconnect.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.CompoundButton;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/api/ConfirmDialog.class */
public class ConfirmDialog extends Activity implements CompoundButton.OnCheckedChangeListener, DialogInterface.OnClickListener {
    private static final String TAG = "OpenVPNVpnConfirm";
    private AlertDialog mAlert;
    private Button mButton;
    private String mPackage;

    @Override // android.app.Activity
    public void onBackPressed() {
        setResult(0);
        finish();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z7) {
        this.mButton.setEnabled(z7);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i7) {
        if (i7 == -1) {
            new ExternalAppDatabase(this).addApp(this.mPackage);
            setResult(-1);
            finish();
        }
        if (i7 == -2) {
            setResult(0);
            finish();
        }
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
    }
}
