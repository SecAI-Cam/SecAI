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
@Command(name = "explain", description = "Explain a specific vulnerability finding and show an attack scenario.")
public class ExplainCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The ID of the finding to explain (e.g. 1)")
    private String findingId;
    
    @Option(names = {"-p", "--path"}, description = "Project path", defaultValue = ".")
    private String projectPath;

    private final AIEngine aiEngine;
    private final ReportManager reportManager;

    @Autowired
    public ExplainCommand(AIEngine aiEngine, ReportManager reportManager) {
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
        System.out.println("\nAnalyzing Finding [" + finding.getId() + "]: " + finding.getTitle() + "...\n");
        
        aiEngine.analyzeFindings(List.of(finding));
        
        System.out.println("\n--- AI Explanation ---");
        System.out.println(finding.getAiExplanation());
        System.out.println("\n--- Attack Scenario ---");
        System.out.println(finding.getAttackScenario());
        
        return 0;
    }
}
