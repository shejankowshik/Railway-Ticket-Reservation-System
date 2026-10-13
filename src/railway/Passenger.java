package railway;

/** Passenger account. */
public class Passenger extends Person {
    private String contact;

    public Passenger(String id, String name, String username, String password, String contact) {
        super(id, name, username, password);
        this.contact = contact == null ? "" : contact;
    }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact == null ? "" : contact.trim(); }

    @Override
    public String getRole() { return "PASSENGER"; }
}
