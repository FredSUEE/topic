import argparse
import csv
import json

def convert(input, output):
    with open(input) as inputfile:
        csvreader = csv.DictReader(inputfile, delimiter=',')
        with open(output, 'w') as outfile:
            for row in csvreader:
                data = {
                    'id': row['id'],
                    'title': row['title'],
                    'desc': row['desc'],
                    'topic_image': row['topic_image'],
                }
                json.dump(data, outfile)
                outfile.write('\n')


if __name__ == "__main__":
    import sys

    parser = argparse.ArgumentParser(
        description="CSV Convert Script")
    parser.add_argument("--input", help="Input CSV file")
    parser.add_argument("--output", help="Output JSON file")
    args = parser.parse_args(sys.argv[1:])

    convert(args.input, args.output)