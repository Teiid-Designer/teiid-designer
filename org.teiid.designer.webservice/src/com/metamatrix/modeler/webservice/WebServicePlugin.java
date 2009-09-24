/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice;

import java.io.File;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.compare.selector.ModelResourceSelector;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.compare.selector.TransientModelSelector;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.webservice.BasicWebServiceModelBuilder;
import com.metamatrix.modeler.internal.webservice.WebServiceModelGenerator;
import com.metamatrix.modeler.internal.webservice.gen.BasicWebServiceGenerator;
import com.metamatrix.modeler.internal.webservice.gen.BasicWebServiceXmlDocumentGenerator;
import com.metamatrix.modeler.internal.webservice.gen.BasicWsdlGenerator;

/**
 * The main plugin class to be used in the desktop.
 */
public class WebServicePlugin extends Plugin {
    
    /**
     * An instance o fthis plugin.
     */
    private static WebServicePlugin plugin = null;
    
    /**
     * The plug-in identifier of this plugin
     * (value <code>"com.metamatrix.modeler.webservice"</code>).
     */
    public static final String PLUGIN_ID = "org.teiid.designer.webservice" ; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = WebServicePlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,I18N_NAME,ResourceBundle.getBundle(I18N_NAME));

    public static boolean DEBUG = false;
    
    /**
     * Collection of WSDL file extensions. Each element in the collection can be used used in dialog file chooser's to filter
     * out resources.
     */
    public static String[] WSDL_FILE_EXTENSIONS = new String[] {"wsdl"}; //$NON-NLS-1$

    public static final String WAR_DEFAULT = Util.getString("WebServicePlugin.defaultWARFileLocation"); //$NON-NLS-1$
    /** 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        ((PluginUtilImpl)Util).initializePlatformLogger(this);   // This must be called to initialize the platform logger!  
    }
    
    /**
     * Create a web service model builder given the supplied information.
     * @param theParentResource the parent of the model that is to be created or built
     * @param theModelPath the path to the model
     * @param theDescriptor the metamodel descriptor
     * @return the model builder
     * @since 4.2
     */
    public static IWebServiceModelBuilder createModelBuilder( final IResource theParentResource,
                                                                final IPath theModelPath,
                                                                final MetamodelDescriptor theDescriptor ) {
        final BasicWebServiceModelBuilder builder = new BasicWebServiceModelBuilder();
        builder.setModelPath(theModelPath);
        builder.setMetamodelDescriptor(theDescriptor);
        builder.setParentResource(theParentResource);
        return builder;
    }
    
    /**
     * Create a component that can be used to generate WSDL from one or more Web Service
     * models and the XML Schemas they reference. 
     * @return the WSDL generator
     * @since 4.2
     */
    public static IWsdlGenerator createWsdlGenerator() {
        return new BasicWsdlGenerator();
    }
    
    /**
     * Create a component that can be used to generate a Web Service
     * model from one or more WSDL definitions and the XML Schemas they reference. 
     * @return the web service model generator
     * @since 4.2
     */
    public static IWebServiceGenerator createWebServiceGenerator() {
        return new BasicWebServiceGenerator();
    }
    
    /**
     * Create a component that can be used to generate XML documents from one or more Web Service
     * models and the XML Schemas they reference. 
     * @return the XML document generator
     * @since 4.2
     */
    public static IWebServiceXmlDocumentGenerator createXmlDocumentGenerator() {
        return new BasicWebServiceXmlDocumentGenerator();
    }

    /**
     * Indicates if the specified extensions is a valid WSDL file extension. 
     * @param theExtension the extension being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isWsdlFileExtension(String theExtension) {
        boolean result = false;
        
        if ((theExtension != null) && (theExtension.length() > 0)) {
            for (int i = 0; i < WebServicePlugin.WSDL_FILE_EXTENSIONS.length; i++) {
                if (theExtension.equalsIgnoreCase(WebServicePlugin.WSDL_FILE_EXTENSIONS[i])) {
                    result = true;
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Indicates if the specified workspace file is a WSDL. 
     * @param theFile the file being checked
     * @return <code>true</code>if a WSDL file; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isWsdlFile(final IFile theFile) {
        return isWsdlFileExtension(theFile.getFileExtension());
    }
    
    /**
     * Indicates if the specified file system file is a WSDL. 
     * @param theFile the file being checked
     * @return <code>true</code>if a WSDL file; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isWsdlFile(final File theFile) {
        boolean result = false;
        String name = theFile.getName();
        int index = name.lastIndexOf(FileUtils.Constants.FILE_EXTENSION_SEPARATOR);
        
        if ((index != -1) && ((index + 2) < name.length())) {
            result = isWsdlFileExtension(name.substring(index + 1));
        }
        
        return result;
    }
    
    public static ModelGenerator createModelGenerator(  final IWebServiceModelBuilder builder ) {
        // Locate the Web Service model resource ...
        final ModelWorkspace modelWorkspace = ModelerCore.getModelWorkspace();
        final IPath wsModelPath = builder.getModelPath();
        ModelResource wsModelResource = modelWorkspace.findModelResource(wsModelPath);
        if ( wsModelResource == null ) {
            // Then create the model ...
            final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            final IFile file = workspaceRoot.getFile(wsModelPath);
            wsModelResource = ModelerCore.create(file);
            try {
                wsModelResource.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
                wsModelResource.getModelAnnotation().setPrimaryMetamodelUri(WebServicePackage.eNS_URI);
                wsModelResource.save(new NullProgressMonitor(), true);
            } catch (ModelWorkspaceException theException) {
                Util.log(theException);
            }
        }
        final ModelSelector wsSelector = new ModelResourceSelector(wsModelResource);
        
        // Locate (or create temp selector for) XML Document model resource ..
        final IPath xmlModelPath = builder.getXmlModel();
        ModelSelector xmlSelector = null;
        if ( xmlModelPath != null ) {
            ModelResource xmlModelResource = modelWorkspace.findModelResource(xmlModelPath);
            if ( xmlModelResource == null ) {
                // Then create the model ...
                final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
                final IFile file = workspaceRoot.getFile(xmlModelPath);
                xmlModelResource = ModelerCore.create(file);
                try {
                    xmlModelResource.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
                    xmlModelResource.getModelAnnotation().setPrimaryMetamodelUri(XmlDocumentPackage.eNS_URI);
                    xmlModelResource.save(new NullProgressMonitor(), true);
                } catch (ModelWorkspaceException theException) {
                    Util.log(theException);
                }
            }
            xmlSelector = new ModelResourceSelector(xmlModelResource);
        } else {
            final String uri = "WebServiceModelGenerator_XmlDocumentModelOutput.xmi"; //$NON-NLS-1$
            xmlSelector = new TransientModelSelector(uri);
        }
    
        return new WebServiceModelGenerator(builder,wsSelector,xmlSelector);
    }
    
    /**
     * Returns an instance of the plugin.
     * 
     * @return WebServicePlugin
     * @since 4.4
     */
    public static WebServicePlugin getInstance() {
        return plugin;
    }
    
    /**
     * Returns the default location for the user to save a WAR file to.
     *  
     * @return
     * @since 4.4
     */
    public static String getDefaultWarFileSaveLocation() {
        
        String defaultWarFileSaveLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
        File workspaceFile = new File(defaultWarFileSaveLocation);
        if(workspaceFile.exists()) {
            File parentFolder = workspaceFile.getParentFile();
            if(parentFolder!=null) {
                File saveFolder = new File(parentFolder,WAR_DEFAULT);
                
                defaultWarFileSaveLocation = saveFolder.toString();
            }
        }
        
        return defaultWarFileSaveLocation;
    }

}
