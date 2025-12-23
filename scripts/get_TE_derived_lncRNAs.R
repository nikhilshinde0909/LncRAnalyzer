#!/usr/bin/env Rscript
library(dplyr)
library(ggplot2)
library(conflicted)
conflict_prefer("filter", "dplyr")
conflict_prefer("lag", "dplyr")

args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 8) {
  stop("Usage: get_TE_derived_lncRNAs.R LTR_file LINE_file SINE_file MITE_file TIR_file Helitron_file TE_derived_lnRNAs TE_derived_summary \n")
}

LTR_file <- args[1]
LINE_file <- args[2]
SINE_file <- args[3]
MITE_file <- args[4]
TIR_file <- args[5]
Helitron_file <- args[6]
TE_derived_lncRNAs <- args[7]
TE_summary_file <-args[8]

# LTR
LTR <- read.table(LTR_file, header = F, sep = '\t')
LTR <- LTR[LTR$V1==LTR$V7,]
LTR <- LTR[-7]
LTR$V1 <- as.character(LTR$V1)
LTR$Class <- 'LTR'

#LINE
LINE <- read.table(LINE_file, header = F, sep = '\t')
LINE <- LINE[LINE$V1==LINE$V7,]
LINE <- LINE[-7]
LINE$V1 <- as.character(LINE$V1)
LINE$Class <- 'LINE'

# SINE
SINE <- read.table(SINE_file, header = F, sep = '\t')
SINE <- SINE[SINE$V1==SINE$V7,]
SINE <- SINE[-7]
SINE$V1 <- as.character(SINE$V1)
SINE$Class <- 'SINE'

# MITE
MITE <- read.table(MITE_file, header = F, sep = '\t')
MITE <- MITE[MITE$V1==MITE$V7,]
MITE <- MITE[-7]
MITE$V1 <- as.character(MITE$V1)
MITE$Class <- 'MITE'

# TIR
TIR <- read.table(TIR_file, header = F, sep = '\t')
TIR <- TIR[TIR$V1==TIR$V7,]
TIR <- TIR[-7]
TIR$V1 <- as.character(TIR$V1)
TIR$Class <- 'TIR'

# Helitron
Helitron <- read.table(Helitron_file, header = F, sep = '\t')
Helitron <- Helitron[Helitron$V1==Helitron$V7,]
Helitron <- Helitron[-7]
Helitron$V1 <- as.character(Helitron$V1)
Helitron$Class <- 'Helitron'

data <- bind_rows(LTR,LINE,SINE,MITE,TIR,Helitron)
data <- data[!duplicated(data$V4),]
data <- data[,c(-5,-10)]
colnames(data) <- c('Chromosome','Lnc_start','Lnc_end','LncRNA','Strand','TE_start','TE_end','TE','TE_strand','Class')
write.table(data,TE_derived_lncRNAs, row.names = F, col.names = T, sep = '\t', quote = F)

TE_summary <- data %>% group_by(Class) %>% summarise(Count=n())
write.table(TE_summary,TE_summary_file, row.names = F, col.names = T, sep = '\t', quote = F)

tiff("LncRAnalyzer-summary/TE-derived-lncRNAs.tiff", units="cm", width = 15,
     height=10, res=300)
TE_summary %>% ggplot(aes(x=Class, y=Count, fill = Count)) + 
  geom_bar(stat = "identity") +
  coord_flip() +
  scale_fill_gradient(low = '#6aa84f', high = '#6aa84f', name = "Count") +
  labs(x = "TE derived lncRNAs", y = "Count") +
  theme(
    strip.text = element_text(size = 8), 
    axis.title = element_text(size = 10), 
    axis.text = element_text(size = 8), 
    legend.position = "none")
dev.off()
