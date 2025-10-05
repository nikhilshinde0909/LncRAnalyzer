#!/usr/bin/env python
import sys
import os
import requests
from bs4 import BeautifulSoup

if len(sys.argv) < 4:
    print("Usage: python download_TE.py <species_name> <element type LTR/SINE/LINE/TIR/MITE/Helitron> <output_file>")
    sys.exit(1)

url = "http://apte.cp.utfpr.edu.br/download"

species_name = sys.argv[1]
element_type = sys.argv[2]  
output_file = sys.argv[3]

response = requests.get(url)
if response.status_code == 200:
    soup = BeautifulSoup(response.text, 'html.parser')
    links = soup.find_all('a', href=True)
    speciesmatch = str(species_name).lower()
    
    filematch = f"{element_type}.gff3"  
    file_found = False  

    for link in links:
        matched_files = link['href']
        if speciesmatch in matched_files and filematch in matched_files:
            target_file_url = matched_files
            target_file = target_file_url.split('/')[-1]
            print(f"Found target file {target_file} for species {species_name}")           
            file_response = requests.get(target_file_url)
            if file_response.status_code == 200:
                with open(output_file, 'wb') as file:
                    file.write(file_response.content)
                print(f"Saving {target_file} to {output_file}")
            else:
                print(f"Failed to download {target_file} for species '{species_name}'. Status code: {file_response.status_code}")
            file_found = True  
            break  

    if not file_found:
        print(f"No matching files found for species '{species_name}'.")

else:
    print(f"Failed to retrieve data from {url}. Status code: {response.status_code}")

