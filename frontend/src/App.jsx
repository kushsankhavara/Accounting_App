import { useEffect, useMemo, useState } from 'react';
import { addTransaction, fetchCategorySummary, fetchMonthlySummary, fetchTransactions } from './api';

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

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const tx = await fetchTransactions(filters);
    setTransactions(tx);
    if (filters.startDate) {
      const date = new Date(filters.startDate);
      const summary = await fetchMonthlySummary(date.getFullYear(), date.getMonth() + 1);
      setMonthly(summary);
    }
    const catSummary = await fetchCategorySummary(filters.startDate, filters.endDate);
    setCategorySummary(catSummary);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await addTransaction(form);
    setForm(initialForm);
    await loadData();
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
              <input type="text" required value={form.account} onChange={(e) => setForm({ ...form, account: e.target.value })} />
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
              <input type="text" value={filters.account} onChange={(e) => updateFilter('account', e.target.value)} />
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
              </tr>
            ))}
            {transactions.length === 0 && (
              <tr><td colSpan="7" className="muted">No transactions yet.</td></tr>
            )}
          </tbody>
        </table>
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
