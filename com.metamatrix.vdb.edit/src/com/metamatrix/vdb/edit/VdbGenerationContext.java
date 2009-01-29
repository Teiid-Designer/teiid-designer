/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * A context that contains all information about the VDB definition needed by a
 * {@link com.metamatrix.vdb.edit.VdbArtifactGenerator}. The context has methods to obtain the models that are in the VDB
 * definition, methods to record problems, and methods to add new artifacts to the VDB definition.
 * 
 * @since 4.2
 */
public interface VdbGenerationContext {

    /**
     * Returns all of the models in the VDB
     * 
     * @return the array of {@link Resource EMF Resource} objects, each of which is the in-memory form of a model in the VDB;
     *         never null, but may be empty if there are no models in the VDB
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    Resource[] getModels();

    /**
     * Returns the models in the VDB whose primary metamodels are identified by the supplied URL.
     * 
     * @param primaryMetamodelUri the URI of the metamodel; may not be null
     * @return the array of {@link Resource EMF Resource} objects, each of which is the in-memory form of a model in the VDB;
     *         never null, but may be empty if there are no models in the VDB that have the supplied primary metamodel
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    Resource[] getModels( String primaryMetamodelUri );

    /**
     * Returns the helper object that can be used to obtain some typical information about models.
     * 
     * @return the helper object for obtaining model information.
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    ModelHelper getModelHelper();

    /**
     * Returns the helper object that can be used to obtain some standard but metamodel-independent information for model objects.
     * (Metamodel-specific features and references can be accessed using the metamodel-specific interfaces or through standard EMF
     * reflection). All calls to this method on the same VdbGenerationContext instance will return the same ModelObjectHelper
     * instance.
     * 
     * @return the helper object for obtaining object information.
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    ModelObjectHelper getObjectHelper();

    /**
     * Notifies the Modeler of an error message that should be recorded in the VDB and (eventually) displayed to the user on the
     * Problems tab of the VDB editor. The Modeler will not convert the message to the current locale; it is the responsibility of
     * the generator to supply a message already in the proper locale and language. The exception parameter may be null.
     * 
     * @param message the message to be recorded; may be null or zero-length
     * @param code an error code that often is used to uniquely identify an application-specific error; use 0 if no specified
     *        value is needed
     * @param t the exception that is considered the cause of the error; may be null if there is no exception or the exception is
     *        not to be recorded
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    void addErrorMessage( String message,
                          int code,
                          Throwable t );

    /**
     * Notifies the Modeler of a warning message that should be recorded in the VDB and (eventually) displayed to the user on the
     * Problems tab of the VDB editor. The Modeler will not convert the message to the current locale; it is the responsibility of
     * the generator to supply a message already in the proper locale and language.
     * 
     * @param message the message to be recorded; may be null or zero-length
     * @param code an error code that often is used to uniquely identify an application-specific warning; use 0 if no specified
     *        value is needed
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    void addWarningMessage( String message,
                            int code );

    /**
     * Notifies the Modeler of an information message that should be recorded in the VDB and (eventually) displayed to the user on
     * the Problems tab of the VDB editor. The Modeler will not convert the message to the current locale; it is the
     * responsibility of the generator to supply a message already in the proper locale and language.
     * 
     * @param message the message to be recorded; may be null or zero-length
     * @param code an error code that often is used to uniquely identify an application-specific warning; use 0 if no specified
     *        value is needed
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    void addInfoMessage( String message,
                         int code );

    /**
     * Sets the current message on the progress monitor. The Modeler will not convert the message to the current locale; it is the
     * responsibility of the generator to supply a message already in the proper locale and language.
     * 
     * @param displayableMessage a new message for the progress monitory.
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    void setProgressMessage( String displayableMessage );

    /**
     * Gets the message for the progress monitor.
     * 
     * @return message for the progress monitory.
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 5.0
     */
    String getProgressMessage();

    /**
     * Notifies the Modeler that a new artifact is to be included in the .vdb file. This method is useful when the generator
     * produces basic character content using the Java String object. The method will return false if the file could not be added
     * to the .vdb archive (e.g., a file already exists at the specified path).
     * 
     * @param pathInVdb the path in the VDB archive where the artifact file is to be placed; may not be null
     * @param content the character content for the new artifact file; may not be null
     * @return
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    boolean addGeneratedArtifact( String pathInVdb,
                                  String content );

    /**
     * Notifies the Modeler that a new XML artifact is to be included in the .vdb file. This method is useful when the generator
     * produces XML content via JDOM. The method will return false if the file could not be added to the .vdb archive (e.g., a
     * file already exists at the specified path).
     * 
     * @param pathInVdb the path in the VDB archive where the artifact file is to be placed; may not be null or zero-length
     * @param xmlContent the {@link Document JDOM Document} that defines the content of the new artifact file; may not be null
     * @return
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    boolean addGeneratedArtifact( String pathInVdb,
                                  org.jdom.Document xmlContent );

    /**
     * Notifies the Modeler that a new artifact is to be included in the .vdb file. This method is useful for binary files or
     * files that are to be streamed. The method will return false if the file could not be added to the .vdb archive (e.g., a
     * file already exists at the specified path).
     * 
     * @param pathInVdb the path in the VDB archive where the artifact file is to be placed; may not be null or zero-length
     * @param content the stream containing the content of the new artifact file; may not be null
     * @return
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    boolean addGeneratedArtifact( String pathInVdb,
                                  java.io.InputStream content );

    /**
     * Notifies the Modeler that a new artifact is to be included in the .vdb file. This method is useful when files (such as
     * temporary files) are to be added directly to the .vdb archive files. The method will return false if the file could not be
     * added to the .vdb archive (e.g., a file already exists at the specified path).
     * 
     * @param pathInVdb the path in the VDB archive where the artifact file is to be placed; may not be null or zero-length
     * @param content the new artifact file; may not be null
     * @return
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    boolean addGeneratedArtifact( String pathInVdb,
                                  java.io.File content );

    /**
     * Returns the content objects keyed by {@link IPath path}. The content objects are instances of {@link String},
     * {@link Document}, or {@link InputStream}.
     * 
     * @return map of artifacts added through this context; never null
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    Map getGeneratedArtifactsByPath();

    /**
     * Get a folder into which temporary files may be placed. This folder will be automatically cleaned up and all files deleted
     * when the VDB save operation is completed. The returned folder will exist and may already contain files.
     * 
     * @return the File representing a temporary folder on the file system.
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    File getTemporaryDirectory();

    /**
     * Return a list of {@link org.eclipse.core.runtime.IStatus} instances representing problems resulting from artifact
     * generation.
     * 
     * @return problem list
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 5.0
     */
    List getProblems();

    /**
     * The ModelObjectHelper object can be used to find some of the �extra information� on model objects made available by the
     * MetaBase Modeler architecture and that isn�t accessible via the metamodel-specific interfaces.
     * 
     * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
     * @since 4.2
     */
    interface ModelObjectHelper {
        /**
         * Returns the stringified UUID for the object. Every model object within the Modeler has a universally unique identifier
         * (UUID).
         * 
         * @param object
         * @return
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        String getUuid( EObject objectInModel );

        /**
         * Returns the description for the object, or null there is no description. The Modeler provides a universal way to add
         * descriptions to all model objects, regardless of whether the metamodel defines a specific place. These descriptions are
         * not stored on the object itself but are instead stored in a different location within the model.
         * 
         * @param object
         * @return
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        String getDescription( EObject objectInModel );

        /**
         * Returns true if this model object has errors. Every model within the Modeler is validated against a set of rules to
         * determine if its contents adhere to constraints or boundaries imposed by the metamodel, Federate Designer, or Federate
         * Server. Errors on a model object would prevent the model from being used within the server.
         * 
         * @param object
         * @return
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        boolean hasErrors( EObject objectInModel );

        /**
         * Returns true if this model object has warnings. Every model within the Modeler is validated against a set of rules to
         * determine if its contents adhere to constraints or boundaries imposed by the metamodel, Federate Designer, or Federate
         * Server. Warnings on a model object will not prevent the model from being used within the server but should be examined
         * by the creator of the model.
         * 
         * @param object
         * @return
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        boolean hasWarnings( EObject objectInModel );

        /**
         * Returns the extended properties for the object; the properties object is empty if there are no properties. The Modeler
         * provides an extensible framework that allows users to dynamically extend a metamodel with custom name-value pairs.
         * These name-value pairs are not stored on the object itself but are instead stored in a different location within the
         * model.
         * 
         * @param object
         * @return
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        java.util.Properties getProperties( EObject objectInModel );

    }

    /**
     * The ModelHelper object can be used to find some standard information about models. Not all this information is standard EMF
     * information; much of it is made available by the MetaBase Modeler architecture.
     * 
     * @since 4.2
     */
    interface ModelHelper {
        /**
         * Returns the stringified UUID for the model. Every model object within the Modeler has a universally unique identifier
         * (UUID), and there is a single object within each model that contains model-level information.
         * 
         * @param model
         * @return
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        String getUuid( Resource model );

        /**
         * Returns the name for the model, or null there is no name.
         * 
         * @param model
         * @return the name of the model
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        String getName( Resource model );

        /**
         * Returns the path for the model, or null there is no path.
         * 
         * @param model
         * @return the path of the model
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        String getPath( Resource model );

        /**
         * Returns the target namespace for the model.
         * 
         * @param model
         * @return the target namespace URI, or null if there is none
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        String getTargetNamespaceUri( Resource model );

        /**
         * Returns the primary metamodel URI for this model. Every model has a primary metamodel; the primary objects in the model
         * are instances of that metamodel.
         * 
         * @param model
         * @return the URI of the primary metamodel, or null if it could not be determined.
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        String getPrimaryMetamodelUri( Resource model );

        /**
         * Returns the description for the model, or null there is no description.
         * 
         * @param model
         * @return
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        String getDescription( Resource model );

        /**
         * Returns the extended properties for the model; the properties object is empty if there are no properties. The Modeler
         * provides an extensible framework that allows users to dynamically extend a metamodel with custom name-value pairs.
         * These name-value pairs are not stored on the object itself but are instead stored in a different location within the
         * model.
         * 
         * @param model
         * @return
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.2
         */
        java.util.Properties getProperties( Resource model );

        /**
         * Return the {@link VdbGenerationContext.ModelType ModelType} for the supplied model.
         * 
         * @param model
         * @return the object denoting the type of model
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.3
         */
        ModelType getModelType( Resource model );

        /**
         * Return whether the model is considered visible in the virtual database.
         * 
         * @param model
         * @return true if the model is marked as visible, or false otherwise.
         * @throws VdbGenerationInterruptedException if VDB artifact generation is canceled by the user.
         * @since 4.3
         */
        boolean isVisible( Resource model );

    }

    public final class ModelType {
        public static final ModelType PHYSICAL = new ModelType(com.metamatrix.metamodels.core.ModelType.PHYSICAL_LITERAL);
        public static final ModelType CONFIGURATION = new ModelType(
                                                                    com.metamatrix.metamodels.core.ModelType.CONFIGURATION_LITERAL);
        public static final ModelType EXTENSION = new ModelType(com.metamatrix.metamodels.core.ModelType.EXTENSION_LITERAL);
        public static final ModelType FUNCTION = new ModelType(com.metamatrix.metamodels.core.ModelType.FUNCTION_LITERAL);
        public static final ModelType LOGICAL = new ModelType(com.metamatrix.metamodels.core.ModelType.LOGICAL_LITERAL);
        public static final ModelType MATERIALIZATION = new ModelType(
                                                                      com.metamatrix.metamodels.core.ModelType.MATERIALIZATION_LITERAL);
        public static final ModelType METAMODEL = new ModelType(com.metamatrix.metamodels.core.ModelType.METAMODEL_LITERAL);
        public static final ModelType TYPE = new ModelType(com.metamatrix.metamodels.core.ModelType.TYPE_LITERAL);
        public static final ModelType UNKNOWN = new ModelType(com.metamatrix.metamodels.core.ModelType.UNKNOWN_LITERAL);
        public static final ModelType VDB_ARCHIVE = new ModelType(com.metamatrix.metamodels.core.ModelType.VDB_ARCHIVE_LITERAL);
        public static final ModelType VIRTUAL = new ModelType(com.metamatrix.metamodels.core.ModelType.VIRTUAL_LITERAL);
        public static final ModelType[] ALL_MODEL_TYPES = new ModelType[] {PHYSICAL, CONFIGURATION, EXTENSION, FUNCTION, LOGICAL,
            MATERIALIZATION, METAMODEL, TYPE, UNKNOWN, VDB_ARCHIVE, VIRTUAL};

        public static final ModelType getModelType( com.metamatrix.metamodels.core.ModelType type ) {
            final int v = type.getValue();
            for (int i = 0; i != ALL_MODEL_TYPES.length; ++i) {
                final ModelType modelType = ALL_MODEL_TYPES[i];
                if (modelType.getValue() == v) {
                    return modelType;
                }
            }
            return null;
        }

        private final com.metamatrix.metamodels.core.ModelType type;

        private ModelType( final com.metamatrix.metamodels.core.ModelType type ) {
            this.type = type;
        }

        public int getValue() {
            return type.getValue();
        }

        public String getDisplayName() {
            return type.getDisplayName();
        }

        public String getName() {
            return type.getName();
        }

        @Override
        public String toString() {
            return type.getName();
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj instanceof ModelType) {
                ModelType that = (ModelType)obj;
                return this.type.equals(that.type);
            }
            return false;
        }

        /**
         * Convenience method to see whether this type represents a virtual model type.
         * 
         * @return true if this type represents a virtual model, or false otherwise
         * @since 4.3
         */
        public boolean isVirtual() {
            return this == ModelType.VIRTUAL;
        }

        /**
         * Convenience method to see whether this type represents a physical model type.
         * 
         * @return true if this type represents a physical model, or false otherwise
         * @since 4.3
         */
        public boolean isPhysical() {
            return this == ModelType.PHYSICAL;
        }
    }
}
