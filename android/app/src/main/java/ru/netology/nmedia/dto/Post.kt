package ru.netology.nmedia.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val authorAvatar: String= "",
    val content: String= "",
    val published: String= "",
    val likedByMe: Boolean = false,
    val likes: Int = 0,
)

