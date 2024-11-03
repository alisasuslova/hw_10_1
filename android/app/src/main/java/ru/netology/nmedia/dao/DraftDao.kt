package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.DraftEntity


@Dao
interface DraftDao {
    @Query("SELECT * FROM DraftEntity ORDER BY id DESC")
    fun getAll(): Flow<List<DraftEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: DraftEntity) : Long


    @Query("DELETE FROM DraftEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}
