#!/usr/bin/env Rscript

library(LncFinder)
library(seqinr)
library(e1071)
library(dplyr)
library(tidyverse)
library(conflicted)
conflict_prefer("filter", "dplyr")
conflict_prefer("lag", "dplyr")


args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 7) {
  stop("Usage: Run_LncFinder.R threads model frequencies candidate_transcript Lncfinder_output output_lnc_list output_NPCTs_list\n")
}

threads <- as.integer(args[1])
model_file <- args[2]
frequencies_file <- args[3]
candidate_transcript <- args[4]
Lncfinder_output <- args[5]
output_lnc_list <- args[6]
output_NPCTs_list <- args[7]

model <- readRDS(file = model_file)
frequencies = readRDS(file = frequencies_file)

Seqs <- seqinr::read.fasta(file = candidate_transcript)
results <- LncFinder::lnc_finder(Seqs, SS.features = FALSE, format = "DNA", frequencies.file = frequencies, svm.model = model, parallel.cores = threads)
results <- results %>% rownames_to_column(var = "Transcript")
write.table(results, file = Lncfinder_output, sep = "\t", row.names = FALSE, col.names = TRUE, quote = FALSE)
noncoding <- results[results$Pred=="NonCoding",]
write.table(noncoding$Transcript, file = output_lnc_list, sep = "\t", row.names = FALSE, col.names = FALSE, quote = FALSE)
coding <- results[results$Pred=="Coding",]
write.table(coding$Transcript, file = output_NPCTs_list, sep = "\t", row.names = FALSE, col.names = FALSE, quote = FALSE)
