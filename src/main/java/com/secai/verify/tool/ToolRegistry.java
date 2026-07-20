package com.secai.verify.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToolRegistry {

    private final List<SecurityTool> tools;

    @Autowired
    public ToolRegistry(List<SecurityTool> tools) {
        this.tools = tools;
    }

    public List<SecurityTool> getAllTools() {
        return tools;
    }

    public Optional<SecurityTool> getToolByName(String name) {
        return tools.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
