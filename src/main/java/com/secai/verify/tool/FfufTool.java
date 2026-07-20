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
public class FfufTool implements SecurityTool {

    @Override
    public String getName() {
        return "ffuf";
    }

    @Override
    public String getDescription() {
        return "Fast web fuzzer written in Go";
    }

    @Override
    public List<String> buildCommands(VerificationStep step, VerificationTarget target) {
        List<String> cmd = new ArrayList<>();
        cmd.add("ffuf");
        
        // Require a wordlist, use a small default if not provided
        boolean hasWordlist = step.getCommandTemplate() != null && step.getCommandTemplate().contains("-w");
        if (!hasWordlist) {
            cmd.add("-w");
            cmd.add("/usr/share/wordlists/dirb/common.txt"); // Assuming Kali layout
        }
        
        String url = target.getTargetUrl();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "FUZZ";
        
        cmd.add("-u");
        cmd.add(url);
        
        cmd.add("-o");
        cmd.add("/workspace/ffuf-results.json");
        cmd.add("-of");
        cmd.add("json");
        
        // Add arguments from step template
        if (step.getCommandTemplate() != null && !step.getCommandTemplate().isEmpty()) {
             String[] parts = step.getCommandTemplate().split("\\s+");
             for (String part : parts) {
                 if (!part.equals("ffuf") && !part.equals("-u") && !part.contains("FUZZ")) {
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
        evidence.setParsedResults(new HashMap<>());
        return evidence;
    }

    @Override
    public boolean isApplicable(VerificationTarget target) {
        return target != null && target.getTargetUrl() != null && 
               (target.getTargetUrl().startsWith("http://") || target.getTargetUrl().startsWith("https://"));
    }

    @Override
    public ExecutionMode getDefaultMode() {
        return ExecutionMode.VERIFY;
    }
}
