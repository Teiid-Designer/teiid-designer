/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.expression;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor;
import com.metamatrix.query.internal.ui.builder.model.ElementEditorModel;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener;
import com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.query.internal.ui.builder.util.ICriteriaStrategy;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.symbol.ElementSymbol;

/**
 * ElementEditor
 */
public class ElementEditor extends AbstractLanguageObjectEditor {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ElementEditor.class);

    private ViewController controller;

    private ElementEditorModel model;

    private ICriteriaStrategy strategy;

    private Composite pnlContent;

    private TreeViewer viewer;

    /**
     * Constructs a <code>ElementEditor</code> using the given model.
     * 
     * @param theParent the parent container
     * @param theModel the editor's model
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    public ElementEditor( Composite theParent,
                          ElementEditorModel theModel ) {
        super(theParent, ElementSymbol.class, theModel);
        controller = new ViewController();
        model = theModel;
        model.addModelListener(controller);

        // set the viewer on the CriteriaStrategy
        strategy = ElementViewerFactory.getCriteriaStrategy(viewer);
        strategy.setTreeViewer(viewer);
    }

    /**
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#acceptFocus()
     */
    @Override
    public void acceptFocus() {
        viewer.getTree().setFocus();
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor#createUi(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createUi( Composite theParent ) {
        pnlContent = new Composite(theParent, SWT.NONE);
        pnlContent.setLayoutData(new GridData(GridData.FILL_BOTH));
        pnlContent.setLayout(new FillLayout());

        //
        // pnlContent contents
        //

        viewer = ElementViewerFactory.createElementViewer(pnlContent);
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent theEvent ) {
                handleDoubleClick();
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleTreeSelection();
            }
        });
        viewer.expandAll();
    }

    /**
     * Displays the appropriate UI for the current model state.
     */
    void displayElementSymbol() {
        Assertion.isNotNull(viewer);

        StructuredSelection selection = StructuredSelection.EMPTY;

        if (model.getElementSymbol() != null) {
            strategy.setTreeViewer(viewer); // make sure this viewer is set before using strategy
            Object node = strategy.getNode(model.getElementSymbol());

            if (node != null) {
                selection = new StructuredSelection(node);
            }

            if (!selection.equals(viewer.getSelection())) {
                viewer.setSelection(selection);
            }
        }
    }

    /**
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return Util.getString(PREFIX + "tip"); //$NON-NLS-1$
    }

    /**
     * Handler for double-click in tree.
     */
    void handleDoubleClick() {
    }

    /**
     * Handler for tree selection.
     */
    void handleTreeSelection() {
        IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
        ElementSymbol element = null;

        if (!selection.isEmpty()) {
            ICriteriaStrategy strategy = ElementViewerFactory.getCriteriaStrategy(viewer);
            strategy.setTreeViewer(viewer); // make sure this viewer is set before using strategy

            if (strategy.isValid(selection.getFirstElement())) {
                Object eObj = selection.getFirstElement();
                if (eObj instanceof ElementSymbol) {
                    element = ((ElementSymbol)eObj);
                } else {
                    element = new ElementSymbol(strategy.getRuntimeFullName(eObj), true);

                    // the viewer model contains EObjects. so the objects in the selection will
                    // be EObjects. since the EObject is used later on in the QueryCriteriaStrategy.getNode()
                    // method. save it here.
                    element.setMetadataID(eObj);
                }
            }
        }

        model.selectElementSymbol(element);
    }

    /**
     * @see com.metamatrix.query.ui.builder.ILanguageObjectEditor#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLanguageObject ) {
        if (theLanguageObject == null) {
            clear();
        } else {
            if (!(theLanguageObject instanceof ElementSymbol)) {
                Assertion.assertTrue((theLanguageObject instanceof ElementSymbol),
                                     Util.getString(PREFIX + "invalidLanguageObject", //$NON-NLS-1$
                                                    new Object[] {theLanguageObject.getClass().getName()}));
            }

            model.setLanguageObject(theLanguageObject);
        }
    }

    /**
     * The <code>ViewController</code> class is a view controller for the <code>ElementEditor</code>.
     */
    class ViewController implements ILanguageObjectEditorModelListener {

        /**
         * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            displayElementSymbol();
        }

    }
}
