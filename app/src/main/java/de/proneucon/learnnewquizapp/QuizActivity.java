package de.proneucon.learnnewquizapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    // benötigte ELEMENTE
    //LOG
    public static final String TAG = QuizActivity.class.getSimpleName();
    // für Request Werte
    public static final String LETZTE_PUNKTE = "letztePunkte";

    // für CountDownTimer
    private static final Long COUNTODWN_IN_MILLIS = 10000L;
    private static final Long COUNTDOWN_ALERT_TIME = 5001L;

    // zum speichern der Zustände (beim drehen des Gerätes)
    private static final String KEY_PUNKTE = "keyPunkte";
    private static final String KEY_FRAGEN_ANZAHL = "keyFragenAnzahl";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANTWORT = "KeyAntwort";
    private static final String KEY_FRAGEN_LISTE = "keyFragenListe";
    private static final String KEY_BUTTON_ANTWORT_USER_NR = "keyAntwortUserNr";


    // TextFelder
    private TextView tv_quiz_punkte;
    private TextView tv_quiz_fragenAnzahl;
    private TextView tv_quiz_countDownTime;
    private TextView tv_quiz_frage;
    private TextView tv_quiz_schwierigkeit;

    // Buttons
    private Button btn_quiz_antwort1;
    private Button btn_quiz_antwort2;
    private Button btn_quiz_antwort3;
    private Button btn_quiz_antwort4;
    private Button btn_quiz_bestaetigen_next;

    // Speichern der Farben
    private int colorButtonTextDefault;
    private int colorButtonTextAntwortRichtig;
    private int colorButtonTextAntwortFalsch;
    private int colorButtonBackgroundDefault;
    private int colorButtonBackgroundHighlight;
    private int colorButtonBackgroundAntworFalsch;
    private int colorButtonBackgroundAntworRichtig;

    // speichert den Zustand des derzeit ausgewählten Buttons
    private int buttonAusgewaehlt;

    // Farben des CountDownTimers
    private ColorStateList colorCoundownTextDefault;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    // um die Liste ikm bundle speichern zu können müssen wir die List<Fragen> in ArrayList<Fragen> ändern
    //private List<Fragen> fragenListe;
    private ArrayList<Fragen> fragenListe;

    private int punkte = 0;
    private int frageCounter;
    private int frageCounterTotal;
    private int antwortUserNr;
    private Fragen aktuelleFrage;
    private String textPunkte;

    private boolean btn_quiz_antwort1_checked;
    private boolean btn_quiz_antwort2_checked;
    private boolean btn_quiz_antwort3_checked;
    private boolean btn_quiz_antwort4_checked;
    private boolean antwort;

    //-----------------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //Implementieren der ELEMENTE
        tv_quiz_punkte = findViewById(R.id.tv_quiz_punkte);
        tv_quiz_fragenAnzahl = findViewById(R.id.tv_quiz_fragenAnzahl);
        tv_quiz_countDownTime = findViewById(R.id.tv_quiz_countDownTime);
        tv_quiz_frage = findViewById(R.id.tv_quiz_frage);
        tv_quiz_schwierigkeit = findViewById(R.id.tv_anzeige_schwierigkeit);

        btn_quiz_antwort1 = findViewById(R.id.btn_quiz_antwort1);
        btn_quiz_antwort2 = findViewById(R.id.btn_quiz_antwort2);
        btn_quiz_antwort3 = findViewById(R.id.btn_quiz_antwort3);
        btn_quiz_antwort4 = findViewById(R.id.btn_quiz_antwort4);
        btn_quiz_bestaetigen_next = findViewById(R.id.btn_quiz_bestaetigen_next);

        // Speichert die TextFarbe der Buttons
        colorButtonTextDefault = R.color.colorButtonTextDefault;
        colorButtonTextAntwortRichtig = R.color.colorButtonTextAntwortRichtig;
        colorButtonTextAntwortFalsch = R.color.colorButtonTextAntwortFalsch;
        colorButtonBackgroundDefault = R.color.colorButtonBackgroundDefault;
        colorButtonBackgroundHighlight = R.color.colorButtonBackgroundHighlight;
        colorButtonBackgroundAntworRichtig = R.color.colorButtonBackgroundantwortRichtig;
        colorButtonBackgroundAntworFalsch = R.color.colorButtonBackgroundAntwortFalsch;

        //Speichert die TextFarbe des CountDownTimers
        colorCoundownTextDefault = tv_quiz_countDownTime.getTextColors();

        //!!!!!!!!!!!!!NACHBESSERUNG -> Text
        //Anzeiger des Punktestandes
        tv_quiz_punkte.setText( "Punkte: "+ punkte);

        //SCHWIERIGKEIT und übergeben der schw. aus der StartingScreenActivity
        Intent intent = getIntent();
        String schwierigkeit = intent.getStringExtra(StartingSceenActivity.EXTRA_SCHWIERIGKEIT);
        // Anzeigetext übergeben
        tv_quiz_schwierigkeit.setText("Schwierigkeit: " + schwierigkeit);



        // ERZEUGEN EINER NEUEN INSTANZ DER FRAGENLISTE -> ANSONSTEN WIEDERHERSTELLEN DER ALTEN WERTE::::
        // -> zum speichern der Liste in einem Bundle wollen wir die Liste verwenden, die bereits erstellt wurde,
        // -> ansonsten erstellen wir eine neue Insatnz
        if(savedInstanceState == null){

            //initialisieren des QuizDbHelper
            QuizDbHelper dbHelper = new QuizDbHelper(this);
            //füllen der fragenListe über den dbHelper
            //fragenListe = dbHelper.getAlleFragen();                       //ALLE FRAGEN
            fragenListe = dbHelper.getSpezielleFragen(schwierigkeit);       //SPEZIELLE FRAGEN
            //Speichert die aktuelle Anzahl ALLER Fragen
            frageCounterTotal = fragenListe.size();
            // Mischen der Fragen
            Collections.shuffle(fragenListe);
            // nächste Frage anzeigen.
            zeigeNaechsteFrage();

        } else {
            // LADEN/WIEDERHERSTELLEN der INSTANCE-STATE-DATEN über .getParcelableArrayList()
            fragenListe = savedInstanceState.getParcelableArrayList(KEY_FRAGEN_LISTE);
            //um den Fall auszuschließen, dass die geladene Liste NULL ist:
            if(fragenListe == null){
                finish(); // einige logische variante ist das beenden des quiz
            }
            frageCounterTotal = fragenListe.size();
            frageCounter = savedInstanceState.getInt(KEY_FRAGEN_ANZAHL);
            //! hier bei der AktuellenFrage aufpassen, da die zählung bei 0 beginnt, ansonsten Exeption bei der letzten Frage
            aktuelleFrage = fragenListe.get(frageCounter -1);
            punkte = savedInstanceState.getInt(KEY_PUNKTE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            antwort = savedInstanceState.getBoolean(KEY_ANTWORT);

            //Wenn ein Butten bereits ausgewählt wurde:
            antwortUserNr = savedInstanceState.getInt(KEY_BUTTON_ANTWORT_USER_NR);
            if(antwortUserNr != 0){
                Log.d(TAG, "onCreate: IF ANTWORT USER NR ______________________" + antwortUserNr);
                // Highligt des ausgewählen Buttons
                switch (antwortUserNr){
                    case 1:
                        btn_quiz_antwort1_checked = true;
                        btn_quiz_antwort2_checked = false;
                        btn_quiz_antwort3_checked = false;
                        btn_quiz_antwort4_checked = false;
                        btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundHighlight);
                        btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundDefault);
                        break;
                    case 2:
                        btn_quiz_antwort1_checked = false;
                        btn_quiz_antwort2_checked = true;
                        btn_quiz_antwort3_checked = false;
                        btn_quiz_antwort4_checked = false;
                        btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundHighlight);
                        btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundDefault);
                        break;
                    case 3:
                        btn_quiz_antwort1_checked = false;
                        btn_quiz_antwort2_checked = false;
                        btn_quiz_antwort3_checked = true;
                        btn_quiz_antwort4_checked = false;
                        btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundHighlight);
                        btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundDefault);
                        break;
                    case 4:
                        btn_quiz_antwort1_checked = false;
                        btn_quiz_antwort2_checked = false;
                        btn_quiz_antwort3_checked = false;
                        btn_quiz_antwort4_checked = true;
                        btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundDefault);
                        btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundHighlight);
                        break;
                }
            }


            // Wenn eine Antwort zu der Zeit gegeben worden ist...
            if(!antwort){

                startCountDown();
            }else {


                updateCountDownText();
                zeigeLoesung();
            }
        }



        //Funktionen der Buttons über OnClickListener
        btn_quiz_antwort1.setOnClickListener(this);
        btn_quiz_antwort2.setOnClickListener(this);
        btn_quiz_antwort3.setOnClickListener(this);
        btn_quiz_antwort4.setOnClickListener(this);
        btn_quiz_bestaetigen_next.setOnClickListener(this);

    }
    //-----------------------------------------------------------------
    // ERSTELLEN DER NÄCHSTEN FRAGE AUS DER FRAGELISTE
    private void zeigeNaechsteFrage(){



        // zurücksetzen der Button-Farben
        btn_quiz_antwort1.setTextColor(colorButtonTextDefault);
        btn_quiz_antwort2.setTextColor(colorButtonTextDefault);
        btn_quiz_antwort3.setTextColor(colorButtonTextDefault);
        btn_quiz_antwort4.setTextColor(colorButtonTextDefault);

        // zurücksetzen der Button-Background-Farben
        btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundDefault);
        btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundDefault);
        btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundDefault);
        btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundDefault);

        // löschen der Button-Auswahl
        //btn_quiz_antwort1.setBackgroundColor();

        if( frageCounter < frageCounterTotal){
            aktuelleFrage = fragenListe.get(frageCounter);

            //Frage anzeigen
            tv_quiz_frage.setText(aktuelleFrage.getFrage());

            //Antworten anzeigen
            btn_quiz_antwort1.setText(aktuelleFrage.getAntwort1());
            btn_quiz_antwort2.setText(aktuelleFrage.getAntwort2());
            btn_quiz_antwort3.setText(aktuelleFrage.getAntwort3());
            btn_quiz_antwort4.setText(aktuelleFrage.getAntwort4());

            frageCounter++; // erhöht den wert der aktuellen Frage
            tv_quiz_fragenAnzahl.setText( "Frage: " +frageCounter+ " von " + frageCounterTotal );

            //zurücksetzen des antwort-booleans
            btn_quiz_antwort1_checked = false;
            btn_quiz_antwort2_checked = false;
            btn_quiz_antwort3_checked = false;
            btn_quiz_antwort4_checked = false;
            antwort = false;
            antwortUserNr = 0;

            // Button btn_quiz_bestaetigen_next auf weiter/ende setzen
            btn_quiz_bestaetigen_next.setText("Weiter");

            // Hier startet der CountDownTimer
            timeLeftInMillis = COUNTODWN_IN_MILLIS;
            startCountDown();


        }else {
            beendeQuiz();
        }

    }

    //-----------------------------------------------------------------
    // COUNTDOWN-TIMER DER APP im QUIZ
    public void startCountDown(){
        //new CountDownTimer(ZeitangabeDesCountDowns, Intervall) {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                // wenn die Zeit abgelaufen idt
                // implizit noch einmal auf 0 setzen und ein letztes mal die Methode updateCountDownText() aufrufen!
                timeLeftInMillis = 0;
                updateCountDownText();
                // Aufruf der pruefeAntworten()
                pruefeAntwort();

            }
        }.start(); // zum starten des Timers

    }

    public void updateCountDownText(){
        // um die minuten zu erhalten rechnen wir (X/1000)/60
        int minuten = (int) (timeLeftInMillis / 1000) /60;
        // um die sek. zu erhalten müssen wir den Rest der Minuten mit % berechnen
        int sekunden = (int) (timeLeftInMillis / 1000) %60;
        // zum anzeigen in einem String zusammenfügen über .format()
        String timeFormatted = String.format(Locale.getDefault() , "%02d%02d" , minuten , sekunden);
        // String in TextView anzeigen
        // ...wird bei jedem tick ausgeführt (jede sekunde)
        tv_quiz_countDownTime.setText(timeFormatted);

        // um die Textfarbe zu ändern wenn weniger als 5 sekunden
        if(timeLeftInMillis < COUNTDOWN_ALERT_TIME){
             tv_quiz_countDownTime.setTextColor(Color.RED);
        }else {
            tv_quiz_countDownTime.setTextColor(colorCoundownTextDefault);
        }

    }

    //-----------------------------------------------------------------
    // BEIM KLICKEN EINES BUTTONS DER APP
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_quiz_antwort1:
                btn_quiz_antwort1_checked = true;
                btn_quiz_antwort2_checked = false;
                btn_quiz_antwort3_checked = false;
                btn_quiz_antwort4_checked = false;
                btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundHighlight);
                btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundDefault);
                antwortUserNr = 1;
                Log.d(TAG, "onClick: Userauswagr Antwort = 1");
                break;

            case R.id.btn_quiz_antwort2:
                btn_quiz_antwort1_checked = false;
                btn_quiz_antwort2_checked = true;
                btn_quiz_antwort3_checked = false;
                btn_quiz_antwort4_checked = false;
                btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundHighlight);
                btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundDefault);
                antwortUserNr = 2;
                Log.d(TAG, "onClick: Userauswagr Antwort = 2");
                break;

            case R.id.btn_quiz_antwort3:
                btn_quiz_antwort1_checked = false;
                btn_quiz_antwort2_checked = false;
                btn_quiz_antwort3_checked = true;
                btn_quiz_antwort4_checked = false;
                btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundHighlight);
                btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundDefault);
                antwortUserNr = 3;
                Log.d(TAG, "onClick: Userauswagr Antwort = 3");
                break;

            case R.id.btn_quiz_antwort4:
                btn_quiz_antwort1_checked = false;
                btn_quiz_antwort2_checked = false;
                btn_quiz_antwort3_checked = false;
                btn_quiz_antwort4_checked = true;
                btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundDefault);
                btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundHighlight);
                antwortUserNr = 4;
                Log.d(TAG, "onClick: Userauswagr Antwort = 4");
                break;

            case R.id.btn_quiz_bestaetigen_next:
                //Abfrage ob eine Antwort ausgewählt wurde:
                if(!antwort){ //

                    if(btn_quiz_antwort1_checked==true || btn_quiz_antwort2_checked==true || btn_quiz_antwort3_checked==true || btn_quiz_antwort4_checked==true  ){
                        Log.d(TAG, "onClick:  pruefeAntwort() wird gestartet");
                        pruefeAntwort();

                    }else{
                        Log.d(TAG, "onClick: Toast wird gestartet");
                        Toast.makeText(this, getString(R.string.toast_quiz_antwortAngeben), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Log.d(TAG, "onClick:  zeigeNaechsteFrage() wird gestartet ");
                    zeigeNaechsteFrage();
                }
                break;
        }

    }

    //-----------------------------------------------------------------
    // PRÜFEN DER ANTWORTEN
    private void pruefeAntwort(){
        // nur wenn eine Antwort ausgewählt wurde
        antwort = true;

        //Wenn der User nach ablauf des CountDown keine Antwort gegeben hat
        countDownTimer.cancel();

        Log.d(TAG, "pruefeAntwort:   BIS HIER OK");
        
        //wenn die RICHTIGE Antwort gegeben wurde:
        if(antwortUserNr == aktuelleFrage.getAntwortNr()){
            punkte++;
            tv_quiz_punkte.setText("Punkte: "+ punkte);
            Log.d(TAG, "pruefeAntwort: Punkte::::::" +punkte  +" ->-> " + R.string.text_punkte +"" + punkte  );

        }
        //Lösung in jedem Fall anzeigen
        zeigeLoesung();
    }
    //-----------------------------------------------------------------
    // ANZEIGEN DER LÖSUNG
    private void zeigeLoesung(){
        Log.d(TAG, "zeigeLoesung: in zeigeLoesung()... ");
        btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundAntworFalsch);
        btn_quiz_antwort1.setTextColor(colorButtonTextAntwortFalsch);
        btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundAntworFalsch);
        btn_quiz_antwort2.setTextColor(colorButtonTextAntwortFalsch);
        btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundAntworFalsch);
        btn_quiz_antwort3.setTextColor(colorButtonTextAntwortFalsch);
        btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundAntworFalsch);
        btn_quiz_antwort4.setTextColor(colorButtonTextAntwortFalsch);

        switch (aktuelleFrage.getAntwortNr()){
            case 1:
                btn_quiz_antwort1.setBackgroundResource(colorButtonBackgroundAntworRichtig);
                btn_quiz_antwort1.setTextColor(colorButtonTextAntwortRichtig);
                break;
            case 2:
                btn_quiz_antwort2.setBackgroundResource(colorButtonBackgroundAntworRichtig);
                btn_quiz_antwort2.setTextColor(colorButtonTextAntwortRichtig);
                break;
            case 3:
                btn_quiz_antwort3.setBackgroundResource(colorButtonBackgroundAntworRichtig);
                btn_quiz_antwort3.setTextColor(colorButtonTextAntwortRichtig);
                break;
            case 4:
                btn_quiz_antwort4.setBackgroundResource(colorButtonBackgroundAntworRichtig);
                btn_quiz_antwort4.setTextColor(colorButtonTextAntwortRichtig);
                break;
        }

        //Anfrage ob es die letzte Frage ist
        if(frageCounter < frageCounterTotal){
            btn_quiz_bestaetigen_next.setText(R.string.btn_quiz_naechsteFrage);
        }else{
            btn_quiz_bestaetigen_next.setText(R.string.btn_quiz_quizBeenden);
        }
    }

    //-----------------------------------------------------------------
    //BEENDEN DES QUIZ
    private void beendeQuiz(){

        //Intent um den Punktestand zu übergeben
        Intent resultIntent  = new Intent();
        //speichern des KEY-VALUE-Paar in dem resultIntent
        resultIntent.putExtra(LETZTE_PUNKTE , punkte);
        //übermitteln des Results
        setResult(RESULT_OK , resultIntent);


        finish();
    }

    //-----------------------------------------------------------------
    // BEIM BEENDEN DER APP

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Beenden des CountDownTimers damit er nicht im Hintergrund weiter läuft
        if(countDownTimer != null ){
            countDownTimer.cancel();
        }
    }

    //-----------------------------------------------------------------
    //SPEICHERN DER ZUSTÄNDE DER APP BEIM DREHEN DES DEVICES

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Speichern der zustände über KEY-VALUE-PAARE in dem Bundle (idf outState)
        outState.putInt(KEY_PUNKTE , punkte);
        outState.putInt(KEY_FRAGEN_ANZAHL , frageCounter);
        outState.putLong(KEY_MILLIS_LEFT , timeLeftInMillis);
        outState.putBoolean(KEY_ANTWORT , antwort);
        //Da wir eine ArrayList<String[]> haben, müssen wir die .putParcelableArrayList() verwenden.
        // 1. dazu müssen wir in der zusätzlich in der Fragen.java die Parcelable implementieren
        // 2. und anpassen der Felder und
        // 3. in der QuizDbHelper von List<Fragen>  zu  ArrayList<Fragen>
        // 4. anpassen  ... in onCreate()
        outState.putParcelableArrayList(KEY_FRAGEN_LISTE , fragenListe );
        outState.putInt(KEY_BUTTON_ANTWORT_USER_NR, antwortUserNr);




    }
}
