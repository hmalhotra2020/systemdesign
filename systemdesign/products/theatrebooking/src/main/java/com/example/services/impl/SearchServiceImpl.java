package com.example.services.impl;

import com.example.models.Models.Show;
import com.example.models.Models.Movie;
import com.example.services.SearchService;
import com.example.store.InMemoryStore;

import java.util.*;
import java.util.stream.Collectors;

public class SearchServiceImpl implements SearchService {
    public static final SearchServiceImpl INSTANCE = new SearchServiceImpl();

    private SearchServiceImpl() {}

    @Override
    public Optional<List<Show>> getAllShowsInTheatresForMovie(int movieId) {
        return Optional.ofNullable(InMemoryStore.shows.get(movieId));
    }

    @Override
    public Optional<Map<Movie, List<Show>>> getMoviesByShowsInTheatre() {
        Map<Movie, List<Show>> newMoviesMap = new HashMap<>();
        for (Map.Entry<Integer, List<Show>> entry : InMemoryStore.shows.entrySet()) {
            int movieId = entry.getKey();
            Movie movie = InMemoryStore.movies.stream()
                .filter(m -> m.id() == movieId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Movie not found: " + movieId));
            newMoviesMap.put(movie, newShow(entry.getValue()));
        }
        return Optional.of(newMoviesMap);
    }

    @Override
    public Show getShowById(int id, Movie movie) {
        List<Show> showsList = InMemoryStore.shows.get(movie.id());
        if (showsList == null) {
            throw new NoSuchElementException("No shows found for movie: " + movie.id());
        }
        return showsList.stream()
            .filter(s -> s.getId() == id)
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Show not found: " + id));
    }

    @Override
    public Movie getMovieById(int id) {
        return InMemoryStore.movies.stream()
            .filter(m -> m.id() == id)
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Movie not found: " + id));
    }

    private List<Show> newShow(List<Show> showSeq) {
        return showSeq.stream()
            .map(s -> new Show(
                s.getId(),
                s.getMovie(),
                s.getScreen(),
                s.getStartTime(),
                s.getEndDateTime(),
                s.getAvailable(),
                s.getReserved(),
                null
            ))
            .collect(Collectors.toList());
    }
}
