/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.explorer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * @since 4.0
 */
public final class FakeDecoration implements IDecoration {
    //============================================================================================================================
	// Variables

    public List pfxs, sfxs, overlays;

    //============================================================================================================================
	// Methods

    /**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.viewers.IDecoration#getDecorationContext()
	 */
	public IDecorationContext getDecorationContext() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IDecoration#addPrefix(java.lang.String)
	 * @since 4.0
	 */
	public void addPrefix(final String prefix) {
        if (this.pfxs == null) {
            this.pfxs = new ArrayList(1);
        }
        this.pfxs.add(prefix);
	}

	/**
	 * @see org.eclipse.jface.viewers.IDecoration#addSuffix(java.lang.String)
	 * @since 4.0
	 */
	public void addSuffix(final String suffix) {
        if (this.sfxs == null) {
            this.sfxs = new ArrayList(1);
        }
        this.sfxs.add(suffix);
	}

	/**
	 * @see org.eclipse.jface.viewers.IDecoration#addOverlay(org.eclipse.jface.resource.ImageDescriptor)
	 * @since 4.0
	 */
	public void addOverlay(final ImageDescriptor overlay) {
        if (this.overlays == null) {
            this.overlays = new ArrayList(1);
        }
        this.overlays.add(overlay);
	}

	public void addOverlay(ImageDescriptor overlay, int quadrant) {
		// TODO Auto-generated method stub

	}

	public void setBackgroundColor(Color color) {
		// TODO Auto-generated method stub

	}

	public void setFont(Font color) {
		// TODO Auto-generated method stub

	}

	public void setForegroundColor(Color color) {
		// TODO Auto-generated method stub

	}


}
