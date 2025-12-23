#!/usr/bin/env Rscript

library(LncFinder)
library(seqinr)
library(e1071)

args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 5) {
  stop("Usage: Train_LncFinder.R threads training_mRNA training_lncRNA output_model output_freq\n")
}

threads <- as.integer(args[1])
training_mRNA <- args[2]
training_lncRNA <- args[3]
output_model <- args[4]
output_freq <- args[5]

mRNA <- seqinr::read.fasta(file = training_mRNA)
lncRNA <- seqinr::read.fasta(file = training_lncRNA)

frequencies <- make_frequencies(cds.seq = mRNA, lncRNA.seq = lncRNA, SS.features = FALSE, cds.format = "DNA", lnc.format = "DNA", check.cds = TRUE, ignore.illegal = TRUE)	
model <- build_model(mRNA.seq = mRNA, lncRNA.seq = lncRNA,frequencies.file = frequencies, SS.features = FALSE, lncRNA.format = "DNA", mRNA.format = "DNA", parallel.cores = threads, folds.num = 10, seed = 1, gamma.range = (2^seq(-5, 0, 1)), cost.range = c(1, 4, 8, 16, 24, 32))
saveRDS(model,output_model)
saveRDS(frequencies,output_freq)

