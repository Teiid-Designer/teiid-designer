/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.core.status;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public interface IStatusContentProvider {

    String getTitle();

    String getDescription();

    IStatus getDefaultStatus();

    IStatus getStatus( int id );

    Image getStatusImage( int id );

    String getLinkTooltip( int id );

    Image getImage( int id );

    String getText( int id );

    IStatusRowProvider[] getRowsProviders();

	String getId();
	
	void updateStatus(boolean forceUpdate);
	
	void shutdown();
	
	void startup();

}
