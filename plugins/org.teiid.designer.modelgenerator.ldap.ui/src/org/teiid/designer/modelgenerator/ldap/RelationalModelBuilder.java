/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.ldap;

import java.util.Collection;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.profiles.ldap.LDAPConnectionInfoProvider;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.modelgenerator.ldap.ui.ModelGeneratorLdapUiConstants;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapAttributeNode;
import org.teiid.designer.modelgenerator.ldap.ui.wizards.ILdapEntryNode;
import org.teiid.designer.ui.viewsupport.ModelUtilities;



/**
 * The Model Builder for creating the physical model for LDAP services.
 */
public class RelationalModelBuilder {
    private RelationalFactory factory;

    private DatatypeManager datatypeManager;

    private LDAPConnectionInfoProvider connProvider;

    /**
     * Create a new instance
     */
    public RelationalModelBuilder() {
        this.factory = org.teiid.designer.metamodels.relational.RelationalPackage.eINSTANCE.getRelationalFactory();
        this.datatypeManager = ModelerCore.getBuiltInTypesManager();
        this.connProvider = new LDAPConnectionInfoProvider();
    }

    private ModelResource createNewModelResource(IContainer modelLocation, String modelName) {
        if (! modelName.endsWith(StringConstants.DOT_XMI)) {
            // Will probably not include it if the model is new but will certainly include
            // it if the model already exists.
            modelName = modelName + StringConstants.DOT_XMI;
        }

        Path modelPath = new Path(modelName);
        IFile modelFile = modelLocation.getFile(modelPath);

        ModelResource resource = ModelerCore.create(modelFile);
        return resource;
    }

    /**
     * @param entry
     * @param entryModel
     */
    private void modelEntry(ILdapEntryNode entry, ModelResource entryModel) throws Exception {
        BaseTable entryTable = factory.createBaseTable();
        entryModel.getEmfResource().getContents().add(entryTable);
        entryTable.setName(entry.getLabel());
        entryTable.setNameInSource(entry.getSourceName() + entry.getSourceNameSuffix());

        for (ILdapAttributeNode attribute : entry.getAttributes()) {
            Column attrColumn = factory.createColumn();
            entryTable.getColumns().add(attrColumn);
            attrColumn.setName(attribute.getLabel());
            attrColumn.setNameInSource(attribute.getId());
            attrColumn.setNullValueCount(attribute.getNullValueCount());
            attrColumn.setDistinctValueCount(attribute.getDistinctValueCount());
            attrColumn.setLength(attribute.getMaximumValueLength());

            attrColumn.setType(datatypeManager.getBuiltInDatatype(DatatypeConstants.BuiltInNames.STRING));
            attrColumn.setNullable(NullableType.NULLABLE_UNKNOWN_LITERAL);
            attrColumn.setCaseSensitive(true);
            attrColumn.setRadix(0);
            attrColumn.setSigned(false);
        }
    }

    /**
     * Create a new model resource and populate it with the given contents
     *
     * @param modelLocation destination folder of the new model
     * @param modelName name of the model file
     * @param connectionProfile profile containing all connection information
     * @param ldapModelEntries collection of LDAP entries with attributes to be modelled as tables
     *
     * @throws Exception
     */
    public ModelResource modelEntries(IContainer modelLocation, String modelName, IConnectionProfile connectionProfile, Collection<ILdapEntryNode> ldapModelEntries) throws Exception {
        final ModelResource entryModel = createNewModelResource(modelLocation, modelName);

        ModelAnnotation modelAnnotation = entryModel.getModelAnnotation();
        modelAnnotation.setModelType(ModelType.PHYSICAL_LITERAL);
        modelAnnotation.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);

        for (ILdapEntryNode entry : ldapModelEntries) {
            modelEntry(entry, entryModel);
        }

        // Inject the connection profile properties into the physical model
        connProvider.setConnectionInfo(entryModel, connectionProfile);

        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

            @Override
            public void execute(final IProgressMonitor monitor) {
                try {
                    ModelUtilities.saveModelResource(entryModel, monitor, false, this);
                } catch (Exception e) {
                    ModelGeneratorLdapUiConstants.UTIL.log(e);
                }
            }
        };

        IProgressMonitor monitor = new NullProgressMonitor();
        operation.run(monitor);
        
        return entryModel;
    }
}