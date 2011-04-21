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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.refactor.IRefactorModelHandler;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.mapping.PluginConstants;
import com.metamatrix.modeler.mapping.choice.IChoiceObject;
import com.metamatrix.modeler.mapping.factory.IChoiceFactory;

/**
 * This class provides the mapping plugin a mechanism to affect changes internal to xml document models during
 * Model refactoring operations. (i.e. Move & Rename)
 * 
 * In particular, a Rename of an XML Document model could result in mis-named models in Choice Criteria.
 * 
 *
 */
public class MappingRefactorModelHandler implements IRefactorModelHandler {

	public MappingRefactorModelHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void helpUpdateDependentModelContents(int type, ModelResource modelResource,
			Map refactoredPaths, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(refactoredPaths.values(), "refactoredPaths"); //$NON-NLS-1$
		
		try {
			switch( type ) {
				case IRefactorModelHandler.RENAME: {
					refactorChoiceCriteria(modelResource, refactoredPaths);
				}break;
				case IRefactorModelHandler.DELETE: {
					
				}break;
				case IRefactorModelHandler.MOVE: {
					
				}break;
				default: break;
			}
		} catch (ModelWorkspaceException e) {
			PluginConstants.Util.log(IStatus.ERROR, e, e.getMessage());
		}

	}
	
	@Override
	public void helpUpdateModelContents(int type, ModelResource modelResource,
			Map refactoredPaths, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(refactoredPaths.values(), "refactoredPaths"); //$NON-NLS-1$
		
		try {
			switch( type ) {
				case IRefactorModelHandler.RENAME: {
					refactorChoiceCriteria(modelResource, refactoredPaths);
				}break;
				case IRefactorModelHandler.DELETE: {
					
				}break;
				case IRefactorModelHandler.MOVE: {
					
				}break;
				default: break;
			}
		} catch (ModelWorkspaceException e) {
			PluginConstants.Util.log(IStatus.ERROR, e, e.getMessage());
		}

	}
	
    private void refactorChoiceCriteria(ModelResource mr, Map refactoredPaths) throws ModelWorkspaceException {
    	
    	Resource res = mr.getEmfResource();
    
    	if (res instanceof EmfResource && ((EmfResource)res).getModelType() == ModelType.VIRTUAL_LITERAL &&
    			mr.getModelAnnotation().getPrimaryMetamodelUri().equals(XmlDocumentPackage.eNS_URI)) {
            for (Iterator iter = ((EmfResource)res).getAllContents(); iter.hasNext();) {
                EObject eObject = (EObject)iter.next();
                if (eObject instanceof XmlChoice) {
        	        IChoiceFactory icfFactory = ChoiceFactoryManager.getChoiceFactory( eObject );
        	        
        	        if ( icfFactory != null ) {
        	            IChoiceObject ico = icfFactory.createChoiceObject( eObject );
        	            for(Object option : ico.getOrderedOptions() ) {
        	            	String userStr = ico.getCriteria(option);
        	            	if( userStr != null && !userStr.isEmpty() ) {
        	            		String newStr = refactorUserSql(userStr, refactoredPaths);
        	            		ico.setCriteria(option, newStr);
        	            	}
        	            }
        	        }
                }
            }
    	}
    }
    
    protected String refactorUserSql(String sqlStr, Map refactoredPaths) {
    	// Only need to fix SQL if Models are renamed, so looking for 
    	Map<String, String> changedModelNames = getChangedNameMap(refactoredPaths);
    	
    	// So we need to walk through the String and check for 
    	
    	String copyOfSql = new StringBuffer(sqlStr).toString();
    	
    	for( String key: changedModelNames.keySet() ) {
    		// Replace all names with ' ' preceeding
    		String oldName = ' ' + (String)key + '.';
    		String newName = ' ' + (String)changedModelNames.get(key) + '.';
    		if( copyOfSql.contains(oldName) ) {
    			copyOfSql = copyOfSql.replaceAll(oldName, newName);
    		}
    		// Replace all names with ',' preceeding
    		oldName = ',' + (String)key + '.';
    		newName = ',' + (String)changedModelNames.get(key) + '.';
    		if( copyOfSql.contains(oldName) ) {
    			copyOfSql = copyOfSql.replaceAll(oldName, newName);
    		}
    		// Replace all names with ',' preceeding
    		oldName = '\t' + (String)key + '.';
    		newName = '\t' + (String)changedModelNames.get(key) + '.';
    		if( copyOfSql.contains(oldName) ) {
    			copyOfSql = copyOfSql.replaceAll(oldName, newName);
    		}
    		// Replace all names with ',' preceeding
    		oldName = '\n' + (String)key + '.';
    		newName = '\n' + (String)changedModelNames.get(key) + '.';
    		if( copyOfSql.contains(oldName) ) {
    			copyOfSql = copyOfSql.replaceAll(oldName, newName);
    		}
		}
    	
    	// We've taken care of everything but the '(' preceeding char. Seems the replaceAll() can't handle it
    	// So we should find all indexes of the old name
    	StringBuffer sb = new StringBuffer(copyOfSql.length());
    	for( String key: changedModelNames.keySet() ) {
    		String oldName = '(' + (String)key + '.';
    		String newName = '(' + (String)changedModelNames.get(key) + '.';
    		while( copyOfSql.contains(oldName) ) {
    			int startIndex = copyOfSql.indexOf(oldName);
    			int endIndex = startIndex + oldName.length();
    			sb.append(copyOfSql.subSequence(0, startIndex)).append(newName);
    			copyOfSql = copyOfSql.substring(endIndex);
    		}
    		sb.append(copyOfSql);
    	}
    	return sb.toString();
    }
    
    private Map<String, String> getChangedNameMap(Map refactoredPaths) {
    	Map<String, String> nameMap = new HashMap<String, String>(refactoredPaths.size());
    	for( Object key: refactoredPaths.keySet()) {
    		String oldPathStr = (String)key;
    		String newPathStr = (String)refactoredPaths.get(key);
    		IPath oldPath = new Path(oldPathStr);
    		IPath newPath = new Path(newPathStr);
    		nameMap.put(oldPath.removeFileExtension().lastSegment(), newPath.removeFileExtension().lastSegment());
    	}
    	
    	return nameMap;
    }

    @Override
	public void helpUpdateModelContentsForDelete(
			Collection<Object> deletedResourcePaths,
			Collection<Object> directDependentResources,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub
	}

}
