package be.cocoding.bubblepdf.web.controller;

import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import be.cocoding.bubblepdf.parser.RequestJsonParser;
import be.cocoding.bubblepdf.report.OpenPdfGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @PostMapping
    public ResponseEntity<byte[]> createPdf(@RequestBody String json){
        //Parse json
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
        byte[] pdfContent;
        try {
            OpenPdfGenerator pdfGenerator = new OpenPdfGenerator();
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
            pdfGenerator.generatePdf(pdfRequestWrapper, pdfOut);
            pdfOut.close();
            pdfContent = pdfOut.toByteArray();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }


        // Return content as byte[]
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setCacheControl(CacheControl.noStore().mustRevalidate().cachePrivate());
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
    @GetMapping(produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> helloWorld(){
        return ResponseEntity.ok("Hello PDF World !");
    }

}
