package com.minierp.services;

import com.minierp.dao.LostItemDAO;
import com.minierp.dao.FoundItemDAO;
import com.minierp.dao.ClaimRequestDAO;
import com.minierp.models.LostItem;
import com.minierp.models.FoundItem;
import com.minierp.models.ClaimRequest;
import java.util.List;

public class LostFoundService {
    private final LostItemDAO lostDAO = new LostItemDAO();
    private final FoundItemDAO foundDAO = new FoundItemDAO();
    private final ClaimRequestDAO claimDAO = new ClaimRequestDAO();

    public List<LostItem> getAllLostItems() { return lostDAO.findAll(); }
    public List<LostItem> getActiveLostItems() { return lostDAO.findActive(); }
    public boolean reportLostItem(LostItem item) { return lostDAO.insert(item) > 0; }

    public List<FoundItem> getAllFoundItems() { return foundDAO.findAll(); }
    public List<FoundItem> getActiveFoundItems() { return foundDAO.findActive(); }
    public boolean reportFoundItem(FoundItem item) { return foundDAO.insert(item) > 0; }

    public List<ClaimRequest> getAllClaims() { return claimDAO.findAll(); }
    public List<ClaimRequest> getPendingClaims() { return claimDAO.findPending(); }
    public boolean submitClaim(ClaimRequest cr) { return claimDAO.insert(cr) > 0; }

    public boolean approveClaim(int claimId, int reviewerId, int itemId, boolean isLost) {
        int r = claimDAO.updateStatus(claimId, "APPROVED", reviewerId);
        if (r > 0) {
            if (isLost) lostDAO.updateStatus(itemId, "CLAIMED");
            else foundDAO.updateStatus(itemId, "CLAIMED");
        }
        return r > 0;
    }

    public boolean rejectClaim(int claimId, int reviewerId) {
        return claimDAO.updateStatus(claimId, "REJECTED", reviewerId) > 0;
    }
}
