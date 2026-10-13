package railway;

/** A train service definition. */
public class Train {
    private String id, name, source, destination, departure, arrival, coachType;
    private int totalSeats;
    private double fare;
    private boolean active;

    public Train(String id, String name, String source, String destination,
                 String departure, String arrival, int totalSeats, double fare, String coachType) {
        this.id = require(id, "Train ID");
        this.name = require(name, "Train name");
        this.source = require(source, "Source");
        this.destination = require(destination, "Destination");
        if (this.source.equalsIgnoreCase(this.destination)) throw new IllegalArgumentException("Source and destination must differ.");
        this.departure = require(departure, "Departure time");
        this.arrival = require(arrival, "Arrival time");
        if (totalSeats < 1) throw new IllegalArgumentException("Total seats must be positive.");
        if (fare < 0) throw new IllegalArgumentException("Fare cannot be negative.");
        this.totalSeats = totalSeats;
        this.fare = fare;
        this.coachType = require(coachType, "Coach type");
        this.active = true;
    }

    private static String require(String s, String label) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(label + " cannot be empty.");
        return s.trim();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public int getTotalSeats() { return totalSeats; }
    public double getFare() { return fare; }
    public String getCoachType() { return coachType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public void update(String name, String source, String destination, String departure,
                       String arrival, int seats, double fare, String coachType) {
        Train checked = new Train(id, name, source, destination, departure, arrival, seats, fare, coachType);
        this.name = checked.name;
        this.source = checked.source;
        this.destination = checked.destination;
        this.departure = checked.departure;
        this.arrival = checked.arrival;
        this.totalSeats = checked.totalSeats;
        this.fare = checked.fare;
        this.coachType = checked.coachType;
    }

    public String toFileLine() {
        return String.join("|", id, enc(name), enc(source), enc(destination), enc(departure), enc(arrival),
                String.valueOf(totalSeats), String.valueOf(fare), enc(coachType), String.valueOf(active));
    }

    public static Train fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        Train t = new Train(p[0], dec(p[1]), dec(p[2]), dec(p[3]), dec(p[4]), dec(p[5]),
                Integer.parseInt(p[6]), Double.parseDouble(p[7]), dec(p[8]));
        t.active = Boolean.parseBoolean(p[9]);
        return t;
    }

    static String enc(String s) { return s.replace("%", "%25").replace("|", "%7C").replace("\n", "%0A"); }
    static String dec(String s) { return s.replace("%0A", "\n").replace("%7C", "|").replace("%25", "%"); }
}
