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
public interface IStatusRowProvider {
    int getId();

    String getText( IStatus status );

    String getTextTooltip( IStatus status );

    Image getImage( IStatus status );

    String getImageTooltip( IStatus status );

    String getLinkTooltip( IStatus status );

    Image getLinkImage( IStatus status );
}
