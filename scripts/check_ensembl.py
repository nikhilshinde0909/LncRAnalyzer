#!/usr/bin/env python3

import glob, os, sys
from pybiomart import Server
import pandas as pd


ensembl_name = sys.argv[1]

#Ensembl mart
server = Server(host='http://plants.ensembl.org')
version = server.list_marts()
version = version[version['display_name'].str.contains('Ensembl Plants Genes')]
version = str(version.iloc[0]['display_name']).split(" ")[2]
mart = server['plants_mart'] 
df = mart.list_datasets()
df2 = df[df['name'].str.contains(str(ensembl_name))]
if df2.empty == True:
    df2 = df[df['display_name'].str.contains(str(ensembl_name))]
sys.stdout.write(str(df2.iloc[0]['name']).split("_")[0])
sys.exit(0)
