/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.internal.relationship.ui.search;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.search.ui.text.Match;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.search.runtime.RelationshipRecord;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;
import com.metamatrix.modeler.internal.ui.search.SearchPageUtil;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.ui.search.IModelObjectMatch;
import com.metamatrix.modeler.ui.search.MetadataMatchInfo;

/**
 * The <code>RelationshipMatch</code> class contains one relationship search match. The match may correspond to an {@link EObject}
 * that satisfied the search criteria. This class is used in the search result page.
 * 
 * @since 6.0.0
 */
public class RelationshipMatch extends Match implements IModelObjectMatch, UiConstants {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    private static final String PREFIX = I18nUtil.getPropertyPrefix(RelationshipMatch.class);

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The object found by the metadata search.
     * 
     * @since 6.0
     */
    private EObject eObject;

    /**
     * The full path to match object's parent.
     * 
     * @since 6.0
     */
    private String parentPath;

    private final RelationshipRecord record;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs a match given a relationship search result record.
     * 
     * @param info the match resource information
     * @param record the search result record
     * @since 6.0
     */
    public RelationshipMatch( MetadataMatchInfo info,
                              RelationshipRecord record ) {
        super(info, 0, 0);
        this.record = record;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof RelationshipMatch) {
            RelationshipMatch match = (RelationshipMatch)obj;
            RelationshipRecord thatRecord = (RelationshipRecord)match.getSearchRecord();
            return (this.record.getUri().equals(thatRecord.getUri()));
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.IModelObjectMatch#getEObject()
     */
    @Override
    public EObject getEObject() {
        if (eObject == null) {
            URI uri = URI.createURI(this.record.getUri());

            try {
                this.eObject = ModelerCore.getModelContainer().getEObject(uri, true);
                this.parentPath = ModelerCore.getModelEditor().getFullPathToParent(eObject).toString();
            } catch (CoreException e) {
                Util.log(e);
            }
        }

        return eObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.IModelObjectMatch#getMatchDescription()
     */
    @Override
    public String getMatchDescription() {
        return Util.getString(PREFIX + "description", record.getName(), record.getTypeName(), getResource().getName()); //$NON-NLS-1$
    }

    private IResource getResource() {
        return SearchPageUtil.getResource(record.getUri());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.IModelObjectMatch#getResourcePath()
     */
    @Override
    public String getResourcePath() {
        getEObject(); // this will make sure the path is set
        return this.parentPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.IModelObjectMatch#getSearchRecord()
     */
    @Override
    public SearchRecord getSearchRecord() {
        return this.record;
    }
}
