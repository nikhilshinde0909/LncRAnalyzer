#!/usr/bin/env python

import sys

if len(sys.argv) < 3:
    print("Usage: python format_TE_gff3tobed.py <input_gff3> <output_bed>")
    sys.exit(1)

input_gff3 = sys.argv[1]  
output_bed = sys.argv[2]  

# Function to parse a GFF3 line and convert to BED format
def gff3_to_bed(gff3_lines):
    bed_lines = []
    name_count = {} 
    
    for line in gff3_lines:
        if line.startswith("#"):
            continue
        
        # Split by tab character
        fields = line.strip().split("\t")
        
        # Ensure the GFF3 line has at least 9 columns
        if len(fields) < 9:
            continue

        # Extract relevant information
        chrom = fields[0]
        start = int(fields[3]) - 1  
        end = int(fields[4])
        
        # Formatting name_raw based on specific patterns
        name_raw = (fields[2].replace('/LTR/', ':LTR-').replace('/SINE/', ':SINE-').replace('/LINE/', ':LINE-').replace('/TIR/', ':TIR-').replace('/MITE/', ':MITE-').replace('/Helitron/', ':Helitron-').replace(' ', '-'))
        
        if name_raw == fields[2]:
            name_raw = 'novel-TE'
     
        if name_raw not in name_count:
            name_count[name_raw] = 1
        else:
            name_count[name_raw] += 1
            
        name = f"{name_raw}-{name_count[name_raw]}"
        score = fields[5] if fields[5] != "." else "100"
        strand = fields[6] if fields[6] in ['+', '-', '.'] else '.'
        bed_line = f"{chrom}\t{start}\t{end}\t{name}\t{score}\t{strand}"
        bed_lines.append(bed_line)
    
    bed_lines.sort(key=lambda x: (x.split("\t")[0], int(x.split("\t")[1]), int(x.split("\t")[2])))
    return bed_lines


# Function to read GFF3 file and convert to BED format
def convert_gff3_to_bed(input_gff3_file, output_bed_file):
    with open(input_gff3_file, 'r') as infile:
        gff3_lines = infile.readlines()

    # Convert GFF3 lines to BED format
    bed_data = gff3_to_bed(gff3_lines)

    # Write BED data to the output file
    with open(output_bed_file, 'w') as outfile:
        for line in bed_data:
            outfile.write(line + "\n")

convert_gff3_to_bed(input_gff3, output_bed)
