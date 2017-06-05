/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

/* Copied from org.eclipse.ui.views.* packages
 * 
 * The Properties View source was restrictive to use in a dockable View's page object
 * 
 * PropertySheetViewer was tightly connected to our extended ModelObjectPropertySheetPage and it's
 * functionality met our needs to embed this viewer in an Editor page.
 * 
 * So copied over this class and minimum number of associated classes to utilize this viewer in our
 * editor.
 * 
 *  see:  org.eclipse.ui.views.properties.PropertySheetCategory.java
 */
package org.teiid.designer.transformation.ui.editors.summary.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertySheetEntry;

/**
 * A category in a PropertySheet used to group <code>IPropertySheetEntry</code>
 * entries so they are displayed together.
 */
public class PropertySheetCategory {
    private String categoryName;

    private List entries = new ArrayList();

    private boolean shouldAutoExpand = true;

    /**
     * Create a PropertySheet category with name.
     * @param name
     */
    public PropertySheetCategory(String name) {
        categoryName = name;
    }

    /**
     * Add an <code>IPropertySheetEntry</code> to the list
     * of entries in this category.
     * @param entry
     */
    public void addEntry(IPropertySheetEntry entry) {
        entries.add(entry);
    }

    /**
     * Return the category name.
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Returns <code>true</code> if this category should be automatically
     * expanded. The default value is <code>true</code>.
     *
     * @return <code>true</code> if this category should be automatically
     * expanded, <code>false</code> otherwise
     */
    public boolean getAutoExpand() {
        return shouldAutoExpand;
    }

    /**
     * Sets if this category should be automatically
     * expanded.
     * @param autoExpand
     */
    public void setAutoExpand(boolean autoExpand) {
        shouldAutoExpand = autoExpand;
    }

    /**
     * Returns the entries in this category.
     *
     * @return the entries in this category
     */
    public IPropertySheetEntry[] getChildEntries() {
        return (IPropertySheetEntry[]) entries
                .toArray(new IPropertySheetEntry[entries.size()]);
    }

    /**
     * Removes all of the entries in this category.
     * Doing so allows us to reuse this category entry.
     */
    public void removeAllEntries() {
        entries = new ArrayList();
    }
}
