package com.booking.service;

import com.booking.entity.Booking;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateTicketPdf(Booking booking) {

        try {

            Document document = new Document();

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);

            document.open();

            document.add(
                    new Paragraph("MOVIE TICKET")
            );

            document.add(new Paragraph(" "));

            document.add(
                    new Paragraph(
                            "Booking ID : " + booking.getId()
                    )
            );

            document.add(
                    new Paragraph(
                            "Movie : " +
                            booking.getSeat()
                                   .getShow()
                                   .getMovie()
                                   .getTitle()
                    )
            );

            document.add(
                    new Paragraph(
                            "Theater : " +
                            booking.getSeat()
                                   .getShow()
                                   .getTheaterName()
                    )
            );

            document.add(
                    new Paragraph(
                            "Seat : " +
                            booking.getSeat()
                                   .getSeatNumber()
                    )
            );

            document.add(
                    new Paragraph(
                            "Status : " +
                            booking.getStatus()
                    )
            );

            document.close();

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "PDF generation failed"
            );
        }
    }
}