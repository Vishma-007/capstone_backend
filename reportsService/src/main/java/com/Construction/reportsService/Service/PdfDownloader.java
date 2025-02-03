package com.Construction.reportsService.Service;

import com.Construction.reportsService.Dto.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PdfDownloader {

    public byte[] generatePdfReport(ReportResponseDto reportData) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // ✅ Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("Construction Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // ✅ User Input Section (Complete)
            UserInputDto userInput = reportData.getUserInput();
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("User Details:", boldFont));
            document.add(new Paragraph("User Name: " + userInput.getUserName()));
            document.add(new Paragraph("Email: " + userInput.getUserEmail()));
            document.add(new Paragraph("Phone: " + userInput.getPhoneNumber()));
            document.add(new Paragraph("City: " + userInput.getCity()));
            document.add(new Paragraph("State: " + userInput.getState()));

            document.add(new Paragraph("\nProject Details:", boldFont));
            document.add(new Paragraph("Property Name: " + userInput.getPropName()));
            document.add(new Paragraph("Construction Type: " + userInput.getConstructionType()));
            document.add(new Paragraph("Total Floors: " + userInput.getTotalFloor()));
            document.add(new Paragraph("Built-up Area: " + userInput.getBuiltupArea() + " sq.ft"));
            document.add(new Paragraph("Material Quality: " + userInput.getMaterialQuality()));
            document.add(new Paragraph("Land Clearance Required: " + (userInput.getLandclearence() ? "Yes" : "No")));
            document.add(new Paragraph("\n"));

            // ✅ Total Cost Section
            Font costFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Paragraph totalCost = new Paragraph("Total Construction Cost: ₹" + reportData.getTotalCost(), costFont);
            totalCost.setAlignment(Element.ALIGN_CENTER);
            document.add(totalCost);
            document.add(new Paragraph("\n"));

            // ✅ Material Cost Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 2, 2});

            addTableHeader(table);
            for (MaterialCostDto material : reportData.getMaterialCosts()) {
                addTableRow(table, material);
            }
            document.add(table);

            // ✅ Watermark before closing
            addWatermark(writer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        BaseColor headerColor = new BaseColor(100, 100, 100); // Gray

        PdfPCell cell;

        cell = new PdfPCell(new Phrase("Material", headerFont));
        cell.setBackgroundColor(headerColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Quantity", headerFont));
        cell.setBackgroundColor(headerColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Quality", headerFont));
        cell.setBackgroundColor(headerColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Amount", headerFont));
        cell.setBackgroundColor(headerColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, MaterialCostDto material) {
        table.addCell(material.getResourceName());
        table.addCell(material.getResourceQuantity());
        table.addCell(material.getMaterialQuality());
        table.addCell("₹" + material.getAmount());
    }

    private void addWatermark(PdfWriter writer) {
        PdfContentByte canvas = writer.getDirectContentUnder();
        Font watermarkFont = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.NORMAL, new GrayColor(0.80f));
        Phrase watermark = new Phrase("Approved by your construction engineer", watermarkFont);

        Rectangle pageSize = writer.getPageSize();
        float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
        float y = (pageSize.getTop() + pageSize.getBottom()) / 5;

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, x, y, 0);
    }
}
