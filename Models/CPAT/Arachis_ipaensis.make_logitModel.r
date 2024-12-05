data <- read.table(file="Arachis_ipaensis.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Arachis_ipaensis.logit.RData")
