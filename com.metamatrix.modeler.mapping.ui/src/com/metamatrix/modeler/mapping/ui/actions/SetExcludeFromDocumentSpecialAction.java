/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.IgnorableNotificationSource;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.0
 */
public class SetExcludeFromDocumentSpecialAction extends SortableSelectionAction implements IgnorableNotificationSource {

    private static String EXCLUDE_TITLE = UiConstants.Util.getString("SetIncludeExcludeAction.Exclude.title"); //$NON-NLS-1$
    private static String EXCLUDE_TOOLTIP = UiConstants.Util.getString("SetIncludeExcludeAction.Exclude.tooltip"); //$NON-NLS-1$

    boolean bDefaultExcludeState = true;

    protected int ALL_EXCLUDED = 0;
    protected int ALL_INCLUDED = 1;
    protected int BOTH_INCLUDED_AND_EXCLUDED = 2;

    /**
     * @since 5.0
     */
    public SetExcludeFromDocumentSpecialAction() {
        super();
        this.setText(EXCLUDE_TITLE);
        this.setToolTipText(EXCLUDE_TOOLTIP);

        // What distinguishes this 'set excluded' action is that it sets the 'excluded state'
        // of the selelcted xml document nodes to 'true'.
        setDefaultExcludeState(true);
    }

    @Override
    public boolean isApplicable( ISelection selection ) {
        return isValidSelection(selection);
    }

    /**
     * Valid selections include Relational Tables, Procedures or Relational Models. The roots instance variable will populated
     * with all Tables and Procedures contained within the current selection.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( ISelection selection ) {
        boolean isValid = true;
        if (SelectionUtilities.isEmptySelection(selection)) {
            isValid = false;
        }

        if (isValid && SelectionUtilities.isAllEObjects(selection)) {
            List eObjs = SelectionUtilities.getSelectedEObjects(selection);
            isValid = isSelectionAppropriate(eObjs);

        } else {
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void run() {
        internalRun();
    }

    private void internalRun() {
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                try {
                    List lstSelectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
                    setExcluded(lstSelectedEObjects, bDefaultExcludeState);

                } catch (Exception e) {
                    UiConstants.Util.log(e);
                    WidgetUtil.showError(e.getLocalizedMessage());
                } finally {
                    DiagramEditor de = DiagramEditorUtil.getVisibleDiagramEditor();
                    if (de != null) {
                        DiagramController controller = de.getDiagramController();
                        if (controller != null && controller instanceof MappingDiagramController) {
                            ((MappingDiagramController)controller).refresh(true);
                        }
                    }
                }
            }
        });
    }

    void setExcluded( List lstSelectedEObjects,
                      final boolean exclude ) {
        boolean startedTxn = ModelerCore.startTxn(false, false, "Set Excluded Value", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            Iterator it = lstSelectedEObjects.iterator();

            while (it.hasNext()) {
                EObject eoTemp = (EObject)it.next();
                XmlDocumentUtil.setExcluded(eoTemp, exclude);
            }
            succeeded = true;
        } finally {
            if (startedTxn) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    protected boolean isSelectionAppropriate( List lstSelectedEObjects ) {
        if (!allSelectedAreDocNodes(lstSelectedEObjects)) {
            return false;
        }
        int iSelectionType = getXmlDocNodeSelectionType(lstSelectedEObjects);

        if (iSelectionType == this.ALL_INCLUDED || iSelectionType == this.BOTH_INCLUDED_AND_EXCLUDED) {
            return true;
        }
        return false;
    }

    private boolean allSelectedAreDocNodes( List selectedEObjects ) {
        Iterator it = selectedEObjects.iterator();

        // determine current state of selected objects: all ex, all in, mixed
        while (it.hasNext()) {
            EObject eoTemp = (EObject)it.next();
            if (!ModelMapperFactory.isXmlTreeNode(eoTemp) && !TransformationHelper.isXmlDocument(eoTemp)) {
                return false;
            }
        }

        return true;
    }

    public int getXmlDocNodeSelectionType( List lstSelectedEObjects ) {
        boolean bSomeAreExcluded = false;
        boolean bSomeAreIncluded = false;
        int iResultType = 0;

        Iterator it = lstSelectedEObjects.iterator();

        // determine current state of selected objects: all ex, all in, mixed
        while (it.hasNext()) {
            EObject eoTemp = (EObject)it.next();
            if (XmlDocumentUtil.isExcluded(eoTemp, false)) {
                bSomeAreExcluded = true;
            } else {
                bSomeAreIncluded = true;
            }
        }

        if (bSomeAreExcluded && bSomeAreIncluded) {
            iResultType = BOTH_INCLUDED_AND_EXCLUDED;
        } else if (bSomeAreExcluded) {
            iResultType = ALL_EXCLUDED;
        } else if (bSomeAreIncluded) {
            iResultType = ALL_INCLUDED;
        }

        return iResultType;
    }

    public void setDefaultExcludeState( boolean bDefaultExcludeState ) {
        this.bDefaultExcludeState = bDefaultExcludeState;
    }
}
