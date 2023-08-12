/***********************************************************
 ** Stages run lnc RNA analysis with slncky with python 3
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Python 2.7 and slncky
slncky_dir="slncky_out"

ref_genome_bed = {
	output.dir=slncky_dir
	produce("Ref_genome.bed","Rel_ref_genome.bed","Ref_genome.nc.bed","Rel_ref_genome.nc.bed"){
	exec """
	$gffread $annotation --bed -o ${output.dir}/temp.bed ;
	cut -f1-12 ${output.dir}/temp.bed > $output1 && rm ${output.dir}/temp.bed ;
	$gffread $annotation_related_species --bed -o ${output.dir}/temp1.bed ;
        cut -f1-12 ${output.dir}/temp1.bed > $output2 && rm ${output.dir}/temp1.bed ;
        $gffread $noncoding --bed -o ${output.dir}/temp.bed ;
        cut -f1-12 ${output.dir}/temp.bed > $output3 && rm ${output.dir}/temp.bed ;
        $gffread $rel_noncoding --bed -o ${output.dir}/temp1.bed ;
        cut -f1-12 ${output.dir}/temp1.bed > $output4 && rm ${output.dir}/temp1.bed 
	"""
	  }
}

fasta_index = {
    if ('${genome}.fai' != "" && '${genome_related_species}.fai' != "") {
        exec """
        $samtools faidx ${genome} ;
        $samtools faidx ${genome_related_species}
        """
    } else {
        exec "echo \"Required .fai files are already there for genome or related species.\""
    }
}

annotation_config = {
    output.dir = slncky_dir
    from("Ref_genome.bed","Rel_ref_genome.bed","Ref_genome.nc.bed","Rel_ref_genome.nc.bed") produce("annotation.config") {
        if (liftover != "") {
            exec """
            echo '>'$org_name >> $output ;
            echo 'CODING='$input1 >> $output ;
            echo 'GENOME_FA='$genome >> $output ;
            echo 'ORTHOLOG='$rel_sp_name >> $output ;
            echo 'LIFTOVER='$liftover >> $output ;
            echo 'NONCODING='$input3 >> $output ;
            echo 'MIRNA='$mir >> $output ;
            echo 'SNORNA='$sno >> $output ;
            echo '>'$rel_sp_name >> $output ;
            echo 'CODING='$input2 >> $output ;
            echo 'GENOME_FA='$genome_related_species >> $output ;
            echo 'ORTHOLOG='$org_name >> $output ;
            echo 'LIFTOVER='$rel_liftover >> $output ;
            echo 'NONCODING='$input4 >> $output ;
            echo 'MIRNA='$rel_mir >> $output ;
            echo 'SNORNA='$rel_sno >> $output
            """
        } else {
            exec """
            echo '>'$org_name >> $output ;
            echo 'CODING='$input1 >> $output ;
            echo 'GENOME_FA='$genome >> $output ;
            echo 'ORTHOLOG='$rel_sp_name >> $output ;
            echo 'NONCODING='$input3 >> $output ;
            echo 'MIRNA='$mir >> $output ;
            echo 'SNORNA='$sno >> $output ;
            echo '>'$rel_sp_name >> $output ;
            echo 'CODING='$input2 >> $output ;
            echo 'GENOME_FA='$genome_related_species >> $output ;
            echo 'ORTHOLOG='$org_name >> $output ;
            echo 'NONCODING='$input4 >> $output ;
            echo 'MIRNA='$rel_mir >> $output ;
            echo 'SNORNA='$rel_sno >> $output
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
