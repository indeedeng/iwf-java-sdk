package io.iworkflow.core.command;

public enum CommandCarryOverType {
    NONE, // this will NOT carry over any unfinished command to next states
    ALL_UNFINISHED; // this will carry over all unfinished commands to next states
}
