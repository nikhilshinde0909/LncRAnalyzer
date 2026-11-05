/****************************************************************
 ** Stages to retieve quality trimming and removing rRNA reads
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 ****************************************************************/

//Output directory
unmapped_reads_dir="unmapped_reads"

//User specified read alignment to rRNAs

build_rRNA_index = {
	output.dir=unmapped_reads_dir
	produce("rRNA.1.ht2"){
	  exec "${hisat2}-build $rRNAs $output.prefix.prefix"
        }
}

map_reads_to_rRNAs_and_unmapped_bam = {
    def input_reads_option = ""
    def bam_options = ""
	
    if (reads_R2 == "") {
        input_reads_option = "-U "+fastp_dir+"/"+branch.name+".fastq.gz"
        bam_options = unmapped_bam_options
    } else {
        input_reads_option = "-1 "+fastp_dir+"/"+branch.name+"_1.fastq.gz"+" -2 "+fastp_dir+"/"+branch.name+"_2.fastq.gz"
        bam_options = unmapped_bam_paired_options
    }
    
    doc "Aligning reads to rRNAs using HISAT2"
    output.dir = unmapped_reads_dir
    
    produce(branch.name+".rRNA.u.bam", branch.name+".rRNA.summary") {
        exec """
        $hisat2 --summary-file $output2 -x $input.ht2.prefix.prefix $input_reads_option |
        $samtools view $bam_options -Su - |
        $samtools sort -n - -o $output1
        """
    }
}

unmapped_reads = {
        output.dir=unmapped_reads_dir
        if(reads_R2=="")
       	from(branch.name+".rRNA.u.bam") produce(branch.name+".fastq"){
        exec """
        $bamToFastq -i $input -fq $output && rm $input      
        """
	}
	else
	from(branch.name+".rRNA.u.bam") produce(branch.name+"_1.fastq",branch.name+"_2.fastq"){
        exec """
        $bamToFastq -i $input -fq $output1 -fq2 $output2 && rm $input
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
    exec "gzip -f $input_gzip_options"
}

unmapped_reads_to_rRNAs = segment { build_rRNA_index + fastqInputFormat * [ map_reads_to_rRNAs_and_unmapped_bam + 
                        unmapped_reads + gzip_reads ] }
