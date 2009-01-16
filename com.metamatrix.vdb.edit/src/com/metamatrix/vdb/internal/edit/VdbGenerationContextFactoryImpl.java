/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.jdom.Document;
import com.metamatrix.vdb.edit.VdbGenerationContext;
import com.metamatrix.vdb.edit.VdbGenerationContextFactory;
import com.metamatrix.vdb.edit.VdbGenerationContextParameters;
import com.metamatrix.vdb.edit.VdbGenerationInterruptedException;

/**
 * @since 5.0
 */
public class VdbGenerationContextFactoryImpl implements VdbGenerationContextFactory {

    /**
     * @since 5.0
     */
    VdbGenerationContextFactoryImpl() {
        super();
    }

    /**
     * @see com.metamatrix.vdb.edit.prototype.VdbGenerationContextFactor#createVdbGenerationContext(com.metamatrix.vdb.edit.VdbGenerationContext.VdbGenerationContextParameters,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public VdbGenerationContext createVdbGenerationContext( final VdbGenerationContextParameters parameters,
                                                            final IProgressMonitor monitor ) {

        return new VdbGenerationContext() {
            VdbGenerationContext delegate = new VdbGenerationContextImpl(parameters);

            void checkCanceled() {
                if (monitor.isCanceled()) {
                    throw new VdbGenerationInterruptedException();
                }
            }

            public void addErrorMessage( String message,
                                         int code,
                                         Throwable t ) {
                checkCanceled();
                delegate.addErrorMessage(message, code, t);
            }

            public boolean addGeneratedArtifact( String pathInVdb,
                                                 Document xmlContent ) {
                checkCanceled();
                return delegate.addGeneratedArtifact(pathInVdb, xmlContent);
            }

            public boolean addGeneratedArtifact( String pathInVdb,
                                                 File content ) {
                checkCanceled();
                return delegate.addGeneratedArtifact(pathInVdb, content);
            }

            public boolean addGeneratedArtifact( String pathInVdb,
                                                 InputStream content ) {
                checkCanceled();
                return delegate.addGeneratedArtifact(pathInVdb, content);
            }

            public boolean addGeneratedArtifact( String pathInVdb,
                                                 String content ) {
                checkCanceled();
                return delegate.addGeneratedArtifact(pathInVdb, content);
            }

            public void addInfoMessage( String message,
                                        int code ) {
                checkCanceled();
                delegate.addInfoMessage(message, code);
            }

            public void addWarningMessage( String message,
                                           int code ) {
                checkCanceled();
                delegate.addWarningMessage(message, code);
            }

            public Map getGeneratedArtifactsByPath() {
                checkCanceled();
                return delegate.getGeneratedArtifactsByPath();
            }

            public ModelHelper getModelHelper() {
                checkCanceled();
                return new ModelHelper() {

                    private ModelHelper helperDelegate = delegate.getModelHelper();

                    public String getDescription( Resource model ) {
                        checkCanceled();
                        return helperDelegate.getDescription(model);
                    }

                    public ModelType getModelType( Resource model ) {
                        checkCanceled();
                        return helperDelegate.getModelType(model);
                    }

                    public String getName( Resource model ) {
                        checkCanceled();
                        return helperDelegate.getName(model);
                    }

                    public String getPath( Resource model ) {
                        checkCanceled();
                        return helperDelegate.getPath(model);
                    }

                    public String getPrimaryMetamodelUri( Resource model ) {
                        checkCanceled();
                        return helperDelegate.getPrimaryMetamodelUri(model);
                    }

                    public Properties getProperties( Resource model ) {
                        checkCanceled();
                        return helperDelegate.getProperties(model);
                    }

                    public String getTargetNamespaceUri( Resource model ) {
                        checkCanceled();
                        return helperDelegate.getTargetNamespaceUri(model);
                    }

                    public String getUuid( Resource model ) {
                        checkCanceled();
                        return helperDelegate.getUuid(model);
                    }

                    public boolean isVisible( Resource model ) {
                        checkCanceled();
                        return helperDelegate.isVisible(model);
                    }
                };
            }

            public Resource[] getModels() {
                checkCanceled();
                return delegate.getModels();
            }

            public Resource[] getModels( String primaryMetamodelUri ) {
                checkCanceled();
                return delegate.getModels(primaryMetamodelUri);
            }

            public ModelObjectHelper getObjectHelper() {
                checkCanceled();
                return new ModelObjectHelper() {

                    private ModelObjectHelper helperDelegate = delegate.getObjectHelper();

                    public String getDescription( EObject objectInModel ) {
                        checkCanceled();
                        return helperDelegate.getDescription(objectInModel);
                    }

                    public Properties getProperties( EObject objectInModel ) {
                        checkCanceled();
                        return helperDelegate.getProperties(objectInModel);
                    }

                    public String getUuid( EObject objectInModel ) {
                        checkCanceled();
                        return helperDelegate.getUuid(objectInModel);
                    }

                    public boolean hasErrors( EObject objectInModel ) {
                        checkCanceled();
                        return helperDelegate.hasErrors(objectInModel);
                    }

                    public boolean hasWarnings( EObject objectInModel ) {
                        checkCanceled();
                        return helperDelegate.hasWarnings(objectInModel);
                    }
                };
            }

            public List getProblems() {
                checkCanceled();
                return delegate.getProblems();
            }

            public String getProgressMessage() {
                checkCanceled();
                return delegate.getProgressMessage();
            }

            public File getTemporaryDirectory() {
                checkCanceled();
                return delegate.getTemporaryDirectory();
            }

            public void setProgressMessage( String displayableMessage ) {
                checkCanceled();
                delegate.setProgressMessage(displayableMessage);
            }
        };
    }
}
