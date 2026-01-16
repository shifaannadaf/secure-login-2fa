import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main login window for username/password authentication
 * This is the first step in the 2FA login process
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel mainPanel;

    public LoginFrame() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("Secure Login with 2FA");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
    }

    /**
     * Setup the layout using GridBagLayout for better control
     */
    private void setupLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginButton, gbc);

        add(mainPanel);
    }

    /**
     * Setup event handlers for login button and Enter key
     */
    private void setupEventHandlers() {
        LoginListener listener = new LoginListener();
        loginButton.addActionListener(listener);

        // Allow Enter key to submit from password field
        passwordField.addActionListener(listener);
    }

    /**
     * Action listener for login button
     */
    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "Please enter both username and password!",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Authenticate user
            if (authenticateUser(username, password)) {
                // Clear password field for security
                passwordField.setText("");

                // Proceed to 2FA step
                TwoFAFrame twoFAFrame = new TwoFAFrame(username);
                twoFAFrame.setVisible(true);

                // Hide login frame (don't dispose in case 2FA fails)
                setVisible(false);
            } else {
                // Clear password on failed attempt
                passwordField.setText("");
                passwordField.requestFocus();

                JOptionPane.showMessageDialog(
                        mainPanel,
                        "Invalid username or password!",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Simulates Apache basic authentication
     * In production, this would check against a database or LDAP
     *
     * @param username The username to authenticate
     * @param password The password to verify
     * @return true if credentials are valid, false otherwise
     */
    private boolean authenticateUser(String username, String password) {
        // Hardcoded credentials for demonstration
        // In production: query database, check Apache htpasswd, or use LDAP
        return "admin".equals(username) && "password123".equals(password);
    }

    /**
     * Main entry point for the application
     */
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for native appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);

            // Display usage information
            System.out.println("=================================");
            System.out.println("Secure Login with 2FA - Started");
            System.out.println("=================================");
            System.out.println("Default credentials:");
            System.out.println("  Username: admin");
            System.out.println("  Password: password123");
            System.out.println("  2FA Secret: JBSWY3DPEHPK3PXP");
            System.out.println("=================================");
        });
    }
}
