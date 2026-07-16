package com.secai.model;

import java.util.List;

public class Finding {
    private String id;
    private String title;
    private String description;
    private String severity;
    private String file;
    private int line;
    private String scannerName;
    private String aiExplanation;
    private String aiRemediation;
    private String attackScenario;
    private String secureCodeExample;
    private String searchString;
    private String replaceString;
    private String fileContext;
    private List<String> cweMappings;
    private List<String> owaspMappings;

    // Getters and Setters

    public String getAttackScenario() {
        return attackScenario;
    }

    public void setAttackScenario(String attackScenario) {
        this.attackScenario = attackScenario;
    }

    public String getSecureCodeExample() {
        return secureCodeExample;
    }

    public void setSecureCodeExample(String secureCodeExample) {
        this.secureCodeExample = secureCodeExample;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getScannerName() {
        return scannerName;
    }

    public void setScannerName(String scannerName) {
        this.scannerName = scannerName;
    }

    public String getAiExplanation() {
        return aiExplanation;
    }

    public void setAiExplanation(String aiExplanation) {
        this.aiExplanation = aiExplanation;
    }

    public String getAiRemediation() {
        return aiRemediation;
    }

    public void setAiRemediation(String aiRemediation) {
        this.aiRemediation = aiRemediation;
    }

    public List<String> getCweMappings() {
        return cweMappings;
    }

    public void setCweMappings(List<String> cweMappings) {
        this.cweMappings = cweMappings;
    }

    public List<String> getOwaspMappings() {
        return owaspMappings;
    }

    public void setOwaspMappings(List<String> owaspMappings) {
        this.owaspMappings = owaspMappings;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getReplaceString() {
        return replaceString;
    }

    public void setReplaceString(String replaceString) {
        this.replaceString = replaceString;
    }

    public String getFileContext() {
        return fileContext;
    }

    public void setFileContext(String fileContext) {
        this.fileContext = fileContext;
    }
}
