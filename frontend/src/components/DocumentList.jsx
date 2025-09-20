import React from 'react';

const DocumentList = ({ documents, selectedDocumentId, onSelectDocument }) => {
  if (documents.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6 mb-8">
        <h2 className="text-2xl font-semibold text-gray-800 mb-4">Nahrané dokumenty</h2>
        <p className="text-gray-500">Žádné dokumenty zatím nebyly nahrány.</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6 mb-8">
      <h2 className="text-2xl font-semibold text-gray-800 mb-4">Nahrané dokumenty</h2>
      <div className="space-y-3">
        {documents.map((doc) => (
          <div
            key={doc.id}
            className={`border rounded-lg p-4 hover:bg-gray-50 transition-colors ${
              selectedDocumentId === doc.id
                ? 'bg-blue-50 border-blue-200'
                : 'border-gray-200'
            }`}
          >
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <h3 className="font-medium text-gray-800">{doc.filename}</h3>
                <p className="text-sm text-gray-600 mt-1">
                  Nahráno: {new Date(doc.uploadedAt).toLocaleDateString('cs-CZ')} |
                  Velikost textu: {doc.textLength} znaků
                </p>
              </div>
              <button
                onClick={() => onSelectDocument(doc.id, doc.filename)}
                className={`px-3 py-1 rounded text-sm transition-colors ${
                  selectedDocumentId === doc.id
                    ? 'bg-green-600 text-white'
                    : 'bg-primary text-white hover:bg-blue-700'
                }`}
              >
                {selectedDocumentId === doc.id ? 'Vybrán' : 'Vybrat'}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default DocumentList;