package org.strongswan.android.logic;

import androidx.annotation.Keep;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.xbill.DNS.KEYRecord;

@Keep
/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/SimpleFetcher.class */
public class SimpleFetcher {
    private static boolean mDisabled;
    private static ExecutorService mExecutor = Executors.newCachedThreadPool();
    private static Object mLock = new Object();
    private static ArrayList<Future> mFutures = new ArrayList<>();

    public static void disable() {
        synchronized (mLock) {
            mDisabled = true;
            Iterator<Future> it = mFutures.iterator();
            while (it.hasNext()) {
                it.next().cancel(true);
            }
        }
    }

    public static void enable() {
        synchronized (mLock) {
            mDisabled = false;
        }
    }

    public static byte[] fetch(final String str, final byte[] bArr, final String str2) {
        synchronized (mLock) {
            if (mDisabled) {
                return null;
            }
            Future submit = mExecutor.submit(new Callable(str, str2, bArr) { // from class: org.strongswan.android.logic.a

                /* renamed from: a, reason: collision with root package name */
                public final String f5255a;

                /* renamed from: b, reason: collision with root package name */
                public final String f5256b;
                public final byte[] c;

                {
                    this.f5255a = str;
                    this.f5256b = str2;
                    this.c = bArr;
                }

                @Override // java.util.concurrent.Callable
                public final Object call() {
                    byte[] lambda$fetch$0;
                    lambda$fetch$0 = SimpleFetcher.lambda$fetch$0(this.f5255a, this.f5256b, this.c);
                    return lambda$fetch$0;
                }
            });
            mFutures.add(submit);
            try {
                byte[] bArr2 = (byte[]) submit.get(10000L, TimeUnit.MILLISECONDS);
                synchronized (mLock) {
                    mFutures.remove(submit);
                }
                return bArr2;
            } catch (InterruptedException | CancellationException | ExecutionException | TimeoutException e8) {
                synchronized (mLock) {
                    mFutures.remove(submit);
                    return null;
                }
            } catch (Throwable th) {
                synchronized (mLock) {
                    mFutures.remove(submit);
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ byte[] lambda$fetch$0(String str, String str2, byte[] bArr) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(f3.a.PRIORITY_HIGHEST);
        httpURLConnection.setReadTimeout(f3.a.PRIORITY_HIGHEST);
        if (str2 != null) {
            try {
                httpURLConnection.setRequestProperty("Content-Type", str2);
            } catch (SocketTimeoutException e8) {
                httpURLConnection.disconnect();
                return null;
            } catch (Throwable th) {
                httpURLConnection.disconnect();
                throw th;
            }
        }
        if (bArr != null) {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setFixedLengthStreamingMode(bArr.length);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            bufferedOutputStream.write(bArr);
            bufferedOutputStream.close();
        }
        byte[] streamToArray = streamToArray(httpURLConnection.getInputStream());
        httpURLConnection.disconnect();
        return streamToArray;
    }

    private static byte[] streamToArray(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[KEYRecord.Flags.FLAG5];
        while (true) {
            try {
                try {
                    int read = inputStream.read(bArr);
                    if (read == -1) {
                        return byteArrayOutputStream.toByteArray();
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                } catch (IOException e8) {
                    e8.printStackTrace();
                    inputStream.close();
                    return null;
                }
            } finally {
                inputStream.close();
            }
        }
    }
}
