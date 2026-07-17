package com.example.services;

import com.example.models.Models.Show;
import com.example.models.Models.Movie;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SearchService {
    Optional<List<Show>> getAllShowsInTheatresForMovie(int movieId);
    Optional<Map<Movie, List<Show>>> getMoviesByShowsInTheatre();
    Show getShowById(int id, Movie movie);
    Movie getMovieById(int id);
}
