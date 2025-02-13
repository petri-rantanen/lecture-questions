package com.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * In-memory question storage.
 */
@ApplicationScoped
public class InMemoryQuestionStorage implements QuestionStorage {
    private final HashMap<String, List<String>> questions = new HashMap<>();

    /**
     * Create a new in-memory question storage.
     */
    public InMemoryQuestionStorage(@ConfigProperty(name = "topics.identifiers") String identifiers) {
        String[] ids = identifiers.split(",");
        for (String id : ids) {
            questions.put(id, new ArrayList<>());
        }
    }

    @Override
    public synchronized boolean addQuestion(String id, String question) {
        List<String> questionList = questions.get(id);
        if(questionList == null) {
            return false;
        }
        questionList.add(question);
        return true;
    }

    @Override
    public synchronized List<String> getQuestions(String id) {
        List<String> questionList = questions.get(id);
        return (questionList == null || questionList.isEmpty()) ? null : new ArrayList<>(questionList);
    }
}

