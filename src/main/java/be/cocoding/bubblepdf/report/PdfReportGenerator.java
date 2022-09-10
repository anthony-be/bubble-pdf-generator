package be.cocoding.bubblepdf.report;

import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class PdfReportGenerator {

    private static final String REPORT_LOCATION = "/home/anthony/JaspersoftWorkspace/MyReports/Bubble-PDF.jrxml";

    public static void generatePdf(PdfRequestWrapper request, OutputStream out){
        //TODO

        try {
            JasperReport jasperReport = getReport();
            Map<String, Object> parameters = new HashMap<>();
            JRDataSource datasource = new JRBeanCollectionDataSource(request.getQuestions());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);

//            JasperViewer jv = new JasperViewer(jasperPrint);
//            jv.setVisible(true);

            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }


    }

    private static JasperReport getReport() throws JRException {
        return JasperCompileManager.compileReport(REPORT_LOCATION);
    }

}
