# theatrebooking (Java Edition)
A fresh theatre booking app in Java 17+ for system/logical design learners, ported from the original Scala 3 version.

## Project Structure
The application structure is organized as follows:
- **`com.example.models.Models`**: Contains enums (`BookingStatus`, `SeatType`, `SeatStatus`, `UserType`) and records (`Theatre`, `SeatCategory`, `Screen`, `User`, `Movie`, `BookedSeat`, `Booking`) along with a mutable `Show` class representing movie screening state (with its seats grid).
- **`com.example.store.InMemoryStore`**: An in-memory data store containing collections of movies, theatres, screens, users, shows, and bookings.
- **`com.example.generators.DataGenerator`**: Mock data generator creating initial sets of users, theatres, screens, movies, and shows.
- **`com.example.services`**:
  - `SearchService`: Provides movie and show retrieval capability.
  - `SeatService`: Auto-allocates contiguous seat rows based on requests, and prints seat maps.
  - `BookingService`: Creates, tracks, and registers specific seats booking transactions.
- **`com.example.Main`**: Entry point executing sample operations to verify logical flows.

## How to Build and Run
This project uses Gradle. Ensure you have Java 17+ installed on your system.

### Running the Application
From the project root directory, run:
```bash
./gradlew run
```

### Running Tests
To run unit tests:
```bash
./gradlew test
```

## Sample Output
Running `./gradlew run` produces an output mimicking the original project:

```json
Movies List: 
[ {
  "id" : 1,
  "title" : "The Matrix",
  "synopsis" : "Sci-Fi Fantasy",
  "durationInMinutes" : 90
}, {
  "id" : 2,
  "title" : "Babys Day Out",
  "synopsis" : "Comedy",
  "durationInMinutes" : 75
}, {
  "id" : 3,
  "title" : "Dhoom2",
  "synopsis" : "Action Drama",
  "durationInMinutes" : 150
} ]
```

```declarative
Seats Allocated in Row: 0, Starting form Seat: 0
1110000000, 0000000000, 0000000000, ...

Seats Allocated in Row: 0, Starting form Seat: 3
1111111100, 0000000000, 0000000000, ...

Seats Allocated in Row: 1, Starting form Seat: 0
1111111100, 1111111000, 0000000000, ...

Seats Allocated in Row: 2, Starting form Seat: 0
1111111100, 1111111000, 1111100000, ...
```

```declarative
Seats Booked:
Some(Booking[id=1, user=User[id=1, name=Raj Oberoi, email=raj.oberoi@somemail.com, phone=9988998811, userType=CUSTOMER], show=Show(11,Movie[id=2, title=Babys Day Out, synopsis=Comedy, durationInMinutes=75],Screen[id=1002, theatre=Theatre[id=1000, name=Alpha, city=Delhi, address=Sarojni Nagar], name=AUDI2, seatsConfig=[SeatCategory[order=1, seatType=GOLD, rows=10, cols=10, price=200], SeatCategory[order=2, seatType=SILVER, rows=10, cols=10, price=250], SeatCategory[order=3, seatType=PLATINUM, rows=10, cols=10, price=300]], totalSeats=260, totalRows=26, colsPerRow=10],2026-07-17T16:00:00.000,2026-07-17T17:15:00.000,260,0,[[I@58ceff1), seats=[BookedSeat[id=1, row=5, col=0, seatType=ANY, screenName=AUDI2, price=220], BookedSeat[id=2, row=5, col=1, seatType=ANY, screenName=AUDI2, price=220], BookedSeat[id=3, row=5, col=2, seatType=ANY, screenName=AUDI2, price=220], BookedSeat[id=4, row=5, col=3, seatType=ANY, screenName=AUDI2, price=220]]])

Seat on row: 5, col: 0 already booked

Seats Booked again: 
None
```

```declarative
Print Show 11 seatmap
1111111100, 1111111000, 1111100000, 0000000000, 0000000000, 1111000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000, 0000000000
```
