package com.secai;

import com.secai.cli.SecAiCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class SecaiApplication implements CommandLineRunner {

    private final SecAiCommand secAiCommand;
    private final IFactory factory;

    public SecaiApplication(SecAiCommand secAiCommand, IFactory factory) {
        this.secAiCommand = secAiCommand;
        this.factory = factory;
    }

    public static void main(String[] args) {
        SpringApplication.run(SecaiApplication.class, args);
    }

    @Override
    public void run(String... args) {
        int exitCode = new CommandLine(secAiCommand, factory).execute(args);
        // Only exit if we are not testing or if it's the intended behavior.
        // For a pure CLI, system exit is fine.
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
