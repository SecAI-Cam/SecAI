package com.secai.verify.tool;

import com.secai.verify.model.ExecutionMode;
import com.secai.verify.model.VerificationEvidence;
import com.secai.verify.model.VerificationStep;
import com.secai.verify.model.VerificationTarget;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class SqlmapTool implements SecurityTool {

    @Override
    public String getName() {
        return "sqlmap";
    }

    @Override
    public String getDescription() {
        return "Automatic SQL injection and database takeover tool";
    }

    @Override
    public List<String> buildCommands(VerificationStep step, VerificationTarget target) {
        List<String> cmd = new ArrayList<>();
        cmd.add("sqlmap");
        
        cmd.add("-u");
        cmd.add(target.getTargetUrl());
        
        cmd.add("--batch"); // Never ask for user input
        
        if (step.getMode() == ExecutionMode.VERIFY) {
            // Safe, fast check
            cmd.add("--level=1");
            cmd.add("--risk=1");
            // Only check, don't dump
        } else {
            // Exploit mode
            cmd.add("--level=3"); // Increase level for better detection
            cmd.add("--risk=2");
            // Might add --dump or --dbs if specifically requested in template
        }
        
        // Add arguments from step template
        if (step.getCommandTemplate() != null && !step.getCommandTemplate().isEmpty()) {
             // Basic parsing (a real implementation would use a proper shell lexer)
             String[] parts = step.getCommandTemplate().split("\\s+");
             for (String part : parts) {
                 if (!part.equals("sqlmap") && !part.equals("-u") && !part.equals(target.getTargetUrl()) && !part.equals("--batch")) {
                     cmd.add(part);
                 }
             }
        }
        
        return cmd;
    }

    @Override
    public VerificationEvidence parseOutput(String rawOutput, int exitCode, long durationMs) {
        VerificationEvidence evidence = new VerificationEvidence();
        evidence.setRawOutput(rawOutput);
        evidence.setExitCode(exitCode);
        evidence.setExecutionDurationMs(durationMs);
        
        boolean isInjected = rawOutput.contains("is vulnerable") || rawOutput.contains("identified the following injection point");
        
        evidence.setParsedResults(new HashMap<>());
        evidence.getParsedResults().put("vulnerable", isInjected);
        
        return evidence;
    }

    @Override
    public boolean isApplicable(VerificationTarget target) {
        return target != null && target.getTargetUrl() != null && 
               (target.getTargetUrl().startsWith("http://") || target.getTargetUrl().startsWith("https://")) &&
               target.getTargetUrl().contains("?"); // Needs parameters to inject
    }

    @Override
    public ExecutionMode getDefaultMode() {
        return ExecutionMode.VERIFY;
    }
}
