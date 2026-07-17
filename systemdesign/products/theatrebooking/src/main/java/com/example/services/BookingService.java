package com.example.services;

import com.example.models.Models.Booking;
import com.example.models.Models.Movie;
import com.example.models.Models.Show;
import com.example.models.Models.User;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Optional<Booking> book(User user, Movie movie, Show show, SeatReq seatReq);
    Optional<List<Booking>> showMyBookings(int userId);
}
