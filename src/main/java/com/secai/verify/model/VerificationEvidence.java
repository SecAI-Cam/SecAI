package com.secai.verify.model;

import java.util.Map;
import java.util.HashMap;

public class VerificationEvidence {
    private String commandExecuted;
    private String timestamp;
    private String rawOutput;
    private int exitCode;
    private Map<String, Object> parsedResults = new HashMap<>(); // E.g., HTTP responses, retrieved files
    private String aiSummary;
    private long executionDurationMs;

    public String getCommandExecuted() {
        return commandExecuted;
    }

    public void setCommandExecuted(String commandExecuted) {
        this.commandExecuted = commandExecuted;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRawOutput() {
        return rawOutput;
    }

    public void setRawOutput(String rawOutput) {
        this.rawOutput = rawOutput;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public Map<String, Object> getParsedResults() {
        return parsedResults;
    }

    public void setParsedResults(Map<String, Object> parsedResults) {
        this.parsedResults = parsedResults;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public long getExecutionDurationMs() {
        return executionDurationMs;
    }

    public void setExecutionDurationMs(long executionDurationMs) {
        this.executionDurationMs = executionDurationMs;
    }
}
