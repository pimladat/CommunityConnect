package com.example.jetnews.data.posts.impl

import android.content.Context
import androidx.room.Room
import com.example.jetnews.R
import com.example.jetnews.data.AppDatabase
import com.example.jetnews.data.Result
import com.example.jetnews.data.adapter.convertFeedItemsToPostDataList
import com.example.jetnews.data.adapter.convertPostDataListToDisplayPosts
import com.example.jetnews.data.dao.PostData
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.service.NewsApi
import com.example.jetnews.data.service.RetrofitClient
import com.example.jetnews.model.*
import com.example.jetnews.utils.addOrRemove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


@OptIn(ExperimentalCoroutinesApi::class)
class RssPostsRepository(appContext: Context) : PostsRepository {
    // TODO: Create config file for RSS urls
    val url = "https://www.sandiegouniontribune.com/news/rss2.0.xml";
    private val favorites = MutableStateFlow<Set<String>>(setOf())
    private val retrofit = RetrofitClient().get()
    private val newsApi: NewsApi = retrofit.create(NewsApi::class.java)
    private val db = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "AppDatabase"
    ).build()

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val postData : PostData = db.postsDao().getPost(postId!!)
            Result.Success(convertPostDataListToDisplayPosts(listOf(postData))[0]);
        }
    }

    /**
     * Filter posts by search
     */
    override suspend fun getSearchedPostsFeed(searchInput: String): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            val postsData = db.postsDao().filterPosts(searchInput = "%$searchInput%")
            val postList: List<Post> = convertPostDataListToDisplayPosts(postsData)
            val postsFeed = PostsFeed(
                emptyPost,
                postList,
            )
            Result.Success(postsFeed);
        }
    }

    /**
     * Filter posts to show only favorited posts
     */
    override suspend fun getFavoritedPostsFeed(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            val postsData = db.postsDao().getMostRecentFavorites(100)
            val postList: List<Post> = convertPostDataListToDisplayPosts(postsData)
            val postsFeed = PostsFeed(
                emptyPost,
                postList,
            )
            Result.Success(postsFeed);
        }
    }

    /**
     * Return favorite set to UI
     */
    override fun observeFavorites(): Flow<Set<String>> {
        return favorites
    }

    /**
     * Toggle favorite post on and off
     */
    override suspend fun toggleFavorite(postId: String) {
        // Get and set favorite data
        val post = db.postsDao().getPost(postId)
        post.isFavorite = post.isFavorite != true
        db.postsDao().insertPostData(post)
        val favoriteSet = favorites.value.toMutableSet()
        if (post.isFavorite!!) {
            favoriteSet.add(postId);
        } else {
            favoriteSet.remove(postId);
        }
        favorites.value = favoriteSet
    }

    /**
     * Gets RSS feed and stores in database, then updates display list
     */
    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            // Grab RSS from API and store in DDB. Any new links are deduped into app db.
            val feed = newsApi.getRssFeed(url)?.execute();
            val feedItems: List<Item>? = feed?.body()?.channels?.get(0)?.items
            val postData: List<PostData> = convertFeedItemsToPostDataList(feedItems)
            db.postsDao().insertPosts(postData)
            // Get 100 most recent posts. 100 is an arbitrary number
            val mostRecentPosts: List<PostData> = db.postsDao().getMostRecent(100);
            val displayPosts: List<Post> = convertPostDataListToDisplayPosts(mostRecentPosts)
            // TODO: Edge case of having 0 posts should be handled
            val headlinePost = displayPosts[0]
            val postsFeed = PostsFeed(
                headlinePost,
                displayPosts.subList(1, displayPosts.size),
            )
            Result.Success(postsFeed)
        }
    }

    override suspend fun updateFavoritesDisplay() {
        val favoritedPosts = db.postsDao().getMostRecentFavorites(100)
        val favoriteSet = favorites.value.toMutableSet()
        favorites.value = favoriteSet
        favoritedPosts.forEach {
            postData -> favoriteSet.add(postData.title);
        }
        favorites.value = favoriteSet
    }
}