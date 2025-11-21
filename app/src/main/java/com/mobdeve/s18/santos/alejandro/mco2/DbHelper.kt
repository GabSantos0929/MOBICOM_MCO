package com.mobdeve.s18.santos.alejandro.mco2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_VERSION = 4
        private const val DATABASE_NAME = "class_ease.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ClassDbHelper.DbReferences.CREATE_CLASS_TABLE)
        db.execSQL(NotifDbHelper.DbReferences.CREATE_NOTIF_TABLE)
        db.execSQL(BuildingDbHelper.DbReferences.CREATE_BUILDING_TABLE)
        db.execSQL(ClassDbHelper.DbReferences.INSERT_CLASS_TABLE)
        db.execSQL(NotifDbHelper.DbReferences.INSERT_NOTIF_TABLE)
        db.execSQL(BuildingDbHelper.DbReferences.INSERT_BUILDING_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(ClassDbHelper.DbReferences.DROP_CLASS_TABLE)
        db.execSQL(NotifDbHelper.DbReferences.DROP_NOTIF_TABLE)
        db.execSQL(BuildingDbHelper.DbReferences.DROP_BUILDING_TABLE)
        onCreate(db)
    }
}