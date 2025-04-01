package com.example;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Is a ThreadOS Shell interpreter.
 */
public class Shell extends Thread {
    private SysLib sysLib;
    private volatile boolean shouldStop = false;

    // Constructor
    public Shell() {
    }

    // Setter method to inject the mock SysLib
    public void setSysLib(SysLib sysLib) {
        this.sysLib = sysLib;
    }

    public void run() {
        for (int line = 1; !shouldStop; line++) {
            String cmdLine = "";
            String shell_prompt = "shell[" + line + "]% ";
            System.out.println("Shell Prompt: " + shell_prompt);
            
            do {
                StringBuffer inputBuf = new StringBuffer();
                sysLib.cin(inputBuf);
                cmdLine = inputBuf.toString().trim();
            } while (cmdLine.length() == 0);
            System.out.println("Received command: " + cmdLine);

            if (cmdLine.equals("exit")) {
                shouldStop = true;
                break;
            } else {
                // Call exec() with the received command
                String[] cmdArray = new String[] { cmdLine };
                sysLib.exec(cmdArray);
            }
        }
    }
}