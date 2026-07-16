package com.secai.scanner;

import com.secai.model.Finding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScannerEngine {
    private static final Logger logger = LoggerFactory.getLogger(ScannerEngine.class);
    private final List<ScannerProvider> providers;

    @Autowired
    public ScannerEngine(List<ScannerProvider> providers) {
        this.providers = providers;
    }

    public List<ScannerProvider> getProviders() {
        return providers;
    }

    public List<Finding> runAllScanners(String projectPath) {
        List<Finding> allFindings = new ArrayList<>();
        for (ScannerProvider provider : providers) {
            if (provider.isAvailable()) {
                logger.info("Running scanner: {}", provider.getName());
                try {
                    List<Finding> findings = provider.scan(projectPath);
                    allFindings.addAll(findings);
                    logger.info("Scanner {} found {} issues.", provider.getName(), findings.size());
                } catch (Exception e) {
                    logger.error("Error running scanner {}: {}", provider.getName(), e.getMessage());
                }
            } else {
                logger.warn("Scanner {} is not available. Skipping.", provider.getName());
            }
        }
        return allFindings;
    }

    public void updateAllRules() {
        logger.info("Updating all scanner rules and databases...");
        for (ScannerProvider provider : providers) {
            if (provider.isAvailable()) {
                logger.info("Updating rules for scanner: {}", provider.getName());
                try {
                    provider.updateRules();
                } catch (Exception e) {
                    logger.error("Error updating scanner {}: {}", provider.getName(), e.getMessage());
                }
            } else {
                logger.warn("Scanner {} is not available. Skipping update.", provider.getName());
            }
        }
    }
}
