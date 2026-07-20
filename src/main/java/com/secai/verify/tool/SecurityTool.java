package com.secai.verify.tool;

import com.secai.verify.model.VerificationEvidence;
import com.secai.verify.model.VerificationStep;
import com.secai.verify.model.VerificationTarget;
import com.secai.verify.model.ExecutionMode;
import java.util.List;

public interface SecurityTool {
    String getName();
    String getDescription();
    
    /**
     * Builds the command to be executed inside the sandbox.
     */
    List<String> buildCommands(VerificationStep step, VerificationTarget target);
    
    /**
     * Parses the raw output from the tool execution into structured evidence.
     */
    VerificationEvidence parseOutput(String rawOutput, int exitCode, long durationMs);
    
    /**
     * Determines if this tool is applicable for the given target.
     */
    boolean isApplicable(VerificationTarget target);
    
    /**
     * The default execution mode for this tool.
     */
    ExecutionMode getDefaultMode();
}
