package com.example.services;

import com.example.models.Models.Show;
import com.example.models.Models.User;
import com.example.models.Models.Movie;
import java.util.List;

public interface SeatService {
    List<String> seatMap(Show show);
    void autoAllocateSeats(User user, Movie movie, Show show, SeatReq seatReq);
}
