const admin = require("firebase-admin");
const fetch = require("node-fetch");
const serviceAccount = require("./santepriceindex-3a120-firebase-adminsdk-fbsvc-2c08387fe2.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://santepriceindex-3a120-default-rtdb.firebaseio.com/"
});

const db = admin.database();

async function updatePrices() {
  try {
    console.log("Fetching data...");

    const response = await fetch(
      "https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070?api-key=579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b&format=json&limit=100"
    );

    const data = await response.json();
    const records = data.records;

    for (let item of records) {
      const name = item.commodity?.toLowerCase();
      const modal = parseFloat(item.modal_price);

      if (!name || !modal) continue;

      const mandiPrice = Math.floor(modal / 100);

      const ref = db.ref("prices/" + name);

      const snapshot = await ref.once("value");
      const oldMandi = snapshot.val()?.mandi || mandiPrice;

      await ref.update({
        previous: oldMandi,
        mandi: mandiPrice
      });

      console.log(`Updated ${name}: ${mandiPrice}`);
    }

    console.log("Update complete!");
  } catch (error) {
    console.error("Error:", error);
  }
}

updatePrices();
