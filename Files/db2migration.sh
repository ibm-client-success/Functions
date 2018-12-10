##############################################################################################################################
# This db2migration.sh file is created by Poojitha-Bikki and the original code can be located at this link:
# https://github.ibm.com/BlueTardigrade/db2oc-migration/blob/firststeps/db2migration.sh
# Usage:
# bash db2migration.sh <path to folder to save table info> <source db hostname> <source db port> <source db username> 
# <source db name> <source db password> <arbitrary node name to use when cataloging db> <target db user> <target db port> 
# <target db name>
##############################################################################################################################

#!/bin/bash

FOLDER=${1?"Folder name with ixf files is undefined"}

SOURCE_HOST=${2?"Source DB host is undefined"}
SOURCE_PORT=${3?"Source DB port is undefined"}
SOURCE_USER=${4?"Source DB user is undefined"}
SOURCE_DB=${5?"Source DB name is undefined"}
SOURCE_DB_ALIAS=${6?"Source DB alias is undefined"}
SOURCE_PASSWORD=${7?"Source DB password is undefined"}
SOURCE_SCHEMA=${SOURCE_USER^^}
NODE_NAME=${8?"Node name is undefined"}

TARGET_USER=${9?"Target DB user is undefined"}
TARGET_PASSWORD=${10?"Target DB password is undefined"}
TARGET_DB=${11?"Target DB name is undefined"}

DEBUG=true

if [ "$DEBUG" = true ]; then
    echo FOLDER: $FOLDER

    echo SOURCE HOST: $SOURCE_HOST
    echo SOURCE PORT: $SOURCE_PORT
    echo SOURCE USER: $SOURCE_USER
    echo SOURCE DB NAME: $SOURCE_DB
    echo SOURCE DB ALIAS: $SOURCE_DB_ALIAS
    echo SOURCE PASSWORD: $SOURCE_PASSWORD
    echo SOURCE SCHEMA: $SOURCE_SCHEMA
    echo NODE NAME: $NODE_NAME

    echo TARGET USER: $TARGET_USER
    echo TARGET PASSWORD: $TARGET_PASSWORD
    echo TARGET DB: $TARGET_DB
fi

# This function catalogues a given remote DB.
db2_catalog () {
    NODE=$1
    HOST=$2
    PORT=$3
    DB=$4
    DB_ALIAS=$5

    db2 catalog tcpip node $NODE remote $HOST server $PORT
    db2 catalog database $DB as $DB_ALIAS at node $NODE
}

# This function removes previously catalogued DBs from the machine.
# If passed in a DB or node name that isn't catalogued, it will display an harmless warning.
db2_uncatalog() {
    DB_ALIAS=$1
    NODE=$2
    db2 uncatalog database $DB_ALIAS
    db2 uncatalog node $NODE
}

# This function makes a connection to a given DB. It assumes that the user will pass in credentials.
db2_connect () {
    DB_ALIAS=$1
    USER=$2
    PASSWORD=$3

    db2 connect to $DB_ALIAS user $USER using $PASSWORD
}

# This function exports all of the source DB tables and exports them as .ixf files into the folder directory passed in as a command line arg.
db2_export () {
    pushd $FOLDER

    for t in $(db2 -x "select rtrim(tabschema) || '.' || rtrim(tabname) from syscat.tables where tabschema = '$SOURCE_SCHEMA'"); do
        db2 "EXPORT TO $t.ixf OF ixf MESSAGES $t.msg SELECT * FROM $t"; echo $t;
    done

    popd
}

# This function switches to the directory containing the old *.ixf files and imports them into the newly created DB.
db2_import () {
    pushd $FOLDER

    for file in *.ixf; do
        table="$(echo $file | awk -F. '{print $2}')";
        db2 "IMPORT FROM $file of ixf messages $file.msg CREATE INTO $table";
    done

    popd
}

# We start by cataloging the source DB.
# The initial 'uncatalog' command is there to prevent a warning in case node name is already in use by a catalogued node.
db2_uncatalog $SOURCE_DB_ALIAS $NODE_NAME
db2_catalog $NODE_NAME $SOURCE_HOST $SOURCE_PORT $SOURCE_DB $SOURCE_DB_ALIAS
db2_connect $SOURCE_DB_ALIAS $SOURCE_USER $SOURCE_PASSWORD
db2_export
db2 terminate
db2_uncatalog $SOURCE_DB_ALIAS $NODE_NAME

# We then start the import process on a newly created database.
db2 create database $TARGET_DB
db2_connect $TARGET_DB $TARGET_USER $TARGET_PASSWORD
db2_import
db2 terminate
