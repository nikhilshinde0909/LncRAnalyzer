/***********************************************************
 ** Stages to preform annotation compare classcode selection 
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Output directory

lnc_NPCTs_dir="Putative_lnc-NPCTs"

putative_lnc_npc_transcripts_list = {
      output.dir=lnc_NPCTs_dir
      from("gffcompare.annotated.classcode_selected_lnc-npcts.gtf") produce("Putative.lnc-NPCTs.list"){
        exec "grep -E -w -v 'cmp_ref' $input |cut -d ';' -f 1|cut -f 9|sed 's/transcript_id //g;s/\"//g' > $output"
      }
}

putative_lnc_NPCTs = {
      output.dir=lnc_NPCTs_dir
      from("gffcompare.annotated.classcode_selected_lnc-npcts.gtf") produce("Putative.lnc_NPCTs.gtf","Putative.lnc_NPCTs.fa"){
        exec """
	grep -E -w -v 'cmp_ref' $input > $output1 ;
	${gffread} $output1 -g $genome -w $output2
	"""
      }
}

lnc_npc_transcript_selection = segment { putative_lnc_npc_transcripts_list +
					putative_lnc_NPCTs }
