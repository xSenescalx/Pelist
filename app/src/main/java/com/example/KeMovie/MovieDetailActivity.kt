package com.example.KeMovie

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.KeMovie.databinding.ActivityMovieDetailBinding
import com.example.KeMovie.ViewModel.MovieDetailViewModel
import com.example.KeMovie.adapters.MovieDetailAdapter
import com.example.KeMovie.network.DetailMovieResponse
import com.example.KeMovie.network.Movie
import com.google.android.material.snackbar.Snackbar

class MovieDetailActivity : AppCompatActivity() {

    private val viewModel: MovieDetailViewModel by lazy {
        ViewModelProvider(this).get(MovieDetailViewModel::class.java)
    }
    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtengo la info desde el MainActivity sobre la pelicula que se selecciono
        val movie = intent.getParcelableExtra<Movie>("movie")

        //Agrego un boton de regreso al MainActivity y cambio el titulo del action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Pe-List: ${movie?.name}!"

        getData()

        viewModel.movieLiveData.observe(this,{ state ->
            processMoviesResponse(state)
        })

    }
    //Funcion para cargar el id de la pelicula dentro del MovieDetailViewModel ya que si lo hacia
    //dentro del onCreate no se llegaba a inicializar.
    private fun getData(){

        val movie = intent.getParcelableExtra<Movie>("movie")
        viewModel.idValue.value = movie?.id
        viewModel.fetchMovie()

    }

    //Con el processMoviesResponse chequeo el estado de la lista de peliculas
    //Loading -> no hay peliculas guardadas todavia en la lista
    //Success -> se carga una lista de peliculas
    //Error -> ocurre un error y emite un mensaje
    private fun processMoviesResponse(state: ScreenState<DetailMovieResponse?>){
        when(state){
            is ScreenState.Loading ->{
                binding.progressBar.visibility = View.VISIBLE
            }

            is ScreenState.Success ->{
                binding.progressBar.visibility = View.GONE
                if (state.data != null){

                    //Preparo todo para inicializar el RecyclerView y
                    // cargo el adapter con la data ubicada en state
                    val data = state.data
                    checkData(data)

                    val adapter = MovieDetailAdapter(data.genres)
                    val recyclerView = findViewById<RecyclerView>(R.id.genreRV)
                    recyclerView?.layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
                    recyclerView?.adapter = adapter

                }
            }
            is ScreenState.Error ->{
                binding.progressBar.visibility = View.GONE
                val view = binding.progressBar.rootView
                Snackbar.make(view,state.message!!, Snackbar.LENGTH_INDEFINITE).show()
            }
        }
    }

    //Checkeo q los datos existan, si existen, los cargo en el View
    //En el caso de que no existan, oculto del View ese campo
    @SuppressLint("SetTextI18n")
    private fun checkData(data: DetailMovieResponse){
        binding.imageBg.load("https://image.tmdb.org/t/p/w1280"+ data.imageBackdrop){
            transformations(RoundedCornersTransformation())
        }

        if (data.name != null && data.name != ""){
            binding.movieName.text = data.name
        }else{
            binding.movieName.visibility = View.GONE
        }

        if (data.release_date != null && data.release_date != ""){
            binding.movieRelease.text = "Release Date: " + data.release_date
        }else{
            binding.movieRelease.visibility = View.GONE
        }

        if (data.overview != null && data.overview != ""){
            binding.overview.text = data.overview
        }else{
            binding.overviewLl.visibility = View.GONE
        }

        if (data.budget != null && data.budget != 0){
            binding.budget.text = data.budget.toString() + " $"
        }else{
            binding.budgetLl.visibility = View.GONE
        }

        if (data.revenue != null && data.revenue != 0){
            binding.revenue.text = data.revenue.toString() + " $"
        }else{
            binding.revenueLl.visibility = View.GONE
        }

        if (data.runtime != null && data.runtime != 0){
            binding.runtime.text = data.runtime.toString() + " Min"
        }else{
            binding.runtimeLl.visibility = View.GONE
        }

        if (data.original_language != null && data.original_language != ""){
            binding.language.text = data.original_language.toString()
        }else{
            binding.langLl.visibility = View.GONE
        }

        if (data.vote_average != null){
            if (data.vote_average.toString() != "0.0"){
                binding.voteAvg.text = data.vote_average.toString() + " / 10"
            }else {
                binding.voteAvgLl.visibility = View.GONE
            }
        }

        if (data.vote_count != null && data.vote_count != 0){
            binding.voteCount.text = data.vote_count.toString()
        }else{
            binding.voteCountLl.visibility = View.GONE
        }

        if (data.imagePoster != null && data.imagePoster != ""){
            binding.imagePoster.load("https://image.tmdb.org/t/p/w1280"+ data.imagePoster){
                transformations(RoundedCornersTransformation())
            }
        }else{
            binding.posterLl.visibility = View.GONE
        }
    }
}