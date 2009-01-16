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

package com.metamatrix.modeler.core.notification.util;


/** 
 * This interface provides a mechanism for classes that deal with metadata transactions to tag-themselves
 * as being an ignorable source. Then a listener can check for this kind of source and ignore it if it wishes.
 * 
 * Initially implemented to handle Open/Close VDB processing in Dimension VdbView, VdbViewWorker, OpenVdbAction and
 * WebServiceValidationHelper
 * @since 5.0
 */
public interface IgnorableNotificationSource {

}
