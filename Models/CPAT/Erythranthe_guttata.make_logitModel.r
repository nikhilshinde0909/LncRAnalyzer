data <- read.table(file="Erythranthe_guttata.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Erythranthe_guttata.logit.RData")
