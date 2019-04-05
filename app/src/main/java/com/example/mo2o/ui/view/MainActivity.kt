package com.example.mo2o.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mo2o.R
import com.example.mo2o.data.Recepy
import com.example.mo2o.extensions.afterTextChanged
import com.example.mo2o.extensions.openActivity
import com.example.mo2o.repository.ApiFactory
import com.example.mo2o.repository.recepies.RecepiesRepository
import com.example.mo2o.ui.viewmodel.MainViewModel
import com.example.mo2o.ui.viewmodel.MainViewModelFactory
import com.example.mo2o.utils.InjectorUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recepy_list_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(),RecepiesAdapter.RecepiesViewHolder.OnItemClick {

    val recepiesList: MutableList<Recepy> by lazy { mutableListOf<Recepy>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainViewModel = createViewModel()
        setUpRecyclerView()
        observeViewModel(mainViewModel)
        textSearch(mainViewModel)
    }


    private fun setUpRecyclerView() {
        rv_recepies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecepiesAdapter(recepiesList,this@MainActivity)
        }
    }

    private fun textSearch(mainViewModel: MainViewModel) {
        search_view.afterTextChanged {
            mainViewModel.getRecepies(it)
        }
    }

    private fun observeViewModel(mainViewModel: MainViewModel) {
        mainViewModel.recepiesLiveData.observe(this, Observer { recepies ->
            when{
                recepies.isEmpty() -> {
                    rv_recepies.visibility = View.GONE
                }
                else ->{
                    recepiesList.clear()
                    recepiesList.addAll(recepies)
                    rv_recepies.adapter?.notifyDataSetChanged()
                    rv_recepies.visibility = View.VISIBLE
                }
            }
        })

        mainViewModel.errorDialog.observe(this, Observer { error ->
            showErrorSnackBar(error)
        })
    }

    private fun showErrorSnackBar(error: String) {
        Toast.makeText(this,error,Toast.LENGTH_LONG).show()
    }

    private fun createViewModel(): MainViewModel {
        val factory: MainViewModelFactory = InjectorUtils.providesMainViewModelFactory()
        val mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        return mainViewModel
    }

    override fun onItemClick(recepy: Recepy, image: ImageView) {
        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,image,ViewCompat.getTransitionName(image)!!)
        openActivity(DetailActivity::class.java,optionsCompat){
            putString(getString(R.string.TITLE_EXTRA_INTENT),recepy.title)
            putString(getString(R.string.INGREDIENTS_EXTRA_INTENT),recepy.ingredients)
            putString(getString(R.string.HREF_EXTRA_INTENT),recepy.href)
            putString(getString(R.string.THUMBNAIL_EXTRA_INTENT),recepy.thumbnail)
        }
    }
}