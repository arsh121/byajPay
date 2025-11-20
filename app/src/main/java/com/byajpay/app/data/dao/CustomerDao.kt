package com.byajpay.app.data.dao

import androidx.room.*
import com.byajpay.app.data.model.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY lastUpdatedAt DESC")
    fun getAllCustomers(): Flow<List<Customer>>
    
    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getCustomerById(customerId: String): Customer?
    
    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY lastUpdatedAt DESC")
    fun searchCustomers(query: String): Flow<List<Customer>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)
    
    @Update
    suspend fun updateCustomer(customer: Customer)
    
    @Delete
    suspend fun deleteCustomer(customer: Customer)
    
    @Query("SELECT * FROM customers")
    suspend fun getAllCustomersSync(): List<Customer>
}

