package de.ronnyritscher.myquizappproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import de.ronnyritscher.myquizappproject.QuizVertrag.*; // Einbinden der eigenen Klasse


public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String TAG = QuizDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "MeinErstesQuiz.db";
    private static final int DATABASE_VERSION = 2;

    /*INFO bei änderungen der DB ->
        - VersionsNummer ändern,
        - den SQL_CREATE_QUESTIONS_TABLE anpassen */

    private SQLiteDatabase db;

    //public QuizDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
    public QuizDbHelper(@Nullable Context context) {
        //super(context, name, factory, version);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Log.d(TAG, "QuizDbHelper: KONSTRUKTOR ERREICHT");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Hier erstellen wir die DB-TABLE
        this.db = db;
        //Log.d(TAG, "QuizDbHelper: ONCREATE ERREICHT");
        //final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
        final String SQL_ERSTELLE_FRAGEN_TABELLE = "CREATE TABLE " +
                QuizFragenTabelle.TABELLE_NAME + " ( " +
                QuizFragenTabelle._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizFragenTabelle.SPALTE_FRAGE + " TEXT, " +
                QuizFragenTabelle.SPALTE_ANTWORT1 + " TEXT, " +
                QuizFragenTabelle.SPALTE_ANTWORT2 + " TEXT, " +
                QuizFragenTabelle.SPALTE_ANTWORT3 + " TEXT, " +
                QuizFragenTabelle.SPALTE_ANTWORT4 + " TEXT, " +
                QuizFragenTabelle.SPALTE_ANTWORT_NR + " INTEGER, " +
                QuizFragenTabelle.SPALTE_SCHWIERIGKEIT + " TEXT" +
                ")";
        //ausführen der DB mit dem erzeugten TABLE
        //db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_ERSTELLE_FRAGEN_TABELLE);

        //füllen der Tabelle mit Fragen:  Methode unten...
        fuelleFrageTabelle();

    }

    //Bei änderungen an der DB muss dies nicht nur oben angegeben werden, sondern
    // auch über onUpgrade()
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d(TAG, "QuizDbHelper: ONUPGRADE ERREICHT");
        db.execSQL("DROP TABLE IF EXISTS " + QuizFragenTabelle.TABELLE_NAME);
        onCreate(db);

    }

    private void fuelleFrageTabelle(){
        //Log.d(TAG, "QuizDbHelper: FÜLLE FRAGEN ERREICHT");
//        //erzeuge eine instanz von Fragen
//        Fragen f1 = new Fragen(
//                Fragen.SCHWIERIGKEIT_LEICHT +": \n"+"A ist korrekt",  //Frage
//                "A",            //Antwort1
//                "B",            //Antwort2
//                "C",            //Antwort3
//                "D",            //Antwort4
//                1 ,            //RichtigeAntwortIst
//                Fragen.SCHWIERIGKEIT_LEICHT      // Schwierigkeit
//                );
//        //hinzufügen der Fragen in die db
//        fragenHinzufuegen(f1);
        //.....................................................................
        Fragen f1 = new Fragen("Leicht: "+"A ist korrekt",
                "A", "B", "C", "D", 1 , Fragen.SCHWIERIGKEIT_LEICHT );
        fragenHinzufuegen(f1);

        Fragen f2 = new Fragen("Leicht: "+"B ist korrekt",
                "A", "B", "C", "D", 2 , Fragen.SCHWIERIGKEIT_LEICHT );
        fragenHinzufuegen(f2);

        Fragen f3 = new Fragen("Mittel: "+"C ist korrekt",
                "A", "B", "C", "D", 3 , Fragen.SCHWIERIGKEIT_MITTEL );
        fragenHinzufuegen(f3);

        Fragen f4 = new Fragen("Mittel: "+"D ist korrekt",
                "A", "B", "C", "D", 4 , Fragen.SCHWIERIGKEIT_MITTEL );
        fragenHinzufuegen(f4);

        Fragen f5 = new Fragen("Schwer: "+"A ist korrekt",
                "A", "B", "C", "D", 1 , Fragen.SCHWIERIGKEIT_SCHWER );
        fragenHinzufuegen(f5);

        Fragen f6 = new Fragen("Schwer: "+"B ist korrekt",
                "A", "B", "C", "D", 2 , Fragen.SCHWIERIGKEIT_SCHWER );
        fragenHinzufuegen(f6);
    }

    //Hinzufügen der Fragen in die SQL-Tabelle
    private void fragenHinzufuegen(Fragen fragen){
        //Log.d(TAG, "QuizDbHelper: FRAGEN HINZUFÜGEN ERREICHT");
        ContentValues cv = new ContentValues();
        cv.put(QuizFragenTabelle.SPALTE_FRAGE, fragen.getFrage() );             // Fügt die Frage hinzu
        cv.put(QuizFragenTabelle.SPALTE_ANTWORT1 , fragen.getAntwort1() );      // Fügt die Antwort1 hinzu
        cv.put(QuizFragenTabelle.SPALTE_ANTWORT2 , fragen.getAntwort2() );      // Fügt die Antwort2 hinzu
        cv.put(QuizFragenTabelle.SPALTE_ANTWORT3 , fragen.getAntwort3() );      // Fügt die Antwort3 hinzu
        cv.put(QuizFragenTabelle.SPALTE_ANTWORT4 , fragen.getAntwort4() );      // Fügt die Antwort4 hinzu
        cv.put(QuizFragenTabelle.SPALTE_ANTWORT_NR , fragen.getAntwortNr() );   // Fügt die AntwortNr hinzu
        cv.put(QuizFragenTabelle.SPALTE_SCHWIERIGKEIT , fragen.getSchwierigkeit() );   // Fügt die AntwortNr hinzu

        //.insert(tabellenName , nullColumnHack, values );
        db.insert(QuizFragenTabelle.TABELLE_NAME, null , cv);
    }

    // ALLE FRAGEN AUS DER LISTE ERHALTEN
    // -> Um die Tabelle in einem Bundle zu speichern müssen wir die List<Fragen> in ArrayList<Fragen> ändern
    // ... public List<Fragen> getAlleFragen(){
    public ArrayList<Fragen> getAlleFragen(){
        //Log.d(TAG, "QuizDbHelper: METHODE GET-ALLE-FRAGEN ERREICHT");
        // ...List<Fragen> fragenListe = new ArrayList<>();
        ArrayList<Fragen> fragenListe = new ArrayList<>();
        db = getReadableDatabase();   //ERSTLLT DIE DB BEIM ERSTEN AUFRUF
        Cursor cursor = db.rawQuery("SELECT * FROM " + QuizFragenTabelle.TABELLE_NAME , null); //Zeiger für die db

        if(cursor.moveToFirst()){
            do {
                Fragen fragen = new Fragen();
                //Holt die Frage/Antworten/AntwortNr als String/Integer und speichert es in das Objekt "fragen"
                fragen.setFrage(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_FRAGE)));
                fragen.setAntwort1(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT1)));
                fragen.setAntwort2(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT2)));
                fragen.setAntwort3(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT3)));
                fragen.setAntwort4(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT4)));
                fragen.setAntwortNr(cursor.getInt(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT_NR)));
                fragen.setSchwierigkeit(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_SCHWIERIGKEIT)));
                fragenListe.add(fragen);
            }while (cursor.moveToNext());
        }

        //schließen/beenden des cursor
        cursor.close();
        return fragenListe;
    }

    // SPEZIELLE FRAGEN AUS DER LISTE ERHALTEN ÜBER DIE SCHWIERIGKEIT
    // -> Um die Tabelle in einem Bundle zu speichern müssen wir die List<Fragen> in ArrayList<Fragen> ändern
    // ... public List<Fragen> getAlleFragen(){
    public ArrayList<Fragen> getSpezielleFragen(String schwierigkeit){
        //Log.d(TAG, "QuizDbHelper: METHODE GET-FRAGEN ERREICHT");

        // ...List<Fragen> fragenListe = new ArrayList<>();
        ArrayList<Fragen> fragenListe = new ArrayList<>();

        db = getReadableDatabase();   //ERSTLLT DIE DB BEIM ERSTEN AUFRUF

        //Hier wird die Schwierigkeit übergeben und über selectionArgs in der db.rawQuery() abgefragt
        // -> den selectionArgs muss ein StrinArray übergeben werden...
        String[] selectionArgs = new String[] {schwierigkeit};

        Cursor cursor = db.rawQuery(
                //Zeiger für die db
                "SELECT * FROM " + QuizFragenTabelle.TABELLE_NAME + " WHERE " + QuizFragenTabelle.SPALTE_SCHWIERIGKEIT + " = ?" , selectionArgs);

        if(cursor.moveToFirst()){
            do {
                Fragen fragen = new Fragen();
                //Holt die Frage/Antworten/AntwortNr als String/Integer und speichert es in das Objekt "fragen"
                fragen.setFrage(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_FRAGE)));
                fragen.setAntwort1(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT1)));
                fragen.setAntwort2(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT2)));
                fragen.setAntwort3(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT3)));
                fragen.setAntwort4(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT4)));
                fragen.setAntwortNr(cursor.getInt(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_ANTWORT_NR)));
                fragen.setSchwierigkeit(cursor.getString(cursor.getColumnIndex(QuizFragenTabelle.SPALTE_SCHWIERIGKEIT)));
                fragenListe.add(fragen);
            }while (cursor.moveToNext());
        }

        //schließen/beenden des cursor
        cursor.close();
        return fragenListe;
    }

}
