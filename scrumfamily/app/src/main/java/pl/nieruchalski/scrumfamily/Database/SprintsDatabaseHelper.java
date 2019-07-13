package pl.nieruchalski.scrumfamily.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by michal on 21.01.17.
 */

public class SprintsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TESTF";
    private static final int DB_VERSION = 1;

    public SprintsDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE REMEMBERME (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "LOGIN TEXT,"
                + "PASSWORD TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE MYTASKS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TABLENAME TEXT,"
                + "ID INTEGER,"
                + "TITLE TEXT,"
                + "DESCRIPTION TEXT,"
                + "STATE INTEGER,"
                + "TOOKBY TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE SAVEDSUMMARIES (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "SPRINT_ID INTEGER,"
                + "TABLENAME TEXT,"
                + "MOSTDONEUSER TEXT,"
                + "MOSTDONEUSERNUMBER TEXT,"
                + "DONENUMBER TEXT,"
                + "NOTDONENUMBER TEXT,"
                + "STARTDATE TEXT,"
                + "ENDDATE TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE SAVEDSPRINTS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "SPRINT_ID INTEGER,"
                + "ID INTEGER,"
                + "TITLE TEXT,"
                + "DESCRIPTION TEXT,"
                + "STATE INTEGER,"
                + "TOOKBY TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
