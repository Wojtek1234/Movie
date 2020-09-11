package pl.wojtek.list.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_movie_list.*
import kotlinx.android.synthetic.main.vh_movie_element.view.*
import mobi.wojtek.pagination.coroutine.CoroutinePaginModelFactory
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import pl.wojtek.core.extensions.*
import pl.wojtek.core.image.ImageLoader
import pl.wojtek.list.ListNavigation
import pl.wojtek.list.R
import pl.wojtek.list.data.network.MoviesDataSource
import pl.wojtek.list.data.network.MoviesNetworkDataMapper
import pl.wojtek.list.domain.Movie
import pl.wojtek.list.domain.favourite.ChangeFavouriteStatusUseCase
import pl.wojtek.list.domain.filter.FilterMoviesUseCase
import pl.wojtek.list.domain.load.LoadMoviesUseCase
import pl.wojtek.list.domain.load.Mapper


/**
 *
 */


class MovieListFragment : Fragment(R.layout.fragment_movie_list) {

    private val viewModel: MovieListViewModel by viewModel()
    private val hintsViewModel: ProvideHintOptionsViewModel by viewModel()
    private val imageLoader: ImageLoader by inject()
    private val navigator: ListNavigation by inject()

    private var clicked = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        movieRefreshLayout.isEnabled = false

        observe(viewModel.showProgressStream) {
            movieRefreshLayout.isRefreshing = it
        }

        observe(viewModel.errorWrapperStream) {
            showSimpleErrorDialog(it)
        }

        movieSearchEditText.doAfterTextChanged {
            val query = it?.toString() ?: ""
            viewModel.setFilterQuery(query)
            hintsViewModel.setQuery(query)
        }
        movieSearchEditText.threshold = 1

        movieSearchEditText.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            clicked = true
        }
        movieSearchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                movieSearchEditText.dismissDropDown()
            }
            false
        }

        observe(hintsViewModel.queryHintsStream) {
            movieSearchEditText.setAdapter(ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, it))
            if (movieSearchEditText.hasFocus() && movieSearchEditText.text.isNotBlank() && !clicked) {
                movieSearchEditText.showDropDown()
            } else {
                clicked = false
            }
        }

        handleRecyclerView()
    }

    private fun handleRecyclerView() {

        moviesRecyclerView.adapter = listAdapter(R.layout.vh_movie_element,
            itemCallback { areItemsTheSame { t1, t2 -> t1.id == t2.id } }) { _, movie: Movie ->
            setOnClickListener {
                lifecycleScope.launchWhenResumed {
                    ViewCompat.setTransitionName(vhMovieImagePoster, "${getString(R.string.movie_poster_key)}${movie.id}")
                    navigator.openMovie(movie, vhMovieImagePoster)
                }
            }
            imageLoader.loadImageToImageView(movie.imageUrl, vhMovieImagePoster)
            vhMovieLoveIcon.setImageResource(
                getFavouriteIcon(movie.isLoved)
            )
            vhMovieLoveIcon.setOnClickListener {
                viewModel.changeMovieFavouriteStatus(movie)
            }
            vhMovieTitle.text = movie.title
        }.apply {
            viewModel.moviesStream.value?.let {
                submitList(it)
            }
            observe(viewModel.moviesStream) {
                moviesRecyclerView.scheduleAnimationIfEmptyAdapter()
                submitList(it)
            }
        }

        moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore()
                }
            }
        })
    }
}

internal val movieListModule = module {
    viewModel { MovieListViewModel(get(), ChangeFavouriteStatusUseCase(get()), get()) }
    factory {
        val paginModel = get<CoroutinePaginModelFactory>().createPaginModel(MoviesDataSource(getApi()), MoviesNetworkDataMapper())
        LoadMoviesUseCase(paginModel, get(), Mapper(get()))
    }
    viewModel { ProvideHintOptionsViewModel(FilterMoviesUseCase(getApi()), get()) }
}