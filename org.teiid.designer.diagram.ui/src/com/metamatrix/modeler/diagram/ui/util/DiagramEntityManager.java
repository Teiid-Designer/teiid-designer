/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.metamodels.diagram.AbstractDiagramEntity;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;

/**
 * @author BLaFond
 *
 * This class contains static utility methods which cashe the diagramEntity-eObject maps and
 * allow quick access to find DE's, instead of interating through the lists.
 */
public class DiagramEntityManager  {
	private static HashMap diagramMap;
    private static final String MMUUID = "mmuuid"; //$NON-NLS-1$
    private static boolean clearingStaleDiagrams = false;
    private static EventObjectListener eventObjectListener;
    private static boolean debugPrint = false;
    
    static {
            // Need to register a new listener here.        
        eventObjectListener = new EventObjectListener() {
            public void processEvent(EventObject event) {
                DiagramEntityManager.processEvent(event);
            }
        };
        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, eventObjectListener);
        } catch (EventSourceException e) {
            DiagramUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }

    }

    public static void addDiagram(Diagram diagram) {
		if( diagramMap == null ) {
			diagramMap = new HashMap();
		}
		String diagramUuid = getDiagramURI(diagram);
        
		// Now we check to see if diagram already has map
		if( diagramUuid != null && diagramMap.get(diagramUuid) == null ) {
			HashMap newMap = new HashMap();
			
			List diagramChildren = diagram.eContents();
			if (diagramChildren != null && !diagramChildren.isEmpty()) {
                AbstractDiagramEntity nextDE = null;
                String uuid = null;
				Iterator iter = diagramChildren.iterator();
				while (iter.hasNext() ) {
					nextDE = (AbstractDiagramEntity)iter.next();
					EObject someDEMO = nextDE.getModelObject();
                    if( someDEMO != null ) {
                        uuid = ModelObjectUtilities.getFullUuid(someDEMO);
                        if( isValidURI(uuid) ) {
                            if( debugPrint )
                                System.out.println(" DEM.addEntity()  Adding Entity to Map.  EObject = " + nextDE); //$NON-NLS-1$
                            newMap.put(uuid, nextDE);
                        }
                    }
				}
			}
            if( debugPrint )
                System.out.println(" DEM.addDiagram()  Add Diagram To Map = " + diagram); //$NON-NLS-1$
			diagramMap.put(diagramUuid, newMap);
		}
	}
	
	public static void removeDiagram(Diagram diagram) {
		if( diagramMap != null ) {
            String diagramUuid = getDiagramURI(diagram);
            if( diagramUuid != null ) {
    			HashMap existingMap = (HashMap)diagramMap.get(diagramUuid);
    			if( existingMap != null ) {
    				diagramMap.remove(diagramUuid);
                    if( debugPrint )
                        System.out.println(" DEM.removeDiagram()  Removing Diagram From Map = " + diagram); //$NON-NLS-1$
    			}
            }
		}
	}
	
	public static void addEntity(Diagram diagram, AbstractDiagramEntity entity, EObject eObject) {
		if( diagramMap != null ) {
            String diagramUuid = getDiagramURI(diagram);

            if( diagramUuid != null ) {
    			HashMap dMap = (HashMap)diagramMap.get(diagramUuid);
    			if( dMap != null ) {
                    String uuid = ModelObjectUtilities.getFullUuid(eObject);
                    if( isValidURI(uuid) ) {
        				Object existingDE = dMap.get(uuid);
        				if( existingDE == null ) {
                            if( debugPrint )
                                System.out.println(" DEM.addEntity()  Adding Entity to Map.  EObject = " + eObject); //$NON-NLS-1$
        					dMap.put(uuid, entity);
                        }
                    }
    			}
            }
		}
	}
	
	
	public static void removeEntity(Diagram diagram, EObject eObject) {
		if( diagramMap != null ) {
            String diagramUuid = getDiagramURI(diagram);

            if( diagramUuid != null ) {
    			HashMap dMap = (HashMap)diagramMap.get(diagramUuid);
    			if( dMap != null ) {
                    String uuid = ModelObjectUtilities.getFullUuid(eObject);
                    if( isValidURI(uuid) ) {
        				Object existingDE = dMap.get(uuid);
        				if( existingDE != null ) {
                            if( debugPrint )
                                System.out.println(" DEM.removeEntity()  Removing Entity From Map.  EObject = " + eObject); //$NON-NLS-1$
                            dMap.remove(uuid);
                        }
                    }
    			}
            }
		}
	}
	
	public static AbstractDiagramEntity getEntity(Diagram diagram, EObject eObject) {
        AbstractDiagramEntity existingDE = null;
		
		if( diagramMap != null ) {
            String diagramUuid = getDiagramURI(diagram);

            if( diagramUuid != null ) {
    			HashMap dMap = (HashMap)diagramMap.get(diagramUuid);
    			if( dMap != null ) {
                    String uuid = ModelObjectUtilities.getFullUuid(eObject);
                    if( isValidURI(uuid) ) 
                        existingDE = (AbstractDiagramEntity)dMap.get(uuid);
    			}
            }
		}
        
		return existingDE;
	}

    public static void cleanDiagramEntities(Diagram diagram) {
        String diagramUuid = getDiagramURI(diagram);

        HashMap dMap = (HashMap)diagramMap.get(diagramUuid);
        if( dMap != null ) {
            List deleteList = new ArrayList();
            Collection keys = new ArrayList(dMap.keySet());
            Iterator iter = keys.iterator();
            AbstractDiagramEntity de = null;
            String nextKey = null;
            Object someRef = null;
            while( iter.hasNext() ) {
                nextKey = (String)iter.next();
                de = (AbstractDiagramEntity)dMap.get(nextKey);
                someRef = de.getModelObject();
                if( someRef != null && someRef instanceof EObject ) {
                    EObject realEObject = ModelObjectUtilities.getRealEObject((EObject)someRef);
                    EObject resolvedEObject = getEObject(nextKey);
                    if( realEObject == null || resolvedEObject == null ) {
                        dMap.remove(nextKey);
                        if( debugPrint )
                            System.out.println(" DEM.cleanDiagramEntities()  Removing Stale Entity From Map.  EObject = " + someRef); //$NON-NLS-1$
                        deleteList.add(de);
                    }
                    
                    
                } else {
                    dMap.remove(nextKey);
                    deleteList.add(de);
                }
            }
            
            if( !deleteList.isEmpty() ) {
                ModelObjectUtilities.delete(deleteList, false, false, diagram);
            }
        }
    }
    
    public static void cleanUpDiagram(Diagram diagram) {
        if( diagram != null ) {
            List deleteList = new ArrayList();
            Iterator iter = diagram.eContents().iterator();
            AbstractDiagramEntity de = null;
            Object someRef = null;
            while( iter.hasNext() ) {
                de = (AbstractDiagramEntity)iter.next();
                someRef = de.getModelObject();
                if( someRef != null && someRef instanceof EObject ) {
                    EObject realEObject = ModelObjectUtilities.getRealEObject((EObject)someRef);
                    if( realEObject == null ) {
                        deleteList.add(de);
                    }
                } else {
                    deleteList.add(de);
                }
            }
            
            if( !deleteList.isEmpty() ) {
                ModelObjectUtilities.delete(deleteList, false, false, diagram, false);
            }
        }
    }
    
    private static EObject getEObject(String uri) {
        EObject target = null;
        
        if( isValidURI(uri) ) { 
            URI theURI = URI.createURI(uri);
            if( theURI != null ) {
                try {
                    target = ModelerCore.getModelContainer().getEObject(theURI, false);
                    // Need to 
                } catch (CoreException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return target;
    }
    
    private static String getDiagramURI(Diagram diagram) {

        if( diagram.eContainer() != null ) {
            String diagramURI = ModelerCore.getModelEditor().getUri(diagram).toString();
            
            if( isValidURI(diagramURI) )
                return diagramURI;
        }
        return null;
    }
    
    private static boolean isValidURI(String uri) {
        if( uri == null || uri.length() <=6 || uri.indexOf(MMUUID) < 0 )
            return false;
        
        return true;
        
    }
 
    //*******************************
    
    public static void processEvent(EventObject obj) {
        ModelResourceEvent event = (ModelResourceEvent) obj;
        
        // Return if event concerns closed resource, otherwise subsequent call to checkValidity will cause resource to be
        // re-opened
        final int eventType = event.getType();

        IResource resource = event.getResource();
        if( resource != null ) {
            switch(eventType) {
                case ModelResourceEvent.CLOSED:
                case ModelResourceEvent.RELOADED:
                case ModelResourceEvent.REMOVED:
                case ModelResourceEvent.MOVED: 
                    {
                    while( !clearingStaleDiagrams ) {
                        clearStaleDiagrams();
                    }
                    clearingStaleDiagrams = false;
                } break;
                
                default:
                break;
            }
        }
    }
    
    public static void clearStaleDiagrams() {
        clearingStaleDiagrams = true;
        // Access the Map
        // Walk through the list, get each diagram and check for eResource() == NULL
        // Add to remove list and the remove them all
        
        if( diagramMap != null  ) {
            Collection staleKeys = new ArrayList();
            
            Object nextKey = null;
            int keyId = 0;
            for(Iterator iter = diagramMap.keySet().iterator(); iter.hasNext(); keyId++) {
                nextKey = iter.next();
                if( nextKey != null ) {
                    // we need see if the KEY still exists?
                    EObject eObj = getEObject((String)nextKey);
                    
                    if( eObj == null || eObj.eIsProxy() || eObj.eResource() == null ) {
                        if( debugPrint )
                            System.out.println(" DEM.clearStaleDiagrams()  Removing Stale Diagram = " + nextKey); //$NON-NLS-1$
                        staleKeys.add(nextKey);
                    }
                }
            }
            
            if( !staleKeys.isEmpty() ) {
                for (Iterator iter = staleKeys.iterator(); iter.hasNext(); ) {
                    diagramMap.remove(iter.next());
                }
            }
        }
    }
}
