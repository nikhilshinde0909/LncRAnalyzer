#!/bin/bash

# install hmmer=3.1b1 from source
echo 'installing hmmer=3.1b1 from source'
function hmmer_install {
wget -O hmmer-3.1b1.tar.gz http://eddylab.org/software/hmmer/hmmer-3.1b1.tar.gz
tar zxvf hmmer-3.1b1.tar.gz ; rm -rf hmmer-3.1b1.tar.gz
cd hmmer-3.1b1
./configure
make
cd ..
ln -sf $PWD/hmmer-3.1b1/src/* ~/mambaforge/bin/
}

hmmer_install
hmmer_path=`which hmmscan 2>/dev/null`
echo 'hmmer=3.1b1 has been installed to ' ${hmmer_path}
