package com.example.evstationmobileapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.evstationmobileapp.models.EVOwner

class UserDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "EVCharging.db"
        private const val DATABASE_VERSION = 1

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_NIC = "nic"
        private const val COLUMN_FIRST_NAME = "first_name"
        private const val COLUMN_LAST_NAME = "last_name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_IS_ACTIVE = "is_active"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_NIC TEXT PRIMARY KEY,
                $COLUMN_FIRST_NAME TEXT NOT NULL,
                $COLUMN_LAST_NAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_IS_ACTIVE INTEGER DEFAULT 1,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        db.execSQL(createUserTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Create new user
    fun createUser(user: EVOwner): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIC, user.nic)
            put(COLUMN_FIRST_NAME, user.firstName)
            put(COLUMN_LAST_NAME, user.lastName)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PHONE, user.phone)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_IS_ACTIVE, if (user.isActive) 1 else 0)
        }

        return try {
            db.insert(TABLE_USERS, null, values) != -1L
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    // Get user by NIC
    fun getUserByNIC(nic: String): EVOwner? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_NIC = ?",
            arrayOf(nic),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = EVOwner(
                nic = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIC)),
                firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // Update user profile (except NIC and activation status)
    fun updateUser(user: EVOwner): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, user.firstName)
            put(COLUMN_LAST_NAME, user.lastName)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PHONE, user.phone)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_UPDATED_AT, "datetime('now')")
        }

        val rowsAffected = db.update(
            TABLE_USERS,
            values,
            "$COLUMN_NIC = ?",
            arrayOf(user.nic)
        )
        db.close()

        return rowsAffected > 0
    }

    // Deactivate user (user can only deactivate themselves)
    fun deactivateUser(nic: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_ACTIVE, 0)
            put(COLUMN_UPDATED_AT, "datetime('now')")
        }

        val rowsAffected = db.update(
            TABLE_USERS,
            values,
            "$COLUMN_NIC = ?",
            arrayOf(nic)
        )
        db.close()

        return rowsAffected > 0
    }

    // Check if email already exists (for registration)
    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_NIC),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Authenticate user login
    fun authenticateUser(email: String, password: String): EVOwner? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ? AND $COLUMN_IS_ACTIVE = 1",
            arrayOf(email, password),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = EVOwner(
                nic = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIC)),
                firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }
}