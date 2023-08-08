/****************************************************************
 ** Stages to retieve quality trimming and removing rRNA reads
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 ****************************************************************/

//Output directory
fastp_dir="fastp"

FastP = {
    def input_fastq_options = ""
    if (reads_R2 == "") {
        input_fastq_options = "-i " + unmapped_reads_dir + "/" + branch.name + ".fastq.gz"
    } else {
	input_fastq_options = "-i " + unmapped_reads_dir + "/" + branch.name + "_1.fastq.gz" + " -I " + unmapped_reads_dir + "/" + branch.name + "_2.fastq.gz"
    }
    output.dir = fastp_dir
    if (reads_R2 == "") {
        produce(branch.name + ".fastq.gz", branch.name + ".html", branch.name + ".json") {
            exec "$fastp --thread $threads $input_fastq_options -o $output1 --html $output2 --json $output3"
        }
    } else {
	produce(branch.name + "_1.fastq.gz", branch.name + "_2.fastq.gz", branch.name + ".html", branch.name + ".json") {
            exec "$fastp --thread $threads $input_fastq_options -o $output1 -O $output2 --html $output3 --json $output4"
        }
    }
}

quality_trimming = segment {fastqInputFormat * [FastP] }
