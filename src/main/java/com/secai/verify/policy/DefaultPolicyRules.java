package com.secai.verify.policy;

import com.secai.verify.model.ExecutionMode;
import java.util.List;
import java.util.ArrayList;
import java.util.EnumSet;

public class DefaultPolicyRules {

    public static List<PolicyRule> getRules() {
        List<PolicyRule> rules = new ArrayList<>();

        // --- BLOCKLIST (Checked first) ---
        rules.add(new PolicyRule(".*rm\\s+-rf.*", PolicyRule.Action.BLOCK, "Destructive filesystem operation"));
        rules.add(new PolicyRule(".*(shutdown|reboot|halt|poweroff).*", PolicyRule.Action.BLOCK, "System shutdown command"));
        rules.add(new PolicyRule(".*docker.*", PolicyRule.Action.BLOCK, "Docker escape risk"));
        rules.add(new PolicyRule(".*(iptables|ip6tables|ufw).*", PolicyRule.Action.BLOCK, "Firewall manipulation"));
        rules.add(new PolicyRule(".*(mount|umount).*", PolicyRule.Action.BLOCK, "Filesystem mount"));
        rules.add(new PolicyRule(".*(chmod\\s+777|chown\\s+root).*", PolicyRule.Action.BLOCK, "Permission escalation"));
        rules.add(new PolicyRule(".*(nc|netcat)\\s+-e.*", PolicyRule.Action.BLOCK, "Reverse shell via netcat"));
        rules.add(new PolicyRule(".*>/dev/tcp/.*", PolicyRule.Action.BLOCK, "Bash reverse shell"));

        // --- REQUIRE APPROVAL (Risky but potentially useful for exploits) ---
        rules.add(new PolicyRule(".*(curl|wget).*((?!http(s)?://127.0.0.1|http(s)?://localhost).)*", PolicyRule.Action.REQUIRE_APPROVAL, "External network request via curl/wget"));
        rules.add(new PolicyRule(".*msfconsole.*exploit.*", PolicyRule.Action.REQUIRE_APPROVAL, "Metasploit exploit execution", EnumSet.of(ExecutionMode.EXPLOIT)));

        // --- ALLOWLIST (Safe verification commands) ---
        rules.add(new PolicyRule("^nmap\\s+(-sV|-O|--script=).*$", PolicyRule.Action.ALLOW, "Nmap service detection/script scan"));
        rules.add(new PolicyRule("^nuclei\\s+-u\\s+.*", PolicyRule.Action.ALLOW, "Nuclei template scan"));
        rules.add(new PolicyRule("^sqlmap\\s+.*--batch.*$", PolicyRule.Action.ALLOW, "SQLMap automated check"));
        rules.add(new PolicyRule("^nikto\\s+-h\\s+.*", PolicyRule.Action.ALLOW, "Nikto web assessment"));
        rules.add(new PolicyRule("^ffuf\\s+-u\\s+.*", PolicyRule.Action.ALLOW, "Ffuf endpoint discovery"));
        rules.add(new PolicyRule("^msfconsole.*check.*", PolicyRule.Action.ALLOW, "Metasploit check module"));
        
        // Basic harmless commands inside sandbox
        rules.add(new PolicyRule("^(ls|cat|echo|grep|awk|sed|head|tail|wc)\\s+.*", PolicyRule.Action.ALLOW, "Basic safe utility command"));

        return rules;
    }
}
