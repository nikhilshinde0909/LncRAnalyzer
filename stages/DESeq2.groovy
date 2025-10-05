/***********************************************************
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 22/11/2024
 *********************************************************/

DESeq2_dir="LncRAnalyzer-summary"

perform_deseq2 = {
	output.dir=DESeq2_dir
	if(design !=""){
	from(org_name+"_PCG.TSV",org_name+"_Lnc.TSV") produce(org_name+"_PCG_DESeq2.TSV",org_name+"_Lnc_DESeq2.TSV"){
	exec """
	$Rscript $DESeq2 $design $input1 $output1 ;
	$Rscript $DESeq2 $design $input2 $output2
	"""
	}
	} else {
	exec "echo 'Experimental design file not provided for DGE analysis'"
	}
}

DGE_analysis = segment { perform_deseq2 }
