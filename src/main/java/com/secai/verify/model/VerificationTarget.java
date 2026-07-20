package com.secai.verify.model;

import java.util.Map;
import java.util.HashMap;

public class VerificationTarget {
    private String targetUrl;
    private String projectPath;
    private String detectedTechnology; // e.g., "Spring Boot", "Apache 2.4.49"
    private Map<String, String> metadata = new HashMap<>();

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getDetectedTechnology() {
        return detectedTechnology;
    }

    public void setDetectedTechnology(String detectedTechnology) {
        this.detectedTechnology = detectedTechnology;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
