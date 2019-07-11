package de.ronnyritscher.myquizappproject;

import android.provider.BaseColumns;

public final class QuizVertrag {

    private QuizVertrag(){}

    public static class QuizFragenTabelle implements BaseColumns {

        public static final String TABELLE_NAME = "quiz_fragen";
        public static final String SPALTE_FRAGE = "fragen";
        public static final String SPALTE_ANTWORT1 = "antwort1";
        public static final String SPALTE_ANTWORT2 = "antwort2";
        public static final String SPALTE_ANTWORT3 = "antwort3";
        public static final String SPALTE_ANTWORT4 = "antwort4";
        public static final String SPALTE_ANTWORT_NR = "antwort_nr";

        //hinzufügen der Level/Schwierigkeitstufen:
        public static final String SPALTE_SCHWIERIGKEIT = "schwierigkeit";
    }
}
