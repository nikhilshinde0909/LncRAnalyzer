/***********************************************************
 ** Stages run lnc RNA analysis with CPC2 with python 2.7
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Python 2.7 and CPC2
cpc2_dir="cpc2_out"

perform_cpc2 = {
	output.dir=cpc2_dir
	from("Putative.lnc_NPCTs.fa") produce("Putative.lnc_NPCTs.cpc2.txt"){
	exec "$python2 $cpc2 -i $input -o $output"
	  }
}

cpc2_final_lnc_RNAs = {
	output.dir=cpc2_dir
	from("Putative.lnc_NPCTs.cpc2.txt") produce("final_lnc_RNAs-cpc2.TSV","final_lnc_RNAs-cpc2.list"){
	exec """
	grep -E -w 'noncoding' $input > $output1 ;
	grep -E -w 'noncoding' $input|cut -f1 > $output2
	"""
	  }
}

cpc2_final_NPCTs = {
	output.dir=cpc2_dir
	from("Putative.lnc_NPCTs.cpc2.txt") produce("final_NPCTs-cpc2.TSV","final_NPCTs-cpc2.list"){
	exec """
	grep -E -w 'coding' $input > $output1 ;
	grep -E -w 'coding' $input|cut -f1 > $output2
	"""
	  }
}

cpc2_extract_fasta = {
	output.dir=cpc2_dir
	from("Putative.lnc_NPCTs.fa","final_lnc_RNAs-cpc2.list","final_NPCTs-cpc2.list") produce("final_lnc_RNAs-cpc2.fa","final_NPCTs-cpc2.fa"){
	exec """
	${seqtk} subseq $input1 $input2 > $output1 ;
	${seqtk} subseq $input1 $input3 > $output2
	"""
	}
}

coding_potential_calculations = segment { perform_cpc2 + cpc2_final_lnc_RNAs + cpc2_final_NPCTs + cpc2_extract_fasta}
