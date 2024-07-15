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
	exec "cat $input1 $input2 |grep '>' |sed 's/>//g'|sort -u > $output"
	}
}

lnc_venn = {
	output.dir=summary_dir
	from("final_lnc_RNAs-CPAT.list","final_lnc_RNAs-cpc2.list","final_lnc_RNAs-rnasamba.list","FEELnc_out-lnc.list","final_lnc_RNAs-lgc.list") produce("LncRAnalyzer-lnc_venn.log") {
	exec "$Rscript $lnc_venn_script $input1 $input2 $input3 $input4 $input5 > $output"
	}
} 

npcts_venn = {
	output.dir=summary_dir
	from("final_NPCTs-CPAT.list","final_NPCTs-cpc2.list","final_NPCTs-rnasamba.list","final_NPCTs-lgc.list") produce("LncRAnalyzer-NPCTs-Venn.log") {
	exec "$Rscript $npcts_venn_script $input1 $input2 $input3 $input4 > $output"
	}
}

lnc_intersect = {
	output.dir=summary_dir
	from("final_lnc_RNAs-CPAT.list","final_lnc_RNAs-cpc2.list","final_lnc_RNAs-rnasamba.list","FEELnc_out-lnc.list","final_lnc_RNAs-lgc.list") produce("LncRAnalyzer-Lncs-intersect.txt") {
	exec "$Rscript $lnc_intersect_script $input1 $input2 $input3 $input4 $input5 $output"
	}
} 

npcts_intersect = {
	output.dir=summary_dir
	from("final_NPCTs-CPAT.list","final_NPCTs-cpc2.list","final_NPCTs-rnasamba.list","final_NPCTs-lgc.list") produce("LncRAnalyzer-NPCTs-intersect.txt") {
	exec "$Rscript $npct_intersect_script $input1 $input2 $input3 $input4 $output"
	}
}

final_lncs_gtf = {
	output.dir=summary_dir
	from("LncRAnalyzer-Lncs-intersect.txt") produce("LncRAnalyzer-Lncs-intersect.gtf") {
	exec "cat ${intergenic_dir}/feelnc_intergenic.codpot.lncRNA.gtf ${shuffle_dir}/feelnc_shuffle.codpot.lncRNA.gtf | sort -u | grep -w -f $input > $output"
	}
}

final_ntpc_fa = {
	output.dir=summary_dir
	from("Putative.lnc_NPCTs.fa","LncRAnalyzer-NPCTs-intersect.txt") produce("LncRAnalyzer-NPCTs-intersect.fa") {
	exec "${seqtk} subseq $input1 $input2 > $output"
	}
}


LncRAnalyzer_summary = segment {get_FEELnc_results + lnc_venn + npcts_venn + lnc_intersect + npcts_intersect + final_lncs_gtf + final_ntpc_fa}
