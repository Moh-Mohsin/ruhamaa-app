package net.ruhamaa.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import net.ruhamaa.mobile.ui.user.UserViewModel
import net.ruhamaa.mobile.util.AppSignatureHelper
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private val appBarConfiguration by lazy { buildAppBarConfiguration() }
    val userViewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up ActionBar
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            handleDestinationChange(destination)
        }
        nav_view.setupWithNavController(navController)
        if (BuildConfig.DEBUG) {
            showAppSignature()
        }
        hideActionBar()
    }

    override fun onResume() {
        super.onResume()
        navController.currentDestination?.let { handleDestinationChange(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                userViewModel.logout()
                navController.navigate(R.id.loginFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleDestinationChange(destination: NavDestination) {
        when (destination.id) {
            in appBarConfiguration.topLevelDestinations -> {
                showActionBar()
                showNav()
//                hideNav()
            }
            R.id.loginFragment, R.id.verifyFragment -> {
                hideActionBar()
                hideNav()
            }
            else -> {
                showActionBar()
                hideNav()
            }
        }
    }

    private fun buildAppBarConfiguration(): AppBarConfiguration {
        return AppBarConfiguration.Builder(hashSetOf(R.id.caseListFragment))
            .setDrawerLayout(drawer_layout)
            .build()
    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawer_layout.setStatusBarBackground(R.color.primaryDarkColor)
    }


    private fun showAppSignature() { //TODO: [IMPORTANT] REMOVE BEFORE PRODUCTION
        val appSignatureHelper = AppSignatureHelper(this)
        appSignatureHelper.appSignatures.forEachIndexed { index, s ->
            //            toast("Hash no ${index + 1}: $s")
            Timber.d( "Hash no ${index + 1}: $s")
        }
    }
    private fun hideActionBar() {
        supportActionBar?.let {
            it.hide()
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun showActionBar() {
        supportActionBar?.let {
            it.show()
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun showNav() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        toolbar.visibility = View.VISIBLE
    }

    private fun hideNav() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toolbar.visibility = View.GONE
    }

}
