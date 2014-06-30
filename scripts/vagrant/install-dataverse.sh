#!/bin/bash
WAR=/dataverse/target/dataverse-4.0.war
if [ ! -f $WAR ]; then
  echo "no war file found... building"
  su $SUDO_USER -s /bin/sh -c "cd /dataverse && mvn package"
fi
cd /dataverse/scripts/installer
./install --hostname localhost --gfdir /home/glassfish/glassfish4 -y --force
