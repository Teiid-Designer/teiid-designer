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

import java.util.HashMap;
import java.util.Map;
import org.overlord.sramp.shell.ShellCommand;
import org.overlord.sramp.shell.ShellCommandProvider;

/**
 *
 */
public class AddVdbCommandProvider implements ShellCommandProvider {

    /**
     * Constructor.
     */
    public AddVdbCommandProvider() {
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommandProvider#getNamespace()
     */
    @Override
    public String getNamespace() {
        return "jvm";
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommandProvider#provideCommands()
     */
    @Override
    public Map<String, Class<? extends ShellCommand>> provideCommands() {
        final Map<String, Class<? extends ShellCommand>> rval = new HashMap<String, Class<? extends ShellCommand>>();
        rval.put("status", AddVdbCommand.class);
        return rval;
    }

}
