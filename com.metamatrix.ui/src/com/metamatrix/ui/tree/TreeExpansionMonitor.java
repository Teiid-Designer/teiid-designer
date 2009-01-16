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

package com.metamatrix.ui.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;

/**
 * TreeExpansionMonitor is a utility class that monitors the expanding & collapsing of
 * tree nodes such that it can quickly return a list of visible objects.  The class works
 * by listening to tree expansion and collapse, then marking the child TreeItems with
 * a visibility key.
 */
public class TreeExpansionMonitor implements TreeListener, IChangeNotifier {

    /** key for setting data on TreeItem */
    private static final String VISIBLE = "TreeExpansionMonitor.visible"; //$NON-NLS-1$

    /** this monitor's TreeViewer */
    protected TreeViewer treeViewer;
    
    /** cached state */
    protected List visibleNodes;
    
    protected List visibleTreeItems; 

    /** IChangeListeners */
    protected ArrayList listenerList = new ArrayList();
    
    /** cached state monitor */
    protected boolean isStale = true;

    /**
     * Construct an instance of TreeExpansionMonitor to monitor the specified TreeViewer.
     */
    public TreeExpansionMonitor(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
        treeViewer.getTree().addTreeListener(this);
//        System.out.println("[TreeExpansionMonitor.ctor] Creating a new TreeExpansionMonitor...");
    }

    /**
     * Unhooks this monitor from the TreeViewer.
     */
    public void dispose() {
        Tree tree = treeViewer.getTree();
        
        if ( tree != null && !tree.isDisposed() ) {
            tree.removeTreeListener( this );
        }
    }
    
    public void setIsStale( boolean bIsStale ) {
//        System.out.println( "[TreeExpansionoMonitor.setIsStale] TOP" );
        isStale = bIsStale;
    }
    
    
    /**      
     * Return an ordered List of user objects that are displayed in the TreeViewer.
     * @result a List of objects representing the currently expanded state of the tree.  
     * Any nodes that are beneath collapsed parent nodes will not be returned in this List.
     */
    public List getVisibleObjects() {
        if ( isStale || visibleNodes == null ) {
            
            /*
             * jh Lyra enh: this is the only method that updates 'visibleTreeItems', so we
             *              must fix this, because we use that array in other Lyra processing.
             */
//            System.out.println("\n\n[TreeExpansionMonitor.getVisibleObjects] About to REFRESH the VISIBLE OBJECTS!!!!");
            treeViewer.getTree().update();
            
            // update visibleTreeItems
            TreeItem[] items = treeViewer.getTree().getItems();
            visibleTreeItems = new ArrayList( items.length );
            
            // update visibleNodes
            ArrayList nodes = new ArrayList(items.length);
            internalGetVisibleObjects( visibleTreeItems, nodes, items );
            isStale = false;
            visibleNodes = Collections.unmodifiableList(nodes);
        } else {
//            System.out.println("[TreeExpansionMonitor.getVisibleObjects] will NOT refresh the visible objects " );
        }
        
//        System.out.println("[TreeExpansionMonitor.getVisibleObjects] visible object count is: " + visibleNodes.size() );
        return visibleNodes;
    }
    
    /**   
     * Return a TreeNodeMap of user objects that are displayed in the TreeViewer.
     * Its key is the treenode and its value is the index in the visibleNodes list
     * @result a List of objects representing the currently expanded state of the tree.  
     * Any nodes that are beneath collapsed parent nodes will not be returned in this List.
     */
    public TreeNodeMap getVisibleObjectsAsMap() {
        if ( isStale || visibleNodes == null ) {
            
            /*
             * jh Lyra enh: this is the only method that updates 'visibleTreeItems', so we
             *              must fix this, because we use that array in other Lyra processing.
             */
//            System.out.println("\n\n[TreeExpansionMonitor.getVisibleObjects] About to REFRESH the VISIBLE OBJECTS!!!!");
            treeViewer.getTree().update();
            
            // update visibleTreeItems
            TreeItem[] items = treeViewer.getTree().getItems();
            visibleTreeItems = new ArrayList( items.length );
            
            // update visibleNodes
            ArrayList nodes = new ArrayList(items.length);
            internalGetVisibleObjects( visibleTreeItems, nodes, items );
            isStale = false;
            visibleNodes = Collections.unmodifiableList(nodes);
        } else {
//            System.out.println("[TreeExpansionMonitor.getVisibleObjects] will NOT refresh the visible objects " );
        }
        
//        System.out.println("[TreeExpansionMonitor.getVisibleObjects] visible object count is: " + visibleNodes.size() );
        return new TreeNodeMap( visibleNodes );
    }

    /**
     * Return an ordered List of tree items that are displayed in the TreeViewer.
     * @result a List of tree items representing the currently expanded state of the tree.  
     * Any nodes that are beneath collapsed parent nodes will not be returned in this List.
     */
    public List getVisibleTreeItems() {
        // run the visible objects method to refresh both collections
        getVisibleObjects();
        return visibleTreeItems;
    }

    private void internalGetVisibleObjects(List listItems, List listObjects, TreeItem[] items) {
        for ( int i=0 ; i<items.length ; ++i ) {
            
            
            // items[i].getData() was returning null.  Defect 12204 fix: Add null check.
            if ( isVisible(items[i]) && items[i].getData() != null) {
                // capture the tree item:
                listItems.add( items[i] );

                // capture the tree item's object, unless null
//                System.out.println("[TreeExpansionMonitor.internalGetVisibleObjects] About to add: " + items[i].getData() );
                listObjects.add( items[i].getData() );

                // Recurse on, Garth!
                internalGetVisibleObjects( listItems, listObjects, items[i].getItems() );
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.TreeListener#treeCollapsed(org.eclipse.swt.events.TreeEvent)
     */
    public void treeCollapsed(TreeEvent e) {
        TreeItem item = (TreeItem) e.item;
        TreeItem[] children = item.getItems();
        for ( int i=0 ; i<children.length ; ++i ) {
            children[i].setData(VISIBLE, Boolean.FALSE);
        }
        fireChangeEvent();
//        System.out.println("\n\nTreeExpansionMonitor.treeCollapsed: nVisibleObjects = " + getVisibleObjects().size());
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.TreeListener#treeExpanded(org.eclipse.swt.events.TreeEvent)
     */
    public void treeExpanded(TreeEvent e) {
//        System.out.println("\n\nTreeExpansionMonitor.treeExpanded: TOP" );

        TreeItem item = (TreeItem) e.item;
        TreeItem[] children = item.getItems();
        for ( int i=0 ; i<children.length ; ++i ) {
            children[i].setData(VISIBLE, Boolean.TRUE);
        }
//        System.out.println("TreeExpansionMonitor.treeExpanded: nVisibleObjects = " + getVisibleObjects().size());
        fireChangeEvent();
    }
    
    /**
     * Method designed to provide a way to directly set the expanded state of all items to "VISIBLE = TRUE".
     * This was needed for the expandAllAction in DocumentTreeController 
     */
    public void handleAllExpanded() {
        TreeItem item = treeViewer.getTree().getTopItem();
        // This is needed to get the ball rolling.
        item.setData(VISIBLE, Boolean.TRUE);
        expandItem(item);
        fireChangeEvent();
//        System.out.println("TreeExpansionMonitor.handleAllExpanded: nVisibleObjects = " + getVisibleObjects().size());
    }

    private void expandItem(TreeItem item) {
//        System.out.println("[TreeExpansionMonitor.expandItem] TOP; " + item );
        TreeItem[] children = item.getItems();
        for ( int i=0 ; i<children.length ; ++i ) {
            children[i].setData(VISIBLE, Boolean.TRUE);
            expandItem(children[i]);
        }
    }
    
    private void collapseItem(TreeItem item) {
        TreeItem[] children = item.getItems();
        for ( int i=0 ; i<children.length ; ++i ) {
            children[i].setData(VISIBLE, Boolean.FALSE);
            collapseItem(children[i]);
        }
    }
    
    /**
     * Method designed to provide a way to directly set the collapsed state of all items to "VISIBLE = FALSE".
     * This was needed for the collapseAllAction in DocumentTreeController 
     */
    public void handleAllCollapsed() {
        TreeItem item = treeViewer.getTree().getTopItem();
        collapseItem(item);
        // Make sure that top item is VISIBLE
        item.setData(VISIBLE, Boolean.TRUE);
        fireChangeEvent();
//        System.out.println("TreeExpansionMonitor.handleAllCollapsed: nVisibleObjects = " + getVisibleObjects().size());
    }
    
    /**
     * Determine if the specified TreeItem in this monitor's TreeViewer is visible. 
     * NOTE: this method is only reliable when walked from the top row of the tree 
     * to the bottom.
     * Inner hidden nodes may return true when an ancestor is actually not visible.
     * @param item
     * @return
     */
    protected boolean isVisible(TreeItem item) {
        boolean result = true;
//        boolean result = false;
        Object obj = item.getData(VISIBLE);
        if ( obj != null ) {
            result = ((Boolean) obj).booleanValue();
        }
        return result;
    }
    
    public void fireChangeEvent() {
        isStale = true;
        for ( Iterator iter = listenerList.iterator() ; iter.hasNext() ; ) {
            IChangeListener listener = (IChangeListener) iter.next();
            listener.stateChanged(this);
        }
    }
    /* (non-Javadoc)
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     */
    public void addChangeListener(IChangeListener listener) {
        if ( ! listenerList.contains(listener) ) {
            listenerList.add(listener);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     */
    public void removeChangeListener(IChangeListener listener) {
        listenerList.remove(listener);
    }

}
