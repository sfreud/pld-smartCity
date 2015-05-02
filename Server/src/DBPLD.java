package com.telys.ls_android;

import android.provider.BaseColumns;

public final class DBPLD {	
	//schéma de la DB
	//une classe interne représente une table de la DB
	/*
	 * La DB locale reprend autant que possible la structure et les noms de la DB sur le serveur 
	 * pour avoir une interface d'accès
	 * cohérente, mais on ne garde en local que les données utiles.
	 *  
	 * */

	//constructeur privé (classe non instanciable)
    private DBPLD() {}

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ", ";
    public static final String INT_TYPE = " INTEGER";
    public static final String FOREIGN_KEY = " FOREIGN KEY";
    public static final String BLOB_TYPE = " BLOB";
    
    
	    /* Inner class that defines the table contents */
    //BaseColumns -> constante héritée _ID servant de clé primaire (nécessaire pour utiliser la classe Cursor)
    public static abstract class users implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_PASSWORD = "password";
 
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PASSWORD + TEXT_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        
    }
    public static abstract class QuestionsTypeS implements BaseColumns {
    	//contient les questions de type S 
    	public static final String TABLE_NAME = "lime_questions_s";
        public static final String COLUMN_NAME_ID = "sid";
        public static final String  = "qid";
        public static final String COLUMN_NAME_LANGUAGE = "language";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_HELP = "help";
        public static final String COLUMN_NAME_USER_ANSWER = "answer";
        public static final String COLUMN_NAME_CORRECTION = "correction";
        public static final String COLUMN_NAME_ANSWERED = "answered";
        public static final String COLUMN_NAME_CORRECTLY_ANSWERED = "correctly_answered";
        
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                 + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_QUESTION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_HELP + TEXT_TYPE + " DEFAULT \"\"" + COMMA_SEP +
                COLUMN_NAME_CORRECTION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_USER_ANSWER + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_CORRECTLY_ANSWERED + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                COLUMN_NAME_ANSWERED + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                COLUMN_NAME_LANGUAGE + TEXT_TYPE + COMMA_SEP + "UNIQUE (" +  + " )" + COMMA_SEP + 
                FOREIGN_KEY + "(" + COLUMN_NAME_ID + ") REFERENCES " 
                + users.TABLE_NAME +"(" + users.COLUMN_NAME_ID + ")"
                + ")";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
    public static abstract class QuestionsTypeM implements BaseColumns {
    	//(SELECT sid,... FROM lime_questions WHERE type = "M")
    	public static final String COLUMN_NAME_HELP = "help";
    	public static final String TABLE_NAME = "lime_questions_m";
        public static final String COLUMN_NAME_ID = "sid";
        public static final String  = "qid";
        public static final String COLUMN_NAME_LANGUAGE = "language";
        public static final String COLUMN_NAME_QUESTION = "question";
        //stocker le nb de fois où le qcm a été passé (ou juste whether il a été passé or not)
        public static final String COLUMN_NAME_ANSWERED = "answered";
        public static final String COLUMN_NAME_CORRECTLY_ANSWERED = "correctly_answered";
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_HELP + TEXT_TYPE+ " DEFAULT null"  + COMMA_SEP +
                 + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_QUESTION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ANSWERED + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                COLUMN_NAME_CORRECTLY_ANSWERED + INT_TYPE + " DEFAULT 0" + COMMA_SEP +
                COLUMN_NAME_LANGUAGE + TEXT_TYPE + COMMA_SEP + "UNIQUE (" +  + " )"
                + COMMA_SEP + 
                FOREIGN_KEY + "(" + COLUMN_NAME_ID + ") REFERENCES " 
                + users.TABLE_NAME +"(" + users.COLUMN_NAME_ID + ")"
                		+ ")";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
    public static abstract class Answers implements BaseColumns {
    	//stockage séparé des réponses à choix multiple
    	public static final String COLUMN_NAME_HELP = "help";
        public static final String COLUMN_NAME_ID = "sid";
    	public static final String TABLE_NAME = "lime_answers";
    	public static final String COLUMN_NAME_PARENT_QID = "parent_qid";
    	public static final String COLUMN_NAME_ID = "qid";
    	public static final String COLUMN_NAME_ANSWER = "answer";
    	//la colonne "clicked" servait à indiquer quelles réponses avaient été sélectionnées. Inutilisée maintenant.
    	//public static final String COLUMN_NAME_CLICKED = "clicked";
    	public static final String COLUMN_NAME_IS_ANSWER = "is_answer";
    	
    	 public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                 TABLE_NAME + " (" +
                 _ID + " INTEGER PRIMARY KEY," +
                 COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                 COLUMN_NAME_HELP + TEXT_TYPE+ " DEFAULT null"  + COMMA_SEP +
                 COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                 COLUMN_NAME_ANSWER + TEXT_TYPE + COMMA_SEP +
                 COLUMN_NAME_PARENT_QID + INT_TYPE + COMMA_SEP +
                 COLUMN_NAME_IS_ANSWER + INT_TYPE + COMMA_SEP +
                 //COLUMN_NAME_CLICKED + INT_TYPE + COMMA_SEP + 
                 "UNIQUE ("+ COLUMN_NAME_ID +")"
                 + COMMA_SEP + 
                 FOREIGN_KEY + "(" + COLUMN_NAME_PARENT_QID + ") REFERENCES " 
                 + QuestionsTypeS.TABLE_NAME +"(" + QuestionsTypeS. + ")"
                 		+ ")";
    	 public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
    
}
	
	
	


