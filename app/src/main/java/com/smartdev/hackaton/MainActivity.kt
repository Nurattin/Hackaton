package com.smartdev.hackaton

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        MapKitFactory.initialize(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener {

            return@setOnItemSelectedListener when (it.itemId) {
                R.id.main_tab_navigation -> {
                    findNavController(R.id.container).navigate(R.id.homeFragment, null, navOptions {
                        findNavController(R.id.container).currentDestination?.let {
                            popUpTo(id = it.id) {
                                inclusive = true
                            }

                        }
                    })
                    true
                }
                R.id.card_tab_navigation -> {
                    true

//                    startActivity(Intent(this, ))
                }
                else -> false
            }
        }
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop();
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart();
    }
}