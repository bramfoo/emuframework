#!/bin/bash

# create svnstat html report
local=$(pwd)
cd ../../
svn update
svn log --xml --verbose > $local/svnlog
java -cp $local/SvnStat-all.jar de.agentlab.svnstat.SvnStat -r $local/svnlog -d $local/stat -config $local/SvnStat.properties

# launch default browser
x-www-browser $local/stat/index.html

# ftp report to website
cd $local/stat/
keeplogin="keepproje"
keepdomain="ftp.keepproject.eu"
keeppwd="kgrf9pcj"
echo "
 open $keepdomain
 user $keeplogin $keeppwd
 cd keepproject.eu/WP2
 binary
 mput *
 bye
" > ftp -n -v -i

