import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Orders window displayed after successful authentication
 * Demonstrates a simple order management interface
 */
public class OrdersFrame extends JFrame {
    private String username;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JButton addOrderButton;
    private JButton deleteOrderButton;
    private JButton refreshButton;
    private JButton logoutButton;
    private JLabel statusLabel;

    public OrdersFrame(String username) {
        this.username = username;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadSampleData();

        setTitle("Orders Management System - " + username);
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Create table model with columns
        String[] columnNames = {"Order ID", "Customer", "Product", "Quantity", "Price", "Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setRowHeight(25);
        ordersTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Customer
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Product
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Quantity
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Price
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Date
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status

        // Create buttons
        addOrderButton = new JButton("Add Order");
        deleteOrderButton = new JButton("Delete Order");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");

        // Status label
        statusLabel = new JLabel("Welcome, " + username + "! Ready to manage orders.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    /**
     * Setup the layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Top panel with welcome message
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel titleLabel = new JLabel("Orders Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Logged in as: " + username);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(userLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons and status
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.add(addOrderButton);
        buttonPanel.add(deleteOrderButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Setup event handlers for buttons
     */
    private void setupEventHandlers() {
        addOrderButton.addActionListener(e -> addNewOrder());
        deleteOrderButton.addActionListener(e -> deleteSelectedOrder());
        refreshButton.addActionListener(e -> refreshOrders());
        logoutButton.addActionListener(e -> logout());
    }

    /**
     * Load sample order data
     */
    private void loadSampleData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());

        addOrderToTable("ORD001", "John Smith", "Laptop Dell XPS", "1", "$1,299.99", today, "Shipped");
        addOrderToTable("ORD002", "Sarah Johnson", "iPhone 15 Pro", "2", "$2,399.98", today, "Processing");
        addOrderToTable("ORD003", "Mike Brown", "Samsung Monitor 27\"", "3", "$899.97", today, "Delivered");
        addOrderToTable("ORD004", "Emily Davis", "Wireless Mouse", "5", "$149.95", today, "Pending");
        addOrderToTable("ORD005", "James Wilson", "Mechanical Keyboard", "1", "$179.99", today, "Shipped");

        updateStatus("Loaded " + tableModel.getRowCount() + " orders");
    }

    /**
     * Add a new order to the table
     */
    private void addOrderToTable(String orderId, String customer, String product,
                                 String quantity, String price, String date, String status) {
        Object[] row = {orderId, customer, product, quantity, price, date, status};
        tableModel.addRow(row);
    }

    /**
     * Add a new order (simplified dialog)
     */
    private void addNewOrder() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        JTextField customerField = new JTextField();
        JTextField productField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();

        panel.add(new JLabel("Customer Name:"));
        panel.add(customerField);
        panel.add(new JLabel("Product:"));
        panel.add(productField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add New Order",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String customer = customerField.getText().trim();
            String product = productField.getText().trim();
            String quantity = quantityField.getText().trim();
            String price = priceField.getText().trim();

            if (customer.isEmpty() || product.isEmpty() || quantity.isEmpty() || price.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "All fields are required!",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Generate order ID
            String orderId = "ORD" + String.format("%03d", tableModel.getRowCount() + 1);

            // Get current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(new Date());

            // Add to table
            addOrderToTable(orderId, customer, product, quantity, price, date, "Pending");
            updateStatus("Order " + orderId + " added successfully");
        }
    }

    /**
     * Delete selected order
     */
    private void deleteSelectedOrder() {
        int selectedRow = ordersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an order to delete!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String orderId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete order " + orderId + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            updateStatus("Order " + orderId + " deleted successfully");
        }
    }

    /**
     * Refresh orders table
     */
    private void refreshOrders() {
        updateStatus("Orders refreshed at " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        JOptionPane.showMessageDialog(
                this,
                "Orders have been refreshed!\nTotal orders: " + tableModel.getRowCount(),
                "Refresh Complete",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Logout and return to login screen
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    /**
     * Update status label
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}
