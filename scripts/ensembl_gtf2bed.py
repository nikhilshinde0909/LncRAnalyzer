import sys

# Function to convert GTF to 12-column BED format
def gtf_to_bed(gtf_file, output_prefix):
    with open(gtf_file, 'r') as f:
        lines = f.readlines()
    
    # Different transcript biotypes
    protein_coding = []
    snoRNA = []
    miRNA = []
    noncoding = []
    noncoding_misc = []

    for line in lines:
        if line.startswith('#'):
            continue 
        fields = line.strip().split('\t')
        if len(fields) < 9:
            continue

        feature_type = fields[2]
        attributes = fields[8]
        
        # Extract transcript biotype and ID
        if 'transcript_biotype' in attributes:
            biotype = attributes.split('transcript_biotype "')[1].split('"')[0]
            transcript_id = attributes.split('transcript_id "')[1].split('"')[0]

            # Create BED12 entries
            bed_entry = (
                f"{fields[0]}\t{int(fields[3]) - 1}\t{fields[4]}\t{transcript_id}\t"
                "0\t"  # Score 
                f"{fields[6]}\t"  # Strand
                f"{fields[3]}\t{fields[4]}\t"  # thickStart, thickEnd
                "0,0,0\t"  # itemRGB (default black)
                "1\t"  # blockCount
                f"{int(fields[4]) - int(fields[3])}\t"  # blockSizes 
                "0\n"  # blockStarts
            )

            # Entries by biotype
            if biotype == 'protein_coding':
                protein_coding.append(bed_entry)
            elif biotype == 'snoRNA':
                snoRNA.append(bed_entry)
            elif biotype in ['miRNA', 'pre_miRNA']:
                miRNA.append(bed_entry)
            elif biotype =='ncRNA':
                noncoding.append(bed_entry)
            else:
                noncoding_misc.append(bed_entry)

    # Write to BED12 files
    with open(f"{output_prefix}.protein_coding.bed", 'w') as f:
        f.writelines(protein_coding)
    
    with open(f"{output_prefix}.snoRNA.bed", 'w') as f:
        f.writelines(snoRNA)

    with open(f"{output_prefix}.miRNA.bed", 'w') as f:
        f.writelines(miRNA)

    with open(f"{output_prefix}.noncoding.bed", 'w') as f:
        f.writelines(noncoding)
    with open(f"{output_prefix}.nc_misc.bed", 'w') as f:
        f.writelines(noncoding_misc)

# Define command-line arguments
if len(sys.argv) != 3:
    print("Usage: python ensembl_gtf2bed.py <ensembl_gtf> <output_prefix>")
    sys.exit(1)

# Sys args
gtf_file = sys.argv[1]
output_prefix = sys.argv[2]

# Run function
gtf_to_bed(gtf_file, output_prefix)
