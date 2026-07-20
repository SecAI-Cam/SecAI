package com.secai.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.secai.verify.model.CommandLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class VerificationLogger {
    private static final Logger logger = LoggerFactory.getLogger(VerificationLogger.class);
    private final ObjectMapper mapper;

    public VerificationLogger() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void logCommand(String projectPath, String toolName, String fullCommand, long durationMs, int exitCode, String approvalId, String rawOutput) {
        CommandLog logEntry = new CommandLog();
        logEntry.setTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
        logEntry.setToolName(toolName);
        logEntry.setFullCommand(fullCommand);
        logEntry.setExecutionTimeMs(durationMs);
        logEntry.setExitCode(exitCode);
        logEntry.setApprovalId(approvalId);
        
        // Hash the output to ensure integrity
        if (rawOutput != null) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(rawOutput.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                logEntry.setOutputHash(hexString.toString());
            } catch (Exception e) {
                logEntry.setOutputHash("error_calculating_hash");
            }
        }

        try {
            Path logDir = Paths.get(projectPath, ".secai", "verify");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            File logFile = logDir.resolve("audit-log.jsonl").toFile();
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(mapper.writeValueAsString(logEntry).replaceAll("\n", " ") + "\n");
            }
        } catch (IOException e) {
            logger.error("Failed to write to audit log: {}", e.getMessage());
        }
    }
}
