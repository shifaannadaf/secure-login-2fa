import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.Path;
import java.nio.file.Paths;

public class QRCodeUtil {

    public static void generateQRCode(String data, String filePath) throws Exception {
        BitMatrix matrix = new MultiFormatWriter()
                .encode(data, BarcodeFormat.QR_CODE, 250, 250);

        Path path = Paths.get(filePath);
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
    }
}

