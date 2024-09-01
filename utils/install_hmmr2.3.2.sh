#!/bin/bash

mkdir utils/bin
cd utils

tools="hmmsearch"

function hmmer_install {
wget -O hmmer-2.3.2.tar.gz http://eddylab.org/software/hmmer/hmmer-2.3.2.tar.gz
tar zxvf hmmer-2.3.2.tar.gz ; rm -rf hmmer-2.3.2.tar.gz
cd hmmer-2.3.2
./configure
make
cd ..
ln -s $PWD/hmmer-2.3.2/squid/translate* ~/mambaforge/bin/
}

hmmer_install
