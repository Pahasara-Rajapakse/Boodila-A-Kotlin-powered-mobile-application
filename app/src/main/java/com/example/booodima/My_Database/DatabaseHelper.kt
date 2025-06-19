package com.example.booodima.My_Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.booodima.My_Data_Classes.Room

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "boarding.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USER = "user"
        const val TABLE_OWNER = "owner"
        const val TABLE_ROOM = "room"
        const val TABLE_ROOM_FACILITIES = "room_facilities"

        const val COLUMN_USER_NAME = "user_name"
        const val COLUMN_USER_PASSWORD = "user_password"
        const val COLUMN_USER_STATUS = "status"

        const val COLUMN_EMAIL = "email"
        const val COLUMN_OWNER_NAME = "owner_name"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_MOBILE = "mobile"
        const val COLUMN_UNIVERSITY_NAME = "university_name"
        const val COLUMN_ANY_NOTE = "any_note"
        const val COLUMN_USER_REF = "user_ref"

        const val COLUMN_ROOM_ID = "room_id"
        const val COLUMN_FACILITY_ID = "facility_id"
        const val COLUMN_OWNER_REF = "owner_ref"
        const val COLUMN_NUM_BEDS = "num_beds"
        const val COLUMN_ROOM_TYPE = "room_type"
        const val COLUMN_PRICE = "price"
        const val COLUMN_LOCATION = "roomLocation"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_FACILITY_NAME = "facility_name"
        const val COLUMN_ROOM_REF = "room_ref"
        const val COLUMN_ROOM_STATUS = "room_status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val userTable = """
            CREATE TABLE $TABLE_USER (
                $COLUMN_USER_NAME TEXT PRIMARY KEY,
                $COLUMN_USER_PASSWORD TEXT,
                $COLUMN_USER_STATUS TEXT
           )
        """.trimIndent()

        val ownerTable = """
            CREATE TABLE $TABLE_OWNER (
                $COLUMN_EMAIL TEXT PRIMARY KEY,
                $COLUMN_OWNER_NAME TEXT,
                $COLUMN_ADDRESS TEXT,
                $COLUMN_MOBILE TEXT,
                $COLUMN_UNIVERSITY_NAME TEXT,
                $COLUMN_ANY_NOTE TEXT,
                $COLUMN_USER_REF TEXT,
                FOREIGN KEY($COLUMN_USER_REF) REFERENCES $TABLE_USER($COLUMN_USER_NAME)
            )
        """.trimIndent()

        val roomTable = """
            CREATE TABLE $TABLE_ROOM (
                $COLUMN_ROOM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NUM_BEDS INTEGER,
                $COLUMN_ROOM_TYPE TEXT,
                $COLUMN_PRICE REAL,
                $COLUMN_LOCATION TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_ROOM_STATUS TEXT,
                $COLUMN_OWNER_REF TEXT,
                FOREIGN KEY($COLUMN_OWNER_REF) REFERENCES $TABLE_OWNER($COLUMN_EMAIL)
            )
        """.trimIndent()

        val roomFacilitiesTable = """
            CREATE TABLE $TABLE_ROOM_FACILITIES (
                $COLUMN_FACILITY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FACILITY_NAME TEXT ,
                $COLUMN_OWNER_REF TEXT ,
                $COLUMN_ROOM_REF INTEGER ,
                FOREIGN KEY($COLUMN_OWNER_REF) REFERENCES $TABLE_OWNER($COLUMN_EMAIL),
                FOREIGN KEY($COLUMN_ROOM_REF) REFERENCES $TABLE_ROOM($COLUMN_ROOM_ID)
            )
        """.trimIndent()

        db.execSQL(userTable)
        db.execSQL(ownerTable)
        db.execSQL(roomTable)
        db.execSQL(roomFacilitiesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OWNER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROOM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROOM_FACILITIES")
        onCreate(db)
    }

    // insert user data when sign up.
    fun insertUserData(userName: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, userName)
        values.put(COLUMN_USER_PASSWORD, password)

        val result = db.insert(TABLE_USER, null, values)
        Log.e("Database", "User Insert Result: $result")
        return result
    }

    // use to insert owner data into the owner tale.
    fun insertOwnerData(email: String, ownerName: String, address: String, mobile: String, universityName: String, note: String, userName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_OWNER_NAME, ownerName)
        values.put(COLUMN_ADDRESS, address)
        values.put(COLUMN_MOBILE, mobile)
        values.put(COLUMN_UNIVERSITY_NAME, universityName)
        values.put(COLUMN_ANY_NOTE, note)
        values.put(COLUMN_USER_REF, userName)

        val result = db.insert(TABLE_OWNER, null, values)
        Log.e("Database", "Owner Insert Result: $result")
        return result != -1L
    }

    // use to insert room data.
    fun insertRoomData(ownerEmail: String, beds: Int, roomType: String, price: Double, description: String, roomDistance: String, roomStatus: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_OWNER_REF, ownerEmail)
        values.put(COLUMN_NUM_BEDS, beds)
        values.put(COLUMN_ROOM_TYPE, roomType)
        values.put(COLUMN_PRICE, price)
        values.put(COLUMN_LOCATION, roomDistance)
        values.put(COLUMN_DESCRIPTION, description)
        values.put(COLUMN_ROOM_STATUS, roomStatus)

        val result = db.insert(TABLE_ROOM, null, values)
        Log.e("Database Room", "Room Insert Result: $result")
        return result
    }

    // use to insert room facilities.
    fun insertRoomFacilities(roomId: Int, ownerEmail: String, facilities: List<String>) {
        val db = writableDatabase
        for (facility in facilities) {
            val values = ContentValues()
            values.put(COLUMN_FACILITY_NAME, facility)
            values.put(COLUMN_OWNER_REF, ownerEmail)
            values.put(COLUMN_ROOM_REF, roomId)

            db.insert(TABLE_ROOM_FACILITIES, null, values)
        }
    }

    // this used to load the ad in the recycler view.
    fun getSearchedAdds(universityName: String, roomStatus: String): List<Room> {
        val roomList = mutableListOf<Room>()
        val db = readableDatabase

        val query = """
        SELECT r.$COLUMN_ROOM_TYPE AS roomType, 
               r.$COLUMN_LOCATION AS address, 
               r.$COLUMN_PRICE AS price, 
               r.$COLUMN_NUM_BEDS AS beds,
               r.$COLUMN_ROOM_ID as roomId
        FROM $TABLE_ROOM r
        INNER JOIN $TABLE_OWNER o ON r.$COLUMN_OWNER_REF = o.$COLUMN_EMAIL
        WHERE o.$COLUMN_UNIVERSITY_NAME = ? AND r.$COLUMN_ROOM_STATUS = ?
    """

        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(query, arrayOf(universityName, roomStatus))

            if (cursor.moveToFirst()) {
                do {
                    val roomType = cursor.getString(cursor.getColumnIndexOrThrow("roomType"))
                    val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                    val beds = cursor.getInt(cursor.getColumnIndexOrThrow("beds"))
                    val roomId = cursor.getInt(cursor.getColumnIndexOrThrow("roomId"))

                    roomList.add(
                        Room(
                            roomId = roomId,
                            roomType = roomType,
                            address = address,
                            price = "Rs. $price",
                            beds = beds
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error executing query: ${e.message}")
        } finally {
            cursor?.close()
        }

        return roomList
    }

    // use to load owner ad details in the owner end.
    fun getOwnerRooms(ownerEmail: String): List<Room> {
        val roomList = mutableListOf<Room>()
        val db = readableDatabase
        val query = """
        SELECT $COLUMN_ROOM_TYPE AS roomType,
               $COLUMN_LOCATION AS address,
               $COLUMN_PRICE AS price,
               $COLUMN_NUM_BEDS AS beds,
               $COLUMN_ROOM_ID AS roomId
        FROM $TABLE_ROOM
        WHERE $COLUMN_OWNER_REF = ?
    """
        val cursor = db.rawQuery(query, arrayOf(ownerEmail))

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val roomType = cursor.getString(cursor.getColumnIndexOrThrow("roomType"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val beds = cursor.getInt(cursor.getColumnIndexOrThrow("beds"))
                val roomId = cursor.getInt(cursor.getColumnIndexOrThrow("roomId"))

                roomList.add(
                    Room(
                        roomId =roomId,
                        roomType = roomType,
                        beds = beds,
                        address = address,
                        price = "Rs. $price"
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return roomList
    }

    // check whether given username exists or not
    fun isUsernameExists(username: String): Boolean {
        val db = readableDatabase
        val query = "SELECT $COLUMN_USER_NAME FROM $TABLE_USER WHERE $COLUMN_USER_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // user to check that user is already setup their profile. if not we can pop up the dialog box.
    fun isUserExists(username: String): Boolean {
        val db = readableDatabase
        val query = "SELECT $COLUMN_OWNER_NAME FROM $TABLE_OWNER WHERE $COLUMN_USER_REF = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val exists = cursor.moveToFirst()
        cursor.close()

        if(exists == true){
            return true
        }else{
            return false
        }
    }

    // check that user
    fun isOwnerPostAd(username: String): Boolean {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ROOM_TYPE FROM $TABLE_ROOM WHERE $COLUMN_OWNER_REF = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val exists = cursor.moveToFirst()
        cursor.close()

        if(exists == true){
            return true
        }else{
            return false
        }
    }

    // use to check user credential when sign in.
    fun checkUserCredentials(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USER_NAME = ? AND $COLUMN_USER_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val isValid = cursor.moveToFirst()
        cursor.close()
        return isValid
    }

    // load owner details.
    fun getOwnerDetails(email: String): Map<String, String>? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_OWNER WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        return try {
            if (cursor.moveToFirst()) {
                mapOf(
                    COLUMN_OWNER_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OWNER_NAME)),
                    COLUMN_ADDRESS to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                    COLUMN_MOBILE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILE)),
                    COLUMN_UNIVERSITY_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIVERSITY_NAME)),
                    COLUMN_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    COLUMN_ANY_NOTE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANY_NOTE))
                )
            } else {
                null
            }
        } finally {
            cursor.close()
        }
    }

    // use to update owner details.
    fun updateOwnerDetails(email: String, ownerName: String, address: String, mobile: String, universityName: String, note: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_OWNER_NAME, ownerName)
            put(COLUMN_ADDRESS, address)
            put(COLUMN_MOBILE, mobile)
            put(COLUMN_UNIVERSITY_NAME, universityName)
            put(COLUMN_ANY_NOTE, note)
        }
        val result = db.update(TABLE_OWNER, values, "$COLUMN_EMAIL = ?", arrayOf(email))
        return result > 0
    }

    // query helps to get relevant ad details.
    fun getRoomDetailsWithFacilitiesAndOwner(roomId: Int): Map<String, Any> {
        val db = readableDatabase
        val result = mutableMapOf<String, Any>()

        // Query to get room details
        val roomQuery = """
        SELECT * FROM $TABLE_ROOM WHERE $COLUMN_ROOM_ID = ?
    """
        // Query to get facilities
        val facilitiesQuery = """
        SELECT $COLUMN_FACILITY_NAME FROM $TABLE_ROOM_FACILITIES WHERE $COLUMN_ROOM_REF = ?
    """
        // Query to get owner details
        val ownerQuery = """
        SELECT $COLUMN_OWNER_NAME, $COLUMN_MOBILE, $COLUMN_EMAIL, $COLUMN_ADDRESS
        FROM $TABLE_OWNER WHERE $COLUMN_EMAIL = 
        (SELECT $COLUMN_OWNER_REF FROM $TABLE_ROOM WHERE $COLUMN_ROOM_ID = ?)
    """

        val roomCursor = db.rawQuery(roomQuery, arrayOf(roomId.toString()))
        if (roomCursor.moveToFirst()) {
            result["roomDetails"] = mapOf(
                COLUMN_ROOM_TYPE to roomCursor.getString(roomCursor.getColumnIndexOrThrow(COLUMN_ROOM_TYPE)),
                COLUMN_PRICE to roomCursor.getDouble(roomCursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                COLUMN_LOCATION to roomCursor.getString(roomCursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                COLUMN_DESCRIPTION to roomCursor.getString(roomCursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                COLUMN_NUM_BEDS to roomCursor.getString(roomCursor.getColumnIndexOrThrow(COLUMN_NUM_BEDS)),
            )
        }
        roomCursor.close()

        val facilitiesCursor = db.rawQuery(facilitiesQuery, arrayOf(roomId.toString()))
        val facilities = mutableListOf<String>()
        if (facilitiesCursor.moveToFirst()) {
            do {
                facilities.add(facilitiesCursor.getString(facilitiesCursor.getColumnIndexOrThrow(COLUMN_FACILITY_NAME)))
                Log.e("From place DB", "$facilities")
            } while (facilitiesCursor.moveToNext())
        }
        facilitiesCursor.close()
        result["facilities"] = facilities

        // Fetch owner details
        val ownerCursor = db.rawQuery(ownerQuery, arrayOf(roomId.toString()))
        if (ownerCursor.moveToFirst()) {
            result["ownerDetails"] = mapOf(
                COLUMN_OWNER_NAME to ownerCursor.getString(ownerCursor.getColumnIndexOrThrow(COLUMN_OWNER_NAME)),
                COLUMN_MOBILE to ownerCursor.getString(ownerCursor.getColumnIndexOrThrow(COLUMN_MOBILE)),
                COLUMN_EMAIL to ownerCursor.getString(ownerCursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                COLUMN_ADDRESS to ownerCursor.getString(ownerCursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
            )
        }
        ownerCursor.close()

        return result
    }

    // bellow code use to upload the image into the database .(Under development) ->
//    fun insertAdImage(adId: Int, image: Bitmap): Boolean {
//        val db = writableDatabase
//        val contentValues = ContentValues()
//        val outputStream = ByteArrayOutputStream()
//        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//        val imageBytes = outputStream.toByteArray()
//
//        contentValues.put("ad_id", adId)
//        contentValues.put("image_data", imageBytes)
//
//        val result = db.insert("ad_images", null, contentValues)
//        db.close()
//        return result != -1L
//    }
//
//
//    fun getAdImages(adId: Int): List<Bitmap> {
//        val db = readableDatabase
//        val cursor = db.query(
//            "ad_images",
//            arrayOf("image_data"),
//            "ad_id = ?",
//            arrayOf(adId.toString()),
//            null,
//            null,
//            null
//        )
//
//        val images = mutableListOf<Bitmap>()
//        while (cursor.moveToNext()) {
//            val imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("image_data"))
//            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//            images.add(bitmap)
//        }
//        cursor.close()
//        db.close()
//        return images
//    }




}
