library(venn)
library(RColorBrewer)
library(conflicted)
library(tidyverse)
conflict_prefer("filter", "dplyr")
conflict_prefer("lag", "dplyr")

# Get command-line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 3) {
  stop("Usage: NPCTS-venn.R CPAT_list CPC2_list RNAsamba_list")
}

# Read data from input files
CPAT <- read.table(args[1], header = FALSE, sep = '\t')
CPC2 <- read.table(args[2], header = FALSE, sep = '\t')
RNAsamba <- read.table(args[3], header = FALSE, sep = '\t')

# Colors 
myCol <- brewer.pal(8, "Accent")

# Venn
data1 <- list('RNAsamba'=RNAsamba$V1
              'CPAT' = CPAT$V1,
              'CPC2' =  CPC2$V1)

tiff("LncRAnalyzer-summary/LncRAnalyzer-NPCTs-Venn.tiff", units="cm", width = 15,
     height=12, res=300)
venn(data1, ilcs = 1.0, sncs = 1.1, ilabels = TRUE, ellipse = TRUE, opacity = 0.30, ggplot = TRUE, box = FALSE, 
     zcolor = myCol, cex = 0.8)
dev.off()
