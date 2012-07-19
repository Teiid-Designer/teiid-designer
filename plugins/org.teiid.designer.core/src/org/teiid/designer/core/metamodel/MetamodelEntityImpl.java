/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;


/**
 * MetamodelEntityImpl
 *
 * @since 8.0
 */
public class MetamodelEntityImpl implements MetamodelEntity {
    
    private final URI metamodelURI;
    private final EClass eClass;
    private Map aspectMap;   // keyed on aspect id
    private List referencedClasses;
    private List containedClasses;
    private List attributes;
	private List uniReferences;

    /**
     * Construct an instance of MetamodelEntityImpl.
     * 
     */
    public MetamodelEntityImpl(final URI metamodelURI, final EClass eClass) {
        if (metamodelURI == null) {
            final String msg = ModelerCore.Util.getString("MetamodelEntityImpl.The_metamodel_URI_reference_may_not_be_null_1"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (eClass == null) {
            final String msg = ModelerCore.Util.getString("MetamodelEntityImpl.The_EObject_reference_may_not_be_null_1"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        this.metamodelURI = metamodelURI;
        this.eClass = eClass;
    }
    
    //#############################################################################
    //# MetamodelEntity method implementations
    //#############################################################################

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getDisplayName()
     */
    @Override
	public String getDisplayName() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getPluralDisplayName()
     */
    @Override
	public String getPluralDisplayName() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getIconPath()
     */
    @Override
	public String getIconPath() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getTooltip()
     */
    @Override
	public String getTooltip() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getMetamodelAspect(java.lang.String)
     */
    @Override
	public MetamodelAspect getMetamodelAspect(final String id) {
        if ( aspectMap == null ) {
            return null;
        }
        return (MetamodelAspect) aspectMap.get(id);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getMetamodelAspects()
     */
    @Override
	public Collection getMetamodelAspects() {
        if ( aspectMap == null ) {
            return Collections.EMPTY_LIST;
        }
        return aspectMap.values();
   }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getEClass()
     */
    @Override
	public EClass getEClass() {
        return this.eClass;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getAttributes()
     */
    @Override
	public List getAttributes() {
        final EClass metamodelClass = this.getEClass();
        if ( attributes == null ) {
            final List attribs = metamodelClass.getEAllAttributes();
            attributes = new ArrayList(attribs.size());
        }
        return attributes;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getContainedClasses()
     */
    @Override
	public List getContainedClasses() {
        final EClass metamodelClass = this.getEClass();
        if ( containedClasses == null ) {
            final List refs = metamodelClass.getEAllContainments();
            containedClasses = new ArrayList(refs.size());
            for (Iterator iter = refs.iterator(); iter.hasNext();) {
                final EReference eRef = (EReference) iter.next();
                if (eRef.getEType() instanceof EClass) {
                    containedClasses.add(eRef.getEType());
                }
            }
        }
        return containedClasses;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getReferencedClasses()
     */
    @Override
	public List getReferencedClasses() {
        final EClass metamodelClass = this.getEClass();
        if ( referencedClasses == null ) {
            final List refs = metamodelClass.getEAllReferences();
            referencedClasses = new ArrayList(refs.size());
            // Find all EClass instances that are referenced by this class
            for (Iterator iter = refs.iterator(); iter.hasNext();) {
                final EReference eRef = (EReference) iter.next();
                if ( !eRef.isContainment() && eRef.getEType() instanceof EClass) {
                    referencedClasses.add(eRef.getEType());
                }
            }
        }
        return referencedClasses;
    }

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getUniDirectionalReferences()
	 */
	@Override
	public List getUniDirectionalReferences() {
		final EClass metamodelClass = this.getEClass();
		if ( uniReferences == null ) {
			final List refs = metamodelClass.getEAllReferences();
			uniReferences = new ArrayList();
			// Find all references by this class that do not have an oppossite reference
			for (Iterator iter = refs.iterator(); iter.hasNext();) {
				final EReference eRef = (EReference) iter.next();
				if ( !eRef.isContainment() && eRef.getEOpposite() == null) {
					uniReferences.add(eRef);
				}
			}			
		}
		return uniReferences;
	}

    //#############################################################################
    //# public methods
    //#############################################################################

    public URI getMetamodelURI() {
        return this.metamodelURI;
    }

    public void addMetamodelAspect(final String id, final MetamodelAspect aspect) {
        if ( aspectMap == null ) {
            aspectMap = new HashMap();
        }
        aspectMap.put(id,aspect);
    }

}
