data <- read.table(file="Arabidopsis_lyrata.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Arabidopsis_lyrata.logit.RData")
