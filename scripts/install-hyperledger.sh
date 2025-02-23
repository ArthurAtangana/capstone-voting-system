#!/bin/bash
###############################################################################
# install-hyperledger.sh
# ----------------------
# Retrieve the Hyperledger install script and execute it, specifying all 
# components for install.
#
# Note: If you want the `docker` and `podman` components, you will need to
# have these programs resident on your system.
#
# Usage:
# ------
# $ ./install-hyperledger.sh 
#
# Notes:
# ------
#
###############################################################################
# Author: M. De Santis
# Date: 2024/11/16
###############################################################################

set -e

SCRIPT=$(readlink -f "$0")
SCRIPT_DIR=$(dirname ${SCRIPT})

echo -e "EXECUTING $SCRIPT..."

###############################################################################
# Pretty

ANSI_BLACK="\x1b[30m"
ANSI_RED="\x1b[31m"
ANSI_GREEN="\x1b[32m"
ANSI_YELLOW="\x1b[33m"
ANSI_BLUE="\x1b[34m"
ANSI_MAGENTA="\x1b[35m"
ANSI_CYAN="\x1b[36m"
ANSI_WHITE="\x1b[37m"
ANSI_DEFAULT="\x1b[39m"

###############################################################################
# System Dependencies

echo -e "$ANSI_BLUE"
echo -e "Installing system dependencies..."
echo -e "$ANSI_DEFAULT"

sudo apt-get install -y \
    git \
    curl

echo -e "$ANSI_BLUE"
echo -e "DONE."
echo -e "$ANSI_DEFAULT"

###############################################################################
# Hyperledger

echo -e "$ANSI_BLUE"
echo -e "Installing Hyperledger Fabric..."
echo -e "$ANSI_DEFAULT"

echo -e "Retrieving installation script..."
curl -sSLO https://raw.githubusercontent.com/hyperledger/fabric/main/scripts/install-fabric.sh && chmod +x install-fabric.sh

echo -e "Executing installation script and retrieving all optional components..."
${PWD}/install-fabric.sh --fabric-version 2.5.9 --ca-version 1.5.12 docker podman binary samples

echo -e "$ANSI_BLUE"
echo -e "DONE."
echo -e "$ANSI_DEFAULT"


echo -e "--- YOU EXECUTED ---"
echo -e "$SCRIPT DONE"

