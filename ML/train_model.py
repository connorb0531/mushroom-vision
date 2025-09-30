#!/usr/bin/env python3
"""
Training script for Mushroom Vision CNN model
Run this script to train the model on the mushroom dataset
"""

import os
import sys
from mushroom_cnn import main

if __name__ == '__main__':
    print("Mushroom Vision CNN Training")
    print("=" * 40)
    
    # Check if data directory exists
    if not os.path.exists('data'):
        print("Error: Data directory not found!")
        print("Please ensure the 'data' directory contains mushroom images.")
        sys.exit(1)
    
    # Check if data subdirectories exist
    expected_dirs = [
        'edible mushroom sporocarp',
        'edible sporocarp', 
        'poisonous mushroom sporocarp',
        'poisonous sporocarp'
    ]
    
    missing_dirs = []
    for dir_name in expected_dirs:
        if not os.path.exists(os.path.join('data', dir_name)):
            missing_dirs.append(dir_name)
    
    if missing_dirs:
        print("Warning: Missing data directories:")
        for dir_name in missing_dirs:
            print(f"  - {dir_name}")
        print("Training will continue with available data.")
    
    print("\nStarting training process...")
    print("This may take several minutes depending on your hardware.")
    print("-" * 40)
    
    try:
        main()
        print("\nTraining completed successfully!")
        print("Model saved as 'mushroom_cnn_model.pth'")
        print("You can now run the Flask API with: python app.py")
    except Exception as e:
        print(f"\nTraining failed with error: {str(e)}")
        sys.exit(1)
