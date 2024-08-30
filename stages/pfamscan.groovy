/***********************************************************
 ** Stages run lnc RNA analysis with Pfamscan
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Run Pfamscan
pfamscan_dir="pfamcsan_out"

download_pfam = {
	output.dir=pfamscan_dir
        produce("Pfam-A.hmm.dat.gz","Pfam-A.hmm.gz"){
	exec """
	wget http://ftp.ebi.ac.uk/pub/databases/Pfam/current_release/Pfam-A.hmm.dat.gz -O $output1 ;
	wget http://ftp.ebi.ac.uk/pub/databases/Pfam/current_release/Pfam-A.hmm.gz -O $output2
	"""
       }
}

gunzip_pfam = {
	output.dir=pfamscan_dir
	from("Pfam-A.hmm.dat.gz","Pfam-A.hmm.gz") produce("Pfam-A.hmm.dat","Pfam-A.hmm"){
	exec """
	gunzip -c $input1 > $output1 ;
	gunzip -c $input2 > $output2
	"""
	}
}

perform_hmmpress = {
	exec "$hmmpress ${pfamscan_dir}/Pfam-A.hmm"
}

perform_pfamcsan = {
	output.dir=pfamscan_dir
	from("Putative.lnc_NPCTs.fa") produce("Putative.lnc_NPCTs.pfamscan.txt"){
	exec "$pfamscan -fasta $input -translate -e_seq 10e-5 -cpu $threads -dir ${pfamscan_dir} -outfile $output"
	  }
}

pfamscan_final_lnc_NPCTs = {
	output.dir=pfamscan_dir
	from("Putative.lnc_NPCTs.pfamscan.txt","Putative.lnc-NPCTs.list") produce("final_NPCTs_pfamscan.list","final_lncRNAs_pfamscan.list"){
	exec """
	cut -f 1 $input1| sort -u > $output1 ;
	grep -v -w -f $output1 $input2 > $output2
	"""
	  }
}

pfamscan_extract_fasta = {
	output.dir=pfamscan_dir
	from("Putative.lnc_NPCTs.fa","final_NPCTs_pfamscan.list","final_lncRNAs_pfamscan.list") produce("final_NPCTs_pfamscan.fa","final_lnc_RNAs_pfamscan.fa"){
	exec """
	${seqtk} subseq $input1 $input2 > $output1 ;
	${seqtk} subseq $input1 $input3 > $output2
	"""
	}
}

execute_pfamscan = segment { download_pfam + gunzip_pfam + perform_hmmpress + perform_pfamcsan + pfamscan_final_lnc_NPCTs + pfamscan_extract_fasta }
