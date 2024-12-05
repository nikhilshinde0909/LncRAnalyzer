data <- read.table(file="Heterocephalus_glaber.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Heterocephalus_glaber.logit.RData")
