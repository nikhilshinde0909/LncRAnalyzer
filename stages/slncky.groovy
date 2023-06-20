/***********************************************************
 ** Stages run lnc RNA analysis with slncky with python 3
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Python 2.7 and slncky
slncky_dir="slncky_out"

ref_genome_bed = {
	output.dir=slncky_dir
	produce("Ref_genome.bed","Rel_ref_genome.bed"){
	exec """
	$gffread $annotation --bed -o ${output.dir}/temp.bed ;
	cut -f1-12 ${output.dir}/temp.bed > $output1 ;
	$gffread $annotation_related_species --bed -o ${output.dir}/temp1.bed ;
        cut -f1-12 ${output.dir}/temp1.bed > $output2 ;
	"""
	  }
}

fasta_index = {
	output.dir=slncky_dir
	produce("Ref_genome.fa","Rel_ref_genome.fa"){
	exec """
	cp $genome $output1 ;
	$samtools faidx $output1 ;
	cp $genome_related_species $output2 ;
        $samtools faidx $output2
	"""
	}
}

annotation_config = {
	output.dir=slncky_dir
	from("Ref_genome.bed","Rel_ref_genome.bed","Ref_genome.fa","Rel_ref_genome.fa") produce("annotation.config"){
	if(liftover!=""){	
	exec """
	echo '>'$org_name >> $output ;
        echo 'CODING='$input1 >> $output ;
        echo 'GENOME_FA='$input3 >> $output ;
	echo 'ORTHOLOG='$rel_sp_name >> $output ;
	echo 'LIFTOVER='$liftover >> $output ;
	echo '>'$rel_sp_name >> $output ;
	echo 'CODING='$input2 >> $output ;
	echo 'GENOME_FA='$input4 >> $output ;
	echo 'ORTHOLOG='$org_name >> $output ;
	echo 'LIFTOVER='$rel_liftover >> $output
	"""
	} else {
	exec """
	echo '>'$org_name >> $output ;
        echo 'CODING='$input1 >> $output ;
        echo 'GENOME_FA='$input3 >> $output ;
        echo 'ORTHOLOG='$rel_sp_name >> $output ;
        echo '>'$rel_sp_name >> $output ;
        echo 'CODING='$input2 >> $output ;
        echo 'GENOME_FA='$input4 >> $output ;
        echo 'ORTHOLOG='$org_name >> $output ;
        """
	}
	}
}

putative_lnc_npcts_bed = {
	output.dir=slncky_dir
	from("Putative.lnc_NPCTs.gtf") produce("Putative-lnc-nptcs.bed"){
	exec """
	$gffread $annotation --bed -o ${output.dir}/temp.bed ;
	cut -f1-12 ${output.dir}/temp.bed > $output
	"""
	  }
}

run_slncky = {
	output.dir=slncky_dir
	from("annotation.config","Putative-lnc-nptcs.bed") produce("final.lncs.info.txt"){
	exec """
	source $Activate cpc2-cpat-slncky ;
	$slncky -n $threads -c $input1 $input2 $org_name $slncky_options $output.prefix.prefix.prefix
	"""
	  }
}

slncky_run = segment { ref_genome_bed + fasta_index + annotation_config + putative_lnc_npcts_bed + run_slncky }
