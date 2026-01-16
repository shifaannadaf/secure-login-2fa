import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

public class OTPServer {

    private static final long TIME_STEP = 30;

    /**
     * Verifies a TOTP code allowing ±1 time-step window
     */
    public static boolean verifyOTP(String secret, String code) {
        long currentTime = System.currentTimeMillis() / 1000;

        for (int i = -1; i <= 1; i++) {
            long time = currentTime + (i * TIME_STEP);
            String expected = generateTOTPForTime(secret, time);
            if (expected.equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * RFC 6238 compliant TOTP generation
     */
    private static String generateTOTPForTime(String secret, long timeSeconds) {
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

            int otp = binary % 1_000_000;
            return String.format("%06d", otp);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TOTP", e);
        }
    }
}
