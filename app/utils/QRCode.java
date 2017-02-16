package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import play.Play;
import models.User;

// http://crunchify.com/java-simple-qr-code-generator-example/
public class QRCode {
    private static String rootPath = Play.application().path().getPath();

    public static String generateImage(String value, User user) {
        try {
            String qr_subdir = "public";
            if (user != null)
                qr_subdir = user.id.toString();

            // ensure the user QR directory exists
            File destination_dir = new File(rootPath + "/data/qr_codes/" + qr_subdir);
            if (!destination_dir.exists())
                destination_dir.mkdir();

            int size = 150;
            String fileType = "png";
            String filename = MD5Util.getMD5Checksum(value) + "." + fileType;
            File qrFile = new File(destination_dir, filename);

            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, size, size, hintMap);
            int bm_width = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(bm_width, bm_width, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            // create the black outer border for our AR marker
            // 10 % of the total size is used as the with of the outer marker container
            int borderWith = (int)(size * 0.10);
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, bm_width, bm_width);
            graphics.setColor(Color.WHITE);
            graphics.fillRect(borderWith, borderWith, bm_width-borderWith*2, bm_width-borderWith*2);

            // create the QR marker by painting the black pixels that are needed
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < bm_width; i++) {
                for (int j = 0; j < bm_width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            ImageIO.write(image, fileType, qrFile);
            String fullPath =  qrFile.getAbsolutePath();
            System.out.println("\nYou have successfully created QR Code.\nFull Path: " + fullPath);
            return fullPath;

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
