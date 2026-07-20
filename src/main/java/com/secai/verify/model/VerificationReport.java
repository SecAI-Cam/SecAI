package com.secai.verify.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class VerificationReport {
    private VerificationTarget target;
    private String executiveSummary;
    private String methodology;
    private List<VerificationResult> results = new ArrayList<>();
    private int overallRiskScore;
    private Map<String, Integer> severityDistribution = new HashMap<>();
    private String aiRemediationRoadmap;
    private String timestamp;
    private String scope;

    public VerificationTarget getTarget() {
        return target;
    }

    public void setTarget(VerificationTarget target) {
        this.target = target;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public void setExecutiveSummary(String executiveSummary) {
        this.executiveSummary = executiveSummary;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public List<VerificationResult> getResults() {
        return results;
    }

    public void setResults(List<VerificationResult> results) {
        this.results = results;
    }

    public int getOverallRiskScore() {
        return overallRiskScore;
    }

    public void setOverallRiskScore(int overallRiskScore) {
        this.overallRiskScore = overallRiskScore;
    }

    public Map<String, Integer> getSeverityDistribution() {
        return severityDistribution;
    }

    public void setSeverityDistribution(Map<String, Integer> severityDistribution) {
        this.severityDistribution = severityDistribution;
    }

    public String getAiRemediationRoadmap() {
        return aiRemediationRoadmap;
    }

    public void setAiRemediationRoadmap(String aiRemediationRoadmap) {
        this.aiRemediationRoadmap = aiRemediationRoadmap;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
