import sys

# Convert GTF to 12-column BED format
def gtf_to_bed(gtf_file, output_prefix):
    with open(gtf_file, 'r') as f:
        lines = f.readlines()

    # Transcript biotypes
    protein_coding = []
    snoRNA = []
    miRNA = []
    noncoding = []
    noncoding_misc = []

    # Store transcripts and their exons
    transcript_exons = {}
    transcript_biotypes = {}

    for line in lines:
        if line.startswith('#'):
            continue
        fields = line.strip().split('\t')
        if len(fields) < 9:
            continue

        feature_type = fields[2]
        attributes = fields[8]

        # Extract transcript IDs and biotypes
        if 'transcript_biotype' in attributes:
            biotype = attributes.split('transcript_biotype "')[1].split('"')[0]
            transcript_id = attributes.split('transcript_id "')[1].split('"')[0]
            transcript_biotypes[transcript_id] = biotype

            # Get exons for each transcript
            if feature_type == "exon":
                if transcript_id not in transcript_exons:
                    transcript_exons[transcript_id] = []
                transcript_exons[transcript_id].append((int(fields[3]), int(fields[4]), fields[6])) 

    # Process the collected exons for BED12 entries
    for transcript_id, exons in transcript_exons.items():
        exons.sort()

        # Get blockStarts and blockSizes
        blockStarts = []
        blockSizes = []
        for exon in exons:
            blockStarts.append(str(exon[0] - exons[0][0]))  # offset from the first exon
            blockSizes.append(str(exon[1] - exon[0] + 1))  # size of the exon

        blockStarts_str = ','.join(blockStarts)
        blockSizes_str = ','.join(blockSizes)

        # Use the first exon to get the chromosome, strand, thickStart, and thickEnd
        chrom = exons[0][0] 
        strand = exons[0][2]
        thickStart = exons[0][0]
        thickEnd = exons[-1][1]
        
        # Transcript biotype for current transcript
        biotype = transcript_biotypes.get(transcript_id, 'unknown')

        # Get BED12 entry for this transcript
        bed_entry = (
            f"{chrom}\t{int(exons[0][0]) - 1}\t{fields[4]}\t{transcript_id}\t"
            "100\t"  # Score
            f"{strand}\t"  # Strand
            f"{thickStart}\t{thickEnd}\t"  # thickStart, thickEnd
            "0,0,0\t"  # itemRGB (default black)
            f"{len(exons)}\t"  # blockCount (number of exons)
            f"{blockSizes_str}\t"  # blockSizes (comma-separated sizes)
            f"{blockStarts_str}\n"  # blockStarts (comma-separated starts)
        )

        # Add entry to the list based on the biotype
        if biotype == 'protein_coding':
            protein_coding.append(bed_entry)
        elif biotype == 'snoRNA':
            snoRNA.append(bed_entry)
        elif biotype in ['miRNA', 'pre_miRNA']:
            miRNA.append(bed_entry)
        elif biotype == 'ncRNA':
            noncoding.append(bed_entry)
        elif biotype in ['lncRNA', 'lincRNA']:
            noncoding.append(bed_entry)
        else:
            noncoding_misc.append(bed_entry)

    # Write BED12 files
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

# Command-line arguments
if len(sys.argv) != 3:
    print("Usage: python ensembl_gtf2bed.py <ensembl_gtf> <output_prefix>")
    sys.exit(1)

# Sys args
gtf_file = sys.argv[1]
output_prefix = sys.argv[2]

# Run function
gtf_to_bed(gtf_file, output_prefix)
