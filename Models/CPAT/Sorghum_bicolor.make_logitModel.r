data <- read.table(file="CPAT/Sorghum_bicolor.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("CPAT/Sorghum_bicolor.logit.RData")
