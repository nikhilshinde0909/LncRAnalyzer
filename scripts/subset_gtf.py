#!/usr/bin/env python

import sys

def main(gtf_file, transcript_ids_file, output_file):
    # Read transcript IDs into a set
    with open(transcript_ids_file) as f:
        transcript_ids = set(line.strip() for line in f)

    # Open the GTF file and output file
    with open(gtf_file) as gtf, open(output_file, 'w') as subset:
        for line in gtf:
            if 'transcript_id' in line:
                for transcript_id in transcript_ids:
                    if f'transcript_id "{transcript_id}"' in line:
                        subset.write(line)
                        break

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: python subset_gtf.py <gtf_file> <transcript_ids_file> <output_file>")
        sys.exit(1)

    gtf_file = sys.argv[1]
    transcript_ids_file = sys.argv[2]
    output_file = sys.argv[3]

    main(gtf_file, transcript_ids_file, output_file)
