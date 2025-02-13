package com.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryQuestionStorageTest {

    private InMemoryQuestionStorage storage;

    @BeforeEach
    void setup() {
        // Manually passing test identifiers
        storage = new InMemoryQuestionStorage("123,456");
    }

    @Test
    void testAddAndGetQuestions() {
        storage.addQuestion("123", "What is Quarkus?");
        storage.addQuestion("123", "How does dependency injection work?");

        List<String> questions = storage.getQuestions("123");

        assertNotNull(questions);
        assertEquals(2, questions.size());
        assertEquals("What is Quarkus?", questions.get(0));
        assertEquals("How does dependency injection work?", questions.get(1));
    }

    @Test
    void testGetQuestionsForUnknownId() {
        List<String> questions = storage.getQuestions("999");
        assertNull(questions);  // Should return null since "999" is not in "123,456"
    }

    @Test
    void testAddQuestionCreatesNewListFails() {
        boolean added = storage.addQuestion("789", "Is this working?");
        assertFalse(added);  // "789" was not in "123,456", so it should fail
    }

    @Test
    void testEmptyListReturnsNull() {
        List<String> questions = storage.getQuestions("456"); // No questions added
        assertNull(questions); // Should return null for empty lists
    }
}
