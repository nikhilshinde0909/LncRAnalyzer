#!/usr/bin/env Rscript
library(dplyr)
library(ggplot2)
library(conflicted)
conflict_prefer("filter", "dplyr")
conflict_prefer("lag", "dplyr")

args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 3) {
  stop("Usage: get_lncRNA_classes.R feelnc_classes.txt lncRNA_classes.TSV Summary_classification.TSV \n")
}

input_file <- args[1]
lnc_class_file <- args[2]
summary_classification <-args[3]

data <- read.table(input_file, header = T, sep = '\t')
data <- data[data$isBest==1,]

antisense_exonic <- data[data$direction=='antisense' & data$location=='exonic',]
antisense_exonic$Class <- 'Antisense exonic'
intronic <- data[data$direction == 'sense' & data$location == 'intronic',]
intronic$Class <- 'Intronic'
upstream_lncRNA <- data[data$direction=='sense' & data$type=='intergenic'  & data$distance <= 2000 & data$location=='upstream',]
upstream_lncRNA$Class <- 'Upstream' 
downstream_lncRNA <- data[data$direction=='sense' & data$type=='intergenic'  & data$distance <= 2000 & data$location=='downstream',]
downstream_lncRNA$Class <- 'Downstream'
intergenic_lncRNA <- data[data$type=='intergenic' & data$distance > 2000,]
intergenic_lncRNA$Class <- 'Intergenic'
bidirectional_lncRNA <- data[data$direction=='antisense' & data$type=='intergenic' & data$distance <= 2000 & data$location=='upstream',]
bidirectional_lncRNA$Class <- 'Bidirectional'

lncRNA_classes <- bind_rows(antisense_exonic,intronic,upstream_lncRNA,
                            downstream_lncRNA, intergenic_lncRNA, bidirectional_lncRNA)
lncRNA_classes <- lncRNA_classes[,c(3,4,6,7,10,8,11)]
colnames(lncRNA_classes)[c(1,2)] <- c('lncRNA', 'referance_gene')
write.table(lncRNA_classes, lnc_class_file, sep = '\t', quote = F,
            row.names = F, col.names = T)
class_summary <- lncRNA_classes %>% group_by(Class) %>% summarise(Numbers =n())
write.table(class_summary, summary_classification, sep = '\t', quote = F,
            row.names = F, col.names = T)

tiff("LncRAnalyzer-summary/LncRAnalyzer_classes.tiff", units="cm", width = 15,
     height=10, res=300)
class_summary %>% ggplot(aes(x=Class, y=Numbers, fill = Numbers)) + 
  geom_bar(stat = "identity") +
  coord_flip() +
  scale_fill_gradient(low = '#2986cc', high = '#2986cc', name = "Count") +
  labs(x = "Various lncRNA classes", y = "Count") +
  theme(
    strip.text = element_text(size = 8), 
    axis.title = element_text(size = 10), 
    axis.text = element_text(size = 8), 
    legend.position = "none")
dev.off()
