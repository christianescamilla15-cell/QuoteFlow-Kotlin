package com.christianhernandez.quoteflow.data.remote

// Feed
data class FeedResponse(
    val data: List<QuoteApiModel>,
    val next_cursor: String?,
    val has_more: Boolean,
    val algorithm: String?,
)

data class QuoteApiModel(
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    val lang: String,
    val swipe_dir: String?,
    val pack_name: String?,
    val is_premium: Boolean?,
)

// Swipe
data class SwipeRequest(
    val quote_id: String,
    val direction: String,
    val category: String,
    val dwell_time_ms: Int,
    val source: String = "feed",
)

// Vault
data class VaultResponse(val data: List<VaultItemApi>)

data class VaultItemApi(
    val quote_id: String,
    val text: String,
    val author: String,
    val category: String?,
    val saved_at: String?,
)

data class SaveVaultRequest(val quote_id: String)

// Challenge
data class ChallengeResponse(val data: ChallengeApi?)

data class ChallengeApi(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val active_date: String,
    val progress: Int?,
    val completed: Boolean?,
    val target: Int?,
)

// Progress
data class ProgressResponse(
    val updated: Boolean,
    val progress: Int,
    val completed: Boolean,
)

// Profile
data class ProfileResponse(
    val age_range: String?,
    val interest: String?,
    val goal: String?,
    val preferred_language: String?,
)

data class ProfileRequest(
    val age_range: String?,
    val interest: String?,
    val goal: String?,
    val preferred_language: String?,
)

// Premium
data class PremiumStatusResponse(
    val is_premium: Boolean,
    val trial_active: Boolean?,
    val trial_quotes_remaining: Int?,
    val daily_swipes_remaining: Int?,
    val owned_packs: List<String>?,
    val user_state: String?,
)

// Map
data class MapResponse(
    val current: MapScores?,
    val snapshot: MapScores?,
    val delta: MapScores?,
)

data class MapScores(
    val wisdom: Int?,
    val discipline: Int?,
    val reflection: Int?,
    val philosophy: Int?,
)

// Packs
data class PacksResponse(val packs: List<PackApi>, val user_state: String?)

data class PackPriceApi(val usd: String?, val product_id_android: String?)

data class PackApi(
    val id: String,
    val name: String,
    val description: String?,
    val icon: String?,
    val color: String?,
    val price: PackPriceApi?,
    val quote_count: Int?,
    val is_active: Boolean?,
    val released_at: String?,
    val is_grandfathered: Boolean?,
    val access_status: String?,
)

data class PackPreviewResponse(val quotes: List<QuoteApiModel>, val pack: PackApi?, val is_preview_complete: Boolean?)

data class PackFeedResponse(
    val data: List<QuoteApiModel>,
    val next_cursor: String?,
    val has_more: Boolean,
)

// Map Snapshot
data class SnapshotResponse(
    val saved: Boolean?,
    val snapshot: MapScores?,
)

// Weekly Insight
data class WeeklyInsightResponse(
    val reflection: String?,
    val dominant_category: String?,
)
