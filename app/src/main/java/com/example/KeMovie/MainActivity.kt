package com.example.KeMovie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.KeMovie.ViewModel.MainViewModel
import com.example.KeMovie.adapters.MainAdapter
import com.example.KeMovie.databinding.ActivityMainBinding
import com.example.KeMovie.network.Movie
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }


    private lateinit var binding: ActivityMainBinding
    private lateinit var newArrayList: ArrayList<Movie>
    private lateinit var tempArrayList: ArrayList<Movie>
    private var numPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Cambio el titulo del action bar
        supportActionBar!!.title = "Pe-List for the night "

        //Observo del MainViewModel si hay cambios en los datos de movieLiveData
        viewModel.movieLiveData.observe(this,{ state ->
                processMoviesResponse(state)
        })

        //Inicializo los dos arrays donde voy a guardar las peliculas encontradas por el buscador
        newArrayList = arrayListOf<Movie>()
        tempArrayList = arrayListOf<Movie>()

    }


    //Con el processMoviesResponse chequeo el estado de la lista de peliculas
    //Loading -> no hay peliculas guardadas todavia en la lista
    //Success -> se carga una lista de peliculas
    //Error -> ocurre un error y emite un mensaje
    private fun processMoviesResponse(state: ScreenState<List<Movie>?>){
        when(state){
            is ScreenState.Loading ->{
                binding.progressBar.visibility = View.VISIBLE
            }
            is ScreenState.Success ->{
                binding.progressBar.visibility = View.GONE
                if (state.data != null){

                    //Preparo todo para inicializar el RecyclerView y
                    // cargo el adapter con la data ubicada en state
                    val layoutManager = GridLayoutManager(this, 2)
                    val adapter = MainAdapter(state.data)
                    val recyclerView = findViewById<RecyclerView>(R.id.moviesRv)

                    //Chequeo en que pagina estamos ubicados con el mutableLiveData "pageValue"
                    //del MainViewModel para que una vez creado el recyclerView, la vista se ubique
                    //nuevamente donde se encontraba parada
                    if (viewModel.pageValue.value == 1){
                        recyclerView?.layoutManager =
                            layoutManager
                        recyclerView?.adapter = adapter

                    }else{
                        recyclerView?.layoutManager =
                            layoutManager
                        recyclerView?.adapter = adapter
                        binding.progressBar.visibility = View.VISIBLE
                        recyclerView.scrollToPosition(((((viewModel.pageValue.value!!)-1)* 20) - 4))
                        binding.progressBar.visibility = View.GONE

                    }

                    //Click en una imagen para cambiar de Activity
                    //Se pasa la info (id) de ese elemento a la proxima Activity
                    adapter.onItemClick = {
                        val intent = Intent(this, MovieDetailActivity::class.java)
                        intent.putExtra("movie", it)
                        startActivity(intent)
                    }

                    //Se pone un contador antes de activar la funcion para darle un tiempo a cargar
                    //al recyclerView.(Si no, encuentra el ultimo elemento del RV antes de que se llegue a cargar
                    // cualquier imagen y entra en un loop de busqueda en la api)
                    Handler().postDelayed({
                        getToBottomPage(layoutManager, adapter)
                    }, 2000)

                    //carga las nuevas peliculas en los ArrayLists
                    getData()
                }
            }
            is ScreenState.Error ->{
                binding.progressBar.visibility = View.GONE
                val view = binding.progressBar.rootView
                Snackbar.make(view,state.message!!, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    //Limpia y carga los ArrayLists
    private fun getData(){

        newArrayList.clear()
        tempArrayList.clear()
        newArrayList.addAll(viewModel.listOfMovies)
        tempArrayList.addAll(viewModel.listOfMovies)
    }

    //Maneja los scrolls dentro del recyclerView
    //Detecta en que posicion se encuentra el usuario y si llega a tocar el final del recyclerView
    //Incrementa el num de paginas del MainViewModel y carga mas peliculas
    private fun getToBottomPage(layoutManager: GridLayoutManager, adapter: MainAdapter){
        binding.moviesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val lastVisibleItem: Int = layoutManager.findLastCompletelyVisibleItemPosition()
                val total = adapter.itemCount
                if (lastVisibleItem + 1 == total){
                    numPage++
                    viewModel.pageValue.value = numPage
                    viewModel.fetchMovie(numPage.toString())
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

    }

    //Crea el Custom Menu y sus funciones
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_item, menu)

        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            //Se manejan las busquedas del usuario a medida que escribe en el buscador
            override fun onQueryTextChange(newText: String?): Boolean {
                //se actualizan los ArrayLists y se limpia el temporal
                getData()
                tempArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                //Si se escribe algo, busca coincidencias con los titulos de las peliculas guardadas en el arrayList
                //y se las guarda en el tempArralyList para luego ser inicializada en el recyclerView
                if (searchText.isNotEmpty()){

                    newArrayList.forEach {

                        if (it.name.toLowerCase(Locale.getDefault()).contains(searchText)){
                            tempArrayList.add((it))
                        }

                    }
                    val layoutManager = GridLayoutManager(this@MainActivity, 2)
                    val recyclerView = findViewById<RecyclerView>(R.id.moviesRv)
                    val adapter = MainAdapter(tempArrayList)
                    recyclerView?.layoutManager =
                        layoutManager
                    recyclerView?.adapter = adapter

                    //Una vez desplegada la lista en el RecyclerView, si el usuario presiona alguna de las peliculas,
                    //podra acceder a su informacion en la proxima Activity
                    adapter.onItemClick = {
                        val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
                        intent.putExtra("movie", it)
                        startActivity(intent)
                    }

                }else{
                    //Si no se escribe nada, continua atento al movieLiveData
                    viewModel.movieLiveData.observe(this@MainActivity,{ state ->
                        processMoviesResponse(state)
                    })

                }

                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

}