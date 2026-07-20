package com.secai.verify.model;

import java.util.List;
import java.util.ArrayList;

public class VerificationStep {
    private String stepId;
    private String toolName;
    private String commandTemplate;
    private String purpose;
    private String expectedEvidence;
    private int confidenceScore; // 0-100
    private String estimatedDuration;
    private ExecutionMode mode = ExecutionMode.VERIFY;
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
    private List<String> associatedFindingIds = new ArrayList<>();
    private String approvalId; // Added when approved

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getCommandTemplate() {
        return commandTemplate;
    }

    public void setCommandTemplate(String commandTemplate) {
        this.commandTemplate = commandTemplate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getExpectedEvidence() {
        return expectedEvidence;
    }

    public void setExpectedEvidence(String expectedEvidence) {
        this.expectedEvidence = expectedEvidence;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(int confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public ExecutionMode getMode() {
        return mode;
    }

    public void setMode(ExecutionMode mode) {
        this.mode = mode;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public List<String> getAssociatedFindingIds() {
        return associatedFindingIds;
    }

    public void setAssociatedFindingIds(List<String> associatedFindingIds) {
        this.associatedFindingIds = associatedFindingIds;
    }

    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }
}
