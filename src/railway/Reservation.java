package railway;

/** One passenger booking one seat on one scheduled journey. */
public class Reservation {
    private final String id, passengerId, scheduleId, seatNumber;
    private final double fare;
    private String status;

    public Reservation(String id, String passengerId, String scheduleId, String seatNumber, double fare, String status) {
        this.id = id;
        this.passengerId = passengerId;
        this.scheduleId = scheduleId;
        this.seatNumber = seatNumber;
        this.fare = fare;
        this.status = status;
    }

    public String getId() { return id; }
    public String getPassengerId() { return passengerId; }
    public String getScheduleId() { return scheduleId; }
    public String getSeatNumber() { return seatNumber; }
    public double getFare() { return fare; }
    public String getStatus() { return status; }
    public void cancel() { status = "CANCELLED"; }

    public String toFileLine() {
        return String.join("|", id, passengerId, scheduleId, seatNumber, String.valueOf(fare), status);
    }
    public static Reservation fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Reservation(p[0], p[1], p[2], p[3], Double.parseDouble(p[4]), p[5]);
    }
}
