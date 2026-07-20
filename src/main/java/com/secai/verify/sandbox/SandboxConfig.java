package com.secai.verify.sandbox;

public class SandboxConfig {
    private int cpuLimit = 2;
    private String memoryLimit = "2g";
    private String toolTimeout = "5m";
    private String overallTimeout = "30m";
    private String networkPolicy = "target-only";

    public int getCpuLimit() {
        return cpuLimit;
    }

    public void setCpuLimit(int cpuLimit) {
        this.cpuLimit = cpuLimit;
    }

    public String getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public String getToolTimeout() {
        return toolTimeout;
    }

    public void setToolTimeout(String toolTimeout) {
        this.toolTimeout = toolTimeout;
    }

    public String getOverallTimeout() {
        return overallTimeout;
    }

    public void setOverallTimeout(String overallTimeout) {
        this.overallTimeout = overallTimeout;
    }

    public String getNetworkPolicy() {
        return networkPolicy;
    }

    public void setNetworkPolicy(String networkPolicy) {
        this.networkPolicy = networkPolicy;
    }
    
    public long getToolTimeoutMs() {
        return parseDurationMs(toolTimeout);
    }
    
    public long getOverallTimeoutMs() {
        return parseDurationMs(overallTimeout);
    }
    
    private long parseDurationMs(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) return 300000; // 5 min default
        durationStr = durationStr.toLowerCase();
        try {
            if (durationStr.endsWith("m")) {
                return Long.parseLong(durationStr.replace("m", "")) * 60 * 1000;
            } else if (durationStr.endsWith("s")) {
                return Long.parseLong(durationStr.replace("s", "")) * 1000;
            } else if (durationStr.endsWith("h")) {
                return Long.parseLong(durationStr.replace("h", "")) * 60 * 60 * 1000;
            } else {
                return Long.parseLong(durationStr);
            }
        } catch (NumberFormatException e) {
            return 300000; // 5 min fallback
        }
    }
}
