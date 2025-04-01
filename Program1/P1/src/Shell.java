/**
 * Shell.java
 * 
 * This program defines a Shell class that extends the Thread class and acts as a command-line
 * interpreter for ThreadOS. When executed, it provides an interactive prompt where users
 * can input commands. The program supports executing multiple commands in a single line, 
 * separated by the delimiters '&' (concurrent execution) and ';' (sequential execution). 
 * 
 * Features:
 * - Displays a prompt: `shell[n]%`, where `n` is the command count.
 * - Executes commands using the SysLib.exec() system call.
 * - Supports concurrent execution ('&') where commands are executed in separate threads simultaneously.
 * - Supports sequential execution (';') where the program waits for each command to complete 
 *   before proceeding to the next one using SysLib.join().
 * - Handles an arbitrary number of commands, with each command and its arguments properly parsed.
 * - Exits the shell gracefully when the user inputs "exit" or "quit."
 * 
 * Limitations:
 * - Does not handle I/O redirection (e.g., `<`, `>`, `|`).
 * - Does not support shell variables or advanced programming constructs.
 * - Commands, arguments, and delimiters must be separated by spaces or tabs.
 * 
 * 
 * Usage:
 * - Compile the program using `javac *.java` to ensure all Java files are compiled.
 * - Run the program as part of the ThreadOS framework. Ensure that the `Boot.java` class is executed, as it acts as the entry point to start ThreadOS and invoke the `Shell` class.
 * - The `Shell` class will prompt you with a shell command prompt (`shell[1]%`), where you can input commands to execute.
 * 
 * Testing Functionality:
 * - To test the shell's functionality, use commands like `PingPong abc 100` 
 * (or other executable threads) as input to verify proper command execution.
 * - Multiple commands separated by `&` or `;` can be tested:
 *   - `&` should run commands concurrently.
 *   - `;` should run commands sequentially, with the shell waiting for each command to complete.
 * - You can test the shell's behavior with commands like:
 *   - `PingPong abc 100 & PingPong def 200` (tests concurrent execution).
 *   - `PingPong abc 100; PingPong def 200` (tests sequential execution).
 * - The shell should display outputs indicating whether commands were executed successfully, 
 *   with completion messages after each command.
 * - To exit the shell, type `exit` or `quit`.
 * 
 * Example Input:
 * shell[1]% PingPong abc 100 & PingPong xyz 50 ; PingPong foo 200
 * 
 * Example Output:
 * - Executes "PingPong abc 100" and "PingPong xyz 50" concurrently.
 * - Waits for "PingPong foo 200" to finish before displaying the next prompt.
 */
import java.io.*;
import java.util.*;

class Shell extends Thread {
    
   /**
    * Purpose:
    * This method serves as an interactive command-line interpreter for ThreadOS. It reads 
    * user input, processes commands, and executes them either concurrently or sequentially, 
    * depending on the specified delimiters ('&' for concurrent and ';' for sequential).
    * 
    * Functionality:
    * - Continuously displays a prompt: `shell[n]%`, where `n` is the command count.
    * - Reads a line of user input, which can contain multiple commands separated by '&' or ';'.
    * - Identifies whether each command should be executed concurrently or sequentially.
    * - Executes each command using the `SysLib.exec()` system call.
    * - For commands separated by ';', waits for each to finish using `SysLib.join()` before 
    *   proceeding to the next command.
    * - Handles errors gracefully, notifying the user if a command fails to execute.
    * - Exits the shell when the user enters "exit" or "quit."
    * 
    * Key Steps:
    * 1. Prompt the user for input and split the command line into individual commands.
    * 2. Determine the execution mode (concurrent or sequential) for each command.
    * 3. Execute the commands in the specified order, handling concurrent and sequential execution.
    * 4. Increment the command count and repeat until the user exits.
    * 
    * Parameters:
    * None (the method is part of the Shell thread and runs independently).
    * 
    * Return Value:
    * None (void). The shell thread terminates when the user chooses to exit.
    * 
    * Example Behavior:
    * - Input: `PingPong abc 100 & PingPong xyz 50 ; PingPong foo 200`
    *   - Executes "PingPong abc 100" and "PingPong xyz 50" concurrently.
    *   - Waits for "PingPong foo 200" to complete before displaying the next prompt.
    * - Input: `exit`
    *   - Exits the shell and terminates the thread.
    */
    public void run() {
        int commandCount = 1;

        while(true) {
            SysLib.cout("shell[" + commandCount + "]% ");
            StringBuffer inputBuffer = new StringBuffer();
            SysLib.cin(inputBuffer);
            String cmdLine = inputBuffer.toString();


            if(cmdLine == null || cmdLine.equalsIgnoreCase("exit")
                || cmdLine.equalsIgnoreCase("quit")) {
                    SysLib.cout("Exiting Shell...\n");
                    SysLib.exit();
                    return;
            }

            String[] commands = cmdLine.split("[;&]");
            boolean[] isConcurrent = new boolean[commands.length];

            for(int i = 0; i < commands.length; i++) {
                isConcurrent[i] = cmdLine.contains(commands[i] + "&");
                commands[i] = commands[i].trim();
            }

            int[] tids = new int[commands.length];

            for(int i = 0; i < commands.length; i++) {
                if(commands[i].isEmpty()) continue;
                
                String[] args = SysLib.stringToArgs(commands[i]);
                tids[i] = SysLib.exec(args);

                if(tids[i] == -1) {
                    SysLib.cerr("Error: Failed to execute command: " + 
                        commands[i] + "\n");
                } else {
                    SysLib.cout("Successfully executed command: " + commands[i] + "\n");
                }

                if(!isConcurrent[i]) {
                    int tid = tids[i];

                    while(true) {
                        int joinedTid = SysLib.join();
                        
                        if(joinedTid == tid) {
                            break;
                        }

                    }
                }

                
            }

            commandCount++;
        }
    }
}
