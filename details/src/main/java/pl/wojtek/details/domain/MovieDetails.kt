package pl.wojtek.details.domain

/**
 *
 */


data class MovieDetails(val title: String, val imageUrl: String?, val date: String, val description: String, val vote: String, val isFavourite: Boolean)