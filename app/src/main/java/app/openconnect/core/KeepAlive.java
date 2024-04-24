package app.openconnect.core;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import com.v2ray.ang.extension._ExtKt;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import org.xbill.DNS.KEYRecord;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/KeepAlive.class */
public class KeepAlive extends BroadcastReceiver {
    public static final String ACTION_KEEPALIVE_ALARM = "app.openconnect.KEEPALIVE_ALARM";
    public static final String TAG = "OpenConnect";
    private int mBaseDelayMs;
    private boolean mConnectionActive;
    private String mDNSHost = "www.google.com";
    private String mDNSServer;
    private DeviceStateReceiver mDeviceStateReceiver;
    private Handler mMainHandler;
    private PendingIntent mPendingIntent;
    private PowerManager.WakeLock mWakeLock;
    private Handler mWorkerHandler;

    /* renamed from: app.openconnect.core.KeepAlive$1, reason: invalid class name */
    /* loaded from: TehShop-dex2jar.jar:app/openconnect/core/KeepAlive$1.class */
    public class AnonymousClass1 implements Runnable {
        public final KeepAlive this$0;
        public final Context val$context;

        public AnonymousClass1(KeepAlive keepAlive, Context context) {
            this.this$0 = keepAlive;
            this.val$context = context;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean z7;
            DatagramSocket openSocket = this.this$0.openSocket();
            if (openSocket != null) {
                z7 = this.this$0.sendDNSQuery(openSocket);
                openSocket.close();
            } else {
                z7 = false;
            }
            this.this$0.mMainHandler.post(new Runnable(this, z7) { // from class: app.openconnect.core.KeepAlive.1.1
                public final AnonymousClass1 this$1;
                public final boolean val$result;

                {
                    this.this$1 = this;
                    this.val$result = z7;
                }

                @Override // java.lang.Runnable
                public void run() {
                    AnonymousClass1 anonymousClass1 = this.this$1;
                    KeepAlive keepAlive = anonymousClass1.this$0;
                    keepAlive.scheduleNext(anonymousClass1.val$context, this.val$result ? keepAlive.mBaseDelayMs : keepAlive.mBaseDelayMs / 2);
                    this.this$1.this$0.mDeviceStateReceiver.setKeepalive(false);
                    this.this$1.this$0.mWakeLock.release();
                }
            });
        }
    }

    public KeepAlive(int i7, String str, DeviceStateReceiver deviceStateReceiver) {
        this.mBaseDelayMs = i7 * _ExtKt.threshold;
        this.mDNSServer = str;
        this.mDeviceStateReceiver = deviceStateReceiver;
    }

    private byte[] buildDNSQuery(byte[] bArr, String str) {
        int length = str.length() + 2;
        byte[] bArr2 = new byte[length];
        int i7 = 0;
        for (String str2 : str.split("\\.")) {
            bArr2[i7] = (byte) str2.length();
            i7++;
            int i8 = 0;
            while (i8 < str2.length()) {
                bArr2[i7] = (byte) str2.charAt(i8);
                i8++;
                i7++;
            }
        }
        byte[] bArr3 = new byte[bArr.length + 10 + length + 4];
        System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        System.arraycopy(new byte[]{1, 0, 0, 1, 0, 0, 0, 0, 0, 0}, 0, bArr3, bArr.length, 10);
        System.arraycopy(bArr2, 0, bArr3, bArr.length + 10, length);
        System.arraycopy(new byte[]{0, 1, 0, 1}, 0, bArr3, bArr.length + 10 + length, 4);
        return bArr3;
    }

    private void handleKeepAlive(Context context) {
        this.mPendingIntent = null;
        if (this.mConnectionActive) {
            this.mWakeLock.acquire();
            this.mDeviceStateReceiver.setKeepalive(true);
            this.mWorkerHandler.post(new AnonymousClass1(this, context));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public DatagramSocket openSocket() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.connect(InetAddress.getByName(this.mDNSServer), 53);
            return datagramSocket;
        } catch (Exception e8) {
            Log.e("OpenConnect", "KeepAlive: unexpected socket exception", e8);
            return null;
        }
    }

    private byte[] receiveDNSResponse(DatagramSocket datagramSocket, int i7) {
        byte[] bArr = new byte[KEYRecord.Flags.FLAG5];
        DatagramPacket datagramPacket = new DatagramPacket(bArr, KEYRecord.Flags.FLAG5);
        try {
            datagramSocket.setSoTimeout(i7);
            datagramSocket.receive(datagramPacket);
            return bArr;
        } catch (IOException e8) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleNext(Context context, int i7) {
        this.mPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_KEEPALIVE_ALARM), 201326592);
        ((AlarmManager) context.getSystemService("alarm")).set(0, System.currentTimeMillis() + i7, this.mPendingIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean sendDNSQuery(DatagramSocket datagramSocket) {
        Random random = new Random();
        byte[] bArr = {(byte) random.nextInt(256), (byte) random.nextInt(256)};
        byte[] buildDNSQuery = buildDNSQuery(bArr, this.mDNSHost);
        try {
            datagramSocket.send(new DatagramPacket(buildDNSQuery, buildDNSQuery.length));
            int i7 = 10000;
            boolean z7 = false;
            while (true) {
                boolean z8 = z7;
                byte[] receiveDNSResponse = receiveDNSResponse(datagramSocket, i7);
                if (receiveDNSResponse == null) {
                    if (z8) {
                        Log.w("OpenConnect", "KeepAlive: got reply with bad transaction ID");
                        return false;
                    }
                    Log.i("OpenConnect", "KeepAlive: no reply was received");
                    return false;
                }
                if (receiveDNSResponse[0] == bArr[0] && receiveDNSResponse[1] == bArr[1]) {
                    Log.d("OpenConnect", "KeepAlive: good reply from server");
                    return true;
                }
                i7 = 100;
                z7 = true;
            }
        } catch (IOException e8) {
            Log.w("OpenConnect", "KeepAlive: error sending DNS request", e8);
            return false;
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals(ACTION_KEEPALIVE_ALARM)) {
                handleKeepAlive(context);
            }
        } catch (Exception e8) {
            e8.printStackTrace();
        }
    }

    @SuppressLint({"InvalidWakeLockTag"})
    public void start(Context context) {
        if (this.mBaseDelayMs == 0) {
            return;
        }
        HandlerThread handlerThread = new HandlerThread("KeepAlive");
        handlerThread.start();
        this.mWorkerHandler = new Handler(handlerThread.getLooper());
        this.mMainHandler = new Handler();
        this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "KeepAlive");
        this.mConnectionActive = true;
        scheduleNext(context, this.mBaseDelayMs);
    }

    public void stop(Context context) {
        this.mConnectionActive = false;
        if (this.mPendingIntent != null) {
            ((AlarmManager) context.getSystemService("alarm")).cancel(this.mPendingIntent);
            this.mPendingIntent = null;
        }
        Handler handler = this.mWorkerHandler;
        if (handler != null) {
            handler.post(new Runnable(this) { // from class: app.openconnect.core.KeepAlive.2
                public final KeepAlive this$0;

                {
                    this.this$0 = this;
                }

                @Override // java.lang.Runnable
                public void run() {
                    Looper.myLooper().quit();
                }
            });
        }
    }
}
