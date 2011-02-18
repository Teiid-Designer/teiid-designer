/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.id.ObjectID;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.query.sql.LanguageVisitor;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.Symbol;


/**
 * This visitor update the language objects in the Command being visited by replacing
 * the names of language objects which point to the old EObjects in the map with
 * the names of the coressponding new objects.   
 * @since 4.2
 */
public class UpdateLanguageObjectNameVisitor extends LanguageVisitor {

    private final Map oldToNewObjects;
    private Map oldToNewNames = new HashMap();    

    /** 
     * UpdateLanguageObjectNameVisitor
     * @param Map of old EObjects to the new EObjects, references to old EObjects
     * in the query need to be replaced with new EObjects
     * @since 4.2
     */
    public UpdateLanguageObjectNameVisitor(final Map oldToNewObjects) {
        this.oldToNewObjects = oldToNewObjects;
    }

    /** 
     * @see com.metamatrix.query.sql.LanguageVisitor#visit(com.metamatrix.query.sql.symbol.ElementSymbol)
     * @since 4.2
     */
    @Override
    public void visit(ElementSymbol obj) {
        updateSymbol(obj);
    }
    
    /** 
     * @see com.metamatrix.query.sql.LanguageVisitor#visit(com.metamatrix.query.sql.symbol.GroupSymbol)
     * @since 4.2
     */
    @Override
    public void visit(GroupSymbol obj) {
        updateSymbol(obj);
    }
    
    /**
     * Update the name on the symbol by looking up the new name in the supplied map
     * of old to new EObjects. 
     * @param obj
     * @since 4.2
     */
    private void updateSymbol(Symbol obj) {
        String fullName = obj.getName();
        
        if(fullName != null) {
            // if the names map is not populated, navigate throught the eObjects and populate it
            // with names and uuids
            if(this.oldToNewNames.isEmpty()) {
	            // for each map entry
			    for(final Iterator objIter = this.oldToNewObjects.entrySet().iterator(); objIter.hasNext();) {
			        Map.Entry mapEntry = (Map.Entry) objIter.next();
			        Object oldObj = mapEntry.getKey();
			        Object newObj = mapEntry.getValue();
			        if(oldObj != null && newObj != null && oldObj instanceof EObject && newObj instanceof EObject) {
			            EObject oldEobject = (EObject) oldObj;
			            EObject newEobject = (EObject) newObj;
			            SqlAspect sqlAspect = AspectManager.getSqlAspect(oldEobject);
			            // if it has a sql aspect (only then it has a name and can be used in sql)
			            if(sqlAspect != null) {
		                    ObjectID oldObjID = (ObjectID) sqlAspect.getObjectID(oldEobject);
		                    ObjectID newObjID = (ObjectID) sqlAspect.getObjectID(newEobject);
		                    if(oldObjID != null && newObjID != null) {
				                this.oldToNewNames.put(oldObjID.toString().toUpperCase(), newObjID.toString());		                        
		                    }
		                    String oldObjName = sqlAspect.getFullName(oldEobject);
		                    String newObjName = sqlAspect.getFullName(newEobject);
		                    if(oldObjName != null && newObjName != null) {
				                this.oldToNewNames.put(oldObjName.toUpperCase(), newObjName);		                        
		                    }
			            }
			        }
			    }
            }

            // look up new name and update symbol name
            String newName = (String) this.oldToNewNames.get(fullName.toUpperCase());
            if(newName != null) {
                obj.setName(newName);
            }
        }
    }    

}
