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

package org.teiid.query.resolver.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.query.mapping.xml.MappingAttribute;
import org.teiid.query.mapping.xml.MappingBaseNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingElement;
import org.teiid.query.mapping.xml.MappingVisitor;
import org.teiid.query.mapping.xml.Navigator;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataID.Type;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.parser.TeiidNodeFactory;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.resolver.CommandResolver;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.GroupContext;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;
import org.teiid.query.sql.visitor.GroupCollectorVisitor;
import org.teiid.query.sql.visitor.ValueIteratorProviderCollectorVisitor;
import org.teiid.runtime.client.Messages;


/**
 */
public class XMLQueryResolver extends CommandResolver {
	
	/**
     * @param queryResolver
     */
    public XMLQueryResolver(QueryResolver queryResolver) {
        super(queryResolver);
    }

    private final class SubSelectVisitor extends MappingVisitor {
		private final List<ElementSymbol> selectElems;
		private final ResolvingNode root;
		private final String mc;
		private String source;

		private SubSelectVisitor(List<ElementSymbol> selectElems,
				ResolvingNode root, String mc) {
			this.selectElems = selectElems;
			this.root = root;
			this.mc = mc;
		}

		@Override
		public void visit(MappingBaseNode baseNode) {
			if (baseNode.getSource() != null && baseNode.getFullyQualifiedName().equalsIgnoreCase(mc)) {
				source = baseNode.getSource();
			}
		}

		@Override
		public void visit(MappingElement element) {
			visit((MappingBaseNode)element);
			String nis = element.getNameInSource();
			getMappingClassColumn(nis, element.getFullyQualifiedName());
		}

		private void getMappingClassColumn(String nis, String fqn) {
			if (nis == null || source == null) {
				return;
			}
			String name = nis.substring(0, nis.lastIndexOf('.'));
			if (source.equalsIgnoreCase(name)) {
				selectElems.add(root.find(fqn));
			}
		}

		@Override
		public void visit(MappingAttribute attribute) {
			getMappingClassColumn(attribute.getNameInSource(), attribute.getFullyQualifiedName());
		}
	}

	private final class ResolvingNode {
		ElementSymbol elementSymbol;
		TreeMap<String, ResolvingNode> children = new TreeMap<String, ResolvingNode>(String.CASE_INSENSITIVE_ORDER);
		
		public void add(String name, ElementSymbol symbol) {
			if (name == null) {
				this.elementSymbol = symbol;
				return;
			}
			int index = name.lastIndexOf('.');
			String childName = name;
			if (index >= 0) {
				childName = name.substring(0, index);
				name = name.substring(index + 1, name.length());
			} else {
				childName = null;
			}
			ResolvingNode child = children.get(name);
			if (child == null) {
				child = new ResolvingNode();
				children.put(name, child);
			}
			child.add(childName, symbol);
		}
		
		public <T extends Collection<ElementSymbol>> T values(T values) {
			if (elementSymbol != null) {
				values.add(elementSymbol);
			}
			for (ResolvingNode node : children.values()) {
				node.values(values);
			}
			return values;
		}
		
		public ElementSymbol find(String name) {
			int index = name.lastIndexOf('.');
			String part = name;
			if (index > 0) {
				part = name.substring(index + 1, name.length());
				name = name.substring(0, index);
			} else {
				name = null;
			}
			ResolvingNode r = children.get(part);
			if (r == null) {
				return null;
			}
			if (name == null) {
				return r.elementSymbol;
			}
			return r.find(name);
		}
		
		public void addAll(Collection<ElementSymbol> elems) {
			for (ElementSymbol es : elems) {
				this.add(es.getName(), es);
			}
		}
		
		public List<ElementSymbol> values() {
			return values(new LinkedList<ElementSymbol>());
		}
	}

	/**
     * @see org.teiid.query.resolver.CommandResolver#resolveCommand(org.teiid.query.sql.lang.Command, TempMetadataAdapter, boolean)
     */
	@Override
    public void resolveCommand(Command command, TempMetadataAdapter metadata, boolean resolveNullLiterals)
		throws Exception {
		resolveCommand((Query)command, null, metadata);
	}

	/**
	 * @param query
	 * @param docGroup
	 * @param metadata
	 * @throws Exception
	 */
	public void resolveCommand(Query query, GroupSymbol docGroup, TempMetadataAdapter metadata)
	throws Exception {
		// set isXML flag
		query.setIsXML(docGroup == null);

		// get the group on this query
		Collection<GroupSymbol> groups = GroupCollectorVisitor.getGroups(query, true);
		GroupSymbol group = groups.iterator().next();

		boolean subQuery = true;
		if (docGroup == null) {
			docGroup = group;
			subQuery = false;
		}
		
		if (subQuery && group.getDefinition() != null) {
			 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30129, group));
		}

		//external groups
        GroupContext externalGroups = query.getExternalGroupContexts();

		// valid elements for select
        List<ElementSymbol> validElems = ResolverUtil.resolveElementsInGroup(docGroup, metadata);
        final ResolvingNode root = new ResolvingNode();
        ResolvingNode selectRoot = root;
        if (subQuery) {
        	validElems = getElementsUnderNode(group.getMetadataID(), validElems, metadata);
        }
        root.addAll(validElems);
		if (subQuery) {
        	//the select can only be to the mapping class itself
        	MappingDocument doc = (MappingDocument) metadata.getMappingNode(docGroup.getMetadataID());
    		final String mc = group.getNonCorrelationName();
    		List<ElementSymbol> selectElems = new LinkedList<ElementSymbol>();
            doc.acceptVisitor(new Navigator(true, new SubSelectVisitor(selectElems, root, mc)));
			selectRoot = new ResolvingNode();
			selectRoot.addAll(selectElems);
        }
		
		resolveXMLSelect(subQuery, query, group, selectRoot, metadata);

		// valid elements for criteria and order by
		root.addAll(collectTempElements(group, metadata));

		Criteria crit = query.getCriteria();
		OrderBy orderBy = query.getOrderBy();
		
		if(crit != null) {
	        List<SubqueryContainer> commands = ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(crit);
	        if (!commands.isEmpty()) {
	        	TempMetadataAdapter tma = new TempMetadataAdapter(metadata, new TempMetadataStore());
	        	if (!subQuery) {
	        		addPseudoSubqueryGroups(tma, group, docGroup);
	        	}
		        for (SubqueryContainer<?> subCommand : commands) {
		            getQueryResolver().setChildMetadata(subCommand.getCommand(), query);
		            if (subCommand.getCommand() instanceof Query && getQueryResolver().isXMLQuery((Query)subCommand.getCommand(), tma)) {
		            	resolveCommand((Query)subCommand.getCommand(), docGroup, tma);
		            } else {
		            	getQueryResolver().resolveCommand(subCommand.getCommand(), metadata.getMetadata());
		            }
		        }
	        }

			resolveXMLCriteria(crit, externalGroups, root, metadata);
			// Resolve functions in current query
			ResolverVisitor visitor = new ResolverVisitor(crit.getTeiidVersion());
			visitor.resolveLanguageObject(crit, metadata);
		}

		// resolve any orderby specified on the query
		if(orderBy != null) {
			resolveXMLOrderBy(orderBy, externalGroups, root, metadata);
		}
        
        //we throw exceptions in these cases, since the clauses will not be resolved
        if (query.getGroupBy() != null) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30130));
        }
        
        if (query.getHaving() != null) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30131));
        }	
    }

	private void addPseudoSubqueryGroups(final TempMetadataAdapter metadata,
			GroupSymbol group, GroupSymbol docGroup)
			throws Exception {
		/*
		 * The next section of resolving logic adds in pseduo groups that can be used
		 * in subqueries
		 */
		MappingDocument doc = (MappingDocument) metadata.getMappingNode(docGroup.getMetadataID());
		
		final String prefix = group.getNonCorrelationName() + Symbol.SEPARATOR;

        doc.acceptVisitor(new Navigator(true, new MappingVisitor() {
        	@Override
        	public void visit(MappingBaseNode baseNode) {
        		if (baseNode.getSource() == null) {
        			return;
        		}
        		if (StringUtil.startsWithIgnoreCase(baseNode.getFullyQualifiedName(), prefix)) {
        			try {
        			    GroupSymbol gs = TeiidNodeFactory.createASTNode(getTeiidVersion(), ASTNodes.GROUP_SYMBOL);
        			    gs.setName(baseNode.getFullyQualifiedName());
						ResolverUtil.addTempGroup(metadata, gs, Collections.EMPTY_LIST, false).setMetadataType(Type.XML);
					} catch (Exception e) {
						 throw new RuntimeException(e);
					}
        		}
        	}
        }));
	}

    /**
     * Method resolveXMLSelect.
     * @param select Select clause in user command
     * @param group GroupSymbol
     * @param externalGroups Collection of external groups
     * @param validElements Collection of valid elements
     * @param metadata QueryMetadataInterface the metadata(for resolving criteria on temp groups)
     * @throws Exception if resolving order by fails
     * @throws Exception if resolving fails
     * @throws Exception if resolving fails
     */
	void resolveXMLSelect(boolean subquery, Query query, GroupSymbol group, ResolvingNode validElements, IQueryMetadataInterface metadata)
		throws Exception {
        
        GroupContext externalGroups = null;

		Select select = query.getSelect();
		// Allow SELECT DISTINCT, which is ignored.  It is meaningless except for
		// self-entity relation using relate() functionality

		List elements = select.getSymbols();
		for (int i = 0; i < elements.size(); i++) {
			Expression ss = (Expression) elements.get(i);

			if (ss instanceof ElementSymbol) {
				// Here we make an assumption that: all elements named with "xml" must use qualified name
				// rather than a simple "xml" in order to distinguish it from "SELECT xml" and
				// "SELECT model.document.xml" case, both of whom stand for selecting the whole document.

				// Then "SELECT xml" or "SELECT model.document.xml" can only stand for one meaning with two cases:
				// 1) whole document
				// 2) whole document, root name = "xml", too

				// There are other cases of "xml", such as, element name = "xml",
				// but those are ok because those will be resolved later as normal elements
				ElementSymbol es = (ElementSymbol)ss;
				String symbolName = es.getName();
				if(!subquery && (symbolName.equalsIgnoreCase("xml") || symbolName.equalsIgnoreCase(group.getName() + ".xml"))) { //$NON-NLS-1$ //$NON-NLS-2$
					if(elements.size() != 1) {
						 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30133));
					}
					select.clearSymbols();
                    MultipleElementSymbol all = TeiidNodeFactory.createASTNode(getTeiidVersion(), ASTNodes.MULTIPLE_ELEMENT_SYMBOL);
                    all.setElementSymbols(validElements.values());
					select.addSymbol(all);
					query.setSelect(select);
					return;
				}
                // normal elements
				resolveElement(es, validElements, externalGroups, metadata);
			} else if (ss instanceof MultipleElementSymbol) {
				// Resolve the element with "*" case. such as "A.*"
				// by stripping off the ".*" part,
                MultipleElementSymbol all =  (MultipleElementSymbol)ss;

                // Check for case where we have model.doc.*
                if(all.getGroup() == null || all.getGroup().getName().equalsIgnoreCase(group.getName())) {
                    all.setElementSymbols(validElements.values());
    				return;
                }
                // resovlve the node which is specified
                ElementSymbol elementSymbol = TeiidNodeFactory.createASTNode(getTeiidVersion(), ASTNodes.ELEMENT_SYMBOL); 
                elementSymbol.setName(all.getGroup().getName());
                resolveElement(elementSymbol, validElements, externalGroups, metadata);

                // now find all the elements under this node and set as elements.
                List<ElementSymbol> elementsInNode = getElementsUnderNode(elementSymbol.getMetadataID(), validElements.values(), metadata);
                all.setElementSymbols(elementsInNode);
			} else if (ss instanceof ExpressionSymbol) {
                 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30134));
            } else if (ss instanceof AliasSymbol) {
                 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30135));
            }
            
		}
	}
        
    /**
     * @param group
     * @param metadata
     * @return collection of temporary elements
     * @throws Exception
     */
    public Collection<ElementSymbol> collectTempElements(GroupSymbol group, IQueryMetadataInterface metadata)
        throws Exception {
    	ArrayList<ElementSymbol> validElements = new ArrayList<ElementSymbol>();
        // Create GroupSymbol for temp groups and add to groups
        Collection<?> tempGroups = metadata.getXMLTempGroups(group.getMetadataID());
        for (Object tempGroupID : tempGroups) {
            String name = metadata.getFullName(tempGroupID);
            GroupSymbol tempGroup = TeiidNodeFactory.createASTNode(getTeiidVersion(), ASTNodes.GROUP_SYMBOL);
            tempGroup.setName(name);
            tempGroup.setMetadataID(tempGroupID);

            validElements.addAll(ResolverUtil.resolveElementsInGroup(tempGroup, metadata));
        }
        return validElements;
    }

    /**
     * <p> Resolve the criteria specified on the XML query. The elements specified on the criteria should
     * be present on one of the mapping node objects passed to this method, or else be an element on a
     * temporary table at the root of the document model (if a temp table exists there).</p>
     * <p>A Exception will be thrown under the following circumstances:
     * <ol>
     * <li>the elements of the XML criteria cannot be resolved</li>
     * <li>the "@" attribute prefix is used to specify that the node is an attribute, but
     * a document node is found that is an element</li>
     * <li>an element is supplied in the criteria and is ambiguous (multiple
     * document nodes and/or root temp table elements exist which have that name)</li>
     * </ol></p>
     * <p>If an element is supplied in the criteria and is ambiguous (multiple document nodes and/or
     * root temp table elements of that name exist)
     * @param criteria The criteria object that should be resolved
     * @param externalGroups 
     * @param validElements
     * @param metadata QueryMetadataInterface the metadata(for resolving criteria on temp groups)
     * @throws Exception if any of the above fail conditions are met
     */
    public void resolveXMLCriteria(LanguageObject criteria,GroupContext externalGroups, ResolvingNode validElements, IQueryMetadataInterface metadata)
        throws Exception {

        // Walk through each element in criteria and check against valid elements
        Collection<ElementSymbol> critElems = ElementCollectorVisitor.getElements(criteria, false);
        for (ElementSymbol critElem : critElems) {
            if(! critElem.isExternalReference()) {
                resolveElement(critElem, validElements, externalGroups, metadata);
            }
        }
    }

    /**
     * Resolve OrderBy clause specified on the XML Query.
     * @param orderBy Order By clause in user command
     * @param group GroupSymbol
     * @param externalGroups Collection of external groups
     * @param validElements Collection of valid elements
     * @param metadata QueryMetadataInterface the metadata(for resolving criteria on temp groups)
     * @throws Exception if resolving order by fails
     * @throws Exception if resolving fails
     * @throws Exception if resolving fails
     */
    void resolveXMLOrderBy(OrderBy orderBy, GroupContext externalGroups, ResolvingNode validElements, IQueryMetadataInterface metadata)
        throws Exception {

        // Walk through each element in OrderBy clause and check against valid elements
        Collection<ElementSymbol> orderElems = ElementCollectorVisitor.getElements(orderBy, false);
        for (ElementSymbol orderElem : orderElems) {
            resolveElement(orderElem, validElements, externalGroups, metadata);
        }
    }

	/**
	 * Resolve Element method.
	 * @param elem
	 * @param validElements
	 * @param externalGroups
	 * @param metadata
	 * @throws Exception
	 * @throws Exception
	 * @throws Exception
	 */
    void resolveElement(ElementSymbol elem, ResolvingNode validElements, GroupContext externalGroups, IQueryMetadataInterface metadata)
        throws Exception {
        
        // Get exact matching name
        String partialName = elem.getName();
        String fullName = partialName;

        ResolvingNode current = validElements;
    	String part = partialName;
        for (int i = 0; partialName != null; i++) {
        	int index = partialName.lastIndexOf('.');
        	if (index < 0) {
        		part = partialName;
        		partialName = null;
        	} else {
        		part = partialName.substring(index + 1, partialName.length());
        		partialName = partialName.substring(0, index);
        	}
			current = current.children.get(part);
			if (current == null) {
				if (i == 0 && part.charAt(0) != '@') {
					//handle attribute case
					part = '@' + part;
					current = validElements.children.get(part);
					if (current != null) {
						continue;
					}
				}
				try {
	                ResolverVisitor visitor = new ResolverVisitor(elem.getTeiidVersion());
	                visitor.resolveLanguageObject(elem, Collections.EMPTY_LIST, externalGroups, metadata);
	                return;
	            } catch (Exception e) {
	                 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30136, fullName));
	            }
			}
		}

        List<ElementSymbol> partialMatches = current.values();

        if (partialMatches.size() != 1) {
        	// Found multiple matches
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30137, fullName));
        } 

        ElementSymbol exactMatch = partialMatches.get(0);
        String name = elem.getOutputName();
        // Resolve based on exact match
        elem.setShortName(exactMatch.getShortName());
        elem.setMetadataID(exactMatch.getMetadataID());
        elem.setType(exactMatch.getType());
        elem.setGroupSymbol(exactMatch.getGroupSymbol());
        if (metadata.useOutputName()) {
        	elem.setOutputName(name);
    	}
    }

    List<ElementSymbol> getElementsUnderNode(Object mid, Collection<ElementSymbol> validElements, IQueryMetadataInterface metadata) 
        throws Exception {
        
        List<ElementSymbol> elements = new ArrayList<ElementSymbol>();
        String nodeName = metadata.getFullName(mid);
        for (ElementSymbol validElement : validElements) {
            String qualifiedName = validElement.getName();
            if (StringUtil.startsWithIgnoreCase(qualifiedName, nodeName) && (qualifiedName.length() == nodeName.length() || qualifiedName.charAt(nodeName.length()) == '.')) {
                elements.add(validElement);
            }
        }
        return elements;
    }

}
