#!/usr/bin/env Rscript

library(UpSetR)
library(RColorBrewer)
library(conflicted)
library(tidyverse)
conflict_prefer("filter", "dplyr")
conflict_prefer("lag", "dplyr")

# Get command-line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 6) {
  stop("Usage: NPCTS-venn.R CPAT_list CPC2_list RNAsamba_list LGC_list pfamscan_list lncfinder_list")
}

# Read data from input files
CPAT <- read.table(args[1], header = FALSE, sep = '\t')
CPC2 <- read.table(args[2], header = FALSE, sep = '\t')
RNAsamba <- read.table(args[3], header = FALSE, sep = '\t')
LGC <- read.table(args[4], header = FALSE, sep = '\t')
Pfamscan <- read.table(args[5], header = FALSE, sep = '\t')
LncFinder <- read.table(args[6], header = FALSE, sep = '\t')

# Results
data <- data.frame("NPCTs RNA Prediction Method"=c("RNAsamba", "CPAT", "CPC2", "LGC","PfamScan","LncFinder"), 
                   "Number of Predicted NPCTs"=c(length(RNAsamba$V1), length(CPAT$V1), length(CPC2$V1), length(LGC$V1), length(Pfamscan$V1),length(LncFinder$V1)))
write.table(data,'LncRAnalyzer-summary/LncRAnalyzer-NPCTs.TSV', row.names = F, col.names = T,
            sep = '\t', quote = F)

# Colors 
myCol <- brewer.pal(7, "Set1")
myCol <- myCol[-3]

# Upset
data1 <- list('RNAsamba'=RNAsamba$V1,
              'CPAT' = CPAT$V1,
              'CPC2' =  CPC2$V1,
              'LGC' = LGC$V1,
              'PfamScan'=Pfamscan$V1,
              'LncFinder' = LncFinder$V1)

binary_matrix <- fromList(data1)

# Create the upset plot
tiff("LncRAnalyzer-summary/LncRAnalyzer-NPCTs-UpSet.tiff", units="cm", width = 22.5,
     height = 15, res = 300)
upset(binary_matrix, 
      sets = names(data1), 
      sets.bar.color = myCol,    
      matrix.color = "#a64d79",   
      order.by = "freq", 
      keep.order = TRUE, 
      main.bar.color = "#93c47d",  
      matrix.dot.alpha = 0.6)
dev.off()
