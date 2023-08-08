library(tidyverse)
library(venn)
library(RColorBrewer)

# Get command-line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 5) {
  stop("Usage: Lnc-venn.R CPAT_list CPC2_list RNAsamba_list FEELnc_list out_dir")
}

setwd(args[5])

# Read data from input files
CPAT <- read.table(args[1], header = FALSE, sep = '\t')
CPC2 <- read.table(args[2], header = FALSE, sep = '\t')
RNAsamba <- read.table(args[3], header = FALSE, sep = '\t')
FEELnc <- read.table(args[4], header = FALSE, sep = '\t')

# Venn
data1 <- list('FEELnc'=  FEELnc$V1,
              'CPAT' = CPAT$V1,
              'CPC2' =  CPC2$V1,
              'RNAsamba'=RNAsamba$V1)

tiff("LncRAnalyzer-lnc_venn.tiff", units="cm", width = 15,
     height=12, res=300)
venn(data1, ilcs = 1.1, sncs = 1.3, ilabels = TRUE, ellipse = TRUE, opacity = 0.30, ggplot = TRUE, box = FALSE, 
     zcolor = myCol, cex = 0.8)
dev.off()
