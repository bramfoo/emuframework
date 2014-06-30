@echo off
REM this script requires the tigris SVN command line client to be installed (the tortoiseSVN contextual menu client is not sufficient) 
REM It can be downloaded at http://subversion.tigris.org/getting.html#windows

REM create svnstat html report
set CURR_DIR=%CD%
echo %CURR_DIR%
cd ..\..\
svn update
svn log --xml --verbose > "%CURR_DIR%\svnlog"
java -cp "%CURR_DIR%\SvnStat-all.jar" de.agentlab.svnstat.SvnStat -r "%CURR_DIR%\svnlog" -d "%CURR_DIR%\stat" -config "%CURR_DIR%\SvnStat.properties"

REM  launch default browser
start iexplore "%CURR_DIR%\stat\index.html"

REM ftp report to website
cd "%CURR_DIR%\stat"
set keeplogin="keepproje"
set keepdomain="ftp.keepproject.eu"
set keeppwd="kgrf9pcj"

echo open %keepdomain% > ftp_cmd.txt
echo user %keeplogin% %keeppwd% >> ftp_cmd.txt
echo cd keepproject.eu/WP2 >> ftp_cmd.txt
echo binary >> ftp_cmd.txt
echo mput "*.*" >> ftp_cmd.txt
echo bye >> ftp_cmd.txt

ftp -n -v -i -s:ftp_cmd.txt
