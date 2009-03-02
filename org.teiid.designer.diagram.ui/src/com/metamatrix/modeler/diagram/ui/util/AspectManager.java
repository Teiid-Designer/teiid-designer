/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.DependencyAspect;
import com.metamatrix.modeler.core.metamodel.aspect.FeatureConstraintAspect;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * AspectManager
 */
public class AspectManager {
    
    // Mappings between the plugin.xml extension ID for the MetamodelAspectFactory and the aspect type produced by that factory
    private static final Map<String, Class<? extends MetamodelAspect>> EXTENSION_ID_TO_ASPECT_INTERFACE_MAP = new HashMap<String, Class<? extends MetamodelAspect>>(7);
    static {
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID,                SqlAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID,        UmlDiagramAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.VALIDATION_ASPECT.ID,         ValidationAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.DEPENDENCY_ASPECT.ID,         DependencyAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.FEATURE_CONSTRAINT_ASPECT.ID, FeatureConstraintAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.ID,             ImportsAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.ID,       RelationshipMetamodelAspect.class);
    }
    
    private HashMap<EClass, MetamodelAspect> hmAspectCache;
    private String sAspectId;
    

    public AspectManager( String sAspectId ) {
        
        hmAspectCache = new HashMap<EClass, MetamodelAspect>();
        setAspectId( sAspectId );
    }
    
    public AspectManager() {
        this( ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID  );
    }
    
    public void setAspectId( String sAspectId ) {
        this.sAspectId = sAspectId;
        
        // if we change aspect ids, toss the old cache entries
        if ( hmAspectCache != null ) {
            hmAspectCache.clear();
        }
    }
    
    public String getAspectId() {
        return sAspectId;
    }    
    
    
    /**
     * Helper method to get the UmlAspect given an EObject
     */
    public MetamodelAspect getUmlAspect(EObject eObject) {
        EClass eClass = eObject.eClass();

        // If aspect for this EClass is not in the cache, add it.        
        if(!hmAspectCache.containsKey(eClass)) {

           final Class<? extends MetamodelAspect> type = EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.get(getAspectId());
           if (type != null) {
               MetamodelAspect aspect  = ModelerCore.getMetamodelRegistry().getMetamodelAspect( eClass, type );
               if( aspect != null ) {
                   hmAspectCache.put( eClass,aspect );
               }
           }
        } 

        return hmAspectCache.get(eClass);    
    }

    /**
     * Helper method to get a list of children beneath a given model object.
     * @param parent
     * @return
     */
    public List<Object> getChildren(EObject parent) {
        ITreeContentProvider provider = ModelUtilities.getModelContentProvider();
        if ( provider.hasChildren(parent) ) {
            return Arrays.asList(provider.getChildren(parent));
        }
        return Collections.emptyList();
    }
    
    public boolean isClassifier(EObject eObject ) {
    	MetamodelAspect someAspect = getUmlAspect(eObject);
    	if(someAspect != null && someAspect instanceof UmlClassifier ) {
    		return true;
    	}
    	return false;
    }

}
