# lncPipe
Pipeline for identification of lncRNAs and Novel Protein Coding Transcripts (NPCT)

# Introduction
lncPipe can be used to identify lncRNAs and Novel Protein Coding Transcripts (NPCT) with large number of RNA-seq datasets, it contains genome guided assembly, annotation compare, classcode selection and final retrival of transcripts in fasta format. The putative lncRNAs and NPCTs can be further tested for their coding potentials with CPC2 or CNCI to obtain CPC score, based on CPC score lncRNAs and NPCTs will be selected. For NPCTs one can go for TransDecoder followed by Pfamscan to retrive protein family annotations.

<p align="center">
  <img src="https://github.com/nikhilshinde0909/lncPipe/blob/main/lncPipe.png" width=50% height=25%>
</p>


# Implementation
1. To execute the steps in pipeline, download latest release of lncPipe to your local system with following commamnd 
```
git clone https://github.com/nikhilshinde0909/lncPipe.git
```

2. Download and install latest release of Mambaforge from github [https://github.com/conda-forge/miniforge] to install required softwares and tools.


3. Once the Mambaforge is installed, Install the requrired softwares in base environment with following command 
```
mamba install -c bioconda stringtie samtools hisat2 gffread gffcompare fastp pblat bpipe bedtools seqtk
```
or \
One can create and activate lncPipe environment from lncPipe.yml file as follows
```
mamba env create -f lncPipe.yml
mamba activate lncPipe
```

4. Create conda environment for FEELnc with following commmand 
```
mamba create -p ~/FEELnc -c bioconda feelnc 
```
  
5. Create environment CPC2, CPAT and snlcky from environment file 
```
mamba env create -f cpc2-cpat-slncky.yml
```

6. Add the path of conda environments and installed softwares in the file named tools.groovy

 
7. Inputs
Create directory to store inputs 
```
mkdir data 
```  
Copy your RNA-seq reads (.fastq.gz), rRNA sequences (.fa), Reference genome (.fa) and Annotations (.gtf) in data directory; create file data.txt in the same by using data_template.txt and add paths for raw fastq.gz, rRNA sequences, reference genome and annotations in the same
 
  
8. Pipeline is ready for executaion \
Run following command and execute the steps for lncRNAs and NPCTs analysis 
```
bpipe run -n ${threads} ~/Path_to_lncPipe/Main.groovy data/data.txt
```
## Thanks for using lncPipe !!
