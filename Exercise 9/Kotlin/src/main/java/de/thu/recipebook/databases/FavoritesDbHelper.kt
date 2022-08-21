package de.thu.recipebook.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class FavoritesDbHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES)
        onCreate(sqLiteDatabase)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Recipe.db"
        const val FAVORITES_TABLE = "favorites"
        private const val SQL_CREATE_ENTRIES = ("CREATE TABLE " + FAVORITES_TABLE + " ("
                + BaseColumns._ID + " STRING PRIMARY KEY)")
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FAVORITES_TABLE
    }
}