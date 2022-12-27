package be.cocoding.bubblepdf.web.controller;

import be.cocoding.bubblepdf.model.Metadata;
import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import be.cocoding.bubblepdf.parser.RequestJsonParser;
import be.cocoding.bubblepdf.report.OpenPdfGenerator;
import be.cocoding.bubblepdf.storage.ReportStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfController implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    private final ReportStore store;

    @Autowired
    public PdfController(ReportStore store) {
        this.store = store;
    }

    @PostMapping
    public ResponseEntity createPdf(@RequestBody String json){
        logger.info("Handling request to generate pdf report...");
        logger.info("Payload: {}", StringUtils.truncate(json, 140));

        //Parse json
        logger.info("Parsing JSON payload...");
        PdfRequestWrapper pdfRequestWrapper = null;
        try {
            pdfRequestWrapper = RequestJsonParser.parseJson(json);
        } catch (Exception e) {
            logger.warn("Failed to parse JSON body", e);
        }
        if(pdfRequestWrapper == null || !pdfRequestWrapper.hasData()){
            return ResponseEntity.badRequest().build();
        }

        //Generate PDF
        logger.info("Generating PDF report...");
        byte[] pdfContent;
        try {
            OpenPdfGenerator pdfGenerator = new OpenPdfGenerator();
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();

            pdfGenerator.generatePdf(pdfRequestWrapper, pdfOut);
            pdfOut.close();
            pdfContent = pdfOut.toByteArray();
        } catch (IOException e) {
            logger.error("Failed to generate the PDF report", e);
            return ResponseEntity.internalServerError().build();
        }

        Metadata metadata = pdfRequestWrapper.getMetadata();
        if(metadata!=null && metadata.isCompleteForStorage()){
            return storePdfAndReturnOkResponse(metadata, pdfContent);
        }

        // Return content as response payload
        return pdfAsResponsePayload(pdfContent);
    }

    private ResponseEntity pdfAsResponsePayload(byte[] pdfContent){
        logger.info("Returning PDF as HTTP response payload");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setCacheControl(CacheControl.noStore().mustRevalidate().cachePrivate());
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    private ResponseEntity storePdfAndReturnOkResponse(Metadata metadata, byte[] pdfContent){
        store.store(metadata.getBucketId(), metadata.getPdfFileId(), pdfContent);
        logger.info("PDF report stored, returning http response");
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> helloWorld(){
        return ResponseEntity.ok("Hello PDF World !");
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(store, "Property 'store' should not be null");
    }
}
