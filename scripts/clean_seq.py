
#!/usr/bin/env python

import sys
from Bio import SeqIO
from Bio.Seq import Seq

def remove_invalid_sequences(input_file, output_file):

    records = SeqIO.parse(input_file, "fasta")

    cleaned_records = []

    total = 0
    removed = 0
    removed_invalid = 0
    removed_single_base = 0

    valid_bases = set("ATGC")

    for record in records:

        total += 1

        sequence = str(record.seq).upper()

        if not set(sequence).issubset(valid_bases):
            removed += 1
            removed_invalid += 1
            continue

        if not sequence:
            removed += 1
            continue

        if len(set(sequence)) == 1:
            removed += 1
            removed_single_base += 1
            continue

        record.seq = Seq(sequence)
        cleaned_records.append(record)

    SeqIO.write(cleaned_records, output_file, "fasta")

if __name__ == "__main__":

    if len(sys.argv) != 3:
        print("Usage: python remove_invalid_sequences.py input.fasta output.fasta")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    remove_invalid_sequences(input_file, output_file)
