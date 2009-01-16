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

package com.metamatrix.modeler.internal.core.association;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.association.AssociationDescriptor;

/**
 * AbstractAssociationDescriptor
 */
public abstract class AbstractAssociationDescriptor implements AssociationDescriptor {

    protected static final AssociationDescriptor[] EMPTY_ARRAY = new AssociationDescriptor[0];
    private static final String PLUGIN_ID = ModelerCore.PLUGIN_ID;

    private EObject newAssociation;
    private List eObjects;
    private IStatus status;
    private ArrayList children;
    private boolean ambiguous;
    private boolean creationComplete;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    protected AbstractAssociationDescriptor(final List eObjects) {
        ArgCheck.isNotNull(eObjects);
        this.eObjects  = eObjects;
        this.status    = new Status(IStatus.OK, PLUGIN_ID, -1, "", null); //$NON-NLS-1$
        this.children  = null;
        this.ambiguous = false;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getImage()
     */
    public abstract Object getImage();

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getText()
     */
    public abstract String getText();


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association#isComplete()
     */
    public abstract boolean isComplete();

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association#creationComplete()
     */
    public boolean creationComplete() {
        return this.creationComplete;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association#setCreationComplete()
     */
    public void setCreationComplete(boolean complete) {
        this.creationComplete = complete;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association#getType()
     */
    public abstract String getType();

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getChildren()
     */
    public AssociationDescriptor[] getChildren() {
        if (children == null || children.size() == 0) {
            return EMPTY_ARRAY;
        }
        AssociationDescriptor[] result = new AssociationDescriptor[children.size()];
        children.toArray(result);
        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getStatus()
     */
    public IStatus getStatus() {
        return this.status;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#isAmbiguous()
     */
    public boolean isAmbiguous() {
        return this.ambiguous;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * Create a new association instance using information available through
     * this descriptor.
     * @return
     */
    public abstract EObject create() throws ModelerCoreException;

    /**
     * Return false if the list of objects contains model entities that would
     * prevent an association of this type from being created.
     * @return
     */
    public abstract boolean canCreate();

    /**
     * Return the list of model entities for this descriptor.
     * @return
     */
    public List getEObjects() {
        return this.eObjects;
    }

    /**
     * @param b
     */
    public void setAmbiguous(boolean b) {
        this.ambiguous = b;
    }

    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================

    public void updateStatus(final IStatus s){
        if (s != null) {
            if (this.status.isMultiStatus()) {
                final MultiStatus multiStatus = (MultiStatus)this.status;
                if(s.isMultiStatus()) {
                    multiStatus.addAll(s);
                } else {
                    multiStatus.add(s);
                }
            } else {
                this.status = s;
            }
        }
    }

    public void updateStatus(final int severity, final int code, final String msg, final Throwable e){
        ArgCheck.isNotNull(msg);
        updateStatus( new Status(severity, PLUGIN_ID, code, msg, e) );
    }

    public void addDescriptor(final AssociationDescriptor descriptor) {
        if (children == null) {
            children = new ArrayList();
        }
        if (!children.contains(descriptor)) {
            children.add(descriptor);
        }
    }


    /**
     *
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getNewAssociation()
     * @since 4.3
     */
    public EObject getNewAssociation() {
        return this.newAssociation;
    }


    /**
     * @param association The association to set.
     * @since 4.3
     */
    public void setAssociation(EObject association) {
        this.newAssociation = association;
    }

}
