/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.model.BasicConnectorBinding;
import com.metamatrix.common.config.util.ConfigurationPropertyNames;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.vdb.VdbConstants;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.loader.VDBReader;
import com.metamatrix.vdb.edit.loader.VDBWriter;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;
import com.metamatrix.vdb.internal.edit.VdbEditingContextImpl;
import com.metamatrix.vdb.runtime.BasicModelInfo;
import com.metamatrix.vdb.runtime.BasicVDBDefn;

/**
 * @since 4.2
 */
public class VdbDefnHelper {

    public static Properties HEADER_PROPERTIES = new Properties();
    private static final String VERSION_1 = "1"; //$NON-NLS-1$
    public static final String PREFIX = I18nUtil.getPropertyPrefix(VdbDefnHelper.class);

    static {
        HEADER_PROPERTIES.put(ConfigurationPropertyNames.APPLICATION_CREATED_BY, "MetaBase Modeler"); //$NON-NLS-1$
    }

    /**
     * Returns a ModelReference from the specified VdbEditingContext that matches the input ModelInfo.
     * 
     * @param context
     * @param model
     * @return the ModelReference matching the specified ModelInfo, or null if no ModelReference could be found.
     * @since 4.3
     */
    public static ModelReference findModelReference( VdbEditingContext context,
                                                     ModelInfo model ) {
        for (Iterator iter = context.getVirtualDatabase().getModels().iterator(); iter.hasNext();) {
            ModelReference ref = (ModelReference)iter.next();
            String refName = ref.getName();
            int index = StringUtil.indexOfIgnoreCase(refName, ModelerCore.MODEL_FILE_EXTENSION);

            // if needed strip off model extension
            if (index != -1) {
                refName = refName.substring(0, index);
            }

            if (refName.equals(model.getName())) {
                return ref;
            }
        }

        return null;
    }

    public static ModelReference findModelReference( VdbContextEditor context,
                                                     ModelInfo model ) {
        for (Iterator iter = context.getVirtualDatabase().getModels().iterator(); iter.hasNext();) {
            ModelReference ref = (ModelReference)iter.next();
            String refName = ref.getName();
            int index = StringUtil.indexOfIgnoreCase(refName, ModelerCore.MODEL_FILE_EXTENSION);

            // if needed strip off model extension
            if (index != -1) {
                refName = refName.substring(0, index);
            }

            if (refName.equals(model.getName())) {
                return ref;
            }
        }

        return null;
    }

    public static File getVdbDefinitionFile( VdbEditingContext theContext ) {
        File defnFile = null;
        if (theContext != null) {
            try {
                if (!theContext.isOpen()) {
                    theContext.open();
                }
            } catch (Exception e) {
                DqpPlugin.Util.log(IStatus.ERROR, e.getLocalizedMessage());
                return null;
            }

            defnFile = theContext.getVdbDefinitionFile();
        }
        return defnFile;
    }

    public static File getVdbDefinitionFile( VdbContextEditor theContext ) {
        File defnFile = null;
        if (theContext != null) {
            try {
                if (!theContext.isOpen()) {
                    theContext.open(new NullProgressMonitor());
                }
            } catch (Exception e) {
                DqpPlugin.Util.log(IStatus.ERROR, e.getLocalizedMessage());
                return null;
            }

            File tempDirFolder = new File(theContext.getTempDirectory().getPath());
            defnFile = new File(tempDirFolder, VdbConstants.DEF_FILE_NAME);

            // If the .DEF file does not already exists in the temp directory then get from the VDB
            if (!defnFile.exists()) {
                InputStream is = null;
                try {
                    File vdbFile = theContext.getVdbFile();

                    // can't create a zip file from an empty VDB because an exception is thrown
                    if (vdbFile.exists() && (vdbFile.length() != 0)) {
                        ZipFile archive = new ZipFile(vdbFile);
                        ZipEntry entry = archive.getEntry(VdbConstants.DEF_FILE_NAME);
                        if (entry != null) {
                            is = archive.getInputStream(entry);
                            FileUtils.write(is, defnFile);
                        }
                    }
                } catch (IOException e) {
                    defnFile = null;
                    DqpPlugin.Util.log(IStatus.ERROR, e.getLocalizedMessage());
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
        return defnFile;
    }

    private File vdbFile;
    private InternalVdbEditingContext vdbContext;
    private VdbContextEditor vdbContextEditor;
    private VDBDefn vdbDefn;

    public VdbDefnHelper( File theVdb,
                          InternalVdbEditingContext theEditingContext ) {
        Assertion.isNotNull(theVdb);
        Assertion.isNotNull(theEditingContext);

        this.vdbContext = theEditingContext;
        this.vdbContextEditor = null;
        this.vdbFile = theVdb;

        getOrCreateVdbDefnFile();
    }

    public VdbDefnHelper( File theVdb,
                          VdbContextEditor theEditingContext ) {
        Assertion.isNotNull(theVdb);
        Assertion.isNotNull(theEditingContext);

        this.vdbContext = null;
        this.vdbContextEditor = theEditingContext;
        this.vdbFile = theVdb;

        getOrCreateVdbDefnFile();
    }

    /**
     * <strong>Constructor used for test purposes only.</strong>
     * 
     * @param vdbDefn
     * @throws Exception
     * @since 4.3
     */
    protected VdbDefnHelper( VDBDefn vdbDefn ) throws Exception {
        this.vdbDefn = vdbDefn;
    }

    public ConnectorBindingType[] findConnectorBindingTypeMatches( ModelInfo theModelInfo ) {
        ConnectorBindingType[] result = null;
        ModelReference modelRef = getModelReference(theModelInfo);

        if (modelRef != null) {
            try {
                ModelConnectorBindingMapper mapper = getModelConnectorBindingMapper();
                Collection temp = mapper.findConnectorTypeMatches(modelRef);

                if ((temp != null) && !temp.isEmpty()) {
                    temp.toArray(result = new ConnectorBindingType[temp.size()]);
                }
            } catch (Exception theException) {
                result = null;
                theException.printStackTrace();
                DqpPlugin.Util.log(theException);
            }
        }

        if (result == null) {
            result = new ConnectorBindingType[0];
        }

        return result;
    }

    /**
     * Obtains a collection of {@link ConnectorBinding}s that match the import source.
     * 
     * @param theModelInfo the model info
     * @return the matching bindings (never <code>null</code>)
     * @since 4.3
     */
    public ConnectorBinding[] findConnectorBindingMatches( ModelInfo theModelInfo ) {
        ConnectorBinding[] result = null;
        ModelReference modelRef = getModelReference(theModelInfo);

        if (modelRef != null) {
            try {
                ModelConnectorBindingMapper mapper = getModelConnectorBindingMapper();

                Collection temp = mapper.findConnectorBindingMatches(modelRef);

                if ((temp != null) && !temp.isEmpty()) {
                    result = (ConnectorBinding[])temp.toArray(new ConnectorBinding[temp.size()]);
                }
            } catch (Exception theException) {
                result = null;
                theException.printStackTrace();
                DqpPlugin.Util.log(theException);
            }
        }

        if (result == null) {
            result = new ConnectorBinding[0];
        }

        return result;
    }

    /**
     * Preconditions: VDB context and VDB file have been set and are not null.
     * 
     * @return
     * @since 4.3
     */
    private VDBDefn getOrCreateVdbDefnFile() {
        // if VDB Defn exists use it
        File defnFile = getVdbDefinitionFile();

        if (defnFile != null) {
            try {
                ConfigurationModelContainer cmc = DqpPlugin.getInstance().getConfigurationManager().getDefaultConfig().getCMContainerImpl();
                this.vdbDefn = null;
                if (this.vdbContext != null) {
                    this.vdbDefn = VDBReader.loadVDBDefn((VdbEditingContextImpl)this.vdbContext, false, cmc);
                } else if (this.vdbContextEditor != null) {
                    this.vdbDefn = VDBReader.loadVDBDefn(this.vdbContextEditor, false, cmc);
                }
            } catch (Exception theException) {
                theException.printStackTrace();
                DqpPlugin.Util.log(theException);
            }
        }

        // create defn if necessary
        if (this.vdbDefn == null) {
            // need to create VDB Defn
            try {
                this.vdbDefn = null;
                if (this.vdbContext != null) {
                    this.vdbDefn = VDBReader.loadVDBDefn((VdbEditingContextImpl)this.vdbContext, false);
                } else if (this.vdbContextEditor != null) {
                    this.vdbDefn = VDBReader.loadVDBDefn(this.vdbContextEditor, false);
                }
            } catch (Exception theException) {
                theException.printStackTrace();
                DqpPlugin.Util.log(theException);
            }
        }

        // must have a defn for this class to function
        Assertion.isNotNull(this.vdbDefn, DqpPlugin.Util.getStringOrKey(PREFIX + "nullVdbDefn")); //$NON-NLS-1$

        return this.vdbDefn;
    }

    private String getDefnLocation() {
        return DqpPath.getRuntimeConfigPath().toFile().getAbsolutePath();
    }

    public VDBDefn getVdbDefn() {
        return this.vdbDefn;
    }

    private String getVdbFileName() {
        return this.vdbFile.getName();
    }

    /**
     * Ensure that this defn is up-to-date with the vdb in the workspace. Retains default behavior which is to clear bindings upon
     * model removal.
     * 
     * @since 4.2
     */
    public boolean updateToVdb() {
        return updateToVdb(true);
    }

    /**
     * Ensure that this defn is up-to-date with the vdb in the workspace.
     * 
     * @since 4.2
     * @param removeBindings allows user to specify whether unused connectorBindings get removed upon removal of a model.
     */
    public boolean updateToVdb( boolean removeBindings ) {
        boolean wasUpdated = false;
        try {
            assertContextIsOpen();

            // Update description
            final VirtualDatabase vdb = getVirtualDatabase();
            final BasicVDBDefn basicDefn = (BasicVDBDefn)this.vdbDefn;
            basicDefn.setDescription(vdb.getDescription());

            // check the in memory VDB for it's models to compare with
            List currentModelList = vdb.getModels();

            // load the collection of models from the VDB
            Collection currentModelNameList = new ArrayList(currentModelList.size());
            Collection visibleModelNameList = new ArrayList(currentModelList.size());
            List temp = new ArrayList(currentModelList.size());
            HashMap currentModelNameMap = new HashMap();

            // build up a collection of the model names and a map of names to model objects
            for (Iterator iter = currentModelList.iterator(); iter.hasNext();) {
                ModelReference ref = (ModelReference)iter.next();
                ModelInfo model = VDBReader.createModelInfo(ref);
                temp.add(model);
                currentModelNameList.add(model.getName());
                currentModelNameMap.put(model.getName(), model);
                if (model.isVisible()) {
                    visibleModelNameList.add(model.getName());
                    wasUpdated = true;
                }
            }

            // list needs to be containing ModelInfo objects
            currentModelList = temp;

            // load the collection of models from the DEF file that we read in the constructor
            Collection defFileModelList = this.vdbDefn.getModels();
            Collection defFileModelNameList = new ArrayList(defFileModelList.size());
            // build up a collection of the model names and a map of names to model objects
            for (Iterator iter = defFileModelList.iterator(); iter.hasNext();) {
                ModelInfo model = (ModelInfo)iter.next();
                defFileModelNameList.add(model.getName());
            }

            // figure out what models need to be added from the VDB to the DEF file
            Collection modelsToAdd = new ArrayList(currentModelList.size());
            // walk through the current model list and see if they all exist in the DEF file
            for (Iterator iter = currentModelList.iterator(); iter.hasNext();) {
                ModelInfo modelFromVdb = (ModelInfo)iter.next();
                if (!defFileModelNameList.contains(modelFromVdb.getName())) {
                    modelsToAdd.add(currentModelNameMap.get(modelFromVdb.getName()));
                }
            }

            // figure out what models need to be removed DEF file that are no longer in the VDB
            Collection modelNamesToRemove = new ArrayList(defFileModelList.size());
            // walk through the DEF file model list and see if they all exist in the VDB
            for (Iterator iter = defFileModelList.iterator(); iter.hasNext();) {
                ModelInfo modelFromDefFile = (ModelInfo)iter.next();
                if (!currentModelNameList.contains(modelFromDefFile.getName())) {
                    modelNamesToRemove.add(modelFromDefFile.getName());
                }
            }

            // add any models to the DEF file that are new in the VDB
            if (!modelsToAdd.isEmpty()) {
                for (Iterator iter = modelsToAdd.iterator(); iter.hasNext();) {
                    basicDefn.addModelInfo((ModelInfo)iter.next());
                    wasUpdated = true;
                }
            }
            // remove any models from the DEF file that are no longer in the VDB
            if (!modelNamesToRemove.isEmpty()) {
                for (Iterator iter = modelNamesToRemove.iterator(); iter.hasNext();) {
                    basicDefn.removeModelInfo((String)iter.next(), removeBindings);
                    wasUpdated = true;
                }
            }
            // check the visibility of all models in the VDB against the Def file models
            for (Iterator iter = this.vdbDefn.getModels().iterator(); iter.hasNext();) {
                BasicModelInfo model = (BasicModelInfo)iter.next();
                if (model.isVisible()) {
                    if (!visibleModelNameList.contains(model.getName())) {
                        // this model is not in the VDB's visible name list, so it needs to be set PUBLIC
                        model.setVisibility(ModelInfo.PRIVATE);
                        wasUpdated = true;
                    }
                } else {
                    if (visibleModelNameList.contains(model.getName())) {
                        // this model is in the VDB's visible name list, so it needs to be set PUBLIC
                        model.setVisibility(ModelInfo.PUBLIC);
                        wasUpdated = true;
                    }
                }
            }

        } catch (Exception e) {
            DqpPlugin.Util.log(e);
        }

        return wasUpdated;
    }

    public ConnectorBinding getFirstConnectorBinding( ModelInfo modelDefn ) {
        ConnectorBinding result = null;
        ModelInfo model = this.vdbDefn.getModel(modelDefn.getName());
        if (model != null) {
            List cbNames = model.getConnectorBindingNames();

            if ((cbNames != null) && !cbNames.isEmpty()) {
                result = this.vdbDefn.getConnectorBindingByName((String)cbNames.get(0));
            }
        }
        return result;
    }

    protected Collection getConnectorBindings( ModelInfo modelDefn ) {
        List result = Collections.EMPTY_LIST;
        ModelInfo model = this.vdbDefn.getModel(modelDefn.getName());
        List cbnames = model.getConnectorBindingNames();

        if ((cbnames != null) && !cbnames.isEmpty()) {
            result = new ArrayList();

            for (int size = cbnames.size(), i = 0; i < size; ++i) {
                ConnectorBinding binding = this.vdbDefn.getConnectorBindingByName((String)cbnames.get(i));

                if (binding != null) {
                    result.add(binding);
                } else {
                    // should not happen
                    String msg = DqpPlugin.Util.getString("VdbDefnHelper.noBindingForUuid", cbnames.get(i)); //$NON-NLS-1$
                    DqpPlugin.Util.log(IStatus.ERROR, msg);
                }
            }
        }

        return result;
    }

    public ConnectorBinding createConnectorBinding( ComponentType ctConnector,
                                                    String sConnBindName,
                                                    boolean theAddToConfigurationFlag ) throws Exception {
        return DqpPlugin.getInstance().getConfigurationManager().createConnectorBinding(ctConnector,
                                                                                        sConnBindName,
                                                                                        theAddToConfigurationFlag);
    }

    /**
     * Creates a new binding from an existing one.
     * 
     * @param theSourceBinding the binding whose properties are being copied
     * @param theNewBindingName the new binding name
     * @return the new binding
     * @since 4.3
     */
    public ConnectorBinding createConnectorBinding( ConnectorBinding theSourceBinding,
                                                    String theNewBindingName ) throws Exception {
        return DqpPlugin.getInstance().getConfigurationManager().createConnectorBinding(theSourceBinding, theNewBindingName);
    }

    public void removeConnectorBinding( ModelInfo modelDefn,
                                        ConnectorBinding binding ) {
        BasicVDBDefn basicDefn = (BasicVDBDefn)vdbDefn;
        basicDefn.removeConnectorBinding(modelDefn.getName(), binding.getName());
    }

    // TODO: this needs to work for single bindings, but I don't see a way to remove a binding from basicDefn.
    public void setConnectorBinding( ModelInfo modelDefn,
                                     ConnectorBinding binding,
                                     ComponentType type ) {
        BasicVDBDefn basicDefn = (BasicVDBDefn)vdbDefn;

        BasicConnectorBinding clonedBinding = (BasicConnectorBinding)binding;
        // don't remove from VDBDefn as this would remove all mappings to this binding
        // calling add will replace existing binding of that name with this one.
        // then make sure defn has type

        clonedBinding = (BasicConnectorBinding)binding.clone();

        // add a deploy name to all the connector bindings going into the VDBS
        clonedBinding.setDeployedName(basicDefn.getName() + "_" + basicDefn.getVersion() + "." + binding.getFullName()); //$NON-NLS-1$ //$NON-NLS-2$ 
        basicDefn.addConnectorBinding(modelDefn.getName(), clonedBinding);
        basicDefn.addConnectorType(type);
    }

    public void exportDefn( String fileName,
                            String location ) throws Exception {
        VDBWriter.exportVDBDefn(getVdbDefn(), fileName, getVdbFileName(), location, HEADER_PROPERTIES);
    }

    /**
     * Assigns a {@link ConnectorBinding} to a model if no binding currently exists and there is a exactly one matching binding
     * available. A binding matches when it has the same driver class, user, and URL as the import source. If there is no import
     * source or more than one matching binding no assignment is made.
     * 
     * @return <code>true</code>if VDB is not readonly and at least one binding was auto assigned; <code>false</code> otherwise.
     * @throws Exception if a problem saving VDB
     * @since 4.3
     */
    public boolean autoAssignBindings() {
        boolean result = false;

        // make sure VDB can be saved before assigning binding
        if (this.vdbFile.canWrite()) {
            VDBDefn defn = getVdbDefn();

            if (defn != null) {
                Collection models = defn.getModels();

                if ((models != null) && !models.isEmpty()) {
                    Iterator itr = models.iterator();

                    while (itr.hasNext()) {
                        ModelInfo info = (ModelInfo)itr.next();

                        // if model needs a binding check to see if any binding has been assigned
                        if (info.requiresConnectorBinding()) {
                            List bindingNames = info.getConnectorBindingNames();

                            // if no bindings have been assigned then find any matches
                            if ((bindingNames == null) || bindingNames.isEmpty()) {
                                ConnectorBinding[] bindings = findConnectorBindingMatches(info);

                                // assign the first match
                                if ((bindings != null) && (bindings.length == 1)) {
                                    setConnectorBinding(info, bindings[0], ModelerDqpUtils.getConnectorType(bindings[0]));
                                    result = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Saves the VDB Defn in the VDB archive.
     * 
     * @throws Exception if a problem writing archive
     * @since 4.3
     */
    public boolean saveDefn() throws Exception {
        return saveDefn(true);
    }

    /**
     * Saves the VDB Defn in the VDB archive.
     * 
     * @throws Exception if a problem writing archive
     * @since 4.3
     */
    public boolean saveDefn( boolean syncWithVdb ) throws Exception {

        if (syncWithVdb) {
            updateToVdb();
        }
        boolean wasSaved = true;

        // make sure VDB has a version before writing DEF
        VirtualDatabase vdb = getVirtualDatabase();

        if (vdb.getVersion() == null) {
            vdb.setVersion(VERSION_1);
        }

        VDBWriter.writeVDBDefn(new FileOutputStream(new File(getDefnLocation(), VdbConstants.DEF_FILE_NAME)),
                               getVdbDefn(),
                               HEADER_PROPERTIES);
        IPath archivePath = new Path(VdbConstants.DEF_FILE_NAME);
        if (this.vdbContext != null) {
            this.vdbContext.refreshNonModel(null,
                                            DqpPath.getRuntimeConfigPath().append(VdbConstants.DEF_FILE_NAME).toFile(),
                                            archivePath);
        } else if (this.vdbContextEditor != null) {
            File sourceDirectory = DqpPath.getRuntimeConfigPath().toFile();
            File sourceDefnFile = new File(sourceDirectory, VdbConstants.DEF_FILE_NAME);
            File targetDirectory = new File(this.vdbContextEditor.getTempDirectory().getPath());
            File targetDefnFile = new File(targetDirectory, VdbConstants.DEF_FILE_NAME);
            if (sourceDefnFile.exists()) {
                FileUtils.copy(sourceDefnFile.getAbsolutePath(), targetDefnFile.getAbsolutePath(), true);
            }
        }

        return wasSaved;
    }

    private ModelConnectorBindingMapper getModelConnectorBindingMapper() throws Exception {
        ModelConnectorBindingMapper mapper = null;
        if (this.vdbContext != null) {
            mapper = new ModelConnectorBindingMapperImpl(this.vdbContext);
        } else if (this.vdbContextEditor != null) {
            mapper = new ModelConnectorBindingMapperImpl(this.vdbContextEditor);
        }
        return mapper;
    }

    private ModelReference getModelReference( ModelInfo theModelInfo ) {
        ModelReference modelRef = null;
        if (this.vdbContext != null) {
            modelRef = VdbDefnHelper.findModelReference(this.vdbContext, theModelInfo);
        } else if (this.vdbContextEditor != null) {
            modelRef = VdbDefnHelper.findModelReference(this.vdbContextEditor, theModelInfo);
        }
        return modelRef;
    }

    private VirtualDatabase getVirtualDatabase() {
        VirtualDatabase vdb = null;
        if (this.vdbContext != null) {
            vdb = this.vdbContext.getVirtualDatabase();
        } else if (this.vdbContextEditor != null) {
            vdb = this.vdbContextEditor.getVirtualDatabase();
        }
        return vdb;
    }

    private void assertContextIsOpen() throws Exception {
        if (this.vdbContext != null) {
            if (!this.vdbContext.isOpen()) {
                this.vdbContext.open();
            }
        } else if (this.vdbContextEditor != null) {
            if (!this.vdbContextEditor.isOpen()) {
                this.vdbContextEditor.open(new NullProgressMonitor());
            }
        }
    }

    private File getVdbDefinitionFile() {
        try {
            assertContextIsOpen();
        } catch (Exception e) {
            DqpPlugin.Util.log(IStatus.ERROR, e.getLocalizedMessage());
            return null;
        }
        File defnFile = null;
        if (this.vdbContext != null) {
            defnFile = VdbDefnHelper.getVdbDefinitionFile(this.vdbContext);
        } else if (this.vdbContextEditor != null) {
            defnFile = VdbDefnHelper.getVdbDefinitionFile(this.vdbContextEditor);
        }
        return defnFile;
    }

}
