const metalSelect = document.querySelector("#metalSelect");
const loadButton = document.querySelector("#loadButton");
const priceSummary = document.querySelector("#priceSummary");
const causes = document.querySelector("#causes");
const consequences = document.querySelector("#consequences");
const moneyPressure = document.querySelector("#moneyPressure");
const metalMomentum = document.querySelector("#metalMomentum");
const cryptoRisk = document.querySelector("#cryptoRisk");
const currencyStress = document.querySelector("#currencyStress");
const rippleTitle = document.querySelector("#rippleTitle");
const rippleMap = document.querySelector("#rippleMap");
const dataStatus = document.querySelector("#dataStatus");
const canvas = document.querySelector("#priceChart");
const ctx = canvas.getContext("2d");

loadButton.addEventListener("click", () => loadMetal(metalSelect.value));

async function loadMetal(symbol) {
    const [prices, insight, pressure, map] = await Promise.all([
        fetchJson(`/api/assets/${symbol}/prices`),
        fetchJson(`/api/assets/${symbol}/insight`),
        fetchJson(`/api/metals/${symbol}/money-pressure`),
        fetchJson(`/api/metals/${symbol}/ripple-map`)
    ]);

    priceSummary.textContent = `${insight.assetName}: $${Number(insight.latestPriceUsd).toLocaleString()} (${insight.thirtyDayChangePercent}% in 30 days)`;
    dataStatus.textContent = insight.live ? `Live: ${insight.dataSource}` : insight.dataSource;
    renderList(causes, insight.possibleCauses);
    renderList(consequences, insight.possibleConsequences);
    moneyPressure.textContent = pressure.interpretation;
    metalMomentum.value = pressure.metalMomentumScore;
    cryptoRisk.value = pressure.cryptoRiskScore;
    currencyStress.value = pressure.currencyStressScore;
    drawRippleMap(map);
    drawChart(prices);
}

async function fetchJson(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`Request failed: ${url}`);
    }
    return response.json();
}

function renderList(element, values) {
    element.innerHTML = "";
    values.forEach((value) => {
        const item = document.createElement("li");
        item.textContent = value;
        element.appendChild(item);
    });
}

function drawChart(prices) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    const padding = 28;
    const values = prices.map((point) => Number(point.priceUsd));
    const min = Math.min(...values);
    const max = Math.max(...values);
    const range = max - min || 1;

    ctx.strokeStyle = "#d8e0e8";
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(padding, padding);
    ctx.lineTo(padding, canvas.height - padding);
    ctx.lineTo(canvas.width - padding, canvas.height - padding);
    ctx.stroke();

    ctx.strokeStyle = "#0f766e";
    ctx.lineWidth = 3;
    ctx.beginPath();

    values.forEach((value, index) => {
        const x = padding + (index / (values.length - 1)) * (canvas.width - padding * 2);
        const y = canvas.height - padding - ((value - min) / range) * (canvas.height - padding * 2);
        if (index === 0) {
            ctx.moveTo(x, y);
        } else {
            ctx.lineTo(x, y);
        }
    });

    ctx.stroke();

    ctx.fillStyle = "#617080";
    ctx.font = "14px system-ui";
    ctx.fillText(`Low $${min.toFixed(2)}`, padding + 8, canvas.height - padding - 8);
    ctx.fillText(`High $${max.toFixed(2)}`, padding + 8, padding + 16);
}

function drawRippleMap(map) {
    rippleTitle.textContent = map.title;
    rippleMap.innerHTML = "";

    const nodesById = Object.fromEntries(map.nodes.map((node) => [node.id, node]));

    map.edges.forEach((edge) => {
        const row = document.createElement("div");
        row.className = "ripple-edge";

        const from = document.createElement("div");
        from.className = "node";
        from.textContent = nodesById[edge.fromNodeId]?.label ?? edge.fromNodeId;

        const arrow = document.createElement("div");
        arrow.className = "arrow";
        arrow.textContent = "->";

        const to = document.createElement("div");
        to.className = "node";
        to.textContent = nodesById[edge.toNodeId]?.label ?? edge.toNodeId;

        const detail = document.createElement("div");
        detail.className = "edge-detail";
        detail.textContent = `${edge.relationship}. Direction: ${edge.direction}. Strength: ${edge.strength}. Confidence: ${edge.confidence}.`;

        row.append(from, arrow, to, detail);
        rippleMap.appendChild(row);
    });
}

loadMetal("XAU");
