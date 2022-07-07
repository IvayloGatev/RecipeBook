package de.thu.recipebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class FavoritesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Recipe.db";
    public static final String FAVORITES_TABLE = "favorites";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FAVORITES_TABLE + " ("
            + BaseColumns._ID + " STRING PRIMARY KEY)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FAVORITES_TABLE;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
