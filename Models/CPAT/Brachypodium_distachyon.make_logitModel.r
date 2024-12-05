data <- read.table(file="Brachypodium_distachyon.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Brachypodium_distachyon.logit.RData")
