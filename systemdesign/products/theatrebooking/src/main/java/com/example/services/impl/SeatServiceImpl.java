package com.example.services.impl;

import com.example.models.Models.Show;
import com.example.models.Models.User;
import com.example.models.Models.Movie;
import com.example.models.Models.SeatStatus;
import com.example.services.SeatService;
import com.example.services.SeatReq;

import java.util.ArrayList;
import java.util.List;

public class SeatServiceImpl implements SeatService {
    public static final SeatServiceImpl INSTANCE = new SeatServiceImpl();

    private SeatServiceImpl() {}

    @Override
    public List<String> seatMap(Show show) {
        List<String> printableSeatMap = new ArrayList<>();
        for (int[] row : show.getSeatMap()) {
            StringBuilder sb = new StringBuilder();
            for (int seat : row) {
                sb.append(seat);
            }
            printableSeatMap.add(sb.toString());
        }
        return printableSeatMap;
    }

    @Override
    public void autoAllocateSeats(User user, Movie movie, Show show, SeatReq seatReq) {
        int rowSize = show.getScreen().totalRows();
        for (int r = 0; r < rowSize; r++) {
            int[] row = show.getSeatMap()[r];
            int index = checkRow(row, seatReq.noSeats());
            if (index > -1) {
                for (int i = 0; i < seatReq.noSeats(); i++) {
                    row[index + i] = SeatStatus.BOOKED.getStatus();
                }
                show.getSeatMap()[r] = row;
                System.out.println("Seats Allocated in Row: " + r + ", Starting form Seat: " + index);
                return;
            }
        }
        System.out.println("Could not find seats, allocate manually.");
    }

    private int checkRow(int[] row, int noSeats) {
        int index = 0;
        while (index <= row.length - noSeats) {
            if (row[index] == SeatStatus.AVAILABLE.getStatus()) {
                if (checkNextNSeats(row, index, noSeats)) {
                    return index;
                }
            }
            index++;
        }
        return -1;
    }

    private boolean checkNextNSeats(int[] row, int index, int noSeats) {
        if (index + noSeats > row.length) {
            return false;
        }
        for (int i = index; i < index + noSeats; i++) {
            if (row[i] != SeatStatus.AVAILABLE.getStatus()) {
                return false;
            }
        }
        return true;
    }
}
