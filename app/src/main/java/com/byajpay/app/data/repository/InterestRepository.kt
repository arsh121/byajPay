package com.byajpay.app.data.repository

import com.byajpay.app.data.dao.InterestRuleDao
import com.byajpay.app.data.dao.TransactionDao
import com.byajpay.app.data.model.InterestCycle
import com.byajpay.app.data.model.InterestRule
import com.byajpay.app.data.model.Transaction
import com.byajpay.app.data.model.TransactionType
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterestRepository @Inject constructor(
    private val interestRuleDao: InterestRuleDao,
    private val transactionDao: TransactionDao
) {
    suspend fun createInterestRule(
        transactionId: String,
        customerId: String,
        principal: Double,
        rate: Double,
        cycle: InterestCycle,
        startDate: Long
    ): InterestRule {
        val nextAccrualDate = calculateNextAccrualDate(startDate, cycle)
        val rule = InterestRule(
            id = UUID.randomUUID().toString(),
            transactionId = transactionId,
            customerId = customerId,
            principal = principal,
            rate = rate,
            cycle = cycle,
            startDate = startDate,
            lastAccruedDate = startDate,
            nextAccrualDate = nextAccrualDate
        )
        interestRuleDao.insertInterestRule(rule)
        return rule
    }
    
    suspend fun processInterestAccrual() {
        val currentTime = System.currentTimeMillis()
        val dueRules = interestRuleDao.getInterestRulesDueForAccrual(currentTime)
        
        dueRules.forEach { rule ->
            if (rule.isActive) {
                val interestAmount = calculateInterest(
                    principal = rule.principal,
                    rate = rule.rate,
                    cycle = rule.cycle,
                    startDate = rule.lastAccruedDate,
                    endDate = currentTime
                )
                
                if (interestAmount > 0) {
                    // Create interest transaction
                    val interestTransaction = Transaction(
                        id = UUID.randomUUID().toString(),
                        customerId = rule.customerId,
                        amount = interestAmount,
                        type = TransactionType.CREDIT,
                        date = currentTime,
                        notes = "Interest Added (${rule.rate}% ${rule.cycle.name.lowercase()})",
                        isInterestEntry = true,
                        interestRuleId = rule.id
                    )
                    transactionDao.insertTransaction(interestTransaction)
                    
                    // Update rule
                    val nextAccrualDate = calculateNextAccrualDate(currentTime, rule.cycle)
                    interestRuleDao.updateInterestRule(
                        rule.copy(
                            lastAccruedDate = currentTime,
                            nextAccrualDate = nextAccrualDate
                        )
                    )
                }
            }
        }
    }
    
    private fun calculateInterest(
        principal: Double,
        rate: Double,
        cycle: InterestCycle,
        startDate: Long,
        endDate: Long
    ): Double {
        val timeInMillis = endDate - startDate
        val time = when (cycle) {
            InterestCycle.DAILY -> timeInMillis / (1000.0 * 60 * 60 * 24)
            InterestCycle.WEEKLY -> timeInMillis / (1000.0 * 60 * 60 * 24 * 7)
            InterestCycle.MONTHLY -> timeInMillis / (1000.0 * 60 * 60 * 24 * 30)
            InterestCycle.YEARLY -> timeInMillis / (1000.0 * 60 * 60 * 24 * 365)
        }
        // Simple Interest: Principal × Rate × Time / 100
        return principal * rate * time / 100.0
    }
    
    private fun calculateNextAccrualDate(startDate: Long, cycle: InterestCycle): Long {
        val millisToAdd = when (cycle) {
            InterestCycle.DAILY -> 1000L * 60 * 60 * 24
            InterestCycle.WEEKLY -> 1000L * 60 * 60 * 24 * 7
            InterestCycle.MONTHLY -> 1000L * 60 * 60 * 24 * 30
            InterestCycle.YEARLY -> 1000L * 60 * 60 * 24 * 365
        }
        return startDate + millisToAdd
    }
    
    fun getActiveInterestRulesForCustomer(customerId: String) =
        interestRuleDao.getActiveInterestRulesForCustomer(customerId)
    
    suspend fun getInterestRuleByTransactionId(transactionId: String): InterestRule? =
        interestRuleDao.getInterestRuleByTransactionId(transactionId)
}

