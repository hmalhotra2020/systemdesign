package com.example.store;

import com.example.models.Models.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore implements Store {
    public static List<Movie> movies = new CopyOnWriteArrayList<>();
    public static List<Theatre> theatres = new CopyOnWriteArrayList<>();
    public static List<Screen> screens = new CopyOnWriteArrayList<>();
    public static List<User> users = new CopyOnWriteArrayList<>();
    public static Map<Integer, List<Show>> shows = new ConcurrentHashMap<>();
    public static Map<Integer, List<Booking>> bookings = new ConcurrentHashMap<>();
}
