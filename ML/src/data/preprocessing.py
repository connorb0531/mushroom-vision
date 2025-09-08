import os
import cv2

# Paths
RAW_DIR = "/Users/connorbuckley/Desktop/mushroom-vision/ML/data/raw"
PROCESSED_DIR = "/Users/connorbuckley/Desktop/mushroom-vision/ML/data/processed"

TARGET_SIZE = (224, 224)

# Iterate through label subfolders
for label in os.listdir(RAW_DIR):
    label_path = os.path.join(RAW_DIR, label)
    if not os.path.isdir(label_path):
        continue

    # Create matching subfolder
    save_label_path = os.path.join(PROCESSED_DIR, label)
    os.makedirs(save_label_path, exist_ok=True)

    # Loop through images
    for file_name in os.listdir(label_path):
        img_path = os.path.join(label_path, file_name)

        # Read image
        img = cv2.imread(img_path)
        if img is None:
            continue

        # Resize and normalize
        resized = cv2.resize(img, TARGET_SIZE, interpolation=cv2.INTER_LINEAR)
        normalized = cv2.normalize(resized, None, alpha=0, beta=1, norm_type=cv2.NORM_MINMAX, dtype=cv2.CV_32F)

        # Save to processed folder
        save_path = os.path.join(save_label_path, file_name)
        cv2.imwrite(save_path, resized)




    



