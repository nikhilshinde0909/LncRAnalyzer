/***********************************************************
 ** Stages run lnc RNA analysis with slncky with python 2.75
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
	cut -f1-12 ${output.dir}/temp.bed > $output1 && rm ${output.dir}/temp.bed ;
	$gffread $annotation_related_species --bed -o ${output.dir}/temp1.bed ;
        cut -f1-12 ${output.dir}/temp1.bed > $output2 && rm ${output.dir}/temp1.bed ;
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
    from("Ref_genome.bed","Rel_ref_genome.bed") produce("annotation.config") {
        if (liftover != "") {
            exec """
            echo '>'$org_name >> $output ;
            echo 'CODING='$input1 >> $output ;
            echo 'GENOME_FA='$genome >> $output ;
            echo 'ORTHOLOG='$rel_sp_name >> $output ;
            echo 'LIFTOVER='$liftover >> $output ;
            echo 'NONCODING='$noncoding >> $output ;
            echo 'MIRNA='$mir >> $output ;
            echo 'SNORNA='$sno >> $output ;
            echo '>'$rel_sp_name >> $output ;
            echo 'CODING='$input2 >> $output ;
            echo 'GENOME_FA='$genome_related_species >> $output ;
            echo 'ORTHOLOG='$org_name >> $output ;
            echo 'NONCODING='$rel_noncoding >> $output ;
            echo 'MIRNA='$rel_mir >> $output ;
            echo 'SNORNA='$rel_sno >> $output
            """
        } else {
            exec """
            echo '>'$org_name >> $output ;
            echo 'CODING='$input1 >> $output ;
            echo 'GENOME_FA='$genome >> $output ;
            echo 'ORTHOLOG='$rel_sp_name >> $output ;
            echo '>'$rel_sp_name >> $output ;
            echo 'CODING='$input2 >> $output ;
            echo 'GENOME_FA='$genome_related_species >> $output ;
            echo 'ORTHOLOG='$org_name >> $output ;
	"""
        }
    }
}

putative_lnc_npcts_bed = {
	output.dir=slncky_dir
	from("Putative.lnc_NPCTs.gtf") produce("Putative-lnc-nptcs.bed"){
	exec """
	$gffread $input --bed -o ${output.dir}/temp.bed ;
	cut -f1-12 ${output.dir}/temp.bed > $output && rm ${output.dir}/temp.bed
	"""
	  }
}

run_slncky = {
	output.dir=slncky_dir
	from("annotation.config","Putative-lnc-nptcs.bed") produce("slncky_out.lncs.info.txt"){
	exec """
	source $Activate cpc2-cpat-slncky ;
	$python2 $slncky -n $threads -c $input1 $input2 $org_name $slncky_options $output.prefix.prefix.prefix
	"""
	  }
}

ortholog_search = {
	output.dir = slncky_dir
	if (liftover != ""){
	from("annotation.config", "Putative-lnc-nptcs.bed") produce(rel_sp_name+".orthologs.top.txt"){
        exec """
        source ${Activate} cpc2-cpat-slncky ;
        $python2 $slncky -n $threads -c $input1 $slncky_ortho_options $input2 $org_name $output.prefix.prefix.prefix
        """
    	} 
	} else {
	exec "echo 'Liftover files not provided for conservation studies'"
        }
}

slncky_run = segment { ref_genome_bed + fasta_index + annotation_config + putative_lnc_npcts_bed + run_slncky + ortholog_search }
