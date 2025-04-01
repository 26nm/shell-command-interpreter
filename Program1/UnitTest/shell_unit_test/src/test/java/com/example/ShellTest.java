package com.example;

import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatcher;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ShellTest {

    @Test
    public void testCommandExecutionThenExit() throws InterruptedException {
        SysLib mockSysLib = mock(SysLib.class);
        Shell shell = new Shell();
        shell.setSysLib(mockSysLib);
    
        AtomicInteger callCount = new AtomicInteger(0); 
    
        doAnswer(invocation -> {
            if (callCount.get() == 0) { 
                StringBuffer buffer = (StringBuffer) invocation.getArguments()[0];
                buffer.append("command\n");
            } else {
                // Simulate exit command after processing "command"
                StringBuffer buffer = (StringBuffer) invocation.getArguments()[0];
                buffer.append("exit\n"); 
            }
            callCount.incrementAndGet(); 
            return null;
        }).when(mockSysLib).cin(any(StringBuffer.class)); 
    
        shell.start();
    
        // Wait for a short time for the thread to process the input
        Thread.sleep(100); 
    
        verify(mockSysLib, times(2)).cin(any(StringBuffer.class)); 
        verify(mockSysLib).exec(argThat(new ArgumentMatcher<String[]>() {
            @Override
            public boolean matches(String[] argument) {
                return Arrays.equals(argument, new String[] { "command" });
            }
        })); 
        shell.join(); 
    }

    @Test
    public void testMultipleCommandExecutionAndExit() throws InterruptedException {
        SysLib mockSysLib = mock(SysLib.class);
        Shell shell = new Shell();
        shell.setSysLib(mockSysLib);
    
        AtomicInteger callCount = new AtomicInteger(0); 

        doAnswer(invocation -> {
            StringBuffer buffer = (StringBuffer) invocation.getArguments()[0];
            switch (callCount.get()) {
                case 0:
                    buffer.append("command1\n");
                    break;
                case 1:
                    buffer.append("command2\n");
                    break;
                case 2:
                    buffer.append("command3\n");
                    break;
                case 3:
                    buffer.append("exit\n");
                    break;
                default:
                    buffer.append(""); // Simulate empty input after "exit"
                    break;
            }
            callCount.incrementAndGet();
            return null;
        }).when(mockSysLib).cin(any(StringBuffer.class)); 
    
        shell.start();
    
        // Wait for a short time for the thread to process the input
        Thread.sleep(100); 
    
        verify(mockSysLib, times(4)).cin(any(StringBuffer.class)); 
        verify(mockSysLib).exec(argThat(new ArgumentMatcher<String[]>() {
            @Override
            public boolean matches(String[] argument) {
                return Arrays.equals(argument, new String[] { "command1" });
            }
        }));
        verify(mockSysLib).exec(argThat(new ArgumentMatcher<String[]>() {
            @Override
            public boolean matches(String[] argument) {
                return Arrays.equals(argument, new String[] { "command2" });
            }
        }));
        verify(mockSysLib).exec(argThat(new ArgumentMatcher<String[]>() {
            @Override
            public boolean matches(String[] argument) {
                return Arrays.equals(argument, new String[] { "command3" });
            }
        }));
    
        shell.join(); 
    }
}