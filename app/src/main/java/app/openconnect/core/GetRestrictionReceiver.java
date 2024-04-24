package app.openconnect.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.os.Bundle;
import java.util.ArrayList;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/GetRestrictionReceiver.class */
public class GetRestrictionReceiver extends BroadcastReceiver {
    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<RestrictionEntry> initRestrictions(Context context) {
        ArrayList<RestrictionEntry> arrayList = new ArrayList<>();
        RestrictionEntry restrictionEntry = new RestrictionEntry("allow_changes", false);
        restrictionEntry.setTitle(context.getString(2131755078));
        arrayList.add(restrictionEntry);
        return arrayList;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        new Thread(this, context, goAsync()) { // from class: app.openconnect.core.GetRestrictionReceiver.1
            public final GetRestrictionReceiver this$0;
            public final Context val$context;
            public final BroadcastReceiver.PendingResult val$result;

            {
                this.this$0 = this;
                this.val$context = context;
                this.val$result = r6;
            }

            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("android.intent.extra.restrictions_list", this.this$0.initRestrictions(this.val$context));
                this.val$result.setResult(-1, null, bundle);
                this.val$result.finish();
            }
        }.run();
    }
}
