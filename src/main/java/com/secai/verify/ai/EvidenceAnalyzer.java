package com.secai.verify.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secai.ai.AIEngine;
import com.secai.model.ChatMessage;
import com.secai.model.Finding;
import com.secai.verify.model.VerificationEvidence;
import com.secai.verify.model.VerificationResult;
import com.secai.verify.model.VerificationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EvidenceAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(EvidenceAnalyzer.class);

    private final AIEngine aiEngine;
    private final ObjectMapper mapper;

    @Autowired
    public EvidenceAnalyzer(AIEngine aiEngine) {
        this.aiEngine = aiEngine;
        this.mapper = new ObjectMapper();
    }

    public VerificationResult analyze(Finding finding, List<VerificationStep> steps, List<VerificationEvidence> evidenceList) {
        logger.info("Analyzing evidence for finding: {}", finding.getId());

        String prompt = buildPrompt(finding, steps, evidenceList);
        
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system", "You are an expert penetration tester AI. Analyze the dynamic verification evidence against the static finding and output ONLY a JSON object. Do not use markdown code blocks."));
        history.add(new ChatMessage("user", prompt));

        String aiResponse = aiEngine.chat(history);
        
        try {
            VerificationResult result = parseResult(aiResponse);
            result.setFindingReferenceId(finding.getId());
            result.setEvidence(evidenceList);
            
            // Auto-generate AI summary for each piece of evidence if missing
            for (VerificationEvidence evidence : evidenceList) {
                if (evidence.getAiSummary() == null || evidence.getAiSummary().isEmpty()) {
                    evidence.setAiSummary(result.getVerificationStatus() + " confirmed by this output.");
                }
            }
            
            return result;
        } catch (Exception e) {
            logger.error("Failed to parse AI response into VerificationResult: {}", e.getMessage());
            // Fallback
            VerificationResult result = new VerificationResult();
            result.setFindingReferenceId(finding.getId());
            result.setVerificationStatus(VerificationResult.Status.ERROR);
            result.setBusinessImpactAssessment("Error analyzing evidence: " + e.getMessage());
            result.setEvidence(evidenceList);
            return result;
        }
    }

    private String buildPrompt(Finding finding, List<VerificationStep> steps, List<VerificationEvidence> evidenceList) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please analyze the following dynamic verification evidence to confirm if the static finding is valid.\n\n");
        
        sb.append("STATIC FINDING:\n");
        sb.append("- Title: ").append(finding.getTitle()).append("\n");
        sb.append("- Severity: ").append(finding.getSeverity()).append("\n");
        sb.append("- Description: ").append(finding.getDescription()).append("\n\n");
        
        sb.append("EVIDENCE COLLECTED:\n");
        for (int i = 0; i < evidenceList.size(); i++) {
            VerificationEvidence ev = evidenceList.get(i);
            sb.append("--- Evidence ").append(i+1).append(" ---\n");
            sb.append("Command: ").append(ev.getCommandExecuted()).append("\n");
            sb.append("Exit Code: ").append(ev.getExitCode()).append("\n");
            
            String output = ev.getRawOutput();
            // Truncate output if it's too long
            if (output != null && output.length() > 2000) {
                output = output.substring(0, 1000) + "\n...[TRUNCATED]...\n" + output.substring(output.length() - 1000);
            }
            sb.append("Output:\n").append(output).append("\n\n");
        }
        
        sb.append("OUTPUT FORMAT:\n");
        sb.append("Return ONLY a JSON object with this structure:\n");
        sb.append("{\n");
        sb.append("  \"verificationStatus\": \"CONFIRMED|NOT_VULNERABLE|INCONCLUSIVE\",\n");
        sb.append("  \"riskScore\": 85, // 0-100 score based on exploitability\n");
        sb.append("  \"cvssString\": \"CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H\",\n");
        sb.append("  \"businessImpactAssessment\": \"Explanation of business risk...\",\n");
        sb.append("  \"remediationPriority\": \"CRITICAL|HIGH|MEDIUM|LOW\"\n");
        sb.append("}\n");

        return sb.toString();
    }
    
    private VerificationResult parseResult(String aiResponse) throws JsonProcessingException {
        String jsonStr = aiResponse.trim();
        Matcher m = Pattern.compile("(?s)```(?:json)?\\s*(.*?)\\s*```").matcher(jsonStr);
        if (m.find()) {
            jsonStr = m.group(1).trim();
        }
        
        return mapper.readValue(jsonStr, VerificationResult.class);
    }
}
