package com.secai.report.exporter;

import com.secai.model.Finding;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HtmlExporter implements ReportExporter {

    @Override
    public String export(List<Finding> findings) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("    <meta charset=\"UTF-8\">\n");
        sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("    <title>SecAI Vulnerability Report</title>\n");
        sb.append("    <style>\n");
        sb.append("        :root {\n");
        sb.append("            --bg-color: #0f172a;\n");
        sb.append("            --card-bg: rgba(30, 41, 59, 0.7);\n");
        sb.append("            --text-main: #f8fafc;\n");
        sb.append("            --text-muted: #94a3b8;\n");
        sb.append("            --accent: #3b82f6;\n");
        sb.append("            --accent-glow: rgba(59, 130, 246, 0.5);\n");
        sb.append("            --high: #ef4444;\n");
        sb.append("            --medium: #f59e0b;\n");
        sb.append("            --low: #3b82f6;\n");
        sb.append("        }\n");
        sb.append("        body {\n");
        sb.append("            margin: 0;\n");
        sb.append("            padding: 2rem;\n");
        sb.append("            background-color: var(--bg-color);\n");
        sb.append("            color: var(--text-main);\n");
        sb.append("            font-family: 'Inter', -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;\n");
        sb.append("            line-height: 1.6;\n");
        sb.append("        }\n");
        sb.append("        .container {\n");
        sb.append("            max-width: 1000px;\n");
        sb.append("            margin: 0 auto;\n");
        sb.append("        }\n");
        sb.append("        header {\n");
        sb.append("            text-align: center;\n");
        sb.append("            margin-bottom: 3rem;\n");
        sb.append("        }\n");
        sb.append("        h1 {\n");
        sb.append("            font-size: 3rem;\n");
        sb.append("            font-weight: 800;\n");
        sb.append("            margin-bottom: 0.5rem;\n");
        sb.append("            background: linear-gradient(to right, #3b82f6, #8b5cf6);\n");
        sb.append("            -webkit-background-clip: text;\n");
        sb.append("            -webkit-text-fill-color: transparent;\n");
        sb.append("        }\n");
        sb.append("        .stats {\n");
        sb.append("            font-size: 1.2rem;\n");
        sb.append("            color: var(--text-muted);\n");
        sb.append("        }\n");
        sb.append("        .finding-card {\n");
        sb.append("            background: var(--card-bg);\n");
        sb.append("            border: 1px solid rgba(255, 255, 255, 0.1);\n");
        sb.append("            border-radius: 12px;\n");
        sb.append("            padding: 2rem;\n");
        sb.append("            margin-bottom: 2rem;\n");
        sb.append("            backdrop-filter: blur(10px);\n");
        sb.append("            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n");
        sb.append("            transition: transform 0.2s, box-shadow 0.2s;\n");
        sb.append("        }\n");
        sb.append("        .finding-card:hover {\n");
        sb.append("            transform: translateY(-5px);\n");
        sb.append("            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);\n");
        sb.append("            border-color: rgba(255, 255, 255, 0.2);\n");
        sb.append("        }\n");
        sb.append("        .finding-header {\n");
        sb.append("            display: flex;\n");
        sb.append("            justify-content: space-between;\n");
        sb.append("            align-items: center;\n");
        sb.append("            border-bottom: 1px solid rgba(255, 255, 255, 0.1);\n");
        sb.append("            padding-bottom: 1rem;\n");
        sb.append("            margin-bottom: 1.5rem;\n");
        sb.append("        }\n");
        sb.append("        .finding-title {\n");
        sb.append("            font-size: 1.5rem;\n");
        sb.append("            font-weight: 700;\n");
        sb.append("            margin: 0;\n");
        sb.append("        }\n");
        sb.append("        .badge {\n");
        sb.append("            padding: 0.4rem 0.8rem;\n");
        sb.append("            border-radius: 9999px;\n");
        sb.append("            font-size: 0.875rem;\n");
        sb.append("            font-weight: 600;\n");
        sb.append("            text-transform: uppercase;\n");
        sb.append("        }\n");
        sb.append("        .severity-high { background-color: rgba(239, 68, 68, 0.2); color: #fca5a5; border: 1px solid rgba(239, 68, 68, 0.5); }\n");
        sb.append("        .severity-medium { background-color: rgba(245, 158, 11, 0.2); color: #fcd34d; border: 1px solid rgba(245, 158, 11, 0.5); }\n");
        sb.append("        .severity-low { background-color: rgba(59, 130, 246, 0.2); color: #93c5fd; border: 1px solid rgba(59, 130, 246, 0.5); }\n");
        sb.append("        .meta {\n");
        sb.append("            font-family: monospace;\n");
        sb.append("            color: var(--text-muted);\n");
        sb.append("            background: rgba(0, 0, 0, 0.3);\n");
        sb.append("            padding: 0.5rem;\n");
        sb.append("            border-radius: 6px;\n");
        sb.append("            margin-bottom: 1.5rem;\n");
        sb.append("        }\n");
        sb.append("        .section-title {\n");
        sb.append("            font-size: 1.1rem;\n");
        sb.append("            color: var(--accent);\n");
        sb.append("            margin-top: 1.5rem;\n");
        sb.append("            margin-bottom: 0.5rem;\n");
        sb.append("            font-weight: 600;\n");
        sb.append("        }\n");
        sb.append("        pre {\n");
        sb.append("            background: #000;\n");
        sb.append("            padding: 1rem;\n");
        sb.append("            border-radius: 8px;\n");
        sb.append("            overflow-x: auto;\n");
        sb.append("            border: 1px solid rgba(255, 255, 255, 0.05);\n");
        sb.append("            color: #e2e8f0;\n");
        sb.append("        }\n");
        sb.append("    </style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("    <div class=\"container\">\n");
        sb.append("        <header>\n");
        sb.append("            <h1>SecAI Report</h1>\n");
        sb.append("            <div class=\"stats\">Total Findings: <strong>").append(findings.size()).append("</strong></div>\n");
        sb.append("        </header>\n");
        sb.append("        <main>\n");

        for (Finding finding : findings) {
            sb.append("            <div class=\"finding-card\">\n");
            sb.append("                <div class=\"finding-header\">\n");
            sb.append("                    <h2 class=\"finding-title\">[").append(finding.getId()).append("] ").append(escapeHtml(finding.getTitle())).append("</h2>\n");
            
            String sevClass = "severity-low";
            String sev = finding.getSeverity() != null ? finding.getSeverity().toLowerCase() : "";
            if (sev.contains("high") || sev.contains("critical")) sevClass = "severity-high";
            else if (sev.contains("medium")) sevClass = "severity-medium";
            
            sb.append("                    <span class=\"badge ").append(sevClass).append("\">").append(escapeHtml(finding.getSeverity())).append("</span>\n");
            sb.append("                </div>\n");
            
            sb.append("                <div class=\"meta\">\n");
            sb.append("                    <strong>Scanner:</strong> ").append(escapeHtml(finding.getScannerName())).append(" | \n");
            sb.append("                    <strong>File:</strong> ").append(escapeHtml(finding.getFile() != null ? finding.getFile() : "N/A")).append("\n");
            sb.append("                </div>\n");
            
            sb.append("                <div class=\"section-title\">Description</div>\n");
            sb.append("                <p>").append(escapeHtml(finding.getDescription())).append("</p>\n");
            
            if (finding.getAiExplanation() != null && !finding.getAiExplanation().isEmpty()) {
                sb.append("                <div class=\"section-title\">✨ AI Explanation</div>\n");
                sb.append("                <p>").append(escapeHtml(finding.getAiExplanation())).append("</p>\n");
            }
            
            if (finding.getAttackScenario() != null && !finding.getAttackScenario().isEmpty()) {
                sb.append("                <div class=\"section-title\">⚔️ Attack Scenario</div>\n");
                sb.append("                <p>").append(escapeHtml(finding.getAttackScenario())).append("</p>\n");
            }
            
            if (finding.getAiRemediation() != null && !finding.getAiRemediation().isEmpty()) {
                sb.append("                <div class=\"section-title\">🛡️ Remediation</div>\n");
                sb.append("                <p>").append(escapeHtml(finding.getAiRemediation())).append("</p>\n");
            }
            
            if (finding.getSecureCodeExample() != null && !finding.getSecureCodeExample().isEmpty()) {
                sb.append("                <div class=\"section-title\">💻 Secure Code Example</div>\n");
                sb.append("                <pre><code>").append(escapeHtml(finding.getSecureCodeExample())).append("</code></pre>\n");
            }
            
            sb.append("            </div>\n");
        }

        sb.append("        </main>\n");
        sb.append("    </div>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    @Override
    public String getExtension() {
        return ".html";
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
