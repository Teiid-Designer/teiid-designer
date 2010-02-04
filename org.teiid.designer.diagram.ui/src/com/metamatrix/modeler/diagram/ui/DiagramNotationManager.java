/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILabelProvider;
import com.metamatrix.modeler.diagram.ui.actions.NotationChoiceRadioActionGroup;
import com.metamatrix.modeler.diagram.ui.notation.NotationFigureGenerator;
import com.metamatrix.modeler.diagram.ui.notation.NotationModelGenerator;
import com.metamatrix.modeler.diagram.ui.notation.NotationPartGenerator;
import com.metamatrix.modeler.diagram.ui.preferences.NotationIDAndName;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;

/**
 * DiagramNotationManager - instantiates and provides access to the extensions that control Notation; Each must supply an
 * EditPartGenerator, DiagramModelGenerator and FigureGenerator. - Reflects user's choice/preference of available Notation
 * extensions - Supports the generation of dynamic Actions representing the available Notation extensions
 */
public class DiagramNotationManager implements
                                   PluginConstants,
                                   DiagramUiConstants,
                                   NotationChangeListener {

    private NotationPartGenerator depfCurrentPartGenerator;
    private NotationModelGenerator dmfCurrentModelGenerator;
    private NotationFigureGenerator fgfCurrentFigureGenerator;
    private List<NotationIDAndName> notationSelectionList;

    private String sCurrentExtensionUid = null;

    private ILabelProvider labelProvider;

    private IExtension[] exExtensions;
    private HashMap<String, IExtension> hmExtensions;

    private HashMap<String, IConfigurationElement> hmEditPartGeneratorElements;
    private HashMap<String, IConfigurationElement> hmDiagramModelGeneratorElements;
    private HashMap<String, IConfigurationElement> hmFigureGeneratorElements;

    //    private ArrayList arylNotationActions;
    private NotationChangeListener nclNotationListener;

    //    private String sNotationId;

    public DiagramNotationManager() {
        loadNotationExtensions();
        establishInitialNotationExtension();
        //        setNotationChangeListener( this );
    }

    public String getExtensionDisplayName(String sExtensionId) {
        IExtension ex = hmExtensions.get(sExtensionId);

        if (ex != null) {
            return ex.getLabel();
        }
        return "Unknown extension id..."; //$NON-NLS-1$     
    }

    public String getCurrentExtensionId() {
        return sCurrentExtensionUid;
    }

    public List<String> getExtensionIds() {
        return new ArrayList<String>(hmExtensions.keySet());
    }

    public NotationPartGenerator getEditPartGenerator() {
        if (depfCurrentPartGenerator == null) {
            setEditPartGenerator(getEditPartGeneratorClassExecutable(sCurrentExtensionUid));
        }
        return depfCurrentPartGenerator;
    }

    public NotationPartGenerator getEditPartGenerator(String sExtensionId) {

        NotationPartGenerator depf = getEditPartGeneratorClassExecutable(sExtensionId);

        return depf;
    }

    public void setEditPartGenerator(NotationPartGenerator epfGenerator) {
        this.depfCurrentPartGenerator = epfGenerator;

    }

    public NotationModelGenerator getDiagramModelGenerator() {

        if (dmfCurrentModelGenerator == null) {
            setDiagramModelGenerator(getDiagramModelGeneratorClassExecutable(sCurrentExtensionUid));
        }
        return dmfCurrentModelGenerator;

    }

    public NotationModelGenerator getDiagramModelGenerator(String sExtensionId) {

        return getDiagramModelGeneratorClassExecutable(sExtensionId);
    }

    public void setDiagramModelGenerator(NotationModelGenerator dmfGenerator) {
        this.dmfCurrentModelGenerator = dmfGenerator;

    }

    public NotationFigureGenerator getFigureGenerator() {
        if (fgfCurrentFigureGenerator == null) {
            setFigureGenerator(getFigureGeneratorClassExecutable(sCurrentExtensionUid));
        }
        return fgfCurrentFigureGenerator;
    }

    public NotationFigureGenerator getFigureGenerator(String sExtensionId) {

        return getFigureGeneratorClassExecutable(sExtensionId);
    }

    public void setFigureGenerator(NotationFigureGenerator fgfGenerator) {
        this.fgfCurrentFigureGenerator = fgfGenerator;

    }

    private void loadNotationExtensions() {
        //        Util.log( IStatus.INFO, "[DiagramNotationManager.loadNotationExtensions] TOP" ); //$NON-NLS-1$

        IExtension[] exExtensions = getDiagramNotationExtensions();
        hmExtensions = new HashMap<String, IExtension>();
        hmEditPartGeneratorElements = new HashMap<String, IConfigurationElement>();
        hmDiagramModelGeneratorElements = new HashMap<String, IConfigurationElement>();
        hmFigureGeneratorElements = new HashMap<String, IConfigurationElement>();
        notationSelectionList = new ArrayList<NotationIDAndName>(exExtensions.length);
        // process each extension
        for (int iExtensionIndex = 0; iExtensionIndex < exExtensions.length; iExtensionIndex++ ) {

            hmExtensions.put(exExtensions[iExtensionIndex].getUniqueIdentifier(), exExtensions[iExtensionIndex]);

            String sExtensionId = exExtensions[iExtensionIndex].getUniqueIdentifier();
            IConfigurationElement[] elements = exExtensions[iExtensionIndex].getConfigurationElements();
            String sElementName = null;

            // process each element within this extension
            for (int iElementIndex = 0; iElementIndex < elements.length; iElementIndex++ ) {
                sElementName = elements[iElementIndex].getName();

                // determine which element, then save the classname in that array

                if (sElementName.equals(ExtensionPoints.DiagramNotation.EDIT_PART_GENERATOR_ELEMENT)) {
                    //                    Util.log( IStatus.INFO, "[DiagramNotationManager.loadNotationExtensions] 'put'-ing to
                    // hmEditPartGeneratorElements; key is: " + sExtensionId + " content is: " + elements[ iElementIndex ]);
                    hmEditPartGeneratorElements.put(sExtensionId, elements[iElementIndex]);
                } else if (sElementName.equals(ExtensionPoints.DiagramNotation.DIAGRAM_MODEL_GENERATOR_ELEMENT)) {
                    //                    Util.log( IStatus.INFO, "[DiagramNotationManager.loadNotationExtensions] 'put'-ing to
                    // hmDiagramModelGeneratorElements; key is: " + sExtensionId + " content is: " + elements[ iElementIndex ]);
                    hmDiagramModelGeneratorElements.put(sExtensionId, elements[iElementIndex]);
                } else if (sElementName.equals(ExtensionPoints.DiagramNotation.FIGURE_GENERATOR_ELEMENT)) {
                    //                    Util.log( IStatus.INFO, "[DiagramNotationManager.loadNotationExtensions] 'put'-ing to
                    // hmFigureGeneratorElements; key is: " + sExtensionId + " content is: " + elements[ iElementIndex ]);
                    hmFigureGeneratorElements.put(sExtensionId, elements[iElementIndex]);
                }
                if (sElementName.equals(ExtensionPoints.DiagramNotation.NOTATION_PREFERENCES)) {
                    //                    Util.log( IStatus.INFO, "[DiagramNotationManager.loadNotationExtensions] 'put'-ing to
                    // hmFigureGeneratorElements; key is: " + sExtensionId + " content is: " + elements[ iElementIndex ]);
                    String displayName = elements[iElementIndex].getAttribute(ExtensionPoints.DiagramNotation.DISPLAY_NAME);
                    if (displayName != null) {
                        NotationIDAndName newIDandName = new NotationIDAndName(
                                                                               exExtensions[iExtensionIndex]
                                                                                                            .getSimpleIdentifier(),
                                                                               displayName);
                        notationSelectionList.add(newIDandName);
                    }
                }
            }
        }
    }

    private void establishInitialNotationExtension() {
        // 1. use what is established in user prefs
        //        IPreferenceStore store = DiagramUiPlugin.getDefault().getPreferenceStore();

        String simpleExtensionID = PluginConstants.DEFAULT_DIAGRAM_NOTATION_ID; //store.getString(PluginConstants.Prefs.DIAGRAM_NOTATION);

        // OR 2. default to the first one in the plugins.xml (index = 0)
        // set the default index value, if we found any extensions
        IExtension[] exExtensions = getDiagramNotationExtensions();

        if (exExtensions.length > 0) {
            boolean foundDefault = false;
            for (int i = 0; i < exExtensions.length; i++ ) {
                String extensionID = exExtensions[i].getSimpleIdentifier();
                if (extensionID.equals(simpleExtensionID)) {
                    foundDefault = true;
                    sCurrentExtensionUid = exExtensions[i].getUniqueIdentifier();
                }
                if (foundDefault)
                    break;
            }
            if (!foundDefault)
                sCurrentExtensionUid = exExtensions[0].getUniqueIdentifier();
            //            Util.log( IStatus.INFO, "[DiagramNotationManager.getDiagramNotationExtensions] Selecting Default Extension: " +
            // sCurrentExtensionUid );
        }

    }

    private IExtension[] getDiagramNotationExtensions() {
        if (exExtensions == null) {

            IExtensionPoint epExtensionPoint = 
                Platform.getExtensionRegistry().getExtensionPoint(DiagramUiConstants.PLUGIN_ID, ExtensionPoints.DiagramNotation.ID);

            exExtensions = epExtensionPoint.getExtensions();
        }
        return exExtensions;
    }

    private NotationPartGenerator getEditPartGeneratorClassExecutable(String sExtensionUid) {
        Object oExecutableExtension = null;
        IConfigurationElement ceElement = null;

        try {
            ceElement = hmEditPartGeneratorElements.get(sExtensionUid);

            //            if ( ceElement == null ) {
            //                Util.log( IStatus.INFO, "[DiagramNotationManager.getEditPartGeneratorClassExecutable] ceElement is NULL! (Key is: "
            // + sExtensionUid );
            //            } else {
            //                Util.log( IStatus.INFO, "[DiagramNotationManager.getEditPartGeneratorClassExecutable] ceElement is NOT null (Key
            // is: " + sExtensionUid );
            //            }
            oExecutableExtension = ceElement.createExecutableExtension(ExtensionPoints.DiagramNotation.CLASS_NAME);

        } catch (CoreException ce) {
            ce.printStackTrace();
        }

        if (oExecutableExtension instanceof NotationPartGenerator) {
            return (NotationPartGenerator)oExecutableExtension;
        }
        return null;
    }

    private NotationModelGenerator getDiagramModelGeneratorClassExecutable(String sExtensionUid) {
        Object oExecutableExtension = null;
        IConfigurationElement ceElement = null;

        try {
            ceElement = hmDiagramModelGeneratorElements.get(sExtensionUid);
            //           if ( ceElement == null ) {
            //               Util.log( IStatus.INFO, "[DiagramNotationManager.getDiagramModelGeneratorClassExecutable] ceElement is NULL!: (Key
            // is: " + sExtensionUid );
            //           } else {
            //               Util.log( IStatus.INFO, "[DiagramNotationManager.getDiagramModelGeneratorClassExecutable] ceElement is NOT null
            // (Key is: " + sExtensionUid );
            //           }
            oExecutableExtension = ceElement.createExecutableExtension(ExtensionPoints.DiagramNotation.CLASS_NAME);

        } catch (CoreException ce) {
            ce.printStackTrace();
        }

        if (oExecutableExtension instanceof NotationModelGenerator) {
            return (NotationModelGenerator)oExecutableExtension;
        }
        return null;
    }

    private NotationFigureGenerator getFigureGeneratorClassExecutable(String sExtensionUid) {
        Object oExecutableExtension = null;
        IConfigurationElement ceElement = null;

        try {
            ceElement = hmFigureGeneratorElements.get(sExtensionUid);
            //           if ( ceElement == null ) {
            //               Util.log( IStatus.INFO, "[DiagramNotationManager.getFigureGeneratorClassExecutable] ceElement is NULL!: (Key is: "
            // + sExtensionUid );
            //           } else {
            //               Util.log( IStatus.INFO, "[DiagramNotationManager.getFigureGeneratorClassExecutable] ceElement is NOT null (Key is:
            // " + sExtensionUid );
            //           }
            oExecutableExtension = ceElement.createExecutableExtension(ExtensionPoints.DiagramNotation.CLASS_NAME);

        } catch (CoreException ce) {
            ce.printStackTrace();
        }

        if (oExecutableExtension instanceof NotationFigureGenerator) {
            return (NotationFigureGenerator)oExecutableExtension;
        }
        return null;
    }

    public void setLabelProvider(ILabelProvider provider) {
        this.labelProvider = provider;
    }

    public ILabelProvider getLabelProvider() {
        return this.labelProvider;
    }

    public void setNotationChangeListener(NotationChangeListener nclNotationListener) {
        this.nclNotationListener = nclNotationListener;
    }

    public NotationChangeListener getNotationChangeListener() {
        return nclNotationListener;
    }

    public void setNotationId(String sNotationId) {
        // this method is obsolete for now; may return when we implement notation prefs.

    }

    public MenuManager getNotationActionGroup(NotationChangeListener ncl,
                                              String currentNotationId) {
        if (exExtensions != null && exExtensions.length > 1)
            return new NotationChoiceRadioActionGroup(ncl, currentNotationId);

        return null;
    }

    public List<NotationIDAndName> getDiagramNotationInfo() {
        return notationSelectionList;
    }

}

