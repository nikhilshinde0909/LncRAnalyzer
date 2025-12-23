#!/bin/bash

# Check for existing Mambaforge, Miniforge, or Anaconda installation
if [[ -d "$HOME/mambaforge" ]]; then
    echo "Existing Mambaforge installation detected."
    BIN=$HOME/mambaforge/bin/
elif [[ -d "$HOME/miniforge" ]]; then
    echo "Existing Miniforge installation detected."
    BIN=$HOME/miniforge/bin/
elif [[ -d "$HOME/anaconda3" ]]; then
    echo "Existing Anaconda installation detected."
    BIN=$HOME/anaconda3/bin/
else
    echo "No recognized environment (Mambaforge, Miniforge, Anaconda) found in $HOME."
    echo "Installing Miniforge..."
    
    curl -L https://github.com/conda-forge/miniforge/releases/latest/download/Miniforge3-Linux-x86_64.sh -o miniforge.sh \
    && chmod +x miniforge.sh \
    && bash miniforge.sh -b -p $HOME/miniforge \
    && rm miniforge.sh
    
    BIN=$HOME/miniforge/bin/
fi

# Export paths
export PATH=$PATH:$BIN

# Verify that mamba is installed, if not use conda
if ! command -v mamba &> /dev/null; then
    echo "Mamba not found, trying conda..."
    mamba="conda"
else
    mamba="mamba"
fi

# Update the mamba environment using the YAML file
echo "Updating existing environment..."
$mamba env update --file LncRAnalyzer.yml && conda clean -y -a

# Installing LncFinder
if [[ -f "$BIN/R" ]]; then
    echo "R installation found.."
    R="$BIN/R"
    $R -e 'options(timeout=600); install.packages(c("LncFinder", "seqinr"), repos="https://cran.r-project.org")'
else
    echo "R installation not found..."
    exit 1
fi

# Creating FEELnc environment
echo "Creating FEELnc environment..."
$mamba create -y -n FEELnc -c bioconda feelnc && conda clean -y -a

# Creating CPC-CPAT-Slncky environment
echo "Creating CPC-CPAT-Slncky environment..."
$mamba env create -f cpc2-cpat-slncky.yml && conda clean -y -a

# Creating RNAsamba environment
echo "Creating RNAsamba environment..."
$mamba env create -f rnasamba.yml && conda clean -y -a

# install hmmer=3.1b1 from source
echo "Installing hmmer=3.1b1 from source"

function hmmer_install {
    curl -L -o hmmer-3.1b1.tar.gz http://eddylab.org/software/hmmer/hmmer-3.1b1.tar.gz
    tar zxvf hmmer-3.1b1.tar.gz ; rm -rf hmmer-3.1b1.tar.gz
    mv hmmer-3.1b1 utils/
    cd utils/hmmer-3.1b1/
    ./configure
    make
    cd ..
    cd ..
}

hmmer_install

# Create the symbolic link for hmmmer-3.1b
ln -sf $PWD/utils/hmmer-3.1b1/src/* $BIN
hmmer_path=`which hmmscan 2>/dev/null`
echo "HMMER 3.1b1 has been installed to: ${hmmer_path}"

# Detect existing Python2.7 installation and change permissions
if [[ -d "$HOME/mambaforge/envs/cpc2-cpat-slncky/" ]]; then
    echo "Python2.7 installation detected in Mambaforge."
    PY_DIR="$HOME/mambaforge/envs/cpc2-cpat-slncky/lib/python2.7"
    LINK_PATH="$HOME/mambaforge/envs/cpc2-cpat-slncky/bin/"
elif [[ -d "$HOME/miniforge/envs/cpc2-cpat-slncky/" ]]; then
    echo "Python2.7 installation detected in Miniforge."
    PY_DIR="$HOME/miniforge/envs/cpc2-cpat-slncky/lib/python2.7"
    LINK_PATH="$HOME/miniforge/envs/cpc2-cpat-slncky/bin/"
elif [[ -d "$HOME/anaconda3/envs/cpc2-cpat-slncky/" ]]; then
    echo "Python2.7 installation detected in Anaconda."
    PY_DIR="$HOME/anaconda3/envs/cpc2-cpat-slncky/lib/python2.7"
    LINK_PATH="$HOME/anaconda3/envs/cpc2-cpat-slncky/bin/"
else
    echo "No Python2.7 found in $HOME."
    exit 1
fi
echo "Changing permissions for $PY_DIR."
chmod -R 777 $PY_DIR
echo "Creating symbolic link for Slncky"
chmod +x $PWD/utils/slncky/slncky.v1.0 $PWD/utils/slncky/alignTranscripts1.0
ln -sf $PWD/utils/slncky/alignTranscripts1.0 $PWD/utils/slncky/slncky.v1.0 $LINK_PATH

echo "getting paths for tools"
Activate_path=`which activate 2>/dev/null`
bpipe_path=`which bpipe 2>/dev/null`
hisat2_path=`which hisat2 2>/dev/null`
stringtie_path=`which stringtie 2>/dev/null`
gffread_path=`which gffread 2>/dev/null`
gffcompare_path=`which gffcompare 2>/dev/null`
samtools_path=`which samtools 2>/dev/null`
hmmpress_path=`which hmmpress 2>/dev/null`
pfamscan_path=`which hmmscan 2>/dev/null`
transeq_path=`which transeq 2>/dev/null`
bowtie2_path=`which bowtie2 2>/dev/null`
bamToFastq_path=`which bamToFastq 2>/dev/null`
fastp_path=`which fastp 2>/dev/null`
seqtk_path=`which seqtk 2>/dev/null`
featureCounts_path=`which featureCounts 2>/dev/null`
python3_path=`which python3 2>/dev/null`
Rscript_path=`which Rscript 2>/dev/null`
PLEK_path=`which PLEK.py 2>/dev/null`
PLEKModelling_path=`which PLEKModelling.py 2>/dev/null`
bedtools_path=`which bedtools 2>/dev/null`

source $Activate_path rnasamba
rnasamba_path=`which rnasamba 2>/dev/null`

source $Activate_path FEELnc
perl_path=`which perl 2>/dev/null`
FEELnc_filter_path=`which FEELnc_filter.pl 2>/dev/null`
FEELnc_codpot_path=`which FEELnc_codpot.pl 2>/dev/null`
FEELnc_classifier_path=`which FEELnc_classifier.pl 2>/dev/null`

source $Activate_path cpc2-cpat-slncky
python2_path=`which python 2>/dev/null`
cpc2_path=`which CPC2.py 2>/dev/null`
make_hexamer_path=`which make_hexamer_tab.py 2>/dev/null`
logit_model_path=`which make_logitModel.py 2>/dev/null`
CPAT_path=`which cpat.py 2>/dev/null`
slncky_path=`which slncky.v1.0 2>/dev/null`

# Add paths to tools.groovy
echo "adding paths to tools.groovy"
echo "// Path to tools used by the pipeline" > ./tools.groovy
echo "Activate=\"$Activate_path\"" >> ./tools.groovy
echo "bpipe=\"$bpipe_path\"" >> ./tools.groovy
echo "hisat2=\"$hisat2_path\"" >> ./tools.groovy
echo "stringtie=\"$stringtie_path\"" >> ./tools.groovy
echo "gffread=\"$gffread_path\"" >> ./tools.groovy
echo "gffcompare=\"$gffcompare_path\"" >> ./tools.groovy
echo "samtools=\"$samtools_path\"" >> ./tools.groovy
echo "hmmpress=\"$hmmpress_path\"" >> ./tools.groovy
echo "pfamscan=\"$pfamscan_path\"" >> ./tools.groovy
echo "transeq=\"$transeq_path\"" >> ./tools.groovy
echo "bowtie2=\"$bowtie2_path\"" >> ./tools.groovy
echo "bamToFastq=\"$bamToFastq_path\"" >> ./tools.groovy
echo "bedtools=\"$bedtools_path\"" >> ./tools.groovy
echo "fastp=\"$fastp_path\"" >> ./tools.groovy
echo "featureCounts=\"$featureCounts_path\"" >> ./tools.groovy
echo "seqtk=\"$seqtk_path\"" >> ./tools.groovy
echo "python3=\"$python3_path\"" >> ./tools.groovy
echo "Rscript=\"$Rscript_path\"" >> ./tools.groovy
echo "" >> ./tools.groovy
echo "// Path to PLEK Optional" >> ./tools.groovy
echo "PLEK=\"$PLEK_path\"" >> ./tools.groovy
echo "PLEKModelling=\"$PLEKModelling_path\"" >> ./tools.groovy
echo ""	>> ./tools.groovy
echo "// Path to rnasamba" >> ./tools.groovy
echo "rnasamba=\"$rnasamba_path\"" >> ./tools.groovy
echo ""	>> ./tools.groovy
echo "// Path to FEELnc env and tools used by the pipeline" >> ./tools.groovy
echo "perl=\"$perl_path\"" >> ./tools.groovy
echo "FEELnc_filter=\"$FEELnc_filter_path\"" >> ./tools.groovy
echo "FEELnc_codpot=\"$FEELnc_codpot_path\"" >> ./tools.groovy
echo "FEELnc_classifier=\"$FEELnc_classifier_path\"" >> ./tools.groovy
echo ""	>> ./tools.groovy
echo "// Path to python 2.7, CPC2, CPAT and slncky" >> ./tools.groovy
echo "python2=\"$python2_path\"" >> ./tools.groovy
echo "cpc2=\"$cpc2_path\"" >> ./tools.groovy
echo "make_hexamer=\"$make_hexamer_path\"" >> ./tools.groovy
echo "make_logit_model=\"$logit_model_path\"" >> ./tools.groovy
echo "CPAT=\"$CPAT_path\"" >> ./tools.groovy
echo "slncky=\"$slncky_path\"" >> ./tools.groovy

echo "Installation complete !!"
