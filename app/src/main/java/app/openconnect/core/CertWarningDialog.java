package app.openconnect.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import sp.openconnecttest.R;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/CertWarningDialog.class */
public class CertWarningDialog extends UserDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    public static final int RESULT_ALWAYS = 2;
    public static final int RESULT_NO = 0;
    public static final int RESULT_ONCE = 1;
    private int mAccept;
    private AlertDialog mAlert;
    public String mCertSHA1;
    public String mHostname;
    public String mReason;

    public CertWarningDialog(SharedPreferences sharedPreferences, String str, String str2, String str3) {
        super(sharedPreferences);
        this.mAccept = 0;
        this.mHostname = str;
        this.mCertSHA1 = str2;
        this.mReason = str3;
    }

    @Override // app.openconnect.core.UserDialog
    public Object earlyReturn() {
        return null;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i7) {
        int i8;
        if (i7 == -1) {
            i8 = 2;
        } else if (i7 != -3) {
            return;
        } else {
            i8 = 1;
        }
        this.mAccept = i8;
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish(Integer.valueOf(this.mAccept));
        this.mAlert = null;
    }

    @Override // app.openconnect.core.UserDialog
    public void onStart(Context context) {
        super.onStart(context);
        AlertDialog create =
                new AlertDialog.Builder(context).setTitle("CERT WARNING")
                        .setMessage(
                                context.getString("THIS IS MSG", this.mHostname,
                                        this.mReason, this.mCertSHA1))
                        .setPositiveButton(R.string.app_name, this)
                        .setNeutralButton(R.string.app_name, this)
                        .setNegativeButton(R.string.app_name, this).create();
        this.mAlert = create;
        create.setOnDismissListener(this);
        this.mAlert.show();
    }

    @Override // app.openconnect.core.UserDialog
    public void onStop(Context context) {
        super.onStop(context);
        finish(null);
        AlertDialog alertDialog = this.mAlert;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
