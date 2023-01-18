package be.cocoding.bubblepdf.web.controller;

import be.cocoding.bubblepdf.download.ImagesDownloaderImpl;
import be.cocoding.bubblepdf.model.Element;
import be.cocoding.bubblepdf.model.ImageElement;
import be.cocoding.bubblepdf.model.Metadata;
import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import be.cocoding.bubblepdf.parser.RequestJsonParser;
import be.cocoding.bubblepdf.report.OpenPdfGenerator;
import be.cocoding.bubblepdf.storage.ReportStore;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pdf")
public class PdfController implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    private final ReportStore store;

    @Autowired
    public PdfController(ReportStore store) {
        this.store = store;
    }

//    @PostMapping(path = "/download-images/{strategy}")
//    public ResponseEntity downloadImages(@RequestBody String json, @PathVariable("strategy") String strategy){
//        logger.info("Handling request to download report's images with strategy {} ...", strategy);
//
//        //Parse json
//        logger.info("Parsing JSON payload...");
//        PdfRequestWrapper pdfRequestWrapper = getPdfRequestWrapper(json);
//        if(pdfRequestWrapper == null || !pdfRequestWrapper.hasData()){
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Extract uri from request
//        List<String> uriList = pdfRequestWrapper.getQuestions().stream()
//                .filter(Objects::nonNull)
//                .flatMap(question -> question.getElements().stream())
//                .filter(ImageElement.class::isInstance)
//                .map(Element::getValue)
//                .collect(Collectors.toList());
//
//
//        // Download images
//        ImagesDownloader imgDownloader = new ImagesDownloaderImpl(strategy);
//        Map<String, Path> downloadedImagesMap = imgDownloader.download(uriList);
//
//        return ResponseEntity.ok(downloadedImagesMap);
//    }

    @PostMapping
    public ResponseEntity createPdf(@RequestBody String json){
        logger.info("Handling request to generate pdf report...");

        //Parse json
        logger.info("Parsing JSON payload...");
        PdfRequestWrapper pdfRequestWrapper = getPdfRequestWrapper(json);
        if(pdfRequestWrapper == null || !pdfRequestWrapper.hasData()){
            return ResponseEntity.badRequest().build();
        }

        // Download images locally
        logger.info("Downloading images from external to local system...");
        downloadImagesAndFillRequestWrapper(pdfRequestWrapper);

        //Generate PDF
        logger.info("Generating PDF report...");
        File pdfFile;
        try {
            pdfFile = Files.createTempFile("GeneratedPdf", ".pdf").toFile();
            pdfFile.deleteOnExit();
            try(FileOutputStream pdfOut = new FileOutputStream(pdfFile)){
                OpenPdfGenerator pdfGenerator = new OpenPdfGenerator();
                pdfGenerator.generatePdf(pdfRequestWrapper, pdfOut);
            }
        } catch (IOException e) {
            logger.error("Failed to generate the PDF report", e);
            return ResponseEntity.internalServerError().build();
        }

        Metadata metadata = pdfRequestWrapper.getMetadata();
        if(metadata!=null && metadata.isCompleteForStorage()){
            return storePdfAndReturnOkResponse(metadata, pdfFile);
        }

        // Return content as response payload
        return pdfAsResponsePayload(pdfFile);
    }

    private void downloadImagesAndFillRequestWrapper(PdfRequestWrapper pdfRequestWrapper) {
        ImagesDownloaderImpl imageDownloader = new ImagesDownloaderImpl();

        // Distribute image uris into a map and keep track of related ImageElement
        Map<String, List<Element>> imageElementsByUriMap =
        pdfRequestWrapper.getQuestions().stream()
                .filter(Objects::nonNull)
                .flatMap(question -> question.getElements().stream())
                .filter(Objects::nonNull)
                .filter(ImageElement.class::isInstance)
                .filter(imgElement -> { return !((ImageElement) imgElement).isBase64Value(); /* Skip Base64 image value */ })
                .collect(Collectors.groupingBy(Element::getValue));

        // Download each uri to local file
        Map<String, Path> downloadedImages = imageDownloader.download(imageElementsByUriMap.keySet());
        logger.info("{} images have been downloaded", downloadedImages.size());

        // Reference back downloaded images to ImageElement
        downloadedImages.entrySet().stream()
                .flatMap(entry -> {
                    String imageUri = entry.getKey();
                    List<Element> elements = imageElementsByUriMap.getOrDefault(imageUri, Collections.emptyList());
                    // Convert Entry<Uri, PAth> to Triple <Uri, PAth, Element>
                    return elements
                            .stream().map(element -> Triple.of(imageUri, entry.getValue(), element));
                })
                .forEach(triple -> {
                    // Attach Path to ImageElement
                    Element element = triple.getRight();
                    Path localFilePath = triple.getMiddle();
                    if(element instanceof ImageElement){
                        ImageElement imgElement = (ImageElement) element;
                        imgElement.setMirrorLocalFile(localFilePath.toFile());
                    }else{
                        throw new IllegalStateException("Element is not an ImageElement ! How does it come here ?");
                    }
                });
    }

    private static PdfRequestWrapper getPdfRequestWrapper(String json) {
        PdfRequestWrapper pdfRequestWrapper = null;
        try {
            pdfRequestWrapper = RequestJsonParser.parseJson(json);
        } catch (Exception e) {
            logger.warn("Failed to parse JSON body", e);
        }
        return pdfRequestWrapper;
    }


    private ResponseEntity pdfAsResponsePayload(File pdfFile){
        logger.info("Returning PDF as HTTP response payload");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setCacheControl(CacheControl.noStore().mustRevalidate().cachePrivate());
        Resource resource = new FileSystemResource(pdfFile);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    private ResponseEntity storePdfAndReturnOkResponse(Metadata metadata, File pdfFile){
        store.store(metadata.getBucketId(), metadata.getPdfFileId(), pdfFile);
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
