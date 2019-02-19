package de.proneucon.learnnewquizapp;

import android.os.Parcel;
import android.os.Parcelable;
/* um das Parcelable zu implementieren müssen wir auf über "Fragen" die Lampe klicken und die Parcelable implemenieren lassen */

public class Fragen implements Parcelable {

    //Finale Konstanten für die Schwierigkeit  !public
    public static final String SCHWIERIGKEIT_LEICHT = "Leicht";
    public static final String SCHWIERIGKEIT_MITTEL = "Mittel";
    public static final String SCHWIERIGKEIT_SCHWER = "Schwer";



    private String fragen;
    private String antwort1;
    private String antwort2;
    private String antwort3;
    private String antwort4;
    private int antwortNr;

    // Variable für die Schwierigkeit
    private String schwierigkeit;

    public Fragen(){

    }

    public Fragen(String fragen, String antwort1, String antwort2, String antwort3, String antwort4, int antwortNr , String schwierigkeit) {
        this.fragen = fragen;
        this.antwort1 = antwort1;
        this.antwort2 = antwort2;
        this.antwort3 = antwort3;
        this.antwort4 = antwort4;
        this.antwortNr = antwortNr;
        this.schwierigkeit = schwierigkeit;
    }


    // implementiert um die Informationen in einem Bundle zu speichern!
    protected Fragen(Parcel in) {
        fragen = in.readString();
        antwort1 = in.readString();
        antwort2 = in.readString();
        antwort3 = in.readString();
        antwort4 = in.readString();
        antwortNr = in.readInt();
        //anpassen der schwierigkeit
        schwierigkeit = in.readString();
    }
    // implementiert um die Informationen in einem Bundle zu speichern!
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fragen);
        dest.writeString(antwort1);
        dest.writeString(antwort2);
        dest.writeString(antwort3);
        dest.writeString(antwort4);
        dest.writeInt(antwortNr);
        dest.writeString(schwierigkeit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Fragen> CREATOR = new Creator<Fragen>() {
        @Override
        public Fragen createFromParcel(Parcel in) {
            return new Fragen(in);
        }

        @Override
        public Fragen[] newArray(int size) {
            return new Fragen[size];
        }
    };

    public String getFrage() {
        return fragen;
    }

    public void setFrage(String fragen) {
        this.fragen = fragen;
    }

    public String getAntwort1() {
        return antwort1;
    }

    public void setAntwort1(String antwort1) {
        this.antwort1 = antwort1;
    }

    public String getAntwort2() {
        return antwort2;
    }

    public void setAntwort2(String antwort2) {
        this.antwort2 = antwort2;
    }

    public String getAntwort3() {
        return antwort3;
    }

    public void setAntwort3(String antwort3) {
        this.antwort3 = antwort3;
    }

    public String getAntwort4() {
        return antwort4;
    }

    public void setAntwort4(String antwort4) {
        this.antwort4 = antwort4;
    }

    public int getAntwortNr() {
        return antwortNr;
    }

    public void setAntwortNr(int antwortNr) {
        this.antwortNr = antwortNr;
    }


    public String getSchwierigkeit() {
        return schwierigkeit;
    }

    public void setSchwierigkeit(String schwierigkeit) {
        this.schwierigkeit = schwierigkeit;
    }

    // um die schwierigkeiten später anzeigen zu können
    public static String[] getAlleSchwierigkeitsStufen(){

        return new String[]{
                SCHWIERIGKEIT_LEICHT,
                SCHWIERIGKEIT_MITTEL,
                SCHWIERIGKEIT_SCHWER
        };
    }
}
