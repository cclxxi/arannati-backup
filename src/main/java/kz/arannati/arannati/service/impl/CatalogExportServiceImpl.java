package kz.arannati.arannati.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.CatalogExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogExportServiceImpl implements CatalogExportService {

    @Override
    public ByteArrayOutputStream exportToPdf(List<ProductDTO> products, String title, UserDTO user) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Fonts
            BaseFont baseFont = BaseFont.createFont("fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font normalFont = new Font(baseFont, 10);

            // Title
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.setSpacingAfter(20);
            document.add(titleParagraph);

            // User info
            Paragraph userInfo = new Paragraph(
                    "Косметолог: " + user.getFirstName() + " " + user.getLastName() + "\n" +
                            "Email: " + user.getEmail() + "\n" +
                            "Дата экспорта: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    normalFont
            );
            userInfo.setSpacingAfter(20);
            document.add(userInfo);

            // Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 2, 2, 1, 2});

            // Headers
            String[] headers = {"Наименование", "Бренд", "Категория", "Цена (₸)", "Кол-во", "Артикул"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Data rows
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("kk", "KZ"));
            for (ProductDTO product : products) {
                table.addCell(new PdfPCell(new Phrase(product.getName(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(product.getBrandName() != null ? product.getBrandName() : "", normalFont)));
                table.addCell(new PdfPCell(new Phrase(product.getCategoryName() != null ? product.getCategoryName() : "", normalFont)));

                BigDecimal price = product.getEffectivePrice() != null ? product.getEffectivePrice() : product.getRegularPrice();
                table.addCell(new PdfPCell(new Phrase(formatCurrency(price), normalFont)));

                table.addCell(new PdfPCell(new Phrase(product.getStockQuantity().toString(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(product.getSku() != null ? product.getSku() : "", normalFont)));
            }

            document.add(table);

            // Footer
            Paragraph footer = new Paragraph(
                    "\nВсего товаров: " + products.size() + "\n" +
                            "Цены указаны с учетом специальной скидки для косметологов",
                    normalFont
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            log.error("Error creating PDF", e);
            throw new RuntimeException("Ошибка создания PDF", e);
        }

        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportToExcel(List<ProductDTO> products, String title) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Каталог");

            // Create styles
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);

            // Info row
            Row infoRow = sheet.createRow(2);
            Cell infoCell = infoRow.createCell(0);
            infoCell.setCellValue("Дата экспорта: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

            // Header row
            Row headerRow = sheet.createRow(4);
            String[] headers = {"Наименование", "Бренд", "Категория", "Артикул", "Обычная цена (₸)",
                    "Цена со скидкой (₸)", "Количество на складе", "Описание"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 5;
            for (ProductDTO product : products) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(product.getName());
                row.createCell(1).setCellValue(product.getBrandName() != null ? product.getBrandName() : "");
                row.createCell(2).setCellValue(product.getCategoryName() != null ? product.getCategoryName() : "");
                row.createCell(3).setCellValue(product.getSku() != null ? product.getSku() : "");

                Cell regularPriceCell = row.createCell(4);
                if (product.getRegularPrice() != null) {
                    regularPriceCell.setCellValue(product.getRegularPrice().doubleValue());
                    regularPriceCell.setCellStyle(currencyStyle);
                }

                Cell effectivePriceCell = row.createCell(5);
                BigDecimal effectivePrice = product.getEffectivePrice() != null ? product.getEffectivePrice() : product.getRegularPrice();
                if (effectivePrice != null) {
                    effectivePriceCell.setCellValue(effectivePrice.doubleValue());
                    effectivePriceCell.setCellStyle(currencyStyle);
                }

                row.createCell(6).setCellValue(product.getStockQuantity() != null ? product.getStockQuantity() : 0);
                row.createCell(7).setCellValue(product.getShortDescription() != null ? product.getShortDescription() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

        } catch (IOException e) {
            log.error("Error creating Excel file", e);
            throw new RuntimeException("Ошибка создания Excel файла", e);
        }

        return outputStream;
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₸";
        return String.format("%,.0f ₸", amount);
    }
}
