/***********************************************************
 ** Stages run lnc RNA analysis with lgc with python 2.7
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Python 2.7 and lgc
lgc_dir="lgc_out"

perform_lgc = {
	output.dir=lgc_dir
	from("Putative.lnc_NPCTs.fa") produce("Putative.lnc_NPCTs.lgc.txt"){
	exec "$python2 $lgc $input $output"
	  }
}

lgc_final_lnc_RNAs = {
	output.dir=lgc_dir
	from("Putative.lnc_NPCTs.lgc.txt") produce("final_lnc_RNAs-lgc.TSV","final_lnc_RNAs-lgc.list"){
	exec """
	sed 1,11d $input | grep -w 'Non-coding' > $output1 ;
	sed 1,11d $input | grep -w 'Non-coding' |cut -f1 > $output2
	"""
	  }
}

lgc_final_NPCTs = {
	output.dir=lgc_dir
	from("Putative.lnc_NPCTs.lgc.txt") produce("final_NPCTs-lgc.TSV","final_NPCTs-lgc.list"){
	exec """
	sed 1,11d $input | grep -w 'Coding' > $output1 ;
	sed 1,11d $input | grep -w 'Coding' |cut -f1 > $output2
	"""
	  }
}

lgc_extract_fasta = {
	output.dir=lgc_dir
	from("Putative.lnc_NPCTs.fa","final_lnc_RNAs-lgc.list","final_NPCTs-lgc.list") produce("final_lnc_RNAs-lgc.fa","final_NPCTs-lgc.fa"){
	exec """
	${seqtk} subseq $input1 $input2 > $output1 ;
	${seqtk} subseq $input1 $input3 > $output2
	"""
	}
}

lgc_based_coding_potentials = segment { perform_lgc + lgc_final_lnc_RNAs + lgc_final_NPCTs + lgc_extract_fasta}
