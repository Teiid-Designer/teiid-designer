/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.visitor.AbstractSymbolMappingVisitor;

/**
 * SymbolUUIDMappingVisitor for mapping fullnames to UUIDs and vice-versa.
 */
public class SymbolUUIDMappingVisitor extends AbstractSymbolMappingVisitor {

    private static final char SEPARATOR_CHAR = '.';
    private boolean toUUID = false;
    private QueryMetadataInterface qmi = null;
    
    /**
     * <p> This constructor initialises the visitor</p>
     */
    public SymbolUUIDMappingVisitor( ) {
        super();
    }

    /**
     * Set the UUID / String conversion flag.  'true' means map to UUID, 'false' means
     * map to String.
     * @param flag the UUID conversion flag.
     */
    public void convertToUUID(boolean flag) {
        toUUID = flag;
    }

    /**
     * Set the QueryMetadata implementation to use in query resolution
     * @param qmi the QueryMetadataInterface to use in resolving the sql.
     */
    public void setQueryMetadata(QueryMetadataInterface qmi) {
        this.qmi = qmi;
    }

    /**
     * Get the mapped symbol from the specified symbol.  Subclasses should implement
     * this method to look up the target symbol from the specified symbol.
     * @param symbol Source symbol
     * @return Target symbol
     */
    @Override
    protected Symbol getMappedSymbol(Symbol symbol) {
        if ( symbol instanceof GroupSymbol ) {
            return mapGroupSymbol((GroupSymbol) symbol);
        } else if ( symbol instanceof ElementSymbol ) {
            return mapElementSymbol((ElementSymbol) symbol);
        }
        return symbol;
    }

    /**
     * Map the GroupSymbol
     * @param groupSymbol the GroupSymbol
     * @return the GroupSymbol after mapping
     */
    private GroupSymbol mapGroupSymbol(GroupSymbol groupSymbol) {
        // clone the groupSymbol as our return object
        GroupSymbol result = (GroupSymbol) groupSymbol.clone();
        // Check if the MetadataID is a TempMetadataID first
        Object metadataID = groupSymbol.getMetadataID();
        if(metadataID instanceof TempMetadataID) {
            return mapPseudoGroup(groupSymbol);
        }
        // Get the EObject that is resolved to the symbol
        EObject groupEObject = TransformationSqlHelper.getGroupSymbolEObject(groupSymbol);
        
        if ( groupSymbol.isResolved() && groupEObject!=null ) {
            String newName = null;
            // set the symbol name
            if( toUUID ) {
                newName = ModelerCore.getModelEditor().getObjectID(groupEObject).toString();
            } else {
                newName = TransformationHelper.getSqlEObjectFullName(groupEObject);
            }
            // set the GroupSymbol using the newName
            setGroupSymbolName(result, newName);
        } else {
            // the LanguageObjects must be resolved to use this Visitor
            throw new RuntimeException("Error using MetaObjectSymbolMappingVisitor: LanguageObjects must be resolved first"); //$NON-NLS-1$
        }
        return result;
    }

    /**
     * Map the ElementSymbol
     * @param elementSymbol the ElementSymbol
     * @return the ElementSymbol after mapping
     */
    private ElementSymbol mapElementSymbol(ElementSymbol elementSymbol) {
        ElementSymbol result = (ElementSymbol)elementSymbol.clone();
        if ( elementSymbol.isResolved() ) {
            // Check if the MetadataID is a TempMetadataID first
            Object metadataID = elementSymbol.getMetadataID();
            if(metadataID instanceof TempMetadataID) {
                return mapPseudoElement(elementSymbol,(TempMetadataID)metadataID);
            }

            // convert the cloned element symbol's GroupSymbol
            GroupSymbol gSymbol = elementSymbol.getGroupSymbol();
            GroupSymbol mappedGroupSymbol = (GroupSymbol) getMappedSymbol(gSymbol);
            result.setGroupSymbol(mappedGroupSymbol);

            // Get the EObject that is resolved to the ElementSymbol
            EObject elementEObject = TransformationSqlHelper.getElementSymbolEObject(elementSymbol);
            if ( toUUID ) {
                setElementSymbolToUID(result,elementEObject);
            } else {
                setElementSymbolToName(result,elementEObject);
            }
        }
        return result;
    }
    
    /**
     * Set the GroupSymbol name, using the supplied name string.
     * @param gSymbol the GroupSymbol
     * @param name the group symbol name
     */
    private void setGroupSymbolName(GroupSymbol gSymbol, String name) {
        if(gSymbol!=null) {
            // Get symbol Defn
            String symbolDefn = gSymbol.getDefinition();
            // set the symbols name
            if( symbolDefn!=null ) {
                // the name is the alias, which got cloned.  set the definition to the supplied string
                gSymbol.setDefinition(name);
            } else {
                // set the name to the supplied string, the definition is null
                gSymbol.setName(name);
            }
        }
    }

    /**
     * Set the ElementSymbol to the UUID version of the symbol, using the supplied EObject.
     * @param eSymbol the ElementSymbol
     * @param elementEObject the EObject to use in setting the symbol properties
     */
    private void setElementSymbolToUID(ElementSymbol eSymbol,EObject elementEObject) {
        // set the symbols to UUIDs
        GroupSymbol groupSymbol = eSymbol.getGroupSymbol();
        if(elementEObject!=null && groupSymbol!=null) {
            String UIDString = ModelerCore.getModelEditor().getObjectID(elementEObject).toString();
            if ( groupSymbol.getDefinition() != null ) {
                // there is an alias on the group, set that in the uuid string
                eSymbol.setName(groupSymbol.getName() + SEPARATOR_CHAR + UIDString);
            } else {
                eSymbol.setName(UIDString);
            }
        }
        eSymbol.setDisplayFullyQualified(true);
    }

    /**
     * Set the ElementSymbol to the name version of the symbol, using the supplied EObject.
     * @param eSymbol the ElementSymbol
     * @param elementEObject the EObject to use in setting the symbol properties
     */
    private void setElementSymbolToName(ElementSymbol eSymbol,EObject elementEObject) {
        // Get parent groupSymbol
        GroupSymbol gSymbol = eSymbol.getGroupSymbol();
        if(elementEObject!=null && gSymbol!=null) {
            // Set the elementSymbol
            if ( gSymbol.getDefinition() != null ) {
                String name = TransformationHelper.getSqlEObjectName(elementEObject);
                // the group has an alias, so use that in the SymbolElement's name
                eSymbol.setName(gSymbol.getName() + SEPARATOR_CHAR + name);
            } else {
                String fullName = TransformationHelper.getSqlEObjectFullName(elementEObject);
                eSymbol.setName(fullName);
            }
            eSymbol.setDisplayFullyQualified(true);
        }
    }

    /**
     * <p>Get the mapping for a pseudo group and create a new GroupSymbol OR just return the passed groupSymbol if no
     * match is found.  Pseduo groups may be created in the following situations: </p>
     * <ul>
     * <li>Stored query name - lookup stored query and get UUID</li>
     * <li>Stored procedure name - lookup stored procedure and get UUID</li>
     * <li>Subquery alias in from clause - ignore and return passed groupSymbol (no mapping exists)</li>
     * </ul>
     * @param groupSymbol Group symbol to map
     * @return Mapped symbol or original if no mapping was found
     */
    private GroupSymbol mapPseudoGroup(GroupSymbol groupSymbol) {
        GroupSymbol result = groupSymbol;
//        String name = null;
//        String alias = null;
//        
//        if(groupSymbol.getDefinition() == null) { 
//            name = groupSymbol.getName();
//        } else {
//            name = groupSymbol.getDefinition();
//            alias = groupSymbol.getName();    
//        }

//  Look up MetaObject by name
//  If no match was found
//        return groupSymbol
//  else 
//        if MetaObject refers to a stored query or stored procedure
//            get UUID of stored query or stored procedure 
//            create new GroupSymbol using that UUID (in place of the name) and the alias (if it exists)
//            set the metadataID to be the groupSymbol metadataID (which is a TempMetadataID) - (not sure if this matters)
//            return new groupSymbol
//        else 
//            return groupSymbol

//        Collection c = session.getNavigationView().lookup(name);
//        if ( c != null && c.size() == 1 ) {
//            MetaObject mo = (MetaObject) c.iterator().next();
//            if ( MetaModelUtilities.isProcedure(mo) ) {
//                if ( alias == null ) {
//                    result = new GroupSymbol(mo.getGlobalUID().toString());
//                } else {
//                    result = new GroupSymbol(alias, mo.getGlobalUID().toString());
//                }
//                result.setMetadataID(groupSymbol.getMetadataID());
//            }
//        }
        
        return result;
    }

    /**
     * <p>Get the mapping for a pseudo element and create a new ElementSymbol OR just return the passed 
     * ElementSymbol if no match is found.  Pseduo elements may be created in the following situations: 
     * </p>
     * <ul>
     * <li>Stored query parameter used as a variable - lookup stored query parameter and get UUID</li>
     * <li>Virtual group element used as a variable - lookup real virtual group element and get UUID</li>
     * <li>Well-known variables in a procedure - ignore and return passed ElementSymbol (no mapping exists)</li>
     * <li>Element from pseudo-group defined by a subquery - ignore and return passed elementSymbol (no mapping exists)
     * </li>
     * </ul>
     * @param elementSymbol Element symbol to map
     * @param tempID the TempMetadataID
     * @return Mapped symbol of original if no mapping was found 
     */
    private ElementSymbol mapPseudoElement(ElementSymbol elementSymbol,TempMetadataID tempID) {
        ElementSymbol result = null;
        if(elementSymbol!=null && tempID!=null) {
            result = (ElementSymbol)elementSymbol.clone();
            // Try to do a name lookup using the element name
            String elemName = elementSymbol.getName();
            Object elemID = getElementIDFromName(elemName);
            // Element ID lookup failed, try group lookup
            if(elemID==null) {
                // Get elements parent group
                GroupSymbol gSymbol = elementSymbol.getGroupSymbol();
                if(gSymbol!=null) {
                    // Get groupName
                    String grpName = gSymbol.getName();
                    // Try lookup using group Name
                    Object grpID = getGroupIDFromName(grpName);
                    // lookup was successful, look thru its elements for matching name
                    if(grpID!=null) {
                        // GroupID - StoredProcedure
                        if(grpID instanceof StoredProcedureInfo) {
                            StoredProcedureInfo storedProcInfo = (StoredProcedureInfo)grpID;
                            // Get StoredProcedure parameters
                            List params = storedProcInfo.getParameters();
                            // Look for parameter name matching element name
                            Iterator paramIter = params.iterator();
                            while(paramIter.hasNext()) {
                                SPParameter spp = (SPParameter)paramIter.next();
                                String paramName = spp.getName();
                                if(paramName.equalsIgnoreCase(elemName)) {
                                    elemID = spp.getMetadataID();
                                    if(elemID!=null && elemID instanceof MetadataRecord) {
                                        EObject elemEObject = (EObject)((MetadataRecord)elemID).getEObject();
                                        if ( toUUID ) {
                                            setElementSymbolToUID(result,elemEObject);
                                        } else {
                                            setElementSymbolToName(result,elemEObject);
                                        }
                                    }
                                    break;
                                }
                            }
                        } else {
                        }
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Get the elementID from the QueryMetadataInterface for the supplied element name.  Returns null
     * if the element could not be found 
     * @param elemName the elementName to find
     * @return the Element ID for the supplied name
     */
    private Object getElementIDFromName(String elemName) {
        Object elemID = null;
        if(qmi!=null) {
            try {
                elemID = qmi.getElementID(elemName);
            // log info message only
            } catch (Exception e) {
                //TransformationPlugin.Util.log(IStatus.INFO, "[SymbolUUIDMappingVisitor.getElementIDFromName] Couldnt find elementID"); //$NON-NLS-1$ 
            }
        }
        return elemID;
    }
    
    /**
     * Get the groupID from the QueryMetadataInterface for the supplied group name.  Returns null
     * if the group could not be found 
     * @param grpName the elementName to find
     * @return the group ID for the supplied name
     */
    private Object getGroupIDFromName(String grpName) {
        // Try lookup using group Name
        Object grpID = null;
        if(qmi!=null) {
            // First try to find a groupID with group Name
            try {
                grpID = qmi.getGroupID(grpName);
            // log info message only
            } catch (Exception e) {
                //TransformationPlugin.Util.log(IStatus.INFO, "[SymbolUUIDMappingVisitor.getGroupIDFromName] Couldnt find groupID"); //$NON-NLS-1$ 
            }
            // If groupID lookup failed, try to find a StoredProc with the Name
            if(grpID==null) {
                try {
                    grpID = qmi.getStoredProcedureInfoForProcedure(grpName);
                // log info message only
                } catch (Exception e) {
                    //TransformationPlugin.Util.log(IStatus.INFO, "[SymbolUUIDMappingVisitor.getGroupIDFromName] Couldnt find groupID"); //$NON-NLS-1$ 
                }
            }
        }
        return grpID;
    }


	@Override
	protected boolean createAliases() {
		// TODO Auto-generated method stub
		return false;
	}
}
