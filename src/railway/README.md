# Railway Ticket Reservation System

A beginner-friendly Java Swing desktop project for an OOP lab. It uses local TXT files and does not require a database, server, or external library.

## Requirements
- JDK 21
- IntelliJ IDEA Community or Ultimate

## Features
### Admin
- Login with the demo admin account
- Add, update, deactivate trains
- Add train schedules by date
- View passengers and reservations
- View ticket details and cancel a reservation
- View a basic report summary

### Passenger
- Register and log in
- Search by source, destination, date, and coach type
- Book a seat and receive a reservation ID
- View and cancel personal bookings
- View/update profile

### OOP and Java concepts
- Encapsulation, inheritance, abstraction, polymorphism
- Constructors, `this`, access modifiers
- Overriding, method overloading opportunities, interfaces
- Collections, exception handling, file handling
- Swing event handling and tables

## Demo admin login
- Username: `admin`
- Password: `admin123`

This is a local educational demonstration. Do not use real passwords or real passenger information.

## Open in IntelliJ IDEA
1. Extract `RailwayTicketReservationSystem.zip`.
2. Open the extracted `RailwayTicketReservationSystem` folder in IntelliJ IDEA.
3. Set the Project SDK to JDK 21: **File > Project Structure > Project > SDK**.
4. Mark `src` as **Sources Root** if IntelliJ does not detect it automatically.
5. Open `src/railway/Main.java`.
6. Click the green Run icon beside `main()`.

No external libraries are needed.

## Data storage
When the app runs, it creates a `data` folder in the application's working directory. It stores:
- `trains.txt`
- `passengers.txt`
- `schedules.txt`
- `reservations.txt`

The first run creates sample trains and a sample schedule for tomorrow. You can add additional schedules through the admin interface.

Data is saved after each successful change. Back up the `data` folder before manually editing its files.

## Notes and limitations
- Each reservation books one seat.
- Seat numbers are assigned automatically per schedule.
- The application is designed for one local computer, not concurrent multi-user booking over a network.
- Demo passwords are stored locally for educational simplicity; production software must use secure password hashing and stronger access controls.
- Train source and destination fields are entered as free text by the admin. Passenger search must match the station names.
- The TXT files use a simple pipe-delimited format. The program escapes pipe characters and percent signs in text fields.
- A deactivated train cannot be newly booked. Existing reservations must be cancelled before deactivation.
