data <- read.table(file="Eucalyptus_grandis.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Eucalyptus_grandis.logit.RData")
