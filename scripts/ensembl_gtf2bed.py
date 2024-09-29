import sys

# Convert GTF to BED format
def gtf_to_bed(gtf_file, output_prefix):
    with open(gtf_file, 'r') as f:
        lines = f.readlines()
    
    # Lists for different biotypes
    protein_coding = []
    snoRNA = []
    miRNA = []
    noncoding = []

    for line in lines:
        if line.startswith('#'):
            continue 
        fields = line.strip().split('\t')
        if len(fields) < 9:
            continue

        feature_type = fields[2]
        attributes = fields[8]
        
        # Transcript biotype
        if 'transcript_biotype' in attributes:
            biotype = attributes.split('transcript_biotype "')[1].split('"')[0]

            # Create BED entry
            bed_entry = f"{fields[0]}\t{int(fields[3]) - 1}\t{fields[4]}\t{biotype}\n"

            # Entries by biotype
            if biotype == 'protein_coding':
                protein_coding.append(bed_entry)
            elif biotype == 'snoRNA':
                snoRNA.append(bed_entry)
            elif biotype in ['miRNA', 'pre_miRNA']:
                miRNA.append(bed_entry)
            else:
                noncoding.append(bed_entry)

    # Write to BED files
    with open(f"{output_prefix}_protein_coding.bed", 'w') as f:
        f.writelines(protein_coding)
    
    with open(f"{output_prefix}_snoRNA.bed", 'w') as f:
        f.writelines(snoRNA)

    with open(f"{output_prefix}_miRNA.bed", 'w') as f:
        f.writelines(miRNA)

    with open(f"{output_prefix}_noncoding.bed", 'w') as f:
        f.writelines(noncoding)
        
# Define command-line arguments
if len(sys.argv) != 3:
    print("Usage: python ensembl_gtf2bed.py <ensembl_gtf> <output_prefix>")
    sys.exit(1)
    
# Sys args
gtf_file = sys.argv[1]
output_prefix = sys.argv[2]

# Run function
gtf_to_bed(gtf_file, output_prefix)
