package com.secai.verify;

import com.secai.verify.model.ApprovalStatus;
import com.secai.verify.model.VerificationPlan;
import com.secai.verify.model.VerificationStep;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.UUID;

@Service
public class ApprovalManager {

    public void displayPlan(VerificationPlan plan) {
        System.out.println("\n\033[36m============================================================\033[0m");
        System.out.println("\033[1;36m              AI VERIFICATION PLAN              \033[0m");
        System.out.println("\033[36m============================================================\033[0m\n");
        
        System.out.println("Target: " + plan.getTarget().getTargetUrl());
        System.out.println("Estimated Duration: " + plan.getEstimatedTotalDuration() + "\n");
        
        if (plan.getAiReasoningForExclusions() != null && !plan.getAiReasoningForExclusions().isEmpty()) {
            System.out.println("\033[33mAI Note:\033[0m " + plan.getAiReasoningForExclusions() + "\n");
        }
        
        System.out.println("Proposed Steps:");
        for (int i = 0; i < plan.getSteps().size(); i++) {
            VerificationStep step = plan.getSteps().get(i);
            String modeColor = step.getMode().name().equals("VERIFY") ? "\033[32m" : "\033[31m";
            
            System.out.println("  " + (i + 1) + ". [" + modeColor + step.getMode() + "\033[0m] " + step.getToolName());
            System.out.println("     Command: " + step.getCommandTemplate());
            System.out.println("     Purpose: " + step.getPurpose());
            System.out.println("     Confidence: " + step.getConfidenceScore() + "% | Est: " + step.getEstimatedDuration());
            System.out.println();
        }
    }

    public boolean requestApproval(VerificationPlan plan) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("\033[1;33mApprove execution of this plan? [Y]es all / [N]o / [I]ndividual steps: \033[0m");
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("y") || input.equals("yes")) {
                String approvalId = UUID.randomUUID().toString();
                plan.setPlanApprovalStatus(ApprovalStatus.APPROVED);
                for (VerificationStep step : plan.getSteps()) {
                    step.setApprovalStatus(ApprovalStatus.APPROVED);
                    step.setApprovalId(approvalId);
                }
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                plan.setPlanApprovalStatus(ApprovalStatus.REJECTED);
                return false;
            } else if (input.equals("i") || input.equals("individual")) {
                boolean anyApproved = false;
                for (int i = 0; i < plan.getSteps().size(); i++) {
                    VerificationStep step = plan.getSteps().get(i);
                    System.out.print("\033[36mApprove step " + (i+1) + " (" + step.getToolName() + ")? [y/N]: \033[0m");
                    String stepInput = scanner.nextLine().trim().toLowerCase();
                    if (stepInput.equals("y") || stepInput.equals("yes")) {
                        step.setApprovalStatus(ApprovalStatus.APPROVED);
                        step.setApprovalId(UUID.randomUUID().toString());
                        anyApproved = true;
                    } else {
                        step.setApprovalStatus(ApprovalStatus.REJECTED);
                    }
                }
                plan.setPlanApprovalStatus(anyApproved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
                return anyApproved;
            }
        }
    }
}
