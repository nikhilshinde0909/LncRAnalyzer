/***********************************************************
 ** Stages run lnc RNA analysis with Pfamscan
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Run Pfamscan
pfamscan_dir="pfamscan_out"

download_pfam = {
	output.dir=pfamscan_dir
        produce("Pfam-A.hmm.dat.gz","Pfam-A.hmm.gz"){
	exec """
	wget ftp://ftp.ebi.ac.uk/pub/databases/Pfam/current_release/Pfam-A.hmm.dat.gz -O $output1 ;
	wget ftp://ftp.ebi.ac.uk/pub/databases/Pfam/current_release/Pfam-A.hmm.gz -O $output2
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
	exec "$hmmpress -f ${pfamscan_dir}/Pfam-A.hmm"
}

translate_seq = {
	output.dir=pfamscan_dir
	from("Putative.lnc_NPCTs.fa") produce("Putative.lnc_NPCTs.pep"){
	exec "$transeq -sequence $input -outseq $output"
	}
}

perform_pfamcsan = {
	output.dir=pfamscan_dir
	from("Pfam-A.hmm","Putative.lnc_NPCTs.pep") produce("Putative.lnc_NPCTs.pfamscan.txt","pfam.log"){
	exec "$pfamscan --cpu $threads -E 10e-5 --tblout $output1 $input1 $input2 > $output2"
	  }
}

pfamscan_final_NPCTs = {
    output.dir = pfamscan_dir
    from("Putative.lnc_NPCTs.pfamscan.txt") produce("final_NPCTs_pfamscan.list") {
        exec """
        grep -v -E '#' $input | awk '{print \$3}' | cut -d '_' -f 1 | sort -u > $output
        """
    }
}

pfamscan_final_lncrnas = {
    output.dir = pfamscan_dir
    from("final_NPCTs_pfamscan.list", "Putative.lnc-NPCTs.list") produce("final_lncRNAs_pfamscan.list") {
        R {"""
        # Reading the input files
        data1 <- read.table('$input1', header=F, sep = '\t')
        data2 <- read.table('$input2', header=F, sep = '\t')

        # Removing entries from data2 that are present in data1
        data3 <- data2[!data2$V1 %in% data1$V1,]

        # Writing the output file
        write.table(data3, file = '$output', row.names = F, col.names = F, quote = F, sep = '\t')
        """}
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

execute_pfamscan = segment { download_pfam + gunzip_pfam + perform_hmmpress + 
                            translate_seq + perform_pfamcsan + 
                            pfamscan_final_NPCTs + pfamscan_final_lncrnas +
                            pfamscan_extract_fasta }