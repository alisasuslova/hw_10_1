package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.flow.combine
import okio.IOException
import retrofit2.http.POST
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.DraftDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.DraftEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val draftDao: DraftDao
) : PostRepository {
    override val data = draftDao.getAll().combine(postDao.getAll()) { drafts, posts ->
        drafts.map { it.toDto() } + posts.map { it.toDto() }
    }.asLiveData()

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val id = draftDao.insert(DraftEntity(content = post.content))
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            draftDao.removeById(id)
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {

        postDao.removeById(id)

        try{
            
            val response = PostsApi.service.removeById(id)


            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {


        //бд
        val entity = postDao.getById(id)

        val updated = entity.copy(
            likedByMe = !entity.likedByMe,
            likes = if (entity.likedByMe) entity.likes - 1 else entity.likes + 1
        )

        postDao.insert(updated)

        try {

            //запрос
            val response = if (entity.likedByMe) {
                PostsApi.service.dislikeById(id)
            } else {
                PostsApi.service.likeById(id)
            }

            entity.toDto()

            //проверяем ответ
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            //запись в базу
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            postDao.insert(entity)
            throw NetworkError
        } catch (e: Exception) {
            postDao.insert(entity)
            throw UnknownError
        }


    }
}
