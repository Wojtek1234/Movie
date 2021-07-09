package pl.wojtek.list.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import mobi.wojtek.pagination.coroutine.CoroutinePaginModelFactory
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import pl.wojtek.core.extensions.*
import pl.wojtek.core.image.ImageLoader
import pl.wojtek.list.R
import pl.wojtek.list.data.network.MoviesDataSource
import pl.wojtek.list.data.network.MoviesNetworkDataMapper
import pl.wojtek.list.databinding.FragmentMovieListBinding
import pl.wojtek.list.databinding.VhMovieElementBinding
import pl.wojtek.list.domain.Movie
import pl.wojtek.list.domain.favourite.ChangeFavouriteStatusUseCase
import pl.wojtek.list.domain.filter.FilterMoviesUseCase
import pl.wojtek.list.domain.load.LoadMoviesUseCase
import pl.wojtek.list.domain.load.Mapper
import pl.wojtek.list.presentation.MoveToDetailsViewModel
import pl.wojtek.list.presentation.MovieListViewModel
import pl.wojtek.list.presentation.ProvideHintOptionsViewModel


/**
 *
 */


class MovieListFragment : Fragment(R.layout.fragment_movie_list) {

    private val viewModel: MovieListViewModel by viewModel()
    private val hintsViewModel: ProvideHintOptionsViewModel by viewModel()
    private val moveToDetailsViewModel: MoveToDetailsViewModel by viewModel()
    private val imageLoader: ImageLoader by inject()

    private val binding by viewBinding(FragmentMovieListBinding::bind)

    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        binding.movieRefreshLayout.isEnabled = false

        observe(viewModel.showProgressStream) {
            binding.movieRefreshLayout.isRefreshing = it
        }

        observe(viewModel.errorWrapperStream) {
            startPostponedEnterTransition()
            showSimpleErrorDialog(it)
        }

        binding.movieSearchEditText.doAfterTextChanged {
            val query = it?.toString() ?: ""
            viewModel.setFilterQuery(query)
            hintsViewModel.setQuery(query)
        }
        binding.movieSearchEditText.threshold = 1

        binding.movieSearchEditText.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            clicked = true
        }
        binding.movieSearchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.movieSearchEditText.dismissDropDown()
            }
            false
        }

        observe(hintsViewModel.queryHintsStream) {
            binding.movieSearchEditText.setAdapter(ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, it))
            if (binding.movieSearchEditText.hasFocus() && binding.movieSearchEditText.text.isNotBlank() && !clicked) {
                binding.movieSearchEditText.showDropDown()
            } else {
                clicked = false
            }
        }

        handleRecyclerView()
    }

    private fun handleRecyclerView() {

        binding.moviesRecyclerView.adapter = listAdapter(R.layout.vh_movie_element,
            itemCallback { areItemsTheSame { t1, t2 -> t1.id == t2.id } }) { _, movie: Movie ->
            with(VhMovieElementBinding.bind(this)) {


                ViewCompat.setTransitionName(vhMovieImagePoster, "${getString(R.string.movie_poster_key)}${movie.id}")
                setOnClickListener {
                    navigateToDetails(movie, vhMovieImagePoster)
                }
                imageLoader.loadImageToImageView(movie.imageUrl, vhMovieImagePoster)
                vhMovieLoveIcon.setImageResource(
                    getFavouriteIcon(movie.isLoved)
                )
                vhMovieLoveIcon.setOnClickListener {
                    viewModel.changeMovieFavouriteStatus(movie)
                }
                vhMovieTitle.text = movie.title
            }
        }.apply {

            postponeEnterTransition()
            binding.moviesRecyclerView.viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
            viewModel.moviesStream.value?.let {
                submitList(it)
            }
            observe(viewModel.moviesStream) {
                binding.moviesRecyclerView.scheduleAnimationIfEmptyAdapter()
                submitList(it)
            }
        }

        binding.moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore()
                }
            }
        })
    }

    private fun navigateToDetails(movie: Movie,
                                  vhMovieImagePoster: ImageView) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        lifecycleScope.launchWhenResumed {
            moveToDetailsViewModel.moveToDetails(
                bundleOf(getString(R.string.movie_details_argument) to movie.id),
                vhMovieImagePoster
            )
        }
    }
}

internal val movieListModule = module {
    viewModel { MovieListViewModel(get(), ChangeFavouriteStatusUseCase(get()), get()) }
    factory {
        val paginModel = get<CoroutinePaginModelFactory>().createPaginModel(MoviesDataSource(getApi()), MoviesNetworkDataMapper())
        LoadMoviesUseCase(paginModel, get(), Mapper(get()))
    }
    viewModel { ProvideHintOptionsViewModel(FilterMoviesUseCase(getApi()), get()) }
    viewModel { MoveToDetailsViewModel(get(), get()) }
}