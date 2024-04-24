package org.strongswan.android.logic.imc;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: TehShop-dex2jar.jar:org/strongswan/android/logic/imc/RemediationInstruction.class */
public class RemediationInstruction implements Parcelable {
    public static final Parcelable.Creator<RemediationInstruction> CREATOR = new Parcelable.Creator<RemediationInstruction>() { // from class: org.strongswan.android.logic.imc.RemediationInstruction.1
        @Override // android.os.Parcelable.Creator
        public RemediationInstruction createFromParcel(Parcel parcel) {
            return new RemediationInstruction(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public RemediationInstruction[] newArray(int i7) {
            return new RemediationInstruction[i7];
        }
    };
    private String mDescription;
    private String mHeader;
    private final List<String> mItems;
    private String mTitle;

    private RemediationInstruction() {
        this.mItems = new LinkedList();
    }

    private RemediationInstruction(Parcel parcel) {
        LinkedList linkedList = new LinkedList();
        this.mItems = linkedList;
        this.mTitle = parcel.readString();
        this.mDescription = parcel.readString();
        this.mHeader = parcel.readString();
        parcel.readStringList(linkedList);
    }

    private void addItem(String str) {
        this.mItems.add(str);
    }

    public static List<RemediationInstruction> fromXml(String str) {
        LinkedList linkedList = new LinkedList();
        XmlPullParser newPullParser = Xml.newPullParser();
        try {
            newPullParser.setInput(new StringReader(str));
            newPullParser.nextTag();
            readInstructions(newPullParser, linkedList);
        } catch (IOException e8) {
            e8.printStackTrace();
        } catch (XmlPullParserException e9) {
            e9.printStackTrace();
        }
        return linkedList;
    }

    private static void readInstruction(XmlPullParser xmlPullParser, RemediationInstruction remediationInstruction) {
        xmlPullParser.require(2, null, "instruction");
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("title")) {
                    remediationInstruction.setTitle(xmlPullParser.nextText());
                } else if (name.equals("description")) {
                    remediationInstruction.setDescription(xmlPullParser.nextText());
                } else if (name.equals("itemsheader")) {
                    remediationInstruction.setHeader(xmlPullParser.nextText());
                } else if (name.equals("items")) {
                    readItems(xmlPullParser, remediationInstruction);
                } else {
                    skipTag(xmlPullParser);
                }
            }
        }
    }

    private static void readInstructions(XmlPullParser xmlPullParser, List<RemediationInstruction> list) {
        xmlPullParser.require(2, null, "remediationinstructions");
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("instruction")) {
                    RemediationInstruction remediationInstruction = new RemediationInstruction();
                    readInstruction(xmlPullParser, remediationInstruction);
                    list.add(remediationInstruction);
                } else {
                    skipTag(xmlPullParser);
                }
            }
        }
    }

    private static void readItems(XmlPullParser xmlPullParser, RemediationInstruction remediationInstruction) {
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("item")) {
                    remediationInstruction.addItem(xmlPullParser.nextText());
                } else {
                    skipTag(xmlPullParser);
                }
            }
        }
    }

    private void setDescription(String str) {
        this.mDescription = str;
    }

    private void setHeader(String str) {
        this.mHeader = str;
    }

    private void setTitle(String str) {
        this.mTitle = str;
    }

    private static void skipTag(XmlPullParser xmlPullParser) {
        xmlPullParser.require(2, null, null);
        int i7 = 1;
        while (i7 != 0) {
            int next = xmlPullParser.next();
            if (next == 2) {
                i7++;
            } else if (next == 3) {
                i7--;
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getHeader() {
        return this.mHeader;
    }

    public List<String> getItems() {
        return Collections.unmodifiableList(this.mItems);
    }

    public String getTitle() {
        return this.mTitle;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i7) {
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mDescription);
        parcel.writeString(this.mHeader);
        parcel.writeStringList(this.mItems);
    }
}
