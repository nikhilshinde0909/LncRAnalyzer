/***********************************************************
 ** Stages for TE derived lncRNAs
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/01/2025
 *********************************************************/

//TE derived lncRNAs
TE_dir="TE_derived_lncRNAs"

lncRNA_bed = {
	output.dir=TE_dir
	if(clade=="plants"){
	from("LncRAnalyzer-Lncs-intersect.gtf") produce("LncRAnalyzer-Lncs-intersect.bed"){
	exec """
	$gffread $input --bed -o ${output.dir}/temp.bed ;
	cut -f1-6 ${output.dir}/temp.bed > $output && rm ${output.dir}/temp.bed 
	"""
	}
     } else {
	exec "echo 'No need to prepare bed files,TE derived lncRNA not supports for vertebrates'" 
	}
}

get_TEs = {
	output.dir=TE_dir
	if(clade=="plants"){
	produce(org_name+"_LTR.gff3",org_name+"_LINE.gff3",org_name+"_SINE.gff3",org_name+"_MITE.gff3",org_name+"_TIR.gff3",org_name+"_Helitron.gff3"){
	exec """
	$python3 $download_TE $org_name LTR $output1 ;
	$python3 $download_TE $org_name LINE $output2 ;
	$python3 $download_TE $org_name SINE $output3 ;
	$python3 $download_TE $org_name MITE $output4 ;
	$python3 $download_TE $org_name TIR $output5 ;
	$python3 $download_TE $org_name Helitron $output6
	"""
	}
    } else {
	exec "echo 'Downloading TE datasets not supports for vertebrates'" 
	}
}

TE_gff_to_bed = {
	output.dir=TE_dir
	if(clade=="plants"){
	from(org_name+"_LTR.gff3",org_name+"_LINE.gff3",org_name+"_SINE.gff3",org_name+"_MITE.gff3",org_name+"_TIR.gff3",org_name+"_Helitron.gff3") produce(org_name+"_LTR.bed",org_name+"_LINE.bed",org_name+"_SINE.bed",org_name+"_MITE.bed",org_name+"_TIR.bed",org_name+"_Helitron.bed"){
	exec """
	$python3 $format_gff_to_bed $input1 $output1 ;
	$python3 $format_gff_to_bed $input2 $output2 ;
	$python3 $format_gff_to_bed $input3 $output3 ;
	$python3 $format_gff_to_bed $input4 $output4 ;
	$python3 $format_gff_to_bed $input5 $output5 ;
	$python3 $format_gff_to_bed $input6 $output6 
	"""
	}
    } else {
	exec "echo 'TE datasets conversion not appliacable to vertebrates'" 
	}
}

get_TE_lncRNAs = {
	output.dir = TE_dir
	if (clade == "plants") {
	from("LncRAnalyzer-Lncs-intersect.bed",org_name+"_LTR.bed",org_name+"_LINE.bed",org_name+"_SINE.bed",org_name+"_MITE.bed",org_name+"_TIR.bed",org_name+"_Helitron.bed")
        produce(org_name+"_LTR.TSV",org_name+"_LINE.TSV",org_name+"_SINE.TSV",org_name+"_MITE.TSV",org_name+"_TIR.TSV",org_name+"_Helitron.TSV") {
	exec """
	$bedtools intersect -wo -a $input1 -b $input2 | cut -f 1-12 > $output1 ;
	$bedtools intersect -wo -a $input1 -b $input3 | cut -f 1-12 > $output2 ;
	$bedtools intersect -wo -a $input1 -b $input4 | cut -f 1-12 > $output3 ;
	$bedtools intersect -wo -a $input1 -b $input5 | cut -f 1-12 > $output4 ;
	$bedtools intersect -wo -a $input1 -b $input6 | cut -f 1-12 > $output5 ;
	$bedtools intersect -wo -a $input1 -b $input7 | cut -f 1-12 > $output6 ;
	"""
        }
    } else {
    exec "echo 'No TE-derived lncRNAs will be reported for vertebrates'"
    }
}

summary_TE_lncRNAs = {
	output.dir=summary_dir
	if(clade=="plants"){ 
	from(org_name+"_LTR.TSV",org_name+"_LINE.TSV",org_name+"_SINE.TSV",org_name+"_MITE.TSV",org_name+"_TIR.TSV",org_name+"_Helitron.TSV") produce("TE_derived_lncRNAs.TSV","TE_derived_lncRNAs_summary.TSV"){
	exec """
	$Rscript $get_TE_derived_lncRNA $input1 $input2 $input3 $input4 $input5 $input6 $output1 $output2
	"""
	}
    } else {
	exec "echo 'No TE derived lncRNAs wiil be reported for vertebrates'" 
	}
}

TE_derived_lncRNAs = segment { lncRNA_bed + get_TEs + TE_gff_to_bed + get_TE_lncRNAs + summary_TE_lncRNAs }

