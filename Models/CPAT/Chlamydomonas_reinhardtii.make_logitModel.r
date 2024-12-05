data <- read.table(file="Chlamydomonas_reinhardtii.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Chlamydomonas_reinhardtii.logit.RData")
