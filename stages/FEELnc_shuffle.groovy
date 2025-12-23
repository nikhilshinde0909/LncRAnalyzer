/***********************************************************
 ** Stages run lnc RNA analysis with FEELnc-pipe
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//FEELnc shuffle mode
shuffle_dir="FEELnc_shuffle_out"

feelnc_shuffle_filter = {
	output.dir=shuffle_dir
	from("genome_merged.gtf") produce("feelnc_shuffle_filter.log","feelnc_shuffle_filter.gtf"){
	exec "$perl $FEELnc_filter --proc=$threads --infile=$input --mRNAfile=$annotation $shuffle_filter_options --outlog=$output1 > $output2"
	  }
}

feelnc_shuffle_codpot = {
	output.dir=shuffle_dir
	from("feelnc_shuffle_filter.gtf") produce("feelnc_shuffle.codpot.log"){
	exec """
	source $Activate FEELnc ;
	$perl $FEELnc_codpot --infile=$input --mRNAfile=$annotation --genome=$genome $shuffle_codpot_options --outname=feelnc_shuffle.codpot --outdir=$shuffle_dir > $output
	"""
	}
}

feelnc_shuffle_classifier = {
	output.dir=shuffle_dir
	produce("feelnc_shuffle_classifier.log","feelnc_shuffle.classifier.txt"){
	exec "$perl $FEELnc_classifier -i ${output.dir}/feelnc_shuffle.codpot.lncRNA.gtf -a $annotation --log=$output1  > $output2"
	  }
}

extract_mRNA_spliced_lncRNAs = {
	output.dir=shuffle_dir
	produce("FEELnc_mRNA_spliced_lncRNAs.fa"){
	exec "$gffread ${output.dir}/feelnc_shuffle.codpot.lncRNA.gtf -g $genome -w $output"
	}
}

shuffle = segment { feelnc_shuffle_filter + feelnc_shuffle_codpot + feelnc_shuffle_classifier + extract_mRNA_spliced_lncRNAs}
