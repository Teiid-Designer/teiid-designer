/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.modeshape.common.text.ParsingException;
import org.modeshape.common.text.Position;
import org.modeshape.sequencer.ddl.DdlParsers;
import org.modeshape.sequencer.ddl.StandardDdlLexicon;
import org.modeshape.sequencer.ddl.node.AstNode;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.exception.EmptyArgumentException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.OperationUtil.Unreliable;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ddl.DdlImporterManager;
import org.teiid.designer.ddl.DdlNodeImporter;
import org.teiid.designer.ddl.registry.DdlNodeImporterRegistry;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.relational.compare.DifferenceGenerator;
import org.teiid.designer.relational.compare.DifferenceReport;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.processor.EmfModelGenerator;

/**
 * DdlImporter parses the provided DDL and generates a model containing the parsed entities
 *
 * @since 8.0
 */
public class DdlImporter {

    private IProject[] projects;

    private IContainer modelFolder;
    private String ddlFileName;
    private String specifiedParser;
	private IFile modelFile;
    private DifferenceReport diffReport;
    private ModelResource model;
    private String ddlString;
    private DdlErrorMessage failedMessage; 

    private DdlImporterManager importManager = new DdlImporterManager();

    /**
     * DdlImporter constructor
     * @param projects the list of open workspace projects
     */
    public DdlImporter(IProject[] projects ) {
        this.projects = projects;
    }

    /**
     * @return ddlFileName
     */
    public String ddlFileName() {
        return ddlFileName;
    }

    private void handleStatus(IStatus status ) {
        if (!status.isOK()) {
            if (status.getException() != null) throw CoreModelerPlugin.toRuntimeException(status.getException());
            throw new RuntimeException(status.getMessage());
        }
    }

    /**
     * @param messages
     * @param monitor
     * @param totalWork
     */
    public void importDdl(final List<String> messages, final IProgressMonitor monitor, final int totalWork ) {
        OperationUtil.perform(new Unreliable() {

            private FileReader reader = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (reader != null) reader.close();
            }

            @Override
            public void tryToDo() throws Exception {
                reader = new FileReader(ddlFileName());
                importDdl(reader, messages, monitor, totalWork);
            }
        });
    }

    void importDdl(FileReader reader, List<String> messages, IProgressMonitor monitor, int totalWork ) throws Exception {
    	ddlString = null;
    	failedMessage = null;
    	
        int workUnit = totalWork / 3;

        // ------------------------------------------------------------------------------
        // Parse the DDL from the file
        // ------------------------------------------------------------------------------
        monitor.subTask(DdlImporterI18n.PARSING_DDL_MSG);
        // Read the file contents
        char[] buf = new char[FileUtils.DEFAULT_BUFFER_SIZE];
        StringBuilder builder = new StringBuilder();
        for (int charTot = reader.read(buf); charTot >= 0; charTot = reader.read(buf))
            builder.append(buf, 0, charTot);
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        // Use specified parser if it has been set
        AstNode rootNode = null;
        DdlParsers parsers = new DdlParsers();
        ddlString = builder.toString();
        try {
        	if(!CoreStringUtil.isEmpty(specifiedParser)) {
        		rootNode = parsers.parseUsing(ddlString,specifiedParser);
        		// No DDL parser is specified - user DdlParsers which will score the best fit
        	} else {
        		rootNode = parsers.parse(ddlString, ddlFileName);
        	}
        // If parsing exception is encountered, throw DdlImportException
        } catch (ParsingException e) {
        	failedMessage = new DdlErrorMessage(e.getMessage());
        	Position position = e.getPosition();
        	failedMessage.setIsParse(true);
        	failedMessage.setColNumber(position.getColumn());
        	failedMessage.setLineNumber(position.getLine());
        	failedMessage.setIndex(position.getIndexInContent());
        	if(!CoreStringUtil.isEmpty(specifiedParser)) {
        		failedMessage.setParserId(specifiedParser);
        	} else if(rootNode!=null) {
                String parserId = (String) rootNode.getProperty(StandardDdlLexicon.PARSER_ID);
            	failedMessage.setParserId(parserId);
        	}
        	return;
        }
        String parserId = (String) rootNode.getProperty(StandardDdlLexicon.PARSER_ID);

        if (monitor.isCanceled())
            throw new OperationCanceledException();
        monitor.worked(workUnit);

        // ------------------------------------------------------------------------------
        // Set up DifferenceProcessor
        //   - startingSelector -- existing model
        //   - endingSelector   -- generated from parsed ddl nodes 
        // ------------------------------------------------------------------------------
        monitor.subTask(DdlImporterI18n.CREATING_MODEL_MSG);
        model = ModelerCore.create(modelFile);
        importManager.setRelationalModel(model);
        
        RelationalModel targetRelationalModel = importManager.getObjectFactory().createRelationalModel(model);
        
        importManager.setProgressMonitor(monitor);
        importManager.setProgressMessages(messages);

        DdlNodeImporter nodeImporter = DdlNodeImporterRegistry.getInstance().getRegistered(parserId.toUpperCase());
        if (nodeImporter == null)
            throw new Exception(DdlImporterPlugin.i18n("noDDLImporterRegisteredMsg", parserId)); //$NON-NLS-1$

        importManager.setNodeImporter(nodeImporter);
        RelationalModel ddlImportModel = nodeImporter.importNode(rootNode,importManager);
        
        if (monitor.isCanceled())
            throw new OperationCanceledException();
        monitor.worked(workUnit);

        // ------------------------------------------------------------------------------
        // Generate a DifferenceReport
        // ------------------------------------------------------------------------------
        monitor.subTask(DdlImporterI18n.CREATING_CHANGE_REPORT_MSG);
        diffReport = DifferenceGenerator.compare(ddlImportModel,targetRelationalModel);
        
        if (monitor.isCanceled())
            throw new OperationCanceledException();
        monitor.worked(workUnit);
    }

    /**
     * @return the 'true' if has a failure mesage
     */
    public boolean hasFailureMessage() {
    	if(this.failedMessage!=null) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * @return the failure message
     */
    public DdlErrorMessage getFailureMessage() {
    	return this.failedMessage;
    }

    /**
     * @return the last ddl string
     */
    public String getDdlString() {
    	return ddlString;
    }
    
    /**
     * @return differenceReport
     */
    public DifferenceReport getDifferenceReport() {
    	return diffReport;
    }
    
    /**
     * @return modelFile
     */
    public IFile modelFile() {
        return modelFile;
    }

    /**
     * @return modelFolder
     */
    public IContainer modelFolder() {
        return modelFolder;
    }

    /**
     * @return modelType
     */
    public ModelType modelType() {
        return importManager.getModelType();
    }

    /**
     * @param monitor
     * @param totalWork
     */
    public void save(IProgressMonitor monitor, int totalWork ) {
        monitor.subTask(DdlImporterI18n.SAVING_MODEL_MSG);
        try {
        	// Set model type etc if new model
            if (!model.exists()) {
                ModelAnnotation modelAnnotation = model.getModelAnnotation();
                modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
                modelAnnotation.setModelType(importManager.getModelType());
            }
            
            // Update the model, based on difference report
            IStatus importStatus = EmfModelGenerator.INSTANCE.execute(diffReport, model, monitor, totalWork);
            handleStatus(importStatus);

            // Save model
            model.save(monitor, false);
            
        } catch (Exception error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
        monitor.worked(totalWork);
        monitor.done();
    }

    /**
     * @param ddlFileName
     */
    public void setDdlFileName(String ddlFileName ) {
        this.ddlFileName = null;
        if (ddlFileName == null) throw new EmptyArgumentException("ddlFileName"); //$NON-NLS-1$
        ddlFileName = ddlFileName.trim();
        if (ddlFileName.isEmpty()) throw new EmptyArgumentException("ddlFileName"); //$NON-NLS-1$
        File file = new File(ddlFileName);
        if (!file.exists() || file.isDirectory()) throw new IllegalArgumentException(DdlImporterI18n.DDL_FILE_NOT_FOUND_MSG);
        this.ddlFileName = ddlFileName;
    }

    /**
     * @param modelFolder
     */
    public void setModelFolder(IContainer modelFolder ) {
        this.modelFolder = modelFolder;
    }

    /**
     * @param modelFolderName
     */
    public void setModelFolder(String modelFolderName ) {
        modelFolder = null;
        //chgProcessor = null;
        if (modelFolderName == null) throw new EmptyArgumentException("modelFolderName"); //$NON-NLS-1$
        modelFolderName = modelFolderName.trim();
        IPath modelFolderPath = Path.fromPortableString(modelFolderName).makeAbsolute();
        if (modelFolderName.isEmpty() || modelFolderPath.segmentCount() == 0) throw new EmptyArgumentException("modelFolderName"); //$NON-NLS-1$
        // Verify project is valid
        String projectName = modelFolderPath.segment(0);
        IWorkspace workspace = ModelerCore.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        if (root.findMember(projectName) != null) {
            boolean found = false;
            for (IProject project : projects)
                if (projectName.equals(project.getName())) {
                    found = true;
                    break;
                }
            if (!found) throw new IllegalArgumentException(DdlImporterI18n.MODEL_FOLDER_IN_NON_MODEL_PROJECT_MSG);
        }
        // Verify folder is valid
        if (!workspace.validatePath(modelFolderPath.toString(), IResource.PROJECT | IResource.FOLDER).isOK()) throw new IllegalArgumentException(
                                                                                                                                                 DdlImporterI18n.INVALID_MODEL_FOLDER_MSG);
        IResource resource = root.findMember(modelFolderPath);
        // Verify segment in folder is not a file
        if (resource instanceof IFile) throw new IllegalArgumentException(DdlImporterI18n.MODEL_FOLDER_IS_FILE_MSG);
        if (resource == null) {
            if (modelFolderPath.segmentCount() == 1) modelFolder = root.getProject(projectName);
            else modelFolder = root.getFolder(modelFolderPath);
        } else modelFolder = (IContainer)resource;
    }

    /**
     * @param modelName
     */
    public void setModelName(String modelName ) {
    	importManager.setModelName(null);
        modelFile = null;

        if (modelName == null) throw new EmptyArgumentException("modelName"); //$NON-NLS-1$
        modelName = modelName.trim();
        if (modelName.isEmpty()) throw new EmptyArgumentException("modelName"); //$NON-NLS-1$
        // Verify name is valid
        IWorkspace workspace = ModelerCore.getWorkspace();
        if (!workspace.validateName(modelName, IResource.FILE).isOK()) throw new IllegalArgumentException(
                                                                                                          DdlImporterI18n.INVALID_MODEL_NAME_MSG);
        if (modelFolder != null) {
            IWorkspaceRoot root = workspace.getRoot();
            IPath modelPath = modelFolder.getFullPath().append(modelName);
            if (!modelName.endsWith(ModelerCore.MODEL_FILE_EXTENSION)) modelPath = modelPath.addFileExtension(ModelerCore.MODEL_FILE_EXTENSION.substring(1));
            if (modelFolder.exists()) {
                // Verify name is not a folder
                IResource resource = root.findMember(modelPath);
                if (resource instanceof IContainer) throw new IllegalArgumentException(DdlImporterI18n.MODEL_NAME_IS_FOLDER_MSG);
                if (resource == null) modelFile = root.getFile(modelPath);
                else {
                    // Verify name is not a non-model file
                    if (!ModelUtil.isModelFile(resource)) throw new IllegalArgumentException(
                                                                                             DdlImporterI18n.MODEL_NAME_IS_NON_MODEL_FILE_MSG);
                    // Verify name is not a non-relational model
                    if (!RelationalPackage.eNS_URI.equals(ModelUtil.getXmiHeader(resource).getPrimaryMetamodelURI())) throw new IllegalArgumentException(
                                                                                                                                                         DdlImporterI18n.MODEL_NAME_IS_NON_RELATIONAL_MODEL_MSG);
                    modelFile = (IFile)resource;
                }
            } else modelFile = root.getFile(modelPath);
        }
        importManager.setModelName(new Path(modelName).removeFileExtension().lastSegment());
    }

    /**
     * @param modelType Sets modelType to the specified value.
     */
    public void setModelType(ModelType modelType ) {
    	importManager.setModelType(modelType);
    }

    /**
     * @param optToCreateModelEntitiesForUnsupportedDdl
     */
    public void setOptToCreateModelEntitiesForUnsupportedDdl(boolean optToCreateModelEntitiesForUnsupportedDdl ) {
    	importManager.setOptToCreateModelEntitiesForUnsupportedDdl(optToCreateModelEntitiesForUnsupportedDdl);
    }

    /**
     * @param optToSetModelEntityDescription
     */
    public void setOptToSetModelEntityDescription(boolean optToSetModelEntityDescription ) {
    	importManager.setOptToSetModelEntityDescription(optToSetModelEntityDescription);
    }

    /**
	 * @return the specifiedParser
	 */
	public String getSpecifiedParser() {
		return this.specifiedParser;
	}

	/**
	 * Allows a specific parser to be used for the import.  If the parser is not specified,
	 * the DdlParsers class is used - which does scoring to determine the best parser
	 * @param specifiedParser the specifiedParser to set
	 */
	public void setSpecifiedParser(String specifiedParser) {
		this.specifiedParser = specifiedParser;
	}

    /**
     * 
     */
    public void undoImport() {
    	diffReport = null;
    }
}