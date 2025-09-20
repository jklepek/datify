import React, { useState, useRef, useEffect } from 'react';
import { documentAPI } from '../services/api';

const ChatInterface = ({ selectedDocument }) => {
  const [question, setQuestion] = useState('');
  const [chatHistory, setChatHistory] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [queryMode, setQueryMode] = useState('global'); // 'global' or 'document'
  const chatEndRef = useRef(null);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chatHistory]);

  useEffect(() => {
    if (selectedDocument && queryMode === 'document') {
      setChatHistory([
        {
          id: Date.now(),
          type: 'system',
          message: `Dokument "${selectedDocument.filename}" vybrán. Můžete klást otázky na tento konkrétní dokument.`
        }
      ]);
    } else if (queryMode === 'global') {
      setChatHistory([
        {
          id: Date.now(),
          type: 'system',
          message: 'Režim globálního vyhledávání aktivní. Můžete klást otázky napříč všemi dokumenty.'
        }
      ]);
    }
  }, [selectedDocument, queryMode]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!question.trim() || isLoading) return;
    if (queryMode === 'document' && !selectedDocument) return;

    const userMessage = {
      id: Date.now(),
      type: 'user',
      message: question
    };

    setChatHistory(prev => [...prev, userMessage]);
    setQuestion('');
    setIsLoading(true);

    try {
      let response;
      if (queryMode === 'global') {
        response = await documentAPI.askGlobalQuestion(question);
      } else {
        response = await documentAPI.askQuestion(selectedDocument.id, question);
      }

      const aiMessage = {
        id: Date.now() + 1,
        type: 'assistant',
        message: response.data.answer
      };

      setChatHistory(prev => [...prev, aiMessage]);
    } catch (error) {
      const errorMessage = {
        id: Date.now() + 1,
        type: 'error',
        message: `Chyba: ${error.response?.data || error.message}`
      };

      setChatHistory(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-semibold text-gray-800">
          {queryMode === 'global'
            ? 'Dotaz napříč všemi dokumenty'
            : `Dotaz na dokument: ${selectedDocument?.filename || 'Nevybrán'}`}
        </h2>

        <div className="flex gap-2">
          <button
            onClick={() => setQueryMode('global')}
            className={`px-3 py-1 rounded-md text-sm transition-colors ${
              queryMode === 'global'
                ? 'bg-primary text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Všechny dokumenty
          </button>
          <button
            onClick={() => setQueryMode('document')}
            className={`px-3 py-1 rounded-md text-sm transition-colors ${
              queryMode === 'document'
                ? 'bg-primary text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
            disabled={!selectedDocument}
          >
            Konkrétní dokument
          </button>
        </div>
      </div>

      <div className="bg-gray-50 rounded-lg p-4 mb-4 h-64 overflow-y-auto">
        {chatHistory.length === 0 ? (
          <p className="text-gray-500 text-center">Vyberte dokument a položte otázku...</p>
        ) : (
          <div className="space-y-3">
            {chatHistory.map((message) => (
              <div
                key={message.id}
                className={`chat-message ${
                  message.type === 'user' ? 'ml-auto' : 'mr-auto'
                }`}
              >
                <div
                  className={`rounded-lg p-3 ${
                    message.type === 'user'
                      ? 'bg-primary text-white'
                      : message.type === 'error'
                      ? 'bg-red-100 border border-red-200 text-red-700'
                      : message.type === 'system'
                      ? 'bg-green-100 border border-green-200 text-green-700'
                      : 'bg-white border border-gray-200'
                  }`}
                >
                  <div className="text-xs text-opacity-70 mb-1">
                    {message.type === 'user'
                      ? 'Vy'
                      : message.type === 'system'
                      ? 'Systém'
                      : 'DATIFY'}
                  </div>
                  <div className="whitespace-pre-wrap">{message.message}</div>
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="chat-message mr-auto">
                <div className="bg-white border border-gray-200 rounded-lg p-3">
                  <div className="text-xs text-gray-500 mb-1">DATIFY</div>
                  <div className="flex items-center">
                    <div className="loading mr-2"></div>
                    <span>Hledám odpověď...</span>
                  </div>
                </div>
              </div>
            )}
            <div ref={chatEndRef} />
          </div>
        )}
      </div>

      <form onSubmit={handleSubmit} className="flex gap-2">
        <input
          type="text"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder={
            queryMode === 'global'
              ? 'Zeptejte se napříč všemi dokumenty...'
              : selectedDocument
              ? 'Zeptejte se na vybraný dokument...'
              : 'Vyberte dokument nebo přepněte na globální režim...'
          }
          className="flex-1 border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          disabled={isLoading || (queryMode === 'document' && !selectedDocument)}
        />
        <button
          type="submit"
          className="bg-primary text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
          disabled={
            !question.trim() ||
            isLoading ||
            (queryMode === 'document' && !selectedDocument)
          }
        >
          {isLoading ? 'Čekám...' : 'Zeptat se'}
        </button>
      </form>
    </div>
  );
};

export default ChatInterface;