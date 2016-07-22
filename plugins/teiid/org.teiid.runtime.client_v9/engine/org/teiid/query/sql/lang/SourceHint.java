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

package org.teiid.query.sql.lang;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.teiid.designer.query.sql.lang.ISourceHint;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.TeiidParser;

/**
 *
 */
public class SourceHint implements ISourceHint {

    /**
     *
     */
    public static class SpecificHint {
        String hint;
        boolean useAliases;

        /**
         * @param hint
         * @param useAliases
         */
        public SpecificHint(String hint, boolean useAliases) {
            this.hint = hint;
            this.useAliases = useAliases;
        }

        /**
         * @return hint
         */
        public String getHint() {
            return hint;
        }

        /**
         * @return use aliases
         */
        public boolean isUseAliases() {
            return useAliases;
        }
    }

    private final TeiidParser teiidParser;

    private boolean useAliases;

    private String generalHint;

    private Map<String, SpecificHint> sourceHints;

    public SourceHint(TeiidParser teiidParser) {
        this.teiidParser = teiidParser;
    }

    /**
     * @return the teiidParser
     */
    public TeiidParser getTeiidParser() {
        return this.teiidParser;
    }

    /**
     * @return version
     */
    public ITeiidServerVersion getTeiidVersion() {
        return this.getTeiidParser().getVersion();
    }

    /**
     * @return comments from parser
     */
    public Set<Comment> getComments() {
        return getTeiidParser().getComments();
    }

    /**
     * @return general hint
     */
    public String getGeneralHint() {
        return generalHint;
    }

    /**
     * @param generalHint
     */
    public void setGeneralHint(String generalHint) {
        this.generalHint = generalHint;
    }

    /**
     * @param translatorName
     * @param hint
     * @param useAliases
     */
    public void setSourceHint(String translatorName, String hint, boolean useAliases) {
        if (this.sourceHints == null) {
            this.sourceHints = new TreeMap<String, SpecificHint>(String.CASE_INSENSITIVE_ORDER);
        }
        this.sourceHints.put(translatorName, new SpecificHint(hint, useAliases));
    }

    /**
     * @param sourceName
     *
     * @return specific hint for given name
     */
    public SpecificHint getSpecificHint(String sourceName) {
        if (this.sourceHints == null) {
            return null;
        }
        return this.sourceHints.get(sourceName);
    }

    /**
     * @param sourceName
     *
     * @return source hint with name
     */
    public String getSourceHint(String sourceName) {
        SpecificHint sp = getSpecificHint(sourceName);
        if (sp != null) {
            return sp.getHint();
        }
        return null;
    }

    /**
     * @return map of specific hints
     */
    public Map<String, SpecificHint> getSpecificHints() {
        return sourceHints;
    }

    /**
     * @return use aliases flag
     */
    public boolean isUseAliases() {
        return useAliases;
    }

    /**
     * @param useAliases
     */
    public void setUseAliases(boolean useAliases) {
        this.useAliases = useAliases;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.generalHint == null) ? 0 : this.generalHint.hashCode());
        result = prime * result + ((this.sourceHints == null) ? 0 : this.sourceHints.hashCode());
        result = prime * result + (this.useAliases ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SourceHint other = (SourceHint)obj;
        if (this.generalHint == null) {
            if (other.generalHint != null)
                return false;
        } else if (!this.generalHint.equals(other.generalHint))
            return false;
        if (this.sourceHints == null) {
            if (other.sourceHints != null)
                return false;
        } else if (!this.sourceHints.equals(other.sourceHints))
            return false;
        if (this.useAliases != other.useAliases)
            return false;
        return true;
    }

}
