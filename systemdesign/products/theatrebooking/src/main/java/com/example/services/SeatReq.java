package com.example.services;

import com.example.models.Models.SeatType;
import java.util.List;

public record SeatReq(int noSeats, SeatType seatType, List<int[]> mapSeats) {
    public SeatReq(int noSeats, SeatType seatType) {
        this(noSeats, seatType, List.of());
    }
}
