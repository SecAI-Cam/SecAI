package com.secai.cli;

import com.secai.ai.AIEngine;
import com.secai.model.Finding;
import com.secai.report.ReportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Component
@Command(name = "fix", description = "Get remediation advice and a secure code example for a finding.")
public class FixCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The ID of the finding to fix (e.g. 1)")
    private String findingId;
    
    @Option(names = {"-p", "--path"}, description = "Project path", defaultValue = ".")
    private String projectPath;
    
    @Option(names = {"--apply"}, description = "Write the secure code example to a .fixed file")
    private boolean apply;

    private final AIEngine aiEngine;
    private final ReportManager reportManager;

    @Autowired
    public FixCommand(AIEngine aiEngine, ReportManager reportManager) {
        this.aiEngine = aiEngine;
        this.reportManager = reportManager;
    }

    @Override
    public Integer call() {
        System.out.println("Loading findings for project at: " + projectPath);
        
        List<Finding> findings = reportManager.loadLatestScan(projectPath);
        if (findings.isEmpty()) {
            return 1;
        }
        
        Optional<Finding> findingOpt = findings.stream()
                .filter(f -> f.getId().equals(findingId))
                .findFirst();
                
        if (findingOpt.isEmpty()) {
            System.out.println("Error: No finding found with ID " + findingId);
            return 1;
        }
        
        Finding finding = findingOpt.get();
        System.out.println("\nGenerating Fix for Finding [" + finding.getId() + "]: " + finding.getTitle() + "...\n");
        
        aiEngine.analyzeFindings(List.of(finding));
        
        System.out.println("\n--- AI Remediation ---");
        System.out.println(finding.getAiRemediation());
        System.out.println("\n--- Secure Code Example ---");
        System.out.println(finding.getSecureCodeExample());
        
        if (apply && finding.getSecureCodeExample() != null && !finding.getSecureCodeExample().isEmpty()) {
            if (finding.getFile() != null && !finding.getFile().isEmpty()) {
                try {
                    java.nio.file.Path targetPath = java.nio.file.Paths.get(projectPath, finding.getFile());
                    if (java.nio.file.Files.exists(targetPath)) {
                        java.nio.file.Path fixedPath = java.nio.file.Paths.get(projectPath, finding.getFile() + ".fixed");
                        java.nio.file.Files.writeString(fixedPath, finding.getSecureCodeExample());
                        System.out.println("\n[Self-Healing] Successfully wrote suggested fix to: " + fixedPath);
                        System.out.println("Please diff this file against the original and accept the changes in your IDE.");
                    } else {
                        System.out.println("\n[Self-Healing] Error: Original file not found at " + targetPath);
                    }
                } catch (Exception e) {
                    System.out.println("\n[Self-Healing] Error writing .fixed file: " + e.getMessage());
                }
            } else {
                System.out.println("\n[Self-Healing] Error: Finding does not contain a specific file path.");
            }
        }
        
        return 0;
    }
}
