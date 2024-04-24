package org.strongswan.android.data;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/data/VpnProfile.class */
public class VpnProfile implements Cloneable {
    public static final int FLAGS_DISABLE_CRL = 2;
    public static final int FLAGS_DISABLE_OCSP = 4;
    public static final int FLAGS_RSA_PSS = 16;
    public static final int FLAGS_STRICT_REVOCATION = 8;
    public static final int FLAGS_SUPPRESS_CERT_REQS = 1;
    public static final int SPLIT_TUNNELING_BLOCK_IPV4 = 1;
    public static final int SPLIT_TUNNELING_BLOCK_IPV6 = 2;
    private String mCertificate;
    private String mDnsServers;
    private String mEspProposal;
    private String mExcludedSubnets;
    private Integer mFlags;
    private String mGateway;
    private String mIkeProposal;
    private String mIncludedSubnets;
    private String mLocalId;
    private Integer mMTU;
    private Integer mNATKeepAlive;
    private String mName;
    private String mPassword;
    private Integer mPort;
    private String mRemoteId;
    private String mSelectedApps;
    private Integer mSplitTunneling;
    private String mUserCertificate;
    private String mUsername;
    private VpnType mVpnType;
    private SelectedAppsHandling mSelectedAppsHandling = SelectedAppsHandling.SELECTED_APPS_DISABLE;
    private long mId = -1;
    private UUID mUUID = UUID.randomUUID();

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/data/VpnProfile$SelectedAppsHandling.class */
    public enum SelectedAppsHandling {
        SELECTED_APPS_DISABLE(0),
        SELECTED_APPS_EXCLUDE(1),
        SELECTED_APPS_ONLY(2);

        private Integer mValue;

        SelectedAppsHandling(int i7) {
            this.mValue = Integer.valueOf(i7);
        }

        public Integer getValue() {
            return this.mValue;
        }
    }

    public VpnProfile clone() {
        try {
            return (VpnProfile) super.clone();
        } catch (CloneNotSupportedException e8) {
            throw new AssertionError();
        }
    }

    public boolean equals(Object obj) {
        boolean z7 = false;
        if (obj != null) {
            z7 = false;
            if (obj instanceof VpnProfile) {
                VpnProfile vpnProfile = (VpnProfile) obj;
                if (this.mUUID != null && vpnProfile.getUUID() != null) {
                    return this.mUUID.equals(vpnProfile.getUUID());
                }
                z7 = false;
                if (this.mId == vpnProfile.getId()) {
                    z7 = true;
                }
            }
        }
        return z7;
    }

    public String getCertificateAlias() {
        return this.mCertificate;
    }

    public String getDnsServers() {
        return this.mDnsServers;
    }

    public String getEspProposal() {
        return this.mEspProposal;
    }

    public String getExcludedSubnets() {
        return this.mExcludedSubnets;
    }

    public Integer getFlags() {
        Integer num = this.mFlags;
        return Integer.valueOf(num == null ? 0 : num.intValue());
    }

    public String getGateway() {
        return this.mGateway;
    }

    public long getId() {
        return this.mId;
    }

    public String getIkeProposal() {
        return this.mIkeProposal;
    }

    public String getIncludedSubnets() {
        return this.mIncludedSubnets;
    }

    public String getLocalId() {
        return this.mLocalId;
    }

    public Integer getMTU() {
        return this.mMTU;
    }

    public Integer getNATKeepAlive() {
        return this.mNATKeepAlive;
    }

    public String getName() {
        return this.mName;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public Integer getPort() {
        return this.mPort;
    }

    public String getRemoteId() {
        return this.mRemoteId;
    }

    public String getSelectedApps() {
        return this.mSelectedApps;
    }

    public SelectedAppsHandling getSelectedAppsHandling() {
        return this.mSelectedAppsHandling;
    }

    public SortedSet<String> getSelectedAppsSet() {
        TreeSet treeSet = new TreeSet();
        if (!TextUtils.isEmpty(this.mSelectedApps)) {
            treeSet.addAll(Arrays.asList(this.mSelectedApps.split("\\s+")));
        }
        return treeSet;
    }

    public Integer getSplitTunneling() {
        return this.mSplitTunneling;
    }

    public UUID getUUID() {
        return this.mUUID;
    }

    public String getUserCertificateAlias() {
        return this.mUserCertificate;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public VpnType getVpnType() {
        return this.mVpnType;
    }

    public void setCertificateAlias(String str) {
        this.mCertificate = str;
    }

    public void setDnsServers(String str) {
        this.mDnsServers = str;
    }

    public void setEspProposal(String str) {
        this.mEspProposal = str;
    }

    public void setExcludedSubnets(String str) {
        this.mExcludedSubnets = str;
    }

    public void setFlags(Integer num) {
        this.mFlags = num;
    }

    public void setGateway(String str) {
        this.mGateway = str;
    }

    public void setId(long j7) {
        this.mId = j7;
    }

    public void setIkeProposal(String str) {
        this.mIkeProposal = str;
    }

    public void setIncludedSubnets(String str) {
        this.mIncludedSubnets = str;
    }

    public void setLocalId(String str) {
        this.mLocalId = str;
    }

    public void setMTU(Integer num) {
        this.mMTU = num;
    }

    public void setNATKeepAlive(Integer num) {
        this.mNATKeepAlive = num;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public void setPassword(String str) {
        this.mPassword = str;
    }

    public void setPort(Integer num) {
        this.mPort = num;
    }

    public void setRemoteId(String str) {
        this.mRemoteId = str;
    }

    public void setSelectedApps(String str) {
        this.mSelectedApps = str;
    }

    public void setSelectedApps(SortedSet<String> sortedSet) {
        this.mSelectedApps = sortedSet.size() > 0 ? TextUtils.join(" ", sortedSet) : null;
    }

    public void setSelectedAppsHandling(Integer num) {
        this.mSelectedAppsHandling = SelectedAppsHandling.SELECTED_APPS_DISABLE;
        for (SelectedAppsHandling selectedAppsHandling : SelectedAppsHandling.values()) {
            if (selectedAppsHandling.mValue.equals(num)) {
                this.mSelectedAppsHandling = selectedAppsHandling;
                return;
            }
        }
    }

    public void setSelectedAppsHandling(SelectedAppsHandling selectedAppsHandling) {
        this.mSelectedAppsHandling = selectedAppsHandling;
    }

    public void setSplitTunneling(Integer num) {
        this.mSplitTunneling = num;
    }

    public void setUUID(UUID uuid) {
        this.mUUID = uuid;
    }

    public void setUserCertificateAlias(String str) {
        this.mUserCertificate = str;
    }

    public void setUsername(String str) {
        this.mUsername = str;
    }

    public void setVpnType(VpnType vpnType) {
        this.mVpnType = vpnType;
    }

    public String toString() {
        return this.mName;
    }
}
