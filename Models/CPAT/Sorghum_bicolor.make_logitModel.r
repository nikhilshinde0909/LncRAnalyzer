data <- read.table(file="Sorghum_bicolor.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Sorghum_bicolor.logit.RData")
