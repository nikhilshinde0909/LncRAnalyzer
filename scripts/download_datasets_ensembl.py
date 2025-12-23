#!/usr/bin/env python

import sys
import glob, os, sys
from pybiomart import Server
from pybiomart import Dataset
import pandas as pd
from ftplib import FTP

ensembl_name = sys.argv[1]


#Ensembl mart
server = Server(host='http://plants.ensembl.org')
version = server.list_marts()
version = version[version['display_name'].str.contains('Ensembl Plants Genes')]
vertual_schema = str(version.iloc[0]['name'])
version = str(version.iloc[0]['display_name']).split(" ")[3]
print("Ensembl version " + version)
mart = server['plants_mart'] 
df = mart.list_datasets()
df = df[df['display_name'].str.contains(ensembl_name)]
if df.empty:
   df = mart.list_datasets()
   df = df[df['name'].str.contains(ensembl_name)]

#print("Assembly version " + str(df.iloc[0]['display_name']).split("(")[1].split(")")[0])
#print(str(df.iloc[0]['name']).split("_")[0] + "_" + str(df.iloc[0]['name']).split("_")[1])
print(str(df.iloc[0]['name']))
dfx = str(df.iloc[0]['name'])

dataset = Dataset(name= dfx, host= 'http://plants.ensembl.org', virtual_schema= vertual_schema , 
                  path ='/biomart/martservice' , port= 80)

df2 = dataset.query(attributes=['ensembl_gene_id', 'external_gene_name', 'gene_biotype'], filters={'biotype':'rRNA'})
#print(df2.shape)
dfToList = df2['Gene stable ID'].tolist()
#print(dfToList)

###For lnc identification
df3 = dataset.query(attributes=['ensembl_transcript_id', 'transcript_biotype'])
#print(df3.shape)
df3.to_csv('./transcript_biotypes',sep='\t', index=False)
df4 = dataset.query(attributes=['ensembl_gene_id', 'ensembl_transcript_id', 'ensembl_peptide_id', 'description', 'chromosome_name', 'strand','band','transcript_start','transcript_end','transcript_length',"external_gene_name", 'gene_biotype','transcript_biotype'])
#print(df4.shape)
df4.to_csv('./ensembl_data.txt',sep='\t', index=False)

#Download rRNA fasta
import requests
from requests.adapters import HTTPAdapter
from requests.packages.urllib3.util.retry import Retry

def requests_retry_session(
    retries=3,
    backoff_factor=0.3,
    status_forcelist=(500, 502, 504, 503),
    session=None,
):
    session = session or requests.Session()
    retry = Retry(
        total=retries,
        read=retries,
        connect=retries,
        backoff_factor=backoff_factor,
        status_forcelist=status_forcelist,
    )
    adapter = HTTPAdapter(max_retries=retry)
    session.mount('http://', adapter)
    session.mount('https://', adapter)
    return session

fasta = open(ensembl_name + ".rRNAs.fasta","w")
server = "http://rest.ensembl.org"
reqs_per_sec=15
for i in dfToList:
	ext = "/sequence/id/" + str(i) + "?type=cdna"
	r = requests_retry_session().get(server+ext, headers={ "Content-Type" : "text/x-fasta"})
	if not r.ok:
		r.raise_for_status()
		sys.exit()
	fasta.write( r.text + '\n')
fasta.close()
import urllib 
urllib.request.urlretrieve(str("ftp://ftp.ensemblgenomes.org/pub/plants/release-51/species_EnsemblPlants.txt"), './ensembl_release.tsv')

### ENSEMBL relese 51 added new column
new_columns=["#name","species","division","taxonomy_id","assembly","assembly_accession","genebuild","variation", "microarray", "pan_compara","peptide_compara", "genome_alignments","other_alignments","core_db","species_id","new_unexpected_column"]
df_temp = pd.read_csv('./ensembl_release.tsv', sep='\t', index_col=False, names=new_columns)
df_temp = df_temp[df_temp['assembly'].str.contains(str(df.iloc[0]['display_name']).split("(")[1].split(")")[0])]
print(str(df_temp["species"].values[0]))
name_temp = str(df_temp["species"].values[0])

#Fasta
ftp = FTP("ftp.ensemblgenomes.org")
ftp.login()
filepath = "/pub/plants/release-51/fasta/" + name_temp + "/dna_index/"
ftp.cwd(filepath)
ftp.retrlines('LIST *dna.toplevel.fa.gz*')
target_dir='./'
filematch='*.dna.toplevel.fa.gz'
filematch_primary='*.dna.primary_assembly.fa.gz'

if not ftp.nlst(filematch_primary):
	for filename in ftp.nlst(filematch):
		target_file_name = os.path.join(target_dir,os.path.basename(filename))
		print(str(target_file_name))
		with open(ensembl_name + ".dna.toplevel.fa.gz",'wb') as fhandle:
			ftp.retrbinary('RETR %s' %filename, fhandle.write)

else:

	for filename in ftp.nlst(filematch_primary):
		target_file_name = os.path.join(target_dir,os.path.basename(filename))
		print(str(target_file_name))		
		with open(ensembl_name + ".dna.toplevel.fa.gz",'wb') as fhandle:
			ftp.retrbinary('RETR %s' %filename, fhandle.write)	

#GTF
ftp = FTP("ftp.ensemblgenomes.org")
ftp.login()
filepath = "/pub/plants/release-51/gtf/" + name_temp + "/"
ftp.cwd(filepath)
ftp.retrlines('LIST *.gtf.gz*')
target_dir='./'
filematch='*.gtf.gz'
for filename in ftp.nlst(filematch):
	target_file_name = os.path.join(target_dir,os.path.basename(filename))
	if "chr" not in str(target_file_name):
		if "abinitio" not in str(target_file_name):
			#print(str(target_file_name))
			with open(ensembl_name + ".genes.gtf.gz",'wb') as fhandle:
				ftp.retrbinary('RETR %s' %filename, fhandle.write)
#unzip everything

"""


wget ftp://ftp.ensemblgenomes.org/pub/plants/release-51/fasta/panicum_hallii/dna_index/Panicum_hallii.PhalliiHAL_v2.1.dna.toplevel.fa.gz
gunzip Panicum_hallii.PhalliiHAL_v2.1.dna.toplevel.fa.gz
wget ftp://ftp.ensemblgenomes.org/pub/plants/release-51/gtf/panicum_hallii/Panicum_hallii.PhalliiHAL_v2.1.51.gtf.gz
gunzip Panicum_hallii.PhalliiHAL_v2.1.51.gtf.gz


"""
