package com.secai.cli;

import com.secai.model.Finding;
import com.secai.report.ReportManager;
import com.secai.report.exporter.ReportExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "report", description = "Generate and export a security report.")
public class ReportCommand implements Callable<Integer> {

    @Option(names = {"-f", "--format"}, description = "Format to export (markdown, html)", defaultValue = "markdown")
    private String format;

    @Option(names = {"-o", "--output"}, description = "Output file path (optional)")
    private String outputPath;
    
    @Option(names = {"-p", "--path"}, description = "Project path to read scan from", defaultValue = ".")
    private String projectPath;

    private final ReportManager reportManager;
    private final List<ReportExporter> exporters;

    @Autowired
    public ReportCommand(ReportManager reportManager, List<ReportExporter> exporters) {
        this.reportManager = reportManager;
        this.exporters = exporters;
    }

    @Override
    public Integer call() {
        System.out.println("Generating security report in format: " + format);
        
        List<Finding> findings = reportManager.loadLatestScan(projectPath);
        if (findings.isEmpty()) {
            return 1;
        }

        ReportExporter selectedExporter = null;
        for (ReportExporter exporter : exporters) {
            if (exporter.getClass().getSimpleName().toLowerCase().startsWith(format.toLowerCase())) {
                selectedExporter = exporter;
                break;
            }
        }

        if (selectedExporter == null) {
            System.out.println("Error: Unsupported format '" + format + "'. Use 'markdown' or 'html'.");
            return 1;
        }

        String reportContent = selectedExporter.export(findings);
        
        String finalOutputPath = outputPath;
        if (finalOutputPath == null || finalOutputPath.isEmpty()) {
            finalOutputPath = Paths.get(projectPath, "secai-report" + selectedExporter.getExtension()).toString();
        }

        try {
            Path outPath = Paths.get(finalOutputPath);
            if (outPath.getParent() != null && !Files.exists(outPath.getParent())) {
                Files.createDirectories(outPath.getParent());
            }
            Files.writeString(outPath, reportContent);
            System.out.println("Report successfully generated: " + outPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing report to file: " + e.getMessage());
            return 1;
        }

        return 0;
    }
}

