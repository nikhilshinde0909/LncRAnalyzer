#!/usr/bin/env Rscript
library(venn)
library(RColorBrewer)
library(conflicted)
library(tidyverse)
conflict_prefer("filter", "dplyr")
conflict_prefer("lag", "dplyr")

# Get command-line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 6) {
  stop("Usage: Lnc-venn.R CPAT_list CPC2_list RNAsamba_list FEELnc_list lgc_list pfamscan_list")
}

# Read data from input files
CPAT <- read.table(args[1], header = FALSE, sep = '\t')
CPC2 <- read.table(args[2], header = FALSE, sep = '\t')
RNAsamba <- read.table(args[3], header = FALSE, sep = '\t')
FEELnc <- read.table(args[4], header = FALSE, sep = '\t')
LGC <- read.table(args[5], header = FALSE, sep = '\t')
Pfamscan <- read.table(args[6], header = FALSE, sep = '\t')

# Results
data <- data.frame("Lnc RNA Prediction Method"=c("FEELnc", "RNAsamba", "CPAT", "CPC2", "LGC", "PfamScan"), 
                   "Number of Predicted Lncs"=c(length(FEELnc$V1),length(RNAsamba$V1), length(CPAT$V1), length(CPC2$V1), length(LGC$V1), length(Pfamscan$V1)))
write.table(data,'LncRAnalyzer-summary/LncRAnalyzer-Lncs.TSV', row.names = F, col.names = T,
            sep = '\t', quote = F)

# Colors
myCol <- brewer.pal(8, "Dark2")

# Venn
data1 <- list('FEELnc'=  FEELnc$V1,
              'CPAT' = CPAT$V1,
              'CPC2' =  CPC2$V1,
              'RNAsamba'=RNAsamba$V1,
             'LGC' = LGC$V1,
             'PfamScan' = Pfamscan$V1)

tiff("LncRAnalyzer-summary/LncRAnalyzer-Lncs-Venn.tiff", units="cm", width = 15,
     height=15, res=300)
venn(data1, ilcs = 1.0, sncs = 1.1, ilabels = TRUE , ellipse = TRUE, opacity = 0.30, ggplot = TRUE, box = FALSE, 
     zcolor = myCol, cex = 0.8)
dev.off()
