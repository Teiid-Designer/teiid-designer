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

package org.teiid.query.sql;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.language.SQLConstants.Reserved;

/**
 * Special variable names in stored procedure language.
 */
public class ProcedureReservedWords {

    @Removed(Version.TEIID_8_0)
    public static final String INPUT = Reserved.INPUT;

    @Removed(Version.TEIID_8_0)
    public static final String INPUTS = "INPUTS"; //$NON-NLS-1$

    @Removed(Version.TEIID_8_0)
    public static final String ROWS_UPDATED = "ROWS_UPDATED"; //$NON-NLS-1$

    public static final String ROWCOUNT = "ROWCOUNT"; //$NON-NLS-1$

	public static final String CHANGING = "CHANGING"; //$NON-NLS-1$

    public static final String VARIABLES = "VARIABLES"; //$NON-NLS-1$
    
    public static final String DVARS = "DVARS"; //$NON-NLS-1$

    private static ITeiidServerVersion CACHED_TEIID_VERSION = null;

    private static Set<String> RESERVED_WORDS = null;

    /** Can't construct */
    private ProcedureReservedWords() {}

    /**
     * @throws AssertionError
     */
    private static Set<String> extractFieldNames(Class<?> clazz) throws AssertionError {
        HashSet<String> result = new HashSet<String>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() != String.class)
                continue;

            // If teiid version is less than the version listed in the since annotation
            // then word should not be included in the word sets
            if (AnnotationUtils.hasAnnotation(field, Removed.class)) {
                Removed removed = AnnotationUtils.getAnnotation(field, Removed.class);
                if (AnnotationUtils.isGreaterThanOrEqualTo(removed, CACHED_TEIID_VERSION))
                    continue;
            }

            try {
                if (!result.add((String)field.get(null))) {
                    throw new AssertionError("Duplicate value for " + field.getName()); //$NON-NLS-1$
                }
            } catch (Exception e) {}
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * @param teiidVersion
     */
    private static void initialiseConstants(ITeiidServerVersion teiidVersion) {
        CACHED_TEIID_VERSION = teiidVersion;
        RESERVED_WORDS = extractFieldNames(ProcedureReservedWords.class);
    }

    /**
     * Check whether a string is a procedure reserved word.  
     * @param teiidVersion
     * @param str String to check
     * @return True if procedure reserved word, false if not or null
     */
    public static final boolean isProcedureReservedWord(ITeiidServerVersion teiidVersion, String str) {
        if (str == null) 
            return false;

        String word = str.toUpperCase();
        if (CACHED_TEIID_VERSION == null || ! CACHED_TEIID_VERSION.equals(teiidVersion) || RESERVED_WORDS == null)
            initialiseConstants(teiidVersion);

        return RESERVED_WORDS.contains(word);
    }
}
