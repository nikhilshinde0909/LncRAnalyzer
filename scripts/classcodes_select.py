#!/usr/bin/env python

import sys

def extract_transcript_ids_from_gtf(file_path):
    results = []
    defined_class_codes = {"class_code \"i\"", "class_code \"o\"", "class_code \"u\"", "class_code \"x\"", "class_code \"j\""}

    with open(file_path, 'r') as file:
        for line in file:
            if line.startswith("#"):
                continue
            
            attributes = line.split('\t')[8]
            if any(code in attributes for code in defined_class_codes):
                transcript_id = extract_attribute(attributes, "transcript_id")
                if transcript_id:
                    results.append(transcript_id)
    
    return results

# Extract attributes
def extract_attribute(attribute_string, attribute_name):
    for attribute in attribute_string.split(';'):
        if attribute_name in attribute:
            return attribute.split('"')[1].strip()
    return None

if len(sys.argv) != 3:
    print("Usage: python select_classcodes.py <gffcompare_gtf_file> <out_classcodes_selected_transcripts>")
    sys.exit(1)

gtf_file_path = sys.argv[1]
out_classcodes_selected_transcripts = sys.argv[2]

# Get results
results = extract_transcript_ids_from_gtf(gtf_file_path)

# Write output file
with open(out_classcodes_selected_transcripts, 'w') as output_file:
    for transcript_id in results:
        output_file.write(f"{transcript_id}\n") 
