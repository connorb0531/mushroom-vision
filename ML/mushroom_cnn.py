import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, Dataset
from torchvision import transforms, models
from PIL import Image
import os
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report
import matplotlib.pyplot as plt
from dotenv import load_dotenv
from tqdm import tqdm
import time

# Load environment variables
load_dotenv()

class MushroomDataset(Dataset):
    def __init__(self, image_paths, labels, transform=None):
        self.image_paths = image_paths
        self.labels = labels
        self.transform = transform
    
    def __len__(self):
        return len(self.image_paths)
    
    def __getitem__(self, idx):
        image_path = self.image_paths[idx]
        image = Image.open(image_path).convert('RGB')
        label = self.labels[idx]
        
        if self.transform:
            image = self.transform(image)
        
        return image, label

class MushroomResNet(nn.Module):
    def __init__(self, num_classes=2, pretrained=True):
        super(MushroomResNet, self).__init__()
        
        # Load pretrained ResNet18
        self.resnet = models.resnet18(pretrained=pretrained)
        
        # Freeze early layers
        for param in self.resnet.parameters():
            param.requires_grad = False
        
        # Unfreeze last layers
        for param in self.resnet.layer4.parameters():
            param.requires_grad = True
        for param in self.resnet.avgpool.parameters():
            param.requires_grad = True
        
        # Replace final layer
        num_features = self.resnet.fc.in_features
        self.resnet.fc = nn.Sequential(
            nn.Dropout(0.5),
            nn.Linear(num_features, 512),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(512, 128),
            nn.ReLU(),
            nn.Dropout(0.2),
            nn.Linear(128, num_classes)
        )
        
    def forward(self, x):
        return self.resnet(x)

def load_data(data_dir):
    """Load mushroom images and create labels"""
    image_paths = []
    labels = []
    
    # Define class mapping
    class_mapping = {
        'edible mushroom sporocarp': 0,
        'edible sporocarp': 0,
        'poisonous mushroom sporocarp': 1,
        'poisonous sporocarp': 1
    }
    
    for class_name in os.listdir(data_dir):
        class_path = os.path.join(data_dir, class_name)
        if os.path.isdir(class_path):
            label = class_mapping.get(class_name, -1)
            if label != -1:
                for image_file in os.listdir(class_path):
                    if image_file.lower().endswith(('.jpg', '.jpeg', '.png')):
                        image_paths.append(os.path.join(class_path, image_file))
                        labels.append(label)
    
    return image_paths, labels

def train_model(model, train_loader, val_loader, num_epochs=10, learning_rate=0.001):
    """Train the ResNet model with transfer learning"""
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    model.to(device)
    
    criterion = nn.CrossEntropyLoss()
    # Use lower learning rate
    optimizer = optim.Adam(model.parameters(), lr=learning_rate, weight_decay=1e-4)
    scheduler = optim.lr_scheduler.StepLR(optimizer, step_size=5, gamma=0.5)
    
    train_losses = []
    val_accuracies = []
    
    print(f"\nStarting training on {device}")
    print(f"Training samples: {len(train_loader.dataset)}")
    print(f"Validation samples: {len(val_loader.dataset)}")
    print(f"Epochs: {num_epochs}")
    print(f"Learning rate: {learning_rate}")
    print("-" * 60)
    
    for epoch in range(num_epochs):
        start_time = time.time()
        
        # Training phase
        model.train()
        running_loss = 0.0
        train_correct = 0
        train_total = 0
        
        train_pbar = tqdm(train_loader, desc=f'Epoch {epoch+1}/{num_epochs} [Training]', 
                         leave=False, ncols=100)
        
        for batch_idx, (images, labels) in enumerate(train_pbar):
            images, labels = images.to(device), labels.to(device)
            
            optimizer.zero_grad()
            outputs = model(images)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()
            
            running_loss += loss.item()
            
            _, predicted = torch.max(outputs.data, 1)
            train_total += labels.size(0)
            train_correct += (predicted == labels).sum().item()
            current_loss = running_loss / (batch_idx + 1)
            current_acc = 100 * train_correct / train_total
            train_pbar.set_postfix({
                'Loss': f'{current_loss:.4f}',
                'Acc': f'{current_acc:.1f}%'
            })
        
        # Validation phase
        model.eval()
        val_correct = 0
        val_total = 0
        val_loss = 0.0
        
        with torch.no_grad():
            val_pbar = tqdm(val_loader, desc=f'Epoch {epoch+1}/{num_epochs} [Validation]', 
                           leave=False, ncols=100)
            
            for images, labels in val_pbar:
                images, labels = images.to(device), labels.to(device)
                outputs = model(images)
                loss = criterion(outputs, labels)
                val_loss += loss.item()
                
                _, predicted = torch.max(outputs.data, 1)
                val_total += labels.size(0)
                val_correct += (predicted == labels).sum().item()
                
                current_val_acc = 100 * val_correct / val_total
                val_pbar.set_postfix({'Acc': f'{current_val_acc:.1f}%'})
        
        # Calculate metrics
        val_accuracy = 100 * val_correct / val_total
        avg_train_loss = running_loss / len(train_loader)
        avg_val_loss = val_loss / len(val_loader)
        train_accuracy = 100 * train_correct / train_total
        
        train_losses.append(avg_train_loss)
        val_accuracies.append(val_accuracy)
        
        epoch_time = time.time() - start_time
        print(f"\nEpoch {epoch+1}/{num_epochs} Summary:")
        print(f"   Time: {epoch_time:.1f}s")
        print(f"   Train Loss: {avg_train_loss:.4f} | Train Acc: {train_accuracy:.2f}%")
        print(f"   Val Loss: {avg_val_loss:.4f} | Val Acc: {val_accuracy:.2f}%")
        print(f"   Learning Rate: {optimizer.param_groups[0]['lr']:.6f}")
        
        scheduler.step()
        
        if val_accuracy > 95:
            print(f"\nEarly stopping! Validation accuracy reached {val_accuracy:.2f}%")
            break
    
    print(f"\nTraining completed!")
    print(f"Final validation accuracy: {val_accuracies[-1]:.2f}%")
    
    return train_losses, val_accuracies

def evaluate_model(model, test_loader):
    """Evaluate the model on test data"""
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    model.eval()
    
    all_predictions = []
    all_labels = []
    
    with torch.no_grad():
        for images, labels in test_loader:
            images, labels = images.to(device), labels.to(device)
            outputs = model(images)
            _, predicted = torch.max(outputs.data, 1)
            
            all_predictions.extend(predicted.cpu().numpy())
            all_labels.extend(labels.cpu().numpy())
    
    accuracy = accuracy_score(all_labels, all_predictions)
    print(f'Test Accuracy: {accuracy:.4f}')
    print('\nClassification Report:')
    print(classification_report(all_labels, all_predictions, 
                              target_names=['Edible', 'Poisonous']))
    
    return accuracy

def test_prediction(model_path='mushroom_cnn_model.pth'):
    """Test the trained model with a simple prediction"""
    print("\nTesting model prediction...")
    
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    model = MushroomResNet(num_classes=2, pretrained=False)
    
    if os.path.exists(model_path):
        model.load_state_dict(torch.load(model_path, map_location=device))
        model.to(device)
        model.eval()
        
        dummy_image = torch.randn(1, 3, 224, 224).to(device)
        
        with torch.no_grad():
            outputs = model(dummy_image)
            probabilities = torch.softmax(outputs, dim=1)
            edible_prob = probabilities[0][0].item()
            poisonous_prob = probabilities[0][1].item()
            
            prediction = "edible" if edible_prob > poisonous_prob else "poisonous"
            confidence = max(edible_prob, poisonous_prob)
            
            print(f"Model loaded successfully!")
            print(f"Test prediction: {prediction}")
            print(f"Confidence: {confidence:.2%}")
            print(f"Edible probability: {edible_prob:.2%}")
            print(f"Poisonous probability: {poisonous_prob:.2%}")
    else:
        print(f"Model file {model_path} not found!")

def main():
    torch.manual_seed(42)
    np.random.seed(42)
    data_dir = os.getenv('DATA_DIR', 'data')
    image_size = int(os.getenv('IMAGE_SIZE', '256'))
    train_epochs = int(os.getenv('TRAIN_EPOCHS', '20'))
    batch_size = int(os.getenv('TRAIN_BATCH_SIZE', '32'))
    learning_rate = float(os.getenv('TRAIN_LEARNING_RATE', '0.001'))
    
    image_paths, labels = load_data(data_dir)
    
    print(f'Total images loaded: {len(image_paths)}')
    print(f'Edible mushrooms: {labels.count(0)}')
    print(f'Poisonous mushrooms: {labels.count(1)}')
    
    transform_train = transforms.Compose([
        transforms.Resize((image_size, image_size)),
        transforms.RandomHorizontalFlip(p=0.5),
        transforms.RandomRotation(10),
        transforms.ColorJitter(brightness=0.2, contrast=0.2, saturation=0.2, hue=0.1),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    ])
    
    transform_test = transforms.Compose([
        transforms.Resize((image_size, image_size)),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    ])
    
    train_paths, temp_paths, train_labels, temp_labels = train_test_split(
        image_paths, labels, test_size=0.3, random_state=42, stratify=labels
    )
    val_paths, test_paths, val_labels, test_labels = train_test_split(
        temp_paths, temp_labels, test_size=0.5, random_state=42, stratify=temp_labels
    )
    
    train_dataset = MushroomDataset(train_paths, train_labels, transform_train)
    val_dataset = MushroomDataset(val_paths, val_labels, transform_test)
    test_dataset = MushroomDataset(test_paths, test_labels, transform_test)
    train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True)
    val_loader = DataLoader(val_dataset, batch_size=batch_size, shuffle=False)
    test_loader = DataLoader(test_dataset, batch_size=batch_size, shuffle=False)
    
    model = MushroomResNet(num_classes=2, pretrained=True)
    print(f'Model created with {sum(p.numel() for p in model.parameters())} parameters')
    print(f'Trainable parameters: {sum(p.numel() for p in model.parameters() if p.requires_grad)}')
    
    print('Starting training...')
    train_losses, val_accuracies = train_model(model, train_loader, val_loader, train_epochs, learning_rate)
    
    print('\nEvaluating on test set...')
    test_accuracy = evaluate_model(model, test_loader)
    
    torch.save(model.state_dict(), 'mushroom_cnn_model.pth')
    print('Model saved as mushroom_cnn_model.pth')
    
    test_prediction('mushroom_cnn_model.pth')
    plt.figure(figsize=(12, 4))
    
    plt.subplot(1, 2, 1)
    plt.plot(train_losses)
    plt.title('Training Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    
    plt.subplot(1, 2, 2)
    plt.plot(val_accuracies)
    plt.title('Validation Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy (%)')
    
    plt.tight_layout()
    plt.savefig('training_history.png')
    print('Training history plot saved as training_history.png')
    plt.show()

if __name__ == '__main__':
    main()
