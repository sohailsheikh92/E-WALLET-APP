document.addEventListener("DOMContentLoaded", () => {
  const username = localStorage.getItem("username");
  const token = localStorage.getItem("token");

  if (!username || !token) {
    alert("⚠️ Please log in first.");
    window.location.href = "login.html";
    return;
  }

  document.getElementById("userName").textContent = username;

  // ✅ Check Balance Button Handler
  document.getElementById("checkBalanceBtn").addEventListener("click", async () => {
    const balanceText = document.getElementById("balance");
    balanceText.textContent = "Loading...";

    try {
      const res = await fetch(`http://localhost:8083/wallet/getbalance?username=${username}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (res.ok) {
        const data = await res.json();
        balanceText.textContent = `₹${data.Balance}`;
      } else {
        balanceText.textContent = "❌ Error fetching balance";
      }
    } catch (err) {
      console.error("Error:", err);
      balanceText.textContent = "❌ Server error";
    }
  });

  // ✅ Send Money
  document.getElementById("sendForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const to = document.getElementById("receiver").value;
    const amount = document.getElementById("amount").value;

    try {
      const res = await fetch(`http://localhost:8084/transaction/send?to=${to}&amount=${amount}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });

      const sendMsg = document.getElementById("sendMsg");
      if (res.ok) {
        sendMsg.textContent = "✅ Money sent!";
        sendMsg.style.color = "green";

        // ⟳ Optional: Refresh balance after transaction
        document.getElementById("checkBalanceBtn").click();

      } else {
        sendMsg.textContent = "❌ Transaction failed.";
        sendMsg.style.color = "red";
      }
    } catch {
      const sendMsg = document.getElementById("sendMsg");
      sendMsg.textContent = "❌ Something went wrong.";
      sendMsg.style.color = "red";
    }
  });
});
