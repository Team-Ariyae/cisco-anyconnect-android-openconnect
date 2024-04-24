package app.openconnect;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import io.github.inflationx.calligraphy3.BuildConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.xbill.DNS.KEYRecord;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/FileProvider.class */
public class FileProvider extends ContentProvider implements ContentProvider.PipeDataWriter<InputStream> {
    private File getFileFromURI(Uri uri) {
        String path = uri.getPath();
        String str = path;
        if (path.startsWith("/")) {
            str = path.replaceFirst("/", BuildConfig.FLAVOR);
        }
        if (str.matches("^[0-9a-z-.]*(dmp|dmp.log)$")) {
            return new File(getContext().getCacheDir(), str);
        }
        throw new FileNotFoundException("url not in expect format " + uri);
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "application/octet-stream";
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public AssetFileDescriptor openAssetFile(Uri uri, String str) {
        File fileFromURI = getFileFromURI(uri);
        try {
            return new AssetFileDescriptor(openPipeHelper(uri, null, null, new FileInputStream(fileFromURI), this), 0L, fileFromURI.length());
        } catch (IOException e8) {
            throw new FileNotFoundException("Unable to open minidump " + uri);
        }
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        try {
            File fileFromURI = getFileFromURI(uri);
            MatrixCursor matrixCursor = new MatrixCursor(strArr);
            Object[] objArr = new Object[strArr.length];
            int i7 = 0;
            for (String str3 : strArr) {
                if (str3.equals("_size")) {
                    objArr[i7] = Long.valueOf(fileFromURI.length());
                }
                if (str3.equals("_display_name")) {
                    objArr[i7] = fileFromURI.getName();
                }
                i7++;
            }
            matrixCursor.addRow(objArr);
            return matrixCursor;
        } catch (FileNotFoundException e8) {
            e8.printStackTrace();
            return null;
        }
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider.PipeDataWriter
    public void writeDataToPipe(ParcelFileDescriptor parcelFileDescriptor, Uri uri, String str, Bundle bundle, InputStream inputStream) {
        byte[] bArr = new byte[KEYRecord.Flags.FLAG2];
        FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
        while (true) {
            try {
                try {
                    int read = inputStream.read(bArr);
                    if (read >= 0) {
                        fileOutputStream.write(bArr, 0, read);
                    }
                } catch (Throwable th) {
                    try {
                        inputStream.close();
                    } catch (IOException e8) {
                    }
                    try {
                        fileOutputStream.close();
                    } catch (IOException e9) {
                    }
                    throw th;
                }
            } catch (IOException e10) {
                Log.i("OpenVPNFileProvider", "Failed transferring", e10);
            }
            try {
                break;
            } catch (IOException e11) {
            }
        }
        inputStream.close();
        try {
            fileOutputStream.close();
        } catch (IOException e12) {
        }
    }
}
