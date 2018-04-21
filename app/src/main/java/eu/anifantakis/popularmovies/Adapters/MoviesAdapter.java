package eu.anifantakis.popularmovies.Adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.anifantakis.popularmovies.R;
import eu.anifantakis.popularmovies.DataModels.MoviesCollection;
import eu.anifantakis.popularmovies.databinding.MovieRowBinding;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    final private MovieItemClickListener mOnClickListener;
    private MoviesCollection collection;

    public MoviesAdapter(MovieItemClickListener clickListener) {
        this.mOnClickListener = clickListener;
    }

    public MoviesCollection.Movie getMovieAtIndex(int index) {
        return collection.getMovie(index);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MovieRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.movie_row, parent, false);
        return new MovieViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.setTitle(collection.getMovie(position).getTitle());
        holder.setImage(collection.getMovie(position).getBackdropPath());
        holder.setRating(collection.getMovie(position).getRating(1) / 2);
        holder.setYear(collection.getMovie(position).getReleaseDate());
    }

    @Override
    public int getItemCount() {
        if (null == collection) return 0;
        return collection.getCollectionSize();
    }

    public void clearCollection(){
        if (collection!=null) {
            collection.clear();
            notifyDataSetChanged();
        }
    }

    public void appendCollectionData(MoviesCollection fetchedMovies) {
        if (fetchedMovies != null) { // if there is at least one movie fetched (useful check if favorites is empty)...
            if (this.collection == null || fetchedMovies.getPage() == 1) {
                this.collection = fetchedMovies;
            } else {
                if (this.collection.getPage() < fetchedMovies.getPage()) {
                    this.collection.getAllItems().addAll(fetchedMovies.getAllItems());
                }
            }
        }
        notifyDataSetChanged();
    }

    public MoviesCollection getCollection() {
        return collection;
    }

    public interface MovieItemClickListener {
        void onMovieItemClick(int clickedItemIndex);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MovieRowBinding binding;
        private Context context;

        private MovieViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            context = itemView.getContext();

            itemView.setOnClickListener(this);
        }

        /**
         * Set the holder movie's thumbnail
         *
         * @param image
         */
        void setImage(String image) {
            if (image.trim().equals("")){
                // if movie has no accompanied backdrop image, load the "no image found" from the drawable folder
                binding.rowIvMovieThumb.setImageResource(R.drawable.backdrop_noimage);
            }else {
                image = context.getString(R.string.network_url_images) + context.getString(R.string.network_width_342) + image;
                Picasso.with(context)
                        .load(image)
                        .into(binding.rowIvMovieThumb);
            }
        }

        /**
         * Set the holder movie's rating
         *
         * @param rating
         */
        void setRating(float rating) {
            binding.rowRatingBar.setRating(rating);
        }

        /**
         * Set the holder movie's title
         *
         * @param title
         */
        void setTitle(String title) {
            binding.rowTvTitle.setText(title);
        }

        /**
         * Set the holder movie's release year
         *
         * @param date
         */
        void setYear(Date date) {
            if (date==null){
                binding.rowTvYear.setText("(N/A)");
            }
            else {
                DateFormat df = android.text.format.DateFormat.getDateFormat(context);
                ((SimpleDateFormat) df).applyLocalizedPattern("yyyy");
                binding.rowTvYear.setText("(".concat(df.format(date)).concat(")"));
            }
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onMovieItemClick(clickedPosition);
        }
    }
}
