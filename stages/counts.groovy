/***********************************************************
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 22/11/2024
 *********************************************************/

counts_dir="counts"

count_reads = {
    output.dir=counts_dir
    from("*.bam","LncRAnalyzer-Lncs-intersect.gtf") produce (org_name+"_PCG.counts",org_name+"_Lnc.counts"){
     if (reads_R2 == ""){
     exec """
     $featureCounts -T $threads $featurecounts_option1 -a $annotation -o $output1 $inputs.bam ;
     $featureCounts -T $threads $featurecounts_option2 -a $input.gtf -o $output2 $inputs.bam 
     """
     } else {
     exec """
     $featureCounts -T $threads -p $featurecounts_option1 -a $annotation -o $output1 $inputs.bam ;
     $featureCounts -T $threads -p $featurecounts_option2 -a $input.gtf -o $output2 $inputs.bam 
     """
     }
     }
}

get_tsv = {
     output.dir=summary_dir
     from(org_name+"_PCG.counts",org_name+"_Lnc.counts") produce(org_name+"_PCG.TSV",org_name+"_Lnc.TSV"){
     exec """
     sed 1,1d $input1 > $output1 ;
     sed -i 's:Align_and_assembly/::g;s:\\.bam::g' $output1 ;
     sed 1,1d $input2 > $output2 ;
     sed -i 's:Align_and_assembly/::g;s:\\.bam::g' $output2
     """
     }
}

get_counts = segment { fastqInputFormat*[count_reads] + get_tsv }
