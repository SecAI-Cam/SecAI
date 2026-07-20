package com.secai.verify.model;

import java.util.List;
import java.util.ArrayList;

public class VerificationResult {
    public enum Status {
        CONFIRMED,
        NOT_VULNERABLE,
        INCONCLUSIVE,
        ERROR
    }

    private String findingReferenceId;
    private Status verificationStatus;
    private List<VerificationEvidence> evidence = new ArrayList<>();
    private int riskScore; // 0-100
    private String cvssString;
    private String businessImpactAssessment;
    private String remediationPriority;

    public String getFindingReferenceId() {
        return findingReferenceId;
    }

    public void setFindingReferenceId(String findingReferenceId) {
        this.findingReferenceId = findingReferenceId;
    }

    public Status getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(Status verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public List<VerificationEvidence> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<VerificationEvidence> evidence) {
        this.evidence = evidence;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getCvssString() {
        return cvssString;
    }

    public void setCvssString(String cvssString) {
        this.cvssString = cvssString;
    }

    public String getBusinessImpactAssessment() {
        return businessImpactAssessment;
    }

    public void setBusinessImpactAssessment(String businessImpactAssessment) {
        this.businessImpactAssessment = businessImpactAssessment;
    }

    public String getRemediationPriority() {
        return remediationPriority;
    }

    public void setRemediationPriority(String remediationPriority) {
        this.remediationPriority = remediationPriority;
    }
}
