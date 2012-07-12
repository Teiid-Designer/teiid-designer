/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.search;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.search.ui.text.Match;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.search.runtime.RelationshipRecord;
import org.teiid.designer.core.search.runtime.SearchRecord;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.ui.search.IModelObjectMatch;
import org.teiid.designer.ui.search.MetadataMatchInfo;
import org.teiid.designer.ui.search.SearchPageUtil;


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
     * @see org.teiid.designer.ui.search.IModelObjectMatch#getEObject()
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
     * @see org.teiid.designer.ui.search.IModelObjectMatch#getMatchDescription()
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
     * @see org.teiid.designer.ui.search.IModelObjectMatch#getResourcePath()
     */
    @Override
    public String getResourcePath() {
        getEObject(); // this will make sure the path is set
        return this.parentPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.search.IModelObjectMatch#getSearchRecord()
     */
    @Override
    public SearchRecord getSearchRecord() {
        return this.record;
    }
}
