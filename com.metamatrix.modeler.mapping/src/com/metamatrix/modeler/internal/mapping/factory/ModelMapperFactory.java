/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.mapping.DebugConstants;
import com.metamatrix.modeler.mapping.PluginConstants;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;

/**
 * ModelMapperFactory
 */
public class ModelMapperFactory implements DebugConstants,
                                           PluginConstants,
                                           PluginConstants.ExtensionPoints.ModelMapper {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Used in logging debug messages. */
    private static final Class CLASS = ModelMapperFactory.class;
    
    /** Localization prefix found in properties file key words. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(CLASS);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        buildMapperMaps();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Map used to create a new instance of a Mapper upon request. */
    private static Map uriConfigElementMap; // key=metamodel URI; value=IConfigurationElement
    
    /** Map used to see if a known Mapper has a tree root of a certain type. */
    private static Map uriMapperMap; // key=metamodel URI; value=ITreeToRelationalMapper
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** Don't allow construction. */
    private ModelMapperFactory() {}
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static void buildMapperMaps() {
        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printEntered(CLASS, "buildMapperMap()"); //$NON-NLS-1$
        }
        
        uriConfigElementMap = new HashMap();
        uriMapperMap = new HashMap();

        // get the ModelObjectActionContributor extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, ID);

        // get the all extensions to the ModelObjectActionContributor extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        if (extensions.length > 0) {
            // for each extension get their contributor
            for (int i = 0; i < extensions.length; i++) {
                IConfigurationElement[] elements = extensions[i].getConfigurationElements();
                Object extension = null;
                String metamodelUri = null;

                if (Util.isDebugEnabled(MODEL_MAPPER)) {
                    Util.print(CLASS, "buildMapperMap():found " + elements.length + " mapper extensions"); //$NON-NLS-1$ //$NON-NLS-2$
                }

                for (int j = 0; j < elements.length; j++) {
                    try {
                        if (Util.isDebugEnabled(MODEL_MAPPER)) {
                            Util.print(CLASS, "buildMapperMap():processing " + elements[j].getAttribute(CLASSNAME)); //$NON-NLS-1$
                        }

                        extension = elements[j].createExecutableExtension(CLASSNAME);

                        if (extension instanceof ITreeToRelationalMapper) {
                            metamodelUri = elements[j].getAttribute(METAMODEL_URI);
                            
                            if (Util.isDebugEnabled(MODEL_MAPPER)) {
                                Util.print(CLASS, "buildMapperMap():model URI=<" + metamodelUri + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                            }

                            if ((metamodelUri != null) && (metamodelUri.length() > 0)) {
                                uriMapperMap.put(metamodelUri, extension);
                                uriConfigElementMap.put(metamodelUri, elements[j]);
                            } else {
                                Util.log(IStatus.ERROR, Util.getString(PREFIX + "invalidModelId", //$NON-NLS-1$
                                                                       new Object[] {extension.getClass().getName()}));
                            }
                        } else {
                            Util.log(IStatus.ERROR, Util.getString(PREFIX + "invalidMapperClass", //$NON-NLS-1$
                                                                   new Object[] {extension.getClass().getName()}));
                        }
                    } catch (Exception theException) {
                        Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "loadingMapperProblem", //$NON-NLS-1$
                                                                             new Object[] {elements[j].getAttribute(CLASSNAME)}));
                    }
                }
            }
        }

        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printExited(CLASS, "buildMapperMap():uriConfigElementMap=" + uriConfigElementMap); //$NON-NLS-1$
        }
    }
    
    /**
     * Creates the <code>ITreeToRelationalMapper</code> for the specified metamodel URI.
     * @param theMetamodelUri the metamodel URI whose mapper is being requested
     * @return the mapper or <code>null</code> if none found
     * @throws IllegalArgumentException if the metamodel URI is <code>null</code> or empty
     * 
     */
    public static ITreeToRelationalMapper createModelMapper(String theMetamodelUri) {
        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printEntered(CLASS, "createModelMapper(String):theMetamodelUri=" + theMetamodelUri); //$NON-NLS-1$
        }
        
        ArgCheck.isNotNull(theMetamodelUri);
        ArgCheck.isNotEmpty(theMetamodelUri);
        
        ITreeToRelationalMapper result = null;
        
        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.print(CLASS, new StringBuffer().append("createModelMapper(String):model URI=") //$NON-NLS-1$
                                                .append(theMetamodelUri)
                                                .append(", IConfigurationElement=") //$NON-NLS-1$
                                                .append(uriConfigElementMap.get(theMetamodelUri))
                                                .toString());
        }
        
        IConfigurationElement configElement = (IConfigurationElement)uriConfigElementMap.get(theMetamodelUri);
        
        if (configElement != null) {
            try {
                result = (ITreeToRelationalMapper)configElement.createExecutableExtension(CLASSNAME);
            } catch (CoreException theException) {
                Util.log(IStatus.ERROR, Util.getString(PREFIX + "createMapperProblem", //$NON-NLS-1$
                                                       new Object[] {theMetamodelUri,
                                                                     configElement.getAttribute(CLASSNAME)}));
            }
        }

        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printExited(CLASS, "createModelMapper(String):mapper=" + result); //$NON-NLS-1$
        }

        return result;
    }
    
    /**
     * Creates the appropriate <code>ITreeToRelationalMapper</code> for the specified tree node.
     * @param theTreeNode the tree node whose mapper is being requested
     * @return the mapper or <code>null</code> if none exists
     * @throws IllegalArgumentException if the tree node is <code>null</code>
     */
    public static ITreeToRelationalMapper createModelMapper(EObject theTreeNode) {
        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printEntered(CLASS, "createModelMapper(EObject):theTreeNode=" + theTreeNode); //$NON-NLS-1$
        }
        
        ArgCheck.isNotNull(theTreeNode);
        
        ITreeToRelationalMapper result = null;
        String uri = getMetamodelUri(theTreeNode);
        
        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.print(CLASS, "createModelMapper(EObject):metamodelUri=" + uri); //$NON-NLS-1$
        }

        if (uri != null) {
            result = createModelMapper(uri);
        }

        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printExited(CLASS, "createModelMapper(EObject):mapper=" + result); //$NON-NLS-1$
        }
        if( result != null ) {
            result.setTreeRoot(theTreeNode);
        }
        return result;
    }
    
    /**
     * Obtains the metamodel URI for the specified tree node.  If the tree node
     * is contained within an EMF resource that is not a Federate Designer model resource,
     * for example an XSD resource, then null will be returned.
     * @param theTreeNode the tree node whose metamodel URI is being requested
     * @return the metamodel URI; may return null for unsupported metamodels
     * @throws IllegalArgumentException if the tree node is <code>null</code>
     */
    private static String getMetamodelUri(EObject theTreeNode) {
        ArgCheck.isNotNull(theTreeNode);

        // Get the container to use when resolving the EObject if it is a proxy ...
        Container container = ModelerCore.getContainer(theTreeNode);
        if (container == null) {
            try {
                // The container is null if "theTreeNode" is a proxy  
                container = ModelerCore.getModelContainer();
            } catch (CoreException err) {
                ModelerCore.Util.log(err);
            }
        }
        // First try finding the primary metamodel URI directly from the EMF resource ...
        Resource eResource = ModelerCore.getModelEditor().findResource(container,theTreeNode);
        if (eResource instanceof EmfResource) {
            final ModelAnnotation annot = ((EmfResource)eResource).getModelAnnotation();
            if (annot != null && annot.getPrimaryMetamodelUri() != null) {
                return annot.getPrimaryMetamodelUri();
            }
        }
        return null;
        
//        // 
//        String result = null;
//        ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(theTreeNode);
//        if( modelResource != null ) {
//            try {
//            	MetamodelDescriptor mmd = modelResource.getPrimaryMetamodelDescriptor();
//            	if( mmd != null )
//            		result = mmd.getURI();
////                result = modelResource.getModelAnnotation().getPrimaryMetamodelUri();
//            } catch (ModelWorkspaceException theException) {
//                Util.log(IStatus.ERROR, Util.getString(PREFIX + "metamodelUriProblem", //$NON-NLS-1$
//                                                       new Object[] {theTreeNode, theTreeNode.getClass().getName()}));
//            } 
//        }
//
//        return result;
    }
    
    /**
     * Indicates if the specifed tree node is a root in one of the loaded <code>ITreeToRelationalMapper</code>s.
     * @param theTreeNode the tree node being checked
     * @return <code>true</code> if a tree root; <code>false</code> otherwise.
     * @throws IllegalArgumentException if the tree node is <code>null</code>
     */
    public static boolean isTreeRoot(EObject theTreeNode) {
        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printEntered(CLASS, "isTreeRoot(EObject):theTreeNode=" + theTreeNode); //$NON-NLS-1$
        }
        
        ArgCheck.isNotNull(theTreeNode);

        boolean result = false;
        String uri = getMetamodelUri(theTreeNode);
        if (uri != null) {
            ITreeToRelationalMapper mapper = (ITreeToRelationalMapper)uriMapperMap.get(uri);
            
            if (mapper == null) {
                // put null value in map for URI for efficiency
                if (!uriMapperMap.containsKey(uri)) {
                    uriMapperMap.put(uri, null);
                }
            } else {
                result = mapper.isTreeRoot(theTreeNode);
            }
            
            if (Util.isDebugEnabled(MODEL_MAPPER)) {
                Util.printExited(CLASS, "isTreeRoot(EObject):result=" + result + ", metamodel URI=" + uri); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        return result;
    }
    
	/**
	 * Indicates if the specifed tree node is a root in one of the loaded <code>ITreeToRelationalMapper</code>s.
	 * @param theTreeNode the tree node being checked
	 * @return <code>true</code> if a tree root; <code>false</code> otherwise.
	 * @throws IllegalArgumentException if the tree node is <code>null</code>
	 */
	public static boolean isXmlTreeNode(EObject theTreeNode) {
		if (Util.isDebugEnabled(MODEL_MAPPER)) {
			Util.printEntered(CLASS, "isTreeRoot(EObject):theTreeNode=" + theTreeNode); //$NON-NLS-1$
		}
        
		ArgCheck.isNotNull(theTreeNode);

		boolean result = false;
        
        // Get the metamodel URI associated with this EObject.  The URI that is
        // returned may not be the primary metamodel URI of the resource if this
        // EObject is from one of our participatory metamodels (e.g. Diagrams, Transformation, ...)
        String uri = theTreeNode.eClass().getEPackage().getNsURI();
        ITreeToRelationalMapper mapper = (ITreeToRelationalMapper)uriMapperMap.get(uri);
        
//		String uri = getMetamodelUri(theTreeNode);
//      ITreeToRelationalMapper mapper = (ITreeToRelationalMapper)uriMapperMap.get(uri);
        
//		if (mapper == null) {
//			// put null value in map for URI for efficiency
//			if (!uriMapperMap.containsKey(uri)) {
//				uriMapperMap.put(uri, null);
//			}
//		} else {
//			result = mapper.isTreeNode(theTreeNode);
//		}

        if (mapper != null) {
            result = mapper.isTreeNode(theTreeNode);
        }
        
		if (Util.isDebugEnabled(MODEL_MAPPER)) {
			Util.printExited(CLASS, "isTreeRoot(EObject):result=" + result + ", metamodel URI=" + uri); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return result;
	}

	
	/**
	 * gets the parent tree root for a specifed tree node is a tree (document).
	 * @param someTreeNode the tree node starting point
	 * @return treeRoot EObject
	 */
	public static EObject getTreeRoot(EObject someTreeNode ) {
		// Need to walk it's parents until you get isTreeRoot(node) == TRUE
		EObject treeRoot = null;
		if( isTreeRoot(someTreeNode)) {
			treeRoot = someTreeNode;
		} else if( someTreeNode.eContainer() != null ){
			EObject parent = someTreeNode.eContainer();
			// in case we accidently get to the model resource...
			boolean failure = false;
			
			while( treeRoot == null && !failure )  {
				if( isTreeRoot(parent) ) {
					treeRoot = parent;
				} else {
					if( parent.eContainer() != null )
						parent = parent.eContainer();
					else
						failure = true;
				}
			}
		}
		
		return treeRoot;
	}

    /**
     * Provides access to xml document tree node xsd component via a loaded <code>ITreeToRelationalMapper</code>s.
     * @param theTreeNode the tree node being checked
     * @return <code>EObject</code> xsd component
     * @throws IllegalArgumentException if the tree node is <code>null</code>
     */
    public static EObject getXsdComponent(EObject theTreeNode) {
        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printEntered(CLASS, "getXsdComponent(EObject):theTreeNode=" + theTreeNode); //$NON-NLS-1$
        }
        
        ArgCheck.isNotNull(theTreeNode);

        EObject result = null;
        
        // Get the metamodel URI associated with this EObject.  The URI that is
        // returned may not be the primary metamodel URI of the resource if this
        // EObject is from one of our participatory metamodels (e.g. Diagrams, Transformation, ...)
        String uri = theTreeNode.eClass().getEPackage().getNsURI();
        ITreeToRelationalMapper mapper = (ITreeToRelationalMapper)uriMapperMap.get(uri);
        
//      String uri = getMetamodelUri(theTreeNode);
//      ITreeToRelationalMapper mapper = (ITreeToRelationalMapper)uriMapperMap.get(uri);
        
//      if (mapper == null) {
//          // put null value in map for URI for efficiency
//          if (!uriMapperMap.containsKey(uri)) {
//              uriMapperMap.put(uri, null);
//          }
//      } else {
//          result = mapper.isTreeNode(theTreeNode);
//      }

        if (mapper != null) {
            result = mapper.getXsdComponent(theTreeNode);
        }
        
        if (Util.isDebugEnabled(MODEL_MAPPER)) {
            Util.printExited(CLASS, "getXsdComponent(EObject):result=" + result + ", metamodel URI=" + uri); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return result;
    }
}
