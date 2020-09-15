package pl.wojtek.details.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.fragment_movie_details.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import pl.wojtek.core.extensions.getApi
import pl.wojtek.core.extensions.getFavouriteIcon
import pl.wojtek.core.extensions.observe
import pl.wojtek.core.extensions.showSimpleErrorDialog
import pl.wojtek.core.image.ImageLoader
import pl.wojtek.details.R
import pl.wojtek.details.domain.LoadMovieDetailsUseCase

/**
 *
 */


@SuppressLint("UseRequireInsteadOfGet")
class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    private val movieId by lazy { arguments!!.getInt(getString(R.string.movie_details_argument)) }
    private val viewModel: LoadMovieDetailsViewModel by viewModel { parametersOf(movieId) }

    private val imageLoader: ImageLoader by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postponeEnterTransition()

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.change_image)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(moviePosterImageView, "${requireContext().getString(R.string.movie_poster_key)}$movieId")

        observe(viewModel.errorWrapperStream) {
            startPostponedEnterTransition()
            showSimpleErrorDialog(it)
        }

        observe(viewModel.movieDetailsStream) {
            imageLoader.loadImageToImageView(it.imageUrl, moviePosterImageView) {
                startPostponedEnterTransition()
            }
            titleText.text = it.title
            overviewText.text = it.description
            dateText.text = it.date
            voteText.text = it.vote
        }

        observe(viewModel.favouriteStream) {
            toolbarFavouriteIcon.setImageResource(getFavouriteIcon(it))
        }
        toolbarFavouriteIcon.setOnClickListener {
            viewModel.changeFavourite()
        }
    }
}


val detailsModule = module {
    viewModel { (id: Int) -> LoadMovieDetailsViewModel(LoadMovieDetailsUseCase(id, get(), getApi(), get(), get()), get()) }
}