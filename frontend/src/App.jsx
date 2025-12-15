import { useEffect, useMemo, useState } from 'react';
import {
  addTransaction,
  createAccount,
  deleteTransaction,
  fetchAccounts,
  fetchCategorySummary,
  fetchMonthlySummary,
  fetchTransactions
} from './api';

const initialForm = {
  date: '',
  amount: '',
  type: 'EXPENSE',
  category: '',
  account: '',
  note: '',
  paymentMode: ''
};

function App() {
  const [transactions, setTransactions] = useState([]);
  const [filters, setFilters] = useState({ startDate: '', endDate: '', category: '', account: '', type: '' });
  const [form, setForm] = useState(initialForm);
  const [monthly, setMonthly] = useState(null);
  const [categorySummary, setCategorySummary] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [accountForm, setAccountForm] = useState({ name: '', description: '' });

  useEffect(() => {
    loadData();
    loadAccounts();
  }, []);

  const loadData = async () => {
    const tx = await fetchTransactions(filters);
    setTransactions(tx);
    if (filters.startDate) {
      const date = new Date(filters.startDate);
      const summary = await fetchMonthlySummary(date.getFullYear(), date.getMonth() + 1);
      setMonthly(summary);
    } else {
      setMonthly(null);
    }
    const catSummary = await fetchCategorySummary(filters.startDate, filters.endDate);
    setCategorySummary(catSummary);
  };

  const loadAccounts = async () => {
    const response = await fetchAccounts();
    setAccounts(response);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await addTransaction(form);
    setForm(initialForm);
    await Promise.all([loadData(), loadAccounts()]);
  };

  const balance = useMemo(() => {
    const income = transactions.filter((t) => t.type === 'INCOME').reduce((sum, t) => sum + Number(t.amount), 0);
    const expense = transactions.filter((t) => t.type === 'EXPENSE').reduce((sum, t) => sum + Number(t.amount), 0);
    return income - expense;
  }, [transactions]);

  const updateFilter = (field, value) => {
    setFilters({ ...filters, [field]: value });
  };

  const applyFilters = async () => {
    const tx = await fetchTransactions(filters);
    setTransactions(tx);
    const catSummary = await fetchCategorySummary(filters.startDate, filters.endDate);
    setCategorySummary(catSummary);
  };

  const handleDelete = async (id) => {
    await deleteTransaction(id);
    await loadData();
  };

  const handleAccountSubmit = async (e) => {
    e.preventDefault();
    if (!accountForm.name) return;
    await createAccount(accountForm);
    setAccountForm({ name: '', description: '' });
    await loadAccounts();
  };

  return (
    <div className="page">
      <header>
        <h1>Home Accounts</h1>
        <p className="muted">Track income, expenses, and download CSVs from your Spring Boot backend.</p>
      </header>

      <section className="cards">
        <div className="card">
          <h2>Add Transaction</h2>
          <form className="grid" onSubmit={handleSubmit}>
            <label>
              Date
              <input type="date" required value={form.date} onChange={(e) => setForm({ ...form, date: e.target.value })} />
            </label>
            <label>
              Amount
              <input type="number" step="0.01" required value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} />
            </label>
            <label>
              Type
              <select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}>
                <option value="EXPENSE">Expense</option>
                <option value="INCOME">Income</option>
              </select>
            </label>
            <label>
              Category
              <input type="text" required value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} />
            </label>
            <label>
              Account
              <input
                list="account-options"
                type="text"
                required
                value={form.account}
                onChange={(e) => setForm({ ...form, account: e.target.value })}
              />
              <datalist id="account-options">
                {accounts.map((account) => (
                  <option key={account.id} value={account.name}>{account.description}</option>
                ))}
              </datalist>
            </label>
            <label>
              Payment mode
              <input type="text" required value={form.paymentMode} onChange={(e) => setForm({ ...form, paymentMode: e.target.value })} />
            </label>
            <label className="full">
              Note
              <input type="text" value={form.note} onChange={(e) => setForm({ ...form, note: e.target.value })} />
            </label>
            <button type="submit" className="primary">Save</button>
          </form>
        </div>

        <div className="card">
          <h2>Filters</h2>
          <div className="grid">
            <label>
              Start date
              <input type="date" value={filters.startDate} onChange={(e) => updateFilter('startDate', e.target.value)} />
            </label>
            <label>
              End date
              <input type="date" value={filters.endDate} onChange={(e) => updateFilter('endDate', e.target.value)} />
            </label>
            <label>
              Category
              <input type="text" value={filters.category} onChange={(e) => updateFilter('category', e.target.value)} />
            </label>
            <label>
              Account
              <select value={filters.account} onChange={(e) => updateFilter('account', e.target.value)}>
                <option value="">Any</option>
                {accounts.map((account) => (
                  <option key={account.id} value={account.name}>{account.name}</option>
                ))}
              </select>
            </label>
            <label>
              Type
              <select value={filters.type} onChange={(e) => updateFilter('type', e.target.value)}>
                <option value="">Any</option>
                <option value="EXPENSE">Expense</option>
                <option value="INCOME">Income</option>
              </select>
            </label>
            <button onClick={applyFilters} className="secondary">Apply filters</button>
          </div>
          <div className="stats">
            <div>
              <span className="label">Balance</span>
              <strong className={balance < 0 ? 'negative' : ''}>{balance.toFixed(2)}</strong>
            </div>
            {monthly && (
              <div>
                <span className="label">Monthly</span>
                <strong>{monthly.totalIncome} - {monthly.totalExpense} = {monthly.balance}</strong>
              </div>
            )}
          </div>
        </div>
      </section>

      <section className="card">
        <h2>Transactions</h2>
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Type</th>
              <th>Category</th>
              <th>Account</th>
              <th>Amount</th>
              <th>Payment</th>
              <th>Note</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((t) => (
              <tr key={t.id}>
                <td>{t.date}</td>
                <td>{t.type}</td>
                <td>{t.category}</td>
                <td>{t.account}</td>
                <td className={t.type === 'EXPENSE' ? 'negative' : 'positive'}>{t.amount}</td>
                <td>{t.paymentMode}</td>
                <td>{t.note}</td>
                <td>
                  <button className="link" onClick={() => handleDelete(t.id)}>Delete</button>
                </td>
              </tr>
            ))}
            {transactions.length === 0 && (
              <tr><td colSpan="8" className="muted">No transactions yet.</td></tr>
            )}
          </tbody>
        </table>
      </section>

      <section className="card">
        <h2>Accounts</h2>
        <form className="grid" onSubmit={handleAccountSubmit}>
          <label>
            Account name
            <input type="text" required value={accountForm.name} onChange={(e) => setAccountForm({ ...accountForm, name: e.target.value })} />
          </label>
          <label className="full">
            Description
            <input type="text" value={accountForm.description} onChange={(e) => setAccountForm({ ...accountForm, description: e.target.value })} />
          </label>
          <button type="submit" className="secondary">Save account</button>
        </form>
        <ul className="categories">
          {accounts.map((account) => (
            <li key={account.id}>
              <span>{account.name}</span>
              <small className="muted">{account.description || 'Personal directory'}</small>
            </li>
          ))}
          {accounts.length === 0 && <li className="muted">No accounts yet.</li>}
        </ul>
      </section>

      <section className="card">
        <h2>Category breakdown</h2>
        <ul className="categories">
          {categorySummary.map((c) => (
            <li key={c.category}>
              <span>{c.category}</span>
              <strong>{c.total}</strong>
            </li>
          ))}
          {categorySummary.length === 0 && <li className="muted">No data yet.</li>}
        </ul>
      </section>
    </div>
  );
}

export default App;
