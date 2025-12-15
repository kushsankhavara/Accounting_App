const BASE_URL = 'http://localhost:8080/api';

const parseResponse = async (response) => {
  if (!response.ok) {
    throw new Error('Request failed');
  }
  return response.json();
};

export const fetchTransactions = async (filters = {}) => {
  const params = new URLSearchParams();
  Object.entries(filters).forEach(([key, value]) => {
    if (value) {
      params.append(key, value);
    }
  });
  const query = params.toString();
  const response = await fetch(`${BASE_URL}/transactions${query ? `?${query}` : ''}`);
  return parseResponse(response);
};

export const addTransaction = async (payload) => {
  const response = await fetch(`${BASE_URL}/transactions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  return parseResponse(response);
};

export const deleteTransaction = async (id) => {
  const response = await fetch(`${BASE_URL}/transactions/${id}`, {
    method: 'DELETE'
  });
  if (!response.ok && response.status !== 204) {
    throw new Error('Failed to delete');
  }
};

export const fetchMonthlySummary = async (year, month) => {
  const response = await fetch(`${BASE_URL}/summary/monthly?year=${year}&month=${month}`);
  return parseResponse(response);
};

export const fetchCategorySummary = async (startDate, endDate) => {
  const params = new URLSearchParams();
  if (startDate) params.append('startDate', startDate);
  if (endDate) params.append('endDate', endDate);
  const query = params.toString();
  const response = await fetch(`${BASE_URL}/summary/categories${query ? `?${query}` : ''}`);
  return parseResponse(response);
};

export const fetchAccounts = async () => {
  const response = await fetch(`${BASE_URL}/accounts`);
  return parseResponse(response);
};

export const createAccount = async (payload) => {
  const response = await fetch(`${BASE_URL}/accounts`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  return parseResponse(response);
};
