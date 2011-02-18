/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.importer.rose.builder.RoseStrings;
import org.eclipse.emf.importer.rose.parser.RoseNode;
import org.eclipse.emf.importer.rose.parser.Util;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceVisitor;
import com.metamatrix.rose.internal.IMessage;
import com.metamatrix.rose.internal.IRoseConstants;
import com.metamatrix.rose.internal.IRoseHandler;
import com.metamatrix.rose.internal.IUnit;
import com.metamatrix.rose.internal.impl.AmbiguousReference;
import com.metamatrix.rose.internal.impl.Message;
import com.metamatrix.rose.internal.impl.Unit;

/**
 * This class provides the implementation common to all to all RoseHanders. In particular, it provides several maps that can be
 * used in the {@link #visitObject(org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode) visit}methods to store object
 * references that can be resolved later in {@link IRoseHandler#parsingFinished(List)}.
 * 
 * @since 4.1
 */
public abstract class AbstractRoseHandler implements IRoseConstants, IRoseHandler, CoreStringUtil.Constants {

    private static final char MULTIPLICITY_START_DELIMITER = '[';
    private static final char MULTIPLICITY_END_DELIMITER = ']';

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(AbstractRoseHandler.class);

    private static final String PARSING_NODE_MESSAGE_ID = "parsingNodeMessage"; //$NON-NLS-1$

    private static final String DATA_MODELER = "Data Modeler"; //$NON-NLS-1$
    static final String DEFAULT_SET = "default__"; //$NON-NLS-1$

    /**
     * @since 4.1
     */
    private static String getString( final String id,
                                     final Object parameter ) {
        return UTIL.getString(I18N_PREFIX + id, parameter);
    }

    private Map idMap, ownerMap, nameMap;

    private List unresolvedRefs;

    private List problems;

    private transient Unit unit;

    private transient List roots;

    private List ambiguousRefs;

    private List workspaceModels;

    private IProgressMonitor mon;

    private StringNameValidator nameValidator;

    /**
     * @since 4.1
     */
    protected AbstractRoseHandler() {
        initialize();
    }

    /**
     * Initializes the {@link Map}properties provided by this class.
     * 
     * @since 4.1
     */
    protected void initialize() {
        this.idMap = new HashMap();
        this.ownerMap = new HashMap();
        this.nameMap = new HashMap();
        this.unresolvedRefs = new ArrayList();
        this.problems = new ArrayList();
        this.ambiguousRefs = new ArrayList();
        this.nameValidator = new StringNameValidator();
        // Initialize workspace models
        final List models = new ArrayList();
        final ModelWorkspaceVisitor visitor = new ModelWorkspaceVisitor() {

            /**
             * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceVisitor#visit(com.metamatrix.modeler.core.workspace.ModelWorkspaceItem)
             * @since 4.1
             */
            public boolean visit( final ModelWorkspaceItem item ) throws ModelWorkspaceException {
                if (item instanceof ModelResource) {
                    final MetamodelDescriptor descriptor = ((ModelResource)item).getPrimaryMetamodelDescriptor();
                    if (descriptor != null && getPrimaryMetamodelUri().equals(descriptor.getNamespaceURI())) {
                        models.add(item);
                    }
                    return false;
                }
                return true;
            }
        };
        try {
            ModelerCore.getModelWorkspace().accept(visitor, ModelWorkspaceItem.DEPTH_INFINITE);
        } catch (final ModelWorkspaceException err) {
            UTIL.log(err);
        }
        this.workspaceModels = models;
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#cleanup()
     * @since 4.2.2
     */
    public void cleanup() {
        clear();
        this.workspaceModels.clear();
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#clear()
     * @since 4.1
     */
    public void clear() {
        this.problems.clear();
        this.ambiguousRefs.clear();
        this.idMap.clear();
        this.nameMap.clear();
        this.ownerMap.clear();
        this.unresolvedRefs.clear();
        this.unit = null;
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#getAmbiguousReferences()
     * @since 4.1
     */
    public final List getAmbiguousReferences() {
        return this.ambiguousRefs;
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#getProblems()
     * @since 4.1
     */
    public final List getProblems() {
        return Collections.unmodifiableList(this.problems);
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.rose.internal.IRoseHandler#parsingFinished(java.util.List)
     * @since 4.2
     */
    public void parsingFinished( final List rootsList ) {
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#parsingStarted(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.1
     */
    public void parsingStarting( final IProgressMonitor monitor ) {
        this.mon = monitor;
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.rose.internal.IRoseHandler#unitParsingFinished(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void unitParsingFinished( final IProgressMonitor monitor ) {
    }

    /**
     * @see com.metamatrix.rose.internal.IRoseHandler#unitParsingStarting(com.metamatrix.rose.internal.impl.Unit, java.util.List)
     * @since 4.2
     */
    public void unitParsingStarting( Unit unit,
                                     List roots ) {
        this.unit = unit;
        this.roots = roots;
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.emf.codegen.ecore.rose2ecore.RoseVisitor#visitList(org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode)
     * @since 4.1
     */
    public final void visitList( final RoseNode node ) {
    }

    /**
     * @see org.eclipse.emf.codegen.ecore.rose2ecore.RoseVisitor#visitObject(org.eclipse.emf.codegen.ecore.rose2ecore.parser.RoseNode)
     * @since 4.1
     */
    public final void visitObject( final RoseNode node ) {
        String key = node.getKey();
        String val = node.getValue();
        String type = Util.getType(val);
        String name = Util.getName(val);
        // Update monitor
        if (name != null && name.length() > 0) {
            getProgressMonitor().subTask(getString(PARSING_NODE_MESSAGE_ID, name));
        }
        Object elem = null;
        if (RoseStrings.CLASS_CATEGORY.equals(type) && EMPTY_STRING.equals(key)) {
            if (node.isLoaded()) {
                // Visit package
                elem = createPackage(name, node);
                // Save mapping from Rose node to EMF object
                node.setNode(elem);
            }
        } else if (RoseStrings.CLASS.equals(type)) {
            elem = createClass(name, node);
            if (elem != null) {
                // Save mapping from Rose node to EMF object
                node.setNode(elem);
                // Map Rose node's ID to type
                this.idMap.put(node.getRoseId(), elem);
                // Save classifier and name for use during type resolution in complete method
                final Map nameMap = getNameMap();
                List types = (List)nameMap.get(name);
                if (types == null) {
                    types = new ArrayList();
                    nameMap.put(name, types);
                }
                types.add(elem);
            }
        } else if (RoseStrings.CLASSATTRIBUTE.equals(type)) {
            // Get attribute's multiplicity
            String multiplicity;
            final int startNdx = name.indexOf(MULTIPLICITY_START_DELIMITER);
            final int endNdx = name.indexOf(MULTIPLICITY_END_DELIMITER, startNdx + 1);
            if (startNdx >= 0 && endNdx > startNdx) {
                // Get multiplicity from name
                multiplicity = name.substring(startNdx + 1, endNdx);
                name = name.substring(0, startNdx).trim();
            } else {
                // Get multiplicity from stereotype
                multiplicity = node.getStereotype();
            }
            elem = createClassAttribute(name, node, multiplicity);
            // Save mapping from Rose node to EMF object
            node.setNode(elem);
        } else if (RoseStrings.INHERITANCE_RELATIONSHIP.equals(type)) {
            createInheritanceRelationship(name, node);
        } else if (RoseStrings.ASSOCIATION.equals(type)) {
            // Save mapping from Rose node to EMF object
            elem = createAssociation(name, node);
            node.setNode(elem);
        } else if (RoseStrings.ROLE.equals(type)) {
            // Save mapping from Rose node to EMF object
            elem = createRole(name, node);
            node.setNode(elem);
            // Save Rose node for reference resolution in complete method
            this.unresolvedRefs.add(node);
        } else if (RoseStrings.ATTRIBUTE.equals(type)) {
            // Check if attribute is grandchild of "properties" node
            RoseNode grandParent = node.getParent();
            if (grandParent != null) {
                grandParent = grandParent.getParent();
            }
            if (grandParent != null) {
                if (RoseStrings.PROPERTIES.equals(grandParent.getKey())) {
                    final Map propMap = new HashMap();
                    name = null;
                    for (final Iterator childIter = node.getNodes().iterator(); childIter.hasNext();) {
                        final RoseNode child = (RoseNode)childIter.next();
                        key = child.getKey();
                        val = Util.trimQuotes(child.getValue());
                        if (RoseStrings.TOOL.equals(key)) {
                            if (DATA_MODELER.equals(val)) {
                                break;
                            }
                        } else if (RoseStrings.NAME.equals(key)) {
                            // Parse set and type from name
                            if (val.startsWith(DEFAULT_SET)) {
                                name = val;
                            }
                        } else if (RoseStrings.VALUE.equals(key)) {
                            // Map name/value pairs
                            for (final Iterator grandchildIter = child.getNodes().iterator(); grandchildIter.hasNext();) {
                                final RoseNode grandchild = (RoseNode)grandchildIter.next();
                                String prop = EMPTY_STRING;
                                for (final Iterator greatGrandchildIter = grandchild.getNodes().iterator(); greatGrandchildIter.hasNext();) {
                                    final RoseNode greatGrandchild = (RoseNode)greatGrandchildIter.next();
                                    key = greatGrandchild.getKey();
                                    if (RoseStrings.TOOL.equals(key)) {
                                        prop += '_' + Util.trimQuotes(greatGrandchild.getValue());
                                    } else if (RoseStrings.NAME.equals(key)) {
                                        prop += '_' + Util.trimQuotes(greatGrandchild.getValue());
                                    } else if (RoseStrings.VALUE.equals(key)) {
                                        val = greatGrandchild.getValue();
                                    }
                                }
                                final String validName = this.nameValidator.createValidName(prop);
                                if (validName != null) {
                                    prop = validName;
                                }
                                propMap.put(prop, val);
                            }
                        }
                    }
                    if (name != null) {
                        createAttribute(name, node, propMap);
                    }
                }
            }
        } else if (RoseStrings.OPERATION.equals(type)) {
            elem = createOperation(name, node);
            // Save mapping from Rose node to EMF object
            node.setNode(elem);
        }
        // Add description to object if available
        final EObject eObj = (EObject)elem;
        if (eObj != null && eObj.eResource() != null) {
            final String text = node.getDocumentation();
            if (text != null && text.length() > 0) {
                try {
                    final Annotation annotation = ModelerCore.getModelEditor().getAnnotation(eObj, true);
                    annotation.setDescription(text);
                } catch (final ModelerCoreException err) {
                    addProblem(IStatus.ERROR, err, elem);
                }
            }
        }
    }

    /**
     * Adds a new problem encountered during parsing, containing the specified attributes, to the {@link #getProblems() problems
     * list}.{@link IStatus#ERROR Errors}will be added to the top of the list.
     * 
     * @param severity One of the severities defined by {@link org.eclipse.core.runtime.IStatus}.
     * @param message A message describing the problem; never null.
     * @param object The object to which the problem pertains; may be null.
     * @since 4.1
     */
    protected final void addProblem( final int severity,
                                     final String message,
                                     final Object object ) {
        addProblem(severity, new Message(severity, message, object));
    }

    /**
     * Adds a new problem encountered during parsing, containing the specified attributes, to the {@link #getProblems() problems
     * list}.{@link IStatus#ERROR Errors}will be added to the top of the list.
     * 
     * @param severity One of the severities defined by {@link org.eclipse.core.runtime.IStatus}.
     * @param error The error that was the cause of the problem; never null.
     * @param object The object to which the problem pertains; may be null.
     * @since 4.1
     */
    protected final void addProblem( final int severity,
                                     final Throwable error,
                                     final Object object ) {
        addProblem(severity, new Message(severity, error, object));
    }

    /**
     * @return The models in the workspace with the same primary metamodel URI as is handled by this handler; never null,
     *         modifiable.
     * @since 4.1
     */
    protected List getWorkspaceModels() {
        return Collections.unmodifiableList(this.workspaceModels);
    }

    /**
     * @return The map of ID's to Teiid Designer model objects; never null, modifiable.
     * @since 4.1
     */
    protected final Map getIdMap() {
        return this.idMap;
    }

    /**
     * @return The map of unqualified object names to the list of Teiid Designer model objects with those names; never null,
     *         modifiable.
     * @since 4.1
     */
    protected final Map getNameMap() {
        return this.nameMap;
    }

    /**
     * @return The map of EMF ownable objects to owning Rose nodes; never null, modifiable.
     * @since 4.1
     */
    protected final Map getOwnerMap() {
        return this.ownerMap;
    }

    /**
     * @return The progress monitor to be updated by this handler.
     * @since 4.1
     */
    protected final IProgressMonitor getProgressMonitor() {
        return this.mon;
    }

    /**
     * @return The root objects of the target Teiid Designer model for the current Rose model being parsed; never null,
     *         modifiable.
     * @since 4.1
     */
    protected final List getRoots() {
        return this.roots;
    }

    /**
     * @return The IUnit being handled; never null.
     * @since 4.1
     */
    protected IUnit getUnit() {
        return this.unit;
    }

    /**
     * @return The list of Rose nodes containing unresolved references; never null, modifiable.
     * @since 4.1
     */
    protected final List getUnresolvedReferences() {
        return this.unresolvedRefs;
    }

    /**
     * Implemented by subclasses to create a Teiid Designer model object that represents the specified Rose Association node.
     * After this method is called, the Rose node's ID will be mapped to the returned object for later {@link #getIdMap() use}by
     * other create methods. The Rose node's "node" property will also be set to the returned object.
     * 
     * @param name The Rose node's name.
     * @param node A Rose Association node.
     * @return The newly created Teiid Designer model object that corresponds to the specified Rose node.
     * @since 4.1
     */
    protected abstract Object createAssociation( String name,
                                                 RoseNode node );

    /**
     * Implemented by subclasses to handle the specified Rose Attribute node.
     * 
     * @param name The Rose node's name.
     * @param node The Rose Attribute node.
     * @param propertyMap The map that applies to the specified node of Rose custom properties to their values.
     * @since 4.1
     */
    protected abstract void createAttribute( String name,
                                             RoseNode node,
                                             Map propertyMap );

    /**
     * Implemented by subclasses to create a Teiid Designer model object that represents the specified Rose Class node. After
     * this method is called, the Rose node's ID will be mapped to the returned object for later {@link #getIdMap() use}by other
     * create methods. The Rose node's "node" property will also be set to the returned object.
     * 
     * @param name The Rose node's name.
     * @param node A Rose Class node.
     * @return The newly created Teiid Designer model object that corresponds to the specified Rose node.
     * @since 4.1
     */
    protected abstract Object createClass( String name,
                                           RoseNode node );

    /**
     * Implemented by subclasses to create a Teiid Designer model object that represents the specified Rose Class Attribute
     * node. After this method is called, the Rose node's ID will be mapped to the returned object for later {@link #getIdMap()
     * use}by other create methods. The Rose node's "node" property will also be set to the returned object.
     * 
     * @param name The Rose node's name.
     * @param node The Rose Attribute node.
     * @param multiplicity The Rose Attribute's multiplicity.
     * @return The newly created Teiid Designer model object that corresponds to the specified Rose node.
     * @since 4.1
     */
    protected abstract Object createClassAttribute( String name,
                                                    RoseNode node,
                                                    String multiplicity );

    /**
     * Implemented by subclasses to create a Teiid Designer model object that represents the specified Rose Inheritance
     * Relationship node.
     * 
     * @param name The Rose node's name.
     * @param node A Rose Inheritance Relationship node.
     * @since 4.1
     */
    protected abstract void createInheritanceRelationship( String name,
                                                           RoseNode node );

    /**
     * Implemented by subclasses to create a Teiid Designer model object that represents the specified Rose Operation node.
     * 
     * @param name The Rose node's name.
     * @param node A Rose Operation node.
     * @return The newly created Teiid Designer model object that corresponds to the specified Rose node.
     * @since 4.1
     */
    protected abstract Object createOperation( String name,
                                               RoseNode node );

    /**
     * Must be implemented by subclasses to create a Teiid Designer model object that represents the specified Rose Package
     * node. The following steps will be performed <em>after</em> calling this method:
     * <ul>
     * <li>The Rose node's ID will be mapped to the returned object for later {@link #getIdMap() use}by other create methods.
     * <li>The Rose node's "node" property will be set to the returned object.
     * <li>The returned object will be added to the model's root contents.
     * </ul>
     * 
     * @param name The Rose node's name.
     * @param node A Rose Package node.
     * @return The newly created Teiid Designer model object that corresponds to the specified Rose node.
     * @since 4.1
     */
    protected abstract Object createPackage( String name,
                                             RoseNode node );

    /**
     * Must be implemented by subclasses to create a Teiid Designer model object that represents the specified Rose Role node.
     * 
     * @param name The Rose node's name.
     * @param node A Rose Role node.
     * @return The newly created Teiid Designer model object that corresponds to the specified Rose node.
     * @since 4.1
     */
    protected abstract Object createRole( String name,
                                          RoseNode node );

    /**
     * @param referencer
     * @param type
     * @param node
     * @param name
     * @param matches
     * @return Resolved reference
     * @since 4.1
     */
    protected Object resolveReferenceByName( final Object referencer,
                                             final String type,
                                             final RoseNode node,
                                             final String name,
                                             final List matches ) {
        if (matches == null || matches.isEmpty()) {
            // Unresolvable
            final Object obj = createMissingObject(type, referencer, node.getRoseRefId(), name);
            if (obj != null) {
                resolveReference(referencer, obj, type, node.getRoseId());
            }
            addProblem(IStatus.WARNING, getUnresolvableReferenceMessage(referencer, type, name), referencer);
        } else {
            if (matches.size() == 1) {
                // Resolve reference using matching type
                return matches.get(0);
            }
            this.ambiguousRefs.add(new AmbiguousReference(referencer, type, node, name, matches));
        }
        return null;
    }

    /**
     * @since 4.1
     */
    private void addProblem( final int severity,
                             final IMessage problem ) {
        if (severity == IStatus.ERROR) {
            this.problems.add(0, problem);
        } else {
            this.problems.add(problem);
        }
    }
}
