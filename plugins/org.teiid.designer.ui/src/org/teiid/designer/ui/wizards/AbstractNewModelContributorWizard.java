/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.product.IProductCharacteristics;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.wizard.AbstractWizard;



/** 
 * @since 8.0
 */
public abstract class AbstractNewModelContributorWizard extends AbstractWizard implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private IFile file;
    
    private IWizardPage hiddenProjectPage;
    
    private ModelResource model;
    
    private AbstractUiPlugin plugin;
    
    protected ISelection selection;
    
    private IContainer modelContainer;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public AbstractNewModelContributorWizard(AbstractUiPlugin thePlugin,
                                             String theTitle,
                                             ImageDescriptor theImage,
                                             ModelResource theNewModel) {
        super(thePlugin, theTitle, theImage);
        this.model = theNewModel;
        this.file = (IFile)this.model.getResource();
        this.modelContainer = this.file.getParent();
    }
    
    public AbstractNewModelContributorWizard(AbstractUiPlugin thePlugin,
                                             String theTitle,
                                             ImageDescriptor theImage,
                                             ModelResource theNewModel,
                                             ISelection theSelection) {
        this(thePlugin, theTitle, theImage, theNewModel);
        this.selection = theSelection;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     * @since 5.0.2
     */
    @Override
    public void addPages() {
        IProductCharacteristics characteristics = ProductCustomizerMgr.getInstance().getProductCharacteristics();

        if (characteristics.isHiddenProjectCentric() && (characteristics.getHiddenProject(false) == null)) {
            this.hiddenProjectPage = characteristics.getCreateHiddenProjectWizardPage();
                        
            if (this.hiddenProjectPage != null) {
                addPage(this.hiddenProjectPage);
            }
        }
        
        // now create/obtain contributor pages
        getContributor().createWizardPages(getSelection(),
                                           getModelContainer(),
                                           getModelPath(),
                                           getMetamodelDescriptor(),
                                           isVirtual());
        IWizardPage[] pages = getContributor().getWizardPages();
        
        for (int i = 0; i < pages.length; ++i) {
            addPage(pages[i]);
        }
    }
    
    /** 
     * @see org.teiid.designer.ui.common.wizard.AbstractWizard#canFinish()
     * @since 5.0.2
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if ((this.hiddenProjectPage == null) || ((this.hiddenProjectPage != null) && (this.hiddenProjectPage != currentPage))) {
            result = getContributor().canFinishEarly(currentPage);
        }

        return result;
    }
    
    /** 
     * @see org.teiid.designer.ui.common.wizard.AbstractWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     * @since 5.0.2
     */
    @Override
    public void createPageControls(Composite thePageContainer) {
        super.createPageControls(thePageContainer, false);
    }
    
    /**
     * Contributor  
     * 
     * @param modelResource
     * @param theMonitor
     * @throws CoreException
     * @since 5.0.2
     */
    protected void doFinish(final ModelResource modelResource,
                             final IProgressMonitor theMonitor ) {
        getShell().getDisplay().syncExec(new Runnable() {
            @Override
			public void run() {
                getContributor().doFinish(modelResource, theMonitor);
            }
        });
    }
    
    /** 
     * @see org.teiid.designer.ui.common.wizard.AbstractWizard#finish()
     * @since 5.0.2
     */
    @Override
    public final boolean finish() {
        final ModelResource modelResource = this.model;
        
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute(IProgressMonitor theMonitor) throws InvocationTargetException {
                try {
                    doFinish(modelResource, theMonitor);
                } catch (Exception theException) {
                    throw new InvocationTargetException(theException);
                } finally {
                    theMonitor.done();
                }
            }
        };

        boolean startedTxn = ModelerCore.startTxn(false, false, getWindowTitle(), this);
        boolean success = true;        

        try {
            getContainer().run(false, true, op);
        } catch (InterruptedException theException) {
            success = false;
        } catch (InvocationTargetException theException) {
            final String PREFIX = I18nUtil.getPropertyPrefix(AbstractNewModelContributorWizard.class);
            success = false;
            String msg = theException.getTargetException().getLocalizedMessage();
            
            if (CoreStringUtil.isEmpty(msg)) {
                msg = Util.getString(PREFIX + "noDetailsMsg"); //$NON-NLS-1$
            }
            
            MessageDialog.openError(getShell(), Util.getString(PREFIX + "problemDialog.title"), msg); //$NON-NLS-1$
        } finally {
            if (startedTxn) {
                if (success) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return success;
    }
    
    /**
     * Obtains the <code>INewModelWizardContributor</code> for this wizard.
     * 
     * @return the contributor
     * @since 5.0.2
     */
    protected abstract INewModelWizardContributor getContributor();
    
    /**
     * Obtains the <code>IFile</code> associated with the new model.
     * 
     * @return the file
     * @since 5.0.2
     */
    protected IFile getFile() {
        return this.file;
    }
    
    /**
     * Obtains the <code>MetamodelDescriptor</code> associated with the new model being created.
     * 
     * @return the model descriptor
     * @since 5.0.2
     */
    protected abstract MetamodelDescriptor getMetamodelDescriptor();
    
    /**
     * Obtains the new model's container. 
     * 
     * @return the container
     * @since 5.0.2
     */
    protected IContainer getModelContainer() {
        return this.modelContainer;
    }
    
    protected String getModelShortName() {
        return this.file.getProjectRelativePath().removeFileExtension().lastSegment();
    }
    
    /**
     * Obtains the new model's path. 
     * 
     * @return the path
     * @since 5.0.2
     */
    protected IPath getModelPath() {
        return this.model.getPath();
    }
    
    /** 
     * Obtains the new model the contributor is modifying.
     * 
     * @return the model
     * @since 5.0.2
     */
    public ModelResource getModelResource() {
        return this.model;
    }
    
    /** 
     * @see org.teiid.designer.ui.common.wizard.AbstractWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     * @since 5.0.2
     */
    @Override
    public IWizardPage getNextPage(IWizardPage thePage) {
        getContributor().currentPageChanged(thePage);
        return super.getNextPage(thePage);
    }
    
    /**
     * Obtains the <code>INewModelWizardContributor</code> for this wizard.
     * 
     * @return the plugin
     * @since 5.0.2
     */
    protected AbstractUiPlugin getPlugin() {
        return this.plugin;
    }
    
    /**
     * Obtains the workspace <code>ISelection</code> for this wizard.
     * 
     * @return the plugin
     * @since 5.0.2
     */
    protected ISelection getSelection() {
        if (this.selection == null) {
            this.selection = StructuredSelection.EMPTY;
        }
        
        return this.selection;
    }
    
    /**
     * Indicates if the model being created is virtual. 
     * 
     * @return <code>true</code> if virtual; <code>false</code> otherwise.
     * @since 5.0.2
     */
    protected abstract boolean isVirtual();
    
    /** 
     * @see org.eclipse.jface.wizard.Wizard#performCancel()
     * @since 5.0.2
     */
    @Override
    public boolean performCancel() {
        getContributor().doCancel();
        return super.performCancel();
    }
    
}
