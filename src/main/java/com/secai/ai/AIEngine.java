package com.secai.ai;

import com.secai.model.Finding;
import com.secai.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.secai.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIEngine {
    private static final Logger logger = LoggerFactory.getLogger(AIEngine.class);
    private AIProvider activeProvider;

    @Autowired
    public AIEngine(AppConfig appConfig, List<AIProvider> providers) {
        String configuredProvider = appConfig.getProvider();
        if (configuredProvider != null) {
            for (AIProvider provider : providers) {
                if (provider.getName().equalsIgnoreCase(configuredProvider)) {
                    this.activeProvider = provider;
                    break;
                }
            }
        }
        if (this.activeProvider == null && !providers.isEmpty()) {
            this.activeProvider = providers.get(0); // fallback
            logger.warn("Configured AI provider '{}' not found. Falling back to {}.", configuredProvider, activeProvider.getName());
        }
    }

    public void analyzeFindings(List<Finding> findings) {
        if (activeProvider == null) {
            logger.warn("No AI provider configured. Skipping AI analysis.");
            return;
        }

        logger.info("Starting AI analysis using provider: {}", activeProvider.getName());
        for (Finding finding : findings) {
            try {
                activeProvider.analyzeFinding(finding);
                logger.debug("Successfully analyzed finding: {}", finding.getTitle());
            } catch (Exception e) {
                logger.error("Failed to analyze finding {}: {}", finding.getTitle(), e.getMessage());
            }
        }
    }

    public String chat(List<ChatMessage> history) {
        if (activeProvider == null) {
            return "Error: No AI provider configured.";
        }
        return activeProvider.chat(history);
    }
}
