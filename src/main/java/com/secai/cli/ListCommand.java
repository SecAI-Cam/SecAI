package com.secai.cli;

import com.secai.model.Finding;
import com.secai.report.ReportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "list", description = "List findings from the most recent scan.")
public class ListCommand implements Callable<Integer> {

    @Option(names = {"-p", "--path"}, description = "Project path to read scan from", defaultValue = ".")
    private String projectPath;

    private final ReportManager reportManager;

    @Autowired
    public ListCommand(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    @Override
    public Integer call() {
        List<Finding> findings = reportManager.loadLatestScan(projectPath);
        
        if (findings == null || findings.isEmpty()) {
            // reportManager already prints a warning if file doesn't exist
            return 1;
        }

        System.out.println("\n--- Findings Summary ---");
        for (Finding finding : findings) {
            System.out.printf("[%s] %s (%s) - %s\n", 
                    finding.getId(), finding.getTitle(), finding.getSeverity(), finding.getFile());
        }
        System.out.println("\nRun 'secai chat <id>' or 'secai fix <id>' for AI analysis.");
        
        return 0;
    }
}
