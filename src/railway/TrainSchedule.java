package railway;

/** A train running on a particular journey date. */
public class TrainSchedule {
    private String id;
    private String trainId;
    private String date;

    public TrainSchedule(String id, String trainId, String date) {
        if (id == null || id.trim().isEmpty() || trainId == null || trainId.trim().isEmpty())
            throw new IllegalArgumentException("Schedule ID and train ID are required.");
        if (date == null || date.trim().isEmpty()) throw new IllegalArgumentException("Journey date is required.");
        this.id = id.trim();
        this.trainId = trainId.trim();
        this.date = date.trim();
    }

    public String getId() { return id; }
    public String getTrainId() { return trainId; }
    public String getDate() { return date; }

    public String toFileLine() { return id + "|" + trainId + "|" + date; }
    public static TrainSchedule fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new TrainSchedule(p[0], p[1], p[2]);
    }
}
