package com.secai.verify.model;

import java.util.List;
import java.util.ArrayList;

public class VerificationPlan {
    private VerificationTarget target;
    private List<VerificationStep> steps = new ArrayList<>();
    private String estimatedTotalDuration;
    private ApprovalStatus planApprovalStatus = ApprovalStatus.PENDING;
    private String creationTimestamp;
    private String aiReasoningForExclusions;

    public VerificationTarget getTarget() {
        return target;
    }

    public void setTarget(VerificationTarget target) {
        this.target = target;
    }

    public List<VerificationStep> getSteps() {
        return steps;
    }

    public void setSteps(List<VerificationStep> steps) {
        this.steps = steps;
    }

    public String getEstimatedTotalDuration() {
        return estimatedTotalDuration;
    }

    public void setEstimatedTotalDuration(String estimatedTotalDuration) {
        this.estimatedTotalDuration = estimatedTotalDuration;
    }

    public ApprovalStatus getPlanApprovalStatus() {
        return planApprovalStatus;
    }

    public void setPlanApprovalStatus(ApprovalStatus planApprovalStatus) {
        this.planApprovalStatus = planApprovalStatus;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getAiReasoningForExclusions() {
        return aiReasoningForExclusions;
    }

    public void setAiReasoningForExclusions(String aiReasoningForExclusions) {
        this.aiReasoningForExclusions = aiReasoningForExclusions;
    }
}
