package com.byajpay.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @PrimaryKey
    val id: String,
    val customerId: String,
    val amount: Double,
    val type: TransactionType,
    val date: Long,
    val notes: String? = null,
    val isInterestEntry: Boolean = false,
    val interestRuleId: String? = null, // Reference to interest rule if this is an interest entry
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long? = null
)

enum class TransactionType {
    CREDIT, // Money given (increases outstanding)
    DEBIT   // Money received (reduces outstanding)
}

