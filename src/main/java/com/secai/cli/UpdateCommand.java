package com.secai.cli;

import com.secai.scanner.ScannerEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Component
@Command(name = "update", description = "Update the internal databases and rules of all scanners.")
public class UpdateCommand implements Callable<Integer> {

    private final ScannerEngine scannerEngine;

    @Autowired
    public UpdateCommand(ScannerEngine scannerEngine) {
        this.scannerEngine = scannerEngine;
    }

    @Override
    public Integer call() {
        System.out.println("Starting update process...");
        scannerEngine.updateAllRules();
        System.out.println("Update process complete.");
        return 0;
    }
}
