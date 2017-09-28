/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.resource.xmi.MtkXmiResourceImpl;
import org.teiid.designer.core.util.ModelVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.LogicalRelationship;
import org.teiid.designer.metamodels.relational.LogicalRelationshipEnd;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.UniqueKey;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;


/**
 * RelationalUtil
 *
 * @since 8.0
 */
public class RelationalUtil {

    /**
     * Prevent allocation
     */
    private RelationalUtil() {
        super();
    }

    protected static void executeVisitor( final Object container,
                                          final ModelVisitor visitor,
                                          final int depth ) {
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        try {
            if (container instanceof Resource) {
                processor.walk((Resource)container, depth);
            } else if (container instanceof EObject) {
                processor.walk((EObject)container, depth);
            }
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    /**
     * Add any {@link UniqueKey} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findUniqueKeys( final Object container ) {
        return findUniqueKeys(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link UniqueKey} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the keys that were found; may not be null
     */
    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public static List findUniqueKeys( final Object container,
                                       final int depth ) {
        final UniqueKeyFinder finder = new UniqueKeyFinder();
        // TODO:  TEIIDDES-2660
        // Foreign keys can now reference PK/UC outside of a resource (i.e. another model)
        // So need to get the "project" for the initial container (if MtkXmiResourceImpl) and find other resources of like metamodel 
        // i.e. source or view models and add any other PK/UC's in like models that can be referenced.
        // 
        if( container instanceof MtkXmiResourceImpl ) {
			List allObjects = new ArrayList();
        	try {
				Collection<MtkXmiResourceImpl> allProjectRelationalModels = getProjectsOtherModels((MtkXmiResourceImpl)container);

				
				for( MtkXmiResourceImpl nextContainer : allProjectRelationalModels ) {
					List tempObjects = findObjects(finder, nextContainer, depth);
					allObjects.addAll(tempObjects);
				}
				
			} catch (ModelWorkspaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	return allObjects;
        }
        
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link ForeignKey} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findForeignKeys( final Object container ) {
        return findForeignKeys(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link ForeignKey} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the keys that were found; may not be null
     */
    public static List findForeignKeys( final Object container,
                                        final int depth ) {
        final ForeignKeyFinder finder = new ForeignKeyFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link ForeignKey} or {@link UniqueKey} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findKeys( final Object container ) { // NO_UCD
        return findKeys(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link ForeignKey} or {@link UniqueKey} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the keys that were found; may not be null
     */
    public static List findKeys( final Object container,
                                 final int depth ) {
        final KeyFinder finder = new KeyFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Index} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the indexes are to be found; may not be null
     * @return the indexes that were found; may not be null
     */
    public static List findIndexes( final Object container ) {
        return findIndexes(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Index} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the indexes are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the indexes that were found; may not be null
     */
    public static List findIndexes( final Object container,
                                    final int depth ) {
        final IndexFinder finder = new IndexFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Table} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @return the tables that were found; may not be null
     */
    public static List findTables( final Object container ) {
        return findTables(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Table} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the tables that were found; may not be null
     */
    public static List findTables( final Object container,
                                   final int depth ) {
        final TableFinder finder = new TableFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Procedure} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the procedures are to be found; may not be null
     * @return the procedures that were found; may not be null
     */
    public static List findProcedures( final Object container ) { // NO_UCD
        return findProcedures(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Procedure} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the procedures are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the procedures that were found; may not be null
     */
    public static List findProcedures( final Object container,
                                       final int depth ) {
        final ProcedureFinder finder = new ProcedureFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link ProcedureParameter} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the procedure parameters are to be found; may not be null
     * @return the procedures parameters that were found; may not be null
     */
    public static List findProcedureParameters( final Object container ) { // NO_UCD
        return findProcedureParameters(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link ProcedureParameter} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the procedure parameters are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the procedures parameters that were found; may not be null
     */
    public static List findProcedureParameters( final Object container,
                                                final int depth ) {
        final ProcedureParameterFinder finder = new ProcedureParameterFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link BaseTable} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @return the tables that were found; may not be null
     */
    public static List findBaseTables( final Object container ) { // NO_UCD
        return findBaseTables(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link BaseTable} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the tables that were found; may not be null
     */
    public static List findBaseTables( final Object container,
                                       final int depth ) {
        final BaseTableFinder finder = new BaseTableFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Column} instances found under the supplied container.
     * 
     * @param container the EObject or Resource under which the columns are to be found; may not be null
     * @return the columns that were found; may not be null
     */
    public static List findColumns( final Object container ) { // NO_UCD
        return findColumns(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Column} instances found under the supplied container.
     * 
     * @param container the EObject or Resource under which the columns are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the columns that were found; may not be null
     */
    public static List findColumns( final Object container,
                                    final int depth ) {
        final ColumnFinder finder = new ColumnFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Column} instances found under the supplied container but that are also contained by a {@link BaseTable}.
     * 
     * @param container the EObject or Resource under which the columns are to be found; may not be null
     * @return the columns that were found; may not be null
     */
    public static List findBaseTableColumns( final Object container ) {
        return findBaseTableColumns(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Column} instances found under the supplied container but that are also contained by a {@link BaseTable}.
     * 
     * @param container the EObject or Resource under which the columns are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the columns that were found; may not be null
     */
    public static List findBaseTableColumns( final Object container,
                                             final int depth ) {
        final BaseTableColumnFinder finder = new BaseTableColumnFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link LogicalRelationship} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @return the tables that were found; may not be null
     */
    public static List findLogicalRelationships( final Object container ) { // NO_UCD
        return findLogicalRelationships(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link LogicalRelationship} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the tables that were found; may not be null
     */
    public static List findLogicalRelationships( final Object container,
                                                 final int depth ) {
        final LogicalRelationshipFinder finder = new LogicalRelationshipFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link LogicalRelationshipEnd} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @return the tables that were found; may not be null
     */
    public static List findLogicalRelationshipEnds( final Object container ) {
        return findLogicalRelationshipEnds(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link LogicalRelationshipEnd} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the tables that were found; may not be null
     */
    public static List findLogicalRelationshipEnds( final Object container,
                                                    final int depth ) {
        final LogicalRelationshipEndFinder finder = new LogicalRelationshipEndFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Table} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @return the tables that were found; may not be null
     */
    public static List findSchemas( final Object container ) { // NO_UCD
        return findSchemas(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Table} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the tables that were found; may not be null
     */
    public static List findSchemas( final Object container,
                                    final int depth ) {
        final SchemaFinder finder = new SchemaFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Catalog} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @return the tables that were found; may not be null
     */
    public static List findCatalogs( final Object container ) { // NO_UCD
        return findCatalogs(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Catalog} instances found under the supplied container
     * 
     * @param container the EObject or Resource under which the tables are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the tables that were found; may not be null
     */
    public static List findCatalogs( final Object container,
                                     final int depth ) {
        final CatalogFinder finder = new CatalogFinder();
        return findObjects(finder, container, depth);
    }

    /**
     * Add any objects found under the supplied container
     * 
     * @param container the EObject or Resource under which the objects are to be found; may not be null
     * @return the objects that were found; may not be null
     */
    private static List findObjects( final RelationalEntityFinder finder,
                                     final Object container,
                                     final int depth ) {
        executeVisitor(container, finder, depth);
        // the container is collected along with childre
        // remove the container from results
        finder.removeContainer(container);
        return finder.getObjects();
    }

    public static boolean addChildToParent( final EObject child,
                                            final EObject parent ) {
        if (parent instanceof BaseTable) {
            if (child instanceof Column) {
                ((BaseTable)parent).getColumns().add(child);
            } else if (child instanceof PrimaryKey) {
                ((BaseTable)parent).setPrimaryKey((PrimaryKey)child);
            } else if (child instanceof ForeignKey) {
                ((BaseTable)parent).getForeignKeys().add(child);
            } else if (child instanceof UniqueConstraint) {
                ((BaseTable)parent).getUniqueConstraints().add((UniqueConstraint) child);
            } else if (child instanceof AccessPattern) {
                ((BaseTable)parent).getAccessPatterns().add(child);
            } else {
                return false;
            }
        } else if (parent instanceof Schema) {
            if (child instanceof Table) {
                ((Schema)parent).getTables().add(child);
            } else if (child instanceof Index) {
                ((Schema)parent).getIndexes().add(child);
            } else if (child instanceof Procedure) {
                ((Schema)parent).getProcedures().add(child);
            } else {
                return false;
            }
        } else if (parent instanceof Catalog) {
            if (child instanceof Table) {
                ((Catalog)parent).getTables().add(child);
            } else if (child instanceof Index) {
                ((Catalog)parent).getIndexes().add(child);
            } else if (child instanceof Schema) {
                ((Catalog)parent).getSchemas().add(child);
            } else if (child instanceof Procedure) {
                ((Schema)parent).getProcedures().add(child);
            } else {
                return false;
            }
        } else if (parent instanceof Procedure) {
            if (child instanceof ProcedureParameter) {
                ((Procedure)parent).getParameters().add(child);
            } else if (child instanceof ProcedureResult) {
                ((Procedure)parent).setResult((ProcedureResult)child);
            } else {
                return false;
            }
        } else if (parent instanceof ProcedureResult) {
            if (child instanceof Column) {
                ((ProcedureResult)parent).getColumns().add(child);
            } else {
                return false;
            }
        } else {
            return false;
        }

        return true;

    }
    
    public static boolean isGlobalTempTable(final EObject eObject) {
    
		final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
	    final String prefix = RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
	    final RelationalModelExtensionAssistant assistant = 
	    		(RelationalModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);
	    
		// Check to see if the table has extension property for isGlobalTemporaryTable == TRUE
	    boolean result = false;
		if( assistant != null ) {
			try {
				String resultString = assistant.getPropertyValue(eObject, 
						RelationalModelExtensionConstants.PropertyIds.GLOBAL_TEMP_TABLE);
				if( resultString != null ) {
					result = Boolean.parseBoolean(resultString);
				}
			} catch (Exception ex) {
				RelationalPlugin.Util.log(IStatus.ERROR, ex, ex.getMessage());
			}
		}
		return result;
    }
    
    public static RelationalModelExtensionAssistant getRelationalExtensionAssistant() {
    	final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        final String prefix = RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
        final RelationalModelExtensionAssistant assistant = (RelationalModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);
        
        return assistant;
    }
    
    private static Collection<MtkXmiResourceImpl> getProjectsOtherModels(MtkXmiResourceImpl containerModel) throws ModelWorkspaceException {
    	List<MtkXmiResourceImpl> projectRelationalModels = new ArrayList<MtkXmiResourceImpl>();
    	
    	ModelResource mr = ModelUtil.getModel(containerModel);
    	
    	IProject proj = mr.getCorrespondingResource().getProject();
    	
    	Collection<IFile> allProjectFiles = DotProjectUtils.getAllProjectResources(proj);
    	
    	for( IFile res : allProjectFiles ) {
    		if ( ModelUtil.isModelFile(res) ) {
    			ModelResource theMR = ModelUtil.getModel(res);
    			if( theMR != null && RelationalPackage.eNS_URI.equals(theMR.getPrimaryMetamodelUri()) ) {
    				projectRelationalModels.add((MtkXmiResourceImpl)theMR.getEmfResource());
    			}
    		}
    	}
    	
    	return projectRelationalModels;
    }
}
