# LncRAnalyzer
Pipeline for identification of lncRNAs and Novel Protein Coding Transcripts (NPCTs)

# Introduction
LncRAnalyzer can be used to identify lncRNAs and Novel Protein Coding Transcripts (NPCT) with a large number of RNA-seq datasets, it contains genome-guided assembly, merge annotations, annotation compare, class code selection, and final retrieval of transcripts in fasta format. The putative lncRNAs and NPCTs will be further tested for their coding potentials with CPC2, CPAT, PLEK (Time-consuming), LGC, and RNAsamba. Based on coding potentials lncRNAs and NPCTs will be selected. Additionally, if someone has Lifover files for the organism and related species; conservation analysis will be also performed with Slncky. We integrated the FEELnc plugin to detect the mRNA spliced and intergenic lncRNAs in RNA-seq samples. For NPCTs, one can go for TransDecoder followed by Pfamscan to retrieve protein family annotations. The pipeline will be executed in a conda environment.

<p align="center">
  <img src="https://github.com/nikhilshinde0909/LncRAnalyzer/blob/main/scripts/LncRAnalyzer.png" width=50% height=25%>
</p>


# Implementation
1. To execute the steps in the pipeline, download the latest release of LncRAnalyzer to your local system with the following command 
```
git clone https://github.com/nikhilshinde0909/LncRAnalyzer.git
```

2. Download and install the latest release of Mambaforge from github [https://github.com/conda-forge/miniforge] to install the required software and tools.


3. Once the Mambaforge is installed, Install the required software by updating the base environment from the LncRAnalyzer.yml file as follows
```
mamba env update --file LncRAnalyzer.yml
```

4. Create a conda environment for FEELnc with the following command 
```
mamba create -n FEELnc -c bioconda feelnc 
```
  
5. Create the environment for CPC2, CPAT, and Slncky from environment file 
```
mamba env create -f cpc2-cpat-slncky.yml
```

6. Create the conda environment for RNAsamba
```
mamba env create -f rnasamba.yml
```

7. Run bash script named "add_paths_for_tools.sh" to add the path of conda environments and software in tools.groovy file
```
chmod +x add_paths_for_tools.sh && bash add_paths_for_tools.sh
```
9. Prepare your inputs and data.txt in the working directory
```
mkdir data
Working directory
├── data
│   ├── SRR975551_1.fastq.gz
│   ├── SRR975552_1.fastq.gz
│   └── (and other fastq.gz files)
│   ├── SRR975551_2.fastq.gz
│   ├── SRR975552_2.fastq.gz
│   └── (and other fastq.gz files)
│   └── hg38.rRNA.fasta
|   └── hg38.genome.fasta
|   └── hg38.annotation.gtf
|   └── (and other files)
└── data.txt 
```  
Copy your RNA-seq reads (\*.fastq.gz), rRNA sequences (\*.fa), reference genomes (\*.fa), related sp. reference genome (\*.fa), annotations (\*.gtf) and liftover files in data directory; create file data.txt in the same by using data_template.txt and add paths for raw fastq.gz, rRNA sequences, reference genome, rel sp. reference genome, annotations and liftover files in the same \
If you don't have a reference genome, annotations, and rRNA sequence information; you can download the same with the script provided with the pipeline as follows
```
python check_ensembl.py org_name
eg. python find_species_in_ensembl.py Sorghum
> sbicolor
python ensembl.py org_name_in_ensembl
eg. python download_datasets_ensembl.py sbicolor
> Ensembl version 56 <- download the datasets
```
Similarly, if you don't have liftover files for conservation analysis then you can generate it through genome alignments of reference and query species genomes as follows
```
python Liftover.py <threads> <genome> <org_name> <genome_related_species> <rel_sp_name> <params_distance>
eg.
python Liftover.py 16 Sorghum_bicolor.dna.toplevel.fa Sbicolor Zea_mays.dna.toplevel.fa Zmays near
```
We also provide an additional script which will take ensembl gtf and produce bed files to run Slncky as follows
```
python ensembl_gtf2bed.py <ensembl_gtf> <output_prefix>
eg.
python ensembl_gtf2bed.py Sorghum_bicolor.58.gtf Sorghum_bicolor
```
This will produce protein-coding, non-coding, mirRNA, and snoRNA bed files for Slncky. 
9. Pipeline is ready for execution \
Run the following command and execute the steps for lncRNAs and NPCTs analysis 
```
bpipe run -n ${threads} ~/Path_to_LncRAnalyzer/Main.groovy data/data.txt
```

Note: If the pipeline reports a "core-dumped" error for PfamScan then replace your existing hmmer installation with hmmer=3.1b1 using the script in the utils directory as follows
```
bash install_hmmer3.1.sh
```

## Thanks for using LncRAnalyzer !!

## Peformace
The performance of coding potential prediction using CPAT, CPC2, LGC, RNAsamba, and FEELnc was estimated with 50 RNA-Seq accessions of sorghum cultivar PR22 from past studies [https://doi.org/10.1186/s12864-019-5734-x] 

<p align="center">
  <img src="https://github.com/nikhilshinde0909/LncRAnalyzer/blob/main/scripts/ROC.png" width=70% height=70%>
</p>
