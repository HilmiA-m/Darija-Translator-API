package com.translator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.http.*;
import java.net.URI;
import com.google.gson.*;

@Path("/translate")
public class TranslatorResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "API is working!";
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response translate(@FormParam("text") String text) {
        if (text == null || text.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"No text provided\"}")
                    .build();
        }
        
        String translation = callGemini(text);
        return Response.ok("{\"translation\": \"" + escapeJson(translation) + "\"}").build();
    }

    private String callGemini(String text) {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null) {
            return "Error: GEMINI_API_KEY environment variable not set";
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
            
            String prompt = "Translate the following English text to Moroccan Arabic (Darija). " +
                           "Only return the translation, nothing else. " +
                           "Text: " + text;
            
            String jsonBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\"", "\\\"") + "\"}]}]}";
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            String responseBody = response.body();
            
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            
            if (jsonResponse.has("error")) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                return "API Error: " + error.get("message").getAsString();
            }
            
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            if (candidates == null || candidates.size() == 0) {
                return "No translation received";
            }
            
            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");
            JsonObject firstPart = parts.get(0).getAsJsonObject();
            String translation = firstPart.get("text").getAsString();
            
            return translation;
            
        } catch (Exception e) {
            return "Translation error: " + e.getMessage();
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
