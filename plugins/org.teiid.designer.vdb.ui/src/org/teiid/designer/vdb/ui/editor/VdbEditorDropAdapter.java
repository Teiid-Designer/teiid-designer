/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.ui.editor;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.teiid.designer.ui.util.EObjectTransfer;

/**
 * @author SDelap
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 *
 * @since 8.0
 */
public class VdbEditorDropAdapter extends ViewerDropAdapter {
    /**
     * @param viewer
     */
    public VdbEditorDropAdapter(StructuredViewer viewer) {
        super(viewer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
     */

//    @Override
//    public void drop(DropTargetEvent event) {
//        List eObjList = getEventEObjects(event);
//        
//        System.out.println("Drop");
//        
//        Object data = event.data;
//        try {
//            if (data instanceof IResource) {
//                performDrop(data);
//            }
//        } catch (Exception ce) {
//            event.detail = DND.DROP_NONE;
//        }
//    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
     */
    @Override
    public boolean performDrop(Object data) {
        // TODO Auto-generated method stub
        System.out.println("performDrop");
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
     */
    @Override
    public boolean validateDrop(Object target,
                                int operation,
                                TransferData transferType) {
        // TODO Auto-generated method stub
        System.out.println("validateDrop");
        
        if(target!=null) System.out.println(target.toString());
        else System.out.println("target is null");
        
        System.out.println("operation: " + operation);
                
        
//        ResourceTransfer.getInstance(),
//            FileTransfer.getInstance(), PluginTransfer.getInstance(), EObjectTransfer.getInstance()};
//
        
        if(transferType!=null) System.out.println(transferType.toString());
        else System.out.println("transferType is null");
        
        return true;
    }
    
    private List getEventEObjects( DropTargetEvent event ) {
        Transfer[] transfers = ((DropTarget)event.getSource()).getTransfer();
        for (int i = 0; i < transfers.length; i++) {
            if (transfers[i] instanceof EObjectTransfer) {
                EObjectTransfer transfer = (EObjectTransfer)transfers[i];
                if (transfer.getObject() != null) return (List)transfer.getObject();
            }
        }
        return Collections.EMPTY_LIST;
    }
    
}
