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
        this.mapper = new ObjectMapper();
    }

    public VerificationPlan createPlan(VerificationTarget target, List<Finding> findings) {
        logger.info("Creating verification plan for target: {}", target.getTargetUrl());

        List<SecurityTool> applicableTools = toolRegistry.getAllTools().stream()
                .filter(t -> t.isApplicable(target))
                .collect(Collectors.toList());

        String prompt = buildPrompt(target, findings, applicableTools);
        
        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("system", "You are an expert penetration tester orchestration AI. Your job is to output ONLY a valid JSON object representing a verification plan based on the provided findings and target. Do not output markdown code blocks like ```json, just the raw JSON object."));
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

    private String buildPrompt(VerificationTarget target, List<Finding> findings, List<SecurityTool> applicableTools) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please create a verification plan to pentest the following target based on static analysis findings.\n\n");
        
        sb.append("TARGET:\n");
        sb.append("- URL: ").append(target.getTargetUrl()).append("\n");
        sb.append("- Tech Stack: ").append(target.getDetectedTechnology() != null ? target.getDetectedTechnology() : "Unknown").append("\n\n");
        
        if (findings == null || findings.isEmpty()) {
            sb.append("FINDINGS: None provided. Please perform a general blind dynamic vulnerability assessment using the available tools.\n\n");
        } else {
            sb.append("FINDINGS:\n");
            for (Finding f : findings) {
                sb.append("- [").append(f.getId()).append("] ").append(f.getSeverity()).append(": ").append(f.getTitle()).append("\n");
            }
            sb.append("\n");
        }
        
        sb.append("AVAILABLE TOOLS:\n");
        for (SecurityTool t : applicableTools) {
            sb.append("- ").append(t.getName()).append(": ").append(t.getDescription()).append("\n");
        }
        sb.append("\n");
        
        sb.append("All tools will run inside a single shared Kali Linux Docker container, so they can share files in /workspace.\n");
        
        sb.append("OUTPUT FORMAT:\n");
        sb.append("Return ONLY a JSON object with the following structure:\n");
        sb.append("{\n");
        sb.append("  \"estimatedTotalDuration\": \"10m\",\n");
        sb.append("  \"aiReasoningForExclusions\": \"Why certain tools were not selected...\",\n");
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
        sb.append("      \"associatedFindingIds\": [\"1\", \"2\"]\n");
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
