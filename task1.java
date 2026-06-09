import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

// User class for storing user information
class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    private String phone;

    public User(String userId, String password, String name, String email, String phone) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}

// Train class for storing train information
class Train {
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private String departureTime;
    private int totalSeats;
    private int availableSeats;

    public Train(String trainNumber, String trainName, String source, String destination,
                 String departureTime, int totalSeats) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public int getAvailableSeats() { return availableSeats; }
    public void bookSeat() { if (availableSeats > 0) availableSeats--; }
    public void cancelSeat() { if (availableSeats < totalSeats) availableSeats++; }
}

// Reservation class for storing reservation information
class Reservation {
    private String pnr;
    private String userId;
    private String trainNumber;
    private String trainName;
    private String classType;
    private String journeyDate;
    private String source;
    private String destination;
    private Date bookingDate;
    private int numberOfPassengers;
    private String status;

    public Reservation(String pnr, String userId, String trainNumber, String trainName,
                       String classType, String journeyDate, String source, String destination,
                       int numberOfPassengers) {
        this.pnr = pnr;
        this.userId = userId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.source = source;
        this.destination = destination;
        this.bookingDate = new Date();
        this.numberOfPassengers = numberOfPassengers;
        this.status = "CONFIRMED";
    }

    public String getPnr() { return pnr; }
    public String getUserId() { return userId; }
    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public String getClassType() { return classType; }
    public String getJourneyDate() { return journeyDate; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public int getNumberOfPassengers() { return numberOfPassengers; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDetails() {
        return "PNR: " + pnr + "\nUser ID: " + userId + "\nTrain Number: " + trainNumber +
                "\nTrain Name: " + trainName + "\nClass: " + classType + 
                "\nJourney Date: " + journeyDate + "\nFrom: " + source + "\nTo: " + destination +
                "\nPassengers: " + numberOfPassengers + "\nStatus: " + status;
    }
}

// Database class for managing all data operations
class ReservationDatabase {
    private HashMap<String, User> users;
    private HashMap<String, Train> trains;
    private HashMap<String, Reservation> reservations;
    private int pnrCounter;

    public ReservationDatabase() {
        users = new HashMap<>();
        trains = new HashMap<>();
        reservations = new HashMap<>();
        pnrCounter = 100000;
        initializeData();
    }

    private void initializeData() {
        // Add sample users
        users.put("user1", new User("user1", "pass123", "John Doe", "john@email.com", "9876543210"));
        users.put("user2", new User("user2", "pass456", "Jane Smith", "jane@email.com", "9876543211"));
        users.put("admin", new User("admin", "admin123", "Admin User", "admin@email.com", "9999999999"));

        // Add sample trains
        trains.put("12001", new Train("12001", "Rajdhani Express", "Delhi", "Mumbai", "06:00 AM", 100));
        trains.put("12002", new Train("12002", "Shatabdi Express", "Delhi", "Agra", "07:00 AM", 80));
        trains.put("12003", new Train("12003", "Duranto Express", "Mumbai", "Bangalore", "08:00 PM", 120));
        trains.put("12004", new Train("12004", "Garib Rath", "Kolkata", "Chennai", "09:00 AM", 150));
        trains.put("12005", new Train("12005", "Express Train", "Delhi", "Jaipur", "10:00 AM", 90));
    }

    public boolean authenticateUser(String userId, String password) {
        User user = users.get(userId);
        return user != null && user.getPassword().equals(password);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public Train getTrain(String trainNumber) {
        return trains.get(trainNumber);
    }

    public Collection<Train> getAllTrains() {
        return trains.values();
    }

    public String addReservation(String userId, String trainNumber, String classType,
                                  String journeyDate, String source, String destination,
                                  int numberOfPassengers) {
        Train train = trains.get(trainNumber);
        if (train == null || train.getAvailableSeats() < numberOfPassengers) {
            return null;
        }

        String pnr = "PNR" + (++pnrCounter);
        Reservation reservation = new Reservation(pnr, userId, trainNumber, train.getTrainName(),
                classType, journeyDate, source, destination, numberOfPassengers);
        reservations.put(pnr, reservation);
        train.bookSeat();
        return pnr;
    }

    public Reservation getReservation(String pnr) {
        return reservations.get(pnr);
    }

    public boolean cancelReservation(String pnr) {
        Reservation reservation = reservations.get(pnr);
        if (reservation != null) {
            Train train = trains.get(reservation.getTrainNumber());
            if (train != null) {
                train.cancelSeat();
                reservation.setStatus("CANCELLED");
                return true;
            }
        }
        return false;
    }
}

// Login Form GUI
class LoginForm extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private ReservationDatabase database;
    private String currentUser;

    public LoginForm(ReservationDatabase database) {
        this.database = database;
        setupUI();
    }

    private void setupUI() {
        setTitle("Online Reservation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Online Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // User ID
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(userIdLabel, gbc);

        userIdField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(userIdField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 12));
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> handleLogin());

        add(panel);
        setVisible(true);
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter User ID and Password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (database.authenticateUser(userId, password)) {
            currentUser = userId;
            dispose();
            new MainMenu(database, userId);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid User ID or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            userIdField.setText("");
            passwordField.setText("");
        }
    }
}

// Main Menu after login
class MainMenu extends JFrame {
    private ReservationDatabase database;
    private String currentUser;

    public MainMenu(ReservationDatabase database, String userId) {
        this.database = database;
        this.currentUser = userId;
        setupUI();
    }

    private void setupUI() {
        setTitle("Online Reservation System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        JLabel titleLabel = new JLabel("Welcome to Online Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // User info
        User user = database.getUser(currentUser);
        JLabel userInfoLabel = new JLabel("Logged in as: " + user.getName() + " (" + currentUser + ")");
        userInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 1;
        panel.add(userInfoLabel, gbc);

        // Reservation Button
        JButton reservationButton = new JButton("New Reservation");
        reservationButton.setFont(new Font("Arial", Font.PLAIN, 14));
        reservationButton.setPreferredSize(new Dimension(200, 50));
        reservationButton.setBackground(new Color(0, 153, 76));
        reservationButton.setForeground(Color.WHITE);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(reservationButton, gbc);

        // Cancellation Button
        JButton cancellationButton = new JButton("Cancel Ticket");
        cancellationButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancellationButton.setPreferredSize(new Dimension(200, 50));
        cancellationButton.setBackground(new Color(204, 0, 0));
        cancellationButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        panel.add(cancellationButton, gbc);

        // View Reservation Button
        JButton viewButton = new JButton("View My Reservations");
        viewButton.setFont(new Font("Arial", Font.PLAIN, 14));
        viewButton.setPreferredSize(new Dimension(200, 50));
        viewButton.setBackground(new Color(0, 102, 204));
        viewButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(viewButton, gbc);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setPreferredSize(new Dimension(200, 50));
        logoutButton.setBackground(new Color(128, 128, 128));
        logoutButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        panel.add(logoutButton, gbc);

        reservationButton.addActionListener(e -> new ReservationForm(database, currentUser));
        cancellationButton.addActionListener(e -> new CancellationForm(database, currentUser));
        viewButton.addActionListener(e -> showReservations());
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm(database);
        });

        add(panel);
        setVisible(true);
    }

    private void showReservations() {
        JOptionPane.showMessageDialog(this, "View reservations feature - Coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}

// Reservation Form GUI
class ReservationForm extends JFrame {
    private ReservationDatabase database;
    private String currentUser;
    private JComboBox<String> trainComboBox;
    private JComboBox<String> classComboBox;
    private JTextField journeyDateField;
    private JSpinner passengerSpinner;

    public ReservationForm(ReservationDatabase database, String userId) {
        this.database = database;
        this.currentUser = userId;
        setupUI();
    }

    private void setupUI() {
        setTitle("Reservation Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("New Reservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Train Selection
        JLabel trainLabel = new JLabel("Train Number:");
        trainLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(trainLabel, gbc);

        Vector<String> trainList = new Vector<>();
        for (Train train : database.getAllTrains()) {
            trainList.add(train.getTrainNumber() + " - " + train.getTrainName());
        }
        trainComboBox = new JComboBox<>(trainList);
        gbc.gridx = 1;
        panel.add(trainComboBox, gbc);

        // Class Type
        JLabel classLabel = new JLabel("Class Type:");
        classLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(classLabel, gbc);

        String[] classes = {"First AC", "Second AC", "Third AC", "Sleeper", "General"};
        classComboBox = new JComboBox<>(classes);
        gbc.gridx = 1;
        panel.add(classComboBox, gbc);

        // Journey Date
        JLabel dateLabel = new JLabel("Journey Date (DD/MM/YYYY):");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(dateLabel, gbc);

        journeyDateField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(journeyDateField, gbc);

        // Number of Passengers
        JLabel passengerLabel = new JLabel("Number of Passengers:");
        passengerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(passengerLabel, gbc);

        passengerSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        gbc.gridx = 1;
        panel.add(passengerSpinner, gbc);

        // Submit Button
        JButton submitButton = new JButton("Book Ticket");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 12));
        submitButton.setBackground(new Color(0, 153, 76));
        submitButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);

        submitButton.addActionListener(e -> handleReservation());

        add(panel);
        setVisible(true);
    }

    private void handleReservation() {
        String selectedTrain = (String) trainComboBox.getSelectedItem();
        String trainNumber = selectedTrain.split(" - ")[0];
        String classType = (String) classComboBox.getSelectedItem();
        String journeyDate = journeyDateField.getText().trim();
        int passengers = (Integer) passengerSpinner.getValue();

        if (journeyDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter journey date!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Train train = database.getTrain(trainNumber);
        if (train == null) {
            JOptionPane.showMessageDialog(this, "Train not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (train.getAvailableSeats() < passengers) {
            JOptionPane.showMessageDialog(this, "Not enough seats available!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String pnr = database.addReservation(currentUser, trainNumber, classType, journeyDate,
                train.getSource(), train.getDestination(), passengers);

        if (pnr != null) {
            JOptionPane.showMessageDialog(this, "Reservation Successful!\nPNR: " + pnr,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Reservation failed! Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// Cancellation Form GUI
class CancellationForm extends JFrame {
    private ReservationDatabase database;
    private String currentUser;
    private JTextField pnrField;
    private JTextArea detailsArea;

    public CancellationForm(ReservationDatabase database, String userId) {
        this.database = database;
        this.currentUser = userId;
        setupUI();
    }

    private void setupUI() {
        setTitle("Cancellation Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Cancel Reservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // PNR Field
        JLabel pnrLabel = new JLabel("PNR Number:");
        pnrLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(pnrLabel, gbc);

        pnrField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(pnrField, gbc);

        // Search Button
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton.setBackground(new Color(0, 102, 204));
        searchButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(searchButton, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        // Details Area
        JLabel detailsLabel = new JLabel("Reservation Details:");
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(detailsLabel, gbc);

        detailsArea = new JTextArea(10, 40);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        detailsArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);

        // Cancel Button
        JButton cancelButton = new JButton("Confirm Cancellation");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(204, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        cancelButton.setEnabled(false);
        panel.add(cancelButton, gbc);

        searchButton.addActionListener(e -> searchReservation(cancelButton));
        cancelButton.addActionListener(e -> handleCancellation());

        add(panel);
        setVisible(true);
    }

    private void searchReservation(JButton cancelButton) {
        String pnr = pnrField.getText().trim();

        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter PNR number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Reservation reservation = database.getReservation(pnr);

        if (reservation != null) {
            detailsArea.setText(reservation.getDetails());
            cancelButton.setEnabled(true);
        } else {
            detailsArea.setText("No reservation found with this PNR number!");
            cancelButton.setEnabled(false);
        }
    }

    private void handleCancellation() {
        String pnr = pnrField.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (database.cancelReservation(pnr)) {
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel reservation!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

// Main application class
public class task1 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReservationDatabase database = new ReservationDatabase();
            new LoginForm(database);
        });
    }
}
