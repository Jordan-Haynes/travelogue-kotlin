package com.akkeritech.android.travellogue

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.akkeritech.android.travellogue.database.PlaceRepository

class PlaceListActivity : AppCompatActivity() {

    private lateinit var item: MenuItem

    val placeViewModel: PlaceListViewModel by viewModels {
        val application = requireNotNull(this).application
        val repository = PlaceRepository(application)
        PlaceListViewModelFactory(repository, application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_list)
        setSupportActionBar(findViewById(R.id.toolbar))


        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        if (currentFragment == null) {
            val fragment = PlaceListFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, fragment).commit()
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.action_placeListFragment_to_addPlaceFragment)
        }

        findViewById<FloatingActionButton>(R.id.edit_fab).setOnClickListener {
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.action_placeDetailViewFragment_to_addPlaceFragment)
        }

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            val fab = findViewById<FloatingActionButton>(R.id.fab)
            val editFab = findViewById<FloatingActionButton>(R.id.edit_fab)
            if (destination.label.toString() == "PlaceListFragment") {
                fab.show()
                editFab.hide()
            } else if (destination.label.toString() == "fragment_place_detail_view") {
                editFab.show()
                fab.hide()
            } else {
                editFab.hide()
                fab.hide()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail_view, menu)
        item = menu.findItem(R.id.action_delete)
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            item.isVisible = destination.label.toString() == "fragment_place_detail_view"
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                placeViewModel.deletePlace()
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_placeDetailViewFragment_to_placeListFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}