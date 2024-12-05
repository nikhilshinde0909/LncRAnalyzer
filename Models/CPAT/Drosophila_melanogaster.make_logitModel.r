data <- read.table(file="Drosophila_melanogaster.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Drosophila_melanogaster.logit.RData")
