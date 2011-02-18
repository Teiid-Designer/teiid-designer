/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.IProgressConstants;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.relational.ui.textimport.RelationalTableLocationSelectionValidator;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractObjectProcessor;
import com.metamatrix.modeler.transformation.materialization.MaterializationModelGenerator;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.ListContentProvider;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

public class CreateMaterializedViewWizard extends AbstractWizard
    implements INewWizard, InternalUiConstants.Widgets, CoreStringUtil.Constants, UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(CreateMaterializedViewWizard.class);

    private static final String TITLE = UiPlugin.getDefault().getString(I18N_PREFIX, "title");  //$NON-NLS-1$
    private static final String PAGE_TITLE = UiPlugin.getDefault().getString(I18N_PREFIX, "pageTitle"); //$NON-NLS-1$
    private static final String UNDEFINED = UiPlugin.getDefault().getString(I18N_PREFIX, "undefined"); //$NON-NLS-1$
    
    private static final int COLUMN_COUNT = 3;

    
    private WizardPage wizardPage;
    private Text mvModelText, mvLocationText;
    private Button btnBrowse;

    IStructuredSelection initialSelection;
    
    Object mvTableLocation;
    
    private MaterializationModelGenerator generator;

    /**
     * @since 4.0
     */
    public CreateMaterializedViewWizard() {
        super(UiPlugin.getDefault(), TITLE, null);
    }
    
    
    /**
     * @see org.eclipse.jfac
     * e.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            @SuppressWarnings("unchecked")
			public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    runAsJob();
                } catch (final Exception err) {
                    throw new InvocationTargetException(err);
                } finally {
                    monitor.done();
                }
            }
        };
        
        try {
            new ProgressMonitorDialog(getShell()).run(false, true, op);
            return true;
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                err = ((InvocationTargetException)err).getTargetException();
            }
            Util.log(err);
            WidgetUtil.showError(UiPlugin.getDefault().getString(I18N_PREFIX, "errorCreatingMaterializedViews")); //$NON-NLS-1$
            return false;
        }
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
    	
        if (isAllVirtualTablesSelected(selection)) {
            initialSelection = new StructuredSelection(selection.toArray());
        }
        this.wizardPage = new WizardPage(CreateMaterializedViewWizard.class.getSimpleName(), PAGE_TITLE, null) {
            public void createControl( final Composite parent ) {
                setControl(createPageControl(parent));
            }
        };
        
        this.wizardPage.setPageComplete(false);
        this.wizardPage.setMessage(UiPlugin.getDefault().getString(I18N_PREFIX, "initialMessage")); //$NON-NLS-1$
        addPage(wizardPage);
        
        this.generator = new MaterializationModelGenerator();
        this.generator.setVirtualTables(initialSelection.toList());
        
        validatePage();
    }
    
    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        // defect 16154 -- Finish can be enabled even if errors on page.
        // check the page's isComplete status (in super) -- just follow its advice.
        return super.canFinish();
    }

    Composite createEmptyPageControl( final Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }
    
    /**
     * @param parent 
     * @return composite the page
     * @since 4.0
     */
    @SuppressWarnings("unchecked")
	Composite createPageControl( final Composite parent ) {
        // Create page
        final Composite pg = new Composite(parent, SWT.NONE);
        pg.setLayout(new GridLayout(COLUMN_COUNT, false));
        // Add widgets to page
        WidgetFactory.createLabel(pg, UiPlugin.getDefault().getString(I18N_PREFIX, "modelLabel")); //$NON-NLS-1$
        
        this.mvModelText = WidgetFactory.createTextField(pg, GridData.FILL_HORIZONTAL, 1, UNDEFINED, SWT.READ_ONLY);

        btnBrowse = WidgetFactory.createButton(pg, BROWSE_BUTTON);
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                browseForModelSelected();
            }
        });
        WidgetFactory.createLabel(pg, UiPlugin.getDefault().getString(I18N_PREFIX, "targetLocationLabel")); //$NON-NLS-1$
        this.mvLocationText = WidgetFactory.createTextField(pg, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.mvLocationText.setText(UNDEFINED);
        
        btnBrowse.setFocus();

        
        if( isAllVirtualTablesSelected() ) {
	        Group group = WidgetFactory.createGroup(pg, 
	        		UiPlugin.getDefault().getString(I18N_PREFIX, "virtualTablesGroup"),  //$NON-NLS-1$
	        		GridData.FILL_BOTH, COLUMN_COUNT, COLUMN_COUNT);
	        TableViewer viewer = new TableViewer(group);
	        GridData gdv = new GridData(GridData.FILL_BOTH);
	        //gdv.horizontalSpan = COLUMN_COUNT;
	        viewer.getControl().setLayoutData(gdv);
	        viewer.setContentProvider(new ListContentProvider());
	        viewer.setLabelProvider(new ModelExplorerLabelProvider());
	        List selectedModels = SelectionUtilities.getSelectedEObjects(initialSelection);
	        viewer.setInput(selectedModels);
        }
        
        
        return pg;
    }
    
    private boolean isAllVirtualTablesSelected( final ISelection selection ) {
        boolean isValid = true;
        if (SelectionUtilities.isEmptySelection(selection) || !SelectionUtilities.isAllEObjects(selection)) isValid = false;

        if (isValid ) {
            final Collection objs = SelectionUtilities.getSelectedEObjects(selection);
            final Iterator selections = objs.iterator();
            while (selections.hasNext() && isValid) {
                final EObject next = (EObject)selections.next();
                
                if ( isRelationalVirtualTable(next) && TransformationHelper.isVirtualSqlTable(next) ) {
                	isValid = true;
                } else isValid = false;

                // stop processing if no longer valid:
                if (!isValid) break;
            } // endwhile -- all selected
        } else isValid = false;

        return isValid;
    }
    private boolean isAllVirtualTablesSelected( ) {
    	return isAllVirtualTablesSelected(this.initialSelection);
    	
    }
    
    private boolean isRelationalVirtualTable( EObject eObject ) {
    	// Do a quick object check
    	if( TransformationHelper.isVirtualSqlTable(eObject)) {
    		// make sure it's a virtual relational model
	        final Resource resource = eObject.eResource();
	        if (resource != null ) {
	        	ModelResource mr = ModelUtilities.getModelResource(resource, true);
	        	return ModelIdentifier.isRelationalViewModel(mr);
	        }
    	}
        return false;
    }
    
    void browseForModelSelected() {
    	Object result = null;
    	
    	RelationalModelSelectorDialog mwdDialog = new RelationalModelSelectorDialog(
                UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell());
		mwdDialog.setValidator(new RelationalTableLocationSelectionValidator());
		mwdDialog.setAllowMultiple(false);
		mwdDialog.open();
		
		if (mwdDialog.getReturnCode() == Window.OK) {
			Object[] oSelectedObjects = mwdDialog.getResult();
			
			// add the selected location to this Relationship
			if (oSelectedObjects.length > 0) {
				result = oSelectedObjects[0];
			}
		}

        this.generator.setTargetLocation(result);
        
        validatePage();
        
        updateWidgetValues();
    }
    
    private void updateWidgetValues() {
    	this.mvModelText.setText(this.generator.getModelName());
    	this.mvLocationText.setText(this.generator.getLocationName());
    }
    
    private void validatePage() {
    	
    	// For materialized views to be created we'll need
    	// 1) Valid Relational model, so query user for existing or to create new
    	// 2) Valid container within that model. Could be schema OR just the model
    	// 3) Maybe other validation checks?  Already a materialized view?
    	
    	IStatus currentStatus = this.generator.getExecuteStatus();
    	
    	if( currentStatus.isOK() ) {
            this.wizardPage.setErrorMessage(null);
            this.wizardPage.setMessage(currentStatus.getMessage());
            this.wizardPage.setPageComplete(true);
    	} else if( currentStatus.getSeverity() == IStatus.WARNING){
    		this.wizardPage.setMessage("WARNING: " + currentStatus.getMessage()); //$NON-NLS-1$
    		this.wizardPage.setPageComplete(true);
    	} else {
    		this.wizardPage.setErrorMessage(currentStatus.getMessage());
    		this.wizardPage.setPageComplete(false);
    	}

    }
    
    private boolean runAsJob() {
        final String message = UiPlugin.getDefault().getString(I18N_PREFIX, "progressMonitorTitle"); //$NON-NLS-1$
        
        final Job job = new Job(message) {
            @Override
            protected IStatus run( IProgressMonitor monitor ) {
                try {
                    monitor.beginTask(message, generator.getVirtualTables().size());

                    if (!monitor.isCanceled()) {
                        execute(monitor);
                    }

                    monitor.done();

                    if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }

                    return new Status(IStatus.OK, UiConstants.PLUGIN_ID, IStatus.OK, AbstractObjectProcessor.FINISHED, null);
                } catch (Exception e) {
                    UiConstants.Util.log(e);
                    return new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, IStatus.ERROR, 
                    		UiPlugin.getDefault().getString(I18N_PREFIX, "errorCreatingMaterializedViews"), e); //$NON-NLS-1$
                } finally {
                }
            }
        };

        job.setSystem(false);
        job.setUser(true);
        job.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
        // start as soon as possible
        job.schedule();
        return true;
    }
    
    private boolean execute(IProgressMonitor monitor ) {
        boolean requiredStart = ModelerCore.startTxn(false, false, "Create Materialized View Model", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	
        	generator.execute();
        	
        	succeeded = true;
        } catch (Exception ex) {
            UiConstants.Util.log(IStatus.ERROR, ex, UiPlugin.getDefault().getString(I18N_PREFIX, "errorCreatingMaterializedViews")); //$NON-NLS-1$
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded && !monitor.isCanceled()) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        if (succeeded) {
            ModelEditorManager.activate(this.generator.getMaterializedViewModel(), true);
        }

        return succeeded;
    }
}
