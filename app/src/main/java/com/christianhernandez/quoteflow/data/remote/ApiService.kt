package com.christianhernandez.quoteflow.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("quotes/feed")
    suspend fun getFeed(
        @Query("lang") lang: String = "en",
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null,
    ): FeedResponse

    @POST("swipes")
    suspend fun recordSwipe(@Body event: SwipeRequest): Response<Unit>

    @GET("vault")
    suspend fun getVault(): VaultResponse

    @POST("vault")
    suspend fun saveToVault(@Body body: SaveVaultRequest): Response<Unit>

    @DELETE("vault/{quoteId}")
    suspend fun removeFromVault(@Path("quoteId") quoteId: String): Response<Unit>

    @GET("challenges/today")
    suspend fun getTodayChallenge(@Query("lang") lang: String = "en"): ChallengeResponse

    @POST("challenges/{id}/progress")
    suspend fun incrementProgress(@Path("id") id: String): ProgressResponse

    @GET("profile")
    suspend fun getProfile(): ProfileResponse?

    @POST("profile")
    suspend fun updateProfile(@Body body: ProfileRequest): Response<Unit>

    @GET("premium/status")
    suspend fun getPremiumStatus(): PremiumStatusResponse

    @GET("map")
    suspend fun getPhilosophyMap(): MapResponse

    @POST("map/snapshot")
    suspend fun saveMapSnapshot(): SnapshotResponse

    @GET("packs")
    suspend fun getPacks(): PacksResponse

    @GET("packs/{id}/preview")
    suspend fun getPackPreview(@Path("id") id: String, @Query("lang") lang: String = "en"): PackPreviewResponse

    @GET("packs/{id}/feed")
    suspend fun getPackFeed(
        @Path("id") id: String,
        @Query("cursor") cursor: String? = null,
    ): PackFeedResponse

    @GET("insights/weekly")
    suspend fun getWeeklyInsight(@Query("lang") lang: String = "en"): WeeklyInsightResponse
}
