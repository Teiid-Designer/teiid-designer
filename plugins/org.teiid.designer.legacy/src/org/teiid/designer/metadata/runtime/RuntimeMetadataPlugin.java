/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime;

import java.util.ResourceBundle;
import org.teiid.core.designer.BundleUtil;

/**
 * CommonPlugin
 * <p>Used here in <code>metadata.runtime</code> to have access to the new
 * logging framework for <code>LogManager</code>.</p>
 *
 * @since 8.0
 */
public class RuntimeMetadataPlugin {

	/**
     * The plug-in identifier of this plugin
     * (value <code>"org.teiid.designer.metadata.runtime"</code>).
	 */
	public static final String PLUGIN_ID = RuntimeMetadataPlugin.class.getName();

	public static final BundleUtil Util = new BundleUtil(PLUGIN_ID,
	                                                         PLUGIN_ID + ".i18n", ResourceBundle.getBundle(PLUGIN_ID + ".i18n")); //$NON-NLS-1$ //$NON-NLS-2$
}
