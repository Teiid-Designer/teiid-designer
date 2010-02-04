/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDependency;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DiagramUmlDependency extends DiagramUmlAssociation {


	/**
	 * @param source
	 * @param target
	 * @param bAss
	 */
	public DiagramUmlDependency(
		DiagramModelNode source,
		DiagramModelNode target,
		BinaryAssociation bAss) {
		super(source, target, bAss);
	}

	/**
	 * @param source
	 * @param target
	 * @param bAss
	 * @param sName
	 */
	public DiagramUmlDependency(
		DiagramModelNode source,
		DiagramModelNode target,
		BinaryAssociation bAss,
		String sName) {
		super(source, target, bAss, sName);
	}

	@Override
    protected void createAdditionalLabelNodes() {
//		Do nothing here.
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	@Override
    public int getSourceDecoratorId() {	
		return BinaryAssociation.DECORATOR_NONE;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	@Override
    public int getTargetDecoratorId() {
		if( ((UmlDependency)getBAssociation().getRelationshipAspect()).isRealization(getBAssociation().getReference()))
			return BinaryAssociation.DECORATOR_ARROW_CLOSED;
		
		return BinaryAssociation.DECORATOR_ARROW_OPEN;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getLineStyle()
	 */
	@Override
    public int getLineStyle() {
		if( ((UmlDependency)getBAssociation().getRelationshipAspect()).isRealization(getBAssociation().getReference()))
			return BinaryAssociation.LINE_DASH;
			
		return BinaryAssociation.LINE_DASH;
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getToolTipString()
	 */
	@Override
    public List getToolTipStrings() {
		if( getBAssociation().getReference() != null && 
			getBAssociation().getRelationshipAspect() != null &&
			getBAssociation().getRelationshipAspect() instanceof UmlDependency )  {
			List stringList = new ArrayList(3);
			
			String toolTip = "Dependency"; //$NON-NLS-1$
			UmlDependency theAspect = (UmlDependency)getBAssociation().getRelationshipAspect();
			String tempS = theAspect.getToolTip(getBAssociation().getReference());
			if( tempS != null )
				toolTip = tempS;
			stringList.add(toolTip);
//			stringList.add(" Source = " + getSourceNode().getName()); //$NON-NLS-1$
//			stringList.add(" Target = " + getTargetNode().getName()); //$NON-NLS-1$
			return stringList;
		}
		return Collections.EMPTY_LIST;
	}
	
}
