package com.example;

import com.example.models.Models.*;
import com.example.services.*;
import com.example.services.impl.*;
import com.example.store.InMemoryStore;
import com.example.generators.DataGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        SearchService searchService = SearchServiceImpl.INSTANCE;
        SeatService seatService = SeatServiceImpl.INSTANCE;
        BookingService bookingService = BookingServiceImpl.INSTANCE;
        DataGenerator dg = DataGenerator.INSTANCE;

        dg.movies();
        dg.users();
        dg.theatres();
        dg.screens();
        dg.shows();

        User user1 = InMemoryStore.users.get(0);
        User user2 = InMemoryStore.users.get(1);
        User user3 = InMemoryStore.users.get(2);
        Movie movie1 = searchService.getMovieById(2);

        System.out.println("\nMovies List: ");
        System.out.println(prettyPrint(InMemoryStore.movies));

        Optional<List<Show>> shows = searchService.getAllShowsInTheatresForMovie(1);
        System.out.println("\nShows List: ");
        System.out.println(prettyPrint(shows.orElse(null)));

        Optional<java.util.Map<Movie, List<Show>>> moviesMap = searchService.getMoviesByShowsInTheatre();
        System.out.println("\nMovies List with Theatres: ");
        System.out.println(prettyPrint(moviesMap.orElse(null)));

        seatService.autoAllocateSeats(user1, movie1, searchService.getShowById(11, movie1), new SeatReq(3, SeatType.ANY));
        System.out.println("\n" + String.join(", ", seatService.seatMap(searchService.getShowById(11, movie1))));

        seatService.autoAllocateSeats(user2, movie1, searchService.getShowById(11, movie1), new SeatReq(5, SeatType.ANY));
        System.out.println("\n" + String.join(", ", seatService.seatMap(searchService.getShowById(11, movie1))));

        seatService.autoAllocateSeats(user3, movie1, searchService.getShowById(11, movie1), new SeatReq(7, SeatType.ANY));
        System.out.println("\n" + String.join(", ", seatService.seatMap(searchService.getShowById(11, movie1))));

        seatService.autoAllocateSeats(user1, movie1, searchService.getShowById(11, movie1), new SeatReq(5, SeatType.ANY));
        System.out.println("\n" + String.join(", ", seatService.seatMap(searchService.getShowById(11, movie1))));

        Optional<Booking> booking = bookingService.book(
            user1,
            movie1,
            searchService.getShowById(11, movie1),
            new SeatReq(4, SeatType.ANY, List.of(new int[]{5, 0}, new int[]{5, 1}, new int[]{5, 2}, new int[]{5, 3}))
        );

        System.out.println("\nSeats Booked:");
        System.out.println(booking.map(b -> "Some(" + b + ")").orElse("None"));

        booking = bookingService.book(
            user1,
            movie1,
            searchService.getShowById(11, movie1),
            new SeatReq(4, SeatType.ANY, List.of(new int[]{5, 0}, new int[]{5, 1}, new int[]{5, 2}, new int[]{5, 3}))
        );

        System.out.println("\nSeats Booked again: ");
        System.out.println(booking.map(b -> "Some(" + b + ")").orElse("None"));

        System.out.println("\nPrint Show 11 seatmap");
        System.out.println(String.join(", ", seatService.seatMap(searchService.getShowById(11, movie1))));

        System.out.println("\nShow My Bookings for user1: ");
        List<Booking> user1Bookings = InMemoryStore.bookings.get(user1.id());
        System.out.println(user1Bookings != null ? "Some(\n  value = " + prettyPrint(user1Bookings).replaceAll("(?m)^", "  ") + "\n)" : "None");
    }

    private static String prettyPrint(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}
