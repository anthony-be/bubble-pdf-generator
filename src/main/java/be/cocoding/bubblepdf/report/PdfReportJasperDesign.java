package be.cocoding.bubblepdf.report;

import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.RotationEnum;

import java.util.List;

public class PdfReportJasperDesign extends JasperDesign {

    private static final int COLUMN_WIDTH = 555;
    public static final int PAGE_HEIGHT = 842;
    public static final int MARGIN = 20;

    private final PdfRequestWrapper request;
    private JasperReportsContext context;

    public PdfReportJasperDesign(PdfRequestWrapper request) throws JRException {
        this.request = request;
        initialize();
    }

    public PdfReportJasperDesign(PdfRequestWrapper request, JasperReportsContext context) throws JRException {
        super(context);
        this.request = request;
        this.context = context;
        initialize();
    }

    private void initialize() throws JRException {
        initPage();
        JRDesignDataset questionsDataset = questionsDataset();
        addDataset(questionsDataset);

        JRDesignField fieldTitle = new JRDesignField();
        fieldTitle.setName("title");
        fieldTitle.setValueClass(String.class);
        addField(fieldTitle);

        JRDesignField fieldElements = new JRDesignField();
        fieldTitle.setName("elements");
        fieldTitle.setValueClass(List.class);
        addField(fieldElements);

        initBackground();
        //TODO Detail band
    }

    private void initBackground() {
        JRDesignBand band = new JRDesignBand();
        band.setHeight(PAGE_HEIGHT - MARGIN /*Top*/ - MARGIN /*Bottom*/);

        JRDesignStaticText textWatermark = new JRDesignStaticText() ;
        textWatermark.setHeight(540);
        textWatermark.setWidth(76);
        textWatermark.setX(479);
        textWatermark.setY(90);
        textWatermark.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
        textWatermark.setRotation(RotationEnum.LEFT);
        textWatermark.setFontSize(50f);
        textWatermark.setText("Sample Watermark");
        band.addElement(textWatermark);

        setBackground(band);
    }

    private void initPage() {
        setPageHeight(PAGE_HEIGHT);
        setPageWidth(595);
        setLeftMargin(MARGIN);
        setRightMargin(20);
        setTopMargin(20);
        setBottomMargin(20);
        setColumnWidth(COLUMN_WIDTH);
    }

    private JRDesignDataset questionsDataset() throws JRException {
        JRDesignDataset dataset = new JRDesignDataset(context, false);

        JRDesignField fieldType = new JRDesignField();
        fieldType.setName("type");
        fieldType.setValueClass(String.class);
        dataset.addField(fieldType);

        JRDesignField fieldValue = new JRDesignField();
        fieldValue.setName("value");
        fieldValue.setValueClass(String.class);
        dataset.addField(fieldValue);

        return dataset;
    }
}
