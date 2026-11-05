/***********************************************************
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 22/11/2024
 ** Corrections: fixed ambiguous $input.gtf references,
 **              fixed segment split pattern, fixed sed header strip
 *********************************************************/

counts_dir = "counts"

count_reads = {
    output.dir = counts_dir
    from("*.bam", "LncRAnalyzer-Lncs-intersect.gtf", "LncRAnalyzer-NPCTs-intersect.gtf") produce(org_name+"_PCGs.counts", org_name+"_Lncs.counts", org_name+"_NPCTs.counts") {
        if (reads_R2 == "") {
            exec """
            $featureCounts -T $threads $featurecounts_option1 -a $annotation -o $output1 $inputs.bam ;
            $featureCounts -T $threads $featurecounts_option2 -a $input2.gtf -o $output2 $inputs.bam ;
            $featureCounts -T $threads $featurecounts_option2 -a $input3.gtf -o $output3 $inputs.bam ;
            """
        } else {
            exec """
            $featureCounts -T $threads -p $featurecounts_option1 -a $annotation -o $output1 $inputs.bam ;
            $featureCounts -T $threads -p $featurecounts_option2 -a $input2.gtf -o $output2 $inputs.bam ;
            $featureCounts -T $threads -p $featurecounts_option2 -a $input3.gtf -o $output3 $inputs.bam ;
            """
        }
    }
}

get_tsv = {
    output.dir = summary_dir
    from(org_name+"_PCGs.counts", org_name+"_Lncs.counts", org_name+"_NPCTs.counts") produce(org_name+"_PCGs.TSV", org_name+"_Lncs.TSV", org_name+"_NPCTs.TSV") {
        exec """
        sed 1,2d $input1 > $output1 ;
        sed -i 's:Align_and_assembly/::g;s:\\.bam::g' $output1 ;
        sed 1,2d $input2 > $output2 ;
        sed -i 's:Align_and_assembly/::g;s:\\.bam::g' $output2 ;
        sed 1,2d $input3 > $output3 ;
        sed -i 's:Align_and_assembly/::g;s:\\.bam::g' $output3
        """
    }
}

get_counts = segment { count_reads + get_tsv }
