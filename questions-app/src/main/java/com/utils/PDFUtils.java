package com.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for reading text from PDF files.
 */
public final class PDFUtils {
    /**
     * Read text from a PDF file.
     * @param filename
     * @return the text in the PDF file
     */
    public static String readPDF(String filename) {
        if(StringUtils.isBlank(filename)) {
            return null;
        }
        try (InputStream inputStream = PDFUtils.class.getClassLoader().getResourceAsStream(filename)){
            if(inputStream == null) {
                return null;
            }
            try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                return text;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
