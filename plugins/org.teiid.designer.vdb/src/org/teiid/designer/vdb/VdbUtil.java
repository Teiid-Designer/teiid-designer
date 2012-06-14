/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.teiid.designer.vdb.Vdb.Xml;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.VdbElement;
import org.xml.sax.SAXException;
import com.metamatrix.core.modeler.util.OperationUtil;
import com.metamatrix.core.modeler.util.OperationUtil.Unreliable;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelType;

/**
 * Utility methods used to query VDB manifest and VDB's
 */
public class VdbUtil {

    @SuppressWarnings( "javadoc" )
    public static final String PHYSICAL = "PHYSICAL"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String VIRTUAL = "VIRTUAL"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String FUNCTION = "FUNCTION"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String OTHER = "OTHER"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String DEPRECATED_TYPE = "TYPE"; //$NON-NLS-1$

    private static final String MANIFEST = "META-INF/vdb.xml"; //$NON-NLS-1$

    /**
     * @param theVdb
     * @return list of vdb model files
     */
    public static Collection<IFile> getVdbModels( Vdb theVdb ) {
        Collection<IFile> iFiles = new ArrayList<IFile>();

        for (VdbModelEntry modelEntry : theVdb.getModelEntries()) {
            // IPath modelPath = modelEntry.getName();
            IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(modelEntry.getName());

            // if resource has been moved in the workspace since being added to the VDB then it will not be found
            if ((resource != null) && resource.exists()) {
                iFiles.add((IFile)resource);
            }
        }

        return iFiles;
    }

    /**
     * @param file
     * @return preview attribute value for VDB. true or false
     */
    public static boolean isPreviewVdb( final IFile file ) {
        CoreArgCheck.isNotNull(file, "file is null"); //$NON-NLS-1$

        if (file.exists()) {
            // if VDB file is empty just check file name
            if (file.getLocation().toFile().length() == 0L) {
                // make sure file prefix and extension is right
                if (!Vdb.FILE_EXTENSION_NO_DOT.equals(file.getFileExtension())) {
                    return false;
                }

                return file.getName().startsWith(Vdb.PREVIEW_PREFIX);
            }

            VdbElement manifest = VdbUtil.getVdbManifest(file);
            if (manifest != null) {
                // VDB properties
                for (final PropertyElement property : manifest.getProperties()) {
                    final String name = property.getName();
                    if (Xml.PREVIEW.equals(name)) {
                        return Boolean.parseBoolean(property.getValue());
                    }
                }
            }
        }

        return false;
    }

    /**
     * Utility method to determine if a vdb contains models of a certain "class"
	 * @param file
	 * * @param modelClass
     * @param type
     * @return preview attribute value for VDB. true or false
     */
	public static boolean hasModelClass(final IFile file, final String modelClass, final String type) {
        if (file.exists() && Vdb.FILE_EXTENSION_NO_DOT.equals(file.getFileExtension())) {
            VdbElement manifest = VdbUtil.getVdbManifest(file);
            if (manifest != null) {
                for (ModelElement model : manifest.getModels()) {
                    String typeValue = model.getType();
                    if (type.equalsIgnoreCase(typeValue)) {
                        for (final PropertyElement property : model.getProperties()) {
                            final String name = property.getName();
                            if (ModelElement.MODEL_CLASS.equals(name)) {
                                String modelClassValue = property.getValue();
                                if (modelClass.equalsIgnoreCase(modelClassValue)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private static VdbElement getVdbManifest( final IFile file ) {
        final VdbElement[] manifest = new VdbElement[1];

        if (!file.exists()) {
            return null;
        }

        OperationUtil.perform(new Unreliable() {

            ZipFile archive = null;
            InputStream entryStream = null;

            @Override
            public void doIfFails() {
            }

            @Override
            public void finallyDo() throws Exception {
                if (entryStream != null) entryStream.close();
                if (archive != null) archive.close();
            }

            @Override
            public void tryToDo() throws Exception {
                archive = new ZipFile(file.getLocation().toString());
                boolean foundManifest = false;
                for (final Enumeration<? extends ZipEntry> iter = archive.entries(); iter.hasMoreElements();) {
                    final ZipEntry zipEntry = iter.nextElement();
                    entryStream = archive.getInputStream(zipEntry);
                    if (zipEntry.getName().equals(MANIFEST)) {
                        // Initialize using manifest
                        foundManifest = true;
                        final Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
                        unmarshaller.setSchema(getManifestSchema());
                        manifest[0] = (VdbElement)unmarshaller.unmarshal(entryStream);

                    }
                    // Don't process any more than we need to.
                    if (foundManifest) {
                        break;
                    }
                }
            }
        });

        return manifest[0];
    }

    /**
     * @param file
     * @return version the vdb version number
     */
    public static int getVdbVersion( final IFile file ) {

        if (file.exists()) {
            VdbElement manifest = VdbUtil.getVdbManifest(file);
            if (manifest != null) {
                return manifest.getVersion();
            }
        }

        return 0;
    }

    static JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(new Class<?>[] {VdbElement.class});
    }

    static Schema getManifestSchema() throws SAXException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(VdbElement.class.getResource("/vdb-deployer.xsd")); //$NON-NLS-1$
    }

    /**
     * This method converts a vdb manifest model type and model path to a Designer ModelType object
     * Reason being that an XML Schema (TYPE) model is defined in the vdb manifest as "OTHER"
     * @param vdbModelType
     * @param modelPath
     * @return ModelType
     */
    public static ModelType getModelType(String vdbModelType, String modelPath) {
        if (vdbModelType == OTHER && modelPath.toUpperCase().endsWith(".XSD")) { //$NON-NLS-1$
            return ModelType.TYPE_LITERAL;
        }

        return ModelType.get(vdbModelType);
    }
}
