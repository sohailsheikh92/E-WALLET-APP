document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    const res = await fetch("http://localhost:8082/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });

    if (res.ok) {
      const data = await res.json();
      localStorage.setItem("token", data.token);
      localStorage.setItem("username", username);
      window.location.href = "dashboard.html"; // ✅ redirect to wallet/dashboard
    } else {
      document.getElementById("loginError").textContent = "❌ Invalid username or password.";
    }
  } catch (error) {
    console.error("Login failed", error);
    document.getElementById("loginError").textContent = "❌ Server error. Try again later.";
  }
});
