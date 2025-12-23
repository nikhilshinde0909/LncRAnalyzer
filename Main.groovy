#!/usr/bin/env bpipe

/***********************************************************
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 29/05/2023
 *********************************************************/

VERSION="1.00"
def printHelp() {
    println "Usage: bpipe run Main.groovy [options]"
    println "Options:"
    println "  < config_file >       Specify the config file eg. data.groovy"
    println "  --supporting_species  Show supporting species for LncRNAlyzer"
    println "  --help                Show this help message."
    println "  --version             Show version of pipeline."
    System.exit(0)
}

if ('--help' in args) {
    printHelp()
}

if ('--version' in args) {
    println "Version: ${VERSION} "
    System.exit(0) 
}


def supporting_species() {
    println "Plants	               Vertebrates"
    println "Arabidopsis_thaliana      Homo_sapiens"
    println "Oryza_sativa              Mus_musculus"
    println "Hordeum_vulgare	       Danio_rerio"
    println "Triticum_aestivum	       Drosophila_melanogaster"
    println "Zea_mays	               Heterocephalus_glaber"
    println "Sorghum_bicolor	       Gallus_gallus"
    println "Nicotina_tabacum	       Canis_lupus_familiaris"
    println "Pisum_sativum             Gorilla_gorilla"
    println "Glycine_max               Naja_naja"
    println "Gossypium_barbadense      Macaca_mulatta"
    println "Cicer_arietinum           Equus_caballus"
    println "Malus_domestica	       Bos_taurus"
    println "Brassica_napus            Ovis_aries"
    println "Citrus_sinesis            Sus_scrofa"
    println "Jatropha_curcas           Salmo_salar"
    println "Capsicum_annum            Pan_troglodytes"
    println "Brachypodium_distachyon   Capra_hircus"
    println "Coffea_arabica            Pongo_abelii"
    println "Musa_acuminata            Rattus_norvegicus"
    println "Vitis_vinifera"
    println "Actinidia_chinensis"
    println "Gossypium_raimondii"
    println "Amborella_trichopoda"
    println "Ananas_comosus"
    println "Aquilegia_coerulea"
    println "Arabidopsis_lyrata"
    println "Arachis_ipaensis"
    println "Brassica_rapa"
    println "Capsella_rubella"
    println "Carica_papaya"
    println "Chlamydomonas_reinhardtii"
    println "Citrus_clementina"
    println "Citrus_maxima"
    println "Cucumis_sativus"
    println "Daucus_carota"
    println "Durio_zibethinus"
    println "Elaeis_guineensis"
    println "Erythranthe_guttata"
    println "Eucalyptus_grandis"
    println "Eutrema_salsugineum"
    println "Fragaria_vesca"
    System.exit(0)
}

if ('--supporting_species' in args) {
    supporting_species()
}

if (args == null) {
    println "No config file provided"
    printHelp()
    System.exit(1)
} else {
    println "LncRAnalyzer configuration file found"
    load args[0]
}

//option strings to pass to tools
hisat2_options="--mp 2"
stringtie_options="-m 200 -a 10 --conservative -g 50 -u -c 3"
stringtie_merge_options="-m 200 -c 3 -T 1"
gffread_options="-l 200 -U -T"
unmapped_bam_options="-f 4"
unmapped_bam_paired_options="-f 12"
shuffle_filter_options="--biotype=transcript_biotype=protein_coding --monoex=-1"
shuffle_codpot_options="--mode=shuffle"
intergenic_filter_options="--monoex=0 --size=200"
intergenic_codpot_options="--mode=intergenic"
featurecounts_option1="--primary -t exon -g gene_id -F GTF"
featurecounts_option2="--primary -t exon -g transcript_id -F GTF"
slncky_ortho_options="--no_filter --minMatch=0.01 --no_orf --pad=100000"
slncky_options=""
CPAT_options=""

// Input options
fastqFormatPaired="%_*.fastq.gz"
fastqFormatSingle="%.fastq.gz"

fastqInputFormat=fastqFormatPaired
if(reads_R2=="") fastqInputFormat=fastqFormatSingle

codeBase = file(bpipe.Config.config.script).parentFile.absolutePath
lgc = codeBase + "/utils/lgc-1.0.py"
npcts_upset_script = codeBase + "/scripts/NPCTs-Upset.R"
lnc_upset_script = codeBase + "/scripts/Lnc-Upset.R"
lnc_intersect_script = codeBase + "/scripts/Lnc-Intersect.R"
npct_intersect_script = codeBase + "/scripts/NPCTs-Intersect.R"
lnc_roc_script = codeBase + "/scripts/ROC_curve.R"
subset_gtf = codeBase + "/scripts/subset_gtf.py"
fold10_crossval = codeBase + "/scripts/10fold_crossval.R"
train_lncfinder=codeBase + "/scripts/Train_LncFinder.R"
run_lncfinder=codeBase + "/scripts/Run_LncFinder.R"
summary_clasification = codeBase + "/scripts/get_lncRNA_classes.R"
download_TE = codeBase + "/scripts/download_TE.py"
format_gff_to_bed = codeBase + "/scripts/format_TE_gff3tobed.py"
get_TE_derived_lncRNA = codeBase + "/scripts/get_TE_derived_lncRNAs.R"
DESeq2 = codeBase + "/scripts/DESeq2.R"

load codeBase+"/tools.groovy"
load codeBase+"/stages/fastp.groovy"
load codeBase+"/stages/rRNA_unmapped.groovy"
load codeBase+"/stages/align_assembly.groovy"
load codeBase+"/stages/merge_and_compare_annotations.groovy"
load codeBase+"/stages/lnc_npc_transcript_filter.groovy"
load codeBase+"/stages/CPC2.groovy"
load codeBase+"/stages/CPAT.groovy"
load codeBase+"/stages/LGC.groovy"
load codeBase+"/stages/pfamscan.groovy"
load codeBase+"/stages/lncfinder.groovy"
load codeBase+"/stages/slncky.groovy"
load codeBase+"/stages/PLEK.groovy"
load codeBase+"/stages/rnasamba.groovy"
load codeBase+"/stages/FEELnc_shuffle.groovy"
load codeBase+"/stages/FEELnc_intergenic.groovy"
load codeBase+"/stages/summary.groovy"
load codeBase+"/stages/TE_derived_lncRNA.groovy"
load codeBase+"/stages/lnc_ROC.groovy"
load codeBase+"/stages/counts.groovy"
load codeBase+"/stages/DESeq2.groovy"
/******************* Here are the pipeline stages **********************/

set_input = {
   def files=reads_R1.split(",")
   if(reads_R2!="") files+=reads_R2.split(",")
   forward files
}

run_check = {
    doc "check that the data files exist"
    produce("checks_passed") {
        exec """
            echo "Running lnc RNA analysis pipeline version $VERSION" ;
	    echo "Using ${bpipe.Config.config.maxThreads} threads" ;
            echo "Checking for the data files..." ;
	    for i in $rRNAs $genome $annotation $inputs.fastq.gz ; 
                 do ls $i 2>/dev/null || { echo "CAN'T FIND ${i}..." ;
		 echo "PLEASE FIX PATH... STOPPING NOW" ; exit 1  ; } ; 
	    done ;
            echo "All looking good" ;
            echo "Running lnc RNA analysis pipeline version $VERSION.. checks passed" > $output
        ""","checks"
    }
}

nthreads=bpipe.Config.config.maxThreads

run { set_input + run_check + 
	quality_trimming.using(threads: nthreads) +
	unmapped_reads_to_rRNAs.using(threads: nthreads) +
	genome_guided_assembly +
	annotation_compare.using(threads: nthreads) +
	lnc_npc_transcript_selection.using(threads: nthreads) +
	cpat_based_coding_potentials +
	coding_potential_calculations + 
	execute_pfamscan +
	lgc_based_coding_potentials +
	lncfinder_train_and_classify.using(threads: nthreads) +
	rnasamba_train_and_classify +
	//plek_based_coding_potentials.using(threads: nthreads) +
	shuffle.using(threads: nthreads) +
	intergenic.using(threads: nthreads) +
	LncRAnalyzer_summary + 
	slncky_run.using(threads: nthreads) +
	TE_derived_lncRNAs +
	LncRAnalyzer_ROC +
	get_counts +
    DGE_analysis
}
