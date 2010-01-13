/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.util.NewModelObjectHelperManager;
import com.metamatrix.modeler.internal.core.ClearEObjectReferences;
import com.metamatrix.modeler.mapping.ModelerMappingPlugin;
import com.metamatrix.modeler.mapping.PluginConstants;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.mapping.factory.MappableTreeIterator;
import com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy;

/**
 * MappingClassFactory is a class for creating and editing mapping classes and attributes in a mappable tree.
 */
public class MappingClassFactory {

    private static MappingClassBuilderStrategy defaultStrategy;

    private static final String STRATEGY_PREF_KEY = "MappingClassFactory.defaultStrategy"; //$NON-NLS-1$
    private static final String COMPOSITOR_PREF_VALUE = "MappingClassFactory.compositorStrategy"; //$NON-NLS-1$
    private static final String ITERATION_PREF_VALUE = "MappingClassFactory.iterationStrategy"; //$NON-NLS-1$

    public static MappingClassBuilderStrategy getDefaultStrategy() {
        if (defaultStrategy == null) {
            String defValue = ModelerMappingPlugin.getDefault().getPluginPreferences().getString(STRATEGY_PREF_KEY);
            if (COMPOSITOR_PREF_VALUE.equals(defValue)) {
                defaultStrategy = MappingClassBuilderStrategy.compositorStrategy;
            } else {
                defaultStrategy = MappingClassBuilderStrategy.iterationStrategy;
            }
        }
        return defaultStrategy;
    }

    public static void setDefaultStrategy( MappingClassBuilderStrategy strategy ) {
        if (strategy instanceof CompositorBasedBuilderStrategy) {
            ModelerMappingPlugin.getDefault().getPluginPreferences().setValue(STRATEGY_PREF_KEY, COMPOSITOR_PREF_VALUE);
        } else if (strategy instanceof IterationBasedBuilderStrategy) {
            ModelerMappingPlugin.getDefault().getPluginPreferences().setValue(STRATEGY_PREF_KEY, ITERATION_PREF_VALUE);
        }
        defaultStrategy = strategy;
    }

    private EObject treeRoot;
    private ITreeToRelationalMapper mapper;
    private IMappableTree tree;
    private TreeMappingAdapter mapping;
    private AdapterFactory emfAdapter;
    private TransformationFactory metamodelFactory;
    private MappingClassSet mappingClassSet;

    private boolean generatingMappingClasses = false;

    /**
     * Construct an instance of MappingClassBuilder.
     */
    public MappingClassFactory( ITreeToRelationalMapper mapper ) {
        this.mapper = mapper;
        this.tree = mapper.getMappableTree();
        this.treeRoot = mapper.getMappableTree().getTreeRoot();
        this.mapping = new TreeMappingAdapter(treeRoot);
        this.metamodelFactory = TransformationFactory.eINSTANCE;
    }

    /**
     * Construct an instance of MappingClassBuilder.
     */
    public MappingClassFactory( ITreeToRelationalMapper mapper,
                                TreeMappingAdapter mapping ) {
        this.mapper = mapper;
        this.tree = mapper.getMappableTree();
        this.treeRoot = mapper.getMappableTree().getTreeRoot();
        this.mapping = mapping;
        this.metamodelFactory = TransformationFactory.eINSTANCE;
    }

    public TreeMappingAdapter getMappingAdapter() {
        return mapping;
    }

    /**
     * Recursive method to generate Mapping Classes at and beneath the specified node.
     * 
     * @param node the model object in the tree to begin looking for flat fragments.
     * @param autoPopulateAttributes if true, this method will automatically generate virtual mapping attributes within the new
     *        mapping classes and assign their values to the mapping property of the corresponding document model nodes.
     * @return a Set of all datatypes assigned to the Mapping Class Columns that were created.
     */
    public Set generateMappingClasses( EObject node,
                                       MappingClassBuilderStrategy strategy,
                                       boolean autoPopulateAttributes ) {

        setGeneratingMappingClasses(true);

        Set result = new HashSet();
        Map mappingClassMap = strategy.buildMappingClassMap(node, this.tree, this.mapper);
        MappingClassGenerationVisitor visitor = new MappingClassGenerationVisitor(mapper, this, mappingClassMap,
                                                                                  autoPopulateAttributes, true, result);

        MappableTreeIterator nodeIter = new MappableTreeIterator(this.tree);

        if (nodeIter.hasNext()) {
            // skip over the root itself
            nodeIter.next();
        }

        while (nodeIter.hasNext()) {
            visitor.visit((EObject)nodeIter.next());
        }

        finishGeneratingMappingClasses(autoPopulateAttributes);
        setGeneratingMappingClasses(false);

        return result;
    }

    private void setGeneratingMappingClasses( boolean generating ) {
        mapping.setGeneratingMappingClasses(generating);
        generatingMappingClasses = generating;
    }

    public boolean isGeneratingMappingClasses() {
        return generatingMappingClasses;
    }

    /**
     * Determine if a MappingClass can be created at the specified location.
     * 
     * @param location
     * @return
     */
    public boolean canCreateMappingClass( EObject location ) {
        // Defect 21999
        // mapping AND mapper should NEVER be null
        if (mapping != null) {
            // if there is already a mapping class at this location, you can't add another one
            if (mapping.getMappingClass(location) != null) {
                return false;
            }
        }
        if (mapper != null) {
            return mapper.allowsMappingClass(location);
        }

        return false;
    }

    /**
     * Determine if a MappingClass can be created at the specified location.
     * 
     * @param location
     * @return
     */
    public boolean canCreateStagingTable( EObject location ) {
        // Defect 21999
        // mapping AND mapper should NEVER be null
        if (mapping != null) {
            // if there is already a mapping class at this location, you can't add another one
            if (mapping.getStagingTable(location) != null) {
                return false;
            }
        }
        if (mapper != null) {
            // can put a StagingTable anywhere you can put a MappingClass
            return mapper.allowsMappingClass(location);
        }

        return false;
    }

    /**
     * Create a MappingClass at the specified location of the mappable tree.
     * 
     * @param location
     * @param moveParentAttributes
     * @param markRecursive
     * @return
     */
    public MappingClass createMappingClass( EObject location,
                                            boolean moveParentAttributes,
                                            boolean markRecursive ) {
        //        System.out.println("  <<<< TEMP LOGGING >>>> MappingClassFactory.createMappingClass() START"); //$NON-NLS-1$
        //startTracking("createMappingClass()"); //$NON-NLS-1$
        MappingClass newMappingClass = metamodelFactory.createMappingClass();
        if (newMappingClass != null) {
            boolean setGenerated = false;
            if (!isGeneratingMappingClasses()) {
                setGeneratingMappingClasses(true);
                setGenerated = true;
            }

            EObject newTreeMappingRoot = mapping.createTreeMappingRoot(newMappingClass);

            newMappingClass.setMappingClassSet(getMappingClassSet());
            newMappingClass.setName(convertLocationNameToMappingClassName(location));

            InputSet inputSet = metamodelFactory.createInputSet();
            inputSet.setMappingClass(newMappingClass);

            mapping.addMappingClassAtLocation(newTreeMappingRoot, newMappingClass, location);

            if (markRecursive) {
                newMappingClass.setRecursionAllowed(true);
            }

            // Whenever a MappingClass is created, the corresponding Transformation should be created
            ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(newMappingClass);
            // Fix for Defect 17638
            if (modelContents != null) {
                // Check to see if transformation exists already. (Don't duplicate it)
                List allTransforms = modelContents.getTransformations(newMappingClass);
                if (allTransforms.isEmpty()) {
                    // Defect 18433 - BML 9/15/05 - Changed to add both root object (and helpers) and
                    // Transformation Diagram
                    try {
                        NewModelObjectHelperManager.helpCreate(newMappingClass, null);
                    } catch (ModelerCoreException err) {
                        PluginConstants.Util.log(err);
                    }
                }
            }

            if (moveParentAttributes) {

                Collection documentNodes = getMappingClassExtentNodes(newMappingClass);
                Collection mappableDocumentNodes = new ArrayList(documentNodes.size() + 1);

                // first, see if this node itself is mappable
                if (mapper.isMappable(location)) {
                    // make or move a mapping attribute for this attribute
                    mappableDocumentNodes.add(location);
                }

                // next, gather all mappable nodes in the coarse extent
                Iterator iter = documentNodes.iterator();
                while (iter.hasNext()) {
                    EObject docNode = (EObject)iter.next();
                    if (mapper.isMappable(docNode) && !mappableDocumentNodes.contains(docNode)) {
                        mappableDocumentNodes.add(docNode);
                    }
                }

                // finally, move or create the attributes, as appropriate
                if (!mappableDocumentNodes.isEmpty()) {
                    moveOrCreateMappingClassColumns(newMappingClass, mappableDocumentNodes, new HashMap(), false, new HashSet());
                }
            }
            if (setGenerated) {
                setGeneratingMappingClasses(false);
            }
        }
        return newMappingClass;
    }

    /**
     * Create a MappingClass at the specified location of the mappable tree.
     * 
     * @param location
     * @param moveParentAttributes
     * @param markRecursive
     * @return
     */
    public MappingClass createMappingClass( EObject location,
                                            boolean markRecursive ) {
        //startTracking("createMappingClass(GENERATING)"); //$NON-NLS-1$
        MappingClass result = metamodelFactory.createMappingClass();
        if (result != null) {
            EObject newTreeMappingRoot = mapping.createTreeMappingRoot(result);

            result.setMappingClassSet(getMappingClassSet());
            result.setName(convertLocationNameToMappingClassName(location));

            mapping.addMappingClassAtLocation(newTreeMappingRoot, result, location);

            InputSet inputSet = metamodelFactory.createInputSet();
            inputSet.setMappingClass(result);

            if (markRecursive) {
                result.setRecursionAllowed(true);
            }

            // Whenever a MappingClass is created, the corresponding Transformation should be created
            ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(result);
            // Fix for Defect 17638
            if (modelContents != null) {
                // Check to see if transformation exists already. (Don't duplicate it)
                List allTransforms = modelContents.getTransformations(result);
                if (allTransforms.isEmpty()) {
                    // Defect 18433 - BML 9/15/05 - Changed to add both root object (and helpers) and
                    // Transformation Diagram
                    try {
                        NewModelObjectHelperManager.helpCreate(result, null);
                    } catch (ModelerCoreException err) {
                        PluginConstants.Util.log(err);
                    }
                }
            }
        }
        return result;
    }

    private void finishGeneratingMappingClasses( boolean createAttributes ) {
        // re-order keeper's structure
        Iterator iter = getMappingClassSet().getMappingClasses().iterator();
        MappingClass mappingClass = null;
        while (iter.hasNext()) {
            mappingClass = (MappingClass)iter.next();
            EObject attribute = mapping.getMappingClassLocation(mappingClass);
            finishCreateMappingClass(attribute, mappingClass, createAttributes);
        }

    }

    private void finishCreateMappingClass( EObject location,
                                           MappingClass mappingClass,
                                           boolean moveParentAttributes ) {

        if (mappingClass != null) {

            if (moveParentAttributes) {

                Collection documentNodes = getMappingClassExtentNodes(mappingClass);
                Collection mappableDocumentNodes = new ArrayList(documentNodes.size() + 1);

                // first, see if this node itself is mappable
                if (mapper.isMappable(location)) {
                    // make or move a mapping attribute for this attribute
                    mappableDocumentNodes.add(location);
                }

                // next, gather all mappable nodes in the coarse extent
                Iterator iter = documentNodes.iterator();
                while (iter.hasNext()) {
                    EObject docNode = (EObject)iter.next();
                    if (mapper.isMappable(docNode) && !mappableDocumentNodes.contains(docNode)) {
                        mappableDocumentNodes.add(docNode);
                    }
                }

                // finally, move or create the attributes, as appropriate
                if (!mappableDocumentNodes.isEmpty()) {
                    moveOrCreateMappingClassColumns(mappingClass, mappableDocumentNodes, new HashMap(), true, new HashSet());
                }
            }

        }
    }

    /**
     * Obtain an ordered list of all locations visible in the TreeViewer that are in the extent of the specified MappingClass.
     * 
     * @param theMappingClass
     * @return
     */
    public List getMappingClassExtentNodes( MappingClass theMappingClass ) {
        // get all locations for this mapping class

        List extentNodes = new ArrayList();

        List locations = mapping.getMappingClassOutputLocations(theMappingClass);

        List columnLocations = new ArrayList();
        for (Iterator iter = theMappingClass.getColumns().iterator(); iter.hasNext();) {
            columnLocations.addAll(mapping.getMappingClassColumnOutputLocations((MappingClassColumn)iter.next()));
        }

        if (!locations.isEmpty()) {
            List allMCLocations = mapping.getAllMappingClassLocations();
            for (Iterator iter = locations.iterator(); iter.hasNext();) {
                EObject nextLocation = (EObject)iter.next();
                // add the location to the collection of extent nodes
                extentNodes.add(nextLocation);
                // recurse down this location and collect up the extent nodes
                extentNodes.addAll(gatherExtentNodes(nextLocation, columnLocations, allMCLocations));
            }
        }

        // return the extent nodes
        return extentNodes;
    }

    /**
     * Create a StagingTable at the specified location.
     * 
     * @param location
     * @return
     */
    public StagingTable createStagingTable( EObject location ) {
        StagingTable newStagingTable = metamodelFactory.createStagingTable();

        if (newStagingTable != null) {
            EObject newTreeMappingRoot = mapping.createTreeMappingRoot(newStagingTable);

            newStagingTable.setMappingClassSet(getMappingClassSet());
            newStagingTable.setName(convertLocationNameToStagingTableName(location));

            // set the Document Reference property value on the MappingClass
            // Note: setLocation() will result in the TreeMappingRoot being created
            mapping.addStagingTableAtLocation(newTreeMappingRoot, newStagingTable, location);

            // Whenever a Staging Table is created, the corresponding Transformation should be created
            ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(newStagingTable);
            // Fix for Defect 17638
            if (modelContents != null) {
                // Check to see if transformation exists already. (Don't duplicate it)
                List allTransforms = modelContents.getTransformations(newStagingTable);
                if (allTransforms.isEmpty()) {
                    // Defect 18433 - BML 9/15/05 - Changed to add both root object (and helpers) and
                    // Transformation Diagram
                    try {
                        NewModelObjectHelperManager.helpCreate(newStagingTable, null);
                    } catch (ModelerCoreException err) {
                        PluginConstants.Util.log(err);
                    }
                }
            }
        }
        return newStagingTable;
    }

    /**
     * Delete the specified StagingTable. Also unhooks the StagingTable from this tree mapping.
     * 
     * @param table
     * @throws ModelerCoreException
     */
    public void deleteStagingTable( StagingTable table ) throws ModelerCoreException {
        mapping.deleteMappingClass(table);

    }

    /**
     * Delete the specified MappingClass. Also unhooks the MappingClass from this tree mapping.
     * 
     * @param mappingClass
     * @throws ModelerCoreException
     */
    public void deleteMappingClass( MappingClass mappingClass ) throws ModelerCoreException {
        mapping.deleteMappingClass(mappingClass);
    }

    /**
     * Delete the specified MappingClassColumn. Also unhooks the column from any tree mappings.
     * 
     * @param column
     * @throws ModelerCoreException
     */
    public void deleteMappingClassColumn( MappingClassColumn column ) throws ModelerCoreException {
        ArrayList locationList = new ArrayList(mapping.getMappingClassColumnOutputLocations(column));
        for (Iterator iter = locationList.iterator(); iter.hasNext();) {
            mapping.removeMappingClassColumnLocation(column, (EObject)iter.next());
        }
        ModelerCore.getModelEditor().delete(column);
    }

    /**
     * Determine if the specified MappingClasses may be merged into one. This is only allowed if they are siblings or if the
     * upperMappingClass has a location that is an ancestor of the lower MappingClass.
     * 
     * @param upperMappingClass
     * @param lowerMappingClass
     * @return
     */
    public boolean canMergeMappingClasses( MappingClass upperMappingClass,
                                           MappingClass lowerMappingClass ) {
        boolean result = false;

        // two classes can be merged if their location nodes are siblings or if one is a descendent of the other

        List topLocations = mapping.getMappingClassOutputLocations(upperMappingClass);
        if (!topLocations.isEmpty()) {
            EObject topLocation = (EObject)topLocations.get(0);

            List bottomLocations = mapping.getMappingClassOutputLocations(lowerMappingClass);
            if (!bottomLocations.isEmpty()) {
                // any node will do
                EObject bottomLocation = (EObject)bottomLocations.get(0);

                EObject topParent = tree.getParent(topLocation);
                EObject bottomParent = tree.getParent(bottomLocation);

                if (topParent.equals(bottomParent)) {
                    result = true;
                } else if (tree.isAncestorOf(topLocation, bottomLocation) || tree.isAncestorOf(bottomLocation, topLocation)) {
                    // the can also be merged if one is an ancestor of the other
                    result = true;
                } else {
                    // the nodes must share a common Choice ancestor
                    boolean keepGoing = true;
                    EObject topChoice = null;
                    EObject bottomChoice = null;

                    while (keepGoing) {
                        // see if we have found a choice node ancestor for topParent
                        if (topChoice == null) {
                            topParent = tree.getParent(topParent);
                            if (topParent != null && tree.isChoiceNode(topParent)) {
                                topChoice = topParent;
                            }
                        }

                        // see if we have found a choice node ancestor for bottomParent
                        if (bottomChoice == null) {
                            bottomParent = tree.getParent(bottomParent);
                            if (bottomParent != null && tree.isChoiceNode(bottomParent)) {
                                bottomChoice = bottomParent;
                            }
                        }

                        if (topChoice != null && bottomChoice != null) {
                            // found two choice nodes, but they must be the same one
                            result = topChoice.equals(bottomChoice);

                            // no need to keep going
                            keepGoing = false;
                        }

                        // if either returns null, then we're at the top - break out
                        if (topParent == null || bottomParent == null) {
                            keepGoing = false;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Merge the specified mapping classes into one. This method assumes that canMergeMappingClasses has already been called and
     * returned true.
     * 
     * @param upperMappingClass
     * @param lowerMappingClass
     * @throws ModelerCoreException
     */
    public void mergeMappingClasses( MappingClass upperMappingClass,
                                     MappingClass lowerMappingClass,
                                     boolean removeDuplicates ) throws ModelerCoreException {
        setGeneratingMappingClasses(true);
        List upperLocations = mapping.getMappingClassOutputLocations(upperMappingClass);
        List lowerLocations = new ArrayList(mapping.getMappingClassOutputLocations(lowerMappingClass));

        // if any bottom document node is a descendent of any top node (merging up to a parent),
        // then just add the bottom references to the top ones
        boolean keepLooking = true;
        Iterator topIter = upperLocations.iterator();
        while (keepLooking && topIter.hasNext()) {
            EObject topLocation = (EObject)topIter.next();
            Iterator bottomIter = lowerLocations.iterator();
            while (keepLooking && bottomIter.hasNext()) {
                EObject bottomLocation = (EObject)bottomIter.next();
                if (tree.isAncestorOf(topLocation, bottomLocation)) {
                    keepLooking = false;
                }
            }
        }

        // if we never found an ancestor, then we are merging siblings
        if (keepLooking) {
            // add the bottom document references to the top
            Iterator bottomIter = lowerLocations.iterator();
            while (bottomIter.hasNext()) {
                EObject location = (EObject)bottomIter.next();
                mapping.addMappingClassLocation(upperMappingClass, location);
                mapping.removeMappingClassLocation(lowerMappingClass, location);
            }
        }

        Collection bottomAttributes = lowerMappingClass.getColumns();
        Collection copyOfChildren = new ArrayList(bottomAttributes);

        // Move attributes from bottom mapping class to top mapping class.
        Iterator iter = copyOfChildren.iterator();
        MappingClassColumn nextAttribute = null;
        MappingClassColumn duplicateAttribute = null;

        while (iter.hasNext()) {
            nextAttribute = (MappingClassColumn)iter.next();
            List locations = mapping.getMappingClassColumnOutputLocations(nextAttribute);

            duplicateAttribute = getDuplicateAttributeMO(upperMappingClass, nextAttribute);

            if (duplicateAttribute != null && !removeDuplicates) {

                // changed the attribute naming strategy in 4.1
                // duplicate attributes are always created if like-names are found in the merge.
                // both the old attribute and the new attribute will be renamed to avoid name clash.

                // might need to un-clash the duplicateAttribute name - see if the unique names match
                Collection existingColumns = upperMappingClass.getColumns();
                HashMap existingNames = new HashMap(existingColumns.size() + 2);
                for (Iterator nameIter = existingColumns.iterator(); nameIter.hasNext();) {
                    String theName = ((MappingClassColumn)nameIter.next()).getName();
                    existingNames.put(theName, theName);
                }

                if (duplicateAttribute.getName().equals(nextAttribute.getName())) {
                    String dupeName = generateAttributeName(duplicateAttribute.getName(), existingNames, duplicateAttribute);
                    List dupeLocations = this.mapping.getMappingClassColumnOutputLocations(duplicateAttribute);
                    if (!dupeLocations.isEmpty()) {
                        dupeName = generateAttributeName(duplicateAttribute.getName(),
                                                         existingNames,
                                                         (EObject)dupeLocations.get(0));
                    }

                    ModelerCore.getModelEditor().rename(duplicateAttribute, dupeName);
                    existingNames.put(dupeName, dupeName);
                }

                String newName = generateAttributeName(nextAttribute.getName(), existingNames, nextAttribute);
                if (!locations.isEmpty()) {
                    newName = generateAttributeName(nextAttribute.getName(), existingNames, (EObject)locations.get(0));
                }
                ModelerCore.getModelEditor().rename(nextAttribute, newName);
            }
            if (!removeDuplicates) {
                ModelerCore.getModelEditor().move(upperMappingClass, nextAttribute);
                for (Iterator locIter = locations.iterator(); locIter.hasNext();) {
                    mapping.addMappingClassColumnLocation(nextAttribute, (EObject)locIter.next());
                }
            } else if (duplicateAttribute == null) {
                ModelerCore.getModelEditor().move(upperMappingClass, nextAttribute);
                for (Iterator locIter = locations.iterator(); locIter.hasNext();) {
                    mapping.addMappingClassColumnLocation(nextAttribute, (EObject)locIter.next());
                }

            }

        }

        // Remove the associated transformation and diagram for the mapping class being deleted
        ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(lowerMappingClass);
        if (modelContents != null) {
            // Delete the transformations for the mapping class
            List allTransforms = modelContents.getTransformations(lowerMappingClass);
            if (allTransforms != null && !allTransforms.isEmpty()) {
                Iterator tIter = allTransforms.iterator();
                while (tIter.hasNext()) {
                    ModelerCore.getModelEditor().delete((EObject)tIter.next(), true, false);
                }
            }
            // Delete the diagrams for the mapping class
            List allDiagrams = modelContents.getDiagrams(lowerMappingClass);
            if (allDiagrams != null && !allDiagrams.isEmpty()) {
                Iterator dIter = allDiagrams.iterator();
                while (dIter.hasNext()) {
                    ModelerCore.getModelEditor().delete((EObject)dIter.next(), true, false);
                }
            }
            // Clear all DiagramEntity references to the mapping class (defect 21451)
            clearDiagramRefs(lowerMappingClass);
        }

        // delete the mappingClass

        mapping.deleteMappingClass(lowerMappingClass);
        setGeneratingMappingClasses(false);
    }

    /**
     * Ensure that any modelObject references by a DiagramEntity to the specified EObject are either unset or removed
     * 
     * @param eObj
     * @since 4.3
     */
    protected void clearDiagramRefs( final EObject eObj ) {
        if (eObj != null) {
            ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(eObj);
            if (modelContents != null && modelContents.getDiagramContainer(false) != null) {
                DiagramContainer cntr = modelContents.getDiagramContainer(false);

                try {
                    // Use a visitor to clear any non-containment references to this EObject
                    final ClearEObjectReferences visitor = new ClearEObjectReferences(eObj);
                    final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);

                    // Visit all EObject within the DiagramContainer
                    processor.walk(cntr, ModelVisitorProcessor.DEPTH_INFINITE);

                    // Remove any affected Diagram Entity instance that no longer references a model object
                    for (Iterator iter = visitor.getAffectedObjects().iterator(); iter.hasNext();) {
                        final EObject affectedObj = (EObject)iter.next();
                        if (affectedObj instanceof DiagramEntity && ((DiagramEntity)affectedObj).getModelObject() == null) {
                            Diagram diagram = ((DiagramEntity)affectedObj).getDiagram();
                            diagram.getDiagramEntity().remove(affectedObj);
                        }
                    }
                } catch (ModelerCoreException err) {
                    PluginConstants.Util.log(err);
                }
            }
        }
    }

    /**
     * Determine if the specified MappingClass may be split into two. This is an expensive method to call.
     * 
     * @param theMappingClass
     * @return
     */
    public boolean canSplitMappingClass( MappingClass theMappingClass,
                                         MappingClassBuilderStrategy strategy ) {
        boolean result = false;

        List locations = mapping.getMappingClassOutputLocations(theMappingClass);
        if (locations.size() > 1) {
            // any mapping class located at multiple document nodes can be split.
            result = true;
        } else {

            if (!locations.isEmpty()) {
                EObject location = (EObject)locations.get(0);

                if (location != null) {
                    // build the MappingClass Map beneath the specified mapping class
                    Map mappingClassMap = strategy.buildMappingClassMap(location, this.tree, this.mapper);

                    // go through the keys and see if any do not already store a MappingClass
                    Iterator iter = mappingClassMap.keySet().iterator();
                    while (iter.hasNext()) {
                        EObject docNode = (EObject)iter.next();
                        // if there should be a mapping class here, but there is currently none
                        if (shouldContainMappingClass(docNode) && mapping.getMappingClass(docNode) == null) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Recursive method to regenerate Mapping Classes at and beneath the specified node, for use with the "split mapping classes"
     * functionality.
     * 
     * @param node the MetaObject in the XML document to begin looking for flat document fragments.
     * @param autoPopulateAttributes if true, this method will automatically generate virtual mapping attributes within the new
     *        mapping classes and assign their values to the mapping property of the corresponding document model nodes.
     */
    public void splitMappingClass( MappingClass mappingClass,
                                   MappingClassBuilderStrategy strategy,
                                   boolean moveAttributes ) {
        //startTracking("splitMappingClass()"); //$NON-NLS-1$
        // build a map of where the mapping class algorithm thinks the mapping classes should be
        Map mappingClassMap = strategy.buildMappingClassMap(tree.getTreeRoot(), this.tree, this.mapper);

        // see if we have a multiple-location mapping class
        List docNodes = new ArrayList(mapping.getMappingClassOutputLocations(mappingClass));
        if (docNodes.size() > 1) {
            // iterate through the nodes and create new mapping class at each one
            Iterator docIter = docNodes.iterator();
            // skip the first one
            EObject remainingNode = (EObject)docIter.next();
            while (docIter.hasNext()) {
                EObject docNode = (EObject)docIter.next();
                MappingClass newClass = createMappingClass(docNode, false, false);
                Collection nodesToMap = (Collection)mappingClassMap.get(docNode);
                if (nodesToMap != null && !nodesToMap.isEmpty()) {
                    moveOrCreateMappingClassColumns(newClass, nodesToMap, new HashMap(), false, new HashSet());
                }
            }
            // update the reference list on only the original mapping class
            List locationsToRemove = new ArrayList(docNodes);
            locationsToRemove.remove(remainingNode);
            Iterator removeIter = locationsToRemove.iterator();
            while (removeIter.hasNext()) {
                mapping.removeMappingClassLocation(mappingClass, (EObject)removeIter.next());
            }

        } else {
            // not a multi-located mapping class
            List referencedNodes = mapping.getMappingClassOutputLocations(mappingClass);
            EObject documentNode = (EObject)referencedNodes.get(0);

            // launch the SplitterVisitor to re-generate the mapping classes
            MappingClassSplitterVisitor visitor = new MappingClassSplitterVisitor(mappingClass, mapping, mapper, this,
                                                                                  mappingClassMap);
            MappableTreeIterator nodeIter = new MappableTreeIterator(this.tree, documentNode);

            while (nodeIter.hasNext()) {
                visitor.visit((EObject)nodeIter.next());
            }

        }
        //stopTracking("splitMappingClass()"); //$NON-NLS-1$
        // printTracking();
    }

    /**
     * Determine if the specified location can be added as a mapping to the specified MappingClassColumn. Considers whether the
     * location is appropriate given the MappingClass location, and whether or not the location is currently mapped elsewhere.
     * 
     * @param column
     * @param location
     * @return
     */
    public boolean canAddLocation( MappingClassColumn column,
                                   EObject location ) {
        boolean result = false;

        // Precheck to see if location is an xml document node...
        if (ModelMapperFactory.isXmlTreeNode(location)) {
            // first, the location must not currently be mapped here
            result = !mapping.getMappingClassColumnOutputLocations(column).contains(location);

            if (result) {
                // second, the location must be at or below one of the mapping class locations
                MappingClass mc = column.getMappingClass();
                List mcLocations = mapping.getMappingClassOutputLocations(mc);
                if (!mcLocations.contains(location)) {
                    // if not a location, then it must be a decendent of one of the locations
                    for (Iterator locIter = mcLocations.iterator(); locIter.hasNext();) {
                        if (tree.isAncestorOf((EObject)locIter.next(), location)) {
                            result = true;
                            break;
                        }
                    }
                }

            }

            if (result) {
                // third, the location must not already be mapped
                result = mapping.getMappingClassColumn(location) == null;
            }
        }

        return result;
    }

    /**
     * Add the specified location as a mapping to the specified MappingClassColumn. Assumes that the canAddLocation method has
     * been called first and responded with true.
     * 
     * @param column
     * @param location
     */
    public void addLocation( MappingClassColumn column,
                             EObject location ) {

        mapping.addMappingClassColumnLocation(column, location);
    }

    /**
     * Determines if the specified location may be removed from the MappingClassColumn mapping. The method simply checks to
     * determine that this mapping already exists.
     * 
     * @param column
     * @param location
     * @return
     */
    public boolean canRemoveLocation( MappingClassColumn column,
                                      EObject location ) {
        return mapping.getMappingClassColumnOutputLocations(column).contains(location);
    }

    /**
     * Remove the specified location as a mapping from the specified MappingClassColumn. Assumes that the canRemoveLocation method
     * has been called first and responded with true.
     * 
     * @param column
     * @param location
     */
    public void removeLocation( MappingClassColumn column,
                                EObject location ) {
        mapping.removeMappingClassColumnLocation(column, location);
    }

    public MappingClassSet getMappingClassSet() {
        if (mappingClassSet == null) {
            // Defect 18433 - BML 8/31/05 - Changed call to create mapping class set via a new
            // utility method that correctly adds it to the model (via addValue())
            mappingClassSet = ModelResourceContainerFactory.getMappingClassSet(this.treeRoot, true);
        }
        return mappingClassSet;
    }

    /**
     * Used by GenerateMappingClassesAction to enable/disable action. Only cares that the mappingClassSet is null or the set is
     * Empty (i.e. no mapping classes yet).
     * 
     * @return canGenerate
     */
    public boolean canGenerateMappingClasses() {
        boolean canGenerate = true;
        if (getMappingClassSet() == null || !getMappingClassSet().eContents().isEmpty()) {
            canGenerate = false;
        }

        return canGenerate;
    }

    /**
     * Access to the document's tree root reference.
     * 
     * @return
     */
    public EObject getTreeRoot() {
        return treeRoot;
    }

    // ===========================================================================================================================
    // PACKAGE METHODS
    // ===========================================================================================================================

    /**
     * Generate an appropriate name for a MappingClass to be located at the specified node. The algorithm generates a unique name
     * appropriate for the location.
     * 
     * @return the generated name.
     */
    String convertLocationNameToMappingClassName( EObject node ) {
        //startTracking("convertLocationNameToMappingClassName()"); //$NON-NLS-1$
        String newName = getName(node);

        String possibleName = null;
        if (mapper.isContainerNode(node)) {
            // if this compositor has only one child node, then name the mapping class after the child
            Collection children = tree.getChildren(node);
            if (children.size() == 1) {
                EObject child = (EObject)children.iterator().next();
                newName = getName(child);
            } else {
                EObject parent = tree.getParent(node);
                if (parent != null && tree.getTreeRoot() != parent) {
                    newName = getName(parent);
                    // generate a compositor-based name in case we need to disambiguate newName
                    possibleName = getName(parent) + '_' + getName(node);
                }
            }
        }

        // see if we should try to use the compositor-based name
        // if ( names.contains(newName) && possibleName != null ) {
        if (mapping.containsMappingClassWithName(newName) && possibleName != null) {
            newName = possibleName;
        }

        // check to see if the name is allowed
        String result = newName;
        int suffix = 0;
        boolean tryAgain = true;
        while (tryAgain) {
            if (mapping.containsMappingClassWithName(result)) {
                result = newName + (++suffix);
            } else {
                tryAgain = false;
            }
        }
        //stopTracking("convertLocationNameToMappingClassName()"); //$NON-NLS-1$
        return result;
    }

    String convertLocationNameToStagingTableName( EObject node ) {
        String newName = "ST_" + getName(node); //$NON-NLS-1$
        // collect up the existing names so we can check for a conflict
        Collection existingStagingTables = mapping.getAllStagingTables();
        Collection names = new ArrayList(existingStagingTables.size());
        Iterator iter = existingStagingTables.iterator();
        while (iter.hasNext()) {
            names.add(((StagingTable)iter.next()).getName());
        }

        // check to see if the name is allowed
        String result = newName;
        int suffix = 0;
        boolean tryAgain = true;
        while (tryAgain) {
            if (names.contains(result)) {
                result = newName + (++suffix);
            } else {
                tryAgain = false;
            }
        }
        return result;
    }

    void moveOrCreateMappingClassColumns( MappingClass mappingClass,
                                          Collection documentNodeList,
                                          Map nameMappingClassColumnMap,
                                          boolean initialBuild,
                                          Set datatypeAccumulator ) {
        //startTracking("moveOrCreateMappingClassColumns()"); //$NON-NLS-1$
        // iterate through the document nodes

        HashMap mcColumnNameMap = new HashMap(documentNodeList.size());

        Iterator docNodeIter = (documentNodeList).iterator();
        while (docNodeIter.hasNext()) {
            EObject nodeToMap = (EObject)docNodeIter.next();
            boolean createMappingClassColumn = true;

            // see if there is already a Mapping Attribute for this document node
            MappingClassColumn mcColumn = null;

            if (!initialBuild) {
                mcColumn = mapping.getMappingClassColumn(nodeToMap);
            }

            if (mcColumn != null) {
                // there is an attribute mapped to this node. see how many nodes use this attribute.
                List locations = new ArrayList(mapping.getMappingClassColumnOutputLocations(mcColumn));
                if (locations.size() > 1) {
                    // this attribute is used by more than this one node.
                    // delete this mapping and create the attribute in the new mapping class
                    mapping.removeMappingClassColumnLocation(mcColumn, nodeToMap);
                } else {
                    // move the node and don't create a new one.
                    // TODO: detect name clash before the move?
                    String mcColumnName = mcColumn.getName();
                    if (!mappingClassContainsAttribute(mappingClass, mcColumn)) {
                        try {
                            // Remove Location
                            mapping.removeMappingClassColumnLocation(mcColumn, nodeToMap);
                            ModelerCore.getModelEditor().move(mappingClass, mcColumn);
                            // add location
                            mapping.addMappingClassColumnLocation(mcColumn, nodeToMap);
                            createMappingClassColumn = false;
                            nameMappingClassColumnMap.put(mcColumnName, mcColumn);
                            mcColumnNameMap.put(mcColumnName, mcColumnName);
                        } catch (Exception e) {
                            PluginConstants.Util.log(e);
                        }
                    } else {
                        if (nameMappingClassColumnMap.get(mcColumnName) != null) {
                            nameMappingClassColumnMap.put(mcColumnName, mcColumn);
                            mcColumnNameMap.put(mcColumnName, mcColumnName);
                        }
                        createMappingClassColumn = false;
                    }
                }
            }

            if (createMappingClassColumn) {

                // create the MappingAttribute
                MappingClassColumn existingMappingClassColumn = null;

                try {
                    boolean createNewMappingClassColumn = true;
                    String proposedNewMappingClassColumnName = getName(nodeToMap);
                    // detect attribute name clashes
                    for (final Iterator iter = nameMappingClassColumnMap.entrySet().iterator(); iter.hasNext();) {
                        final Entry entry = (Entry)iter.next();
                        if (proposedNewMappingClassColumnName.equalsIgnoreCase((String)entry.getKey())) {
                            // an attribute with this name already exists.
                            // see if the types match also.
                            existingMappingClassColumn = (MappingClassColumn)nameMappingClassColumnMap.get(proposedNewMappingClassColumnName);
                            // NOTE: the following if() check will always be FALSE
                            if (areDataTypesEquivalent(nodeToMap, existingMappingClassColumn)) {
                                // same data types, so we'll reuse this MappingAttribute
                                createNewMappingClassColumn = false;
                            } else {
                                // different data types, so we'll need an attribute name that doesn't clash
                                proposedNewMappingClassColumnName = generateAttributeName(proposedNewMappingClassColumnName,
                                                                                          mcColumnNameMap,
                                                                                          nodeToMap);
                            }
                            break;
                        }
                    }

                    MappingClassColumn newMappingClassColumn = null;
                    if (createNewMappingClassColumn) {
                        newMappingClassColumn = metamodelFactory.createMappingClassColumn();
                        newMappingClassColumn.setName(proposedNewMappingClassColumnName);
                        newMappingClassColumn.setMappingClass(mappingClass);
                        EObject datatype = tree.getDatatype(nodeToMap);
                        if (datatype != null) {
                            newMappingClassColumn.setType(datatype);
                            datatypeAccumulator.add(datatype);
                        }
                        nameMappingClassColumnMap.put(proposedNewMappingClassColumnName, newMappingClassColumn);
                        mcColumnNameMap.put(proposedNewMappingClassColumnName, proposedNewMappingClassColumnName);
                    }

                    // set the document node's mapping property to this attribute
                    mapping.addMappingClassColumnLocation(newMappingClassColumn, nodeToMap);

                } catch (Exception e) {
                    PluginConstants.Util.log(e);
                }

            }
        }
        //stopTracking("moveOrCreateMappingClassColumns()"); //$NON-NLS-1$
    }

    // ===========================================================================================================================
    // PRIVATE METHODS
    // ===========================================================================================================================

    private boolean mappingClassContainsAttribute( MappingClass mappingClass,
                                                   MappingClassColumn mappingClassColumn ) {
        boolean result = false;

        if (mappingClass.getColumns() != null && !mappingClass.getColumns().isEmpty()) {
            Iterator iter = mappingClass.getColumns().iterator();
            MappingClassColumn nextColumn = null;
            while (iter.hasNext() && !result) {
                nextColumn = (MappingClassColumn)iter.next();
                if (nextColumn == mappingClassColumn) result = true;
            }
        }

        return result;
    }

    String getName( EObject node ) {
        //startTracking("getName()"); //$NON-NLS-1$
        if (emfAdapter == null) {
            emfAdapter = ModelerCore.getMetamodelRegistry().getAdapterFactory();
        }
        // Cache the Label Providers here!!!!!
        IItemLabelProvider provider = (IItemLabelProvider)emfAdapter.adapt(node, IItemLabelProvider.class);
        //stopTracking("getName()"); //$NON-NLS-1$
        return provider.getText(node);
    }

    private boolean shouldContainMappingClass( EObject node ) {
        boolean result = false;
        if (mapper.allowsMappingClass(node)) {
            if (mapper.canIterate(node)) {
                result = true;
            } else if (mapper.isRecursive(node)) {
                result = true;
            }
        }

        return result;
    }

    private boolean areDataTypesEquivalent( EObject documentNode,
                                            MappingClassColumn mappingAttribute ) {
        // NOTE: BML as of 5/10/07, both DefaultMappableTree & XmlMappableTree both have areEquivalent() Methods that are
        // hard-coded to return FALSE. SO, we are going to hard-code this here for performance purposes
        return false;

        // get the datatype for the tree node that the mappingAttribute is bound to
        // Collection locations = mapping.getMappingClassColumnOutputLocations(mappingAttribute);
        // if ( locations.isEmpty() ) {
        // return false;
        // }
        // EObject mappedNode = (EObject) locations.iterator().next();
        // return mapper.getMappableTree().areEquivalent(mappedNode, documentNode);
    }

    private String generateAttributeName( String duplicateName,
                                          HashMap existingNames,
                                          EObject node ) {
        int count = 0;
        String baseName = mapper.getMappableTree().getUniqueName(node);
        String result = baseName;
        while (existingNames.get(result) != null) {
            result = baseName + (++count);
        }
        return result;
    }

    /**
     * Used by mergeMappingClasses, checks the proposed column to see if there is already a MappingClassColumn in the
     * topMappingClass by this name.
     * 
     * @param topMappingClass
     * @param proposedColumn
     * @return
     */
    private MappingClassColumn getDuplicateAttributeMO( MappingClass topMappingClass,
                                                        MappingClassColumn proposedColumn ) {

        Collection topAttributes = topMappingClass.getColumns();
        if (topAttributes == null || topAttributes.isEmpty()) {
            return null;
        }

        MappingClassColumn nextColumn = null;
        Iterator iter = topAttributes.iterator();
        while (iter.hasNext()) {
            nextColumn = (MappingClassColumn)iter.next();
            if (nextColumn.getName().equalsIgnoreCase(proposedColumn.getName())) {
                return nextColumn;
            }
        }

        return null;
    }

    /**
     * Recursive method used by getMappingClassExtentNodes to walk down a branch of the tree and find all nodes in the extent.
     * 
     * @param visibleNode the branch node that this method will look beneath
     * @param columnLocations a Collection of tree nodes that should automatically be added in the result
     * @param mappingClassLocations a Collection of mapping class locations. Any node inside this collection should not be added
     *        to the result.
     * @return
     */
    private List gatherExtentNodes( EObject locationNode,
                                    Collection columnLocations,
                                    Collection mappingClassLocations ) {
        ArrayList result = new ArrayList();
        for (Iterator childIter = mapper.getMappableTree().getChildren(locationNode).iterator(); childIter.hasNext();) {
            EObject node = (EObject)childIter.next();
            // check to see if this node is mapped into the MappingClass by checking columnLocations
            if (columnLocations.contains(node)) {
                // if so, then this node is in the extent
                result.add(node);
                // recurse down this node's children
                result.addAll(gatherExtentNodes(node, columnLocations, mappingClassLocations));
            } else {
                // see if there is a mapping class located at this node
                if (mappingClassLocations.contains(node)) {
                    // stop; this node is in another extent. do not check this node's children.
                } else {
                    // this node is in the extent
                    result.add(node);
                    // recurse down this node's children
                    result.addAll(gatherExtentNodes(node, columnLocations, mappingClassLocations));
                }
            }

        }
        return result;
    }

    /**
     * Method used primarily for JUnit testing. Allows generic named finder of XML Document element/attribtutes nodes NOTE: this
     * method finds the first named occurance of a element/attribute
     * 
     * @param treeNodeName
     * @return
     * @since 5.0
     */
    public EObject findXmlDocumentTreeNode( String treeNodeName ) {
        XmlDocumentNodeFinderVisitor visitor = new XmlDocumentNodeFinderVisitor(treeNodeName);

        MappableTreeIterator nodeIter = new MappableTreeIterator(this.tree);

        if (nodeIter.hasNext()) {
            // skip over the root itself
            nodeIter.next();
        }

        while (nodeIter.hasNext() && visitor.keepSearching()) {
            visitor.visit((EObject)nodeIter.next());
        }
        return visitor.getTreeNode();
    }

    /**
     * Simple visitor designed to look for an EObject who's name matches the target "treeNodeName" in the constructor
     * 
     * @since 5.0
     */
    class XmlDocumentNodeFinderVisitor {

        EObject treeNode;
        String treeNodeName;

        public XmlDocumentNodeFinderVisitor( String name ) {
            super();
            treeNodeName = name;
        }

        public void visit( EObject eObject ) {
            if (treeNode == null) {
                String eObjectName = getName(eObject);
                if (treeNodeName.equalsIgnoreCase(eObjectName)) {
                    treeNode = eObject;
                }

            }
        }

        public boolean keepSearching() {
            return treeNode == null;
        }

        public EObject getTreeNode() {
            return treeNode;
        }
    }
}
