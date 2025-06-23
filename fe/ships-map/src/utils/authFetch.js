export const authFetch = async (url, options = {}, onLogout) => {
  const token = localStorage.getItem('token');

  if (token) {
    // Decode JWT and check expiration
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const now = Math.floor(Date.now() / 1000);

      if (payload.exp && payload.exp < now) {
        console.warn('[authFetch] Token has expired.');
        // Clear token
        localStorage.removeItem('token');
        // Redirect to welcome page
        window.location.href = '/';
        return null;
      }
    } catch (err) {
      console.error('[authFetch] Invalid token format:', err);
      return null;
    }
  }

  const headers = {
    ...options.headers,
    Authorization: `Bearer ${token}`,
  };

  try {
    const response = await fetch(url, { ...options, headers });

    return response;
  } catch (error) {
    console.error('[authFetch] Request failed:', error);
    return null;
  }
};
