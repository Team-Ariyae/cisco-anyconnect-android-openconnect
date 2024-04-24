package app.openconnect.core;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/VPNLog.class */
public class VPNLog {
    public static final String DEFAULT_TIME_FORMAT = "short";
    public static final int LEVEL_DEBUG = 2;
    public static final int LEVEL_ERR = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_TRACE = 3;
    private static final int MAX_ENTRIES = 512;
    public static final String TAG = "OpenConnect";
    private static VPNLog mInstance;
    private ArrayList<VPNLogItem> circ = new ArrayList<>();
    private LogArrayAdapter mArrayAdapter;

    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/VPNLog$LogArrayAdapter.class */
    public class LogArrayAdapter extends BaseAdapter {
        private Context mContext;
        private String mTimeFormat = VPNLog.DEFAULT_TIME_FORMAT;
        public final VPNLog this$0;

        public LogArrayAdapter(VPNLog vPNLog, Context context) {
            this.this$0 = vPNLog;
            this.mContext = context;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.this$0.circ.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i7) {
            return this.this$0.circ.get(i7);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i7) {
            return i7;
        }

        @Override // android.widget.Adapter
        public View getView(int i7, View view, ViewGroup viewGroup) {
            TextView textView = (view == null || !(view instanceof TextView)) ? new TextView(this.mContext) : (TextView) view;
            textView.setText(((VPNLogItem) getItem(i7)).format(this.mContext, this.mTimeFormat));
            return textView;
        }

        public void setTimeFormat(String str) {
            this.mTimeFormat = str;
            notifyDataSetChanged();
        }
    }

    public VPNLog() {
        mInstance = this;
    }

    public static String dumpLast() {
        VPNLog vPNLog = mInstance;
        return vPNLog == null ? BuildConfig.FLAVOR : vPNLog.dump();
    }

    private void updateAdapter() {
        LogArrayAdapter logArrayAdapter = this.mArrayAdapter;
        if (logArrayAdapter != null) {
            logArrayAdapter.notifyDataSetChanged();
        }
    }

    public void add(int i7, String str) {
        this.circ.add(new VPNLogItem(i7, str));
        while (this.circ.size() > 512) {
            this.circ.remove(0);
        }
        updateAdapter();
    }

    public void clear() {
        this.circ.clear();
        updateAdapter();
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        for (Object obj : this.circ.toArray()) {
            sb.append(obj.toString() + "\n");
        }
        return sb.toString();
    }

    public LogArrayAdapter getArrayAdapter(Context context) {
        if (this.mArrayAdapter != null) {
            Log.w("OpenConnect", "duplicate LogArrayAdapter registration");
        }
        LogArrayAdapter logArrayAdapter = new LogArrayAdapter(this, context);
        this.mArrayAdapter = logArrayAdapter;
        return logArrayAdapter;
    }

    public void putArrayAdapter(LogArrayAdapter logArrayAdapter) {
        this.mArrayAdapter = null;
    }

    public int restoreFromFile(String str) {
        StringBuilder sb;
        String str2;
        int i7;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(str));
            this.circ.clear();
            for (int intValue = ((Integer) objectInputStream.readObject()).intValue(); intValue > 0; intValue--) {
                this.circ.add((VPNLogItem) objectInputStream.readObject());
            }
            objectInputStream.close();
            i7 = 0;
        } catch (FileNotFoundException e8) {
            Log.d("OpenConnect", "file not found reading " + str);
            i7 = -1;
            return i7;
        } catch (IOException e9) {
            e = e9;
            sb = new StringBuilder();
            str2 = "I/O error reading ";
            sb.append(str2);
            sb.append(str);
            Log.w("OpenConnect", sb.toString(), e);
            i7 = -1;
            return i7;
        } catch (ClassNotFoundException e10) {
            e = e10;
            sb = new StringBuilder();
            str2 = "Class not found reading ";
            sb.append(str2);
            sb.append(str);
            Log.w("OpenConnect", sb.toString(), e);
            i7 = -1;
            return i7;
        }
        return i7;
    }

    public int saveToFile(String str) {
        StringBuilder sb;
        String str2;
        int i7;
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(str));
            objectOutputStream.writeObject(Integer.valueOf(this.circ.size()));
            Iterator<VPNLogItem> it = this.circ.iterator();
            while (it.hasNext()) {
                objectOutputStream.writeObject(it.next());
            }
            objectOutputStream.close();
            i7 = 0;
        } catch (FileNotFoundException e8) {
            e = e8;
            sb = new StringBuilder();
            str2 = "file not found writing ";
            sb.append(str2);
            sb.append(str);
            Log.w("OpenConnect", sb.toString(), e);
            i7 = -1;
            return i7;
        } catch (IOException e9) {
            e = e9;
            sb = new StringBuilder();
            str2 = "I/O error writing ";
            sb.append(str2);
            sb.append(str);
            Log.w("OpenConnect", sb.toString(), e);
            i7 = -1;
            return i7;
        }
        return i7;
    }
}
