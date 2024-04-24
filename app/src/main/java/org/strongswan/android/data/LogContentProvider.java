package org.strongswan.android.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import androidx.activity.result.a;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import org.strongswan.android.logic.CharonVpnService;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/data/LogContentProvider.class */
public class LogContentProvider extends ContentProvider {
    private static final String AUTHORITY = "org.strongswan.android.content.logProvider";
    private static final long URI_VALIDITY = 1800000;
    private static ConcurrentHashMap<Uri, Long> mUris = new ConcurrentHashMap<>();
    private File mLogFile;

    public static Uri createContentUri() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            StringBuilder r7 = a.r("content://org.strongswan.android.content.logProvider/");
            r7.append(secureRandom.nextLong());
            Uri parse = Uri.parse(r7.toString());
            mUris.put(parse, Long.valueOf(SystemClock.uptimeMillis()));
            return parse;
        } catch (NoSuchAlgorithmException e8) {
            return null;
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "text/plain";
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mLogFile = new File(getContext().getFilesDir(), CharonVpnService.LOG_FILE);
        return true;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) {
        Long l7 = mUris.get(uri);
        if (l7 != null) {
            long uptimeMillis = SystemClock.uptimeMillis() - l7.longValue();
            if (uptimeMillis > 0 && uptimeMillis < URI_VALIDITY) {
                return ParcelFileDescriptor.open(this.mLogFile, 402653184);
            }
            mUris.remove(uri);
        }
        return super.openFile(uri, str);
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        MatrixCursor.RowBuilder newRow;
        Object valueOf;
        if (strArr == null || strArr.length < 1 || mUris.get(uri) == null) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(strArr, 1);
        if ("_display_name".equals(matrixCursor.getColumnName(0))) {
            newRow = matrixCursor.newRow();
            valueOf = CharonVpnService.LOG_FILE;
        } else {
            if (!"_size".equals(matrixCursor.getColumnName(0))) {
                return null;
            }
            newRow = matrixCursor.newRow();
            valueOf = Long.valueOf(this.mLogFile.length());
        }
        newRow.add(valueOf);
        return matrixCursor;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
