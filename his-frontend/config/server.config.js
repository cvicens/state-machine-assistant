const package = require('../package.json');

const DEFAULT_BACKEND_SERVICE = 'his-backend';

function getConfig (fullUrl) {
    const config = {
        API_ENDPOINT: '',
        SSO_ENABLED: process.env.SSO_URL ? true : false
    };

    if (fullUrl != null) {
        let match = fullUrl.match(/(http.*\:\/\/[^\/]+)\/*/);
        console.debug(`match: ${match}`);
        if (match != null && match[0] != null) {
            console.debug(`match[0]: ${match[0]}`);
            config.API_ENDPOINT = match[0].replace(package.name, DEFAULT_BACKEND_SERVICE);  
        }
    }
    
    if (process.env.GW_ENDPOINT != null) {
        config.API_ENDPOINT = process.env.GW_ENDPOINT;
    } else if (process.env.COOLSTORE_GW_SERVICE != null) {
        config.API_ENDPOINT = process.env.COOLSTORE_GW_SERVICE + '-' + process.env.OPENSHIFT_BUILD_NAMESPACE;
    }
    
    if (process.env.SECURE_GW_ENDPOINT != null) {
        config.SECURE_API_ENDPOINT = process.env.SECURE_GW_ENDPOINT;
    } else if (process.env.SECURE_GW_SERVICE != null) {
        config.SECURE_API_ENDPOINT = process.env.SECURE_GW_SERVICE + '-' + process.env.OPENSHIFT_BUILD_NAMESPACE;
    }

    console.log(`config: ${JSON.stringify(config)}`);

    return config;
}


module.exports = getConfig;