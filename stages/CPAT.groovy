/***********************************************************
 ** Stages run lnc RNA analysis with CPAT with python 3
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Python 2.7 and CPC2
CPAT_dir="CPAT_out"

extract_cds = {
	output.dir=CPAT_dir
	produce("Ref_genome.CDS.fa"){
	exec "$gffread $annotation -g $genome -x $output"
	}
}
	
build_hexamer_table = {
	output.dir=CPAT_dir
	from("Ref_genome.CDS.fa","Putative.lnc_NPCTs.fa") produce("CPAT_hexamer_table.TSV"){
	exec "$python2 $make_hexamer -c $input1 -n $input2 > $output"
	  }
}

build_logit_model = {
	output.dir=CPAT_dir
	from("CPAT_hexamer_table.TSV","Ref_genome.CDS.fa","Putative.lnc_NPCTs.fa") produce(org_name+".make_logitModel.r"){
	exec "$python2 $logit_model -x $input1 -c $input2 -n $input3 -o $output.prefix.prefix"
	  }
}

run_CPAT = {
	output.dir=CPAT_dir
	from("CPAT_hexamer_table.TSV","Putative.lnc_NPCTs.fa") produce("CPAT_output.TSV"){
	exec "$python2 $CPAT -x $input1 -g $input2 -d ${output.dir}/${org_name}.logit.RData $CPAT_options -o $output"
	  }
}

CPAT_extract_fasta = {
	output.dir=CPAT_dir
	from("CPAT_output.TSV","Putative.lnc_NPCTs.fa") produce("final_lnc_RNAs-CPAT.list","final_lnc_RNAs-CPAT.fa","final_NPCTs-CPAT.list","final_NPCTs-CPAT.fa"){
	exec """
	awk -F '\t'  '\$6 < 0.5' $input1 | cut -f1 > $output1 ;
	${seqtk} subseq $input2 $output1 > $output2 ;
	awk -F '\t'  '\$6 > 0.5' $input1 | cut -f1 > $output3 ;
	${seqtk} subseq $input2 $output3 > $output4
	"""
	}
}

cpat_based_coding_potentials = segment { extract_cds + build_hexamer_table + build_logit_model + run_CPAT + CPAT_extract_fasta }
