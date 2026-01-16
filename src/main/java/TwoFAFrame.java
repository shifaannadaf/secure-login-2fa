import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

/**
 * Two-Factor Authentication window
 * Prompts user for TOTP code from Google Authenticator
 */
public class TwoFAFrame extends JFrame {

    // Base32 secret (scan via QR in Google Authenticator)
    private static final String SECRET_KEY = "JBSWY3DPEHPK3PXP";

    private static final long TIME_STEP = 30;
    private static final int CODE_DIGITS = 6;

    private JTextField totpField;
    private JButton verifyButton;
    private JButton cancelButton;
    private JLabel instructionLabel;
    private JLabel qrLabel;

    private final String username;

    public TwoFAFrame(String username) {
        this.username = username;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        generateAndLoadQRCode();

        setTitle("Two-Factor Authentication");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeComponents() {
        totpField = new JTextField(10);
        totpField.setFont(new Font("Monospaced", Font.BOLD, 18));
        totpField.setHorizontalAlignment(JTextField.CENTER);

        verifyButton = new JButton("Verify");
        cancelButton = new JButton("Cancel");

        instructionLabel = new JLabel(
                "<html><center>Scan the QR code using Google Authenticator<br>" +
                        "then enter the 6-digit code</center></html>",
                SwingConstants.CENTER
        );

        qrLabel = new JLabel();
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        JLabel welcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(welcome, gbc);

        gbc.gridy = 1;
        panel.add(instructionLabel, gbc);

        gbc.gridy = 2;
        panel.add(qrLabel, gbc);

        gbc.gridy = 3;
        panel.add(totpField, gbc);

        gbc.gridy = 4;
        JPanel buttons = new JPanel();
        buttons.add(verifyButton);
        buttons.add(cancelButton);
        panel.add(buttons, gbc);

        add(panel);
    }

    private void setupEventHandlers() {
        verifyButton.addActionListener(new VerifyListener());
        totpField.addActionListener(new VerifyListener());

        cancelButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
    }

    /* ===================== QR CODE ===================== */

    private void generateAndLoadQRCode() {
        try {
            String otpAuthUrl = generateOTPAuthURL(username, SECRET_KEY);
            QRCodeUtil.generateQRCode(otpAuthUrl, "qrcode.png");
            qrLabel.setIcon(new ImageIcon("qrcode.png"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to generate QR Code",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateOTPAuthURL(String user, String secret) {
        String issuer = "SecureLogin";
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, user, secret, issuer
        );
    }

    /* ===================== VERIFY ===================== */

    class VerifyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String entered = totpField.getText().trim();

            if (!entered.matches("\\d{6}")) {
                JOptionPane.showMessageDialog(
                        TwoFAFrame.this,
                        "Enter a valid 6-digit code",
                        "Invalid Code",
                        JOptionPane.ERROR_MESSAGE
                );
                totpField.setText("");
                return;
            }

            if (OTPServer.verifyOTP(SECRET_KEY, entered)) {
                JOptionPane.showMessageDialog(
                        TwoFAFrame.this,
                        "Authentication successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
                SwingUtilities.invokeLater(() ->
                        new OrdersFrame(username).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(
                        TwoFAFrame.this,
                        "Invalid authentication code",
                        "Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                totpField.setText("");
            }
        }
    }

    /* ===================== TOTP CORE ===================== */

    public static String generateTOTP(String secret, long timeSeconds) {
        try {
            Base32 base32 = new Base32();
            byte[] key = base32.decode(secret);

            long counter = timeSeconds / TIME_STEP;
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(counter);

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(buffer.array());

            int offset = hash[hash.length - 1] & 0x0F;
            int binary =
                    ((hash[offset] & 0x7F) << 24) |
                            ((hash[offset + 1] & 0xFF) << 16) |
                            ((hash[offset + 2] & 0xFF) << 8) |
                            (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, CODE_DIGITS);
            return String.format("%06d", otp);

        } catch (Exception e) {
            return "000000";
        }
    }
}


