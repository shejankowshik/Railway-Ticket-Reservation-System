package railway;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/** Main Swing interface. Uses beginner-friendly panels, forms, tables, and dialogs. */
public class MainFrame extends JFrame {
    private final RailwayService service;
    private Passenger currentPassenger;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);

    public MainFrame(RailwayService service) {
        this.service = service;
        setTitle("Railway Ticket Reservation System");
        setSize(1000, 650);
        setMinimumSize(new Dimension(850, 560));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        root.add(createWelcomePanel(), "welcome");
        root.add(createAdminLoginPanel(), "adminLogin");
        root.add(createPassengerLoginPanel(), "passengerLogin");
        root.add(createRegisterPanel(), "register");
        setContentPane(root);
    }

    private JPanel page(String title) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("SansSerif", Font.BOLD, 25));
        panel.add(heading, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(239, 245, 250));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(10, 10, 10, 10);
        JLabel title = new JLabel("RAILWAY TICKET RESERVATION");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        p.add(title, c);
        c.gridy++;
        JLabel subtitle = new JLabel("Java Swing • OOP • Local TXT File Storage");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        p.add(subtitle, c);
        c.gridy++;
        JButton admin = new JButton("Admin Login");
        JButton passenger = new JButton("Passenger Login");
        JButton register = new JButton("Register Passenger");
        JButton exit = new JButton("Exit");
        JPanel buttons = new JPanel(new GridLayout(4, 1, 8, 8));
        buttons.setOpaque(false);
        buttons.add(admin); buttons.add(passenger); buttons.add(register); buttons.add(exit);
        buttons.setPreferredSize(new Dimension(260, 180));
        p.add(buttons, c);
        admin.addActionListener(e -> cardLayout.show(root, "adminLogin"));
        passenger.addActionListener(e -> cardLayout.show(root, "passengerLogin"));
        register.addActionListener(e -> cardLayout.show(root, "register"));
        exit.addActionListener(e -> dispose());
        c.gridy++;
        JLabel demo = new JLabel("Demo admin: admin / admin123");
        demo.setForeground(Color.DARK_GRAY);
        p.add(demo, c);
        return p;
    }

    private JPanel createAdminLoginPanel() {
        JPanel p = page("Admin Login");
        JPanel form = new JPanel(new GridBagLayout());
        JTextField username = new JTextField(18);
        JPasswordField password = new JPasswordField(18);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 7, 7, 7); c.anchor = GridBagConstraints.WEST;
        addFormRow(form, c, 0, "Username:", username);
        addFormRow(form, c, 1, "Password:", password);
        JButton login = new JButton("Login");
        JButton back = new JButton("Back");
        JPanel actions = new JPanel(); actions.add(login); actions.add(back);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; form.add(actions, c);
        p.add(form, BorderLayout.CENTER);
        login.addActionListener(e -> {
            if (service.loginAdmin(username.getText().trim(), new String(password.getPassword()))) showAdminDashboard();
            else message("Invalid admin username or password.");
        });
        back.addActionListener(e -> cardLayout.show(root, "welcome"));
        return p;
    }

    private JPanel createPassengerLoginPanel() {
        JPanel p = page("Passenger Login");
        JPanel form = new JPanel(new GridBagLayout());
        JTextField username = new JTextField(18);
        JPasswordField password = new JPasswordField(18);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 7, 7, 7); c.anchor = GridBagConstraints.WEST;
        addFormRow(form, c, 0, "Username:", username);
        addFormRow(form, c, 1, "Password:", password);
        JButton login = new JButton("Login");
        JButton register = new JButton("Register");
        JButton back = new JButton("Back");
        JPanel actions = new JPanel(); actions.add(login); actions.add(register); actions.add(back);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; form.add(actions, c);
        p.add(form, BorderLayout.CENTER);
        login.addActionListener(e -> {
            Passenger psg = service.loginPassenger(username.getText().trim(), new String(password.getPassword()));
            if (psg == null) message("Invalid username or password.");
            else { currentPassenger = psg; showPassengerDashboard(); }
        });
        register.addActionListener(e -> cardLayout.show(root, "register"));
        back.addActionListener(e -> cardLayout.show(root, "welcome"));
        return p;
    }

    private JPanel createRegisterPanel() {
        JPanel p = page("Passenger Registration");
        JPanel form = new JPanel(new GridBagLayout());
        JTextField name = new JTextField(20), username = new JTextField(20), contact = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6); c.anchor = GridBagConstraints.WEST;
        addFormRow(form, c, 0, "Full name:", name);
        addFormRow(form, c, 1, "Username:", username);
        addFormRow(form, c, 2, "Password:", password);
        addFormRow(form, c, 3, "Contact (optional):", contact);
        JButton create = new JButton("Create Account"), back = new JButton("Back");
        JPanel actions = new JPanel(); actions.add(create); actions.add(back);
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2; form.add(actions, c);
        p.add(form, BorderLayout.CENTER);
        create.addActionListener(e -> runSafely(() -> {
            Passenger created = service.registerPassenger(name.getText(), username.getText(),
                    new String(password.getPassword()), contact.getText());
            currentPassenger = created;
            message("Registration successful. Your passenger ID is " + created.getId());
            showPassengerDashboard();
        }));
        back.addActionListener(e -> cardLayout.show(root, "welcome"));
        return p;
    }

    private void showAdminDashboard() {
        JPanel p = page("Admin Dashboard");
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Trains", adminTrainsPanel());
        tabs.addTab("Schedules", adminSchedulesPanel());
        tabs.addTab("Reservations", adminReservationsPanel());
        tabs.addTab("Passengers", adminPassengersPanel());
        JPanel report = new JPanel(new BorderLayout());
        JTextArea reportText = new JTextArea(service.reportSummary());
        reportText.setEditable(false); reportText.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JButton refresh = new JButton("Refresh Report");
        refresh.addActionListener(e -> reportText.setText(service.reportSummary()));
        report.add(new JScrollPane(reportText), BorderLayout.CENTER); report.add(refresh, BorderLayout.SOUTH);
        tabs.addTab("Reports", report);
        p.add(tabs, BorderLayout.CENTER);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> cardLayout.show(root, "welcome"));
        p.add(logout, BorderLayout.SOUTH);
        replacePage("admin", p);
    }

    private JPanel adminTrainsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        String[] columns = {"ID", "Name", "Source", "Destination", "Departure", "Arrival", "Seats", "Fare", "Coach", "Active"};
        DefaultTableModel model = tableModel(columns);
        JTable table = new JTable(model);
        refreshTrainTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 6, 5, 5));
        JTextField id = new JTextField(), name = new JTextField(), source = new JTextField(), destination = new JTextField();
        JTextField departure = new JTextField("08:00"), arrival = new JTextField("14:00");
        JTextField seats = new JTextField("40"), fare = new JTextField("500");
        JComboBox<String> coach = new JComboBox<>(new String[]{"AC", "Non-AC"});
        form.add(new JLabel("Train ID (new only)")); form.add(id);
        form.add(new JLabel("Train name")); form.add(name);
        form.add(new JLabel("Source")); form.add(source);
        form.add(new JLabel("Destination")); form.add(destination);
        form.add(new JLabel("Departure")); form.add(departure);
        form.add(new JLabel("Arrival")); form.add(arrival);
        form.add(new JLabel("Total seats")); form.add(seats);
        form.add(new JLabel("Fare")); form.add(fare);
        form.add(new JLabel("Coach type")); form.add(coach);
        JButton add = new JButton("Add Train"), update = new JButton("Update Selected"), deactivate = new JButton("Deactivate Selected"), refresh = new JButton("Refresh");
        JPanel buttons = new JPanel(); buttons.add(add); buttons.add(update); buttons.add(deactivate); buttons.add(refresh);
        JPanel south = new JPanel(new BorderLayout()); south.add(form, BorderLayout.CENTER); south.add(buttons, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.convertRowIndexToModel(table.getSelectedRow());
                id.setText(String.valueOf(model.getValueAt(r, 0)));
                name.setText(String.valueOf(model.getValueAt(r, 1)));
                source.setText(String.valueOf(model.getValueAt(r, 2)));
                destination.setText(String.valueOf(model.getValueAt(r, 3)));
                departure.setText(String.valueOf(model.getValueAt(r, 4)));
                arrival.setText(String.valueOf(model.getValueAt(r, 5)));
                seats.setText(String.valueOf(model.getValueAt(r, 6)));
                fare.setText(String.valueOf(model.getValueAt(r, 7)));
                coach.setSelectedItem(String.valueOf(model.getValueAt(r, 8)));
            }
        });
        add.addActionListener(e -> runSafely(() -> {
            Train t = new Train(id.getText(), name.getText(), source.getText(), destination.getText(),
                    departure.getText(), arrival.getText(), Integer.parseInt(seats.getText()),
                    Double.parseDouble(fare.getText()), String.valueOf(coach.getSelectedItem()));
            service.addTrain(t); refreshTrainTable(model); message("Train added.");
        }));
        update.addActionListener(e -> runSafely(() -> {
            if (table.getSelectedRow() < 0) throw new IllegalArgumentException("Select a train first.");
            service.updateTrain(id.getText(), name.getText(), source.getText(), destination.getText(),
                    departure.getText(), arrival.getText(), Integer.parseInt(seats.getText()),
                    Double.parseDouble(fare.getText()), String.valueOf(coach.getSelectedItem()));
            refreshTrainTable(model); message("Train updated.");
        }));
        deactivate.addActionListener(e -> runSafely(() -> {
            if (table.getSelectedRow() < 0) throw new IllegalArgumentException("Select a train first.");
            service.deactivateTrain(id.getText()); refreshTrainTable(model); message("Train deactivated.");
        }));
        refresh.addActionListener(e -> refreshTrainTable(model));
        return panel;
    }

    private void refreshTrainTable(DefaultTableModel m) {
        m.setRowCount(0);
        for (Train t : service.getTrains())
            m.addRow(new Object[]{t.getId(), t.getName(), t.getSource(), t.getDestination(), t.getDeparture(),
                    t.getArrival(), t.getTotalSeats(), t.getFare(), t.getCoachType(), t.isActive()});
    }

    private JPanel adminSchedulesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        DefaultTableModel model = tableModel(new String[]{"Schedule ID", "Train ID", "Train Name", "Date"});
        JTable table = new JTable(model);
        refreshScheduleTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> trainBox = new JComboBox<>();
        refreshTrainCombo(trainBox);
        JTextField date = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
        JButton add = new JButton("Add Schedule"), refresh = new JButton("Refresh");
        form.add(new JLabel("Train:")); form.add(trainBox); form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(date); form.add(add); form.add(refresh);
        panel.add(form, BorderLayout.SOUTH);
        add.addActionListener(e -> runSafely(() -> {
            String selected = (String) trainBox.getSelectedItem();
            if (selected == null) throw new IllegalArgumentException("Add a train first.");
            service.addSchedule(selected.split(" - ")[0], date.getText().trim());
            refreshScheduleTable(model); message("Schedule added.");
        }));
        refresh.addActionListener(e -> { refreshTrainCombo(trainBox); refreshScheduleTable(model); });
        return panel;
    }

    private void refreshTrainCombo(JComboBox<String> box) {
        box.removeAllItems();
        for (Train t : service.getTrains()) if (t.isActive()) box.addItem(t.getId() + " - " + t.getName());
    }
    private void refreshScheduleTable(DefaultTableModel m) {
        m.setRowCount(0);
        for (TrainSchedule s : service.getSchedules()) {
            Train t = service.findTrain(s.getTrainId());
            m.addRow(new Object[]{s.getId(), s.getTrainId(), t == null ? "" : t.getName(), s.getDate()});
        }
    }

    private JPanel adminReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        DefaultTableModel model = tableModel(new String[]{"Booking ID", "Passenger ID", "Schedule ID", "Seat", "Fare", "Status"});
        JTable table = new JTable(model);
        refreshReservationTable(model, service.getReservations());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton cancel = new JButton("Cancel Selected Booking"), details = new JButton("View Ticket"), refresh = new JButton("Refresh");
        JPanel buttons = new JPanel(); buttons.add(details); buttons.add(cancel); buttons.add(refresh);
        panel.add(buttons, BorderLayout.SOUTH);
        details.addActionListener(e -> {
            Reservation r = selectedReservation(table, model);
            if (r != null) message(service.reservationDetails(r));
        });
        cancel.addActionListener(e -> runSafely(() -> {
            Reservation r = selectedReservation(table, model);
            if (r == null) throw new IllegalArgumentException("Select a reservation.");
            service.cancelReservationByAdmin(r.getId());
            refreshReservationTable(model, service.getReservations()); message("Reservation cancelled.");
        }));
        refresh.addActionListener(e -> refreshReservationTable(model, service.getReservations()));
        return panel;
    }

    private JPanel adminPassengersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = tableModel(new String[]{"Passenger ID", "Name", "Username", "Contact"});
        JTable table = new JTable(model);
        refreshPassengerTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshPassengerTable(model));
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }
    private void refreshPassengerTable(DefaultTableModel m) {
        m.setRowCount(0);
        for (Passenger p : service.getPassengers()) m.addRow(new Object[]{p.getId(), p.getName(), p.getUsername(), p.getContact()});
    }

    private void showPassengerDashboard() {
        if (currentPassenger == null) return;
        JPanel p = page("Passenger Portal — Welcome, " + currentPassenger.getName());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Search Trains", passengerSearchPanel());
        tabs.addTab("My Bookings", passengerBookingsPanel());
        tabs.addTab("My Profile", passengerProfilePanel());
        p.add(tabs, BorderLayout.CENTER);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { currentPassenger = null; cardLayout.show(root, "welcome"); });
        p.add(logout, BorderLayout.SOUTH);
        replacePage("passenger", p);
    }

    private JPanel passengerSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> from = new JComboBox<>(new String[]{"Dhaka", "Chattogram", "Sylhet", "Rajshahi", "Khulna"});
        JComboBox<String> to = new JComboBox<>(new String[]{"Chattogram", "Dhaka", "Sylhet", "Rajshahi", "Khulna"});
        JTextField date = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
        JComboBox<String> coach = new JComboBox<>(new String[]{"AC", "Non-AC"});
        JButton searchButton = new JButton("Search");
        search.add(new JLabel("From:")); search.add(from); search.add(new JLabel("To:")); search.add(to);
        search.add(new JLabel("Date:")); search.add(date); search.add(coach); search.add(searchButton);
        panel.add(search, BorderLayout.NORTH);
        DefaultTableModel model = tableModel(new String[]{"Schedule ID", "Train", "Route", "Date", "Departure", "Arrival", "Coach", "Fare", "Seats"});
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton book = new JButton("Book Selected Train");
        panel.add(book, BorderLayout.SOUTH);
        searchButton.addActionListener(e -> runSafely(() -> {
            if (from.getSelectedItem().equals(to.getSelectedItem())) throw new IllegalArgumentException("Choose different stations.");
            LocalDate.parse(date.getText().trim());
            model.setRowCount(0);
            List<TrainSchedule> results = service.searchSchedules(String.valueOf(from.getSelectedItem()),
                    String.valueOf(to.getSelectedItem()), date.getText().trim(), String.valueOf(coach.getSelectedItem()));
            for (TrainSchedule s : results) {
                Train t = service.findTrain(s.getTrainId());
                model.addRow(new Object[]{s.getId(), t.getName(), t.getSource() + " to " + t.getDestination(),
                        s.getDate(), t.getDeparture(), t.getArrival(), t.getCoachType(), t.getFare(), service.availableSeats(s)});
            }
            if (results.isEmpty()) message("No matching train with available seats was found.");
        }));
        book.addActionListener(e -> runSafely(() -> {
            int row = table.getSelectedRow();
            if (row < 0) throw new IllegalArgumentException("Search and select a train first.");
            String scheduleId = String.valueOf(model.getValueAt(table.convertRowIndexToModel(row), 0));
            Reservation r = service.book(currentPassenger, scheduleId);
            message("Booking successful!\n\n" + service.reservationDetails(r));
        }));
        return panel;
    }

    private JPanel passengerBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        DefaultTableModel model = tableModel(new String[]{"Booking ID", "Schedule ID", "Seat", "Fare", "Status"});
        JTable table = new JTable(model);
        refreshPassengerReservationTable(model, service.reservationsFor(currentPassenger));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton details = new JButton("View Ticket"), cancel = new JButton("Cancel Selected"), refresh = new JButton("Refresh");
        JPanel buttons = new JPanel(); buttons.add(details); buttons.add(cancel); buttons.add(refresh);
        panel.add(buttons, BorderLayout.SOUTH);
        details.addActionListener(e -> {
            Reservation r = selectedReservation(table, model);
            if (r != null) message(service.reservationDetails(r));
        });
        cancel.addActionListener(e -> runSafely(() -> {
            Reservation r = selectedReservation(table, model);
            if (r == null) throw new IllegalArgumentException("Select a booking.");
            service.cancelReservation(currentPassenger, r.getId());
            refreshPassengerReservationTable(model, service.reservationsFor(currentPassenger));
            message("Booking cancelled. The seat is available again.");
        }));
        refresh.addActionListener(e -> refreshPassengerReservationTable(model, service.reservationsFor(currentPassenger)));
        return panel;
    }

    private JPanel passengerProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField name = new JTextField(currentPassenger.getName(), 22);
        JTextField contact = new JTextField(currentPassenger.getContact(), 22);
        JPasswordField newPassword = new JPasswordField(22);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 7, 7, 7); c.anchor = GridBagConstraints.WEST;
        addFormRow(panel, c, 0, "Passenger ID:", new JLabel(currentPassenger.getId()));
        addFormRow(panel, c, 1, "Name:", name);
        addFormRow(panel, c, 2, "Contact:", contact);
        addFormRow(panel, c, 3, "New password (optional):", newPassword);
        JButton save = new JButton("Save Profile");
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2; panel.add(save, c);
        save.addActionListener(e -> runSafely(() -> {
            service.updateProfile(currentPassenger, name.getText(), contact.getText(), new String(newPassword.getPassword()));
            message("Profile updated.");
            showPassengerDashboard();
        }));
        return panel;
    }

    private void refreshReservationTable(DefaultTableModel m, List<Reservation> list) {
        m.setRowCount(0);
        for (Reservation r : list) m.addRow(new Object[]{r.getId(), r.getPassengerId(), r.getScheduleId(),
                r.getSeatNumber(), r.getFare(), r.getStatus()});
    }

    private void refreshPassengerReservationTable(DefaultTableModel m, List<Reservation> list) {
        m.setRowCount(0);
        for (Reservation r : list) m.addRow(new Object[]{r.getId(), r.getScheduleId(),
                r.getSeatNumber(), r.getFare(), r.getStatus()});
    }

    private Reservation selectedReservation(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) { message("Select a reservation first."); return null; }
        String id = String.valueOf(model.getValueAt(table.convertRowIndexToModel(row), 0));
        return service.findReservation(id);
    }

    private DefaultTableModel tableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private void addFormRow(JPanel panel, GridBagConstraints c, int row, String label, Component field) {
        c.gridx = 0; c.gridy = row; c.gridwidth = 1; c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(field, c);
    }

    private void replacePage(String name, JPanel page) {
        root.add(page, name);
        cardLayout.show(root, name);
        root.revalidate();
        root.repaint();
    }

    private void runSafely(CheckedAction action) {
        try { action.run(); }
        catch (Exception ex) { message("Operation failed: " + ex.getMessage()); }
    }
    private interface CheckedAction { void run() throws Exception; }

    private void message(String text) {
        JOptionPane.showMessageDialog(this, text, "Railway Reservation System", JOptionPane.INFORMATION_MESSAGE);
    }
}
