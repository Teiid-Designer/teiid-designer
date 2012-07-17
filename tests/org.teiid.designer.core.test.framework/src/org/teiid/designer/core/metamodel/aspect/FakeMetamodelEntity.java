/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.ecore.EClass;


/**
 * FakeMetamodelEntity
 */
public class FakeMetamodelEntity implements MetamodelEntity {

    /**
     * Construct an instance of FakeMetamodelEntity.
     * 
     */
    public FakeMetamodelEntity() {
        super();
    }

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
	public MetamodelAspect getMetamodelAspect(String id) {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getMetamodelAspects()
     */
    @Override
	public Collection getMetamodelAspects() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getEClass()
     */
    @Override
	public EClass getEClass() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getReferencedClasses()
     */
    @Override
	public List getReferencedClasses() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getContainedClasses()
     */
    @Override
	public List getContainedClasses() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getAttributes()
     */
    @Override
	public List getAttributes() {
        return null;
    }

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.MetamodelEntity#getUniDirectionalReferences()
	 */
	@Override
	public List getUniDirectionalReferences() {
		return null;
	}

}
