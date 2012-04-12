<#ftl attributes={"configDir":"configDir", "configFile":"configFile", "digobj":"digobj", "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"}, "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}}>
<#-- JavaCPC 6.7 configuration template (CLI) -->

<#-- Floppy drive letter definition -->
<#assign floppyDriveLetter = {"0":"0", "1":"1", "2":"2", "3":"3"}>

<#-- Floppy drive macro -->
<#macro floppyDisk item>
<#if item.type == "AMS3_180">
--df${floppyDriveLetter[item.num]}
${item.digobj}
--bootdrive
df${floppyDriveLetter[item.num]}
</#if>
<#if item.type == "AMSTAPE">
--tape
${item.digobj}
--bootdrive
tape
</#if>
</#macro>

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
--desktop
off
<#list floppyDisks as floppy>
 <@floppyDisk item=floppy/>
</#list>
<@separator section="postscript"/>
<#-- Start of postscript -->