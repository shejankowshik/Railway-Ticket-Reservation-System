package railway;

/** Common parent class for system users. */
public abstract class Person {
    private final String id;
    private String name;
    private String username;
    private String password;

    public Person(String id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Username cannot be empty.");
        this.username = username.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) throw new IllegalArgumentException("Password cannot be empty.");
        this.password = password;
    }

    public abstract String getRole();
}
