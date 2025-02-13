package com.database;

import java.util.List;

/**
 * Interface for storing questions.
 */
public interface QuestionStorage {
    /**
     * 
     * @param id
     * @param question
     * @return true if the question was added, false otherwise
     */
    boolean addQuestion(String id, String question);
    /**
     * 
     * @param id
     * @return a list of questions for the given id or null if no questions are found
     */
    List<String> getQuestions(String id);
}

