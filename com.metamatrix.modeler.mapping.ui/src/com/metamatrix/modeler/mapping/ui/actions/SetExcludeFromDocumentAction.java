/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.mapping.ui.actions;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.IgnorableNotificationSource;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 4.3
 */
public class SetExcludeFromDocumentAction extends MappingAction implements IgnorableNotificationSource {

    private static final String EXCLUDE_TITLE = UiConstants.Util.getString("SetIncludeExcludeAction.Exclude.title"); //$NON-NLS-1$
    private static final String EXCLUDE_TOOLTIP = UiConstants.Util.getString("SetIncludeExcludeAction.Exclude.tooltip"); //$NON-NLS-1$

    static final int ALL_EXCLUDED = 0;
    static final int ALL_INCLUDED = 1;
    static final int BOTH_INCLUDED_AND_EXCLUDED = 2;

    boolean bDefaultExcludeState = true;

    /**
     * Construct an instance of SetIncludeExcludeAction.
     */
    public SetExcludeFromDocumentAction() {
        super();
        this.setUseWaitCursor(false);
        this.setText(EXCLUDE_TITLE);
        this.setToolTipText(EXCLUDE_TOOLTIP);

        // What distinguishes this 'set excluded' action is that it sets the 'excluded state'
        // of the selelcted xml document nodes to 'true'.
        setDefaultExcludeState(true);
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

    public void setDefaultExcludeState( boolean bDefaultExcludeState ) {
        this.bDefaultExcludeState = bDefaultExcludeState;
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                try {
                    List lstSelectedEObjects = getSelectedObjects();
                    setExcluded(lstSelectedEObjects, bDefaultExcludeState);

                } catch (Exception e) {
                    UiConstants.Util.log(e);
                    WidgetUtil.showError(e.getLocalizedMessage());
                } finally {
                    determineEnablement();
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

    /**
     * @since 4.0
     */
    void determineEnablement() {
        boolean enable = false;

        List lstSelectedEObjects = getSelectedObjects();
        if (lstSelectedEObjects.size() > 0) {
            enable = isSelectionAppropriate(lstSelectedEObjects);
        }

        setEnabled(enable);
    }

    protected boolean isSelectionAppropriate( List lstSelectedEObjects ) {
        int iSelectionType = getXmlDocNodeSelectionType(lstSelectedEObjects);

        if (iSelectionType == ALL_INCLUDED || iSelectionType == BOTH_INCLUDED_AND_EXCLUDED) {
            return true;
        }
        return false;
    }

    @Override
    public List getSelectedObjects() {
        boolean bSelectionOk = true;
        List lstSelectedEObjects = super.getSelectedObjects();

        Iterator it = lstSelectedEObjects.iterator();

        // qualify the selected set of objects only if they are all xml tree nodes
        while (it.hasNext()) {
            Object oTemp = it.next();
            if (oTemp instanceof EObject && ModelMapperFactory.isXmlTreeNode((EObject)oTemp)) {
                // ok
            } else {
                bSelectionOk = false;
                break;
            }
        }

        // Only return the selected items if they are all xml tree nodes
        if (bSelectionOk) {
            return lstSelectedEObjects;
        }

        return Collections.EMPTY_LIST;
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

    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
}
