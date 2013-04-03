/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.refactor.IRefactorModelHandler;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.mapping.PluginConstants;
import org.teiid.designer.mapping.choice.IChoiceObject;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.xml.XmlChoice;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;


/**
 * This class provides the mapping plugin a mechanism to affect changes internal to xml document models during
 * Model refactoring operations. (i.e. Move & Rename)
 * 
 * In particular, a Rename of an XML Document model could result in mis-named models in Choice Criteria.
 * 
 *
 *
 * @since 8.0
 */
public class MappingRefactorModelHandler implements IRefactorModelHandler {

	@Override
	public void helpUpdateDependentModelContents(int type, ModelResource modelResource,
			Collection<PathPair> refactoredPaths, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(refactoredPaths, "refactoredPaths"); //$NON-NLS-1$

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
			Collection<PathPair> refactoredPaths, IProgressMonitor monitor) {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(refactoredPaths, "refactoredPaths"); //$NON-NLS-1$

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
	
    private void refactorChoiceCriteria(ModelResource mr, Collection<PathPair> refactoredPaths) throws ModelWorkspaceException {

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
    
    protected String refactorUserSql(String sqlStr, Collection<PathPair> refactoredPaths) {
    	// Only need to fix SQL if Models are renamed, so looking for 
        Collection<PathPair> changedModelNames = getChangedNamePairs(refactoredPaths);

    	// So we need to walk through the String and check for 

    	String copyOfSql = new StringBuffer(sqlStr).toString();

        for( PathPair pathPair: changedModelNames ) {
    		// Replace all names with ' ' preceeding
            String oldName = ' ' + pathPair.getSourcePath() + '.';
            String newName = ' ' + pathPair.getTargetPath() + '.';
    		if( copyOfSql.contains(oldName) ) {
    			copyOfSql = copyOfSql.replaceAll(oldName, newName);
    		}
    		// Replace all names with ',' preceeding
    		oldName = ',' + pathPair.getSourcePath() + '.';
    		newName = ',' + pathPair.getTargetPath() + '.';
    		if( copyOfSql.contains(oldName) ) {
    			copyOfSql = copyOfSql.replaceAll(oldName, newName);
    		}
    		// Replace all names with '\t' preceeding
    		oldName = '\t' + pathPair.getSourcePath() + '.';
    		newName = '\t' + pathPair.getTargetPath() + '.';
    		if( copyOfSql.contains(oldName) ) {
    			copyOfSql = copyOfSql.replaceAll(oldName, newName);
    		}
    		// Replace all names with '\n' preceeding
    		oldName = '\n' + pathPair.getSourcePath() + '.';
    		newName = '\n' + pathPair.getTargetPath() + '.';
    		if( copyOfSql.contains(oldName) ) {
    			copyOfSql = copyOfSql.replaceAll(oldName, newName);
    		}
		}

    	// We've taken care of everything but the '(' preceeding char. Seems the replaceAll() can't handle it
    	// So we should find all indexes of the old name
    	StringBuffer sb = new StringBuffer(copyOfSql.length());
    	for( PathPair pathPair: changedModelNames ) {
    	    String oldName = '(' + pathPair.getSourcePath() + '.';
    	    String newName = '(' + pathPair.getTargetPath() + '.';
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

    private Collection<PathPair> getChangedNamePairs(Collection<PathPair> refactoredPaths) {
    	List<PathPair> namePairs = new ArrayList<PathPair>(refactoredPaths.size());
    	for( PathPair pair : refactoredPaths) {
    		String oldPathStr = pair.getSourcePath();
    		String newPathStr = pair.getTargetPath();
    		IPath oldPath = new Path(oldPathStr);
    		IPath newPath = new Path(newPathStr);
    		namePairs.add(new PathPair(oldPath.removeFileExtension().lastSegment(), newPath.removeFileExtension().lastSegment()));
    	}

    	return namePairs;
    }

    @Override
	public void helpUpdateModelContentsForDelete(
			Collection<Object> deletedResourcePaths,
			Collection<Object> directDependentResources,
			IProgressMonitor monitor) {
		// Nothing to do
	}

    /**
     * {@inheritDoc}
     * 
    * @see org.teiid.designer.core.refactor.IRefactorModelHandler#preProcess(int, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public boolean preProcess(int refactoryType, IResource refactoredResource, IProgressMonitor monitor) {
   	return true;
   }

    /**
     * {@inheritDoc}
     * 
    * @see org.teiid.designer.core.refactor.IRefactorModelHandler#postProcess(int, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void postProcess(int refactoryType, IResource refactoredResource, IProgressMonitor monitor) {
   	// Nothing to do
   }
}
