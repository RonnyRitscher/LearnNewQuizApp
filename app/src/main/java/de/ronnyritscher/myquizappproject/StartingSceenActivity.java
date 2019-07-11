package de.ronnyritscher.myquizappproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class StartingSceenActivity extends AppCompatActivity implements View.OnClickListener {

    //ZUM TESTEN - KANN SPÄTER ENTFERNT WERDEN
    public static final String TAG = StartingSceenActivity.class.getSimpleName();

    // ...um den highscore des letzten spieles zu erhalten/speichen
    // Parameter der Methode startActivityForResult()
    public static final int REQUEST_CODE_QUIZ = 1;
    // Speichern des SchwierigkeitsLevels
    public static final String EXTRA_SCHWIERIGKEIT = "extraSchwierigkeit";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    private TextView tv_startScreen_highscore;
    private int highscore;

    //SpinnerSchwierigkeit
    private Spinner spinnerSchwierigkeit;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startingscreen);

        //Laden des HighScores
        tv_startScreen_highscore = findViewById(R.id.tv_startScreen_highscore);
        ladeHighscore();

        //Laden des spinnerSchwierigkeit
        spinnerSchwierigkeit = findViewById(R.id.spinnerSchwierigkeit);
        //Übergeben der (public) alleSchwierigkeitsStufen
        String[] schwierigkeitLevel = Fragen.getAlleSchwierigkeitsStufen();
        //ADAPTER für den Spinner -> ArrayAdapter<>(WO, LAYOUT , INHALT );
        ArrayAdapter<String> adapterSchwierigkeit = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item , schwierigkeitLevel);
        // Angabe wie der DropDown des spinners aussehen soll
        adapterSchwierigkeit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Füllen des Spinners mit dem erzeugten Adapter
        spinnerSchwierigkeit.setAdapter(adapterSchwierigkeit);



        Button btnStartScreenStart = findViewById(R.id.btn_startScreen_start);
        btnStartScreenStart.setOnClickListener(this);  //Damit er auch vom CL gefunden wird
    }

    @Override
    public void onClick(View view) {

        switch ( view.getId() ) {
            case R.id.btn_startScreen_start:
                startQuiz();
                break;
        }
    }

    private void startQuiz(){
        //SPINNER -> übergibt die vom User gewählte Schwierigkeit als String
        String schwierigkeit = spinnerSchwierigkeit.getSelectedItem().toString();

        Intent intent = new Intent(this, QuizActivity.class);
        //übergeben der schwierigkeit an den intent
        intent.putExtra(EXTRA_SCHWIERIGKEIT , schwierigkeit);

        //startActivity(intent);
        startActivityForResult(intent , REQUEST_CODE_QUIZ);
    }

    // um das Ergebnis des Quiz auswerten/anzeigen zu können:


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_QUIZ){
            if(resultCode == RESULT_OK){
                int punkte = data.getIntExtra("letztePunkte" , 0);

                //prüfe ob der score größer als der aktuelle highscore ist
                if(punkte > highscore ){
                    //anzeigen und speichern des neuen hoghscores
                    updateHighscore(punkte);
                }

            }
        }
    }

    //
    private void ladeHighscore(){
        //erzeugen der sharedPrefs
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //holen des Inhaltes
        highscore = sharedPreferences.getInt(KEY_HIGHSCORE, 0);
        // Anzeigen des HS
        tv_startScreen_highscore.setText("Highscore : " + highscore);
    }


    //Methode zum Anzeigen und Speichern des Highscores
    private void updateHighscore(int punkteNeu){
        highscore = punkteNeu;
        // Anzeigen des HS
        tv_startScreen_highscore.setText("Highscore : " + highscore);

        //erzeugen der sharedPrefs
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        // verwenden des Editors mit edit()
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Daten in den Editor übergeben
        editor.putInt(KEY_HIGHSCORE, highscore);
        // Daten de Editord an das SP übergeben
        editor.apply();


    }
}
