package com.byajpay.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "interest_rules",
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InterestRule(
    @PrimaryKey
    val id: String,
    val transactionId: String, // The credit transaction this rule applies to
    val customerId: String,
    val principal: Double, // Original amount
    val rate: Double, // Interest rate percentage
    val cycle: InterestCycle,
    val startDate: Long,
    val lastAccruedDate: Long, // Last time interest was added
    val nextAccrualDate: Long, // Next time interest should be added
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long? = null
)

enum class InterestCycle {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

