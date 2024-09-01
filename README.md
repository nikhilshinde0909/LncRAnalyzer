# LncRAnalyzer
Pipeline for identification of lncRNAs and Novel Protein Coding Transcripts (NPCT)

# Introduction
LncRAnalyzer can be used to identify lncRNAs and Novel Protein Coding Transcripts (NPCT) with large number of RNA-seq datasets, it contains genome guided assembly, merge annotattions, annotation compare, classcode selection and final retrival of transcripts in fasta format. The putative lncRNAs and NPCTs will be further tested for their coding potentials with CPC2,CPAT, PLEK (Time consuming), LGC, and RNAsamba. Based on coding potentials lncRNAs and NPCTs will be selected. Additionally, if someone have Lifover files for the organism and related species; conservation analysis will be also performed with slncky. We integreated FEELnc plugin to detect the mRNA spliced and intergenic lncRNAs in RNA-seq samples. For NPCTs one can go for TransDecoder followed by Pfamscan to retrive protein family annotations. Pipeline will be executed in conda environment.

<p align="center">
  <img src="https://github.com/nikhilshinde0909/LncRAnalyzer/blob/main/scripts/LncRAnalyzer.png" width=50% height=25%>
</p>


# Implementation
1. To execute the steps in pipeline, download latest release of LncRAnalyzer to your local system with following commamnd 
```
git clone https://github.com/nikhilshinde0909/LncRAnalyzer.git
```

2. Download and install latest release of Mambaforge from github [https://github.com/conda-forge/miniforge] to install required softwares and tools.


3. Once the Mambaforge is installed, Install the requrired softwares by updating base environment from LncRAnalyzer.yml file as follows
```
mamba env update --file LncRAnalyzer.yml
```

4. Create conda environment for FEELnc with following commmand 
```
mamba create -p ~/FEELnc -c bioconda feelnc 
```
  
5. Create environment CPC2, CPAT and snlcky from environment file 
```
mamba env create -f cpc2-cpat-slncky.yml
```

6.Create environment for RNAsamba
```
mamba env create -f rnasamba.yml
```

7. Run bash script in directory to add the path of conda environments and installed softwares in the file named tools.groovy
```
bash
```
9. Inputs
Create directory to store inputs 
```
mkdir data 
```  
Copy your RNA-seq reads (.fastq.gz), rRNA sequences (.fa), Reference genomes (.fa), rel sp. reference genome (.fa), Annotations (.gtf) and Lifover files in data directory; create file data.txt in the same by using data_template.txt and add paths for raw fastq.gz, rRNA sequences, reference genome, rel sp. reference genome, annotations and lifover files in the same \
If you don't have reference genome, annotations and rRNA sequence information; you can download the same with script provided with pipeline as follows
```
python check_ensembl.py org_name
eg. python find_species_in_ensembl.py Sorghum
> sbicolor
python ensembl.py org_name_in_ensembl
eg. python download_datasets_ensembl.py sbicolor
> Ensembl version 56 <- download the datasets
```

9. Pipeline is ready for executaion \
Run following command and execute the steps for lncRNAs and NPCTs analysis 
```
bpipe run -n ${threads} ~/Path_to_LncRAnalyzer/Main.groovy data/data.txt
```
## Thanks for using LncRAnalyzer !!

## Peformace
The performance of coding potential prediction using CPAT, CPC2, RNAsamba and FEELnc was estimated with 50 RNA-Seq accessions of sorghum cultivar PR22 from past studies [https://doi.org/10.1186/s12864-019-5734-x] 

<p align="center">
  <img src="https://github.com/nikhilshinde0909/LncRAnalyzer/blob/main/scripts/ROC.png" width=70% height=70%>
</p>
