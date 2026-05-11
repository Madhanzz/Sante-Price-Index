const { onSchedule } = require("firebase-functions/v2/scheduler");
const admin = require("firebase-admin");

admin.initializeApp();

exports.updatePrices = onSchedule("every 24 hours", async () => {

  const db = admin.database();

  try {
    const url = "https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070?api-key=579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b&format=json";

    const response = await fetch(url);
    const data = await response.json();

    const records = data.records;

    for (let item of records) {

      const name = item.commodity?.toLowerCase();
      const modal = item.modal_price;

      if (!name || !modal) continue;

      // 🔥 convert ₹/quintal → ₹/kg
      const mandiPrice = Math.floor(modal / 100);

      // 🔥 only store required vegetables
      if (["onion", "tomato", "carrot", "brinjal"].includes(name)) {

        const ref = db.ref("prices/" + name);

        const snapshot = await ref.once("value");
        const oldMandi = snapshot.val()?.mandi || mandiPrice;

        await ref.update({
          previous: oldMandi,
          mandi: mandiPrice
        });
      }
    }

    console.log("Prices updated successfully");

  } catch (error) {
    console.error("API error:", error);
  }

});