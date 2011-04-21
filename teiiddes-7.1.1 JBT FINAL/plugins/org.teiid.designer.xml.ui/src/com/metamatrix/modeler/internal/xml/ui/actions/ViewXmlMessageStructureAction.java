/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.actions;

import java.io.File;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.xsd.XSDElementDeclaration;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelSelectorInfo;
import com.metamatrix.modeler.internal.xml.ui.wizards.VirtualDocumentWizardContributor;
import com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;


/** 
 * @since 5.0
 */
public class ViewXmlMessageStructureAction extends CreateXmlViewFromXsdAction {
    private static final String MODEL_NAME           = Util.getString("ViewXmlMessageStructureAction.modelName"); //$NON-NLS-1$
    private static final String NEW_MODEL_NAME_LABEL = Util.getString("ViewXmlMessageStructureAction.newModelNameLabel"); //$NON-NLS-1$
    private final static String XMI_FILE_EXT     = "xmi"; //$NON-NLS-1$
    private final static String DOT_XMI_FILE_EXT = ".xmi"; //$NON-NLS-1$
    private final static String UNDERSCORE_MS    = "_MS"; //$NON-NLS-1$
    
    private static ModelSelectorInfo newModelInfo;
    
    /** 
     * 
     * @since 5.0
     */
    public ViewXmlMessageStructureAction() {
        super();
        
        if( newModelInfo == null ) {
            newModelInfo = new ModelSelectorInfo(MODEL_NAME,
                                                 ModelType.LOGICAL_LITERAL,
                                                 XmlDocumentPackage.eNS_URI,
                                                 NEW_MODEL_NAME_LABEL,
                                                 null
                                                 );
        }
    }

    @Override
    protected boolean executeBuild(List theXsdRoots, final IProgressMonitor theMonitor) {
        boolean result = false;
        theMonitor.beginTask("Building View Model Documents:  ", 100);  //$NON-NLS-1$;
        
        try {
            ModelResource schemaModelResource = getSchemaModel(theXsdRoots);
            
            if (schemaModelResource == null) {
                result = false;
            } else {
                IFile schemaModel = (IFile)schemaModelResource.getUnderlyingResource();
                ISelection selection =  new StructuredSelection(schemaModel);
                String newModelName = schemaModel.getProjectRelativePath().removeFileExtension().lastSegment().concat(UNDERSCORE_MS);
                ModelResource xmlDocModel = getModelResource(schemaModel.getParent(), newModelName);
                ViewXmlMessageStructureWizard wizard = new ViewXmlMessageStructureWizard(xmlDocModel, selection);
                wizard.setDocumentRoots((XSDElementDeclaration[])theXsdRoots.toArray(new XSDElementDeclaration[theXsdRoots.size()]));

                VirtualDocumentWizardContributor contributor = (VirtualDocumentWizardContributor)wizard.getContributor();
                contributor.setMappingClassBuilderStrategy(MappingClassBuilderStrategy.compositorStrategy);
    
                // show wizard
                if (new WizardDialog(getShell(), wizard).open() == Window.OK) {
                    result = true;
                    activateModelEditor(xmlDocModel);
                }
                
                // need to refresh Model Explorer if opened since when validation is run the XSD is unloaded and
                // reloaded which makes new instances of schema elements. refresh updates the to the new elements.
                IViewPart part = UiUtil.getViewPart(UiConstants.Extensions.Explorer.VIEW);
                
                if (part != null) {
                    ((ResourceNavigator)part).getViewer().refresh(schemaModel);
                }
            }
        } catch (ModelWorkspaceException theException) {
            result = false;
            Util.log(theException);
        }
        
        return result;
    }
    
    private ModelResource getModelResource(IContainer modelContainer, String name) {
        ModelResource modelResource = null;
        
        IPath modelPath = modelContainer.getProjectRelativePath().append(name).removeFileExtension().addFileExtension(XMI_FILE_EXT);
        File theModel = modelPath.toFile();
        if( theModel.exists() ) {
            IResource modelFile = modelContainer.getProject().findMember(modelPath);
            if( modelFile != null ) {
                try {
                    modelResource = ModelUtil.getModelResource((IFile)modelFile, false);
                } catch (ModelWorkspaceException theException) {
                    theException.printStackTrace();
                }
            }
        } else {
            // Need to create the resource
            modelResource = constructModel(modelContainer, name);
        }
        
        return modelResource;
    }
    
    /**
     * Create a Model with the supplied name, in the desired project
     * 
     * @param targetProj
     *            the project resource under which to create the model
     * @param modelName
     *            the model name to create
     * @return the newly-created ModelResource
     */
    private ModelResource constructModel(IResource targetRes, String sModelName) {

        String sFileName = getFileName(sModelName);
        IPath relativeModelPath = targetRes.getProjectRelativePath().append(sFileName);
        final IFile modelFile = targetRes.getProject().getFile(relativeModelPath);
        final ModelResource resrc = ModelerCore.create(modelFile);
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri(newModelInfo.getModelURI());
            resrc.getModelAnnotation().setModelType(newModelInfo.getModelType());
        } catch (ModelWorkspaceException mwe) {
            mwe.printStackTrace();
        }
        return resrc;
    }
    
    /**
     * get the full file name, given a modelName string
     * 
     * @param modelName
     *            the model name
     * @return the full model name, including extension
     */
    private String getFileName(String sModelName) {
        String sResult = sModelName.trim();

        if (!sResult.endsWith(DOT_XMI_FILE_EXT)) {
            sResult += DOT_XMI_FILE_EXT;
        }

        return sResult;
    }
}
