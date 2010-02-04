/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.internal.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConstraint;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDForm;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.ValueType;
import com.metamatrix.metamodels.xml.XmlAll;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlBaseElement;
import com.metamatrix.metamodels.xml.XmlBuildable;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlContainerHolder;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocumentBuilder;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlElementHolder;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlFragmentUse;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.XmlSequence;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;

/**
 * XmlDocumentBuilderImpl
 */
public class XmlDocumentBuilderImpl implements XmlDocumentBuilder {
    //private static final int MONITOR_MOD_COUNT = 100;
    private static final String TASK_NAME = XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.buildingXmlDocForRoot"); //$NON-NLS-1$
    private static final String CALCULATING_MSG = XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Calculating_effort_to_build__1"); //$NON-NLS-1$
    private static final String CALCULATING_MSG2 = XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Calculating_effort_to_update__2"); //$NON-NLS-1$
    private static final String BUILDING_MSG = XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Building_Document_node__3"); //$NON-NLS-1$
    private static final String UPDATING_MSG = XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Updating_Document_node__4"); //$NON-NLS-1$
    //private static final int MAX_COUNT = 50000;

    private final Collection schemaUris = new ArrayList();
    private final Collection referencedSchemas = new ArrayList();
    private final HashMap updateDeleteMap = new HashMap();
    private final HashMap updateCurrentChildMap = new HashMap();
    private final HashMap fragmentMap = new HashMap();
    private final HashMap uriToNamespaceMap = new HashMap();
    private final StringNameValidator validator = new StringNameValidator();
    private final HashSet unhandledModelImports = new HashSet();
    private Collection recursiveElementUUIDs = new HashSet();



    private int nodeCount;
    private int newCount;
    private int moveCount;
    private int factor;
    private int tempFactor;

    private Stack recursionStack;
    private int numberOfLevelsToBuild = -1;
    private XmlElement root;
    private IProgressMonitor monitor;
    private XmlDocumentFactory docFactory = XmlDocumentFactory.eINSTANCE;
    private ModelEditor me = ModelerCore.getModelEditor();
    private boolean useFragments;

    /**
     * Construct an instance of XmlDocumentBuilderImpl.
     *
     */
    public XmlDocumentBuilderImpl() {
        this(-1);
    }

    /**
     * Construct an instance of XmlDocumentBuilderImpl, passing in the number of levels to build
     * @param the number of levels deep to build.
     */

    public XmlDocumentBuilderImpl(final int levels) {
        setNumberOfLevelsToBuild(levels);
    }

    //-------------------------------------------------------------------------
    //      I N T E R F A C E     M E T H O D S
    //-------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.xml.XmlDocumentBuilder#setFragments(java.util.Collection)
     */
    public void setFragments(final Collection xmlFragments) throws ModelerCoreException {
        //Check arguements and initialize variables
        ArgCheck.isNotNull(xmlFragments);
        final Iterator iter = xmlFragments.iterator();
        while (iter.hasNext()) {
            final Object next = iter.next();
            if( !(next instanceof XmlFragment) ){
                throw new ModelerCoreException(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.All_xml_fragments_must_be_an_instance_of_XmlFragment_1")); //$NON-NLS-1$
            }

            //Create an XmlFragmentUse for each fragment and register it in the fragment map by xsdcomponent
            final XmlFragment fragment = (XmlFragment)next;
            XSDComponent xsdComponent = fragment.getRoot() == null ? null : fragment.getRoot().getXsdComponent();
            if(xsdComponent != null){
                //resolve the xsd component if it is a ref
                final XSDComponent ref = resolveSchemaRef(xsdComponent);
                if(ref != null){
                    xsdComponent = ref;
                }

                //Add the new new XmlFragment to the map with the xsdComponent as the key
                fragmentMap.put(xsdComponent, fragment);
            }
        }

        useFragments = !fragmentMap.isEmpty();
    }



    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.xml.XmlDocumentBuilder#buildDocument(com.metamatrix.metamodels.xml.XmlElement, org.eclipse.core.runtime.IProgressMonitor)
     */
    public int buildDocument(final XmlElement rootElement, final IProgressMonitor progressMonitor) throws ModelerCoreException {
        ArgCheck.isNotNull(rootElement);
        this.root = rootElement;
        this.monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();

        //reset the attributes for tracking namespaces
        referencedSchemas.clear();
        initializeSchemaUris(rootElement);

        //Start UoW if neccessary
        final boolean startedTxn = ModelerCore.startTxn(true, true, XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Build_XML_Document_5"), this); //$NON-NLS-1$
        boolean succeeded = false;
        try{
            // update flags:
            setBuildStatusProperty(rootElement, BuildStatus.COMPLETE_LITERAL);
            if (rootElement.isRecursive()) {
                setRecursiveProperty(rootElement, false);
            } // endif
            //Resolve the Schema definition for the root object
            final XSDComponent schemaComponent = root.getXsdComponent();
            if(schemaComponent == null){
                throw new ModelerCoreException(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Schema_reference_not_set_on_root_element_1")); //$NON-NLS-1$
            }

            // The root Element in the schema should have a type that is either
            // a SimpleType or ComplexType
            XSDTypeDefinition typeDefn = XmlDocumentUtil.findXSDType(schemaComponent);
            if ( typeDefn != null ) {
                //Ensure name matches
                if(schemaComponent instanceof XSDNamedComponent){
                    String name = ((XSDNamedComponent)schemaComponent).getName();

                    // if the element is not named get it from the type
                    if ((name == null) || (name.length() == 0)) {
                        XSDComponent ref = resolveSchemaRef(schemaComponent);

                        if ((ref != null) && (ref instanceof XSDNamedComponent)) {
                            name = ((XSDNamedComponent)ref).getName();
                        }
                    }

                    final String newName = this.validator.createValidName(name);
                    if (newName != null) {
                        name = newName;
                    }
                    root.setName(name);
                }

                //Set the units of work on the monitor
                monitor.beginTask(TASK_NAME , 5000);
                monitor.subTask(CALCULATING_MSG + root.getName() );
                initializeRecursionStack(root);
                //countNodes(typeDefn, CALCULATING_MSG + root.getName(), false);
                //monitor.worked(500);
                monitor.subTask(BUILDING_MSG + root.getName());

//                if(ModelerCore.DEBUG_XML){
//                    System.out.println("Done Counting : " + nodeCount); //$NON-NLS-1$
//                }
//
//                estimatedNodeCount = nodeCount;

//                //Calculate factor to use when adding work for each remaining node.
//                final double temp = 4500d / nodeCount;
//                if(temp > 0){
//                    this.factor = 4500 / nodeCount;
//                }else{
//                    this.factor = new Double(0 - (1/(4500d/9200) ) ).intValue();
//                }

                initializeRecursionStack(root);
                nodeCount = 0;

                //Recursively build document from schema
                addChildren(this.root, typeDefn, BUILDING_MSG + root.getName());
            }else{
                throw new ModelerCoreException(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.No_type_element_found_for_root_document_element_2")); //$NON-NLS-1$
            }

            succeeded = true;
            return nodeCount;
        }catch(ModelerCoreException e){
            //Just throw MCE
            throw e;
        }catch(Throwable e){
            //This is an unexpected e... ensure that it is logged
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage() );
        }finally{
            //cleanup
            cleanup();
            //Commit txn if we started it.
            if(startedTxn){
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }

            //Force GC
            System.gc();
            Thread.yield();
            monitor.worked(10);
        }

        return nodeCount;
    }

    public Collection getUnhandledModelImports() {
        return this.unhandledModelImports;
    }

    private void cleanup() {
        useFragments = false;
        numberOfLevelsToBuild = -1;
        numberOfLevelsToBuild = -1;
        root = null;

        if( recursionStack != null ) {
            recursionStack.clear();
        }
        recursiveElementUUIDs.clear();
        updateDeleteMap.clear();
        updateCurrentChildMap.clear();
        uriToNamespaceMap.clear();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.xml.XmlDocumentBuilder#updateFromSchema(com.metamatrix.metamodels.xml.XmlElement, org.eclipse.core.runtime.IProgressMonitor)
     */
    public int updateFromSchema(final XmlElement rootElement, IProgressMonitor progressMonitor) throws ModelerCoreException {
        ArgCheck.isNotNull(rootElement);
        this.root = rootElement;
        int count = 0;
        this.monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();

        // Start UoW if neccessary
        final boolean startedTxn = ModelerCore.startTxn(true, true, XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Update_XML_Document_from_Schema_6"), this); //$NON-NLS-1$
        boolean succeeded = false;
        try{
            //First do all the deletes - Do deletes first to ensure indexes are correct
            updateFromSchema(root, false);

            //Process all the deletes. - Ensure model has no extra nodes prior to beginning adds
            count = performTreeUpdates();

            //reset the attributes for tracking namespaces
            referencedSchemas.clear();
            initializeSchemaUris(rootElement);

            //Now do the Adds
            updateFromSchema(root, true);
            count = count + newCount + moveCount;
        }finally{
            cleanup();

            //Commit UoW if we started it.
            if(startedTxn){
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        return count;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.xml.XmlDocumentBuilder#setNumberOfLevelsToBuild(int)
     */
    public void setNumberOfLevelsToBuild(final int numberOfLevelsToBuild) {
        this.numberOfLevelsToBuild = numberOfLevelsToBuild;
    }

    /**
     * Returns the collection of ObjectID objects for any recursive nodes in the tree
     * @return Colection of ObjectIDs... never null
     */
    public Collection getRecursiveElementObjectIDs(){
        return recursiveElementUUIDs;
    }

    /**
     * Setter for the addNamespaces attribute.  If true,
     * namespaces will automatically be added at the document
     * level for each schema referenced in the build process.
     *
     * Default is true, attribute only needs to be set if no namespaces are desired
     * @param addNamespaces
     */
    public void setAddNamespaces(final boolean addNamespaces) {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.xml.XmlDocumentBuilder#buildXmlFragments(org.eclipse.xsd.XSDSchema)
     */
    public Collection buildXmlFragments(final XSDSchema schema) throws ModelerCoreException {
        ArgCheck.isNotNull(schema);
        final Collection fragments = new ArrayList();
        final Iterator iter = schema.getContents().iterator();
        while (iter.hasNext()) {
            if ( this.monitor != null && this.monitor.isCanceled() ) {
                break;
            }
            final Object next = iter.next();
            if(next instanceof XSDElementDeclaration){
                final XSDElementDeclaration child = (XSDElementDeclaration)next;
                final XmlRoot root = docFactory.createXmlRoot();
                final XmlDocumentBuilder builder = new XmlDocumentBuilderImpl();

                root.setXsdComponent(child);
                builder.buildDocument(root, null);
                final XmlFragment fragment = docFactory.createXmlFragment();
                String name = root.getName();
                final String newName = this.validator.createValidName(name);
                if (newName != null) {
                    name = newName;
                }
                fragment.setName(name);
                fragment.setRoot(root);
                fragments.add(fragment);
            }
        }

        return fragments;
    }

    //-------------------------------------------------------------------------
    //      I N S T A N C E     M E T H O D S
    //-------------------------------------------------------------------------
    /**
     * Initialize the schemaUris attribute, adding an entry for each namespace found at
     * the given element's document
     * @param rootElement
     */
    private void initializeSchemaUris(final XmlElement element) {
        final XSDComponent xsdComp = element.getXsdComponent();

        //If there is not xsd component for this element, just return
        if(xsdComp == null){
            return;
        }

        //find the root element
        XmlElement root = null;
        EObject node = element;
        while(root == null && node != null){
            if(node instanceof XmlRoot){
                root = (XmlElement)node;
            }else{
                node = node.eContainer();
            }
        }

        //If we can't find a doc parent... just return;
        if(root == null){
            return;
        }

        //Add the uri for each namespace child of the document
        final Iterator children = root.eContents().iterator();
        while(children.hasNext() ){
            EObject next = (EObject)children.next();
            if(next instanceof XmlNamespace){
                schemaUris.add( ((XmlNamespace)next).getUri() );
            }
        }

        //Update the list for the given xsd component
        updateSchemaUris(element, xsdComp);
    }

    private void updateSchemaUris(final EObject docNode, final XSDComponent xsdComp){
        if(xsdComp == null || docNode == null){
            return;
        }

        final XSDSchema schema = xsdComp.getSchema();
        if ( schema == null ) {
            // Then this is likely an XSD component that is an element/attribute reference to something
            // that cannot be resolved.
            String msg = null;
            if ( xsdComp instanceof XSDNamedComponent ) {
                final XSDNamedComponent namedComp = (XSDNamedComponent)xsdComp;
                final Object[] params = new Object[]{namedComp.getName()};
                msg = XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Unable_to_resolve_XSD_reference_to",params); //$NON-NLS-1$
            } else {
                final IPath path = ModelerCore.getModelEditor().getModelRelativePath(docNode);
                final Object[] params = new Object[]{path};
                msg = XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Unable_to_resolve_XSD_reference_for",params); //$NON-NLS-1$
            }
            XmlDocumentPlugin.Util.log(IStatus.ERROR,msg);
            return;
        }

        //check to see if we've already done this schema
        if(referencedSchemas.contains( schema ) ){
            return;
        }
        referencedSchemas.add( schema );

        //find the root element
        XmlElement root = null;
        EObject node = docNode;
        while(root == null && node != null){
            if(node instanceof XmlRoot){
                root = (XmlElement)node;
            }else{
                node = node.eContainer();
            }
        }

        final Map qNameMap = schema.getQNamePrefixToNamespaceMap();
        if(qNameMap != null){
            final Iterator entries = qNameMap.entrySet().iterator();
            while(entries.hasNext() ){
                final Map.Entry next = (Map.Entry)entries.next();
                final String prefix = (String)next.getKey();
                final String uri = (String)next.getValue();
                if(!schemaUris.contains(uri) ){
                    final XmlNamespace ns = docFactory.createXmlNamespace();
                    if("this".equals(prefix) || prefix == null || prefix.trim().length() == 0 ){ //$NON-NLS-1$
                        ns.setPrefix( XmlDocumentUtil.createXmlPrefixFromUri(uri) );
                    } else {
                        ns.setPrefix(prefix);
                    }
                    ns.setUri(uri);
                    ns.setElement(root);
                    schemaUris.add(uri);
                    this.uriToNamespaceMap.put(uri, ns);
                }
            }
        }
    }

    /**
     * Perform deletes from the updateDeleteMap
     * @return number of deleted nodes.
     */
    private int performTreeUpdates() throws ModelerCoreException{
        int count = 0;

        //Perform the deletes
        Iterator keys = updateDeleteMap.keySet().iterator();
        while(keys.hasNext() ){
            EObject parent = (EObject)keys.next();
            ArrayList children = (ArrayList)updateDeleteMap.get(parent);
            if(children != null){
                Iterator childrenIT = children.iterator();
                while(childrenIT.hasNext() ){
                    EObject child = (EObject)childrenIT.next();
                    me.delete(child);
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Update the XML Document from the given Root MetaObject down.
     * This will remove xml doc nodes that do not exist in the schema, add xml doc nodes
     * for schema nodes that are missing and ensure indexes are the same.
     * @param root - current root element
     * @param monitor
     * @param boolean true if performing the adds.
     */
    private void updateFromSchema(final XmlElement rootElement, final boolean doAdds) throws ModelerCoreException {
        this.root = rootElement;
        newCount = 0;

        //Resolve the Schema definition for the root object
        final XSDComponent schemaComponent = root.getXsdComponent();

        // The root Element in the schema should have a type that is either
        // a SimpleType or ComplexType
        XSDTypeDefinition typeDefn = XmlDocumentUtil.findXSDType(schemaComponent);
        if ( typeDefn != null ) {
            //Set the units of work on the monitor
            nodeCount = 1;
            monitor.beginTask(CALCULATING_MSG2 + root, 5000);
            initializeRecursionStack(root);
            //countNodes(typeDefn, CALCULATING_MSG2 + root, false);
            monitor.worked(500);
            monitor.subTask(UPDATING_MSG + root);

            //Calculate factor to use when adding work for each remaining node.
            final double temp = 4500d / nodeCount;
            if(temp > 0){
                this.factor = 4500 / nodeCount;
            }else{
                this.factor = new Double(0 - (1/(4500d/9200) ) ).intValue();
            }

            initializeRecursionStack(root);
            nodeCount = 0;
            if(doAdds){
                updateAdd(root,typeDefn, UPDATING_MSG + root);
            }else{
                updateDelete(root,typeDefn, UPDATING_MSG + root);
            }

         }
    }

    /**
     * Perform deletes for the updateFromSchema process
     * @param documentElement
     * @param xsdComponent
     * @param string task name
     */
    private void updateDelete(final XmlDocumentEntity documentElement, XSDComponent xsdComponent, final String taskName) {
        //Check the progressMonitor
        if(this.monitor.isCanceled() ){
            return;
        }
        this.monitor.subTask(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.{0}_{1}_of_{2}", taskName, new Integer(nodeCount), new Integer(4500) )); //$NON-NLS-1$


        //Convert any schema element ref  or substitution group objects
        final XSDComponent ref = resolveSchemaRef(xsdComponent);

        //check for recursion and isExisting constants
        boolean checkRecursion = false;
        boolean isRecursive = false;
        boolean checkExisting = false;
        if(ref != null){
            if(recursionStack.contains(ref) ){
                isRecursive = true;
            }
            recursionStack.push(ref);
        }else{
            if(recursionStack.contains(xsdComponent) ){
                isRecursive = true;
            }
            recursionStack.push(xsdComponent);
        }


        //Capture existing children info
        XmlDocumentEntity documentChild = documentElement;
        updateDeleteMap(documentElement, null);
        final XmlDocumentEntity existingChild = findExistingDocumentChild( (ArrayList)updateDeleteMap.get(documentElement), xsdComponent);

        if( isAllCompositor(xsdComponent) ){
            XmlAll temp = null;

            // Attempt to find an existing all (this occurs when the complex type that uses
            // an all extends another complex type that has an all);
            final ArrayList currentChildren = (ArrayList)updateCurrentChildMap.get(documentElement);
            if(currentChildren != null){
                final Iterator iter = currentChildren.iterator();
                while (iter.hasNext()) {
                    final Object sibling = iter.next();
                    if ( sibling instanceof XmlAll ) {
                        temp = (XmlAll)sibling; // use this all component ..
                        documentChild = temp;
                        break;
                    }
                }
            }

            //If we didn't find one under the documentElement, look for one to reuse
            if(temp == null){
                final ArrayList oldChildren = (ArrayList)updateDeleteMap.get(documentElement);
                if(oldChildren != null){
                    final Iterator childrenIT = oldChildren.iterator();
                    while (childrenIT.hasNext()) {
                        final Object sibling = childrenIT.next();
                        if ( sibling instanceof XmlAll ) {
                            temp = (XmlAll)sibling; // use this all component ..
                            documentChild = temp;
                            break;
                        }
                    }
                }
            }

            if(temp != null){
                updateDeleteMap(documentElement, documentChild);
            }

            if(ref != null){
                 xsdComponent = ref;
            }

            updateMonitor();
            checkRecursion = true;
        }else if( isSequenceCompositor(xsdComponent) ){
            XmlSequence temp = null;

            // Attempt to find an existing sequence (this occurs when the complex type that uses
            // a sequence extends another complex type that has a sequence);
            ArrayList currentChildren = (ArrayList)updateCurrentChildMap.get(documentElement);
            if(currentChildren != null){
                final Iterator iter = currentChildren.iterator();
                while (iter.hasNext()) {
                   final EObject sibling = (EObject)iter.next();
                   if ( sibling instanceof XmlSequence ) {
                       //check the min / max occurs the the schema references
                       //if they match then use this sequence
                        if(isValidSequenceForReuse(findSchemaReference(sibling), xsdComponent) ){
                            temp = (XmlSequence)sibling; // use this sequence ..
                            documentChild = temp;
                            break;
                       }
                    }
                }
            }

            //If we didn't find one under the documentElement, look for one to reuse
            if(temp == null){
                final ArrayList oldChildren = (ArrayList)updateDeleteMap.get(documentElement);
                if(oldChildren != null){
                    final Iterator childrenIT = oldChildren.iterator();
                    while (childrenIT.hasNext()) {
                       final EObject sibling = (EObject) childrenIT.next();
                       if ( sibling instanceof XmlSequence ) {
                           //check the min / max occurs the the schema references
                           //if they match then use this sequence
                            if(isValidSequenceForReuse(findSchemaReference(sibling), xsdComponent) ){
                                temp = (XmlSequence)sibling; // use this sequence ..
                                documentChild = temp;
                                break;
                           }
                        }
                    }
                }
            }

            if(temp != null){
                updateDeleteMap(documentElement, documentChild);
            }

            updateMonitor();
        }else if(existingChild != null){
            documentChild = existingChild;
            updateDeleteMap(documentElement, documentChild);

            nodeCount++;
            updateMonitor();
            checkExisting = true;
            checkRecursion = (xsdComponent instanceof XSDElementDeclaration) && !((XSDElementDeclaration)xsdComponent).isAbstract();
        }else{
            checkRecursion = (xsdComponent instanceof XSDElementDeclaration) && !((XSDElementDeclaration)xsdComponent).isAbstract();
        }

        if(isRecursive && checkRecursion){
            ObjectID uuid = ModelerCore.getObjectId(documentChild);
			if (recursiveElementUUIDs.contains(uuid)) {
                if(!recursionStack.isEmpty() ){
                    recursionStack.pop();
                }
                return;
            }

            recursiveElementUUIDs.add(uuid);
            if(!recursionStack.isEmpty() ){
                recursionStack.pop();
            }
            return;
        }

        // The element and attributes in the schema should have a type that is either
        // a SimpleType or ComplexType; use that and build child objects according to the type
        // However, DO NOT do this if the type is an anonymous type below the schema element,
        // since children are handled below!
        final XSDComponent typeEntity = XmlDocumentUtil.findXSDType(xsdComponent);
        if ( typeEntity != null && typeEntity.eContainer() != null && !typeEntity.eContainer().equals(xsdComponent) ) {
            if(!checkExisting || (checkExisting && existingChild != null) ){
                updateDelete(documentChild, typeEntity, taskName);
            }
        }

        //Now add this Node's Children (like attributes) to the new Node (or this node if the schema element was not valid)
        final Iterator children = xsdComponent.eContents().iterator();
        while(children.hasNext() ){
            final EObject schemaChild = (EObject)children.next();   // may be XSDDiagnostic if validated!
            if(!checkExisting || (checkExisting && existingChild != null) ){
                if ( schemaChild instanceof XSDComponent ) {
                    updateDelete(documentChild, (XSDComponent)schemaChild, taskName);
                }
            }
        }

        if(!recursionStack.isEmpty() ){
            recursionStack.pop();
        }
    }

    /**
     * Updates the updateDeleteMap for given document entity
     * The entities in the value collection have been added
     * to the documentEntity as children via the updateChildren method.
     * When the update is complete any children of the document entity
     * which are not in the collection in the updateReUseMap need to
     * be deleted from the model.
     * The updateCurrentChildMap should be updated as well
     */
    private void updateDeleteMap(final XmlDocumentEntity documentEntity, final XmlDocumentEntity child){
        //If child == null initialize the entry
        if(child == null){
            if(updateDeleteMap.get(documentEntity) == null){
                ArrayList children = new ArrayList(documentEntity.eContents() );
                updateDeleteMap.put(documentEntity, children );
            }

            return;
        }

        //Update the updateDeleteMap
        ArrayList children = (ArrayList)updateDeleteMap.get(documentEntity);
        if(children != null){
            children.remove(child);
            updateDeleteMap.put(documentEntity, children);
        }

        //Update the updateCurrentChildMap
        children = (ArrayList)updateCurrentChildMap.get(documentEntity);
        if(children == null){
            children = new ArrayList();
        }

        children.add(child);
        updateCurrentChildMap.put(documentEntity, children);
    }

    /**
     * Perform the adds for the updateFromSchema process
     * @param documentElement
     * @param xsdComponent
     * @param string
     */
    private void updateAdd(final XmlDocumentEntity documentElement, XSDComponent xsdComponent, final String taskName) throws ModelerCoreException {
        //Check the progressMonitor
        if(this.monitor.isCanceled() ){
            return;
        }

        this.monitor.subTask(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.{0}_{1}_of_{2}", taskName, new Integer(nodeCount), new Integer(4500) )); //$NON-NLS-1$

        //Convert any schema element ref  or substitution group objects
        final XSDComponent ref = resolveSchemaRef(xsdComponent);

        //look for a reusable documentFragment
        if(useFragments){
            final XmlFragment fragment = (ref == null ? (XmlFragment)fragmentMap.get(xsdComponent) : (XmlFragment)fragmentMap.get(ref) );
            if(fragment != null){
                if(documentElement instanceof XmlElementHolder){
                    //create the XmlFragmentUse and set it's values from the xmlFragment
                    final XmlFragmentUse use = docFactory.createXmlFragmentUse();
                    use.setFragment(fragment);
                    use.setName(fragment.getName() );
                    use.setXsdComponent(ref == null ? xsdComponent : ref);
                    use.setNamespace(fragment.getRoot() == null ? null : fragment.getRoot().getNamespace() );

                    use.setParent( (XmlElementHolder)documentElement );

                    //No need to process this node any further... just return;
                    return;
                }
                if(ModelerCore.DEBUG_XML){
                    System.out.println("Can't add docFragment to " + documentElement.getClass().getName() ); //$NON-NLS-1$
                }
            }
        }

        //check for recursion and isExisting variables
        boolean checkRecursion = false;
        boolean isRecursive = false;
        boolean checkExisting = false;
        if(ref != null){
            if(recursionStack.contains(ref) ){
                isRecursive = true;
            }
            recursionStack.push(ref);
        }else{
            if(recursionStack.contains(xsdComponent) ){
                isRecursive = true;
            }
            recursionStack.push(xsdComponent);
        }


        XmlDocumentEntity documentChild = documentElement;
        final ArrayList docChildren = new ArrayList(documentChild.eContents());
        final XmlDocumentEntity existingChild = findExistingDocumentChild( docChildren, xsdComponent);

        if(xsdComponent instanceof XSDElementDeclaration && !((XSDElementDeclaration)xsdComponent).isAbstract() ){
            //Process only non-abstract XSDElements
            String name = ((XSDElementDeclaration)xsdComponent).getName();
            if(ref != null){
                name = ((XSDElementDeclaration)ref).getName();
            }

            if(existingChild == null){
                XmlElement temp = docFactory.createXmlElement();
                temp.setXsdComponent(xsdComponent);
                if(documentElement instanceof XmlContainerNode){
//                    ((XmlContainerNode)documentElement).getElements().add(temp);
                    addValueToList(documentElement, temp, ((XmlContainerNode)documentElement).getElements());
                }else{
                    if(ModelerCore.DEBUG_XML){
                        System.out.println("Can't add element to " + documentElement.getClass().getName() ); //$NON-NLS-1$
                    }
                }

                final XSDElementDeclaration xsdElement = (XSDElementDeclaration)xsdComponent;
                if(xsdElement.getLexicalValue() != null) {
                    temp.setValue(xsdElement.getLexicalValue() );
                    final XSDConstraint constraint = xsdElement.getConstraint();
                    if(XSDConstraint.DEFAULT_LITERAL.equals(constraint) ) {
                        temp.setValueType(ValueType.DEFAULT_LITERAL);
                    }else if(XSDConstraint.FIXED_LITERAL.equals(constraint) ) {
                        temp.setValueType(ValueType.FIXED_LITERAL);
                    }
                }

                documentChild = temp;
                newCount++;
            }else{
                //Ensure index is the same
                boolean moved = moveIfNotAtSameIndex(existingChild, xsdComponent);
                if(moved){
                    moveCount++;
                }

                //verify the name's match
                final String newName = this.validator.createValidName(name);
                if (newName != null) {
                    name = newName;
                }
                if(!name.equals( ((XmlElement)existingChild).getName() ) ){
                    ((XmlElement)existingChild).setName(name);
                }

                documentChild = existingChild;
            }

            if(ref != null){
                xsdComponent = ref;
            }

            checkRecursion = true;
            checkExisting = true;
            nodeCount++;
            updateMonitor();
        }else if(xsdComponent instanceof XSDModelGroupDefinition){
            if(ref != null){
                xsdComponent = ref;
            }

            updateMonitor();
        }else if(xsdComponent instanceof XSDAttributeDeclaration ){
            String name = ((XSDAttributeDeclaration)xsdComponent).getName();
            if(ref != null){
                name = ((XSDAttributeDeclaration)ref).getName();
            }

            if(existingChild == null){
                // Determine if the attribute is prohibited ...
                final boolean prohibited = isProhibited( (XSDAttributeDeclaration)xsdComponent);
                if ( prohibited ) {
                    // Remove any existing sibling attribute with the same name ...
                    removeChildrenOfSameName(documentElement, XSDAttributeDeclaration.class, ((XSDAttributeDeclaration)xsdComponent).getName() );
                } else {
                    final XmlAttribute attribute = docFactory.createXmlAttribute();
                    attribute.setXsdComponent(xsdComponent);
                    if(documentElement instanceof XmlElement){
                        //((XmlElement)documentElement).getAttributes().add(attribute);
                        addValueToList(documentElement, attribute, ((XmlElement)documentElement).getAttributes());
                    }else{
                        if(ModelerCore.DEBUG_XML){
                            System.out.println("Can't add attribute to " + documentElement.getClass().getName() ); //$NON-NLS-1$
                        }
                    }
                    documentChild = attribute;
                    newCount++;
                }
            }else{
                //Ensure index is the same
                boolean moved = moveIfNotAtSameIndex(existingChild, xsdComponent);
                if(moved){
                    moveCount++;
                }

                //verify the name's match
                final String newName = this.validator.createValidName(name);
                if (newName != null) {
                    name = newName;
                }
                if(!name.equals( ((XmlAttribute)existingChild).getName() ) ){
                    ((XmlAttribute)existingChild).setName(name);
                }

                documentChild = existingChild;
            }

            if(ref != null){
                xsdComponent = ref;
            }

            checkExisting = true;
            nodeCount++;
            updateMonitor();
        }else if( isAllCompositor(xsdComponent)){
            XmlAll temp = null;

            // Attempt to find an existing all (this occurs when the complex type that uses
            // an all extends another complex type that has an all);
            final Iterator iter = documentElement.eContents().iterator();
            while (iter.hasNext()) {
                final XmlAll sibling = (XmlAll) iter.next();
                temp = sibling;
                temp.setXsdComponent(xsdComponent);
                documentChild = sibling; // use this all component ..
                break;
            }

            // If no all sibling found, create one ...
            if(temp == null){
                temp = docFactory.createXmlAll();
                temp.setXsdComponent(xsdComponent);
                //((XmlContainerHolder)documentElement).getContainers().add(temp);
                addValueToList(documentElement, temp, ((XmlContainerHolder)documentElement).getContainers());
                documentChild = temp;
                newCount++;
            }

            if(ref != null){
                xsdComponent = ref;
            }

            checkRecursion = true;
            nodeCount++;
            updateMonitor();
        }else if( isChoiceCompositor(xsdComponent) ){
            if(existingChild == null){
                final XmlChoice temp = docFactory.createXmlChoice();
                temp.setXsdComponent(xsdComponent);
                //((XmlContainerHolder)documentElement).getContainers().add(temp);
                addValueToList(documentElement, temp, ((XmlContainerHolder)documentElement).getContainers());
                documentChild = temp;
                newCount++;
            }else{
                documentChild = existingChild;
            }

            checkExisting = true;
            nodeCount++;
            updateMonitor();
        }else if( isSequenceCompositor(xsdComponent) ){
            XmlSequence temp = null;

            // Attempt to find an existing sequence (this occurs when the complex type that uses
            // a sequence extends another complex type that has a sequence);
            final Iterator iter = documentElement.eContents().iterator();
            while (iter.hasNext()) {
                final EObject sibling = (EObject) iter.next();
                if ( sibling instanceof XmlSequence ) {
                    //check the min / max occurs the the schema references
                    //if they match then use this sequence
                    if(isValidSequenceForReuse( findSchemaReference(sibling), xsdComponent) ){
                        temp = (XmlSequence)sibling; // use this sequence ..
                        documentChild = temp;
                        break;
                    }
                }
            }

            // If no sequence sibling found, create one ...
            if(temp == null){
                temp = docFactory.createXmlSequence();
                //((XmlContainerHolder)documentElement).getContainers().add(temp);
                addValueToList(documentElement, temp, ((XmlContainerHolder)documentElement).getContainers());
                documentChild = temp;
                newCount++;
            }

            temp.setXsdComponent(xsdComponent);
            nodeCount++;
            updateMonitor();
        }

        if(isRecursive && checkRecursion){
            //break on recursion
            if(documentChild instanceof XmlElement){
                ((XmlElement)documentChild).setRecursive(true);
            }

            ObjectID uuid = ModelerCore.getObjectId(documentChild);
			if (recursiveElementUUIDs.contains(uuid)) {
                if(!recursionStack.isEmpty() ) {
                    recursionStack.pop();
               }

                return;
            }

            recursiveElementUUIDs.add(uuid);
            if(!recursionStack.isEmpty() ) {
                recursionStack.pop();
            }

            return;
        }


        //Add namespace for XSDComponent if required
        if(documentChild != documentElement){
            updateSchemaUris(documentChild, xsdComponent);
        }

        // The element and attributes in the schema should have a type that is either
        // a SimpleType or ComplexType; use that and build child objects according to the type
        // However, DO NOT do this if the type is an anonymous type below the schema element,
        // since children are handled below!
        final XSDComponent typeEntity = XmlDocumentUtil.findXSDType(xsdComponent);
        if ( typeEntity != null && typeEntity.eContainer() != null && !typeEntity.eContainer().equals(xsdComponent) ) {
            if(checkExisting && existingChild == null){
                addChildren(documentChild, typeEntity, taskName);
            }else{
                updateAdd(documentChild, typeEntity, taskName);
            }
        }

        //Now add this Node's Children (like attributes) to the new Node (or this node if the schema element was not valid)
        final Iterator children = xsdComponent.eContents().iterator();
        while(children.hasNext() ){
            EObject schemaChild = (EObject)children.next();     // may have XSDDiagnostics that are not XSDComponents!
            if ( schemaChild instanceof XSDComponent ) {
                if(checkExisting && existingChild == null){
                    addChildren(documentChild, (XSDComponent)schemaChild, taskName);
                }else{
                    updateAdd(documentChild, (XSDComponent)schemaChild, taskName);
                }
            }
        }

        if(!recursionStack.isEmpty() ) {
            recursionStack.pop();
        }
    }

    /**
     * Move the existing child to the appropriate index in the docParent so that it is consistent with the schema
     * @param documentElement
     * @param xsdComponent
     * @return true if document element was moved.
     */
    private boolean moveIfNotAtSameIndex(final XmlDocumentEntity documentElement, final XSDComponent xsdComponent) throws ModelerCoreException {
        boolean moved = false;
        final XmlDocumentEntity docParent = (XmlDocumentEntity)documentElement.eContainer();
        final XSDComponent schemaParent = (XSDComponent)xsdComponent.eContainer();
        int docIndex = getIndexOfChild(docParent, documentElement);
        int schemaIndex = getIndexOfChild(schemaParent, xsdComponent);

        if(docIndex == -1){
            throw new ModelerCoreException(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Unable_to_find_document_index_for_{0}_5") + xsdComponent.toString() ); //$NON-NLS-1$
        }else if(schemaIndex == -1){
            throw new ModelerCoreException(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Unable_to_find_schema_index_for_{0}_6") + documentElement.toString() ); //$NON-NLS-1$
        }

        //If the documentElement and schemaElement are both at index 0 of their parent no move required
        if(docIndex == 0 && schemaIndex == 0){
            return moved;
        }

        //If both index are not == 0 ensure that the index of the docElement is in the same relative
        //place as it's schemaElement
        final EList docList = (EList)docParent.eGet(documentElement.eContainmentFeature() );
        EList schemaList = null;

        //If the schemaParent is a Particle, you really want the list from the ModelGroup.
        if(schemaParent instanceof XSDParticle || schemaParent instanceof XSDAttributeUse){
            schemaList = (EList)schemaParent.eContainer().eGet(schemaParent.eContainmentFeature() );
        }else{
            schemaList = (EList)schemaParent.eGet(xsdComponent.eContainmentFeature() );
        }

        //Easy Case... schemaIndex > 0
        //If schemaIndex > 0 and docParent.getChildCount() > 1 move may be required
        if(schemaIndex > 0 && docList.size() > 1){
            final EObject tempSchemaElement = (EObject)schemaList.get(( schemaIndex - 1) );
            final Iterator docChildren = docList.iterator();
            boolean found = false;
            while(docChildren.hasNext() && !found ){
                final EObject docChild = (EObject)docChildren.next();
                final EObject schemaRef = findSchemaReference(docChild);
                if(schemaRef != null && schemaRef == tempSchemaElement){
                    found = true;
                    int temp = getIndexOfChild(docParent, docChild) + 1;
                    if(temp != docIndex){
                        me.move(docParent,  documentElement, temp);
                        moved = true;
                    }

                }
            }
        }else if(schemaIndex == 0 && docList.size() > 1){
            //Ensure that no schemaElement siblings are ahead of this element in doc
            int foundIndex = Integer.MAX_VALUE;
            final Iterator docChildren = docList.iterator();
            while(docChildren.hasNext() ){
                final EObject docChild = (EObject)docChildren.next();
                final EObject schemaRef = findSchemaReference(docChild);
                if(schemaRef != null && schemaRef != xsdComponent && schemaList.contains(schemaRef) ){
                    int temp = getIndexOfChild(docParent, docChild);
                    if(temp < foundIndex){
                        foundIndex = temp;
                    }
                }
            }

            if(foundIndex < Integer.MAX_VALUE  && foundIndex != docIndex){
                me.move(docParent, documentElement, foundIndex);
                moved = true;
            }
        }

        return moved;
    }

    /**
     * Derive the index of the child within the containment feature of the given parent
     * @param parent
     * @param child
     * @return -1 if the child is not a child of the given parent, else return the index of the child in the
     * parent's containment feature.
     */
    private int getIndexOfChild(final EObject parent, final EObject child){
        if(child.eContainer() == null || !child.eContainer().equals(parent) ){
            return -1;
        }

        //If the parent is a XSDParticle, you really want the index of the Particle in it's ModelGroup.
        if(parent instanceof XSDParticle || parent instanceof XSDAttributeUse){
            return getIndexOfChild(parent.eContainer(), parent);
        }

        final EStructuralFeature sf = child.eContainmentFeature();
        if(sf == null){
            return -1;
        }

        final Object result = parent.eGet(sf);
        if(result == null || !(result instanceof EList) ){
            return -1;
        }

        return ((EList)result).indexOf(child);
    }

    /**
     * Find an existing child in the document that is valid for reuse
     * @param documentChildren - collection of existing document children
     * @param xsdComponent
     * @return matching docChild or null if none found
     */
    private XmlDocumentEntity findExistingDocumentChild(final ArrayList documentChildren, final XSDComponent xsdComponent) {
        if(documentChildren == null || documentChildren.isEmpty() ){
            return null;
        }

        Iterator children = documentChildren.iterator();
        while(children.hasNext() ){
            XmlDocumentEntity next = (XmlDocumentEntity)children.next();
            if(xsdComponent.equals( findSchemaReference(next) ) ){
                return next;
            }
        }

        //If no exact match found and the schema reference is a sequence or an all
        //look for a re-useable seqence or all.
        if( isAllCompositor(xsdComponent) || isSequenceCompositor(xsdComponent) ){
            children = documentChildren.iterator();
            while(children.hasNext() ){
                final XmlDocumentEntity child = (XmlDocumentEntity)children.next();
                final XSDComponent schemaRef = findSchemaReference(child);
                if(schemaRef != null){
                    if(xsdComponent.eClass().equals(schemaRef.eClass() ) ){
                        final Iterator grandchildren = child.eContents().iterator();
                        while(grandchildren.hasNext() ){
                            final XmlDocumentEntity grandchild = (XmlDocumentEntity)grandchildren.next();
                            final XSDComponent gcSchemaRef = findSchemaReference(grandchild);
                            if(xsdComponent.eContents().contains(gcSchemaRef) ){
                                return child;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Return the XsdComponent for the given XmlDocumentEntity - or null if none exists
     * @param child
     * @return the XsdComponent for the given XmlDocumentEntity - or null if none exists
     */
    private XSDComponent findSchemaReference(final EObject child) {
        if(child instanceof XmlDocumentNode){
            return ((XmlDocumentNode)child).getXsdComponent();
        }else if(child instanceof XmlContainerNode){
            return ((XmlContainerNode)child).getXsdComponent();
        }

        return null;
    }

    /**
     * Count the buildable nodes from the given schemaComponent
     * @param schemaComponent
     * @param string - task name
     * @param done
     */
//    private void countNodes(final XSDComponent schemaComponent, final String taskName, final boolean done) {
//        if(ModelerCore.DEBUG_XML){
//            if(schemaComponent instanceof XSDNamedComponent){
//                System.out.println("Counting " + ((XSDNamedComponent)schemaComponent).getName() ); //$NON-NLS-1$
//            }else{
//                System.out.println("Counting " + schemaComponent); //$NON-NLS-1$
//            }
//        }
//
//        if(nodeCount >= MAX_COUNT) {
//            return;
//        }
//
//        boolean isRecursive = recursionStack.contains(schemaComponent);
//        boolean checkRecursion = false;
//        recursionStack.push(schemaComponent);
//
//        //Don't count when building steps
//        if(numberOfLevelsToBuild > -1){
//            nodeCount = 50;
//            return;
//        }
//
//        //Check the progressMonitor
//        if(this.monitor.isCanceled() ){
//            return;
//        }
//
//        //find resolvable schema ref for the given node
//        final XSDComponent ref = resolveSchemaRef(schemaComponent);
//        if(ref != null){
//            countNodes(ref, taskName, done);
//            if(!recursionStack.isEmpty() ) {
//                recursionStack.pop();
//                System.gc();
//                Thread.yield();
//            }
//            return;
//        }
//
//
//
//        //First add this node if it is an Element, Attribute, Namespace or a Comment
//        if(schemaComponent instanceof XSDElementDeclaration){
//            nodeCount++;
//            checkRecursion = true;
//            if (nodeCount % MONITOR_MOD_COUNT == 0) {
//                monitor.subTask(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.{0}___{1}_elements_found_7", taskName, new Integer(nodeCount) )); //$NON-NLS-1$
//                updateMonitor();
//            } // endif
//        }else if(schemaComponent instanceof XSDAttributeDeclaration){
//            nodeCount++;
//            if (nodeCount % MONITOR_MOD_COUNT == 0) {
//                monitor.subTask(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.{0}___{1}_elements_found_7", taskName, new Integer(nodeCount) )); //$NON-NLS-1$
//                updateMonitor();
//            } // endif
//        }else if(schemaComponent instanceof XSDParticle){
//            nodeCount++;
//            checkRecursion = true;
//            if (nodeCount % MONITOR_MOD_COUNT == 0) {
//                monitor.subTask(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.{0}___{1}_elements_found_7", taskName, new Integer(nodeCount) )); //$NON-NLS-1$
//                updateMonitor();
//            } // endif
//        }else if(schemaComponent instanceof XSDModelGroup ){
//            nodeCount++;
//            checkRecursion = isAllCompositor(schemaComponent);
//            if (nodeCount % MONITOR_MOD_COUNT == 0) {
//                monitor.subTask(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.{0}___{1}_elements_found_7", taskName, new Integer(nodeCount) )); //$NON-NLS-1$
//                updateMonitor();
//            } // endif
//        }
//
//
//        //Check for recursion
//        if(isRecursive && checkRecursion){
//            if(!recursionStack.isEmpty() ) {
//                recursionStack.pop();
//                System.gc();
//                Thread.yield();
//            }
//            return;
//        }
//
//
//
//        //Now add this Node's Children (like attributes) to the new Node (or this node if the schema element was not valid)
//        final Iterator children = schemaComponent.eContents().iterator();
//        while(children.hasNext() ){
//            final Object child = children.next();
//            if(child instanceof XSDComponent){
//                countNodes((XSDComponent)child, taskName, done);
//            }
//        }
//
//        // The element and attributes in the schema should have a type that is either
//        // a SimpleType or ComplexType; use that and build child objects according to the type
//        // However, DO NOT do this if the type is an anonymous type below the schema element,
//        // since children were handled in the previous block!
//        final XSDComponent typeComponent = XmlDocumentUtil.findXSDType(schemaComponent);
//        if ( typeComponent != null && typeComponent.eContainer() != null && !typeComponent.eContainer().equals(schemaComponent) ) {
//            countNodes(typeComponent, taskName, done);
//        }
//
//        if(!recursionStack.isEmpty() ) {
//            recursionStack.pop();
//            System.gc();
//            Thread.yield();
//        }
//    }

    /**
     * Update the monitor for a completed entity using factor computed during countNodes
     */
    private void updateMonitor() {
        if(factor > 0){
            monitor.worked(factor);
            return;
        }

        tempFactor--;
        if(tempFactor == factor){
            monitor.worked(1);
            tempFactor = 0;
        }

    }

    /**
     * Return the resolvable schema ref for the given schemaComponent - may be null
     * @param schemaComponent
     * @return the resolvable schema ref for the given schemaComponent - may be null
     */
    private XSDComponent resolveSchemaRef(final XSDComponent schemaComponent) {
        XSDComponent ref = null;
        if(schemaComponent instanceof XSDElementDeclaration){
            ref = ((XSDElementDeclaration)schemaComponent).getResolvedElementDeclaration();
        }else if(schemaComponent instanceof XSDModelGroupDefinition){
            ref = ((XSDModelGroupDefinition)schemaComponent).getResolvedModelGroupDefinition();
        }else if(schemaComponent instanceof XSDAttributeDeclaration){
            ref = ((XSDAttributeDeclaration)schemaComponent).getResolvedAttributeDeclaration();
        }else if(schemaComponent instanceof XSDAttributeGroupDefinition){
            ref = ((XSDAttributeGroupDefinition)schemaComponent).getResolvedAttributeGroupDefinition();
        }

        if(ref == null || ref == schemaComponent){
            return null;
        }

        return ref;
    }

    /**
     * @param node the root node to work from
     */
    private void initializeRecursionStack(final XmlElement root) {
        recursionStack = new Stack();
        addRecursionParentTypes(root);
    }

    /** This method insures that all ancestor elements are added to the
      * recursion stack.
      */
    private void addRecursionParentTypes(XmlBaseElement node) {
        // process parent first:
        XmlElementHolder parent = node.getParent();
        if (parent instanceof XmlBaseElement) {
            addRecursionParentTypes((XmlBaseElement) parent);
        } else if (parent instanceof XmlContainerNode) {
            // keep searching parents until we get another base element:
            XmlContainerHolder nextParent = ((XmlContainerNode)parent).getParent();
            while (nextParent != null) {
                if (nextParent instanceof XmlElement) {
                    addRecursionParentTypes((XmlElement) nextParent);
                    break; // we found a parent, don't need to process any further.
                } // endif
                nextParent = ((XmlContainerNode)parent).getParent();
            } // endwhile
        } // endif


        // process the type:
        XSDComponent schemaComponent = node.getXsdComponent();

        //Convert any schema element ref objects
        XSDComponent ref = resolveSchemaRef(schemaComponent);

        // add to stack:
        if(ref != null){
            recursionStack.push(ref);
        }else{
            recursionStack.push(schemaComponent);
        }
    }

    /**
      * Determine if a sequence if valid for reuse.  A sequence may be reused if the min / max occurs
      * are the same for it's parent and the potential resuse parent
      */
    private boolean isValidSequenceForReuse(final EObject sequence1, final EObject sequence2){
        if(sequence1 == null || sequence2 == null){
            return false;
        }

        if(sequence1 instanceof XSDModelGroup && sequence2 instanceof XSDModelGroup){
            final Object obj1 = sequence1.eContainer();
            final Object obj2 = sequence2.eContainer();
            boolean sameMult = false;
            if (obj1 instanceof XSDParticle && obj2 instanceof XSDParticle) {
                final XSDParticle p1 = (XSDParticle)sequence1.eContainer();
                final XSDParticle p2 = (XSDParticle)sequence2.eContainer();

                if(p1 != null && p2 != null){
                    int max1 = p1.getMaxOccurs();
                    int max2 = p2.getMaxOccurs();
                    boolean maxMatch = max1 == max2;

                    int min1 = p1.getMinOccurs();
                    int min2 = p2.getMinOccurs();
                    boolean minMatch = min1 == min2;

                    sameMult = minMatch && maxMatch;
                }

                //Case 3171: Do not reuse sequence if they are both a child of the same
                //choice node.
                boolean isChoice = false;
                if(sameMult) {
                    final EObject parent1 = p1.eContainer();
                    final EObject parent2 = p2.eContainer();
                    if(parent1 == parent2 && parent1 != null && parent1 instanceof XSDModelGroup) {
                        isChoice = ((XSDModelGroup)parent1).getCompositor().equals(XSDCompositor.CHOICE_LITERAL);
                    }

                    if(isChoice) {
                        return false;
                    }
                }

                return sameMult;
            }

        }

        return false;
    }

    /**
     * Recursively adds children of the given Schema Node to the given Document Node
     * @param docParent
     * @param xsdComponent
     * @param taskName
     */
    private void addChildren(final XmlDocumentEntity docParent, XSDComponent xsdComponent, final String taskName) {

        // Add appropriate namespace on document element if corresponding XSD component is global or has form = qualified
        if (docParent instanceof XmlElement) {
            final XmlElement elem = (XmlElement)docParent;
            final XSDComponent parentXsdComp = elem.getXsdComponent();
            if (parentXsdComp instanceof XSDFeature) {
                final XSDFeature feature = (XSDFeature)parentXsdComp;
                if (feature.isGlobal() || XSDForm.QUALIFIED_LITERAL.equals(feature.getForm())) {
                    final String uri = feature.getTargetNamespace();
                    if (uri != null) {
                        final XmlNamespace ns = (XmlNamespace)this.uriToNamespaceMap.get(uri);
                        if (ns != null) {
                            elem.setNamespace(ns);
                        }
                    }
                }
            }
        }

        //check numberOfLevelToBuild
        if(numberOfLevelsToBuild != -1){
            boolean proceed = checkDepth(docParent);
            if(!proceed){
                setBuildStatusProperty((XmlBuildable)docParent, BuildStatus.INCOMPLETE_LITERAL);
                return;
            }
        }

        //Check the progressMonitor
        if(this.monitor.isCanceled() ){
            return;
        }
//        if (nodeCount % MONITOR_MOD_COUNT == 0) {
//            if(nodeCount < MAX_COUNT  && estimatedNodeCount < MAX_COUNT) {
//                monitor.subTask(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.{0}_{1}_of_{2}", taskName, new Integer(nodeCount), new Integer(MAX_COUNT))); //$NON-NLS-1$
//            }else {
//                monitor.subTask(XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.{0}_{1}_of_{2}+", taskName, new Integer(nodeCount), new Integer(estimatedNodeCount))); //$NON-NLS-1$
//            }
//        } // endif

        //Convert any schema element ref  or substitution group objects
        final XSDComponent ref = resolveSchemaRef(xsdComponent);

        //look for a reusable documentFragment
        if(useFragments){
            final XmlFragment fragment = (ref == null ? (XmlFragment)fragmentMap.get(xsdComponent) : (XmlFragment)fragmentMap.get(ref) );
            if(fragment != null){
                if(docParent instanceof XmlElementHolder){
                    //create the XmlFragmentUse and set it's values from the xmlFragment
                    final XmlFragmentUse use = docFactory.createXmlFragmentUse();
                    use.setFragment(fragment);
                    use.setName(fragment.getName() );
                    use.setXsdComponent(ref == null ? xsdComponent : ref);
                    use.setNamespace(fragment.getRoot() == null ? null : fragment.getRoot().getNamespace() );

                    use.setParent( (XmlElementHolder)docParent );

                    //No need to process this node any further... just return;
                    return;
                }
                if(ModelerCore.DEBUG_XML){
                    System.out.println("Can't add docFragment to " + docParent.getClass().getName() ); //$NON-NLS-1$
                }
            }
        }

        //init recursion variables
        boolean isRecursive = false;
        if(ref != null){
            isRecursive = isRecursive(ref);
            recursionStack.push(ref);
        }else{
            isRecursive = isRecursive(xsdComponent);
            recursionStack.push(xsdComponent);
        }

        XmlDocumentEntity documentChild = docParent;
        if(xsdComponent instanceof XSDElementDeclaration && !((XSDElementDeclaration)xsdComponent).isAbstract() ){
            final XmlElement element = docFactory.createXmlElement();
            element.setXsdComponent(xsdComponent);
            if(docParent instanceof XmlContainerNode){
                // ((XmlContainerNode)docParent).getElements().add(element);
                addValueToList(docParent, element, ((XmlContainerNode)docParent).getElements());
            }else{
                if(ModelerCore.DEBUG_XML){
                    System.out.println("Can't add element to " + docParent.getClass().getName() ); //$NON-NLS-1$
                }
            }

            final XSDElementDeclaration xsdElement = (XSDElementDeclaration)xsdComponent;
            if(xsdElement.getLexicalValue() != null) {
                element.setValue(xsdElement.getLexicalValue() );
                final XSDConstraint constraint = xsdElement.getConstraint();
                if(XSDConstraint.DEFAULT_LITERAL.equals(constraint) ) {
                    element.setValueType(ValueType.DEFAULT_LITERAL);
                }else if(XSDConstraint.FIXED_LITERAL.equals(constraint) ) {
                    element.setValueType(ValueType.FIXED_LITERAL);
                }
            }

            documentChild = element;

            if(ref != null){
                xsdComponent = ref;
            }

            String name = ((XSDElementDeclaration)xsdComponent).getName();
            if(ref != null){
                name = ((XSDElementDeclaration)ref).getName();
            }

            final String newName = this.validator.createValidName(name);
            if (newName != null) {
                name = newName;
            }
            element.setName(name);

            nodeCount++;

            updateMonitor();
        }else if(xsdComponent instanceof XSDModelGroupDefinition || xsdComponent instanceof XSDAttributeGroupDefinition){
            if(ref != null){
                xsdComponent = ref;
            }

            updateMonitor();
        }else if(xsdComponent instanceof XSDAttributeDeclaration ){
            // Determine if the attribute is prohibited ...
            final boolean prohibited = isProhibited( (XSDAttributeDeclaration)xsdComponent);
            if ( prohibited ) {
                // Remove any existing sibling attribute with the same name ...
                removeChildrenOfSameName(docParent, XSDAttributeDeclaration.class, ((XSDAttributeDeclaration)xsdComponent).getName() );
            } else {
                final XmlAttribute attribute = docFactory.createXmlAttribute();
                attribute.setXsdComponent(xsdComponent);
                if(docParent instanceof XmlElement){
                    //((XmlElement)docParent).getAttributes().add(attribute);
                    addValueToList(docParent, attribute, ((XmlElement)docParent).getAttributes());
                }else{
                    if(ModelerCore.DEBUG_XML){
                        System.out.println("Can't add attribute to " + docParent.getClass().getName() ); //$NON-NLS-1$
                    }
                }
                documentChild = attribute;

                if(ref != null){
                    xsdComponent = ref;
                }

                String name = ((XSDAttributeDeclaration)xsdComponent).getName();
                if(ref != null){
                    name = ((XSDAttributeDeclaration)ref).getName();
                }
                final String newName = this.validator.createValidName(name);
                if (newName != null) {
                    name = newName;
                }
                attribute.setName(name);

                nodeCount++;
                updateMonitor();
            }
        }else if( isAllCompositor(xsdComponent) ){
            XmlAll temp = null;
            // Attempt to find an existing all (this occurs when the complex type that uses
            // an all extends another complex type that has an all);
            final Iterator iter = docParent.eContents().iterator();
            while (iter.hasNext()) {
                final Object sibling = iter.next();
                if ( sibling instanceof XmlAll ) {
                    temp = (XmlAll)sibling; // use this all component ..
                    break;
                }
            }
            // If no all sibling found, then create one ...
            if ( temp == null ) {
                temp = docFactory.createXmlAll();
                //((XmlContainerHolder)docParent).getContainers().add(temp);
                addValueToList(docParent, temp, ((XmlContainerHolder)docParent).getContainers());

                if(ref != null){
                    xsdComponent = ref;
                }

                nodeCount++;
                updateMonitor();
            }

            temp.setXsdComponent(xsdComponent);
            documentChild = temp;
        }else if( isChoiceCompositor(xsdComponent) ){
            final XmlChoice choice = docFactory.createXmlChoice();
            choice.setXsdComponent(xsdComponent);
            //((XmlContainerHolder)docParent).getContainers().add(choice);
            addValueToList(docParent, choice, ((XmlContainerHolder)docParent).getContainers());
            documentChild = choice;

            nodeCount++;
            updateMonitor();
        }else if( isSequenceCompositor(xsdComponent) ){
            XmlSequence temp = null;
            //If the current doc node is a sequence, just use this instead of creating a new one (defect 8489)
            if(documentChild instanceof XmlSequence ){
                if(isValidSequenceForReuse( ((XmlSequence)documentChild).getXsdComponent(), xsdComponent) ){
                    temp = (XmlSequence)documentChild;
                }
            }

            if(temp == null){
                // Attempt to find an existing sequence (this occurs when the complex type that uses
                // a sequence extends another complex type that has a sequence);
                final Iterator iter = docParent.eContents().iterator();
                while (iter.hasNext()) {
                    final Object sibling = iter.next();
                    if ( sibling instanceof XmlSequence ) {
                        //check the min / max occurs the the schema references
                        //if they match then use this sequence
                        if(isValidSequenceForReuse( ((XmlSequence)sibling).getXsdComponent(), xsdComponent) ){
                            temp = (XmlSequence)sibling; // use this sequence ..
                            documentChild = temp;
                            break;
                        }
                    }
                }

                // If no reusable sequence found, then create one ...
                if ( temp == null ) {
                    documentChild = docFactory.createXmlSequence();
                    //((XmlContainerHolder)docParent).getContainers().add(documentChild);
                    addValueToList(docParent, documentChild, ((XmlContainerHolder)docParent).getContainers());
                    nodeCount++;
                    updateMonitor();
                }

                ((XmlSequence)documentChild).setXsdComponent(xsdComponent);
            }
        }

        //debug
        else if (ModelerCore.DEBUG_XML){
            System.out.println("Found  unexpected " + xsdComponent.getClass().getName() ); //$NON-NLS-1$
        }

        ObjectID uuid = ModelerCore.getObjectId(documentChild);
        if(isRecursive){
            if(documentChild instanceof XmlElement){
                setRecursiveProperty( (XmlElement)documentChild, true );
                setBuildStatusProperty((XmlElement)documentChild, BuildStatus.INCOMPLETE_LITERAL);
            }

			if (recursiveElementUUIDs.contains(uuid)) {
                if(!recursionStack.isEmpty() ) {
                    recursionStack.pop();
                }

                return;
            }

            recursiveElementUUIDs.add(uuid);
            if(!recursionStack.isEmpty() ) {
                recursionStack.pop();
            }

            return;
        }

        //Add namespace for XSDComponent if required
        if(documentChild != docParent){
            updateSchemaUris(documentChild, xsdComponent);

            //Force GC
            if(nodeCount % 10000 == 0) {
                System.gc();
                Thread.yield();
            }
        }

        // The element and attributes in the schema should have a type that is either
        // a SimpleType or ComplexType; use that and build child objects according to the type
        // However, DO NOT do this if the type is an anonymous type below the schema element,
        // since children are handled below!
        final XSDComponent typeEntity = XmlDocumentUtil.findXSDType(xsdComponent);
        if ( typeEntity != null && typeEntity.eContainer() != null && !typeEntity.eContainer().equals(xsdComponent) ) {
            addChildren(documentChild, typeEntity, taskName);

            // re-check recursive status in case above call incorrectly set it:
            if (xsdComponent instanceof XSDComplexTypeDefinition
             && isRecursive(typeEntity)) {
                // xsdComponent is a complex type and it is not recursive (see earlier isRecursive check).
                //  Its super type is recursive, but only exact matches count.
                setRecursiveProperty((XmlElement)documentChild, false);
            } // endif
        }

        // make sure we are not marked recursive:
        if (!recursiveElementUUIDs.contains(uuid)) {
            //Now add this Node's Children (like attributes) to the new Node (or this node if the schema element was not valid)
            final Iterator children = xsdComponent.eContents().iterator();
            while (children.hasNext()) {
                final Object child = children.next();
                if (child instanceof XSDComponent) {
                    addChildren(documentChild, (XSDComponent) child, taskName);
                }
                // Child may be an XSDDiagnostic if it has been validated ...
            } // enwhile
        } // endif

        if(!recursionStack.isEmpty() ) {
            recursionStack.pop();
        }

    }

    private void setBuildStatusProperty(XmlBuildable xb, BuildStatus status) {
        xb.setBuildState(status);
    }

    private void setRecursiveProperty(final XmlElement element, boolean recursive){
        element.setRecursive(recursive);
        final XSDComponent xsdComponent = element.getXsdComponent();

        //now climb the tree and find the top recursive element
        EObject parent = element.eContainer();
        while(parent != null){
            if(parent instanceof XmlElement){
                if( ((XmlElement)parent).getXsdComponent() == xsdComponent){
                    ((XmlElement)parent).setRecursive(recursive);
                    return;
                }
            }

            parent = parent.eContainer();
        }
    }

    /**
     * @param xsdComponent
     * @return
     */
    private boolean isRecursive(final XSDComponent xsdComponent) {
        return this.recursionStack.contains(xsdComponent);
    }

    /**
      * return true if depth OK
      */
    private boolean checkDepth(final EObject child){
        final boolean isCompositor = child instanceof XmlAll || child instanceof XmlChoice || child instanceof XmlSequence;

        if(isCompositor || child == this.root){
            return true;
        }

        int depth = 1;
        EObject parent = child.eContainer();
        while(parent != null && parent != this.root){
            parent = parent.eContainer();
            depth++;
        }

        return depth < numberOfLevelsToBuild;
    }

    private boolean isAllCompositor(final XSDComponent xsdComponent){
        if(xsdComponent instanceof XSDModelGroup){
            return ( ((XSDModelGroup)xsdComponent).getCompositor().getValue() == XSDCompositor.ALL );
        }

        return false;
    }

    private boolean isSequenceCompositor(final XSDComponent xsdComponent){
        if(xsdComponent instanceof XSDModelGroup){
            return ( ((XSDModelGroup)xsdComponent).getCompositor().getValue() == XSDCompositor.SEQUENCE );
        }

        return false;
    }

    private boolean isChoiceCompositor(final XSDComponent xsdComponent){
        if(xsdComponent instanceof XSDModelGroup){
            return ( ((XSDModelGroup)xsdComponent).getCompositor().getValue() == XSDCompositor.CHOICE );
        }

        return false;
    }

    private boolean isProhibited(final XSDAttributeDeclaration attribute){
        ArgCheck.isNotNull(attribute);
        final XSDAttributeUse attUse = (XSDAttributeUse)attribute.eContainer();
        if(attUse == null){
            return false;
        }

        XSDAttributeUseCategory cat = attUse.getUse();
        if(cat == null){
            return false;
        }

        int val = cat.getValue();
        return val == XSDAttributeUseCategory.PROHIBITED;
    }

    private void removeChildrenOfSameName(final EObject parent, final Class clazz, String name){
        final String newName = this.validator.createValidName(name);
        if (newName != null) {
            name = newName;
        }
        final Iterator children = parent.eContents().iterator();
        while(children.hasNext() ){
            final EObject next = (EObject)children.next();
            if(clazz.isAssignableFrom(next.getClass() ) ){
                try {
                    final String name2 = ((XmlDocumentNode)next).getName();
                    if(name == null && name2 == null){
                        me.delete(next);
                    }else if(name != null && name.equals(name2) ){
                        me.delete(next);
                    }
                } catch (ModelerCoreException e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Error_removing_children_of_same_name_1")); //$NON-NLS-1$
                }
            }
        }
    }

    private void addValueToList(Object owner, Object value, EList feature) {
        try {
            ModelerCore.getModelEditor().addValue(owner, value, feature);
        } catch (ModelerCoreException err) {
            ModelerCore.Util.log(IStatus.ERROR, err, XmlDocumentPlugin.Util.getString("XmlDocumentBuilderImpl.Error_adding_object", value)); //$NON-NLS-1$
        }
    }
}
