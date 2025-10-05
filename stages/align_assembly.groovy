/***********************************************************
 ** Stages to preform genome-guided assembly with Stringties
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Output directory
align_and_assembly_dir="Align_and_assembly"

//User specified genome assembly
genome_guided_assembly_file=""

build_genome_index = {
	output.dir=align_and_assembly_dir
	produce("genome.1.ht2"){
	  exec "${hisat2}-build $genome $output.prefix.prefix"
        }
}

gtf_to_splice_sites = {
       output.dir=align_and_assembly_dir
       produce("splicesites.txt"){
          exec "cat $annotation | $python ${hisat2}_extract_splice_sites.py - > $output"
       }
}

map_reads_to_genome = {
        def input_reads_option=""
        if(reads_R2=="")
             input_reads_option = "-U "+unmapped_reads_dir+"/"+branch.name+".fastq.gz"
        else
             input_reads_option = "-1 "+unmapped_reads_dir+"/"+branch.name+"_1.fastq.gz"+" -2 "+unmapped_reads_dir+"/"+branch.name+"_2.fastq.gz"
	doc "Aligning reads to genome using HISAT2"
	output.dir=align_and_assembly_dir
	produce(branch.name+".bam",branch.name+".summary"){
	   exec """
	   $hisat2 $hisat2_options 
	   	     --known-splicesite-infile $input.txt 
	   	     --dta
	             --summary-file $output2
	             -x $input.ht2.prefix.prefix 
		     $input_reads_option |
		     $samtools view -Su - | $samtools sort - -o $output1
	   """
	  }
}

genome_assembly = {
	output.dir=align_and_assembly_dir
	produce(branch.name+".nc.gtf"){
	   if(genome_guided_assembly_file!=""){
	      exec "cp $genome_guided_assembly_file $output"
	   } else {
	      exec "$stringtie $input.bam -o $output $stringtie_options"
	   }
	}
}

genome_guided_assembly = segment {
				build_genome_index +
				gtf_to_splice_sites +
				fastqInputFormat *
				[ map_reads_to_genome +
				genome_assembly ]
				}
