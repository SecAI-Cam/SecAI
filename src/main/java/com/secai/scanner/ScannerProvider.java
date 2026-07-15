package com.secai.scanner;

import com.secai.model.Finding;
import java.util.List;

public interface ScannerProvider {
    /**
     * Executes the scanner against the specified project path.
     * @param projectPath The path to the project to scan.
     * @return A list of findings discovered by the scanner.
     */
    List<Finding> scan(String projectPath);

    /**
     * Updates the scanner's internal rules or databases.
     */
    void updateRules();

    /**
     * Returns the name of the scanner.
     */
    String getName();
    
    /**
     * Checks if the scanner is installed and available in the system.
     */
    boolean isAvailable();
}
