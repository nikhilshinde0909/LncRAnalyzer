// sequencing data
reads_R1="data/SRR975551_1.fastq.gz,data/SRR975552_1.fastq.gz,data/SRR975553_1.fastq.gz,data/SRR975554_1.fastq.gz,data/SRR975555_1.fastq.gz,data/SRR975556_1.fastq.gz,data/SRR975557_1.fastq.gz,data/SRR975559_1.fastq.gz,data/SRR975560_1.fastq.gz,data/SRR975561_1.fastq.gz,data/SRR975563_1.fastq.gz,data/SRR975564_1.fastq.gz,data/SRR975565_1.fastq.gz,data/SRR975566_1.fastq.gz,data/SRR975567_1.fastq.gz,data/SRR975568_1.fastq.gz"

reads_R2="data/SRR975551_2.fastq.gz,data/SRR975552_2.fastq.gz,data/SRR975553_2.fastq.gz,data/SRR975554_2.fastq.gz,data/SRR975555_2.fastq.gz,data/SRR975556_2.fastq.gz,data/SRR975557_2.fastq.gz,data/SRR975559_2.fastq.gz,data/SRR975560_2.fastq.gz,data/SRR975561_2.fastq.gz,data/SRR975563_2.fastq.gz,data/SRR975564_2.fastq.gz,data/SRR975565_2.fastq.gz,data/SRR975566_2.fastq.gz,data/SRR975567_2.fastq.gz,data/SRR975568_2.fastq.gz"

// rRNA sequences
rRNAs="data/hg38.rRNA.fasta"

// Organism name
org_name="Homo_sapiens" 

// The genome and annotation
genome="data/hg38.fa"
annotation="data/hg38.pc.gtf"
liftover="hg38tomm39.over.chain.gz"
noncoding="data/hg38.nc.bed"
mir="data/hg38.mir.bed"
sno="data/hg38.sno.bed"
known_lncRNAs_FA="data/hg38.lncpedia.fa"  // Optional, In case the organism is not listed in supporting species

// related species name
rel_sp_name="Mus_musculus"

//The genome and annotation of a related species
genome_related_species="data/mm39.fa"
annotation_related_species="data/mm39.pc.gtf"
rel_liftover=""
rel_noncoding="data/mm39.nc.bed"
rel_mir="data/mm39.mir.bed"
rel_sno="data/mm39.sno.bed"

// Experimental design file 
design="data/design.TSV" // Optional, Provide tab-separated exp design file with columns Run and Conditions
