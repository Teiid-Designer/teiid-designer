/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;

/**
 * MappingClassGenerationVisitor
 */
public class MappingClassGenerationVisitor {
    
    protected ITreeToRelationalMapper mapper;
    protected MappingClassFactory factory;
    protected Map attributeMap;
    protected boolean createAttributes;
    protected boolean markNextMappingClassRecursive = false;
    protected boolean initialBuild = false;
    protected Set datatypeAccumulator;

    /**
     * Construct an instance of MappingClassGenerationVisitor.
     */
    public MappingClassGenerationVisitor(ITreeToRelationalMapper mapper, 
                                         MappingClassFactory factory,
                                         Map mappingClassAttributeMap, 
                                         boolean createAttributes,
                                         boolean initialBuild,
                                         Set datatypeAccumulator) {
        this.mapper = mapper;
        this.factory = factory;
        this.attributeMap = mappingClassAttributeMap;
        this.createAttributes = createAttributes;
        this.initialBuild = initialBuild;
        this.datatypeAccumulator = datatypeAccumulator;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit(EObject object) {
        if ( attributeMap.containsKey(object) ) {

            Collection attributes = (Collection) attributeMap.get(object);
            if ( attributes.size() == 0 ) {
                // must put mapping classes at choice nodes and recursive nodes
                if ( ! mapper.isChoiceNode(object) && ! mapper.isRecursive(object)) {
                    return true;
                }
            }
            
            // set it's recursion property value
            if ( mapper.isRecursive(object) ) {
                markNextMappingClassRecursive = true;
            }

            factory.createMappingClass(object, markNextMappingClassRecursive);

            markNextMappingClassRecursive = false;
        } else if ( mapper.isRecursive(object) ) {
            markNextMappingClassRecursive = true;
        }
        
        return true;

    }

}
