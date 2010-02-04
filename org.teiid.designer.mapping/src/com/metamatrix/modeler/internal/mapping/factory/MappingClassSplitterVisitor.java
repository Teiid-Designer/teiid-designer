/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;

/**
 * MappingClassSplitterVisitor
 */
public class MappingClassSplitterVisitor extends MappingClassGenerationVisitor {

    private MappingClass mappingClassToSplit;
    private TreeMappingAdapter mapping;
    private List mappingClassList;
    private Map mappingClassLocationMap;

    /**
     * Construct an instance of MappingClassSplitterVisitor.
     * @param mapper
     * @param factory
     * @param mappingClassAttributeMap
     * @param createAttributes
     */
    public MappingClassSplitterVisitor(
        MappingClass mappingClassToSplit,
        TreeMappingAdapter mapping,
        ITreeToRelationalMapper mapper,
        MappingClassFactory factory,
        Map mappingClassAttributeMap) {
            
        super(mapper, factory, mappingClassAttributeMap, false, false, new HashSet());

        this.mappingClassToSplit = mappingClassToSplit;
        this.mapping = mapping;
        this.mappingClassList = mapping.getAllMappingClasses();
        generateMappingClassLocationMap();

    }

    @Override
    public boolean visit(EObject node) {

        // see if the map thinks this node should store a mapping class
        if ( super.attributeMap.containsKey(node) ) {
        
            // the map thinks there should be a mapping class here.  See if there is already one here.
            if ( mappingClassLocationMap.containsKey(node) ) {
                // already a MappingClass here - skip it
                markNextMappingClassRecursive = false;
            } else {
                // no mapping class here, so create one

                // set it's recursion property value
                if ( mapper.isRecursive(node) ) {
                    markNextMappingClassRecursive = true;
                }

                MappingClass mappingClass =
                             super.factory.createMappingClass(node, true, markNextMappingClassRecursive);

                markNextMappingClassRecursive = false;

                // remove this node from the mappingClassToSplit's document reference list
                mapping.removeMappingClassLocation(mappingClassToSplit, node);
                // keep track of names for one-to-many mappings
                Map nameAttributeMap = new HashMap();

                super.factory.moveOrCreateMappingClassColumns(mappingClass, (Collection) super.attributeMap.get(node), nameAttributeMap, false, super.datatypeAccumulator);
                
            }

        } else if ( mapper.isRecursive(node) ) {
            markNextMappingClassRecursive = true;
        }
        
        return true;
    }
    
    private void generateMappingClassLocationMap() {
        mappingClassLocationMap = new HashMap();
        Iterator iter = this.mappingClassList.iterator();
        while ( iter.hasNext() ) {
            MappingClass mappingClass = (MappingClass) iter.next();
            Iterator locationIter = mapping.getMappingClassOutputLocations(mappingClass).iterator();
            while ( locationIter.hasNext() ) {
                EObject documentLocation = (EObject) locationIter.next();
                if ( documentLocation != null ) {
                    mappingClassLocationMap.put(documentLocation, mappingClass);
                }
            }
        }
    }

}
