/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.impl.DiagramEntityImpl;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.editor.CanOpenContextException;
import com.metamatrix.modeler.diagram.ui.pakkage.PackageDiagramProvider;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramColorObject;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * DiagramTypeManager - instantiates and provides access to the extensions that control Diagram Types Each must supply an
 * EditPartFactory, DiagramModelFactory and FigureFactory.
 */
public class DiagramTypeManager implements PluginConstants, DiagramUiConstants, DiagramUiConstants.ExtensionPoints {

    private IExtension[] exExtensions;

    private HashMap<String, IExtension> hmExtensionsByExtensionId;
    private HashMap<String, IConfigurationElement> hmDiagramTypeElements;
    private HashMap<String, IDiagramType> diagramTypeMap;
    private List<String> orderedExtentionIDs;

    private List<DiagramColorObject> bkgdColorList;

    public DiagramTypeManager() {

        // load the notation extensions

        loadAllExtensions();
    }

    private void loadAllExtensions() {
        IExtension[] exExtensions = getDiagramTypeExtensions();

        hmExtensionsByExtensionId = new HashMap<String, IExtension>();
        hmDiagramTypeElements = new HashMap<String, IConfigurationElement>();
        diagramTypeMap = new HashMap<String, IDiagramType>();

        // process each extension
        for (int iExtensionIndex = 0; iExtensionIndex < exExtensions.length; iExtensionIndex++) {

            hmExtensionsByExtensionId.put(exExtensions[iExtensionIndex].getSimpleIdentifier(), exExtensions[iExtensionIndex]);

            String sExtensionId = exExtensions[iExtensionIndex].getSimpleIdentifier();
            IConfigurationElement[] elements = exExtensions[iExtensionIndex].getConfigurationElements();
            String sElementName = null;

            // process each element within this extension
            for (int iElementIndex = 0; iElementIndex < elements.length; iElementIndex++) {
                sElementName = elements[iElementIndex].getName();

                if (sElementName.equals(DiagramType.DIAGRAM_TYPE_ELEMENT)) {
                    hmDiagramTypeElements.put(sExtensionId, elements[iElementIndex]);
                }

            }
        }

        setOrderedExtentionIds();
    }

    private IExtension[] getDiagramTypeExtensions() {
        if (exExtensions == null) {

            IExtensionPoint epExtensionPoint = Platform.getExtensionRegistry().getExtensionPoint(DiagramUiConstants.PLUGIN_ID,
                                                                                                 DiagramType.ID);

            exExtensions = epExtensionPoint.getExtensions();
        }
        return exExtensions;
    }

    public boolean isDiagramSimple( String sDiagramTypeId ) {
        IConfigurationElement ceElement = hmDiagramTypeElements.get(sDiagramTypeId);
        String simpleDiagram = ceElement.getAttribute(DiagramType.SIMPLE_DIAGRAM);
        if (simpleDiagram != null && simpleDiagram.equalsIgnoreCase("true")) {//$NON-NLS-1$ 
            return true;
        }
        return false;
    }

    public boolean isTransientDiagram( Diagram diagram ) {
        // Walk through the diagram type's and see if any of them can interpret
        boolean isTransient = false;
        Iterator<String> iter = getOrderedExtentionIds().iterator();
        String nextExtensionID = null;
        IDiagramType nextDiagramType = null;
        while (iter.hasNext() && !isTransient) {
            nextExtensionID = iter.next();
            nextDiagramType = getDiagram(nextExtensionID);
            if (nextDiagramType != null) isTransient = nextDiagramType.isTransientDiagram(diagram);
        }

        return isTransient;
    }

    /**
     * Has the diagram type been marked as deprecated in the plugin extension.
     * For diagram types that are being phased out the extension attribute will
     * be marked as true, while other diagram types will be either empty or
     * false.
     * 
     * @param diagramTypeId
     * @return
     */
    public boolean isDeprecatedDiagram(String diagramTypeId) {
        IConfigurationElement ceElement = hmDiagramTypeElements
                .get(diagramTypeId);
        String deprecatedDiagram = ceElement
                .getAttribute(DiagramType.DEPRECATED_DIAGRAM);
        if (deprecatedDiagram != null
                && deprecatedDiagram.equalsIgnoreCase("true")) {//$NON-NLS-1$ 
            return true;
        }
        return false;
    }

    public IDiagramType getDiagram( String sDiagramTypeId ) {
        return getDiagramTypeClassExecutable(sDiagramTypeId);
    }

    private IDiagramType getDiagramTypeClassExecutable( String sExtensionId ) {
        IDiagramType diagramType = null;
        IConfigurationElement ceElement = null;

        if (diagramTypeMap != null) diagramType = diagramTypeMap.get(sExtensionId);

        if (diagramType == null) {
            try {
                ceElement = hmDiagramTypeElements.get(sExtensionId);
                Object oExecutableExtension = ceElement.createExecutableExtension(DiagramNotation.CLASS_NAME);
                if (oExecutableExtension instanceof IDiagramType) {
                    diagramType = (IDiagramType)oExecutableExtension;
                    diagramType.setType(sExtensionId);
                    diagramTypeMap.put(sExtensionId, diagramType);
                }
            } catch (CoreException ce) {
                ce.printStackTrace();
            }
        }

        return diagramType;
    }

    public boolean canOpenContext( Object input ) {
        boolean canOpen = false;

        // Added this to handle the rare case where the input may be a diagram entity. In this case
        // we just need to get the parent diagram and go from there.
        // In theory, we should never get here, but the current state of "findMOdelObject" may result in a diagram entity
        // being used in the "ModelEditorManager.open()" call.
        if (input instanceof DiagramEntityImpl) {
            Diagram parentDiagram = ((DiagramEntityImpl)input).getDiagram();
            if (parentDiagram != null) canOpen = true;
        } else {
            // Walk through the diagram type's and see if any of them can interpret
            List<String> extensionIDs = getOrderedExtentionIds();
            try {
                Iterator<String> iter = extensionIDs.iterator();
                String nextExtensionID = null;
                IDiagramType nextDiagramType = null;
                while (iter.hasNext() && !canOpen) {
                    nextExtensionID = iter.next();
                    nextDiagramType = getDiagram(nextExtensionID);
                    if (nextDiagramType != null) canOpen = nextDiagramType.canOpenContext(input);
                }
            } catch (CanOpenContextException ex) {
                // Present dialog
                WidgetUtil.showWarning(ex.getMessage());
                canOpen = false;
            }
        }
        return canOpen;
    }

    public Diagram getDiagramForContext( Object input ) {
        Diagram diagram = null;

        // Added this to handle the rare case where the input may be a diagram entity. In this case
        // we just need to get the parent diagram and go from there.
        // In theory, we should never get here, but the current state of "findMOdelObject" may result in a diagram entity
        // being used in the "ModelEditorManager.open()" call.
        if (input instanceof DiagramEntityImpl) {
            Diagram parentDiagram = ((DiagramEntityImpl)input).getDiagram();
            if (parentDiagram != null) diagram = parentDiagram;
        } else {
            // Walk through the diagram type's and see if any of them can interpret
            List<String> extensionIDs = getOrderedExtentionIds();

            Iterator<String> iter = extensionIDs.iterator();
            String nextExtensionID = null;
            IDiagramType nextDiagramType = null;
            while (iter.hasNext() && diagram == null) {
                nextExtensionID = iter.next();
                nextDiagramType = getDiagram(nextExtensionID);
                if (nextDiagramType != null) diagram = nextDiagramType.getDiagramForContext(input);
            }
        }
        return diagram;
    }

    public List<DiagramColorObject> getDiagramColorInfo() {
        if (bkgdColorList == null) {
            bkgdColorList = new ArrayList<DiagramColorObject>();
            // Walk through the diagram type's and see if any of them can interpret
            List<String> extensionIDs = getOrderedExtentionIds();
            DiagramColorObject dco = null;

            Iterator<String> iter = extensionIDs.iterator();
            String nextExtensionID = null;
            IDiagramType nextDiagramType = null;
            while (iter.hasNext()) {
                nextExtensionID = iter.next();
                
                if (isDeprecatedDiagram(nextExtensionID)) {
                    // Do not display colour object for deprecated diagrams
                    continue;
                }
                
                nextDiagramType = getDiagram(nextExtensionID);
                if (nextDiagramType != null) {
                    dco = nextDiagramType.getBackgroundColorObject(nextDiagramType.getType());
                    if (dco != null) {
                        bkgdColorList.add(dco);
                    }
                }
            }
        }
        return bkgdColorList;
    }

    public Diagram getPackageDiagram( ModelResource resource,
                                      EObject packageEObject,
                                      boolean forceCreate ) {
        Diagram someDiagram = null;

        // Walk through all plugin extension types and ask for their
        // IPackageDiagramProvider.
        // If you find one, ask it for a package diagram.
        // if it has one, set it and return;
        List<String> extensionIDs = getOrderedExtentionIds();

        Iterator<String> iter = extensionIDs.iterator();
        String nextExtensionID = null;
        IDiagramType nextDiagramType = null;
        while (iter.hasNext() && someDiagram == null) {
            nextExtensionID = iter.next();
            nextDiagramType = getDiagram(nextExtensionID);
            if (nextDiagramType.getPackageDiagramProvider() != null) someDiagram = nextDiagramType.getPackageDiagramProvider().getPackageDiagram(resource,
                                                                                                                                                 packageEObject,
                                                                                                                                                 forceCreate);
        }

        return someDiagram;
    }

    public Diagram getDiagramForGoToMarkerEObject( EObject eObject,
                                                   boolean forceCreate ) {
        Diagram someDiagram = null;

        // Walk through all plugin extention types and ask for their IPackageDiagramProvider.
        // If you find one, ask it for a package diagram.
        // if it has one, set it and return;
        List<String> extensionIDs = getOrderedExtentionIds();

        Iterator<String> iter = extensionIDs.iterator();
        String nextExtensionID = null;
        IDiagramType nextDiagramType = null;
        while (iter.hasNext() && someDiagram == null) {
            nextExtensionID = iter.next();
            nextDiagramType = getDiagram(nextExtensionID);
            someDiagram = nextDiagramType.getDiagramForGoToMarkerEObject(eObject);
        }

        if (someDiagram == null) {
            PackageDiagramProvider pdp = new PackageDiagramProvider();
            someDiagram = pdp.getPackageDiagram(eObject, forceCreate);
            if (someDiagram == null) {
                EObject parentPackage = DiagramUiUtilities.getParentPackage(eObject);
                if (parentPackage != null) someDiagram = pdp.getPackageDiagram(parentPackage, forceCreate);
            }
        }

        return someDiagram;
    }

    public String getDisplayedPath( Diagram diagram,
                                    EObject eObject ) {
        String somePath = null;

        if (diagram != null && eObject != null) {
            IDiagramType theDiagramType = getDiagram(diagram.getType());
            if (theDiagramType != null) somePath = theDiagramType.getDisplayedPath(diagram, eObject);
        }
        return somePath;
    }

    private List<String> getOrderedExtentionIds() {
        if (orderedExtentionIDs == null) {
            return Collections.emptyList();
        }
        return orderedExtentionIDs;
    }

    private void setOrderedExtentionIds() {
        List<String> rawList = new ArrayList<String>(hmExtensionsByExtensionId.keySet());
        orderedExtentionIDs = new ArrayList<String>(rawList.size());

        Iterator<String> iter = rawList.iterator();
        String pkgType = null;
        String nextID = null;
        while (iter.hasNext()) {
            nextID = iter.next();
            if (nextID.equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID)) {
                pkgType = nextID;
            } else {
                orderedExtentionIDs.add(nextID);
            }
        }
        if (pkgType != null) orderedExtentionIDs.add(pkgType);
    }

}
