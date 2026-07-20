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
public class NucleiTool implements SecurityTool {

    @Override
    public String getName() {
        return "nuclei";
    }

    @Override
    public String getDescription() {
        return "Fast and customizable vulnerability scanner based on simple YAML based DSL";
    }

    @Override
    public List<String> buildCommands(VerificationStep step, VerificationTarget target) {
        List<String> cmd = new ArrayList<>();
        cmd.add("nuclei");
        
        cmd.add("-u");
        cmd.add(target.getTargetUrl());
        
        // Output as JSON for easier parsing
        cmd.add("-jsonl");
        cmd.add("-o");
        cmd.add("/workspace/nuclei-results.json");
        
        // Add arguments from step template
        if (step.getCommandTemplate() != null && !step.getCommandTemplate().isEmpty()) {
            String[] parts = step.getCommandTemplate().split("\\s+");
            for (String part : parts) {
                if (!part.equals("nuclei") && !part.equals("-u") && !part.equals(target.getTargetUrl())) {
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
        // Would parse JSONL file here
        return evidence;
    }

    @Override
    public boolean isApplicable(VerificationTarget target) {
        return target != null && target.getTargetUrl() != null && (target.getTargetUrl().startsWith("http://") || target.getTargetUrl().startsWith("https://"));
    }

    @Override
    public ExecutionMode getDefaultMode() {
        return ExecutionMode.VERIFY;
    }
}
