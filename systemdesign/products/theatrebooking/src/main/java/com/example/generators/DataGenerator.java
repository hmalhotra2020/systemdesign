package com.example.generators;

import com.example.models.Models.*;
import com.example.store.InMemoryStore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

interface Generator {
    void movies();
    void theatres();
    void shows();
    void users();
    void screens();
}

public class DataGenerator implements Generator {

    public static final DataGenerator INSTANCE = new DataGenerator();

    private DataGenerator() {}

    @Override
    public void movies() {
        InMemoryStore.movies.add(new Movie(1, "The Matrix", "Sci-Fi Fantasy", 90));
        InMemoryStore.movies.add(new Movie(2, "Babys Day Out", "Comedy", 75));
        InMemoryStore.movies.add(new Movie(3, "Dhoom2", "Action Drama", 150));
    }

    @Override
    public void theatres() {
        InMemoryStore.theatres.add(new Theatre(1000, "Alpha", "Delhi", "Sarojni Nagar"));
        InMemoryStore.theatres.add(new Theatre(2000, "Beta", "Delhi", "Lajpat Nagar"));
        InMemoryStore.theatres.add(new Theatre(3000, "Gaama", "Delhi", "Sundar Nagar"));
    }

    @Override
    public void screens() {
        for (Theatre theatre : InMemoryStore.theatres) {
            InMemoryStore.screens.add(new Screen(theatre.id() + 1, theatre, "AUDI1", seatConfigs(), 260, 26, 10));
            InMemoryStore.screens.add(new Screen(theatre.id() + 2, theatre, "AUDI2", seatConfigs(), 260, 26, 10));
            InMemoryStore.screens.add(new Screen(theatre.id() + 3, theatre, "AUDI3", seatConfigs(), 260, 26, 10));
        }
    }

    private List<SeatCategory> seatConfigs() {
        List<SeatCategory> config = new ArrayList<>();
        config.add(new SeatCategory(1, SeatType.GOLD, 10, 10, 200));
        config.add(new SeatCategory(2, SeatType.SILVER, 10, 10, 250));
        config.add(new SeatCategory(3, SeatType.PLATINUM, 10, 10, 300));
        return config;
    }

    @Override
    public void shows() {
        int nextShowId = 1;
        int repeatTimes = 3;
        int max = InMemoryStore.movies.size() * repeatTimes;
        int movieNum = 0;

        for (int i = 1; i <= max; i++) {
            if (movieNum == InMemoryStore.movies.size()) {
                movieNum = 0;
            } else {
                movieNum = movieNum % InMemoryStore.movies.size();
            }

            Movie movie = InMemoryStore.movies.get(movieNum);
            List<Show> showsList = createShows(nextShowId, movie);
            nextShowId += showsList.size();

            if (!InMemoryStore.shows.containsKey(movie.id())) {
                InMemoryStore.shows.put(movie.id(), new ArrayList<>(showsList));
            } else {
                InMemoryStore.shows.get(movie.id()).addAll(showsList);
            }

            movieNum++;
        }
    }

    private List<Show> createShows(int nextShowId, Movie movie) {
        List<Show> showsList = new ArrayList<>();
        int showId = nextShowId;

        for (Screen screen : InMemoryStore.screens) {
            Show show = createShow(showId, movie, screen);
            showsList.add(show);
            showId++;
        }

        return showsList;
    }

    private Show createShow(int showId, Movie movie, Screen screen) {
        int[][] seatMap = new int[screen.totalRows()][screen.colsPerRow()];
        // Initialize seatMap to 0 (available) by default (already 0 in Java)
        return new Show(
            showId,
            movie,
            screen,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(movie.durationInMinutes()),
            screen.totalSeats(),
            0,
            seatMap
        );
    }

    @Override
    public void users() {
        InMemoryStore.users.add(new User(1, "Raj Oberoi", "raj.oberoi@somemail.com", "9988998811", UserType.CUSTOMER));
        InMemoryStore.users.add(new User(2, "Emma", "emma@somemail.com", "9988998811", UserType.CUSTOMER));
        InMemoryStore.users.add(new User(3, "Sophia", "sophia@somemail.com", "9988998811", UserType.CUSTOMER));
    }
}
