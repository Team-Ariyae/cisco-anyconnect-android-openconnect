package app.openconnect.core;

import android.content.Context;
import android.util.Log;
import androidx.activity.result.a;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: TehShop-dex2jar.jar:app/openconnect/core/AssetExtractor.class */
public class AssetExtractor {
    private static final int BUFLEN = 65536;
    public static final int FL_FORCE = 1;
    public static final int FL_NOEXEC = 2;
    public static final String TAG = "OpenConnect";

    private static long crc32(File file) {
        FileInputStream fileInputStream = new FileInputStream(file);
        CRC32 crc32 = new CRC32();
        byte[] bArr = new byte[BUFLEN];
        while (true) {
            int read = fileInputStream.read(bArr);
            if (read == -1) {
                fileInputStream.close();
                return crc32.getValue();
            }
            crc32.update(bArr, 0, read);
        }
    }

    public static boolean extractAll(Context context) {
        return extractAll(context, 0, null);
    }

    public static boolean extractAll(Context context, int i7, String str) {
        String str2;
        StringBuilder r7 = a.r("assets/raw/");
        r7.append(getArch());
        String sb = r7.toString();
        if (str == null) {
            str2 = context.getFilesDir().getAbsolutePath();
        } else {
            String str3 = File.separator;
            str2 = str;
            if (!str.endsWith(str3)) {
                str2 = a.m(str, str3);
            }
        }
        try {
            ZipFile zipFile = new ZipFile(context.getPackageCodePath());
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry nextElement = entries.nextElement();
                if (!nextElement.isDirectory()) {
                    String name = nextElement.getName();
                    for (int i8 = 0; i8 < 2; i8++) {
                        String str4 = new String[]{"assets/raw/noarch", sb}[i8];
                        if (name.startsWith(str4)) {
                            String str5 = str2 + name.substring(str4.length());
                            File file = new File(str5);
                            if ((i7 & 1) == 0 && file.exists() && crc32(file) == nextElement.getCrc()) {
                                Log.d("OpenConnect", "AssetExtractor: skipping " + str5);
                                name = str5;
                            } else {
                                Log.i("OpenConnect", "AssetExtractor: writing " + str5);
                                writeStream(zipFile.getInputStream(nextElement), file);
                                name = str5;
                                if ((i7 & 2) == 0) {
                                    file.setExecutable(true);
                                    name = str5;
                                }
                            }
                        }
                    }
                }
            }
            zipFile.close();
            return true;
        } catch (IOException e8) {
            Log.e("OpenConnect", "AssetExtractor: caught exception", e8);
            return false;
        }
    }

    private static String getArch() {
        String property = System.getProperty("os.arch");
        return (property.contains("x86") || property.contains("i686") || property.contains("i386")) ? "x86" : property.contains("mips") ? "mips" : "armeabi";
    }

    private static String readAndClose(Reader reader) {
        StringWriter stringWriter = new StringWriter();
        char[] cArr = new char[BUFLEN];
        while (true) {
            int read = reader.read(cArr);
            if (read == -1) {
                reader.close();
                return stringWriter.toString();
            }
            stringWriter.write(cArr, 0, read);
        }
    }

    public static String readString(Context context, String str) {
        try {
            return readAndClose(new BufferedReader(new InputStreamReader(context.getAssets().open(str), "UTF-8")));
        } catch (IOException e8) {
            Log.e("OpenConnect", "AssetExtractor: readString exception", e8);
            return null;
        }
    }

    public static String readStringFromFile(String str) {
        try {
            return readAndClose(new BufferedReader(new FileReader(str)));
        } catch (IOException e8) {
            Log.e("OpenConnect", "AssetExtractor: readString exception", e8);
            return null;
        }
    }

    private static void writeStream(InputStream inputStream, File file) {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bArr = new byte[BUFLEN];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                fileOutputStream.close();
                return;
            }
            fileOutputStream.write(bArr, 0, read);
        }
    }
}
