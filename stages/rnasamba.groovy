/***********************************************************
 ** Stages run lnc RNA analysis with rnasamba
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

// RNASamba
rnasamba_dir="rnasamba_out"

extract_mRNAs = {
        output.dir=rnasamba_dir
        produce("Ref_genome.mRNAs.fa"){
        exec "$gffread $annotation -g $genome -w $output"
        }
}

rnasamba_train = {
	output.dir=rnasamba_dir
	from("Ref_genome.mRNAs.fa","Putative.lnc_NPCTs.fa") produce("rnasamba_model.hdf5"){
	exec "$rnasamba train -v 2 $output $input1 $input2"
	}
}


rnasamba_classify = {
	output.dir=rnasamba_dir
	from("Putative.lnc_NPCTs.fa","rnasamba_model.hdf5") produce("Putative_lnc_NPCTs.rnasamba.TSV"){
	exec "$rnasamba classify $output $input1 $input2"
	  }
}

rnasamba_final_lnc_RNAs = {
	output.dir=rnasamba_dir
	from("Putative_lnc_NPCTs.rnasamba.TSV") produce("final_lnc_RNAs-rnasamba.TSV","final_lnc_RNAs-rnasamba.list"){
	exec """
	grep -w 'noncoding' $input > $output1 ;
	grep -w 'noncoding' $input|cut -f1|sed 1,1d > $output2
	"""
	  }
}

rnasamba_final_NPCTs = {
	output.dir=rnasamba_dir
	from("Putative_lnc_NPCTs.rnasamba.TSV") produce("final_NPCTs-rnasamba.TSV","final_NPCTs-rnasamba.list"){
	exec """
	grep -w 'coding' $input > $output1 ;
	grep -w 'coding' $input|cut -f1|sed 1,1d > $output2
	"""
	  }
}

rnasamba_extract_fasta = {
	output.dir=rnasamba_dir
	from("Putative.lnc_NPCTs.fa","final_lnc_RNAs-rnasamba.list","inal_NPCTs-rnasamba.list") produce("final_lnc_RNAs-rnasamba.fa","final_NPCTs-rnasamba.fa"){
	exec """
	${seqtk} subseq $input1 $input2 > $output1 ;
	${seqtk} subseq $input1 $input3 > $output2
	"""
	}
}

rnasamba_train_and_classify = segment {extract_mRNAs + rnasamba_train + rnasamba_classify + 
				rnasamba_final_lnc_RNAs + rnasamba_final_NPCTs + 
				rnasamba_extract_fasta }

