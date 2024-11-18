import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;
import javax.swing.event.TableModelEvent;


class DatabaseConnection1 {
    // Database URL, username, and password for connecting to MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/event_management"; // URL to the MySQL database
    private static final String USER = "root"; // Username for MySQL connection
    private static final String PASS = "root"; // Password for MySQL connection

    public static Connection getConnection() throws SQLException {
        // Establish and return the connection to the database using the credentials
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}

public class Gandhaar_Management {
    public static void main(String[] args) {
        new RoleSelection();
    }
}

class RoleSelection extends JFrame {
    public RoleSelection() {
        // Set the title for the frame
        setTitle("Role Selection");

        // Set the layout manager to FlowLayout, which arranges components in a left-to-right flow
        setLayout(new FlowLayout());

        // Create buttons for selecting the role: Student or Organizer
        JButton studentButton = new JButton("Student");
        JButton organizerButton = new JButton("Organizer");

        // Add an ActionListener for the "Student" button
        studentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Student Registration form and close the current window
                new StudentRegistration();
                dispose(); // Close the RoleSelection window
            }
        });

        // Add an ActionListener for the "Organizer" button
        organizerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Organizer Login form and close the current window
                new OrganizerLogin();  // Open the Gandhaar Dashboard for organizer
                dispose(); // Close the RoleSelection window
            }
        });

        // Add the buttons to the JFrame
        add(studentButton);
        add(organizerButton);

        // Set the size of the frame
        setSize(300, 150);

        // Set the default close operation (exit the application when the window is closed)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the location of the window to be centered on the screen
        setLocationRelativeTo(null);

        // Make the frame visible
        setVisible(true);
    }
}



class StudentRegistration extends JFrame {

    // Declare components for form fields and the back button
    private JTextField nameField, emailField, phoneField, departmentField, yearField;
    private JButton backButton;

//    // Method to set up the Back button functionality
//    private void setupBackButton(JFrame previousFrame) {
//        backButton = new JButton("Back");
//        // ActionListener to handle the back button click
//        backButton.addActionListener(e -> {
//            previousFrame.setVisible(true);  // Show the previous frame
//            dispose();  // Dispose of the current frame
//        });
//        add(backButton);  // Add the back button to the frame
//    }

    // Constructor for the Student Registration form
    public StudentRegistration() {
        setTitle("Student Registration");  // Set window title
        setLayout(new GridLayout(6, 2));  // Use a GridLayout for form fields

        // Add form labels and corresponding text fields
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Department:"));
        departmentField = new JTextField();
        add(departmentField);

        add(new JLabel("Year:"));
        yearField = new JTextField();
        add(yearField);

        // Create the Register button and add its functionality
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> registerStudent());
        add(registerButton);

        // Set up window properties
        setSize(400, 300);  // Set the window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Ensure the app exits when the window is closed
        setLocationRelativeTo(null);  // Center the window on the screen
        setVisible(true);  // Make the window visible
    }

    // Method to handle the student registration
    private void registerStudent() {
        // Retrieve data from text fields
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String department = departmentField.getText();
        String year = yearField.getText();

        // Check if any field is empty
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || department.isEmpty() || year.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");  // Show error message
            return;  // Exit method if validation fails
        }

        // Try to connect to the database and insert the student data
        try (Connection con = DatabaseConnection1.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO Students (name, email, phone_number, department, year) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            // Set the values of the prepared statement from the form fields
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, department);
            ps.setString(5, year);
            ps.executeUpdate();  // Execute the insert statement

            // Retrieve the generated student ID from the database
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int studentId = rs.getInt(1);  // Get the auto-generated student ID
                new StudentDashboard(studentId);  // Open the student dashboard
                dispose();  // Close the registration window
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Print the error stack trace if an exception occurs
        }

        sendingmail(email);
    }

    void sendingmail(String receipent ) {

        String from = "vaishnavi.machale@cumminscollege.in";
        String password = "tyqxatkhddojxfyj";  // Use the exact App Password here

        // Recipient's email ID
        String to = receipent;

        // SMTP server configuration
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Get the Session object and authenticate
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the message
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the message
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject header field
            message.setSubject("Registration for Gandhaar event");

            // Now set the actual message
            message.setText(" Your registration for Gandhaar event is succesfull!!");

            // Send message
            Transport.send(message);
            // System.out.println("Email sent successfully!");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}

class StudentDashboard extends JFrame {
    private JTable eventTable;  // Table to display events
    private DefaultTableModel model;  // Table model to handle data
    private int studentId;  // The ID of the student accessing the dashboard

    // Constructor to initialize the dashboard
    public StudentDashboard(int studentId) {
        this.studentId = studentId;  // Assign student ID passed to the constructor
        setTitle("Student Dashboard");  // Set window title
        setLayout(new BorderLayout());  // Use BorderLayout for the main layout

        // Initialize the table model with non-editable cells
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make all cells non-editable
            }
        };

        // Initialize the table with the model
        eventTable = new JTable(model);

        // Add columns to the model
        model.addColumn("Event ID");
        model.addColumn("Event Name");
        model.addColumn("Venue");
        model.addColumn("Date");
        model.addColumn("Time");

        // Load the available events into the table
        loadAvailableEvents();
        // Remove the "Event ID" column from the table (it's only needed for backend)
        eventTable.removeColumn(eventTable.getColumnModel().getColumn(0));

        // Add the table inside a scroll pane for better viewing
        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        // Create and add the "Register for Selected Events" button
        JButton registerButton = new JButton("Register for Selected Events");
        registerButton.addActionListener(e -> registerForEvents());  // Register event handler
        add(registerButton, BorderLayout.SOUTH);

        // Set the size, close operation, and other window properties
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method to load available events from the database
    private void loadAvailableEvents() {
        model.setRowCount(0);  // Clear any existing rows in the table

        // Query to fetch event data from the database
        try (Connection con = DatabaseConnection1.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, venue, date, CONCAT(start_time, ' - ', end_time) AS time FROM Events")) {

            // Loop through the result set and add rows to the table model
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),  // Event ID
                        rs.getString("name"),  // Event Name
                        rs.getString("venue"),  // Venue
                        rs.getString("date"),  // Date
                        rs.getString("time")  // Time
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Print stack trace if SQL exception occurs
        }
    }

    // Method to handle the registration for selected events
    private void registerForEvents() {
        int[] selectedRows = eventTable.getSelectedRows();  // Get the indices of selected rows

        // If no events are selected, show already registered events
        if (selectedRows.length == 0) {
            showRegisteredEvents();
            return;
        }

        // Insert the selected events into the event_registration table
        try (Connection con = DatabaseConnection1.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO event_registration (student_id, event_id) VALUES (?, ?)")) {

            // Loop through the selected rows and register each event
            for (int row : selectedRows) {
                int eventId = (int) model.getValueAt(row, 0);  // Get the Event ID
                ps.setInt(1, studentId);  // Set student ID
                ps.setInt(2, eventId);  // Set event ID
                ps.addBatch();  // Add to the batch for execution
            }

            // Execute the batch to insert the data into the database
            ps.executeBatch();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Successfully registered for selected events!");
            showRegisteredEvents();  // Show the list of registered events

        } catch (SQLException e) {
            e.printStackTrace();  // Print stack trace if SQL exception occurs
        }
    }

    // Method to show the list of events the student has already registered for
    private void showRegisteredEvents() {
        StringBuilder registeredEvents = new StringBuilder("Registered Events:\n");

        // Query to fetch the events that the student has already registered for
        try (Connection con = DatabaseConnection1.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT Events.name FROM Events INNER JOIN event_registration er ON Events.id = er.event_id WHERE er.student_id = ?")) {

            ps.setInt(1, studentId);  // Set student ID for the query
            ResultSet rs = ps.executeQuery();  // Execute the query

            // Loop through the result set and append event names to the string builder
            while (rs.next()) {
                registeredEvents.append("- ").append(rs.getString("name")).append("\n");
            }

            // Display the list of registered events in a message dialog
            JOptionPane.showMessageDialog(this, registeredEvents.toString());

        } catch (SQLException e) {
            e.printStackTrace();  // Print stack trace if SQL exception occurs
        }
    }
}



class GandhaarDashboard extends JFrame {
    // Constructor for the main dashboard
    public GandhaarDashboard() {
        setTitle("Gandhaar Dashboard");  // Set title of the window
        setLayout(new FlowLayout());  // Set layout for the dashboard

        // Create buttons for different sections of the dashboard
        JButton eventsButton = new JButton("Events");
        JButton decorationButton = new JButton("Decoration");
        JButton catererButton = new JButton("Caterer");
        JButton sponsorButton = new JButton("Sponsor");

        // ActionListener for Events button: Opens the Events Dashboard
        eventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrganizerDashboard();  // Open Events Dashboard
            }
        });

        // ActionListener for Decoration button: Show decoration details
        decorationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDecorationDetails();  // Open decoration details in editable table
            }
        });

        // ActionListener for Caterer button: Show caterer details
        catererButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCatererDetails();  // Open caterer details in editable table
            }
        });

        // ActionListener for Sponsor button: Show sponsor details
        sponsorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSponsorDetails();  // Open sponsor details in editable table
            }
        });

        // Add buttons to the dashboard window
        add(eventsButton);
        add(decorationButton);
        add(catererButton);
        add(sponsorButton);

        // Set window properties
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close application on window close
        setLocationRelativeTo(null);  // Center window on screen
        setVisible(true);  // Make the window visible
    }

    // Method to show decoration details in an editable table
    private void showDecorationDetails() {
        showEditableTable("Decoration Contractors",
                "SELECT decor_contractor, decor_phno, decor_status FROM Gandhaar WHERE decor_contractor IS NOT NULL",
                "INSERT INTO Gandhaar (decor_contractor, decor_phno, decor_status) VALUES (?, ?, ?)",
                "DELETE FROM Gandhaar WHERE decor_contractor = ?",
                new String[]{ "decor_contractor", "decor_phno", "decor_status"});  // Column names
    }

    // Method to show caterer details in an editable table
    private void showCatererDetails() {
        showEditableTable("Caterer Companies",
                "SELECT cateror_companyname, cateror_phno, cateror_status FROM Gandhaar WHERE cateror_companyname IS NOT NULL",
                "INSERT INTO Gandhaar (cateror_companyname, cateror_phno, cateror_status) VALUES (?, ?, ?)",
                "DELETE FROM Gandhaar WHERE cateror_companyname = ?",
                new String[]{"cateror_companyname", "cateror_phno", "cateror_status"});  // Column names
    }

    // Method to show sponsor details in an editable table
    private void showSponsorDetails() {
        showEditableTable("Sponsors",
                "SELECT sponser_companyname, sponser_contactperson, sponser_email, sponser_phno, sponser_amount FROM Gandhaar WHERE sponser_companyname IS NOT NULL",
                "INSERT INTO Gandhaar (sponser_companyname, sponser_contactperson, sponser_email, sponser_phno, sponser_amount) VALUES (?, ?, ?, ?, ?)",
                "DELETE FROM Gandhaar WHERE sponser_companyname = ?",
                new String[]{"sponser_companyname", "sponser_contactperson", "sponser_email", "sponser_phno", "sponser_amount"});  // Column names
    }

    // Method to display an editable table with the provided data
    private void showEditableTable(String title, String query, String insertQuery, String deleteQuery, String[] columnNames) {
        JDialog dialog = new JDialog(this, title, true);  // Create a modal dialog
        dialog.setLayout(new BorderLayout());  // Set layout for the dialog

        // Initialize table model and table
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);  // Enable row sorting

        // Add columns to the table model
        for (String columnName : columnNames) {
            tableModel.addColumn(columnName);
        }

        // Fetch data from the database and populate the table
        try (Connection con = DatabaseConnection1.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Loop through the result set and add rows to the table
            while (rs.next()) {
                Object[] rowData = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage());  // Show error if any
        }

        // Make table cells editable except for the Company Name column
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();  // Get updated row
                int column = e.getColumn();  // Get updated column
                Object newValue = tableModel.getValueAt(row, column);  // Get new value
                Object companyName = tableModel.getValueAt(row, 0);  // Get Company Name (first column)

                // Update the database with the new value
                String updateQuery = "UPDATE Gandhaar SET " + columnNames[column] + " = ? WHERE " + columnNames[0] + " = ?";
                try (Connection con = DatabaseConnection1.getConnection();
                     PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
                    pstmt.setObject(1, newValue);
                    pstmt.setObject(2, companyName);
                    pstmt.executeUpdate();  // Execute update query
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error updating data: " + ex.getMessage());  // Show error if any
                }
            }
        });

        // Button to add new records
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            Object[] newRow = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                newRow[i] = JOptionPane.showInputDialog("Enter " + columnNames[i]);  // Prompt user for input
            }
            try (Connection con = DatabaseConnection1.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(insertQuery)) {
                // Insert new row data into the database
                for (int i = 0; i < newRow.length; i++) {
                    pstmt.setObject(i + 1, newRow[i]);
                }
                pstmt.executeUpdate();  // Execute insert query
                tableModel.addRow(newRow);  // Add new row to the table
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding data: " + ex.getMessage());  // Show error if any
            }
        });

        // Button to delete selected row
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();  // Get selected row
            if (selectedRow != -1) {
                String companyName = tableModel.getValueAt(selectedRow, 0).toString();  // Get Company Name of selected row
                try (Connection con = DatabaseConnection1.getConnection();
                     PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
                    pstmt.setObject(1, companyName);  // Set the company name parameter for deletion
                    pstmt.executeUpdate();  // Execute delete query
                    tableModel.removeRow(selectedRow);  // Remove the row from the table
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting data: " + ex.getMessage());  // Show error if any
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete");  // Show error if no row is selected
            }
        });

        // Button to calculate total sponsorship amount
        JButton totalSponsorshipButton = new JButton("Total Sponsorship");
        totalSponsorshipButton.addActionListener(e -> {
            try (Connection con = DatabaseConnection1.getConnection();
                 CallableStatement stmt = con.prepareCall("{? = CALL GetTotalSponsorshipAmount()}")) {

                // Register the output parameter (the return value of the function)
                stmt.registerOutParameter(1, Types.DECIMAL);

                // Execute the function
                stmt.execute();

                // Retrieve and display the result
                double total = stmt.getDouble(1); // Get the total from the output parameter
                JOptionPane.showMessageDialog(dialog, "Total Sponsorship Amount: ₹ " + total);  // Display total amount

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error calculating total sponsorship: " + ex.getMessage());  // Show error if any
            }
        });

        // Layout for the buttons at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(totalSponsorshipButton);

        // Adding components to the dialog
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);  // Add table to dialog
        dialog.add(buttonPanel, BorderLayout.SOUTH);  // Add buttons to dialog
        dialog.setSize(600, 400);  // Set dialog size
        dialog.setLocationRelativeTo(this);  // Center dialog on screen
        dialog.setVisible(true);  // Show the dialog
    }
}


class OrganizerLogin extends JFrame{
    // Declare text fields for user input
    private JTextField nameField, rollNoField;

    // Constructor to set up the login window and handle user interactions
    public OrganizerLogin() {
        // Set the title of the window
        setTitle("Organizer Login");

        // Set layout for the form (3 rows, 2 columns)
        setLayout(new GridLayout(3, 2));

        // Add a label and text field for entering the organizer's name
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        // Add a label and text field for entering the organizer's ID (roll number)
        add(new JLabel("Organizer ID:"));
        rollNoField = new JTextField();
        add(rollNoField);

        // Create a submit button and add an ActionListener to handle the login logic
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // Get the input values for name and roll number
            String name = nameField.getText();
            String rollNo = rollNoField.getText();

            // Check if any of the fields are empty
            if (name.isEmpty() || rollNo.isEmpty()) {
                // Display a message if any of the fields are empty
                JOptionPane.showMessageDialog(this, "Please enter all details");
            } else {
                // Check if the entered roll number exists in the organizer table
                if (isOrganizerValid(rollNo)) {
                    // If valid, open the GandhaarDashboard and close the login window
                    new GandhaarDashboard();
                    dispose();  // Close the login window
                } else {
                    // Display an error message if the roll number is invalid
                    JOptionPane.showMessageDialog(this, "Invalid Roll No. or Organizer not found");
                }
            }
        });

        // Add the submit button to the layout
        add(submitButton);

        // Set the size of the window
        setSize(400, 200);
        // Close the application when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Center the window on the screen
        setLocationRelativeTo(null);
        // Make the window visible
        setVisible(true);
    }

    // Method to check if the organizer's roll number exists in the database
    private boolean isOrganizerValid(String rollNo) {
        boolean isValid = false;

        // SQL query to check if the roll number exists in the organizer table
        String query = "SELECT organizer_id FROM organizer WHERE organizer_id = ?";

        try (Connection con = DatabaseConnection1.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // Set the roll number parameter in the query
            pstmt.setString(1, rollNo);
            ResultSet rs = pstmt.executeQuery();

            // If a result is found, the roll number is valid
            if (rs.next()) {
                isValid = true;  // Roll No. found in the organizer table
            }

        } catch (SQLException ex) {
            // Show an error message if there is a database-related issue
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }

        return isValid;
    }
}

class OrganizerDashboard extends JFrame {
    private JTable eventTable; // Table to display event details
    private DefaultTableModel model; // Model for the table
    private JTextArea eventDetailsArea; // Area to show event details

    public OrganizerDashboard() {
        setTitle("Organizer Dashboard - Welcome Organizer");
        setLayout(new BorderLayout());

        // Header Label
        JLabel headerLabel = new JLabel("Events List", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(headerLabel, BorderLayout.NORTH);

        // Table to display event names
        model = new DefaultTableModel();
        eventTable = new JTable(model);
        model.addColumn("Event ID"); // Hidden column for event ID
        model.addColumn("Event Name"); // Visible column for event name

        loadEventNames(); // Method to load event names from the database

        // Hide Event ID column
        eventTable.removeColumn(eventTable.getColumnModel().getColumn(0));

        // Add Mouse Listener for selecting events and showing details
        eventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single-click to show details
                    int selectedRow = eventTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Object eventIdObj = model.getValueAt(selectedRow, 0);
                        if (eventIdObj != null) {
                            int eventId = (int) eventIdObj;
                            showEventDetails(eventId); // Show details for the selected event
                        } else {
                            JOptionPane.showMessageDialog(null, "Event ID is missing!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // Adding event table to the center
        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        // Text area to show event details on the right
        eventDetailsArea = new JTextArea(10, 30);
        eventDetailsArea.setEditable(false); // Set to non-editable
        add(new JScrollPane(eventDetailsArea), BorderLayout.EAST);

        // Panel for Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20)); // FlowLayout with spacing
        buttonPanel.setPreferredSize(new Dimension(getWidth(), 60)); // Set preferred height for the button panel

        // Button to add a new event
        JButton addEventButton = new JButton("Add New Event");
        addEventButton.addActionListener(e -> addEventDialog()); // Open add event dialog
        buttonPanel.add(addEventButton);

        // Button to view all anchors
        JButton viewAnchorsButton = new JButton("View All Anchors");
        viewAnchorsButton.addActionListener(e -> showAllAnchors()); // Show all anchors in a dialog
        buttonPanel.add(viewAnchorsButton);

        // Button to view all registrations
        JButton viewRegistrationsButton = new JButton("View All Registrations");
        viewRegistrationsButton.addActionListener(e -> showAllRegistrations()); // Show all registrations
        buttonPanel.add(viewRegistrationsButton);

        // Add the button panel to the bottom
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }


    // Load event names from the database
    private void loadEventNames() {
        model.setRowCount(0); // Clear existing rows
        try (Connection con = DatabaseConnection1.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM Events")) {

            // Add rows for each event
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), // Event ID (hidden)
                        rs.getString("name") // Event Name (displayed)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print exception in case of error
        }
    }

    // Display detailed information for a selected event
    private void showEventDetails(int eventId) {
        try (Connection con = DatabaseConnection1.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT e.name, e.venue, e.date, e.start_time, e.end_time, e.status, e.registered_count, " +
                             "o.oragnizer_name " +
                             "FROM Events e " +
                             "LEFT JOIN organizer o ON e.id = o.event_id " +
                             "WHERE e.id = ?")) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("Event Name: ").append(rs.getString("name")).append("\n");
                details.append("Venue: ").append(rs.getString("venue")).append("\n");
                details.append("Date: ").append(rs.getString("date")).append("\n");
                details.append("Time: ").append(rs.getString("start_time"))
                        .append(" - ").append(rs.getString("end_time")).append("\n");
                details.append("Status: ").append(rs.getString("status")).append("\n");
                details.append("Registered Count: ").append(rs.getInt("registered_count")).append("\n");

                // Add Organizer Name if available
                String organizerName = rs.getString("oragnizer_name");
                if (organizerName != null && !organizerName.isEmpty()) {
                    details.append("Organizer: ").append(organizerName).append("\n");
                } else {
                    details.append("Organizer: Not Assigned\n");
                }

                eventDetailsArea.setText(details.toString()); // Display event details
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Dialog to add a new event
    private void addEventDialog() {
        JDialog dialog = new JDialog(this, "Add New Event", true);
        dialog.setLayout(new GridLayout(6, 2));

        JTextField eventNameField = new JTextField();
        JTextField venueField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField startTimeField = new JTextField();
        JTextField endTimeField = new JTextField();

        dialog.add(new JLabel("Event Name:"));
        dialog.add(eventNameField);

        dialog.add(new JLabel("Venue:"));
        dialog.add(venueField);

        dialog.add(new JLabel("Date (YYYY-MM-DD):"));
        dialog.add(dateField);

        dialog.add(new JLabel("Start Time (HH:MM):"));
        dialog.add(startTimeField);

        dialog.add(new JLabel("End Time (HH:MM):"));
        dialog.add(endTimeField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = eventNameField.getText();
            String venue = venueField.getText();
            String date = dateField.getText();
            String startTime = startTimeField.getText();
            String endTime = endTimeField.getText();

            // Check if all fields are filled
            if (name.isEmpty() || venue.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            // Save the new event to the database
            addNewEvent(name, venue, date, startTime, endTime);
            dialog.dispose(); // Close the dialog after saving
        });

        dialog.add(saveButton);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Add a new event to the database
    private void addNewEvent(String name, String venue, String date, String startTime, String endTime) {
        try (Connection con = DatabaseConnection1.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO Events (name, venue, date, start_time, end_time, status, registered_count) VALUES (?, ?, ?, ?, ?, 'Scheduled', 0)")) {

            ps.setString(1, name);
            ps.setString(2, venue);
            ps.setString(3, date);
            ps.setString(4, startTime);
            ps.setString(5, endTime);
            ps.executeUpdate(); // Insert the event into the database

            JOptionPane.showMessageDialog(this, "Event added successfully!");
            loadEventNames(); // Refresh the events list
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Show a dialog with all anchors
    private void showAllAnchors() {
        JDialog anchorsDialog = new JDialog(this, "All Anchors", true);
        anchorsDialog.setLayout(new BorderLayout());

        DefaultTableModel anchorsModel = new DefaultTableModel();
        JTable anchorsTable = new JTable(anchorsModel);
        anchorsModel.addColumn("Anchor ID");
        anchorsModel.addColumn("Name");
        anchorsModel.addColumn("Contact Number");

        try (Connection con = DatabaseConnection1.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM anchor")) {

            while (rs.next()) {
                anchorsModel.addRow(new Object[]{
                        rs.getInt("anchor_id"),
                        rs.getString("anchor_name"),
                        rs.getString("phno")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading anchors: " + e.getMessage());
        }

        anchorsDialog.add(new JScrollPane(anchorsTable), BorderLayout.CENTER);
        anchorsDialog.setSize(600, 400);
        anchorsDialog.setLocationRelativeTo(this);
        anchorsDialog.setVisible(true);
    }

    // Show a dialog with all registrations
    private void showAllRegistrations() {
        // Create a dialog to show all registrations
        JDialog registrationsDialog = new JDialog(this, "All Registrations", true);
        registrationsDialog.setLayout(new BorderLayout());

        // Define the table model with columns
        DefaultTableModel registrationsModel = new DefaultTableModel();
        JTable registrationsTable = new JTable(registrationsModel);
        registrationsModel.addColumn("Registration ID");
        registrationsModel.addColumn("Student Name");
        registrationsModel.addColumn("Event Name");
        registrationsModel.addColumn("Registration Date");

        try (Connection con = DatabaseConnection1.getConnection();
             // Use CallableStatement to call the stored procedure
             CallableStatement stmt = con.prepareCall("{CALL list_registers()}");
             // Execute the stored procedure
             ResultSet rs = stmt.executeQuery()) {

            // Loop through the result set and populate the table model with data
            while (rs.next()) {
                registrationsModel.addRow(new Object[]{
                        rs.getInt("registration_id"),
                        rs.getString("student_name"),
                        rs.getString("event_name"),
                        rs.getTimestamp("registration_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading registrations: " + e.getMessage());
        }

        // Add the table to the dialog within a scroll pane
        registrationsDialog.add(new JScrollPane(registrationsTable), BorderLayout.CENTER);

        // Adjust the dialog size and make it visible
        registrationsDialog.setSize(800, 500);  // Adjust the size as needed
        registrationsDialog.setLocationRelativeTo(this);  // Center the dialog on the screen
        registrationsDialog.setVisible(true);
    }


}

