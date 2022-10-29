package com.example.jetnews.data.adapter

import com.example.jetnews.R
import com.example.jetnews.data.dao.PostData
import com.example.jetnews.model.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Utility functions for transforming post data
 */


/**
 * Convert list of post data items to post for display
 */
fun convertPostDataListToDisplayPosts(postData: List<PostData>?): List<Post> {
    val posts: MutableList<Post> = mutableListOf()
    if (postData == null) {
        return posts
    }
    for (item in postData) {
        posts.add(convertPostDataToDisplayPost(item))
    }
    return posts
}

private fun convertPostDataToDisplayPost(item: PostData): Post {
    if (item.author == null) {
        item.author = " "
    }
    return Post(
            id = item.title,
            title = item.title,
            metadata = Metadata(
                author = PostAuthor(item.author!!),
                date = item.pubDate!!,
                readTimeMinutes = calculateReadTime(item.text)
            ),
            imageURL = item.imageURL,
            imageThumbId = R.drawable.post_1_thumb,
            paragraphs = listOf(
                Paragraph(
                    type = ParagraphType.Text,
                    text = item.text!!
                )
            ),
            url = item.url
    )
}

/**
 * Calculates read time, done for fun not for accuracy
 */
private fun calculateReadTime(postText: String?): Int {
    return if (postText != null) postText.length / 10 else 1
}

/**
 * Convert API results for entry to database
 */
fun convertFeedItemsToPostDataList(items: List<Item>?): List<PostData> {
    val posts: MutableList<PostData> = mutableListOf()
    if (items == null) {
        return posts
    }
    for (item in items) {
        posts.add(convertFeedItemToPostData(item))
    }
    return posts
}

private fun convertFeedItemToPostData(item: Item): PostData {
    if (item.creator == null) {
        item.creator = " "
    }
    if (item.mediaContent == null) {
        item.mediaContent = MediaContent()
    }
    val date = convertRssDateToAppDate(item.pubDate!!)
    return PostData(
        title = item.title!!,
        author = item.creator,
        date = date.toEpochSecond(ZoneOffset.UTC),
        pubDate = convertAppDateToDisplayDate(date),
        imageURL = item.mediaContent!!.url,
        text = item.description!!,
        url = item.link
    )
}

private fun convertRssDateToAppDate(pubDate: String): LocalDateTime {
    val rfcFormatter = DateTimeFormatter.RFC_1123_DATE_TIME
    return LocalDateTime.parse(pubDate, rfcFormatter)
}

private fun convertAppDateToDisplayDate(date: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("E yyyy-MM-dd hh:mm:ss a")
    val zoneId = ZoneId.of("America/Los_Angeles")
    val zdt: ZonedDateTime = date.atZone(zoneId)
    return zdt.format(formatter)
}
