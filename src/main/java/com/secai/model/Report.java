package com.secai.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Report {
    private String projectId;
    private String timestamp;
    private List<Finding> findings;
    private Map<String, Integer> severityDistribution;
    private int riskScore;

    public Report() {
        this.findings = new ArrayList<>();
        this.severityDistribution = new HashMap<>();
    }

    // Getters and Setters

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Finding> getFindings() {
        return findings;
    }

    public void setFindings(List<Finding> findings) {
        this.findings = findings;
    }

    public Map<String, Integer> getSeverityDistribution() {
        return severityDistribution;
    }

    public void setSeverityDistribution(Map<String, Integer> severityDistribution) {
        this.severityDistribution = severityDistribution;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }
}
