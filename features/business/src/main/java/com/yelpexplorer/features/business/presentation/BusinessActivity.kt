package com.yelpexplorer.features.business.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.yelpexplorer.features.business.R
import com.yelpexplorer.features.business.databinding.ActivityBusinessBinding
import com.yelpexplorer.features.business.presentation.businesslist.BusinessListScreen
import com.yelpexplorer.features.business.presentation.businesslist.BusinessListViewModel
import com.yelpexplorer.features.business.presentation.businesslist.YelpExplorer
import org.koin.androidx.viewmodel.ext.android.viewModel

class BusinessActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val compose = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (compose) {
            val viewModel: BusinessListViewModel by viewModel()
            setContent {
                YelpExplorer {
                    BusinessListScreen(viewModel)
                }
            }
        } else {

            val binding = ActivityBusinessBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setSupportActionBar(binding.toolbar)
//        supportActionBar?.setTitle(R.string.app_name)

            navController = findNavController(R.id.activity_business_navHostFragment)
            appBarConfiguration = AppBarConfiguration(navController.graph)
            setupActionBarWithNavController(navController, appBarConfiguration)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
