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

package com.metamatrix.core.xslt;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import com.metamatrix.core.MetaMatrixCoreException;

/**
 * Style
 */
public interface Style {
    
    public String getName();
    
    public String getDescription();

    public InputStream getInputStream() throws IOException, MetaMatrixCoreException;

    public StreamSource getStreamSource() throws IOException, MetaMatrixCoreException;
}
