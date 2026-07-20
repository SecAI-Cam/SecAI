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
public class MetasploitTool implements SecurityTool {

    @Override
    public String getName() {
        return "metasploit";
    }

    @Override
    public String getDescription() {
        return "Metasploit Framework for vulnerability verification and exploitation";
    }

    @Override
    public List<String> buildCommands(VerificationStep step, VerificationTarget target) {
        List<String> cmd = new ArrayList<>();
        cmd.add("msfconsole");
        cmd.add("-q"); // Quiet mode
        
        // We expect the commandTemplate to contain the exact MSF module and options
        // e.g. "use auxiliary/scanner/http/apache_optionsbleed; set RHOSTS target.com; run"
        if (step.getCommandTemplate() != null && !step.getCommandTemplate().isEmpty()) {
            cmd.add("-x");
            
            // Ensure we never leave sessions hanging in exploit mode
            String msfCmd = step.getCommandTemplate();
            if (step.getMode() == ExecutionMode.EXPLOIT && !msfCmd.contains("sessions -K")) {
                msfCmd += "; sessions -K; exit";
            } else if (!msfCmd.contains("exit")) {
                msfCmd += "; exit";
            }
            
            cmd.add(msfCmd);
        } else {
             // Fallback if no template provided (should not happen in real usage)
             cmd.add("-x");
             cmd.add("exit");
        }
        
        return cmd;
    }

    @Override
    public VerificationEvidence parseOutput(String rawOutput, int exitCode, long durationMs) {
        VerificationEvidence evidence = new VerificationEvidence();
        evidence.setRawOutput(rawOutput);
        evidence.setExitCode(exitCode);
        evidence.setExecutionDurationMs(durationMs);
        
        // Basic parsing for Metasploit success indicators
        boolean isVulnerable = rawOutput.contains("is vulnerable") || 
                               rawOutput.contains("VULNERABLE") || 
                               rawOutput.contains("Command shell session") ||
                               rawOutput.contains("Meterpreter session");
                               
        evidence.setParsedResults(new HashMap<>());
        evidence.getParsedResults().put("vulnerable", isVulnerable);
        
        return evidence;
    }

    @Override
    public boolean isApplicable(VerificationTarget target) {
        // Metasploit is applicable to almost any target, but requires AI to select a specific module
        return target != null && target.getTargetUrl() != null;
    }

    @Override
    public ExecutionMode getDefaultMode() {
        // Always default to VERIFY (using check or auxiliary scanners)
        return ExecutionMode.VERIFY;
    }
}
