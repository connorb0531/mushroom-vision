import React, { useCallback, useState, useRef } from 'react';
import { useDropzone } from 'react-dropzone';

const ImageUploader = ({ onImageSelect, selectedImage, onClassificationResult }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const fileInputRef = useRef(null);

  const uploadUrl = process.env.REACT_APP_API_URL 
    ? `${process.env.REACT_APP_API_URL}/classifications/classify-file`
    : 'http://localhost:8080/api/classifications/classify-file';

  const onDrop = useCallback(
    (acceptedFiles) => {
      if (acceptedFiles.length > 0) {
        const file = acceptedFiles[0];
        const reader = new FileReader();

        reader.onload = () => {
          // Pass BOTH the preview (base64) and the actual file
          onImageSelect({
            preview: reader.result,
            file: file,
            name: file.name,
          });
          
          // Clear previous messages when new image is selected
          setSuccessMessage('');
          setErrorMessage('');
          setUploadProgress(0);
        };

        reader.readAsDataURL(file);
      }
    },
    [onImageSelect]
  );

  const handleUpload = useCallback(() => {
    if (!selectedImage?.file) {
      setErrorMessage('Please select an image first!');
      return;
    }

    setIsLoading(true);
    setUploadProgress(0);
    setSuccessMessage('');
    setErrorMessage('');

    const formData = new FormData();
    formData.append('file', selectedImage.file);

    const xhr = new XMLHttpRequest();

    // Track upload progress
    xhr.upload.onprogress = (event) => {
      if (event.lengthComputable) {
        const progress = Math.round((event.loaded / event.total) * 100);
        setUploadProgress(progress);
      }
    };

    // Handle successful upload
    xhr.onload = () => {
      setIsLoading(false);
      
      if (xhr.status >= 200 && xhr.status < 300) {
        try {
          const response = JSON.parse(xhr.responseText);
          setSuccessMessage('Upload successful!');
          setUploadProgress(100);
          
          // Pass the result to parent component
          if (onClassificationResult) {
            onClassificationResult(response);
          }
        } catch (error) {
          setErrorMessage('Failed to parse response from server');
        }
      } else {
        setErrorMessage(`Upload failed with status: ${xhr.status} - ${xhr.statusText}`);
      }
    };

    // Handle upload errors
    xhr.onerror = () => {
      setIsLoading(false);
      setErrorMessage('Upload failed due to network error');
    };

    // Handle timeout
    xhr.ontimeout = () => {
      setIsLoading(false);
      setErrorMessage('Upload timed out');
    };

    // Configure and send request
    xhr.open('POST', uploadUrl, true);
    xhr.timeout = 30000; // 30 second timeout
    xhr.send(formData);
  }, [selectedImage, uploadUrl, onClassificationResult]);

  const handleClear = useCallback(() => {
    // Revoke object URL to prevent memory leaks
    if (selectedImage?.preview) {
      URL.revokeObjectURL(selectedImage.preview);
    }
    
    onImageSelect(null);
    setSuccessMessage('');
    setErrorMessage('');
    setUploadProgress(0);
    setIsLoading(false);
  }, [selectedImage, onImageSelect]);

  const handleClick = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!isLoading && fileInputRef.current) {
      fileInputRef.current.click();
    }
  }, [isLoading]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'image/*': ['.jpeg', '.jpg', '.png'],
    },
    multiple: false,
    disabled: isLoading,
    noClick: false,
    noKeyboard: false,
  });

  const dropZoneClass = `mx-auto w-full max-w-2xl grid place-items-center min-h-64 border-2 border-dashed rounded-2xl p-8 text-center cursor-pointer shadow-sm transition ${
    isDragActive
      ? 'bg-blue-50 border-blue-400'
      : 'bg-gray-50 border-gray-300 hover:border-gray-400'
  } ${isLoading ? 'opacity-50 cursor-not-allowed' : ''}`;

  return (
    <div className="image-uploader space-y-4">
      <div {...getRootProps()} className={dropZoneClass} onClick={handleClick}>
        <input 
          {...getInputProps()} 
          ref={fileInputRef}
          style={{ display: 'none' }}
        />
        {selectedImage ? (
          <div className="flex flex-col items-center gap-4">
            <img
              src={selectedImage.preview}
              alt="Selected mushroom"
              className="max-w-full max-h-80 rounded-xl shadow-lg"
            />
            <div className="text-sm text-gray-500">
              Click or drag to change image
            </div>
          </div>
        ) : (
          <div className="space-y-4">
            <div className="text-6xl text-gray-400">
              <svg
                className="w-16 h-16 mx-auto"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                />
              </svg>
            </div>
            <div className="space-y-2">
              <p className="text-lg font-medium text-gray-700">
                {isDragActive
                  ? 'Drop the mushroom image here...'
                  : 'Drag & drop a mushroom image here, or click to select'}
              </p>
              <p className="text-sm text-gray-500">Supports JPG, PNG, JPEG</p>
            </div>
          </div>
        )}
      </div>

      {/* Upload Progress Bar */}
      {isLoading && (
        <div className="w-full max-w-2xl mx-auto">
          <div className="flex justify-between text-sm text-gray-600 mb-2">
            <span>Uploading...</span>
            <span>{uploadProgress}%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div
              className="bg-blue-600 h-2 rounded-full transition-all duration-300 ease-out"
              style={{ width: `${uploadProgress}%` }}
            ></div>
          </div>
        </div>
      )}

      {/* Action Buttons */}
      {selectedImage && (
        <div className="flex gap-4 justify-center">
          <button
            onClick={handleUpload}
            disabled={isLoading}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg shadow hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isLoading ? 'Uploading...' : 'Classify Mushroom'}
          </button>
          <button
            onClick={handleClear}
            disabled={isLoading}
            className="px-6 py-2 bg-gray-500 text-white rounded-lg shadow hover:bg-gray-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Clear
          </button>
        </div>
      )}

      {/* Success Message */}
      {successMessage && (
        <div className="w-full max-w-2xl mx-auto p-4 bg-green-50 border border-green-200 rounded-lg">
          <p className="text-green-800 font-medium">{successMessage}</p>
        </div>
      )}

      {/* Error Message */}
      {errorMessage && (
        <div className="w-full max-w-2xl mx-auto p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-800 font-medium">{errorMessage}</p>
        </div>
      )}
    </div>
  );
};

export default ImageUploader;
