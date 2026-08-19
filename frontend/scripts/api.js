// scripts/api.js
// Módulo compartilhado de acesso à API REST.

const API_CONFIG = {
    // Em desenvolvimento (localhost/127.0.0.1) aponta pro Spring Boot local.
    // Quando o backend estiver hospedado (ex: Render), troque o valor de
    // PROD_API_URL abaixo pela URL pública, algo como:
    //   "https://seu-app.onrender.com/api"
    baseUrl: (() => {
        const host = window.location.hostname;
        const isLocal = host === "localhost" || host === "127.0.0.1";
        const PROD_API_URL = "https://TROCAR-PELA-URL-DO-RENDER.onrender.com/api";
        return isLocal ? "http://localhost:8080/api" : PROD_API_URL;
    })(),
};

async function apiRequest(path, { method = "GET", params = null, body = null } = {}) {
    let url = `${API_CONFIG.baseUrl}${path}`;

    if (params) {
        const entradasValidas = Object.entries(params)
            .filter(([_, v]) => v !== null && v !== undefined && v !== "");
        const query = new URLSearchParams(entradasValidas).toString();
        if (query) url += `?${query}`;
    }

    const options = { method, headers: {} };
    if (body !== null) {
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(body);
    }

    let response;
    try {
        response = await fetch(url, options);
    } catch (err) {
        throw new Error(`Não foi possível conectar à API (${url}). O back-end está rodando? Detalhe: ${err.message}`);
    }

    if (!response.ok) {
        const texto = await response.text().catch(() => "");
        throw new Error(`Erro ${response.status} ao chamar ${path}. ${texto}`);
    }

    if (response.status === 204) return null;

    const contentType = response.headers.get("content-type") || "";
    return contentType.includes("application/json") ? response.json() : null;
}

// API pública usada pelas outras telas
const api = {
    get: (path, params) => apiRequest(path, { method: "GET", params }),
    post: (path, body) => apiRequest(path, { method: "POST", body }),
};

// Pequeno helper de UI para mostrar erros sem quebrar a experiência
function mostrarErroApi(erro) {
    console.error(erro);
    alert(erro.message || "Ocorreu um erro ao falar com o servidor.");
}