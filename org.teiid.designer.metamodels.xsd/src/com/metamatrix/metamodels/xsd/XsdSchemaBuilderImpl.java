/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConstraint;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * Build XSD models from a set of Relational Entities.
 */
public class XsdSchemaBuilderImpl {
    // Constants for creating entity names
    private static final String TYPE_SUFFIX = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.type"); //$NON-NLS-1$
    private static final String ROOT_SUFFIX = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.rootSuffix"); //$NON-NLS-1$
    private static final String INPUT_SUFFIX = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.inputSuffix"); //$NON-NLS-1$
    private static final String OUTPUT_SUFFIX = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.outputSuffix"); //$NON-NLS-1$
    //    private static final String XSD_EXTENSION = ".xsd"; //$NON-NLS-1$
    private static final String MM_URI = "http://www.metamatrix.com/"; //$NON-NLS-1$

    // Used to enable Unit Testing.
    public static boolean HEADLESS = false;

    private final XSDFactory factory = XSDFactory.eINSTANCE;
    private final XsdBuilderOptions ops;
    private final StringNameValidator nameValidator = new StringNameValidator();
    private final Collection addedNamespaces = new HashSet();
    private final Collection includedSchemas = new HashSet();
    private final Collection completedRoots = new HashSet();
    private final HashMap scoreMap = new HashMap();
    private final Collection outputRoots = new HashSet();
    private final boolean doFlat;

    private MultiStatus result;
    private XSDSchema reusableSchema;
    private XSDResourceImpl currentResource;
    private ModelResource modelResource;
    private Collection rootElements;
    private Collection rootElementNames;
    private Collection inputRootElements;
    private Collection inputRootElementNames;
    private HashMap outputToInputMap;
    private HashMap tableToOutputMap;
    private HashMap refMap;
    private boolean trackMappings = false;
    private int currentScore;

    /**
     * Public constructor
     */
    public XsdSchemaBuilderImpl( final XsdBuilderOptions ops ) {
        super();
        ArgCheck.isNotNull(ops);
        ArgCheck.isNotNull(ops.getRoots());
        this.ops = ops;
        this.doFlat = ops.isFlat();
    }

    /**
     * Static method to use for building XSDs
     * 
     * @param ops - Options to use
     * @param monitor - Progress Monitor
     * @return - The StringBuffer of messages for the user.
     */
    public static MultiStatus buildSchemas( final XsdBuilderOptions ops, // NO_UCD
                                            IProgressMonitor monitor ) {
        XsdSchemaBuilderImpl builder = new XsdSchemaBuilderImpl(ops);
        return builder.buildSchemas(monitor, null);
    }

    /**
     * Return the set of Root Elements
     */
    public Collection getRootElements() {
        if (this.rootElements == null) {
            this.rootElements = new ArrayList();
        }

        return this.rootElements;
    }

    /**
     * Return the set of Root Input Elements
     */
    public Collection getInputRootElements() {
        if (this.inputRootElements == null) {
            this.inputRootElements = new ArrayList();
        }

        return this.inputRootElements;
    }

    /**
     * Root method for building XSDs.
     * 
     * @param monitor
     * @return the StringBuffer containg messages to the user.
     */
    public MultiStatus buildSchemas( IProgressMonitor monitor,
                                     MultiStatus result ) {
        // Initialize Instance variables
        this.result = result == null ? new MultiStatus(XsdPlugin.PLUGIN_ID, 0,
                                                       XsdPlugin.Util.getString("XsdSchemaBuilderImpl.result"), null) : result; //$NON-NLS-1$
        rootElements = new ArrayList();
        rootElementNames = new ArrayList();
        inputRootElements = new ArrayList();
        inputRootElementNames = new ArrayList();
        outputToInputMap = new HashMap();
        tableToOutputMap = new HashMap();
        refMap = new HashMap();
        trackMappings = ops.genInput() && ops.genOutput();
        monitor = monitor == null ? new NullProgressMonitor() : monitor;

        // Intialize Txn if required
        final String txnDescr = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.genxsd"); //$NON-NLS-1$
        final boolean startedTxn = ModelerCore.startTxn(true, true, txnDescr, this);
        final Collection inputRoots = new ArrayList();

        try {
            Collection roots = null;
            // Process the roots. If flat, just get them from the Ops. Else,
            // Score the roots and re-order in the collection by score
            if (doFlat) {
                roots = ops.getRoots();
            } else {
                buildScoreMap(ops.getRoots());
                roots = buildScoredRootsCollection();
            }
            monitor.beginTask(XsdPlugin.Util.getString("XsdSchemaBuilderImpl.creating", roots.size()), roots.size()); //$NON-NLS-1$
            // Must have at least one root.
            if (roots.isEmpty()) {
                final String noRoots = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.noRoots"); //$NON-NLS-1$
                addStatus(IStatus.ERROR, noRoots, null);
            }

            // Ensure all staring objects are Tables or Procedure Results.
            validateRoots(roots);

            // Create the output model
            if (ops.genOutput()) {
                currentResource = createModel(false);
            }

            // Process all the roots, building the Output.xsd
            // and Capturing roots that need to be processed as inputs.
            final Iterator rootIt = roots.iterator();
            while (rootIt.hasNext()) {
                final RelationalEntity root = (RelationalEntity)rootIt.next();
                // Capture the Item that should be used for Inputs
                // exclude tables that do not have primary key or access pattern
                // and procedures that do not have input parameter
                if (root instanceof ProcedureResult) {
                    Procedure proc = ((ProcedureResult)root).getProcedure();
                    if (hasInputParameter(proc)) {
                        inputRoots.add(proc);
                    }

                } else if (root instanceof BaseTable) {
                    final BaseTable table = (BaseTable)root;
                    if (table.getPrimaryKey() != null) {
                        inputRoots.add(root);
                    } else if (!table.getAccessPatterns().isEmpty()) {
                        inputRoots.add(root);
                    }
                } else if (root instanceof Procedure) {
                    if (hasInputParameter((Procedure)root)) {
                        inputRoots.add(root);
                    }
                }

                if (ops.genOutput()) {
                    // Execute the build for the current root object
                    doBuild(currentResource, root, false);
                }

                // If we are done processing roots, do the save
                boolean doSave = ops.genOutput() && !rootIt.hasNext();
                try {
                    if (doSave && modelResource != null) {
                        modelResource.save(new NullProgressMonitor(), true); // Use the ModelResource to save (related to defect
                        // 13670)
                        modelResource.getEmfResource().setModified(false);
                    }
                } catch (Exception e) {
                    final String saveError = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.errSave", modelResource.getItemName()); //$NON-NLS-1$
                    addStatus(IStatus.ERROR, saveError, e);
                }
                monitor.worked(1);
            }

            // Reset the completed roots variable prior to processing inputs
            completedRoots.clear();

            // If we are creating input docs, execute that build.
            if (ops.genInput()) {
                createInputXsd(inputRoots);
            }

            // Cleanup all instance variables to release memory
            cleanup();

            return this.result;
        } catch (Exception e) {
            final String err = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.err1"); //$NON-NLS-1$
            addStatus(IStatus.ERROR, err, e);
            return this.result;
        } finally {
            // This Txn is not undoable and not significant. Always commit.
            if (startedTxn) {
                ModelerCore.commitTxn();
            }
        }

    }

    private void cleanup() {
        completedRoots.clear();
        currentResource = null;
        addedNamespaces.clear();
        includedSchemas.clear();
        outputRoots.clear();
        inputRootElementNames = null;
        inputRootElements = null;
        modelResource = null;
        reusableSchema = null;
        tableToOutputMap = null;
        refMap = null;
    }

    /*
     * Private method used to create the input / output xsd resource(s)
     */
    private XSDResourceImpl createModel( boolean isInput ) throws CoreException {
        final String parentLocation = ops.getParentPath();

        String rootName;
        String fileName;
        // Create the model name using the user prefs
        if (isInput) {
            rootName = ops.getInputModelName();
            fileName = parentLocation + File.separator + rootName;
        } else {
            rootName = ops.getOutputModelName();
            fileName = parentLocation + File.separator + rootName;
        }

        // check for existing file
        IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
        File check = new File(fileName);
        if (reusableSchema == null && check.exists()) {
            final String fileExists = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.nameExists", rootName, fileName); //$NON-NLS-1$
            addStatus(IStatus.ERROR, fileExists, null);
            return null;
        }

        IFile resource = wsroot.getFileForLocation(new Path(fileName));
        XSDResourceImpl rsrc = null;
        if (HEADLESS) {
            // Don't assume ModelResources so we can UnitTest
            Container cntr = ModelerCore.getModelContainer();
            rsrc = (XSDResourceImpl)cntr.getOrCreateResource(URI.createFileURI(fileName));
        } else if (reusableSchema != null) {
            // We are already working with a model, just get that model
            modelResource = ModelerCore.getModelWorkspace().findModelResource(resource);
            if (modelResource != null) {
                rsrc = (XSDResourceImpl)modelResource.getEmfResource();
            }
        } else {
            // No model created yet, so create one
            modelResource = ModelerCore.create(resource);
            if (modelResource != null) {
                // Add a status that the model was created.
                addStatus(IStatus.INFO, XsdPlugin.Util.getString("XsdSchemaBuilderImpl.newModel", resource.getName()), null); //$NON-NLS-1$
                rsrc = (XSDResourceImpl)modelResource.getEmfResource();
            }
        }

        return rsrc;
    }

    /*
     * Helper method to return true of the given procedure has input params
     */
    private boolean hasInputParameter( Procedure proc ) {
        final Iterator params = proc.getParameters().iterator();
        while (params.hasNext()) {
            final ProcedureParameter param = (ProcedureParameter)params.next();
            final DirectionKind dir = param.getDirection();
            if (dir.equals(DirectionKind.IN_LITERAL) || dir.equals(DirectionKind.INOUT_LITERAL)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Helper to add a new child status to the instance multistatus result
     */
    private void addStatus( final int severity,
                            final String msg,
                            final Throwable ex ) {
        result.add(new Status(severity, result.getPlugin(), 0, msg, ex));
    }

    /*
     * Create the input documents from the given Procedures using their input parameters
     * to build the document 
     */
    private void createInputXsd( final Collection inputRoots ) throws CoreException {
        // If no inputs, return
        if (inputRoots.isEmpty()) {
            return;
        }

        // Reset the reference map
        refMap.clear();

        // Reset the reusable schema instance variable
        reusableSchema = null;

        // Set the current resource to the newly created input xsd
        currentResource = createModel(true);

        // Process the input roots
        final Iterator rootIt = inputRoots.iterator();
        while (rootIt.hasNext()) {
            final RelationalEntity root = (RelationalEntity)rootIt.next();
            if (doFlat || outputRoots.contains(root)) {
                doBuild(currentResource, root, true);
            }
        }

        // Force save after setting these properties.
        try {
            if (modelResource != null) {
                modelResource.save(new NullProgressMonitor(), true); // Use the ModelResource to save (related to defect 13670)
                modelResource.getEmfResource().setModified(false);
            }
        } catch (Exception e) {
            final String saveError = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.errSave", modelResource.getItemName()); //$NON-NLS-1$
            addStatus(IStatus.ERROR, saveError, e);
        }

    }

    /*
     * Helper method to score the given roots for nested xsd creation.
     * Add one to the current score for each child base table that can be 
     * found by navigating fk - pk relationships
     */
    private void buildScoreMap( final Collection roots ) {
        final Iterator rootIt = roots.iterator();
        while (rootIt.hasNext()) {
            Object next = rootIt.next();
            currentScore = 0;
            scoreEntity(next);
            final Integer score = new Integer(currentScore);
            Collection entities = (Collection)scoreMap.get(score);
            if (entities == null) {
                entities = new HashSet();
                scoreMap.put(score, entities);
            }

            entities.add(next);
        }

    }

    /*
     * Build a new collection ordered by score so that we process entities
     * with highest score first.  This will produce the most likely intended
     * result for the user
     */
    private Collection buildScoredRootsCollection() {
        final List scoredRoots = new ArrayList();

        int count = 0;
        int score = 0;
        while (count < scoreMap.size()) {
            final Integer nextScore = new Integer(score++);
            final Collection nextRoots = (Collection)scoreMap.get(nextScore);
            if (nextRoots != null && !nextRoots.isEmpty()) {
                scoredRoots.addAll(0, nextRoots);
                count++;
            }
        }

        return scoredRoots;
    }

    /*
     * Recurse down through any base tables, adding to the current score
     * for any child tables that can be found by navigationg fk - pk relationships
     */
    private void scoreEntity( Object entity ) {
        if (entity instanceof BaseTable) {
            final HashSet processed = new HashSet();
            scoreTable((BaseTable)entity, processed);
        }
    }

    /*
     * Recurse down through the give base table, adding to the current score
     * for any child tables that can be found by navigationg fk - pk relationships
     */
    private void scoreTable( BaseTable table,
                             final HashSet processed ) {
        processed.add(table);

        final Iterator fks = table.getForeignKeys().iterator();
        while (fks.hasNext()) {
            final ForeignKey fk = (ForeignKey)fks.next();
            final UniqueKey pk = fk.getUniqueKey();
            final BaseTable bt = pk == null ? null : pk.getTable();
            if (!processed.contains(bt) && pk.getTable() != null) {
                currentScore++;
                scoreTable(pk.getTable(), processed);
            }
        }
    }

    /*
     * Execute the actual build for the given relational root entity
     */
    private void doBuild( final XSDResourceImpl rsrc,
                          final RelationalEntity root,
                          final boolean isInput ) {
        // If we don't have a resource, return
        if (rsrc == null) {
            return;
        }

        // If we are building nested xsd and we've already built this root as a child
        // somewhere else, don't rebuild it as a root entity
        if (isInput || !completedRoots.contains(root)) {
            buildEntities(root, new Stack(), isInput, null);
        }
    }

    /*
     * Create the xsd structure under the given type for the given relational entity
     */
    private void addStructure( final XSDComplexTypeDefinition type,
                               final RelationalEntity entity,
                               final boolean isInput ) {
        // Add an Element for each Column from the Table, PrimaryKey
        // Procedure Result, or AccessPattern or Input Parameter from the Procedure.

        final Collection inParams = new ArrayList();
        Collection atts = new ArrayList();
        if (entity instanceof BaseTable) {
            final BaseTable table = (BaseTable)entity;
            if (isInput) {
                // Use the Primary Key and AccessPatterns to build the Input Document
                final PrimaryKey pk = table.getPrimaryKey();
                if (pk != null) {
                    final Iterator pkCols = pk.getColumns().iterator();
                    while (pkCols.hasNext()) {
                        final Object nextCol = pkCols.next();
                        if (!atts.contains(nextCol)) {
                            atts.add(nextCol);
                        }
                    }
                }

                final Iterator accessPatterns = table.getAccessPatterns().iterator();
                while (accessPatterns.hasNext()) {
                    final AccessPattern ap = (AccessPattern)accessPatterns.next();
                    final Iterator apCols = ap.getColumns().iterator();
                    while (apCols.hasNext()) {
                        final Object nextCol = apCols.next();
                        if (!atts.contains(nextCol)) {
                            atts.add(nextCol);
                        }
                    }
                }
            } else {
                atts = new ArrayList(table.getColumns());
            }
        } else if (entity instanceof ProcedureResult) {
            final ProcedureResult procResult = (ProcedureResult)entity;
            if (result != null) {
                atts = new ArrayList(procResult.getColumns());
            }

            // Also add the procedure input params to the sequence
            final Procedure p = procResult.getProcedure();
            final Iterator params = p.getParameters().iterator();
            while (params.hasNext()) {
                final ProcedureParameter param = (ProcedureParameter)params.next();
                final DirectionKind dir = param.getDirection();
                if (dir.equals(DirectionKind.IN_LITERAL) || dir.equals(DirectionKind.INOUT_LITERAL)) {
                    atts.add(param);
                }
            }
        } else if (entity instanceof Procedure) {
            final Iterator params = ((Procedure)entity).getParameters().iterator();
            while (params.hasNext()) {
                final ProcedureParameter param = (ProcedureParameter)params.next();
                final DirectionKind dir = param.getDirection();
                if (isInput && dir.equals(DirectionKind.IN_LITERAL) || dir.equals(DirectionKind.INOUT_LITERAL)) {
                    atts.add(param);
                } else if (!isInput) {
                    // Add OUT params to the Element first and then the INs after as we assume
                    // During Doc generation that the out params are first.
                    if (dir.equals(DirectionKind.OUT_LITERAL) || dir.equals(DirectionKind.INOUT_LITERAL)) {
                        atts.add(param);
                    } else {
                        inParams.add(param);
                    }
                }
            }
        } else {
            final String msg = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.invalidRoot", entity.getClass().getName()); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, null);
        }

        atts.addAll(inParams);

        final Iterator attsIterator = atts.iterator();
        while (attsIterator.hasNext()) {
            final RelationalEntity att = (RelationalEntity)attsIterator.next();
            final XSDElementDeclaration next = factory.createXSDElementDeclaration();
            next.setName(att.getName());
            XSDTypeDefinition attType = null;
            if (att instanceof Column) {
                final Column col = (Column)att;
                attType = (XSDTypeDefinition)col.getType();
                final NullableType nullable = col.getNullable();
                next.setNillable(NullableType.NULLABLE_LITERAL == nullable);
            } else {
                final ProcedureParameter param = (ProcedureParameter)att;
                attType = (XSDTypeDefinition)param.getType();
                final NullableType nullable = param.getNullable();
                next.setNillable(NullableType.NULLABLE_LITERAL == nullable);
                final String defaultVal = param.getDefaultValue();
                if (defaultVal != null) {
                    next.setLexicalValue(defaultVal);
                    next.setConstraint(XSDConstraint.DEFAULT_LITERAL);
                }
            }

            // Add imports for the datatypes if required
            addImportForType(reusableSchema, attType);
            next.setTypeDefinition(attType);

            // Add the new element as a child of the complex type
            addChild(type, next, false);
        }
    }

    /*
     * Create the elements and complex types for the given relational element
     */
    private boolean buildEntities( final RelationalEntity entity,
                                   final Stack recursionStack,
                                   final boolean isInput,
                                   XSDComplexTypeDefinition parent ) {
        // Check to see if we are back to a starting point if building a loop of
        // inter-related fk - pk tables (nested xsd path only)
        if (recursionStack.contains(entity)) {
            return false;
        }

        // Add this entity to the recursion check and as a completed root entity
        recursionStack.push(entity);
        completedRoots.add(entity);

        String rootName = entity.getName();
        String uniqueName = null;
        if (isInput) {
            uniqueName = this.nameValidator.createValidUniqueName(rootName + INPUT_SUFFIX, inputRootElementNames);
        } else {
            uniqueName = this.nameValidator.createValidUniqueName(rootName + OUTPUT_SUFFIX, rootElementNames);
        }

        // if null, that means original name was unique and valid
        if (uniqueName == null) {
            if (isInput) {
                uniqueName = rootName + INPUT_SUFFIX;
            } else {
                uniqueName = rootName + OUTPUT_SUFFIX;
            }
        }

        // If a base table's primary key is referenced by more than one foreign key,
        // we need to build it as a global element that is referenced by all the
        // referencing foreign keys.
        boolean doRef = false;
        if (!isInput && !doFlat && (entity instanceof BaseTable)) {
            final BaseTable bt = (BaseTable)entity;
            if (bt.getPrimaryKey() != null && bt.getPrimaryKey().getForeignKeys().size() > 1) {
                doRef = true;

            }
        }

        // Create the new element. If there is a passed in parent, the element will be
        // added as a child of that complex type, else, it will be a global element
        final XSDElementDeclaration element = createElement(isInput, parent, entity, uniqueName);
        XSDElementDeclaration refd = null;
        if (doRef) {
            // Check to see if we've already built the global element
            refd = (XSDElementDeclaration)refMap.get(entity);
            if (refd != null) {
                // No need to do anything else, return
                element.setResolvedElementDeclaration(refd);
                return true;
            }

            // Referenced global element does not exist yet, create and populate it.
            refd = createElement(isInput, null, entity, uniqueName);
            refMap.put(entity, refd);
        }

        // Create the complex type
        XSDComplexTypeDefinition type = factory.createXSDComplexTypeDefinition();
        if (parent == null || doRef) {
            reusableSchema.getContents().add(type);
            // If this is going to be a ref set type on the referenced element
            if (refd != null) {
                refd.setTypeDefinition(type);
            } else {
                element.setTypeDefinition(type);
            }
            type.setName(uniqueName + TYPE_SUFFIX);
        } else {
            // This is not a global type, set it as an anonymous type
            element.setAnonymousTypeDefinition(type);
        }

        // Add the structure for the entity to the type
        addStructure(type, entity, isInput);

        // For the output XSD recurse through all primary key - foreign key relationships adding structure
        if (!isInput && !doFlat && entity instanceof BaseTable) {
            final BaseTable bt = (BaseTable)entity;
            final Iterator fks = bt.getForeignKeys().iterator();
            while (fks.hasNext()) {
                final ForeignKey fk = (ForeignKey)fks.next();
                if (fk.getUniqueKey() != null && fk.getUniqueKey().getTable() != null) {
                    final BaseTable child = fk.getUniqueKey().getTable();
                    // recursive call to build child element structure
                    final boolean added = buildEntities(child, recursionStack, isInput, type);
                    if (added && !recursionStack.isEmpty()) {
                        recursionStack.pop();
                    }
                }
            }
        }

        // If this is a reference and we had to create the referenced element,
        // set the resolved element declaration now.
        if (doRef && refd != null) {
            element.setResolvedElementDeclaration(refd);
        }

        return true;
    }

    /*
     * Helper to create an element under the given type for the given relational entity
     */
    private XSDElementDeclaration createElement( final boolean isInput,
                                                 final XSDComplexTypeDefinition parent,
                                                 final RelationalEntity entity,
                                                 final String uniqueName ) {
        // Init the reusable schema
        if (reusableSchema == null) {
            initReusableSchema(uniqueName);
        }
        XSDElementDeclaration element = factory.createXSDElementDeclaration();
        XSDElementDeclaration wrapper = null;
        if (isInput) {
            element.setName(uniqueName);
            if (parent == null) {
                // Parent == null means this is a root entity
                reusableSchema.getContents().add(element);
                inputRootElements.add(element);
                inputRootElementNames.add(uniqueName);

                final XSDElementDeclaration outGlobal = (XSDElementDeclaration)tableToOutputMap.get(entity);
                if (trackMappings && outGlobal != null) {
                    // Track mappings when building input, ouput AND XML
                    outputToInputMap.put(outGlobal, element);
                }
            } else {
                // Add as a child of the given parent
                addChild(parent, element, false);
            }
        } else {
            element.setName(uniqueName);
            // Create the Global Element and Wrapper for the output
            if (parent == null) {
                // Parent == null means this is a root entity...
                // Create a wrapper global element and anonymous type
                if (entity instanceof ProcedureResult) {
                    outputRoots.add(((ProcedureResult)entity).getProcedure());
                } else {
                    outputRoots.add(entity);
                }
                wrapper = factory.createXSDElementDeclaration();
                wrapper.setName(uniqueName + ROOT_SUFFIX);
                final XSDComplexTypeDefinition elementType = factory.createXSDComplexTypeDefinition();
                element.setAnonymousTypeDefinition(elementType);
                addChild(elementType, wrapper, true);

                reusableSchema.getContents().add(element);
                rootElements.add(element);

                // Capture the mapping between table and element to be used downstream when
                // creating xml view mappings
                if (trackMappings) {
                    // Track mappings when building input, output AND XML
                    if (entity instanceof ProcedureResult) {
                        // Use the proc instead of the result as the proc will be used
                        // as the root for generating the input.
                        final Procedure tmp = ((ProcedureResult)entity).getProcedure();
                        tableToOutputMap.put(tmp, element);
                    } else {
                        tableToOutputMap.put(entity, element);
                    }
                }
            } else {
                // Add as a child of the given parent
                addChild(parent, element, false);
            }
        }

        if (wrapper != null) {
            // Return the wrapper instead of the global as we want to build below the wrapper
            // NOT the global element
            return wrapper;
        }

        return element;

    }

    /*
     * Helper to add an element as a child of the given complex type
     */
    private void addChild( final XSDComplexTypeDefinition parent,
                           final XSDElementDeclaration element,
                           boolean isRoot ) {
        XSDParticle particle = null;
        XSDModelGroup sequence = null;
        if (parent.getContent() != null && parent.getContent() instanceof XSDParticle) {
            particle = (XSDParticle)parent.getContent();
            if (particle.getContent() instanceof XSDModelGroup) {
                sequence = (XSDModelGroup)particle.getContent();
            } else {
                sequence = factory.createXSDModelGroup();
                sequence.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
                particle.setContent(sequence);
            }
        } else {
            particle = factory.createXSDParticle();
            sequence = factory.createXSDModelGroup();
            sequence.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
            particle.setContent(sequence);
            parent.setContent(particle);
        }

        final XSDParticle holder = factory.createXSDParticle();
        holder.setContent(element);
        if (isRoot) {
            holder.setMaxOccurs(-1);
        } else {
            holder.setMaxOccurs(1);
        }
        holder.setMinOccurs(0);
        sequence.getContents().add(holder);
    }

    /*
     * Initialize the reusable schema for the current resource
     */
    private XSDSchema initReusableSchema( final String rootName ) {
        if (reusableSchema != null) {
            // reusableSchema.setIncrementalUpdate(false);
            return reusableSchema;
        }

        // Create the Schema
        reusableSchema = XSDFactory.eINSTANCE.createXSDSchema();
        currentResource.getContents().add(reusableSchema);
        Map map = reusableSchema.getQNamePrefixToNamespaceMap();
        String schemaForSchemaPrefixText = "xs"; //$NON-NLS-1$
        map.put(schemaForSchemaPrefixText, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        reusableSchema.setSchemaForSchemaQNamePrefix(schemaForSchemaPrefixText);
        this.includedSchemas.clear();
        this.addedNamespaces.clear();
        this.addedNamespaces.add(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        final String ns = MM_URI + rootName;
        reusableSchema.setTargetNamespace(ns);
        map.put(null, ns);
        // reusableSchema.setIncrementalUpdate(false);

        return reusableSchema;
    }

    /*
     * Update the XSD Imports if necessary 
     */
    private void addImportForType( final XSDSchema target,
                                   final XSDTypeDefinition type ) {
        if (target == null || type == null || type.getSchema() == null) {
            return;
        }

        final String ns = type.getTargetNamespace();
        try {
            if (addedNamespaces.contains(ns) || includedSchemas.contains(type.getSchema())) {
                return;
            }

            if (ns == null) {
                final XSDInclude xsdInclude = XSDFactory.eINSTANCE.createXSDInclude();
                xsdInclude.setSchemaLocation(type.eResource().getURI().toFileString());
                target.getContents().add(0, xsdInclude);
                includedSchemas.add(type.getSchema());
            } else {
                final XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
                xsdImport.setNamespace(ns);
                xsdImport.setSchemaLocation(ns);
                target.getContents().add(0, xsdImport);
                addedNamespaces.add(ns);
            }
        } catch (Exception err) {
            final String msg = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.importErr", ns); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, err);
        }

    }

    /*
     * All roots must be Tables or Procedure Results 
     */
    private void validateRoots( final Collection roots ) {
        if (roots == null || roots.isEmpty()) {
            return;
        }

        final Iterator rootIT = roots.iterator();
        while (rootIT.hasNext()) {
            final Object next = rootIT.next();
            if (!(next instanceof BaseTable) && !(next instanceof ProcedureResult) && !(next instanceof Procedure)) {
                final String invalidRoot = XsdPlugin.Util.getString("XsdSchemaBuilderImpl.invalidRoot", next.getClass().getName()); //$NON-NLS-1$
                throw new MetaMatrixRuntimeException(invalidRoot);
            }
        }
    }

    /**
     * Return a map of Output Global Element to Input Global Element. The map is keyed by the Output Global Element and the values
     * and keys are all XSDElementDeclarations.
     * 
     * @return
     * @since 4.3
     */
    public HashMap getOutputToInputMappings() {
        if (outputToInputMap == null) {
            outputToInputMap = new HashMap();
        }
        return outputToInputMap;
    }

}
