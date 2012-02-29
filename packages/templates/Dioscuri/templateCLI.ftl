<#ftl attributes={"configDir":"configDir", "configFile":"configFile", "digobj":"digobj", "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"}, "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}}>
<#-- Dioscuri 0.4.3 - 0.6.0 configuration template (CLI) -->

<#-- Seperator macro -->
<#macro separator section="undefined">
##Section: ${section}##
</#macro>

<#-- Start of preamble -->
<@separator section="preamble"/>
java
-jar
<#-- Start of body -->
<@separator section="body"/>
-r
-c
${configFile}
<@separator section="postscript"/>
<#-- Start of postscript -->