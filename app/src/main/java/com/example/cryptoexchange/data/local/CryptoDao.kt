package com.example.cryptoexchange.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CryptoDao {

    @Query("SELECT * FROM cryptos ORDER BY position ASC")
    abstract fun observeAll(): Flow<List<CryptoEntity>>

    @Query("DELETE FROM cryptos")
    abstract suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(items: List<CryptoEntity>)

    @Transaction
    open suspend fun replaceAll(items: List<CryptoEntity>) {
        clear()
        insertAll(items)
    }
}