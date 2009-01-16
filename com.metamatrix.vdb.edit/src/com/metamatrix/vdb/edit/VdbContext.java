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

package com.metamatrix.vdb.edit;

import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.internal.core.xml.vdb.VdbHeader;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * Provides a context useful for editing VDBs.
 */
public interface VdbContext extends IChangeNotifier {

    final String MANIFEST_MODEL_NAME = "MetaMatrix-VdbManifestModel.xmi"; //$NON-NLS-1$
    final String OPENED_EVENT_NAME   = "opened"; //$NON-NLS-1$
    final String CLOSED_EVENT_NAME   = "closed"; //$NON-NLS-1$
    final String CLOSING_EVENT_NAME  = "closing"; //$NON-NLS-1$
    
    /**
     * Return the TempDirectory instance used by the VdbContext
     * for extracting the contents of the VDB 
     * @return
     * @since 5.0
     */
    TempDirectory getTempDirectory();
    
    /**
     * Return the VDB file instance associated with the VdbContext
     * @return
     * @since 5.0
     */
    File getVdbFile();
    
    /**
     * Return the {@link com.metamatrix.vdb.edit.manifest.Severity} value associated
     * with this VDB
     * @return
     * @since 5.0
     */
    Severity getSeverity();
    
    /**
     * Return true if the VDB file is readonly. 
     * @return <code>true</code>if readonly; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean isReadOnly();
    
    /**
     * Return true if the VDB file is empty. 
     * @return <code>true</code>if empty; <code>false</code> otherwise.
     * @since 5.0
     */
    boolean isEmpty();
    
    /**
     * Peek into the contents of the VDB without opening the context.
     * @return the VdbHeader for this VDB file or null if the file does not exist or cannot be read
     * @since 5.0
     */
    VdbHeader peekAtContents() throws IOException;

    /**
     * Return whether the VDB context is open.
     * @return true if the VDB is open, false otherwise.
     */
    boolean isOpen();

    /**
     * Open the VDB context and extract its contents into a predefined working folder.
     * @param theMonitor the progress monitor; may be null
     */
    void open(IProgressMonitor theMonitor) throws IOException;

//    /**
//     * Open the VDB context and extract its contents into a predefined working folder.
//     * @param theMonitor the progress monitor; may be null
//     * @param notify if true, all registered will be notified of the open otherwise no
//     * notifications will be fired
//     */
//    void open(IProgressMonitor theMonitor, boolean notify) throws IOException;
    
    /**
     * Return the description associated with this VDB.  The VDB context does 
     * not have to be open to call this method.
     * @return the description; may be null
     */
    String getDescription();

    /**
     * Return the VirtualDatabase object, which is the root object of the manifest model.
     * Note: the VDB context must be open to call this method.
     * @return the VirtualDatabase; never null
     */
    VirtualDatabase getVirtualDatabase();

    /**
     * Return the ModelReference associated with the given archive path if
     * it exists otherwise null will be returned.
     * @param pathInArchive The path to the model in the vdb; may not be null
     * @return The ModelReference to the model in the vdb
     * @since 4.2
     */
    ModelReference getModelReference(String pathInArchive);

    /**
     * Return the ModelReference associated with the given temp directory file if
     * it exists otherwise null will be returned.
     * @param tempDirFile The reference to the model in the temp directory; may not be null
     * @return The ModelReference to the model or null if no ModelReference exists for this file
     * @since 4.2
     */
    ModelReference getModelReference(File tempDirFile);

    /**
     * Return the ModelReference associated with the given UUID if
     * it exists otherwise null will be returned.
     * @param pathInArchive The path to the model in the vdb; may not be null
     * @return The ModelReference to the model in the vdb
     * @since 4.2
     */
    ModelReference getModelReference(ObjectID uuid);

    /**
     * Return the NonModelReference associated with the given archive path if
     * it exists otherwise null will be returned.
     * @param pathInArchive The path to the file in the vdb; may not be null
     * @return The NonModelReference to the non-model in the vdb
     * @since 4.2
     */
    NonModelReference getNonModelReference(String pathInArchive);

    /**
     * Return the NonModelReference associated with the given temp directory file if
     * it exists otherwise null will be returned.
     * @param tempDirFile The reference to the model in the temp directory; may not be null
     * @return The ModelReference to the non-model or null if no NonModelReference exists for this file
     * @since 4.2
     */
    NonModelReference getNonModelReference(File tempDirFile);
    
    /**
     * Returns an input stream for reading the contents of the specified ModelReference
     * @param modelRef; may not be null
     * @return the input stream for reading the contents of the specified ModelReference
     * @throws IOException if an I/O error has occurred
     * @since 5.0
     */
    InputStream getInputStream(ModelReference modelRef) throws IOException;
    
    /**
     * Returns an input stream for reading the contents of the specified NonModelReference
     * @param nonModelRef; may not be null
     * @return the input stream for reading the contents of the specified NonModelReference
     * @throws IOException if an I/O error has occurred
     * @since 5.0
     */
    InputStream getInputStream(NonModelReference nonModelRef) throws IOException;

    /**
     * Close the VDB context.
     * @param theMonitor the progress monitor; may be null
     */
    void close(IProgressMonitor theMonitor) throws IOException;

//    /**
//     * Close the VDB context.
//     * @param theMonitor the progress monitor; may be null
//     * @param notify if true, all registered will be notified of the open otherwise no
//     * notifications will be fired
//     * @param vetoable if true, the close operation can be vetoed leaving the context open.
//     */
//    void close(IProgressMonitor theMonitor, boolean notify, boolean vetoable) throws IOException;
    
    /**
     * Dispose of the VDB context and clean up any associated state
     * @since 5.0
     */
    void dispose();
    
    /**
     * Adds the given listener to this notifier. Has no effect if an identical listener is already registered.
     * @param theListener the listener being registered
     */
    void addVetoableChangeListener(VetoableChangeListener theListener);

    /**
     * Removes the given listener from this notifier. Has no effect if the listener is not registered.
     * @param theListener the listener being unregistered
     */
    void removeVetoableChangeListener(VetoableChangeListener theListener);

    /** 
     * Return the execution options of this VDB
     * @since 5.0.2
     */
    Properties getExecutionProperties();
}
