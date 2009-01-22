/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
