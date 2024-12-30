/****************************************************************
 ** Stages to prepare ROC curve
 ** Author: Nikhil Shinde <sd1172@srmist.edu.in>
 ** Last Update: 30/05/2023
 ****************************************************************/

// Output directory
summary_dir="LncRAnalyzer-summary"

shuffle_CPS = {
    output.dir=shuffle_dir
    produce("FEELnc_shuffle_codpot.TSV") {
        exec "sed 1,1d ${output.dir}/feelnc_shuffle.codpot_RF.txt | cut -f 1,10 > $output"
    }
}

intergenic_CPS = {
    output.dir=intergenic_dir
    produce("FEELnc_intergenic_codpot.TSV") {
        exec "sed 1,1d ${output.dir}/feelnc_intergenic.codpot_RF.txt | cut -f 1,10 > $output"
    }
}

FEELnc_CPS = {
    output.dir=summary_dir
    from("FEELnc_shuffle_codpot.TSV","FEELnc_intergenic_codpot.TSV") produce("FEELnc_codpot.TSV") {
        exec "cat $input1 $input2 > $output"
    }
}

CPC2_codpot = {
    output.dir=summary_dir
    from("Putative.lnc_NPCTs.cpc2.txt") produce("CPC2_codpot.TSV") {
        exec "sed 1,1d $input | cut -f 1,7 > $output"
    }
}

lgc_codpot = {
    output.dir=summary_dir
    from("Putative.lnc_NPCTs.lgc.txt") produce("lgc_codpot.TSV") {
        exec "sed 1,11d $input | cut -f 1,4 > $output"
    }
}

CPAT_codpot = {
    output.dir=summary_dir
    from("CPAT_output.TSV") produce("CPAT_codpot.TSV"){
        exec "sed 1,1d $input | cut -f 1,6 > $output"
    }
}

RNAsamba_codpot = {
    output.dir=summary_dir
    from("Putative.lnc_NPCTs.rnasamba.TSV") produce("RNAsamba_codpot.TSV"){
        exec "sed 1,1d $input | cut -f 1,2 > $output"
    }
}

ROC_curve = {
    output.dir=summary_dir
    from("LncRAnalyzer-Lncs-intersect.txt","LncRAnalyzer-NPCTs-intersect.txt","FEELnc_codpot.TSV","CPAT_codpot.TSV","CPC2_codpot.TSV","RNAsamba_codpot.TSV","lgc_codpot.TSV") produce("LncRAnalyzer-ROC.log") {
        exec "$Rscript $lnc_roc_script $input1 $input2 $input3 $input4 $input5 $input6 $input7 > $output"
    }
}

LncRAnalyzer_ROC = segment{
    shuffle_CPS + intergenic_CPS + FEELnc_CPS + 
    CPC2_codpot + CPAT_codpot + RNAsamba_codpot + lgc_codpot + ROC_curve
}
