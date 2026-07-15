package com.secai.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Component
@Command(
    name = "secai",
    mixinStandardHelpOptions = true,
    version = "secai 1.0",
    description = "AI-Powered Security Analysis CLI.",
    subcommands = {
        ScanCommand.class,
        ExplainCommand.class,
        FixCommand.class,
        ReportCommand.class,
        ConfigCommand.class,
        DoctorCommand.class,
        UpdateCommand.class,
        ChatCommand.class
    }
)
public class SecAiCommand implements Callable<Integer> {

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output")
    boolean verbose;

    @Override
    public Integer call() {
        System.out.println("""
                 _____           _    ___ 
                /  ___|         / \\  |_ _|
                \\ `--.  ___  ___\\  \\  | | 
                 `--. \\/ _ \\/ __|  _ \\ | | 
                /\\__/ /  __/ (__| | \\ \\| | 
                \\____/ \\___|\\___\\_|  \\_\\___|
                """);
        System.out.println("AI-Powered Security Analysis CLI (v1.0)");
        System.out.println("Use 'secai --help' for available commands.");
        return 0;
    }
}
