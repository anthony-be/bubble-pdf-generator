package be.cocoding.bubblepdf.report;

import be.cocoding.bubblepdf.model.Element;
import be.cocoding.bubblepdf.model.ImageElement;
import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import be.cocoding.bubblepdf.parser.RequestJsonParser;
import be.cocoding.test.utils.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import static be.cocoding.bubblepdf.model.PdfRequestWrappers.sampleModelWithLocalFileProfileImage;
import static be.cocoding.bubblepdf.model.PdfRequestWrappers.sampleModelWithProfileImage;
import static org.junit.jupiter.api.Assertions.*;

//@Disabled
class OpenPdfGeneratorTest {

    @Test
    void generatePdf() throws IOException {
        File f = FileUtils.createTempFileWithDeleteOnExist("sample-pdf-bubble-OpenPDF",".pdf");
        OutputStream out = Files.newOutputStream(f.toPath());

        OpenPdfGenerator generator = new OpenPdfGenerator();
        PdfRequestWrapper request = sampleModelWithProfileImage();
        Element q2e2 = request.getQuestions().get(1).getElements().get(1);
        assertTrue(q2e2 instanceof ImageElement);
        ImageElement imagelement = (ImageElement) q2e2;
        imagelement.setValue(imagePaysageBase64());
        generator.generatePdf(request, out);

        out.flush();out.close();
    }

    @Test
    void generatePdfWithImagesNotDownloaded() throws IOException {
        OutputStream out = NullOutputStream.NULL_OUTPUT_STREAM;

        OpenPdfGenerator generator = new OpenPdfGenerator();
        PdfRequestWrapper request = sampleModel2();
        try {
            generator.generatePdf(request, out);
            fail("An exception should be thrown");
        } catch (Exception e) {
            assertEquals("Failed to create Document report", e.getMessage());
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            assertEquals("ImageElement is not a base64 representation and has not been previously downloaded", rootCause.getMessage());
        }

    }

    @Test
    void generatePdfWithImageLocalFile() throws IOException {
        File f = FileUtils.createTempFileWithDeleteOnExist("sample-pdf-bubble-OpenPDF-URL-Image",".pdf");
        OutputStream out = Files.newOutputStream(f.toPath());

        OpenPdfGenerator generator = new OpenPdfGenerator();
        PdfRequestWrapper request = sampleModelWithLocalFileProfileImage();
        generator.generatePdf(request, out);

        out.flush();out.close();
    }

    private String imagePaysageBase64() throws IOException {
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