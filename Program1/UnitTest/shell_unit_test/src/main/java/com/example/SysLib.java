package com.example;

import java.util.*;

public class SysLib {

    public final static int OK = 0; 
    public final static int ERROR = -1;

    public void cin(StringBuffer inputBuf) {
        // This method would typically read input from the user
        // and append it to the inputBuf.
        // For testing purposes, this method will be mocked.
    }

    public void cerr(String message) {
        // This method would typically print an error message to the console.
        // For testing purposes, this method will be mocked.
    }

    public int exec(String[] command) {
        // This method would typically execute the given command
        // and return the thread ID of the newly created process.
        // For testing purposes, this method will be mocked.
        return 0; // Default return value for testing
    }

    public int join() { 
        // No need for implementation here, as it will be mocked
        return 0; // Default return value (not used in this case)
    }

    public int exit() {
        // This method would typically terminate the current process.
        // For testing purposes, this method will be mocked.
        return OK;
    }
    
    public static String[] stringToArgs(String s) {
        StringTokenizer token = new StringTokenizer(s, " ");
        String[] progArgs = new String[token.countTokens()];
        for (int i = 0; token.hasMoreTokens(); i++) {
            progArgs[i] = token.nextToken();
        }
        return progArgs;
    }
}