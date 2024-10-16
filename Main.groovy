/***********************************************************
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 29/05/2023
 *********************************************************/

VERSION="1.00"

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
CPAT_options=""
slncky_options=""
slncky_ortho_options="--no_filter --minMatch=0.01 --no_orf --pad=100000"

// Input options
fastqFormatPaired="%_*.fastq.gz"
fastqFormatSingle="%.fastq.gz"

load args[0]

fastqInputFormat=fastqFormatPaired
if(reads_R2=="") fastqInputFormat=fastqFormatSingle

codeBase = file(bpipe.Config.config.script).parentFile.absolutePath
lgc = codeBase + "/utils/lgc-1.0.py"
slncky = codeBase + "/slncky/slncky.v1.0"
npcts_venn_script = codeBase + "/scripts/NPCTs-Venn.R"
lnc_venn_script = codeBase + "/scripts/Lnc-Venn.R"
lnc_intersect_script = codeBase + "/scripts/Lnc-Intersect.R"
npct_intersect_script = codeBase + "/scripts/NPCTs-Intersect.R"
lnc_roc_script = codeBase + "/scripts/ROC_curve.R"
subset_gtf = codeBase + "/scripts/subset_gtf.py"

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
load codeBase+"/stages/slncky.groovy"
load codeBase+"/stages/PLEK.groovy"
load codeBase+"/stages/rnasamba.groovy"
load codeBase+"/stages/FEELnc_shuffle.groovy"
load codeBase+"/stages/FEELnc_intergenic.groovy"
load codeBase+"/stages/summary.groovy"
load codeBase+"/stages/lnc_ROC.groovy"

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
	slncky_run.using(threads: nthreads) +
	//plek_based_coding_potentials.using(threads: nthreads) +
	rnasamba_train_and_classify +
	shuffle.using(threads: nthreads) +
	intergenic.using(threads: nthreads) +
	LncRAnalyzer_summary + 
	LncRAnalyzer_ROC 
}
