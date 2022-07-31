package com.example.KeMovie.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.KeMovie.Repository
import com.example.KeMovie.ScreenState
import com.example.KeMovie.network.ApiClient
import com.example.KeMovie.network.Movie
import com.example.KeMovie.network.MovieResponse
import retrofit2.Call
import retrofit2.Response


class MainViewModel(private val repository: Repository
                    = Repository(ApiClient.apiService)
): ViewModel() {

    //Inicializo variables LiveData
    private var _moviesLiveData = MutableLiveData<ScreenState<List<Movie>?>>()
    val movieLiveData:LiveData<ScreenState<List<Movie>?>>
    get() = _moviesLiveData

    var pageValue = MutableLiveData<Int>(1)
    var listOfMovies: MutableList<Movie> = mutableListOf()


    init{
        //Inicializo la busqueda de peliculas de la primer pagina
        fetchMovie(pageValue.value.toString())
    }
    //Buscador de peliculas por pagina
    fun fetchMovie(page: String){
        //Llama a la api
        val client = ApiClient.apiService.fetchMovies(page)
        //Se le avisa al MoviesLiveData que esta en null para que entre en estado "Loading"
        _moviesLiveData.postValue(ScreenState.Loading(null))

        //Se hace el llamado a la api y se obtienen los resultados en el Movie Response
        client.enqueue(object : retrofit2.Callback<MovieResponse>{
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ){
                if (response.isSuccessful){
                    response.body()?.result?.forEach { movie ->
                        listOfMovies.add(movie)
                    }
                    _moviesLiveData.postValue(ScreenState.Success(listOfMovies))
                }else{
                    _moviesLiveData.postValue(ScreenState.Error(response.code()?.toString(), null))
                }
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable){
                _moviesLiveData.postValue(ScreenState.Error(t.message.toString(), null))
            }

        })
    }
}