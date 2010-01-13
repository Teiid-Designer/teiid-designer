/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.transformation.FragmentMappingRoot;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.AbstractDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.editor.MappingExtent;
import com.metamatrix.modeler.mapping.ui.editor.SummaryExtent;

/**
 * MappingExtentModelNode
 */
public class MappingExtentNode extends AbstractDiagramModelNode {
    protected static final String sNone = UiConstants.Util.getString("MappingExtentTooltip.none"); //$NON-NLS-1$
    protected static final String sNamePrefix = UiConstants.Util.getString("MappingExtentTooltip.namePrefix"); //$NON-NLS-1$
    protected static final String sTypePrefix = UiConstants.Util.getString("MappingExtentTooltip.typePrefix"); //$NON-NLS-1$
    protected static final String sTargetNamespace = UiConstants.Util.getString("MappingExtentTooltip.targetNamespace"); //$NON-NLS-1$
    protected static final String sQualifiedName = UiConstants.Util.getString("MappingExtentTooltip.qualifiedName"); //$NON-NLS-1$
    protected static final String sMappingClassPrefix = UiConstants.Util.getString("MappingExtentTooltip.mappingClassPrefix"); //$NON-NLS-1$
    protected static final String sLocationPrefix = UiConstants.Util.getString("MappingExtentTooltip.locationPrefix"); //$NON-NLS-1$
    protected static final String sStagingTablePrefix = UiConstants.Util.getString("MappingExtentTooltip.stagingTablePrefix"); //$NON-NLS-1$
    protected static final String sPathInDocumentPrefix = UiConstants.Util.getString("MappingExtentTooltip.pathInDocumentPrefix"); //$NON-NLS-1$
    protected static final String sXsdComponentPrefix = UiConstants.Util.getString("MappingExtentTooltip.xsdComponentPrefix"); //$NON-NLS-1$
    protected static final String sMappingRequiredPrefix = UiConstants.Util.getString("MappingExtentTooltip.mappingRequiredPrefix"); //$NON-NLS-1$
    protected static final String threeSpaces = "   "; //$NON-NLS-1$
    protected static final String NODE_PREFIX = UiConstants.Util.getString("MappingExtentTooltip.nodePrefix"); //$NON-NLS-1$
    protected static final String PATH_PREFIX = UiConstants.Util.getString("MappingExtentTooltip.pathPrefix"); //$NON-NLS-1$

    protected static final int MC_EXTENT_WIDTH = 20;
    protected static final int ST_EXTENT_WIDTH = 40;

    // jh: Defect 20609
    protected static final int SM_EXTENT_WIDTH = 75;

    public static final int X_ORIGIN = 4;
    protected EObject mappingClassEObject = null;
    protected MappingExtent extent = null;
    protected boolean isCoarseExtent = false;

    public MappingExtentNode( Diagram diagramModelObject,
                              EObject modelObject,
                              boolean isCoarse ) {
        super(diagramModelObject, modelObject);
        setName("M"); //$NON-NLS-1$
        isCoarseExtent = isCoarse;
    }

    public MappingExtentNode( DiagramModelNode diagramModelNode,
                              EObject modelObject,
                              MappingExtent theExtent,
                              boolean isCoarse ) {
        super((Diagram)diagramModelNode.getModelObject(), modelObject, theExtent);
        isCoarseExtent = isCoarse;
        setParent(diagramModelNode);

        setExtent(theExtent);

        setName("M"); //$NON-NLS-1$
    }

    @Override
    protected DiagramEntity findDiagramEntity( Diagram diagram,
                                               Object secondaryObject ) {
        return null;
    }

    @Override
    protected void initialize( Diagram diagramModelObject,
                               Object secondObject ) {
        // Don't create a diagramEntity for this object.
    }

    @Override
    public String toString() {
        return "MappingExtentNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void setMappingClass( EObject locationEObject ) {
        this.mappingClassEObject = locationEObject;
    }

    public EObject getMappingClass() {
        return mappingClassEObject;
    }

    /*
     *  (non-Javadoc)
     * This concrete method sends back the "Mapping Class" or "Attribute" stored as the model
     * object.  The locationEObject is the "target" end.  This list can be used for hiliting, etc..
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getDependencies()
     */
    @Override
    public List getDependencies() {

        if (getModelObject() instanceof TreeMappingRoot || getModelObject() instanceof FragmentMappingRoot) {
            List deps = new ArrayList(1);
            // EObject target = TransformationHelper.getTargetEObject( getModelObject() );
            // if( target != null ) {
            // List deps = new ArrayList(1);
            // deps.add(target);
            // return deps;
            // }
            Iterator connIter = getTargetConnections().iterator();
            NodeConnectionModel nextLink = null;
            while (connIter.hasNext()) {
                nextLink = (NodeConnectionModel)connIter.next();
                if (nextLink.getSourceNode() != null) deps.add(((DiagramModelNode)nextLink.getSourceNode()).getModelObject());
            }
            return deps;
        }
        // else {
        // // The case where the model object is an "attribute" not a mapping class
        // // We need to get the dependencies from the "Mappings".
        // return Collections.EMPTY_LIST;
        // }

        return Collections.EMPTY_LIST;
    }

    public void updateModelForExtent() {

        double zoomFactor = DiagramEditorUtil.getCurrentZoomFactor();
        if (getExtent() != null) {
            setPosition(new Point(X_ORIGIN, (int)(extent.getOffset() / zoomFactor)));

            if (getExtent().getMappingReference() instanceof StagingTable) {
                setSize(new Dimension((int)(ST_EXTENT_WIDTH / zoomFactor), (int)(extent.getHeight() / zoomFactor)));
            } else
            // jh: Defect 20609: provide a wider space to let SummaryExtent's 'arrowhead' show
            if (getExtent() instanceof SummaryExtent) {
                setSize(new Dimension((int)(SM_EXTENT_WIDTH / zoomFactor), (int)(extent.getHeight() / zoomFactor)));
            } else {
                // let it default to MappingExtent; must check for SummaryExtent first as it is a subclass of MappingExtent
                setSize(new Dimension((int)(MC_EXTENT_WIDTH / zoomFactor), (int)(extent.getHeight() / zoomFactor)));
            }
        }
    }

    /**
     * @return
     */
    public MappingExtent getExtent() {
        return extent;
    }

    /**
     * @param extent
     */
    public void setExtent( MappingExtent extent ) {
        this.extent = extent;
    }

    public void setExtentPosition( int newYOrigin ) {
        double zoomFactor = DiagramEditorUtil.getCurrentZoomFactor();
        if (getExtent() != null) {
            int iExtentOffset = (int)getExtent().getOffset();
            int newY = (int)(iExtentOffset / zoomFactor) + (int)(newYOrigin / zoomFactor);

            setPosition(new Point(X_ORIGIN, newY));
        } else setPosition(new Point(X_ORIGIN, (int)(newYOrigin / zoomFactor)));
    }

    public String getReferenceName() {
        String extentReference = extent.getDocumentNodeReference().toString();
        return extentReference;
    }

    public boolean isOnCoarseMappingDiagram() {
        return isCoarseExtent;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getModelObject()
     */
    @Override
    public EObject getModelObject() {
        if (isOnCoarseMappingDiagram()) {
            return super.getModelObject();
        } else if (getExtent() != null && !(getExtent().getMappingReference() instanceof StagingTable)) {
            return super.getModelObject();
        }
        if (getExtent() != null) {
            return getExtent().getMappingReference();
        }
        return super.getModelObject();
    }

    public List getToolTipStrings() {
        List returnList = new ArrayList();
        if (isOnCoarseMappingDiagram()) {
            if (getExtent().getMappingReference() instanceof StagingTable) {
                String stName = ModelerCore.getModelEditor().getName(getExtent().getMappingReference());
                returnList.add(sStagingTablePrefix + stName);
            } else if (getExtent().getMappingReference() != null) {
                String stName = ModelerCore.getModelEditor().getName(getExtent().getMappingReference());
                returnList.add(sMappingClassPrefix + stName);
            } else {
                // handle Unmapped extents
                returnList.add("(unmapped)"); //$NON-NLS-1$                
            }
            if (getExtent().getDocumentNodeReference() != null) {
                addToolTipStringsForLocation(returnList, getExtent());
            }
        } else {
            if (getExtent() != null) {
                if (getExtent().getMappingReference() instanceof StagingTable) {
                    String stName = ModelerCore.getModelEditor().getName(getExtent().getMappingReference());
                    returnList.add(sStagingTablePrefix + stName);
                } else {
                    String stName = ModelerCore.getModelEditor().getName(super.getModelObject());
                    returnList.add(sNamePrefix + stName);
                    String sType = ModelerCore.getMetamodelRegistry().getMetaClassLabel(super.getModelObject().eClass());
                    returnList.add(sTypePrefix + sType);

                    returnList.add(sPathInDocumentPrefix + getExtent().getPathToDocumentRoot());

                    String sComp = getExtent().getXsdQualifiedName();
                    if (sComp == null || sComp.trim().length() == 0) {
                        sComp = sNone;
                        returnList.add(sXsdComponentPrefix + sComp);
                    } else {
                        returnList.add(sXsdComponentPrefix);
                        String sNamespace = getExtent().getXsdTargetNamespace();
                        if (sNamespace == null || sNamespace.trim().length() == 0) sNamespace = sNone;
                        returnList.add(threeSpaces + sTargetNamespace + sNamespace);
                        returnList.add(threeSpaces + sQualifiedName + sComp);
                    }

                    stName = "" + getExtent().isMappingRequired(); //$NON-NLS-1$
                    returnList.add(sMappingRequiredPrefix + stName);
                }
            }
        }
        return returnList;
    }

    public List addToolTipStringsForLocation( List lstTooltipStrings,
                                              MappingExtent meExtent ) {

        String sFullPath = ""; //$NON-NLS-1$
        if (meExtent.getPathToDocumentRoot() != null && !meExtent.getPathToDocumentRoot().equals("null")) { //$NON-NLS-1$
            sFullPath = meExtent.getPathToDocumentRoot();
        }

        if (!sFullPath.equals("")) { //$NON-NLS-1$

            int iLastSlash = sFullPath.lastIndexOf('/');
            String sNode = sFullPath.substring(iLastSlash + 1);
            String sPath = sFullPath.substring(0, iLastSlash);

            lstTooltipStrings.add(NODE_PREFIX + sNode);
            lstTooltipStrings.add(PATH_PREFIX + sPath);
        }

        return lstTooltipStrings;
    }

}
