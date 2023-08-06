package store.management.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.management.annotation.RequireAllPermissions;
import store.management.dto.CustomerOrderDto;
import store.management.dto.CustomerOrderedProductDto;
import store.management.entities.CustomerOrder;
import store.management.enums.Permission;
import store.management.services.CustomerOrderedProductsService;
import store.management.services.PdfExportService;
import store.management.util.ResponseEntityBuilder;
import org.apache.pdfbox.pdmodel.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerOrderedProductsController {
 	private final CustomerOrderedProductsService customerOrderedProductsService;
	private final PdfExportService pdfExportService;

	@RequireAllPermissions({ Permission.MANAGE_SALES })
	@GetMapping("/add-to-cart")
	public ResponseEntity<Map<String, Object>> addToCart(Principal principal, @RequestParam long productId, @RequestParam int quantity) {
		try {
			return ResponseEntityBuilder.okResponseEntity(customerOrderedProductsService.addToCart(principal.getName(), productId,
					quantity));
		} catch (Exception ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex);
		}
	}

	@RequireAllPermissions({ Permission.MANAGE_SALES })
	@GetMapping("/orders")
	public ResponseEntity<Map<String, Object>> getOrders(Principal principal, @RequestParam String type,
														 @RequestParam(defaultValue = "0") long customerOrderId) {
		try {
			return ResponseEntityBuilder.okResponseEntity(customerOrderedProductsService.getOrders(principal.getName(),
				type, customerOrderId));
		} catch (Exception ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex);
		}
	}

	@RequireAllPermissions({ Permission.MANAGE_SALES })
	@GetMapping("/remove-from-cart")
	public ResponseEntity<Map<String, Object>> removeFromCart(@RequestParam long orderedProductId, @RequestParam long customerOrderId) {
		try {
			return ResponseEntityBuilder.okResponseEntity(customerOrderedProductsService.removeFromCart(orderedProductId,
					customerOrderId));
		} catch (Exception ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex);
		}
	}

	@RequireAllPermissions({ Permission.MANAGE_SALES })
	@GetMapping("/set-status")
	public ResponseEntity<Map<String, Object>> setCustomerOrderStatus(@RequestParam long customerOrderId, @RequestParam String status,
										  @RequestParam String customerName, @RequestParam String customerPhone) {
		try {
			return ResponseEntityBuilder.okResponseEntity(customerOrderedProductsService.setCustomerOrderStatus(customerOrderId, status, customerName,
				 customerPhone));
		} catch (Exception ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex);
		}
	}

	@RequireAllPermissions({ Permission.MANAGE_RETURNS})
	@GetMapping("/return-item")
	public ResponseEntity<Map<String, Object>> returnItem(@RequestParam long orderedProductId,
														  @RequestParam long customerOrderId,
														  @RequestParam int quantity) {
		try {
			return ResponseEntityBuilder.okResponseEntity(customerOrderedProductsService.returnItem(orderedProductId, customerOrderId,
					quantity));
		} catch (Exception ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex);
		}
	}

	@GetMapping(value = "/export", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportCustomerOrderToPdf(Principal principal) {
		try {
			List<CustomerOrderDto> customerOrders = customerOrderedProductsService.getOrders(principal.getName(), "ordered", 0);
			if (CollectionUtils.isNotEmpty(customerOrders)) {
				byte[] pdfBytes = generatePDF(customerOrders);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_PDF);
				headers.setContentDispositionFormData("attachment", "customer_order.pdf");
				return ResponseEntity.ok().headers(headers).body(pdfBytes);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception ex) {
			return ResponseEntity.notFound().build();
		}
	}

	private byte[] generatePDF(List<CustomerOrderDto> customerOrders) throws IOException {
		try (PDDocument document = new PDDocument()) {
			for (CustomerOrderDto customerOrder : customerOrders) {
				PDPage page = new PDPage(PDRectangle.A4);
				document.addPage(page);

				try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
					contentStream.beginText();
					contentStream.setFont(PDType1Font.TIMES_BOLD, 16);
					contentStream.newLineAtOffset(100, 700);
					contentStream.showText("Customer Order Details");
					contentStream.endText();

					contentStream.beginText();
					contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
					contentStream.newLineAtOffset(100, 680);
					contentStream.showText("Order ID: " + customerOrder.getCustomerOrderId());
					contentStream.newLineAtOffset(0, -20);
					contentStream.showText("Status: " + customerOrder.getStatus());
					contentStream.newLineAtOffset(0, -20);
					contentStream.showText("Date: " + customerOrder.getDate());
					contentStream.newLineAtOffset(0, -20);
					contentStream.showText("Customer Name: " + customerOrder.getCustomerName());
					contentStream.newLineAtOffset(0, -20);
					contentStream.showText("Customer Phone: " + customerOrder.getCustomerPhone());
					contentStream.newLineAtOffset(0, -20);
					contentStream.showText("Sales Person ID: " + customerOrder.getSalesPersonId());
					contentStream.newLineAtOffset(0, -20);

					contentStream.showText("Customer Ordered Products:");
					contentStream.newLineAtOffset(0, -20);
					List<CustomerOrderedProductDto> products = customerOrder.getCustomerOrderedProductList();
					for (CustomerOrderedProductDto product : products) {
						contentStream.showText("Product Name: " + product.getProduct().getProductName());
						contentStream.newLineAtOffset(0, -20);
						contentStream.showText("Quantity: " + product.getQuantity());
						contentStream.newLineAtOffset(0, -20);
						contentStream.showText("Price: " + product.getPrice());
						contentStream.newLineAtOffset(0, -20);
						contentStream.showText("Order Date: " + product.getOrderDate());
						contentStream.newLineAtOffset(0, -20);
						contentStream.showText("Status: " + product.getStatus());
						contentStream.newLineAtOffset(0, -20);
						contentStream.newLineAtOffset(0, -10); // Add extra space between products
					}
					contentStream.endText();
				}
			}
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			document.save(byteArrayOutputStream);
			document.close();

			return byteArrayOutputStream.toByteArray();
		}
	}
}
