const PROXY_CONFIG = [
    {
        context: [
            "/server.json"
        ],
        target: "http://localhost:8090",
        secure: false
    }
]

module.exports = PROXY_CONFIG;