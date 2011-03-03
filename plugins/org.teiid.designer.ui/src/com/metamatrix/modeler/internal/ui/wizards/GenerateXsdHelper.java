/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;
import org.eclipse.xsd.XSDElementDeclaration;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.internal.xml.XmlDocumentBuilderImpl;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xsd.XsdBuilderOptions;
import com.metamatrix.metamodels.xsd.XsdSchemaBuilderImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.mapping.factory.MappingClassFactory;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.transformation.util.AttributeMappingHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.webservice.util.WebServiceBuildOptions;

/**
 * Helper class used by the Generate Web Service Schemas Wizard
 */
public class GenerateXsdHelper {
    public static boolean HEADLESS = false;

    private static final String DOC_SUFFIX = UiConstants.Util.getString("GenerateXsdWizard.docSuffix"); //$NON-NLS-1$
    private static final String OUTPUT_SUFFIX = UiConstants.Util.getString("GenerateXsdWizard.outSuffix"); //$NON-NLS-1$
    private static final String INSTANCE_SUFFIX = UiConstants.Util.getString("GenerateXsdWizard.instanceSuffix"); //$NON-NLS-1$
    private static final String NESTED_SUFFIX = UiConstants.Util.getString("GenerateXsdWizard.nestedSuffix"); //$NON-NLS-1$
    private static final String IN_MSG = UiConstants.Util.getString("GenerateXsdWizard.inMsg"); //$NON-NLS-1$
    private static final String OUT_MSG = UiConstants.Util.getString("GenerateXsdWizard.outMsg"); //$NON-NLS-1$
    private static final String DOCUMENT_SUFFIX = UiConstants.Util.getString("GenerateXsdWizard.documentSuffix"); //$NON-NLS-1$
    private static final String GET_PREFIX = UiConstants.Util.getString("GenerateXsdWizard.getPrefix"); //$NON-NLS-1$
    private static final String XSD_EXTENSION = ModelUtilities.DOT_XSD_FILE_EXTENSION;
    private static final String XMI_EXTENSION = ModelUtilities.DOT_MODEL_FILE_EXTENSION;

    private final XsdSchemaBuilderImpl xsdBuilder;
    private final XsdBuilderOptions ops;
    private final MultiStatus result;
    private final PluginUtil Util = UiConstants.Util;

    private MtkXmiResourceImpl xmlResource;
    private ModelResource xmlModelResource;

    // Map of output XmlDocuments to input global Elements
    private final HashMap outputToInputMappings = new HashMap();

    private IProgressMonitor monitor;

    public GenerateXsdHelper( final XsdSchemaBuilderImpl xsdBuilder,
                              final XsdBuilderOptions ops,
                              final MultiStatus result ) {
        CoreArgCheck.isNotNull(xsdBuilder);
        CoreArgCheck.isNotNull(ops);

        this.xsdBuilder = xsdBuilder;
        this.ops = ops;
        this.result = result == null ? new MultiStatus(UiConstants.PLUGIN_ID, 0, Util.getString("GenerateXsdWizard.result"), null) : result; //$NON-NLS-1$
    }

    public HashMap execute( final IProgressMonitor monitor ) {
        this.monitor = monitor == null ? new NullProgressMonitor() : monitor;

        // Don't save if we still have to generate the SQL.
        final boolean genSql = ops.genSql();
        final boolean genWS = ops.genWs();
        final boolean buildXml = ops.genXml();
        final boolean doSave = !genSql && !genWS;

        // If the user elected to build XML, execute that build.
        if (buildXml) {
            HashMap xsdMappings = xsdBuilder.getOutputToInputMappings();
            buildDocuments(ops, xsdBuilder.getRootElements(), xsdMappings, doSave);
        }

        // Update the monitor
        monitor.worked(1);

        // If the user elected to generate default SQL, generate that SQL for all mapping classes
        if (genSql) {
            generateSql(ops);
        }

        return outputToInputMappings;
    }

    // ******************** Helper methods for building XML ********************

    /**
     * This method will build XML Document models using the builder options to determine whether to build many models with one doc
     * each or a single model with many docs.
     * 
     * @param ops - XsdBuilderOptions used to determine how to build documents
     * @param roots - the XsdElementDefinitions to use as root elements
     * @param xsdMappings = the output Xsd Global Element to input Xsd Global Element mappings
     * @param doSave - the flag to specify whether Resources should be saved. Should be true if will not be saved by subsequent
     *        operation.
     */
    private void buildDocuments( final XsdBuilderOptions ops,
                                 final Collection roots,
                                 final HashMap xsdMappings,
                                 final boolean doSave ) {
        if (roots == null || roots.isEmpty()) {
            return;
        }

        final boolean genWs = ops.genWs();
        monitor.beginTask(Util.getString("GenerateXsdWizard.buildingDocs", roots.size()), roots.size()); //$NON-NLS-1$

        // Initialize the XmlDocumentBuilder
        XmlDocumentBuilderImpl builder = new XmlDocumentBuilderImpl();
        XmlDocumentFactory factory = XmlDocumentFactory.eINSTANCE;

        for (Iterator iter = roots.iterator(); iter.hasNext();) {
            final XSDElementDeclaration schemaRootElement = (XSDElementDeclaration)iter.next();
            if (xmlResource == null) {
                // initialize the resource if we have not alrady done so:
                initDocResources(schemaRootElement, ops);
            }

            final Object inputGlobalElement = xsdMappings.get(schemaRootElement);
            try {
                XmlDocument document = factory.createXmlDocument();
                document.setName(schemaRootElement.getName() + DOCUMENT_SUFFIX);
                if (xmlResource != null) {
                    xmlResource.getContents().add(document);
                }

                // Create the output document to input global element mapping
                if (genWs) {
                    outputToInputMappings.put(document, inputGlobalElement);
                }

                // Create the document root.
                XmlRoot docRoot = factory.createXmlRoot();
                docRoot.setName(schemaRootElement.getName());
                docRoot.setXsdComponent(schemaRootElement);
                document.setRoot(docRoot);

                // Pass the root into the builder and execute the build
                builder.buildDocument(docRoot, monitor);
                if (!HEADLESS) {
                    // Build the mapping classes
                    final ITreeToRelationalMapper mapper = ModelMapperFactory.createModelMapper(document);
                    new MappingClassFactory(mapper).generateMappingClasses(docRoot,
                                                                           MappingClassBuilderStrategy.compositorStrategy,
                                                                           true);
                }
            } catch (Exception e) {
                final String msg = Util.getString("GenerateXsdWizard.errBuild"); //$NON-NLS-1$
                addStatus(IStatus.ERROR, msg, e);
            }
        }

        if (doSave) {
            doSave();
        }
    }

    private void addStatus( final int severity,
                            final String msg,
                            final Throwable ex ) {
        result.add(new Status(severity, result.getPlugin(), 0, msg, ex));
    }

    /**
     * Method to actually create the ModelResoruce (Resource in headless mode) for a XMLDocument model
     * 
     * @param element - The XSDElementDeclaration to use as context.
     * @param ops - The XsdBuilderOptions object to use for creation options
     */
    private void initDocResources( final XSDElementDeclaration element,
                                   final XsdBuilderOptions ops ) {
        final String parentLocation = ops.getParentPath();
        String mdlName = null;
        String fileName;
        if (ops.genOutput()) {
            // Use the model name specified in the Options
            mdlName = ops.getOutputModelName();
            mdlName = mdlName.replaceAll(XSD_EXTENSION, new String());
            fileName = parentLocation + File.separator + mdlName + DOC_SUFFIX + XMI_EXTENSION;
        } else {
            // Use the Element name as the model name
            mdlName = element.getName();
            fileName = parentLocation + File.separator + element.getName() + DOC_SUFFIX + XMI_EXTENSION;
        }

        // create for existing file
        IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
        File check = new File(fileName);
        if (check.exists()) {
            final String fileExists = Util.getString("GenerateXsdWizard.docExists", fileName); //$NON-NLS-1$
            addStatus(IStatus.ERROR, fileExists, null);
            return;
        }

        if (HEADLESS) {
            // Don't assume ModelResources so we can UnitTest
            try {
                Container cntr = ModelerCore.getModelContainer();
                xmlResource = (MtkXmiResourceImpl)cntr.getOrCreateResource(URI.createFileURI(fileName));
            } catch (Exception err) {
                addStatus(IStatus.ERROR, err.getMessage(), err);
            }
        } else {
            IFile resource = wsroot.getFileForLocation(new Path(fileName));

            // Create the ModelResource
            xmlModelResource = ModelerCore.create(resource);
            if (xmlModelResource != null) {
                try {
                    // Add a status that the model was created.
                    addStatus(IStatus.INFO, Util.getString("GenerateXsdWizard.newModel", resource.getName()), null); //$NON-NLS-1$

                    xmlResource = (MtkXmiResourceImpl)xmlModelResource.getEmfResource();
                } catch (ModelerCoreException err) {
                    Util.log(IStatus.ERROR, err, err.getMessage());
                }
            }
        }

        if (xmlResource != null) {
            try {
                // Set the ModelAnnotation info.
                final ModelAnnotation ma = xmlModelResource.getModelAnnotation();
                ma.setPrimaryMetamodelUri(XmlDocumentPackage.eNS_URI);
                ma.setModelType(ModelType.VIRTUAL_LITERAL);
            } catch (ModelerCoreException err) {
                final String msg = Util.getString("GenerateXsdWizard.errModelImport"); //$NON-NLS-1$
                addStatus(IStatus.ERROR, msg, err);
            }
        }
    }

    // ******************** Helper methods for generating default SQL ********************

    /**
     * Root method to drive the creation of default SQL in the XmlDoc mapping class transformatins.
     * 
     * @param docModels - Collection of XmlDocument model Model Resources
     * @param ops - The XsdBuilderOptions to use to drive generation options
     * @param messages - The StringBuffer to use for user messages.
     */
    private void generateSql( final XsdBuilderOptions ops ) {
        final boolean genWs = ops.genWs();

        // Build name map to expidte root lookup by Name
        final HashMap nameMap = buildRootNameMap(ops);
        final Collection mappingRoots = getMappingRoots();
        if (nameMap.isEmpty() || mappingRoots.isEmpty()) {
            return;
        }

        // Initialize the Progress Monitor
        this.monitor.beginTask(Util.getString("GenerateXsdWizard.genSqlSts", mappingRoots.size()), mappingRoots.size()); //$NON-NLS-1$

        final Iterator mappingRootIt = mappingRoots.iterator();
        while (mappingRootIt.hasNext()) {
            final TransformationMappingRoot nextRoot = (TransformationMappingRoot)mappingRootIt.next();
            final EObject target = nextRoot.getTarget();
            if (target instanceof MappingClass) {
                // Find the corresponding source for the MappingClass target
                final MappingClass mc = (MappingClass)target;
                RelationalEntity source = (RelationalEntity)nameMap.get(mc.getName());
                if (source == null) {
                    // No exact match found, try more generic search
                    source = deepSearchNameMap(nameMap, mc.getName());
                }
                if (source instanceof ProcedureResult) {
                    TransformationHelper.createTransformation(target, ((ProcedureResult)source).getProcedure());
                } else {
                    TransformationHelper.createTransformation(target, source);
                }
                generateSql(mc, source, nextRoot);
            }
        }

        if (!genWs) {
            doSave();
        }
    }

    /*
     * No exact match found in name map, look for closest match.  This would occur if two
     * mapping classes had the same name the name validator would result in the second getting
     * a unique name with an integer appended.
     * 
     * Search the name map keys and look for the first match where the mapping class name begins
     * with the relational entity name.
     */
    private RelationalEntity deepSearchNameMap( final HashMap nameMap,
                                                final String mcName ) {
        if (mcName == null || nameMap == null) {
            return null;
        }

        final Iterator keys = nameMap.keySet().iterator();
        String match = null;
        while (match == null && keys.hasNext()) {
            final String next = (String)keys.next();
            if (mcName.startsWith(next)) {
                match = next;
            }
        }

        if (match != null) {
            return (RelationalEntity)nameMap.get(match);
        }

        return null;
    }

    public void doSave() {
        if (xmlModelResource != null) {
            try {
                xmlModelResource.save(this.monitor, true);
                xmlModelResource.getEmfResource().setModified(false);
            } catch (Exception e) {
                final String saveError = Util.getString("GenerateXsdWizard.errSave"); //$NON-NLS-1$
                addStatus(IStatus.ERROR, saveError, e);
            }
            this.monitor.worked(1);
        }
    }

    /**
     * Check to see if all the input / output datatypes match. If not, we need to create custom SQL.
     * 
     * @param mc - The MappingClass target
     * @param source - The Source of the transformation
     * @param root - The TransformationMappingRoot for the given transformation
     */
    private void generateSql( final MappingClass mc,
                              final RelationalEntity source,
                              final TransformationMappingRoot root ) {
        // Create defaultSql to use in the event of any exceptions
        final String defaultSql = "SELECT * FROM "; //$NON-NLS-1$
        final StringBuffer newSql = new StringBuffer(defaultSql);
        final Collection mappingsForProcParams = new ArrayList();

        try {
            final Iterator mcCols = mc.getColumns().iterator();
            int index = 0;
            int outCount = 0;
            boolean isProc = false;

            Procedure proc = null;
            final List outCols = getOutParams(source);
            while (mcCols.hasNext()) {
                final MappingClassColumn next = (MappingClassColumn)mcCols.next();
                if (source instanceof ProcedureResult) {
                    final ProcedureResult procResult = (ProcedureResult)source;
                    isProc = true;
                    proc = procResult.getProcedure();
                    if (index >= procResult.getColumns().size()) {
                        // This is a procedure parameter mark the XMLElement as exclude from doc
                        mappingsForProcParams.add(next);
                    }
                } else if (source instanceof Procedure) {
                    isProc = true;
                    proc = (Procedure)source;
                    if (outCount++ >= outCols.size()) {
                        // This is a procedure parameter mark the XMLElement as exclude from doc
                        mappingsForProcParams.add(next);
                    }
                }

                index++;

            }

            newSql.append(TransformationHelper.getSqlEObjectFullName(source));

            // Handle the input sets for the non-flat scenario
            if (!ops.isFlat()) {
                final MappingClassSet mcs = mc.getMappingClassSet();
                final EObject target = mcs == null ? null : mcs.getTarget();
                if (target instanceof XmlDocument) {
                    final XmlDocument doc = (XmlDocument)target;
                    final TreeMappingAdapter tma = new TreeMappingAdapter(doc);
                    final List parents = tma.getParentMappingClasses(mc, doc, false);
                    if (parents.size() > 0) {
                        final Collection cols = getInputColumns(parents, source);
                        addInputColsAndCriteria(cols, mc, newSql, source);
                    }

                }
            }

            // Update the XML Elements for all the outPut Procedure Parameters
            updateElementsForParams(mappingsForProcParams, root);

            TransformationHelper.setSelectSqlString(root, newSql.toString(), false, this);
            AttributeMappingHelper.updateAttributeMappings(root, this);
        } catch (Exception e) {
            // Use the default SQL and log msg.
            final String msg = Util.getString("GenerateXsdWizard.errSql"); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, e);
            TransformationHelper.setSelectSqlString(root, defaultSql, false, this);
        }
    }

    // ******************** Utility and Helper Methods ********************

    private void addInputColsAndCriteria( final Collection inputCols,
                                          final MappingClass mc,
                                          final StringBuffer sql,
                                          final RelationalEntity source ) {
        final InputSet is = mc.getInputSet();
        final Iterator cols = inputCols.iterator();
        final StringBuffer crit = new StringBuffer();
        if (cols.hasNext()) {
            crit.append(" where "); //$NON-NLS-1$
        }

        int colCount = 0;
        while (cols.hasNext()) {
            final MappingClassColumn nextCol = (MappingClassColumn)cols.next();
            final String colName = nextCol.getName();

            // Create the inputParam
            final InputParameter nxt = TransformationFactory.eINSTANCE.createInputParameter();
            nxt.setName(colName);
            nxt.setType(nextCol.getType());
            is.getInputParameters().add(nxt);

            // Create the input binding
            final InputBinding binding = TransformationFactory.eINSTANCE.createInputBinding();
            binding.setInputParameter(nxt);
            binding.setMappingClassColumn(nextCol);
            binding.setMappingClassSet(mc.getMappingClassSet());

            // Append the criteria
            crit.append("input."); //$NON-NLS-1$
            crit.append(colName);
            crit.append("="); //$NON-NLS-1$
            crit.append(TransformationHelper.getSqlEObjectFullName(source));
            crit.append("."); //$NON-NLS-1$             
            crit.append(getPKColName(source, colCount, colName));
            colCount++;

            if (cols.hasNext()) {
                crit.append(" and "); //$NON-NLS-1$
            }

        } // while

        if (colCount > 1) {
            // Log a warning that assumptions where made creating Criteria
            final String msg = UiConstants.Util.getString("GenerateXsdHelper.criteriaAssumptions", mc.getName()); //$NON-NLS-1$
            addStatus(IStatus.WARNING, msg, null);
        }

        if (crit.length() > 0) {
            sql.append(crit.toString());
        }
    }

    private String getPKColName( final RelationalEntity source,
                                 final int index,
                                 final String defaultName ) {
        if (source instanceof BaseTable && ((BaseTable)source).getPrimaryKey() != null) {
            final List pkCols = ((BaseTable)source).getPrimaryKey().getColumns();
            if (pkCols.size() > index) {
                final Column col = (Column)pkCols.get(index);
                return col.getName();
            }
        }

        // Could not find a matching PK column for the given source and index
        return defaultName;
    }

    private Collection getInputColumns( final List mcs,
                                        final RelationalEntity source ) {
        for (int i = mcs.size(); i > 0; i--) {
            final MappingClass mc = (MappingClass)mcs.get(i - 1);
            final Collection result = getInputColumns(mc, source);
            if (result.size() != 0) {
                return result;
            }

        }

        return new ArrayList();
    }

    /*
     * For the given Mapping class find the set of Columns that should be used to create input set
     * columns by traversing the PK -> FK relationship on the given relatinal entity (if a table).
     * This is in effect climbing up one level on the relational side that is used to build the
     * XML.
     */
    private Collection getInputColumns( final MappingClass mc,
                                        final RelationalEntity source ) {
        final Collection result = new ArrayList();
        if (source instanceof BaseTable) {
            // Only possible for Tables... Find the PK -> FK relationship
            final PrimaryKey pk = ((BaseTable)source).getPrimaryKey();
            if (pk != null && !pk.getForeignKeys().isEmpty()) {
                // If the FK column names exist on the Mapping class, include them for
                // Input Set Col creation
                final Iterator fks = pk.getForeignKeys().iterator();
                while (fks.hasNext()) {
                    final ForeignKey nxtFK = (ForeignKey)fks.next();
                    final Collection fkColNames = getFKColNames(nxtFK);
                    final Iterator cols = mc.getColumns().iterator();
                    // Only use the names if all columns match
                    while (cols.hasNext()) {
                        final MappingClassColumn col = (MappingClassColumn)cols.next();
                        final String name = col.getName();
                        if (fkColNames.contains(name)) {
                            result.add(col);
                            // We've found a matching column for every FK col... we are done
                            if (result.size() == fkColNames.size()) {
                                return result;
                            }
                        }
                    }

                    // We didn't find a match for every FK Col... clear the list and try the next FK
                    result.clear();
                }
            }
        }

        return result;
    }

    final Collection getFKColNames( final ForeignKey fk ) {
        final HashSet result = new HashSet();
        final Iterator cols = fk.getColumns().iterator();
        while (cols.hasNext()) {
            final Column col = (Column)cols.next();
            result.add(col.getName());
        } // while

        return result;
    }

    /**
     * Build a map of names to Physical sources
     * 
     * @param ops
     * @return the map of names to Physical sources
     * @since 4.1
     */
    private HashMap buildRootNameMap( final XsdBuilderOptions ops ) {
        final Collection roots = ops.getRoots();
        if (roots == null || roots.isEmpty()) {
            return new HashMap();
        }

        final StringNameValidator nameValidator = new StringNameValidator();
        final Collection names = new ArrayList();
        final HashMap nameMap = new HashMap(roots.size());
        final Iterator rootIt = roots.iterator();
        while (rootIt.hasNext()) {
            RelationalEntity next = (RelationalEntity)rootIt.next();

            final String nestedName = next.getName() + NESTED_SUFFIX;
            final String globalName = next.getName() + INSTANCE_SUFFIX;

            String nestedUniqueName = nameValidator.createValidUniqueName(nestedName, names);
            if (nestedUniqueName == null) {
                nestedUniqueName = nestedName;
            }

            names.add(nestedUniqueName);
            nameMap.put(nestedUniqueName, next);

            String globalUniqueName = nameValidator.createValidUniqueName(globalName, names);
            if (globalUniqueName == null) {
                globalUniqueName = globalName;
            }

            names.add(globalUniqueName);
            nameMap.put(globalUniqueName, next);

            if (ops.genInput() && next instanceof ProcedureResult) {
                final Procedure proc = ((ProcedureResult)next).getProcedure();
                nameMap.put(proc.getName(), proc);
            }
        }

        return nameMap;
    }

    private List getOutParams( final RelationalEntity entity ) {
        final List result = new ArrayList();
        if (entity instanceof Procedure) {
            final Procedure proc = (Procedure)entity;
            final Iterator params = proc.getParameters().iterator();
            while (params.hasNext()) {
                final ProcedureParameter next = (ProcedureParameter)params.next();
                final DirectionKind dir = next.getDirection();
                if (dir == DirectionKind.OUT_LITERAL || dir == DirectionKind.INOUT_LITERAL) {
                    result.add(next);
                }
            }
        }

        return result;

    }

    private List getInParams( final RelationalEntity entity ) {
        final List result = new ArrayList();
        if (entity instanceof Procedure) {
            final Procedure proc = (Procedure)entity;
            final Iterator params = proc.getParameters().iterator();
            while (params.hasNext()) {
                final ProcedureParameter next = (ProcedureParameter)params.next();
                final DirectionKind dir = next.getDirection();
                if (dir == DirectionKind.IN_LITERAL || dir == DirectionKind.INOUT_LITERAL) {
                    result.add(next);
                }
            }
        }

        return result;

    }

    /**
     * Mark all XML Elements mapped to Procedure Params as "Exclude From Document
     * 
     * @param theMappingColumns
     * @param root
     */
    private void updateElementsForParams( final Collection theMappingColumns,
                                          final MappingRoot root ) {
        final MappingRoot treeRoot = getTreeMappingRoot(root);
        if (treeRoot == null) {
            return;
        }

        List columnMappings = treeRoot.getNested();

        if (!columnMappings.isEmpty()) {
            for (int size = columnMappings.size(), i = 0; i < size; i++) {
                Mapping mapping = (Mapping)columnMappings.get(i);
                Iterator columns = mapping.getInputs().iterator();
                while (columns.hasNext()) {
                    final MappingClassColumn mappingColumn = (MappingClassColumn)columns.next();
                    if (theMappingColumns.contains(mappingColumn)) {
                        final Iterator outputs = mapping.getOutputs().iterator();
                        while (outputs.hasNext()) {
                            final XmlElement element = (XmlElement)outputs.next();
                            element.setExcludeFromDocument(true);
                        }
                    }
                }
            }
        }
    }

    private MappingRoot getTreeMappingRoot( MappingRoot root ) {
        TransformationContainer cntr = null;
        EObject parent = root;
        while (parent != null && cntr == null) {
            if (parent instanceof TransformationContainer) {
                cntr = (TransformationContainer)parent;
            }

            parent = parent.eContainer();
        }

        if (cntr == null) {
            return null;
        }

        final Iterator roots = cntr.getTransformationMappings().iterator();
        while (roots.hasNext()) {
            Object next = roots.next();
            if (next instanceof TreeMappingRoot) {
                final TreeMappingRoot tmr = (TreeMappingRoot)next;
                if (root.getOutputs().containsAll(tmr.getInputs())) {
                    return tmr;
                }
            }
        }

        return null;

    }

    /**
     * Find all the TransformationMappingRoots within the xmlResource.
     * 
     * @param docModels
     * @return the Collection of TransformationMappingRoots
     */
    private Collection getMappingRoots() {
        if (xmlResource == null) {
            return Collections.EMPTY_LIST;
        }

        final Collection mappingRoots = new HashSet();
        final Iterator rsrcIt = xmlResource.getContents().iterator();
        while (rsrcIt.hasNext()) {
            final Object next = rsrcIt.next();
            if (next instanceof TransformationContainer) {
                final TransformationContainer tc = (TransformationContainer)next;
                mappingRoots.addAll(tc.getTransformationMappings());
            }
        }

        return mappingRoots;
    }

    private ModelProject getModelProject( EObject eObj ) {
        final Resource rsrc = eObj.eResource();

        ModelProject prj = null;
        if (rsrc == null) {
            return null;
        }

        final ModelResource mr = ModelerCore.getModelEditor().findModelResource(rsrc);
        if (mr != null) {
            prj = mr.getModelProject();
        }

        return prj;
    }

    public Collection createWebServiceBuildOptions( final HashMap outDocToInElementMappings,
                                                    final XsdBuilderOptions ops ) {
        CoreArgCheck.isNotNull(outDocToInElementMappings);
        CoreArgCheck.isNotNull(ops);

        if (!ops.genWs()) {
            return Collections.EMPTY_LIST;
        }

        final String rootModelName = ops.getRootModelName();
        final String wsModelName = ops.getWsModelName();
        final ArrayList options = new ArrayList();
        final Iterator mappings = outDocToInElementMappings.entrySet().iterator();
        ModelProject project = null;
        while (mappings.hasNext()) {
            final WebServiceBuildOptions option = new WebServiceBuildOptions();
            final Map.Entry next = (Map.Entry)mappings.next();
            final XmlDocument outDoc = (XmlDocument)next.getKey();
            if (outDoc != null && outDoc.getRoot() != null) {
                final XmlRoot root = outDoc.getRoot();
                final XSDElementDeclaration inElement = (XSDElementDeclaration)next.getValue();
                final XSDElementDeclaration outElement = (XSDElementDeclaration)root.getXsdComponent();

                option.setModel(wsModelName);

                if (project == null) {
                    project = getModelProject(outDoc);
                }

                option.setCurrentProject(project);

                option.setOperationOutputMessageElem(outElement);
                option.setOperationOutputXmlDoc(outDoc);
                option.setOperationOutputMessageName(outElement.getName() + OUT_MSG);

                if (inElement != null) {
                    option.setOperationInputMessageElem(inElement);
                    option.setOperationInputMessageName(inElement.getName() + IN_MSG);
                }

                // if the root.getName() contains an _Output, let's remove it
                String interfaceName = null;
                if (rootModelName != null) {
                    interfaceName = rootModelName + "_" + root.getName(); //$NON-NLS-1$
                } else {
                    interfaceName = root.getName();
                }
                int osIndex = interfaceName.indexOf(OUTPUT_SUFFIX);
                if (osIndex > -1) {
                    interfaceName = interfaceName.substring(0, osIndex);
                }
                option.setInterface(interfaceName);

                String operationName = GET_PREFIX + root.getName();
                int onIndex = operationName.indexOf(OUTPUT_SUFFIX);
                if (onIndex > -1) {
                    operationName = operationName.substring(0, onIndex);
                }
                option.setOperationName(operationName);

                if (project != null) options.add(option);
                else {
                    if (HEADLESS) {
                        options.add(option);
                    } else {
                        final String msg = Util.getString("GenerateXsdWizard.invalidWsOption", root.getName()); //$NON-NLS-1$    
                        addStatus(IStatus.ERROR, msg, null);
                    }
                }
            }
        }

        // Let's order the collection of options alphabetically based on operationName
        Object[] optionsArray = options.toArray();
        Arrays.sort(optionsArray);
        Collection sortedOptions = new ArrayList(options.size());

        for (int i = 0; i < optionsArray.length; i++) {
            sortedOptions.add(optionsArray[i]);
        }

        return sortedOptions;
    }

}
