/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.swt.widgets.Display;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants.Images;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.ui.internal.util.UiUtil;


/**
 * Action for deletion of connector types
 * @since 5.5.3
 */
public class DeleteConnectorTypeAction  extends ConfigurationManagerAction {

    /**
     * Constructor
     * @since 5.5.3
     */
    public DeleteConnectorTypeAction() {
        super(DqpUiConstants.UTIL.getString("DeleteConnectorTypeAction.label")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.DELETE_ICON));
    }

    /**
     *
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.5.3
     */
    @Override
    public void run() {
        // Get Selection
        Object[] selectedObjects = getSelectedObjects().toArray();
        if( selectedObjects != null ) {
            boolean success = false;
            Collection<File> jarFilesToDelete = getDeletableReferencedConnectorJars(selectedObjects);
            try {
                for( int i=0; i<selectedObjects.length; i++ ) {
                    if( selectedObjects[i] instanceof ComponentTypeID ) {
                        ComponentTypeID theTypeID = (ComponentTypeID)selectedObjects[i];
                        ConnectorType theType = DqpPlugin.getInstance().getAdmin().getComponentType(theTypeID.getName());
                        if( theType != null ) {
                            try {
                                getAdmin().removeConnectorType(theType);
                                success = true;
                            } catch (final Exception error) {
                                success = false;
                                UiUtil.runInSwtThread(new Runnable() {

                                    public void run() {
                                        DqpUiPlugin.showErrorDialog(Display.getCurrent().getActiveShell(), error);
                                    }
                                }, false);
                            }
                        }
                    }
                }
            } finally {
                if( success ) {
                    deleteConnectorJarFiles(jarFilesToDelete);
                }
            }
        }
    }

    /**
     *
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ConfigurationManagerAction#setEnablement()
     * @since 5.5.3
     */
    @Override
    protected void setEnablement() {
        boolean result = false;

        // Prevent deletion of standard connector types
        // All selected objects must be non-standard

        if(  !isEmptySelection() ) {
            Object[] selectedObjects = getSelectedObjects().toArray();
            result = areAllNonStandardConnectorTypes(selectedObjects);
        }

        setEnabled(result);
    }

    /**
     * Test whether the supplied connector types are all non-standard connector types.
     * @param objs the supplied array of connector types
     * @return <code>true</code> if the supplied connector types are all non-standard types.
     * @since 5.5.3
     */
    private boolean areAllNonStandardConnectorTypes(Object[] objs) {
    	boolean result = true;
    	if( objs != null ) {
    		for( Object obj: objs ) {
    			// If any of the objects are non-standard, quit false
    			if( obj instanceof ComponentTypeID ) {
    				ComponentTypeID typeID = (ComponentTypeID)obj;
    				ConnectorType theType = DqpPlugin.getInstance().getAdmin().getComponentType(typeID.getName());
    				boolean isStandard = DqpPlugin.getInstance().getAdmin().isStandardComponentType(theType);
    				if(isStandard) {
    					result = false;
    					break;
    				}
    			// Object is not a ComponentTypeID, quit false
    			} else {
    				result = false;
    				break;
    			}
    		}
    	}

    	return result;
    }

    /**
     * Collect any referenced jars for the supplied list of componentTypeIDs that can be
     * safely deleted (not used by anything else in workspace)
     * @param objs the supplied array of connector types
     * @return collection of referenced jar files which are not being used by anything else
     * @since 5.5.3
     */
    private Collection<File> getDeletableReferencedConnectorJars(Object[] selectedComponentTypeIDs) {
    	// Get array of unselected component type ids
    	Object[] unSelectedComponentTypeIDs = this.getUnselectedComponentTypeIDs(selectedComponentTypeIDs);

    	// Get unique jarNames for selected and unselected types
        Set<String> jarNamesForSelectedTypes = getRequiredJarNamesForComponentTypeIDs(selectedComponentTypeIDs);
        Set<String> jarNamesForUnselectedTypes = getRequiredJarNamesForComponentTypeIDs(unSelectedComponentTypeIDs);

        // Remove standard type jars and udf jars from the sets
        removeUdfJars(jarNamesForSelectedTypes);
        removeUdfJars(jarNamesForUnselectedTypes);

        // Create a list of the deletable jars
    	List<File> jarFilesToDelete = new ArrayList<File>();

    	// Iterate all jar files
    	List<File> nonStdJars = DqpPlugin.getInstance().getExtensionsHandler().getExtensionJarFiles();
    	for(Iterator<File> jIter = nonStdJars.iterator(); jIter.hasNext();) {
    		File jarFile = jIter.next();
    		String jarName = jarFile.getName();
    		// jar *IS* in the list for the selected types, but *NOT* in the list for unselected types
    		if(jarNamesForSelectedTypes.contains(jarName) && !jarNamesForUnselectedTypes.contains(jarName)) {
    			jarFilesToDelete.add(jarFile);
    		}
    	}
    	return jarFilesToDelete;
    }

    /**
     * Delete the supplied list of connector jar files.
     * @param jarFiles the supplied list of jar Files for deletion.
     * @since 5.5.3
     */
    private void deleteConnectorJarFiles(Collection<File> jarFilesToDelete) {
    	// Go ahead with the jar deletion.
        if (!jarFilesToDelete.isEmpty()) {
            DqpExtensionsHandler handler = DqpPlugin.getInstance().getExtensionsHandler();
            handler.deleteConnectorJars(this, jarFilesToDelete.toArray(new File[jarFilesToDelete.size()]));
        }
    }

    /**
     * Remove any UDF or Standard jars from the supplied jar set.
     * @param targetJars the list of target Jars
     * @param deletableJars the original list of target Jars, minus any UDF jars or Standard Connector jars.
     * @since 5.5.3
     */
     private void removeUdfJars(Set<String> jarNames) {
    	 DqpExtensionsHandler dqpExtHandler = DqpPlugin.getInstance().getExtensionsHandler();
    	for(Iterator<String> iter = jarNames.iterator(); iter.hasNext();) {
    		String jarName = iter.next();
        	// If the jar is a UDF jar remove it from the supplied set.
            if( dqpExtHandler.isUdfJar(jarName) ) {
            	iter.remove();
            }
    	}
    }


     /**
      * Get an array of the unselected workspace component type IDs.
      * @param selectedComponentTypeIDs the list of selected ComponentTypeID objects
      * @return the array of workspace component type IDs that are not in the selected list
      * @since 5.5.3
      */
     private Object[] getUnselectedComponentTypeIDs (Object[] selectedComponentTypeIDs) {
     	Collection<ComponentTypeID> unselectedTypeIDs = new ArrayList<ComponentTypeID>();

		// Iterate all type ids in the workspace, looking for selected
     	// if not selected, add to unselected list
     	Collection<ComponentTypeID> allTypeIDs = DqpPlugin.getInstance().getAdmin().getConnectorTypeIds();
     	for(Iterator<ComponentTypeID> tIter = allTypeIDs.iterator(); tIter.hasNext();) {
     		ComponentTypeID typeID = tIter.next();
     		boolean found = false;
    	    // Iterate thru all of the selected type ids.
     		for(int i=0; i<selectedComponentTypeIDs.length; i++) {
     			ComponentTypeID selectedID = (ComponentTypeID)selectedComponentTypeIDs[i];
     			if(typeID.equals(selectedID)) {
     				found = true;
     				break;
     			}
     		}
     		if(!found) {
     			if(!unselectedTypeIDs.contains(typeID)) {
     				unselectedTypeIDs.add(typeID);
     			}
     		}
     	}

     	return unselectedTypeIDs.toArray();
     }

     /**
      * Get the Set of required jarNames for the supplied ComponentTypeID objects.  This is the collection of all
      * jarNames required by the componentTypes and by all of the connectors of the supplied types.  No filtering of UDF
      * or standard type jars is done on the returned list.
      * @param componentTypeIDs the list of supplied ComponentTypeID objects
      * @return the set of required jar names for the supplied ComponentTypeID objs.
      * @since 5.5.3
      */
     private Set<String> getRequiredJarNamesForComponentTypeIDs(Object[] componentTypeIDs) {
         Set<String> jarNames = new HashSet<String>();

         // Add required jars for the ConnectorTypes
         for( int i=0; i<componentTypeIDs.length; i++ ) {
             if( componentTypeIDs[i] instanceof ComponentTypeID ) {
                 ComponentTypeID theTypeID = (ComponentTypeID)componentTypeIDs[i];
                 ConnectorType theType = DqpPlugin.getInstance().getAdmin().getConnectorType(theTypeID);
                 if(theType instanceof ConnectorType) {
                     Collection<String> typeJars = ModelerDqpUtils.getRequiredExtensionJarNames(theType);
                     jarNames.addAll(typeJars);
                 }
             }
         }

         // Now add required jars for all connectors of the supplied types
         Set<ConnectorBinding> conns = getConnectorsForTypeIDs(componentTypeIDs);
         for(Iterator<ConnectorBinding> connIter = conns.iterator(); connIter.hasNext();) {
        	 ConnectorBinding conn = connIter.next();
        	 Collection<String> connJars = ModelerDqpUtils.getRequiredExtensionJarNames(conn);
        	 jarNames.addAll(connJars);
         }
         return jarNames;
     }

     /**
      * Get the Set of ConnectorBinding objects which use the supplied ComponentTypeID objects.
      * @param componentTypeIDs the list of supplied ComponentTypeID objects
      * @return the set of ConnectorBindings which use the supplied ComponentTypeID objs.
      * @since 5.5.3
      */
     private Set<ConnectorBinding> getConnectorsForTypeIDs(Object[] componentTypeIDs) {
    	 Set<ConnectorBinding> conns = new HashSet<ConnectorBinding>();

    	 // Get collection of all connectors, keep connectors that have matching types
    	 Collection<ConnectorBinding> allConns = DqpPlugin.getInstance().getAdmin().getConnectorBindings();
    	 for(Iterator<ConnectorBinding> cIter = allConns.iterator(); cIter.hasNext();) {
    		 ConnectorBinding conn = cIter.next();
    		 // If connector binding type matches one of the supplied types, add it to set
        	 for( int i=0; i<componentTypeIDs.length; i++ ) {
                 if( componentTypeIDs[i] instanceof ComponentTypeID ) {
                     ComponentTypeID theTypeID = (ComponentTypeID)componentTypeIDs[i];
                     if(conn.getComponentTypeID().equals(theTypeID)) {
                    	 conns.add(conn);
                     }
                 }
             }
    	 }
    	 return conns;
     }

}
