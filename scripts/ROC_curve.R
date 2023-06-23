setwd('/home/mpilab/ROC_curve/lnc_ROC/')

total <- read.table('total-lnc.codpot.txt', header = F, sep = '\t')
colnames(total) <- c("Gene","CodPot")
total <- total %>% group_by(Gene) %>% summarise(CodPot=paste0(mean(CodPot)))
rnasamba <- read.table('final_lnc_RNAs-rnasamba.list', header = F, sep = '\t')
colnames(rnasamba) <- 'Gene'
rnasamba$RNAsamba <- 1

cpat <- read.table('final_lnc_RNAs-CPAT.list', header = F, sep = '\t')
colnames(cpat) <- 'Gene'
cpat$CPAT <- 1

cpc2 <- read.table('final_lnc_RNAs-cpc2.list', header = F, sep = '\t')
colnames(cpc2) <- 'Gene'
cpc2$CPC2 <- 1

feelnc <- read.table('FEELnc_total.lnc.list', header = F, sep = '\t')
colnames(feelnc) <- 'Gene'
feelnc$FEELnc <- 1

library(dplyr)
library(tidyverse)

data <- list(total,rnasamba,feelnc,cpc2,cpat) %>% reduce(left_join)
data[is.na(data)] <- 0
rnasamba <- data %>% select(Gene,CodPot,RNAsamba)
colnames(rnasamba)[3] <- 'Rank'

feelnc <- data %>% select(Gene,CodPot,FEELnc)
colnames(feelnc)[3] <- 'Rank'

cpc2 <- data %>% select(Gene,CodPot,CPC2)
colnames(cpc2)[3] <- 'Rank'

cpat <- data %>% select(Gene,CodPot,CPAT)
colnames(cpat)[3] <- 'Rank'
##

#Calculate the rates
rate = function(dfs_final){
  ## order
  dfs_final = dfs_final[order(dfs_final$CodPot),]
  
  ## Cumulative sum
  dfs_final$cumtp=cumsum(dfs_final$Rank)
  dfs_final$cumtn=cumsum(1 - dfs_final$Rank)
  
  ## Normalize
  dfs_final$cumtp=dfs_final$cumtp/sum(dfs_final$Rank)
  dfs_final$cumtn=dfs_final$cumtn/sum(1 - dfs_final$Rank)
  return(dfs_final)
}

## Get values
final_rnasamba <- rate(rnasamba)
final_feelnc <- rate(feelnc)
final_cpc2 <- rate(cpc2)
final_cpat <- rate(cpat)

final_rnasamba$Method <- 'RNAsamba'
final_cpat$Method <- 'CPAT'
final_feelnc$Method <- 'FEELnc'
final_cpc2$Method <- 'CPC2'
all <- bind_rows(final_rnasamba, final_feelnc, final_cpc2, final_cpat)

#Make the plot
library(ggplot2)
cbPalette<-c("#79097e","#003d18","#ff8000","#f0006b")
p = ggplot(data=all,aes(x=cumtn,y=cumtp,group=Method,colour=Method)) + geom_line() 
p = p + geom_abline(intercept=0,slope=1,linetype=2) + xlim(0,1) +ylim(0,1) + theme_bw()
p = p + xlab('False Positive Rate') + ylab('True Positive Rate') + theme(text = element_text(size=12))
p = p + scale_colour_manual(values=cbPalette)
p=p+theme(legend.position=c(0.8,0.2))

tiff('ROC.tiff', width = 15, height = 12, units = 'cm', res = 400)
p + labs(colour="lncRNAs detection \n methods")
dev.off()

png('ROC.png', width = 15, height = 12, units = 'cm', res = 600)
p + labs(colour="lncRNAs detection \n methods")
dev.off()
