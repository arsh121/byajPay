package com.byajpay.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.byajpay.app.data.dao.CustomerDao
import com.byajpay.app.data.dao.InterestRuleDao
import com.byajpay.app.data.dao.TransactionDao
import com.byajpay.app.data.dao.UserDao
import com.byajpay.app.data.model.Customer
import com.byajpay.app.data.model.InterestRule
import com.byajpay.app.data.model.Transaction
import com.byajpay.app.data.model.User

@Database(
    entities = [User::class, Customer::class, Transaction::class, InterestRule::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
    abstract fun interestRuleDao(): InterestRuleDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "byajpay_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

