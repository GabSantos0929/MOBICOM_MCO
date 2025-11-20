package com.mobdeve.s18.santos.alejandro.mco2

import android.content.Context
import android.content.ContentValues

class BuildingDbHelper(context: Context) {
    private lateinit var dbHelper : DbHelper

    init {
        this.dbHelper = DbHelper(context)
    }

    fun getAllBuildings(): List<Building> {
        val buildings = mutableListOf<Building>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DbReferences.BUILDING_TABLE,
            null,
            null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            buildings.add(
                Building(
                    cursor.getLong(cursor.getColumnIndexOrThrow(DbReferences.BUILDING_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbReferences.BUILDING_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DbReferences.BUILDING_LAT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DbReferences.BUILDING_LNG))
                )
            )
        }

        cursor.close()
        db.close()
        return buildings
    }

    fun insertBuilding(name: String, lat: Double, lng: Double) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DbReferences.BUILDING_NAME, name)
            put(DbReferences.BUILDING_LAT, lat)
            put(DbReferences.BUILDING_LNG, lng)
        }

        db.insert(DbReferences.BUILDING_TABLE, null, values)
        db.close()
    }

    object DbReferences {
        const val BUILDING_TABLE = "buildings"
        const val BUILDING_ID = "id"
        const val BUILDING_NAME = "building_name"
        const val BUILDING_LAT = "lat"
        const val BUILDING_LNG = "lng"

        const val CREATE_BUILDING_TABLE =
            "CREATE TABLE IF NOT EXISTS $BUILDING_TABLE (" +
                    "$BUILDING_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$BUILDING_NAME TEXT NOT NULL, " +
                    "$BUILDING_LAT REAL NOT NULL, " +
                    "$BUILDING_LNG REAL NOT NULL)"

        const val INSERT_BUILDING_TABLE =
            "INSERT INTO $BUILDING_TABLE (" +
                    "$BUILDING_NAME, $BUILDING_LAT, $BUILDING_LNG) VALUES " +
                    "('Gokongwei Hall', 14.566417609669571, 120.99321983212516)," +
                    "('Andrew Gonzalez Hall', 14.567172953924684, 120.992894304498020)," +
                    "('Enrique Razon', 14.567047151806962, 120.9921144221616)," +
                    "('St. La Salle Hall', 14.564281919673157, 120.99386155524157)," +
                    "('Yuchengco Hall', 14.564431920567717, 120.99327363324552)," +
                    "('Velasco Hall', 14.565467093112716, 120.99316224913895)"

        const val DROP_BUILDING_TABLE =
            "DROP TABLE IF EXISTS $BUILDING_TABLE"
    }
}