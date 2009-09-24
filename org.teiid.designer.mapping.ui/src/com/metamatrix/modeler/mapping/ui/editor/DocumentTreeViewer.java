/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.internal.mapping.factory.MappingClassFactory;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.util.MappingUiUtil;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.ui.actions.ActionService;

/**
 * The TreeViewer for the DocumentTreeEditor. It is the JFace version of DocumentJTree from Modeler 3.0
 */
public final class DocumentTreeViewer extends TreeViewer implements UiConstants {

    private static final Color TEMP_TABLE_BACKGROUND = DiagramUiConstants.Colors.TEMP_GROUP_HEADER;
    private static final Color CLEAR_BACKGROUND = ColorConstants.white;
    private static final Color LOCATION_BACKGROUND = DiagramUiConstants.Colors.VIRTUAL_GROUP_HEADER;
    private static final Color MAPPING_BACKGROUND = DiagramUiConstants.Colors.VIRTUAL_GROUP_BKGRND;

    /** actions allowing user to directly expandAll or collapseAll the document tree */
    private IAction collapseAllAction;
    private IAction expandAllAction;

    private MappingAdapterFilter mappingFilter;
    private TreeMappingAdapter mappingAdapter;

    private MappingClassFactory mcfFactory;

    private int mappingType = PluginConstants.COARSE_MAPPING;

    private EObject target;

    private Collection selectedNodeList;

    public DocumentTreeViewer( Composite parent ) {
        super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        createContextMenu();
    }

    private void createContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow( IMenuManager theMenuMgr ) {
                fillContextMenu(theMenuMgr);
            }
        });

        Tree tree = getTree();
        tree.setMenu(menuMgr.createContextMenu(tree));

        expandAllAction = new Action() {
            @Override
            public void run() {
                myExpandAll();
            }
        };
        expandAllAction.setToolTipText(UiConstants.Util.getString("DocumentTreeViewer.expandAllAction.tooltip")); //$NON-NLS-1$
        expandAllAction.setText(UiConstants.Util.getString("DocumentTreeViewer.expandAllAction.text")); //$NON-NLS-1$
        expandAllAction.setEnabled(true);

        collapseAllAction = new Action() {
            @Override
            public void run() {
                myCollapseAll();
            }
        };
        collapseAllAction.setToolTipText(UiConstants.Util.getString("DocumentTreeViewer.collapseAllAction.tooltip")); //$NON-NLS-1$
        collapseAllAction.setText(UiConstants.Util.getString("DocumentTreeViewer.collapseAllAction.text")); //$NON-NLS-1$
        collapseAllAction.setEnabled(true);

    }

    void myExpandAll() {

        // jh Defect 19925: Performance enhancement, turn off drawing during expandAll
        getControl().setRedraw(false);
        expandAll();
        getControl().setRedraw(true);

        // Let's make sure teh TreeExpansionMOnitor is updated...
        getMappingAdapterFilter().getTreeExpansionMonitor().handleAllExpanded();
        TreeExpansionEvent tee = new TreeExpansionEvent(getThis(), getTree().getItems()[0].getData());
        fireTreeExpanded(tee);

        // Reveal the first item in tree so it doesn't scroll to bottom
        reveal(getTree().getItems()[0].getData());
    }

    void myCollapseAll() {
        collapseAll();
        // Let's make sure teh TreeExpansionMOnitor is updated...
        getMappingAdapterFilter().getTreeExpansionMonitor().handleAllCollapsed();
        fireTreeCollapsed(new TreeExpansionEvent(getThis(), getTree().getItems()[0].getData()));
    }

    private void createMappingFilter() {
        // System.out.println("[DocumentTreeViewer.createMappingFilter]:  Creating New Mapping Filter");

        // jh Lyra enh: ISSUE: can the filter persist? Must it be recreated on each refresh???
        if (mappingFilter != null) mappingFilter.dispose();

        mappingFilter = null;

        if (target != null) {
            try {
                mappingFilter = new MappingAdapterFilter(target, this, mappingAdapter);
                // Let's cache the TreeMappingAdapter because it exists INDEPENDENT of the filter and does NOT change
                mappingAdapter = mappingFilter.getMappingAdapter();

                // jh Lyra enh: update the MappingAdapterFilter's idea of tree selection
                mappingFilter.setSelectedNodes((List)selectedNodeList);

                // jh Defect 21277: Locating MappingClassFactory here to keep it in sync with
                // MappingAdapterFilter/TreeMappingAdapter. Create a new one
                // whenever the mapping filter is recreated.
                createMappingClassFactory(target);

            } catch (Exception e) {
                Util.log(e);
            }
        } else {
            Assertion.isNotNull(target);
        }
    }

    // jh Defect 21277: Locating MappingClassFactory here to keep it in sync with
    // MappingAdapterFilter/TreeMappingAdapter.
    public void createMappingClassFactory( EObject target ) {

        ITreeToRelationalMapper ittrm = ModelMapperFactory.createModelMapper(target);

        mcfFactory = new MappingClassFactory(ittrm, MappingUiUtil.getCurrentTreeMappingAdapter());
    }

    public MappingClassFactory getMappingClassFactory() {
        return mcfFactory;
    }

    void fillContextMenu( IMenuManager theMenuMgr ) {
        UiPlugin plugin = UiPlugin.getDefault();
        ActionService actionService = plugin.getActionService(plugin.getCurrentWorkbenchWindow().getActivePage());
        ((ModelerActionService)actionService).contributeToContextMenu(theMenuMgr, null, getSelection());
        theMenuMgr.add(collapseAllAction);
        theMenuMgr.add(expandAllAction);
    }

    public void setXmlMappingTarget( EObject theTarget ) {
        // see if target changed
        if (target == null || target != theTarget) {
            mappingAdapter = null;
        }
        target = theTarget;
        if (target != null) {
            /*
             * jh Lyra ehn: perhaps this SHOULD be true, as it is only done once
             */
            getMappingAdapterFilter(true);
        }
    }

    public MappingAdapterFilter getMappingAdapterFilter() {
        return getMappingAdapterFilter(false);
    }

    public MappingAdapterFilter getMappingAdapterFilter( boolean theRefreshFlag ) {
        if (theRefreshFlag || mappingFilter == null) {
            createMappingFilter();
        }

        return mappingFilter;
    }

    /**
     * Highlight the appropriate mappingFilter locations for the specified collection of MappingClasses.
     */
    public void setSelectedMapping( Collection mappingClasses ) {

        ArrayList locations = new ArrayList();
        ArrayList extent = new ArrayList();

        for (Iterator iter = mappingClasses.iterator(); iter.hasNext();) {
            Object nextObject = iter.next();
            if (nextObject instanceof StagingTable) {
                locations.add(mappingFilter.getLocation((StagingTable)nextObject));
            } else if (nextObject instanceof MappingClass) {
                locations.addAll(mappingFilter.getLocations((MappingClass)nextObject));
                extent.addAll(mappingFilter.getCoarseMappingExtentNodes((MappingClass)nextObject));
            } else if (nextObject instanceof MappingClassColumn) {
                // Defect 22776 - were wrongly adding locations to the Extent list. needed to be the locations list
                locations.addAll(mappingFilter.getLocations((MappingClassColumn)nextObject));
            }

            // remove all the locations out of the extent
            extent.removeAll(locations);
        }
        if (locations.size() > 0) reveal(locations.get(0));
        paintBackground(Collections.EMPTY_LIST, locations, extent);
    }

    /**
     * Highlight the appropriate objects in the tree if they exist?.
     */
    public void setSelectedNodes( Collection nodeList ) {
        // System.out.println( "[DocumentTreeViewer.setSelectedNodes] TOP" );
        selectedNodeList = nodeList;

        // jh Lyra enh: update the MappingAdapterFilter's idea of tree selection
        getMappingAdapterFilter().setSelectedNodes((List)nodeList);

        ArrayList locations = new ArrayList();

        for (Iterator iter = nodeList.iterator(); iter.hasNext();) {
            Object nextObject = iter.next();
            locations.add(nextObject);
        }

        boolean singleSelection = locations.size() == 1;

        if (singleSelection) {
            reveal(locations.get(0));
        }
        paintBackground(Collections.EMPTY_LIST, Collections.EMPTY_LIST, locations);
    }

    private void paintBackground( Collection tempTableLocations,
                                  Collection mappingClassLocations,
                                  Collection mappedNodes ) {
        ArrayList nodes = new ArrayList();
        buildTreeItemList(getTree().getItems(), nodes);
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            TreeItem item = (TreeItem)iter.next();
            Object o = item.getData();
            if (mappingClassLocations.contains(o) && shouldHighlight(o)) {
                highlightMappingClassLocation(item);
            } else if (mappedNodes.contains(o) && shouldHighlight(o)) {
                highlightAttributeLocation(item);
            } else {
                clearHighlight(item, tempTableLocations.contains(o));
            }
        }

    }

    private boolean shouldHighlight( Object oItem ) {

        boolean bResult = false;

        if (oItem instanceof EObject) {
            EObject eo = (EObject)oItem;
            // System.out.println("[DocumentTreeViewer.shouldHighlight] eo is: " + eo.toString() );

            // one more rule, the supercedes the others:
            MappingClass mc = mappingFilter.getMappingAdapter().getMappingClass(eo);

            // we'll find a mapping class ONLY if this treenode is the mapping root element
            if (mc != null) {
                // System.out.println("[DocumentTreeViewer.shouldHighlight] eo is MC Root " );
                bResult = true;
            }

            // jh Lyra enh: TODO do the same for Staging Tables...
            StagingTable st = mappingFilter.getMappingAdapter().getStagingTableForRootTreeNode(eo);

            // we'll find a staging table ONLY if this treenode is mapped to a staging table
            // (a staging table can only be mapped to one element)
            if (st != null) {
                // System.out.println("[DocumentTreeViewer.shouldHighlight] eo is ST Root " );
                bResult = true;
            }

            // if we have not found a reason to highlight yet, do the final tests
            if (!bResult) {
                // if this treenode is the mc's root, highlight it
                if (eo instanceof XmlContainerNode) {
                    // System.out.println("[DocumentTreeViewer.shouldHighlight] eo failing because container, root, or has children "
                    // );
                    bResult = false;
                } else {
                    // System.out.println("[DocumentTreeViewer.shouldHighlight] eo has passed all tests " );
                    bResult = true;
                }
            }
        }
        // System.out.println("[DocumentTreeViewer.shouldHighlight] BOT; about to return: " + bResult );
        return bResult;
    }

    public void showNoneSelected() {
        clearAllHilites();
        setSelection(Collections.EMPTY_LIST);
    }

    public void clearAllHilites() {
        ArrayList nodes = new ArrayList();
        if (!getTree().isDisposed()) {
            buildTreeItemList(getTree().getItems(), nodes);
            for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                TreeItem item = (TreeItem)iter.next();
                clearHighlight(item, false);
            }
        }
    }

    private void buildTreeItemList( TreeItem[] level,
                                    List result ) {
        for (int i = 0; i < level.length; ++i) {
            result.add(level[i]);
            TreeItem[] children = level[i].getItems();
            if (children != null && children.length > 0) {
                buildTreeItemList(children, result);
            }
        }
    }

    private void highlightMappingClassLocation( TreeItem row ) {
        row.setBackground(LOCATION_BACKGROUND);
    }

    private void highlightAttributeLocation( TreeItem row ) {
        row.setBackground(MAPPING_BACKGROUND);
    }

    private void clearHighlight( TreeItem row,
                                 boolean hasTempTable ) {
        if (hasTempTable) {
            row.setBackground(TEMP_TABLE_BACKGROUND);
        } else {
            row.setBackground(CLEAR_BACKGROUND);
        }

    }

    /**
     * @return
     */
    public int getMappingType() {
        return mappingType;
    }

    /*
     * Method required to supply this inside run() methods to wire up collapseAll and expandAll actions.
     */
    private AbstractTreeViewer getThis() {
        return this;
    }

    /**
     * @param i
     */
    public void setMappingType( int i ) {
        mappingType = i;
    }

    /**
     * @see org.eclipse.jface.viewers.StructuredViewer#handleLabelProviderChanged(org.eclipse.jface.viewers.LabelProviderChangedEvent)
     * @since 5.0
     */
    @Override
    protected void handleLabelProviderChanged( LabelProviderChangedEvent theEvent ) {
        // System.out.println("\n\n\n[DocumentTreeViewer.handleLabelProviderChanged] event: " + theEvent );
        refresh();
    }

}
