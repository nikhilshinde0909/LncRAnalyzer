/***********************************************************
 ** Stages run lnc RNA analysis with PLEK with python 3
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Python 3 and PLEK
PLEK_dir="PLEK_out"

plek_modelling = {
	output.dir=PLEK_dir
	from("Ref_genome.mRNAs.fa","Putative.lnc_NPCTs.fa") produce("PLEK.model"){
	exec """
	$python3 $PLEKModelling -thread $threads -mRNA $input1 -lncRNA $input2 -prefix $output.prefix
	"""
	}
}


perform_plek = {
	output.dir=PLEK_dir
	from("Putative.lnc_NPCTs.fa","PLEK.model","PLEK.range") produce("Putative.lnc_NCPTs.PLEK.txt","PLEK.log"){
	exec """
	$python3 $PLEK -thread $threads -fasta $input1 -model $input2 -range $input3 -out $output
	"""
	  }
}

plek_final_lnc_RNAs = {
	output.dir=PLEK_dir
	from("Putative.lnc_NCPTs.PLEK.txt") produce("final_lnc_RNAs-PLEK.TSV","final_lnc_RNAs-PLEK.list"){
	exec """
	grep -w 'Non-coding' $input|sed 's/>//g' > $output1 ;
	grep -w 'Non-coding' $input|sed 's/>//g'|cut -f3 > $output2
	"""
	  }
}

plek_final_NPCTs = {
	output.dir=PLEK_dir
	from("Putative.lnc_NCPTs.PLEK.txt") produce("final_NPCTs-PLEK.TSV","final_NPCTs-PLEK.list"){
	exec """
	grep -w 'Coding' $input|sed 's/>//g' > $output1 ;
	grep -w 'Coding' $input|sed 's/>//g'|cut -f3 > $output2
	"""
	  }
}

plek_extract_fasta = {
	output.dir=PLEK_dir
	from("Putative.lnc_NPCTs.fa","final_lnc_RNAs-PLEK.list","final_NPCTs-PLEK.list") produce("final_lnc_RNAs-PLEK.fa","final_NPCTs-PLEK.fa"){
	exec """
	${seqtk} subseq $input1 $input2 > $output1 ;
	${seqtk} subseq $input1 $input3 > $output2
	"""
	}
}

plek_based_coding_potentials = segment { plek_modelling + perform_plek + plek_final_lnc_RNAs + plek_final_NPCTs + plek_extract_fasta }
