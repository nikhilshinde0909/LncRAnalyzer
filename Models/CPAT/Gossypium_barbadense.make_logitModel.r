data <- read.table(file="Gossypium_barbadense.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Gossypium_barbadense.logit.RData")
