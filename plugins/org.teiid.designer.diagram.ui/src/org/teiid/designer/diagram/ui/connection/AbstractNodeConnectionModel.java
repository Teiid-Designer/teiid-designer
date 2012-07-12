/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.connection;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;
import org.teiid.designer.metamodels.diagram.DiagramLink;
import org.teiid.designer.metamodels.diagram.DiagramLinkType;



/** 
 * @since 4.2
 */
public abstract class AbstractNodeConnectionModel implements
                                                 NodeConnectionModel {

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getSourceNode()
     * @since 4.2
     */
    public Object getSourceNode() {
        return null;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getTargetNode()
     * @since 4.2
     */
    public Object getTargetNode() {
        return null;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#setSourceNode(java.lang.Object)
     * @since 4.2
     */
    public void setSourceNode(Object node) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#setTargetNode(java.lang.Object)
     * @since 4.2
     */
    public void setTargetNode(Object node) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#setName(java.lang.String)
     * @since 4.2
     */
    public void setName(String sName) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getName()
     * @since 4.2
     */
    public String getName() {
        return null;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getLineStyle()
     * @since 4.2
     */
    public int getLineStyle() {
        return BinaryAssociation.LINE_SOLID;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getSourceDecoratorId()
     * @since 4.2
     */
    public int getSourceDecoratorId() {
        return BinaryAssociation.DECORATOR_NONE;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getTargetDecoratorId()
     * @since 4.2
     */
    public int getTargetDecoratorId() {
        return BinaryAssociation.DECORATOR_NONE;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getModelObject()
     * @since 4.2
     */
    public EObject getModelObject() {
        return null;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getLabelNodes()
     * @since 4.2
     */
    public List getLabelNodes() {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#updateLabels()
     * @since 4.2
     */
    public void updateLabels() {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getToolTipStrings()
     * @since 4.2
     */
    public List getToolTipStrings() {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#layout()
     * @since 4.2
     */
    public void layout() {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#layout(org.eclipse.draw2d.ConnectionAnchor, org.eclipse.draw2d.ConnectionAnchor, org.teiid.designer.diagram.ui.part.DiagramEditPart)
     * @since 4.2
     */
    public void layout(ConnectionAnchor ncaSourceAnchor,
                       ConnectionAnchor ncaTargetAnchor,
                       DiagramEditPart adepParentEditPart) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#setRouterStyle(java.lang.String)
     * @since 4.2
     */
    public void setRouterStyle(String sRouterStyle) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#setRouterStyle(int)
     * @since 4.2
     */
    public void setRouterStyle(int iRouterStyle) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getRouterStyle()
     * @since 4.2
     */
    public int getRouterStyle() {
        return DiagramLinkType.DIRECTED;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#placeStereotypeAndName(int, int, org.eclipse.draw2d.geometry.PointList)
     * @since 4.2
     */
    public void placeStereotypeAndName(int iSourceSide,
                                       int iTargetSide,
                                       PointList plConnectionPoints) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#addPropertyChangeListener(java.beans.PropertyChangeListener)
     * @since 4.2
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#removePropertyChangeListener(java.beans.PropertyChangeListener)
     * @since 4.2
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void firePropertyChange(String prop,
                                   Object old,
                                   Object newValue) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getDiagramLink()
     * @since 4.2
     */
    public DiagramLink getDiagramLink() {
        return null;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#getBendpoints()
     * @since 4.2
     */
    public List getBendpoints() {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#insertBendpoint(int, org.eclipse.draw2d.Bendpoint)
     * @since 4.2
     */
    public void insertBendpoint(int index,
                                Bendpoint point) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#removeBendpoint(int)
     * @since 4.2
     */
    public void removeBendpoint(int index) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#setBendpoint(int, org.eclipse.draw2d.Bendpoint)
     * @since 4.2
     */
    public void setBendpoint(int index,
                             Bendpoint point) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#setBendpoints(java.util.Vector)
     * @since 4.2
     */
    public void setBendpoints(Vector points) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#refreshBendPoints()
     * @since 4.2
     */
    public void refreshBendPoints() {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.NodeConnectionModel#clearBendpoints()
     * @since 4.2
     */
    public void clearBendpoints() {
    }

}
