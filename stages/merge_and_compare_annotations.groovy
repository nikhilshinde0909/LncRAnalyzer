/***********************************************************
 ** Stages to preform annotation compare classcode selection 
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Output directory
annotation_compare_dir="Annotation_compare"

merge_genome_annotations = {
      output.dir=annotation_compare_dir
      produce("genome_merged.gtf"){
	exec "${stringtie} -p $threads --merge $stringtie_merge_options -o $output $inputs"
      }
}

size_selection = {
      output.dir=annotation_compare_dir
      from("genome_merged.gtf") produce("genome_merged.size.selected.gtf"){
        exec "${gffread} $input $gffread_options -o $output"
      }
}

compare_genome_annotations = {
      output.dir=annotation_compare_dir
      from("genome_merged.size.selected.gtf") produce("gffcompare.annotated.gtf"){
        exec "${gffcompare} -r $annotation -o $output.prefix.prefix $input"
      }
}

classcode_selection = {
      output.dir=annotation_compare_dir
      from("gffcompare.annotated.gtf") produce("gffcompare.annotated.classcode_selected_lnc-npcts.gtf"){
        exec "grep -E 'class_code \"i\"|class_code \"u\"|class_code \"x\"|class_code \"o\"|class_code \"j\"' $input > $output"
	}
}

annotation_compare = segment { merge_genome_annotations + 
				size_selection +
                                compare_genome_annotations +
                                classcode_selection 
                                }
