/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.connection;

import java.util.Collections;
import java.util.List;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.connection.AbstractNodeConnectionModel;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.mapping.ui.UiConstants;


/**
 * @since 8.0
 */
public class EnumeratedTypeLink extends AbstractNodeConnectionModel
                                implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private DiagramModelNode sourceNode;

    private DiagramModelNode targetNode;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public EnumeratedTypeLink(DiagramModelNode theSource,
                              DiagramModelNode theTarget) {
        setSourceNode(theSource);
        setTargetNode(theTarget);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AbstractNodeConnectionModel#getRouterStyle()
     * @since 5.0.2
     */
    @Override
    public int getRouterStyle() {
        return DiagramUiConstants.LinkRouter.DIRECT;
    }
    
    /** 
     * @see org.teiid.designer.diagram.ui.connection.AbstractNodeConnectionModel#getSourceNode()
     * @since 5.0.2
     */
    @Override
    public Object getSourceNode() {
        return this.sourceNode;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AbstractNodeConnectionModel#getTargetNode()
     * @since 5.0.2
     */
    @Override
    public Object getTargetNode() {
        return this.targetNode;
    }
    
    /** 
     * @see org.teiid.designer.diagram.ui.connection.AbstractNodeConnectionModel#getToolTipStrings()
     * @since 5.0.2
     */
    @Override
    public List getToolTipStrings() {
        return Collections.singletonList(toString());
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AbstractNodeConnectionModel#setSourceNode(java.lang.Object)
     * @since 5.0.2
     */
    @Override
    public void setSourceNode(Object theNode) {
        CoreArgCheck.isInstanceOf(DiagramModelNode.class, theNode);
        this.sourceNode = (DiagramModelNode)theNode;
    }
    
    /** 
     * @see org.teiid.designer.diagram.ui.connection.AbstractNodeConnectionModel#setTargetNode(java.lang.Object)
     * @since 5.0.2
     */
    @Override
    public void setTargetNode(Object theNode) {
        CoreArgCheck.isInstanceOf(DiagramModelNode.class, theNode);
        this.targetNode = (DiagramModelNode)theNode;
    }

    /** 
     * @see java.lang.Object#toString()
     * @since 5.0.2
     */
    @Override
    public String toString() {
        final String PREFIX = I18nUtil.getPropertyPrefix(EnumeratedTypeLink.class);
        String source = null;
        String target = null;

        if ((this.sourceNode == null) || (this.targetNode == null)) {
            source = ((this.sourceNode == null) ? Util.getString(PREFIX + "undefined") : this.sourceNode.getName()); //$NON-NLS-1$
            target = ((this.targetNode == null) ? Util.getString(PREFIX + "undefined") : this.targetNode.getName()); //$NON-NLS-1$
        } else {
            source = this.sourceNode.getName();
            target = this.targetNode.getName();
        }
        
        return Util.getString(PREFIX + "toString", new Object[] {source, target}); //$NON-NLS-1$
    }

}
