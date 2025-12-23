#!/usr/bin/env python

import os
import sys
import shutil

def run_command(command, output_file=None):
    """Executes a shell command and checks for errors, with optional output redirection."""
    print(f"Running command: {' '.join(command)}")
    if output_file:
        with open(output_file, 'w') as outfile:
            result = os.system(' '.join(command) + f' > {output_file}')
            if result != 0:
                print(f"Error occurred while running command: {' '.join(command)}")
                sys.exit(1)
    else:
        result = os.system(' '.join(command))
        if result != 0:
            print(f"Error occurred while running command: {' '.join(command)}")
            sys.exit(1)

def main():
    print("Creating liftover files from genome alignments")

    # Usage 
    if len(sys.argv) < 3:
        print("Usage: python Liftover.py <threads> <genome> <org_name> <genome_related_species> <rel_sp_name> <params_distance>")
        sys.exit(1)

    threads = sys.argv[1]
    genome = sys.argv[2]
    org_name = sys.argv[3]
    genome_related_species = sys.argv[4]
    rel_sp_name = sys.argv[5]
    params_distance = sys.argv[6] if len(sys.argv) > 6 else "near"

    # Liftover parameters
    par1 = {
        "near": "NEAR",
        "medium": "MAM4",
        "far": "MAM4",
        "medium_fast": "MAM4",
        "far_fast": "MAM4"
    }

    par2 = {
        "near": "-minScore=5000 -linearGap=medium",
        "medium": "-minScore=3000 -linearGap=medium",
        "far": "-minScore=5000 -linearGap=loose",
        "medium_fast": "-minScore=3000 -linearGap=medium",
        "far_fast": "-minScore=5000 -linearGap=loose"
    }

    par3 = {
        "near": "-m10",
        "medium": "-m100",
        "far": "-m100",
        "medium_fast": "-m10",
        "far_fast": "-m10"
    }

    par4 = {
        "near": "-W99",
        "medium": "",
        "far": "",
        "medium_fast": "-W99",
        "far_fast": "-W99"
    }

    # Retrieve par values based on distance
    distance_seed = par1[params_distance]
    distance_axt = par2[params_distance]
    distance_sense = par3[params_distance]
    distance_fast = par4[params_distance]

    # Process target fasta files
    run_command(["faToTwoBit", genome, f"{org_name}.2bit"])
    run_command(["twoBitInfo", f"{org_name}.2bit", f"{org_name}.chromInfo"])
    run_command(["faidx -x",genome])

    # LastDB
    run_command(["lastdb", "-P" + threads, distance_fast, "-u" + distance_seed, "-R01", f"{org_name}-{distance_seed}", f"*.fa"])

    # Process query fasta files
    shutil.copy(genome_related_species, f"{rel_sp_name}.fa")
    run_command(["faToTwoBit", f"{rel_sp_name}.fa", f"{rel_sp_name}.2bit"])
    run_command(["twoBitInfo", f"{rel_sp_name}.2bit", f"{rel_sp_name}.chromInfo"])

    # Last train with output redirection
    run_command(
        ["last-train", "-P" + threads, "--revsym", "--matsym", "--gapsym", "-E0.05", "-C2", f"{org_name}-{distance_seed}", f"{rel_sp_name}.fa"],
        output_file=f"{org_name}-{rel_sp_name}.mat"
    )

    # Alignment with output redirection
    run_command(
        ["lastal", "-P" + threads, "-i3G", distance_sense, "-E0.05", "-C2", "-p", f"{org_name}-{rel_sp_name}.mat", f"{org_name}-{distance_seed}", f"{rel_sp_name}.fa",f"|","last-split -m1"],
        output_file=f"{org_name}-{rel_sp_name}.maf"
    )

    # MAF to PSL with output redirection
    run_command(
        ["maf-convert", "psl", f"{org_name}-{rel_sp_name}.maf"],
        output_file=f"{org_name}-{rel_sp_name}.psl"
    )

    # Raw chain
    run_command(["axtChain", "-psl", distance_axt, "-scoreScheme=" + f"{org_name}-{rel_sp_name}.mat", f"{org_name}-{rel_sp_name}.psl", f"{org_name}.2bit", f"{rel_sp_name}.2bit", f"{org_name}.{rel_sp_name}.all.chain"])

    # Net
    run_command(["chainNet", f"{org_name}.{rel_sp_name}.all.chain", f"{org_name}.chromInfo", f"{rel_sp_name}.chromInfo", "all.net", "/dev/null"])

    # Over chain
    run_command(["netChainSubset", "all.net", f"{org_name}.{rel_sp_name}.all.chain", f"{org_name}.{rel_sp_name}.over.chain"])
    run_command(["head", f"{org_name}.{rel_sp_name}.over.chain"])

    # Gzip
    run_command(["gzip", f"{org_name}.{rel_sp_name}.over.chain"])

    # Remove unnecessary files
    for file in os.listdir('.'):
       if (f"-{distance_seed}" in file or 
          file.startswith(f"{org_name}-{rel_sp_name}") or
          file.endswith(".all.chain") or 
          file.endswith(".fa") or
          file.endswith(".2bit") or
          file.endswith(".chromInfo") or
          file.endswith(".net") or
          file.endswith(".maf")):
          os.remove(file)

if __name__ == "__main__":
    main()
