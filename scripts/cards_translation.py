import csv
import json
import argparse


"""Script to convert cards data from CSV to JSON."""


resources_dict = {
    'f': "fungi",
    'p': "plant",
    'a': "animal",
    'i': "insect"
}

items_dict = {
    '-': "hidden",
    'x': "empty",
    'f': "fungi",
    'p': "plant",
    'a': "animal",
    'i': "insect",
    'q': "quill",
    'w': "inkwell",
    'm': "manuscript"
}

points_type_dict = {
    '3': "three",
    '5': "five",
    'q': "quill",
    'w': "inkwell",
    'm': "manuscript",
    'c': ""
}



def translate_resource_cards() -> None:
    with open("../legacy/cards_translation/csv/resource_cards.csv", 'r') as fin:
        reader = csv.reader(fin)
        next(reader)        # skipping first line

        cards = []

        with open("../src/main/resources/resourceCards.json", 'w') as fout:
            for row in reader:
                card = {
                    "id": int(row[0]),
                    "resourceType": resources_dict[row[1]],
                    "cornerItems": {
                        "topLeft": items_dict[row[2][0]],
                        "topRight": items_dict[row[2][1]],
                        "bottomRight": items_dict[row[2][2]],
                        "bottomLeft": items_dict[row[2][3]]
                    },
                    "pointsAmount": int(row[3])
                }

                cards.append(card)

            print(json.dumps(cards, indent=4), file=fout)

def translate_gold_cards() -> None:
    with open("../legacy/cards_translation/csv/gold_cards.csv", 'r') as fin:
        reader = csv.reader(fin)
        next(reader)        # skipping first line

        cards = []

        with open("../src/main/resources/goldCards.json", 'w') as fout:
            for row in reader:
                card = {
                    "id": int(row[0]),
                    "resourceType": resources_dict[row[1]],
                    "pointsType": points_type_dict[row[2]],
                    "resourcesNeeded": {
                        "plant": row[3].count('p'),
                        "animal": row[3].count('a'),
                        "fungi": row[3].count('f'),
                        "insect": row[3].count('i')
                    },
                    "cornerItems": {
                        "topLeft": items_dict[row[4][0]],
                        "topRight": items_dict[row[4][1]],
                        "bottomRight": items_dict[row[4][2]],
                        "bottomLeft": items_dict[row[4][3]]
                    }
                }

                cards.append(card)

            print(json.dumps(cards, indent=4), file=fout)

def translate_starter_cards() -> None:
    with open("../legacy/cards_translation/csv/starter_cards.csv", 'r') as fin:
        reader = csv.reader(fin)
        next(reader)        # skipping first line

        cards = []

        with open("../src/main/resources/starterCards.json", 'w') as fout:
            for row in reader:
                card = {
                    "id": int(row[0]),
                    "frontCornerItems": {
                        "topLeft": items_dict[row[1][0]],
                        "topRight": items_dict[row[1][1]],
                        "bottomRight": items_dict[row[1][2]],
                        "bottomLeft": items_dict[row[1][3]]
                    },
                    "frontCentralItems": {
                        "plant": row[2].count('p'),
                        "animal": row[2].count('a'),
                        "fungi": row[2].count('f'),
                        "insect": row[2].count('i')
                    },
                    "backCornerItems": {
                        "topLeft": items_dict[row[3][0]],
                        "topRight": items_dict[row[3][1]],
                        "bottomRight": items_dict[row[3][2]],
                        "bottomLeft": items_dict[row[3][3]]
                    }
                }

                cards.append(card)

            print(json.dumps(cards, indent=4), file=fout)
            


def parse_args():
    parser = argparse.ArgumentParser(description="Translates CSV to Java code.")
    parser.add_argument("-r","--resource", action="store_true", help="insert this option to translate resource cards")
    parser.add_argument("-g", "--gold", action="store_true", help="insert this option to translate gold cards")
    parser.add_argument("-s", "--starter", action="store_true", help="insert this option to translate starter cards")

    args = parser.parse_args()

    if not (args.resource or args.gold or args.starter):
        parser.print_help()
        exit(1)

    return args



def main():
    args = parse_args()

    dict1 = {
        "name": "gay",
        "surname": "supergay"
    }

    dict2 = {
        "name": "gay",
        "surname": "supergay"
    }

    if args.resource:
        translate_resource_cards()
        print("resource cards succesfully translated.")
    
    if args.gold:
        translate_gold_cards()
        print("gold cards succesfully translated.")

    if args.starter:
        translate_starter_cards()
        print("starter cards succesfully translated.")

main()