/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.udf.ui.UdfJarWrapper;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 5.0
 */
public class DeleteUdfJarsAction extends SortableSelectionAction implements DqpUiConstants {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(DeleteUdfJarsAction.class);

    public static final String ACTION_ID = "DeleteUdfJarsAction"; //$NON-NLS-1$

    private static final String label = UTIL.getString(PREFIX + "delete"); //$NON-NLS-1$ 
    private static final String CONFIRM_SINGLE_DELETE_TITLE = UTIL.getString(PREFIX + "deleteFileDialogTitle"); //$NON-NLS-1$
    private static final String CONFIRM_MULTIPLE_DELETE_TITLE = UTIL.getString(PREFIX + "deleteFilesDialogTitle"); //$NON-NLS-1$
    private static final String CONFIRM_MULTIPLE_DELETE_MESSAGE_KEY = PREFIX + "deleteFilesDialogMessage"; //$NON-NLS-1$
    private static final String DELETE_JAR_LABEL = UTIL.getString(PREFIX + "single_label"); //$NON-NLS-1$
    private static final String DELETE_JARS_LABEL = UTIL.getString(PREFIX + "multi_label"); //$NON-NLS-1$

    /**
     * @since 5.0
     */
    public DeleteUdfJarsAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.DELETE_ICON));
        setText(DELETE_JARS_LABEL);
        setToolTipText(UTIL.getString(PREFIX + "tooltip")); //$NON-NLS-1$
        setId(ACTION_ID);
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable always
        return true;
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        deleteJars(getSelection());
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return onlyUdfJarsSelected(selection);
    }

    private boolean onlyUdfJarsSelected( ISelection theSelection ) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty()) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof UdfJarWrapper) {
                    result = true;
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    public void deleteJars( ISelection selection ) {
        boolean shouldDelete = false;
        File[] jarFiles = null;
        StructuredSelection sel = (StructuredSelection)selection;
        int size = sel.size();

        if (size == 1) {
            File selFile = ((UdfJarWrapper)sel.getFirstElement()).getJarFile();

            if (confirmDeleteFile(selFile)) {
                shouldDelete = true;
                jarFiles = new File[] {selFile};
            }
        } else if (size != 0) {
            if (confirmDeleteFile(sel.toList())) {
                shouldDelete = true;
                Collection<File> theJars = new ArrayList<File>();
                List allObjs = SelectionUtilities.getSelectedObjects(selection);
                if (!allObjs.isEmpty()) {
                    Iterator iter = allObjs.iterator();
                    Object nextObj = null;
                    while (iter.hasNext()) {
                        nextObj = iter.next();

                        if (nextObj instanceof UdfJarWrapper) {
                            theJars.add(((UdfJarWrapper)nextObj).getJarFile());
                        }
                    }
                }
                jarFiles = theJars.toArray(new File[sel.size()]);
            }
        }

        if (shouldDelete) {
            boolean success = DqpPlugin.getInstance().getExtensionsHandler().deleteUdfJarFiles(this, jarFiles);
            if (!success) {
                // not all jar files were deleted
                String msg = UTIL.getString(PREFIX + "problemDeletingJars"); //$NON-NLS-1$
                MessageDialog.openWarning(Display.getCurrent().getActiveShell(), CONFIRM_MULTIPLE_DELETE_TITLE, msg);
            }
        }
    }

    /**
     * Asks the user to confirm deleting one or more extension modules.
     * 
     * @param resources the selected extension modules
     * @return <code>true</code> if the user says to go ahead, and <code>false</code> if the deletion should be abandoned
     */
    private boolean confirmDeleteFile( Object object ) {
        String message = null;

        if (object instanceof File) {
            message = UTIL.getString(PREFIX + "deleteFileDialogMessage", ((File)object).getName()); //$NON-NLS-1$
            return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), CONFIRM_SINGLE_DELETE_TITLE, message);
        } else if (object instanceof List) {
            message = UTIL.getString(CONFIRM_MULTIPLE_DELETE_MESSAGE_KEY, Integer.toString(((List)object).size()));
            return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), CONFIRM_MULTIPLE_DELETE_TITLE, message);
        }

        assert false;
        return false;
    }

    @Override
    public void setSelection( ISelection theSelection ) {
        super.setSelection(theSelection);
        if (SelectionUtilities.isMultiSelection(theSelection)) {
            setText(DELETE_JARS_LABEL);
        } else {
            setText(DELETE_JAR_LABEL);
        }
    }
}
