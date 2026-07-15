package com.secai.report.exporter;

import com.secai.model.Finding;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarkdownExporter implements ReportExporter {

    @Override
    public String export(List<Finding> findings) {
        StringBuilder sb = new StringBuilder();
        sb.append("# SecAI Vulnerability Report\n\n");
        sb.append("Total Findings: **").append(findings.size()).append("**\n\n");
        sb.append("---\n\n");

        for (Finding finding : findings) {
            sb.append("## [").append(finding.getId()).append("] ").append(finding.getTitle()).append("\n\n");
            sb.append("- **Scanner**: ").append(finding.getScannerName()).append("\n");
            sb.append("- **Severity**: ").append(finding.getSeverity()).append("\n");
            sb.append("- **File**: `").append(finding.getFile() != null ? finding.getFile() : "N/A").append("`\n\n");
            
            sb.append("### Description\n");
            sb.append(finding.getDescription()).append("\n\n");
            
            if (finding.getAiExplanation() != null && !finding.getAiExplanation().isEmpty()) {
                sb.append("### AI Explanation\n");
                sb.append(finding.getAiExplanation()).append("\n\n");
            }
            
            if (finding.getAttackScenario() != null && !finding.getAttackScenario().isEmpty()) {
                sb.append("### Attack Scenario\n");
                sb.append(finding.getAttackScenario()).append("\n\n");
            }
            
            if (finding.getAiRemediation() != null && !finding.getAiRemediation().isEmpty()) {
                sb.append("### Remediation\n");
                sb.append(finding.getAiRemediation()).append("\n\n");
            }
            
            if (finding.getSecureCodeExample() != null && !finding.getSecureCodeExample().isEmpty()) {
                sb.append("### Secure Code Example\n");
                sb.append("```\n").append(finding.getSecureCodeExample()).append("\n```\n\n");
            }
            
            sb.append("---\n\n");
        }

        return sb.toString();
    }

    @Override
    public String getExtension() {
        return ".md";
    }
}
