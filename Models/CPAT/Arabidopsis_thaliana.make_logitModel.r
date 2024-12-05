data <- read.table(file="Arabidopsis_thaliana.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Arabidopsis_thaliana.logit.RData")
