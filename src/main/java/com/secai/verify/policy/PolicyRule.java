package com.secai.verify.policy;

import com.secai.verify.model.ExecutionMode;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.EnumSet;

public class PolicyRule {
    public enum Action {
        ALLOW,
        BLOCK,
        REQUIRE_APPROVAL
    }

    private final Pattern pattern;
    private final Action action;
    private final String description;
    private final Set<ExecutionMode> applicableModes;

    public PolicyRule(String regex, Action action, String description, Set<ExecutionMode> applicableModes) {
        this.pattern = Pattern.compile(regex);
        this.action = action;
        this.description = description;
        this.applicableModes = applicableModes != null ? applicableModes : EnumSet.allOf(ExecutionMode.class);
    }
    
    public PolicyRule(String regex, Action action, String description) {
        this(regex, action, description, EnumSet.allOf(ExecutionMode.class));
    }

    public boolean matches(String command, ExecutionMode mode) {
        if (!applicableModes.contains(mode)) {
            return false;
        }
        return pattern.matcher(command).matches() || pattern.matcher(command).find();
    }

    public Action getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }
    
    public String getRegex() {
        return pattern.pattern();
    }
}
