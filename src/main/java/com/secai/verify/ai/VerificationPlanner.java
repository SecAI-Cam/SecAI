package com.secai.verify.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secai.ai.AIEngine;
import com.secai.model.ChatMessage;
import com.secai.model.Finding;
import com.secai.verify.model.VerificationPlan;
import com.secai.verify.model.VerificationTarget;
import com.secai.verify.tool.SecurityTool;
import com.secai.verify.tool.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VerificationPlanner {
    private static final Logger logger = LoggerFactory.getLogger(VerificationPlanner.class);

    private final AIEngine aiEngine;
    private final ToolRegistry toolRegistry;
    private final ObjectMapper mapper;

    @Autowired
    public VerificationPlanner(AIEngine aiEngine, ToolRegistry toolRegistry) {
        this.aiEngine = aiEngine;
        this.toolRegistry = toolRegistry;
        this.mapper = new ObjectMapper().configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public VerificationPlan createNextSteps(VerificationTarget target, Finding finding, List<com.secai.verify.model.VerificationEvidence> previousEvidence) {
        logger.info("Creating next verification steps for target: {} regarding finding: {}", target.getTargetUrl(), finding.getId());

        List<SecurityTool> applicableTools = toolRegistry.getAllTools().stream()
                .filter(t -> t.isApplicable(target))
                .collect(Collectors.toList());

        String prompt = buildNextStepsPrompt(target, finding, applicableTools, previousEvidence);
        
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system", "You are an expert penetration tester orchestration AI. Your job is to output ONLY a valid JSON object representing the NEXT step in a verification plan. Do not output markdown code blocks like ```json, just the raw JSON object."));
        history.add(new ChatMessage("user", prompt));

        String aiResponse = aiEngine.chat(history);
        
        try {
            VerificationPlan plan = parsePlan(aiResponse);
            plan.setTarget(target);
            plan.setCreationTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(new Date()));
            return plan;
        } catch (Exception e) {
            logger.error("Failed to parse AI response into VerificationPlan: {}", e.getMessage());
            logger.debug("Raw AI response:\n{}", aiResponse);
            throw new RuntimeException("Failed to generate verification plan", e);
        }
    }

    private String buildNextStepsPrompt(VerificationTarget target, Finding finding, List<SecurityTool> applicableTools, List<com.secai.verify.model.VerificationEvidence> previousEvidence) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an autonomous penetration testing agent. Your task is to decide the NEXT logical step to verify the following finding.\n\n");
        
        sb.append("TARGET:\n");
        sb.append("- URL: ").append(target.getTargetUrl()).append("\n");
        sb.append("- Tech Stack: ").append(target.getDetectedTechnology() != null ? target.getDetectedTechnology() : "Unknown").append("\n\n");
        
        sb.append("FINDING TO VERIFY:\n");
        sb.append("- [").append(finding.getId()).append("] ").append(finding.getSeverity()).append(": ").append(finding.getTitle()).append("\n\n");
        
        sb.append("PREVIOUS EVIDENCE GATHERED:\n");
        if (previousEvidence == null || previousEvidence.isEmpty()) {
            sb.append("None. This is the first step.\n\n");
        } else {
            for (int i = 0; i < previousEvidence.size(); i++) {
                com.secai.verify.model.VerificationEvidence ev = previousEvidence.get(i);
                sb.append("Step ").append(i+1).append(" Command: ").append(ev.getCommandExecuted()).append("\n");
                sb.append("Output:\n").append(ev.getRawOutput()).append("\n\n");
            }
        }
        
        sb.append("AVAILABLE TOOLS:\n");
        for (SecurityTool t : applicableTools) {
            sb.append("- ").append(t.getName()).append(": ").append(t.getDescription()).append("\n");
        }
        sb.append("\n");
        
        sb.append("All tools will run inside a single shared Kali Linux Docker container, so they can share files in /workspace.\n");
        
        sb.append("OUTPUT FORMAT:\n");
        sb.append("Return ONLY a JSON object. If you have gathered enough evidence to prove or disprove the finding, return an empty steps array. Otherwise, provide the next step to execute.\n");
        sb.append("{\n");
        sb.append("  \"aiReasoning\": \"Why you are choosing this next step, or why you are stopping...\",\n");
        sb.append("  \"steps\": [\n");
        sb.append("    {\n");
        sb.append("      \"stepId\": \"1\",\n");
        sb.append("      \"toolName\": \"nmap\",\n");
        sb.append("      \"commandTemplate\": \"nmap -sV target.com\",\n");
        sb.append("      \"purpose\": \"Why run this tool?\",\n");
        sb.append("      \"expectedEvidence\": \"What evidence are we looking for?\",\n");
        sb.append("      \"confidenceScore\": 90,\n");
        sb.append("      \"estimatedDuration\": \"5m\",\n");
        sb.append("      \"mode\": \"VERIFY\",\n");
        sb.append("      \"associatedFindingIds\": [\"" + finding.getId() + "\"]\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }
    
    private VerificationPlan parsePlan(String aiResponse) throws JsonProcessingException {
        // Sometimes LLMs output markdown blocks even when told not to. Clean it up.
        String jsonStr = aiResponse.trim();
        Matcher m = Pattern.compile("(?s)```(?:json)?\\s*(.*?)\\s*```").matcher(jsonStr);
        if (m.find()) {
            jsonStr = m.group(1).trim();
        }
        
        return mapper.readValue(jsonStr, VerificationPlan.class);
    }
}
