/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.product;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.core.ModelerCore;


/** 
 * @since 8.0
 */
public class DefaultProductCharacteristics implements IProductCharacteristics {
    private WorkbenchState defaultWorkbenchState;
    
    private static final String DEFAULT_EXPLORER_ID = "explorer.view"; //$NON-NLS-1$

    /** 
     * 
     * @since 4.3
     */
    public DefaultProductCharacteristics() {
        super();
    }

    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#workspaceLocationExposed()
     * @since 4.3
     */
    @Override
	public boolean workspaceLocationExposed() {
        return true;
    }

    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getPrimaryNavigationViewId()
     * @since 4.3
     */
    @Override
	public String getPrimaryNavigationViewId() {
        return DEFAULT_EXPLORER_ID;
    }
    
    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getDefaultPerspectiveId()
     * @since 5.0
     */
    @Override
	public String getDefaultPerspectiveId() {
        return null;
    }

    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getHiddenProject()
     * @since 4.3
     */
    @Override
	public IProject getHiddenProject() {
        return getHiddenProject(true);
    }

    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getHiddenProject(boolean)
     * @since 4.3
     */
    @Override
	public IProject getHiddenProject(boolean theCreateProjectFlag) {
        return null;
    }

   /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#isHiddenProjectCentric()
     * @since 4.3
     */
    @Override
	public boolean isHiddenProjectCentric() {
        return false;
    }
    
    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getCreateHiddenProjectWizardPage()
     * @since 4.4
     */
    @Override
	public IWizardPage getCreateHiddenProjectWizardPage() {
        return null;
    }
    
    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getWorkbenchState()
     * @since 5.0
     */
    public WorkbenchState getWorkbenchState() {
        if( defaultWorkbenchState == null ) {
            defaultWorkbenchState = new WorkbenchState();
        }
        return defaultWorkbenchState;
    }

    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getFileMruMenu()
     * @since 5.0
     */
    public ContributionItem getFileMruMenu() {
        return null;
    }

    /** 
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getRootWorkspaceContext()
     * @since 5.0
     */
    public Object getRootWorkspaceContext() {
        return ModelerCore.getWorkspace().getRoot();
    }

    /**
     *  
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getNewModelInput(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
	public Object getNewModelInput(ISelection theSelection) {
        return null;
    }

    /**
     *  
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#preProcess(java.lang.Object, org.eclipse.swt.widgets.Shell)
     * @since 5.0
     */
    @Override
	public boolean preProcess(Object theSomeObject,
                              Shell theShell) {
        // Default implementation is a pass-through:  TRUE
        return true;
    }
    

    /**
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#getObjectInfo(java.lang.Object, int)
     * @since 5.0
     */
    @Override
	public Object getObjectInfo( int infoType, 
                                  Object theSomeObject ) {
        // Default implementation is a no-op
        return null;
    }

    /**
     * @see org.teiid.designer.ui.common.product.IProductCharacteristics#setObjectInfo(java.lang.Object, int, java.lang.Object)
     * @since 5.0
     */
    @Override
	public void setObjectInfo(int infoType, 
                                Object theSomeObject, 
                                Object theValue) {
        // Default implementation is a no-op        
    }

}
