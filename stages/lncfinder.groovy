/***********************************************************
 ** Stages run lnc RNA analysis with lncfinder
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 05/02/2025
 *********************************************************/

// lncfinder
lncfinder_dir="lncfinder_out"

lncfinder_model=codeBase+"/Models/LncFinder/"+org_name+".model.RDS"
lncfinder_frequencies=codeBase+"/Models/LncFinder/"+org_name+".freq.RDS"

extract_mRNA_fa = {
        output.dir = lncfinder_dir
        if (file(lncfinder_model).exists()||file(lncfinder_frequencies).exists()){
        exec "echo 'No need to extract fasta for model training'"
        } else {
        produce(org_name+".mRNAs.fa"){
        exec """
        $gffread $annotation -g $genome -w $output
        """
        }
    }
}

lncfinder_train = {
        output.dir = lncfinder_dir
        if (file(lncfinder_model).exists()||file(lncfinder_frequencies).exists()){
        exec "echo 'No need to train models'"
        } else {
        from(org_name+".mRNAs.fa") produce(org_name+".model.RDS",org_name+".freq.RDS"){
        exec """
        $Rscript $train_lncfinder $threads $input $known_lncRNAs_FA $output1 $output2
        """
        }     
    }
}

lncfinder_classify = {
        output.dir = lncfinder_dir
        if (file(lncfinder_model).exists()||file(lncfinder_frequencies).exists()){
        from("Putative.lnc_NPCTs.fa") produce("Putative.lnc_NPCTs.lncfinder.TSV","final_lnc_RNAs-lncfinder.list","final_NPCTs-lncfinder.list"){
        exec """
        $Rscript $run_lncfinder $threads $lncfinder_model $lncfinder_frequencies $input $output1 $output2 $output3
        """
        }
    } else {
        from(org_name+".model.RDS",org_name+".freq.RDS","Putative.lnc_NPCTs.fa") produce("Putative.lnc_NPCTs.lncfinder.TSV","final_lnc_RNAs-lncfinder.list","final_NPCTs-lncfinder.list") {
        exec """
        $Rscript $run_lncfinder $threads $input1 $input2 $input3 $output1 $output2 $output3
        """
        }
    }
}


lncfinder_extract_fasta = {
	output.dir=lncfinder_dir
	from("Putative.lnc_NPCTs.fa","final_lnc_RNAs-lncfinder.list","final_NPCTs-lncfinder.list") produce("final_lnc_RNAs-lncfinder.fa","final_NPCTs-lncfinder.fa"){
	exec """
	${seqtk} subseq $input1 $input2 > $output1 ;
	${seqtk} subseq $input1 $input3 > $output2
	"""
	}
}

lncfinder_train_and_classify = segment { extract_mRNA_fa + lncfinder_train + lncfinder_classify + 
				lncfinder_extract_fasta }
