#!/usr/bin/env Rscript

library(venn)
library(RColorBrewer)
library(conflicted)
library(tidyverse)
conflict_prefer("filter", "dplyr")
conflict_prefer("lag", "dplyr")

# Get command-line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 5) {
  stop("Usage: NPCTS-venn.R CPAT_list CPC2_list RNAsamba_list LGC_list pfamscan_list")
}

# Read data from input files
CPAT <- read.table(args[1], header = FALSE, sep = '\t')
CPC2 <- read.table(args[2], header = FALSE, sep = '\t')
RNAsamba <- read.table(args[3], header = FALSE, sep = '\t')
LGC <- read.table(args[4], header = FALSE, sep = '\t')
Pfamscan <- read.table(args[5], header = FALSE, sep = '\t')

# Results
data <- data.frame("NPCTs RNA Prediction Method"=c("RNAsamba", "CPAT", "CPC2", "LGC","PfamScan"), 
                   "Number of Predicted NPCTs"=c(length(RNAsamba$V1), length(CPAT$V1), length(CPC2$V1), length(LGC$V1), length(Pfamscan$V1)))
write.table(data,'LncRAnalyzer-summary/LncRAnalyzer-NPCTs.TSV', row.names = F, col.names = T,
            sep = '\t', quote = F)

# Colors 
myCol <- brewer.pal(8, "Accent")

# Venn
data1 <- list('RNAsamba'=RNAsamba$V1,
              'CPAT' = CPAT$V1,
              'CPC2' =  CPC2$V1,
              'LGC' = LGC$V1,
              'PfamScan'=Pfamscan$V1)

tiff("LncRAnalyzer-summary/LncRAnalyzer-NPCTs-Venn.tiff", units="cm", width = 15,
     height=15, res=300)
venn(data1, ilcs = 1.0, sncs = 1.1, ilabels = TRUE, ellipse = TRUE, opacity = 0.30, ggplot = TRUE, box = FALSE, 
     zcolor = myCol, cex = 0.8)
dev.off()
