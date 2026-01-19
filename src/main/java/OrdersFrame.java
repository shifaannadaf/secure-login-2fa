import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class OrdersFrame extends JFrame {

    private String username;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JButton addOrderButton, deleteOrderButton, refreshButton, logoutButton;
    private JButton searchByIdButton, searchByDateButton;

    private JLabel statusLabel;

    private static final boolean ENABLE_S3 = false;

    // MySQL configuration
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/orders_db";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "";

    // AWS S3
    private static final String BUCKET = "your-s3-bucket-name";
    private S3Client s3Client;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrdersFrame(String username) {
        this.username = username;

        System.out.println("OrdersFrame constructor called for user: " + username);

        if (ENABLE_S3) {
            s3Client = S3Client.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadOrdersFromDatabase();

        setTitle("Orders Management System - " + username);
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        String[] columnNames = {"Order ID", "Customer", "Items", "Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(25);

        addOrderButton = new JButton("Add Order");
        deleteOrderButton = new JButton("Delete Order");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");
        searchByIdButton = new JButton("Search by Order ID");
        searchByDateButton = new JButton("Search by Date");

        statusLabel = new JLabel("Welcome, " + username + "!");
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        add(statusLabel, BorderLayout.NORTH);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(addOrderButton);
        bottom.add(deleteOrderButton);
        bottom.add(refreshButton);
        bottom.add(searchByIdButton);
        bottom.add(searchByDateButton);
        bottom.add(logoutButton);


        add(bottom, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        addOrderButton.addActionListener(e -> addNewOrder());
        deleteOrderButton.addActionListener(e -> deleteSelectedOrder());
        refreshButton.addActionListener(e -> loadOrdersFromDatabase());
        logoutButton.addActionListener(e -> logout());
        searchByIdButton.addActionListener(e -> searchByOrderId());
        searchByDateButton.addActionListener(e -> searchByDate());

    }

    /* ---------------- DATABASE ---------------- */

    private void loadOrdersFromDatabase() {
        System.out.println("Loading orders from database...");
        tableModel.setRowCount(0);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(
                    MYSQL_URL, MYSQL_USER, MYSQL_PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM orders")) {

                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getString("id"),
                            rs.getString("customer"),
                            rs.getString("items"),
                            rs.getTimestamp("date").toLocalDateTime(),
                            rs.getString("status")
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void addNewOrder() {
        JTextField customerField = new JTextField();
        JTextField itemsField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Customer:"));
        panel.add(customerField);
        panel.add(new JLabel("Items JSON:"));
        panel.add(itemsField);

        if (JOptionPane.showConfirmDialog(this, panel, "Add Order",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String orderId = "ORD-" + System.currentTimeMillis();
        LocalDateTime orderDate = LocalDateTime.now();

        try (Connection conn = DriverManager.getConnection(
                MYSQL_URL, MYSQL_USER, MYSQL_PASS);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO orders (id, date, customer, items, status) VALUES (?, ?, ?, ?, ?)")) {

            ps.setString(1, orderId);
            ps.setTimestamp(2, Timestamp.valueOf(orderDate));
            ps.setString(3, customerField.getText());
            ps.setString(4, itemsField.getText());
            ps.setString(5, "PENDING");
            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }

        if (ENABLE_S3) {
            try {
                Order order = new Order(orderId, orderDate,
                        customerField.getText(), itemsField.getText());

                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(BUCKET)
                                .key(orderId + ".json")
                                .build(),
                        RequestBody.fromString(mapper.writeValueAsString(order))
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        loadOrdersFromDatabase();
    }

    private void deleteSelectedOrder() {
        int row = ordersTable.getSelectedRow();
        if (row == -1) return;

        String orderId = tableModel.getValueAt(row, 0).toString();

        try (Connection conn = DriverManager.getConnection(
                MYSQL_URL, MYSQL_USER, MYSQL_PASS);
             PreparedStatement ps =
                     conn.prepareStatement("DELETE FROM orders WHERE id=?")) {

            ps.setString(1, orderId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

        loadOrdersFromDatabase();
    }

    private void searchByOrderId() {
        String orderId = JOptionPane.showInputDialog(this, "Enter Order ID:");
        if (orderId == null || orderId.trim().isEmpty()) return;

        tableModel.setRowCount(0);

        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(
                MYSQL_URL, MYSQL_USER, MYSQL_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, orderId.trim());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("customer"),
                        rs.getString("items"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("status")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    private void searchByDate() {
        String date = JOptionPane.showInputDialog(this, "Enter Date (YYYY-MM-DD):");
        if (date == null || date.trim().isEmpty()) return;

        tableModel.setRowCount(0);

        String sql = "SELECT * FROM orders WHERE DATE(date) = ?";

        try (Connection conn = DriverManager.getConnection(
                MYSQL_URL, MYSQL_USER, MYSQL_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, date.trim());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("customer"),
                        rs.getString("items"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("status")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }



    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
