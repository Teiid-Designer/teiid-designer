/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.search.ui.text.Match;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * The <code>MetadataMatch</code> class contains one metadata search match. The match may correspond to an {@link EObject} that
 * satisfied the search criteria. This class is used in the search result page.
 * 
 * @since 6.0.0
 */
public final class MetadataMatch extends Match implements IModelObjectMatch, UiConstants {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    private static final String PREFIX = I18nUtil.getPropertyPrefix(MetadataMatch.class);

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

    private final ResourceObjectRecord record;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs a match given a metadata search result record.
     * 
     * @param info the match resource information
     * @param record the search result record
     * @since 6.0
     */
    public MetadataMatch( MetadataMatchInfo info,
                          ResourceObjectRecord record ) {
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
        if (obj instanceof MetadataMatch) {
            MetadataMatch match = (MetadataMatch)obj;
            return getObjectUri().equals(match.getObjectUri());
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
            URI uri = URI.createURI(getObjectUri());

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
        if (this.eObject == null) {
            return Util.getString(PREFIX + "description", getName(), getName(), getMetaclassUri()); //$NON-NLS-1$
        }

        return Util.getString(PREFIX + "description", getName(), getMetaclassUri(), getParentPath()); //$NON-NLS-1$
    }

    private String getMetaclassUri() {
        return this.record.getMetaclassURI();
    }

    /**
     * @return the match object's name (maybe <code>null</code>)
     */
    public String getName() {
        return this.record.getName();
    }

    /**
     * @return the match object's URI (never <code>null</code>)
     */
    private String getObjectUri() {
        String uri = this.record.getObjectURI();
        assert (uri != null);
        return uri;
    }

    /**
     * @return the full path of the match object or <code>null</code> if no match object found
     */
    private String getParentPath() {
        if (getEObject() == null) {
            return null;
        }

        return this.parentPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.IModelObjectMatch#getResourcePath()
     */
    @Override
    public String getResourcePath() {
        return this.record.getResourcePath();
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
