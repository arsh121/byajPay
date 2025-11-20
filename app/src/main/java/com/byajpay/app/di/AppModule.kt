package com.byajpay.app.di

import android.content.Context
import androidx.room.Room
import com.byajpay.app.data.dao.*
import com.byajpay.app.data.database.AppDatabase
import com.byajpay.app.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "byajpay_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
    
    @Provides
    fun provideCustomerDao(database: AppDatabase): CustomerDao = database.customerDao()
    
    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()
    
    @Provides
    fun provideInterestRuleDao(database: AppDatabase): InterestRuleDao = database.interestRuleDao()
    
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepository(userDao)
    
    @Provides
    @Singleton
    fun provideCustomerRepository(
        customerDao: CustomerDao,
        transactionDao: TransactionDao
    ): CustomerRepository = CustomerRepository(customerDao, transactionDao)
    
    @Provides
    @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository =
        TransactionRepository(transactionDao)
    
    @Provides
    @Singleton
    fun provideInterestRepository(
        interestRuleDao: InterestRuleDao,
        transactionDao: TransactionDao
    ): InterestRepository = InterestRepository(interestRuleDao, transactionDao)
}

