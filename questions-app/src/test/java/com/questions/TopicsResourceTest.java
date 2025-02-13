package com.questions;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.database.QuestionStorage;
import com.utils.OpenAIClient;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

/*
 * This test class uses the QuarkusTest annotation to start the Quarkus application
 */
@QuarkusTest
public class TopicsResourceTest {

    @InjectMock
    OpenAIClient openAIClient; // Mock OpenAIClient

    @InjectMock
    QuestionStorage questionStorage; // Mock QuestionStorage

    @BeforeEach
    void setup() {
        when(openAIClient.generateTopics(anyList(), anyString()))
            .thenReturn("Quarkus, Dependency Injection");

        when(questionStorage.addQuestion(anyString(), anyString())).thenReturn(true); // Ensure storage accepts questions
    }

    @Test
    void testPostQuestion() {
        given()
            .queryParam("question", "What is Quarkus?")
            .queryParam("id", "123")
            .when().post("/topics/question")
            .then()
            .statusCode(200)
            .body(equalTo("OK"));
    }

    @Test
    void testPostQuestionWithInvalidId() {
        when(questionStorage.addQuestion(anyString(), anyString())).thenReturn(false); // Simulate failure

        given()
            .queryParam("question", "Invalid question")
            .queryParam("id", "999") // Assume this ID is invalid
            .when().post("/topics/question")
            .then()
            .statusCode(404)  // Now returns 404 instead of 200
            .body(equalTo("ERROR"));
    }

    @Test
    void testGetTopics() {
        // Mock the storage to return stored questions
        when(questionStorage.getQuestions("123"))
            .thenReturn(Arrays.asList("What is Quarkus?", "How does dependency injection work?"));

        // Verify that the topics are returned
        given()
            .queryParam("id", "123")
            .when().get("/topics/topic")
            .then()
            .statusCode(200)  // Ensure 200 OK
            .body(equalTo("Quarkus, Dependency Injection"));  // Expected response
    }

    @Test
    void testGetTopicsWithNoQuestions() {
        when(questionStorage.getQuestions("999")).thenReturn(Collections.emptyList()); // Simulate no questions

        given()
            .queryParam("id", "999") // Non-existent ID
            .when().get("/topics/topic")
            .then()
            .statusCode(404)  // Returns 404 instead of 200
            .body(equalTo("ERROR"));
    }
}
