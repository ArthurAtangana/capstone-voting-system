#!/bin/bash
###############################################################################
# install-docker.sh
# -----------------
# Install Docker on Linux.
#
# Usage:
# ------
# $ ./install-docker.sh [ARGS]
#
# Optional Arguments:
#   1. reinstall
#       Force reinstall of Docker if already installed.
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
# Docker

# Check Docker version
if [ -f /usr/bin/docker ]
then
    DOCKER_VERSION="$(docker -v)"
    echo -e "Docker already installed:\t"
    echo -e "\t${DOCKER_VERSION}"
    if [ ! "${1}" == "reinstall" ]
    then
        exit 1
    fi
fi

# Proceed
echo -e "$ANSI_BLUE"
echo -e "Installing Docker..."
echo -e "$ANSI_DEFAULT"

# Remove existing packages to avoid conflict
for pkg in docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc
do 
    sudo apt-get remove $pkg
done

# Add Docker's official GPG key
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

# Install Docker
sudo apt-get install -y \
    docker-ce \
    docker-ce-cli \
    containerd.io \
    docker-buildx-plugin \
    docker-compose-plugin \

# Install QEMU multi-arch support
sudo apt-get install qemu-user-static -y

# Create docker group 
if [ $(getent group docker) ]
then
  echo -e "docker group exists"
else
  echo -e "creating docker group"
    sudo groupadd docker
fi

# Add user to docker group
if id -nG "${USER}" | grep -qw "docker"
then
    echo -e "${USER} already in docker group"
else
    echo -e "Adding ${USER} to docker group"
    sudo usermod -aG docker $USER
fi

# Verify we can run without sudo
if docker run --rm hello-world
then
    echo -e "Docker executable as ${USER}."
else
    echo -e "Docker requires superuser priviles."
    echo -e "Use 'sudo docker'"
fi

echo -e "$ANSI_BLUE"
echo -e "DONE."
echo -e "$ANSI_DEFAULT"

echo -e "--- YOU EXECUTED ---"
echo -e "$SCRIPT DONE"

