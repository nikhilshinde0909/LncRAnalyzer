#!/usr/bin/env Rscript

library(dplyr)
library(tidyverse)

# Check if the required number of arguments is provided
if (length(commandArgs(trailingOnly = TRUE)) != 7) {
  cat("Usage: Rscript Lnc_Intersect.R FEELnc_file CPAT_file CPC2_file RNAsamba_file LGC_file Pfamscan_file output_file\n")
  quit(save = "no", status = 1)
}

# Get input file paths from command-line arguments
FEELnc_file <- commandArgs(trailingOnly = TRUE)[1]
CPAT_file <- commandArgs(trailingOnly = TRUE)[2]
CPC2_file <- commandArgs(trailingOnly = TRUE)[3]
RNAsamba_file <- commandArgs(trailingOnly = TRUE)[4]
LGC_file <- commandArgs(trailingOnly = TRUE)[5]
Pfamscan_file <- commandArgs(trailingOnly = TRUE)[6]
output_file <- commandArgs(trailingOnly = TRUE)[7] 

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
