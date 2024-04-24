package org.strongswan.android.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/utils/SettingsWriter.class */
public class SettingsWriter {
    private final SettingsSection mTop = new SettingsSection();

    /* loaded from: TehShop-dex2jar.jar:org/strongswan/android/utils/SettingsWriter$SettingsSection.class */
    public class SettingsSection {
        public LinkedHashMap<String, SettingsSection> Sections;
        public LinkedHashMap<String, String> Settings;
        public final SettingsWriter this$0;

        private SettingsSection(SettingsWriter settingsWriter) {
            this.this$0 = settingsWriter;
            this.Settings = new LinkedHashMap<>();
            this.Sections = new LinkedHashMap<>();
        }
    }

    private String escapeValue(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private SettingsSection findOrCreateSection(String[] strArr) {
        SettingsSection settingsSection = this.mTop;
        int length = strArr.length;
        int i7 = 0;
        while (i7 < length) {
            String str = strArr[i7];
            SettingsSection settingsSection2 = settingsSection.Sections.get(str);
            SettingsSection settingsSection3 = settingsSection2;
            if (settingsSection2 == null) {
                settingsSection3 = new SettingsSection();
                settingsSection.Sections.put(str, settingsSection3);
            }
            i7++;
            settingsSection = settingsSection3;
        }
        return settingsSection;
    }

    private void serializeSection(SettingsSection settingsSection, StringBuilder sb) {
        for (Map.Entry<String, String> entry : settingsSection.Settings.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            if (entry.getValue() != null) {
                sb.append("\"");
                sb.append(escapeValue(entry.getValue()));
                sb.append("\"");
            }
            sb.append('\n');
        }
        for (Map.Entry<String, SettingsSection> entry2 : settingsSection.Sections.entrySet()) {
            sb.append(entry2.getKey());
            sb.append(" {\n");
            serializeSection(entry2.getValue(), sb);
            sb.append("}\n");
        }
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        serializeSection(this.mTop, sb);
        return sb.toString();
    }

    public SettingsWriter setValue(String str, Boolean bool) {
        return setValue(str, bool == null ? null : bool.booleanValue() ? "1" : "0");
    }

    public SettingsWriter setValue(String str, Integer num) {
        return setValue(str, num == null ? null : num.toString());
    }

    public SettingsWriter setValue(String str, String str2) {
        Pattern compile = Pattern.compile("[^#{}=\"\\n\\t ]+");
        if (str != null && compile.matcher(str).matches()) {
            String[] split = str.split("\\.");
            findOrCreateSection((String[]) Arrays.copyOfRange(split, 0, split.length - 1)).Settings.put(split[split.length - 1], str2);
        }
        return this;
    }
}
