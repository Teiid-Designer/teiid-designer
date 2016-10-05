/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.jdg;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.IProgressConstants;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.tools.textimport.ui.wizards.AbstractObjectProcessor;
import org.teiid.designer.transformation.materialization.MaterializedModelManager;
import org.teiid.designer.transformation.reverseeng.ReverseEngConstants;
import org.teiid.designer.transformation.reverseeng.ReverseEngineerFactory;
import org.teiid.designer.transformation.reverseeng.api.Options;
import org.teiid.designer.transformation.reverseeng.api.Options.Parms;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.editors.ModelEditorManager;

public class MaterializationWizard extends AbstractWizard
		implements INewWizard, InternalUiConstants.Widgets, CoreStringUtil.Constants, UiConstants, ReverseEngConstants {

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(MaterializationWizard.class);

	private static final String TITLE = UiPlugin.getDefault().getString(I18N_PREFIX, "title"); //$NON-NLS-1$

	IStructuredSelection initialSelection;

	private MaterializedModelManager manager;
	
	private MaterializationWizardPage_1 page_1;
	private MaterializationWizardPage_2 page_2;
//	private PojoWizardPage_3 page_3;
	
	Mode mode;

	/**
	 * @since 4.0
	 */
	public MaterializationWizard(Mode mode) {
		super(UiPlugin.getDefault(), TITLE, null);
		
		this.mode = mode;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 * @since 4.0
	 */
	@Override
	public boolean finish() {
		final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) throws InvocationTargetException {
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
				err = ((InvocationTargetException) err).getTargetException();
			}
			Util.log(err);
			WidgetUtil.showError(UiPlugin.getDefault().getString(I18N_PREFIX, "errorCreatingMaterializedViews")); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 * @since 4.0
	 */
	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {

		initialSelection = new StructuredSelection(selection.toArray());

		this.manager = new MaterializedModelManager((EObject)initialSelection.toList().get(0), this.mode);
		
		if( this.mode == Mode.MATERIALIZE) {
			this.page_1 = new MaterializationWizardPage_1(this.manager);
			addPage(page_1);
			
			this.page_2 = new MaterializationWizardPage_2(this.manager);
			addPage(page_2);
		} else {
			this.page_2 = new MaterializationWizardPage_2(this.manager);
			addPage(page_2);
		}
		

//		
//		this.page_3 = new PojoWizardPage_3(this.manager);
//		addPage(page_3);

	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#canFinish()
	 * @since 4.0
	 */
	@Override
	public boolean canFinish() {
		// defect 16154 -- Finish can be enabled even if errors on page.
		// check the page's isComplete status (in super) -- just follow its
		// advice.
		
		return super.canFinish();
	}

	Composite createEmptyPageControl(final Composite parent) {
		return new Composite(parent, SWT.NONE);
	}


//	private boolean isAllVirtualTablesSelected(final ISelection selection) {
//		boolean isValid = true;
//		if (SelectionUtilities.isEmptySelection(selection) || !SelectionUtilities.isAllEObjects(selection))
//			isValid = false;
//
//		if (isValid) {
//			final Collection<?> objs = SelectionUtilities.getSelectedEObjects(selection);
//			final Iterator<?> selections = objs.iterator();
//			while (selections.hasNext() && isValid) {
//				final EObject next = (EObject) selections.next();
//
//				if (isRelationalVirtualTable(next) && TransformationHelper.isVirtualSqlTable(next)) {
//					isValid = true;
//				} else
//					isValid = false;
//
//				// stop processing if no longer valid:
//				if (!isValid)
//					break;
//			} // endwhile -- all selected
//		} else
//			isValid = false;
//
//		return isValid;
//	}

//	private boolean isRelationalVirtualTable(EObject eObject) {
//		// Do a quick object check
//		if (TransformationHelper.isVirtualSqlTable(eObject)) {
//			// make sure it's a virtual relational model
//			final Resource resource = eObject.eResource();
//			if (resource != null) {
//				ModelResource mr = ModelUtilities.getModelResource(resource, true);
//				return ModelIdentifier.isRelationalViewModel(mr);
//			}
//		}
//		return false;
//	}

	private boolean runAsJob() {
		final String message = UiPlugin.getDefault().getString(I18N_PREFIX, "progressMonitorTitle"); //$NON-NLS-1$

		final Job job = new Job(message) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask(message, 1);

					if (!monitor.isCanceled()) {
						execute(monitor);
					}

					monitor.done();

					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}

					return new Status(IStatus.OK, UiConstants.PLUGIN_ID, IStatus.OK, AbstractObjectProcessor.FINISHED,
							null);
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

	private boolean execute(IProgressMonitor monitor) {
		boolean requiredStart = ModelerCore.startTxn(false, false, "Generate JDG Materialized Module", this); //$NON-NLS-1$
		boolean succeeded = false;
		try {
			
			if( !manager.isPojoMode() ) {
				manager.execute();

	            // need to save view model if open in editor.
				IEditorPart editor = UiUtil.getEditorForFile((IFile)manager.getMaterializedViewModel().getUnderlyingResource(), false);
				if( editor != null && editor.isDirty() ) {
					editor.doSave(new NullProgressMonitor());
				}
			}
			
			if( this.manager.doCreatePojo() ) {
				processPojo();
			}
			
			succeeded = true;
		} catch (Exception ex) {
			UiConstants.Util.log(IStatus.ERROR, ex,
					UiPlugin.getDefault().getString(I18N_PREFIX, "errorCreatingMaterializedViews")); //$NON-NLS-1$
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
			ModelEditorManager.activate(this.manager.getMaterializedViewModel(), true);
			IContainer projectLocation = this.manager.getPojoWorkspaceFolder();
			if( projectLocation != null ) {
				try {
					projectLocation.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				} catch (CoreException e) {
					UiConstants.Util.log(e);
				}
			}
		}

		return succeeded;
	}
	
	private void processPojo() {
		try {
			Options newOptions = new Options();
			IContainer projectLocation = this.manager.getPojoWorkspaceFolder();
			
			newOptions.setProperty(Parms.BUILD_LOCATION,  
					projectLocation.getLocation().toOSString() + MaterializedModelManager.DEFAULT_JDG_MODULE_FOLDER);
			  
			newOptions.setProperty(Options.Parms.MODULE_ZIP_FILE, manager.getModuleZipFileName());
			
			newOptions.setProperty(Parms.POJO_PACKAGE_NAME, manager.getPojoPackageName());
			
			String pojoJarName = this.manager.getPojoClassName() + ".jar"; //
			newOptions.setProperty(Parms.POJO_JAR_FILE, pojoJarName);
			newOptions.setProperty(Parms.POJO_CLASS_NAME, this.manager.getPojoClassName());
			
			String annotationType = manager.getAnnotationType();
			
			if( annotationType.equalsIgnoreCase(MaterializedModelManager.PROTOBUF) ){
				newOptions.setAnnotationType(Options.Annotation_Type.Protobuf);
			} 
			// TODO: enable HIBERNATE 
//			else if( annotationType.equalsIgnoreCase(MaterializedModelManager.HIBERNATE) ){
//				newOptions.setAnnotationType(Options.Annotation_Type.Hibernate);
//			}
			
			if( this.manager.doGenerateModule() ) {
				newOptions.setProperty(Options.Parms.GENERATE_MODULE, Boolean.TRUE.toString());
			}
			
			ReverseEngineerFactory.perform((Table)manager.getVirtualTable(), newOptions);

		} catch (Exception e) {
			UiConstants.Util.log(e);
		}
	}
}
