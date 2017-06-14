/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelTransformations;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.util.DependencyReportDialog;
import org.teiid.designer.transformation.ui.util.SqlDependencyHelper;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * GenerateDependencyReportAction
 *
 * @since 8.0
 */
public class GenerateDependencyReportAction extends TransformationAction implements UiConstants {

    static final String PREFIX = I18nUtil.getPropertyPrefix(GenerateDependencyReportAction.class);

    /**
     * Construct an instance of GenerateDependencyReportAction.
     */
    public GenerateDependencyReportAction() {
        super(null);
        this.setUseWaitCursor(false);
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged( final IWorkbenchPart part,
                                  final ISelection selection ) {
        super.selectionChanged(part, selection);
        determineEnablement();
    }

    @Override
	public void selectionChanged(SelectionChangedEvent theEvent) {
		super.selectionChanged(theEvent);
		determineEnablement();
	}

	/**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
        if (eObject != null) {
            // See if it has a transformation object:
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
            if (modelResource != null && ModelUtilities.isVirtual(modelResource)) {
                try {

                    List transformations = modelResource.getModelTransformations().getTransformations(eObject);
                    if (transformations != null && transformations.size() == 1) {
                        // show dialog
                        DependencyReportDialog dialog = new DependencyReportDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(),
                                                                                   eObject);

                        if (dialog.open() == Window.OK) {
                            SqlDependencyHelper helper = new SqlDependencyHelper(eObject, dialog.isIncludeIntermediate(),
                                                                                 dialog.getColumnDelimeter(),
                                                                                 dialog.getNullValue());
                            writeReport(dialog.getReportFileName(), helper.getFileString());
                        }
                    }
                } catch (ModelWorkspaceException e) {
                    String message = Util.getString("GenerateDependencyReportAction.generateDependencyReportError", modelResource.toString()); //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
                }
            }
        }
        determineEnablement();
    }

    public void writeReport( final String theFileName,
                             final String theContents ) {
        // write file
        UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            @Override
			public void run() {
                try {
                    File file = new File(theFileName);

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    if (file.canWrite()) {
                        Writer output = null;

                        try {
                            output = new BufferedWriter(new FileWriter(file));
                            output.write(theContents);
                        } catch (IOException theException) {
                            Util.log(theException);
                            WidgetUtil.showError(theException);
                        } finally {
                            if (output != null) {
                                output.close();
                            }
                        }
                    } else {
                        WidgetUtil.showError(Util.getString(PREFIX + "dialog.errorMsg", new Object[] {theFileName})); //$NON-NLS-1$
                    }
                } catch (Exception theException) {
                    Util.log(theException);
                    WidgetUtil.showError(theException);
                }
            }
        });
    }

    /**
     * @since 4.0
     */
    private void determineEnablement() {
        final EObject eObject = SelectionUtilities.getSelectedEObject(getSelection());
        if (eObject != null && canLegallyEditResource()) {
            // See if it has a transformation object:
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
            if (modelResource != null && ModelUtilities.isVirtual(modelResource)) {
                try {
                    ModelTransformations modelTransformations = modelResource.getModelTransformations();
                    if (modelTransformations != null) {
                        List transformations = modelTransformations.getTransformations(eObject);
                        if (transformations != null && transformations.size() == 1) {
                            setEnabled(true);
                            return;
                        }
                    }
                } catch (ModelWorkspaceException e) {
                    String message = Util.getString("GenerateDependencyReportAction.generateDependencyReportError", modelResource.toString()); //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, message);
                }
            }
        }

        setEnabled(false);
    }
}
