package com.booking.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class QRCodeService {

    public String generateQRCode(
            String text,
            String fileName) {

        try {

            String folder = "uploads/qrcodes/";

            java.io.File dir = new java.io.File(folder);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filePath =
                    folder + fileName + ".png";
            
            System.out.println("QR SAVED TO: " + filePath);

            QRCodeWriter writer =
                    new QRCodeWriter();

            BitMatrix matrix =
                    writer.encode(
                            text,
                            BarcodeFormat.QR_CODE,
                            300,
                            300
                    );

            Path path =
                    FileSystems.getDefault()
                            .getPath(filePath);

            MatrixToImageWriter.writeToPath(
                    matrix,
                    "PNG",
                    path
            );

            return "http://localhost:8080/qrcodes/"
                    + fileName + ".png";

        } catch (Exception e) {

            throw new RuntimeException(
                    "QR generation failed"
            );
        }
        
    }
    
}