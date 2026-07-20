package com.secai.verify.model;

public enum ExecutionMode {
    VERIFY, // Safe, default - scanners, version checks, banner grabs
    EXPLOIT // Requires explicit approval - active exploitation, PoC execution
}
