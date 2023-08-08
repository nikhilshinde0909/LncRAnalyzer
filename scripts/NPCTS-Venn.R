library(tidyverse)
library(venn)
library(RColorBrewer)

# Get command-line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 4) {
  stop("Usage: NPCTS-venn.R CPAT_list CPC2_list RNAsamba_list output_file")
}

# Read data from input files
CPAT <- read.table(args[1], header = FALSE, sep = '\t')
CPC2 <- read.table(args[2], header = FALSE, sep = '\t')
RNAsamba <- read.table(args[3], header = FALSE, sep = '\t')

# Venn
data1 <- list('CPAT' = CPAT$V1,
              'CPC2' =  CPC2$V1,
              'RNAsamba'=RNAsamba$V1)

tiff(args[1], units="cm", width = 15,
     height=12, res=300)
venn(data1, ilcs = 1.1, sncs = 1.3, ilabels = TRUE, ellipse = TRUE, opacity = 0.30, ggplot = TRUE, box = FALSE, 
     zcolor = myCol, cex = 0.8)
dev.off()
