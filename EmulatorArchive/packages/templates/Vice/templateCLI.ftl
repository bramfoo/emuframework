<#ftl attributes={"configDir":"configDir", "configFile":"configFile", "digobj":"digobj", "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"}, "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}}>
<#-- Vice 2.2 configuration template (CLI) -->

<#-- Floppy drive letter definition -->
<#assign floppyDriveLetter = {"0":"8", "1":"9", "2":"10", "3":"11"}>

<#-- Drive type definition -->
<#assign driveTypes = {"FAT3_5_720":"unsupported drive", "FAT3_5_1440":"unsupported drive", "C645_25_170":"1541", "C645_25_340":"1571", "C64TAPE":"1541"}>

<#-- Seperator macro -->
<#macro separator section="undefined">
##Section: ${section}##
</#macro>

<#-- Floppy drive macro -->
<#macro floppyDisk item>
<#if item.type?has_content >
-drive${floppyDriveLetter[item.num]}type
${driveTypes[item.type]}
-${floppyDriveLetter[item.num]}
${item.digobj}
</#if>
<#if item.inserted == "true">
+truedrive
<#else>
-truedrive
</#if>
</#macro>
<#-- Start of preamble -->
<@separator section="preamble"/>
<#-- Start of body -->
<@separator section="body"/>
-autostart
${digobj}
<#list floppyDisks as floppy>
 <@floppyDisk item=floppy/>
</#list>
<@separator section="postscript"/>
<#-- Start of postscript -->