# Mushroom Vision

A full-stack project for uploading mushroom images and running ML predictions to classify edible vs. poisonous mushrooms.

## Branches

This repository contains two different implementations:

###  **pytorch** (Production Ready)
- **Status**: Working and fully functional
- **ML Framework**: PyTorch with custom CNN architecture
- **Features**: 
  - Drag-and-drop image upload with preview
  - Real-time upload progress tracking
  - Spring Boot REST API backend
  - React frontend with Tailwind CSS
  - Binary classification (edible/poisonous)
- **Documentation**: Complete setup and usage instructions
- **Recommended for**: Production use and development

###  **tensorflow** (Development)
- **Status**: In development
- **ML Framework**: TensorFlow
- **Features**: 
  - Basic implementation
  - Under active development
- **Documentation**: Limited
- **Recommended for**: Experimental use and TensorFlow-specific development

## Quick Start

To get started with the working PyTorch implementation:

```bash
# Clone the repository
git clone git@github.com:connorb0531/mushroom-vision.git
cd mushroom-vision

# Switch to the working branch
git checkout pytorch

# Follow the setup instructions in the pytorch branch README
```

## Project Overview

Mushroom Vision is a machine learning application that helps users identify whether mushrooms are edible or poisonous by analyzing uploaded images. The application uses deep learning models to provide real-time classification with confidence scores.

### Key Features
- **Image Upload**: Drag-and-drop interface for easy image submission
- **ML Classification**: Deep learning models for mushroom identification
- **Confidence Scores**: Detailed probability analysis for each prediction
- **Modern UI**: Responsive design with Tailwind CSS
- **Real-time**: Fast inference with progress tracking

### Tech Stack
- **Frontend**: React + Vite + Tailwind CSS
- **Backend**: Spring Boot (Java)
- **ML**: PyTorch (production) / TensorFlow (development)
- **Database**: PostgreSQL (optional)

## Contributing

Please refer to the specific branch documentation for contribution guidelines:
- For PyTorch implementation: See `pytorch` branch README
- For TensorFlow development: See `tensorflow` branch README

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.