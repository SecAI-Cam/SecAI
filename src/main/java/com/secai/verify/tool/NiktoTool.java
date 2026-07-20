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
public class NiktoTool implements SecurityTool {

    @Override
    public String getName() {
        return "nikto";
    }

    @Override
    public String getDescription() {
        return "Web server scanner";
    }

    @Override
    public List<String> buildCommands(VerificationStep step, VerificationTarget target) {
        List<String> cmd = new ArrayList<>();
        cmd.add("nikto");
        
        cmd.add("-h");
        cmd.add(target.getTargetUrl());
        
        cmd.add("-Format");
        cmd.add("json");
        cmd.add("-output");
        cmd.add("/workspace/nikto-results.json");
        
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
