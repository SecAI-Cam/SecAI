package com.secai.verify;

import com.secai.model.Finding;
import com.secai.verify.ai.EvidenceAnalyzer;
import com.secai.verify.ai.ReportGenerator;
import com.secai.verify.ai.VerificationPlanner;
import com.secai.verify.model.*;
import com.secai.verify.policy.PolicyDecision;
import com.secai.verify.policy.PolicyEngine;
import com.secai.verify.sandbox.KaliSandbox;
import com.secai.verify.sandbox.SandboxConfig;
import com.secai.verify.sandbox.SandboxResult;
import com.secai.verify.tool.SecurityTool;
import com.secai.verify.tool.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationEngine {
    private static final Logger logger = LoggerFactory.getLogger(VerificationEngine.class);

    private final VerificationPlanner planner;
    private final ApprovalManager approvalManager;
    private final PolicyEngine policyEngine;
    private final KaliSandbox sandbox;
    private final ToolRegistry toolRegistry;
    private final EvidenceAnalyzer analyzer;
    private final ReportGenerator reportGenerator;
    private final VerificationLogger auditLogger;

    @Autowired
    public VerificationEngine(VerificationPlanner planner, ApprovalManager approvalManager, 
                              PolicyEngine policyEngine, KaliSandbox sandbox, ToolRegistry toolRegistry, 
                              EvidenceAnalyzer analyzer, ReportGenerator reportGenerator, 
                              VerificationLogger auditLogger) {
        this.planner = planner;
        this.approvalManager = approvalManager;
        this.policyEngine = policyEngine;
        this.sandbox = sandbox;
        this.toolRegistry = toolRegistry;
        this.analyzer = analyzer;
        this.reportGenerator = reportGenerator;
        this.auditLogger = auditLogger;
    }

    public void runVerification(String projectPath, String targetUrl, List<Finding> findings, SandboxConfig config, boolean planOnly) {
        VerificationTarget target = new VerificationTarget();
        target.setProjectPath(projectPath);
        target.setTargetUrl(targetUrl);
        // Normally AI would fingerprint this here
        target.setDetectedTechnology("Unknown"); 
        
        System.out.println("Planning verification for " + targetUrl + " based on " + findings.size() + " findings...");
        
        if (findings.isEmpty()) {
            Finding dummy = new Finding();
            dummy.setId("BLIND-01");
            dummy.setTitle("General Vulnerability Discovery");
            dummy.setDescription("Blind dynamic assessment without prior static findings.");
            dummy.setSeverity("UNKNOWN");
            findings.add(dummy);
        }
        
        if (planOnly) {
            System.out.println("Generating initial steps for all findings...");
            VerificationPlan masterPlan = new VerificationPlan();
            masterPlan.setTarget(target);
            masterPlan.setSteps(new ArrayList<>());
            for (Finding f : findings) {
                VerificationPlan partial = planner.createNextSteps(target, f, new ArrayList<>());
                if (partial.getSteps() != null) {
                    masterPlan.getSteps().addAll(partial.getSteps());
                }
            }
            approvalManager.displayPlan(masterPlan);
            return;
        }
        
        System.out.println("\nInitializing Kali Sandbox environment...");
        sandbox.buildImageIfNeeded(projectPath);
        
        try {
            sandbox.start(projectPath, targetUrl, config);
            
            VerificationReport report = new VerificationReport();
            report.setTarget(target);
            report.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(new Date()));
            
            List<VerificationResult> finalResults = new ArrayList<>();

            for (Finding finding : findings) {
                System.out.println("\n\033[36mVerifying Finding: " + finding.getTitle() + "\033[0m");
                
                List<VerificationEvidence> findingEvidence = new ArrayList<>();
                boolean isFindingComplete = false;
                
                while (!isFindingComplete) {
                    VerificationPlan nextPlan = planner.createNextSteps(target, finding, findingEvidence);
                    
                    if (nextPlan.getSteps() == null || nextPlan.getSteps().isEmpty()) {
                        System.out.println("\033[32mAI determined no further steps are needed for this finding.\033[0m");
                        isFindingComplete = true;
                        continue;
                    }
                    
                    approvalManager.displayPlan(nextPlan);
                    boolean proceed = approvalManager.requestApproval(nextPlan);
                    if (!proceed) {
                        System.out.println("Step rejected by user. Moving to analysis.");
                        isFindingComplete = true;
                        continue;
                    }
                    
                    for (VerificationStep step : nextPlan.getSteps()) {
                        if (step.getApprovalStatus() != ApprovalStatus.APPROVED) continue;

                        Optional<SecurityTool> toolOpt = toolRegistry.getToolByName(step.getToolName());
                        if (toolOpt.isEmpty()) {
                            logger.error("Tool {} not found in registry", step.getToolName());
                            continue;
                        }
                        SecurityTool tool = toolOpt.get();

                        List<String> command = tool.buildCommands(step, target);
                        String cmdString = String.join(" ", command);
                        
                        PolicyDecision decision = policyEngine.validate(cmdString, step.getMode());
                        if (decision.isBlocked()) {
                            System.out.println("\033[31mBLOCKED by Policy:\033[0m " + decision.getReason());
                            continue;
                        }
                        
                        System.out.println("Executing " + tool.getName() + "...");
                        SandboxResult result = sandbox.execute(command, config.getToolTimeoutMs());
                        
                        auditLogger.logCommand(projectPath, tool.getName(), cmdString, result.getExecutionDurationMs(), 
                                               result.getExitCode(), step.getApprovalId(), result.getCombinedOutput());
                        
                        VerificationEvidence evidence = tool.parseOutput(result.getCombinedOutput(), result.getExitCode(), result.getExecutionDurationMs());
                        evidence.setCommandExecuted(cmdString);
                        evidence.setTimestamp(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        findingEvidence.add(evidence);
                    }
                }

                if (!findingEvidence.isEmpty()) {
                    System.out.println("Analyzing evidence for " + finding.getTitle() + "...");
                    VerificationResult vResult = analyzer.analyze(finding, new ArrayList<>(), findingEvidence);
                    finalResults.add(vResult);
                    
                    String color = vResult.getVerificationStatus() == VerificationResult.Status.CONFIRMED ? "\033[31m" : "\033[32m";
                    System.out.println(color + "Result: " + vResult.getVerificationStatus() + "\033[0m (Risk Score: " + vResult.getRiskScore() + ")");
                }
            }

            report.setResults(finalResults);
            
            // Calculate overall risk
            int maxRisk = 0;
            for (VerificationResult r : finalResults) {
                if (r.getRiskScore() > maxRisk) maxRisk = r.getRiskScore();
            }
            report.setOverallRiskScore(maxRisk);
            
            System.out.println("\nGenerating final report...");
            reportGenerator.generateExecutiveSummary(report);
            String mdReport = reportGenerator.renderMarkdown(report);
            
            String outPath = Paths.get(projectPath, ".secai", "verify", "pentest-report.md").toString();
            Files.createDirectories(Paths.get(outPath).getParent());
            Files.writeString(Paths.get(outPath), mdReport);
            
            // Copy raw evidence out of the container
            sandbox.copyEvidence(Paths.get(projectPath, ".secai", "verify", "raw_evidence").toString());
            
            System.out.println("\033[32mVerification complete! Report saved to: " + outPath + "\033[0m");

        } catch (Exception e) {
            logger.error("Verification failed: {}", e.getMessage(), e);
            System.out.println("\033[31mVerification failed: " + e.getMessage() + "\033[0m");
        } finally {
            sandbox.stop();
        }
    }
}
