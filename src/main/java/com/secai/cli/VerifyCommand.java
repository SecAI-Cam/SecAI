package com.secai.cli;

import com.secai.config.AppConfig;
import com.secai.model.Finding;
import com.secai.report.ReportManager;
import com.secai.verify.VerificationEngine;
import com.secai.verify.sandbox.KaliSandbox;
import com.secai.verify.sandbox.SandboxConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "verify", description = "Autonomously verify static findings against a target URL using an isolated Kali Linux sandbox.")
public class VerifyCommand implements Callable<Integer> {

    @Autowired
    private VerificationEngine verificationEngine;

    @Autowired
    private KaliSandbox kaliSandbox;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ReportManager reportManager;

    @Parameters(index = "0", description = "The target URL to verify against (e.g., http://localhost:8080)", arity = "0..1")
    private String targetUrl;

    @Option(names = {"--setup"}, description = "Build the Kali Linux Docker sandbox image and exit")
    private boolean setupOnly;
    
    @Option(names = {"--plan-only"}, description = "Generate and display the verification plan, but do not execute it")
    private boolean planOnly;

    @Option(names = {"-d", "--dir"}, description = "The project directory containing findings to verify (defaults to current directory)")
    private String projectDir = ".";

    @Override
    public Integer call() {
        if (setupOnly) {
            System.out.println("Building Kali Linux sandbox image...");
            kaliSandbox.buildImageIfNeeded(projectDir);
            System.out.println("Setup complete.");
            return 0;
        }

        if (targetUrl == null || targetUrl.isEmpty()) {
            System.err.println("Error: Target URL is required unless --setup is used.");
            System.err.println("Usage: secai verify http://target-url.com");
            return 1;
        }

        System.out.println("Starting Autonomous Pentest Verification for: " + targetUrl);
        System.out.println("Loading latest static analysis findings from " + projectDir + "...");

        List<Finding> findings = reportManager.loadLatestScan(projectDir);
        if (findings == null || findings.isEmpty()) {
            System.out.println("\033[33mNo static findings found in the project. Proceeding with a blind dynamic assessment...\033[0m");
            findings = new java.util.ArrayList<>();
        }

        SandboxConfig sandboxConfig = appConfig.getSandbox();
        if (sandboxConfig == null) {
            sandboxConfig = new SandboxConfig(); // Use defaults
        }

        try {
            verificationEngine.runVerification(projectDir, targetUrl, findings, sandboxConfig, planOnly);
            return 0;
        } catch (Exception e) {
            System.err.println("Verification engine encountered a fatal error: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}
