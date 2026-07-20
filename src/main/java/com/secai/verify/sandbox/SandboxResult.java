package com.secai.verify.sandbox;

public class SandboxResult {
    private String stdout;
    private String stderr;
    private int exitCode;
    private long executionDurationMs;
    private boolean timeoutHit;

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public long getExecutionDurationMs() {
        return executionDurationMs;
    }

    public void setExecutionDurationMs(long executionDurationMs) {
        this.executionDurationMs = executionDurationMs;
    }

    public boolean isTimeoutHit() {
        return timeoutHit;
    }

    public void setTimeoutHit(boolean timeoutHit) {
        this.timeoutHit = timeoutHit;
    }
    
    public String getCombinedOutput() {
        StringBuilder sb = new StringBuilder();
        if (stdout != null && !stdout.isEmpty()) sb.append(stdout);
        if (stderr != null && !stderr.isEmpty()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(stderr);
        }
        return sb.toString();
    }
}
