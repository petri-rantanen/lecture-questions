package com.questions;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.database.QuestionStorage;
import com.utils.OpenAIClient;
import com.utils.PDFUtils;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/topics")
public class TopicsResource {
    @Inject
    private QuestionStorage questionStorage;
    private static final String PDF_FILE = PDFUtils.readPDF("slides.pdf");
    @Inject
    private OpenAIClient openAIClient;

    @POST
    @Path("/question")
    @Produces(MediaType.TEXT_PLAIN)
    public Response postQuestion(@QueryParam("question") String question, @QueryParam("id") String id) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(question)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("ERROR").build();
        }
        if(!questionStorage.addQuestion(id, question)){
            return Response.status(Response.Status.NOT_FOUND).entity("ERROR").build();
        }
        return Response.ok("OK").build();
    }

    @GET
    @Path("/topic")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTopics(@QueryParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("ERROR").build();
        }
        String topics = generateTopics(id);
        if (topics == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("ERROR").build();
        }
        return Response.ok(topics).build();
    }

    /**
     * Generate a list of topics for the given id.
     * @param id
     * @return
     */
    private String generateTopics(String id) {
        List<String> questionList = questionStorage.getQuestions(id);
        if(questionList == null || questionList.isEmpty()) {
            return null;
        }
        return openAIClient.generateTopics(questionList, PDF_FILE);
    }
}
