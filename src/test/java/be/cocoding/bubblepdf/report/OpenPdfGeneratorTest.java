package be.cocoding.bubblepdf.report;

import be.cocoding.bubblepdf.model.Element;
import be.cocoding.bubblepdf.model.ImageElement;
import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import be.cocoding.bubblepdf.parser.RequestJsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import static be.cocoding.bubblepdf.model.PdfRequestWrapper.sampleModelWithOnlineProfileImage;
import static be.cocoding.bubblepdf.model.PdfRequestWrapper.sampleModelWithProfileImage;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class OpenPdfGeneratorTest {

    @Test
    void generatePdf() throws IOException {
        File f = new File("/home/anthony/projects/GitHub/sample-pdf-bubble-OpenPDF.pdf");
        OutputStream out = Files.newOutputStream(f.toPath());

        OpenPdfGenerator generator = new OpenPdfGenerator();
        PdfRequestWrapper request = sampleModelWithProfileImage();
        Element q2e2 = request.getQuestions().get(1).getElements().get(1);
        assertTrue(q2e2 instanceof ImageElement);
        ImageElement imagelement = (ImageElement) q2e2;
        imagelement.setValue(imagePaysage());
        generator.generatePdf(request, out);

        out.flush();out.close();
    }

    @Test
    void generatePdf2() throws IOException {
        File f = new File("/home/anthony/projects/GitHub/sample-pdf-bubble-OpenPDF-2.pdf");
        OutputStream out = Files.newOutputStream(f.toPath());

        OpenPdfGenerator generator = new OpenPdfGenerator();
        PdfRequestWrapper request = sampleModel2();
        generator.generatePdf(request, out);

        out.flush();out.close();
    }

    @Test
    void generatePdfWithImageUrl() throws IOException {
        File f = new File("/home/anthony/projects/GitHub/sample-pdf-bubble-OpenPDF-URL-Image.pdf");
        OutputStream out = Files.newOutputStream(f.toPath());

        OpenPdfGenerator generator = new OpenPdfGenerator();
        PdfRequestWrapper request = sampleModelWithOnlineProfileImage();
        generator.generatePdf(request, out);

        out.flush();out.close();
    }

    private String imagePaysage() throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("classpath:/images/pngtree-summer-landscape-png-image_3911110.jpg");
        byte[] bytes = IOUtils.toByteArray(resource.getInputStream());
        return Base64.getEncoder().encodeToString(bytes);
    }

    private PdfRequestWrapper sampleModel2() throws IOException {
            Resource resource = new DefaultResourceLoader().getResource("classpath:/sample-request-with-images.json");
            String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            return RequestJsonParser.parseJson(json);
    }
}