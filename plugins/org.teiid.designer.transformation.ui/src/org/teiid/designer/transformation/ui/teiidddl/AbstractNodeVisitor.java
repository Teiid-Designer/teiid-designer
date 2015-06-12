///*
// * JBoss, Home of Professional Open Source.
//*
//* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
//*
//* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
//*/
///*
// * JBoss, Home of Professional Open Source.
// * See the COPYRIGHT.txt file distributed with this work for information
// * regarding copyright ownership.  Some portions may be licensed
// * to Red Hat, Inc. under one or more contributor license agreements.
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * version 2.1 of the License, or (at your option) any later version.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
// * 02110-1301 USA.
// */
//package org.teiid.designer.transformation.ui.teiidddl;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import org.teiid.core.designer.util.StringConstants;
//import org.teiid.designer.core.ModelerCore;
//import org.teiid.designer.extension.convertor.mxd.PropertyType;
//import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
//import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
//import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
//import org.teiid.designer.type.IDataTypeManagerService;
//import org.w3c.dom.traversal.NodeIterator;
//
///**
// *
// */
//public abstract class AbstractNodeVisitor {
//
//    private ITeiidServerVersion version;
//
//    private IDataTypeManagerService dataTypeManager;
//
//    /**
//     * @param version teiid version
//     */
//    public AbstractNodeVisitor(ITeiidServerVersion version) {
//        if (version == null)
//            this.version = TeiidServerVersion.Version.TEIID_DEFAULT.get();
//        else
//            this.version = version;
//    }
//
//    protected boolean isITeiidServerVersionOrGreater(Version teiidVersion) {
//        ITeiidServerVersion minVersion = getVersion().getMinimumVersion();
//        return minVersion.equals(teiidVersion.get()) || minVersion.isGreaterThan(teiidVersion.get());
//    }
//
//    protected boolean isLessThanITeiidServerVersion(Version teiidVersion) {
//        ITeiidServerVersion maxVersion = getVersion().getMaximumVersion();
//        return maxVersion.isLessThan(teiidVersion.get());
//    }
//
//    protected boolean isTeiid87OrGreater() {
//        return isITeiidServerVersionOrGreater(Version.TEIID_8_7);
//    }
//
//    /**
//     * @return teiid version
//     */
//    public ITeiidServerVersion getVersion() {
//        return version;
//    }
//
//    /**
//     * @return data type manager service
//     */
//    public IDataTypeManagerService getDataTypeManager() {
//        if (dataTypeManager == null) {
//            dataTypeManager = ModelerCore.getTeiidDataTypeManagerService();
//        }
//
//        return dataTypeManager;
//    }
//
//    protected abstract String undefined();
//
//    protected NodeType findMixinTypeByNamespace(Node node, String nspacePrefix) throws RepositoryException {
//        NodeType[] mixinTypes = node.getMixinNodeTypes();
//        if (mixinTypes.length == 0)
//            return null;
//
//        if (nspacePrefix == null)
//            return null;
//
//        if (! nspacePrefix.endsWith(StringConstants.COLON))
//            nspacePrefix = nspacePrefix + StringConstants.COLON;
//
//        for (NodeType mixinType : mixinTypes) {
//
//            if (mixinType.getName().startsWith(nspacePrefix))
//                return mixinType;
//        }
//
//        return null;
//    }
//
//    protected NodeType findMixinTypeById(Node node, String mixinTypeId) throws RepositoryException {
//        NodeType[] mixinTypes = node.getMixinNodeTypes();
//        if (mixinTypes.length == 0)
//            return null;
//
//        if (mixinTypeId == null)
//            return null;
//
//        for (NodeType mixinType : mixinTypes) {
//            if (mixinType.getName().equals(mixinTypeId))
//                return mixinType;
//        }
//
//        return null;
//    }
//
//    protected boolean hasMixinType(Node node, String mixinTypeId) throws RepositoryException {
//        if (node == null || mixinTypeId == null)
//            return false;
//
//        String[] components = mixinTypeId.split(StringConstants.COLON);
//        if (components == null)
//            return false;
//
//        NodeType mixinType = findMixinTypeById(node, mixinTypeId);
//        return mixinType != null ? mixinType.getName().equals(mixinTypeId) : false;
//    }
//
//    protected void visitChild(Node node, String relNodePath) throws PathNotFoundException, RepositoryException {
//        if (node.hasNode(relNodePath)) {
//            Node child = node.getNode(relNodePath);
//            child.accept(this);
//        }
//    }
//
//    protected Collection<Node> getChildren(Node node) throws RepositoryException {
//        List<Node> children = new ArrayList<Node>();
//        NodeIterator nodeIterator = node.getNodes();
//
//        while (nodeIterator.hasNext()) {
//            Node child = nodeIterator.nextNode();
//            children.add(child);
//        }
//
//        return children;
//    }
//
//    protected Collection<Node> getChildren(Node node, String mixinTypeId) throws RepositoryException {
//        if (node == null)
//            return Collections.emptyList();
//
//        List<Node> children = new ArrayList<Node>();
//        NodeIterator nodeIterator = node.getNodes();
//
//        while (nodeIterator.hasNext()) {
//            Node child = nodeIterator.nextNode();
//
//            if (! hasMixinType(child, mixinTypeId))
//                continue;
//
//            children.add(child);
//        }
//
//        return children;
//    }
//
//    protected void visitFilteredChildren(Node node, String nodeTypeName) throws PathNotFoundException, RepositoryException {
//        NodeIterator nodeIterator = node.getNodes();
//        while (nodeIterator.hasNext()) {
//            Node child = nodeIterator.nextNode();
//            NodeType nodeType = child.getPrimaryNodeType();
//            if (nodeTypeName.equals(nodeType.getName()))
//                child.accept(this);
//        }
//    }
//
//    protected void visitChildren(Node node) throws RepositoryException {
//        NodeIterator nodeIterator = node.getNodes();
//        while (nodeIterator.hasNext()) {
//            Node child = nodeIterator.nextNode();
//            child.accept(this);
//        }
//    }
//
//    protected Property property(Node node, String propName) throws RepositoryException {
//        if (node == null || propName == null)
//            return null;
//    
//        if (! node.hasProperty(propName))
//            return null;
//    
//        Property property = node.getProperty(propName);
//        return property;
//    }
//
//    protected List<Value> multiPropertyValues(Property refProp) throws RepositoryException {
//        List<Value> values = null;
//        if (! refProp.isMultiple())
//            values = Collections.singletonList(refProp.getValue());
//        else
//            values = Arrays.asList(refProp.getValues());
//        return values;
//    }
//
//    protected String toString(Property property) throws RepositoryException {
//        if (property == null)
//            return undefined();
//
//        Value value = property.isMultiple() ? property.getValues()[0] : property.getValue();
//        String valueString = null;
//    
//        switch (value.getType()) {
//            case PropertyType.STRING:
//                valueString = value.getString();
//                break;
//            case PropertyType.DATE:
//                valueString = value.getDate().toString();
//                break;
//            case PropertyType.BINARY:
//                valueString = value.getBinary().toString();
//                break;
//            case PropertyType.DOUBLE:
//                valueString = Double.toString(value.getDouble());
//                break;
//            case PropertyType.DECIMAL:
//                valueString = value.getDecimal().toString();
//                break;
//            case PropertyType.LONG:
//                valueString = Long.toString(value.getLong());
//                break;
//            case PropertyType.BOOLEAN:
//                valueString = Boolean.toString(value.getBoolean());
//                break;
//            case PropertyType.NAME:
//                valueString = value.getString();
//                break;
//            case PropertyType.PATH:
//                valueString = value.getString();
//                break;
//            case PropertyType.REFERENCE:
//                valueString = value.getString();
//                break;
//            case PropertyType.WEAKREFERENCE:
//                valueString = value.getString();
//                break;
//            case PropertyType.URI:
//                valueString = value.getString();
//                break;
//            default:
//                valueString = undefined();
//        }
//    
//        return valueString;
//    }
//
//}
//
