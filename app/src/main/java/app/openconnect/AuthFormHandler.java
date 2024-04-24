package app.openconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.activity.result.a;
import app.openconnect.core.UserDialog;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import org.infradead.libopenconnect.LibOpenConnect;
import q.g;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/AuthFormHandler.class */
public class AuthFormHandler extends UserDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private static final int BATCH_MODE_ABORTED = 3;
    private static final int BATCH_MODE_DISABLED = 0;
    private static final int BATCH_MODE_EMPTY_ONLY = 1;
    private static final int BATCH_MODE_ENABLED = 2;
    public static final String TAG = "OpenConnect";
    private int batchMode;
    private LinearLayout.LayoutParams fillWidth;
    private String formPfx;
    private boolean isOK;
    private AlertDialog mAlert;
    private boolean mAllFilled;
    private boolean mAuthgroupSet;
    private Context mContext;
    private TextView mFirstEmptyText;
    private TextView mFirstText;
    private LibOpenConnect.AuthForm mForm;
    private Credentials mUserCredentials;
    private boolean noSave;
    private CheckBox savePassword;

    public AuthFormHandler(SharedPreferences sharedPreferences, LibOpenConnect.AuthForm authForm, boolean z7, String str) {
        super(sharedPreferences);
        this.savePassword = null;
        int i7 = 0;
        this.noSave = false;
        this.batchMode = 0;
        this.mAllFilled = true;
        this.fillWidth = new LinearLayout.LayoutParams(-1, -2);
        this.mForm = authForm;
        this.mAuthgroupSet = z7;
        this.formPfx = getFormPrefix(authForm);
        this.noSave = getBooleanPref("disable_username_caching");
        String stringPref = getStringPref("batch_mode");
        if (stringPref.equals("empty_only")) {
            this.batchMode = 1;
        } else if (stringPref.equals("enabled")) {
            this.batchMode = 2;
        }
        if (this.formPfx.equals(str)) {
            int i8 = this.batchMode;
            if (i8 != 1) {
                if (i8 != 2) {
                    return;
                } else {
                    i7 = 3;
                }
            }
            this.batchMode = i7;
        }
    }

    private String digest(String str) {
        String str2;
        String str3 = str;
        if (str == null) {
            str3 = BuildConfig.FLAVOR;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            StringBuilder sb = new StringBuilder();
            for (byte b8 : messageDigest.digest(str3.getBytes("UTF-8"))) {
                sb.append(String.format("%02x", Byte.valueOf(b8)));
            }
            str2 = sb.toString();
        } catch (Exception e8) {
            Log.e("OpenConnect", "MessageDigest failed", e8);
            str2 = BuildConfig.FLAVOR;
        }
        return str2;
    }

    private void fixPadding(View view) {
    }

    private String getFormPrefix(LibOpenConnect.AuthForm authForm) {
        StringBuilder sb = new StringBuilder();
        Iterator<LibOpenConnect.FormOpt> it = authForm.opts.iterator();
        while (it.hasNext()) {
            sb.append(getOptDigest(it.next()));
        }
        return g.c(a.r("FORMDATA-"), digest(sb.toString()), "-");
    }

    private String getOptDigest(LibOpenConnect.FormOpt formOpt) {
        StringBuilder sb = new StringBuilder();
        int i7 = formOpt.type;
        if (i7 != 1 && i7 != 2) {
            if (i7 == 3) {
                Iterator<LibOpenConnect.FormChoice> it = formOpt.choices.iterator();
                while (it.hasNext()) {
                    LibOpenConnect.FormChoice next = it.next();
                    sb.append(digest(next.name));
                    sb.append(digest(next.label));
                }
            }
            return digest(sb.toString());
        }
        StringBuilder r7 = a.r(":");
        r7.append(Integer.toString(formOpt.type));
        r7.append(":");
        sb.append(r7.toString());
        sb.append(digest(formOpt.name));
        sb.append(digest(formOpt.label));
        return digest(sb.toString());
    }

    private LinearLayout newDropdown(LibOpenConnect.FormOpt formOpt, int i7) {
        ArrayList arrayList = new ArrayList();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mContext, R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        Iterator<LibOpenConnect.FormChoice> it = formOpt.choices.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().label);
        }
        Spinner spinner = new Spinner(this.mContext);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
        spinner.setLayoutParams(this.fillWidth);
        spinner.setSelection(i7);
        spinnerSelect(formOpt, i7);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(this, formOpt) { // from class: app.openconnect.AuthFormHandler.1
            public final AuthFormHandler this$0;
            public final LibOpenConnect.FormOpt val$opt;

            {
                this.this$0 = this;
                this.val$opt = formOpt;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i8, long j7) {
                this.this$0.spinnerSelect(this.val$opt, (int) j7);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        LinearLayout newHorizLayout = newHorizLayout(formOpt.label);
        newHorizLayout.addView(spinner);
        return newHorizLayout;
    }

    private LinearLayout newHorizLayout(String str) {
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(this.fillWidth);
        fixPadding(linearLayout);
        TextView textView = new TextView(this.mContext);
        textView.setText(str);
        linearLayout.addView(textView);
        return linearLayout;
    }

    private CheckBox newSavePasswordView(boolean z7) {
        CheckBox checkBox = new CheckBox(this.mContext);
        checkBox.setText(2131755726);
        checkBox.setChecked(z7);
        fixPadding(checkBox);
        return checkBox;
    }

    private LinearLayout newTextBlank(LibOpenConnect.FormOpt formOpt, String str) {
        LinearLayout newHorizLayout = newHorizLayout(formOpt.label);
        EditText editText = new EditText(this.mContext);
        editText.setLayoutParams(this.fillWidth);
        String str2 = str;
        if (str == null) {
            str2 = formOpt.value;
            if (str2 == null) {
                str2 = BuildConfig.FLAVOR;
            }
        }
        editText.setText(str2);
        if (this.mFirstEmptyText == null && str2.equals(BuildConfig.FLAVOR)) {
            this.mFirstEmptyText = editText;
        }
        if (this.mFirstText == null) {
            this.mFirstText = editText;
        }
        int i7 = (formOpt.flags & 2) != 0 ? 2 : 1;
        if (formOpt.type == 2) {
            editText.setInputType(i7 | 128);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            editText.setInputType(i7 | 32);
        }
        formOpt.userData = editText;
        newHorizLayout.addView(editText);
        return newHorizLayout;
    }

    private void saveAndStore() {
        String str;
        String str2;
        Iterator<LibOpenConnect.FormOpt> it = this.mForm.opts.iterator();
        while (it.hasNext()) {
            LibOpenConnect.FormOpt next = it.next();
            if ((next.flags & 1) == 0) {
                int i7 = next.type;
                if (i7 == 1) {
                    Credentials credentials = this.mUserCredentials;
                    if (credentials != null) {
                        String str3 = credentials.username;
                        str = str3;
                        if (!this.noSave) {
                            str2 = this.formPfx + getOptDigest(next);
                            str = str3;
                            setStringPref(str2, str);
                        }
                        next.value = str;
                    }
                } else if (i7 == 2) {
                    Credentials credentials2 = this.mUserCredentials;
                    if (credentials2 != null) {
                        String str4 = credentials2.password;
                        CheckBox checkBox = this.savePassword;
                        str = str4;
                        if (checkBox != null) {
                            boolean isChecked = checkBox.isChecked();
                            setStringPref(this.formPfx + getOptDigest(next), isChecked ? str4 : BuildConfig.FLAVOR);
                            setStringPref(g.c(new StringBuilder(), this.formPfx, "savePass"), isChecked ? "true" : "false");
                            str = str4;
                        }
                        next.value = str;
                    }
                } else if (i7 == 3) {
                    String str5 = (String) next.userData;
                    str = str5;
                    if (!this.noSave) {
                        setStringPref(this.formPfx + getOptDigest(next), str5);
                        str = str5;
                        if ("group_list".equals(next.name)) {
                            str2 = "authgroup";
                            str = str5;
                            setStringPref(str2, str);
                        }
                    }
                    next.value = str;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void spinnerSelect(LibOpenConnect.FormOpt formOpt, int i7) {
        String str = formOpt.choices.get(i7).name;
        if (str == null) {
            str = BuildConfig.FLAVOR;
        }
        Object obj = formOpt.userData;
        if (obj == null) {
            formOpt.userData = str;
        } else {
            if (str.equals(obj)) {
                return;
            }
            formOpt.value = str;
            this.mAlert.dismiss();
            finish(2);
        }
    }

    public boolean SkipDialog(Credentials credentials) {
        if (credentials == null) {
            return false;
        }
        this.mUserCredentials = credentials;
        saveAndStore();
        finish(0);
        return true;
    }

    @Override // app.openconnect.core.UserDialog
    public Object earlyReturn() {
        String stringPref;
        if (SkipDialog(this.mUserCredentials)) {
            return 0;
        }
        if (setAuthgroup()) {
            return 2;
        }
        int i7 = this.batchMode;
        if (i7 != 1 && i7 != 2) {
            return null;
        }
        Iterator<LibOpenConnect.FormOpt> it = this.mForm.opts.iterator();
        while (it.hasNext()) {
            LibOpenConnect.FormOpt next = it.next();
            if ((next.flags & 1) == 0) {
                int i8 = next.type;
                if (i8 == 1 || i8 == 2) {
                    if (this.noSave) {
                        stringPref = BuildConfig.FLAVOR;
                    } else {
                        stringPref = getStringPref(this.formPfx + getOptDigest(next));
                    }
                    if (stringPref.equals(BuildConfig.FLAVOR)) {
                        return null;
                    }
                    next.value = stringPref;
                } else if (i8 == 3 && next.value == null) {
                    return null;
                }
            }
        }
        return 0;
    }

    public String getFormDigest() {
        return this.formPfx;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i7) {
        if (i7 == -1) {
            this.isOK = true;
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        if (this.isOK) {
            saveAndStore();
        }
        finish(Integer.valueOf(!this.isOK ? 1 : 0));
    }

    @Override // app.openconnect.core.UserDialog
    public void onStart(Context context) {
        Integer num;
        String stringPref;
        LinearLayout newTextBlank;
        int i7;
        super.onStart(context);
        this.mContext = context;
        this.isOK = false;
        float f8 = context.getResources().getDisplayMetrics().density;
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        linearLayout.setOrientation(1);
        int i8 = (int) (14.0f * f8);
        int i9 = (int) (2.0f * f8);
        linearLayout.setPadding(i8, i9, (int) (f8 * 10.0f), i9);
        this.mFirstEmptyText = null;
        this.mFirstText = null;
        Iterator<LibOpenConnect.FormOpt> it = this.mForm.opts.iterator();
        boolean z7 = false;
        boolean z8 = false;
        while (true) {
            boolean hasNext = it.hasNext();
            String str = BuildConfig.FLAVOR;
            if (!hasNext) {
                break;
            }
            LibOpenConnect.FormOpt next = it.next();
            if ((next.flags & 1) == 0) {
                int i10 = next.type;
                boolean z9 = z7;
                if (i10 != 1) {
                    if (i10 == 2) {
                        z9 = true;
                    } else if (i10 == 3 && next.choices.size() != 0) {
                        LibOpenConnect.AuthForm authForm = this.mForm;
                        if (next != authForm.authgroupOpt) {
                            if (!this.noSave) {
                                str = getStringPref(this.formPfx + getOptDigest(next));
                            }
                            int i11 = 0;
                            int i12 = 0;
                            while (true) {
                                i7 = i11;
                                if (i12 >= next.choices.size()) {
                                    break;
                                }
                                if (next.choices.get(i12).name.equals(str)) {
                                    i11 = i12;
                                }
                                i12++;
                            }
                        } else {
                            i7 = authForm.authgroupSelection;
                        }
                        newTextBlank = newDropdown(next, i7);
                        linearLayout.addView(newTextBlank);
                        z8 = true;
                    }
                }
                if (this.noSave) {
                    stringPref = BuildConfig.FLAVOR;
                } else {
                    stringPref = getStringPref(this.formPfx + getOptDigest(next));
                }
                String str2 = stringPref;
                if (stringPref.equals(BuildConfig.FLAVOR)) {
                    String str3 = next.value;
                    if (str3 == null || str3.equals(BuildConfig.FLAVOR)) {
                        this.mAllFilled = false;
                        str2 = stringPref;
                    } else {
                        str2 = next.value;
                    }
                }
                newTextBlank = newTextBlank(next, str2);
                z7 = z9;
                linearLayout.addView(newTextBlank);
                z8 = true;
            }
        }
        if (z7 && !this.noSave) {
            CheckBox newSavePasswordView = newSavePasswordView(!getStringPref(this.formPfx + "savePass").equals("false"));
            this.savePassword = newSavePasswordView;
            linearLayout.addView(newSavePasswordView);
        }
        int i13 = this.batchMode;
        if (i13 == 3) {
            num = 1;
        } else {
            if ((i13 != 1 || !this.mAllFilled) && i13 != 2 && z8) {
                AlertDialog create = new AlertDialog.Builder(this.mContext).setView(linearLayout).setTitle(this.mContext.getString(2131755403, getStringPref("profile_name"))).setPositiveButton(2131755538, this).setNegativeButton(2131755115, this).create();
                this.mAlert = create;
                create.setOnDismissListener(this);
                String str4 = this.mForm.message;
                if (str4 != null) {
                    String trim = str4.trim();
                    String str5 = trim;
                    if (trim.length() > 128) {
                        str5 = trim.substring(0, 128);
                    }
                    if (str5.length() > 0) {
                        this.mAlert.setMessage(str5);
                    }
                }
                if (SkipDialog(this.mUserCredentials)) {
                    this.mAlert = null;
                    return;
                }
                this.mAlert.show();
                TextView textView = this.mFirstEmptyText;
                if (textView == null) {
                    textView = this.mFirstText;
                }
                if (textView != null) {
                    textView.append(BuildConfig.FLAVOR);
                    textView.requestFocus();
                    return;
                }
                return;
            }
            saveAndStore();
            num = 0;
        }
        finish(num);
    }

    @Override // app.openconnect.core.UserDialog
    public void onStop(Context context) {
        super.onStop(context);
        if (this.mAlert != null) {
            saveAndStore();
            this.mAlert.dismiss();
            this.mAlert = null;
        }
    }

    public boolean setAuthgroup() {
        LibOpenConnect.FormOpt formOpt = this.mForm.authgroupOpt;
        if (formOpt == null) {
            return false;
        }
        String stringPref = getStringPref("authgroup");
        if (stringPref.equals(BuildConfig.FLAVOR)) {
            return false;
        }
        LibOpenConnect.FormChoice formChoice = formOpt.choices.get(this.mForm.authgroupSelection);
        if (this.mAuthgroupSet || stringPref.equals(formChoice.name)) {
            formOpt.value = stringPref;
            return false;
        }
        Iterator<LibOpenConnect.FormChoice> it = formOpt.choices.iterator();
        while (it.hasNext()) {
            if (stringPref.equals(it.next().name)) {
                formOpt.value = stringPref;
                return true;
            }
        }
        StringBuilder s7 = a.s("saved authgroup '", stringPref, "' not present in ");
        s7.append(formOpt.name);
        s7.append(" dropdown");
        Log.w("OpenConnect", s7.toString());
        return false;
    }

    public void setCredentials(Credentials credentials) {
        this.mUserCredentials = credentials;
    }
}
