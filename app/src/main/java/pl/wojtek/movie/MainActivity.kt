package pl.wojtek.movie

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import pl.wojtek.list.ListNavigationListener

class MainActivity : AppCompatActivity() {

    private val listNavigationListener: ListNavigationListener by inject()
    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).findNavController() }

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

        lifecycleScope.launchWhenResumed {
            listNavigationListener.openMovieDetails().collect {
                if (navController.currentDestination?.id == R.id.movieListFragment)
                    navController.navigate(
                        R.id.action_movieListFragment_to_movieDetailsFragment,
                        bundleOf(getString(R.string.movie_details_argument) to it.first),
                        null,
                        FragmentNavigatorExtras(
                            it.second to "${getString(R.string.movie_poster_key)}${it.first}"
                        )
                    )
            }
        }
    }
}