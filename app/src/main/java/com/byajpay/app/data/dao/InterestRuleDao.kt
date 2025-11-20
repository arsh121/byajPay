package com.byajpay.app.data.dao

import androidx.room.*
import com.byajpay.app.data.model.InterestRule
import kotlinx.coroutines.flow.Flow

@Dao
interface InterestRuleDao {
    @Query("SELECT * FROM interest_rules WHERE customerId = :customerId AND isActive = 1")
    fun getActiveInterestRulesForCustomer(customerId: String): Flow<List<InterestRule>>
    
    @Query("SELECT * FROM interest_rules WHERE isActive = 1 AND nextAccrualDate <= :currentTime")
    suspend fun getInterestRulesDueForAccrual(currentTime: Long): List<InterestRule>
    
    @Query("SELECT * FROM interest_rules WHERE transactionId = :transactionId")
    suspend fun getInterestRuleByTransactionId(transactionId: String): InterestRule?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterestRule(rule: InterestRule)
    
    @Update
    suspend fun updateInterestRule(rule: InterestRule)
    
    @Delete
    suspend fun deleteInterestRule(rule: InterestRule)
    
    @Query("SELECT * FROM interest_rules")
    suspend fun getAllInterestRulesSync(): List<InterestRule>
}

