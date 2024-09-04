/***********************************************************
 ** Stages run lnc RNA analysis with FEELnc-pipe
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//FEELnc intergenic mode
intergenic_dir="FEELnc_intergenic_out"

feelnc_intergenic_filter = {
	output.dir=intergenic_dir
	from("genome_merged.gtf") produce("feelnc_intergenic_filter.log","feelnc_intergenic_filter.gtf"){
	exec "$perl $FEELnc_filter --proc=$threads --infile=$input --mRNAfile=$annotation $intergenic_filter_options --outlog=$output1 > $output2"
	  }
}

feelnc_intergenic_codpot = {
	output.dir=intergenic_dir
	from("feelnc_intergenic_filter.gtf") produce("feelnc_intergenic.codpot.log"){
	exec """
	source $Activate FEELnc ;
	$perl $FEELnc_codpot --infile=$input --mRNAfile=$annotation --genome=$genome $intergenic_codpot_options --outname=feelnc_intergenic.codpot --outdir=$intergenic_dir > $output
	"""
	}
}

feelnc_intergenic_classifier = {
	output.dir=intergenic_dir
	produce("feelnc_intergenic_classifier.log","feelnc_intergenic.classifier.txt"){
	exec "$perl $FEELnc_classifier -i ${output.dir}/feelnc_intergenic.codpot.lncRNA.gtf -a $annotation --log=$output1 > $output2"
	  }
}

extract_intergenic_lncRNAs = {
        output.dir=intergenic_dir
        produce("FEELnc_intergenic_lncRNAs.fa"){
        exec "$gffread ${output.dir}/feelnc_intergenic.codpot.lncRNA.gtf -g $genome -w $output"
        }
}

intergenic = segment { feelnc_intergenic_filter + feelnc_intergenic_codpot + feelnc_intergenic_classifier + extract_intergenic_lncRNAs}
