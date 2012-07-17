/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.association;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;


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
        CoreArgCheck.isNotNull(eObjects);
        this.eObjects  = eObjects;
        this.status    = new Status(IStatus.OK, PLUGIN_ID, -1, "", null); //$NON-NLS-1$
        this.children  = null;
        this.ambiguous = false;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @See org.teiid.designer.core.association.AssociationDescriptor#getImage()
     */
    @Override
	public abstract Object getImage();

    /* (non-Javadoc)
     * @See org.teiid.designer.core.association.AssociationDescriptor#getText()
     */
    @Override
	public abstract String getText();


    /* (non-Javadoc)
     * @See org.teiid.designer.core.association#isComplete()
     */
    @Override
	public abstract boolean isComplete();

    /* (non-Javadoc)
     * @See org.teiid.designer.core.association#creationComplete()
     */
    @Override
	public boolean creationComplete() {
        return this.creationComplete;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.association#setCreationComplete()
     */
    @Override
	public void setCreationComplete(boolean complete) {
        this.creationComplete = complete;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.association#getType()
     */
    @Override
	public abstract String getType();

    /* (non-Javadoc)
     * @See org.teiid.designer.core.association.AssociationDescriptor#getChildren()
     */
    @Override
	public AssociationDescriptor[] getChildren() {
        if (children == null || children.size() == 0) {
            return EMPTY_ARRAY;
        }
        AssociationDescriptor[] result = new AssociationDescriptor[children.size()];
        children.toArray(result);
        return result;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.association.AssociationDescriptor#getStatus()
     */
    @Override
	public IStatus getStatus() {
        return this.status;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.association.AssociationDescriptor#isAmbiguous()
     */
    @Override
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
        CoreArgCheck.isNotNull(msg);
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
     * @see org.teiid.designer.core.association.AssociationDescriptor#getNewAssociation()
     * @since 4.3
     */
    @Override
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
