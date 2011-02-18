/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.ui.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.MappingClassSetContainer;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.compare.CompareFactory;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.compare.impl.CompareFactoryImpl;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * MappingTreeContentProvider
 */
public class MappingTreeContentProvider implements ITreeContentProvider {

    private List /* of DifferenceReport*/diffReports;
    private HashMap hmTargetToRelatedNodes;
    private ArrayList arylContainerNodes;
    private boolean bInsertRelatedNodes = true;

    /** Factory needed to create {@link PropertyDifference}s. */
    private CompareFactory compareFactory = new CompareFactoryImpl();

    /**
     * A <code>Map</code> keyed by target <code>EObject</code> model relative path. Values are the {@link PropertyDifference}
     * associated with the object's additional property (like description).
     */
    private Map propDiffMap = null;

    public MappingTreeContentProvider() {
        super();
    }

    /**
     * Obtains (creating if necessary) the EObject property difference map used to store additional property differences (like
     * descriptions). Keyed by EObject model relative path. Value is {@link PropertyDifference}.
     * 
     * @return the map
     */
    private Map getPropertyDifferenceMap() {
        if (this.propDiffMap == null) {
            this.propDiffMap = new PropertyDifferenceMap();
        }

        return this.propDiffMap;
    }

    /**
     * Indicates if the {@link EObject} of the given <code>Mapping</code> has additional property differences.
     * 
     * @param theMapping the mapping being checked
     * @return <code>true</code>if has additional property differences; <code>false</code> otherwise.
     */
    public boolean hasAdditionalPropertyDifferences( Mapping theMapping ) {
        EObject eobj = MappingTreeContentProvider.getEObjectForMapping(theMapping);
        return ((eobj != null) && (this.propDiffMap != null) && this.propDiffMap.containsKey(eobj));
    }

    public boolean hasChildAdditionalPropertyDifferences( Mapping theMapping ) {
        if (this.propDiffMap == null) {
            return false;
        }

        EObject eobj = MappingTreeContentProvider.getEObjectForMapping(theMapping);
        if (eobj == null) {
            return false;
        }

        return hasChildAdditionalPropertyDifferences(eobj);
    }

    public boolean hasChildAdditionalPropertyDifferences( final EObject eobj ) {
        final Iterator children = eobj.eContents().iterator();
        boolean result = false;
        while (children.hasNext() && !result) {
            final EObject nextChild = (EObject)children.next();
            if (this.propDiffMap.containsKey(nextChild)) {
                result = true;
            } else {
                result = hasChildAdditionalPropertyDifferences(nextChild);
            }
        }

        return result;

    }

    public void setDifferenceReports( List diffReports ) {
        hmTargetToRelatedNodes = null;
        arylContainerNodes = null;
        this.propDiffMap = null;

        this.diffReports = diffReports;

        if (this.diffReports != null) {
            Iterator it = this.diffReports.iterator();

            // this implies that a series of reports can be parsed into
            // a common set of collections. verify that.
            while (it.hasNext()) {
                DifferenceReport drTemp = (DifferenceReport)it.next();
                parseReport(drTemp);
            }
        }
    }

    private void parseReport( DifferenceReport drReport ) {
        /*
         * Analyze the report and parse out certain content into Maps for later use.
         */

        if (drReport == null) {
            return;
        }

        // this is the root of all mappings
        Mapping mapping = drReport.getMapping();

        if (mapping != null) {

            MappingHelper helper = mapping.getHelper();

            if (helper instanceof DifferenceDescriptor) {
                parseChildren(drReport);
            }
        }

    }

    private HashMap getTargetToRelatedMap() {
        if (hmTargetToRelatedNodes == null) {
            hmTargetToRelatedNodes = new HashMap();
        }
        return hmTargetToRelatedNodes;
    }

    private ArrayList getcontainerNodesArray() {
        if (arylContainerNodes == null) {
            arylContainerNodes = new ArrayList();
        }
        return arylContainerNodes;
    }

    public static EObject getEObjectForMapping( Mapping mapping ) {
        EObject eoResult = null;

        MappingHelper helper = mapping.getHelper();
        if (helper instanceof DifferenceDescriptor) {
            final DifferenceDescriptor desc = (DifferenceDescriptor)helper;
            final DifferenceType type = desc.getType();
            if (type.getValue() == DifferenceType.DELETION) {
                final List inputs = mapping.getInputs();
                final EObject input = inputs.isEmpty() ? null : (EObject)inputs.get(0);

                if (input != null) {
                    eoResult = input;
                }
            } else {
                final List outputs = mapping.getOutputs();
                final EObject output = outputs.isEmpty() ? null : (EObject)outputs.get(0);
                final List inputs = mapping.getInputs();
                final EObject input = inputs.isEmpty() ? null : (EObject)inputs.get(0);

                if (output != null) {
                    eoResult = output;
                } else if (input != null) {
                    eoResult = input;
                }
            }
        }

        return eoResult;
    }

    private void parseChildren( Object mapping ) {
        /*  For each container...
         *  1. get children.  These are always of type Mapping.
         *  2. for each child,
         *      - get the EObject for that mapping
         *      - get the target of that EObject
         *      - using the target as a key, add the child (the Mapping!) to the Map (see addEntry)
         * 
         */
        EObject eo = null;

        // the 'mapping' is either the root (DiffReport) or a mapping under it:
        if (mapping instanceof Mapping) {
            eo = getEObjectForMapping((Mapping)mapping);
        }

        if (eo != null && eo instanceof DiagramContainer) {

            // save the container node so we can ignore it later
            getcontainerNodesArray().add(mapping);

            final Object[] children = getTheChildren(mapping);

            for (int i = 0, j = children.length; i < j; i++) {

                // special processing for this type:
                EObject eoChild = getEObjectForMapping((Mapping)children[i]);
                if (eoChild instanceof Diagram) {

                    EObject eoTarget = ((Diagram)eoChild).getTarget();
                    addEntry(eoTarget, (Mapping)children[i]);
                } else if (eoChild instanceof DiagramEntity) {

                    EObject eoTarget = ((DiagramEntity)eoChild).getDiagram().getTarget();
                    addEntry(eoTarget, (Mapping)children[i]);
                }
            }
        } else if (eo != null && eo instanceof TransformationContainer) {

            // save the container node so we can ignore it later
            getcontainerNodesArray().add(mapping);

            final Object[] children = getTheChildren(mapping);

            for (int i = 0, j = children.length; i < j; i++) {
                EObject eoChild = getEObjectForMapping((Mapping)children[i]);
                if (eoChild instanceof TransformationMappingRoot) {
                    // special processing for this type:
                    EObject eoTarget = ((TransformationMappingRoot)eoChild).getTarget();
                    addEntry(eoTarget, (Mapping)children[i]);
                }
            }
        } else if (eo != null && eo instanceof MappingClassSetContainer) {

            // save the container node so we can ignore it later
            getcontainerNodesArray().add(mapping);
            final Object[] children = getTheChildren(mapping);

            for (int i = 0, j = children.length; i < j; i++) {
                EObject eoChild = getEObjectForMapping((Mapping)children[i]);
                if (eoChild instanceof MappingClassSet) {
                    // special processing for this type:
                    EObject eoTarget = ((MappingClassSet)eoChild).getTarget();
                    addEntry(eoTarget, (Mapping)children[i]);
                }
            }
        }

        else if (eo != null && eo instanceof AnnotationContainer) {
            /* DESIGN NOTE:
             * The Annotations contained in the AnnotationContainer will be made to look as a feature of the annotated EObject.
             * The AnnotationContainer itself will not be shown as content.
             */

            // save the container node so we can ignore it later
            getcontainerNodesArray().add(mapping);

            // add annotation descriptions
            List annotations = ((AnnotationContainer)eo).getAnnotations();

            if ((annotations != null) && !annotations.isEmpty()) {
                List mappings = ((Mapping)mapping).getNested();

                if (mappings.isEmpty()) {
                    // Since there are no nested mappings this is the first time AnnotationContainer existed.
                    // So one or more descriptions were added.
                    List list = null;
                    AnnotationContainer ac = null;
                    MappingHelper helper = ((Mapping)mapping).getHelper();

                    if (helper instanceof DifferenceDescriptor) {
                        boolean isAdd = true;

                        if (((DifferenceDescriptor)helper).isAddition()) {
                            list = ((Mapping)mapping).getOutputs();
                        } else if (((DifferenceDescriptor)helper).isDeletion()) {
                            list = ((Mapping)mapping).getInputs();
                            isAdd = false;
                        }

                        if ((list != null) && !list.isEmpty()) {
                            ac = (AnnotationContainer)list.get(0);
                            List newAnnotations = ac.getAnnotations();

                            // create a PropertyDifference for each Annotation. These will show up as a property of the
                            // annotated object.
                            for (int size = newAnnotations.size(), i = 0; i < size; ++i) {
                                Annotation a = (Annotation)newAnnotations.get(i);
                                String desc = a.getDescription();
                                EMap tags = a.getTags();

                                if (isAdd) {
                                    if (!CoreStringUtil.isEmpty(desc)) {
                                        createNewDescriptionPropertyDifference(a, true);
                                    }
                                    if (tags != null && !tags.isEmpty()) {
                                        createNewAnnotationTagPropertyDifference(a, true);
                                    }
                                } else {
                                    if (!CoreStringUtil.isEmpty(desc)) {
                                        createNewDescriptionPropertyDifference(a, false);
                                    }
                                    if (tags != null && !tags.isEmpty()) {
                                        createNewAnnotationTagPropertyDifference(a, false);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // description was changed
                    for (int size = mappings.size(), i = 0; i < size; ++i) {
                        Mapping nestedMapping = (Mapping)mappings.get(i);
                        MappingHelper nestedHelper = nestedMapping.getHelper();

                        if ((nestedHelper instanceof DifferenceDescriptor) && !((DifferenceDescriptor)nestedHelper).isNoChange()) {
                            List inputs = nestedMapping.getInputs();
                            Annotation oldValue = inputs.isEmpty() ? null : (Annotation)inputs.get(0);

                            List outputs = nestedMapping.getOutputs();
                            Annotation newValue = outputs.isEmpty() ? null : (Annotation)outputs.get(0);

                            // create property difference for description feature and attach to annotated object's mapping if
                            // available
                            createChangedDescriptionPropertyDifference(oldValue, newValue);
                        }
                    }
                }
            }
        } else {
            // this branch always/only handles the mapping arg, never a translated EObject
            // recurse over all children
            final Object[] children = getTheChildren(mapping);
            for (int i = 0, j = children.length; i < j; i++) {
                if (children[i] instanceof Mapping) {
                    parseChildren(children[i]);
                }
            }
        }
    }

    /**
     * Creates a {@link PropertyDifference} for the specified changed description.
     * 
     * @param theOldAnnotation the annotation containing the old description
     * @param theNewAnnotation the annotation containing the new description
     * @since 4.2
     */
    private void createChangedDescriptionPropertyDifference( Annotation theOldAnnotation,
                                                             Annotation theNewAnnotation ) {

        EClass eclass = (theNewAnnotation == null) ? theOldAnnotation.eClass() : theNewAnnotation.eClass();
        PropertyDifference propDiff = this.compareFactory.createPropertyDifference();
        propDiff.setAffectedFeature(eclass.getEAttributes().get(CorePackage.ANNOTATION__DESCRIPTION));
        propDiff.setOldValue((theOldAnnotation == null) ? null : theOldAnnotation.getDescription());
        propDiff.setNewValue((theNewAnnotation == null) ? null : theNewAnnotation.getDescription());

        EObject target = (theNewAnnotation == null) ? theOldAnnotation.getAnnotatedObject() : theNewAnnotation.getAnnotatedObject();
        registerDescriptionPropertyDifference(target, propDiff);
    }

    /**
     * Associates the specified <code>PropertyDifference</code> with specified <code>Mapping</code>.
     * 
     * @param thePropDifference the property difference being associated to the mapping
     * @param theMapping the mapping that the property difference is being associated with
     * @return <code>true</code>if successfully associated; <code>false</code> otherwise.
     * @since 4.2
     */
    private boolean associate( PropertyDifference thePropDifference,
                               Mapping theMapping ) {
        boolean result = false;
        Object helper = theMapping.getHelper();

        if (helper instanceof DifferenceDescriptor) {
            thePropDifference.setDescriptor((DifferenceDescriptor)helper);

            // defect 17172 - make sure we do not change the original mapping helper type, so that dependent behavior is not
            // affected
            if (((DifferenceDescriptor)helper).isNoChange()) {
                ((DifferenceDescriptor)helper).setType(DifferenceType.get(DifferenceType.CHANGE));
            }

            result = true;
        }

        return result;
    }

    /**
     * Registers the specified <code>PropertyDifference</code> of the specified object.
     * 
     * @param theObject the target of the property difference
     * @param thePropDifference the property difference
     * @since 4.2
     * @see #hasAdditionalPropertyDifferences(Mapping)
     */
    private void registerDescriptionPropertyDifference( EObject theObject,
                                                        PropertyDifference thePropDifference ) {
        boolean cacheDiff = true;
        Mapping mapping = getPropertyDifferenceMapping(theObject);

        if (mapping != null) {
            cacheDiff = !associate(thePropDifference, mapping);
        }

        if (cacheDiff) {
            // save diff since it can't be associated with a mapping right now.
            // it will be associated either later until mapping of target is processed
            getPropertyDifferenceMap().put(theObject, thePropDifference);
        }
    }

    /**
     * Creates a {@link PropertyDifference} for the specified <code>Annotation</code>. This <code>PropertyDifference</code> will
     * be associated with the annotated object of the <code>Annotation</code>.
     * 
     * @param theAnnotation the Annotation whose description has changed
     * @param theAddedFlag the flag indicating if the description was added or deleted
     * @since 4.2
     */
    private void createNewDescriptionPropertyDifference( Annotation theAnnotation,
                                                         boolean theAddedFlag ) {
        EClass eclass = theAnnotation.eClass();
        PropertyDifference propDiff = this.compareFactory.createPropertyDifference();
        propDiff.setAffectedFeature(eclass.getEStructuralFeature(CorePackage.ANNOTATION__DESCRIPTION));

        if (theAddedFlag) {
            propDiff.setNewValue(theAnnotation.getDescription());
        } else {
            propDiff.setOldValue(theAnnotation.getDescription());
        }

        EObject target = theAnnotation.getAnnotatedObject();
        registerDescriptionPropertyDifference(target, propDiff);
    }

    /**
     * Creates a {@link PropertyDifference} for the specified <code>Annotation</code>. This <code>PropertyDifference</code> will
     * be associated with the annotated object of the <code>Annotation</code>.
     * 
     * @param theAnnotation the Annotation whose description has changed
     * @param theAddedFlag the flag indicating if the description was added or deleted
     * @since 4.2
     */
    private void createNewAnnotationTagPropertyDifference( Annotation theAnnotation,
                                                           boolean theAddedFlag ) {
        EClass eclass = theAnnotation.eClass();
        PropertyDifference propDiff = this.compareFactory.createPropertyDifference();
        propDiff.setAffectedFeature(eclass.getEStructuralFeature(CorePackage.ANNOTATION__TAGS));

        if (theAddedFlag) {
            propDiff.setOldValue(new BasicEMap());
            propDiff.setNewValue(theAnnotation.getTags());
        } else {
            propDiff.setOldValue(theAnnotation.getTags());
            propDiff.setNewValue(new BasicEMap());
        }

        EObject target = theAnnotation.getAnnotatedObject();
        registerDescriptionPropertyDifference(target, propDiff);
    }

    /*
     * getTheChildren( node ) is a private version for use by the parsing preprocess.
     * It is necessary because the public getChildren( o ) has requirements (like the supression
     * of Containers) that make it unsuitable for use in the parsing step.
     */
    private Object[] getTheChildren( Object node ) {
        Object[] result;
        if (node instanceof DifferenceReport) {
            Mapping mapping = ((DifferenceReport)node).getMapping();
            result = getTheChildren(mapping);
        } else if (node instanceof List) {
            result = ((List)node).toArray();
        } else if (node instanceof Mapping) {
            EList nestedMappings = ((Mapping)node).getNested();
            result = nestedMappings.toArray();
        } else {
            EObject parent = (EObject)node;
            EList children = parent.eContents();
            result = children.toArray();
        }
        return result;
    }

    private void addEntry( Object oKey,
                           Mapping mpgValue ) {

        if (getTargetToRelatedMap().containsKey(oKey)) {
            // add to existing list
            List lstNodes = (List)getTargetToRelatedMap().get(oKey);
            lstNodes.add(mpgValue);
        } else {
            // create new list and add to it
            ArrayList aryl = new ArrayList();
            aryl.add(mpgValue);
            getTargetToRelatedMap().put(oKey, aryl);
        }
    }

    /**
     * Obtains the <code>Mapping</code> that a {@link PropertyDifference} can be associated with for the specified
     * <code>EObject</code>.
     * 
     * @param theObject the object whose mapping is being requested
     * @return the mapping or <code>null</code> if one not found
     * @since 4.2
     */
    private Mapping getPropertyDifferenceMapping( EObject theObject ) {
        Mapping result = null;
        List list = (List)getTargetToRelatedMap().get(theObject);

        if (list != null) {
            IPath path = ModelerCore.getModelEditor().getModelRelativePath(theObject);

            // loop through mappings here. use first mapping whose EObject is the same.
            for (int size = list.size(), i = 0; i < size; ++i) {
                Mapping m = (Mapping)list.get(i);
                EObject eobj = getEObjectForMapping(m);

                // need to use path since EObject.equals fails
                if ((eobj != null) && path.equals(ModelerCore.getModelEditor().getModelRelativePath(eobj))) {
                    result = m;
                    break;
                }
            }
        }

        return result;
    }

    public Object[] getChildren( Object node ) {
        Object[] result;
        if (node instanceof DifferenceReport) {

            /*
             * special case: the EObject for the mapping we get from DifferenceReport.getMapping()
             *     is actually the Model Annotation.  The getChildren call we do on that mapping
             *     (see next line) will look up the Model Annotation in the Target Hashmap and 
             *     get the Diagram stuff that we normally insert into the tree under the Annotation.
             *     The result is that the Diagram stuff appears twice in the tree, once at the very top
             *     at later, correctly, under the Model Annotation.  We must suppress the one at the
             *     top.  To do this we will implement a flag that will control whether or not
             *     we do the inserts in getChildren.  When we are processing the DifferenceReport
             *     node that flag will be turned off.
             */
            Mapping mapping = ((DifferenceReport)node).getMapping();

            // get the children of the root. prevent inserting related nodes this time.
            bInsertRelatedNodes = false;
            result = getChildren(mapping);
            bInsertRelatedNodes = true;

            // remove the Container nodes from this list before returning
            result = filterContainers(result);

        } else if (node instanceof List) {
            result = ((List)node).toArray();
        } else if (node instanceof Mapping) {

            // create an array
            ArrayList aryl = new ArrayList();

            // if this is a container, do no further processing
            if (getcontainerNodesArray().contains(node)) {
                return aryl.toArray();
            }

            if (bInsertRelatedNodes) {
                // 1. get the EObject for this mapping node
                EObject eo = getEObjectForMapping((Mapping)node);

                // 2. Try to get a list of mappings from the 'target' hashmap for this EObject
                List lst = (List)getTargetToRelatedMap().get(eo);

                // check to see if a PropertyDifference has been saved for the target EObject of this Mapping.
                // if one is available associate it with that Mapping
                if (hasAdditionalPropertyDifferences((Mapping)node)) {
                    PropertyDifference propDiff = (PropertyDifference)this.getPropertyDifferenceMap().get(eo);
                    associate(propDiff, (Mapping)node);
                }

                // 3. If such a list exists, add it to the front of the result
                if (lst != null) {
                    aryl.addAll(lst);
                }
            }

            // Now add the 'real' children
            EList nestedMappings = ((Mapping)node).getNested();
            aryl.addAll(nestedMappings);
            result = aryl.toArray();

        } else {
            EObject parent = (EObject)node;
            EList children = parent.eContents();
            result = children.toArray();
        }
        return result;
    }

    private Object[] filterContainers( Object[] children ) {

        ArrayList aryl = new ArrayList();
        for (int i = 0, j = children.length; i < j; i++) {
            if (!getcontainerNodesArray().contains(children[i])) {
                aryl.add(children[i]);
            }
        }

        return aryl.toArray();
    }

    public boolean hasChildren( Object node ) {
        boolean hasChildren = false;
        if (node instanceof DifferenceReport) {
            Mapping mapping = ((DifferenceReport)node).getMapping();
            hasChildren = hasChildren(mapping);
        } else if (node instanceof List) {
            hasChildren = ((List)node).size() > 0;
        } else if (node instanceof Mapping) {
            Mapping parent = (Mapping)node;
            EList children = parent.getNested();
            hasChildren = children.size() > 0;
        }
        return hasChildren;
    }

    public Object[] getElements( Object node ) {

        Object[] array = null;
        if (node instanceof DifferenceReport) {
            Mapping mapping = ((DifferenceReport)node).getMapping();

            ArrayList aryl = new ArrayList(1);
            aryl.add(mapping);
            array = aryl.toArray();

        } else if (node instanceof List) {

            array = ((List)node).toArray();

        } else if (node instanceof Mapping) {
            Mapping parent = (Mapping)node;
            java.util.List elements = parent.getNested();
            if (elements == null) {
                elements = new ArrayList(0);
            }
            array = elements.toArray();
        }
        return array;
    }

    public Object getParent( Object node ) {
        Object result = null;
        if (node instanceof Mapping) {
            Mapping child = (Mapping)node;
            result = child.eContainer();
        }
        return result;
    }

    public void dispose() {
    }

    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }

    /**
     * The <code>PropertyDifferenceMap</code> class is a helper <code>Map</code> which internally generates the <code>Map</code>
     * key based on a given {@link EObject}. Care should be used when using methods other than the overridden ones (i.e., did not
     * ensure all appropriate methods have been overridden to handle the internal key generation).
     * 
     * @since 4.2
     */
    class PropertyDifferenceMap extends HashMap {
        /**
         */
        private static final long serialVersionUID = 1L;

        /**
         * @see java.util.HashMap#containsKey(java.lang.Object)
         * @since 4.2
         */
        @Override
        public boolean containsKey( Object theKey ) {
            Object key = getKey(theKey);
            return (key == null) ? false : super.containsKey(key);
        }

        /**
         * @see java.util.HashMap#get(java.lang.Object)
         * @since 4.2
         */
        @Override
        public Object get( Object theKey ) {
            Object key = getKey(theKey);
            return (key == null) ? null : super.get(key);
        }

        /**
         * Obtains the key for the specified object.
         * 
         * @param theObject the object whose key is being requested
         * @return the key or <code>null</code> if object is not an {@link EObject}
         * @since 4.2
         */
        private Object getKey( Object theObject ) {
            return (theObject instanceof EObject) ? ModelerCore.getModelEditor().getModelRelativePath((EObject)theObject) : null;
        }

        /**
         * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
         * @since 4.2
         */
        @Override
        public Object put( Object theKey,
                           Object theValue ) {
            Object key = getKey(theKey);
            return (key == null) ? null : super.put(key, theValue);
        }
    }

}
