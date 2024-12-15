#!/usr/bin/env Rscript

args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 3) {
  stop("Usage: DESeq2.R exp_design count_matrix output_file\n")
}

exp_design <- args[1]
count_matrix <- args[2]
output_file <-  args[3]

# read experimental design
coldata <- read.table(exp_design, header=TRUE, sep = '\t',
                       row.names=1)
colnames(coldata) <- 'Condition'
coldata$Condition <- factor(coldata$Condition)

# read count matrix
countdata <- read.table(count_matrix, header=TRUE, sep = '\t',
                        row.names=1)
countdata <- countdata[,c(6:ncol(countdata))]
countdata <- countdata[,rownames(coldata)]
countdata <- as.matrix(countdata)
head(countdata)

library(DESeq2)

# import data
dds <- DESeqDataSetFromMatrix(countData=countdata, colData=coldata, design=~Condition)
dds

# Run the DESeq pipeline
dds <- DESeq(dds)


# Regularized log transformation for clustering/heatmaps, etc
rld <- rlogTransformation(dds)
head(assay(rld))
hist(assay(rld))

# Get differential expression results
res <- results(dds)
table(res$padj<0.05)

## Order by adjusted p-value
res <- res[order(res$padj), ]

## Merge with normalized count data
resdata <- merge(as.data.frame(res), as.data.frame(counts(dds, normalized=TRUE)), by="row.names", sort=FALSE)
names(resdata)[1] <- "Transcript"
head(resdata)
resdata <- resdata[!is.na(resdata$padj),]

## Write results
write.table(resdata, output_file, row.names = F,
            col.names = T, sep = '\t', quote = F)
