# LncRAnalyzer
A pipeline for lncRNAs and Novel Protein Coding Transcripts (NPCTs) identification using RNA-Seq

# Introduction
# LncRAnalyzer
A pipeline for lncRNAs and Novel Protein Coding Transcripts (NPCTs) identification using RNA-Seq

# Introduction
LncRAnalyzer is a comprehensive workflow to identify lncRNAs and Novel Protein Coding Transcripts (NPCT) using RNA-Seq. The pipeline contains several steps including quality control, read alignment to reference genome, reference-guided transcript assembly, merge annotations, annotation comparison, class code selection, and retrieval of transcripts in FASTA format. The putative class code selected transcripts will be further evaluated for their coding potentials, features, and protein domain homologies using CPC2, CPAT, PLEK (Time-consuming), RNAsamba, LncFinder, LGC, and PfamScan. The final lncRNAs and NPCTs will be selected based on coding potentials, features, and protein domain homologies. Additionally, if LiftOver files for the organism and related species is provided; this pipeline also performs cross-species lncRNA conservation analysis using Slncky. We also integrated the FEELnc plugin to report the mRNA spliced and intergenic lncRNAs in given RNA-seq samples. For NPCTs, further functional annotations is needed which includes peptide sequences prediction using TransDecoder followed by homology searches using Pfamscan, BLASTP, and BLASTX. The entire workflow is automated using Bpipe and could be implemented in multiple working environment such as Conda, Docker and Singularity.

<p align="center">
  <img src="https://gitlab.com/nikhilshinde0909/LncRAnalyzer/raw/main/scripts/LncRAnalyzer.png" width=50% height=25%>
</p>


# Implementation

## Conda environment
1. To execute the steps in the pipeline, download the latest release of LncRAnalyzer to your local system with the following command 
```
git clone https://gitlab.com/nikhilshinde0909/LncRAnalyzer.git
```

2. Change directory to LncRAnalyzer.
```
cd LncRAnalyzer
```

3. Install latest Miniforge and required software tools as follows
```
bash install_pipeline_tools.sh
```
This will generate tools.groovy with configured paths for tools

4. Pipeline is ready to execute, prepare your inputs and data.groovy in the working directory

```
Working directory
mkdir data
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
└── data.groovy 
```  
Copy your RNA-seq reads (\*.fastq.gz), rRNA sequences (\*.fa), reference genomes (\*.fa), related sp. reference genome (\*.fa), annotations (\*.gtf) and liftover files in data directory; create file data.txt in the same by using data_template.txt and add paths for raw fastq.gz, rRNA sequences, reference genome, rel sp. reference genome, annotations and liftover files in the same. \
Note: Please refer data.groovy template in LncRAnalyzer dir

5. If you don't have a reference genome, annotations, and rRNA sequence information; you can download the same with the script provided with the pipeline as follows
```
python check_ensembl.py org_name
eg. python find_species_in_ensembl.py Sorghum
> sbicolor
python ensembl.py org_name_in_ensembl
eg. python download_datasets_ensembl.py sbicolor
> Ensembl version 56 <- download the datasets
```

6. Similarly, if you don't have liftover files for conservation analysis then you can generate it through genome alignments of reference and query species genomes as follows
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

7. The pipeline is ready for execution \
Run the following command to print help
```
bpipe run ~/Path_to_LncRAnalyzer/Main.groovy --help
```

8. Search for supporting species and configure it in the data.groovy
```
bpipe run ~/Path_to_LncRAnalyzer/Main.groovy --supporting_species
```

9. Execute pipeline with RNA-seq datasets as follows
```
bpipe run -n ${threads} ~/Path_to_LncRAnalyzer/Main.groovy data.groovy
```

## Docker environment
1. Build a docker image from the docker file
```
docker build -t nikhilshinde0909/lncranalyzer .
```

2. Run the following commands and check LncRAnalyzer and tools.groovy has been created and configured with the proper paths 
```
docker run --rm -it nikhilshinde0909/lncranalyzer bash
cd LncRAnalyzer/
cat tools.groovy
exit
```

3. Prepare data and data.groovy in your working directory
```
Working directory
mkdir data
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
└── data.groovy
```

4. Download the required reference genomes and annotations, and then prepare the Slncky annotations and LiftOver files as outlined in the Conda environment section (steps 5 and 6).

5. Run the LncRAnalyzer using docker in your working directory as follows
```
docker run \
    -v $(pwd)/data:/pipeline/data \
    -v $(pwd)/data.groovy:/pipeline/data.groovy \
    nikhilshinde0909/lncranalyzer bpipe run -n 16 /pipeline/LncRAnalyzer/Main.groovy /pipeline/data.groovy
```

6. Export your results to local as follows
```
# list containers
docker ps -a

# Copy data
docker cp container_id:/pipeline ${path to copy resuls}
```

Note: Please refer to the LncRAnalyzer documentation to work with pre-built LncRAnalyzer Docker images.

## Singularity environment
1. Build a Singularity image from the Singularity file
```
sudo singularity build lncranalyzer.sif Singularity
```

2. Run the following commands and check LncRAnalyzer and tools.groovy has been created and configured with the proper paths 
```
singularity exec lncranalyzer.sif bash
cd /pipeline/LncRAnalyzer/
cat tools.groovy
exit
```

3. Prepare data and data.groovy in your working directory
```
Working directory
mkdir data
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
└── data.groovy
```

4. Download the required reference genomes and annotations, and then prepare the Slncky annotations and LiftOver files as outlined in the Conda environment section (steps 5 and 6).

5. Run the LncRAnalyzer using singularity in your working directory as follows
```
singularity exec -B $(pwd)/data:/pipeline/data \
  -B $(pwd)/data.groovy:/pipeline/data.groovy \
  ~/LncRAnalyzer/lncranalyzer.sif \
  bpipe run -n 16 /pipeline/LncRAnalyzer/Main.groovy /pipeline/data.groovy
```
6. Enjoy your results in the working directory

Note: Please refer to the LncRAnalyzer documentation to work with pre-built LncRAnalyzer Singularity images.

## Thanks for using LncRAnalyzer

# Peformace
The performance of coding potential prediction using CPAT, CPC2, LGC, RNAsamba, and FEELnc was estimated with 50 RNA-Seq accessions of sorghum cultivar PR22 from past studies [https://doi.org/10.1186/s12864-019-5734-x] 

<p align="center">
  <img src="https://gitlab.com/nikhilshinde0909/LncRAnalyzer/raw/main/scripts/ROC.png" width=70% height=70%>
</p>

