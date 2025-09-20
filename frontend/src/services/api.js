import axios from 'axios';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
});

export const documentAPI = {
  uploadDocument: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/documents/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  getAllDocuments: () => {
    return api.get('/documents');
  },

  getDocument: (id) => {
    return api.get(`/documents/${id}`);
  },

  askQuestion: (documentId, question) => {
    return api.post(`/documents/${documentId}/ask`, {
      question: question,
    });
  },

  askGlobalQuestion: (question) => {
    return api.post('/documents/ask', {
      question: question,
    });
  },

  // Invoice endpoints
  getAllInvoices: () => {
    return api.get('/documents/invoices');
  },

  getOverdueInvoices: () => {
    return api.get('/documents/invoices/overdue');
  },

  getInvoiceSummary: () => {
    return api.get('/documents/invoices/summary');
  },

  getInvoiceForDocument: (documentId) => {
    return api.get(`/documents/${documentId}/invoice`);
  },

  getInvoicesByVendor: (vendorName) => {
    return api.get(`/documents/invoices/vendor/${encodeURIComponent(vendorName)}`);
  },
};

export default api;