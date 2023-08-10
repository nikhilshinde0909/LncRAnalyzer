/****************************************************************
 ** Stages to retieve quality trimming and removing rRNA reads
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 ****************************************************************/

//Output directory
unmapped_reads_dir="unmapped_reads"
genome_guided_assembly_file=""

//User specified read alignment to rRNAs
build_rRNA_index = {
	output.dir=unmapped_reads_dir
	produce("rRNA.1.ht2"){
	  exec "${hisat2}-build $rRNAs $output.prefix.prefix"
        }
}

map_reads_to_rRNAs = {
        def input_reads_option=""
	if(reads_R2=="")
             input_reads_option = "-U "+ input
        else
             input_reads_option = "-1 "+ input1 + " -2 "+ input2
        doc "Aligning reads to rRNAs using HISAT2"
	output.dir=unmapped_reads_dir
	produce(branch.name+".rRNA.bam",branch.name+".rRNA.summary"){
        exec "$hisat2 $hisat2_options --summary-file $output2 -x $input.ht2.prefix.prefix $input_reads_option | $samtools view -Su - | $samtools sort - -o $output1"
        }
}

unmapped_bam = {
        def bam_options=""
        if(reads_R2="")
		bam_options=unmapped_bam_options
        else
		bam_options=unmapped_bam_paired_options
        output.dir=unmapped_reads_dir
        from(branch.name+".rRNA.bam") produce(branch.name+".u.bam"){
              exec "$samtools view -@ $threads $bam_options $input -o $output && rm -rf $input"
        }
}

qsorted_bam = {
	output.dir=unmapped_reads_dir
	from(branch.name+".u.bam") produce(branch.name+".qsort.bam"){
	      exec "$samtools sort -@ $threads -n -o $output $input && rm -rf $input"
	}
}

unmapped_reads = {
	output.dir=unmapped_reads_dir
	if(reads_R2=="") 
	produce(branch.name+".fastq"){
	exec """
	$bamToFastq -i $input.qsort.bam -fq $output && rm -rf $input.qsort.bam
	"""
	}
	else
	produce(branch.name+"_1.fastq",branch.name+"_2.fastq"){
	exec """
	$bamToFastq -i $input.qsort.bam -fq $output1 -fq2 $output2 && rm -rf $input.qsort.bam
	"""
	}
}

gzip_reads = {
    def input_gzip_options=""
    if (reads_R2 == "") {
        input_gzip_options = unmapped_reads_dir + "/" + branch.name + ".fastq"
    } else {
        input_gzip_options = unmapped_reads_dir + "/" + branch.name + "_1.fastq " + unmapped_reads_dir + "/" + branch.name + "_2.fastq"
    }
    exec "gzip $input_gzip_options"
}

unmapped_reads_to_rRNAs = segment { build_rRNA_index + fastqInputFormat * [ map_reads_to_rRNAs  + 
			unmapped_bam ] + qsorted_bam + 
			fastqInputFormat * [unmapped_reads + gzip_reads] }
