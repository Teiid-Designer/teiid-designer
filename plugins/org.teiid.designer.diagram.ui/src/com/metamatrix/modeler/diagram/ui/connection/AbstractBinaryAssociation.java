/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;

/**
 * AbstractBinaryAssociation
 */
public class AbstractBinaryAssociation 
  implements BinaryAssociation,
             DiagramUiConstants {
    
    private UmlRelationship umlAspect;
    private EObject eObj;
    private EObject sourceEObject;
    private EObject targetEObject;
    private int relationshipType = TYPE_UNKNOWN_RELATIONSHIP;

    public AbstractBinaryAssociation( EObject eObj, boolean initialize ) {
        setEObject( eObj );
        
		MetamodelAspect theAspect = DiagramUiPlugin.getDiagramAspectManager().getUmlAspect( eObj );
        if( theAspect instanceof UmlRelationship )
        	setUmlAspect( (UmlRelationship)theAspect );
        
        if( initialize )
        	init(null, null);
    }
    
	public AbstractBinaryAssociation( EObject eObj, EObject sourceObject, EObject targetObject) {
		setEObject( eObj );
		if( eObj != null ) {
			MetamodelAspect theAspect = DiagramUiPlugin.getDiagramAspectManager().getUmlAspect( eObj );
			if( theAspect != null && theAspect instanceof UmlRelationship )
				setUmlAspect( (UmlRelationship)theAspect );
		}
		init(sourceObject, targetObject);
	}

	private void init(EObject sourceEObject, EObject targetEObject) {
		// Let's populate the ends...
		if( sourceEObject != null ) {
			setSourceEObject(sourceEObject);
		}
		if( targetEObject != null ) {
			setTargetEObject(targetEObject);
		}
		
		if( sourceEObject == null && targetEObject == null ) {
			setSourceEObject(getEObject()); // UmlAssociation case
			setTargetEObject(getEObject()); // UmlAssociation case
		}
		
	}

    /**
     * Set the association that the other methods will use
     * @param assoc
     */
    public void setEObject( EObject eObj ) {
        this.eObj = eObj;
    }

    /**
     * Set the association that the other methods will use
     * @param assoc
     */
    public EObject getEObject() {
        return eObj;
    }
    
    /**
     * Set the association that the other methods will use
     * @param assoc
     */
    public void setUmlAspect( UmlRelationship umlAspect ) {
        this.umlAspect = umlAspect;
    }

    /**
     * Get the association that the other methods will use
     * @return the assoc 
     */
    public UmlRelationship getUmlAspect() {
        return umlAspect;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getEndCount(java.lang.Object)
     */
    public int getEndCount() {
        return UmlRelationshipHelper.getEndCount( getUmlAspect(), getEObject() );
        
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getRoleName(java.lang.Object, int)
     */
    public String getRoleName( int end ) {
        return UmlRelationshipHelper.getRoleName( getUmlAspect(), getEObject() , end );

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getMultiplicity(java.lang.Object, int)
     */
    public String getMultiplicity( int end ) {
        return UmlRelationshipHelper.getMultiplicity( getUmlAspect(), getEObject() , end );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getAggregation(java.lang.Object, int)
     */
    public int getAggregation( int end ) {
        return UmlRelationshipHelper.getAggregation( getUmlAspect(), getEObject() , end );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getProperties(java.lang.Object, int)
     */
    public String[] getProperties( int end ) {
        return UmlRelationshipHelper.getProperties( getUmlAspect(), getEObject() , end );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getNavigability(java.lang.Object, int)
     */
    public int getNavigability( int end ) {
        return UmlRelationshipHelper.getNavigability( getUmlAspect(), getEObject() , end );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getEnd(java.lang.Object, int)
     */
    public EObject getEnd( int end ) {
    	if( !(getUmlAspect() instanceof UmlAssociation) ) {
	    	if( end == BinaryAssociation.SOURCE_END && sourceEObject != null )
	    		return sourceEObject;
	    	
	    	if( end == BinaryAssociation.TARGET_END && targetEObject != null )
	    		return targetEObject;
		}
        return UmlRelationshipHelper.getEnd( getUmlAspect(), getEObject() , end );
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getEndTarget(java.lang.Object, int)
     */
    public EObject getEndTarget( int end ) {
        return UmlRelationshipHelper.getEndTarget( getUmlAspect(), getEObject() , end );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setRoleName(java.lang.Object, int, java.lang.String)
     */
    public IStatus setRoleName( int end, String name ) {
        return UmlRelationshipHelper.setRoleName( getUmlAspect(), getEObject() , end, name );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setMultiplicity(java.lang.Object, int, java.lang.String)
     */
    public IStatus setMultiplicity( int end, String mult ) {
        return UmlRelationshipHelper.setMultiplicity( getUmlAspect(), getEObject() , end, mult );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setProperties(java.lang.Object, int, java.lang.String)
     */
    public IStatus setProperties( int end, String[] props ) {
        return UmlRelationshipHelper.setProperties( getUmlAspect(), getEObject() , end, props );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setNavigability(java.lang.Object, int, int)
     */
    public IStatus setNavigability( int end, int navigability ) {
        return UmlRelationshipHelper.setNavigability( getUmlAspect(), getEObject() , end, navigability );
    }


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getVisibility(java.lang.Object)
     */
    public int getVisibility() {
        return UmlRelationshipHelper.getVisibility( getUmlAspect(), getEObject() );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getStereotype(java.lang.Object)
     */
    public String getStereotype() {
        return UmlRelationshipHelper.getStereotype( getUmlAspect(), getEObject() );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getSignature(java.lang.Object, int)
     */
    public String getSignature( int showMask ) {
        return UmlRelationshipHelper.getSignature( getUmlAspect(), getEObject(), showMask );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#getEditableSignature(java.lang.Object)
     */
    public String getEditableSignature() {
        return UmlRelationshipHelper.getEditableSignature( getUmlAspect(), getEObject() );
    }

    /* (non-Javadoc) 
     * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssocation#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature( String newSignature ) {
        return UmlRelationshipHelper.setSignature( getUmlAspect(), getEObject(), newSignature );
    }




	/**
	 * @return
	 */
	public EObject getSourceEObject() {
		return sourceEObject;
	}

	/**
	 * @return
	 */
	public EObject getTargetEObject() {
		return targetEObject;
	}

	/**
	 * @param object
	 */
	public void setSourceEObject(EObject object) {
		sourceEObject = object;
		// give the helper a chance to change it here?? i.e. UmlAssociations (column to column)
		EObject endSourceEObject = object;
		
		if( getUmlAspect()!= null && getUmlAspect() instanceof UmlAssociation )	
			endSourceEObject = UmlRelationshipHelper.getEndTarget(getUmlAspect(), object, BinaryAssociation.SOURCE_END);
			
		if( endSourceEObject != null ) {
			sourceEObject = endSourceEObject;
		}

	}

	/**
	 * @param object
	 */
	public void setTargetEObject(EObject object) {
		targetEObject = object;
		// give the helper a chance to change it here?? i.e. UmlAssociations (column to column)

		EObject endTargetEObject = object;
		
		if( getUmlAspect()!= null && getUmlAspect() instanceof UmlAssociation )	
			endTargetEObject = UmlRelationshipHelper.getEndTarget(getUmlAspect(), object, BinaryAssociation.TARGET_END);
			
		if( endTargetEObject != null ) {
			targetEObject = endTargetEObject;
		}
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation#getRelationshipType()
	 */
	public int getRelationshipType() {
		return relationshipType;
	}

	/**
	 * @param i
	 */
	public void setRelationshipType(int i) {
		relationshipType = i;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation#getRelationshipAspect()
	 */
	public UmlRelationship getRelationshipAspect() {
		// XXX Auto-generated method stub
		return umlAspect;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation#getReference()
	 */
	public EObject getReference() {
		// XXX Auto-generated method stub
		return eObj;
	}
	


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object someOtherBass) {
		if( someOtherBass == this)
			return true;
		
		boolean isEqual = false;
		
		if( someOtherBass instanceof BinaryAssociation) {
			isEqual = true;
			BinaryAssociation otherBass = (BinaryAssociation)someOtherBass;
			if( otherBass.getReference() == null && getReference() != null )
				isEqual = false;
				
			if( isEqual && otherBass.getReference() != null && getReference() == null )
				isEqual = false;
			
			if( isEqual && !(otherBass.getReference().equals(getReference())) )
				isEqual = false;
			
			if( isEqual && otherBass.getReference() != null && getReference() != null ) {
				if( otherBass.getRelationshipAspect() instanceof UmlAssociation &&
					getRelationshipAspect() instanceof UmlAssociation ) {
						EObject otherSource = otherBass.getEndTarget(BinaryAssociation.SOURCE_END);
						EObject thisSource = getEndTarget(BinaryAssociation.SOURCE_END);
						EObject otherTarget = otherBass.getEndTarget(BinaryAssociation.TARGET_END);
						EObject thisTarget = getEndTarget(BinaryAssociation.TARGET_END);
						
						if( otherSource == null || thisSource == null )
							isEqual = false;
							
						if( isEqual && !thisSource.equals(otherSource))
							isEqual = false;
						
						if( otherTarget == null || thisTarget == null )
							isEqual = false;
													
						if( isEqual && !otherTarget.equals(thisTarget))
							isEqual = false;	
					}
			}
	
		}
		return isEqual;
	}

}
