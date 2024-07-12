#!/usr/bin/env Rscript
library(dplyr)
library(tidyverse)
cutoff=0.5

# Get command-line arguments
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 5) {
  stop("Usage: ROC_curve.R Lnc-intersect FEELnc_codpot CPAT_codpot CPC2_codpot RNAsamba_codpot")
}

intersect <- read.table(args[1], 
                        header = F, sep = '\t')
colnames(intersect) <- 'lncRNA'
intersect$Label <- 1
# FEELnc
FEELnc <- read.table(args[2], header = F, sep = '\t')
colnames(FEELnc) <- c('lncRNA', 'CodPot')
FEELnc <- FEELnc[FEELnc$CodPot < cutoff,]
FEELnc$CodPot <- as.numeric(FEELnc$CodPot)
FEELnc <- FEELnc %>% group_by(lncRNA) %>% slice_min(CodPot)

#CPAT
CPAT <- read.table(args[3], header = T, sep = '\t')
colnames(CPAT) <- c('lncRNA', 'CodPot')
CPAT <- CPAT[CPAT$CodPot < cutoff,]
CPAT$CodPot <- as.numeric(CPAT$CodPot)

# CPC2
CPC2 <- read.table(args[4], header = T, sep = '\t')
colnames(CPC2) <- c('lncRNA', 'CodPot')
CPC2 <- CPC2[CPC2$CodPot < cutoff,]
CPC2$CodPot <- as.numeric(CPC2$CodPot)

# RNAsamba
RNAsamba <- read.table(args[5], header = T, sep = '\t')
colnames(RNAsamba) <- c('lncRNA', 'CodPot')
RNAsamba <- RNAsamba[RNAsamba$CodPot < cutoff,]
RNAsamba$CodPot <- as.numeric(RNAsamba$CodPot)

# Assign lebels
FEELnc <- list(FEELnc,intersect) %>% reduce(left_join)
FEELnc[is.na(FEELnc)] <- 0

CPC2 <- list(CPC2,intersect) %>% reduce(left_join)
CPC2[is.na(CPC2)] <- 0

CPAT <- list(CPAT,intersect) %>% reduce(left_join)
CPAT[is.na(CPAT)] <- 0

RNAsamba <- list(RNAsamba,intersect) %>% reduce(left_join)
RNAsamba[is.na(RNAsamba)] <- 0


#Calculate the rates
rate = function(dfs_final){
  ## order
  dfs_final = dfs_final[order(dfs_final$CodPot),]
  
  ## Cumulative sum
  dfs_final$cumtp=cumsum(dfs_final$Label)
  dfs_final$cumtn=cumsum(1 - dfs_final$Label)
  
  ## Normalize
  dfs_final$cumtp=dfs_final$cumtp/sum(dfs_final$Label)
  dfs_final$cumtn=dfs_final$cumtn/sum(1 - dfs_final$Label)
  return(dfs_final)
}

## Get values
final_rnasamba <- rate(RNAsamba)
final_feelnc <- rate(FEELnc)
final_cpc2 <- rate(CPC2)
final_cpat <- rate(CPAT)

final_rnasamba$Method <- 'RNAsamba'
final_cpat$Method <- 'CPAT'
final_feelnc$Method <- 'FEELnc'
final_cpc2$Method <- 'CPC2'
all <- bind_rows(final_rnasamba, final_feelnc, final_cpc2, final_cpat)

#Make the plot
library(ggplot2)
cbPalette<-c("#003d18","#000080","#cc0000","#ff8000")
p = ggplot(data=all,aes(x=cumtn,y=cumtp,group=Method,colour=Method)) + geom_line() 
p = p + geom_abline(intercept=0,slope=1,linetype=1) + xlim(0,1) +ylim(0,1) + theme_bw()
p = p + xlab('False Positive Rate') + ylab('True Positive Rate') + theme(text = element_text(size=18))
p = p + scale_colour_manual(values=cbPalette)
p=p+theme(legend.position=c(0.72,0.2))

tiff('LncRAnalyzer-summary/Lnc_ROC.tiff', width = 15, height = 15, units = 'cm', res = 400)
p + labs(colour="lncRNAs detection \n methods")
dev.off()
