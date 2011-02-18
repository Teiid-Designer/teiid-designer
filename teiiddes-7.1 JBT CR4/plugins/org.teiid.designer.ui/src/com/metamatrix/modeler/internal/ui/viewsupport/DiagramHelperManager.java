/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.HashMap;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.IDiagramHelper;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class DiagramHelperManager {
	
	/** Array of all extensions to the DiagramHelper extension point */
	private static HashMap diagramHelpers;
	private static boolean helpersLoaded = false;

	public static boolean canRename(Diagram diagram) {
		if( getDiagramHelper(diagram) != null )
			return (getDiagramHelper(diagram)).canRename(diagram);
		
		return false;
	}

	public static boolean canCopy(Diagram diagram) {
		if( getDiagramHelper(diagram) != null )
			return (getDiagramHelper(diagram)).canCopy(diagram);
		
		return false;
	}

	public static boolean canCut(Diagram diagram) {
		if( getDiagramHelper(diagram) != null )
			return (getDiagramHelper(diagram)).canCut(diagram);
		
		return false;
	}

	public static boolean canDelete(Diagram diagram) {
		if( getDiagramHelper(diagram) != null )
			return (getDiagramHelper(diagram)).canDelete(diagram);
		
		return false;
	}
    
	public static boolean canClone(Diagram diagram) {
		if( getDiagramHelper(diagram) != null )
			return (getDiagramHelper(diagram)).canClone(diagram);
		
		return false;
	}
	
    
	public static boolean canPaste(Diagram diagram, EObject pasteParent) {
		if( getDiagramHelper(diagram) != null )
			return (getDiagramHelper(diagram)).canPaste(diagram, pasteParent);
		
		return false;
	}
	
	public static void paste(Diagram diagram, EObject pasteParent) {
		if( getDiagramHelper(diagram) != null )
			(getDiagramHelper(diagram)).paste(diagram, pasteParent);
	}
	
    
	public static boolean canCreate(Diagram diagram) {
		if( getDiagramHelper(diagram) != null )
			return (getDiagramHelper(diagram)).canCreate(diagram);
		
		return false;
	}
	
	public static IDiagramHelper getDiagramHelper(Diagram diagram) {
		if( !helpersLoaded )
			loadDiagramHelperExtensions();
			
		if( diagramHelpers != null ) {
			return (IDiagramHelper)diagramHelpers.get(diagram.getType());
		}
		return null;
	}

	private static void loadDiagramHelperExtensions() {
		diagramHelpers = new HashMap();
		helpersLoaded = true;
		
		// get the NewChildAction extension point from the plugin class
		String id = UiConstants.ExtensionPoints.DiagramHelperExtension.ID;
		String classTag = UiConstants.ExtensionPoints.DiagramHelperExtension.CLASS;
		String className = UiConstants.ExtensionPoints.DiagramHelperExtension.CLASSNAME;
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, id);
		
		// get the all extensions to the NewChildAction extension point
		IExtension[] extensions = extensionPoint.getExtensions();
		
		// walk through the extensions and find all INewChildAction implementations
		for ( int i=0 ; i<extensions.length ; ++i ) {
			IConfigurationElement[] elements = extensions[i].getConfigurationElements();
			try {

				// first, find the content provider instance and add it to the instance list
				for ( int j=0 ; j<elements.length ; ++j ) {
					if ( elements[j].getName().equals(classTag)) {
						Object helper = elements[j].createExecutableExtension(className);
						if ( helper instanceof IDiagramHelper ) {
							String diagramType = 
								elements[j].getAttribute(UiConstants.ExtensionPoints.DiagramHelperExtension.DIAGRAM_TYPE);
							diagramHelpers.put(diagramType, helper);
						}
					}
				}
            
			} catch (Exception e) {
				// catch any Exception that occurred obtaining the configuration and log it
				String message = UiConstants.Util.getString("ModelerActionService.configurationErrorMessage", //$NON-NLS-1$
							extensions[i].getUniqueIdentifier()); 
				UiConstants.Util.log(IStatus.ERROR, e, message);
			}
		}
	}


}
