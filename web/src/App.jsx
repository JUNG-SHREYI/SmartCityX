import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Activity, AlertTriangle, ArrowRight, ArrowUpRight,
  Bell, BookOpen, Boxes, Building2, Check, ChevronRight,
  Clock3, Cloud, FileText, Gauge, Grid2X2, HardHat, LayoutDashboard,
  MapPin, Plus, RefreshCw, Search, ShieldCheck, Siren, Trash2, Users,
  Waves, Wifi, X, Zap,
} from 'lucide-react';

const navigation = [
  { id: 'overview', label: 'City overview', icon: LayoutDashboard, group: 'CITY OPERATIONS' },
  { id: 'incidents', label: 'Citizen reports', icon: Siren },
  { id: 'infrastructure', label: 'Infrastructure', icon: Building2 },
  { id: 'utilities', label: 'Utility network', icon: Zap },
  { id: 'sensors', label: 'IoT telemetry', icon: Wifi, group: 'MONITORING' },
  { id: 'crews', label: 'Response teams', icon: Users },
  { id: 'documents', label: 'City documents', icon: FileText },
  { id: 'risk', label: 'Risk watch', icon: ShieldCheck },
  { id: 'algorithms', label: 'DSA field guide', icon: BookOpen, group: 'INSIGHTS' },
];

const algorithmGuides = [
  { name: 'Citizen report search', module: 'M1 · String matching', detail: 'KMP, Z-algorithm, Rabin–Karp, Aho–Corasick', use: 'Find incidents by keywords and triage multiple emergency terms.' },
  { name: 'Document intelligence', module: 'M2 · Suffix structures', detail: 'Suffix arrays, SA-IS, Kasai LCP, suffix automaton', use: 'Index municipal policy text and identify repeated clauses.' },
  { name: 'Dispatch & resource planning', module: 'M3 · Dynamic programming', detail: 'Edit distance, bitmask routing, matrix-chain planning', use: 'Support typo-tolerant searches and route-planning scenarios.' },
  { name: 'Utility flow analysis', module: 'M4 · Network flow', detail: 'Edmonds–Karp, Dinic, max-flow min-cut', use: 'Model capacity and locate constrained points in a utility network.' },
  { name: 'Planning & surveillance', module: 'M5 · Approximation', detail: 'DPLL, polynomial reductions, vertex-cover approximation', use: 'Explore constraint feasibility and infrastructure surveillance planning.' },
  { name: 'Streaming telemetry', module: 'M6 · Parallel & randomized', detail: 'Reservoir sampling, prefix scans, parallel work-depth analysis', use: 'Summarize sensor streams and reason about parallel processing.' },
];

const formatLabel = (value = '') => value.toLowerCase().split('_').map((part) => part.charAt(0).toUpperCase() + part.slice(1)).join(' ');
const formatTime = (timestamp) => new Intl.DateTimeFormat(undefined, { hour: '2-digit', minute: '2-digit' }).format(new Date(timestamp));
const formatDateTime = (timestamp) => new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(timestamp));

function App() {
  const [page, setPage] = useState(location.hash.slice(1) || 'overview');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [search, setSearch] = useState('');
  const [reportFilter, setReportFilter] = useState('ALL');
  const active = navigation.find((item) => item.id === page) || navigation[0];

  const loadDashboard = useCallback(async (quiet = false) => {
    if (quiet) setRefreshing(true);
    else setLoading(true);
    setError('');
    try {
      const response = await fetch('/api/dashboard', { headers: { Accept: 'application/json' } });
      const result = await response.json();
      if (!response.ok) throw new Error(result.error || 'Could not load city data.');
      setData(result);
    } catch (err) {
      setError(err.message || 'The dashboard could not connect to its data service.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  const createReport = useCallback(async (formData) => {
    const response = await fetch('/api/reports', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
      body: new URLSearchParams(formData),
    });
    const result = await response.json();
    if (!response.ok) throw new Error(result.error || 'The report could not be submitted.');
    await loadDashboard(true);
    setNotice(result.message);
    return result;
  }, [loadDashboard]);

  const assignTeam = useCallback(async (reportId, crewId) => {
    const response = await fetch(`/api/reports/${encodeURIComponent(reportId)}/assignment`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
      body: new URLSearchParams({ crewId }),
    });
    const result = await response.json();
    if (!response.ok) throw new Error(result.error || 'The response team could not be assigned.');
    await loadDashboard(true);
    setNotice(result.message);
    return result;
  }, [loadDashboard]);

  const deleteReport = useCallback(async (reportId) => {
    const response = await fetch(`/api/reports/${encodeURIComponent(reportId)}`, { method: 'DELETE' });
    const result = await response.json();
    if (!response.ok) throw new Error(result.error || 'The report could not be deleted.');
    await loadDashboard(true);
    setNotice(result.message);
    return result;
  }, [loadDashboard]);

  useEffect(() => {
    loadDashboard();
    const syncPage = () => {
      setPage(location.hash.slice(1) || 'overview');
      setSearch('');
      setNotice('');
    };
    window.addEventListener('hashchange', syncPage);
    return () => window.removeEventListener('hashchange', syncPage);
  }, [loadDashboard]);

  const stats = useMemo(() => {
    if (!data) return null;
    const critical = data.reports.filter((report) => report.priority === 1).length;
    const attentionAssets = data.assets.filter((asset) => asset.status !== 'OPERATIONAL' || asset.health < 75).length;
    const availableCrews = data.crews.filter((crew) => crew.status === 'AVAILABLE').length;
    const averageHealth = data.assets.reduce((sum, asset) => sum + asset.health, 0) / Math.max(data.assets.length, 1);
    return { critical, attentionAssets, availableCrews, averageHealth };
  }, [data]);

  function navigate(id) {
    location.hash = id;
    setPage(id);
    setSearch('');
    setReportFilter('ALL');
  }

  const filteredReports = useMemo(() => {
    if (!data) return [];
    const needle = search.trim().toLowerCase();
    return data.reports.filter((report) => (reportFilter === 'ALL' || String(report.priority) === reportFilter)
      && (!needle || [report.id, report.category, report.sector, report.description, report.citizen].some((value) => value.toLowerCase().includes(needle))));
  }, [data, reportFilter, search]);

  const filteredAssets = useMemo(() => {
    if (!data) return [];
    const needle = search.trim().toLowerCase();
    return data.assets.filter((asset) => !needle || [asset.id, asset.name, asset.type, asset.sector, asset.status].some((value) => value.toLowerCase().includes(needle)));
  }, [data, search]);

  const filteredSensors = useMemo(() => {
    if (!data) return [];
    const needle = search.trim().toLowerCase();
    return [...data.sensors].sort((a, b) => b.timestamp - a.timestamp)
      .filter((sensor) => !needle || [sensor.id, sensor.type, sensor.sector].some((value) => value.toLowerCase().includes(needle)));
  }, [data, search]);

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="#overview" onClick={() => navigate('overview')}>
          <span className="brand-mark"><Activity size={20} strokeWidth={2.5} /></span>
          <span className="brand-copy"><strong>SmartCity<span>X</span></strong><small>CITY INTELLIGENCE</small></span>
        </a>
        <div className="nav-scroll">
          {navigation.map((item, index) => {
            const ItemIcon = item.icon;
            return (
              <div key={item.id}>
                {item.group && <div className={`side-caption ${index > 0 ? 'with-space' : ''}`}>{item.group}</div>}
                <button className={`nav-item ${page === item.id ? 'selected' : ''}`} onClick={() => navigate(item.id)}>
                  <ItemIcon size={17} /><span>{item.label}</span>{page === item.id && <ChevronRight className="nav-chevron" size={15} />}
                </button>
              </div>
            );
          })}
        </div>
        <div className="sidebar-bottom">
          <div className="connection-card"><span className={`status-dot ${error ? 'offline' : ''}`} /><span><b>{error ? 'Connection issue' : 'City data connected'}</b><small>SmartCityX project dataset</small></span></div>
          <div className="course-badge"><Grid2X2 size={13} /> MUNICIPAL OPERATIONS</div>
        </div>
      </aside>

      <main className="main-area">
        <header className="topbar">
          <div className="breadcrumb"><span>Workspace</span><ChevronRight size={14} /><b>{active.label}</b></div>
          <div className="topbar-right">
            <span className="data-source"><i /> CITY DATASET</span>
            <span className="topbar-divider" />
            <span className="updated-label">{data ? `Updated ${formatTime(data.generatedAt)}` : 'Loading data'}</span>
            <button className={`icon-button ${refreshing ? 'is-refreshing' : ''}`} onClick={() => loadDashboard(true)} aria-label="Refresh city data" disabled={refreshing}><RefreshCw size={15} /></button>
            <span className="avatar">SC</span>
          </div>
        </header>
        <section className="content">
          <div className="page-heading">
            <div className="eyebrow">{active.group || 'CITY INTELLIGENCE'}</div>
            <div className="title-line"><div><h1>{active.label}</h1><p>{pageDescription(page)}</p></div><div className="heading-date"><Clock3 size={14} /> {new Intl.DateTimeFormat(undefined, { weekday: 'long', month: 'long', day: 'numeric' }).format(new Date())}</div></div>
          </div>

          {error && <div className="error-banner"><AlertTriangle size={17} /><span><b>Could not load the dashboard.</b> {error}</span><button onClick={() => loadDashboard()}>Try again <ArrowRight size={14} /></button></div>}
          {loading && !data ? <LoadingState /> : data && (
            <PageContent page={page} data={data} stats={stats} search={search} setSearch={setSearch}
              reportFilter={reportFilter} setReportFilter={setReportFilter}
              filteredReports={filteredReports} filteredAssets={filteredAssets} filteredSensors={filteredSensors}
              onNavigate={navigate} onCreateReport={createReport} onAssignTeam={assignTeam} onDeleteReport={deleteReport}
              notice={notice} onDismissNotice={() => setNotice('')} />
          )}
          <footer className="page-footer"><span>SmartCityX <b>·</b> municipal analytics workspace</span><span>{data ? `${data.reports.length + data.assets.length + data.sensors.length + data.crews.length + data.documents.length} records available` : 'Connecting to city data'}</span></footer>
        </section>
      </main>
    </div>
  );
}

function pageDescription(page) {
  const descriptions = {
    overview: 'A unified view of city services, infrastructure health, and community activity.',
    incidents: 'Review, search, and prioritize incoming citizen service requests.',
    infrastructure: 'Monitor municipal assets, operating condition, and maintenance needs.',
    utilities: 'Inspect utility infrastructure across the power, water, and transport networks.',
    sensors: 'Explore the latest readings from city-wide environmental and utility sensors.',
    crews: 'Track response team availability, specializations, and station coverage.',
    documents: 'Browse operational policies and city planning documents.',
    risk: 'Surface high-priority incidents and infrastructure requiring attention.',
    algorithms: 'A guide to the algorithms that support SmartCityX analytical workflows.',
  };
  return descriptions[page] || descriptions.overview;
}

function PageContent({ page, data, stats, search, setSearch, reportFilter, setReportFilter, filteredReports, filteredAssets, filteredSensors, onNavigate, onCreateReport, onAssignTeam, onDeleteReport, notice, onDismissNotice }) {
  if (page === 'overview') return <Overview data={data} stats={stats} onNavigate={onNavigate} />;
  if (page === 'incidents') return <Incidents data={data} reports={filteredReports} search={search} setSearch={setSearch} reportFilter={reportFilter} setReportFilter={setReportFilter} onCreateReport={onCreateReport} onAssignTeam={onAssignTeam} onDeleteReport={onDeleteReport} notice={notice} onDismissNotice={onDismissNotice} />;
  if (page === 'infrastructure') return <Infrastructure assets={filteredAssets} search={search} setSearch={setSearch} />;
  if (page === 'utilities') return <Utilities assets={data.assets} />;
  if (page === 'sensors') return <Sensors sensors={filteredSensors} search={search} setSearch={setSearch} />;
  if (page === 'crews') return <Crews crews={data.crews} />;
  if (page === 'documents') return <Documents documents={data.documents} />;
  if (page === 'risk') return <RiskWatch data={data} stats={stats} onNavigate={onNavigate} />;
  if (page === 'algorithms') return <AlgorithmGuide />;
  return <Overview data={data} stats={stats} onNavigate={onNavigate} />;
}

function Overview({ data, stats, onNavigate }) {
  const categories = countBy(data.reports, (report) => report.category).slice(0, 6);
  const attention = data.assets.filter((asset) => asset.status !== 'OPERATIONAL' || asset.health < 75).slice(0, 4);
  const recent = [...data.reports].sort((a, b) => b.timestamp - a.timestamp).slice(0, 6);
  const sensorTypes = countBy(data.sensors, (sensor) => sensor.type);
  return (
    <>
      <div className="welcome-strip"><div className="welcome-icon"><Cloud size={20} /></div><div><b>Good day, City Operations</b><p>Here’s the latest snapshot across your municipal services.</p></div><span className="welcome-date">{new Intl.DateTimeFormat(undefined, { month: 'short', day: 'numeric', year: 'numeric' }).format(new Date(data.generatedAt))}</span></div>
      <div className="metric-grid">
        <Metric icon={Siren} label="Citizen reports" value={data.reports.length} detail="Requests in current dataset" tone="green" />
        <Metric icon={AlertTriangle} label="Priority 1 alerts" value={stats.critical} detail="Require urgent attention" tone="red" />
        <Metric icon={Building2} label="Infrastructure assets" value={data.assets.length} detail={`${stats.attentionAssets} need maintenance`} tone="blue" />
        <Metric icon={Wifi} label="Sensor readings" value={data.sensors.length} detail={`${sensorTypes.length} telemetry categories`} tone="purple" />
      </div>
      <div className="dashboard-grid overview-grid">
        <Panel title="Reports by category" subtitle="Distribution of citizen service requests" action={<button className="text-button" onClick={() => onNavigate('incidents')}>All reports <ArrowRight size={14} /></button>}>
          <div className="category-chart">{categories.map(([name, count]) => <div className="category-row" key={name}><div className="category-label"><span>{formatLabel(name)}</span><b>{count}</b></div><div className="bar-track"><span style={{ width: `${Math.max(count / categories[0][1] * 100, 4)}%` }} /></div></div>)}</div>
          <div className="chart-foot"><span><i className="legend-dot green" /> Reports in dataset</span><span>{data.reports.length} total</span></div>
        </Panel>
        <Panel title="Infrastructure health" subtitle="Assets flagged for maintenance or reduced health" action={<button className="text-button" onClick={() => onNavigate('infrastructure')}>View assets <ArrowRight size={14} /></button>}>
          <div className="health-summary"><div className="health-score">{stats.averageHealth.toFixed(1)}<small>%</small></div><div><b>Average asset health</b><p>Across {data.assets.length} municipal assets</p></div><span className="health-trend"><ArrowUpRight size={15} /> monitored</span></div>
          <div className="compact-list">{attention.map((asset) => <div className="asset-compact" key={asset.id}><span className={`asset-symbol ${asset.health < 70 ? 'warning' : ''}`}><Building2 size={15} /></span><span className="compact-main"><b>{asset.name}</b><small>{asset.sector}</small></span><span className="health-value">{asset.health.toFixed(1)}%</span></div>)}</div>
        </Panel>
        <Panel title="Recent citizen reports" subtitle="Latest service requests by submitted time" action={<button className="text-button" onClick={() => onNavigate('incidents')}>Open inbox <ArrowRight size={14} /></button>} className="span-two">
          <ReportTable reports={recent} compact />
        </Panel>
        <Panel title="Telemetry coverage" subtitle="Incoming readings by sensor category" action={<button className="text-button" onClick={() => onNavigate('sensors')}>Explore sensors <ArrowRight size={14} /></button>}>
          <div className="telemetry-bars">{sensorTypes.map(([type, count]) => <div className="telemetry-bar" key={type}><div className="telemetry-count">{count}<small> readings</small></div><div className="vertical-track"><span style={{ height: `${count / Math.max(...sensorTypes.map((entry) => entry[1])) * 100}%` }} /></div><div className="telemetry-name">{formatLabel(type)}</div></div>)}</div>
          <div className="chart-foot"><span><i className="legend-dot cyan" /> Data points</span><span>{data.sensors.length} total</span></div>
        </Panel>
        <Panel title="Response teams" subtitle="Crew availability across the city" action={<button className="text-button" onClick={() => onNavigate('crews')}>View teams <ArrowRight size={14} /></button>}>
          <div className="crew-overview"><div className="crew-ring"><div><b>{stats.availableCrews}</b><small>available</small></div></div><div className="crew-legend"><span><i className="legend-dot green" /> Available <b>{stats.availableCrews}</b></span><span><i className="legend-dot muted" /> Other status <b>{data.crews.length - stats.availableCrews}</b></span><span className="crew-total">{data.crews.length} response teams</span></div></div>
        </Panel>
      </div>
    </>
  );
}

function Incidents({ data, reports, search, setSearch, reportFilter, setReportFilter, onCreateReport, onAssignTeam, onDeleteReport, notice, onDismissNotice }) {
  const [showForm, setShowForm] = useState(false);
  const [actionError, setActionError] = useState('');
  const [pendingDelete, setPendingDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const availableCrews = data.crews.filter((crew) => crew.status === 'AVAILABLE');
  const handleCreate = async (values) => {
    setActionError('');
    await onCreateReport(values);
    setShowForm(false);
  };
  const handleAssign = async (reportId, crewId) => {
    setActionError('');
    try {
      await onAssignTeam(reportId, crewId);
    } catch (err) {
      setActionError(err.message);
    }
  };
  const handleDelete = async () => {
    if (!pendingDelete) return;
    setDeleting(true);
    setActionError('');
    try {
      await onDeleteReport(pendingDelete.id);
      setPendingDelete(null);
    } catch (err) {
      setActionError(err.message);
    } finally {
      setDeleting(false);
    }
  };
  return <><div className="incident-toolbar"><div><span className="live-indicator" /> REPORTING CHANNEL <span className="toolbar-divider">·</span> Submit and route a city incident</div><button className="primary-button" onClick={() => { setActionError(''); setShowForm(true); }}><Plus size={15} /> Report an incident</button></div>
    {notice && <div className="success-banner"><Check size={15} /><span>{notice}</span><button aria-label="Dismiss notification" onClick={onDismissNotice}><X size={14} /></button></div>}
    {actionError && <div className="error-banner"><AlertTriangle size={16} /><span>{actionError}</span><button onClick={() => setActionError('')}>Dismiss <X size={13} /></button></div>}
    <div className="metric-grid three">
    <Metric icon={Siren} label="All reports" value={data.reports.length} detail="Citizen service requests" tone="green" />
    <Metric icon={AlertTriangle} label="Priority 1" value={data.reports.filter((item) => item.priority === 1).length} detail="Critical response" tone="red" />
    <Metric icon={Clock3} label="Awaiting assignment" value={data.reports.filter((item) => item.status === 'OPEN').length} detail={`${data.crews.filter((crew) => crew.status === 'AVAILABLE').length} teams available`} tone="blue" />
  </div><Panel title="Citizen service inbox" subtitle={`${reports.length} reports match your filters`} action={<select className="filter-select" value={reportFilter} onChange={(event) => setReportFilter(event.target.value)}><option value="ALL">All priorities</option><option value="1">Priority 1</option><option value="2">Priority 2</option><option value="3">Priority 3</option><option value="4">Priority 4</option><option value="5">Priority 5</option></select>}>
    <SearchBox value={search} onChange={setSearch} placeholder="Search reports, locations, categories..." />
    <ReportTable reports={[...reports].sort((a, b) => b.timestamp - a.timestamp)} crews={availableCrews} onAssign={handleAssign} onDelete={(report) => setPendingDelete(report)} />
  </Panel>
  {showForm && <ReportIncidentModal crews={availableCrews} onClose={() => setShowForm(false)} onSubmit={handleCreate} />}
  {pendingDelete && <DeleteReportModal report={pendingDelete} deleting={deleting} onCancel={() => setPendingDelete(null)} onConfirm={handleDelete} />}
  </>;
}

function DeleteReportModal({ report, deleting, onCancel, onConfirm }) {
  return <div className="modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && !deleting && onCancel()}>
    <section className="confirm-modal" role="alertdialog" aria-modal="true" aria-labelledby="delete-report-title">
      <span className="delete-modal-icon"><Trash2 size={19} /></span>
      <h2 id="delete-report-title">Delete this incident?</h2>
      <p><b>{report.id} · {formatLabel(report.category)}</b><br />This report will be removed from the inbox.{report.assignedCrewName ? ` ${report.assignedCrewName} will be marked available again.` : ''}</p>
      <div className="modal-actions"><button className="secondary-button" onClick={onCancel} disabled={deleting}>Keep report</button><button className="delete-button" onClick={onConfirm} disabled={deleting}>{deleting ? <><RefreshCw className="spin" size={14} /> Deleting…</> : <><Trash2 size={14} /> Delete report</>}</button></div>
    </section>
  </div>;
}

function ReportIncidentModal({ crews, onClose, onSubmit }) {
  const [allocation, setAllocation] = useState('AUTO');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [category, setCategory] = useState('');
  const [customCategory, setCustomCategory] = useState('');

  async function submit(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const reportCategory = category === 'OTHER' ? customCategory : category;
    setSubmitting(true);
    setError('');
    try {
      await onSubmit({
        citizen: form.get('citizen'),
        category: reportCategory,
        sector: form.get('sector'),
        description: form.get('description'),
        priority: form.get('priority'),
        allocation,
        crewId: form.get('crewId') || '',
      });
    } catch (err) {
      setError(err.message || 'Could not submit the incident.');
    } finally {
      setSubmitting(false);
    }
  }

  return <div className="modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && !submitting && onClose()}>
    <section className="report-modal" role="dialog" aria-modal="true" aria-labelledby="report-modal-title">
      <header className="modal-header"><div><span className="modal-icon"><Siren size={18} /></span><div><h2 id="report-modal-title">Report a city incident</h2><p>Share what happened so the right team can respond.</p></div></div><button className="modal-close" type="button" onClick={onClose} disabled={submitting} aria-label="Close form"><X size={17} /></button></header>
      <form className="incident-form" onSubmit={submit}>
        <label className="form-field">Problem category
          <select name="category" required value={category} onChange={(event) => setCategory(event.target.value)}>
            <option value="" disabled>Select a category</option>
            <option value="FIRE">Fire or rescue</option><option value="GAS_LEAK">Gas or chemical leak</option>
            <option value="WATER_BURST">Water or flooding</option><option value="GRID_FAIL">Power or electrical issue</option>
            <option value="TRAFFIC_COLLISION">Traffic or road incident</option><option value="POTHOLE">Road damage</option>
            <option value="STREETLIGHT_OUT">Streetlight or public lighting</option><option value="OTHER">Other problem</option>
          </select>
        </label>
        {category === 'OTHER' && <label className="form-field">Describe the problem category<input required value={customCategory} onChange={(event) => setCustomCategory(event.target.value)} placeholder="For example: animal rescue" /></label>}
        <label className="form-field">Incident location<input name="sector" required placeholder="Street, neighborhood, or sector" /></label>
        <div className="form-row">
          <label className="form-field">Priority
            <select name="priority" defaultValue="3"><option value="1">P1 · Critical emergency</option><option value="2">P2 · High priority</option><option value="3">P3 · Medium priority</option><option value="4">P4 · Routine</option><option value="5">P5 · Low priority</option></select>
          </label>
          <label className="form-field">Your name <span className="optional-label">Optional</span><input name="citizen" placeholder="Name or leave blank" /></label>
        </div>
        <label className="form-field">What is the problem?<textarea name="description" required minLength="8" rows="3" placeholder="Describe the incident and any details that will help the response team." /></label>
        <fieldset className="allocation-fieldset">
          <legend>Response team assignment</legend>
          <label className={`allocation-option ${allocation === 'AUTO' ? 'active' : ''}`}><input type="radio" name="allocationMode" value="AUTO" checked={allocation === 'AUTO'} onChange={() => setAllocation('AUTO')} /><span className="allocation-radio" /><span><b>Let the system assign</b><small>Match an available team to the incident type and location.</small></span><Zap size={16} /></label>
          <label className={`allocation-option ${allocation === 'MANUAL' ? 'active' : ''}`}><input type="radio" name="allocationMode" value="MANUAL" checked={allocation === 'MANUAL'} onChange={() => setAllocation('MANUAL')} disabled={!crews.length} /><span className="allocation-radio" /><span><b>Choose a response team</b><small>Select an available crew yourself.</small></span><Users size={16} /></label>
          {allocation === 'MANUAL' && <label className="form-field team-select-field">Available team
            <select name="crewId" required defaultValue=""><option value="" disabled>Select a response team</option>{crews.map((crew) => <option key={crew.id} value={crew.id}>{crew.name} · {formatLabel(crew.specialty)} · {crew.station}</option>)}</select>
          </label>}
          {!crews.length && <p className="no-crews-note">No response teams are currently available. The incident can still be submitted for follow-up.</p>}
        </fieldset>
        {error && <div className="form-error"><AlertTriangle size={15} />{error}</div>}
        <footer className="modal-actions"><button type="button" className="secondary-button" onClick={onClose} disabled={submitting}>Cancel</button><button type="submit" className="primary-button" disabled={submitting}>{submitting ? <><RefreshCw className="spin" size={14} /> Submitting…</> : <><Plus size={15} /> Submit incident</>}</button></footer>
      </form>
    </section>
  </div>;
}

function Infrastructure({ assets, search, setSearch }) {
  const needsAttention = assets.filter((asset) => asset.status !== 'OPERATIONAL' || asset.health < 75).length;
  return <><div className="metric-grid three">
    <Metric icon={Building2} label="Tracked assets" value={assets.length} detail="Municipal infrastructure" tone="blue" />
    <Metric icon={AlertTriangle} label="Need attention" value={needsAttention} detail="Maintenance or low health" tone="amber" />
    <Metric icon={Gauge} label="Average health" value={`${(assets.reduce((sum, item) => sum + item.health, 0) / Math.max(assets.length, 1)).toFixed(1)}%`} detail="Across displayed assets" tone="green" />
  </div><Panel title="Infrastructure inventory" subtitle="Condition and service status across municipal assets"><SearchBox value={search} onChange={setSearch} placeholder="Search assets, types, or sectors..." /><AssetTable assets={assets} /></Panel></>;
}

function Utilities({ assets }) {
  const utilityAssets = assets.filter((asset) => ['POWER_SUBSTATION', 'WATER_TREATMENT', 'TRAFFIC_JUNCTION'].includes(asset.type));
  const types = countBy(utilityAssets, (asset) => asset.type);
  const totalCapacity = utilityAssets.reduce((sum, asset) => sum + asset.capacity, 0);
  return <><div className="metric-grid three">
    <Metric icon={Zap} label="Utility assets" value={utilityAssets.length} detail="Power, water, and traffic" tone="amber" />
    <Metric icon={Waves} label="Network categories" value={types.length} detail="Utility services represented" tone="blue" />
    <Metric icon={Gauge} label="Combined capacity" value={Math.round(totalCapacity).toLocaleString()} detail="Reported capacity units*" tone="purple" />
  </div><div className="dashboard-grid utility-grid"><Panel title="Utility network mix" subtitle="Infrastructure assets by network type"><div className="network-list">{types.map(([type, count]) => <div className="network-row" key={type}><span className="network-icon"><UtilityIcon type={type} /></span><span className="network-main"><b>{formatLabel(type)}</b><small>{count} monitored assets</small></span><span className="network-count">{count}</span></div>)}</div><div className="data-footnote">* Capacity values use each asset’s source unit (MW, flow, or vehicles/hour); they are not directly comparable.</div></Panel><Panel title="Assets requiring attention" subtitle="Utility infrastructure needing maintenance"><div className="attention-list">{utilityAssets.filter((asset) => asset.status !== 'OPERATIONAL' || asset.health < 75).map((asset) => <div className="attention-row" key={asset.id}><div><b>{asset.name}</b><small>{asset.sector}</small></div><StatusBadge value={asset.status} /><span className="health-value">{asset.health.toFixed(1)}%</span></div>)}</div></Panel></div><Panel title="Utility infrastructure directory" subtitle="Power, water, and traffic assets from the city inventory"><AssetTable assets={utilityAssets} /></Panel></>;
}

function Sensors({ sensors, search, setSearch }) {
  const allTypes = countBy(sensors, (sensor) => sensor.type);
  const recent = sensors.slice(0, 18);
  return <><div className="metric-grid three">
    <Metric icon={Wifi} label="Sensor readings" value={sensors.length} detail="Available telemetry records" tone="cyan" />
    <Metric icon={Activity} label="Sensor categories" value={allTypes.length} detail="Environmental and utility feeds" tone="purple" />
    <Metric icon={MapPin} label="Sectors covered" value={new Set(sensors.map((sensor) => sensor.sector)).size} detail="Across the city" tone="green" />
  </div><Panel title="Telemetry distribution" subtitle="Readings grouped by monitoring category"><div className="sensor-type-grid">{allTypes.map(([type, count]) => <div className="sensor-type-card" key={type}><span className="sensor-type-icon"><SensorIcon type={type} /></span><b>{formatLabel(type)}</b><strong>{count}</strong><small>readings</small><div className="mini-track"><span style={{ width: `${count / Math.max(...allTypes.map((entry) => entry[1])) * 100}%` }} /></div></div>)}</div></Panel><Panel title="Sensor stream" subtitle={`Showing the ${recent.length} most recent records from ${sensors.length} readings`}><SearchBox value={search} onChange={setSearch} placeholder="Search sensor IDs, types, or sectors..." /><SensorTable sensors={recent} /></Panel></>;
}

function Crews({ crews }) {
  const available = crews.filter((crew) => crew.status === 'AVAILABLE').length;
  const specialities = countBy(crews, (crew) => crew.specialty);
  return <><div className="metric-grid three">
    <Metric icon={Users} label="Response teams" value={crews.length} detail="Municipal service crews" tone="blue" />
    <Metric icon={Check} label="Available now" value={available} detail="Ready for dispatch" tone="green" />
    <Metric icon={HardHat} label="Specialties" value={specialities.length} detail="Coverage disciplines" tone="purple" />
  </div><div className="dashboard-grid crew-grid"><Panel title="Team coverage" subtitle="Crews by response specialty"><div className="category-chart crew-chart">{specialities.map(([name, count]) => <div className="category-row" key={name}><div className="category-label"><span>{formatLabel(name)}</span><b>{count}</b></div><div className="bar-track"><span style={{ width: `${count / Math.max(...specialities.map((entry) => entry[1])) * 100}%` }} /></div></div>)}</div></Panel><Panel title="Dispatch readiness" subtitle="Current crew availability"><div className="readiness-card"><span className="readiness-icon"><Users size={22} /></span><div><b>{available} of {crews.length} teams available</b><small>Availability from the current project dataset</small></div><strong>{Math.round(available / Math.max(crews.length, 1) * 100)}%</strong></div><div className="readiness-track"><span style={{ width: `${available / Math.max(crews.length, 1) * 100}%` }} /></div></Panel></div><Panel title="Response team directory" subtitle="Crew assignments and home stations"><CrewTable crews={crews} /></Panel></>;
}

function Documents({ documents }) {
  return <><div className="metric-grid three">
    <Metric icon={FileText} label="City documents" value={documents.length} detail="Policies and operational plans" tone="blue" />
    <Metric icon={Building2} label="Departments" value={new Set(documents.map((item) => item.department)).size} detail="Contributing city departments" tone="purple" />
    <Metric icon={BookOpen} label="Policy library" value="Available" detail="Browse the current source text" tone="green" />
  </div><div className="document-grid">{documents.map((document) => <article className="document-card" key={document.id}><div className="document-top"><span className="document-icon"><FileText size={18} /></span><span className="document-id">{document.id}</span></div><span className="document-department">{formatLabel(document.department)}</span><h2>{document.title}</h2><p>{document.text}</p><div className="document-footer"><span><FileText size={13} /> Operational policy</span><span><ArrowRight size={14} /></span></div></article>)}</div></>;
}

function RiskWatch({ data, stats, onNavigate }) {
  const critical = [...data.reports].filter((report) => report.priority === 1).sort((a, b) => a.timestamp - b.timestamp);
  const assetRisks = data.assets.filter((asset) => asset.status !== 'OPERATIONAL' || asset.health < 75).sort((a, b) => a.health - b.health);
  return <><div className="risk-banner"><div className="risk-banner-icon"><Bell size={20} /></div><div><b>City attention summary</b><p>{stats.critical} high-priority reports and {stats.attentionAssets} assets are flagged in the current dataset.</p></div><span className="risk-banner-count">{stats.critical + stats.attentionAssets}<small>items</small></span></div><div className="dashboard-grid risk-grid"><Panel title="Priority 1 reports" subtitle="Highest-priority citizen incidents" action={<button className="text-button" onClick={() => onNavigate('incidents')}>All reports <ArrowRight size={14} /></button>}>{critical.length ? <div className="risk-list">{critical.map((report) => <div className="risk-row" key={report.id}><span className="risk-priority">P1</span><div><b>{formatLabel(report.category)}</b><small>{report.description}</small><em><MapPin size={12} /> {report.sector}</em></div><time>{formatDateTime(report.timestamp)}</time></div>)}</div> : <EmptyMessage>No priority 1 reports in this dataset.</EmptyMessage>}</Panel><Panel title="Assets requiring attention" subtitle="Maintenance flags and reduced health" action={<button className="text-button" onClick={() => onNavigate('infrastructure')}>Asset inventory <ArrowRight size={14} /></button>}>{assetRisks.length ? <div className="attention-list">{assetRisks.map((asset) => <div className="attention-row" key={asset.id}><span className="risk-asset-icon"><Building2 size={15} /></span><div><b>{asset.name}</b><small>{asset.sector}</small></div><StatusBadge value={asset.status} /><span className="health-value">{asset.health.toFixed(1)}%</span></div>)}</div> : <EmptyMessage>No assets are currently flagged.</EmptyMessage>}</Panel></div></>;
}

function AlgorithmGuide() {
  return <><div className="guide-banner"><div className="guide-icon"><BookOpen size={20} /></div><div><b>Algorithms as city-service tools</b><p>Reference material for the DSA modules represented in the SmartCityX project. This page is informational; it does not run demos or display source output.</p></div></div><div className="guide-grid">{algorithmGuides.map((guide, index) => <article className="guide-card" key={guide.name}><div className="guide-card-top"><span className="guide-number">0{index + 1}</span><span className="guide-module">{guide.module}</span></div><h2>{guide.name}</h2><p className="guide-algorithms">{guide.detail}</p><div className="guide-divider" /><small>IN THE CITY</small><p className="guide-use">{guide.use}</p></article>)}</div></>;
}

function Metric({ icon: Icon, label, value, detail, tone }) {
  return <div className={`metric-card tone-${tone}`}><div className="metric-top"><span className="metric-icon"><Icon size={17} /></span><span className="metric-more"><ArrowUpRight size={14} /></span></div><div className="metric-value">{typeof value === 'number' ? value.toLocaleString() : value}</div><div className="metric-label">{label}</div><div className="metric-detail">{detail}</div></div>;
}

function Panel({ title, subtitle, action, children, className = '' }) {
  return <section className={`panel ${className}`}><div className="panel-heading"><div><h2>{title}</h2><p>{subtitle}</p></div>{action}</div>{children}</section>;
}

function ReportTable({ reports, compact = false, crews = [], onAssign, onDelete }) {
  if (!reports.length) return <EmptyMessage>No reports match the current filters.</EmptyMessage>;
  return <div className="table-wrap"><table className={onAssign ? 'report-table' : ''}><thead><tr><th>Report</th><th>Category</th><th>Location</th><th>Priority</th><th>Status</th><th>Response team</th>{onAssign && <th>Team assignment</th>}<th>Submitted</th>{onDelete && <th>Actions</th>}</tr></thead><tbody>{reports.map((report) => <tr key={report.id}><td><div className="table-primary">{report.id}</div><div className="table-secondary">{report.citizen}</div>{!compact && <div className="report-description">{report.description}</div>}</td><td><span className="category-chip">{formatLabel(report.category)}</span></td><td><span className="location-cell"><MapPin size={12} />{report.sector}</span></td><td><PriorityBadge priority={report.priority} /></td><td><StatusBadge value={report.status} /></td><td>{report.assignedCrewName ? <div className="assigned-crew"><Users size={12} /><span>{report.assignedCrewName}<small>{report.assignmentMethod === 'AUTO' ? 'Auto-assigned' : 'Assigned'}</small></span></div> : <span className="unassigned-label">Awaiting assignment</span>}</td>{onAssign && <td>{report.assignedCrewId ? <span className="assigned-indicator"><Check size={12} /> Assigned</span> : crews.length ? <select className="assign-select" aria-label={`Assign response team to ${report.id}`} value="" onChange={(event) => onAssign(report.id, event.target.value)}><option value="" disabled>Assign a team</option>{crews.map((crew) => <option key={crew.id} value={crew.id}>{crew.name} · {formatLabel(crew.specialty)}</option>)}</select> : <span className="unassigned-label">No teams available</span>}</td>}<td><span className="date-cell">{formatDateTime(report.timestamp)}</span></td>{onDelete && <td><button className="table-delete-button" aria-label={`Delete report ${report.id}`} title={`Delete ${report.id}`} onClick={() => onDelete(report)}><Trash2 size={14} /></button></td>}</tr>)}</tbody></table></div>;
}

function AssetTable({ assets }) {
  if (!assets.length) return <EmptyMessage>No infrastructure assets match this search.</EmptyMessage>;
  return <div className="table-wrap"><table><thead><tr><th>Asset</th><th>Type</th><th>Location</th><th>Health</th><th>Capacity</th><th>Status</th></tr></thead><tbody>{assets.map((asset) => <tr key={asset.id}><td><div className="table-primary">{asset.name}</div><div className="table-secondary">{asset.id}</div></td><td><span className="category-chip">{formatLabel(asset.type)}</span></td><td><span className="location-cell"><MapPin size={12} />{asset.sector}</span></td><td><HealthMeter health={asset.health} /></td><td><span className="date-cell">{asset.capacity.toLocaleString()}</span></td><td><StatusBadge value={asset.status} /></td></tr>)}</tbody></table></div>;
}

function SensorTable({ sensors }) {
  if (!sensors.length) return <EmptyMessage>No sensor records match this search.</EmptyMessage>;
  return <div className="table-wrap"><table><thead><tr><th>Sensor</th><th>Category</th><th>Location</th><th>Reading</th><th>Recorded</th></tr></thead><tbody>{sensors.map((sensor) => <tr key={sensor.id}><td><div className="table-primary">{sensor.id}</div></td><td><span className="category-chip">{formatLabel(sensor.type)}</span></td><td><span className="location-cell"><MapPin size={12} />{sensor.sector}</span></td><td><span className="reading-value">{sensor.value.toFixed(2)} <small>{sensor.unit}</small></span></td><td><span className="date-cell">{formatDateTime(sensor.timestamp)}</span></td></tr>)}</tbody></table></div>;
}

function CrewTable({ crews }) {
  return <div className="table-wrap"><table><thead><tr><th>Team</th><th>Specialty</th><th>Base station</th><th>Status</th></tr></thead><tbody>{crews.map((crew) => <tr key={crew.id}><td><div className="table-primary">{crew.name}</div><div className="table-secondary">{crew.id}</div></td><td><span className="category-chip">{formatLabel(crew.specialty)}</span></td><td><span className="location-cell"><MapPin size={12} />{crew.station}</span></td><td><StatusBadge value={crew.status} /></td></tr>)}</tbody></table></div>;
}

function PriorityBadge({ priority }) {
  return <span className={`priority-badge priority-${priority}`}>P{priority} · {priority === 1 ? 'Critical' : priority === 2 ? 'High' : priority <= 3 ? 'Medium' : 'Routine'}</span>;
}

function StatusBadge({ value }) {
  const tone = value === 'OPERATIONAL' || value === 'AVAILABLE' || value === 'OPEN' ? 'good' : value === 'MAINTENANCE_REQUIRED' || value === 'DISPATCHED' ? 'warn' : 'neutral';
  return <span className={`status-badge ${tone}`}><i />{formatLabel(value)}</span>;
}

function HealthMeter({ health }) {
  return <div className="health-meter"><span className={health < 70 ? 'low' : health < 80 ? 'mid' : ''}><i style={{ width: `${health}%` }} /></span><b>{health.toFixed(1)}%</b></div>;
}

function SearchBox({ value, onChange, placeholder }) {
  return <label className="table-search"><Search size={15} /><input value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder} /></label>;
}

function EmptyMessage({ children }) {
  return <div className="empty-message"><Boxes size={20} /><span>{children}</span></div>;
}

function LoadingState() {
  return <div className="loading-state"><div className="loading-spinner" /><b>Loading city dashboard</b><span>Fetching project data…</span></div>;
}

function countBy(items, selector) {
  const counts = new Map();
  for (const item of items) counts.set(selector(item), (counts.get(selector(item)) || 0) + 1);
  return [...counts.entries()].sort((a, b) => b[1] - a[1]);
}

function UtilityIcon({ type }) {
  if (type === 'POWER_SUBSTATION') return <Zap size={17} />;
  if (type === 'WATER_TREATMENT') return <Waves size={17} />;
  return <Activity size={17} />;
}

function SensorIcon({ type }) {
  if (type === 'AQI_PM25') return <Cloud size={16} />;
  if (type === 'TRAFFIC_FLOW') return <Activity size={16} />;
  if (type === 'WATER_PRESSURE') return <Waves size={16} />;
  if (type === 'GRID_LOAD_MW') return <Zap size={16} />;
  return <Gauge size={16} />;
}

export default App;
