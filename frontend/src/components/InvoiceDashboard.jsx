import React, { useState, useEffect } from 'react';
import { documentAPI } from '../services/api';

const InvoiceDashboard = () => {
  const [invoices, setInvoices] = useState([]);
  const [summary, setSummary] = useState(null);
  const [overdueInvoices, setOverdueInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('all');

  useEffect(() => {
    loadInvoiceData();
  }, []);

  const loadInvoiceData = async () => {
    try {
      setLoading(true);

      const [invoicesRes, summaryRes, overdueRes] = await Promise.all([
        documentAPI.getAllInvoices(),
        documentAPI.getInvoiceSummary(),
        documentAPI.getOverdueInvoices()
      ]);

      setInvoices(invoicesRes.data);
      setSummary(summaryRes.data);
      setOverdueInvoices(overdueRes.data);
      setError('');
    } catch (err) {
      setError('Chyba při načítání fakturačních dat: ' + (err.response?.data || err.message));
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount, currency = 'CZK') => {
    if (!amount) return 'N/A';
    return new Intl.NumberFormat('cs-CZ', {
      style: 'currency',
      currency: currency
    }).format(amount);
  };

  const formatDate = (date) => {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('cs-CZ');
  };

  const isOverdue = (dueDate) => {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date();
  };

  const getStatusBadgeColor = (status) => {
    switch (status) {
      case 'PAID':
        return 'bg-green-100 text-green-800';
      case 'OVERDUE':
        return 'bg-red-100 text-red-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'CANCELLED':
        return 'bg-gray-100 text-gray-800';
      case 'DISPUTED':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const translateStatus = (status) => {
    switch (status) {
      case 'PAID':
        return 'Zaplaceno';
      case 'OVERDUE':
        return 'Po splatnosti';
      case 'PENDING':
        return 'Čekající';
      case 'CANCELLED':
        return 'Zrušeno';
      case 'DISPUTED':
        return 'Sporné';
      default:
        return 'Neznámé';
    }
  };

  const renderSummaryCards = () => (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-sm font-medium text-gray-500 mb-2">Celkem faktur</h3>
        <p className="text-3xl font-bold text-gray-900">{summary?.totalInvoices || 0}</p>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-sm font-medium text-gray-500 mb-2">Po splatnosti</h3>
        <p className="text-3xl font-bold text-red-600">{summary?.overdueInvoices || 0}</p>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-sm font-medium text-gray-500 mb-2">Celková částka</h3>
        <p className="text-3xl font-bold text-gray-900">
          {formatCurrency(summary?.totalAmount)}
        </p>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-sm font-medium text-gray-500 mb-2">Částka po splatnosti</h3>
        <p className="text-3xl font-bold text-red-600">
          {formatCurrency(summary?.overdueAmount)}
        </p>
      </div>
    </div>
  );

  const renderInvoiceTable = (invoiceList, title) => (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      <div className="px-6 py-4 border-b border-gray-200">
        <h3 className="text-lg font-medium text-gray-900">{title}</h3>
      </div>

      {invoiceList.length === 0 ? (
        <div className="px-6 py-8 text-center text-gray-500">
          Žádné faktury nebyly nalezeny
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Faktura č.
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Dodavatel
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Částka
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Datum splatnosti
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Stav
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Spolehlivost
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {invoiceList.map((invoice) => (
                <tr key={invoice.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {invoice.invoiceNumber || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {invoice.vendorName || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {formatCurrency(invoice.totalAmount, invoice.currency)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    <span className={isOverdue(invoice.dueDate) ? 'text-red-600 font-medium' : ''}>
                      {formatDate(invoice.dueDate)}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusBadgeColor(invoice.status)}`}>
                      {translateStatus(invoice.status || 'PENDING')}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {invoice.confidenceScore ?
                      `${Math.round(invoice.confidenceScore * 100)}%` :
                      'N/A'
                    }
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6 text-center">
        <div className="loading mx-auto mb-2"></div>
        <p className="text-gray-600">Načítání fakturačních dat...</p>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">Přehled faktur</h2>
        <p className="text-gray-600">Správa a sledování vašich faktur</p>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg">
          {error}
          <button
            onClick={() => setError('')}
            className="ml-2 text-red-500 hover:text-red-700"
          >
            ✕
          </button>
        </div>
      )}

      {/* Summary Cards */}
      {summary && renderSummaryCards()}

      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          <button
            onClick={() => setActiveTab('all')}
            className={`py-2 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'all'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Všechny faktury ({invoices.length})
          </button>
          <button
            onClick={() => setActiveTab('overdue')}
            className={`py-2 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'overdue'
                ? 'border-red-500 text-red-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Po splatnosti ({overdueInvoices.length})
          </button>
        </nav>
      </div>

      {/* Invoice Tables */}
      {activeTab === 'all' && renderInvoiceTable(invoices, 'Všechny faktury')}
      {activeTab === 'overdue' && renderInvoiceTable(overdueInvoices, 'Faktury po splatnosti')}
    </div>
  );
};

export default InvoiceDashboard;
