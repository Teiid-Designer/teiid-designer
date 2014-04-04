/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.parser;

import java.io.Serializable;
import java.util.Map;
import org.teiid.core.util.PropertiesUtils;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 * Info object to pass into the Teiid SQL Parser
 */
public class ParseInfo implements Serializable {

    private static final long serialVersionUID = -7323683731955992888L;
    private static final boolean ANSI_QUOTED_DEFAULT = PropertiesUtils.getBooleanProperty(System.getProperties(),
                                                                                          "org.teiid.ansiQuotedIdentifiers", true); //$NON-NLS-1$

    @Removed(Version.TEIID_8_0)
    public Map<String, Integer> nameCounts = null;

    private int referenceCount = 0;

    /**
     * Default instance of this class
     */
    public static final ParseInfo DEFAULT_INSTANCE = new ParseInfo();

    static {
        DEFAULT_INSTANCE.ansiQuotedIdentifiers = true;
    }

    // treat a double quoted variable as variable instead of string 
    private boolean ansiQuotedIdentifiers = ANSI_QUOTED_DEFAULT;

    // Is this used for a designer command
    private boolean designerCommand = false;

    /**
     * Create new instance
     */
    public ParseInfo() {
    }

    /**
     * @param designerCommand the designerCommand to set
     */
    public void setDesignerCommand(boolean designerCommand) {
        this.designerCommand = designerCommand;
    }

    /**
     * @return the designerCommand
     */
    public boolean isDesignerCommand() {
        return this.designerCommand;
    }

    /**
     * @param ansiQuotedIdentifiers
     */
    public void setAnsiQuotedIdentifiers(boolean ansiQuotedIdentifiers) {
        this.ansiQuotedIdentifiers = ansiQuotedIdentifiers;
    }

    /**
     * @return ansiQuotedIdentifiers
     */
    public boolean useAnsiQuotedIdentifiers() {
        return ansiQuotedIdentifiers;
    }

    /**
     * @return incremented reference count
     */
    public int incrementReferenceCount() {
        return this.referenceCount++;
    }

    /**
     * @return the referenceCount
     */
    public int getReferenceCount() {
        return this.referenceCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.ansiQuotedIdentifiers ? 1231 : 1237);
        result = prime * result + (this.designerCommand ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ParseInfo other = (ParseInfo)obj;
        if (this.ansiQuotedIdentifiers != other.ansiQuotedIdentifiers) return false;
        if (this.designerCommand != other.designerCommand) return false;
        return true;
    }
}
