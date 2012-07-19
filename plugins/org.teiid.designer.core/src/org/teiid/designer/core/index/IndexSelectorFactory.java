/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.index;

import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.teiid.designer.core.index.IndexSelector;


/**
 * IndexSelectorFactory
 *
 * @since 8.0
 */
public interface IndexSelectorFactory {

    public IndexSelector createIndexSelector( List modelWorkspaceItems ) throws CoreException;

}
