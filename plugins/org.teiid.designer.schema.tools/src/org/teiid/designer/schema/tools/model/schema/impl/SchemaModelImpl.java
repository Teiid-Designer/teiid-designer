/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.schema.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jdom.Namespace;
import org.teiid.designer.schema.tools.ToolsPlugin;
import org.teiid.designer.schema.tools.model.jdbc.Table;
import org.teiid.designer.schema.tools.model.jdbc.internal.TableImpl;
import org.teiid.designer.schema.tools.model.schema.ComplexSchemaObject;
import org.teiid.designer.schema.tools.model.schema.RootElement;
import org.teiid.designer.schema.tools.model.schema.SchemaModel;
import org.teiid.designer.schema.tools.model.schema.SchemaObject;
import org.teiid.designer.schema.tools.model.schema.SchemaObjectKey;
import org.teiid.designer.schema.tools.processing.SchemaProcessingException;

/**
 * @since 8.0
 */
public class SchemaModelImpl implements SchemaModel {

    private List elements;

    private Map namespaces;

    private Map tableRelationships; // key: Relationship, value Integer
    // relationship type

    // Root elements selected by the user, not our guess at root elements
    private Set rootElements;

    private HashMap tableImplementations = new HashMap();
    private HashMap<QName, SchemaObject> elementLookup = new HashMap();

    private boolean typeAware = false;

    private static String separator;

    public SchemaModelImpl( List elements,
                            Map namespaces,
                            String sep ) {
        this.elements = elements;
        for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
			SchemaObject object = (SchemaObject) iterator.next();
			QName qName = new QName(object.getNamespace(), object.getName());
			elementLookup.put(qName, object);
		}
        this.namespaces = namespaces;
        if (null == sep) {
            SchemaModelImpl.separator = "_"; //$NON-NLS-1$
        } else {
            SchemaModelImpl.separator = sep;
        }
    }

    @Override
	public List getPotentialRootElements() {
        ArrayList roots = new ArrayList();
        for (Iterator iter = elements.iterator(); iter.hasNext();) {
            SchemaObject element = (SchemaObject)iter.next();
            roots.add(element.getRootRepresentation());
        }
        return roots;
    }

    @Override
	public void setTypeAsRoot( String typeName,
                               String namespace ) throws SchemaProcessingException {
        if (!typeAware) {
            throw new SchemaProcessingException(ToolsPlugin.Util.getString("SchemaModelImpl.schemaNotTypeAware")); //$NON-NLS-1$
        }
        for (Iterator iter = elements.iterator(); iter.hasNext();) {
            Object next = iter.next();
            if (next instanceof ComplexSchemaObject) {
                ComplexSchemaObject type = (ComplexSchemaObject)next;
                if (type.getSimpleName().equals(typeName) && type.getNamespace().equals(namespace)) {
                    HashSet roots = new HashSet();
                    roots.add(type.getRootRepresentation());
                    try {
                        this.setSelectedRootElements(roots);
                    } catch (Throwable e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
        throw new SchemaProcessingException(ToolsPlugin.Util.getString("SchemaModelImpl.typeNotFound", //$NON-NLS-1$
                                                                       typeName,
                                                                       namespace));
    }

    @Override
	public SchemaModel copy() {
        SchemaModelCopyTraversalContext ctx = new SchemaModelCopyTraversalContext(elements, rootElements);
        List modelClone = ctx.getCopiedElements();
        Set rootsCopy = ctx.getCopiedRoots();
        SchemaModelImpl copy = new SchemaModelImpl(modelClone, namespaces, separator);
        copy.typeAware = this.typeAware;
        try {
            copy.setSelectedRootElements(rootsCopy);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return copy;
    }

    @Override
	public List getElements() {
        return elements;
    }

    @Override
	public Map getNamespaces() {
        return namespaces;
    }

    @Override
	public void setSelectedRootElements( Set roots ) {
        if (null == roots) {
            this.rootElements = null;
        } else {
            this.rootElements = new HashSet(roots.size());
            for (Iterator iter = roots.iterator(); iter.hasNext();) {
                SchemaObjectKey key;
                Object obj = iter.next();
                if (obj instanceof SchemaObjectKey) {
                    key = (SchemaObjectKey)obj;
                } else if (obj instanceof RootElement) {
                    RootElement root = (RootElement)obj;
                    key = root.getKey();
                } else {
                    throw new RuntimeException(ToolsPlugin.Util.getString("SchemaModelImpl.invalidType")); //$NON-NLS-1$
                }

                this.rootElements.add(key);
                for (Iterator eIter = elements.iterator(); eIter.hasNext();) {
                    SchemaObject element = (SchemaObject)eIter.next();
                    if (element.getKey().equals(key)) {
                        element.cascadeRootSelection(true);
                    }
                }
            }
        }
    }

    @Override
	public boolean isSelectedRootElement( SchemaObject element ) {
        boolean result;
        if (null == rootElements) {
            // Model everything if no selections were made.
            result = true;
        } else {
            result = rootElements.contains(element.getKey());
        }
        return result;
    }

    @Override
	public void setElements( List nonMergedTables ) {
        elements = nonMergedTables;
    }

    @Override
	public List getTables() {
        List tableNamespaces = getNamespacePrefixesValue();

        ArrayList result = new ArrayList();
        for (Iterator iter = elements.iterator(); iter.hasNext();) {
            SchemaObject element = (SchemaObject)iter.next();
            if (element.isWithinSelectedHierarchy()) {
                TableImpl table = new TableImpl(element.getSimpleName(), element.getCatalog(), element.getInputXPath(),
                                                element.getOutputXPath());
                for (Iterator nsIter = tableNamespaces.iterator(); nsIter.hasNext();) {
                    table.addNamespace((Namespace)nsIter.next());
                }

                table.setElement(element);
                table.setSchemaModel(this);
                tableImplementations.put(element.getSimpleName(), table);
                result.add(table);
            }
        }
        return result;
    }

    private List getNamespacePrefixesValue() {
        ArrayList result = new ArrayList(namespaces.size());
        for (Iterator iter = this.namespaces.keySet().iterator(); iter.hasNext();) {
            String prefix = (String)iter.next();
            String namespaceURI = (String)namespaces.get(prefix);
            result.add(Namespace.getNamespace(prefix, namespaceURI));
        }
        return result;
    }

    @Override
	public Map getNamespacePrefixes() {
        // reverse the m_namespaces map
        Map nsMap = getNamespaces();
        HashMap returnMap = new HashMap();
        Iterator nsIter = nsMap.keySet().iterator();
        while (nsIter.hasNext()) {
            String key = (String)nsIter.next();
            returnMap.put(nsMap.get(key), key);
        }
        return returnMap;
    }

    /*	public void printDebug() {
    		System.out.println("SchemaModel");

    		System.out.println("Namespaces");
    		System.out.println(namespaces);
    		System.out.println("End Namespaces");

    		System.out.println("Default Namespaces");
    		System.out.println(defaultNamespaces);
    		System.out.println("End Default Namespaces");


    		System.out.println("Potential Root Elements");
    		List roots = getPotentialRootElements();
    		for(Iterator iter = roots.iterator(); iter.hasNext(); ) {
    			RootElementImpl root = (RootElementImpl) iter.next();
    			root.printDebug();
    		}
    		System.out.println("End Potential Root Elements");


    		System.out.println("Number of Elements " + elements.size());
    		for(Iterator iter = elements.iterator(); iter.hasNext(); ) {
    			ElementImpl element = (ElementImpl) iter.next();
    			element.printDebug();
    		}
    		System.out.println("SchemaModel End");
    	}
    */
    @Override
	public Table findTable( String simpleName ) {
        return (Table)tableImplementations.get(simpleName);
    }

    public int getRelationToParent( String lookupKey ) {
        Integer result = new Integer(-1);
        if ((null != tableRelationships) && (tableRelationships.containsKey(lookupKey))) {
            result = (Integer)tableRelationships.get(lookupKey);
        }
        return result.intValue();
    }

    public void setTableRelationships( Map tableRelationships ) {
        this.tableRelationships = tableRelationships;
    }

    public void setTypeAware( boolean typeAware ) {
        this.typeAware = typeAware;
    }

    @Override
	public boolean isTypeAware() {
        return typeAware;
    }

    public static String getSeparator() {
        return separator;
    }

	@Override
	public SchemaObject getElement(QName qName) {
		return elementLookup.get(qName);
	}
}
