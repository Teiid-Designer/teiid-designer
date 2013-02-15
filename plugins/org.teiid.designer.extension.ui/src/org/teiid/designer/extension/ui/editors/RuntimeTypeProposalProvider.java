/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.custom.CCombo;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Type;

/**
 * Provides matching runtime types based on keystrokes.
 */
class RuntimeTypeProposalProvider extends CComboProposalProvider{

    /**
     * @param combo the combo whose proposals are being requested (cannot be <code>null</code>)
     */
    RuntimeTypeProposalProvider(final CCombo combo) {
        super(combo);
    }

    /**
     * @see org.teiid.designer.extension.ui.editors.CComboProposalProvider#getActivationChars()
     */
    @Override
    protected char[] getActivationChars() {
        return Type.getFirstChars();
    }

    /**
     * @see org.teiid.designer.extension.ui.editors.CComboProposalProvider#proposalsFor(java.lang.String)
     */
    @Override
    protected List<String> proposalsFor(final String pattern) {
        if ((pattern == null) || pattern.isEmpty()) {
            return Collections.emptyList();
        }

        final String lowerCasePattern = pattern.toLowerCase();
        final List<String> matches = new ArrayList<String>(6);

        for (final ModelExtensionPropertyDefinition.Type type : ModelExtensionPropertyDefinition.Type.values()) {
            if (type.getRuntimeType().toLowerCase().startsWith(lowerCasePattern)) {
                matches.add(type.getRuntimeType());
            }
        }

        return matches;
    }

}
