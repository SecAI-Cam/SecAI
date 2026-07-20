package com.secai.verify.ai;

import com.secai.ai.AIEngine;
import com.secai.model.ChatMessage;
import com.secai.verify.model.VerificationReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportGenerator {

    private final AIEngine aiEngine;

    @Autowired
    public ReportGenerator(AIEngine aiEngine) {
        this.aiEngine = aiEngine;
    }

    public void generateExecutiveSummary(VerificationReport report) {
        String prompt = "Generate a professional Executive Summary and Remediation Roadmap for a Penetration Testing Report.\n\n" +
                "Target: " + report.getTarget().getTargetUrl() + "\n" +
                "Overall Risk Score: " + report.getOverallRiskScore() + "/100\n" +
                "Total Findings Confirmed: " + report.getResults().size() + "\n\n" +
                "Output ONLY the markdown content, starting with '## Executive Summary'.";

        List<ChatMessage> history = new ArrayList<>();
        history.add(new ChatMessage("user", prompt));

        String response = aiEngine.chat(history);
        
        // Split response into sections if possible, else just dump into summary
        report.setExecutiveSummary(response);
        report.setMethodology("This assessment was performed using SecAI v2 Autonomous Pentest Verification Engine, orchestrating industry-standard tools (Nmap, Nuclei, SQLMap, Nikto, Metasploit) within a controlled, isolated Kali Linux sandbox.");
    }
    
    public String renderMarkdown(VerificationReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Penetration Testing Verification Report\n\n");
        sb.append("**Target:** ").append(report.getTarget().getTargetUrl()).append("\n");
        sb.append("**Date:** ").append(report.getTimestamp()).append("\n");
        sb.append("**Overall Risk Score:** ").append(report.getOverallRiskScore()).append("/100\n\n");
        
        sb.append(report.getExecutiveSummary()).append("\n\n");
        
        sb.append("## Methodology\n").append(report.getMethodology()).append("\n\n");
        
        sb.append("## Detailed Findings\n\n");
        for (var result : report.getResults()) {
            sb.append("### Finding [").append(result.getFindingReferenceId()).append("]\n");
            sb.append("- **Status:** ").append(result.getVerificationStatus()).append("\n");
            sb.append("- **Risk Score:** ").append(result.getRiskScore()).append("\n");
            sb.append("- **CVSS:** ").append(result.getCvssString()).append("\n\n");
            
            sb.append("#### Business Impact\n").append(result.getBusinessImpactAssessment()).append("\n\n");
            
            sb.append("#### Evidence\n");
            for (var ev : result.getEvidence()) {
                sb.append("**Command:** `").append(ev.getCommandExecuted()).append("`\n");
                sb.append("```\n").append(ev.getRawOutput()).append("\n```\n\n");
            }
        }
        
        return sb.toString();
    }
}
