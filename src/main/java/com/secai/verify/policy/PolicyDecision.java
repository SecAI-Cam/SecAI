package com.secai.verify.policy;

public class PolicyDecision {
    private final PolicyRule.Action action;
    private final String reason;
    private final PolicyRule matchedRule;

    public PolicyDecision(PolicyRule.Action action, String reason, PolicyRule matchedRule) {
        this.action = action;
        this.reason = reason;
        this.matchedRule = matchedRule;
    }

    public PolicyRule.Action getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }

    public PolicyRule getMatchedRule() {
        return matchedRule;
    }
    
    public boolean isAllowed() {
        return action == PolicyRule.Action.ALLOW;
    }
    
    public boolean isBlocked() {
        return action == PolicyRule.Action.BLOCK;
    }
    
    public boolean requiresApproval() {
        return action == PolicyRule.Action.REQUIRE_APPROVAL;
    }
}
