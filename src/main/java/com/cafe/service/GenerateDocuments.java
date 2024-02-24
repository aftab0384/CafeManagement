package com.cafe.service;

import com.cafe.utils.CafeCommon;
import com.cafe.utils.EmailUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;


@Service
public class GenerateDocuments {

    public static ResponseEntity<?> generateBill(Map<String, Object> objectMap){
            try {
                String fileName;
                if (!objectMap.containsKey("name") || !objectMap.containsKey("contactDetails") || !objectMap.containsKey("email") || !objectMap.containsKey("paymentMethod") || !objectMap.containsKey("productDetails") || !objectMap.containsKey("totalPrice") || !objectMap.containsKey("uuid")) {
                     fileName = CafeCommon.generateUuid(String.valueOf(objectMap.containsKey("name")));
                }else{
                     fileName = CafeCommon.getUUID();
                    objectMap.put("uuid",fileName);
                }
                String data = "Name: " + objectMap.get("name")+"\n" +"Contact Number: "+ objectMap.get("contactDetails") + "\n" +"Email: "+objectMap.get("email")+"\n"+"Payment Method: "+ objectMap.get("paymentMethod");
                    Document document = new Document();
                PdfWriter.getInstance(document,new FileOutputStream(CafeCommon.STORAGE_LOCATION+"\\"+fileName+".pdf"));
                document.open();
                setRectangleInPdf(document);
                Paragraph  pp = new Paragraph("Cafe Management",getFont("Header"));
                pp.setAlignment(Element.ALIGN_CENTER);
                document.add(pp);
                Paragraph paragraph = new Paragraph(data+"\n \n"+getFont("data"));
                document.add(paragraph);
                PdfPTable table = new PdfPTable(5);
                            table.setWidthPercentage(100);
                            addTableHeader(table);

                JSONArray jsonArray = CafeCommon.getJsonArrayFromString((String)objectMap.get("productDetails"));
                            for(int i=0; i<jsonArray.length(); i++){
                                addRow(table,CafeCommon.getMapFromJson(jsonArray.getString(i)));
                            }
                            document.add(table);
                            Paragraph footer =  new Paragraph("Total: " + objectMap.get("totalPrice")+"\n"+"Thank you for visiting! please visit again",getFont("data"));
                            document.add(footer);
                            document.close();
                            return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK);
            }catch (Exception e){
                e.fillInStackTrace();
            }

        return ResponseEntity.badRequest().body(CafeCommon.SOMETHING_WENT_WRONG);
    }

    private static void addRow(PdfPTable table, Map<String, Object> data) {
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell(Double.toString((Double) data.get("quantity")));
        table.addCell(Double.toString((Double) data.get("total")));

    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of("Name","Category","Quantity","Price","Sub Total").forEach(columnTitle->{
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            header.setBackgroundColor(BaseColor.YELLOW);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);


        });
    }

    private static void setRectangleInPdf(Document document) throws DocumentException {
        System.out.println("inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577,825,18,15);
                rectangle.enableBorderSide(1);
                rectangle.enableBorderSide(2);
                rectangle.enableBorderSide(4);
                rectangle.enableBorderSide(8);
                rectangle.setBackgroundColor(BaseColor.BLUE);
                rectangle.setBorderWidth(1);
                document.add(rectangle);

    }
    private static Font getFont(String type){

        switch (type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE);
                     headerFont.setStyle(Font.BOLD);
                     return headerFont;
            case "data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                     dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();

        }
    }


}
