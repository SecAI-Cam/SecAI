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
public class NmapTool implements SecurityTool {

    @Override
    public String getName() {
        return "nmap";
    }

    @Override
    public String getDescription() {
        return "Network Mapper for service discovery and version detection";
    }

    @Override
    public List<String> buildCommands(VerificationStep step, VerificationTarget target) {
        List<String> cmd = new ArrayList<>();
        cmd.add("nmap");
        
        // Always run non-interactive, don't ping if not needed, use XML output
        cmd.add("-Pn");
        cmd.add("-oX");
        cmd.add("/workspace/nmap-results.xml");
        
        // Add arguments from step template or use defaults
        if (step.getCommandTemplate() != null && !step.getCommandTemplate().isEmpty()) {
            String[] parts = step.getCommandTemplate().split("\\s+");
            for (String part : parts) {
                if (!part.equals("nmap") && !part.equals(target.getTargetUrl())) {
                    cmd.add(part);
                }
            }
        } else {
            // Default to fast version detection
            cmd.add("-sV");
            cmd.add("-T4");
            cmd.add("-F"); // Fast port scan
        }
        
        // Extract hostname/IP from URL
        String host = extractHost(target.getTargetUrl());
        cmd.add(host);
        
        return cmd;
    }

    @Override
    public VerificationEvidence parseOutput(String rawOutput, int exitCode, long durationMs) {
        VerificationEvidence evidence = new VerificationEvidence();
        evidence.setRawOutput(rawOutput);
        evidence.setExitCode(exitCode);
        evidence.setExecutionDurationMs(durationMs);
        evidence.setParsedResults(new HashMap<>());
        // In a real implementation, we would parse the XML output from /workspace/nmap-results.xml
        // For now, we'll store the raw output
        evidence.getParsedResults().put("nmap_raw", rawOutput);
        return evidence;
    }

    @Override
    public boolean isApplicable(VerificationTarget target) {
        return target != null && target.getTargetUrl() != null && !target.getTargetUrl().isEmpty();
    }

    @Override
    public ExecutionMode getDefaultMode() {
        return ExecutionMode.VERIFY;
    }
    
    private String extractHost(String url) {
        if (url == null) return "";
        String host = url.replaceFirst("^(http://|https://)", "");
        int slashIdx = host.indexOf('/');
        if (slashIdx > 0) {
            host = host.substring(0, slashIdx);
        }
        int colonIdx = host.indexOf(':');
        if (colonIdx > 0) {
            host = host.substring(0, colonIdx);
        }
        return host;
    }
}
