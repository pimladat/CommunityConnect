package com.example.jetnews.data.dao

import androidx.room.*

@Dao
interface PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostData(vararg postData: PostData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @JvmSuppressWildcards
    fun insertPosts(posts: List<PostData>)

    @Delete
    fun delete(post: PostData)

    @Query("SELECT * FROM posts ORDER BY date DESC LIMIT :numPosts")
    suspend fun getMostRecent(numPosts: Int): List<PostData>

    @Query("SELECT * FROM posts WHERE isFavorite = 1 ORDER BY date DESC LIMIT :numPosts")
    suspend fun getMostRecentFavorites(numPosts: Int): List<PostData>

    @Query("SELECT * FROM posts WHERE title = :title")
    suspend fun getPost(title: String): PostData

    @Query("SELECT * FROM posts where LOWER(title) like LOWER(:searchInput)")
    suspend fun filterPosts(searchInput: String): List<PostData>
}
