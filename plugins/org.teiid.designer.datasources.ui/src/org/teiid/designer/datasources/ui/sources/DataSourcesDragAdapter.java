/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.ui.views.navigator.NavigatorDragAdapter;

public class DataSourcesDragAdapter extends NavigatorDragAdapter {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    protected ISelectionProvider selectionProvider;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

	public DataSourcesDragAdapter(ISelectionProvider theProvider) {
        super(theProvider);
        selectionProvider = theProvider;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		// TODO Auto-generated method stub
		super.dragFinished(event);
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		// TODO Auto-generated method stub
		super.dragSetData(event);
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		// TODO Auto-generated method stub
		super.dragStart(event);
		
        IStructuredSelection selection = (IStructuredSelection)selectionProvider.getSelection();
        
        if (!selection.isEmpty() && selection.size() == 1) {
        	event.doit = true;
        	event.detail = DND.DROP_NONE;
        	System.out.println("  DND Object = " + selection.getFirstElement().toString() );
        	
        	ConnectionProfileTransfer.getInstance().setObject(selection.getFirstElement());
        }
	}

}
