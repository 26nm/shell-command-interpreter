# Shell Command Interpreter
This program defines a Shell class that extends the Thread class and acts as a command-line
interpreter for ThreadOS. When executed, it provides an interactive prompt where users
can input commands. The program supports executing multiple commands in a single line, 
separated by the delimiters '&' (concurrent execution) and ';' (sequential execution). 
 
# Features:
 * Displays a prompt: `shell[n]%`, where `n` is the command count.
 * Executes commands using the SysLib.exec() system call.
 * Supports concurrent execution ('&') where commands are executed in separate threads simultaneously.
 * Supports sequential execution (';') where the program waits for each command to complete 
   before proceeding to the next one using SysLib.join().
 * Handles an arbitrary number of commands, with each command and its arguments properly parsed.
 * Exits the shell gracefully when the user inputs "exit" or "quit."
   
# Limitations:
 * Does not handle I/O redirection (e.g., `<`, `>`, `|`).
 * Does not support shell variables or advanced programming constructs.
 * Commands, arguments, and delimiters must be separated by spaces or tabs.
