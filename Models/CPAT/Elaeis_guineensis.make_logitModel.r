data <- read.table(file="Elaeis_guineensis.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Elaeis_guineensis.logit.RData")
