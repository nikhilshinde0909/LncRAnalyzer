data <- read.table(file="Vitis_vinifera.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Vitis_vinifera.logit.RData")
