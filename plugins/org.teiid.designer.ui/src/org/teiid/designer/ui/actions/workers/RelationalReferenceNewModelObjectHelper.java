/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions.workers;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.util.INewModelObjectHelper;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.UniqueConstraint;

public class RelationalReferenceNewModelObjectHelper implements INewModelObjectHelper {

    /**
     * @since 4.3
     */
    public RelationalReferenceNewModelObjectHelper() {
        super();
    }

    /*
     * (non-Javadoc)
     * @See org.teiid.designer.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     */
    @Override
	public boolean canHelpCreate( Object newObject ) {
        CoreArgCheck.isNotNull(newObject);
        // Supported Objects
        
        // PrimaryKey
        // ForeignKey
        // UniqueConstraint
        // AccessPattern
        
        // If the createdObject is VirtualTable, set supportsUpdate to false
        if (newObject instanceof EObject) {
        	if( newObject instanceof UniqueConstraint ||
    			newObject instanceof PrimaryKey ||
    			newObject instanceof AccessPattern ||
    			newObject instanceof ForeignKey ) {
        		return true;

            }
        }
        return false;
    }

    /**
     * 
     */
    public Object getTransactionSetting() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @See org.teiid.designer.core.util.INewModelObjectHelper#helpCreate(java.lang.Object, java.util.Map)
     */
    @SuppressWarnings("rawtypes")
	@Override
	public boolean helpCreate( Object newObject,
                               Map properties,
                               List<EObject> references ) {
        CoreArgCheck.isNotNull(newObject);
        if (references==null || references.isEmpty() ) return false;


        if (newObject instanceof AccessPattern) {
            for( EObject eObj : references ) {
            	if( eObj instanceof Column ) {
            		((AccessPattern)newObject).getColumns().add(eObj);
            	}
            } 
        } else if (newObject instanceof PrimaryKey) {
            for( EObject eObj : references ) {
            	if( eObj instanceof Column ) {
            		((PrimaryKey)newObject).getColumns().add(eObj);
            	}
            }  
        } else if (newObject instanceof UniqueConstraint) {
            for( EObject eObj : references ) {
            	if( eObj instanceof Column ) {
            		((UniqueConstraint)newObject).getColumns().add(eObj);
            	}
            } 
        } else if (newObject instanceof ForeignKey) {
            for( EObject eObj : references ) {
            	if( eObj instanceof Column ) {
            		((ForeignKey)newObject).getColumns().add(eObj);
            	}
            }     
        }
        return false;
    }


}
