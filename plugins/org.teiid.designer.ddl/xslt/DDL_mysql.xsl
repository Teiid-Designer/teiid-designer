<?xml version="1.0"?> 
<!--
  JBoss, Home of Professional Open Source.

  See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.

  See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:CWM="CWM.xml" xmlns:CWMRDB="CWMRDB.xml" >
<xsl:output method="text" encoding="UTF-8" indent="no"/>

<!--
  ************************************************************************
  ** Primary template
  ************************************************************************ -->
<xsl:template match="/">
	<xsl:apply-templates select="/ddl"/>
</xsl:template>

<!--
  ************************************************************************
  ** Define the global constant variables
  ************************************************************************ -->
  <xsl:variable name="line-feed">
  	 <xsl:text>&#x0a;</xsl:text>
  </xsl:variable>

<!--
  ************************************************************************
  ** Process document root
  ************************************************************************ -->
<xsl:template match="/ddl">
<pre>
<xsl:if test="/ddl/@generateInfoComments='true'">
<xsl:text>-- Build Script
--     RDBMS           : MySQL 5.0
--     Generated With  : </xsl:text><xsl:value-of select="@exportTool"/><xsl:text> </xsl:text><xsl:value-of select="@exportToolVersion"/>
<xsl:text>
--     Generated On    : </xsl:text><xsl:value-of select="@exportDate"/><xsl:text> </xsl:text><xsl:value-of select="@exportTime"/>
<xsl:text>
--     Generation Options
--         Generate Drop Statements      : </xsl:text><xsl:value-of select="@generateDrops"/>
<xsl:text>
--         Convert Table Descriptions to COMMENT ON Statements  : </xsl:text><xsl:value-of select="@generateTableComments"/>
<xsl:text>
--         Convert Column Descriptions to COMMENT ON Statements : </xsl:text><xsl:value-of select="@generateColumnComments"/>
<xsl:text>
--
</xsl:text>
</xsl:if>
<xsl:apply-templates select="./model"/>
</pre>
</xsl:template>

<!--
  ************************************************************************
  ** Process document root
  ************************************************************************ -->
<xsl:template match="model">
<pre>
<xsl:if test="/ddl/@generateInfoComments='true'">
<xsl:text>
--  ----------------------------------------------------------------------------------------------------------------
--  Generate From
--    Model       : </xsl:text><xsl:value-of select="@modelFilename"/>
<xsl:text>
--    Model Type  : </xsl:text><xsl:value-of select="@modelType"/>
<xsl:text>
--    Metamodel   : </xsl:text><xsl:value-of select="@metaModel"/>
<xsl:if test="string-length(@metaModelURL)!=0">
	<xsl:text> (</xsl:text>
	<xsl:value-of select="@metaModelURL"/>
	<xsl:text>)</xsl:text>
</xsl:if>
<xsl:text>
--    Model UUID  : </xsl:text><xsl:value-of select="@uuid"/>
<xsl:text>
--  ----------------------------------------------------------------------------------------------------------------
</xsl:text>
</xsl:if>
<!-- Generate the DROP statements all up front -->
<xsl:if test="/ddl/@generateDrops='true'">
	<xsl:apply-templates select="./view" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./foreignKey" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./primaryKey" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./uniqueKey" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./index" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./table" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./procedure" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./schema" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./catalog" mode="generate-table-drops">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:value-of select="$line-feed"/>
</xsl:if>

<!-- Generate the CREATE statements -->
<xsl:apply-templates select="./catalog">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="./schema">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="./table">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="./index">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="./primaryKey">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="./uniqueKey">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="./foreignKey">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="./procedure">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="./view">
	<xsl:with-param name="terminationString">
		<xsl:text>;</xsl:text>
	</xsl:with-param>
</xsl:apply-templates>
</pre>
</xsl:template>

<!--
  ************************************************************************
  ** Process catalog drops
  ************************************************************************ -->
<xsl:template match="catalog" mode="generate-table-drops">
<xsl:param name="terminationString"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process schema drops
  ************************************************************************ -->
<xsl:template match="schema" mode="generate-table-drops">
<xsl:param name="terminationString"/>
<xsl:text>
DROP SCHEMA </xsl:text><xsl:value-of select="@name"/><xsl:text> CASCADE</xsl:text>
<xsl:value-of select="$terminationString"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process table drops
  ************************************************************************ -->
<xsl:template match="table" mode="generate-table-drops">
<xsl:param name="terminationString"/>
<xsl:if test="local-name(./..) != 'schema'">
<xsl:if test="/ddl/@generateInfoComments='true'">
<xsl:text>
-- Uncomment the following 'TRUNCATE' line to purge data before dropping
-- (truncates cannot be rolled back and are therefore permanent)
</xsl:text>
</xsl:if>
<xsl:text>
--TRUNCATE TABLE </xsl:text><xsl:value-of select="@name"/>
<xsl:value-of select="$terminationString"/>
<xsl:text>
DROP TABLE IF EXISTS </xsl:text><xsl:value-of select="@name"/><xsl:text> CASCADE</xsl:text>
<xsl:value-of select="$terminationString"/>
<xsl:value-of select="$line-feed"/>
</xsl:if>
</xsl:template>

<!--
  ************************************************************************
  ** Process view drops
  ************************************************************************ -->
<!--
<xsl:template match="view" mode="generate-table-drops">
<xsl:param name="terminationString"/>
<xsl:if test="local-name(./..) != 'schema'">
<xsl:text>
DROP VIEW IF EXISTS </xsl:text><xsl:value-of select="@name"/>
<xsl:value-of select="$terminationString"/>
<xsl:value-of select="$line-feed"/>
</xsl:if>
</xsl:template>
-->

<!--
  ************************************************************************
  ** Process catalogs
  ************************************************************************ -->
<xsl:template match="catalog">
<xsl:param name="terminationString"/>
	<xsl:apply-templates select="./table">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./index">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./primaryKey">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./uniqueKey">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./foreignKey">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./procedure">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
	<xsl:apply-templates select="./view">
		<xsl:with-param name="terminationString">
			<xsl:text>;</xsl:text>
		</xsl:with-param>
	</xsl:apply-templates>
</xsl:template>

<!--
  ************************************************************************
  ** Process schemas
  ************************************************************************ -->
<xsl:template match="schema">
<xsl:param name="terminationString"/>
<xsl:if test="/ddl/@generateInfoComments='true'">
<xsl:text>
-- ** NOTE: Replace &quot;&lt;USERID>&quot; with the appropriate ID of the user **
</xsl:text>
</xsl:if>
<xsl:text>
CREATE SCHEMA </xsl:text><xsl:value-of select="@name"/><xsl:text> &lt;USERID>
</xsl:text>
<xsl:apply-templates select="./table"/>
<xsl:apply-templates select="./index"/>
<xsl:apply-templates select="./primaryKey"/>
<xsl:apply-templates select="./uniqueKey"/>
<xsl:apply-templates select="./foreignKey"/>
<xsl:apply-templates select="./procedure"/>
<xsl:apply-templates select="./view"/>
<xsl:if test="/ddl/@generateInfoComments='true'">
<xsl:text>
-- ** Run the statements for this schema **
</xsl:text>
</xsl:if>
<xsl:value-of select="$terminationString"/>
<xsl:value-of select="$line-feed"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process tables
  ************************************************************************ -->
<xsl:template match="table">
<xsl:param name="terminationString"/>
<xsl:if test="/ddl/@generateComments='true'">
<xsl:if test="string-length(@description) != 0">
<xsl:text>
--
-- </xsl:text><xsl:value-of select="@description"/>
</xsl:if>
</xsl:if>
<xsl:if test="/ddl/@generateInfoComments='true'">
<xsl:if test="string-length(@pathInModel) != 0">
<xsl:text>
-- (generated from </xsl:text><xsl:value-of select="@pathInModel"/><xsl:text>)
</xsl:text>
</xsl:if>
</xsl:if>
<xsl:text>
CREATE TABLE </xsl:text><xsl:value-of select="@name"/><xsl:text>
(
</xsl:text>
	<!-- Compute the maximum length of the column names -->
	<xsl:variable name="maxColumnNameLength">
		<xsl:for-each select="./column">
			<xsl:sort select="string-length(@name)" order="ascending" data-type="number"/>
			<xsl:if test="position() = last()">
				<xsl:value-of select="string-length(@name)"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>
	<!-- Produce the output for each column -->
	<xsl:for-each select="./column">
		<xsl:choose>
			<xsl:when test="position() = last()">
				<xsl:apply-templates select="." mode="define">
					<xsl:with-param name="position" select="last"/>
					<xsl:with-param name="desiredLengthForNames" select="$maxColumnNameLength"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="." mode="define">
					<xsl:with-param name="desiredLengthForNames" select="$maxColumnNameLength"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
<xsl:text>
)</xsl:text>
<xsl:value-of select="$terminationString"/>
<xsl:value-of select="$line-feed"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process primary keys
  ************************************************************************ -->
<xsl:template match="primaryKey">
<xsl:param name="terminationString"/>
<xsl:text>
ALTER TABLE </xsl:text>
<xsl:value-of select="@tableName"/>
<xsl:text>
  ADD CONSTRAINT </xsl:text>
<xsl:value-of select="@name"/>
<xsl:text>
    PRIMARY KEY (</xsl:text>
	<xsl:for-each select="./column">
		<xsl:value-of select="@name"/>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:for-each>
<xsl:text>)</xsl:text> 
<xsl:value-of select="$terminationString"/>
<xsl:value-of select="$line-feed"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process unique key constraints
  ************************************************************************ -->
<xsl:template match="uniqueKey">
<xsl:param name="terminationString"/>
<xsl:text>
ALTER TABLE </xsl:text>
<xsl:value-of select="@tableName"/>
<xsl:text>
  ADD CONSTRAINT </xsl:text>
<xsl:value-of select="@name"/>
<xsl:text>
    UNIQUE (</xsl:text>
	<xsl:for-each select="./column">
		<xsl:value-of select="@name"/>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:for-each>
<xsl:text>)</xsl:text> 
<xsl:value-of select="$terminationString"/>
<xsl:value-of select="$line-feed"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process foreign keys
  ************************************************************************ -->
<xsl:template match="foreignKey">
<xsl:param name="terminationString"/>
<xsl:text>
ALTER TABLE </xsl:text>
<xsl:value-of select="@tableName"/>
<xsl:text>
  ADD CONSTRAINT </xsl:text>
<xsl:value-of select="@name"/>
<xsl:text>
    FOREIGN KEY (</xsl:text>
	<xsl:for-each select="./column">
		<xsl:value-of select="@name"/>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:for-each>
<xsl:text>)
    REFERENCES </xsl:text>
<xsl:value-of select="@pkTableName"/>
	<xsl:text>(</xsl:text>
	<xsl:for-each select="./column">
		<xsl:value-of select="@pkColumnName"/>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:for-each>
<xsl:text>)</xsl:text> 
<xsl:value-of select="$terminationString"/>
<xsl:value-of select="$line-feed"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process indexes
  ************************************************************************ -->
<xsl:template match="index">
<xsl:param name="terminationString"/>
<xsl:text>
CREATE </xsl:text>
<xsl:if test="@isUnique='true'">
	<xsl:text>UNIQUE </xsl:text>
</xsl:if>
<xsl:text>INDEX </xsl:text><xsl:value-of select="@name"/><xsl:text> ON </xsl:text><xsl:value-of select="@tableName"/>
<xsl:text> (</xsl:text>
	<xsl:for-each select="./indexColumn/column">
		<xsl:value-of select="@name"/>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:for-each>
<xsl:text>)</xsl:text>
<xsl:value-of select="$terminationString"/>
<xsl:value-of select="$line-feed"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process columns
  ************************************************************************ -->
<xsl:template match="column" mode="define">
	<xsl:param name="position"/>
	<xsl:param name="desiredLengthForNames"/>
	<xsl:text> </xsl:text>
	<xsl:text> </xsl:text>
	<xsl:call-template name="fillString">
		<xsl:with-param name="origString" select="@name"/>
		<xsl:with-param name="desiredLength" select="$desiredLengthForNames + 2"/>
	</xsl:call-template>
	<xsl:apply-templates select="@type"/>
	<xsl:apply-templates select="@initialValue"/>
	<xsl:apply-templates select="@isNullable"/>
	<xsl:if test="$position != 'last'">
<xsl:text>,
</xsl:text>
	</xsl:if>
</xsl:template>

<!--
  ************************************************************************
  ** Process column types
  ************************************************************************ -->
<xsl:template name="column-type" match="column/@type">
	<xsl:variable name="upperType">
		<xsl:call-template name="typeToUpperCase">
			<xsl:with-param name="originalString" select="."/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:text> </xsl:text>
	<!-- Define the data type value based upon mapping to DBMS -->
	<xsl:choose>
	
	    <!--                     *******                         -->
	    <!-- THESE CORRESPOND TO RUNTIME TYPE, NOT BUILT-IN TYPE -->
	    <!--                     *******                         -->

		<!-- If 'string' -->
		<xsl:when test=". = 'string' and ../@isLengthFixed='true'">
			<xsl:text>CHAR</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>		
		<xsl:when test=". = 'string' and ../@isLengthFixed='false'">
			<xsl:text>VARCHAR</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'integer' -->
		<xsl:when test=". = 'integer'">
			<xsl:text>INTEGER</xsl:text>
            <xsl:call-template name="column-stringLength">
                <xsl:with-param name="length" select="../@length"/>
            </xsl:call-template>
		</xsl:when>
		<!-- If 'int' -->
		<xsl:when test=". = 'int'">
			<xsl:text>INT</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'unsignedInt' -->
		<xsl:when test=". = 'unsignedInt'">
			<xsl:text>INT</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'long' -->
		<xsl:when test=". = 'long'">
			<xsl:text>BIGINT</xsl:text>
		</xsl:when>
		<!-- If 'unsignedLong' -->
		<xsl:when test=". = 'unsignedLong'">
			<xsl:text>BIGINT</xsl:text>
		</xsl:when>
		<!-- If 'float' -->
		<xsl:when test=". = 'float'">
			<xsl:text>FLOAT</xsl:text>
            <xsl:call-template name="column-floatLength">
                <xsl:with-param name="precision" select="../@precision"/>
                <xsl:with-param name="scale" select="../@scale"/>
            </xsl:call-template>
		</xsl:when>
		<!-- If 'double' -->
		<xsl:when test=". = 'double'">
			<xsl:text>DOUBLE</xsl:text>
			<xsl:call-template name="column-floatLength">
				<xsl:with-param name="precision" select="../@precision"/>
				<xsl:with-param name="scale" select="../@scale"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'bigdecimal' -->
		<xsl:when test=". = 'bigdecimal'">
			<xsl:text>DECIMAL</xsl:text>
			<xsl:call-template name="column-numericLength">
				<xsl:with-param name="precision" select="../@precision"/>
				<xsl:with-param name="scale" select="../@scale"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'biginteger' -->
		<xsl:when test=". = 'biginteger'">
			<xsl:text>BIGINT</xsl:text>
		</xsl:when>
		<!-- If 'byte' -->
		<xsl:when test=". = 'byte'">
			<xsl:text>SMALLINT</xsl:text>
		</xsl:when>
		<!-- If 'unsignedByte' -->
		<xsl:when test=". = 'unsignedByte'">
			<xsl:text>SMALLINT</xsl:text>
		</xsl:when>
		<!-- If 'short' -->
		<xsl:when test=". = 'short'">
			<xsl:text>SMALLINT</xsl:text>
		</xsl:when>
		<!-- If 'unsignedShort' -->
		<xsl:when test=". = 'unsignedShort'">
			<xsl:text>SMALLINT</xsl:text>
		</xsl:when>
		<!-- If 'nonPositiveInteger' -->
		<xsl:when test=". = 'nonPositiveInteger'">
			<xsl:text>INTEGER</xsl:text>
			<xsl:call-template name="column-numericLength">
				<xsl:with-param name="precision" select="../@precision"/>
				<xsl:with-param name="scale" select="../@scale"/>
				<xsl:with-param name="defaultPrecision">
					<xsl:text>12</xsl:text>
				</xsl:with-param>
				<xsl:with-param name="defaultScale">
					<xsl:text></xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:when>		
		<!-- If 'negativeInteger' -->
		<xsl:when test=". = 'negativeInteger'">
			<xsl:text>INTEGER</xsl:text>
			<xsl:call-template name="column-numericLength">
				<xsl:with-param name="precision" select="../@precision"/>
				<xsl:with-param name="scale" select="../@scale"/>
				<xsl:with-param name="defaultPrecision">
					<xsl:text>12</xsl:text>
				</xsl:with-param>
				<xsl:with-param name="defaultScale">
					<xsl:text></xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:when>	
		<!-- If 'nonNegativeInteger' -->
		<xsl:when test=". = 'nonNegativeInteger'">
			<xsl:text>INTEGER</xsl:text>
			<xsl:call-template name="column-numericLength">
				<xsl:with-param name="precision" select="../@precision"/>
				<xsl:with-param name="scale" select="../@scale"/>
				<xsl:with-param name="defaultPrecision">
					<xsl:text>12</xsl:text>
				</xsl:with-param>
				<xsl:with-param name="defaultScale">
					<xsl:text></xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'positiveInteger' -->
		<xsl:when test=". = 'positiveInteger'">
			<xsl:text>INTEGER</xsl:text>
			<xsl:call-template name="column-numericLength">
				<xsl:with-param name="precision" select="../@precision"/>
				<xsl:with-param name="scale" select="../@scale"/>
				<xsl:with-param name="defaultPrecision">
					<xsl:text>12</xsl:text>
				</xsl:with-param>
				<xsl:with-param name="defaultScale">
					<xsl:text></xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:when>								
		<!-- If 'char' -->
		<xsl:when test=". = 'char'">
			<xsl:text>CHAR</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'boolean' -->
		<xsl:when test=". = 'boolean'">
			<xsl:text>TINYINT</xsl:text>
		</xsl:when>
		<!-- If 'date' -->
		<xsl:when test=". = 'date'">
			<xsl:text>DATE</xsl:text>
		</xsl:when>
		<!-- If 'time' -->
		<xsl:when test=". = 'time'">
			<xsl:text>TIME</xsl:text>
		</xsl:when>
		<!-- If 'timestamp' -->
		<xsl:when test=". = 'timestamp'">
			<xsl:text>TIMESTAMP</xsl:text>
		</xsl:when>
		<!-- If 'object' -->
		<xsl:when test=". = 'object'">
			<xsl:text>LONGBLOB</xsl:text>
		</xsl:when>
		<!-- If 'blob' -->
		<xsl:when test=". = 'blob'">
			<xsl:text>LONGBLOB</xsl:text>
		</xsl:when>
		<!-- If 'clob' -->
		<xsl:when test=". = 'clob'">
			<xsl:text>LONGTEXT</xsl:text>
		</xsl:when>
		<!-- If 'CHAR' -->
		<xsl:when test="$upperType = 'CHAR'">
			<xsl:text>CHAR</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'NUMBER' -->
		<xsl:when test="$upperType = 'NUMBER'">
			<xsl:text>BIGINT</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'NUMERIC' -->
		<xsl:when test="$upperType = 'NUMERIC'">
			<xsl:text>DECIMAL</xsl:text>
			<xsl:call-template name="column-numericLength">
				<xsl:with-param name="precision" select="../@precision"/>
				<xsl:with-param name="scale" select="../@scale"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'VARCHAR' -->
		<xsl:when test="$upperType = 'VARCHAR'">
			<xsl:text>VARCHAR</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>
		<!-- If 'VARCHAR2' -->
		<xsl:when test="$upperType = 'VARCHAR2'">
			<xsl:text>VARCHAR</xsl:text>
			<xsl:call-template name="column-stringLength">
				<xsl:with-param name="length" select="../@length"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="."/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!--
  ************************************************************************
  ** Process column length or precision/scale
  ************************************************************************ -->
<xsl:template name="column-stringLength">
	<xsl:param name="length"/>
	<xsl:text>(</xsl:text>
	<xsl:value-of select="$length"/>
	<xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-numericLength">
	<xsl:param name="precision"/>
	<xsl:param name="scale"/>
	<xsl:param name="defaultPrecision"/>
	<xsl:param name="defaultScale"/>
	<xsl:choose>
		<xsl:when test="string-length($precision)!=0 and $precision!='0'">
	        <xsl:text>(</xsl:text>
	        <xsl:value-of select="$precision"/>
	        <xsl:choose>
		        <xsl:when test="string-length($scale)!=0 and $scale!='0'">
		        	<xsl:text>,</xsl:text>
		        	<xsl:value-of select="$scale"/>
		        </xsl:when>
				<xsl:otherwise>
			        <xsl:if test="string-length($defaultScale)!=0 and $defaultScale!='0'">
			        	<xsl:text>,</xsl:text>
			        	<xsl:value-of select="$defaultScale"/>
			        </xsl:if>
				</xsl:otherwise>
	        </xsl:choose>
	        <xsl:text>)</xsl:text>
		</xsl:when>
		<xsl:otherwise>
		    <xsl:if test="string-length($defaultPrecision)!=0 and $defaultPrecision!='0'">
		        <xsl:text>(</xsl:text>
		        <xsl:value-of select="$defaultPrecision"/>
		        <xsl:choose>
			        <xsl:when test="string-length($scale)!=0 and $scale!='0'">
			        	<xsl:text>,</xsl:text>
			        	<xsl:value-of select="$scale"/>
			        </xsl:when>
					<xsl:otherwise>
				        <xsl:if test="string-length($defaultScale)!=0 and $defaultScale!='0'">
				        	<xsl:text>,</xsl:text>
				        	<xsl:value-of select="$defaultScale"/>
				        </xsl:if>
					</xsl:otherwise>
		        </xsl:choose>
	        <xsl:text>)</xsl:text>
		    </xsl:if>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- float, real, double do not have optional scale param --> 
<xsl:template name="column-floatLength">
    <xsl:param name="precision"/>
    <xsl:param name="scale"/>
    <xsl:choose>
        <xsl:when test="string-length($precision)!=0 and $precision!='0'">
            <xsl:text>(</xsl:text>
            <xsl:value-of select="$precision"/>
            <xsl:choose>
                <xsl:when test="string-length($scale)!=0 and $scale!='0'">
                    <xsl:text>,</xsl:text>
                    <xsl:value-of select="$scale"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>,10</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text>)</xsl:text>
        </xsl:when>
    </xsl:choose>
            
</xsl:template>

<!--
  ************************************************************************
  ** Convert to uppercase
  ************************************************************************ -->
<xsl:template name="typeToUpperCase">
	<xsl:param name="originalString"/>
	<xsl:value-of select="translate($originalString,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
</xsl:template>

<!--
  ************************************************************************
  ** Process column cardinality/multiplicity
  ************************************************************************ -->
<xsl:template name="column-nullable" match="column/@isNullable">
	<xsl:if test=". = 'false'">
		<xsl:text> NOT NULL</xsl:text>
	</xsl:if>
</xsl:template>

<!--
  ************************************************************************
  ** Process table comment
  ************************************************************************ -->
<xsl:template name="commentOnTable">
	<xsl:param name="tableName"/>
	<xsl:param name="description"/>
	<xsl:param name="terminationString"/>
	<xsl:if test="/ddl/@generateTableComments='true'">
		<xsl:if test="string-length($description) != 0">
			<xsl:value-of select="$line-feed"/>
			<xsl:text>Comment on Table </xsl:text>
			<xsl:value-of select="$tableName"/>
			<xsl:text> is '</xsl:text>
			<xsl:call-template name="processDescription">
				<xsl:with-param name="origString" select="$description"/>
			</xsl:call-template>
			<xsl:text>'</xsl:text>
			<xsl:value-of select="$terminationString"/>
		</xsl:if>
	</xsl:if>
</xsl:template>

<!--
  ************************************************************************
  ** Process column comment
  ************************************************************************ -->
<xsl:template name="commentOnColumn">
	<xsl:param name="tableName"/>
	<xsl:param name="columnName"/>
	<xsl:param name="description"/>
	<xsl:param name="terminationString"/>
	<xsl:if test="/ddl/@generateColumnComments='true'">
		<xsl:if test="string-length($description) != 0">
			<xsl:text>
Comment on Column </xsl:text>
			<xsl:value-of select="$tableName"/>
			<xsl:text>.</xsl:text>
			<xsl:value-of select="$columnName"/>
			<xsl:text> is '</xsl:text>
			<xsl:call-template name="processDescription">
				<xsl:with-param name="origString" select="$description"/>
			</xsl:call-template>
			<xsl:text>'</xsl:text>
			<xsl:value-of select="$terminationString"/>
		</xsl:if>
	</xsl:if>
</xsl:template>

<!--
  ************************************************************************
  ** Process a string to escape tick marks (for comments)
  ************************************************************************ -->
<xsl:template name="processDescription">
	<xsl:param name="origString"/>
	<xsl:call-template name="escapeTickChar">
		<xsl:with-param name="inputString" select="$origString"/>
		<xsl:with-param name="startIndex">
			<xsl:text>1</xsl:text>
		</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<!--
  ************************************************************************
  ** Recursive method to replace single ticks with double ticks
  ************************************************************************ -->
<xsl:template name="escapeTickChar">
	<xsl:param name="inputString"/>
	<xsl:param name="startIndex"/>
	<xsl:choose>
		<xsl:when test="string-length($inputString) &lt; $startIndex">
			<xsl:value-of select="$inputString"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:variable name="tickCharacter">
				<xsl:text>'</xsl:text>
			</xsl:variable>
			<xsl:variable name="character">
				<xsl:value-of select="substring($inputString,$startIndex,1)"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$character = $tickCharacter">
					<xsl:variable name="newString">
						<xsl:value-of select="concat(substring($inputString,1,$startIndex),$tickCharacter,substring($inputString,$startIndex+1))"/>
					</xsl:variable>
					<xsl:call-template name="escapeTickChar">
						<xsl:with-param name="inputString" select="$newString"/>
						<xsl:with-param name="startIndex" select="$startIndex+2"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="escapeTickChar">
						<xsl:with-param name="inputString" select="$inputString"/>
						<xsl:with-param name="startIndex" select="$startIndex+1"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!--
  ************************************************************************
  ** Process column initial value
  ************************************************************************ -->
<xsl:template name="column-default" match="column/@initialValue">
	<xsl:if test="string-length(.)!=0"> DEFAULT '<xsl:value-of select="."/>'</xsl:if>
</xsl:template>

<!--
  ************************************************************************
  ** Make the specified string value a certain length
  ** This is recursive if the string must be appended to obtain the length
  ************************************************************************ -->
<xsl:template name="fillString">
	<xsl:param name="origString"/>
	<xsl:param name="desiredLength"/>
	<xsl:choose>
		<xsl:when test="string-length($origString) &gt; $desiredLength">
			<xsl:value-of select="substring($origString,1,$desiredLength)"/>
		</xsl:when>
		<xsl:when test="string-length($origString) = $desiredLength">
			<xsl:value-of select="$origString"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="fillString">
				<xsl:with-param name="origString" select="concat($origString,' ')"/>
				<xsl:with-param name="desiredLength" select="$desiredLength"/>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
