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

package org.teiid.query.sql.visitor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CriteriaSelector;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.TranslateCriteria;
import org.teiid.query.sql.navigator.DeepPreOrderNavigator;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.GroupSymbol;


/**
 */
public class ResolveVirtualGroupCriteriaVisitor extends LanguageVisitor {

    private List virtualGroup;

    private IQueryMetadataInterface metadata;

    /**
     * Constructor for ResolveElementsVisitor with no specified groups.  In this
     * case every element's group will be looked up based on the group name.
     * @param teiidVersion
     * @param virtualGroup 
     * @param metadata
     */
    public ResolveVirtualGroupCriteriaVisitor(ITeiidServerVersion teiidVersion, GroupSymbol virtualGroup,  IQueryMetadataInterface metadata) {
        super(teiidVersion);
        this.virtualGroup = Arrays.asList(new Object[] {virtualGroup});
        this.metadata = metadata;
    }

    /**
     * resolve criteria
     *
     * @param obj
     * @param virtualGroup
     * @param metadata
     * @throws Exception
     */
    public static void resolveCriteria(LanguageObject obj, GroupSymbol virtualGroup,  IQueryMetadataInterface metadata)
        throws Exception {
        if(obj == null) {
            return;
        }

        // Resolve elements, deal with errors
        ResolveVirtualGroupCriteriaVisitor resolveVisitor = new ResolveVirtualGroupCriteriaVisitor(obj.getTeiidVersion(), virtualGroup, metadata);
        
        try {
            DeepPreOrderNavigator.doVisit(obj, resolveVisitor);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof QueryResolverException)
                throw (QueryResolverException)e.getCause();

            throw e;
        }
    }

}
