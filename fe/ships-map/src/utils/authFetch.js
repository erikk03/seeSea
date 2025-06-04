export const authFetch = async (url, options = {}, onLogout) => {
  const token = localStorage.getItem('token');

  const headers = {
    ...options.headers,
    Authorization: `Bearer ${token}`,
  };

  try {
    const response = await fetch(url, { ...options, headers });

    // If the token is invalid or expired, log the user out
    if (response.status === 401) {
      console.warn('[authFetch] Token expired or unauthorized. Logging out...');

      // Clear the token from localStorage
    	localStorage.removeItem('token');

			// Redirect to welcome page
			window.location.href = '/';
			
      return null;
    }

    return response;
  } catch (error) {
    console.error('[authFetch] Request failed:', error);
    return null;
  }
};
