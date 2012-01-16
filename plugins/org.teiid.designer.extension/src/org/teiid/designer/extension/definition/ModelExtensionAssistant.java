/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.util.Set;

import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinitionImpl;
import org.teiid.designer.extension.properties.Translation;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * The <code>ModelExtensionAssistant</code> is used when a model extension definition file is parsed and also when working with
 * model objects.
 */
public class ModelExtensionAssistant implements ExtensionConstants {

    private ModelExtensionDefinition definition;

    /**
     * @param modelType the model type being added (cannot be <code>null</code> or empty)
     */
    public boolean addSupportedModelType(String modelType) {
        CoreArgCheck.isNotEmpty(modelType, "modelType is empty"); //$NON-NLS-1$
        assert (this.definition != null) : "model extension definition is null"; //$NON-NLS-1$
        return this.definition.addModelType(modelType);
    }

    /**
     * This method should only be called by the {@link ModelExtensionDefinitionParser}.
     * 
     * @param metaclassName the metaclass name being extended (cannot be <code>null</code> or empty)
     * @param propertyDefinition the property definition being added (cannot be <code>null</code>)
     */
    public void addPropertyDefinition( String metaclassName,
                                       ModelExtensionPropertyDefinition propertyDefinition ) {
        assert this.definition != null : "model extension definition is null"; //$NON-NLS-1$
        this.definition.addPropertyDefinition(metaclassName, propertyDefinition);
    }

    /**
     * This method should only be called by the {@link ModelExtensionDefinitionParser}.
     * 
     * @param namespacePrefix the unique namespace prefix (can be <code>null</code> or empty)
     * @param namespaceUri the unique namespace URI (can be <code>null</code> or empty)
     * @param metamodelUri the metamodel URI this definition is extending (can be <code>null</code> or empty)
     * @param modelTypes the supported model types (can be <code>null</code> or empty)
     * @param description the description of the definition (can be <code>null</code> or empty)
     * @param version the definition version (can be <code>null</code> or empty)
     * @return the new model extension definition (never <code>null</code>)
     */
    public ModelExtensionDefinition createModelExtensionDefinition( String namespacePrefix,
                                                                    String namespaceUri,
                                                                    String metamodelUri,
                                                                    Set<String> modelTypes,
                                                                    String description,
                                                                    String version ) {
        this.definition = new ModelExtensionDefinition(this, namespacePrefix, namespaceUri, metamodelUri, description, version);

        if ((modelTypes != null) && !modelTypes.isEmpty()) {
            for (String modelType : modelTypes) {
                addSupportedModelType(modelType);
            }
        }

        return this.definition;
    }

    /**
     * Create a ModelExtensionDefinition using the header info
     * 
     * @param medHeader the ModelExtensionDefinitionHeader (cannot be <code>null</code>)
     * @return the new model extension definition (never <code>null</code>)
     */
    public ModelExtensionDefinition createModelExtensionDefinition( ModelExtensionDefinitionHeader medHeader ) {
        CoreArgCheck.isNotNull(medHeader, "ModelExtensionDefinitionHeader is null"); //$NON-NLS-1$
        ModelExtensionDefinition med = createModelExtensionDefinition(medHeader.getNamespacePrefix(), medHeader.getNamespaceUri(),
                                                                      medHeader.getMetamodelUri(),
                                                                      medHeader.getSupportedModelTypes(),
                                                                      medHeader.getDescription(),
                                                                      String.valueOf(medHeader.getVersion()));

        return med;
    }

    /**
     * This method should only be called by the {@link ModelExtensionDefinitionParser}.
     * 
     * @param simpleId the property identifier without the namespace prefix (cannot be <code>null</code> or empty)
     * @param runtimeType the Teiid runtime type (cannot be <code>null</code> or empty)
     * @param required <code>true</code> string if this property must have a value (cannot be <code>null</code> or empty)
     * @param defaultValue a default value (can be <code>null</code> or empty)
     * @param fixedValue a constant value, when non-<code>null</code> and non-empty, indicates the property value cannot be changed
     *            (can be <code>null</code> or empty)
     * @param advanced <code>true</code> string if this property should only be shown to advances users (cannot be <code>null</code>
     *            or empty)
     * @param masked <code>true</code> string if this property value must be masked (cannot be <code>null</code> or empty)
     * @param index <code>true</code> string if this property value must be indexed for use by the Teiid server (cannot be
     *            <code>null</code> or empty)
     * @param allowedValues the allowed property values (can be <code>null</code> or empty)
     * @param descriptions the one or more translations of the property description (can be <code>null</code> or empty)
     * @param displayNames the one or more translations of the property display name (can be <code>null</code> or empty)
     * @return the new extension property definition (never <code>null</code>)
     */
    public ModelExtensionPropertyDefinition createPropertyDefinition( String simpleId,
                                                                      String runtimeType,
                                                                      String required,
                                                                      String defaultValue,
                                                                      String fixedValue,
                                                                      String advanced,
                                                                      String masked,
                                                                      String index,
                                                                      Set<String> allowedValues,
                                                                      Set<Translation> descriptions,
                                                                      Set<Translation> displayNames ) {
        assert this.definition != null : "model extension definition is null"; //$NON-NLS-1$
        return new ModelExtensionPropertyDefinitionImpl(this.definition,
                                                        simpleId,
                                                        runtimeType,
                                                        required,
                                                        defaultValue,
                                                        fixedValue,
                                                        advanced,
                                                        masked,
                                                        index,
                                                        allowedValues,
                                                        descriptions,
                                                        displayNames);
    }

    /**
     * @return the model extension definition (MED) (never <code>null</code>)
     */
    public ModelExtensionDefinition getModelExtensionDefinition() {
        return this.definition;
    }

    /**
     * @return the namespace prefix (never <code>null</code> or empty)
     */
    public final String getNamespacePrefix() {
        return this.definition.getNamespacePrefix();
    }

    /**
     * @param proposedOperationName the name of the operation that will be performed on this assistant's model extension definition
     *            (never <code>null</code>)
     * @param context the operation context (can be <code>null</code>)
     * @return <code>true</code> if the operation should be performed
     */
    public boolean supportsMedOperation( String proposedOperationName,
                                         Object context ) {
        CoreArgCheck.isNotEmpty(proposedOperationName, "proposedOperationName is empty"); //$NON-NLS-1$

        if (MedOperations.DELETE_MED_FROM_REGISTRY.equals(proposedOperationName) && getModelExtensionDefinition().isBuiltIn()) {
            return false;
        }

        return true;
    }

}
