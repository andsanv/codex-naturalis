import csv
import argparse



resources_dict = {
    'f': "Resources.FUNGI",
    'p': "Resources.PLANT",
    'a': "Resources.ANIMAL",
    'i': "Resources.INSECT"
}

items_dict = {
    '-': "Optional.empty()",
    'x': "Optional.of(CardItems.EMPTY)",
    'f': "Optional.of(CardItems.FUNGI)",
    'p': "Optional.of(CardItems.PLANT)",
    'a': "Optional.of(CardItems.ANIMAL)",
    'i': "Optional.of(CardItems.INSECT)",
    'q': "Optional.of(CardItems.QUILL)",
    'w': "Optional.of(CardItems.INKWELL)",
    'm': "Optional.of(CardItems.MANUSCRIPT)"
}

point_dict = {
    '0': "false",
    '1': "true"
}

points_type_dict = {
    '3': "GoldCardPoints.THREE",
    '5': "GoldCardPoints.FIVE",
    'q': "GoldCardPoints.ONE_PER_QUILL",
    'w': "GoldCardPoints.ONE_PER_INKWELL",
    'm': "GoldCardPoints.ONE_PER_MANUSCRIPT",
    'c': "GoldCardPoints.TWO_PER_COVERED_CORNER"
}



def translate_resource_cards() -> None:
    with open("../csv/resource_cards.csv", 'r') as fin:
        reader = csv.reader(fin)
        next(reader)        # skipping first line

        with open("../txt/resource_cards.txt", 'w') as fout:
            for row in reader:
                corner_items = ""
                for index, item in enumerate(row[2]):
                    corner_items += items_dict[item]
                    corner_items += ", " if index != len(row[2]) - 1 else ""

                string = "CARD" + row[0] + "(" + resources_dict[row[1]] + ", Arrays.asList(" + corner_items + "), " + point_dict[row[3]] + "),"
                print(string , file=fout)

def translate_gold_cards() -> None:
    with open("../csv/gold_cards.csv", 'r') as fin:
        reader = csv.reader(fin)
        next(reader)        # skipping first line

        with open("../txt/gold_cards.txt", 'w') as fout:
            for row in reader:
                # resources counts
                resources_counts = ""
                for resource, count in [[resources_dict[char], row[3].count(char)] for char in "pafi"]:
                    resources_counts += "\tput(" + resource + ", " + str(count) + ");\n"

                # items at corners
                corner_items = ""
                for index, item in enumerate(row[4]):
                    corner_items += items_dict[item]
                    corner_items += ", " if index != len(row[4]) - 1 else ""

                string = "CARD" + row[0] + "(" + resources_dict[row[1]] + ", " + points_type_dict[row[2]] + ", " + "new HashMap<Resources, Integer>() {{\n" + resources_counts + "}}, Arrays.asList(" + corner_items + ")),"
                print(string , file=fout, end="\n\n")
            


def parse_args():
    parser = argparse.ArgumentParser(description="Translates CSV to Java code.")
    parser.add_argument("-r","--resource", action="store_true", help="add this option to translate resource cards")
    parser.add_argument("-g", "--gold", action="store_true", help="add this option to translate gold cards")

    args = parser.parse_args()

    if not (args.resource or args.gold):
        parser.print_help()
        exit(1)

    return args



def main():
    args = parse_args()

    if args.resource:
        translate_resource_cards()
        print("resource cards succesfully translated.")
    
    if args.gold:
        translate_gold_cards()
        print("gold cards succesfully translated.")

main()