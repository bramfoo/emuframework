<#ftl attributes={"configDir":"configDir", "configFile":"configFile", "digobj":"digobj", "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"}, "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}}>
<#-- Dioscuri 0.4.3 - 0.6.0 configuration simple template (CLI) -->

<#-- Floppy drive letter definition -->
<#assign floppyDriveLetter = {"0":"A", "1":"B"}>

<#-- Drive type definition -->
<#assign driveTypes = {"FAT3_5_720":"720K", "FAT3_5_1440":"1.44M", "C645_25_170":"unsupported drive type", "C645_25_340":"unsupported drive type"}>

<#-- Seperator macro -->
<#macro separator section="undefined">
##Section: ${section}##
</#macro>

<#-- Floppy disk macro -->
<#macro floppyDisk item>
                <floppy>
<#if item.inserted == "true">
                  <enabled>true</enabled>
                  <inserted>true</inserted>
<#else>
                  <enabled>false</enabled>
                  <inserted>false</inserted>
</#if>
                  <driveletter>${floppyDriveLetter[item.num]}</driveletter>
                  <diskformat>${driveTypes[item.type]}</diskformat>
                  <writeprotected>false</writeprotected>
                  <imagefilepath>${item.digobj}</imagefilepath>
                </floppy>
</#macro>

<#-- Fixed disk macro -->
<#macro fixedDisk item>
                <harddiskdrive>
<#if item.enabled == "true">
                  <enabled>true</enabled>
<#else>
                  <enabled>false</enabled>
</#if>
                  <channelindex>${item.index}</channelindex>
<#if item.master == "true">
                  <master>true</master>
<#else>
                  <master>false</master>
</#if>
                  <autodetectcylinders>true</autodetectcylinders>
                  <cylinders>${item.cylinders}</cylinders>
                  <heads>${item.heads}</heads>
                  <sectorspertrack>${item.sectorsPerTrack}</sectorspertrack>
                  <imagefilepath>${item.swImg}</imagefilepath>
                </harddiskdrive>
</#macro>

<#-- Start of preamble -->
<@separator section="preamble"/>
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<emulator debug="false">
    <architecture name="Von Neumann">
        <modules>
            <bios>
                <sysbiosfilepath>images/bios/BIOS-bochs-latest</sysbiosfilepath>
                <vgabiosfilepath>images/bios/VGABIOS-lgpl-latest</vgabiosfilepath>
                <ramaddresssysbiosstartdec>983040</ramaddresssysbiosstartdec>
                <ramaddressvgabiosstartdec>786432</ramaddressvgabiosstartdec>
                <bootdrives>
                    <bootdrive0>Hard Drive</bootdrive0>
                    <bootdrive1>None</bootdrive1>
                    <bootdrive2>None</bootdrive2>
                </bootdrives>
                <floppycheckdisabled>false</floppycheckdisabled>
            </bios>
            <cpu debug="false">
                <cpu32bit>true</cpu32bit>
                <speedmhz>5</speedmhz>
            </cpu>
            <memory debugaddressdecimal="9295" debug="false">
                <sizemb>16</sizemb>
            </memory>
            <pit debug="false">
                <clockrate>5</clockrate>
            </pit>
            <keyboard debug="false">
                <updateintervalmicrosecs>200</updateintervalmicrosecs>
            </keyboard>
            <mouse debug="false">
                <enabled>false</enabled>
                <mousetype>serial</mousetype>
            </mouse>
            <video debug="false">
                <updateintervalmicrosecs>40000</updateintervalmicrosecs>
            </video>
<#-- Start of body -->
<@separator section="body"/>
            <fdc debug="false">
                <updateintervalmicrosecs>250</updateintervalmicrosecs>
                <#list floppyDisks as floppy>
                  <@floppyDisk item=floppy/>
                </#list>
            </fdc>
            <ata debug="false">
                <updateintervalmicrosecs>100000</updateintervalmicrosecs>
                <#list fixedDisks as fixed>
                  <@fixedDisk item=fixed/>
                </#list>
            </ata>
<@separator section="postscript"/>
<#-- Start of postscript -->
        </modules>
    </architecture>
</emulator>