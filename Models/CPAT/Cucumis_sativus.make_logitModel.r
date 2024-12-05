data <- read.table(file="Cucumis_sativus.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Cucumis_sativus.logit.RData")
