/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.xquery.saxon;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.Configuration;
import net.sf.saxon.expr.AxisExpression;
import net.sf.saxon.expr.ContextItemExpression;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.PathMap;
import net.sf.saxon.expr.PathMap.PathMapArc;
import net.sf.saxon.expr.PathMap.PathMapNode;
import net.sf.saxon.expr.PathMap.PathMapNodeSet;
import net.sf.saxon.expr.PathMap.PathMapRoot;
import net.sf.saxon.expr.RootExpression;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.AnyNodeTest;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.query.QueryResult;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.trace.ExpressionPresenter;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.TypeHierarchy;
import net.sf.saxon.value.SequenceType;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.SQLXMLImpl;
import org.teiid.core.types.XMLTranslator;
import org.teiid.core.types.XMLType;
import org.teiid.core.types.XMLType.Type;
import org.teiid.query.function.source.XMLSystemFunctions;
import org.teiid.query.sql.lang.NamespaceItem;
import org.teiid.query.sql.lang.XMLColumn;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.XMLNamespaces;
import org.teiid.runtime.client.Messages;

@SuppressWarnings("serial")
public class SaxonXQueryExpression {
	
	private static final String XQUERY_PLANNING = "XQuery Planning"; //$NON-NLS-1$
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	static final String DEFAULT_PREFIX = "-"; //$NON-NLS-1$

	public static final Properties DEFAULT_OUTPUT_PROPERTIES = new Properties();
	{
		DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
	    //props.setProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		DEFAULT_OUTPUT_PROPERTIES.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
	}
	
	public interface RowProcessor {
		
		void processRow(NodeInfo row);

	}
	
	public static class Result {
		public SequenceIterator iter;
		public List<Source> sources = new LinkedList<Source>();

		/**
		 * Taken from WSConnection.Util
		 *
		 * @param source
		 */
		private void closeSource(final Source source) {
            if (!(source instanceof StreamSource)) {
                return;
            }
            
            StreamSource stream = (StreamSource)source;
            try {
                if (stream.getInputStream() != null) {
                    stream.getInputStream().close();
                }
            } catch (IOException e) {
            }
            try {
                if (stream.getReader() != null) {
                    stream.getReader().close();
                }
            } catch (IOException e) {
            }
        }

		public void close() {
			for (Source source : sources) {
				closeSource(source);
				if (source instanceof StAXSource) {
					StAXSource ss = (StAXSource)source;
					if (ss.getXMLEventReader() != null) {
						try {
							ss.getXMLEventReader().close();
						} catch (XMLStreamException e) {
						}
					} else {
						try {
							ss.getXMLStreamReader().close();
						} catch (XMLStreamException e) {
						}
					}
				}
			}
			if (iter != null) {
				iter.close();
			}
			sources.clear();
			iter = null;
		}
	}
	
	private static final Expression DUMMY_EXPRESSION = new Expression() {
		@Override
		public ItemType getItemType(TypeHierarchy th) {
			return null;
		}

		@Override
		public void explain(ExpressionPresenter out) {
		}

		@Override
		public Expression copy() {
			return null;
		}

		@Override
		protected int computeCardinality() {
			return 0;
		}

		@Override
		public PathMapNodeSet addToPathMap(PathMap pathMap,
				PathMapNodeSet pathMapNodeSet) {
			return pathMapNodeSet;
		}
	};

	// Create a default error listener to use when compiling - this prevents 
    // errors from being printed to System.err.
    private static final ErrorListener ERROR_LISTENER = new ErrorListener() {
        public void warning(TransformerException arg0) throws TransformerException {
        }
        public void error(TransformerException arg0) throws TransformerException {
        }
        public void fatalError(TransformerException arg0) throws TransformerException {
        }       
    };

	XQueryExpression xQuery;
	String xQueryString;
	Map<String, String> namespaceMap = new HashMap<String, String>();
	Configuration config = new Configuration();
	PathMapRoot contextRoot;
	String streamingPath;

    public SaxonXQueryExpression(String xQueryString, XMLNamespaces namespaces, List<DerivedColumn> passing, List<XMLColumn> columns) 
    throws QueryResolverException {
        config.setErrorListener(ERROR_LISTENER);
        this.xQueryString = xQueryString;
        StaticQueryContext context = config.newStaticQueryContext();
        IndependentContext ic = new IndependentContext(config);
        namespaceMap.put(EMPTY_STRING, EMPTY_STRING);
        if (namespaces != null) {
        	for (NamespaceItem item : namespaces.getNamespaceItems()) {
        		if (item.getPrefix() == null) {
        			if (item.getUri() == null) {
        				context.setDefaultElementNamespace(EMPTY_STRING); 
        				ic.setDefaultElementNamespace(EMPTY_STRING);
        			} else {
        				context.setDefaultElementNamespace(item.getUri());
        				ic.setDefaultElementNamespace(item.getUri());
        				namespaceMap.put(EMPTY_STRING, item.getUri());
        			}
        		} else {
    				context.declareNamespace(item.getPrefix(), item.getUri());
    				ic.declareNamespace(item.getPrefix(), item.getUri());
    				namespaceMap.put(item.getPrefix(), item.getUri());
        		}
			}
        }
        namespaceMap.put(DEFAULT_PREFIX, namespaceMap.get(EMPTY_STRING));
        for (DerivedColumn derivedColumn : passing) {
        	if (derivedColumn.getAlias() == null) {
        		continue;
        	}
        	try {
				context.declareGlobalVariable(StructuredQName.fromClarkName(derivedColumn.getAlias()), SequenceType.ANY_SEQUENCE, null, true);
			} catch (XPathException e) {
				//this is always expected to work
				 throw new RuntimeException(Messages.gs(Messages.TEIID.TEIID30153), e);
			}
		}
        
    	processColumns(columns, ic);	    	
    
        try {
			this.xQuery = context.compileQuery(xQueryString);
		} catch (XPathException e) {
		    throw new QueryResolverException(e, Messages.gs(Messages.TEIID.TEIID30154));
		}
    }
    
    private SaxonXQueryExpression() {
    	
    }
    
    public SaxonXQueryExpression clone() {
    	SaxonXQueryExpression clone = new SaxonXQueryExpression();
    	clone.xQuery = xQuery;
    	clone.xQueryString = xQueryString;
    	clone.config = config;
    	clone.contextRoot = contextRoot;
    	clone.namespaceMap = namespaceMap;
    	clone.streamingPath = streamingPath;
    	return clone;
    }
    
    public boolean usesContextItem() {
    	return this.xQuery.usesContextItem();
    }

	public void useDocumentProjection(List<XMLColumn> columns) {
	    try {
            streamingPath = StreamingUtils.getStreamingPath(xQueryString, namespaceMap);
        } catch (IllegalArgumentException e) {
            // Ignored
        }

		this.contextRoot = null;
		//we'll use a new pathmap, since we don't want to modify the one associated with the xquery.
		PathMap map = null;
		if (columns == null) {
			map = this.xQuery.getPathMap();
		} else {
			map = new PathMap(this.xQuery.getExpression());
		}
		PathMapRoot parentRoot;
		parentRoot = map.getContextRoot();

		if (parentRoot == null) {
			//TODO: this seems like we could omit the context item altogether
			//this.xQuery.usesContextItem() should also be false
			return;			
		}
		HashSet<PathMapNode> finalNodes = new HashSet<PathMapNode>();
		getReturnableNodes(parentRoot, finalNodes);
				
		if (!finalNodes.isEmpty()) {  
			if (columns != null && !columns.isEmpty()) {
				if (finalNodes.size() != 1) {
					return;	
				} 
				parentRoot = projectColumns(parentRoot, columns, finalNodes.iterator().next());
				if (parentRoot == null) {
					return;
				}
			} else {
				for (Iterator<PathMapNode> iter = finalNodes.iterator(); iter.hasNext(); ) {
	                PathMapNode subNode = iter.next();
	                subNode.createArc(new AxisExpression(Axis.DESCENDANT_OR_SELF, AnyNodeTest.getInstance()));
	            }
			}
		} 
		if (parentRoot.hasUnknownDependencies()) {
	    	return;
		}
		this.contextRoot = parentRoot;
	}
	
    public static final boolean[] isValidAncestorAxis =
    {
        false,          // ANCESTOR
        false,          // ANCESTOR_OR_SELF;
        true,           // ATTRIBUTE;
        false,           // CHILD;
        false,           // DESCENDANT;
        false,           // DESCENDANT_OR_SELF;
        false,          // FOLLOWING;
        false,          // FOLLOWING_SIBLING;
        true,           // NAMESPACE;
        true,          // PARENT;
        false,          // PRECEDING;
        false,          // PRECEDING_SIBLING;
        true,           // SELF;
        false,          // PRECEDING_OR_ANCESTOR;
    };

	private PathMapRoot projectColumns(PathMapRoot parentRoot, List<XMLColumn> columns, PathMapNode finalNode) {
		for (XMLColumn xmlColumn : columns) {
			if (xmlColumn.isOrdinal()) {
				continue;
			}
	    	Expression internalExpression = xmlColumn.getPathExpression().getInternalExpression();
	    	PathMap subMap = new PathMap(internalExpression);
	    	PathMapRoot subContextRoot = null;
	    	for (PathMapRoot root : subMap.getPathMapRoots()) {
				if (root.getRootExpression() instanceof ContextItemExpression || root.getRootExpression() instanceof RootExpression) {
					if (subContextRoot != null) {
						return null;
					}
					subContextRoot = root;
				}
			}
	    	if (subContextRoot == null) {
	    		//special case for handling '.', which the pathmap logic doesn't consider as a root
	    		if (internalExpression instanceof ContextItemExpression) {
	    			addReturnedArcs(xmlColumn, finalNode);
	    		}
	    		continue;
	    	}
	    	for (PathMapArc arc : subContextRoot.getArcs()) {
	    		if (streamingPath != null && !validateColumnForStreaming(xmlColumn, arc)) {
	    			streamingPath = null;
	    		}
				finalNode.createArc(arc.getStep(), arc.getTarget());
			}
	    	HashSet<PathMapNode> subFinalNodes = new HashSet<PathMapNode>();
			getReturnableNodes(subContextRoot, subFinalNodes);
	    	for (PathMapNode subNode : subFinalNodes) {
		    	addReturnedArcs(xmlColumn, subNode);
	        }
		}
		//Workaround to rerun the reduction algorithm - by making a copy of the old version
		PathMap newMap = new PathMap(DUMMY_EXPRESSION);
		PathMapRoot newRoot = newMap.makeNewRoot(parentRoot.getRootExpression());
		if (parentRoot.isAtomized()) {
			newRoot.setAtomized();
		}
		if (parentRoot.isReturnable()) {
			newRoot.setReturnable(true);
		}
		if (parentRoot.hasUnknownDependencies()) {
			newRoot.setHasUnknownDependencies();
		}
		for (PathMapArc arc : parentRoot.getArcs()) {
			newRoot.createArc(arc.getStep(), arc.getTarget());
		}
		return newMap.reduceToDownwardsAxes(newRoot);
	}

	private boolean validateColumnForStreaming(XMLColumn xmlColumn, PathMapArc arc) {
		boolean ancestor = false;
		LinkedList<PathMapArc> arcStack = new LinkedList<PathMapArc>();
		arcStack.add(arc);
		while (!arcStack.isEmpty()) {
			PathMapArc current = arcStack.removeFirst();
			byte axis = current.getStep().getAxis();
			if (ancestor) {
				if (current.getTarget().isReturnable()) {
					if (axis != Axis.NAMESPACE && axis != Axis.ATTRIBUTE) {
						return false;
					}
				}
				if (!isValidAncestorAxis[axis]) {
					return false;
				}
			} else if (!Axis.isSubtreeAxis[axis]) {
				if (axis == Axis.PARENT 
						|| axis == Axis.ANCESTOR
						|| axis == Axis.ANCESTOR_OR_SELF) {
					if (current.getTarget().isReturnable()) {
						return false;
					}
					ancestor = true; 
				} else {
					return false;
				}
			}
	    	for (PathMapArc pathMapArc : current.getTarget().getArcs()) {
	    		arcStack.add(pathMapArc);
			}
		}
		return true;
	}

	private void addReturnedArcs(XMLColumn xmlColumn, PathMapNode subNode) {
		if (xmlColumn.getSymbol().getType() == DataTypeManagerService.DefaultDataTypes.XML.getTypeClass()) {
			subNode.createArc(new AxisExpression(Axis.DESCENDANT_OR_SELF, AnyNodeTest.getInstance()));
		} else {
			//this may not always be needed, but it doesn't harm anything
			subNode.createArc(new AxisExpression(Axis.CHILD, NodeKindTest.TEXT));
			subNode.setAtomized();
		}
	}

	private void getReturnableNodes(PathMapNode node, HashSet<PathMapNode> finalNodes) {
		if (node.isReturnable()) {
			finalNodes.add(node);
		}
		for (PathMapArc arc : node.getArcs()) {
			getReturnableNodes(arc.getTarget(), finalNodes);
		}
	}

	private void processColumns(List<XMLColumn> columns, IndependentContext ic)
			throws QueryResolverException {
		if (columns == null) {
			return;
		}
        XPathEvaluator eval = new XPathEvaluator(config);
    	eval.setStaticContext(ic);
		for (XMLColumn xmlColumn : columns) {
        	if (xmlColumn.isOrdinal()) {
        		continue;
        	}
        	String path = xmlColumn.getPath();
        	if (path == null) {
        		path = xmlColumn.getName();
        	}
        	path = path.trim();
        	if (path.startsWith("/")) { //$NON-NLS-1$ 
        		if (path.startsWith("//")) { //$NON-NLS-1$
        			path = '.' + path;
        		} else {
        			path = path.substring(1);
        		}
        	}
	    	XPathExpression exp;
			try {
				exp = eval.createExpression(path);
			} catch (XPathException e) {
				 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30155, xmlColumn.getName(), xmlColumn.getPath()));
			}	
	    	xmlColumn.setPathExpression(exp);
		}
	}

	public XMLType createXMLType(final SequenceIterator iter, boolean emptyOnEmpty) throws Exception {
        Item item = iter.next();
        if (item == null && !emptyOnEmpty) {
            return null;
        }
        XMLType.Type type = Type.CONTENT;
        if (item instanceof NodeInfo) {
            NodeInfo info = (NodeInfo)item;
            type = getType(info);
        }
        Item next = iter.next();
        if (next != null) {
            type = Type.CONTENT;
        }
        SQLXMLImpl xml = XMLSystemFunctions.saveToBufferManager(new XMLTranslator() {
            
            @Override
            public void translate(Writer writer) throws TransformerException,
                    IOException {
                QueryResult.serializeSequence(iter.getAnother(), config, writer, DEFAULT_OUTPUT_PROPERTIES);
            }
        });
        XMLType value = new XMLType(xml);
        value.setType(type);
        return value;
    }

	public static XMLType.Type getType(NodeInfo info) {
        switch (info.getNodeKind()) {
            case net.sf.saxon.type.Type.DOCUMENT:
                return Type.DOCUMENT;
            case net.sf.saxon.type.Type.ELEMENT:
                return Type.ELEMENT;
            case net.sf.saxon.type.Type.TEXT:
                return Type.TEXT;
            case net.sf.saxon.type.Type.COMMENT:
                return Type.COMMENT;
            case net.sf.saxon.type.Type.PROCESSING_INSTRUCTION:
                return Type.PI;
        }
        return Type.CONTENT;
    }

    public Configuration getConfig() {
		return config;
	}

	public static void showArcs(StringBuilder sb, PathMapNode node, int level) {
		for (PathMapArc pathMapArc : node.getArcs()) {
			char[] pad = new char[level*2];
			Arrays.fill(pad, ' ');
			sb.append(new String(pad));
			sb.append(pathMapArc.getStep());
			sb.append('\n');
			node = pathMapArc.getTarget();
			showArcs(sb, node, level + 1);
		}
	}
	
	public boolean isStreaming() {
		return streamingPath != null;
	}
//
//	private static class Name11Checker {
//
//	    public static final Name11Checker theInstance = new Name11Checker();
//
//	    /**
//	     * Get the singular instance of this class
//	     * @return the singular instance of this class
//	     */
//
//	    public static Name11Checker getInstance() {
//	        return theInstance;
//	    }
//
//	    /**
//	     * Test whether a character can appear at the start of an NCName
//	     *
//	     * @param ch the character to be tested
//	     * @return true if this is a valid character at the start of an NCName the selected version of XML
//	     */
//
//	    public boolean isNCNameStartChar(int ch) {
//	        return XMLCharacterData.isNCNameStart11(ch);
//	    }
//
//	    /**
//	     * Validate whether a given string constitutes a valid NCName, as defined in XML Namespaces.
//	     *
//	     * @param ncName the name to be tested
//	     * @return true if the name is a lexically-valid QName
//	     */
//	    public final boolean isValidNCName(CharSequence ncName) {
//	        if (ncName.length() == 0) {
//	            return false;
//	        }
//	        char ch = ncName.charAt(0);
//	        if (!isNCNameStartChar(ch)) {
//	            return false;
//	        }
//	        for (int i = 1; i < ncName.length(); i++) {
//	            ch = ncName.charAt(i);
//	            if (!isNCNameChar(ch)) {
//	                return false;
//	            }
//	        }
//	        return true;
//	    }
//	}
}
