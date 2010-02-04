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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.impl.MappingClassImpl;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.mapping.ui.diagram.MappingDiagramUtil;
import com.metamatrix.ui.tree.TreeExpansionMonitor;
import com.metamatrix.ui.tree.TreeNodeMap;

/**
 * MappingAdapterFilter is a specialization for working on an XmlMapping through a TreeViewer, such that the only objects found
 * are those that are visible in the tree.
 */
public class MappingAdapterFilter {

    private int rowHeight;

    private TreeViewer treeViewer;
    private TreeExpansionMonitor treeExpansionMonitor;

    private TreeMappingAdapter mappingAdapter;
    private ITreeToRelationalMapper mapper;
    private List lstNodes = Collections.EMPTY_LIST;

    /**
     * Construct an instance of MappingAdapterFilter.
     */
    public MappingAdapterFilter( EObject target,
                                 TreeViewer treeViewer,
                                 TreeMappingAdapter theMappingAdapter ) {
        this.treeViewer = treeViewer;
        rowHeight = treeViewer.getTree().getItemHeight();

        treeExpansionMonitor = new TreeExpansionMonitor(treeViewer);

        this.mappingAdapter = theMappingAdapter;
        if (mappingAdapter == null) {
            this.mappingAdapter = new TreeMappingAdapter(target);
        }

        mapper = ModelMapperFactory.createModelMapper(target);
        Assertion.isNotNull(mapper);
    }

    public void setTreeExpansionMonitorStale() {
        treeExpansionMonitor.setIsStale(true);
    }

    /**
     * Obtain the TreeMappingAdapter that this object is using.
     * 
     * @return
     */
    public TreeMappingAdapter getMappingAdapter() {
        return mappingAdapter;
    }

    /**
     * Obtain the IMappableTree that this object is using.
     * 
     * @return
     */
    public IMappableTree getMappableTree() {
        return mapper.getMappableTree();
    }

    public ITreeToRelationalMapper getMapper() {
        return mapper;
    }

    private MappingDiagramBehavior getBehavior() {
        return MappingDiagramUtil.getCurrentMappingDiagramBehavior();
    }

    /**
     * Generate an ordered list of coarse MappingExtent instances for the specified MappingClass.
     * 
     * @param theMappingClass
     * @return
     */
    public List getCoarseMappingExtents( IProgressMonitor monitor ) {
        boolean bPopulateDiagramFromTreeSelection = getBehavior().getPopulateDiagramFromTreeSelectionState();
        // System.out.println("[MappingAdapterFilter.getCoarseMappingExtents] Retrieved bPopulateDiagramFromTreeSelection: " +
        // bPopulateDiagramFromTreeSelection );

        if (bPopulateDiagramFromTreeSelection && getSelectedNodes() != null && getSelectedNodes().size() > 0
            && getMappingClassesImpliedBySelection().size() > 0) {
            return getCoarseMappingExtentsInSelectionDrivenStyle(monitor, getSelectedNodes());
        }

        // default to the normal, full diagram
        return getCoarseMappingExtentsForFullDiagram(monitor);
    }

    /**
     * Generate an ordered list of coarse MappingExtent instances for the specified MappingClass.
     * 
     * @param theMappingClass
     * @return
     */
    public List getCoarseMappingExtentsInSelectionDrivenStyle( IProgressMonitor monitor,
                                                               List lstSelectedNodes ) {
        // System.out.println("[MappingAdapterFilter.getCoarseMappingExtentsInSelectionDrivenStyle] TOP");

        boolean showProgress = (monitor != null);
        int rowHeight = this.getTreeViewer().getTree().getItemHeight();

        List totalExtentList = new ArrayList();

        if (showProgress) {
            monitor.subTask("Getting mapping classes from XmlFilter"); //$NON-NLS-1$
        }

        // get the visible mapping classes
        List visibleMappingClasses = this.getMappedClassifiersForSelectionDrivenDiagram();

        /*
         * jh: old version is driven by MCs; this new one will be driven by walking
         *     the visible nodes of the tree.
         */
        List visibleTreeNodes = this.getTreeExpansionMonitor().getVisibleTreeItems();

        TreeItem nextTreeItem = null;
        Iterator iter = visibleTreeNodes.iterator();
        int iTreeNodesCnt = visibleTreeNodes.size();
        int iTreeNodes = 0;
        String pMessage = null;
        MappingClass mcCurrentMappingClass = null;
        MappingExtent meCurrentExtent = null;

        /*
         * Special requirements for this method:
         *  1. As we process nodes, build up a collection of Mapping Classes that the
         *     tree selection will cause us to display in this partial diagram...
         *     Or better yet, do it in advance.
         */
        HashMap hmMappingClassesToDisplay = getMappingClassesImpliedBySelection();

        while (iter.hasNext()) {

            if (showProgress) {
                if (iTreeNodes % 10 == 0) {
                    pMessage = "Getting extents for tree location " + iTreeNodes + " of " + iTreeNodesCnt; //$NON-NLS-1$ //$NON-NLS-2$
                    monitor.subTask(pMessage);
                }
            }

            nextTreeItem = (TreeItem)iter.next();

            // get EObject for this TreeItem, so we'll have both
            EObject eoNext = (EObject)nextTreeItem.getData();

            // ==========================================
            // case: XmlContainer
            // ==========================================
            if (eoNext instanceof XmlContainerNode || eoNext instanceof XmlRoot || isUnexpandedElementWithChildren(eoNext, this)) {

                /*
                 * One more jh Lyra enh: If user selects a Mapping Class Root, even if it is a container that is
                 *                       not really a Mapping Class column, we MUST include that Mapping Class
                 *                       in the diagram, and exclude others.
                 *                       2. Ok, since the special selection-driven version of the diagram will
                 *                          NOT be showing SummaryExtents (verify with John V.), then
                 *                          we should probably write a special version of THIS method
                 *                          (getCoarseMappingExtents()) dedicated to that situation...
                 *                          
                 *                          isNodeAMappingClassRoot( EObject eo )
                 */

                // Note: Container nodes get special decoration; see xxxLabelProvider.
                // Note2: But do NOT do it if it is an element
                boolean bExpanded = this.getTreeViewer().getExpandedState(eoNext);

                if (bExpanded) {
                    // if Expanded, leave a gap
                    // null out the ongoing element extent, if any, because a gap should interrupt it
                    meCurrentExtent = null;
                } else {

                    if (getSelectedNodes().contains(eoNext) && isNodeAMappingClassRoot(eoNext)) {
                        /*
                         * jhTODO Ok, it is selected AND it is a MappingClassRoot.  What do we do with it????
                         */
                    }

                    // get the collection of MCs that elements in this closed branch map to...
                    /*
                     * per the 'Barry Case', we now are going to report the number of columns in
                     * all of the hidden MCs, rather than the count of MCs.
                     * 
                     * So the structure returned from this call should itself be a map whose
                     * key is the MC and whose value is the total column count that is hidden
                     * in this branch FOR THAT MC.
                     * 
                     */
                    HashMap hmapMappingClasses = this.getMappingClassesInBranch(eoNext);
                    // System.out.println("[MappingAdapterFilter.getCoarseMappingExtents] hmapMappingClasses has: " +
                    // hmapMappingClasses.size() );
                    if (!hmapMappingClasses.isEmpty()) {

                        EObject eoMappingRef = null; // ??
                        int iRow = iTreeNodes; // right?

                        /*
                         * jh Defect 22768 (22566): Special case...if Root is also a mapped element,
                         *   use normal MappingExtent, otherwise use the SummaryExtent.
                         */
                        MappingExtent extent = null;
                        if (eoNext instanceof XmlRoot) {

                            eoMappingRef = (MappingClassImpl)hmapMappingClasses.keySet().iterator().next();
                            extent = new MappingExtent(iRow * rowHeight, rowHeight, eoMappingRef, eoNext);
                        } else {
                            extent = new SummaryExtent(iRow * rowHeight, rowHeight, eoMappingRef, eoNext);

                            ((SummaryExtent)extent).setMappingClasses(hmapMappingClasses);

                            boolean bSomeAreVisible = someMappingClassesAreVisible(hmapMappingClasses, visibleMappingClasses);
                            ((SummaryExtent)extent).setSomeMappingClassesAreVisible(bSomeAreVisible);
                        }

                        // test this col to see if mapped
                        boolean bMapped = (getMappingAdapter().getMappingClassColumn(eoNext) != null);

                        // test this col to see if MUST be mapped
                        boolean bThisNodeIsRequired = isMappingRequiredForNode(eoNext);

                        if (bThisNodeIsRequired) {
                            // if required, set 'completely mapped' to the current mapped state
                            extent.setCompletelyMapped(bMapped);
                            extent.setMappingRequired(true);
                        } else {
                            // if not required, set 'completely mapped' to bMapped
                            // jh Defect 21988: was 'true', changing to bMapped:
                            extent.setCompletelyMapped(bMapped);
                            extent.setMappingRequired(false);
                        }

                        extent.setPathToDocumentRoot(this.getMapper().getPathInDocument(eoNext));

                        totalExtentList.add(extent);

                        // null out the ongoing element extent, if any, because a Summary Extent should interrupt it
                        meCurrentExtent = null;
                    }
                }
            } else
            // ==========================================
            // case: XmlElement OR XmlAttribute
            // Note: we must test for this LAST, because everything is an XmlElement,
            // including containers and roots.
            // Perhaps we can discover a property in it that will tell us it
            // that it cannot have children, and is 'elemental'
            //    
            // Do XmlAttributes have any special requirements?
            // ==========================================
            if (eoNext instanceof XmlElement || eoNext instanceof XmlAttribute) {
                /*
                 * if an extent is in progress for the same MC, add this row to it;
                 * otherwise, start a new extent
                 */
                MappingClass mc = this.getMappingAdapter().getMappingClassForTreeNode(eoNext);
                // System.out.println("[MappingAdapterFilter.getCoarseMappingExtentsForFullDiagram] Matching TreeNode: " + eoNext
                // + " to MC: " + mc );

                // if we get an mc (Mapping Class) this col is capable of being mapped
                if (mc != null) {
                    if (hmMappingClassesToDisplay.get(mc) != null) {
                        // test this col to see if mapped
                        boolean bMapped = (getMappingAdapter().getMappingClassColumn(eoNext) != null);

                        // test this col to see if MUST be mapped
                        boolean bThisNodeIsRequired = isMappingRequiredForNode(eoNext);

                        // see if this is a continuation of the current extent
                        if (meCurrentExtent != null && mc == mcCurrentMappingClass) {

                            // override the existing 'mapped' state only if this new col is unmapped AND required
                            if (!bMapped && bThisNodeIsRequired) {
                                meCurrentExtent.setCompletelyMapped(bMapped);
                                meCurrentExtent.setMappingRequired(true);
                            }

                            meCurrentExtent.increaseHeight(rowHeight);
                        } else {
                            // start a new extent
                            int iRow = iTreeNodes;
                            meCurrentExtent = new MappingExtent(iRow * rowHeight, rowHeight, mc, eoNext);
                            meCurrentExtent.setPathToDocumentRoot(this.getMapper().getPathInDocument(eoNext));

                            // 'completely mapped': Any extent containing a 'must-map but unmapped' column should
                            // be colored red, so setCompletelyMapped will be false in that case.
                            // All other cases should be orange, so set 'completely mapped' to true in all other cases
                            if (bThisNodeIsRequired) {
                                // if required, set 'completely mapped' to the current mapped state
                                meCurrentExtent.setCompletelyMapped(bMapped);
                                meCurrentExtent.setMappingRequired(true);
                            } else {
                                // if not required, set 'completely mapped' to true
                                meCurrentExtent.setCompletelyMapped(true);
                                meCurrentExtent.setMappingRequired(false);
                            }

                            totalExtentList.add(meCurrentExtent);
                            mcCurrentMappingClass = mc;
                        }

                    } else {
                        // This MC is not in the selected list, so close out the current mc extent
                        // null out the ongoing element extent, if any, because any non-mapped element
                        // should interrupt it and leave a gap
                        meCurrentExtent = null;
                    }
                } else {
                    // null out the ongoing element extent, if any, because any non-mapped element
                    // should interrupt it and leave a gap
                    meCurrentExtent = null;
                }
            }

            // ==========================================
            // case: any node, mapped to a StagingTable
            // ==========================================

            // Last step for this node: handle a treenode mapped to a staging Table
            StagingTable st = this.getMappingAdapter().getStagingTable(eoNext);

            if (st != null) {
                /*
                 *  I am not sure how to generate the Staging Table extent, or where.
                 *  Can a treenode be both a Staging Table anchor point, and the locus of 
                 *  an ordinary element bound Extent? Yes it can.
                 *  
                 *  First cut: Why not just use the existing process to create Staging table extents?
                 *  We don't really have any new rules for Staging Tables...
                 */
                // System.out.println("[MappingDiagramModelFactory.getAllCoarseExtentsForLyra] This IS a Staging Table: " + st );
                // jhTODO Experiment: Do not show Staging Tables at all in 'selection driven' diagram
                // OR, only show Staging Tables that tie to cols that belong to the MC we are showing.
                // totalExtentList.add( this.getExtent( st ) );
            }
            iTreeNodes++;
        }
        return totalExtentList;
    }

    private HashMap getMappingClassesImpliedBySelection() {

        List lstSelectedTreeNodes = this.getSelectedNodes();
        HashMap hmMappingClassesToDisplay = new HashMap();
        Iterator itNodes = lstSelectedTreeNodes.iterator();
        EObject eoTemp = null;

        while (itNodes.hasNext()) {
            eoTemp = (EObject)itNodes.next();

            MappingClassColumn mccol = getMappingAdapter().getMappingClassColumn(eoTemp);

            if (mccol != null) {
                MappingClass mc = mccol.getMappingClass();

                if (mc != null) {
                    hmMappingClassesToDisplay.put(mc, "x"); //$NON-NLS-1$
                }
            } else if (isNodeAMappingClassRoot(eoTemp)) {
                MappingClass mc = getMappingAdapter().getMappingClass(eoTemp);

                if (mc != null) {
                    hmMappingClassesToDisplay.put(mc, "x"); //$NON-NLS-1$
                }
            }
        }

        return hmMappingClassesToDisplay;
    }

    /**
     * Generate an ordered list of coarse MappingExtent instances for the specified MappingClass.
     * 
     * @param theMappingClass
     * @return
     */
    public List getCoarseMappingExtentsForFullDiagram( IProgressMonitor monitor ) {
        // System.out.println("[MappingAdapterFilter.getCoarseMappingExtentsForFullDiagram] TOP");

        boolean showProgress = (monitor != null);
        int rowHeight = this.getTreeViewer().getTree().getItemHeight();

        List totalExtentList = new ArrayList();

        if (showProgress) {
            monitor.subTask("Getting mapping classes from XmlFilter"); //$NON-NLS-1$
        }

        // get the visible mapping classes
        List visibleMappingClasses = this.getMappedClassifiersForFullDiagram();

        /*
         * jh: old version is driven by MCs; this new one will be driven by walking
         *     the visible nodes of the tree.
         */
        List visibleTreeNodes = this.getTreeExpansionMonitor().getVisibleTreeItems();

        TreeItem nextTreeItem = null;
        Iterator iter = visibleTreeNodes.iterator();
        int iTreeNodesCnt = visibleTreeNodes.size();
        int iTreeNodes = 0;
        String pMessage = null;
        MappingClass mcCurrentMappingClass = null;
        MappingExtent meCurrentExtent = null;

        while (iter.hasNext()) {

            if (showProgress) {
                if (iTreeNodes % 10 == 0) {
                    pMessage = "Getting extents for tree location " + iTreeNodes + " of " + iTreeNodesCnt; //$NON-NLS-1$ //$NON-NLS-2$
                    monitor.subTask(pMessage);
                }
            }

            nextTreeItem = (TreeItem)iter.next();

            // get EObject for this TreeItem, so we'll have both
            EObject eoNext = (EObject)nextTreeItem.getData();

            // ==========================================
            // case: XmlContainer
            // ==========================================
            if (eoNext instanceof XmlContainerNode || eoNext instanceof XmlRoot || isUnexpandedElementWithChildren(eoNext, this)) {

                // Note: Container nodes get special decoration; see xxxLabelProvider.
                // Note2: But do NOT do it if it is an element
                boolean bExpanded = this.getTreeViewer().getExpandedState(eoNext);

                if (bExpanded) {
                    // if Expanded, leave a gap
                    // null out the ongoing element extent, if any, because a gap should interrupt it
                    meCurrentExtent = null;
                } else {
                    // get the collection of MCs that elements in this closed branch map to...
                    /*
                     * per the 'Barry Case', we now are going to report the number of columns in
                     * all of the hidden MCs, rather than the count of MCs.
                     * 
                     * So the structure returned from this call should itself be a map whose
                     * key is the MC and whose value is the total column count that is hidden
                     * in this branch FOR THAT MC.
                     * 
                     */
                    HashMap hmapMappingClasses = this.getMappingClassesInBranch(eoNext);
                    // System.out.println("[MappingAdapterFilter.getCoarseMappingExtents] hmapMappingClasses has: " +
                    // hmapMappingClasses.size() );
                    if (!hmapMappingClasses.isEmpty()) {

                        EObject eoMappingRef = null;
                        int iRow = iTreeNodes;

                        /*
                         * jh Defect 22768 (22566): Special case...if Root is also a mapped element,
                         *   use normal MappingExtent, otherwise use the SummaryExtent.
                         */
                        MappingExtent extent = null;
                        if (eoNext instanceof XmlRoot) {
                            Object nextKey = hmapMappingClasses.keySet().iterator().next();
                            if (nextKey instanceof MappingClassImpl) {
                                eoMappingRef = (MappingClassImpl)nextKey;
                            }
                            extent = new MappingExtent(iRow * rowHeight, rowHeight, eoMappingRef, eoNext);
                        } else {
                            extent = new SummaryExtent(iRow * rowHeight, rowHeight, eoMappingRef, eoNext);

                            ((SummaryExtent)extent).setMappingClasses(hmapMappingClasses);

                            boolean bSomeAreVisible = someMappingClassesAreVisible(hmapMappingClasses, visibleMappingClasses);
                            ((SummaryExtent)extent).setSomeMappingClassesAreVisible(bSomeAreVisible);
                        }

                        // test this col to see if mapped
                        boolean bMapped = (getMappingAdapter().getMappingClassColumn(eoNext) != null);

                        // test this col to see if MUST be mapped
                        boolean bThisNodeIsRequired = isMappingRequiredForNode(eoNext);

                        if (bThisNodeIsRequired) {
                            // if required, set 'completely mapped' to the current mapped state
                            extent.setCompletelyMapped(bMapped);
                            extent.setMappingRequired(true);
                        } else {
                            // if not required, set 'completely mapped' to bMapped
                            // jh Defect 21988: was 'true', changing to bMapped:
                            extent.setCompletelyMapped(bMapped);
                            extent.setMappingRequired(false);
                        }

                        extent.setPathToDocumentRoot(this.getMapper().getPathInDocument(eoNext));

                        totalExtentList.add(extent);

                        // null out the ongoing element extent, if any, because a Summary Extent should interrupt it
                        meCurrentExtent = null;
                    }
                }
            } else
            // ==========================================
            // case: XmlElement OR XmlAttribute
            // Note: we must test for this LAST, because everything is an XmlElement,
            // including containers and roots.
            // Perhaps we can discover a property in it that will tell us it
            // that it cannot have children, and is 'elemental'
            //    
            // Do XmlAttributes have any special requirements?
            // ==========================================
            if (eoNext instanceof XmlElement || eoNext instanceof XmlAttribute) {
                /*
                 * if an extent is in progress for the same MC, add this row to it;
                 * otherwise, start a new extent
                 * TODO: make similar changes in the 'SelectionDriven' get extents method...!
                 */
                MappingClass mc = this.getMappingAdapter().getMappingClassForTreeNode(eoNext);
                // System.out.println("[MappingAdapterFilter.getCoarseMappingExtentsForFullDiagram] Matching TreeNode: " + eoNext
                // + " to MC: " + mc );

                // if we get an mc (Mapping Class) this col is capable of being mapped
                if (mc != null) {
                    // test this col to see if mapped
                    boolean bMapped = (getMappingAdapter().getMappingClassColumn(eoNext) != null);

                    // test this col to see if MUST be mapped
                    boolean bThisNodeIsRequired = isMappingRequiredForNode(eoNext);

                    // see if this is a continuation of the current extent
                    if (meCurrentExtent != null && mc == mcCurrentMappingClass) {

                        // override the existing 'mapped' state only if this new col is unmapped AND required
                        if (!bMapped && bThisNodeIsRequired) {
                            meCurrentExtent.setCompletelyMapped(bMapped);
                            meCurrentExtent.setMappingRequired(true);
                        }

                        meCurrentExtent.increaseHeight(rowHeight);
                    } else {
                        // start a new extent
                        int iRow = iTreeNodes;
                        meCurrentExtent = new MappingExtent(iRow * rowHeight, rowHeight, mc, eoNext);
                        meCurrentExtent.setPathToDocumentRoot(this.getMapper().getPathInDocument(eoNext));

                        // 'completely mapped': Any extent containing a 'must-map but unmapped' column should
                        // be colored red, so setCompletelyMapped will be false in that case.
                        // All other cases should be orange, so set 'completely mapped' to true in all other cases
                        if (bThisNodeIsRequired) {
                            // if required, set 'completely mapped' to the current mapped state
                            meCurrentExtent.setCompletelyMapped(bMapped);
                            meCurrentExtent.setMappingRequired(true);
                        } else {
                            // if not required, set 'completely mapped' to bMapped
                            // jh Defect 21988: was 'true', changing to bMapped:
                            meCurrentExtent.setCompletelyMapped(bMapped);
                            meCurrentExtent.setMappingRequired(false);
                        }

                        totalExtentList.add(meCurrentExtent);
                        mcCurrentMappingClass = mc;
                    }
                } else {
                    // otherwise it is NOT mappable:

                    // null out the ongoing element extent, if any, because any un-mappable element
                    // should interrupt it and leave a gap
                    meCurrentExtent = null;
                }
            }

            // ==========================================
            // case: any node, mapped to a StagingTable
            // ==========================================

            // Last step for this node: handle a treenode mapped to a staging Table
            StagingTable st = this.getMappingAdapter().getStagingTable(eoNext);

            if (st != null) {
                /*
                 *  I am not sure how to generate the Staging Table extent, or where.
                 *  Can a treenode be both a Staging Table anchor point, and the locus of 
                 *  an ordinary element bound Extent? Yes it can.
                 *  
                 *  First cut: Why not just use the existing process to create Staging table extents?
                 *  We don't really have any new rules for Staging Tables...
                 */
                // System.out.println("[MappingDiagramModelFactory.getAllCoarseExtentsForLyra] This IS a Staging Table: " + st );
                totalExtentList.add(this.getExtent(st));
            }
            iTreeNodes++;
        }
        return totalExtentList;
    }

    private boolean isMappingRequiredForNode( EObject eoNode ) {
        if (eoNode instanceof XmlElement) {
            // Defect 22500 - somehow this changed elsewhere. Now we need to check if NO Xsd Component, then return false
            if (((XmlElement)eoNode).getXsdComponent() == null) {
                return false;
            }
            return (((XmlElement)eoNode).getMinOccurs() > 0);
        }
        if (eoNode instanceof XmlAttribute) {
            return (((XmlAttribute)eoNode).getMinOccurs() > 0);
        }
        return false;
    }

    private boolean isUnexpandedElementWithChildren( EObject eo,
                                                     MappingAdapterFilter xmlFilter ) {

        /*
         * jh Lyra enh: This method gets the Summary Extent to show up for unexpanded elements
         *              that have children, but when it is working we don't get any normal
         *              Element extents on the other elements.  Could it be that this logic
         *              is not discriminating enough, and ALL elements wind up passing this test,
         *              but only the elements that have MCs under them get the Summary Extent?????
         *              Yes!  Fixed.
         */

        boolean bExpanded = xmlFilter.getTreeViewer().getExpandedState(eo);

        if (!bExpanded) {
            DocumentContentProvider dcpContentProvider = (DocumentContentProvider)xmlFilter.getTreeViewer().getContentProvider();
            Object[] children = dcpContentProvider.getChildren(eo);
            if (children.length > 0) {
                // if it is not expanded, but has children, return true
                return true;
            }
        }
        // else return false
        return false;
    }

    private boolean someMappingClassesAreVisible( HashMap hmapMappingClasses,
                                                  List lstVisibleMappingClasses ) {

        Iterator itMCs = hmapMappingClasses.keySet().iterator();

        while (itMCs.hasNext()) {

            MappingClass mcTemp = (MappingClass)itMCs.next();

            if (lstVisibleMappingClasses.contains(mcTemp)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNodeAMappingClassRoot( EObject eo ) {
        /*
         * jh Lyra enh: Determine if this node is the anchor of a Mapping Class.
         *              If so, add the MC's name to the text of this node.
         */
        TreeMappingAdapter tmaMappingAdapter = getMappingAdapter();

        // we'll find a mapping class ONLY if this treenode is the mapping root element
        MappingClass mc = tmaMappingAdapter.getMappingClass(eo);

        if (mc != null) {
            return true;
        }

        return false;
    }

    /**
     * Obtain a MappingExtent for the specified StagingTable.
     * 
     * @param theStagingTable
     * @return
     */
    public MappingExtent getExtent( StagingTable theStagingTable ) {
        // System.out.println("[MappingAdapterFilter.getExtent] TOP; theStagingTable: " + theStagingTable );
        EObject documentLocation = mappingAdapter.getStagingTableOutputLocation(theStagingTable);

        int index = treeExpansionMonitor.getVisibleObjects().indexOf(documentLocation);
        // System.out.println("[MappingAdapterFilter.getExtent] index of Staging Tables doc location: " + index );
        MappingExtent extent = new MappingExtent(index * rowHeight, rowHeight, theStagingTable, documentLocation);
        extent.setPathToDocumentRoot(mapper.getPathInDocument(documentLocation));
        return extent;
    }

    /**
     * Generate an ordered list of detailed MappingExtents for the specified MappingClass.
     * 
     * @param theMappingClass
     * @return
     */
    public List getDetailedMappingExtents( MappingClass theMappingClass ) {
        // System.out.println("[MappingAdapterFilter.getDetailedMappingExtents] TOP");
        ArgCheck.isNotNull(theMappingClass);

        // the detailed mapping extents are gathered the same way the Coarse extents are, except
        // one is built for every row (tree node) that is visible and mappable.

        List allNodes = getCoarseMappingExtentNodes(theMappingClass);
        List mappableNodes = new ArrayList(allNodes.size());

        for (Iterator iter = allNodes.iterator(); iter.hasNext();) {
            EObject node = (EObject)iter.next();
            if (isMappable(node)) {
                mappableNodes.add(node);
            }
        }

        // jh PERFORMANCE - make visibleNodes a Map: key=node; value=row number
        TreeNodeMap tnmVisibleNodes = treeExpansionMonitor.getVisibleObjectsAsMap();

        List result = new ArrayList(mappableNodes.size());
        for (Iterator iter = mappableNodes.iterator(); iter.hasNext();) {
            EObject node = (EObject)iter.next();
            // if ( node instanceof XmlElementImpl ) {
            // // XmlElementImpl xeiNode = (XmlElementImpl)node;
            // // System.out.println("[MappingAdapterFilter.getDetailedMappingExtents] processing mappableNodes- Node: " +
            // xeiNode.getName() );
            // }
            // else
            // if ( node instanceof XmlAttributeImpl ) {
            // // XmlAttributeImpl xaiNode = (XmlAttributeImpl)node;
            // // System.out.println("[MappingAdapterFilter.getDetailedMappingExtents] processing mappableNodes- Node: " +
            // xaiNode.getName() );
            // }
            // else {
            // // System.out.println("[MappingAdapterFilter.getDetailedMappingExtents] processing mappableNodes- Node: " + node );
            // }

            // create a MappingExtent for this attributeMapping
            int row = tnmVisibleNodes.indexOf(node);

            // jh Defect 22585: research: is this MappingExtent being created with a
            // null MappingClassColumn?????
            MappingClassColumn column = mappingAdapter.getMappingClassColumn(node, theMappingClass);
            MappingExtent extent = new MappingExtent(row * rowHeight, rowHeight, column, node);

            // System.out.println("{MappingAdapterFilter.getDetailedMappingExtents] new extent.documentNodeRef: " +
            // extent.documentNodeRef );
            if (column == null) {
                // System.out.println("{MappingAdapterFilter.getDetailedMappingExtents] new extent's mapping class column is NULL "
                // );
            }

            extent.setPathToDocumentRoot(mapper.getPathInDocument(node));
            extent.setXsdQualifiedName(mapper.getXsdQualifiedName(node));
            extent.setXsdTargetNamespace(mapper.getXsdTargetNamespace(node));

            // jh Defect 21988: Must also set 'completelyMapped' and 'mappingRequired' here:
            extent.setMappingRequired(isMappingRequiredForNode(node));

            result.add(extent);
        }

        // now add extents for any staging tables at or above the mapping class location
        List mcLocations = mappingAdapter.getMappingClassOutputLocations(theMappingClass);
        for (Iterator stIter = mappingAdapter.getAllStagingTables().iterator(); stIter.hasNext();) {
            StagingTable st = (StagingTable)stIter.next();
            // System.out.println("[MappingAdapterFilter.getDetailedMappingExtents] Staging Table: " + st );
            EObject stLocation = mappingAdapter.getStagingTableOutputLocation(st);
            // see if the staging table location is visible - if not, nothing to return

            if (tnmVisibleNodes.contains(stLocation)) {
                for (Iterator mcIter = mcLocations.iterator(); mcIter.hasNext();) {
                    EObject mcLocation = (EObject)mcIter.next();
                    if (stLocation.equals(mcLocation)) {
                        result.add(getExtent(st));
                        break;
                    } else if (this.mapper.getMappableTree().isAncestorOf(stLocation, mcLocation)) {
                        result.add(getExtent(st));
                    }
                }
            }
        }

        // System.out.println("[MappingAdapterFilter.getDetailedMappingExtents] BOT");
        return result;
    }

    /**
     * Obtain an ordered list of all locations visible in the TreeViewer that are in the extent of the specified MappingClass.
     * 
     * @param theMappingClass
     * @return
     */
    public List getCoarseMappingExtentNodes( MappingClass theMappingClass ) {
        // get all locations for this mapping class
        List locations = mappingAdapter.getMappingClassOutputLocations(theMappingClass);
        List extentNodes = new ArrayList();
        List columnLocations = getColumnLocations(theMappingClass);

        // get all the visible tree nodes
        List visibleTreeNodes = new ArrayList(treeExpansionMonitor.getVisibleObjects());

        HashMap visibleTNMap = new HashMap();
        Iterator iter = visibleTreeNodes.iterator();
        while (iter.hasNext()) {
            visibleTNMap.put(iter.next(), "x"); //$NON-NLS-1$
        }

        if (!locations.isEmpty()) {

            // build a list of all mapping class locations for use in calculating extents
            List mappingClassLocations = new ArrayList();
            for (Iterator mcIter = mappingAdapter.getAllMappingClasses().iterator(); mcIter.hasNext();) {
                mappingClassLocations.addAll(mappingAdapter.getMappingClassOutputLocations((MappingClass)mcIter.next()));
            }

            for (int size = locations.size(), i = 0; i < size; i++) {
                // iterate over every visible location node
                if (visibleTNMap.get(locations.get(i)) != null) { // visibleTreeNodes.contains(locations.get(i)) ) {
                    // add the location to the collection of extent nodes
                    extentNodes.add(locations.get(i));
                    // recurse down this location and collect up the extent nodes
                    extentNodes.addAll(gatherCoarseExtentNodes((EObject)locations.get(i),
                                                               columnLocations,
                                                               mappingClassLocations,
                                                               visibleTNMap));
                }
            }
        }

        // get the extent nodes in order by calling retainAll on the visible nodes
        visibleTreeNodes.retainAll(extentNodes);
        // return the retained visible nodes
        return visibleTreeNodes;
    }

    /**
     * Recursive method used by getCoarseExtentNodes to walk down a branch of the tree and find all visible nodes in the extent.
     * 
     * @param visibleNode the branch node that this method will look beneath
     * @param columnLocations a Collection of tree nodes that should automatically be added in the result
     * @param mappingClassLocations a Collection of mapping class locations. Any node inside this collection should not be added
     *        to the result.
     * @param visibleTreeNodes a Collection of all tree nodes that are visible in the tree. Nodes that are not in this Collection
     *        should not be added to the result.
     * @return
     */
    private List gatherCoarseExtentNodes( EObject visibleNode,
                                          Collection columnLocations,
                                          Collection mappingClassLocations,
                                          HashMap visibleTreeNodes ) {
        ArrayList result = new ArrayList();
        for (Iterator childIter = mapper.getMappableTree().getChildren(visibleNode).iterator(); childIter.hasNext();) {
            EObject node = (EObject)childIter.next();
            // first, make sure the child is visible
            if (visibleTreeNodes.get(node) != null) {
                // next, check to see if this node is mapped into the MappingClass by checking columnLocations

                if (columnLocations.contains(node)) {
                    // if so, then this node is in the extent
                    result.add(node);
                    // recurse down this node's children
                    result.addAll(gatherCoarseExtentNodes(node, columnLocations, mappingClassLocations, visibleTreeNodes));
                } else {
                    // see if there is a mapping class located at this node

                    if (mappingClassLocations.contains(node)) {
                        // stop; this node is in another extent. do not check this node's children.
                    } else {
                        // this node is in the extent
                        result.add(node);
                        // recurse down this node's children
                        result.addAll(gatherCoarseExtentNodes(node, columnLocations, mappingClassLocations, visibleTreeNodes));
                    }
                }
            }

        }
        return result;
    }

    /**
     * return a list of all location mapped to the attributes of the specified MappingClass
     * 
     * @param theMappingClass
     * @return
     */
    public List getColumnLocations( MappingClass theMappingClass ) {
        List result = new ArrayList();
        for (Iterator iter = theMappingClass.getColumns().iterator(); iter.hasNext();) {
            result.addAll(mappingAdapter.getMappingClassColumnOutputLocations((MappingClassColumn)iter.next()));
        }
        return result;
    }

    public List getLocations( MappingClass theMappingClass ) {
        return mappingAdapter.getMappingClassOutputLocations(theMappingClass);
    }

    public EObject getLocation( StagingTable theStagingTable ) {
        return mappingAdapter.getStagingTableOutputLocation(theStagingTable);
    }

    public List getLocations( MappingClassColumn theMappingClassColumn ) {
        return mappingAdapter.getMappingClassColumnOutputLocations(theMappingClassColumn);
    }

    public int getLevel( MappingClass theMappingClass ) {
        int result = 0;
        List locations = getLocations(theMappingClass);
        if (locations != null && locations.size() > 0) {
            EObject location = (EObject)locations.iterator().next();
            result = ModelerCore.getModelEditor().getModelRelativePath(location).segmentCount() - 2;
        }
        return result;
    }

    public int getLevel( StagingTable theStagingTable ) {
        int result = 0;
        EObject location = getLocation(theStagingTable);
        if (location != null) {
            result = ModelerCore.getModelEditor().getModelRelativePath(location).segmentCount() - 2;
        }
        return result;
    }

    public List getMappedClassifiers() {
        boolean bPopulateDiagramFromTreeSelection = getBehavior().getPopulateDiagramFromTreeSelectionState();
        // System.out.println("[MappingAdapterFilter.getMappedClassifiers] Retrieved bPopulateDiagramFromTreeSelection: " +
        // bPopulateDiagramFromTreeSelection );

        if (bPopulateDiagramFromTreeSelection && getSelectedNodes() != null && getSelectedNodes().size() == 1) {
            // && getMappingClassesImpliedBySelection().size() > 0 ) {
            return getMappedClassifiersForSelectionDrivenDiagram();
        }

        // default to the normal, full diagram
        return getMappedClassifiersForFullDiagram();
    }

    /**
     * Obtain all {@link MappingClass}es visible in the filter. This collection will also include any visible {@link StagingTable}
     * s.
     * 
     * @return list of visible <code>MappingClass</code>es and <code>StagingTable</code>s
     */
    private List getMappedClassifiersForSelectionDrivenDiagram() {
        /*
         * jh Lyra enh:  We seem to be doing this redundantly.  Isn't there some way to return the result without
         *               reconstructing it under some circumstances?????
         */
        List result = new ArrayList();

        /*
         * jh Lyra enh: the call: "TreeViewerUtil.getVisibleObjects(treeViewer)"
         *              has a problem for Lyra, which is that it does not return
         *              nodes which are visible but NOT expanded.  For example,
         *              if  a sequence is a child of an expanded sequence, but
         *              is not expanded itself, it will not be returned.
         *              For Lyra, this is bad, because we are tree-driven and
         *              must have the complete picture of which nodes are VISIBLE. 
         */

        // this call should work fine:
        // List visibleNodes = treeExpansionMonitor.getVisibleObjects();
        List mappingClassObjects = new ArrayList(mappingAdapter.getAllMappingClasses());
        mappingClassObjects.addAll(mappingAdapter.getAllStagingTables());

        // boolean bShowAllMappingClasses
        // = editor.getDisplayAllMappingClasses();
        // System.out.println("[MappingAdapterFilter.getMappedClassifiers] Show all Mapping Classes boolean: " +
        // bShowAllMappingClasses );

        for (Iterator mcIter = mappingClassObjects.iterator(); mcIter.hasNext();) {
            MappingClass mappingClass = (MappingClass)mcIter.next();

            // List locations = null;
            //            
            // // StagingTable is a subclass of MappingClass
            // if (mappingClass instanceof StagingTable) {
            // // System.out.println("[MappingAdapterFilter.getMappedClassifiers] Found a Staging Table: " + mappingClass );
            // locations = Collections.singletonList(mappingAdapter.getLocation((StagingTable)mappingClass));
            // } else {
            // // System.out.println("[MappingAdapterFilter.getMappedClassifiers] Found a Mapping Class: " + mappingClass );
            // locations = mappingAdapter.getLocations(mappingClass);
            // }

            HashMap hmMappingClassesToDisplay = getMappingClassesImpliedBySelection();

            if (hmMappingClassesToDisplay.get(mappingClass) != null) {
                // System.out.println("[MappingAdapterFilter.getMappedClassifiersForSelectionDrivenDiagram] About to add to output: "
                // + mappingClass );
                result.add(mappingClass);
                break;
            }

        }

        return result;
    }

    /**
     * Obtain all {@link MappingClass}es visible in the filter. This collection will also include any visible {@link StagingTable}
     * s.
     * 
     * @return list of visible <code>MappingClass</code>es and <code>StagingTable</code>s
     */
    public List getMappedClassifiersForFullDiagram() {
        /*
         * jh Lyra enh:  We seem to be doing this redundantly.  Isn't there some way to return the result without
         *               reconstructing it under some circumstances?????
         */
        List result = new ArrayList();

        /*
         * jh Lyra enh: the call: "TreeViewerUtil.getVisibleObjects(treeViewer)"
         *              has a problem for Lyra, which is that it does not return
         *              nodes which are visible but NOT expanded.  For example,
         *              if  a sequence is a child of an expanded sequence, but
         *              is not expanded itself, it will not be returned.
         *              For Lyra, this is bad, because we are tree-driven and
         *              must have the complete picture of which nodes are VISIBLE. 
         */

        // this call should work fine:
        // List visibleNodes = treeExpansionMonitor.getVisibleObjects();
        TreeNodeMap tnmVisibleNodes = treeExpansionMonitor.getVisibleObjectsAsMap();

        List mappingClassObjects = new ArrayList(mappingAdapter.getAllMappingClasses());
        mappingClassObjects.addAll(mappingAdapter.getAllStagingTables());

        boolean bShowAllMappingClasses = getBehavior().getDisplayAllMappingClasses();
        // System.out.println("[MappingAdapterFilter.getMappedClassifiers] Show all Mapping Classes boolean: " +
        // bShowAllMappingClasses );

        for (Iterator mcIter = mappingClassObjects.iterator(); mcIter.hasNext();) {
            MappingClass mappingClass = (MappingClass)mcIter.next();
            List locations = null;

            // StagingTable is a subclass of MappingClass
            if (mappingClass instanceof StagingTable) {
                // System.out.println("[MappingAdapterFilter.getMappedClassifiers] Found a Staging Table: " + mappingClass );
                locations = Collections.singletonList(mappingAdapter.getStagingTableOutputLocation((StagingTable)mappingClass));
            } else {
                // System.out.println("[MappingAdapterFilter.getMappedClassifiers] Found a Mapping Class: " + mappingClass );
                locations = mappingAdapter.getMappingClassOutputLocations(mappingClass);
            }

            for (Iterator locIter = locations.iterator(); locIter.hasNext();) {
                Object node = locIter.next();

                if (bShowAllMappingClasses || tnmVisibleNodes.contains(node)) {
                    // System.out.println("[MappingAdapterFilter.getMappedClassifiers] About to add to output: " + mappingClass );
                    result.add(mappingClass);
                    break;
                }
            }
        }

        return result;
    }

    private boolean isMappable( EObject theTreeNode ) {
        return mapper.isMappable(theTreeNode);
    }

    public int getNumberVisibleNodes() {
        if (treeExpansionMonitor != null) return treeExpansionMonitor.getVisibleObjects().size();

        return 0;
    }

    public void dispose() {
        treeExpansionMonitor.dispose();
    }

    public TreeExpansionMonitor getTreeExpansionMonitor() {
        return this.treeExpansionMonitor;
    }

    public TreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    public HashMap getMappingClassesInBranch( EObject eo ) {

        HashMap hmap = new HashMap();
        DocumentContentProvider dcpContentProvider = (DocumentContentProvider)getTreeViewer().getContentProvider();

        hmap = internalGetMappingClassesInBranch(dcpContentProvider, dcpContentProvider.getChildren(eo), hmap);

        /*
         * jh Defect 22768 (22566):  Handle the special case in which eo:
         *    1) Is the XmlRootImpl
         *    2) Has no children
         *    3) But as an element itself, maps to a mapping class
         *      --> put eo itself into an object array and call
         *          internalGetMappingClassesInBranch() with it.
         */
        if (hmap.isEmpty() && eo instanceof XmlRoot) {
            Object[] children = {eo};

            hmap = internalGetMappingClassesInBranch(dcpContentProvider, children, hmap);
        }

        return hmap;
    }

    private HashMap internalGetMappingClassesInBranch( DocumentContentProvider dcpContentProvider,
                                                       Object[] eoChildren,
                                                       HashMap hmap ) {

        /* jh Lyra enh:
         * It is possible to get dupes, so correct for that.
         * jhTODO
         * Barry's case: To do this, create a new hashmap in the Mapping Adapter that maps ALL
         *               mapped columns to their mapping classes.  Then we will use it here
         *               to discover hidden nodes that map to VISIBLE mapping classes, and add
         *               those MCs to the result array in this method. 
         */
        for (int i = 0; i < eoChildren.length; i++) {
            EObject eoTemp = (EObject)eoChildren[i];

            if (eoTemp != null) {
                // System.out.println("[MappingAdapterFilter.internalGetMappingnClassesInBranch] processing node: " + eoTemp );
                // see if this tree EObject maps to a MappingClass; if so, capture the Mapping Class
                MappingClassColumn mccol = getMappingAdapter().getMappingClassColumn(eoTemp);

                if (mccol != null) {
                    MappingClass mc = mccol.getMappingClass();

                    if (mc != null) {
                        Integer ICount = (Integer)hmap.get(mc);

                        if (ICount != null) {
                            // if the MC is already in the map, bump up its count by 1
                            // System.out.println("[MappingAdapterFilter.internalGetMappingnClassesInBranch] About to add mc: " +
                            // mc.getName() );
                            hmap.put(mc, new Integer(ICount.intValue() + 1));
                        } else {
                            // Otherwise if it is new, add it to the map
                            hmap.put(mc, new Integer(1));
                        }
                    }
                } else {
                    /*
                     * this treenode is not mapped, but we should only count it 
                     * IF it is mappable, i.e., not a sequence or other container.
                     */

                    // count this node toward a total of mappable + unmapped nodes for this branch
                    if (isMappable(eoTemp)) {
                        Integer ICount = (Integer)hmap.get(SummaryExtent.UNMAPPED_MAPPABLE_COLUMN_COUNT);

                        if (ICount != null) {
                            // System.out.println("\n[MappingAdapterFilter.internalGetMappingnClassesInBranch] About to save new Unmapped cnt: "
                            // + (ICount.intValue() + 1) );
                            // System.out.println("\n[MappingAdapterFilter.internalGetMappingnClassesInBranch] And the unmapped node is: "
                            // + eoTemp );
                            // if the MC is already in the map, bump up its count by 1
                            hmap.put(SummaryExtent.UNMAPPED_MAPPABLE_COLUMN_COUNT, new Integer(ICount.intValue() + 1));
                        } else {
                            // Otherwise if it is new, add it to the map
                            // System.out.println("\n[MappingAdapterFilter.internalGetMappingnClassesInBranch] About to save new Unmapped cnt: 1 "
                            // );
                            // System.out.println("\n[MappingAdapterFilter.internalGetMappingnClassesInBranch] And the unmapped node is: "
                            // + eoTemp );
                            hmap.put(SummaryExtent.UNMAPPED_MAPPABLE_COLUMN_COUNT, new Integer(1));
                        }
                    }
                }
            }

            // if it has children, process the children recursively
            Object[] eoNextChildren = dcpContentProvider.getChildren(eoTemp);
            if (eoNextChildren.length > 0) {
                internalGetMappingClassesInBranch(dcpContentProvider, eoNextChildren, hmap);
            }
        }

        return hmap;
    }

    public void setSelectedNodes( List lstNodes ) {
        // System.out.println("[MappingAdapterFilter.setSelectedNodes");
        this.lstNodes = lstNodes;
    }

    public List getSelectedNodes() {
        return this.lstNodes;
    }

}
