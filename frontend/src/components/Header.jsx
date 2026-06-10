function Header({
  username,
  onUsernameChange,
  password,
  onPasswordChange,
}) {
  return (
    <header className="app-header">
      <div className="header-inner">
        <div className="header-brand">
          <h1>Smart Vacation Planner</h1>
          <p className="app-subtitle">
            Plan your trip with selected activities and a generated itinerary
          </p>
        </div>

        <div className="header-credentials">
          <p className="header-credentials-hint">Local testing only</p>
          <div className="header-credentials-fields">
            <div className="form-field header-form-field">
              <label htmlFor="username">Email / Username</label>
              <input
                id="username"
                type="text"
                autoComplete="username"
                placeholder="e.g. user@email.com"
                value={username}
                onChange={(event) => onUsernameChange(event.target.value)}
              />
            </div>

            <div className="form-field header-form-field">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(event) => onPasswordChange(event.target.value)}
              />
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}

export default Header;
