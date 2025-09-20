import React, { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { documentAPI } from '../services/api';

const FileUpload = ({ onUploadSuccess }) => {
  const [uploading, setUploading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState('');

  const onDrop = useCallback(async (acceptedFiles) => {
    if (acceptedFiles.length === 0) return;

    const file = acceptedFiles[0];

    // Validate file type
    if (!file.type.includes('pdf') && !file.type.includes('text')) {
      setUploadStatus('Pouze PDF a TXT soubory jsou podporované.');
      return;
    }

    setUploading(true);
    setUploadStatus('Nahrávám dokument...');

    try {
      const response = await documentAPI.uploadDocument(file);
      setUploadStatus('Dokument byl úspěšně nahrán!');
      onUploadSuccess(response.data);

      // Clear status after 3 seconds
      setTimeout(() => {
        setUploadStatus('');
      }, 3000);
    } catch (error) {
      const errorMessage = error.response?.data || error.message;
      setUploadStatus(`Chyba při nahrávání: ${errorMessage}`);
    } finally {
      setUploading(false);
    }
  }, [onUploadSuccess]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
      'text/plain': ['.txt']
    },
    maxSize: 10 * 1024 * 1024, // 10MB
    multiple: false
  });

  return (
    <div className="bg-white rounded-lg shadow-md p-6 mb-8">
      <h2 className="text-2xl font-semibold text-gray-800 mb-4">Nahrát dokument</h2>

      <div
        {...getRootProps()}
        className={`file-drop-zone rounded-lg p-8 text-center mb-4 cursor-pointer ${
          isDragActive ? 'dragover' : ''
        }`}
      >
        <input {...getInputProps()} />
        <div className="mb-4">
          <svg
            className="mx-auto h-12 w-12 text-gray-400"
            stroke="currentColor"
            fill="none"
            viewBox="0 0 48 48"
          >
            <path
              d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </div>
        {isDragActive ? (
          <p className="text-primary font-medium">Pusťte soubor zde...</p>
        ) : (
          <>
            <p className="text-gray-600 mb-2">Přetáhněte soubor sem nebo klikněte pro výběr</p>
            <p className="text-sm text-gray-500">Podporované formáty: PDF, TXT (max 10MB)</p>
          </>
        )}
      </div>

      {uploadStatus && (
        <div className={`p-3 rounded-md ${
          uploadStatus.includes('Chyba')
            ? 'bg-red-50 border border-red-200 text-red-700'
            : uploading
            ? 'bg-blue-50 border border-blue-200 text-blue-700'
            : 'bg-green-50 border border-green-200 text-green-700'
        }`}>
          <div className="flex items-center">
            {uploading && <div className="loading mr-2"></div>}
            <span>{uploadStatus}</span>
          </div>
        </div>
      )}
    </div>
  );
};

export default FileUpload;