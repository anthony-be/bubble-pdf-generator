package be.cocoding.bubblepdf.report;

import be.cocoding.bubblepdf.model.*;
import com.lowagie.text.Element;
import com.lowagie.text.Font;

import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * Use OpenPDF library as main pdf generator, to allow dynamic content positioning.
 */
public class OpenPdfGenerator implements PdfGenerator {

    private Font questionTitleFont;
    private Font textFont;
    private Font imageLegendFont;

    public OpenPdfGenerator() {
        init();
    }

    private void init() {
        questionTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
        textFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        imageLegendFont = FontFactory.getFont(FontFactory.HELVETICA, 6, Font.NORMAL, Color.lightGray);
    }

    @Override
    public void generatePdf(PdfRequestWrapper request, OutputStream out) {
        Document doc = new Document();
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(doc, out);
            pdfWriter.setStrictImageSequence(true); // ??
            addWatermarkIfNeeded(pdfWriter, request);
            doc.open();

            if (!CollectionUtils.isEmpty(request.getQuestions())) {
                int counter = 1;
                for (Question q : request.getQuestions()) {
                    printQuestionElement(doc, q, counter++);
                }
            }
        } catch (Exception e) {
            handleException(e);
        } finally {
            doc.close();
        }
    }

    private void addWatermarkIfNeeded(PdfWriter pdfWriter, PdfRequestWrapper request){
        Optional.of(request)
                .map(PdfRequestWrapper::getMetadata)
                .map(Metadata::getWatermark)
                .ifPresent(watermark -> pdfWriter.setPageEvent(new WatermarkPageEvent(watermark)));
    }

    private void handleException(Exception e) {
        throw new RuntimeException("Failed to create Document report", e);
    }

    private void printQuestionElement(Document doc, Question q, int questionNbr) throws DocumentException, IOException {
        // Title
        Phrase titlePhrase = new Phrase(q.getTitle(), questionTitleFont);
        Paragraph titleParagraph = new Paragraph();
        titleParagraph.add(titlePhrase);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingAfter(15f);
        Chapter chapter = new Chapter(titleParagraph, questionNbr);
        chapter.setNumberDepth(0);
        doc.add(chapter);

        // Elements
        for (be.cocoding.bubblepdf.model.Element element : q.getElements()) {
            if (element instanceof TextElement) {
                printTextElement(doc, (TextElement) element);
            } else if (element instanceof ImageElement) {
                printImageElement(doc, (ImageElement) element);
            }
        }

        doc.newPage();
    }

    private void printImageElement(Document doc, ImageElement imageElement) throws DocumentException, IOException {
        Image image = Image.getInstance(imageElement.getImageBytes());

        image.setAlignment(Image.MIDDLE);
        fitImageIfNecessary(image);
        doc.add(image);

//        Phrase imageLegendPhrase = new Phrase(MessageFormat.format("Image size - Original: {0} x {2} - Corrected: {1} x {3}",
//                image.getWidth(), image.getPlainWidth(), image.getHeight(), image.getPlainHeight()), imageLegendFont);
//        Paragraph p = new Paragraph(imageLegendPhrase);
//        p.setAlignment(Element.ALIGN_CENTER);
//        doc.add(p);

    }

    private void fitImageIfNecessary(Image image) {
        float maxWidth = 540;
        float maxHeigth = 802;

        float currentWidth = image.getWidth();
        float currentHeight = image.getHeight();

        float correctionWidth = maxWidth / currentWidth;
        float correctionHeight = maxHeigth / currentHeight;

        float correctionPercent = Math.min(correctionWidth, correctionHeight) * 100;
        if (correctionPercent < 100) {
            image.scalePercent(correctionPercent);
        }
    }

    private void printTextElement(Document doc, TextElement textElement) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        Phrase phrase = new Phrase(textElement.getTextValue(), textFont);
        paragraph.add(phrase);
        paragraph.setSpacingBefore(5f);
        doc.add(paragraph);
    }

    public static class WatermarkPageEvent extends PdfPageEventHelper {

        private final String watermark;
        private final Font FONT = new Font(Font.HELVETICA, 52, Font.BOLD, new GrayColor(0.85f));

        public WatermarkPageEvent(String watermark) {
            this.watermark = watermark;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContentUnder(),
                    Element.ALIGN_CENTER, new Phrase(watermark, FONT),
                    297.5f, 421, writer.getPageNumber() % 2 == 1 ? 45 : -45);
        }
    }


}
