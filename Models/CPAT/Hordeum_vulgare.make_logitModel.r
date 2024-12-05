data <- read.table(file="Hordeum_vulgare.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Hordeum_vulgare.logit.RData")
