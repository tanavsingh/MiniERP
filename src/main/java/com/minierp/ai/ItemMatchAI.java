package com.minierp.ai;

import com.minierp.models.LostItem;
import com.minierp.models.FoundItem;
import java.util.*;

public class ItemMatchAI {

    public List<Map<String, Object>> findMatches(List<LostItem> lostItems, List<FoundItem> foundItems) {
        List<Map<String, Object>> matches = new ArrayList<>();
        for (LostItem lost : lostItems) {
            for (FoundItem found : foundItems) {
                double score = computeSimilarity(lost, found);
                if (score >= 0.3) {
                    Map<String, Object> match = new HashMap<>();
                    match.put("lost_item", lost);
                    match.put("found_item", found);
                    match.put("score", score);
                    match.put("confidence", score >= 0.7 ? "HIGH" : score >= 0.5 ? "MEDIUM" : "LOW");
                    matches.add(match);
                }
            }
        }
        matches.sort((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")));
        return matches;
    }

    private double computeSimilarity(LostItem lost, FoundItem found) {
        double score = 0.0;
        // Category match
        if (lost.getCategory() != null && lost.getCategory().equalsIgnoreCase(found.getCategory())) score += 0.3;
        // Name similarity
        score += tokenSimilarity(lost.getItemName(), found.getItemName()) * 0.4;
        // Description similarity
        score += tokenSimilarity(lost.getDescription(), found.getDescription()) * 0.3;
        return Math.min(score, 1.0);
    }

    private double tokenSimilarity(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) return 0.0;
        Set<String> tokensA = new HashSet<>(Arrays.asList(a.toLowerCase().split("\\W+")));
        Set<String> tokensB = new HashSet<>(Arrays.asList(b.toLowerCase().split("\\W+")));
        Set<String> intersection = new HashSet<>(tokensA);
        intersection.retainAll(tokensB);
        Set<String> union = new HashSet<>(tokensA);
        union.addAll(tokensB);
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
}
