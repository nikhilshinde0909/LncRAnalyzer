data <- read.table(file="Eutrema_salsugineum.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Eutrema_salsugineum.logit.RData")
