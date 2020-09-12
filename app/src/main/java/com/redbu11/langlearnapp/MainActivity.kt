package com.redbu11.langlearnapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.redbu11.langlearnapp.ui.fragments.dashboard.DashboardFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//        val appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.navigation_features, R.id.navigation_dashboard, R.id.navigation_additem))
//        setupActionBarWithNavController(navController, appBarConfiguration)


        // add back arrow to toolbar
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.hide()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // handle arrow click here
        if (item.itemId == android.R.id.home) {
            //finish() // close this activity and return to preview activity (if there is any)

        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * OnBackPressed for fragments
     */
    interface IActivityOnBackPressed {
        fun onBackPressed(): Boolean
    }

    override fun onBackPressed() {
        val fragments: List<Fragment> =
            supportFragmentManager.fragments[0].childFragmentManager.fragments
        //System.out.println(fragments.toString())
        for (f in fragments) {
            if (f is DashboardFragment && f.onBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }
}
