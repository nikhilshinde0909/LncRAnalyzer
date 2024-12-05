data <- read.table(file="Actinidia_chinensis.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Actinidia_chinensis.logit.RData")
