package com.byajpay.app.data.repository

import com.byajpay.app.data.dao.UserDao
import com.byajpay.app.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getUserByPhone(phoneNumber: String): User? = userDao.getUserByPhone(phoneNumber)
    
    fun getCurrentUser(): Flow<User?> = userDao.getCurrentUser()
    
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}

