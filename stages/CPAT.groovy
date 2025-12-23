/***********************************************************
 ** Stages run lnc RNA analysis with CPAT with python 2.75
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 *********************************************************/

//Python 2.7 and CPC2
CPAT_dir="CPAT_out"

// load hexamer table and logit models
hexamer_table=codeBase+"/Models/CPAT/"+org_name+"_hexamer.TSV"
logit_model=codeBase+"/Models/CPAT/"+org_name+".logit.RData"
cutoff_file=codeBase+"/Models/CPAT/CPAT_cutoffs.TSV"


extract_cds = {
      output.dir=CPAT_dir
      if (file(hexamer_table).exists() || file(logit_model).exists()){
      exec "echo 'Hexamer table and logit models exist for organism'"
      } else {
      produce(org_name+".cds.fa",org_name+".mRNAs.fa"){
      exec """
      $gffread $annotation -g $genome -x $output1 ;
      $gffread $annotation -g $genome -x $output2
      """
      }
   }
}

build_hexamer_table = {
      output.dir = CPAT_dir
      if (file(hexamer_table).exists() || file(logit_model).exists()){
      exec "echo 'No need to build hexamer table'"
      } else {
      from(org_name+".cds.fa") produce(org_name+"_hexamer.TSV"){
      exec """
      $python2 $make_hexamer -c $input -n $known_lncRNAs_FA > $output
      """
      }
    }
}

build_logit_model = {
      output.dir = CPAT_dir
      if (file(hexamer_table).exists() || file(logit_model).exists()){
      exec "echo 'No need to build logit model'"
      } else {
      from(org_name+"_hexamer.TSV",org_name+".cds.fa") produce(org_name+".make_logitModel.r"){
      exec """
      $python2 $make_logit_model -x $input1 -c $input2 -n $known_lncRNAs_FA -o $output.prefix.prefix
      """
      } 
    }
}

run_CPAT = {
      output.dir = CPAT_dir
      if (file(hexamer_table).exists() || file(logit_model).exists()){
      from("Putative.lnc_NPCTs.fa") produce("CPAT_output.TSV"){
      exec """
      $python2 $CPAT -x $hexamer_table -g $input -d $logit_model $CPAT_options -o $output
      """
      }
      } else {
      from(org_name+"_hexamer.TSV", "Putative.lnc_NPCTs.fa") produce("CPAT_output.TSV"){
      exec """
      $python2 $CPAT -x $input1 -g $input2 -d ${output.dir}/${org_name}.logit.RData $CPAT_options -o $output
      """
      }
   }
}

get_cutoff = {
      output.dir = CPAT_dir
      produce("cutoff_value"){
      if(file(hexamer_table).exists() || file(logit_model).exists()){
      exec "grep $org_name $cutoff_file | cut -f2 > $output"
      } else {
      exec "$Rscript $fold10_crossval ${output.dir}/${org_name}.feature.xls ${output.dir}/${org_name}.pdf $org_name $output && rm -rf test*.xls"
      }
   }
}

CPAT_extract_fasta = {
      output.dir = CPAT_dir
      def cutoff = ("cat ${output.dir}/cutoff_value").execute().getText()
      println "Cutoff value: $cutoff"
      from("CPAT_output.TSV", "Putative.lnc_NPCTs.fa") produce("final_lnc_RNAs-CPAT.list", "final_lnc_RNAs-CPAT.fa", "final_NPCTs-CPAT.list", "final_NPCTs-CPAT.fa") {
      exec """
      sed 1,1d $input1 | awk -F '\t' -v cutoff=$cutoff '\$6 < cutoff'| cut -f1 > $output1 ;
      $seqtk subseq $input2 $output1 > $output2 ;
      sed 1,1d $input1| awk -F '\t' -v cutoff=$cutoff '\$6 >= cutoff' | cut -f1 > $output3 ;
      $seqtk subseq $input2 $output3 > $output4
      """
      }
}

cpat_based_coding_potentials = segment { extract_cds + build_hexamer_table + 
                               build_logit_model + run_CPAT + get_cutoff + 
                               CPAT_extract_fasta }
