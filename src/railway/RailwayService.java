package railway;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/** Business logic. Swing screens call these methods instead of manipulating files directly. */
public class RailwayService {
    private final FileManager files;
    private final List<Train> trains = new ArrayList<>();
    private final List<Passenger> passengers = new ArrayList<>();
    private final List<TrainSchedule> schedules = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final Admin admin = new Admin("A001", "System Admin", "admin", "admin123");

    public RailwayService() throws IOException {
        files = new FileManager();
        loadAll();
        seedDemoDataIfNeeded();
    }

    public Admin getAdmin() { return admin; }
    public List<Train> getTrains() { return new ArrayList<>(trains); }
    public List<Passenger> getPassengers() { return new ArrayList<>(passengers); }
    public List<TrainSchedule> getSchedules() { return new ArrayList<>(schedules); }
    public List<Reservation> getReservations() { return new ArrayList<>(reservations); }

    private void loadAll() throws IOException {
        loadRows("trains.txt", line -> trains.add(Train.fromFileLine(line)));
        loadRows("passengers.txt", line -> passengers.add(passengerFromLine(line)));
        loadRows("schedules.txt", line -> schedules.add(TrainSchedule.fromFileLine(line)));
        loadRows("reservations.txt", line -> reservations.add(Reservation.fromFileLine(line)));
    }

    private interface RowLoader { void load(String line); }
    private void loadRows(String file, RowLoader loader) throws IOException {
        for (String line : files.readLines(file)) {
            if (line.trim().isEmpty()) continue;
            try { loader.load(line); }
            catch (RuntimeException ex) {
                System.err.println("Skipping invalid row in " + file + ": " + line);
            }
        }
    }

    private Passenger passengerFromLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Passenger(p[0], Train.dec(p[1]), Train.dec(p[2]), Train.dec(p[3]), Train.dec(p[4]));
    }

    private String passengerToLine(Passenger p) {
        return String.join("|", p.getId(), Train.enc(p.getName()), Train.enc(p.getUsername()),
                Train.enc(p.getPassword()), Train.enc(p.getContact()));
    }

    private void seedDemoDataIfNeeded() throws IOException {
        if (trains.isEmpty()) {
            trains.add(new Train("TR101", "Subarna Express", "Dhaka", "Chattogram", "07:00", "13:30", 40, 700, "AC"));
            trains.add(new Train("TR102", "Mahanagar Express", "Dhaka", "Chattogram", "21:00", "04:30", 40, 500, "Non-AC"));
            trains.add(new Train("TR103", "Parabat Express", "Dhaka", "Sylhet", "06:30", "13:00", 30, 450, "AC"));
            saveTrains();
        }
        if (schedules.isEmpty()) {
            // A sample future schedule. Admin can add more dates from the dashboard.
            String date = LocalDate.now().plusDays(1).toString();
            for (Train t : trains) schedules.add(new TrainSchedule(nextId("SC", schedules), t.getId(), date));
            saveSchedules();
        }
    }

    public boolean loginAdmin(String username, String password) {
        return admin.getUsername().equals(username) && admin.getPassword().equals(password);
    }

    public Passenger loginPassenger(String username, String password) {
        for (Passenger p : passengers)
            if (p.getUsername().equalsIgnoreCase(username) && p.getPassword().equals(password)) return p;
        return null;
    }

    public Passenger registerPassenger(String name, String username, String password, String contact) throws IOException {
        if (name == null || name.trim().isEmpty() || username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) throw new IllegalArgumentException("Name, username and password are required.");
        for (Passenger p : passengers)
            if (p.getUsername().equalsIgnoreCase(username.trim())) throw new IllegalArgumentException("That username is already registered.");
        Passenger p = new Passenger(nextId("P", passengers), name.trim(), username.trim(), password, contact);
        passengers.add(p);
        savePassengers();
        return p;
    }

    public void updateProfile(Passenger passenger, String name, String contact, String newPassword) throws IOException {
        passenger.setName(name);
        passenger.setContact(contact);
        if (newPassword != null && !newPassword.trim().isEmpty()) passenger.setPassword(newPassword);
        savePassengers();
    }

    public void addTrain(Train train) throws IOException {
        if (findTrain(train.getId()) != null) throw new IllegalArgumentException("Train ID already exists.");
        trains.add(train);
        saveTrains();
    }

    public void updateTrain(String id, String name, String source, String destination, String departure,
                            String arrival, int seats, double fare, String coach) throws IOException {
        Train t = findTrain(id);
        if (t == null) throw new IllegalArgumentException("Train not found.");
        t.update(name, source, destination, departure, arrival, seats, fare, coach);
        saveTrains();
    }

    public void deactivateTrain(String id) throws IOException {
        Train t = findTrain(id);
        if (t == null) throw new IllegalArgumentException("Train not found.");
        boolean activeBooking = false;
        for (Reservation r : reservations) {
            TrainSchedule s = findSchedule(r.getScheduleId());
            if (s != null && s.getTrainId().equals(id) && r.getStatus().equals("CONFIRMED")) activeBooking = true;
        }
        if (activeBooking) throw new IllegalArgumentException("This train has active bookings. Cancel those bookings first.");
        t.setActive(false);
        saveTrains();
    }

    public void addSchedule(String trainId, String date) throws IOException {
        if (findTrain(trainId) == null) throw new IllegalArgumentException("Select a valid train.");
        try { LocalDate.parse(date); } catch (Exception e) { throw new IllegalArgumentException("Date must use YYYY-MM-DD."); }
        for (TrainSchedule s : schedules)
            if (s.getTrainId().equals(trainId) && s.getDate().equals(date)) throw new IllegalArgumentException("Schedule already exists for this train and date.");
        schedules.add(new TrainSchedule(nextId("SC", schedules), trainId, date));
        saveSchedules();
    }

    // Overloaded method: when coach is omitted, search AC schedules by default.
    public List<TrainSchedule> searchSchedules(String source, String destination, String date) {
        return searchSchedules(source, destination, date, "AC");
    }

    public List<TrainSchedule> searchSchedules(String source, String destination, String date, String coach) {
        List<TrainSchedule> result = new ArrayList<>();
        for (TrainSchedule s : schedules) {
            Train t = findTrain(s.getTrainId());
            if (t != null && t.isActive()
                    && t.getSource().equalsIgnoreCase(source.trim())
                    && t.getDestination().equalsIgnoreCase(destination.trim())
                    && s.getDate().equals(date)
                    && t.getCoachType().equalsIgnoreCase(coach)
                    && availableSeats(s) > 0) result.add(s);
        }
        return result;
    }

    public int availableSeats(TrainSchedule schedule) {
        Train t = findTrain(schedule.getTrainId());
        if (t == null) return 0;
        Set<String> occupied = new HashSet<>();
        for (Reservation r : reservations)
            if (r.getScheduleId().equals(schedule.getId()) && r.getStatus().equals("CONFIRMED")) occupied.add(r.getSeatNumber());
        return Math.max(0, t.getTotalSeats() - occupied.size());
    }

    public Reservation book(Passenger passenger, String scheduleId) throws IOException {
        TrainSchedule s = findSchedule(scheduleId);
        if (s == null) throw new IllegalArgumentException("Schedule not found.");
        Train t = findTrain(s.getTrainId());
        if (t == null || !t.isActive()) throw new IllegalArgumentException("This train is not available.");
        if (s.getDate().compareTo(LocalDate.now().toString()) < 0) throw new IllegalArgumentException("Cannot book a journey in the past.");
        Set<Integer> occupied = new HashSet<>();
        for (Reservation r : reservations)
            if (r.getScheduleId().equals(s.getId()) && r.getStatus().equals("CONFIRMED")) {
                try { occupied.add(Integer.parseInt(r.getSeatNumber())); } catch (NumberFormatException ignored) {}
            }
        int seat = 1;
        while (occupied.contains(seat) && seat <= t.getTotalSeats()) seat++;
        if (seat > t.getTotalSeats()) throw new IllegalArgumentException("No seats are available.");
        Reservation r = new Reservation(nextId("BK", reservations), passenger.getId(), s.getId(),
                String.valueOf(seat), t.getFare(), "CONFIRMED");
        reservations.add(r);
        saveReservations();
        return r;
    }

    public void cancelReservation(Passenger passenger, String reservationId) throws IOException {
        Reservation r = findReservation(reservationId);
        if (r == null || !r.getPassengerId().equals(passenger.getId())) throw new IllegalArgumentException("Reservation not found for this passenger.");
        cancelReservation(r);
    }

    public void cancelReservationByAdmin(String reservationId) throws IOException {
        Reservation r = findReservation(reservationId);
        if (r == null) throw new IllegalArgumentException("Reservation not found.");
        cancelReservation(r);
    }

    private void cancelReservation(Reservation r) throws IOException {
        if (!r.getStatus().equals("CONFIRMED")) throw new IllegalArgumentException("Only confirmed bookings can be cancelled.");
        TrainSchedule s = findSchedule(r.getScheduleId());
        if (s != null && s.getDate().compareTo(LocalDate.now().toString()) < 0) throw new IllegalArgumentException("Past journeys cannot be cancelled.");
        r.cancel();
        saveReservations();
    }

    public List<Reservation> reservationsFor(Passenger p) {
        List<Reservation> list = new ArrayList<>();
        for (Reservation r : reservations) if (r.getPassengerId().equals(p.getId())) list.add(r);
        return list;
    }

    public Train findTrain(String id) {
        for (Train t : trains) if (t.getId().equalsIgnoreCase(id)) return t;
        return null;
    }
    public TrainSchedule findSchedule(String id) {
        for (TrainSchedule s : schedules) if (s.getId().equalsIgnoreCase(id)) return s;
        return null;
    }
    public Reservation findReservation(String id) {
        for (Reservation r : reservations) if (r.getId().equalsIgnoreCase(id)) return r;
        return null;
    }
    public Passenger findPassenger(String id) {
        for (Passenger p : passengers) if (p.getId().equals(id)) return p;
        return null;
    }

    public String scheduleLabel(TrainSchedule s) {
        Train t = findTrain(s.getTrainId());
        return s.getId() + " | " + (t == null ? s.getTrainId() : t.getName()) + " | " + s.getDate();
    }

    public String reservationDetails(Reservation r) {
        TrainSchedule s = findSchedule(r.getScheduleId());
        Train t = s == null ? null : findTrain(s.getTrainId());
        Passenger p = findPassenger(r.getPassengerId());
        return "Reservation: " + r.getId() + "\nPassenger: " + (p == null ? r.getPassengerId() : p.getName())
                + "\nTrain: " + (t == null ? "Unknown" : t.getName())
                + "\nRoute: " + (t == null ? "" : t.getSource() + " to " + t.getDestination())
                + "\nDate: " + (s == null ? "" : s.getDate())
                + "\nDeparture: " + (t == null ? "" : t.getDeparture())
                + "\nCoach: " + (t == null ? "" : t.getCoachType())
                + "\nSeat: " + r.getSeatNumber() + "\nFare: Tk " + String.format("%.2f", r.getFare())
                + "\nStatus: " + r.getStatus();
    }

    public String reportSummary() {
        int confirmed = 0, cancelled = 0;
        double revenue = 0;
        for (Reservation r : reservations) {
            if (r.getStatus().equals("CONFIRMED")) { confirmed++; revenue += r.getFare(); }
            else cancelled++;
        }
        return "Total trains: " + trains.size() + "\nRegistered passengers: " + passengers.size()
                + "\nSchedules: " + schedules.size() + "\nConfirmed bookings: " + confirmed
                + "\nCancelled bookings: " + cancelled + "\nConfirmed booking value: Tk "
                + String.format("%.2f", revenue);
    }

    private String nextId(String prefix, List<?> list) {
        return prefix + String.format("%04d", list.size() + 1);
    }
    private void saveTrains() throws IOException {
        List<String> rows = new ArrayList<>(); for (Train t : trains) rows.add(t.toFileLine());
        files.writeLines("trains.txt", rows);
    }
    private void savePassengers() throws IOException {
        List<String> rows = new ArrayList<>(); for (Passenger p : passengers) rows.add(passengerToLine(p));
        files.writeLines("passengers.txt", rows);
    }
    private void saveSchedules() throws IOException {
        List<String> rows = new ArrayList<>(); for (TrainSchedule s : schedules) rows.add(s.toFileLine());
        files.writeLines("schedules.txt", rows);
    }
    private void saveReservations() throws IOException {
        List<String> rows = new ArrayList<>(); for (Reservation r : reservations) rows.add(r.toFileLine());
        files.writeLines("reservations.txt", rows);
    }
}
