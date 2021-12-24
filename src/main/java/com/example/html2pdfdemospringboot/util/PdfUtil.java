package com.example.html2pdfdemospringboot.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

public class PdfUtil {

    public static String getHtml(String templateFileName, Map<String, Object> data) {

        // 创建一个FreeMarker实例, 负责管理FreeMarker模板的Configuration实例
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        // 指定FreeMarker模板文件的位置
        configuration.setClassForTemplateLoading(PdfUtil.class, "/templates");
        StringWriter writer = new StringWriter();
        // 设置模板的编码格式
        configuration.setEncoding(Locale.CHINA, "UTF-8");
        try{
            // 获取模板文件
            Template template = configuration.getTemplate(templateFileName, "UTF-8");
            // 将数据输出到html中
            template.process(data, writer);
            writer.flush();

            return writer.toString();
        }catch (Exception e) {
            e.printStackTrace();
           return null;
        }
    }

    /**
     * 把html转换成pdf，以字节数组的形式返回pdf文件
     * @param html
     * @return pdf字节数组
     * @throws IOException
     * @throws DocumentException
     * @throws CssResolverException
     */
    public static byte[] html2pdf(String html) throws IOException, DocumentException, CssResolverException {
        Document document = new Document(PageSize.A4);
        //document.setPageCount(40);
        //document.setPageSize(PageSize.A4);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document,os);
        document.open();

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
        return os.toByteArray();
    }

}
