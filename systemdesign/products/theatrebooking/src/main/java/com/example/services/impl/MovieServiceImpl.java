package com.example.services.impl;

import com.example.models.Models.Movie;
import com.example.services.MovieService;
import com.example.store.InMemoryStore;
import java.util.List;

public class MovieServiceImpl implements MovieService {
    public static final MovieServiceImpl INSTANCE = new MovieServiceImpl();

    private MovieServiceImpl() {}

    @Override
    public List<Movie> moviesList() {
        return InMemoryStore.movies;
    }
}
