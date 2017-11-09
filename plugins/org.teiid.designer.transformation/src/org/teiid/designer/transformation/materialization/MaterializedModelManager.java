/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.materialization;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.ModelerCoreRuntimeException;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.core.workspace.ModelFileUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ResourceAnnotationHelper;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.UniqueKey;
import org.teiid.designer.metamodels.relational.extension.InfinispanCacheModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.InfinispanCacheModelExtensionConstants;
import org.teiid.designer.metamodels.relational.extension.InfinispanHotrodModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.InfinispanHotrodModelExtensionConstants;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.reverseeng.ReverseEngConstants;

/**
 * This class is used for managing the creation of the JDG-specific POJO and related content including the
 * materialized source model and table
 * 
 * @author blafond
 *
 */

public class MaterializedModelManager implements ReverseEngConstants {
    private static String RESULT_MSG = TransformationPlugin.Util.getString("MaterializedModelManager.resultStr"); //$NON-NLS-1$
    private static String OK_MSG = TransformationPlugin.Util.getString("MaterializedModelManager.success"); //$NON-NLS-1$
    public static final String CONNECTION_NAMESPACE = "connection:"; //$NON-NLS-1$
    public static final String MATVIEW_JDG_SOURCE = "matview-jdg-source"; //$NON-NLS-1$
    public static final String TRANSLATOR_KEY = "translator:name"; //$NON-NLS-1$
    public static final String JDG6_TRANSLATOR_NAME = "infinispan-cache-dsl"; //$NON-NLS-1$
    public static final String JDG7_TRANSLATOR_NAME = "infinispan-hotrod"; //$NON-NLS-1$
    public static final String SUPPORTS_DIRECT_QUERY_PROCEDURE = "translator:SupportsDirectQueryProcedure"; //$NON-NLS-1$
    public static final String SUPPORTS_NATIVE_QUERIES = "translator:SupportsNativeQueries"; //$NON-NLS-1$
    
    private static IStatus OK_STATUS = new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID,
    		TransformationPlugin.Util.getString("MaterializedModelManager.allInputsOkStatusMessage"));  //$NON-NLS-1$

    private final StringNameValidator nameValidator = new StringNameValidator();


	private ModelResource targetModelResource;
	private IProject targetProject;
    private IContainer targetLocation;
    private Table selectedViewOrTable;
    private String sourceModelName;
    private String sourceTableName;
    private String pojoClassName;
    private String pojoPackageName;
    private String annotationType = PROTOBUF;
    private String jdgCacheName;
    private String jdgStagingCacheName;
    
    private File pojoFileSystemFolder;
    private IContainer pojoWorkspaceFolder;
    
    private String moduleZipFileName;
    
    private boolean createPojo;
    
    private boolean generateModule;

    private MultiStatus result;
    
    private Mode mode;
    
    private JDG_VERSION jdgVersion = JDG_VERSION.JDG_7_DOT_1;
    
    public MaterializedModelManager(EObject selectedTableOrView, Mode mode) {
    	this.mode = mode;
    	
    	//
    	// NOTE that for Materialization a virtual table must be selected
    	// 
    	if( this.mode == Mode.MATERIALIZE)  {
    		this.selectedViewOrTable = (Table)selectedTableOrView;
	        
	        this.sourceTableName = ModelerCore.getModelEditor().getName(this.selectedViewOrTable);
	        this.pojoClassName = this.sourceTableName;
	        this.pojoPackageName = DEFAULT_PACKAGE_NAME;
	        this.sourceModelName = this.sourceTableName + SOURCE_MODEL_NAME_SUFFIX;
	        
	        try {
	            this.targetModelResource = ModelUtil.getModel(this.selectedViewOrTable);
				this.targetLocation = targetModelResource.getCorrespondingResource().getParent();
				this.pojoWorkspaceFolder = this.targetLocation.getProject();
				this.moduleZipFileName = this.sourceTableName + DEFAULT_MODULE_SUFFIX;
				targetProject = this.targetModelResource.getCorrespondingResource().getProject();
			} catch (ModelWorkspaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		// This mode bypasses the materialization step and does NOT create a source model/table
    		this.createPojo = true;
    		this.selectedViewOrTable = (Table)selectedTableOrView;
	        
	        this.sourceTableName = ModelerCore.getModelEditor().getName(this.selectedViewOrTable);
	        this.pojoClassName = this.sourceTableName;
	        this.pojoPackageName = DEFAULT_PACKAGE_NAME;
	        
	        try {
	            this.targetModelResource = ModelUtil.getModel(this.selectedViewOrTable);
				this.targetLocation = targetModelResource.getCorrespondingResource().getParent();
				this.pojoWorkspaceFolder = this.targetLocation.getProject();
				this.moduleZipFileName = this.sourceTableName + DEFAULT_MODULE_SUFFIX;
				this.targetProject = this.targetModelResource.getCorrespondingResource().getProject();
		        this.sourceModelName = this.targetModelResource.getItemName();
			} catch (ModelWorkspaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    public MultiStatus execute() {
      
    	boolean isJDG7DOT1 = (jdgVersion == JDG_VERSION.JDG_7_DOT_1);
        try {  

        	ModelResource newSourceModel = constructRelationalModel();
            
	        selectedViewOrTable.setMaterialized(true);

            Table matTable = createMaterializedSourceTable( selectedViewOrTable, false);
            
            selectedViewOrTable.setMaterializedTable(matTable);
            
            // Add the schema to the materialized view model
            addValue(newSourceModel, matTable, getModelResourceContents(newSourceModel));
            
            // Create staging table, add to model and set extension property
            Table stagingTable =  createMaterializedSourceTable( selectedViewOrTable, true);
            addValue(newSourceModel, stagingTable, getModelResourceContents(newSourceModel));
            
            String modelName = ModelUtil.getName(newSourceModel);
            
            setInfinispanConnectionProperties(modelName, stagingTable, matTable);

            String stageTableStr = sourceModelName + "." + stagingTable.getName();
            String matTableStr = sourceModelName + "." + matTable.getName();
            
            /*
            	"teiid_rel:MATVIEW_AFTER_LOAD_SCRIPT" 'execute {jdgsourcemodelname}.native(''swap cache names'');'
				"teiid_rel:MATVIEW_BEFORE_LOAD_SCRIPT" 'execute {jdgsourcemodelname}.native(''truncate cache'');',
				"teiid_rel:MATERIALIZED_STAGE_TABLE" '{jdgsourcemodelname}.{stagingTable}')

				where {jdgsourcemodelname} as the new JDG Source Model that is created.
             */
            String beforeLoadScriptStr = "execute " + sourceModelName + ".native(\'\'truncate cache\'\');";
            String afterLoadScriptStr = "execute " + sourceModelName + ".native(\'\'swap cache names\'\');";
            
            if( isJDG7DOT1 ) {
            	// "teiid_rel:MATVIEW_BEFORE_LOAD_SCRIPT" '
            	// execute StockJDGSource.native(''truncate StockJDGSource.ST_StockCache'');',
            	beforeLoadScriptStr = "execute " + sourceModelName + ".native(\'\'truncate " + stageTableStr + "\'\');";
            	// "teiid_rel:MATVIEW_AFTER_LOAD_SCRIPT" '
            	// execute StockJDGSource.native(''rename StockJDGSource.ST_StockCache StockJDGSource.StockCache'');'
            	afterLoadScriptStr = "execute " + sourceModelName + ".native(\'\'rename " 
            			+ stageTableStr + StringConstants.SPACE + matTableStr + "\'\');";
            }
            
            
            ModelObjectExtensionAssistant assistant = getRelationalExtensionAssistant();
            assistant.setPropertyValue(selectedViewOrTable, RelationalModelExtensionConstants.PropertyIds.MATVIEW_AFTER_LOAD_SCRIPT, afterLoadScriptStr);
            assistant.setPropertyValue(selectedViewOrTable, RelationalModelExtensionConstants.PropertyIds.MATVIEW_BEFORE_LOAD_SCRIPT, beforeLoadScriptStr);
            assistant.setPropertyValue(selectedViewOrTable, RelationalModelExtensionConstants.PropertyIds.MATERIALIZED_STAGE_TABLE, stageTableStr);
            
            ResourceAnnotationHelper helper = new ResourceAnnotationHelper();
            Properties props = new Properties();
            props.put(CONNECTION_NAMESPACE + MATVIEW_JDG_SOURCE, Boolean.TRUE.toString());
            if( isJDG7DOT1 ) {
            	props.put(TRANSLATOR_KEY, JDG7_TRANSLATOR_NAME);
            } else {
            	props.put(TRANSLATOR_KEY, JDG6_TRANSLATOR_NAME);
            }
    		props.setProperty(SUPPORTS_DIRECT_QUERY_PROCEDURE, Boolean.toString(true));
    		props.setProperty(SUPPORTS_NATIVE_QUERIES, Boolean.toString(true));
            helper.setProperties(newSourceModel, props);
            
            ModelBuildUtil.rebuildImports(targetModelResource.getEmfResource(), true);
            
            newSourceModel.save(new NullProgressMonitor(), false);
            
        } catch (Exception err) {
            TransformationPlugin.Util.log(err);
        }  
        
        if(result == null) {
            addStatus(IStatus.OK, OK_MSG, null);
        }
        
        return result;
    }
    
    /** 
     * @return Returns the resultPhysicalModel.
     * @since 4.1
     */
    public ModelResource getMaterializedViewModel() {
        return this.targetModelResource;
    }
    
    private Table createMaterializedSourceTable(Table table, boolean createStagingTable) {
        
        //create the new table and the staging table and add them to the result physical model
        final Table newMatTable = (Table)RelationalFactory.eINSTANCE.create(table.eClass() );
        
        //Create a uniqueName
        String name = pojoClassName;
        
        if( createStagingTable ) {
        	name = "ST_" + name;
        }
        
        String tmp = nameValidator.createValidUniqueName(name);
        if(tmp != null) {
            name = tmp;
        }
        
        //Set table names
        newMatTable.setName(name);
        
        //Copy table values
        copyTableValues(table, newMatTable);
       
        //Iterate over the children of the Materialized View and create corresponding columns, primary keys, and indexes in both
        //the new table and the stage table.
        final Iterator cols = table.getColumns().iterator();
        final HashSet pks = new HashSet();
        while(cols.hasNext() ) {
            final Column nextCol = (Column)cols.next();
            final Column newCol = RelationalFactory.eINSTANCE.createColumn();
            newMatTable.getColumns().add(newCol);
            copyColValues(nextCol, newCol);
            pks.addAll(nextCol.getUniqueKeys() );
        }
        
        final Iterator pksIt = pks.iterator();
        while(pksIt.hasNext() ) {
            final UniqueKey nextKey = (UniqueKey)pksIt.next();  
            final UniqueKey newKey = (UniqueKey)RelationalFactory.eINSTANCE.create(nextKey.eClass() );
            if(nextKey instanceof PrimaryKey) {
                ((BaseTable)newMatTable).setPrimaryKey( (PrimaryKey)newKey);
            }else {
                ((BaseTable)newMatTable).getUniqueConstraints().add((UniqueConstraint) newKey);
            }
            
            copyUniqueKeyValues(nextKey, newKey);
        }
        
        return newMatTable;
    }
    
    private void addValue(final Object owner, final Object value, EList feature) {
        try {
                ModelerCore.getModelEditor().addValue(owner, value, feature);
        } catch (ModelerCoreException err) {
        	TransformationPlugin.Util.log(err);
        }
    }
    
    protected void copyTableValues(final Table orig, final Table copy) {
        //copy.setNameInSource( orig.getNameInSource() ); // Materialized Tables do not represent DB schema or tables so DO NOT SET
        copy.setCardinality(orig.getCardinality() );
        copy.setSystem(orig.isSystem() );
        // Defect 16941 - the supportsUpdate flag should always be true for a materialization table
        //copy.setSupportsUpdate(orig.isSupportsUpdate() );
        copy.setSupportsUpdate(true);
    }
    
    private void copyColValues(final Column orig, final Column copy) {
        copy.setAutoIncremented(orig.isAutoIncremented() );
        copy.setCaseSensitive(orig.isCaseSensitive() );
        copy.setCharacterSetName(orig.getCharacterSetName() );
        copy.setCollationName(orig.getCollationName() );
        copy.setCurrency(copy.isCurrency() );
        copy.setDefaultValue(orig.getDefaultValue() );
        copy.setFixedLength(orig.isFixedLength() );
        copy.setFormat(orig.getFormat() );
        copy.setLength(orig.getLength() );
        copy.setMaximumValue(orig.getMaximumValue() );
        copy.setMinimumValue(orig.getMinimumValue() );
        copy.setName(orig.getName() );
        // copy.setNameInSource(orig.getNameInSource() ); // Materialized Tables do not represent DB schema or tables so DO NOT SET
        copy.setNativeType(orig.getNativeType() );
        copy.setNullable(orig.getNullable() );
        copy.setPrecision(orig.getPrecision() );
        copy.setRadix(orig.getRadix() );
        copy.setScale(orig.getScale() );
        copy.setSearchability(orig.getSearchability() );
        copy.setSelectable(orig.isSelectable() );
        copy.setSigned(orig.isSigned() );
        copy.setType(orig.getType() );
        // Defect 16941 - the updateable flag should always be true for the columns of a materialization table
        //copy.setUpdateable(orig.isUpdateable() );
        copy.setUpdateable(true);
    }
    
    private void addStatus(final int severity, final String msg, final Exception e) {
        if(result == null) {
            result = new MultiStatus(TransformationPlugin.PLUGIN_ID, -1, RESULT_MSG, null);
        }
        
        result.add(new Status(severity, TransformationPlugin.PLUGIN_ID, -1, msg, e) );
    }
    
    private void copyUniqueKeyValues(final UniqueKey orig, 
                                     final UniqueKey copy) {

        copy.setName(orig.getName());
        
        copy.setNameInSource(orig.getNameInSource());
        
        try {
            final BaseTable copyOwner = copy.getTable();
            final BaseTable origOwner = orig.getTable();
            final Iterator cols = orig.getColumns().iterator();
            while(cols.hasNext() ) {
                final Column nextOrigCol = (Column)cols.next();
                final int index = origOwner.getColumns().indexOf(nextOrigCol);
                final Column copyCol = (Column)copyOwner.getColumns().get(index);
                if(nextOrigCol.getName().equals(copyCol.getName() ) ) {
                    copy.getColumns().add(copyCol);
                }else {
                    final String msg = TransformationPlugin.Util.getString("MaterializedModelManager.errorSettingUCvals", copy.getName()); //$NON-NLS-1$
                    throw new ModelerCoreRuntimeException(msg);
                }
            }
        } catch (Exception err) {
            final String msg = TransformationPlugin.Util.getString("MaterializedModelManager.errorSettingUCvals", copy.getName()); //$NON-NLS-1$
            throw new ModelerCoreRuntimeException(msg);
        }
    }

    public Table getVirtualTable() {
    	return this.selectedViewOrTable;
    }
    
    public IContainer getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(IContainer projectOrFolder) {
		this.targetLocation = projectOrFolder;
	}    
	
	public EList getModelResourceContents(ModelResource mr) {
    	EList eList = null;
    	
    	try {
			eList = mr.getEmfResource().getContents();
		} catch (ModelWorkspaceException err) {
			TransformationPlugin.Util.log(err);
		}
		
		return eList;
    }

	public void setMaterializedViewModel(ModelResource materializedViewModel) {
		this.targetModelResource = materializedViewModel;
	}
	
	public String getMaterializedSourceModelName() {
		return sourceModelName;
	}
	
	public void setMaterializedSourceModelName(String materializedSourceModelName) {
		this.sourceModelName = materializedSourceModelName;
	}
	
	public IContainer getLocationName() {
		return targetLocation;
	}
	
	public String getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	public String getPojoClassName() {
		return pojoClassName;
	}

	public void setPojoClassName(String pojoClassName) {
		this.pojoClassName = pojoClassName;
	}

	public String getPojoPackageName() {
		return pojoPackageName;
	}

	public void setPojoPackageName(String pojoPackageName) {
		this.pojoPackageName = pojoPackageName;
	}

	public String getAnnotationType() {
		return annotationType;
	}

	public void setAnnotationType(String annotationType) {
		this.annotationType = annotationType;
	}

	public File getPojoFileSystemFolder() {
		return pojoFileSystemFolder;
	}

	public void setPojoFileSystemFolder(File pojoFileSystemFolder) {
		this.pojoFileSystemFolder = pojoFileSystemFolder;
	}

	public IContainer getPojoWorkspaceFolder() {
		return pojoWorkspaceFolder;
	}

	public void setPojoWorkspaceFolder(IContainer pojoWorkspaceFolder) {
		this.pojoWorkspaceFolder = pojoWorkspaceFolder;
	}
	
	public String getModuleZipFileName() {
		return moduleZipFileName;
	}

	public void setModuleZipFileName(String moduleZipFileName) {
		this.moduleZipFileName = moduleZipFileName;
	}

	public boolean doCreatePojo() {
		return createPojo;
	}

	public void setCreatePojo(boolean createPojo) {
		this.createPojo = createPojo;
	}

	public boolean doGenerateModule() {
		return generateModule;
	}

	public void setGenerateModule(boolean generateModule) {
		this.generateModule = generateModule;
	}
	
	public IProject getProject() {
		return this.targetProject;
	}
	
	public boolean isPojoMode() {
		return this.mode == Mode.POJO;
	}
	
	public JDG_VERSION getJdgVersion() {
		return this.jdgVersion;
	}
	
	public void setJdgVersion(JDG_VERSION version) {
		this.jdgVersion = version;
	}
	
	public String getJdgCacheName() {
		return jdgCacheName;
	}

	public void setJdgCacheName(String name) {
		this.jdgCacheName = name;
	}
	
	public String getJdgStagingCacheName() {
		return jdgStagingCacheName;
	}

	public void setJdgStagingCacheName(String name) {
		this.jdgStagingCacheName = name;
	}


	public IStatus validate(int pageNumber) {
		if( pageNumber == 1 ) {
			if( this.selectedViewOrTable == null  ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_noVirtualTablesSelectedError")); //$NON-NLS-1$
			}
			
			if( this.targetLocation == null ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_targetLocationIsNullError")); //$NON-NLS-1$
			}
			
			if( StringUtilities.isEmpty(sourceModelName) ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_sourceModelNameIsUndefined")); //$NON-NLS-1$
			} else {
				// check for model name characters
				// if JDG 7.1, then can't user anything but alpha/numeric/underscore
				if(getJdgVersion() == JDG_VERSION.JDG_7_DOT_1 ) {
					StringNameValidator validator = new StringNameValidator(new char[] {'_'});
					String msg = validator.checkValidName(sourceModelName);
					if( msg != null ) {
						return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
								TransformationPlugin.Util.getString("MaterializedModelManager_sourceModelNameInvalid", msg)); //$NON-NLS-1$
					}
				}
			}
	        
	        if( jdgVersion == JDG_VERSION.JDG_7_DOT_1 ) {
	        	// make sure CACHE is not null
	        	if( StringUtilities.isEmpty(jdgCacheName) ) {
	        		return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
		        			TransformationPlugin.Util.getString("MaterializedModelManager_jdgCacheNameMissingError")); //$NON-NLS-1$
	        	}
	        	
	        	if( StringUtilities.isEmpty(jdgStagingCacheName) ) {
	        		return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
		        			TransformationPlugin.Util.getString("MaterializedModelManager_jdgStagingCacheNameMissingError")); //$NON-NLS-1$
	        	}
	        	
	        	if( !StringUtilities.areDifferent(jdgCacheName, jdgStagingCacheName)) {
	        		return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
		        			TransformationPlugin.Util.getString("MaterializedModelManager_jdgCacheNameIdenticalError")); //$NON-NLS-1$
	        		
	        	}
	        }
            
	        if( this.selectedViewOrTable.isMaterialized() ) {
	        	return new Status(IStatus.WARNING, TransformationPlugin.PLUGIN_ID, 
	        			TransformationPlugin.Util.getString("MaterializedModelManager_viewAlreadyMaterializedError", selectedViewOrTable.getName())); //$NON-NLS-1$
	        }

		} else if( pageNumber == 2 ) {
			if( this.doCreatePojo() ) {
				if( StringUtilities.isEmpty(pojoPackageName) ) {
					return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
							TransformationPlugin.Util.getString("MaterializedModelManager_packageNameUndefined")); //$NON-NLS-1$
				}
				
				if( StringUtilities.isEmpty(pojoClassName) ) {
					return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
							TransformationPlugin.Util.getString("MaterializedModelManager_classNameUndefined")); //$NON-NLS-1$
				}
				
				if( this.doGenerateModule() && StringUtilities.isEmpty(moduleZipFileName) ) {
					return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
							TransformationPlugin.Util.getString("MaterializedModelManager_moduleZipFileNameUndefined")); //$NON-NLS-1$
				}
			} 
		}
		
		return OK_STATUS;

	}
	
    /**
     * Create a Relational Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    private ModelResource constructRelationalModel() {

        String modelNameWithExt = sourceModelName.trim();

        if (!modelNameWithExt.endsWith(ModelFileUtil.DOT_XMI)) {
        	modelNameWithExt += ModelFileUtil.DOT_XMI;
        }

        IPath relativeModelPath = targetLocation.getProjectRelativePath().append(modelNameWithExt);
        final IFile modelFile = targetLocation.getProject().getFile(relativeModelPath);
        final ModelResource resrc = ModelerCore.create(modelFile);
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
            resrc.getModelAnnotation().setModelType(ModelType.PHYSICAL_LITERAL);
        } catch (ModelWorkspaceException mwe) {
            mwe.printStackTrace();
        }

        try {
        	getExtensionAssistant().applyMedIfNecessary(modelFile);
        	
        	// TODO:  JDG Use-Case >> Add translator properties some point
        	// <property name="SupportsDirectQueryProcedure" value="true"/>
        	//  <property name="SupportsNativeQueries" value="true"/>
			resrc.save(new NullProgressMonitor(), false);
		} catch (ModelWorkspaceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return resrc;
    }
    
    private void setInfinispanConnectionProperties(String modelName, Table stagingTable, Table matTable) throws Exception {
    	if( getJdgVersion() == JDG_VERSION.JDG_6_DOT_6 ) {
	        getExtensionAssistant().setPropertyValue(
	        		stagingTable, InfinispanCacheModelExtensionConstants.PropertyIds.PRIMARY_TABLE, modelName + StringConstants.DOT + matTable.getName());
    	} else {
	        getExtensionAssistant().setPropertyValue(
	        		matTable, InfinispanHotrodModelExtensionConstants.PropertyIds.CACHE, jdgCacheName);
	        getExtensionAssistant().setPropertyValue(
	        		stagingTable, InfinispanHotrodModelExtensionConstants.PropertyIds.CACHE, jdgStagingCacheName);
    	}
    }
    
    private ModelObjectExtensionAssistant getExtensionAssistant() {
    	if( getJdgVersion() == JDG_VERSION.JDG_6_DOT_6 ) {
    		return InfinispanCacheModelExtensionAssistant.getInstance();
    	} else {
	        return InfinispanHotrodModelExtensionAssistant.getInstance();
    	}
    }
    
    private ModelObjectExtensionAssistant getRelationalExtensionAssistant() {
	        final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
	        return (ModelObjectExtensionAssistant)registry.getModelExtensionAssistant(RELATIONAL_EXT_ASSISTANT_NS);
    }
}
