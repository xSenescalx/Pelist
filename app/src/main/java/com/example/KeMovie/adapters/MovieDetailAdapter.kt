package com.example.KeMovie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.KeMovie.R
import com.example.KeMovie.network.Genre

//Adapter para los generos de las peliculas seleccionadas

class MovieDetailAdapter(val genreList: List<Genre>): RecyclerView.Adapter<MovieDetailAdapter.MainViewHolder>() {

    inner class MainViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindData(genre: Genre){
            val genreText = itemView.findViewById<TextView>(R.id.genreText)
            genreText.text = genre.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_genre_card,parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(genreList[position])
    }

    override fun getItemCount(): Int {
        return genreList.size
    }

}