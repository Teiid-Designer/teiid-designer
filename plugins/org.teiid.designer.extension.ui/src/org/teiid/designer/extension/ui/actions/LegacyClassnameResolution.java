/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.ui.actions;

import static org.teiid.designer.extension.ui.UiConstants.UTIL;
import java.io.InputStream;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution;
import org.teiid.core.designer.util.TempInputStream;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.ModelExtensionAssistantAggregator;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants.ExtensionIds;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 * LegacyClassnameResolution - this resolves MED problem marker issues for MED files and models 
 * which have legacy 'com.metamatrix' classnames.  It converts them to the 'org.teiid.designer' equivalent.
 */
public class LegacyClassnameResolution implements IMarkerResolution {

    private static final String LEGACY_METACLASS_PREFIX = "com.metamatrix.";  //$NON-NLS-1$
    private static final String NEW_METACLASS_PREFIX = "org.teiid.designer.";  //$NON-NLS-1$

    /* (non-Javadoc)
     * @see org.eclipse.ui.IMarkerResolution#getLabel()
     */
    @Override
    public String getLabel() {
        return Messages.legacyClassnameResolutionLabel;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
     */
    @Override
    public void run(IMarker marker) {
        IResource resource = marker.getResource();

        // Fix the Marked Model Resource
        if(ModelUtilities.isModelFile(resource)) {
            final IFile theFile = (IFile)resource;
            
            final ModelEditor editor = ModelEditorManager.getModelEditorForFile(theFile, false);
            // If editor is open and dirty, ask user whether to save
            if ((editor != null) && editor.isDirty()) {
                boolean saveEditor = MessageDialog.openQuestion(getShell(), Messages.quickFixModelDirtyTitle,
                                                                Messages.quickFixModelDirtyMsg);
                if(!saveEditor) {
                    return;
                } else {
                    // Add the selected Med
                    UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                        @Override
                        public void run() {
                            editor.doSave(new NullProgressMonitor());
                            fixModelMed(theFile);
                        }
                    });   
                    return;
                }
            }
            // Add the selected Med
            UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                  fixModelMed(theFile);
                }
            });

            // Fix the Marked mxd File
        } else if(ModelUtilities.isMedFile(resource)) {
            final IFile theFile = (IFile)resource;
            // Check whether there is currently an open editor for the selected Med
            final IEditorPart editor = UiUtil.getEditorForFile(theFile, false);

            // If editor is open and dirty, ask user whether to save
            if ((editor != null) && editor.isDirty()) {
                boolean saveEditor = MessageDialog.openQuestion(getShell(), Messages.quickFixMedFileDirtyTitle,
                                                                Messages.quickFixMedFileDirtyMsg);
                if (!saveEditor) {
                    return;
                } else {
                    // Add the selected Med
                    UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                        @Override
                        public void run() {
                          editor.doSave(new NullProgressMonitor());
                          fixMedFile(theFile);
                        }
                    });   
                    return;
                }

            }
            // Add the selected Med
            UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                  fixMedFile(theFile);
                }
            });
        }
    }

    /*
     * Fix Legacy Names in the supplied Model File by replacing the legacy 'com.metamatrix' names with 'org.teiid.designer'
     * @param modelResource the supplied model resource
     */
    private void fixModelMed( IFile modelFile ) {
        // Get the namespaces which are currently persisted on the model
        final ModelExtensionAssistantAggregator aggregator = ExtensionPlugin.getInstance().getModelExtensionAssistantAggregator();
        Collection<String> supportedNamespaces = null; 

        try {
            supportedNamespaces = aggregator.getSupportedNamespacePrefixes(modelFile);
        } catch (Exception e) {
            UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.getSupportedPrefixesErrorMsg, modelFile.getName()));
            return;
        }

        boolean modelNeedsSave = false;
        for(String namespacePrefix : supportedNamespaces) {
            ModelExtensionAssistant assistant = ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(namespacePrefix);
            // if assistant is null, it couldn't find in registry. Create a default assistant.
            if (assistant == null) {
                assistant = ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant(namespacePrefix);
            }
            assert (assistant instanceof ModelObjectExtensionAssistant) : "ModelExtensionAssistant is not a ModelObjectExtensionAssistant"; //$NON-NLS-1$

            ModelObjectExtensionAssistant mAssistant = (ModelObjectExtensionAssistant)assistant;
            ModelExtensionDefinition modelMed = null;
            try {
                modelMed = mAssistant.getModelExtensionDefinition(modelFile);
            } catch (Exception e) {
                UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.getModelMedErrorMsg, modelFile.getName()));
                return;
            }

            // Update the MED.  Changes the 'com.metamatrix' references to 'org.teiid.designer'
            boolean medChanged = updateMed(modelMed);

            if(medChanged) {
                // Save the ModelResource MED
                try {
                    mAssistant.saveModelExtensionDefinition(modelFile);
                } catch (Exception e) {
                    UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.saveModelMedErrorMsg, modelFile.getName()));
                }
                modelNeedsSave = true;
            }
        }
        if(modelNeedsSave) {
            try {
                ModelResource mdlResc = ModelUtilities.getModelResourceForIFile(modelFile, false);
                if(mdlResc!=null) {
                    mdlResc.save(new NullProgressMonitor(), true);
                }
                modelFile.deleteMarkers(ExtensionIds.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
                modelFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            } catch (CoreException e) {
                UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.saveModelErrorMsg, modelFile.getName()));
            }
        }

    }

    /*
     * Fix Legacy Names in the supplied MED File by replacing the legacy 'com.metamatrix' names with 'org.teiid.designer'
     * @param medFile the supplied MED File
     */
    private void fixMedFile( IFile medFile ) {
    	TempInputStream inputStream = null;
        try {
            ModelExtensionDefinition med = parse(medFile.getContents());
            if(med!=null) {
                // Update the legacy classnames in the MED
                updateMed(med);

                // Re-write the Med File
                final ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
                inputStream = medWriter.writeAsStream(med);
                medFile.setContents(inputStream.getRealInputStream(), false, false, new NullProgressMonitor());

                // Delete Error Markers and Refresh
                medFile.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
                medFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                
                inputStream.getRealInputStream().close();
            }
        } catch (Exception e) {
            UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.fixMedFileClassnamesFailedMsg, medFile.getName()));
        } finally {
        	inputStream.deleteTempFile();
        }
    }

    /*
     * Update the the supplied MED by updating any extended legacy classnames to the new classnames
     * @param med the ModelExtensionDefinition
     * @return 'true' if any metaclass names were updated, 'false' if not
     */
    private boolean updateMed(ModelExtensionDefinition med) {
        boolean wasChanged = false;
        if(med!=null) {
            String[] extendedMCs = med.getExtendedMetaclasses();
            if(extendedMCs!=null) {
                for(int i=0; i<extendedMCs.length; i++) {
                    String metaclass = extendedMCs[i];
                    if(metaclass!=null && metaclass.startsWith(LEGACY_METACLASS_PREFIX)) {
                        String newMetaclass = metaclass.replaceAll(LEGACY_METACLASS_PREFIX, NEW_METACLASS_PREFIX);
                        med.updateMetaclass(metaclass, newMetaclass);
                        wasChanged = true;
                    }
                }
            }
        }
        return wasChanged;
    }

    /*
     * Parse the supplied InputStream and return the MED
     * @param mxdContents the supplied InputStream
     * @return the ModelExtensionDefinition
     */
    private ModelExtensionDefinition parse(InputStream mxdContents) {
        ModelExtensionDefinition med = null;
        try {
            med = ExtensionPlugin.getInstance().parse(mxdContents);
        } catch (Exception e) {
            UTIL.log(Messages.medFileParseErrorMsg);
        }
        return med;
    }

    private static Shell getShell() {
        return Activator.getDefault().getCurrentWorkbenchWindow().getShell();
    }

}
