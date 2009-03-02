/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * This wizard is used to drive the creation of XSD and XML from Relational Selections.
 */

public class ConvertToEnterpriseTypesWizard extends AbstractWizard implements UiConstants {
    public static boolean HEADLESS = false; //Flag to set Wizard to run in headless mode for testing

    private final PluginUtil Util = UiConstants.Util;
    private final ModelEditor me = ModelerCore.getModelEditor();
    private final DatatypeManager dtMgr = ModelerCore.getWorkspaceDatatypeManager();
    private final StringBuffer messages = new StringBuffer();
    
    //The page for driving the user options.
    protected ConvertEnterpriseTypesPage convertTypesPage;
    
    //The current workspace selection
    protected ISelection selection;
    
    private IWizardPage[] wizardPageArray;    
    private Resource selectedResource;
    private ModelResource selectedModelResource;
    

    
    /**
     * Constructor for NewModelWizard.
     */
    public ConvertToEnterpriseTypesWizard() {
        super(UiPlugin.getDefault(), UiConstants.Util.getString("ConvertToEnterpriseTypesWizard.title"), null); //$NON-NLS-1$
        setNeedsProgressMonitor(false);
    }


// ************************** Wizard Methods **************************   
    
	/**
	 * Adding the page to the wizard.
	 */
	@Override
    public void addPages() {
        convertTypesPage = new ConvertEnterpriseTypesPage(this.selectedResource);
		addPage(convertTypesPage);
	}

	/**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as
     * execution context.
	 */
	@Override
    public boolean finish() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) {
                //Get the options and execute the build.
                doFinish(monitor);
            }
        };
        
        //Detmine TXN status and start one if required.
        //This operation is not undoable OR significant.
        final boolean startedTxn = ModelerCore.startTxn(false,
                                                        false,
                                                        ConvertToEnterpriseTypesWizard.this.getWindowTitle(),
                                                        ConvertToEnterpriseTypesWizard.this);
        try {            
            new ProgressMonitorDialog(getShell()).run(false, false, op);
        } catch (Throwable err) {
            Util.log(IStatus.ERROR, err, err.getMessage() );
        } finally {
            //This operation is NOT undoable or significant... ALWAYS comit to ensure
            //Nothing is left hanging.
            if(startedTxn) {
                ModelerCore.commitTxn();                        
            }            
        }

		return true;
	}
    	
	/**
     * The worker method. It will find the container, create the file(s) - Made this method public to allow for headless testing.
     * 
     * @param IPRogressMonitor - The progress monitor for this operation.
	 */

	public void doFinish(final IProgressMonitor monitor) {        
        if(convertTypesPage == null) {
            final String msg = UiConstants.Util.getString("ConvertToEnterpriseTypesWizard.noInit"); //$NON-NLS-1$
            messages.append(msg);
            return;
        }
        
        XSDSchema schema = null;
        final Iterator types = convertTypesPage.getTypesToConvert().iterator();
        while(types.hasNext() ) {
            final XSDSimpleTypeDefinition next = (XSDSimpleTypeDefinition)types.next();
            
            //turn of notifications for Schema until we are done
            if(schema == null) {
                schema = next.getSchema();
                schema.setIncrementalUpdate(false);
            }
            final EnterpriseDatatypeInfo edi =  getEDIForType(next);

            me.setEnterpriseDatatypePropertyValue(next, edi);
        }
        
        if(schema != null) {
            schema.setIncrementalUpdate(true);
        }
        
        try {
            if(selectedResource != null) {
                selectedResource.save(new HashMap() );
            }else if(selectedModelResource != null) {
                selectedModelResource.save(monitor, false);
            }
        } catch (Exception err) {
            final String msg = UiConstants.Util.getString("ConvertToEnterpriseTypesWizard.saveErr"); //$NON-NLS-1$
            messages.append(msg);
            Util.log(IStatus.ERROR, err, msg);
        }
        
        //Log the result
        if(this.messages.length() > 0) {
            Util.log(IStatus.ERROR, this.messages.toString() );
        }
	}
    
    private EnterpriseDatatypeInfo getEDIForType(final XSDSimpleTypeDefinition type) {
        final EnterpriseDatatypeInfo edi = new EnterpriseDatatypeInfo();
        
        XSDSimpleTypeDefinition superType = type;
        XSDSimpleTypeDefinition enterpriseParent = null;
        while(superType != null && enterpriseParent == null) {
            if(dtMgr.isEnterpriseDatatype(superType) ) {
                enterpriseParent = superType;
            }else {
                XSDSimpleTypeDefinition tmp = superType.getBaseTypeDefinition();
                if(tmp != superType) {
                    superType = superType.getBaseTypeDefinition();
                }else {
                    superType = null;
                }
            }
        }
        
        if(enterpriseParent != null) {       
            edi.setRuntimeTypeFixed(dtMgr.getRuntimeTypeFixed(enterpriseParent) );
            edi.setRuntimeType(dtMgr.getRuntimeTypeName(enterpriseParent) );
        }
        
        ModelEditorImpl.fillWithDefaultValues(edi, type);
        return edi;
    }
	
	/**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        this.selection = selection;
        if(SelectionUtilities.isSingleSelection(selection) ) {
            final Object sel = SelectionUtilities.getSelectedObject(selection);
            if(sel instanceof IFile) {
                final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)sel);
                this.selectedModelResource = modelResource;
                try {
                    this.selectedResource = modelResource.getEmfResource();
                } catch (ModelWorkspaceException err) {
                    //TODO log
                }
            }else if(sel instanceof Resource) {
                //This is for headless testing...
                this.selectedResource = (Resource)sel;
            }
        }
        
        if(this.selectedResource == null) {
            //TODO;
        }
    }
    
	        
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if ( page == convertTypesPage ) {
            return null;
        }
        
        for ( int i=0 ; i<wizardPageArray.length ; ++i ) {
            if ( wizardPageArray[i] == page ) {
                if ( i+1 < wizardPageArray.length ) {
                    return wizardPageArray[i+1];
                }
            }
        }
        return null;
    }
    

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * This Wizard can finish if the Options page is complete.
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if (currentPage == this.convertTypesPage) {
            result = currentPage.isPageComplete();
        } else {
            boolean lastPage = (currentPage == wizardPageArray[wizardPageArray.length - 1]);
            result = lastPage && currentPage.isPageComplete();
        }

        return result; 
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getPageCount()
     */
    @Override
    public int getPageCount() {
        if ( wizardPageArray != null ) {
            return wizardPageArray.length + 1;
        }
        return 1;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage(IWizardPage page) {
        
        if ( wizardPageArray == null || page == this.convertTypesPage ) {
            return null;
        }
        if ( page == wizardPageArray[0] ) {
            return this.convertTypesPage;
        }
        for ( int i=1 ; i<wizardPageArray.length ; ++i ) {
            if ( page == wizardPageArray[i] ) {
                return wizardPageArray[i-1];
            }
        }
        return null;
    }
    
    /**
     * A getter for the result message buffer. 
     * 
     * @return The results message buffer
     */
    public StringBuffer getMessages() {
        return this.messages;
    }
    
     
// ********************  Helper methods for building XML ********************
    
}
