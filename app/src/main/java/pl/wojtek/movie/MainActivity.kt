package pl.wojtek.movie

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.wojtek.movie.navigation.NavigationDataProvider
import pl.wojtek.movie.navigation.NavigationViewModel

class MainActivity : AppCompatActivity(), NavigationDataProvider {

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).findNavController() }
    private val navigationViewModel: NavigationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.API_KEY.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("NO API KEY")
                .setMessage("You need to put valid api key through gradle file")
                .setPositiveButton(R.string.ok) { d, _ ->
                    d.dismiss()
                    this.finish()
                }.show()
        }

        lifecycleScope.launchWhenCreated {
            navigationViewModel.navigateToDestinationFlowStream.collect {
                navController.navigate(
                    it.action,
                    it.bundle,
                    null,
                    it.transitionView?.let { it.values.map { it to it.transitionName }.let { FragmentNavigatorExtras(*it.toTypedArray()) } })
            }
        }

        lifecycleScope.launchWhenCreated {
            navigationViewModel.navigateUpStream.collect {
                navController.navigateUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        navigationViewModel.navigationDataProvider = this
    }

    override fun onPause() {
        navigationViewModel.navigationDataProvider = null
        super.onPause()
    }


    override fun getCurrentState(): Int = navController.currentDestination?.id ?: -1
}