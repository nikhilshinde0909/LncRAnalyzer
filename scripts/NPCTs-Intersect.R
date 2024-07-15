#!/usr/bin/env Rscript

library(dplyr)
library(tidyverse)

# Check if the required number of arguments is provided
if (length(commandArgs(trailingOnly = TRUE)) != 5) {
  cat("Usage: Rscript NPCTs-Intersect.R CPAT_file CPC2_file RNAsamba_file LGC_file output_file\n")
  quit(save = "no", status = 1)
}

# Get input file paths from command-line arguments
CPAT_file <- commandArgs(trailingOnly = TRUE)[1]
CPC2_file <- commandArgs(trailingOnly = TRUE)[2]
RNAsamba_file <- commandArgs(trailingOnly = TRUE)[3]
LGC_file <- commandArgs(trailingOnly = TRUE)[4]
output_file <- commandArgs(trailingOnly = TRUE)[5]

# Read data from input files
CPAT <- read.table(CPAT_file, header = FALSE, sep = '\t')
CPC2 <- read.table(CPC2_file, header = FALSE, sep = '\t')
RNAsamba <- read.table(RNAsamba_file, sep = '\t', header = FALSE)
LGC <- read.table(LGC_file, sep = '\t', header = FALSE)


# Combine data using inner joins
data <- list(CPAT, CPC2, RNAsamba, LGC) %>%
  reduce(inner_join)

# Remove duplicates based on column V1
data <- data[!duplicated(data$V1),]

# Write the result to an output file
write.table(data, output_file, row.names = FALSE, col.names = FALSE, sep = '\t', quote = FALSE)
