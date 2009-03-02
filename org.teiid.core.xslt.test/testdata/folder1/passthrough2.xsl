<?xml version="1.0" encoding="UTF-8"?>
<!--
  JBoss, Home of Professional Open Source.

  See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.

  See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
-->
<xsl:stylesheet version="1.0" xmlns:xslns="http://www.w3.org/1999/XMLNS" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

<!--
  ************************************************************************
  ** Primary template; pass everything through
  ************************************************************************ -->
<xsl:template match="@*|node()">
	<xsl:copy>
		<xsl:apply-templates select="@*|node()"/>
	</xsl:copy>
</xsl:template>


</xsl:stylesheet>
