#!/usr/bin/env python3

import glob, os, sys
from pybiomart import Server
import pandas as pd

def main(ensembl_name):
    server = Server(host='http://plants.ensembl.org')
    version = server.list_marts()
    version = version[version['display_name'].str.contains('Ensembl Plants Genes')]
    if version.empty:
        print("No Ensembl Plants Genes mart found.")
        sys.exit(1)
 
    vertual_schema = str(version.iloc[0]['name'])
    version1 = str(version.iloc[0]['display_name']).split(" ")[3]
    mart = server['plants_mart']
    df = mart.list_datasets()
    
    df2 = df[df['name'].str.contains(ensembl_name, case=False) | 
              df['display_name'].str.contains(ensembl_name, case=False)]
    
    if df2.empty:
        print(f"No datasets found matching '{ensembl_name}'.")
        sys.exit(1)

    matching_names = df2['name']
#.apply(lambda x: " ".join(x.split()[:2])).unique()
    print("Following datasets are available at ensembl plants release " + version1 + " for " + ensembl_name)
    for name in matching_names:
        print(name)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: find_species_in_ensembl.py <org_name>")
        sys.exit(1)
    
    ensembl_name = sys.argv[1]
    main(ensembl_name)
