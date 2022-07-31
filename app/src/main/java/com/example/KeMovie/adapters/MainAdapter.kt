package com.example.KeMovie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.KeMovie.R
import com.example.KeMovie.network.Movie

//Adapter para el recyclerView principal del MainActivity

class MainAdapter(val moviesList: List<Movie>): RecyclerView.Adapter<MainAdapter.MainViewHolder>(){

    var onItemClick : ((Movie)-> Unit)? = null

    inner class MainViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindData(movie: Movie){
            val name = itemView.findViewById<TextView>(R.id.name)
            val image = itemView.findViewById<ImageView>(R.id.image)

            name.text = movie.name
            image.load("https://image.tmdb.org/t/p/w500"+ movie.image){
                transformations(RoundedCornersTransformation())
            }
            itemView.setOnClickListener {
                onItemClick?.invoke(moviesList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_movie_card,parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(moviesList[position])
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }
}