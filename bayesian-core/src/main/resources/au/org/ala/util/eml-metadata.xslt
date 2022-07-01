<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eml="eml://ecoinformatics.org/eml-2.1.1">

    <xsl:output method="text" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/eml:eml">
{
  "identifier": "<xsl:value-of select="dataset/alternateIdentifier"/>",
  "version": "",
  "type": "http://purl.org/dc/dcmitype/Dataset",
  "title": "<xsl:value-of select="dataset/title"/>",
  "description": "<xsl:value-of select="dataset/abstract/para"/> <xsl:value-of select="dataset/additionalInfo/para"/>",
  "created": "<xsl:choose><xsl:when test="additionalMetadata/metadata/gbif/dateStamp"><xsl:value-of select="additionalMetadata/metadata/gbif/dateStamp"/></xsl:when><xsl:when test="dataset/pubDate"><xsl:value-of
            select="dataset/pubDate"/>T00:00:00Z</xsl:when></xsl:choose>",
  "creator": "<xsl:apply-templates select="dataset/creator" mode="party"/>",
  "publisher": "<xsl:apply-templates select="dataset/associatedParty[role = 'PUBLISHER']" mode="party"/>",
  "sources": [<xsl:for-each select="additionalMetadata/metadata/gbif/bibliography/citation[not(starts-with(@identifier, 'dr'))]"><xsl:sort select="@identifier"/>
    {
      "identifier": "<xsl:value-of select="@identifier"/>",
      "title": "<xsl:value-of select="translate(text(), '&#10;&#13;', '  ')"/>"
    }<xsl:if test="position() != last()">,</xsl:if>
  </xsl:for-each>
  ]
}
    </xsl:template>

    <xsl:template match="*" mode="party"><xsl:value-of select="individualName"/><xsl:if test="individualName and organizationName">, </xsl:if><xsl:value-of
            select="organizationName"/></xsl:template>
    
</xsl:stylesheet>