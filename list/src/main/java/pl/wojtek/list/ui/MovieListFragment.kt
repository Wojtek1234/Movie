package pl.wojtek.list.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
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
import pl.wojtek.list.R
import pl.wojtek.list.data.network.MoviesDataSource
import pl.wojtek.list.data.network.MoviesNetworkDataMapper
import pl.wojtek.list.domain.LoadMoviesUseCase
import pl.wojtek.list.domain.Mapper
import pl.wojtek.list.domain.Movie

/**
 *
 */


class MovieListFragment : Fragment(R.layout.fragment_movie_list) {

    private val viewModel: MovieListViewModel by viewModel()
    private val imageLoader: ImageLoader by inject()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieRefreshLayout.isEnabled = false

        observe(viewModel.showProgressStream) {
            movieRefreshLayout.isRefreshing = it
        }

        observe(viewModel.errorWrapperStream) {
            showSimpleErrorDialog(it)
        }

        movieSearchEditText.doAfterTextChanged {
            viewModel.setFilterQuery(it?.toString() ?: "")
        }

        moviesRecyclerView.adapter = listAdapter(R.layout.vh_movie_element,
            itemCallback { areItemsTheSame { t1, t2 -> t1.id == t2.id } }) { _, movie: Movie ->
            imageLoader.loadImageToImageView(movie.imageUrl, vhMovieImagePoster)
            vhMovieLoveIcon.setImageResource(if (movie.isLoved) R.drawable.ic_baseline_star_24 else R.drawable.ic_outline_star_border_24)
            vhMovieTitle.text = movie.title
        }.apply {
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
    viewModel { MovieListViewModel(get(),get()) }
    factory {
        val paginModel = get<CoroutinePaginModelFactory>().createPaginModel(MoviesDataSource(getApi()), MoviesNetworkDataMapper())
        LoadMoviesUseCase(paginModel,get(), Mapper(get())) }
}