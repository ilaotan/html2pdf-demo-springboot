package com.example.html2pdfdemospringboot;


import com.example.html2pdfdemospringboot.model.PdfDataTest;
import com.example.html2pdfdemospringboot.util.PdfUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("pdf")
public class PdfController {


    @GetMapping("one")
    public String onePdf(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("onePdf");
        String html = PdfUtil.getHtml("test.html", getStringObjectMap());

        try {
            byte[] bytes = PdfUtil.html2pdf(html);
            response.setContentType("application/pdf");
            response.setHeader("Content-Length", String.valueOf(bytes.length));
            response.setHeader("Connection", "keep-alive");
            response.setHeader("Accept-Ranges", "none");
            response.setHeader("X-Frame-Options", "DENY");
            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @GetMapping("zip")
    public String manyPdfByZip(HttpServletRequest request, HttpServletResponse response) {

        // 模拟多份数据
        List<String> htmlList = new ArrayList<>();
        htmlList.add(PdfUtil.getHtml("test.html", getStringObjectMap()));
        htmlList.add(PdfUtil.getHtml("test.html", getStringObjectMap()));
        htmlList.add(PdfUtil.getHtml("test.html", getStringObjectMap()));


        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String("文件名字年月日等.zip".getBytes("GB2312"), "ISO-8859-1"));
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
            int i = 1;
            for(String html: htmlList) {
                ZipEntry entry = new ZipEntry("每个文件的名字_" + i + ".pdf");
                zip.putNextEntry(entry);
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, zip);
                writer.setCloseStream(false);
                document.open();
//                document.add(new Paragraph("Hello " + i));

                XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(){
                    @Override
                    public Font getFont(String fontname, String encoding, float size, int style) {
                        return super.getFont(fontname == null ? "宋体" : fontname, encoding, size, style);
                    }
                };
                // XML Worker
                XMLWorkerHelper worker =  XMLWorkerHelper.getInstance();
                //html = html.replace('\"','\'');
                worker.parseXHtml(writer, document, new ByteArrayInputStream(html.getBytes("UTF-8")),new ByteArrayInputStream(html.getBytes()), Charset.forName("UTF-8"),fontProvider);

                document.close();
                zip.closeEntry();
                i++;
            }
            zip.close();
        } catch (Exception e) {

        }

        return "";
    }


    private static Map<String, Object> getStringObjectMap() {
        // 模板中的数据，实际运用从数据库中查询
        Map<String, Object> data = new HashMap<>();
        data.put("curr", 1);
        data.put("one", 2);
        data.put("two", 1);
        data.put("three", 6);
        List<PdfDataTest> detailList = new ArrayList<>();
        detailList.add(new PdfDataTest(123456, "测试", "测试", "测试", "测试"));
        detailList.add(new PdfDataTest(111111, "测试", "测试", "测试", "测试"));
        detailList.add(new PdfDataTest(222222, "测试", "测试", "测试", "测试"));
        data.put("detailList", detailList);
        return data;
    }

}
