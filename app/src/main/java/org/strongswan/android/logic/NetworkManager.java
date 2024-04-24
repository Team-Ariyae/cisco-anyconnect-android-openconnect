package org.strongswan.android.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.LinkedList;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/NetworkManager.class */
public class NetworkManager extends BroadcastReceiver implements Runnable {
    private final Context mContext;
    private Thread mEventNotifier;
    private LinkedList<Boolean> mEvents = new LinkedList<>();
    private volatile boolean mRegistered;

    public NetworkManager(Context context) {
        this.mContext = context;
    }

    public void Register() {
        this.mEvents.clear();
        this.mRegistered = true;
        Thread thread = new Thread(this);
        this.mEventNotifier = thread;
        thread.start();
        this.mContext.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void Unregister() {
        this.mContext.unregisterReceiver(this);
        this.mRegistered = false;
        synchronized (this) {
            notifyAll();
        }
        try {
            this.mEventNotifier.join();
            this.mEventNotifier = null;
        } catch (InterruptedException e8) {
            e8.printStackTrace();
        }
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public native void networkChanged(boolean z7);

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        synchronized (this) {
            this.mEvents.addLast(Boolean.valueOf(isConnected()));
            notifyAll();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean booleanValue;
        while (this.mRegistered) {
            synchronized (this) {
                while (this.mRegistered && this.mEvents.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e8) {
                    }
                }
                if (!this.mRegistered) {
                    return;
                } else {
                    booleanValue = this.mEvents.removeFirst().booleanValue();
                }
            }
            networkChanged(!booleanValue);
        }
    }
}
