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

package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect;

/**
 * ModelRecordImpl
 */
public class VdbRecordImpl extends com.metamatrix.metadata.runtime.impl.VdbRecordImpl {

    private static final long serialVersionUID = 6533785711718123816L;

    /**
	 * Flags to determine if values have been set.
	 */
	private boolean versionSet;
	private boolean identifierSet;
	private boolean descriptionSet;
	private boolean producerNameSet;
	private boolean producerVersionSet;
	private boolean providerSet;
	private boolean timeLastChangedSet;
	private boolean timeLastProducedSet;
	private boolean modelIDsSet;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public VdbRecordImpl(final SqlVdbAspect sqlAspect, final EObject eObject) {
        super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
		setRecordType(IndexConstants.RECORD_TYPE.VDB_ARCHIVE);
		this.eObject = eObject;
    }

	private SqlVdbAspect getVdbAspect() {
		return (SqlVdbAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getDescription()
     */
    @Override
    public String getDescription() {
		if(eObject != null && !descriptionSet) {
			setDescription(getVdbAspect().getDescription((EObject)eObject));
		}
        return super.getDescription();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getIdentifier()
     */
    @Override
    public String getIdentifier() {
		if(eObject != null && !identifierSet) {
			setIdentifier(getVdbAspect().getIdentifier((EObject)eObject));
		}
        return super.getIdentifier();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getModelIDs()
     */
    @Override
    public List getModelIDs() {
		if(eObject != null && !modelIDsSet) {
			setModelIDs(getVdbAspect().getModelIDs((EObject)eObject));
		}
        return super.getModelIDs();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getProducerName()
     */
    @Override
    public String getProducerName() {
		if(eObject != null && !producerNameSet) {
			setProducerName(getVdbAspect().getProducerName((EObject)eObject));
		}
        return super.getProducerName();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getProducerVersion()
     */
    @Override
    public String getProducerVersion() {
		if(eObject != null && !producerVersionSet) {
			setProducerVersion(getVdbAspect().getProducerVersion((EObject)eObject));
		}
        return super.getProducerVersion();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getProvider()
     */
    @Override
    public String getProvider() {
		if(eObject != null && !providerSet) {
			setProvider(getVdbAspect().getProvider((EObject)eObject));
		}
        return super.getProvider();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getTimeLastChanged()
     */
    @Override
    public String getTimeLastChanged() {
		if(eObject != null && !timeLastChangedSet) {
			setTimeLastChanged(getVdbAspect().getTimeLastChanged((EObject)eObject));
		}
        return super.getTimeLastChanged();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getTimeLastProduced()
     */
    @Override
    public String getTimeLastProduced() {
		if(eObject != null && !timeLastProducedSet) {
			setTimeLastProduced(getVdbAspect().getTimeLastProduced((EObject)eObject));
		}
        return super.getTimeLastProduced();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.VdbRecord#getVersion()
     */
    @Override
    public String getVersion() {
		if(eObject != null && !versionSet) {
			setVersion(getVdbAspect().getVersion((EObject)eObject));
		}
        return super.getVersion();
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    @Override
    public void setDescription(String string) {
        super.setDescription(string);
		descriptionSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setIdentifier(String string) {
        super.setIdentifier(string);
		identifierSet = true;
    }

    /**
     * @param list
     */
    @Override
    public void setModelIDs(List list) {
        super.setModelIDs(list);
		modelIDsSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setProducerName(String string) {
        super.setProducerName(string);
		producerNameSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setProducerVersion(String string) {
        super.setProducerVersion(string);
		producerVersionSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setProvider(String string) {
        super.setProvider(string);
		providerSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setTimeLastChanged(String string) {
        super.setTimeLastChanged(string);
		timeLastChangedSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setTimeLastProduced(String string) {
        super.setTimeLastProduced(string);
		timeLastProducedSet = true;
    }

    /**
     * @param string
     */
    @Override
    public void setVersion(String string) {
        super.setVersion(string);
		versionSet = true;
    }

}
