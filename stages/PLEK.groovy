/***********************************************************
 ** Stages run lnc RNA analysis with PLEK with python 3
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 15/12/2024
 *********************************************************/

//Python 3 and PLEK
PLEK_dir="PLEK_out"

// load PLEK model and range 
PLEK_model=codeBase+"/Models/PLEK/"+org_name+".model"
PLEK_range=codeBase+"/Models/PLEK/"+org_name+".range"


extract_mRNAs_fa = {
	output.dir=PLEK_dir
	if (file(org_name+".mRNAs.fa").exists()){
	exec "echo 'mRNA fasta already exits'"
	} else {
	produce(org_name+".mRNAs.fa"){
	exec """
	$gffread $annotation -g $genome -x $output
	"""
	}  
    }
}

plek_modelling = {
	output.dir = PLEK_dir
	if (file(PLEK_model).exists() || file(PLEK_range).exists()){
	exec "echo 'PLEK models and range are available for species'"
	} else {
	from(org_name+".mRNAs.fa") produce(org_name+".model") {
	exec """
	$python3 $PLEKModelling -thread $threads -mRNA $input -lncRNA $known_lncRNAs_FA -prefix $output.prefix
	"""
	} 
    }
}

perform_plek = {
	output.dir=PLEK_dir
	if (file(PLEK_model).exists() || file(PLEK_range).exists()){
	from("Putative.lnc_NPCTs.fa") produce("Putative.lnc_NCPTs.PLEK.txt","PLEK.log"){
	exec "$python3 $PLEK -thread $threads -fasta $input -model $PLEK_model -range $PLEK_range -out $output1 > $output2"
	}
     } else {
	from("Putative.lnc_NPCTs.fa",org_name+".model",org_name+".range") produce("Putative.lnc_NCPTs.PLEK.txt","PLEK.log"){
	exec """
	$python3 $PLEK -thread $threads -fasta $input1 -model $input2 -range $input3 -out $output1 > $output2
	"""
	}  
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

plek_get_fasta = {
	output.dir=PLEK_dir
	from("Putative.lnc_NPCTs.fa","final_lnc_RNAs-PLEK.list","final_NPCTs-PLEK.list") produce("final_lnc_RNAs-PLEK.fa","final_NPCTs-PLEK.fa"){
	exec """
	${seqtk} subseq $input1 $input2 > $output1 ;
	${seqtk} subseq $input1 $input3 > $output2
	"""
	}
}

plek_based_coding_potentials = segment { extract_mRNAs_fa + plek_modelling + 
				perform_plek + plek_final_lnc_RNAs + 
				plek_final_NPCTs + plek_get_fasta 
				}
