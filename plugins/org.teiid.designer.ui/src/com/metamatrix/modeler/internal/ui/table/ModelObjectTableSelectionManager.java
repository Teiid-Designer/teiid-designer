/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import com.metamatrix.ui.internal.eventsupport.SelectionProvider;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ModelObjectTableSelectionManager is a selection provider hooked up to each TableViewer in the
 * ModelTableEditor.  It's job is to listen for SelectionChangedEvents on the table and broadcast
 * them to the ModelTableEditor's selection provider.  It also can set a desired selection on the
 * table.
 * @since 4.0
 */
public class ModelObjectTableSelectionManager extends SelectionProvider implements ISelectionChangedListener {
    //============================================================================================================================
	// Variables
    
    private TableViewer tableViewer;
    private ModelObjectTableModel tableModel;
    private boolean isSelecting;
//    private String name;
    
    //============================================================================================================================
	// Constructors
    
    /**
	 * @since 4.0
	 */
    ModelObjectTableSelectionManager(final TableViewer tableViewer, final ModelObjectTableModel tableModel) {
        this.tableViewer = tableViewer;
        this.tableModel = tableModel;
        tableViewer.addSelectionChangedListener(this);
    }

    //============================================================================================================================
	// Property Methods

    /**
	 * @since 4.0
	 */
    public void setName(final String name) {
//        this.name = name;
    }

    //============================================================================================================================
    // SelectionChangedListener methods

    /**
     * Called by the TableViewer when the selection changes, the content of the selection will be 
     * ModelRowElements.  This method responds by converting them to EObjects and re-firing the selection
     * out to the ModelEditor.
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     * @since 4.0
     */
    public void selectionChanged(final SelectionChangedEvent event) {
        if (this.isSelecting) {
            return;
        }
        this.isSelecting = true;
        try {
            if (event.getSource() instanceof TableViewer) {
                // event came from the table.  convert selection to EObjects and fire out to listeners
                final List rowList = SelectionUtilities.getSelectedObjects(event.getSelection());
                final ArrayList objectList = new ArrayList(rowList.size()); 
                for (final Iterator iter = rowList.iterator();  iter.hasNext();) {
                    final ModelRowElement mre = (ModelRowElement)iter.next();
                    objectList.add(mre.getModelObject());
                }
                setSelection(new StructuredSelection(objectList), true);
            } else {
                // event came from the ModelEditor.  Convert selection to ModelRowElements and set it on the table
                setSelection(event.getSelection());
            }
        } finally {
            this.isSelecting = false;
        }
    }
    
    //============================================================================================================================
    // ISelectionProvider Methods

    /**
     * Overridden to allow calling setSelection from outside the table package, this method converts
     * the selection from EObjects to ModelRowElements and set it on the table. 
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     * @since 4.0
     */
    @Override
    public void setSelection(final ISelection selection) {
        final ISelection modelRowSelection = createModelRowElementSelection(selection);
        if (!modelRowSelection.isEmpty()) {
            setSelection(selection, false);
            tableViewer.setSelection(modelRowSelection, true);
            final Control ctrl = tableViewer.getControl();
            final TabFolder tabFolder = (TabFolder)ctrl.getParent();
            final TabItem[] items = tabFolder.getItems();
            for (int ndx = items.length;  --ndx >= 0;) {
                final TabItem item = items[ndx];
                if (item.getControl() == ctrl) {
                    tabFolder.setSelection(ndx);
                }
            }
        }
    }

    //============================================================================================================================
    // Declared Methods

    /**
     * convert the specified selection of EObjects into an ISelection of ModelRowElements
     * that can be set onto the TableViewer.
     * @param modelObjectSelection a StructuredSelection containing EObjects that should be
     * selected in the table.
     * @return an ISelection of ModelRowElements corresponding to the EObjects in the specified
     * argument, null if there are no ModelRowElements in this table corresponding to the EObjects,
     * or if no EObjects were found in the specified selection.
     * @since 4.0
     */
    private ISelection createModelRowElementSelection(final ISelection modelObjectSelection) {
        final List objectList = SelectionUtilities.getSelectedEObjects(modelObjectSelection);
        final ArrayList rowList = new ArrayList(objectList.size()); 
        for (final Iterator iter = objectList.iterator();  iter.hasNext();) {
            final ModelRowElement mre = tableModel.getRowElementForInstance((EObject)iter.next());
            if (mre != null) {
                rowList.add(mre);
            }
        }
        return new StructuredSelection(rowList);
    }
}
