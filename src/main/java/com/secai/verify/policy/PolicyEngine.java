package com.secai.verify.policy;

import com.secai.verify.model.ExecutionMode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyEngine {
    
    private final List<PolicyRule> rules;

    public PolicyEngine() {
        this.rules = DefaultPolicyRules.getRules();
    }

    public PolicyEngine(List<PolicyRule> customRules) {
        this.rules = customRules;
    }

    public PolicyDecision validate(String command, ExecutionMode mode) {
        if (command == null || command.trim().isEmpty()) {
            return new PolicyDecision(PolicyRule.Action.BLOCK, "Empty command", null);
        }

        // 1. Check for explicit blocks
        for (PolicyRule rule : rules) {
            if (rule.getAction() == PolicyRule.Action.BLOCK && rule.matches(command, mode)) {
                return new PolicyDecision(PolicyRule.Action.BLOCK, "Blocked by policy: " + rule.getDescription(), rule);
            }
        }

        // 2. Check for explicit allows
        for (PolicyRule rule : rules) {
            if (rule.getAction() == PolicyRule.Action.ALLOW && rule.matches(command, mode)) {
                return new PolicyDecision(PolicyRule.Action.ALLOW, "Allowed by policy: " + rule.getDescription(), rule);
            }
        }
        
        // 3. Check for explicit require approval
        for (PolicyRule rule : rules) {
            if (rule.getAction() == PolicyRule.Action.REQUIRE_APPROVAL && rule.matches(command, mode)) {
                return new PolicyDecision(PolicyRule.Action.REQUIRE_APPROVAL, "Requires user approval: " + rule.getDescription(), rule);
            }
        }

        // 4. Default fallthrough
        return new PolicyDecision(PolicyRule.Action.REQUIRE_APPROVAL, "No matching policy rule (default behavior)", null);
    }
}
