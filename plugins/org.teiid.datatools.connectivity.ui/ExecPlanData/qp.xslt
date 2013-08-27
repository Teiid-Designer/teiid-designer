<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="no" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" 
          doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />

  <!-- Disable built-in recursive processing templates -->
  <xsl:template match="*|/|text()|@*" mode="ToolTipDescription" />
  <xsl:template match="*|/|text()|@*" mode="ToolTipDetails" />

  <!-- Default template -->  
  <xsl:template match="/">
    <html>
      <head>
        <title>Execution plan</title>
        <link rel="stylesheet" type="text/css" href="qp.css" />
        <script src="jquery.min.js" type="text/javascript"></script>
        <script src="qp.js" type="text/javascript"></script>
        <script type="text/javascript">$(document).ready( function() { QP.drawLines(); });</script>
      </head>
      <body>
        <div class="qp-root">
          <xsl:apply-templates select="node" />
        </div>
      </body>
    </html>
  </xsl:template>

  <!-- Matches a branch in the query plan (either an operation or a statement) -->
  <xsl:template match="node">
    <div class="qp-tr">
      <div>
        <div class="qp-node">
          <xsl:apply-templates select="." mode="NodeIcon" />
          <xsl:apply-templates select="." mode="NodeLabel" />
          <xsl:apply-templates select="." mode="NodeLabel2" />
          <xsl:apply-templates select="." mode="NodeCostLabel" />
          <xsl:call-template name="ToolTip" />
        </div>
      </div>
      <div><xsl:apply-templates select="*/node" /></div>
    </div>
  </xsl:template>

  <!-- Writes the tool tip -->
  <xsl:template name="ToolTip">
    <div class="qp-tt">
      <div class="qp-tt-header">
        <xsl:apply-templates select="." mode="NodeLabel" />
      </div>
      <xsl:call-template name="ToolTipGrid" />
    </div>
  </xsl:template>

  <!-- Renders a row in the tool tip details table. -->
  <xsl:template name="ToolTipRow">
    <xsl:param name="Label" />
    <xsl:param name="Value" />
    <xsl:param name="Condition" select="$Value" />
    <xsl:if test="$Condition">
      <tr>
        <th><xsl:value-of select="$Label" /></th>
        <td><xsl:value-of select="$Value" /></td>
      </tr>      
    </xsl:if>
  </xsl:template>

<!-- Writes the grid of node properties to the tool tip -->
  <xsl:template name="ToolTipGrid">
  
    <xsl:if test="property[@name='Output Columns']">
      <table>
        <caption>Output Columns</caption>
        <xsl:for-each select="property[@name='Output Columns']/value">
          <tr>
            <th><xsl:value-of select="substring-before(normalize-space(.), ' (')" /></th>
            <td><xsl:value-of select="replace(substring-after(normalize-space(.), ' ('), '\)', '')" /></td>
          </tr>
        </xsl:for-each>
      </table>
    </xsl:if>
    
    <xsl:if test="property[@name='Statistics']">
      <table>
        <caption>Statistics</caption>
        <xsl:for-each select="property[@name='Statistics']/value">
          <tr>
            <th><xsl:value-of select="substring-before(normalize-space(.), ':')" /></th>
            <td><xsl:value-of select="substring-after(normalize-space(.), ': ')" /></td>
          </tr>
        </xsl:for-each>
      </table>
    </xsl:if>

    <xsl:if test="property[@name='Cost Estimates']">
      <table>
        <caption>Cost Estimates</caption>
        <xsl:for-each select="property[@name='Cost Estimates']/value">
          <tr>
            <th><xsl:value-of select="substring-before(normalize-space(.), ':')" /></th>
            <td><xsl:value-of select="substring-after(normalize-space(.), ': ')" /></td>
          </tr>
        </xsl:for-each>
      </table>
    </xsl:if>

    <xsl:if test="property[@name='Model Name']">
      <table>
        <tr>
            <th><div class="qp-bold">Model Name</div></th>
            <td><xsl:value-of select="property[@name='Model Name']/value" /></td>
        </tr>
      </table>
    </xsl:if>

    <xsl:if test="property[@name='Select Columns']">
      <table>
        <caption>Select Columns</caption>
        <xsl:for-each select="property[@name='Select Columns']/value">
          <tr>
            <th><xsl:value-of select="normalize-space(.)" /></th>
          </tr>
        </xsl:for-each>
      </table>
    </xsl:if>

    <xsl:if test="property[@name='Query'] and property[@name='Query']/value">
      <div class="qp-query">
        <div class="qp-bold">Query</div>
        <xsl:value-of select="property[@name='Query']/value" />
      </div>
    </xsl:if>
    
    <xsl:if test="property[@name='Join Strategy']">
      <table>
        <tr>
          <th>Join Strategy</th>
          <td><xsl:value-of select="property[@name='Join Strategy']/value" /></td>
        </tr>
      </table>
    </xsl:if>

    <xsl:if test="property[@name='Join Criteria']">
      <table>
        <tr>
          <th>Join Criteria</th>
          <td><xsl:value-of select="property[@name='Join Criteria']/value" /></td>
        </tr>
      </table>
    </xsl:if>
  </xsl:template>


  <!-- 
  ================================
  Node icons
  ================================
  The following templates determine what icon should be shown for a given node
  -->

  <xsl:template match="node[@name='JoinNode']" mode="NodeIcon" priority="1">
    <xsl:element name="div">
      <xsl:variable name="joinType">
        <xsl:call-template name="pascalize">
          <xsl:with-param name="pText" select="normalize-space(property[@name='Join Type']/value)"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:attribute name="class">qp-icon-<xsl:value-of select="$joinType"/></xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template match="node" mode="NodeIcon">
    <xsl:element name="div">
      <xsl:variable name="nodeName">
        <xsl:call-template name="pascalize">
          <xsl:with-param name="pText" select="replace(normalize-space(@name), 'Node', '')"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:attribute name="class">qp-icon-<xsl:value-of select="$nodeName"/></xsl:attribute>
    </xsl:element>
  </xsl:template>

  <!-- Fallback template - show the Bitmap icon. -->
  <xsl:template match="*" mode="NodeIcon">
    <div class="qp-icon-Catchall"></div>
  </xsl:template>

  <!-- 
  ================================
  Node labels
  ================================
  The following section contains templates used to determine the first (main) label for a node.
  -->

  <xsl:template match="node[@name='XMLTableNode']" mode="NodeLabel" priority="1">
    <div>XML Table</div>
  </xsl:template>
  
  <xsl:template match="node" mode="NodeLabel">
    <div>
      <xsl:variable name="pNodeName">
        <xsl:call-template name="pascalize">
          <xsl:with-param name="pText" select="replace(normalize-space(@name), 'Node', '')"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="nodeName">
        <xsl:call-template name="space-out">
          <xsl:with-param name="sText" select="$pNodeName"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:value-of select="$nodeName" />
    </div>
  </xsl:template>

  <!--
  ================================
  Node alternate labels
  ================================
  The following section contains templates used to determine the second label to be displayed for a node.
  -->

  <xsl:template match="node[@name='JoinNode']" mode="NodeLabel2">
    <xsl:variable name="joinType">
      <xsl:call-template name="capitalize">
        <xsl:with-param name="cText" select="replace(lower-case(normalize-space(property[@name='Join Type']/value)), ' join', '')"/>
      </xsl:call-template>
    </xsl:variable>
    <div>(<xsl:value-of select="$joinType"/>)</div>
  </xsl:template>

  <!-- Disable the default template -->
  <xsl:template match="*" mode="NodeLabel2" />

  <!-- Displays the node cost label. -->
  <xsl:template match="node" mode="NodeCostLabel">
    <xsl:for-each select="property[@name='Statistics']/value">
      <xsl:if test="starts-with(normalize-space(.), 'Node Process Time')">
        <div>Process Time: 
          <xsl:value-of select="replace(normalize-space(.), 'Node Process Time: ', '')"/>
        </div>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>


  <!--
  ================================
  Functions
  ================================
  The following section contains templates used for mangling text
  -->

  <!--
    Convert a string of spaced text to pascal-case, eg. "HELLO THERE" becomes "HelloThere"
    If there are no spaces then the text itself is returned with no changes.
  -->
  <xsl:template name="pascalize">
    <xsl:param name="pText"/>
    <xsl:choose>
      <xsl:when test="contains($pText, ' ')">
        <xsl:for-each select="tokenize($pText,' ')">
          <xsl:value-of select="upper-case(substring(.,1,1))"/>
          <xsl:value-of select="lower-case(substring(.,2,string-length(.)))"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$pText"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
    Space out a string of pascalized text, eg. "HelloThere" becomes "HELLO THERE"
  -->
  <xsl:template name="space-out">
    <xsl:param name="sText"/>
    <xsl:value-of select="replace($sText, '[A-Z]', ' $0')"/>
  </xsl:template>

  <!--
    Capitalize the first letter of the text
  -->
  <xsl:template name="capitalize">
    <xsl:param name="cText"/>
    <xsl:value-of select="upper-case(substring($cText, 1, 1))"/>
    <xsl:value-of select="lower-case(substring($cText, 2, string-length($cText)))"/>
  </xsl:template>
</xsl:stylesheet>
