#!/usr/bin/env Rscript
library(dplyr)
library(conflicted)
library(tidyverse)
conflict_prefer("filter", "dplyr")
conflict_prefer("lag", "dplyr")

# cutoffs
lower_cutoff = 0.3
upper_cutoff = 0.7

# Get command-line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 7) {
  stop("Usage: ROC_curve.R Lnc-intersect NPCTs-intersect FEELnc_codpot CPAT_codpot CPC2_codpot RNAsamba_codpot LGC_codpot")
}

# Define the sigmoid function
sigmoid <- function(x, k = 1) {
  return(1 / (1 + exp(-k * x)))
}

# intersect
intersect1 <- read.table(args[1], header = FALSE, sep = '\t')
colnames(intersect1) <- 'Transcript'
intersect1$Label <- 1

intersect2 <- read.table(args[2], header = FALSE, sep = '\t')
colnames(intersect2) <- 'Transcript'
intersect2$Label <- 1
intersect <- bind_rows(intersect1, intersect2)

# FEELnc
FEELnc <- read.table(args[3], header = FALSE, sep = '\t')
colnames(FEELnc) <- c('Transcript', 'CodPot')
FEELnc$CodPot <- as.numeric(FEELnc$CodPot)
FEELnc_T <- FEELnc[FEELnc$CodPot < lower_cutoff,]
FEELnc_F <- FEELnc[FEELnc$CodPot > lower_cutoff,]
FEELnc <- bind_rows(FEELnc_T, FEELnc_F)
FEELnc <- FEELnc[!duplicated(FEELnc$Transcript),]

# CPAT
CPAT <- read.table(args[4], header = TRUE, sep = '\t')
colnames(CPAT) <- c('Transcript', 'CodPot')
CPAT$CodPot <- as.numeric(CPAT$CodPot)
CPAT_T <- CPAT[CPAT$CodPot < lower_cutoff,]
CPAT_F <- CPAT[CPAT$CodPot > lower_cutoff,]
CPAT <- bind_rows(CPAT_T, CPAT_F)

# CPC2
CPC2 <- read.table(args[5], header = TRUE, sep = '\t')
colnames(CPC2) <- c('Transcript', 'CodPot')
CPC2$CodPot <- as.numeric(CPC2$CodPot)
CPC2_T <- CPC2[CPC2$CodPot < lower_cutoff,]
CPC2_F <- CPC2[CPC2$CodPot > lower_cutoff,]
CPC2 <- bind_rows(CPC2_T, CPC2_F)

# RNAsamba
RNAsamba <- read.table(args[6], header = TRUE, sep = '\t')
colnames(RNAsamba) <- c('Transcript', 'CodPot')
RNAsamba$CodPot <- as.numeric(RNAsamba$CodPot)
RNAsamba_T <- RNAsamba[RNAsamba$CodPot < lower_cutoff,]
RNAsamba_F <- RNAsamba[RNAsamba$CodPot > lower_cutoff,]
RNAsamba <- bind_rows(RNAsamba_T, RNAsamba_F)

# LGC
LGC <- read.table(args[7], header = TRUE, sep = '\t')
colnames(LGC) <- c('Transcript', 'CodPot')
LGC$CodPot <- as.numeric(LGC$CodPot)
LGC$CodPot <- sigmoid(x = LGC$CodPot, k = 1)
LGC_T <- LGC[LGC$CodPot < lower_cutoff,]
LGC_F <- LGC[LGC$CodPot > lower_cutoff,]
LGC <- bind_rows(LGC_T, LGC_F)

# Assign labels
FEELnc <- list(FEELnc, intersect) %>% reduce(left_join)
FEELnc[is.na(FEELnc)] <- 0

CPC2 <- list(CPC2, intersect) %>% reduce(left_join)
CPC2[is.na(CPC2)] <- 0

CPAT <- list(CPAT, intersect) %>% reduce(left_join)
CPAT[is.na(CPAT)] <- 0

RNAsamba <- list(RNAsamba, intersect) %>% reduce(left_join)
RNAsamba[is.na(RNAsamba)] <- 0

LGC <- list(LGC, intersect) %>% reduce(left_join)
LGC[is.na(LGC)] <- 0

# Calculate the rates
rate <- function(dfs_final) {
  # Order
  dfs_final <- dfs_final[order(dfs_final$CodPot),]
  dfs_final <- dfs_final[!duplicated(dfs_final$Transcript),]
  
  # Cumulative sum
  dfs_final$cumtp <- cumsum(dfs_final$Label)
  dfs_final$cumtn <- cumsum(1 - dfs_final$Label)
  
  # Normalize
  dfs_final$cumtp <- dfs_final$cumtp / sum(dfs_final$Label)
  dfs_final$cumtn <- dfs_final$cumtn / sum(1 - dfs_final$Label)
  return(dfs_final)
}

# Get values
final_rnasamba <- rate(RNAsamba)
final_feelnc <- rate(FEELnc)
final_cpc2 <- rate(CPC2)
final_cpat <- rate(CPAT)
final_lgc <- rate(LGC)

final_rnasamba$Method <- 'RNAsamba'
final_cpat$Method <- 'CPAT'
final_feelnc$Method <- 'FEELnc'
final_cpc2$Method <- 'CPC2'
final_lgc$Method <- 'LGC'

all <- bind_rows(final_rnasamba, final_feelnc, final_cpc2, final_cpat, final_lgc)

# Make the plot
library(ggplot2)
cbPalette <- c("#003d18", "#000080", "#cc0000", "#ff8000", "#8fce00")
p <- all %>% ggplot(aes(x = cumtn, y = cumtp, group = Method, colour = Method)) +
  geom_line() +
  geom_abline(intercept = 0, slope = 1, linetype = 1) +
  xlim(0, 1) + ylim(0, 1) + theme_bw() +
  xlab('False Positive Rate') + ylab('True Positive Rate') +
  theme(text = element_text(size = 18)) +
  scale_colour_manual(values = cbPalette) +
  theme(legend.position = c(0.72, 0.2), legend.title = element_text(size = 12), legend.text = element_text(size = 10)) +
  labs(colour = 'LncRNAs identification \n methods')

tiff('LncRAnalyzer-summary/Lnc_ROC.tiff', width = 15, height = 15, units = 'cm', res = 400)
p
dev.off()
