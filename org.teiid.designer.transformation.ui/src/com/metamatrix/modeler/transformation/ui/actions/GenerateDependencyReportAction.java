/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelTransformations;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.util.DependencyReportDialog;
import com.metamatrix.modeler.transformation.ui.util.SqlDependencyHelper;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

/**
 * GenerateDependencyReportAction
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
