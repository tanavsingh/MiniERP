package com.minierp.controllers;

import com.minierp.services.LostFoundService;
import com.minierp.ai.ItemMatchAI;
import com.minierp.models.*;
import java.util.*;

public class LostFoundController {
    private final LostFoundService service = new LostFoundService();
    private final ItemMatchAI ai = new ItemMatchAI();

    public List<LostItem> getAllLost() { return service.getAllLostItems(); }
    public List<LostItem> getActiveLost() { return service.getActiveLostItems(); }
    public boolean reportLost(LostItem item) { return service.reportLostItem(item); }

    public List<FoundItem> getAllFound() { return service.getAllFoundItems(); }
    public List<FoundItem> getActiveFound() { return service.getActiveFoundItems(); }
    public boolean reportFound(FoundItem item) { return service.reportFoundItem(item); }

    public List<ClaimRequest> getAllClaims() { return service.getAllClaims(); }
    public List<ClaimRequest> getPendingClaims() { return service.getPendingClaims(); }
    public boolean submitClaim(ClaimRequest cr) { return service.submitClaim(cr); }
    public boolean approveClaim(int id, int reviewerId, int itemId, boolean isLost) {
        return service.approveClaim(id, reviewerId, itemId, isLost);
    }
    public boolean rejectClaim(int id, int reviewerId) { return service.rejectClaim(id, reviewerId); }

    public List<Map<String, Object>> findAIMatches() {
        return ai.findMatches(service.getActiveLostItems(), service.getActiveFoundItems());
    }
}
