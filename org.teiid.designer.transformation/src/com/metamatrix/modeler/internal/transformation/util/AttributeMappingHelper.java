/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.query.metadata.TempMetadataID;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.query.sql.visitor.ElementCollectorVisitor;

/**
 * AttributeMappingHelper - static methods for keeping the target attribute mappings for
 * a transformation up-to-date.
 */
public class AttributeMappingHelper {
    private static final boolean NOT_SIGNIFICANT = false;
    private static final boolean IS_UNDOABLE = true;

    /**
     * Set the attribute mappings using the current SELECT command and target attributes. 
     * @param transMappingRoot the TransformationMappingRoot
     * @param txnSource the source for the transaction
     * @return true if any data was changed.
     */
    public static boolean updateAttributeMappings(Object transMappingRoot, Object txnSource) {
        boolean changed = false;
        if(!TransformationHelper.isTransformationMappingRoot(transMappingRoot)) {
        	return changed;
        }
    	// Get Target attributes
		List targetAttrs = TransformationHelper.getTransformationTargetAttributes((EObject)transMappingRoot);
		if( targetAttrs == null || targetAttrs.isEmpty() ) {
			return changed;
		}
        
        // --------------------- BML 3/21/07 --------------------------
        // Defect 23839 - Added a new method check here to improve performance.  If ALL attributes are fully mapped, the check is
        // more efficient than assuming they may not be and performing the remainder of the method.
        
        if( attributesFullyMapped(transMappingRoot, targetAttrs) ) {
            // Backed out 2/27/07            
            // TODO:  Fix in QueryReconcilerHelper.applyModifications() to actually look at each binding, set any changed references
            // so this can be uncommented and sped up again.
//            return changed;
        }
        
        // start txn if not already in txn
        boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT,IS_UNDOABLE,"Update attr mappings",txnSource); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Fix for case 2778 - clean out bad mappings (no outputs)
            cleanAttributeMappings(transMappingRoot,txnSource);

			// Ensure each attribute has a mapping
            changed = reconcileAttributeMappings(transMappingRoot,targetAttrs,txnSource);
            
            // Set mapping inputs using SQL
            boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);
            if(!isValid) {
            	return changed;
            }
            // Get the SQL Command and the Projected Symbols
            Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            List projectedSymbols = command.getProjectedSymbols();
            // Get the target attributes that arent specified in an accessPattern
            List attrs = TransformationHelper.getTransformationTargetAttributes((EObject)transMappingRoot);       
            
            // Iterate attributes, setting each mapping
            Iterator iter = attrs.iterator();
            while(iter.hasNext()) {
                EObject attr = (EObject)iter.next();
                if(TransformationHelper.isSqlColumn(attr)) {
                    // Attribute name
                    String colName = TransformationHelper.getSqlEObjectName(attr);
                    // If mapping doesnt exist, create one
                    if( !hasAttributeMapping(transMappingRoot,attr) ) {
                        createAttributeMapping(transMappingRoot,attr,txnSource);
                        changed = true;
                    }
                    // Find element matching the attribute name (if it exists)
                    SingleElementSymbol seSymbol = getSymbolWithName(projectedSymbols,colName);
                    if(seSymbol!=null) {
                        // Get the ElementSymbols / corresponding EObjs
                        Collection elemSymbols = ElementCollectorVisitor.getElements(seSymbol,true);
                        Collection elemEObjs = TransformationSqlHelper.getElementSymbolEObjects(elemSymbols,command);
                        // Set Elem EObjs as inputs for attr Mapping
                        changed = setAttributeMapping(transMappingRoot,attr,elemEObjs,txnSource) || changed;
                    }
                }
            }
                                    
            succeeded = true;
        } finally {
            // If we start txn, commit it
            if(requiredStart) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        return changed;
    }
    
    /**
     * Make sure there is an attribute mapping for each target Attribute in the supplied List.
     * Remove any extras.
     * @param transMappingRoot the Transformation MappingRoot
     * @param targetAttrs the List of target attributes
     * @param txnSource the source for the transaction
     * @return true if any data was changed.
     */
    private static boolean reconcileAttributeMappings(Object transMappingRoot,List targetAttrs,Object txnSource) {
        boolean changed = false;
        if(TransformationHelper.isTransformationMappingRoot(transMappingRoot) && targetAttrs!=null) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT,IS_UNDOABLE,"Reconcile attr mappings",txnSource); //$NON-NLS-1$
            boolean succeeded = false;    
            try {
                // Get the current mapped attributes
                List currentMappedAttributes = getMappedAttributes(transMappingRoot);
                
                // Create Mappings for those missing
                Iterator iter = targetAttrs.iterator();
                while(iter.hasNext()) {
                    EObject targetAttr = (EObject)iter.next();
                    // If new target is not already mapped, add it
                    if(!currentMappedAttributes.contains(targetAttr)) {
                        // Create the Attribute Mapping - nested under mapping root
                        TransformationFactory mappingFactory = TransformationFactory.eINSTANCE;
                        Mapping attrMapping = mappingFactory.createTransformationMapping();
                        attrMapping.setNestedIn((MappingRoot)transMappingRoot);
    
                        // Set the output to the supplied target
                        List outputs = attrMapping.getOutputs();
                        outputs.add(targetAttr);
                        changed = true;
                    }
                }
                
                // Remove extraneous mappings
                for(int i=currentMappedAttributes.size()-1; i>=0; i--) {
                    EObject mappingAttr = (EObject)currentMappedAttributes.get(i);
                    if(!targetAttrs.contains(mappingAttr)) {
                        removeAttributeMapping(transMappingRoot,mappingAttr,txnSource);
                        changed = true;
                    }
                }
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Create a new attribute mapping nested under the SqlTransformationMappingRoot.  If a mapping 
     * for the supplied attribute already exists, then a new one is not created.
     * @param transMappingRoot the Transformation MappingRoot
     * @param targetAttr the attribute for the new mapping
     * @param txnSource the source for the transaction
     */
    public static void createAttributeMapping(Object transMappingRoot,EObject targetAttr,Object txnSource) {
        if(TransformationHelper.isTransformationMappingRoot(transMappingRoot)
           && targetAttr!=null && TransformationHelper.isSqlColumn(targetAttr) ) {
            // Get the current mapped attributes
            List currentMappedAttributes = getMappedAttributes(transMappingRoot);
            
            // If new target is not already mapped, add it
            if(!currentMappedAttributes.contains(targetAttr)) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT,IS_UNDOABLE,"Create attr mapping",txnSource); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Create the Attribute Mapping - nested under mapping root
                    TransformationFactory mappingFactory = TransformationFactory.eINSTANCE;
                    Mapping attrMapping = mappingFactory.createTransformationMapping();
                    attrMapping.setNestedIn((MappingRoot)transMappingRoot);

                    // Set the output to the supplied target
                    List outputs = attrMapping.getOutputs();
                    outputs.add(targetAttr);
                    succeeded = true;
                } finally {
                    // If we start txn, commit it
                    if(requiredStart) {
                        if(succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the attribute mapping for the supplied target attribute
     * @param transMappingRoot the Transformation MappingRoot
     * @param targetAttr the mapping output EObject
     */
    public static Mapping getAttributeMapping(Object transMappingRoot,EObject targetAttr) {
        // result
        Mapping attrMapping = null;
        // Get all mappings
        List attrMappings = getAttributeMappings(transMappingRoot);
        // Iterate mappings, look for output matching the supplied targetAttr
        Iterator iter = attrMappings.iterator();
        while(iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            if(eObj instanceof Mapping) {
                List targets = ((Mapping)eObj).getOutputs();
                if(!targets.isEmpty()) {
                    EObject target = (EObject)targets.get(0);
                    if(target!=null && target.equals(targetAttr)) {
                        attrMapping = (Mapping)eObj;
                        break;
                    }
                }
            }
        }
        return attrMapping;
    }

    /**
     * Determine if there is an attribute mapping for the supplied target attribute
     * @param transMappingRoot the Transformation MappingRoot
     * @param targetAttr the mapping output EObject
     * @return 'true' if there is an attribute mapping, 'false' if not.
     */
    public static boolean hasAttributeMapping(Object transMappingRoot,EObject targetAttr) {
        // result
        boolean hasMapping = false;
        // Get all mappings
        List attrMappings = getAttributeMappings(transMappingRoot);
        // Iterate mappings, look for output matching the supplied targetAttr
        Iterator iter = attrMappings.iterator();
        while(iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            if(eObj instanceof Mapping) {
                List targets = ((Mapping)eObj).getOutputs();
                if(!targets.isEmpty()) {
                    EObject target = (EObject)targets.get(0);
                    if(target!=null && target.equals(targetAttr)) {
                        hasMapping = true;
                        break;
                    }
                }
            }
        }
        return hasMapping;
    }

    /**
     * Remove an attribute mapping nested under the SqlTransformationMappingRoot.
     * @param transMappingRoot the Transformation MappingRoot
     * @param targetAttr the mapping output EObject
     * @param txnSource the source for the transaction
     */
    public static void removeAttributeMapping(Object transMappingRoot,EObject targetAttr,Object txnSource) {
        Mapping mapping = getAttributeMapping(transMappingRoot,targetAttr);
        if(mapping!=null) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT,IS_UNDOABLE,"Remove attr mapping",txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                ModelerCore.getModelEditor().delete(mapping);
                succeeded = true;
            } catch (ModelerCoreException e) {
                String message = TransformationPlugin.Util.getString("AttributeMappingHelper.removeAttrMappingError",     //$NON-NLS-1$
                                                                      transMappingRoot.toString()); 
                TransformationPlugin.Util.log(IStatus.ERROR, e, message); 
            } finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
            
        }
    }

    /**
     * Add an attribute mapping nested under the SqlTransformationMappingRoot.
     * @param transMappingRoot the Transformation MappingRoot
     * @param targetAttr the mapping output EObject
     * @param sourceAttrs the List of input EObjects
     * @param txnSource the source for the transaction
     * @return true if any data was changed.
     */
    public static boolean setAttributeMapping(Object transMappingRoot,
                                           EObject targetAttr, Collection sourceAttrs,
                                           Object txnSource ) {
        boolean changed = false;
        // Get Mapping for the attribute
        Mapping attrMapping = getAttributeMapping(transMappingRoot,targetAttr);
        if(attrMapping!=null) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT,IS_UNDOABLE,"Set attr mapping",txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                // Set the inputs to the mapping
                List inputs = attrMapping.getInputs();
                // Dont use clear - notification issue
                Iterator iter = inputs.iterator();
                while(iter.hasNext()) {
                    // defect 16726 - unneeded events were being fired
                    Object currInput = iter.next(); 
                    // see if we really should remove the attribute by checking
                    //  if we will be adding the same back in:
                    if (sourceAttrs.contains(currInput)) {
                        // it is in the list to be added; leave it in inputs,
                        //  and remove it from the list so it won't be added
                        sourceAttrs.remove(currInput);
                    } else {
                        // go ahead and remove, since it is not to be added:
                        iter.remove();
                        changed = true;
                    } // endif
                }
                // Add sources
                iter = sourceAttrs.iterator();
                while(iter.hasNext()) {
                    Object attr = iter.next();
                    if(attr!=null) {
                        inputs.add(attr);
                        changed = true;
                    }
                }
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Clear all of the current attribute mapping inputs for the supplied mappingRoot transformation.
     * @param transMappingRoot the Transformation MappingRoot
     * @param txnSource the source for the transaction
     */
    public static void clearAttributeMappingInputs(Object transMappingRoot,Object txnSource) {
        List attrMappings = getAttributeMappings(transMappingRoot);
        
        if(!attrMappings.isEmpty()) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT,IS_UNDOABLE,"Clear attr mappings",txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                // Go thru the Mappings and remove the "inputs"
                Iterator iter = attrMappings.iterator();
                while(iter.hasNext()) {
                    Mapping mapping = (Mapping)iter.next();
                    List mappingInputs = mapping.getInputs();
                    // Dont use clear - notification issue
                    Iterator mIter = mappingInputs.iterator();
                    while(mIter.hasNext()) {
                        mIter.next(); mIter.remove();
                    }
                }
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
            
        }
    }
    

    /**
     * Delete any mappings without an output. (Case 2778 fix)
     * @param transMappingRoot
     * @param txnSource
     * @since 5.0
     */
    public static void cleanAttributeMappings(Object transMappingRoot,Object txnSource) {
        List attrMappings = new ArrayList(getAttributeMappings(transMappingRoot));
        
        // For efficency sake, let's look for "STALE" mappings first, then we don't create a transaction of no work is necessary.
        // Find the stale mappings so we only delete if there are any
        Iterator iter = attrMappings.iterator();
        Collection staleMappings = new ArrayList();
        while(iter.hasNext()) {
            Mapping mapping = (Mapping)iter.next();
            List outputs = mapping.getOutputs();
            if(outputs==null || outputs.size()==0) {
                staleMappings.add(mapping);
            }
        }

        if(!staleMappings.isEmpty()) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT,IS_UNDOABLE,"check attr mappings",txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                // Remove the mappings with no outputs 
                 ModelerCore.getModelEditor().delete(staleMappings);
                succeeded = true;
            } catch (ModelerCoreException e) {
                String message = TransformationPlugin.Util.getString("AttributeMappingHelper.cleanAttrMappingError",     //$NON-NLS-1$
                                                                      transMappingRoot.toString()); 
                TransformationPlugin.Util.log(IStatus.ERROR, e, message); 
            }  finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
            
        }
    }

    /**
     * Clear all of the current attribute mapping inputs for the supplied mappingRoot transformation.
     * @param transMappingRoot the Transformation MappingRoot
     * @param txnSource the source for the transaction
     */
    public static void clearAttributeMappings(Object transMappingRoot,Object txnSource) {
        List attrMappings = new ArrayList(getAttributeMappings(transMappingRoot));
        
        if(!attrMappings.isEmpty()) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(NOT_SIGNIFICANT,IS_UNDOABLE,"Clear attr mappings",txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                // Using the alternative delete(Collection) call instead of interating.
                ModelerCore.getModelEditor().delete(attrMappings);
                succeeded = true;
            } catch (ModelerCoreException e) {
                String message = TransformationPlugin.Util.getString("AttributeMappingHelper.removeAttrMappingError",     //$NON-NLS-1$
                                                                      transMappingRoot.toString()); 
                TransformationPlugin.Util.log(IStatus.ERROR, e, message); 
            }  finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
            
        }
    }
    
    /**
     * Get a list of Target Attributes that have an attribute mapping.
     * @param transMappingRoot the Transformation MappingRoot
     * @return the List of attributes that have a mapping
     */
    private static List getMappedAttributes(Object transMappingRoot) {
        List attrMappings = getAttributeMappings(transMappingRoot);
        List mappedAttrs = new ArrayList(attrMappings.size());
        // Get the attribute from each mapping
        Iterator iter = attrMappings.iterator();
        while(iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            if(eObj instanceof Mapping) {
                // should only be one output - the attribute
                List targets = ((Mapping)eObj).getOutputs();
                if(!targets.isEmpty()) {
                    mappedAttrs.add(targets.get(0));
                }
                
            }
        }
        return mappedAttrs;
    }
    
    /**
     * Determine if a t-root's target attributes are fully mapped 
     * @param transMappingRoot
     * @return
     * @since 5.0
     */
    private static boolean attributesFullyMapped(Object transMappingRoot, List targetAttributes) {
        // --------------------- BML 3/21/07 --------------------------
        // Defect 23839 - Added a new method check to improve performance.  If ALL attributes are fully mapped, the check is
        // more efficient than assuming they may not be and performing the remainder of the method.
        // 
        // A FULLY Mapped attribute has a mapping where the Virtual Column is the OUTPUT and there is one or more INPUT's.
        List attrMappings = getAttributeMappings(transMappingRoot);
        if( attrMappings.size() != targetAttributes.size() ) {
            return false;
        }
        List localAttrList = new ArrayList(targetAttributes);

        Iterator iter = attrMappings.iterator();
        while(iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            if(eObj instanceof Mapping) {
                Mapping mapping = (Mapping)eObj;
                // should only be one output - the attribute
                if(mapping.getOutputs()==null || mapping.getOutputs().isEmpty() ) {
                    return false;
                }
                // Should be at least one Input
                if( mapping.getInputs() == null || mapping.getInputs().isEmpty() ) {
                    return false;
                }
                localAttrList.remove(mapping.getOutputs().get(0));
            }
        }
        return localAttrList.isEmpty();
    }
    
    /**
     * Get all of the attribute mappings for the supplied SqlTransformationMappingRoot.
     * @param transMappingRoot the Transformation MappingRoot
     * @return the List of attribute Mappings
     */
    public static List getAttributeMappings(Object transMappingRoot) {
        List result = Collections.EMPTY_LIST;
        if(transMappingRoot!=null && TransformationHelper.isTransformationMappingRoot(transMappingRoot)) {
            // Get the Attribute Mapping List and clear it
            result = ((MappingRoot)transMappingRoot).getNested();
        }
        return result;
    }
    
    /**
     * Find the SingleElementSymbol in the supplied List of SingleElementSymbols
     * with the supplied name.
     * @param seSymbols the List of SingleElementSymbols
     * @param name the symbol name
     * @return the SingleElementSymbol from the List with the provided name, null if not found.
     */
    private static SingleElementSymbol getSymbolWithName(List seSymbols, String name) {
        SingleElementSymbol result = null;
        Iterator iter = seSymbols.iterator();
        while(iter.hasNext()) {
            SingleElementSymbol seSymbol = (SingleElementSymbol)iter.next();
            String symbolName = TransformationSqlHelper.getSingleElementSymbolShortName(seSymbol,false);
            if(symbolName!=null && symbolName.equalsIgnoreCase(name)) {
                result = seSymbol;
                break;
            }
        }
        return result;
    }

    /**
     * The symbol might be a UUID, lookup the MetadataRecord, for the
     * actual name.
     * @param symbol 
     */
    public static String getSymbolShortName(final SingleElementSymbol symbol) {
    	String fullName = getSymbolFullName(symbol);
    	int index = fullName.lastIndexOf("."); //$NON-NLS-1$
    	return fullName.substring(index+1);   
    }

    /**
     * The symbol might be a UUID, lookup the MetadataRecord, for the
     * actual name.
     */
    public static String getSymbolFullName(final SingleElementSymbol symbol) {
        ArgCheck.isNotNull(symbol);
        if(symbol instanceof ElementSymbol) {
            Object metadataID = ((ElementSymbol)symbol).getMetadataID();
            if(metadataID != null) {
                if(metadataID instanceof MetadataRecord) {
                    MetadataRecord record = (MetadataRecord) metadataID;
                    return record.getFullName();
                }
                TempMetadataID tempID = (TempMetadataID) metadataID;
                return tempID.getID();
            }
        }
    
        return symbol.getName();
    }

}
