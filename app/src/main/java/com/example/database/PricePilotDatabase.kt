package com.example.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val imageUrl: String,
    val currentPrice: Double,
    val lowestPrice: Double,
    val storeName: String,
    val productUrl: String,
    val priceDropStatus: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_comparisons")
data class RecentComparisonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val queryOrUrl: String,
    val title: String,
    val lowestPrice: Double,
    val storeName: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist ORDER BY addedTimestamp DESC")
    fun getAllWishlist(): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeWishlist(productId: String)

    @Query("DELETE FROM wishlist")
    suspend fun clearWishlist()

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId)")
    fun isWishlisted(productId: String): Flow<Boolean>
}

@Dao
interface RecentComparisonDao {
    @Query("SELECT * FROM recent_comparisons ORDER BY timestamp DESC LIMIT 20")
    fun getAllRecents(): Flow<List<RecentComparisonEntity>>

    @Query("DELETE FROM recent_comparisons WHERE LOWER(TRIM(queryOrUrl)) = LOWER(TRIM(:queryOrUrl))")
    suspend fun removeDuplicateRecent(queryOrUrl: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(item: RecentComparisonEntity)

    @Query("DELETE FROM recent_comparisons")
    suspend fun clearRecents()
}

@Database(entities = [WishlistEntity::class, RecentComparisonEntity::class], version = 1, exportSchema = false)
abstract class PricePilotDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun recentComparisonDao(): RecentComparisonDao

    companion object {
        @Volatile
        private var INSTANCE: PricePilotDatabase? = null

        fun getDatabase(context: Context): PricePilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PricePilotDatabase::class.java,
                    "price_pilot_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
