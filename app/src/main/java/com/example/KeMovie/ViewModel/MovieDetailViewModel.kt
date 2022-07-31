package com.example.KeMovie.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.KeMovie.RepositoryDetail
import com.example.KeMovie.ScreenState
import com.example.KeMovie.network.*
import retrofit2.Call
import retrofit2.Response

class MovieDetailViewModel (private val repository: RepositoryDetail
                            = RepositoryDetail(ApiClient.apiServiceDetailMovie)
): ViewModel() {

    //Inicializo variables LiveData
    private val API_KEY = "cd8a36fbb06bbad9356d49a1a8e6bc3a"
    var _movieLiveData = MutableLiveData<ScreenState<DetailMovieResponse?>>()
    val movieLiveData: LiveData<ScreenState<DetailMovieResponse?>>
        get() = _movieLiveData

    //Guardo el id de la pelicula seleccionada
    var idValue = MutableLiveData<Int>()

    //Buscador de peliculas por pagina
    fun fetchMovie() {
        val client = ApiClient.apiServiceDetailMovie.fetchMovie(idValue.value.toString(), API_KEY)
        //Se le avisa al MoviesLiveData que esta en null para que entre en estado "Loading"
        _movieLiveData.postValue(ScreenState.Loading(null))
        //Se hace el llamado a la api y se obtienen los resultados en el DetailMovieResponse
        client.enqueue(object : retrofit2.Callback<DetailMovieResponse> {

            override fun onResponse(
                call: Call<DetailMovieResponse>,
                response: Response<DetailMovieResponse>
            ) {
                if (response.isSuccessful) {
                    _movieLiveData.postValue(ScreenState.Success(response.body()?.copy()))
                } else {
                    _movieLiveData.postValue(ScreenState.Error(response.code()?.toString(), null))
                }
            }

            override fun onFailure(call: Call<DetailMovieResponse>, t: Throwable) {

                _movieLiveData.postValue(ScreenState.Error(t.message.toString(), null))
            }

        })
    }
}