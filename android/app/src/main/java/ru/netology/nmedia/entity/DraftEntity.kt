package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class DraftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
) {
    fun toDto() = Post(id = id, content = content)
}
