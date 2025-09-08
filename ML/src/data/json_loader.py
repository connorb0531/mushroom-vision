import os
import json

DATA_DIR = "/Users/connorbuckley/Desktop/mushroom-vision/ML/data"
LABEL_DIR = os.path.join(DATA_DIR, "processed")

# Mushroom labels
labels = os.listdir(LABEL_DIR)

data = []

# Creating mushroom data
for label in labels:
    mushroom = { "commonName": label }
    data.append(mushroom)

# Write data to json
full_path_output = os.path.join(DATA_DIR, "mushrooms.json")
with open(full_path_output, "w") as json_file:
    json.dump(data, json_file, indent=3)







