#!/bin/bash
# get paths
echo 'getting paths'
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
python3_path=`which python3 2>/dev/null`
Rscript_path=`which Rscript 2>/dev/null`
PLEK_path=`which PLEK.py 2>/dev/null`
PLEKModelling_path=`which PLEKModelling.py 2>/dev/null`

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
slncky_path=`which slncky 2>/dev/null`

# Add paths to tools.groovy
echo 'adding paths to tools.groovy'
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
echo "fastp=\"$fastp_path\"" >> ./tools.groovy
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
echo "logit_model=\"$logit_model_path\"" >> ./tools.groovy
echo "CPAT=\"$CPAT_path\"" >> ./tools.groovy
echo "slncky=\"$slncky_path\"" >> ./tools.groovy
