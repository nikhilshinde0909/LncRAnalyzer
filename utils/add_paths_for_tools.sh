#!/bin/bash
# get paths
# Check for existing Mambaforge installation
if [[ -d "/opt/miniforge" ]]; then
    Activate_path="/opt/miniforge/bin/activate"
    else
    echo "No miniforge installation detected"
    exit 1
fi
source $Activate_path
bpipe_path=$(which bpipe 2>/dev/null)
hisat2_path=$(which hisat2 2>/dev/null)
stringtie_path=$(which stringtie 2>/dev/null)
gffread_path=$(which gffread 2>/dev/null)
gffcompare_path=$(which gffcompare 2>/dev/null)
samtools_path=$(which samtools 2>/dev/null)
hmmpress_path=$(which hmmpress 2>/dev/null)
pfamscan_path=$(which hmmscan 2>/dev/null)
transeq_path=$(which transeq 2>/dev/null)
bowtie2_path=$(which bowtie2 2>/dev/null)
bamToFastq_path=$(which bamToFastq 2>/dev/null)
fastp_path=$(which fastp 2>/dev/null)
featureCounts_path=$(which featureCounts 2>/dev/null)
seqtk_path=$(which seqtk 2>/dev/null)
python3_path=$(which python3 2>/dev/null)
Rscript_path=$(which Rscript 2>/dev/null)
PLEK_path=$(which PLEK.py 2>/dev/null)
PLEKModelling_path=$(which PLEKModelling.py 2>/dev/null)
bedtools_path=$(which bedtools 2>/dev/null)

source $Activate_path rnasamba
rnasamba_path=$(which rnasamba 2>/dev/null)

source $Activate_path FEELnc
perl_path=$(which perl 2>/dev/null)
FEELnc_filter_path=$(which FEELnc_filter.pl 2>/dev/null)
FEELnc_codpot_path=$(which FEELnc_codpot.pl 2>/dev/null)
FEELnc_classifier_path=$(which FEELnc_classifier.pl 2>/dev/null)

source $Activate_path cpc2-cpat-slncky
python2_path=$(which python 2>/dev/null)
cpc2_path=$(which CPC2.py 2>/dev/null)
make_hexamer_path=$(which make_hexamer_tab.py 2>/dev/null)
logit_model_path=$(which make_logitModel.py 2>/dev/null)
CPAT_path=$(which cpat.py 2>/dev/null)
slncky_path=$(which slncky.v1.0 2>/dev/null)

# Add paths to tools.groovy
echo "// Path to tools used by the pipeline"
echo "Activate=\"$Activate_path\"" 
echo "bpipe=\"$bpipe_path\"" 
echo "hisat2=\"$hisat2_path\"" 
echo "stringtie=\"$stringtie_path\"" 
echo "gffread=\"$gffread_path\"" 
echo "gffcompare=\"$gffcompare_path\"" 
echo "samtools=\"$samtools_path\"" 
echo "hmmpress=\"$hmmpress_path\"" 
echo "pfamscan=\"$pfamscan_path\"" 
echo "transeq=\"$transeq_path\"" 
echo "bowtie2=\"$bowtie2_path\"" 
echo "bamToFastq=\"$bamToFastq_path\"" 
echo "bedtools=\"$bedtools_path\"" 
echo "fastp=\"$fastp_path\"" 
echo "featureCounts=\"$featureCounts_path\""
echo "seqtk=\"$seqtk_path\"" 
echo "python3=\"$python3_path\"" 
echo "Rscript=\"$Rscript_path\"" 
echo "" 
echo "// Path to PLEK Optional" 
echo "PLEK=\"$PLEK_path\"" 
echo "PLEKModelling=\"$PLEKModelling_path\"" 
echo ""	
echo "// Path to rnasamba" 
echo "rnasamba=\"$rnasamba_path\"" 
echo ""	
echo "// Path to FEELnc env and tools used by the pipeline" 
echo "perl=\"$perl_path\"" 
echo "FEELnc_filter=\"$FEELnc_filter_path\"" 
echo "FEELnc_codpot=\"$FEELnc_codpot_path\"" 
echo "FEELnc_classifier=\"$FEELnc_classifier_path\"" 
echo ""	
echo "// Path to python 2.7, CPC2, CPAT and slncky" 
echo "python2=\"$python2_path\"" 
echo "cpc2=\"$cpc2_path\"" 
echo "make_hexamer=\"$make_hexamer_path\"" 
echo "make_logit_model=\"$logit_model_path\"" 
echo "CPAT=\"$CPAT_path\"" 
echo "slncky=\"$slncky_path\"" 
