/****************************************************************
 ** Stages to retieve quality trimming and removing rRNA reads
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 ****************************************************************/

//Output directory
summary_dir="LncRAnalyzer-summary"

get_FEELnc_results = {
	output.dir=intergenic_dir
	from("FEELnc_intergenic_lncRNAs.fa","FEELnc_mRNA_spliced_lncRNAs.fa") produce("FEELnc_out-lnc.list") {
	exec "grep '>' $input1 $input2 |sed 's/>//g'|sort -u > $output"
	}
}

lnc_venn = {
	output.dir=summary_dir
	from("final_lnc_RNAs-CPAT.list","final_lnc_RNAs-cpc2.list","final_lnc_RNAs-rnasamba.list","FEELnc_out-lnc.list") produce("LncRAnalyzer-lnc_venn.tiff") {
	exec "$Rscript $npcts_venn_script $input1 $input2 $input3 $input4 $output"
	}
} 

npcts_venn = {
	output.dir=summary_dir
	from("final_NPCTs-CPAT.list","final_NPCTs-cpc2.list","final_NPCTs-rnasamba.list") produce("LncRAnalyzer-NPCTs-Venn.tiff") {
	exec "$Rscript $lnc_venn_script $input1 $input2 $input3 $output"
	}
}

LncRAnalyzer_summary = segment {get_FEELnc_results + lnc_venn + npcts_venn}
