/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.komodo.sramp.shell.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.overlord.sramp.client.shell.AbstractShellCommand;

/**
 * Adds a VDB to the repository.  
 */
public class AddVdbCommand extends AbstractShellCommand {

    /**
     * Constructor.
     */
    public AddVdbCommand() {
    }

    /**
     * @see org.overlord.sramp.client.shell.ShellCommand#printUsage()
     */
    @Override
    public void printUsage() {
        print("jvm:status [<statusType>]");
        print("\tValid statusTypes: all (default), memory, threads");
    }

    /**
     * @see org.overlord.sramp.client.shell.ShellCommand#printHelp()
     */
    @Override
    public void printHelp() {
        print("The 'status' command displays the status of the currently");
        print("running JVM.");
        print("");
        print("Example usages:");
        print(">  jvm:status");
        print(">  jvm:status all");
        print(">  jvm:status memory");
    }

    /**
     * @see org.overlord.sramp.client.shell.AbstractShellCommand#tabCompletion(java.lang.String, java.util.List)
     */
    @Override
    public int tabCompletion(String lastArgument, List<CharSequence> candidates) {
        // This is the first argument!
        if (getArguments().isEmpty()) {
            if (lastArgument == null)
                lastArgument = "";
            if ("all".startsWith(lastArgument)) {
                candidates.add("all");
            } else if ("memory".startsWith(lastArgument)) {
                candidates.add("memory");
            } else if ("threads".startsWith(lastArgument)) {
                candidates.add("threads");
            }
            // See the tabCompletion() javadoc for why the return value is 0
            return 0;
        } else {
            // There are no more arguments - this command only accepts one argument.
            return -1;
        }
    }

    /**
     * @see org.overlord.sramp.client.shell.ShellCommand#execute()
     */
    @Override
    public void execute() throws Exception {
        String statusType = optionalArgument(0);
        if (statusType == null) {
            statusType = "all";
        }
        if ("all".equals(statusType) || "memory".equals(statusType)) {
            int mb = 1024*1024;
            Runtime runtime = Runtime.getRuntime();
            print("##### Heap utilization statistics [MB] #####");
            print("Used Memory:  %1$d MB", (runtime.totalMemory() - runtime.freeMemory()) / mb);
            print("Free Memory:  %1$d MB", runtime.freeMemory() / mb);
            print("Total Memory: %1$d MB", runtime.totalMemory() / mb);
            print("Max Memory:   %1$d MB", runtime.maxMemory() / mb);
        }
        if ("all".equals(statusType) || "threads".equals(statusType)) {
            print("##### Current Threads #####");
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            int count = 0;
            for (Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
                Thread t = entry.getKey();
                print("Thread %1$d : %2$s", count++, t.getName());
            }
        }
    }
}
