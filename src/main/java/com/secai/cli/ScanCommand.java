package com.secai.cli;

import com.secai.scanner.ScannerEngine;
import com.secai.model.Finding;
import com.secai.report.ReportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "scan", description = "Scan a project for security vulnerabilities.")
public class ScanCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The path to the project to scan")
    private String projectPath;

    private final ScannerEngine scannerEngine;
    private final ReportManager reportManager;

    @Autowired
    public ScanCommand(ScannerEngine scannerEngine, ReportManager reportManager) {
        this.scannerEngine = scannerEngine;
        this.reportManager = reportManager;
    }

    @Override
    public Integer call() {
        System.out.println("Scanning project at: " + projectPath);
        
        List<Finding> findings = scannerEngine.runAllScanners(projectPath);
        
        // Assign short IDs
        int idCounter = 1;
        for (Finding finding : findings) {
            finding.setId(String.valueOf(idCounter++));
        }
        
        reportManager.saveLatestScan(projectPath, findings);
        
        System.out.println("\nScan Complete!");
        System.out.println("Found " + findings.size() + " total issues.");
        
        if (!findings.isEmpty()) {
            System.out.println("\n--- Findings Summary ---");
            for (Finding finding : findings) {
                System.out.printf("[%s] %s (%s) - %s\n", 
                        finding.getId(), finding.getTitle(), finding.getSeverity(), finding.getFile());
            }
            System.out.println("\nRun 'secai explain <id>' or 'secai fix <id>' for AI analysis.");
        }
        
        return 0;
    }
}
