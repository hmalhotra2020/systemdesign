package com.example.services.impl;

import com.example.models.Models.*;
import com.example.services.BookingService;
import com.example.services.SeatReq;
import com.example.store.InMemoryStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingServiceImpl implements BookingService {
    public static final BookingServiceImpl INSTANCE = new BookingServiceImpl();

    private BookingServiceImpl() {}

    @Override
    public Optional<Booking> book(User user, Movie movie, Show show, SeatReq seatReq) {
        int[][] seatMap = show.getSeatMap();
        for (int[] coord : seatReq.mapSeats()) {
            int row = coord[0];
            int col = coord[1];
            if (seatMap[row][col] != SeatStatus.AVAILABLE.getStatus()) {
                System.out.println("Seat on row: " + row + ", col: " + col + " already booked");
                return Optional.empty();
            }
        }

        List<BookedSeat> bookedSeats = new ArrayList<>();
        for (int[] coord : seatReq.mapSeats()) {
            int row = coord[0];
            int col = coord[1];
            show.getSeatMap()[row][col] = SeatStatus.BOOKED.getStatus();
            bookedSeats.add(new BookedSeat(col + 1, row, col, SeatType.ANY, show.getScreen().name()));
        }

        Booking booking = new Booking(1, user, show, bookedSeats);
        
        InMemoryStore.bookings.compute(user.id(), (key, valList) -> {
            if (valList == null) {
                List<Booking> newList = new ArrayList<>();
                newList.add(booking);
                return newList;
            } else {
                valList.add(booking);
                return valList;
            }
        });

        return Optional.of(booking);
    }

    @Override
    public Optional<List<Booking>> showMyBookings(int userId) {
        return Optional.ofNullable(InMemoryStore.bookings.get(userId));
    }

    private void getPrice(Show show, int row, int col) {
        List<SeatCategory> seatsConfig = show.getScreen().seatsConfig();
        for (SeatCategory seat : seatsConfig) {
            // Placeholder logic matching Scala
        }
    }
}
