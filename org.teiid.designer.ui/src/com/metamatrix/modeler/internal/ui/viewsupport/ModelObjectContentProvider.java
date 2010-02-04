/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.PresentationEntity;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;

/**
 * <p>ModelObjectContentProvider is the global ContentProvider for all access to ModelResources
 * and all objects inside ModelResources.</p>
 * <p>ModelObjectContentProvider implements ITreeContentProvider because this interface has the right
 * methods for the functionality this class provides.</p>
 * <p>ModelObjectContentProvider is a singleton to ensure that any operations it performs on a given
 * ModelResource is safe and synchronous.</p>
 */
final public class ModelObjectContentProvider 
    implements ITreeContentProvider, UiConstants.ExtensionPoints.DiagramContentProvider {

    // ===========================================
    // Static

    private static final Object[] NO_CHILDREN = new Object[0];
    private static ModelObjectContentProvider theInstance;


    /** list of ITreeContentProviders from the diagramProvider extension point */
    private static final ArrayList diagramProviders = new ArrayList();
    
    /** key=diagram type ID, value=ITreeContentProvider to use for getParent */
    private static final HashMap diagramProviderIdMap = new HashMap();

    private static ExtendedModelObjectContentProvider extendedContentProvider = new ExtendedModelObjectContentProvider();
    
    /** DEBUG flag to remove any diagram lookup & creation logic. Should be false */
    private static boolean IGNORE_DIAGRAMS = false;
    
    /**
     * Obtain the singleton instance of ModelObjectContentProvider.
     */
    public static ModelObjectContentProvider getInstance() {
        if ( theInstance == null ) {
            theInstance = new ModelObjectContentProvider();
            theInstance.loadProviderList();
        }
        return theInstance;
    }

    // ===========================================
    // Constructors

    /**
     * Construct an instance of ModelObjectContentProvider.
     */
    private ModelObjectContentProvider() {
    }
    
    private void loadProviderList() {
        // -------------------------------------------------------------------------------------------------------
        // build a list of all DiagramProvider contributions of type ILabelProvider
        // -------------------------------------------------------------------------------------------------------
        
        // get the DiagramProvider extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, DIAGRAM_ID);
        // get the all extensions to the DiagramProvider extension point
        IExtension[] extensions = extensionPoint.getExtensions();
    
        // walk through the extensions and find all ITreeContentProviders
        for ( int i=0 ; i<extensions.length ; ++i ) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();

            try {
    
                // first, find the content provider instance and add it to the instance list
                ITreeContentProvider contentProvider = null;
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(DIAGRAM_CLASS)) {
                        Object provider = elements[j].createExecutableExtension(DIAGRAM_CLASSNAME);
                        if ( provider instanceof ITreeContentProvider ) {
                            contentProvider = (ITreeContentProvider) provider;
                            diagramProviders.add(provider);
                            break;
                        }
                    }
                }

                // second, build a map referencing all the diagram types that this provider supports
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(DIAGRAM_TYPE)) {
                        String type = elements[j].getAttribute(DIAGRAM_TYPE_NAME);
                        diagramProviderIdMap.put(type, contentProvider);
                    }
                }
                
            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = UiConstants.Util.getString("ModelObjectContentProvider.configurationErrorMessage", //$NON-NLS-1$
                            extensions[i].getUniqueIdentifier()); 
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }
    }    

    /**
     * Walks through all DiagramContentProvider contributions and asks them for children
     * of the parentElement.  They may obtain previously created and saved diagrams, or they
     * may create new diagrams.  Any children they return are integrated into the child array
     * returned from the getChildren method.
     * @param parentElement
     * @return
     */
    private static ArrayList getDiagramChildren(Object parentElement) {  
        ArrayList result = new ArrayList();
        
        if(isXsdObject(parentElement) ){
            return result;
        }
        
        final boolean startedTxn = ModelerCore.startTxn(false, true, null, theInstance);          
        boolean succeeded = false;
        try{
            for ( Iterator iter = diagramProviders.iterator() ; iter.hasNext() ; ) {
                ITreeContentProvider provider = (ITreeContentProvider) iter.next();
                try {
                    Object[] diagrams = provider.getChildren(parentElement);
                    if ( diagrams != null && diagrams.length > 0 ) {
                        result.addAll(Arrays.asList(diagrams));
                    }
                } catch (Exception e) {
                    // catch any Exception that occurred in the diagram provider and log it
                    String message = UiConstants.Util.getString("ModelObjectContentProvider.diagramProviderErrorMessage"); //$NON-NLS-1$
                    UiConstants.Util.log(IStatus.ERROR, e, message);
                }
            }
            // not really sure if I should roll back if any exceptions get caught.
            succeeded = true;
        } finally {
            if( startedTxn ){
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return result;
    }

    private static boolean isXsdObject(final Object obj){
        if(obj == null){
            return false;
        }
        
        if(obj instanceof ModelResource){
            return ((ModelResource)obj).isXsd();
        }
        
        if(obj instanceof EObject){
            final Resource rsrc = ((EObject)obj).eResource();
            return ModelUtil.isXsdFile(rsrc);
        }
        
        if(obj instanceof Resource){
            return ModelUtil.isXsdFile( (Resource)obj );
        }
            
        return false;
    }
    
    // ===========================================
    // ITreeContentProvider Methods

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public synchronized Object[] getChildren(Object parentElement) {
        Object[] children = NO_CHILDREN;

        try {
            
            //BusyCursor.showBusy();

            if ( parentElement instanceof PresentationEntity ) {
                // then NO_CHILDREN is the right answer

            } else if ( (parentElement instanceof EObject)
                      || (parentElement instanceof ModelResource) ) {
                
                Object[] emfChildren = null;
                
                if ( parentElement instanceof EObject ) {
                    emfChildren = ModelUtilities.getModelContentProvider().getChildren(parentElement);
                } else {
                    try {
                        emfChildren = ((ModelResource) parentElement).getEObjects().toArray();
                    } catch ( ModelWorkspaceException exception ) {
                        if ( ((ModelResource)parentElement).hasErrors() ) {
                            // There are errors, so simply return an empty list ...
                            return children;
                        }
                        throw exception;
                    }
                }

                
                // Find any additional extended children for object
                Object[] extendedChildren = extendedContentProvider.getChildren(parentElement);
                int totalChildren = emfChildren.length;
                if( extendedChildren != null && extendedChildren.length > 0 ) {
                    totalChildren += extendedChildren.length;
                }

                Object[] tmpChildren = new Object[totalChildren];
                
                for ( int i=0 ; i<emfChildren.length ; ++i ) {
                    tmpChildren[i] = emfChildren[i];
                }
                
                if( extendedChildren != null && extendedChildren.length > 0) {
                    int theIndex = emfChildren.length;
                    for( int i = 0 ; i < extendedChildren.length ; ++i) {
                        tmpChildren[theIndex++] = extendedChildren[i];
                    }
                }

                if ( IGNORE_DIAGRAMS ) {
                    return (tmpChildren);
                }

                ArrayList diagramList = getDiagramChildren(parentElement);
                if ( diagramList.isEmpty() ) {
                    children = tmpChildren;
                } else {
                    ArrayList allChildren = new ArrayList(diagramList);
                    for ( int i=0 ; i<tmpChildren.length ; ++i ) {
                        allChildren.add(tmpChildren[i]);
                    }
                    children = allChildren.toArray();
                }
            }

        } catch (CoreException ex) {
            children = NO_CHILDREN;
        } finally {
            //BusyCursor.endBusy();
        }

        return children;
    }

    /* 
     * Get children, with model contents sorted in alphabetical order
     */
    public synchronized Object[] getSortedChildren(Object parentElement) {
        Object[] children = NO_CHILDREN;

        try {
            
            //BusyCursor.showBusy();

            if ( parentElement instanceof PresentationEntity ) {
                // then NO_CHILDREN is the right answer

            } else if ( (parentElement instanceof EObject)
                      || (parentElement instanceof ModelResource) ) {
                
                
                // Get the children (raw order)
                Object[] emfChildren = null;
                
                if ( parentElement instanceof EObject ) {
                    emfChildren = ModelUtilities.getModelContentProvider().getChildren(parentElement);
                } else {
                    try {
                        emfChildren = ((ModelResource) parentElement).getEObjects().toArray();
                    } catch ( ModelWorkspaceException exception ) {
                        if ( ((ModelResource)parentElement).hasErrors() ) {
                            // There are errors, so simply return an empty list ...
                            return children;
                        }
                        throw exception;
                    }
                }
                
                List eClasses = new ArrayList();
                // Put the children into List of Name-Value pair objects
                List nameValueList = new ArrayList(emfChildren.length);
                for(int i=0; i<emfChildren.length; i++) {
                    Object child = emfChildren[i];
                    if(child instanceof EObject) {
                        EObject eObj = (EObject)child;
                        String name = ModelerCore.getModelEditor().getName(eObj);
                        String eClassName = eObj.eClass().getName();
                        nameValueList.add(new NameValuePair(name,eObj,eClassName));
                        if(!eClasses.contains(eClassName)) {
                            eClasses.add(eClassName);
                        }
                    }
                }
                
                // Sort the NameValueList, alpha sort each Class separately in the order provided
                List sortedNameValueList = sortNameValueList(nameValueList,eClasses);
                
                // Sorted Object array
                Object[] sortedChildren = new Object[sortedNameValueList.size()];
                for(int i=0; i<sortedNameValueList.size(); i++) {
                    NameValuePair nvPair = (NameValuePair)sortedNameValueList.get(i);
                    sortedChildren[i] = nvPair.getValue();
                }
                
                // Find any additional extended children for object
                Object[] extendedChildren = extendedContentProvider.getChildren(parentElement);
                int totalChildren = emfChildren.length;
                if( extendedChildren != null && extendedChildren.length > 0 ) {
                    totalChildren += extendedChildren.length;
                }

                Object[] tmpChildren = new Object[totalChildren];
                
                for ( int i=0 ; i<sortedChildren.length ; ++i ) {
                    tmpChildren[i] = sortedChildren[i];
                }
                
                if( extendedChildren != null && extendedChildren.length > 0) {
                    int theIndex = sortedChildren.length;
                    for( int i = 0 ; i < extendedChildren.length ; ++i) {
                        tmpChildren[theIndex++] = extendedChildren[i];
                    }
                }


                if ( IGNORE_DIAGRAMS ) {
                    return (sortedChildren);
                }

                ArrayList diagramList = getDiagramChildren(parentElement);
                if ( diagramList.isEmpty() ) {
                    children = tmpChildren;
                } else {
                    ArrayList allChildren = new ArrayList(diagramList);
                    for ( int i=0 ; i<tmpChildren.length ; ++i ) {
                        allChildren.add(tmpChildren[i]);
                    }
                    children = allChildren.toArray();
                }
            }

        } catch (CoreException ex) {
            children = NO_CHILDREN;
        } finally {
            //BusyCursor.endBusy();
        }

        return children;
    }
    
    private List sortNameValueList(List nameValueList, List eClassList) {
        List resultList = new ArrayList(nameValueList.size());
        
        // Iterate the eClass name list, group the like elements
        Iterator eClassIter = eClassList.iterator();
        while(eClassIter.hasNext()) {
            String currentEClass = (String)eClassIter.next();
            if(currentEClass!=null) {
                // Iterate the entire name value list, create a subList of only the current eClass
                List nameValueSubList = new ArrayList(nameValueList.size());
                Iterator nvIter = nameValueList.iterator();
                while(nvIter.hasNext()) {
                    NameValuePair nvPair = (NameValuePair)nvIter.next();
                    if( currentEClass.equals(nvPair.getEClassName()) ) {
                        nameValueSubList.add(nvPair);
                    }
                }
                // Sort the subList for the current eClass
                Collections.sort(nameValueSubList);
                // Add the sorted List to the result List
                resultList.addAll(nameValueSubList);
            }
        }

        return resultList;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        // don't dispose anything - this is a static instance.
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        Object result = null;
        if ( element instanceof Diagram ) {
            String type = ((Diagram) element).getType();
            if ( type != null ) {
                ITreeContentProvider provider = (ITreeContentProvider) diagramProviderIdMap.get(type);
                if ( provider != null ) {
                    try {
                        result = provider.getParent(element);
                    } catch (Exception e) {
                        // catch any Exception that occurred in the diagram provider and log it
                        String message = UiConstants.Util.getString("ModelObjectContentProvider.diagramProviderErrorMessage"); //$NON-NLS-1$
                        UiConstants.Util.log(IStatus.ERROR, e, message);
                    }
                }
            }
        } else if (element instanceof EObject) {
            result = ModelUtilities.getModelContentProvider().getParent(element);
            if ( result instanceof ModelResource ) {
                result = ((ModelResource) result).getResource();
            } else if ( result instanceof Resource ) {
                result = ModelerCore.getModelEditor().findModelResource((Resource) result);
                if ( result != null ) {
                    result = ((ModelResource) result).getResource();
                }
            }
        } else if( extendedContentProvider.getParent(element) != null ) {
            result = extendedContentProvider.getParent(element);
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
		return hasChildren(element, false);
    	
//        Object[] children= getChildren(element);
//        return (children != null) && children.length > 0;
    }

	private boolean hasChildren(Object parentElement, boolean dummyBoolean) {
		if ( parentElement instanceof PresentationEntity ) {
			return false;
		} else if ( (parentElement instanceof EObject)
				  || (parentElement instanceof ModelResource) ) {
			try {
				if ( parentElement instanceof EObject ) {
					if( ModelUtilities.getModelContentProvider().hasChildren(parentElement) )
						return true;
				} else {
					if( ((ModelResource) parentElement).getEObjects().size() > 0 )
						return true;
				}
				if( hasDiagramChildren(parentElement) )
					return true;
				
				if( getDiagramChildren(parentElement).size() > 0 )
					return true;
					
			}catch (CoreException e) {
			    e.printStackTrace();
		    } finally {
		    }
		} else if( parentElement instanceof IExtendedModelObject ){
            return extendedContentProvider.hasChildren(parentElement);
        }
		
		return false;
	}
	
	private boolean hasDiagramChildren(Object parentElement) {
		if(isXsdObject(parentElement) ){
			return false;
		}
        
		for ( Iterator iter = diagramProviders.iterator() ; iter.hasNext() ; ) {
			ITreeContentProvider provider = (ITreeContentProvider) iter.next();
			if( provider.hasChildren(parentElement) )
				return true;
		}
		
		return false;
	}
    
    class NameValuePair implements Comparable {
        private String name;
        private String eClassName;
        private Object value;
        
        public NameValuePair(String name, Object value, String eClassName) {
            this.name = name;
            this.value = value;
            this.eClassName = eClassName;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public String getEClassName() {
            return this.eClassName;
        }
        
        public int compareTo(Object o) {
            int result = -1;
            if (o instanceof NameValuePair) {
                NameValuePair col2 = (NameValuePair) o;
                if ( this.name == null ) {
                    result = ( col2.getName() == null ? 0 : -1 );
                }
                else if ( col2.getName() == null ) {
                    result = 1;
                }
                else {
                    result = this.getName().compareToIgnoreCase(col2.getName());
                }
            }
            return result;
        }
    }
}
