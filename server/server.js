import "dotenv/config";
import express from "express";
import cors from "cors";

const app = express();
app.use(cors());

const port = Number(process.env.PORT || 3000);
const serpApiKey = process.env.SERPAPI_KEY;

function normalize(item) {
  const price = Number(item.extracted_price);
  if (!item.title || !item.source || !Number.isFinite(price) || !item.product_link) return null;

  return {
    id: String(item.product_id || `${item.source}-${item.title}-${price}`),
    storeName: item.source,
    productTitle: item.title,
    productUrl: item.product_link,
    imageUrl: item.thumbnail || "",
    currentPrice: price,
    originalPrice: Number(item.extracted_old_price) || price,
    discount: Number.isFinite(Number(item.extracted_old_price)) && Number(item.extracted_old_price) > price
      ? Math.round((1 - price / Number(item.extracted_old_price)) * 100)
      : 0,
    currency: "₹",
    availability: item.delivery ? "Available" : "Unknown",
    sellerName: item.source,
    rating: Number(item.rating) || 0,
    lastUpdated: new Date().toISOString(),
    matchConfidence: "Shopping result match",
    variantInfo: item.extensions?.join(" | ") || "Standard",
    brand: "",
    model: "",
    category: "General",
    description: item.snippet || "Live shopping result",
    priceHistory: []
  };
}

app.get("/health", (_req, res) => res.json({ ok: true }));

app.get("/search", async (req, res) => {
  const q = String(req.query.q || "").trim();
  if (!q) return res.status(400).json({ error: "q is required" });
  if (!serpApiKey) return res.status(500).json({ error: "SERPAPI_KEY is not configured" });

  const params = new URLSearchParams({
    engine: "google_shopping",
    q,
    location: "India",
    gl: "in",
    hl: "en",
    device: "mobile",
    api_key: serpApiKey
  });

  try {
    const response = await fetch(`https://serpapi.com/search?${params}`);
    const data = await response.json();
    if (!response.ok || data.error) {
      return res.status(502).json({ error: data.error || "Shopping provider failed" });
    }

    const products = (data.shopping_results || []).map(normalize).filter(Boolean);
    return res.json({ products });
  } catch (error) {
    return res.status(502).json({ error: "Unable to reach shopping provider" });
  }
});

app.listen(port, () => console.log(`PricePilot API listening on ${port}`));
