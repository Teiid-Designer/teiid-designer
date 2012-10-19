/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import java.util.Collection;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.ui.DqpUiConstants;

/**
 * @since 8.0
 */
public class TranslatorsFolder extends AbstractTeiidFolder<TeiidTranslator> {

    private static final String TRANSLATORS_FOLDER_NAME = DqpUiConstants.UTIL.getString(TranslatorsFolder.class.getSimpleName() + ".label"); //$NON-NLS-1$

    /**
     * Create new instance
     * 
     * @param parentNode
     * @param values
     */
    public TranslatorsFolder(TeiidServerContainerNode parentNode, Collection<TeiidTranslator> values) {
        super(parentNode, values);
    }

    @Override
    public String getName() {
        return TRANSLATORS_FOLDER_NAME;
    }

}
