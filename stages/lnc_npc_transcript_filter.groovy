/***********************************************************
 ** Stages to perform annotation: compare class code selection 
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Output directory

lnc_NPCTs_dir="Putative_lnc-NPCTs"

putative_lnc_npcts_gtf = {
      output.dir=lnc_NPCTs_dir
      from("genome_merged.gtf","Putative.lnc-NPCTs.list") produce("Putative.lnc_NPCTs.gtf"){
      exec "$python3 $subset_gtf $input1 $input2 $output"
      }
}

putative_lnc_npcts_fa = {
      output.dir=lnc_NPCTs_dir
      from("Putative.lnc_NPCTs.gtf") produce("Putative.lnc_NPCTs.fa"){
      exec """
      $gffread $input -g $genome -w temp.fa ;
      $python3 $seqclean temp.fa $output && rm temp.fa
      """
      }
}

lnc_npc_transcript_selection = segment { putative_lnc_npcts_gtf +
					putative_lnc_npcts_fa }
