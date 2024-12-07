#!/usr/bin/env Rscript

library(dplyr)
library(tidyverse)

# Check if the required number of arguments is provided
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 7) {
  stop("Usage: Rscript Lnc_Intersect.R FEELnc_file CPAT_file CPC2_file RNAsamba_file LGC_file Pfamscan_file output_file\n")
}

# Get input file paths from command-line arguments
FEELnc_file <- args[1]
CPAT_file <- args[2]
CPC2_file <- args[3]
RNAsamba_file <- args[4]
LGC_file <- args[5]
Pfamscan_file <- args[6]
output_file <- args[7] 

# Read data from input files
FEELnc <- read.table(FEELnc_file, header = FALSE, sep = '\t')
CPAT <- read.table(CPAT_file, header = FALSE, sep = '\t')
CPC2 <- read.table(CPC2_file, header = FALSE, sep = '\t')
RNAsamba <- read.table(RNAsamba_file, sep = '\t', header = FALSE)
LGC <- read.table(LGC_file, sep = '\t', header = FALSE)
Pfamscan <- read.table(Pfamscan_file, sep = '\t', header = FALSE)

# Combine data using inner joins
data <- list(FEELnc, CPAT, CPC2, RNAsamba, LGC, Pfamscan) %>%
  reduce(inner_join)

# Remove duplicates based on column V1
data <- data[!duplicated(data$V1),]

# Write the result to an output file
write.table(data, output_file, row.names = FALSE, col.names = FALSE, sep = '\t', quote = FALSE)
