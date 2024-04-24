package app.openconnect.core;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/ErrorDialog.class */
public class ErrorDialog extends UserDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private AlertDialog mAlert;
    public String mMessage;
    public String mTitle;

    public ErrorDialog(SharedPreferences sharedPreferences, String str, String str2) {
        super(sharedPreferences);
        this.mTitle = str;
        this.mMessage = str2;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i7) {
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish(Boolean.TRUE);
        this.mAlert = null;
    }

    @Override // app.openconnect.core.UserDialog
    public void onStart(Context context) {
        super.onStart(context);
        AlertDialog create = new AlertDialog.Builder(context).setTitle(this.mTitle).setMessage(this.mMessage).setPositiveButton(R.string.ok, this).create();
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
