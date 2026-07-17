package com.example.models;

import java.time.LocalDateTime;
import java.util.List;

public class Models {

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    public enum SeatType {
        ANY, SILVER, GOLD, PLATINUM, PREMIUM
    }

    public enum SeatStatus {
        AVAILABLE(0), BOOKED(1);

        private final int status;

        SeatStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    public enum UserType {
        CUSTOMER, STAFF, ADMIN
    }

    public record Theatre(int id, String name, String city, String address) {}

    public record SeatCategory(int order, SeatType seatType, int rows, int cols, int price) {}

    public record Screen(
        int id,
        Theatre theatre,
        String name,
        List<SeatCategory> seatsConfig,
        int totalSeats,
        int totalRows,
        int colsPerRow
    ) {}

    public record User(int id, String name, String email, String phone, UserType userType) {}

    public record Movie(int id, String title, String synopsis, int durationInMinutes) {}

    public record BookedSeat(int id, int row, int col, SeatType seatType, String screenName, int price) {
        // Constructor that defaults price to 220
        public BookedSeat(int id, int row, int col, SeatType seatType, String screenName) {
            this(id, row, col, seatType, screenName, 220);
        }
    }

    public record Booking(int id, User user, Show show, List<BookedSeat> seats) {}

    public static class Show {
        private final int id;
        private final Movie movie;
        private final Screen screen;
        private final LocalDateTime startTime;
        private final LocalDateTime endDateTime;
        private int available;
        private int reserved;
        private int[][] seatMap;

        public Show(int id, Movie movie, Screen screen, LocalDateTime startTime, LocalDateTime endDateTime, int available, int reserved, int[][] seatMap) {
            this.id = id;
            this.movie = movie;
            this.screen = screen;
            this.startTime = startTime;
            this.endDateTime = endDateTime;
            this.available = available;
            this.reserved = reserved;
            this.seatMap = seatMap;
        }

        public int getId() { return id; }
        public Movie getMovie() { return movie; }
        public Screen getScreen() { return screen; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndDateTime() { return endDateTime; }
        
        public int getAvailable() { return available; }
        public void setAvailable(int available) { this.available = available; }
        
        public int getReserved() { return reserved; }
        public void setReserved(int reserved) { this.reserved = reserved; }
        
        public int[][] getSeatMap() { return seatMap; }
        public void setSeatMap(int[][] seatMap) { this.seatMap = seatMap; }

        @Override
        public String toString() {
            return "Show(" + id +
                   "," + movie +
                   "," + screen +
                   "," + startTime +
                   "," + endDateTime +
                   "," + available +
                   "," + reserved +
                   "," + seatMap + ")";
        }
    }
}
