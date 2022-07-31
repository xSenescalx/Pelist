package com.example.KeMovie.network

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class Movie (
    @Json(name="id")
    val id: Int,
    @Json(name="title")
    val name:String,
    @Json(name="poster_path")
    val image:String
        ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}


data class MovieResponse(@Json(name = "results")
                         val result: List<Movie>)

data class Genre(@Json(name="name")
                 val name:String)

@JsonClass(generateAdapter = true)
data class DetailMovieResponse(@Json(name="id")
                               val id: Int,
                               @Json(name="title")
                               val name:String?,
                               @Json(name="poster_path")
                               val imagePoster:String?,
                               @Json(name="backdrop_path")
                               val imageBackdrop:String?,
                               @Json(name="release_date")
                               val release_date:String?,
                               @Json(name="budget")
                               val budget:Int?,
                               @Json(name="overview")
                               val overview:String?,
                               @Json(name="revenue")
                               val revenue:Int?,
                               @Json(name="runtime")
                               val runtime:Int?,
                               @Json(name="original_language")
                               val original_language:String?,
                               @Json(name="vote_average")
                               val vote_average:Float?,
                               @Json(name="vote_count")
                               val vote_count:Int?,
                               @Json(name="genres")
                               val genres:List<Genre>)
