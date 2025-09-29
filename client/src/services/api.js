import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const classificationAPI = {
  // Classify image using base64 data
  classifyImage: async (imageData, imageName) => {
    try {
      const response = await api.post('/classifications/classify', {
        imageData,
        imageName,
      });
      return response.data;
    } catch (error) {
      console.error('Error classifying image:', error);
      throw error;
    }
  },

  // Classify image using file upload
  classifyFile: async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await api.post('/classifications/classify-file', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      console.error('Error classifying file:', error);
      throw error;
    }
  },

  // Get all classification results
  getAllClassifications: async () => {
    try {
      const response = await api.get('/classifications');
      return response.data;
    } catch (error) {
      console.error('Error fetching classifications:', error);
      throw error;
    }
  },

  // Get classification by ID
  getClassificationById: async (id) => {
    try {
      const response = await api.get(`/classifications/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching classification:', error);
      throw error;
    }
  },

  // Get classifications by prediction
  getClassificationsByPrediction: async (prediction) => {
    try {
      const response = await api.get(`/classifications/prediction/${prediction}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching classifications by prediction:', error);
      throw error;
    }
  },

  // Get statistics
  getStats: async () => {
    try {
      const [edibleCount, poisonousCount, edibleConfidence, poisonousConfidence] = await Promise.all([
        api.get('/classifications/stats/count/edible'),
        api.get('/classifications/stats/count/poisonous'),
        api.get('/classifications/stats/confidence/edible'),
        api.get('/classifications/stats/confidence/poisonous'),
      ]);

      return {
        edibleCount: edibleCount.data,
        poisonousCount: poisonousCount.data,
        edibleConfidence: edibleConfidence.data,
        poisonousConfidence: poisonousConfidence.data,
      };
    } catch (error) {
      console.error('Error fetching statistics:', error);
      throw error;
    }
  },
};

export const healthAPI = {
  checkHealth: async () => {
    try {
      const response = await api.get('/health');
      return response.data;
    } catch (error) {
      console.error('Error checking health:', error);
      throw error;
    }
  },
};

export default api;
