/***********************************************************
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 22/11/2024
 *********************************************************/

DESeq2_dir="LncRAnalyzer-summary"

perform_deseq2 = {
        output.dir=DESeq2_dir
        if(design !=""){
        from(org_name+"_PCGs.TSV",org_name+"_Lncs.TSV",org_name+"_NPCTs.TSV") produce(org_name+"_PCGs_DESeq2.TSV",org_name+"_Lncs_DESeq2.TSV",org_name+"_NPCTs_DESeq2.TSV"){
        exec """
        $Rscript $DESeq2 $design $input1 $output1 ;
        $Rscript $DESeq2 $design $input2 $output2 ;
        $Rscript $DESeq2 $design $input3 $output3
        """
        }
        } else {
        exec "echo 'Experimental design file not provided for DGE analysis'"
        }
}

DGE_analysis = segment { perform_deseq2 }
