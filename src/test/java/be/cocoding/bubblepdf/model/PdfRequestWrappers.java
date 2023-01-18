package be.cocoding.bubblepdf.model;

import be.cocoding.test.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PdfRequestWrappers {

    //    public static PdfRequestWrapper sampleModel(){
//        // Question 1
//        Question.QuestionBuilder q1 = Question.builder();
//        q1.title("Question 1 - What is your ... ?");
//
//        Element q1e1 = new TextElement("First text sample");
//        Element q1e2 = new ImageElement("<Image's Base64 representation>");
//        Element q1e3 = new TextElement("Second text sample coming after the image");
//        q1.elements(Arrays.asList(q1e1, q1e2, q1e3));
//
//        // Question 2
//        Question.QuestionBuilder q2 = Question.builder();
//        q2.title("Question 2 - What is your ... ?");
//
//        Element q2e1 = new TextElement("2 - First text sample");
//        Element q2e2 = new ImageElement("2 - <Image's Base64 representation>");
//        Element q2e3 = new TextElement("2 - Second text sample coming after the image");
//        q2.elements(Arrays.asList(q2e1, q2e2, q2e3));
//
//        return PdfRequestWrapper.builder()
//                .questions(Arrays.asList(q1.build(), q2.build()))
//                .build();
//    }

    public static PdfRequestWrapper sampleModelWithMetadata(){
        // Question 1
        Question.QuestionBuilder q1 = Question.builder();
        q1.title("Question 1 - What is your ... ?");

        Element q1e1 = new TextElement("First text sample");
        Element q1e2 = new ImageElement("https://s3.amazonaws.com/appforest_uf/f1666453946962x421976841385394600/read-369040_1920.jpg");
        Element q1e3 = new TextElement("Second text sample coming after the image");
        q1.elements(Arrays.asList(q1e1, q1e2, q1e3));

        // Question 2
        Question.QuestionBuilder q2 = Question.builder();
        q2.title("Question 2 - What is your ... ?");

        Element q2e1 = new TextElement("2 - First text sample");
        Element q2e2 = new ImageElement("https://s3.amazonaws.com/appforest_uf/f1666453946962x421976841385394600/read-369040_1920.jpg");
        Element q2e3 = new TextElement("2 - Second text sample coming after the image");
        q2.elements(Arrays.asList(q2e1, q2e2, q2e3));

        // Metadata
        Metadata.MetadataBuilder metadata = Metadata.builder()
                .bucketId("bubble-kindred-tales-test")
                .pdfFileId("gdcerfthb34dr78")
                .labels(Arrays.asList("bubble.io", "kindred-tales", "test", "pdf"));

        return PdfRequestWrapper.builder()
                .questions(Arrays.asList(q1.build(), q2.build()))
                .metadata(metadata.build())
                .build();
    }

    public static PdfRequestWrapper sampleModelWithProfileImage(){
        // Question 1
        Question.QuestionBuilder q1 = Question.builder();
        q1.title("Question 1 - What is your ... ?");

        Element q1e1 = new TextElement("First text sample");
        Element q1e2 = new ImageElement(imgUserProfileB64());
        Element q1e3 = new TextElement("Second text sample coming after the image");
        q1.elements(Arrays.asList(q1e1, q1e2, q1e3));

        // Question 2
        Question.QuestionBuilder q2 = Question.builder();
        q2.title("Question 2 - What is your ... ?");

        Element q2e1 = new TextElement("2 - First text sample");
        Element q2e2 = new ImageElement(imgUserProfileB64());
        Element q2e3 = new TextElement("2 - Second text sample coming after the image");
        q2.elements(Arrays.asList(q2e1, q2e2, q2e3));

        return PdfRequestWrapper.builder()
                .questions(Arrays.asList(q1.build(), q2.build()))
                .build();
    }

    public static PdfRequestWrapper sampleModelWithLocalFileProfileImage() throws IOException {
        // Question 1
        Question.QuestionBuilder q1 = Question.builder();
        q1.title("Question 1 - What is your ... Image Local file ?");

        Element q1e1 = new TextElement("First text sample");
        ImageElement q1e2 = new ImageElement(imgEinsteinUrl());
        File q1e2File = FileUtils.downloadToTempFile(q1e2.getValue());
        q1e2.setMirrorLocalFile(q1e2File);
        Element q1e3 = new TextElement("Second text sample coming after the image");
        q1.elements(Arrays.asList(q1e1, q1e2, q1e3));

        // Question 2
        Question.QuestionBuilder q2 = Question.builder();
        q2.title("Question 2 - What is your ... Image Base64 ?");

        Element q2e1 = new TextElement("2 - First text sample");
        Element q2e2 = new ImageElement(imgUserProfileB64());
        Element q2e3 = new TextElement("2 - Second text sample coming after the image");
        q2.elements(Arrays.asList(q2e1, q2e2, q2e3));

        return PdfRequestWrapper.builder()
                .questions(Arrays.asList(q1.build(), q2.build()))
                .build();
    }

    private static String imgUserProfileB64(){
        return "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";
    }

    private static String imgEinsteinUrl(){
        return "https://upload.wikimedia.org/wikipedia/commons/e/e9/Albert-einstein-profile-picture-512x512.png.cf.png";
    }

}