package com.secai.verify.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Service
public class KaliSandbox {
    private static final Logger logger = LoggerFactory.getLogger(KaliSandbox.class);
    
    private static final String IMAGE_NAME = "secai-pentest";
    private String containerId = null;
    private String networkName = null;

    private static final String DOCKERFILE_CONTENT = 
        "FROM kalilinux/kali-rolling\n" +
        "ENV DEBIAN_FRONTEND=noninteractive\n" +
        "RUN echo \"deb http://mirrors.ocf.berkeley.edu/kali kali-rolling main contrib non-free non-free-firmware\" > /etc/apt/sources.list\n" +
        "RUN apt-get update && apt-get install -y --no-install-recommends \\\n" +
        "    nmap nuclei sqlmap nikto ffuf metasploit-framework curl wget jq \\\n" +
        "    && apt-get clean && rm -rf /var/lib/apt/lists/*\n" +
        "RUN mkdir -p /workspace /evidence\n" +
        "WORKDIR /workspace\n" +
        "CMD [\"/bin/bash\", \"-c\", \"tail -f /dev/null\"]\n";

    public void buildImageIfNeeded(String projectPath) {
        // Implement docker build from docker/secai-pentest.Dockerfile
        logger.info("Checking if {} image exists...", IMAGE_NAME);
        try {
            ProcessBuilder checkPb = new ProcessBuilder("docker", "image", "inspect", IMAGE_NAME);
            Process checkProc = checkPb.start();
            if (checkProc.waitFor() != 0) {
                logger.info("Image not found. Building {} (this may take a few minutes)...", IMAGE_NAME);
                
                ProcessBuilder buildPb = new ProcessBuilder("docker", "build", "-t", IMAGE_NAME, "-");
                buildPb.redirectErrorStream(true);
                Process buildProc = buildPb.start();
                
                // Write Dockerfile content to stdin
                try (java.io.OutputStream os = buildProc.getOutputStream()) {
                    os.write(DOCKERFILE_CONTENT.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    os.flush();
                }
                
                // Show output to user
                java.io.InputStream is = buildProc.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    System.out.write(buffer, 0, bytesRead);
                }
                
                int exitCode = buildProc.waitFor();
                if (exitCode != 0) {
                    logger.error("Docker build failed with exit code {}", exitCode);
                } else {
                    logger.info("Successfully built {}", IMAGE_NAME);
                }
            } else {
                logger.info("Image {} already exists.", IMAGE_NAME);
            }
        } catch (Exception e) {
            logger.error("Failed during image check/build: {}", e.getMessage(), e);
        }
    }

    public void start(String projectPath, String targetHost, SandboxConfig config) {
        if (containerId != null) {
            logger.warn("Sandbox container already running: {}", containerId);
            return;
        }

        try {
            // 1. Check docker availability
            Process checkProc = new ProcessBuilder("docker", "--version").start();
            if (checkProc.waitFor() != 0) {
                throw new IllegalStateException("Docker is not available.");
            }

            // 2. Create scoped network (simplified for now to just a bridge)
            networkName = "secai-net-" + UUID.randomUUID().toString().substring(0, 8);
            Process netProc = new ProcessBuilder("docker", "network", "create", networkName).start();
            netProc.waitFor();

            // 3. Start the container
            String evidenceVol = "secai-evidence-" + UUID.randomUUID().toString().substring(0, 8);
            
            // Validate image exists before trying to run it
            if (new ProcessBuilder("docker", "image", "inspect", IMAGE_NAME).start().waitFor() != 0) {
                // If it still doesn't exist, try building it one more time with the current directory
                logger.warn("Image {} not found during start. Attempting emergency build.", IMAGE_NAME);
                buildImageIfNeeded(System.getProperty("user.dir"));
                
                if (new ProcessBuilder("docker", "image", "inspect", IMAGE_NAME).start().waitFor() != 0) {
                    throw new RuntimeException("Docker image '" + IMAGE_NAME + "' is missing and could not be built. Please run 'secai verify --setup' from the project root.");
                }
            }
            
            List<String> cmd = new ArrayList<>();
            cmd.add("docker");
            cmd.add("run");
            cmd.add("-d");
            cmd.add("--rm"); // ephemeral
            cmd.add("--read-only");
            cmd.add("--tmpfs"); cmd.add("/tmp:rw,noexec,nosuid");
            cmd.add("--tmpfs"); cmd.add("/var/lib:rw"); // Needed for some tool DBs
            cmd.add("--cpus=" + config.getCpuLimit());
            cmd.add("--memory=" + config.getMemoryLimit());
            cmd.add("--pids-limit=512");
            cmd.add("--security-opt=no-new-privileges:true");
            cmd.add("--network=" + networkName);
            cmd.add("-v"); cmd.add(evidenceVol + ":/evidence");
            
            // Note: we do NOT mount the host filesystem. This is a true sandbox.
            
            cmd.add(IMAGE_NAME);
            
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process proc = pb.start();
            
            String output = new String(proc.getInputStream().readAllBytes()).trim();
            if (proc.waitFor() == 0) {
                containerId = output;
                logger.info("Started Kali Sandbox container: {}", containerId);
            } else {
                String err = new String(proc.getErrorStream().readAllBytes());
                throw new RuntimeException("Failed to start container: " + err);
            }

        } catch (Exception e) {
            logger.error("Error starting sandbox: {}", e.getMessage(), e);
            stop(); // Cleanup any partial state
            throw new RuntimeException("Sandbox initialization failed", e);
        }
    }

    public SandboxResult execute(List<String> command, long timeoutMs) {
        if (containerId == null) {
            throw new IllegalStateException("Sandbox container is not running.");
        }

        long startTime = System.currentTimeMillis();
        SandboxResult result = new SandboxResult();

        try {
            List<String> execCmd = new ArrayList<>();
            execCmd.add("docker");
            execCmd.add("exec");
            execCmd.add(containerId);
            execCmd.addAll(command);
            
            ProcessBuilder pb = new ProcessBuilder(execCmd);
            
            // Create temp files for stdout/stderr to avoid memory issues with large outputs
            Path outPath = Files.createTempFile("secai-exec-", ".out");
            Path errPath = Files.createTempFile("secai-exec-", ".err");
            pb.redirectOutput(outPath.toFile());
            pb.redirectError(errPath.toFile());
            
            logger.debug("Executing in sandbox: {}", String.join(" ", execCmd));
            Process proc = pb.start();
            
            boolean finished = proc.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
            
            result.setExecutionDurationMs(System.currentTimeMillis() - startTime);
            
            if (finished) {
                result.setExitCode(proc.exitValue());
                result.setStdout(Files.readString(outPath));
                result.setStderr(Files.readString(errPath));
                result.setTimeoutHit(false);
            } else {
                proc.destroyForcibly();
                result.setExitCode(-1);
                result.setTimeoutHit(true);
                result.setStderr("Command timed out after " + timeoutMs + "ms");
            }
            
            // Clean up temp files
            Files.deleteIfExists(outPath);
            Files.deleteIfExists(errPath);
            
        } catch (Exception e) {
            logger.error("Error executing command in sandbox: {}", e.getMessage());
            result.setExitCode(-2);
            result.setStderr("Execution failed: " + e.getMessage());
        }

        return result;
    }

    public void copyEvidence(String hostDestPath) {
        if (containerId == null) return;
        
        try {
            Path dest = Paths.get(hostDestPath);
            if (!Files.exists(dest)) {
                Files.createDirectories(dest);
            }
            
            // docker cp containerId:/evidence/. hostDestPath/
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "cp", containerId + ":/evidence/.", dest.toString()
            );
            pb.start().waitFor();
            logger.info("Copied evidence from sandbox to {}", hostDestPath);
        } catch (Exception e) {
            logger.error("Failed to copy evidence: {}", e.getMessage());
        }
    }

    public void stop() {
        try {
            if (containerId != null) {
                logger.info("Stopping sandbox container {}...", containerId);
                new ProcessBuilder("docker", "stop", containerId).start().waitFor();
                // --rm handles removal
                containerId = null;
            }
            
            if (networkName != null) {
                logger.info("Removing sandbox network {}...", networkName);
                new ProcessBuilder("docker", "network", "rm", networkName).start().waitFor();
                networkName = null;
            }
        } catch (Exception e) {
            logger.error("Error during sandbox cleanup: {}", e.getMessage());
        }
    }
}
