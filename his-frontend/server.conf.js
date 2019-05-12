const PROXY_CONFIG = [
    {
        context: [
            "/server.json"
        ],
        target: "http://localhost:8090",
        secure: false
    },
    {
        context: [
            "/api/patients",
            "/api/patients/",
        ],
        target: "http://localhost:8080",
        secure: false
    }
]

module.exports = PROXY_CONFIG;