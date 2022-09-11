package be.cocoding.bubblepdf.report;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static be.cocoding.bubblepdf.model.PdfRequestWrapper.sampleModelWithProfileImage;

class PdfReportGeneratorTest {

    @Test
    void generatePdf() throws Exception {
        File f = new File("/home/anthony/projects/GitHub/sample-pdf-bubble.pdf");
        OutputStream out = new FileOutputStream(f);
        PdfReportGenerator.generatePdf(sampleModelWithProfileImage(), out);
        out.flush();out.close();
        Thread.sleep(30000);
    }
}