package org.strongswan.android.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import androidx.activity.result.a;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/data/VpnProfileDataSource.class */
public class VpnProfileDataSource {
    private static final String DATABASE_NAME = "strongswan.db";
    private static final int DATABASE_VERSION = 17;
    private static final String TABLE_VPNPROFILE = "vpnprofile";
    private static final String TAG = "VpnProfileDataSource";
    private final Context mContext;
    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDbHelper;
    public static final String KEY_ID = "_id";
    public static final String KEY_UUID = "_uuid";
    public static final String KEY_NAME = "name";
    public static final String KEY_GATEWAY = "gateway";
    public static final String KEY_VPN_TYPE = "vpn_type";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CERTIFICATE = "certificate";
    public static final String KEY_USER_CERTIFICATE = "user_certificate";
    public static final String KEY_MTU = "mtu";
    public static final String KEY_PORT = "port";
    public static final String KEY_SPLIT_TUNNELING = "split_tunneling";
    public static final String KEY_LOCAL_ID = "local_id";
    public static final String KEY_REMOTE_ID = "remote_id";
    public static final String KEY_EXCLUDED_SUBNETS = "excluded_subnets";
    public static final String KEY_INCLUDED_SUBNETS = "included_subnets";
    public static final String KEY_SELECTED_APPS = "selected_apps";
    public static final String KEY_SELECTED_APPS_LIST = "selected_apps_list";
    public static final String KEY_NAT_KEEPALIVE = "nat_keepalive";
    public static final String KEY_FLAGS = "flags";
    public static final String KEY_IKE_PROPOSAL = "ike_proposal";
    public static final String KEY_ESP_PROPOSAL = "esp_proposal";
    public static final String KEY_DNS_SERVERS = "dns_servers";
    public static final DbColumn[] COLUMNS = {new DbColumn(KEY_ID, "INTEGER PRIMARY KEY AUTOINCREMENT", 1), new DbColumn(KEY_UUID, "TEXT UNIQUE", 9), new DbColumn(KEY_NAME, "TEXT NOT NULL", 1), new DbColumn(KEY_GATEWAY, "TEXT NOT NULL", 1), new DbColumn(KEY_VPN_TYPE, "TEXT NOT NULL", 3), new DbColumn(KEY_USERNAME, "TEXT", 1), new DbColumn(KEY_PASSWORD, "TEXT", 1), new DbColumn(KEY_CERTIFICATE, "TEXT", 1), new DbColumn(KEY_USER_CERTIFICATE, "TEXT", 2), new DbColumn(KEY_MTU, "INTEGER", 5), new DbColumn(KEY_PORT, "INTEGER", 5), new DbColumn(KEY_SPLIT_TUNNELING, "INTEGER", 7), new DbColumn(KEY_LOCAL_ID, "TEXT", 8), new DbColumn(KEY_REMOTE_ID, "TEXT", 8), new DbColumn(KEY_EXCLUDED_SUBNETS, "TEXT", 10), new DbColumn(KEY_INCLUDED_SUBNETS, "TEXT", 11), new DbColumn(KEY_SELECTED_APPS, "INTEGER", 12), new DbColumn(KEY_SELECTED_APPS_LIST, "TEXT", 12), new DbColumn(KEY_NAT_KEEPALIVE, "INTEGER", 13), new DbColumn(KEY_FLAGS, "INTEGER", 14), new DbColumn(KEY_IKE_PROPOSAL, "TEXT", 15), new DbColumn(KEY_ESP_PROPOSAL, "TEXT", 15), new DbColumn(KEY_DNS_SERVERS, "TEXT", 17)};
    private static final String[] ALL_COLUMNS = getColumns(17);

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/data/VpnProfileDataSource$DatabaseHelper.class */
    public static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, VpnProfileDataSource.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 17);
        }

        private void updateColumns(SQLiteDatabase sQLiteDatabase, int i7) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile RENAME TO tmp_vpnprofile;");
                sQLiteDatabase.execSQL(VpnProfileDataSource.getDatabaseCreate(i7));
                StringBuilder sb = new StringBuilder("INSERT INTO vpnprofile SELECT ");
                SQLiteQueryBuilder.appendColumns(sb, VpnProfileDataSource.getColumns(i7));
                sb.append(" FROM tmp_vpnprofile;");
                sQLiteDatabase.execSQL(sb.toString());
                sQLiteDatabase.execSQL("DROP TABLE tmp_vpnprofile;");
                sQLiteDatabase.setTransactionSuccessful();
            } finally {
                sQLiteDatabase.endTransaction();
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(VpnProfileDataSource.getDatabaseCreate(17));
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i7, int i8) {
            Log.w(VpnProfileDataSource.TAG, "Upgrading database from version " + i7 + " to " + i8);
            if (i7 < 2) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD user_certificate TEXT;");
            }
            if (i7 < 3) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD vpn_type TEXT DEFAULT '';");
            }
            if (i7 < 4) {
                updateColumns(sQLiteDatabase, 4);
            }
            if (i7 < 5) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD mtu INTEGER;");
            }
            if (i7 < 6) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD port INTEGER;");
            }
            if (i7 < 7) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD split_tunneling INTEGER;");
            }
            if (i7 < 8) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD local_id TEXT;");
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD remote_id TEXT;");
            }
            if (i7 < 9) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD _uuid TEXT;");
                updateColumns(sQLiteDatabase, 9);
            }
            if (i7 < 10) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD excluded_subnets TEXT;");
            }
            if (i7 < 11) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD included_subnets TEXT;");
            }
            if (i7 < 12) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD selected_apps INTEGER;");
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD selected_apps_list TEXT;");
            }
            if (i7 < 13) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD nat_keepalive INTEGER;");
            }
            if (i7 < 14) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD flags INTEGER;");
            }
            if (i7 < 15) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD ike_proposal TEXT;");
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD esp_proposal TEXT;");
            }
            if (i7 < 16) {
                sQLiteDatabase.beginTransaction();
                try {
                    Cursor query = sQLiteDatabase.query(VpnProfileDataSource.TABLE_VPNPROFILE, VpnProfileDataSource.getColumns(16), "_uuid is NULL", null, null, null, null);
                    query.moveToFirst();
                    while (!query.isAfterLast()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(VpnProfileDataSource.KEY_UUID, UUID.randomUUID().toString());
                        sQLiteDatabase.update(VpnProfileDataSource.TABLE_VPNPROFILE, contentValues, "_id = " + query.getLong(query.getColumnIndex(VpnProfileDataSource.KEY_ID)), null);
                        query.moveToNext();
                    }
                    query.close();
                    sQLiteDatabase.setTransactionSuccessful();
                } finally {
                    sQLiteDatabase.endTransaction();
                }
            }
            if (i7 < 17) {
                sQLiteDatabase.execSQL("ALTER TABLE vpnprofile ADD dns_servers TEXT;");
            }
        }
    }

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/data/VpnProfileDataSource$DbColumn.class */
    public static class DbColumn {
        public final String Name;
        public final Integer Since;
        public final String Type;

        public DbColumn(String str, String str2, Integer num) {
            this.Name = str;
            this.Type = str2;
            this.Since = num;
        }
    }

    public VpnProfileDataSource(Context context) {
        this.mContext = context;
    }

    private ContentValues ContentValuesFromVpnProfile(VpnProfile vpnProfile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_UUID, vpnProfile.getUUID().toString());
        contentValues.put(KEY_NAME, vpnProfile.getName());
        contentValues.put(KEY_GATEWAY, vpnProfile.getGateway());
        contentValues.put(KEY_VPN_TYPE, vpnProfile.getVpnType().getIdentifier());
        contentValues.put(KEY_USERNAME, vpnProfile.getUsername());
        contentValues.put(KEY_PASSWORD, vpnProfile.getPassword());
        contentValues.put(KEY_CERTIFICATE, vpnProfile.getCertificateAlias());
        contentValues.put(KEY_USER_CERTIFICATE, vpnProfile.getUserCertificateAlias());
        contentValues.put(KEY_MTU, vpnProfile.getMTU());
        contentValues.put(KEY_PORT, vpnProfile.getPort());
        contentValues.put(KEY_SPLIT_TUNNELING, vpnProfile.getSplitTunneling());
        contentValues.put(KEY_LOCAL_ID, vpnProfile.getLocalId());
        contentValues.put(KEY_REMOTE_ID, vpnProfile.getRemoteId());
        contentValues.put(KEY_EXCLUDED_SUBNETS, vpnProfile.getExcludedSubnets());
        contentValues.put(KEY_INCLUDED_SUBNETS, vpnProfile.getIncludedSubnets());
        contentValues.put(KEY_SELECTED_APPS, vpnProfile.getSelectedAppsHandling().getValue());
        contentValues.put(KEY_SELECTED_APPS_LIST, vpnProfile.getSelectedApps());
        contentValues.put(KEY_NAT_KEEPALIVE, vpnProfile.getNATKeepAlive());
        contentValues.put(KEY_FLAGS, vpnProfile.getFlags());
        contentValues.put(KEY_IKE_PROPOSAL, vpnProfile.getIkeProposal());
        contentValues.put(KEY_ESP_PROPOSAL, vpnProfile.getEspProposal());
        contentValues.put(KEY_DNS_SERVERS, vpnProfile.getDnsServers());
        return contentValues;
    }

    private VpnProfile VpnProfileFromCursor(Cursor cursor) {
        VpnProfile vpnProfile = new VpnProfile();
        vpnProfile.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        vpnProfile.setUUID(UUID.fromString(cursor.getString(cursor.getColumnIndex(KEY_UUID))));
        vpnProfile.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        vpnProfile.setGateway(cursor.getString(cursor.getColumnIndex(KEY_GATEWAY)));
        vpnProfile.setVpnType(VpnType.fromIdentifier(cursor.getString(cursor.getColumnIndex(KEY_VPN_TYPE))));
        vpnProfile.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
        vpnProfile.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
        vpnProfile.setCertificateAlias(cursor.getString(cursor.getColumnIndex(KEY_CERTIFICATE)));
        vpnProfile.setUserCertificateAlias(cursor.getString(cursor.getColumnIndex(KEY_USER_CERTIFICATE)));
        vpnProfile.setMTU(getInt(cursor, cursor.getColumnIndex(KEY_MTU)));
        vpnProfile.setPort(getInt(cursor, cursor.getColumnIndex(KEY_PORT)));
        vpnProfile.setSplitTunneling(getInt(cursor, cursor.getColumnIndex(KEY_SPLIT_TUNNELING)));
        vpnProfile.setLocalId(cursor.getString(cursor.getColumnIndex(KEY_LOCAL_ID)));
        vpnProfile.setRemoteId(cursor.getString(cursor.getColumnIndex(KEY_REMOTE_ID)));
        vpnProfile.setExcludedSubnets(cursor.getString(cursor.getColumnIndex(KEY_EXCLUDED_SUBNETS)));
        vpnProfile.setIncludedSubnets(cursor.getString(cursor.getColumnIndex(KEY_INCLUDED_SUBNETS)));
        vpnProfile.setSelectedAppsHandling(getInt(cursor, cursor.getColumnIndex(KEY_SELECTED_APPS)));
        vpnProfile.setSelectedApps(cursor.getString(cursor.getColumnIndex(KEY_SELECTED_APPS_LIST)));
        vpnProfile.setNATKeepAlive(getInt(cursor, cursor.getColumnIndex(KEY_NAT_KEEPALIVE)));
        vpnProfile.setFlags(getInt(cursor, cursor.getColumnIndex(KEY_FLAGS)));
        vpnProfile.setIkeProposal(cursor.getString(cursor.getColumnIndex(KEY_IKE_PROPOSAL)));
        vpnProfile.setEspProposal(cursor.getString(cursor.getColumnIndex(KEY_ESP_PROPOSAL)));
        vpnProfile.setDnsServers(cursor.getString(cursor.getColumnIndex(KEY_DNS_SERVERS)));
        return vpnProfile;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String[] getColumns(int i7) {
        ArrayList arrayList = new ArrayList();
        for (DbColumn dbColumn : COLUMNS) {
            if (dbColumn.Since.intValue() <= i7) {
                arrayList.add(dbColumn.Name);
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getDatabaseCreate(int i7) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(TABLE_VPNPROFILE);
        sb.append(" (");
        DbColumn[] dbColumnArr = COLUMNS;
        int length = dbColumnArr.length;
        boolean z7 = true;
        int i8 = 0;
        while (i8 < length) {
            DbColumn dbColumn = dbColumnArr[i8];
            boolean z8 = z7;
            if (dbColumn.Since.intValue() <= i7) {
                if (!z7) {
                    sb.append(",");
                }
                sb.append(dbColumn.Name);
                sb.append(" ");
                sb.append(dbColumn.Type);
                z8 = false;
            }
            i8++;
            z7 = z8;
        }
        sb.append(");");
        return sb.toString();
    }

    private Integer getInt(Cursor cursor, int i7) {
        return cursor.isNull(i7) ? null : Integer.valueOf(cursor.getInt(i7));
    }

    public void close() {
        DatabaseHelper databaseHelper = this.mDbHelper;
        if (databaseHelper != null) {
            databaseHelper.close();
            this.mDbHelper = null;
        }
    }

    public boolean deleteVpnProfile(VpnProfile vpnProfile) {
        long id = vpnProfile.getId();
        SQLiteDatabase sQLiteDatabase = this.mDatabase;
        StringBuilder sb = new StringBuilder();
        sb.append("_id = ");
        sb.append(id);
        return sQLiteDatabase.delete(TABLE_VPNPROFILE, sb.toString(), null) > 0;
    }

    public List<VpnProfile> getAllVpnProfiles() {
        ArrayList arrayList = new ArrayList();
        Cursor query = this.mDatabase.query(TABLE_VPNPROFILE, ALL_COLUMNS, null, null, null, null, null);
        query.moveToFirst();
        while (!query.isAfterLast()) {
            arrayList.add(VpnProfileFromCursor(query));
            query.moveToNext();
        }
        query.close();
        return arrayList;
    }

    public VpnProfile getVpnProfile(long j7) {
        Cursor query = this.mDatabase.query(TABLE_VPNPROFILE, ALL_COLUMNS, "_id=" + j7, null, null, null, null);
        VpnProfile VpnProfileFromCursor = query.moveToFirst() ? VpnProfileFromCursor(query) : null;
        query.close();
        return VpnProfileFromCursor;
    }

    public VpnProfile getVpnProfile(String str) {
        if (str == null) {
            return null;
        }
        try {
            return getVpnProfile(UUID.fromString(str));
        } catch (IllegalArgumentException e8) {
            e8.printStackTrace();
            return null;
        }
    }

    public VpnProfile getVpnProfile(UUID uuid) {
        SQLiteDatabase sQLiteDatabase = this.mDatabase;
        String[] strArr = ALL_COLUMNS;
        StringBuilder r7 = a.r("_uuid='");
        r7.append(uuid.toString());
        r7.append("'");
        Cursor query = sQLiteDatabase.query(TABLE_VPNPROFILE, strArr, r7.toString(), null, null, null, null);
        VpnProfile VpnProfileFromCursor = query.moveToFirst() ? VpnProfileFromCursor(query) : null;
        query.close();
        return VpnProfileFromCursor;
    }

    public VpnProfile insertProfile(VpnProfile vpnProfile) {
        long insert = this.mDatabase.insert(TABLE_VPNPROFILE, null, ContentValuesFromVpnProfile(vpnProfile));
        if (insert == -1) {
            return null;
        }
        vpnProfile.setId(insert);
        return vpnProfile;
    }

    public VpnProfileDataSource open() {
        if (this.mDbHelper == null) {
            DatabaseHelper databaseHelper = new DatabaseHelper(this.mContext);
            this.mDbHelper = databaseHelper;
            this.mDatabase = databaseHelper.getWritableDatabase();
        }
        return this;
    }

    public boolean updateVpnProfile(VpnProfile vpnProfile) {
        long id = vpnProfile.getId();
        ContentValues ContentValuesFromVpnProfile = ContentValuesFromVpnProfile(vpnProfile);
        SQLiteDatabase sQLiteDatabase = this.mDatabase;
        StringBuilder sb = new StringBuilder();
        sb.append("_id = ");
        sb.append(id);
        return sQLiteDatabase.update(TABLE_VPNPROFILE, ContentValuesFromVpnProfile, sb.toString(), null) > 0;
    }
}
