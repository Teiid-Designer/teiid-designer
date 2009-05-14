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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.id.UUID;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.GroupSymbol;
import com.metamatrix.query.sql.symbol.Symbol;
import com.metamatrix.query.sql.visitor.AbstractSymbolMappingVisitor;

/**
 * SqlSymbolUUIDMappingVisitor for mapping SQL UUIDs to fullnames.
 */
public class SqlSymbolUUIDMappingVisitor extends AbstractSymbolMappingVisitor {

    private static final char SEPARATOR_CHAR = '.';
    private static final int UUID_LENGTH = 43;
    private Collection emfResources = Collections.EMPTY_LIST;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    
    /**
     * <p> This constructor initialises the visitor</p>
     */
    public SqlSymbolUUIDMappingVisitor( ) {
        super();
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * Get the mapped symbol from the specified symbol.  Subclasses should implement
     * this method to look up the target symbol from the specified symbol.
     * @param symbol Source symbol
     * @return Target symbol
     */
    @Override
    protected Symbol getMappedSymbol(final Symbol symbol) {
        if ( symbol instanceof GroupSymbol ) {
            return mapGroupSymbol((GroupSymbol) symbol);
        } else if ( symbol instanceof ElementSymbol ) {
            return mapElementSymbol((ElementSymbol) symbol);
        }
        return symbol;
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * Set the ResourceSet to use for UUID resolution
     * @param resourceSet the ResourceSet to use in resolving the sql.
     */
    public void setResources(final Collection eResources) {
        // Create a list of only the EmfResource instances contained within
        // the resource set.  Only EmfResource instances can resolve a uuid.
        if (eResources != null) {
            this.emfResources = new ArrayList();
            
            for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
                final Resource resource = (Resource)iter.next();
                if (resource instanceof EmfResource) {
                    this.emfResources.add(resource);
                }
            }
        }
    }

    /**
     * Map the GroupSymbol
     * @param groupSymbol the GroupSymbol
     * @return the GroupSymbol after mapping
     */
    private GroupSymbol mapGroupSymbol(final GroupSymbol groupSymbol) {
        // clone the groupSymbol as our return object
        GroupSymbol result = (GroupSymbol) groupSymbol.clone();
        
        // Get the EObject that is resolved to the symbol
        String groupName = (groupSymbol.getDefinition() != null ? groupSymbol.getDefinition() : groupSymbol.getName());
        EObject groupEObject = (EObject)getGroupIDFromName(groupName);
        
        if ( groupEObject != null ) {
            // set the symbol name
            String newName = getSqlEObjectFullName(groupEObject);
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

        // Get the EObject that is resolved to the ElementSymbol
        EObject elementEObject = (EObject) getElementIDFromName(elementSymbol.getName());

        // convert the cloned element symbol's GroupSymbol
        GroupSymbol gSymbol = elementSymbol.getGroupSymbol();
        GroupSymbol mappedGroupSymbol = null;
        if (gSymbol != null) {
            mappedGroupSymbol = (GroupSymbol) getMappedSymbol(gSymbol);
        } else if (elementEObject != null) {
            final String fullName = getSqlEObjectParentName(elementEObject);
            if (fullName == null) {
                // the LanguageObjects must be resolved to use this Visitor
                throw new RuntimeException("Error using MetaObjectSymbolMappingVisitor: LanguageObjects must be resolved first"); //$NON-NLS-1$
            }
            mappedGroupSymbol = new GroupSymbol(fullName);
        }
        result.setGroupSymbol(mappedGroupSymbol);

        // Set the EObject on the ElementSymbol
        setElementSymbolToName(result,elementEObject);
            
        return result;
    }
    
    /**
     * Set the GroupSymbol name, using the supplied name string.
     * @param gSymbol the GroupSymbol
     * @param name the group symbol name
     */
    private void setGroupSymbolName(GroupSymbol gSymbol, String name) {
        if (gSymbol != null) {
            // Get symbol Defn
            String symbolDefn = gSymbol.getDefinition();
            // set the symbols name
            if( symbolDefn != null ) {
                // the name is the alias, which got cloned.  set the definition to the supplied string
                gSymbol.setDefinition(name);
            } else {
                // set the name to the supplied string, the definition is null
                gSymbol.setName(name);
            }
        }
    }

    /**
     * Set the ElementSymbol to the name version of the symbol, using the supplied EObject.
     * @param eSymbol the ElementSymbol
     * @param elementEObject the EObject to use in setting the symbol properties
     */
    private void setElementSymbolToName(final ElementSymbol eSymbol, final EObject elementEObject) {
        // Get parent GroupSymbol
        GroupSymbol gSymbol = eSymbol.getGroupSymbol();
        if (elementEObject != null && gSymbol != null) {
            // Set the elementSymbol
            if ( gSymbol.getDefinition() != null ) {
                String name = getSqlEObjectName(elementEObject);
                // the group has an alias, so use that in the SymbolElement's name
                eSymbol.setName(gSymbol.getName() + SEPARATOR_CHAR + name);
            } else {
                String fullName = getSqlEObjectFullName(elementEObject);
                eSymbol.setName(fullName);
            }
            eSymbol.setDisplayFullyQualified(true);
        }
    }
    
    /**
     * Get the elementID from the QueryMetadataInterface for the supplied element name.  Returns null
     * if the element could not be found 
     * @param elemName the elementName to find
     * @return the Element ID for the supplied name
     */
    private Object getElementIDFromName(final String elemName) {
        Object elemID = null;
        String uuid   = getUuidString(elemName);
        if (uuid != null) {
            return this.findEObjectInResourceSet(uuid);
        }
        return elemID;
    }
    
    /**
     * Get the groupID from the QueryMetadataInterface for the supplied group name.  Returns null
     * if the group could not be found 
     * @param grpName the elementName to find
     * @return the group ID for the supplied name
     */
    private Object getGroupIDFromName(final String grpName) {
        // Try lookup using group Name
        Object grpID = null;
        String uuid  = getUuidString(grpName);
        if (uuid != null) {
            return this.findEObjectInResourceSet(grpName);
        }
        return grpID;
    }
    
    public static String getUuidString(final String str) {
        if (str == null){
            return null;
        }
        final String strLower = str.toLowerCase();
        int index = strLower.indexOf(UUID.PROTOCOL);
        if ( index == -1 ) {
            // there were no UUIDs in the string
            return null;
        }
        
        if (index == 0) {
            return strLower;
        } else if (strLower.length() == (index + UUID_LENGTH)) {
            return strLower.substring(index);
        } else if (strLower.length() > (index + UUID_LENGTH)) {
            return strLower.substring(index, index + UUID_LENGTH);
        }
        return null;
    }
    
    private EObject findEObjectInResourceSet(final String uuid) {
        for (final Iterator iter = this.emfResources.iterator(); iter.hasNext();) {
            final Resource resource = (Resource)iter.next();
            final EObject eObj = resource.getEObject(uuid);
            if (eObj != null) {
                return eObj;
            }
        }
        return null;
    }
    
    public static String getSqlEObjectName(final EObject eObject) {
        String returnString = "NULL";  //$NON-NLS-1$
        SqlAspect aspect = getSqlAspect(eObject);
        if( aspect != null ) {
            returnString = aspect.getName(eObject);
        }
        return returnString;
    }
    
    public static String getSqlEObjectFullName(final EObject eObject) {
        String returnString = "NULL";  //$NON-NLS-1$
        SqlAspect aspect = getSqlAspect(eObject);
        if( aspect != null ) {
            returnString = aspect.getFullName(eObject);
        }
        return returnString;
    }
    
    protected static String getSqlEObjectParentName(final EObject eObject) {
        if (eObject == null) {
            return null;
        }
        EObject parent = eObject.eContainer();
        if ( parent != null ) {
            SqlAspect parentAspect = getSqlAspect(parent);
            while ( parentAspect != null ) {
                if (parentAspect instanceof SqlTableAspect) {
                    return parentAspect.getName(parent);
                }
                parent = parent.eContainer();
                if ( parent != null ) {
                    parentAspect = getSqlAspect(parent);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    
    protected static String getSqlEObjectParentFullName(final EObject eObject) {
        if (eObject == null) {
            return null;
        }
        EObject parent = eObject.eContainer();
        if ( parent != null ) {
            SqlAspect parentAspect = getSqlAspect(parent);
            while ( parentAspect != null ) {
                if (parentAspect instanceof SqlTableAspect) {
                    return parentAspect.getFullName(parent);
                }
                parent = parent.eContainer();
                if ( parent != null ) {
                    parentAspect = getSqlAspect(parent);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    
    protected static SqlAspect getSqlAspect(final EObject eObject) {
        return AspectManager.getSqlAspect(eObject);
    }

	@Override
	protected boolean createAliases() {
		// TODO Auto-generated method stub
		return false;
	}

}
