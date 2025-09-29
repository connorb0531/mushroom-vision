import React, { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import './ImageUploader.css';

const ImageUploader = ({ onImageSelect, selectedImage, isLoading }) => {
  const onDrop = useCallback((acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      const file = acceptedFiles[0];
      const reader = new FileReader();
      
      reader.onload = () => {
        onImageSelect(reader.result, file.name);
      };
      
      reader.readAsDataURL(file);
    }
  }, [onImageSelect]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'image/*': ['.jpeg', '.jpg', '.png']
    },
    multiple: false,
    disabled: isLoading
  });

  return (
    <div className="image-uploader">
      <div
        {...getRootProps()}
        className={`dropzone ${isDragActive ? 'active' : ''} ${isLoading ? 'loading' : ''}`}
      >
        <input {...getInputProps()} />
        {selectedImage ? (
          <div className="image-preview">
            <img src={selectedImage} alt="Selected mushroom" />
            <div className="image-overlay">
              <p>Click or drag to change image</p>
            </div>
          </div>
        ) : (
          <div className="dropzone-content">
            <div className="upload-icon">🍄</div>
            <p className="upload-text">
              {isDragActive
                ? 'Drop the mushroom image here...'
                : 'Drag & drop a mushroom image here, or click to select'}
            </p>
            <p className="upload-subtext">
              Supports JPG, PNG, JPEG files
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ImageUploader;
