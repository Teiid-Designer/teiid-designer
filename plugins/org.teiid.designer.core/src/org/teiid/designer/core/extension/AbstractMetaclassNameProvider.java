/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider;

/**
 * Base class for all metamodel extendable metaclass name providers.
 *
 * @since 8.0
 */
public abstract class AbstractMetaclassNameProvider implements ExtendableMetaclassNameProvider {

    private static final String MC_PREFIX = ".impl."; //$NON-NLS-1$
    private static final String MC_SUFFIX = "Impl"; //$NON-NLS-1$
    protected static final String[] NO_PARENTS = {};

    /**
     * @param metaclassName the name whose short name is being requested (cannot be <code>null</code> or empty)
     * @return the label (never <code>null</code> or empty)
     */
    public static final String getLabel(final String metaclassName) {
        CoreArgCheck.isNotEmpty(metaclassName);

        // try and extract the name between ".impl." and "Impl" from the metaclass name
        int index1 = metaclassName.indexOf(MC_PREFIX);

        if (index1 == -1) {
            index1 = metaclassName.lastIndexOf("."); //$NON-NLS-1$

            if (index1 != -1) {
                return metaclassName.substring(index1 + 1);
            }
        } else {
            int index2 = metaclassName.indexOf(MC_SUFFIX);

            if (index1 != -1) {
                return metaclassName.substring(index1 + MC_PREFIX.length(), index2);
            }
        }

        return metaclassName;
    }

    private final List<String> metaclassNames;
    private final String metamodelUri;
    private final Map<String, List<String>> parentChildMap;
    private final List<String> roots;

    protected AbstractMetaclassNameProvider(final String metamodelUri) {
        CoreArgCheck.isNotEmpty(metamodelUri);
        this.metaclassNames = new ArrayList<String>();
        this.metamodelUri = metamodelUri;
        this.parentChildMap = new HashMap<String, List<String>>();
        this.roots = new ArrayList<String>();
    }

    protected final void addMetaclass(final String metaclassName,
                                      final String... parents) {
        CoreArgCheck.isNotEmpty(metaclassName);

        if (!this.metaclassNames.contains(metaclassName)) {
            this.metaclassNames.add(metaclassName);

            if ((parents == null) || (parents.length == 0)) {
                this.roots.add(metaclassName);
            } else {
                for (final String parent : parents) {
                    List<String> kids = this.parentChildMap.get(parent);

                    if (kids == null) {
                        kids = new ArrayList<String>();
                        this.parentChildMap.put(parent, kids);
                    }

                    kids.add(metaclassName);
                }
            }
        }
    }

    /**
     * @see org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider#getExtendableMetaclassChildren(java.lang.String)
     */
    @Override
    public String[] getExtendableMetaclassChildren(final String parentMetaclassName) {
        CoreArgCheck.isNotEmpty(parentMetaclassName);
        final List<String> kidNames = this.parentChildMap.get(parentMetaclassName);

        if ((kidNames == null) || kidNames.isEmpty()) {
            return new String[0];
        }

        final String[] result = new String[kidNames.size()];
        int i = 0;

        for (final String kidName : kidNames) {
            result[i++] = kidName;
        }

        return result;
    }

    /**
     * @see org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider#getExtendableMetaclassRoots()
     */
    @Override
    public String[] getExtendableMetaclassRoots() {
        return this.roots.toArray(new String[this.roots.size()]);
    }

    /**
     * @see org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider#getLabelText(java.lang.String)
     */
    @Override
    public final String getLabelText(final String metaclassName) {
        CoreArgCheck.isNotEmpty(metaclassName);
        return getLabel(metaclassName);
    }

    /**
     * @see org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider#getMetamodelUri()
     */
    @Override
    public final String getMetamodelUri() {
        return this.metamodelUri;
    }

    /**
     * @see org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider#getParent(java.lang.String)
     */
    @Override
    public String getParent(String metaclassName) {
        CoreArgCheck.isNotEmpty(metaclassName);

        for (final Map.Entry<String, List<String>> entry : this.parentChildMap.entrySet()) {
            if (entry.getValue().contains(metaclassName)) {
                return entry.getKey();
            }
        }

        assert false;
        return null;
    }

    /**
     * @see org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider#hasChildren(java.lang.String)
     */
    @Override
    public boolean hasChildren(String metaclassName) {
        CoreArgCheck.isNotEmpty(metaclassName);
        return (getExtendableMetaclassChildren(metaclassName).length != 0);
    }

}
