package com.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for the PDFUtils class.
 */
public class PDFUtilsTest {
    @Test
    void testReadPDF() {
        String content = PDFUtils.readPDF("sample.pdf");
        assertNotNull(content, "PDF content should not be null");
        assertTrue(content.contains("Deploying Containerized Applications to the Cloud in Practice"), "PDF content should match expected content");
        assertTrue(content.contains("Docker and Containers"), "PDF should mention Docker");
        assertTrue(content.contains("End of Sample PDF."), "PDF should contain the ending text");
    }


    @Test
    void testReadPDF_FileNotFound() {
        String text = PDFUtils.readPDF("nonexistent.pdf");
        assertNull(text, "Should return null for non-existent PDF");
    }
}
