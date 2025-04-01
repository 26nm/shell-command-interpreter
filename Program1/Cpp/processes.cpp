/*
 * processes.cpp: This program creates a series of processes to execute a pipeline of commands 
 * similar to a shell command like: `ps aux | grep <command> | wc -l`.
 * 
 * Functionality:
 * 1. The program accepts a single command-line argument, which is the search term for the `grep` command.
 * 2. A parent process forks a child process, which creates and manages additional processes to form the pipeline:
 *    - A child process forks a grandchild process.
 *    - The grandchild process forks a great-grandchild process.
 * 3. The pipeline setup:
 *    - The great-grandchild executes `ps aux`, which lists all running processes.
 *    - The grandchild reads the output of `ps aux` and executes `grep <command>` to filter lines containing the search 
        term.
 *    - The child process reads the output of `grep` and executes `wc -l` to count the matching lines.
 * 4. The parent process waits for the child process to complete and then prints a confirmation message.
 * 
 * Pipes are used for inter-process communication between:
 * - Great-grandchild (producing `ps aux` output) and grandchild (`grep` command).
 * - Grandchild (producing `grep` output) and child (`wc -l` command).
 * 
 * Nolan Dela Rosa
 *
 * January 22, 2025
 */
 
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
using namespace std;

/**
 * Main function to set up a pipeline of processes:
 * - Creates a child process that forks further to create a grandchild and great-grandchild.
 * - The great-grandchild executes `ps aux` to list running processes.
 * - The grandchild filters this list using `grep` to search for a specific command (provided as a command-line argument).
 * - The child counts the number of matching lines using `wc -l` and prints the result.
 * 
 * Parameters:
 * - `argc`: The argument count. Should be 2 to provide the search term for `grep`.
 * - `argv`: The argument vector. `argv[1]` should contain the search string that `grep` uses to filter the `ps` output.
 * 
 * Return:
 * - Returns 0 on successful execution. Exits with a non-zero value if any part of the process creation, pipe handling, or command execution fails.
 */
int main(int argc, char **argv) {
    int fds[2][2]; // Two pipes: one for communication between child and grandchild, one between grandchild and great-grandchild
    int pid;

    if (argc != 2) {
        cerr << "Usage: processes command" << endl;
        exit(-1);
    }

    // fork a child
    if ((pid = fork()) < 0) {
        perror("fork error");
    } else if (pid == 0) {
        // I'm a child
        if (pipe(fds[0]) < 0) { // create a pipe using fds[0]
            perror("pipe error");
            exit(-1);
        }

        if ((pid = fork()) < 0) { // fork a grand-child
            perror("fork error");
            exit(-1);
        } else if (pid == 0) {
            // I'm a grand-child
            if (pipe(fds[1]) < 0) { // create a pipe using fds[1]
                perror("pipe error");
                exit(-1);
            }

            if ((pid = fork()) < 0) { // fork a great-grand-child
                perror("fork error");
                exit(-1);
            } else if (pid == 0) {
                // I'm a great-grand-child
                close(fds[0][0]); // Close unused read end of first pipe
                close(fds[0][1]); // Close unused write end of first pipe
                close(fds[1][0]); // Close unused read end of second pipe
                dup2(fds[1][1], STDOUT_FILENO); // Redirect output to write end of second pipe
                close(fds[1][1]); // Close write end after duplication

                execlp("ps", "ps", "aux", NULL); // Execute "ps aux"
                perror("execlp ps failed");
                exit(-1);
            } else {
                // I'm a grand-child
                close(fds[1][1]); // Close unused write end of second pipe
                dup2(fds[1][0], STDIN_FILENO); // Redirect stdin to read from second pipe
                close(fds[1][0]); // Close read end after duplication
                close(fds[0][0]); // Close unused read end of first pipe
                dup2(fds[0][1], STDOUT_FILENO); // Redirect output to write end of first pipe
                close(fds[0][1]); // Close write end after duplication
                execlp("grep", "grep", argv[1], NULL); // Execute "grep <command>"
                perror("execlp grep failed");
                exit(-1);
            }
        } else {
            // I'm a child
            close(fds[0][1]); // Close unused write end of first pipe
            dup2(fds[0][0], STDIN_FILENO); // Redirect stdin to read from first pipe
            close(fds[0][0]); // Close read end after duplication
            execlp("wc", "wc", "-l", NULL); // Execute "wc -l"
            perror("execlp wc failed");
            exit(-1);
        }
    } else {
        // I'm a parent
        wait(NULL); // Wait for the child process to finish
        cout << "commands completed" << endl;
    }

    return 0;
}
