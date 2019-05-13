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
        target: "http://127.0.0.1:8080",
        //target: "http://google.es",
        secure: false
    }
]

module.exports = PROXY_CONFIG;