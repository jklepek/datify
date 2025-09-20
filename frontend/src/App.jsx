import React, { useState, useEffect } from 'react';
import FileUpload from './components/FileUpload.jsx';
import DocumentList from './components/DocumentList.jsx';
import ChatInterface from './components/ChatInterface.jsx';
import InvoiceDashboard from './components/InvoiceDashboard.jsx';
import { documentAPI } from './services/api';

function App() {
  const [documents, setDocuments] = useState([]);
  const [selectedDocument, setSelectedDocument] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('documents');

  useEffect(() => {
    loadDocuments();
  }, []);

  const loadDocuments = async () => {
    try {
      setLoading(true);
      const response = await documentAPI.getAllDocuments();
      setDocuments(response.data);
      setError('');
    } catch (err) {
      setError('Chyba při načítání dokumentů: ' + (err.response?.data || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleUploadSuccess = (newDocument) => {
    setDocuments(prev => [...prev, newDocument]);
  };

  const handleSelectDocument = (documentId, _filename) => {
    const document = documents.find(doc => doc.id === documentId);
    setSelectedDocument(document);
  };

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="container mx-auto px-4 py-8 max-w-7xl">
        {/* Header */}
        <header className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">DATIFY</h1>
          <p className="text-gray-600">AI systém pro dotazy na dokumenty</p>
        </header>

        {/* Error Message */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg mb-6">
            {error}
            <button
              onClick={() => setError('')}
              className="ml-2 text-red-500 hover:text-red-700"
            >
              ✕
            </button>
          </div>
        )}

        {/* File Upload */}
        <FileUpload onUploadSuccess={handleUploadSuccess} />

        {/* Navigation Tabs */}
        <div className="bg-white rounded-lg shadow-md mb-8 overflow-hidden">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex">
              <button
                onClick={() => setActiveTab('documents')}
                className={`py-4 px-6 text-sm font-medium border-b-2 ${
                  activeTab === 'documents'
                    ? 'border-blue-500 text-blue-600 bg-blue-50'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                📄 Dokumenty a dotazy
              </button>
              <button
                onClick={() => setActiveTab('invoices')}
                className={`py-4 px-6 text-sm font-medium border-b-2 ${
                  activeTab === 'invoices'
                    ? 'border-green-500 text-green-600 bg-green-50'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                💰 Faktury
              </button>
            </nav>
          </div>
        </div>

        {/* Loading State */}
        {loading ? (
          <div className="bg-white rounded-lg shadow-md p-6 mb-8 text-center">
            <div className="loading mx-auto mb-2"></div>
            <p className="text-gray-600">Načítání...</p>
          </div>
        ) : (
          <>
            {/* Documents Tab Content */}
            {activeTab === 'documents' && (
              <>
                <DocumentList
                  documents={documents}
                  selectedDocumentId={selectedDocument?.id}
                  onSelectDocument={handleSelectDocument}
                />
                <ChatInterface selectedDocument={selectedDocument} />
              </>
            )}

            {/* Invoices Tab Content */}
            {activeTab === 'invoices' && <InvoiceDashboard />}
          </>
        )}
      </div>
    </div>
  );
}

export default App;
