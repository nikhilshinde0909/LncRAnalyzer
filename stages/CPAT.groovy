/***********************************************************
 ** Stages run lnc RNA analysis with CPAT with python 3
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Python 2.7 and CPC2
CPAT_dir="CPAT_out"

// load hexamer table and logit models
hexamer_table=codeBase+"/Models/CPAT/"+org_name+"_hexamer.TSV"
logit_model=codeBase+"/Models/CPAT/"+org_name+".logit.RData"

CPAT_cutoff = {
    def cutoff = ""
    if (model == "Plants") {
        cutoff = 3.80
    } else if (model == "Human") {
        cutoff = 0.36
    } else if (model == "Mouse") {
        cutoff = 0.44
    } else if (model == "Drosophila") {
        cutoff = 0.39
    } else if (model == "Zebrafish") {
        cutoff = 0.38
    }
    return cutoff
}

run_CPAT = {
	output.dir=CPAT_dir
	from("Putative.lnc_NPCTs.fa") produce("CPAT_output.TSV"){
	exec "$python2 $CPAT -x $hexamer_table -g $input -d $logit_model $CPAT_options -o $output"
	  }
}


CPAT_extract_fasta = {
	output.dir=CPAT_dir
	from("CPAT_output.TSV","Putative.lnc_NPCTs.fa") produce("final_lnc_RNAs-CPAT.list","final_lnc_RNAs-CPAT.fa","final_NPCTs-CPAT.list","final_NPCTs-CPAT.fa"){
	exec """
	awk -F '\t'  '\$6 < $cutoff' $input1 | cut -f1|sed 1,1d > $output1 ;
	${seqtk} subseq $input2 $output1 > $output2 ;
	awk -F '\t'  '\$6 > $cutoff' $input1 | cut -f1|sed 1,1d > $output3 ;
	${seqtk} subseq $input2 $output3 > $output4
	"""
	}
}

cpat_based_coding_potentials = segment { CPAT_cutoff + run_CPAT + CPAT_extract_fasta }
