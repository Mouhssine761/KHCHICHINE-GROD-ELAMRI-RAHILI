package com.example.demo.Service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.*;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name="decision.engine", havingValue="groq")
public class GroqEngine implements Engine {

    private final WebClient client;
    private final String model;

    public GroqEngine(
            @Value("${groq.api-url}") String apiUrl,
            @Value("${groq.api-key}") String apiKey,
            @Value("${groq.model}")   String model
    ) {
        this.client = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.model = model;
    }
    private static final String SYSTEM_PROMPT =
            "You are a domain‐expert decision assistant.  "
                    + "Each input is a proposal or question about whether something can be done (e.g. “price of houses in Morocco”, "
                    + "“build a water pipe under a mountain”).  "
                    + "Respond only with “Yes,” or “No,” on-topic reason focused on feasibility, cost, or context with one or two sentences."
                    + "Do not bring in unrelated facts.";
    @Override
    public Map<String,String> decideWithContext(List<String> history, String newArgument) {
        List<Map<String,String>> msgs = new ArrayList<>();
        msgs.add(Map.of("role","system","content", SYSTEM_PROMPT));
        history.forEach(h -> msgs.add(Map.of("role","user","content", h)));
        msgs.add(Map.of("role","user","content", newArgument));

        Map<String,Object> payload = Map.of(
                "model",    model,
                "messages", msgs
        );


        JsonNode resp = client.post()
                .uri("/openai/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String content = resp
                .path("choices").get(0)
                .path("message").path("content")
                .asText();

        return parseContent(content);
    }

    @Override
    public Map<String, String> decide(String argument) {
        // 1) Construct the OpenAI‐style payload
        Map<String,Object> payload = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role",    "system",
                                "content", "You are a meeting assistant. For each argument, reply “Yes,” or “No,” then explain why."
                        ),
                        Map.of(
                                "role",    "user",
                                "content", argument
                        )
                )
        );

        try {
            JsonNode resp = client.post()
                    .uri("/openai/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String content = resp
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            return parseContent(content);

        } catch (WebClientResponseException e) {
            return Map.of(
                    "willWork", "no",
                    "reason",    "Groq API error " + e.getRawStatusCode()
            );
        } catch (Exception e) {
            return Map.of(
                    "willWork", "no",
                    "reason",    "Groq client error: " + e.getMessage()
            );
        }
    }

    @Override
    public String summarize(String combinedText) {
        Map<String,Object> payload = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role",    "system",
                                "content", """
You are a discussion synthesizer.  The user is showing you a numbered sequence of statements
from a single speaker building on their own prior points.  Produce one cohesive conclusion paragraph
that:
  1) Summarizes the original question
  2) Explains how each follow-up refines or challenges that question
  3) Arrives at a final recommendation
Avoid restating each bullet; weave them into a narrative flow.
""".strip()
                        ),
                        Map.of("role","user", "content", combinedText)
                )
        );

        JsonNode resp = client.post()
                .uri("/openai/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return resp.path("choices").get(0)
                .path("message").path("content")
                .asText()
                .trim();
    }

    /** Helper to split “Yes, …” or “No, …” into fields */
    private Map<String, String> parseContent(String content) {
        boolean works = content.trim().toLowerCase().startsWith("yes");
        String willWork = works ? "yes" : "no";
        int comma = content.indexOf(',');
        String reason = (comma >= 0 && comma < content.length()-1)
                ? content.substring(comma+1).trim()
                : content.replaceFirst("(?i)^(yes|no)\\s*", "").trim();
        return Map.of("willWork", willWork, "reason", reason);
    }
}
