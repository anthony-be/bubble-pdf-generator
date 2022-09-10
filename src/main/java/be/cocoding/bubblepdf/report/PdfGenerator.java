package be.cocoding.bubblepdf.report;

import be.cocoding.bubblepdf.model.PdfRequestWrapper;

import java.io.OutputStream;

public interface PdfGenerator {

    void generatePdf(PdfRequestWrapper request, OutputStream out);
}
