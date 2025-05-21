// src/main/java/com/example/demo/Service/DecisionService.java
package com.example.demo.Service;

import com.example.demo.model.Decision;
import com.example.demo.rest.SummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DecisionService {
    private final Engine engine;
    private final List<Decision> history = new CopyOnWriteArrayList<>();

    private final List<String> historyTexts = new CopyOnWriteArrayList<>();

    public DecisionService(Engine engine) { this.engine = engine; }

    public Decision evaluateArgument(String speaker, String text) {
        // 1) decide in context of all previous texts
        Map<String,String> verdict = engine.decideWithContext(historyTexts, text);

        // 2) build your new Decision
        Decision d = new Decision(
                speaker,
                text,
                verdict.get("willWork"),
                verdict.get("reason")
        );

        // 3) record both
        history.add(d);
        historyTexts.add(text);

        return d;
    }

    public List<Decision> getAllDecisions() {
        return List.copyOf(history);
    }

    public SummaryResponse summarizeDecisions(List<Decision> decisions) {
        // Prepend speaker names and preserve order:
        String combined = IntStream.range(0, decisions.size())
                .mapToObj(i -> {
                    Decision d = decisions.get(i);
                    return String.format("%d. %s: %s",
                            i+1,
                            d.speaker(),
                            d.text());
                })
                .collect(Collectors.joining("\n"));

        // Delegate to your engine
        String summary = engine.summarize(combined);
        return new SummaryResponse(decisions.size(), summary);
    }

}
