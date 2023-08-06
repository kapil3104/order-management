package store.management.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import store.management.dto.CustomerOrderDto;
import store.management.entities.CustomerOrder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] exportCustomerOrderToPdf(List<CustomerOrderDto> customerOrders) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            for(CustomerOrderDto customerOrder : customerOrders) {
                document.add(new Paragraph("Customer Order Details"));
                document.add(new Paragraph("Customer Order ID: " + customerOrder.getCustomerOrderId()));
                document.add(new Paragraph("Customer Name: " + customerOrder.getCustomerName()));
                document.add(new Paragraph("Customer Phone: " + customerOrder.getCustomerPhone()));
                document.add(new Paragraph("Date: " + customerOrder.getDate()));
                //            document.add(new Paragraph("User: " + customerOrder.getUser().getName()));
                document.add(
                        new Paragraph("Customer Ordered Product: " + customerOrder.getCustomerOrderedProductList()));
            }
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}

