package com.example.wisewallet

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.DecimalFormat

class WalletDatabaseHelper(context: Context):
    SQLiteOpenHelper(context,DATABASE_NAME,null,1) {
    companion object{
        private const val DATABASE_NAME = "wallet.db"
        private const val TABLE_NAME = "myWallet"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT , $COLUMN_TYPE TEXT,$COLUMN_CATEGORY TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_AMOUNT DOUBLE,$COLUMN_DATE TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME "
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertWallet( wallet: Wallet){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TYPE,wallet.type)
            put(COLUMN_CATEGORY,wallet.category)
            put(COLUMN_DESCRIPTION,wallet.description)
            put(COLUMN_AMOUNT,wallet.amount)
            put(COLUMN_DATE,wallet.date)
        }
        db.insert(TABLE_NAME,null,values)
        db.close()
    }

    fun getWallet(): List<Wallet>{
        val walletList = mutableListOf<Wallet>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DATE DESC"
        val cursor = db.rawQuery(query,null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val description  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val amount  = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
            val date  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

            val wallet = Wallet(id, type, category, description, amount, date)
            walletList.add(wallet)
        }
        cursor.close()
        db.close()
        return walletList
    }

    fun updateWallet(wallet: Wallet){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TYPE,wallet.type)
            put(COLUMN_CATEGORY,wallet.category)
            put(COLUMN_DESCRIPTION,wallet.description)
            put(COLUMN_AMOUNT,wallet.amount)
            put(COLUMN_DATE,wallet.date)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(wallet.id.toString())
        db.update(TABLE_NAME,values,whereClause,whereArgs)
        db.close()
    }

    fun getWalletByID(walletId: Int):Wallet{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $walletId"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
        val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
        val description  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
        val amount  = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
        val date  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

        cursor.close()
        db.close()
        return  Wallet(id, type, category, description, amount, date)
    }

    fun deleteWallet(walletId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(walletId.toString())
        db.delete(TABLE_NAME,whereClause,whereArgs)
        db.close()
    }

    fun getBalance(): Double {
        var totalIncome = 0.0
        var totalExpense = 0.0
        val db = readableDatabase

        val incomeQuery = "SELECT SUM($COLUMN_AMOUNT) AS total FROM $TABLE_NAME WHERE $COLUMN_TYPE = 'Income'"
        val incomeCursor = db.rawQuery(incomeQuery, null)

        if (incomeCursor.moveToFirst()) {
            totalIncome = incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow("total"))
        }
        incomeCursor.close()

        val expenseQuery = "SELECT SUM($COLUMN_AMOUNT) AS total FROM $TABLE_NAME WHERE $COLUMN_TYPE = 'Expense'"
        val expenseCursor = db.rawQuery(expenseQuery, null)

        if (expenseCursor.moveToFirst()) {
            totalExpense = expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow("total"))
        }
        expenseCursor.close()

        db.close()

        val totalBalance = totalIncome - totalExpense

        return String.format("%.2f", totalBalance).toDouble()
    }

    fun retrieveCategoryTotals(): Map<String, Double> {
        val categoryTotals = mutableMapOf<String, Double>()
        val db = readableDatabase
        val query = "SELECT $COLUMN_CATEGORY, SUM($COLUMN_AMOUNT) AS total FROM $TABLE_NAME WHERE $COLUMN_TYPE = 'Expense' GROUP BY $COLUMN_CATEGORY"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            categoryTotals[category] = total
        }

        cursor.close()
        db.close()
        return categoryTotals
    }

    fun getWallet(Wallettype :String): List<Wallet>{
        val walletList = mutableListOf<Wallet>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TYPE = ? ORDER BY $COLUMN_DATE DESC "
        //val cursor = db.rawQuery(query,null)
        val cursor = db.rawQuery(query, arrayOf(Wallettype))

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val description  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val amount  = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
            val date  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

            val wallet = Wallet(id, type, category, description, amount, date)
            walletList.add(wallet)
        }
        cursor.close()
        db.close()
        return walletList
    }
}